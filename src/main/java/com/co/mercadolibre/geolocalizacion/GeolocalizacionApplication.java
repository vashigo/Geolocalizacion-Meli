package com.co.mercadolibre.geolocalizacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
public class GeolocalizacionApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeolocalizacionApplication.class, args);
	}

}
