package com.gojek.parkinglot.service.elasticservices;

import com.gojek.parkinglot.config.LogService;
import com.gojek.parkinglot.config.Logger;
import com.gojek.parkinglot.exceptions.ElasticIndexException;
import com.gojek.parkinglot.exceptions.NonRetriableException;
import com.gojek.parkinglot.exceptions.RetriableException;
import com.gojek.parkinglot.service.parkinglotservices.CustomerService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.index.VersionType;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;

@Service
public class ElasticIndexService extends AbstractElasticService {
    @Autowired
    private LogService logService;

    private Logger logger;

    @PostConstruct
    public void init() {
        logger = logService.getLogger(CustomerService.class);
    }

    public boolean indexEntity(String indexName, String type, String primaryKey, Object entity, boolean refresh) throws ElasticIndexException {
        try{
            return indexEntity(indexName, type, primaryKey, entity, 0, refresh);
        } catch (Exception exception) {
            logger.info(exception.getMessage(), exception);
            throw new ElasticIndexException("Error while indexing in index" + indexName + "for type" + type + "for payload" + entity.toString());
        }

    }

    public boolean indexEntity(String indexName, String type, String primaryKey, Object entity, long version, boolean refresh) throws ElasticIndexException {
            HashMap entityMap = objectMapper.convertValue(entity, HashMap.class);
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, type);

            if (primaryKey != null) {
                indexRequestBuilder.setId(primaryKey);
            }

            if (refresh) {
                indexRequestBuilder.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
            }

            indexRequestBuilder.setSource(entityMap);

            if (version > 0 && version < Long.MAX_VALUE) {
                indexRequestBuilder.setVersion(version);
                indexRequestBuilder.setVersionType(VersionType.INTERNAL);
            }

            return this.elasticRetryTemplate.execute(
                    k -> {
                        try {
                            IndexResponse response = indexRequestBuilder.execute().get();
                            return true;
                        } catch (Exception e) {
                            if (ExceptionUtils.indexOfThrowable(e, VersionConflictEngineException.class) != -1) {
                                throw new NonRetriableException("Error while indexing into elastic.", e);
                            }
                            throw new RetriableException(e.getMessage(), e);
                        }
                    },
                    v -> {
                        Throwable throwable = v.getLastThrowable();
                        throw new ElasticIndexException(throwable.getMessage(), throwable);
                    }
            );
  }
}
