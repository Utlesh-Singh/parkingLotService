package com.gojek.parkinglot.entity.database;

import com.gojek.parkinglot.model.GeoPoint;
import javax.persistence.*;
import java.util.*;

@Entity
public class ParkingLot {
    @Id
    private Integer licenece_id ;
    @Embedded
    private final GeoPoint geopint = new GeoPoint();
    @ElementCollection(fetch = FetchType.EAGER)
    private List<VehicleClass> vehicleClass = new LinkedList<>();
    private String area;

   public ParkingLot() {
    }

    public void setLicenece_id(Integer licenece_id) {
        this.licenece_id = licenece_id;
    }


    public void setArea(String area) {
        this.area = area;
    }

    public ParkingLot(Integer licenece_id, String area, GeoPoint geoPoint, List<VehicleClass> vehicleClass){
        this.licenece_id = licenece_id;
        this.area = area;
        this.geopint.reset(geoPoint.lon(),geoPoint.lat());
        this.vehicleClass.addAll(vehicleClass);
    }

    public List<VehicleClass> getVehicleClass(){
        return this.vehicleClass;
    }

    public void setVehicleClass(List<VehicleClass> vehicleClass){
        this.vehicleClass = vehicleClass;
    }

    public boolean equals(Object object){
        if(this == object)
            return true;
        return object instanceof ParkingLot && this.licenece_id == ((ParkingLot)object).licenece_id;
    }

    public int hashCode(){
       return this.licenece_id;
    }

    public boolean getParking(String class_name){
        boolean flag = false;

        Iterator<VehicleClass> iter = this.getVehicleClass().iterator();
        while(iter.hasNext()){
            VehicleClass vehicleClass = iter.next();
            if(vehicleClass.getParking_class_name().equalsIgnoreCase(class_name)){
                if(vehicleClass.getVacancy()-1 >= 0)
                  vehicleClass.setVacancy(vehicleClass.getVacancy()-1);
                else
                    break;
                flag = true;
                break;
            }
        }
        return flag;
    }

    public void releaseParking(String class_name){
        Iterator<VehicleClass> iter = this.getVehicleClass().iterator();
        while(iter.hasNext()){
            VehicleClass vehicleClass = iter.next();
            if(vehicleClass.getParking_class_name().equalsIgnoreCase(class_name)){
                vehicleClass.setVacancy(vehicleClass.getVacancy() + 1);
                break;
            }
        }
    }

    public int getVacancyForClass(String class_name){
        int count = 0;
        Iterator<VehicleClass> iter = this.getVehicleClass().iterator();
        while(iter.hasNext()){
            VehicleClass vehicleClass = iter.next();
            if(vehicleClass.getParking_class_name().equalsIgnoreCase(class_name)){
                count = vehicleClass.getVacancy();
                break;
            }
        }
        return count;
    }
}
