package edu.crime.turtles;

import edu.crime.RDFConverter;
import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Jeilones on 18/12/2016.
 */
public class BoroughTest {

    private Borough borough;
    private RDFConverter<Borough> rdfConverter;

    @Before
    public void createBorough(){
        borough = new Borough();
        rdfConverter = new RDFConverter<>(Borough.class);
    }

    @Test
    public void givenEmptyStringsSeparatedByCommas8_ThenIsValid() throws RDFFormatException, RDFTurtleCreatorException {
        String rowEntry = ",,,,,,,,";
        assertTrue(borough.evaluateRow(rdfConverter.splitRowEntry(rowEntry)));
    }

    @Test
    public void givenStringSeparatedByCommasWithLastEmptyComma_ThenIsValid() throws RDFFormatException, RDFTurtleCreatorException {
        String rowEntry = "City_of_London,E09000001,City of London,7604,7648,8072,117000,131000,151000";

        assertTrue(borough.evaluateRow(rdfConverter.splitRowEntry(rowEntry)));
    }

    @Test
    public void givenStringSeparatedByCommasWithCommaInTheMiddleOfData_ThenIsValid() throws RDFFormatException, RDFTurtleCreatorException {
        String rowEntry = "City_of_London,E09000001,\"City of London, X\",7604,7648,8072,117000,131000,151000";

        assertTrue(borough.evaluateRow(rdfConverter.splitRowEntry(rowEntry)));
    }

    @Test(expected = RDFFormatException.class)
    public void givenNotValidString_ThenThrow() throws RDFFormatException, RDFTurtleCreatorException {
        borough.evaluateRow(rdfConverter.splitRowEntry(""));
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnValuesInAHashMap() throws RDFFormatException {
        String rowEntry = "City_of_London,E09000001,City of London,7604,7648,8072,117000,131000,151000";

        Map<String,String> dataHashMapExpected = new HashMap<>();
        dataHashMapExpected.put("DBPedia","City_of_London");
        dataHashMapExpected.put("BoroughName","City of London");
        dataHashMapExpected.put("BoroughCode","E09000001");
        dataHashMapExpected.put("2012_Popu","7604");
        dataHashMapExpected.put("2013_Popu","7648");
        dataHashMapExpected.put("2014_Popu","8072");
        dataHashMapExpected.put("2012_Mean","117000");
        dataHashMapExpected.put("2013_Mean","131000");
        dataHashMapExpected.put("2014_Mean","151000");

        assertEquals(dataHashMapExpected, borough.extractRowData(rdfConverter.splitRowEntry(rowEntry)));

    }

    @Test
    public void givenBoroughCodeEntry_CreateBoroughTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("BoroughCode","E09000007");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("admingeo:gssCode \"E09000007\";", borough.createTurtleDefinition(dataEntry));
        }
    }

    @Test
    public void givenCrimeEntry_CreateCrimeIDTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("DBPedia","City_of_London");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("dbpedia-page:City_of_London", borough.createTurtleDefinition(dataEntry));
        }
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnCompleteTurtle() throws RDFTurtleCreatorException {
        String rowEntry = "City_of_London,E09000001,City of London,7604,7648,8072,117000,131000,151000";

        StringBuffer turtleExpected = new StringBuffer();
        turtleExpected.append("dbpedia-page:City_of_London\n");
        turtleExpected.append("\tadmingeo:gssCode \"E09000001\";\n");
        turtleExpected.append("\tdbpedia:income _:b0, _:b1, _:b2;\n");
        turtleExpected.append("\tdbpedia:Population _:p0, _:p1, _:p2.\n");
        turtleExpected.append("\n");

        turtleExpected.append("_:b0\n");
        turtleExpected.append("\tdc:date \"2012\"^^xsd:gYear;\n");
        turtleExpected.append("\towl:hasValue \"117000\"^^xsd:integer;\n");
        turtleExpected.append("\tgpowl:nameCurrencyEN \"pound sterling\"^^xsd:string.\n");
        turtleExpected.append("\n");
        turtleExpected.append("_:b1\n");
        turtleExpected.append("\tdc:date \"2013\"^^xsd:gYear;\n");
        turtleExpected.append("\towl:hasValue \"131000\"^^xsd:integer;\n");
        turtleExpected.append("\tgpowl:nameCurrencyEN \"pound sterling\"^^xsd:string.\n");
        turtleExpected.append("\n");
        turtleExpected.append("_:b2\n");
        turtleExpected.append("\tdc:date \"2014\"^^xsd:gYear;\n");
        turtleExpected.append("\towl:hasValue \"151000\"^^xsd:integer;\n");
        turtleExpected.append("\tgpowl:nameCurrencyEN \"pound sterling\"^^xsd:string.\n");
        turtleExpected.append("\n");

        turtleExpected.append("_:p0\n");
        turtleExpected.append("\tdc:date \"2012\"^^xsd:gYear;\n");
        turtleExpected.append("\towl:hasValue \"7604\"^^xsd:integer.\n");
        turtleExpected.append("\n");
        turtleExpected.append("_:p1\n");
        turtleExpected.append("\tdc:date \"2013\"^^xsd:gYear;\n");
        turtleExpected.append("\towl:hasValue \"7648\"^^xsd:integer.\n");
        turtleExpected.append("\n");
        turtleExpected.append("_:p2\n");
        turtleExpected.append("\tdc:date \"2014\"^^xsd:gYear;\n");
        turtleExpected.append("\towl:hasValue \"8072\"^^xsd:integer.\n");
        turtleExpected.append("\n");

        String turtleResult = borough.createTurtleDefinition(rdfConverter.splitRowEntry(rowEntry));
        assertEquals(turtleExpected.toString(), turtleResult);
        System.out.println(turtleResult);
    }

}