package com.gojek.parkinglot.service.parkinglotservices;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gojek.parkinglot.config.LogService;
import com.gojek.parkinglot.config.Logger;
import com.gojek.parkinglot.entity.database.*;
import com.gojek.parkinglot.entity.database.CustomerDAO;
import com.gojek.parkinglot.entity.database.ParkingLotDAO;
import com.gojek.parkinglot.entity.elastic.ElasticParkingLot;
import com.gojek.parkinglot.model.ElasticVacantParkingLotsResponse;
import com.gojek.parkinglot.model.GeoPoint;
import com.gojek.parkinglot.model.VehicleSizeForClass;
import com.gojek.parkinglot.service.elasticservices.ElasticIndexService;
import com.gojek.parkinglot.service.elasticservices.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class CustomerService {

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private ElasticIndexService elasticIndexService;

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private  CustomerParkingLotRelationDAO customerParkingLotRelationDAO;

    @Autowired
    private ParkingLotDAO parkingLotDAO;

    @Autowired
    private ParkingServiceDAO parkingServiceDAO;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LogService logService;

    private Logger logger;

    @PostConstruct
    public void init(){
        logger = logService.getLogger(CustomerService.class);
    }

    public Map<String, ElasticVacantParkingLotsResponse> serveParking(Integer customer_id, Double lat, Double lon, Double max_distance){

        logger.info("Fetching available parking lot details for a customer");
        List<Map<String,Object>> responses = elasticSearchService.search("parkingservice", "geoservice", String.valueOf(max_distance), new GeoPoint(lat,lon));

        Customer customer = customerDAO.findById(customer_id).get();
        String vehicle_model_name = customer.getVehicle().vehicle_model_name;
        ParkingLot parkingLot = null;

        Map<String, ElasticVacantParkingLotsResponse> parkingLotsWithInGivenDistance = new HashMap<>();
        for(Map<String,Object> response: responses){

            ElasticVacantParkingLotsResponse elasticVacantParkingLotsResponse = new ElasticVacantParkingLotsResponse();
            List<VehicleClass> vehicleClassList = new LinkedList<>();

            ElasticParkingLot elasticParkingLot = objectMapper.convertValue(response, ElasticParkingLot.class);
            parkingLot = parkingLotDAO.findById(elasticParkingLot.getLicence_id()).get();
            String size = VehicleSizeForClass.getvehicleSizeForClass().get(vehicle_model_name);

            elasticVacantParkingLotsResponse.setLicence_id(elasticParkingLot.getLicence_id());
            elasticVacantParkingLotsResponse.setArea_name(elasticParkingLot.getArea_name());
            elasticVacantParkingLotsResponse.setLocation(elasticParkingLot.getLocation());

            List<VehicleClass> vehicleClasses = parkingLot.getVehicleClass();
            for(VehicleClass vehicleClass : vehicleClasses){
                  if(vehicleClass.getSize() > Double.parseDouble(size) && parkingLot.getVacancyForClass(vehicleClass.getParking_class_name()) > 0){

                      VehicleClass vehicleClassobj = new VehicleClass();
                      vehicleClassobj.setVacancy(parkingLot.getVacancyForClass(vehicleClass.getParking_class_name()));
                      vehicleClassobj.setSize(vehicleClass.getSize());
                      vehicleClassobj.setPrice(vehicleClass.getPrice());
                      vehicleClassobj.setParking_class_name(vehicleClass.getParking_class_name());

                      vehicleClassList.add(vehicleClassobj);
                      elasticVacantParkingLotsResponse.setVehicleClasses(vehicleClassList);
                  }
            }

            parkingLotsWithInGivenDistance.put(String.valueOf(elasticParkingLot.getLicence_id()), elasticVacantParkingLotsResponse);

        }

        logger.info("Fetching available parking lot details for a customer completed");
        return parkingLotsWithInGivenDistance;
    }

    public boolean indexCustomerDetails(Customer customer){
        return elasticIndexService.indexEntity("customers", "customers_detail", String.valueOf(customer.getCustomer_id()), customer,true);
    }

    public void indexParkingLotDetails(Integer licence_id, Double lat, Double lon, String area){
        logger.info("Started indexing parking lot details");
        GeoPoint geoPoint = new GeoPoint(lat, lon);
        ElasticParkingLot elasticParkingLot = new ElasticParkingLot(licence_id, area, geoPoint);
        elasticIndexService.indexEntity("parkingservice","geoservice",String.valueOf(licence_id),elasticParkingLot, true);
        logger.info("Indexing parking lot details completed");
    }

    public boolean bookParkingLot(Integer licence_id, Integer customer_id, String class_opted){
        logger.info("Started booking parking lot");
        if(customerParkingLotRelationDAO.findById(customer_id).isPresent())
        {
            logger.info("Customer already has booked parking lot");
            return false;
        }
        ParkingLot parkingLot = parkingLotDAO.findById(licence_id).get();
        boolean status = parkingLot.getParking(class_opted);
        parkingLotDAO.save(parkingLot);
        logger.info("Booking parking lot completed");
        return status;
    }

    public void releaseParkingLot(Integer licence_id, Integer customer_id){
        logger.info("Started releasing parking lot");
        if(!customerParkingLotRelationDAO.findById(customer_id).isPresent()){
            logger.info("This customer didn't book any parking lot");
            return ;
        }
        ParkingLot parkingLot = parkingLotDAO.findById(licence_id).get();
        CustomerParkingLotRelation customerParkingLotRelation = customerParkingLotRelationDAO.findById(customer_id).get();
        parkingLot.releaseParking(customerParkingLotRelation.getClass_booked());
        parkingServiceDAO.deleteCustomerParkingLotRelation(customerParkingLotRelation);
        parkingLotDAO.save(parkingLot);
        logger.info("Releasing parking lot completed");
    }
}
