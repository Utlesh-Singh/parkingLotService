package com.gojek.parkinglot.service.elasticservices;

import com.gojek.parkinglot.config.LogService;
import com.gojek.parkinglot.config.Logger;
import com.gojek.parkinglot.exceptions.ElasticSearchException;
import com.gojek.parkinglot.model.GeoPoint;
import com.gojek.parkinglot.service.parkinglotservices.CustomerService;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.Map;

@Service
public class ElasticSearchService extends AbstractElasticService {

    @Autowired
    private LogService logService;

    private Logger logger;

    @PostConstruct
    public void init() {
        logger = logService.getLogger(CustomerService.class);
    }

    public LinkedList<Map<String, Object>> search(String index, String type, String distance, GeoPoint geoPoint) throws ElasticSearchException {

        LinkedList<Map<String, Object>> dataList = new LinkedList<>();

        try {
            SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setIndicesOptions(IndicesOptions.fromOptions(true, true, true, true));
            GeoDistanceQueryBuilder geoDistanceQueryBuilder = new GeoDistanceQueryBuilder("location");
            org.elasticsearch.common.geo.GeoPoint geopoint = new org.elasticsearch.common.geo.GeoPoint(geoPoint.lat(), geoPoint.lon());
            geoDistanceQueryBuilder.distance(String.valueOf(distance), DistanceUnit.KILOMETERS).point(geopoint);
            searchRequestBuilder.setQuery(geoDistanceQueryBuilder);

            SearchResponse searchResponse;
            try {
                searchResponse = searchRequestBuilder.execute().get();
            } catch (Exception e) {
                throw new ElasticSearchException("ElasticSearch Query execution failed due to:" + e.getMessage(), e);
            }

            SearchHits searchHits = searchResponse.getHits();
            if (searchHits != null) {
                SearchHit[] searchHitArray = searchHits.getHits();
                if (searchHitArray != null) {
                    for (SearchHit searchHit : searchHitArray) {
                        Map<String, Object> sourceMap = searchHit.getSourceAsMap();
                        sourceMap.put("id", searchHit.getId());
                        dataList.add(sourceMap);
                    }
                }
            }
        } catch (Exception exception) {
            logger.info(exception.getMessage(), exception);
            throw new ElasticSearchException("Error while searching in index" + index + "for type" + type + "for payload" + distance + " " + geoPoint);
        }

        return dataList;
    }
}

