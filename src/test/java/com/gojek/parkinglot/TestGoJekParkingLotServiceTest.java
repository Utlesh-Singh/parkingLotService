package com.gojek.parkinglot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gojek.parkinglot.config.LogService;
import com.gojek.parkinglot.config.Logger;
import com.gojek.parkinglot.entity.database.*;
import com.gojek.parkinglot.entity.elastic.ElasticParkingLot;
import com.gojek.parkinglot.model.ElasticVacantParkingLotsResponse;
import com.gojek.parkinglot.model.GeoPoint;
import com.gojek.parkinglot.model.Vehicle;
import com.gojek.parkinglot.service.elasticservices.ElasticIndexService;
import com.gojek.parkinglot.service.elasticservices.ElasticSearchService;
import com.gojek.parkinglot.service.parkinglotservices.CustomerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestGoJekParkingLotServiceTest {

    @Autowired
    private ElasticSearchService elasticSearchService;
    @Autowired
    private ElasticIndexService elasticIndexService;
    @Autowired
    private CustomerDAO customerDAO;
    @Autowired
    private ParkingLotDAO parkingLotDAO;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ParkingServiceDAO parkingServiceDAO;
    @Autowired
    private LogService logService;
    private Logger logger;
    @Autowired
    private ApplicationContext context;

    @Before
    public void init() {
        logService = context.getBean(LogService.class);
        logger = logService.getLogger(CustomerService.class);
    }

    @Test
    public void parkingService() throws Exception {

        ParkingLot parkingLot1 = new ParkingLot(1200, "Aundh area", new GeoPoint(40, 50),
                Arrays.asList(new VehicleClass("small", 4, 20.0, 15.0),
                        new VehicleClass("medium", 2, 40.0, 30.0),
                        new VehicleClass("large", 1, 80.0, 70.0),
                        new VehicleClass("xlarge", 1, 100.0, 100.0)));

        parkingServiceDAO.saveParkingLot(parkingLot1);
        ElasticParkingLot elasticParkingLot1 = new ElasticParkingLot(1200, "Aundh area", new GeoPoint(40, 50));
        elasticIndexService.indexEntity("parkingservice", "geoservice", "1200", elasticParkingLot1, true);

        ParkingLot parkingLot2 = new ParkingLot(1201, "Baner area", new GeoPoint(45, 50),
                Arrays.asList(new VehicleClass("first_class", 4, 20.0, 15.0),
                        new VehicleClass("second_class", 2, 40.0, 30.0),
                        new VehicleClass("third_class", 1, 80.0, 70.0),
                        new VehicleClass("fourth_class", 1, 100.0, 100.0),
                        new VehicleClass("fifth_class", 2, 150.0, 150.0)
                ));

        parkingServiceDAO.saveParkingLot(parkingLot2);
        ElasticParkingLot elasticParkingLot2 = new ElasticParkingLot(1201, "Baner area", new GeoPoint(45, 50));
        elasticIndexService.indexEntity("parkingservice", "geoservice", "1201", elasticParkingLot2, true);


        //register Customer
        // Model class should be one form  (model, size)
        //            MODEL_PQR = 5
        //            MODEL_ABC = 10
        //            MODEL_XYZ = 25
        //            MODEL_KLM = 70
        //            MODEL_FGH = 100
        //            MODEL_EFG = 150
        // To add more models of vehicles, add them to com.gojek.parkinglot.model.VehicleSizeForClass

        Customer customer1 = new Customer(12177, "utlesh", new Vehicle("1000", "singh_family", "black", "MODEL_XYZ"));
        elasticIndexService.indexEntity("customers", "customers_detail", "12177", customer1, true);
        parkingServiceDAO.saveCustomer(customer1);

        Customer customer2 = new Customer(12178, "rakesh", new Vehicle("1001", "rai_family", "white", "MODEL_KLM"));
        elasticIndexService.indexEntity("customers", "customers_detail", "12178", customer2, true);
        parkingServiceDAO.saveCustomer(customer2);

        Customer customer3 = new Customer(12179, "raj", new Vehicle("1002", "akshay", "blue", "MODEL_ABC"));
        elasticIndexService.indexEntity("customers", "customers_detail", "12179", customer3, true);
        parkingServiceDAO.saveCustomer(customer3);

        // Get parking details for the customer


        Map<String, ElasticVacantParkingLotsResponse> details = customerService.serveParking(12177, 40.0, 45.0, 1500.0);

        logger.info("Details of parking services in given range " + details.toString());
        //Book parking lot. Customer can choose one of the class in any parking service. Here he choose large class form parking service whose licence_id is 1200

        CustomerParkingLotRelation customerParkingLotRelation1 = new CustomerParkingLotRelation(12177, 1200, "large");
        boolean status1 = customerService.bookParkingLot(1200, 12177,"large");
        if (status1 == true) {
            parkingServiceDAO.saveCustomerParkingLotRelation(customerParkingLotRelation1);
            logger.info("Booked parking lot for customer 12177 parking lot with licence id 1200 for class large");
        } else
            logger.info("Cannot book parking lot for customer 12177 parking lot with licence id 1200 for class large");

        Assert.assertTrue(status1);

        CustomerParkingLotRelation customerParkingLotRelation2 = new CustomerParkingLotRelation(12179, 1200, "large");
        boolean status2 = customerService.bookParkingLot(1200, 12179, "large");
        if (status2 == true) {
            parkingServiceDAO.saveCustomerParkingLotRelation(customerParkingLotRelation2);
            logger.info("Booked parking lot for customer 12179 parking lot with licence id 1200 for class large");
        } else
            logger.info("Cannot book parking lot for customer 12179 parking lot with licence id 1200 for class large");

        Assert.assertFalse(status2);
        //Release parking for customer 12177 and now 12179 can have this parking lot.
        customerService.releaseParkingLot(1200, 12177);


        CustomerParkingLotRelation customerParkingLotRelation3 = new CustomerParkingLotRelation(12179, 1200, "large");
        boolean status3 = customerService.bookParkingLot(1200, 12179, "large");
        if (status3 == true) {
            parkingServiceDAO.saveCustomerParkingLotRelation(customerParkingLotRelation3);
            logger.info("Booked parking lot for customer 12179 parking lot with licence id 1200 for class large");
        } else
            logger.info("Cannot book parking lot for customer 12179 parking lot with licence id 1200 for class large");

        customerService.releaseParkingLot(1200, 12179);
        Assert.assertTrue(status3);

    }
}

