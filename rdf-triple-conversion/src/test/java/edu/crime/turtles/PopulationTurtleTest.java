package edu.crime.turtles;

import edu.crime.RDFConverter;
import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFNotDefinedException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jeilones on 18/12/2016.
 */
public class PopulationTurtleTest {

    private PopulationTurtle populationTurtle;
    private RDFConverter<PopulationTurtle> rdfConverter;

    @Before
    public void createPopulationTurtle(){
        populationTurtle = new PopulationTurtle();
        rdfConverter = new RDFConverter<>(PopulationTurtle.class);
    }
    @Test
    public void givenStringSeparatedByCommasWithLastEmptyComma_ThenIsValid() throws RDFFormatException, RDFTurtleCreatorException {
        String rowEntry = "1,2012,234846";
        assertTrue(populationTurtle.evaluateRow(rdfConverter.splitRowEntry(rowEntry)));
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnValuesInAHashMap() throws RDFFormatException {
        String rowEntry = "0,2012,234846";

        Map<String,String> dataHashMapExpected = new HashMap<>();
        dataHashMapExpected.put("PopulationID","0");
        dataHashMapExpected.put("PopulationDate","2012");
        dataHashMapExpected.put("PopulationValue","234846");

        assertEquals(dataHashMapExpected, populationTurtle.extractRowData(rdfConverter.splitRowEntry(rowEntry)));

    }

    @Test
    public void givenIncomeIDEntry_CreateCrimeIDTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<>();
        dataHashMap.put("PopulationID","0");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("_:p0", populationTurtle.createTurtleDefinition(dataEntry));
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
                assertEquals(turtleHashMap.get(dataEntry.getKey()), populationTurtle.createTurtleDefinition(dataEntry));
            } catch (RDFTurtleCreatorException rdfTurtleCreator) {
                rdfTurtleCreator.printStackTrace();
            } catch (RDFNotDefinedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnCompleteTurtle() throws RDFTurtleCreatorException {
        String rowEntry = "1,2012,234846";

        StringBuffer turtleExpected = new StringBuffer();
        turtleExpected.append("_:p1\n");
        turtleExpected.append("\tdc:date \"2012\"^^xsd:gYear;\n");
        turtleExpected.append("\towl:hasValue \"234846\"^^xsd:integer.\n");
        turtleExpected.append("\n");

        String turtleResult = populationTurtle.createTurtleDefinition(rdfConverter.splitRowEntry(rowEntry));
        assertEquals(turtleExpected.toString(), turtleResult);
        System.out.println(turtleResult);
    }
}