package com.gojek.parkinglot.model;
import java.util.HashMap;
import java.util.Map;

/*
Various vehicle models and there size
 */
public class VehicleSizeForClass {

    public static final Map<String,String> map = new HashMap<>();
    public static VehicleSizeForClass vehicleSizeForClass = null;

    private VehicleSizeForClass(){}

    public static Map<String,String> getvehicleSizeForClass(){

        if(vehicleSizeForClass == null)
        {
            vehicleSizeForClass = new VehicleSizeForClass();
            map.put("MODEL_PQR","5");
            map.put("MODEL_ABC","10");
            map.put("MODEL_XYZ","25");
            map.put("MODEL_KLM","70");
            map.put("MODEL_FGH","100");
            map.put("MODEL_EFG","150");
            return map;
        }

        return map;
    }
}
