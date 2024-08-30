package com.co.mercadolibre.geolocalizacion.service;

import com.co.mercadolibre.geolocalizacion.model.CurrencyInfoResponse;
import com.co.mercadolibre.geolocalizacion.model.IpInfoResponse;
import com.co.mercadolibre.geolocalizacion.model.TimeZoneInfoResponse;

public interface GeolocalizationApiCallCacheService {

    IpInfoResponse fetchIpInfo(String ip);
    TimeZoneInfoResponse fetchTimeZoneInfo(String ip);
    CurrencyInfoResponse fetchCurrencyInfo(String ip);
    String fetchCurrencyExchangeRate(String currency);
}
