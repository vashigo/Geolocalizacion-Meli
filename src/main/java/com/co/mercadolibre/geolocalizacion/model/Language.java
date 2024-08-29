package com.co.mercadolibre.geolocalizacion.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Language{
    private String code;
    private String name;
    @JsonProperty("native")
    private String nativeName;
}
