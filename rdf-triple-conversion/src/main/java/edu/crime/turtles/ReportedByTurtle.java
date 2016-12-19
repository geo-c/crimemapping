package edu.crime.turtles;

import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFNotDefinedException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import edu.crime.abstractTurtles.Turtle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeilones on 16/12/2016.
 */
public class ReportedByTurtle extends Turtle {

    private List<String> IDENTIFIERS = new ArrayList<String>();
    private String timeTurtle;

    {
        IDENTIFIERS.add("ReportedByID");
        IDENTIFIERS.add("ReportedBy");
    }

    private Map<String, String> SYNTAXIS = new HashMap<String, String>();
    {
        SYNTAXIS.put("ReportedByID","_:");
        SYNTAXIS.put("ReportedBy","foaf:name");
    }

    protected ReportedByTurtle() {
        super(1);
    }

    @Override
    protected List<String> getIdentifiers() {
        return IDENTIFIERS;
    }

    @Override
    protected Map<String, String> getSyntaxis() {
        return SYNTAXIS;
    }

    @Override
    protected String createTurtleDefinition(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        String turtle;

        if(this.getSyntaxis().get(key) == null) throw new RDFNotDefinedException(key);

        if("ReportedByID".equals(key)){
            turtle = createIDTurtle(key, value);
        }else{
            turtle = createTriple(key, value);
        }
        return turtle;
    }

    @Override
    protected String createIDTurtle(String key, String value) {
        String idTurtle = this.getSyntaxis().get(key);
        String[] reportedBy = value.trim().split(" ");
        String reporterID = "";
        for (String reporterSplitted : reportedBy) {
            reporterID += reporterSplitted.substring(0,1).toUpperCase();
        }
        idTurtle = idTurtle + "reporter" + reporterID;
        return idTurtle;
    }

    @Override
    public String createTurtleString(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        return (key.equals("ReportedByID")?"":"\t") + createTurtleDefinition(key, value);
    }

    @Override
    public Map<String, String> extractRowData(String[] rowEntry) throws RDFFormatException {
        HashMap<String,String> dataHashMap = new HashMap<>();
        dataHashMap.put("ReportedByID",rowEntry[0]);
        dataHashMap.put("ReportedBy",rowEntry[0]);
        return dataHashMap;
    }
}
