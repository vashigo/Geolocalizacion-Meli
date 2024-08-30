package com.co.mercadolibre.geolocalizacion.repository;

import com.co.mercadolibre.geolocalizacion.repository.entity.IpQuery;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class IpQueryCustomRepository {

    private final MongoTemplate mongoTemplate;

    public IpQueryCustomRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void incrementInvocationCountAndDistance(String ip, double additionalDistance) {
        Query query = new Query(Criteria.where("ip").is(ip));
        Update update = new Update().inc("invocationCount", 1).inc("totalDistance", additionalDistance);
        mongoTemplate.updateFirst(query, update, IpQuery.class);
    }
}