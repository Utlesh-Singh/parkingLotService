package com.gojek.parkinglot.service.elasticservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.support.RetryTemplate;

public class AbstractElasticService {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    public Client client;

    @Autowired
    public RetryTemplate elasticRetryTemplate;

}
