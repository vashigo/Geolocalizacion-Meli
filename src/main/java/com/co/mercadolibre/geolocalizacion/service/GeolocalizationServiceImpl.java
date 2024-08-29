package com.co.mercadolibre.geolocalizacion.service;

import com.co.mercadolibre.geolocalizacion.model.*;
import com.co.mercadolibre.geolocalizacion.repository.entity.IpQuery;
import com.co.mercadolibre.geolocalizacion.repository.IpQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeolocalizationServiceImpl implements GeolocalizationService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private IpQueryRepository ipQueryRepository;

    private static final String IP_API_URL = "https://api.ipapi.com/api/{ip}?access_key=ec89b2a04a67ea6c70da666c90764785&language=es";
    private static final String TIMEZONE_API_URL = "https://timeapi.io/api/time/current/ip?ipAddress={ip}";
    private static final String CURRENCY_API_URL = "https://ipapi.co/{ip}/json/";

    @Override
    @Cacheable(value = "ipInfoStringCache", key = "#ip")
    public String getIpInfo(String ip) {
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("ip", ip);

        // Realizar la solicitud a la API de ipapi
        IpInfoResponse response = restTemplate.getForObject(IP_API_URL, IpInfoResponse.class, uriVariables);

        TimeZoneInfoResponse timeResponse = restTemplate.getForObject(TIMEZONE_API_URL, TimeZoneInfoResponse.class, uriVariables);

        CurrencyInfoResponse currencyResponse = restTemplate.getForObject(CURRENCY_API_URL, CurrencyInfoResponse.class, uriVariables);

        // Calcular la distancia a Buenos Aires
        double distanceToBuenosAires = calculateDistanceToBuenosAires(response.getLatitude(), response.getLongitude());
        response.setDistanceToBuenosAires(distanceToBuenosAires);

        // Obtener la tasa de cambio de la moneda
        String currencyExchangeRate = getCurrencyExchangeRate(currencyResponse.getCurrency().toLowerCase());

        // Formatear la fecha y hora actual
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String currentDateTime = LocalDateTime.now().format(formatter);

        // Formatear los idiomas
        String languages = response.getLocation().getLanguages().stream()
                .map(lang -> lang.getName() + " (" + lang.getCode() + ")")
                .collect(Collectors.joining(", "));

        // Guardar en MongoDB
        saveIpQuery(ip, response, timeResponse, currencyResponse, languages);

        // Formatear la respuesta
        return String.format(
                "IP: %s, fecha actual: %s\n" +
                        "País: %s (%s)\n" +
                        "ISO Code: %s\n" +
                        "Idiomas: %s\n" +
                        "Moneda: EUR (1 EUR = %s USD)\n" +
                        "Hora: %s (%s)\n" +
                        "Distancia estimada: %.0f kms (%.0f, %.0f) a (%.0f, %.0f)",
                ip,
                currentDateTime,
                response.getCountryName(),
                response.getCountryName().toLowerCase(),
                response.getCountryCode(),
                languages,
                currencyExchangeRate,
                timeResponse.getTime(),
                currencyResponse.getUtc(),
                distanceToBuenosAires,
                -34.603722, -58.381592,  // Coordenadas de Buenos Aires
                response.getLatitude(), response.getLongitude()
        );
    }

    @Override
    public StatisticsResponse getStatistics() {
        List<IpQuery> queries = ipQueryRepository.findAll();

        double maxDistance = queries.stream().mapToDouble(IpQuery::getDistanceToBuenosAires).max().orElse(0);
        double minDistance = queries.stream().mapToDouble(IpQuery::getDistanceToBuenosAires).min().orElse(0);
        double averageDistance = queries.stream().mapToDouble(IpQuery::getDistanceToBuenosAires).average().orElse(0);

        StatisticsResponse stats = new StatisticsResponse();
        stats.setMaxDistance(maxDistance);
        stats.setMinDistance(minDistance);
        stats.setAverageDistance(averageDistance);

        return stats;
    }

    private String getCurrencyExchangeRate(String currency) {
        // Convertir la moneda a minúsculas
        String lowerCaseCurrency = currency.toLowerCase();

        // Obtener la fecha actual en el formato yyyy-MM-dd
        String currentDate = LocalDate.now().toString();

        // Construir la URL usando la fecha actual y la moneda en minúsculas
        String url = String.format("https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@%s/v1/currencies/%s.json", currentDate, lowerCaseCurrency);

        // Realizar la solicitud a la API de currency-api
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // Extraer las tasas de cambio desde el objeto de la respuesta
        Map<String, Object> rates = (Map<String, Object>) response.get(lowerCaseCurrency);

        // Obtener la tasa de cambio con respecto al USD
        if (rates != null && rates.containsKey("usd")) {
            return rates.get("usd").toString();
        } else {
            return "N/A";
        }
    }

    private void saveIpQuery(String ip, IpInfoResponse response, TimeZoneInfoResponse timeResponse, CurrencyInfoResponse currencyResponse , String languages) {
        IpQuery ipQuery = new IpQuery();
        ipQuery.setIp(ip);
        ipQuery.setDistanceToBuenosAires(response.getDistanceToBuenosAires());
        ipQuery.setQueryTime(LocalDateTime.now());
        ipQuery.setCountryName(response.getCountryName());
        ipQuery.setCountryCode(response.getCountryCode());
        ipQuery.setLanguages(languages);
        ipQuery.setTimezone(timeResponse.getTime() + " (" + currencyResponse.getUtc() + ")");
        ipQuery.setCurrency(currencyResponse.getCurrency());

        ipQueryRepository.save(ipQuery);
    }

    private double calculateDistanceToBuenosAires(double latitude, double longitude) {
        // Coordenadas de Buenos Aires, Argentina
        double buenosAiresLat = -34.603722;
        double buenosAiresLon = -58.381592;

        // Radio de la Tierra en kilómetros
        double earthRadius = 6371;

        // Diferencias en latitud y longitud
        double dLat = Math.toRadians(latitude - buenosAiresLat);
        double dLon = Math.toRadians(longitude - buenosAiresLon);

        // Aplicación de la fórmula del Haversine
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(buenosAiresLat)) * Math.cos(Math.toRadians(latitude)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Distancia entre los dos puntos
        return earthRadius * c;
    }

}
