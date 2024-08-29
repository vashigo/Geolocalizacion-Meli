package com.co.mercadolibre.geolocalizacion.controller;

import com.co.mercadolibre.geolocalizacion.exception.ResourceNotFoundException;
import com.co.mercadolibre.geolocalizacion.model.StatisticsResponse;
import com.co.mercadolibre.geolocalizacion.service.GeolocalizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Tag(name = "get", description = "GET metodos de Geolocalization APIs")
    @Operation(summary = "Obtener información de geolocalización para una IP específica",
            description = "Este endpoint permite obtener la información de geolocalización, zona horaria y moneda para una dirección IP dada.")
    @ApiResponse(responseCode = "200", description = "Información obtenida correctamente")
    @ApiResponse(responseCode = "404", description = "Información no encontrada para la IP proporcionada")
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
    @Tag(name = "get", description = "GET metodos de Geolocalization APIs")
    @Operation(summary = "Obtener estadísticas de las consultas de IP",
            description = "Este endpoint proporciona estadísticas como la distancia máxima, mínima y promedio a Buenos Aires de las IPs consultadas.")
    @ApiResponse(responseCode = "200", description = "Información obtenida correctamente")
    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> getStatistics() {
        StatisticsResponse response = geolocalizationService.getStatistics();
        return ResponseEntity.ok(response);
    }

}
