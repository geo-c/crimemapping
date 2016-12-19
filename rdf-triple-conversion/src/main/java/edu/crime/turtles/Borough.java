package edu.crime.turtles;

import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFNotDefinedException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import edu.crime.abstractTurtles.Turtle;

import java.util.*;

/**
 * Created by Jeilones on 18/12/2016.
 */
public class Borough extends Turtle {

    private List<String> IDENTIFIERS = new ArrayList<>();
    {
        IDENTIFIERS.add("DBPedia");
        IDENTIFIERS.add("BoroughCode");
        IDENTIFIERS.add("Income");
        IDENTIFIERS.add("Population");
    }

    private Map<String, String> SYNTAXIS = new HashMap<>();
    {
        SYNTAXIS.put("DBPedia","dbpedia-page:");
        SYNTAXIS.put("BoroughCode","admingeo:gssCode");
        SYNTAXIS.put("Income","dbpedia:income");
        SYNTAXIS.put("Population","dbpedia:Population");
    }

    private List<String> incomeTurtles =  new ArrayList<>();
    private List<String> populationTurtles = new ArrayList<>();
    private int countIncomeTurtle = -1;
    private int countPopulationTurtle = -1;

    public Borough() {
        super(9);
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

        if(!key.endsWith("_Mean") && !key.endsWith("_Popu") && this.getSyntaxis().get(key) == null){
            throw new RDFNotDefinedException(key);
        }

        if("DBPedia".equals(key)){
            turtle = createIDTurtle(key, value);
        }else if(key.endsWith("_Mean")){
            turtle = createIncomeTurtle(key,value);
        }else if(key.endsWith("_Popu")){
            turtle = createPopulationTurtle(key,value);
        }else {
            turtle = createTriple(key, value);
        }

        return turtle;
    }

    private String createIncomeTurtle(String key, String value) throws RDFTurtleCreatorException {
        IncomeTurtle incomeTurtle = new IncomeTurtle();
        String[] incomeData = new String[4];
        String incomeID = String.valueOf(++this.countIncomeTurtle);
        incomeData[0] = incomeID;
        incomeData[1] = key.split("_")[0];
        incomeData[2] = value;
        incomeData[3] = "pound sterling";


        incomeTurtles.add(incomeTurtle.createTurtleDefinition(incomeData));

        return "_:b" + incomeID;
    }

    private String createPopulationTurtle(String key, String value) throws RDFTurtleCreatorException {
        PopulationTurtle populationTurtle = new PopulationTurtle();
        String[] populationData = new String[3];
        String populationID = String.valueOf(++this.countPopulationTurtle);
        populationData[0] = populationID;
        populationData[1] = key.split("_")[0];
        populationData[2] = value;


        populationTurtles.add(populationTurtle.createTurtleDefinition(populationData));

        return "_:p" + populationID;
    }

    @Override
    public String createTurtleString(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        return (key.equals("DBPedia")||key.endsWith("_Mean")||key.endsWith("_Popu")?"":"\t") + createTurtleDefinition(key, value);
    }

    @Override
    public Map<String, String> extractRowData(String[] rowEntry) throws RDFFormatException {
        HashMap<String,String> dataHashMap = new HashMap<>();

        dataHashMap.put("DBPedia",rowEntry[0]);
        dataHashMap.put("BoroughCode",rowEntry[1]);
        dataHashMap.put("BoroughName",rowEntry[2]);
        dataHashMap.put("2012_Popu",rowEntry[3]);
        dataHashMap.put("2013_Popu",rowEntry[4]);
        dataHashMap.put("2014_Popu",rowEntry[5]);
        dataHashMap.put("2012_Mean",rowEntry[6]);
        dataHashMap.put("2013_Mean",rowEntry[7]);
        dataHashMap.put("2014_Mean",rowEntry[8]);


        return dataHashMap;
    }

    @Override
    public String createTurtleDefinition(String[] rowEntry) throws RDFTurtleCreatorException {
        String turtle = "";
        try {

            evaluateRow(rowEntry);

            Map<String, String> dataHashMap = extractRowData(rowEntry);

            for (int i = 0; i < getIdentifiers().size(); i++) {
                String key = getIdentifiers().get(i);
                if("Income".endsWith(key)){
                    turtle += "\t" + this.getSyntaxis().get(key);
                    for (int j = 2012; j < 2015; j++){
                        String key_mean = j + "_Mean";
                        turtle += " " + createTurtleString(key_mean, dataHashMap.get(key_mean)) + ",";
                    }

                    turtle = turtle.substring(0,turtle.length()-1) + ";";

                }else if("Population".endsWith(key)){
                    turtle += "\t" + this.getSyntaxis().get(key);
                    for (int j = 2012; j < 2015; j++){
                        String key_mean = j + "_Popu";
                        turtle += " " + createTurtleString(key_mean, dataHashMap.get(key_mean)) + ",";
                    }

                    turtle = turtle.substring(0,turtle.length()-1) + ";";

                }else{
                    turtle = turtle + createTurtleString(key, dataHashMap.get(key));
                }

                if (i == getIdentifiers().size()-1){
                    turtle = turtle.substring(0,turtle.length()-1) + ".";
                }

                turtle += "\n";
            }

            turtle += "\n";

            turtle = addIncomeTurtles(turtle);
            turtle = addPopulationTurtles(turtle);
        } catch (RDFFormatException e) {
            throw new RDFTurtleCreatorException(e);
        } catch (RDFNotDefinedException e) {
            throw new RDFTurtleCreatorException(e);
        }

        return turtle;
    }

    private String addIncomeTurtles(String turtle) {
        for (String incomeTurtle :
                this.incomeTurtles) {
            turtle += incomeTurtle;
        }

        this.incomeTurtles.clear();
        return turtle;
    }

    private String addPopulationTurtles(String turtle) {
        for (String populationTurtle :
                this.populationTurtles) {
            turtle += populationTurtle;
        }

        this.populationTurtles.clear();
        return turtle;
    }

}
