package com.gojek.parkinglot.entity.database;

import com.gojek.parkinglot.config.LogService;
import com.gojek.parkinglot.config.Logger;
import com.gojek.parkinglot.errors.GoJekAppErrorList;
import com.gojek.parkinglot.exceptions.PlatformException;
import com.gojek.parkinglot.service.parkinglotservices.CustomerService;
import org.hibernate.JDBCException;
import org.hibernate.exception.JDBCConnectionException;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ParkingServiceDAO {

    @Autowired
    private ParkingLotDAO parkingLotDAO;

    @Autowired
    private CustomerDAO customerDAO;

    @Autowired
    private CustomerParkingLotRelationDAO customerParkingLotRelationDAO;

    @Autowired
    private LogService logService;

    private Logger logger;

    @PostConstruct
    public void init() {
        logger = logService.getLogger(CustomerService.class);
    }

    public void saveCustomer(Customer customer){
        try{
            customerDAO.save(customer);
        }
        catch(JDBCConnectionException exception){
            GoJekAppErrorList.DATABASE_DOWN.setMessage(MessageFormatter.arrayFormat(GoJekAppErrorList.DATABASE_DOWN.getMessage(), new Object[]{this.getClass().toString(),"saveCustomer", customer.toString()}).getMessage());
            logger.error(GoJekAppErrorList.DATABASE_DOWN,exception);
            throw new PlatformException(GoJekAppErrorList.DATABASE_DOWN, exception);
        }
        catch (JDBCException exception){
            GoJekAppErrorList.ERROR_WHILE_SAVING_DATA.setMessage(MessageFormatter.arrayFormat(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA.getMessage(), new Object[]{this.getClass().toString(),"saveCustomer", customer.toString()}).getMessage());
            logger.error(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA,exception);
            throw exception;
        }
    }

    public void saveParkingLot(ParkingLot parkingLot){
        try{
            parkingLotDAO.save(parkingLot);
        }
        catch(JDBCConnectionException exception){
            GoJekAppErrorList.DATABASE_DOWN.setMessage(MessageFormatter.arrayFormat(GoJekAppErrorList.DATABASE_DOWN.getMessage(), new Object[]{this.getClass().toString(),"saveParkingLot", parkingLot.toString()}).getMessage());
            logger.error(GoJekAppErrorList.DATABASE_DOWN,exception);
            throw new PlatformException(GoJekAppErrorList.DATABASE_DOWN, exception);
        }
        catch (JDBCException exception){
            GoJekAppErrorList.ERROR_WHILE_SAVING_DATA.setMessage(MessageFormatter.arrayFormat(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA.getMessage(), new Object[]{this.getClass().toString(),"saveParkingLot", parkingLot.toString()}).getMessage());
            logger.error(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA,exception);
            throw exception;
        }
    }

    public void saveCustomerParkingLotRelation(CustomerParkingLotRelation customerParkingLotRelation){
        try{
            customerParkingLotRelationDAO.save(customerParkingLotRelation);
        }
        catch(JDBCConnectionException exception){
            GoJekAppErrorList.DATABASE_DOWN.setMessage(MessageFormatter.arrayFormat(GoJekAppErrorList.DATABASE_DOWN.getMessage(), new Object[]{this.getClass().toString(),"saveCustomerParkingLotRelation", customerParkingLotRelation.toString()}).getMessage());
            logger.error(GoJekAppErrorList.DATABASE_DOWN,exception);
            throw new PlatformException(GoJekAppErrorList.DATABASE_DOWN, exception);
        }
        catch (JDBCException exception){
            GoJekAppErrorList.ERROR_WHILE_SAVING_DATA.setMessage(MessageFormatter.arrayFormat(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA.getMessage(), new Object[]{this.getClass().toString(),"saveCustomerParkingLotRelation", customerParkingLotRelation.toString()}).getMessage());
            logger.error(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA,exception);
            throw exception;
        }
    }

    public void deleteCustomerParkingLotRelation(CustomerParkingLotRelation customerParkingLotRelation){
        try{
            customerParkingLotRelationDAO.delete(customerParkingLotRelation);
        }
        catch(JDBCConnectionException exception){
            GoJekAppErrorList.DATABASE_DOWN.setMessage(MessageFormatter.arrayFormat(GoJekAppErrorList.DATABASE_DOWN.getMessage(), new Object[]{this.getClass().toString(),"deleteCustomerParkingLotRelation", customerParkingLotRelation.toString()}).getMessage());
            logger.error(GoJekAppErrorList.DATABASE_DOWN,exception);
            throw new PlatformException(GoJekAppErrorList.DATABASE_DOWN, exception);
        }
        catch (JDBCException exception){
            GoJekAppErrorList.ERROR_WHILE_SAVING_DATA.setMessage(MessageFormatter.arrayFormat(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA.getMessage(), new Object[]{this.getClass().toString(),"deleteCustomerParkingLotRelation", customerParkingLotRelation.toString()}).getMessage());
            logger.error(GoJekAppErrorList.ERROR_WHILE_SAVING_DATA,exception);
            throw exception;
        }

    }
}
