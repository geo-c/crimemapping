package edu.crime.turtles;

import edu.crime.RDFConverter;
import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFNotDefinedException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Jeilones on 18/12/2016.
 */
public class IncomeTurtleTest {

    private IncomeTurtle incomeTurtle;
    private RDFConverter<IncomeTurtle> rdfConverter;

    @Before
    public void createIncomeTurtle(){
        incomeTurtle = new IncomeTurtle();
        rdfConverter = new RDFConverter<>(IncomeTurtle.class);
    }
    @Test
    public void givenStringSeparatedByCommasWithLastEmptyComma_ThenIsValid() throws RDFFormatException, RDFTurtleCreatorException {
        String rowEntry = "1,2012,78900,pound sterling";
        assertTrue(incomeTurtle.evaluateRow(rdfConverter.splitRowEntry(rowEntry)));
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnValuesInAHashMap() throws RDFFormatException {
        String rowEntry = "0,2012,78900,pound sterling";

        Map<String,String> dataHashMapExpected = new HashMap<>();
        dataHashMapExpected.put("IncomeID","0");
        dataHashMapExpected.put("IncomeDate","2012");
        dataHashMapExpected.put("IncomeValue","78900");
        dataHashMapExpected.put("NameCurrencyEN","pound sterling");

        assertEquals(dataHashMapExpected, incomeTurtle.extractRowData(rdfConverter.splitRowEntry(rowEntry)));

    }

    @Test
    public void givenIncomeIDEntry_CreateCrimeIDTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<>();
        dataHashMap.put("IncomeID","0");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("_:b0", incomeTurtle.createTurtleDefinition(dataEntry));
        }
    }

    @Test
    public void givenAllEntrySet_CreateRespectivelyTurtle() {
        Map<String,String> dataHashMap = new HashMap<String, String>();

        dataHashMap.put("IncomeID","0");
        dataHashMap.put("IncomeDate","2012");
        dataHashMap.put("IncomeValue","78900");
        dataHashMap.put("NameCurrencyEN","pound sterling");

        Map<String,String> turtleHashMap = new HashMap<String, String>();
        turtleHashMap.put("IncomeID","_:b0");
        turtleHashMap.put("IncomeDate","dc:date \"2012\"^^xsd:gYear;");
        turtleHashMap.put("IncomeValue","owl:hasValue \"78900\"^^xsd:integer;");
        turtleHashMap.put("NameCurrencyEN","gpowl:nameCurrencyEN \"pound sterling\"^^xsd:string;");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            try {
                assertEquals(turtleHashMap.get(dataEntry.getKey()), incomeTurtle.createTurtleDefinition(dataEntry));
            } catch (RDFTurtleCreatorException rdfTurtleCreator) {
                rdfTurtleCreator.printStackTrace();
            } catch (RDFNotDefinedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnCompleteTurtle() throws RDFTurtleCreatorException {
        String rowEntry = "1,2012,78900,pound sterling";

        StringBuffer turtleExpected = new StringBuffer();
        turtleExpected.append("_:b1\n");
        turtleExpected.append("\tdc:date \"2012\"^^xsd:gYear;\n");
        turtleExpected.append("\towl:hasValue \"78900\"^^xsd:integer;\n");
        turtleExpected.append("\tgpowl:nameCurrencyEN \"pound sterling\"^^xsd:string.\n");
        turtleExpected.append("\n");

        String turtleResult = incomeTurtle.createTurtleDefinition(rdfConverter.splitRowEntry(rowEntry));
        assertEquals(turtleExpected.toString(), turtleResult);
        System.out.println(turtleResult);
    }
}