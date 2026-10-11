package com.bookverse.repository;

import com.bookverse.entity.MetadataImportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetadataImportJobRepository extends JpaRepository<MetadataImportJob, Long> {
    List<MetadataImportJob> findAllByOrderByStartedAtDesc();
}
