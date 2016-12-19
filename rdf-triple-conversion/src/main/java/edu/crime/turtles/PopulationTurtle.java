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
 * Created by Jeilones on 19/12/2016.
 */
public class PopulationTurtle extends Turtle {

    private List<String> IDENTIFIERS = new ArrayList<>();

    {
        IDENTIFIERS.add("PopulationID");
        IDENTIFIERS.add("PopulationDate");
        IDENTIFIERS.add("PopulationValue");
    }

    private Map<String, String> SYNTAXIS = new HashMap<>();
    {
        SYNTAXIS.put("PopulationID","_:");
        SYNTAXIS.put("PopulationDate","dc:date \"(YEAR)\"^^xsd:gYear;");
        SYNTAXIS.put("PopulationValue","owl:hasValue \"(VALUE)\"^^xsd:integer;");
    }

    public PopulationTurtle() {
        super(3);
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
        String turtle = null;

        if(this.getSyntaxis().get(key) == null) throw new RDFNotDefinedException(key);

        if("PopulationID".equals(key)){
            turtle = createIDTurtle(key, value);
        }else if("PopulationDate".equals(key)){
            turtle = createPopulationDateTriple(key,value);
        }else if("PopulationValue".equals(key)){
            turtle = createPopulationValueTriple(key,value);
        }else {
            turtle = createTriple(key, value);
        }
        return turtle;
    }

    protected String createIDTurtle(String key, String value) {
        String idTurtle = this.getSyntaxis().get(key);
        idTurtle = idTurtle + "p" + value;
        return idTurtle;
    }

    private String createPopulationDateTriple(String key, String value) {
        String dateTurtle = this.getSyntaxis().get(key);
        return dateTurtle.replace("(YEAR)",value);
    }

    private String createPopulationValueTriple(String key, String value) {
        String dateTurtle = this.getSyntaxis().get(key);
        return dateTurtle.replace("(VALUE)",value);
    }

    @Override
    public String createTurtleString(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        return (key.equals("PopulationID")?"":"\t") + createTurtleDefinition(key, value);
    }

    @Override
    public Map<String, String> extractRowData(String[] rowEntry) throws RDFFormatException {
        Map<String,String> dataHashMap = new HashMap<>();
        dataHashMap.put("PopulationID",rowEntry[0]);
        dataHashMap.put("PopulationDate",rowEntry[1]);
        dataHashMap.put("PopulationValue",rowEntry[2]);
        return dataHashMap;
    }
}
