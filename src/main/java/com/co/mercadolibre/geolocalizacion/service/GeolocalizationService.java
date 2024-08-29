package com.co.mercadolibre.geolocalizacion.service;

import com.co.mercadolibre.geolocalizacion.model.StatisticsResponse;

public interface GeolocalizationService {

    String getIpInfo(String ip);
    StatisticsResponse getStatistics();
}
