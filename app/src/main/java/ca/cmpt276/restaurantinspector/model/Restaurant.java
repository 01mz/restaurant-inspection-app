package ca.cmpt276.restaurantinspector.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Restaurant stores information about a restaurant including its list of inspections.
 * Has Getters for its info including getInspectionList() that returns an iterable of its Inspections
 * The list of inspections can be empty (if there are no inspections associated with the restaurant).
 */
public class Restaurant {
    private final String TRACKING_NUMBER;

    private final String NAME;
    private final String ADDRESS;
    private final String CITY;
    private final String FAC_TYPE;  // facility type (should be "Restaurant" for now)
    private final double LATITUDE;

    private final double LONGITUDE;
    private final List<Inspection> inspectionList = new ArrayList<>();

    private int numViolationsWithinLastYear = -1;

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

    public List<Inspection> getInspectionList() {
        return Collections.unmodifiableList(inspectionList);
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

    public Inspection getInspection(int index){
        return inspectionList.get(index);
    }

    public boolean hasInspection(){
        return inspectionList.size() != 0;
    }

    // Make sure Restaurant has at least one Inspection: call hasInspection() before use
    public Inspection getMostRecentInspection(){
        return inspectionList.get(0);
    }

    protected void addInspection(Inspection inspection){
        inspectionList.add(inspection);
    }

    // Sorted by most recent date
    protected void sortInspectionsByDate() {
        // Compare the inspection dates in "reverse" so a more recent date is treated as "lesser"
        inspectionList.sort((i1, i2) -> i2.getINSPECTION_DATE().compareTo(i1.getINSPECTION_DATE()));
    }

    protected void calculateNumViolationsWithinLastYear() {
        numViolationsWithinLastYear = 0;
        for (Inspection inspection : inspectionList) {
            if(inspection.getINSPECTION_DATE().isWithinLastYear()){
                numViolationsWithinLastYear += inspection.getNUM_CRITICAL();
            }
        }
    }

    public int getNumViolationsWithinLastYear() {
        if(numViolationsWithinLastYear == -1) {
            calculateNumViolationsWithinLastYear();
        }
        return numViolationsWithinLastYear;
    }

    public int getNumInspections() {
        return inspectionList.size();
    }
}
