package com.co.mercadolibre.geolocalizacion.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "ipQueries")
public class IpQuery {

    @Id
    private String id;
    private String ip;
    private double distanceToBuenosAires;
    private LocalDateTime queryTime;
    private String countryName;
    private String countryCode;
    private String languages;
    private String timezone;
    private String currency;
}