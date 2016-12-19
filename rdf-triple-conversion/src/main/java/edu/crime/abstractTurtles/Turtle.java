package edu.crime.abstractTurtles;

import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFNotDefinedException;
import edu.crime.exceptions.RDFTurtleCreatorException;

import java.util.List;
import java.util.Map;

/**
 * Created by Jeilones on 10/12/2016.
 */
public abstract class Turtle {

    private int VALID_LENGTH;

    protected Turtle(int length){
        this.VALID_LENGTH = length;
    }

    public int getValidLength() {
        return VALID_LENGTH;
    }

    public boolean isValidLength(int length) {
        return length == VALID_LENGTH;
    }

    public boolean evaluateRow(String[] rowEntry) throws RDFFormatException {
        boolean isValidLength = isValidLength(rowEntry.length);
        if(!isValidLength){
            throw new RDFFormatException("Not valid length, should contain " + this.getValidLength() + " values separated by commas");
        }
        return isValidLength;
    }
    protected abstract List<String> getIdentifiers();
    protected abstract Map<String,String> getSyntaxis();

    protected abstract String createTurtleDefinition(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException;
    public abstract String createTurtleString(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException;

    public abstract Map<String,String> extractRowData(String[] rowEntry) throws RDFFormatException;


    public String createTurtleDefinition(String[] rowEntry) throws RDFTurtleCreatorException {
        String turtle = "";
        try {

            evaluateRow(rowEntry);

            Map<String, String> dataHashMap = extractRowData(rowEntry);

            for (int i = 0; i < getIdentifiers().size(); i++) {
                String key = getIdentifiers().get(i);
                turtle = turtle + createTurtleString(key, dataHashMap.get(key));

                if (i == getIdentifiers().size()-1){
                    turtle = turtle.substring(0,turtle.length()-1) + ".";
                }

                turtle += "\n";
            }

            turtle += "\n";
        } catch (RDFFormatException e) {
            throw new RDFTurtleCreatorException(e);
        } catch (RDFNotDefinedException e) {
            throw new RDFTurtleCreatorException(e);
        }

        return turtle;
    }

    public String createTurtleDefinition(Map.Entry<String, String> dataEntry) throws RDFTurtleCreatorException, RDFNotDefinedException {
        return createTurtleDefinition(dataEntry.getKey(), dataEntry.getValue());
    }

    protected String createIDTurtle(String key, String value) {
        String idTurtle = this.getSyntaxis().get(key);
        idTurtle = idTurtle + value;
        return idTurtle;
    }

    protected String createTriple(String key, String value) {
        String turtle;
        turtle = this.getSyntaxis().get(key);
        turtle = turtle + " \"" + value + "\";";
        return turtle;
    }
}
