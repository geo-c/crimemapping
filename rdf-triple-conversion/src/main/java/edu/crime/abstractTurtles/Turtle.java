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

    /**
     * Turtle definition with a maximum column length
     * */
    protected Turtle(int length){
        this.VALID_LENGTH = length;
    }

    /**Return the valid length
     *
     * */
    public int getValidLength() {
        return VALID_LENGTH;
    }

    /**
     * Verify if a number is valid with the maximum column length
     * */
    public boolean isValidLength(int length) {
        return length == VALID_LENGTH;
    }

    /**
     * Evaluate if each entry has the maximum valid columns number
     * */
    public boolean evaluateRow(String[] rowEntry) throws RDFFormatException {
        boolean isValidLength = isValidLength(rowEntry.length);
        if(!isValidLength){
            throw new RDFFormatException("Not valid length, should contain " + this.getValidLength() + " values separated by commas");
        }
        return isValidLength;
    }

    /**
     * Return the Syntaxes identifiers
     * */
    protected abstract List<String> getIdentifiers();

    /**
     * Return the hash map that contains the appropiate syntaxes , the key is the syntax's identifier
     * */
    protected abstract Map<String,String> getSyntaxis();

    /**
     * Create a complete turtle given the column identifier and the column value
     * */
    protected abstract String createTurtleDefinition(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException;

    /**
     * Create a piece of the turtle given the column identifier and the column value
     * */
    public abstract String createTurtleString(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException;

    /**
     * Transform each entry into a hash map with column identifier as a kay an the entry column as the value
     * */
    public abstract Map<String,String> extractRowData(String[] rowEntry) throws RDFFormatException;

    /**
     * Create a complete turtle given the row entry column's values
     * */
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
