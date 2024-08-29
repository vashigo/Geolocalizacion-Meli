package com.co.mercadolibre.geolocalizacion.controller;

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

    @GetMapping("/ipinfo")
    public ResponseEntity<String> getIpInfo(@RequestParam("ip") String ip) {
        String response = geolocalizationService.getIpInfo(ip);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        StatisticsResponse response = geolocalizationService.getStatistics();
        return ResponseEntity.ok(response);
    }

}
