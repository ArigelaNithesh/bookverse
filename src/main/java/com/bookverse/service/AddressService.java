package com.bookverse.service;

import com.bookverse.dto.AddressDto;
import com.bookverse.entity.Address;
import com.bookverse.entity.User;
import com.bookverse.exception.ResourceNotFoundException;
import com.bookverse.repository.AddressRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public List<AddressDto> getUserAddresses(User user) {
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressDto addAddress(User user, AddressDto dto) {
        Address address = new Address();
        mapToEntity(dto, address);
        address.setUser(user);

        if (dto.isDefault()) {
            clearDefaultForUser(user.getId());
        }

        Address saved = addressRepository.save(address);
        return mapToDto(saved);
    }

    @Transactional
    public AddressDto updateAddress(User user, Long addressId, AddressDto dto) {
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found or does not belong to user"));

        mapToEntity(dto, address);

        if (dto.isDefault()) {
            clearDefaultForUser(user.getId());
            address.setDefault(true);
        }

        Address updated = addressRepository.save(address);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteAddress(User user, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .filter(a -> a.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Address not found or does not belong to user"));
        addressRepository.delete(address);
    }

    private void clearDefaultForUser(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        for (Address a : addresses) {
            a.setDefault(false);
        }
        addressRepository.saveAll(addresses);
    }

    public AddressDto mapToDto(Address entity) {
        AddressDto dto = new AddressDto();
        dto.setId(entity.getId());
        dto.setRecipientName(entity.getRecipientName());
        dto.setPhoneNumber(entity.getPhoneNumber());
        dto.setStreetAddress(entity.getStreetAddress());
        dto.setCity(entity.getCity());
        dto.setState(entity.getState());
        dto.setPostalCode(entity.getPostalCode());
        dto.setCountry(entity.getCountry());
        dto.setDefault(entity.isDefault());
        return dto;
    }

    private void mapToEntity(AddressDto dto, Address entity) {
        entity.setRecipientName(dto.getRecipientName().trim());
        entity.setPhoneNumber(dto.getPhoneNumber().trim());
        entity.setStreetAddress(dto.getStreetAddress().trim());
        entity.setCity(dto.getCity().trim());
        entity.setState(dto.getState().trim());
        entity.setPostalCode(dto.getPostalCode().trim());
        if (dto.getCountry() != null && !dto.getCountry().isEmpty()) {
            entity.setCountry(dto.getCountry().trim());
        }
        entity.setDefault(dto.isDefault());
    }
}
