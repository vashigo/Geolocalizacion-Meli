package com.co.mercadolibre.geolocalizacion.service;

import com.co.mercadolibre.geolocalizacion.model.CurrencyInfoResponse;
import com.co.mercadolibre.geolocalizacion.model.IpInfoResponse;
import com.co.mercadolibre.geolocalizacion.model.StatisticsResponse;
import com.co.mercadolibre.geolocalizacion.model.TimeZoneInfoResponse;
import com.co.mercadolibre.geolocalizacion.repository.IpQueryRepository;
import com.co.mercadolibre.geolocalizacion.repository.entity.IpQuery;
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

    private static final String IP_API_URL = "https://api.ipapi.com/api/{ip}?access_key=ec89b2a04a67ea6c70da666c90764785&language=es";
    private static final String TIMEZONE_API_URL = "https://timeapi.io/api/time/current/ip?ipAddress={ip}";
    private static final String CURRENCY_API_URL = "https://ipapi.co/{ip}/json/";
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String CURRENCY_API_FORMAT = "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@%s/v1/currencies/%s.json";
    private static final double BUENOS_AIRES_LAT = -34.603722;
    private static final double BUENOS_AIRES_LON = -58.381592;
    private static final double EARTH_RADIUS_KM = 6371;

    private final RestTemplate restTemplate;
    private final IpQueryRepository ipQueryRepository;

    public GeolocalizationServiceImpl(RestTemplate restTemplate, IpQueryRepository ipQueryRepository) {
        this.restTemplate = restTemplate;
        this.ipQueryRepository = ipQueryRepository;
    }

    /**
     * Retrieves and processes geolocation information for a given IP address.
     *
     * @param ip the IP address to retrieve information for.
     * @return a formatted string containing the geolocation, timezone, and currency information.
     */
    @Override
    @Cacheable(value = "ipInfoStringCache", key = "#ip")
    public String getIpInfo(String ip) {
        IpInfoResponse ipInfoResponse = fetchIpInfo(ip);
        TimeZoneInfoResponse timeZoneResponse = fetchTimeZoneInfo(ip);
        CurrencyInfoResponse currencyInfoResponse = fetchCurrencyInfo(ip);

        double distanceToBuenosAires = calculateDistanceToBuenosAires(ipInfoResponse.getLatitude(), ipInfoResponse.getLongitude());
        ipInfoResponse.setDistanceToBuenosAires(distanceToBuenosAires);
        String currencyExchangeRate = fetchCurrencyExchangeRate(currencyInfoResponse.getCurrency().toLowerCase());
        String formattedLanguages = formatLanguages(ipInfoResponse);
        String currentDateTime = formatCurrentDateTime();

        saveIpQuery(ip, ipInfoResponse, timeZoneResponse, currencyInfoResponse, currencyExchangeRate, formattedLanguages);

        return formatResponse(ip, ipInfoResponse, timeZoneResponse, currencyInfoResponse, distanceToBuenosAires, currencyExchangeRate, formattedLanguages, currentDateTime);
    }

    /**
     * Retrieves and calculates statistical data based on previously queried IPs.
     *
     * @return a StatisticsResponse containing the maximum, minimum, and average distance to Buenos Aires.
     */
    @Override
    public StatisticsResponse getStatistics() {
        List<IpQuery> queries = ipQueryRepository.findAll();

        double maxDistance = queries.stream().mapToDouble(IpQuery::getDistanceToBuenosAires).max().orElse(0);
        double minDistance = queries.stream().mapToDouble(IpQuery::getDistanceToBuenosAires).min().orElse(0);
        double averageDistance = queries.stream().mapToDouble(IpQuery::getDistanceToBuenosAires).average().orElse(0);

        return new StatisticsResponse(maxDistance, minDistance, averageDistance);
    }

    /**
     * Fetches geolocation information for a given IP address.
     *
     * @param ip the IP address to fetch geolocation information for.
     * @return an IpInfoResponse object containing geolocation details.
     */
    private IpInfoResponse fetchIpInfo(String ip) {
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
    private TimeZoneInfoResponse fetchTimeZoneInfo(String ip) {
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
    private CurrencyInfoResponse fetchCurrencyInfo(String ip) {
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
    private String fetchCurrencyExchangeRate(String currency) {
        String currentDate = LocalDate.now().toString();
        String url = String.format(CURRENCY_API_FORMAT, currentDate, currency);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        Map<String, Object> rates = (Map<String, Object>) response.get(currency);
        return (rates != null && rates.containsKey("usd")) ? rates.get("usd").toString() : "N/A";
    }

    /**
     * Formats the languages from the IpInfoResponse into a readable string.
     *
     * @param ipInfoResponse the IpInfoResponse containing the language information.
     * @return a formatted string of languages.
     */
    private String formatLanguages(IpInfoResponse ipInfoResponse) {
        return ipInfoResponse.getLocation().getLanguages().stream()
                .map(lang -> lang.getName() + " (" + lang.getCode() + ")")
                .collect(Collectors.joining(", "));
    }

    /**
     * Formats the current date and time using the predefined format.
     *
     * @return the current date and time as a formatted string.
     */
    private String formatCurrentDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        return LocalDateTime.now().format(formatter);
    }

    /**
     * Saves or updates an IpQuery entity in the repository.
     *
     * @param ip the IP address associated with the query.
     * @param ipInfoResponse the geolocation information.
     * @param timeZoneResponse the timezone information.
     * @param currencyResponse the currency information.
     * @param currencyExchangeRate the currency exchange rate.
     * @param languages the formatted languages.
     */
    private void saveIpQuery(String ip, IpInfoResponse ipInfoResponse, TimeZoneInfoResponse timeZoneResponse, CurrencyInfoResponse currencyResponse, String currencyExchangeRate, String languages) {
        // Buscar si ya existe un registro con la misma IP
        IpQuery ipQuery = ipQueryRepository.findByIp(ip);

        if (ipQuery == null) {
            // Si no existe, creamos una nueva instancia de IpQuery
            ipQuery = new IpQuery();
        }

        // En ambos casos, rellenamos el objeto IpQuery
        ipQuery.setIp(ip);
        ipQuery.setFormattedDateTime(formatCurrentDateTime());
        ipQuery.setCountryName(ipInfoResponse.getCountryName());
        ipQuery.setCountryCode(ipInfoResponse.getCountryCode());
        ipQuery.setLanguages(languages);
        ipQuery.setCurrency(currencyResponse.getCurrency().toLowerCase());
        ipQuery.setCurrencyExchangeRate(currencyExchangeRate);
        ipQuery.setTime(timeZoneResponse.getTime());
        ipQuery.setUtcOffset(currencyResponse.getUtc());
        ipQuery.setDistanceToBuenosAires(ipInfoResponse.getDistanceToBuenosAires());
        ipQuery.setLatitude(ipInfoResponse.getLatitude());
        ipQuery.setLongitude(ipInfoResponse.getLongitude());
        ipQuery.setBuenosAiresLatitude(BUENOS_AIRES_LAT);
        ipQuery.setBuenosAiresLongitude(BUENOS_AIRES_LON);

        // Guardar o actualizar el registro
        ipQueryRepository.save(ipQuery);
    }

    /**
     * Formats the timezone and UTC offset information into a readable string.
     *
     * @param timeResponse the TimeZoneInfoResponse containing the time information.
     * @param currencyResponse the CurrencyInfoResponse containing the UTC offset information.
     * @return a formatted string of the time and UTC offset.
     */
    private String formatTimezone(TimeZoneInfoResponse timeResponse, CurrencyInfoResponse currencyResponse) {
        String utcOffset = currencyResponse.getUtc();
        if (!utcOffset.startsWith("+") && !utcOffset.startsWith("-")) {
            utcOffset = "+" + utcOffset;
        }
        return timeResponse.getTime() + " (" + utcOffset + ")";
    }

    /**
     * Formats all the information retrieved and calculated for a given IP into a final response string.
     *
     * @param ip the IP address.
     * @param ipInfoResponse the geolocation information.
     * @param timeResponse the timezone information.
     * @param currencyResponse the currency information.
     * @param distanceToBuenosAires the calculated distance to Buenos Aires.
     * @param currencyExchangeRate the currency exchange rate.
     * @param languages the formatted languages.
     * @param currentDateTime the current date and time.
     * @return a formatted response string containing all the information.
     */
    private String formatResponse(String ip, IpInfoResponse ipInfoResponse, TimeZoneInfoResponse timeResponse, CurrencyInfoResponse currencyResponse, double distanceToBuenosAires, String currencyExchangeRate, String languages, String currentDateTime) {
        return String.format(
                """
                        IP: %s, fecha actual: %s
                        País: %s (%s)
                        ISO Code: %s
                        Idiomas: %s
                        Moneda: %s (1 %s = %s USD)
                        Hora: %s (%s)
                        Distancia estimada: %.0f kms (%.0f, %.0f) a (%.0f, %.0f)""",
                ip,
                currentDateTime,
                ipInfoResponse.getCountryName(),
                ipInfoResponse.getCountryName().toLowerCase(),
                ipInfoResponse.getCountryCode(),
                languages,
                currencyResponse.getCurrency(),
                currencyResponse.getCurrency(),
                currencyExchangeRate,
                timeResponse.getTime(),
                currencyResponse.getUtc(),
                distanceToBuenosAires,
                BUENOS_AIRES_LAT, BUENOS_AIRES_LON,
                ipInfoResponse.getLatitude(), ipInfoResponse.getLongitude()
        );
    }

    /**
     * Calculates the distance between a given latitude and longitude and Buenos Aires.
     *
     * @param latitude the latitude to calculate the distance from.
     * @param longitude the longitude to calculate the distance from.
     * @return the calculated distance in kilometers.
     */
    private double calculateDistanceToBuenosAires(double latitude, double longitude) {
        double dLat = Math.toRadians(latitude - BUENOS_AIRES_LAT);
        double dLon = Math.toRadians(longitude - BUENOS_AIRES_LON);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(BUENOS_AIRES_LAT)) * Math.cos(Math.toRadians(latitude)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

}
