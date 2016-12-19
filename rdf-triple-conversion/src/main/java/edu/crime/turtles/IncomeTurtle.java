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
 * Created by Jeilones on 18/12/2016.
 */
public class IncomeTurtle extends Turtle {

    private List<String> IDENTIFIERS = new ArrayList<>();

    {
        IDENTIFIERS.add("IncomeID");
        IDENTIFIERS.add("IncomeDate");
        IDENTIFIERS.add("IncomeValue");
        IDENTIFIERS.add("NameCurrencyEN");
    }

    private Map<String, String> SYNTAXIS = new HashMap<>();
    {
        SYNTAXIS.put("IncomeID","_:");
        SYNTAXIS.put("IncomeDate","dc:date \"(YEAR)\"^^xsd:gYear;");
        SYNTAXIS.put("IncomeValue","owl:hasValue \"(VALUE)\"^^xsd:integer;");
        SYNTAXIS.put("NameCurrencyEN","gpowl:nameCurrencyEN \"(CURRENCY_NAME)\"^^xsd:string;");
    }

    protected IncomeTurtle() {
        super(4);
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

        if("IncomeID".equals(key)){
            turtle = createIDTurtle(key, value);
        }else if("IncomeDate".equals(key)){
            turtle = createIncomeDate(key,value);
        }else if("IncomeValue".equals(key)){
            turtle = createIncomeValue(key,value);
        }else if("NameCurrencyEN".equals(key)){
            turtle = createIncomeNameCurrencyEN(key,value);
        }else {
            turtle = createTriple(key, value);
        }
        return turtle;
    }

    private String createIncomeDate(String key, String value) {
        String dateTurtle = this.getSyntaxis().get(key);
        return dateTurtle.replace("(YEAR)",value);
    }

    private String createIncomeValue(String key, String value) {
        String dateTurtle = this.getSyntaxis().get(key);
        return dateTurtle.replace("(VALUE)",value);
    }

    private String createIncomeNameCurrencyEN(String key, String value) {
        String dateTurtle = this.getSyntaxis().get(key);
        return dateTurtle.replace("(CURRENCY_NAME)",value);
    }

    protected String createIDTurtle(String key, String value) {
        String idTurtle = this.getSyntaxis().get(key);
        idTurtle = idTurtle + "b" + value;
        return idTurtle;
    }

    @Override
    public String createTurtleString(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        return (key.equals("IncomeID")?"":"\t") + createTurtleDefinition(key, value);
    }

    @Override
    public Map<String, String> extractRowData(String[] rowEntry) throws RDFFormatException {
        Map<String,String> dataHashMap = new HashMap<>();
        dataHashMap.put("IncomeID",rowEntry[0]);
        dataHashMap.put("IncomeDate",rowEntry[1]);
        dataHashMap.put("IncomeValue",rowEntry[2]);
        dataHashMap.put("NameCurrencyEN",rowEntry[3]);
        return dataHashMap;
    }
}
