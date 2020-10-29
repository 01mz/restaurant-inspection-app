package ca.cmpt276.restaurantinspector.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Restaurant {
    private final String TRACKING_NUMBER;

    private final String NAME;
    private final String ADDRESS;
    private final String CITY;
    private final String FAC_TYPE;  // facility type (should be "Restaurant" for now)
    private final double LATITUDE;

    private final double LONGITUDE;
    private final List<Inspection> inspectionList = new ArrayList<>();

    protected Restaurant(String TRACKING_NUMBER, String NAME,
                      String ADDRESS, String CITY, String FAC_TYPE,
                      double LATITUDE, double LONGITUDE) {
        this.TRACKING_NUMBER = TRACKING_NUMBER;
        this.NAME = NAME;
        this.ADDRESS = ADDRESS;
        this.CITY = CITY;
        this.FAC_TYPE = FAC_TYPE;
        this.LATITUDE = LATITUDE;
        this.LONGITUDE = LONGITUDE;
    }

    public Iterable<Inspection> getRestaurantList() {
        return () -> Collections.unmodifiableCollection(inspectionList).iterator();
    }

    public String getTRACKING_NUMBER() {
        return TRACKING_NUMBER;
    }

    public String getNAME() {
        return NAME;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public double getLATITUDE() {
        return LATITUDE;
    }

    public double getLONGITUDE() {
        return LONGITUDE;
    }

    protected void addInspection(Inspection inspection){
        inspectionList.add(inspection);
    }
}
