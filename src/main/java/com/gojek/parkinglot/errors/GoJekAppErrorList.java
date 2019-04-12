package com.gojek.parkinglot.errors;

public class GoJekAppErrorList extends ErrorCode {

    //Platform Error codes 1001-2000
    public static GoJekAppErrorList ELATIC_PLATFORM_DOWN = new GoJekAppErrorList("1001", "Elastic cluster is down, found while processing in class {} and function {}");
    public static GoJekAppErrorList DATABASE_DOWN = new GoJekAppErrorList("1002", "Postgresql database is down, found while processing in class {} and function {}");

    //Service errors codes 2001-3000

    //Unknown errors codes 3001-4000
    public static GoJekAppErrorList ERROR_WHILE_SAVING_DATA = new GoJekAppErrorList("3001", "Error while saving data, found while processing in class {} and function {} ");
    public static GoJekAppErrorList ELATIC_INDEXING_ERROR = new GoJekAppErrorList("3002", "Error while indexing, in index {} and type {} for payload {}");
    public static GoJekAppErrorList ELASTIC_SEARCHING_ERROR = new GoJekAppErrorList("3003", "Error while searching, in index {} and type {} for payload {}");


    public GoJekAppErrorList(String code, String message){
        super(code, message);
    }

    public void setMessage(String message){
        super.setMessage(message);
    }

}
