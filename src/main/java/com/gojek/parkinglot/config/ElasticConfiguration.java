package com.gojek.parkinglot.config;

import com.gojek.parkinglot.exceptions.RetriableException;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.ExceptionClassifierRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ElasticConfiguration {
    @Bean
    public Client client() {
        Settings.Builder settingsBuilder = Settings.builder();
        settingsBuilder.put("cluster.name", "gojek-parking-service");
        TransportClient client = new PreBuiltTransportClient(settingsBuilder.build());

        try {
            client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9300));
        } catch (UnknownHostException e) {
            //throw new ElasticsearchConfigurationException("Error in connecting to Elasticsearch " + host, e);
        }

        return client;
    }

    @Bean(name = "elasticRetryTemplate")
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();

        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        exponentialBackOffPolicy.setMultiplier(2);
        exponentialBackOffPolicy.setMaxInterval(4000);
        exponentialBackOffPolicy.setInitialInterval(2);

        template.setBackOffPolicy(exponentialBackOffPolicy);

        //Simple retryPolicy
        Map<Class<? extends Throwable>, RetryPolicy> policyMap = new HashMap<>();
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(3);
        policyMap.put(RetriableException.class, simpleRetryPolicy);

        ExceptionClassifierRetryPolicy retryPolicy = new ExceptionClassifierRetryPolicy();
        retryPolicy.setPolicyMap(policyMap);
        template.setRetryPolicy(retryPolicy);

        return template;
    }
}
