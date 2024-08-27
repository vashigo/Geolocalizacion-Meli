package com.co.mercadolibre.geolocalizacion.model;

import lombok.Data;

@Data
public class StatisticsResponse {
    private double maxDistance;
    private double minDistance;
    private double averageDistance;
}
