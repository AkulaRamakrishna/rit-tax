package com.rogers.api.repository;

import com.rogers.api.model.TaxLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxLocationRepository extends JpaRepository<TaxLocation, String> {

}
