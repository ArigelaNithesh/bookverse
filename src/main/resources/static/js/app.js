/**
 * BOOKVERSE JavaScript Client Library
 * Handles authentication sessions, CSRF headers, cart/wishlist state,
 * toast alerts, and common API interactions.
 */

const Bookverse = {
  currentUser: null,

  // Helper for CSRF token retrieval from cookie
  getCsrfToken() {
    const name = "XSRF-TOKEN=";
    const decodedCookie = decodeURIComponent(document.cookie);
    const ca = decodedCookie.split(';');
    for (let i = 0; i < ca.length; i++) {
      let c = ca[i].trim();
      if (c.indexOf(name) === 0) {
        return c.substring(name.length, c.length);
      }
    }
    return "";
  },

  // Centralized API Request Handler
  async apiFetch(url, options = {}) {
    options.headers = options.headers || {};
    
    // Auto set JSON content-type if body is provided and not FormData
    if (options.body && !(options.body instanceof FormData) && typeof options.body === 'object') {
      options.headers['Content-Type'] = 'application/json';
      options.body = JSON.stringify(options.body);
    }

    // Attach CSRF token if present
    const csrf = this.getCsrfToken();
    if (csrf) {
      options.headers['X-XSRF-TOKEN'] = csrf;
    }

    try {
      const response = await fetch(url, options);
      const data = await response.json().catch(() => null);

      if (!response.ok) {
        const errorMsg = data && data.message ? data.message : `Request failed (${response.status})`;
        throw new Error(errorMsg);
      }

      return data;
    } catch (err) {
      console.error("API error for " + url + ":", err);
      throw err;
    }
  },

  // Format INR currency
  formatCurrency(amount) {
    if (amount === undefined || amount === null) return "₹0.00";
    return "₹" + Number(amount).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
  },

  // Global Toast Alert Helper
  showToast(message, type = "success") {
    let container = document.getElementById("bv-toast-container");
    if (!container) {
      container = document.createElement("div");
      container.id = "bv-toast-container";
      container.className = "bv-toast-container";
      document.body.appendChild(container);
    }

    const toast = document.createElement("div");
    toast.className = `bv-toast ${type}`;
    
    const iconClass = type === 'success' ? 'bi-check-circle-fill text-success' :
                      type === 'error' ? 'bi-exclamation-triangle-fill text-danger' :
                      'bi-info-circle-fill text-primary';

    toast.innerHTML = `
      <i class="bi ${iconClass} fs-5"></i>
      <div class="flex-grow-1 small text-dark">${message}</div>
      <button type="button" class="btn-close btn-close-sm ms-auto" aria-label="Close"></button>
    `;

    toast.querySelector('.btn-close').onclick = () => toast.remove();
    container.appendChild(toast);

    setTimeout(() => {
      if (toast.parentNode) {
        toast.remove();
      }
    }, 4000);
  },

  // Sync Current User & Navbar State
  async initNavbar() {
    try {
      const res = await fetch('/api/auth/me');
      if (res.ok) {
        const data = await res.json();
        if (data && data.success && data.data) {
          this.currentUser = data.data;
          this.renderUserNav(this.currentUser);
          this.refreshCounters();
          return;
        }
      }
    } catch (e) {
      // User is anonymous
    }
    this.renderGuestNav();
  },

  renderUserNav(user) {
    const userContainer = document.getElementById("nav-user-slot");
    if (!userContainer) return;

    const isAdmin = user.roles && user.roles.includes("ROLE_ADMIN");

    userContainer.innerHTML = `
      <div class="dropdown">
        <button class="btn btn-sm btn-bv-outline dropdown-toggle d-flex align-items-center gap-2" type="button" data-bs-toggle="dropdown">
          <i class="bi bi-person-circle fs-6"></i>
          <span class="d-none d-md-inline">${user.fullName || user.email}</span>
        </button>
        <ul class="dropdown-menu dropdown-menu-end shadow-sm border-0">
          <li><h6 class="dropdown-header text-truncate" style="max-width: 200px;">${user.email}</h6></li>
          <li><a class="dropdown-item" href="/profile.html"><i class="bi bi-person me-2"></i>My Profile</a></li>
          <li><a class="dropdown-item" href="/orders.html"><i class="bi bi-box-seam me-2"></i>My Orders</a></li>
          <li><a class="dropdown-item" href="/wishlist.html"><i class="bi bi-heart me-2"></i>Wishlist</a></li>
          <li><a class="dropdown-item" href="/read.html"><i class="bi bi-book me-2"></i>Digital Reading</a></li>
          ${isAdmin ? '<li><hr class="dropdown-divider"></li><li><a class="dropdown-item text-danger fw-semibold" href="/admin/index.html"><i class="bi bi-speedometer2 me-2"></i>Admin Dashboard</a></li>' : ''}
          <li><hr class="dropdown-divider"></li>
          <li><a class="dropdown-item text-muted" href="#" onclick="Bookverse.logout(event)"><i class="bi bi-box-arrow-right me-2"></i>Sign Out</a></li>
        </ul>
      </div>
    `;
  },

  renderGuestNav() {
    const userContainer = document.getElementById("nav-user-slot");
    if (!userContainer) return;
    userContainer.innerHTML = `
      <a href="/login.html" class="btn btn-sm btn-bv-outline"><i class="bi bi-box-arrow-in-right me-1"></i>Sign In</a>
      <a href="/register.html" class="btn btn-sm btn-bv-primary"><i class="bi bi-person-plus me-1"></i>Register</a>
    `;
  },

  async logout(e) {
    if (e) e.preventDefault();
    try {
      await fetch('/api/auth/logout', { method: 'POST' });
      this.currentUser = null;
      window.location.href = '/index.html';
    } catch (err) {
      window.location.reload();
    }
  },

  // Refresh cart and wishlist count badges
  async refreshCounters() {
    if (!this.currentUser) return;
    try {
      const cartRes = await fetch('/api/cart');
      if (cartRes.ok) {
        const cData = await cartRes.json();
        const badge = document.getElementById('cart-counter-badge');
        if (badge && cData.data) {
          badge.textContent = cData.data.totalItems || 0;
          badge.style.display = (cData.data.totalItems > 0) ? 'inline-block' : 'none';
        }
      }

      const wishRes = await fetch('/api/wishlist');
      if (wishRes.ok) {
        const wData = await wishRes.json();
        const badge = document.getElementById('wishlist-counter-badge');
        if (badge && wData.data && wData.data.books) {
          const count = wData.data.books.length;
          badge.textContent = count;
          badge.style.display = (count > 0) ? 'inline-block' : 'none';
        }
      }
    } catch (e) {
      // Ignore background counter sync errors
    }
  },

  // Quick action: Add to cart
  async addToCart(bookId, quantity = 1) {
    if (!this.currentUser) {
      this.showToast("Please sign in to add books to your cart", "info");
      setTimeout(() => { window.location.href = `/login.html?redirect=${encodeURIComponent(window.location.href)}`; }, 1200);
      return;
    }

    try {
      await this.apiFetch('/api/cart/items', {
        method: 'POST',
        body: { bookId: Number(bookId), quantity: Number(quantity) }
      });
      this.showToast("Book added to cart!", "success");
      this.refreshCounters();
    } catch (err) {
      this.showToast(err.message || "Failed to add to cart", "error");
    }
  },

  // Quick action: Add to wishlist
  async addToWishlist(bookId) {
    if (!this.currentUser) {
      this.showToast("Please sign in to save items to your wishlist", "info");
      setTimeout(() => { window.location.href = `/login.html?redirect=${encodeURIComponent(window.location.href)}`; }, 1200);
      return;
    }

    try {
      await this.apiFetch('/api/wishlist/items', {
        method: 'POST',
        body: { bookId: Number(bookId) }
      });
      this.showToast("Book saved to wishlist!", "success");
      this.refreshCounters();
    } catch (err) {
      this.showToast(err.message || "Failed to add to wishlist", "error");
    }
  },

  // Render a book card HTML
  renderBookCard(book) {
    const coverUrl = book.coverImageUrl || 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80';
    const hasDiscount = book.listPrice && book.listPrice > book.sellingPrice;
    
    return `
      <div class="col-6 col-md-4 col-lg-3 mb-4">
        <div class="book-card h-100">
          <div class="book-cover-wrap">
            <img src="${coverUrl}" alt="${book.title}" class="book-cover-img" loading="lazy" onerror="this.src='https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=600&q=80'">
            <div class="book-badges">
              ${book.bestseller ? '<span class="badge-bv-bestseller">Bestseller</span>' : ''}
              ${book.digitalAvailable ? '<span class="badge-bv-digital"><i class="bi bi-book me-1"></i>Read Online</span>' : ''}
            </div>
          </div>
          <div class="book-card-body">
            <div class="book-category">${book.categoryName || 'General'}</div>
            <h6 class="book-title" title="${book.title}">
              <a href="/book-details.html?slug=${book.slug}">${book.title}</a>
            </h6>
            <div class="book-author">${book.authorName || 'Unknown Author'}</div>
            <div class="book-rating">
              <i class="bi bi-star-fill"></i>
              <span class="fw-semibold">${book.averageRating > 0 ? Number(book.averageRating).toFixed(1) : 'New'}</span>
              ${book.reviewCount > 0 ? `<span class="rating-count">(${book.reviewCount})</span>` : ''}
            </div>
            <div class="book-price-row">
              <span class="selling-price">${this.formatCurrency(book.sellingPrice)}</span>
              ${hasDiscount ? `<span class="list-price">${this.formatCurrency(book.listPrice)}</span>` : ''}
            </div>
            <div class="book-actions">
              <button class="btn btn-sm btn-bv-primary flex-grow-1" onclick="Bookverse.addToCart(${book.id}, 1)">
                <i class="bi bi-bag-plus me-1"></i>Add
              </button>
              <button class="btn btn-sm btn-bv-outline px-2" title="Add to Wishlist" onclick="Bookverse.addToWishlist(${book.id})">
                <i class="bi bi-heart"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    `;
  }
};

document.addEventListener('DOMContentLoaded', () => {
  Bookverse.initNavbar();
});
