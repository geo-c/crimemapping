package edu.crime.turtles;

import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFNotDefinedException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import edu.crime.abstractTurtles.Turtle;

import java.util.*;

/**
 * Created by Jeilones on 16/12/2016.
 */
public class TimeTurtle extends Turtle {

    private List<String> IDENTIFIERS = new ArrayList<String>();
    private String timeTurtle;

    {
        IDENTIFIERS.add("TimeID");
        IDENTIFIERS.add("Month");
        IDENTIFIERS.add("Year");
    }

    private Map<String, String> SYNTAXIS = new HashMap<String, String>();
    {
        SYNTAXIS.put("TimeID","_:");
        SYNTAXIS.put("Month","time:month \"--(MONTH)\"^^xsd:gMonth;");
        SYNTAXIS.put("Year","time:year \"(YEAR)\"^^xsd:gYear;");
    }

    protected TimeTurtle() {
        super(2);
    }

    @Override
    public List<String> getIdentifiers() {
        return IDENTIFIERS;
    }

    @Override
    public Map<String, String> getSyntaxis() {
        return SYNTAXIS;
    }

    @Override
    protected String createTurtleDefinition(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        String turtle;

        if(this.getSyntaxis().get(key) == null) throw new RDFNotDefinedException(key);

        if("TimeID".equals(key)){
            turtle = createIDTurtle(key, value);
        }else{
            turtle = createTimeTurtle(key, value);
        }
        return turtle;
    }

    private String createTimeTurtle(String key, String value) {
        String dateTurtle = this.getSyntaxis().get(key);
        dateTurtle = dateTurtle.replace("(MONTH)", value)
                    .replace("(YEAR)",value);
        return dateTurtle;
    }

    @Override
    public String createTurtleString(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        return (key.equals("TimeID")?"":"\t") + createTurtleDefinition(key, value);
    }

    @Override
    public Map<String, String> extractRowData(String[] rowEntry) throws RDFFormatException {
        HashMap<String,String> dataHashMap = new HashMap<>();
        dataHashMap.put("TimeID","t" + rowEntry[0] + rowEntry[1]);
        dataHashMap.put("Month",rowEntry[0]);
        dataHashMap.put("Year",rowEntry[1]);
        return dataHashMap;
    }
}
