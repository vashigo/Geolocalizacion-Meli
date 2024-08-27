package com.co.mercadolibre.geolocalizacion.model;

import lombok.Data;

import java.util.List;

@Data
public class IpInfoResponse {
    private String countryName;
    private String countryCode;
    private List<String> languages;
    private List<String> timeZones;
    private double distanceToBuenosAires;
    private String currency;
    private double currencyExchangeRate;
}
