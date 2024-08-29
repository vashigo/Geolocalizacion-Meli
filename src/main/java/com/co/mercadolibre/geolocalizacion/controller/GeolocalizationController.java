package com.co.mercadolibre.geolocalizacion.controller;

import com.co.mercadolibre.geolocalizacion.exception.ResourceNotFoundException;
import com.co.mercadolibre.geolocalizacion.model.StatisticsResponse;
import com.co.mercadolibre.geolocalizacion.service.GeolocalizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/geolocalization")
public class GeolocalizationController {

    @Autowired
    private GeolocalizationService geolocalizationService;

    /**
     * Retrieves geolocation information for the given IP address.
     *
     * @param ip the IP address to retrieve information for.
     * @return a ResponseEntity containing the geolocation information.
     */
    @GetMapping("/ipinfo")
    public ResponseEntity<String> getIpInfo(@RequestParam("ip") String ip) {
        String response = geolocalizationService.getIpInfo(ip);
        if (response == null) {
            throw new ResourceNotFoundException("IP information not found for: " + ip);
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves statistical data based on previously queried IPs.
     *
     * @return a ResponseEntity containing the statistics of queried IPs.
     */
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        StatisticsResponse response = geolocalizationService.getStatistics();
        return ResponseEntity.ok(response);
    }

}
