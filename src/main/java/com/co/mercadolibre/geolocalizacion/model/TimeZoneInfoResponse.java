package com.co.mercadolibre.geolocalizacion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TimeZoneInfoResponse {

    @JsonProperty("time")
    private String time;

}
