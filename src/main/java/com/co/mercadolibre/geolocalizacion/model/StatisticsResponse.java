package com.co.mercadolibre.geolocalizacion.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatisticsResponse {
    private double maxDistance;
    private double minDistance;
    private double averageDistance;
}
