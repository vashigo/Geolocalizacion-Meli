package com.co.mercadolibre.geolocalizacion.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "ipQueries")
public class IpQuery {

    @Id
    private String id;
    private String ip;
    private String formattedDateTime;  // Fecha y hora actual formateada
    private String countryName;        // Nombre del país
    private String countryCode;        // ISO Code
    private String languages;          // Idiomas formateados
    private String currency;           // Moneda (EUR)
    private String currencyExchangeRate; // Tasa de cambio con respecto a USD
    private String time;               // Hora formateada
    private String utcOffset;          // Desplazamiento UTC formateado
    private double distanceToBuenosAires;
    private double latitude;           // Latitud de la IP
    private double longitude;          // Longitud de la IP
    private double buenosAiresLatitude;  // Coordenadas de Buenos Aires
    private double buenosAiresLongitude;
    private long invocationCount;  // Número total de invocaciones
    private double totalDistance;
}