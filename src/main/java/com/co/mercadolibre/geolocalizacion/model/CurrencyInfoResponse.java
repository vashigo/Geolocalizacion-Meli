package com.co.mercadolibre.geolocalizacion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CurrencyInfoResponse {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("utc_offset")
    private String utc;

}
