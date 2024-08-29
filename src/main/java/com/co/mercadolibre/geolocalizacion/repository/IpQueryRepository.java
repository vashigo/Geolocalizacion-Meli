package com.co.mercadolibre.geolocalizacion.repository;

import com.co.mercadolibre.geolocalizacion.repository.entity.IpQuery;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IpQueryRepository extends MongoRepository<IpQuery, String> {
    List<IpQuery> findAllByOrderByDistanceToBuenosAiresDesc();
    List<IpQuery> findAllByOrderByDistanceToBuenosAiresAsc();
}