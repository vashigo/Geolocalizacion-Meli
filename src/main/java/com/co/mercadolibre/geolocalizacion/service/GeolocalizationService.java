package com.co.mercadolibre.geolocalizacion.service;

import com.co.mercadolibre.geolocalizacion.model.IpInfoResponse;
import com.co.mercadolibre.geolocalizacion.model.StatisticsResponse;

public interface GeolocalizationService {

    IpInfoResponse getIpInfo(String ip);
    StatisticsResponse getStatistics();
}
