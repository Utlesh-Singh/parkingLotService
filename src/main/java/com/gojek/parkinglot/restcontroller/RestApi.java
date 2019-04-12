package com.gojek.parkinglot.restcontroller;

import com.gojek.parkinglot.config.LogService;
import com.gojek.parkinglot.config.Logger;
import com.gojek.parkinglot.entity.database.*;
import com.gojek.parkinglot.errors.GoJekAppErrorList;
import com.gojek.parkinglot.exceptions.ElasticIndexException;
import com.gojek.parkinglot.exceptions.ElasticSearchException;
import com.gojek.parkinglot.exceptions.PlatformException;
import com.gojek.parkinglot.exceptions.RetriableException;
import com.gojek.parkinglot.model.*;
import com.gojek.parkinglot.entity.database.ParkingServiceDAO;
import com.gojek.parkinglot.service.parkinglotservices.CustomerService;
import org.hibernate.JDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.PostConstruct;
import java.util.*;

@RestController
@Api(value = "API's to get Asset data based on the filter", description = "API's to get Asset data based on the filter data", produces = "application/json",consumes = "application/json")
public class RestApi {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ParkingServiceDAO parkingDAO;

    @Autowired
    private LogService logService;

    private Logger logger;

    @PostConstruct
    public void init(){
        logger = logService.getLogger(CustomerService.class);
    }

    @RequestMapping(value = "/registerCustomer", produces = {"application/json"},method = RequestMethod.POST)
    @ResponseBody
    public boolean registerCustomer(
            @RequestParam(name="customer_id") Integer customer_id,
            @RequestParam(name="customer_name") String customer_name,
            @RequestHeader(name="vehicle_num") String vehicle_num,
            @RequestHeader(name="owner_name") String owner_name,
            @RequestHeader(name="color") String color,
            @RequestHeader(name="vehicle_model_name") String vehicle_model_name){

        logger.info("Started registering customer for parking lot service");
        boolean status = false;

        try{
            Customer customer = new Customer(customer_id, customer_name, new Vehicle(vehicle_num, owner_name, color, vehicle_model_name));
            parkingDAO.saveCustomer(customer);
            status = customerService.indexCustomerDetails(customer);
            logger.info("Registering customer for parking lot service completed");
        }
        catch(JDBCConnectionException exception){
            logger.error(GoJekAppErrorList.DATABASE_DOWN,exception);
            throw new PlatformException(GoJekAppErrorList.DATABASE_DOWN, exception);
        }
        catch(PlatformException platformexception){
            logger.error(platformexception);
            throw new RetriableException(platformexception.getMessage(), platformexception);
        }
        catch (JDBCException exception){
            logger.error(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA,exception);
            throw exception;
        }
        catch (Exception exception){
            logger.info(exception.getMessage(),exception);
        }

        return status;
    }

