package ca.cmpt276.restaurantinspector.model;

public class Violation {
    private final int CODE;

    private final String DESCRIPTION;
    private final boolean CRITICAL;  // Critical or not critical
    private final boolean REPEAT;    // Repeat or not repeat

    private final String TYPE;

    protected Violation(int CODE, String DESCRIPTION, boolean CRITICAL, boolean REPEAT) {
        this.CODE = CODE;
        this.DESCRIPTION = DESCRIPTION;
        this.CRITICAL = CRITICAL;
        this.REPEAT = REPEAT;
        this.TYPE = getTypeOfViolation(DESCRIPTION);
    }

    // Get type/nature of violation by finding certain words in the description
    // Maybe better way: maybe type is classified by violation CODE?
    private String getTypeOfViolation(String description) {
        String [] types = {"food", "pest", "equipment"};
        String type = "other";

        for(String t : types){
            if(description.toLowerCase().contains(t) ){
                type = t;
            }
        }

        return type;
    }

    public int getCODE() {
        return CODE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public boolean isCRITICAL() {
        return CRITICAL;
    }

    public boolean isREPEAT() {
        return REPEAT;
    }

    public String getTYPE() {
        return TYPE;
    }
}
