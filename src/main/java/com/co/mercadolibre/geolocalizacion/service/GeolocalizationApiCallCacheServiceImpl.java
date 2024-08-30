package com.co.mercadolibre.geolocalizacion.service;

import com.co.mercadolibre.geolocalizacion.model.CurrencyInfoResponse;
import com.co.mercadolibre.geolocalizacion.model.IpInfoResponse;
import com.co.mercadolibre.geolocalizacion.model.TimeZoneInfoResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeolocalizationApiCallCacheServiceImpl implements GeolocalizationApiCallCacheService {

    private final RestTemplate restTemplate;

    private static final String IP_API_URL = "https://api.ipapi.com/api/{ip}?access_key=b00e5121cea9da2ed0d54a7d711878bf&language=es";
    private static final String TIMEZONE_API_URL = "https://timeapi.io/api/time/current/ip?ipAddress={ip}";
    private static final String CURRENCY_API_URL = "https://ipapi.co/{ip}/json/";
    private static final String CURRENCY_API_FORMAT = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@%s/v1/currencies/%s.json";

    public GeolocalizationApiCallCacheServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches geolocation information for a given IP address.
     *
     * @param ip the IP address to fetch geolocation information for.
     * @return an IpInfoResponse object containing geolocation details.
     */
    @Override
    @Cacheable(value = "ipInfoCache", key = "#ip + '-geo'")
    public IpInfoResponse fetchIpInfo(String ip) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("ip", ip);
        return restTemplate.getForObject(IP_API_URL, IpInfoResponse.class, uriVariables);
    }

    /**
     * Fetches timezone information for a given IP address.
     *
     * @param ip the IP address to fetch timezone information for.
     * @return a TimeZoneInfoResponse object containing timezone details.
     */
    @Override
    @Cacheable(value = "ipInfoCache", key = "#ip + '-tz'")
    public TimeZoneInfoResponse fetchTimeZoneInfo(String ip) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("ip", ip);
        return restTemplate.getForObject(TIMEZONE_API_URL, TimeZoneInfoResponse.class, uriVariables);
    }

    /**
     * Fetches currency information for a given IP address.
     *
     * @param ip the IP address to fetch currency information for.
     * @return a CurrencyInfoResponse object containing currency details.
     */
    @Override
    @Cacheable(value = "ipInfoCache", key = "#ip + '-currency'")
    public CurrencyInfoResponse fetchCurrencyInfo(String ip) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("ip", ip);
        return restTemplate.getForObject(CURRENCY_API_URL, CurrencyInfoResponse.class, uriVariables);
    }

    /**
     * Fetches the exchange rate of the given currency to USD.
     *
     * @param currency the currency code to fetch the exchange rate for.
     * @return the exchange rate as a String, or "N/A" if not available.
     */
    @Override
    @Cacheable(value = "ipInfoCache", key = "#currency + '-exchangeRate'")
    public String fetchCurrencyExchangeRate(String currency) {
        String currentDate = LocalDate.now().toString();
        String url = String.format(CURRENCY_API_FORMAT, currentDate, currency);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        Map<String, Object> rates = (Map<String, Object>) response.get(currency);
        return (rates != null && rates.containsKey("usd")) ? rates.get("usd").toString() : "N/A";
    }

}
