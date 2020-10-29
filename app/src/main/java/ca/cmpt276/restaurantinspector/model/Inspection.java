package ca.cmpt276.restaurantinspector.model;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.cmpt276.restaurantinspector.BuildConfig;

public class Inspection {
    private final String TRACKING_NUMBER;   // restaurant tracking number
    private final String INSPECTION_DATE;
    private final String INSPECT_TYPE;

    private final int NUM_CRITICAL;
    private final int NUM_NONCRITICAL;

    private final String HAZARD_RATING;

    private final List<Violation> violationList = new ArrayList<>();

    protected Inspection(String TRACKING_NUMBER, String INSPECTION_DATE, String INSPECT_TYPE,
                         int NUM_CRITICAL, int NUM_NONCRITICAL, String HAZARD_RATING,
                         String VIOLATION_LUMP) {
        this.TRACKING_NUMBER = TRACKING_NUMBER;
        this.INSPECTION_DATE = INSPECTION_DATE;
        this.INSPECT_TYPE = INSPECT_TYPE;
        this.NUM_CRITICAL = NUM_CRITICAL;
        this.NUM_NONCRITICAL = NUM_NONCRITICAL;
        this.HAZARD_RATING = HAZARD_RATING;

        processViolationLump(VIOLATION_LUMP);   // add to violationList
    }

    private void processViolationLump(String violation_lump) {
        String[] violations = violation_lump.split("\\|");

        CSVParser parser = new CSVParser();
        for(String violation : violations){
            try {
                String[] fields = parser.parseLine(violation);
                final int CODE = Integer.parseInt(fields[0]);
                final boolean CRITICAL = isCritical(fields[1]);
                final String DESCRIPTION = fields[2];
                final boolean REPEAT = isRepeat(fields[3]);

                violationList.add(new Violation(CODE, DESCRIPTION, CRITICAL, REPEAT));
            } catch (IOException ignored) {

            }
        }
    }

    private boolean isCritical(String field) {
        if (field.trim().equalsIgnoreCase("Critical")) {
            return true;
        }
        if (BuildConfig.DEBUG && !field.trim().equalsIgnoreCase("Not Critical")) {
            throw new AssertionError(String.format("expected: \"Not Critical\", but got %s", field));
        }
        return false;
    }

    private boolean isRepeat(String field) {
        if (field.trim().equalsIgnoreCase("Repeat")) {
            return true;
        }
        if (BuildConfig.DEBUG && !field.trim().equalsIgnoreCase("Not Repeat")) {
            throw new AssertionError(String.format("expected: \"Not Repeat\", but got %s", field));
        }
        return false;
    }

    protected String getTRACKING_NUMBER() {
        return TRACKING_NUMBER;
    }

    public String getINSPECTION_DATE() {
        return INSPECTION_DATE;
    }

    public String getINSPECT_TYPE() {
        return INSPECT_TYPE;
    }

    public int getNUM_CRITICAL() {
        return NUM_CRITICAL;
    }

    public int getNUM_NONCRITICAL() {
        return NUM_NONCRITICAL;
    }

    public String getHAZARD_RATING() {
        return HAZARD_RATING;
    }

    public Iterable<Violation> getViolationList() {
        return () -> Collections.unmodifiableCollection(violationList).iterator();
    }
}