    @RequestMapping(value = "/getParkingDetails", produces = {"application/json"},method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity getParkingDetails(
            @RequestParam(name="customer_id") Integer customer_id,
            @RequestParam Double max_distance,
            @RequestParam Double lon,
            @RequestParam Double lat){

        logger.info("Started processing parking lot service details for a customer");
        Map<String, ElasticVacantParkingLotsResponse> response = null;
        try{
            response = customerService.serveParking(customer_id, lat, lon, max_distance);
            logger.info("Processing parking lot service details for a customer completed");
            return new ResponseEntity(response, HttpStatus.OK);
        }
        catch(JDBCConnectionException exception){
            logger.error(GoJekAppErrorList.DATABASE_DOWN,exception);
            throw new PlatformException(GoJekAppErrorList.DATABASE_DOWN, exception);
        }
        catch(PlatformException platformexception){
            logger.error(platformexception);
            throw new RetriableException(platformexception.getMessage(), platformexception);
        }
        catch (JDBCException exception){
            logger.error(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA,exception);
            throw exception;
        }
        catch (Exception exception){
            logger.info(exception.getMessage(),exception);
        }
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/bookParking", produces = {"application/json"},method = RequestMethod.POST)
    @ResponseBody
    public boolean bookParking(
            @RequestParam(name="customer_id") Integer customer_id,
            @RequestParam Integer licence_id,
            @RequestParam String class_opted
    ){

        logger.info("Started booking parking lot for a customer");
        boolean status = false;

        try{
            CustomerParkingLotRelation customerParkingLotRelation = new CustomerParkingLotRelation(customer_id, licence_id, class_opted);
            if(customerService.bookParkingLot(licence_id, customer_id, class_opted)) {
                parkingDAO.saveCustomerParkingLotRelation(customerParkingLotRelation);
                logger.info("Booking parking lot for a customer completed");
                return true;
            }
        }
        catch(JDBCConnectionException exception){
            logger.error(GoJekAppErrorList.DATABASE_DOWN,exception);
            throw new PlatformException(GoJekAppErrorList.DATABASE_DOWN, exception);
        }
        catch(PlatformException platformexception){
            logger.error(platformexception);
            throw new RetriableException(platformexception.getMessage(), platformexception);
        }
        catch (ElasticIndexException | ElasticSearchException elasticexception){
            logger.info(elasticexception.getMessage(),elasticexception);
            throw elasticexception;
        }
        catch (JDBCException exception){
            logger.error(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA,exception);
            throw exception;
        }
        catch (Exception exception){
            logger.info(exception.getMessage(),exception);
        }

        return status;
    }

    @RequestMapping(value = "/releaseParking", produces = {"application/json"},method = RequestMethod.POST)
    @ResponseBody
    public boolean releaseParking(
            @RequestParam(name="customer_id") Integer customer_id,
            @RequestParam Integer licence_id
    ){

        logger.info("Started releasing parking lot for a customer");
        try{
            customerService.releaseParkingLot(licence_id,customer_id);
            logger.info("Releasing parking lot for a customer completed");
            return true;
        }
        catch(JDBCConnectionException exception){
            logger.error(GoJekAppErrorList.DATABASE_DOWN,exception);
            throw new PlatformException(GoJekAppErrorList.DATABASE_DOWN, exception);
        }
        catch(PlatformException platformexception){
            logger.error(platformexception);
            throw new RetriableException(platformexception.getMessage(), platformexception);
        }
        catch (JDBCException exception){
            logger.error(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA,exception);
            throw exception;
        }
        catch (Exception exception){
            logger.info(exception.getMessage(),exception);
        }

        return true;

    }

    @RequestMapping(value = "/createParkingLot", produces = {"application/json"},method = RequestMethod.POST)
    @ResponseBody
    public boolean createParkingLot(
            @RequestParam Integer licence_id,
            @RequestParam List<String> vehicle_class_name,
            @RequestParam List<Integer> vacancy,
            @RequestParam List<Double> size,
            @RequestParam List<Double> price,
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam String area
    ){


        List<VehicleClass> vehicleClasses = new LinkedList<>();
        Iterator<String> itr_vehicle_class = vehicle_class_name.iterator();
        Iterator<Integer> itr_vacancy = vacancy.iterator();
        Iterator<Double> itr_size = size.iterator();
        Iterator<Double> itr_price = price.iterator();

        while(itr_vacancy.hasNext() && itr_vehicle_class.hasNext() && itr_size.hasNext() && itr_price.hasNext()){
            VehicleClass vehicleClass = new VehicleClass();
            vehicleClass.setParking_class_name(itr_vehicle_class.next());
            vehicleClass.setVacancy(itr_vacancy.next());
            vehicleClass.setPrice(itr_price.next());
            vehicleClass.setSize(itr_size.next());
            vehicleClasses.add(vehicleClass);
        }

        try{
            customerService.indexParkingLotDetails(licence_id, lat, lon, area);
            ParkingLot parkingLot = new ParkingLot(licence_id, area, new GeoPoint(lat,lon), vehicleClasses);
            parkingDAO.saveParkingLot(parkingLot);
        }
        catch(JDBCConnectionException exception){
            logger.error(GoJekAppErrorList.DATABASE_DOWN,exception);
            throw new PlatformException(GoJekAppErrorList.DATABASE_DOWN, exception);
        }
        catch(PlatformException platformexception){
          logger.error(platformexception);
          throw new RetriableException(platformexception.getMessage(), platformexception);
        }
        catch (JDBCException exception){
            logger.error(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA,exception);
            throw exception;
        }
        catch (Exception exception){
            logger.info(exception.getMessage(),exception);
        }

        return true;
    }
}
