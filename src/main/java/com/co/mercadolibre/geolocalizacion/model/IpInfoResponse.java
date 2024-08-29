package com.co.mercadolibre.geolocalizacion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class IpInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ip;
    @JsonProperty("country_name")
    private String countryName;
    @JsonProperty("country_code")
    private String countryCode;
    private String city;
    private String regionName;
    @JsonProperty("continent_name")
    private String continentName;
    private double latitude;
    private double longitude;
    @JsonProperty("location")
    private Location location;
    private double distanceToBuenosAires;
}
