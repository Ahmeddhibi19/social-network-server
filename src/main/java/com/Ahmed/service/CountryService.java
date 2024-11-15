package com.Ahmed.service;

import com.Ahmed.entity.Country;

import java.util.List;

public interface CountryService {
    Country getCountryById(Long id);
    Country getCountryByName(String name);
    List<Country> getCountryList();
}
