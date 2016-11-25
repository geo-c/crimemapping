package edu.crime;

import edu.crime.exceptions.RDFConverterException;
import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFNotDefinedException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Jeilones on 21/11/2016.
 */

public class RDFConverterTest {


    private final RDFConverter rdfConverter = new RDFConverter();
    private final String folderLocation = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Yearly Data (Crime)\\";

    @Before
    public void readFile(){
        rdfConverter.readFiles(folderLocation);
        assertNotNull(rdfConverter.getFiles());
    }

    @Test
    public void containCSVFiles(){
        assertTrue(String.valueOf(true), rdfConverter.getFiles().length > 0);
    }

    @Test
    public void givenEmptyStringsSeparatedByCommas14_ThenIsValid() throws RDFFormatException {
        String rowEntry = ",,,,,,,,,,,,,";
        assertTrue(rdfConverter.evaluateRow(rowEntry));
    }

    @Test
    public void givenStringSeparatedByCommasWithLastEmptyComma_ThenIsValid() throws RDFFormatException {
        String rowEntry = "c945b7b6d42fa86b2c9012c59e63bda8902dc6eb6a5ca0e0199b50f983bb19e5,2014-01,City of London Police,City of London Police,-0.113767,51.517372,On or near Stone Buildings,E01000914,Camden 028B,Theft from the person,Investigation complete; no suspect identified,,Camden,E09000007";

        assertTrue(rdfConverter.evaluateRow(rowEntry));
    }

    @Test
    public void givenStringSeparatedByCommasWithCommaInTheMiddleOfData_ThenIsValid() throws RDFFormatException {
        String rowEntry = "0f11179019081c65d2798c8186a10e21a4568909e3a51a4d1f144f6c9e5eff5a,2014-01,Metropolitan Police Service,Metropolitan Police Service,-0.443403,51.564463,\"On or near High Road, Ickenham\",E01002460,Hillingdon 011C,Violence and sexual offences,Offender ordered to pay compensation,,Hillingdon,E09000017";

        assertTrue(rdfConverter.evaluateRow(rowEntry));
    }

    @Test(expected = RDFFormatException.class)
    public void givenNotValidString_ThenThrow() throws RDFFormatException {
        rdfConverter.evaluateRow("");
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnValuesInAHashMap(){
        String rowEntry = "c945b7b6d42fa86b2c9012c59e63bda8902dc6eb6a5ca0e0199b50f983bb19e5,2014-01,City of London Police,City of London Police,-0.113767,51.517372,On or near Stone Buildings,E01000914,Camden 028B,Theft from the person,Investigation complete; no suspect identified,,Camden,E09000007";

        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("CrimeID","c945b7b6d42fa86b2c9012c59e63bda8902dc6eb6a5ca0e0199b50f983bb19e5");
        dataHashMap.put("Month","2014-01");
        dataHashMap.put("ReportedBy","City of London Police");
        dataHashMap.put("FallsWithin","City of London Police");
        dataHashMap.put("Longitude","-0.113767");
        dataHashMap.put("Latitude","51.517372");
        dataHashMap.put("Location","On or near Stone Buildings");
        dataHashMap.put("LSOACode","E01000914");
        dataHashMap.put("LSOAName","Camden 028B");
        dataHashMap.put("CrimeType","Theft from the person");
        dataHashMap.put("LastOutcomeCategory","Investigation complete; no suspect identified");
        dataHashMap.put("Context","");
        dataHashMap.put("BoroughName","Camden");
        dataHashMap.put("LAUACode","E09000007");

        try {
            assertEquals(dataHashMap, rdfConverter.extractRowData(rowEntry));
        } catch (RDFFormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenCrimeTypeEntry_CreateCrimeTypeTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("CrimeType","Theft from the person");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("rdf:type crime:TheftFromThePerson;",rdfConverter.createTurtle(dataEntry));
        }
    }
    @Test
    public void givenMonthEntry_CreateTimeTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("Month","2013-01");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("time:month \"1\"^^xsd:integer;\n\ttime:year \"2013\"^^xsd:integer;",rdfConverter.createTurtle(dataEntry));
        }
    }

    @Test
    public void givenLongitudEntry_CreateGeoLonTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("Longitude","-0.111497");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("geo:lon \"-0.111497\";",rdfConverter.createTurtle(dataEntry));
        }
    }

    @Test
    public void givenCrimeEntry_CreateCrimeIDTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("CrimeID","ccefde2e8b138159774076b6f149c82f32a0ae7b7bf8bea25a7e43b8da852aa5");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("crime:ccefde2e8b138159774076b6f149c82f32a0ae7b7bf8bea25a7e43b8da852aa5",rdfConverter.createTurtle(dataEntry));
        }
    }

    @Test(expected = RDFNotDefinedException.class)
    public void givenNotDefinedEntrySet_ThrowException() throws RDFTurtleCreatorException, RDFNotDefinedException {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("AnyTurtle","AnyResult");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("AnyResult",rdfConverter.createTurtle(dataEntry));
        }
    }
    @Test
    public void givenAllEntrySet_CreateRespectivelyTurtle() {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("CrimeID","c945b7b6d42fa86b2c9012c59e63bda8902dc6eb6a5ca0e0199b50f983bb19e5");
        dataHashMap.put("Month","2014-01");
        dataHashMap.put("ReportedBy","City of London Police");
        dataHashMap.put("FallsWithin","City of London Police");
        dataHashMap.put("Longitude","-0.113767");
        dataHashMap.put("Latitude","51.517372");
        dataHashMap.put("Location","On or near Stone Buildings");
        dataHashMap.put("LSOACode","E01000914");
        dataHashMap.put("LSOAName","Camden 028B");
        dataHashMap.put("CrimeType","Theft from the person");
        dataHashMap.put("Context","");
        dataHashMap.put("BoroughName","Camden");
        dataHashMap.put("LAUACode","E09000007");

        Map<String,String> turtleHashMap = new HashMap<String, String>();
        turtleHashMap.put("CrimeID","crime:c945b7b6d42fa86b2c9012c59e63bda8902dc6eb6a5ca0e0199b50f983bb19e5");
        turtleHashMap.put("Month","time:month \"1\"^^xsd:integer;\n\ttime:year \"2014\"^^xsd:integer;");
        turtleHashMap.put("ReportedBy","rdfs:comment \"City of London Police\";");
        turtleHashMap.put("FallsWithin","NOT_DEFINE");
        turtleHashMap.put("Longitude","geo:lon \"-0.113767\";");
        turtleHashMap.put("Latitude","geo:lat \"51.517372\";");
        turtleHashMap.put("Location","NOT_DEFINE");
        turtleHashMap.put("LSOACode","admingeo:hasAreaCode \"E01000914\";");
        turtleHashMap.put("LSOAName","gn:name \"Camden 028B\";");
        turtleHashMap.put("CrimeType","rdf:type crime:TheftFromThePerson;");
        turtleHashMap.put("LastOutcomeCategory","NOT_DEFINED");
        turtleHashMap.put("Context","NOT_DEFINED");
        turtleHashMap.put("BoroughName","admingeo:LondonBorough \"Camden\";");
        turtleHashMap.put("LAUACode","admingeo:gssCode \"E09000007\";");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            try {
                assertEquals(turtleHashMap.get(dataEntry.getKey()),rdfConverter.createTurtle(dataEntry));
            } catch (RDFTurtleCreatorException rdfTurtleCreator) {
                rdfTurtleCreator.printStackTrace();
            } catch (RDFNotDefinedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnCompleteTurtle() throws RDFTurtleCreatorException {
        String rowEntry = "c945b7b6d42fa86b2c9012c59e63bda8902dc6eb6a5ca0e0199b50f983bb19e5,2014-01,City of London Police,City of London Police,-0.113767,51.517372,On or near Stone Buildings,E01000914,Camden 028B,Theft from the person,Investigation complete; no suspect identified,,Camden,E09000007";

        StringBuffer turtleExpected = new StringBuffer();
        turtleExpected.append("crime:c945b7b6d42fa86b2c9012c59e63bda8902dc6eb6a5ca0e0199b50f983bb19e5\n");
        turtleExpected.append("\tgeo:lon \"-0.113767\";\n");
        turtleExpected.append("\tgeo:lat \"51.517372\";\n");
        turtleExpected.append("\ttime:month \"1\"^^xsd:integer;\n");
        turtleExpected.append("\ttime:year \"2014\"^^xsd:integer;\n");
        turtleExpected.append("\trdf:type crime:TheftFromThePerson;\n");
        turtleExpected.append("\tgn:name \"Camden 028B\";\n");
        turtleExpected.append("\tadmingeo:hasAreaCode \"E01000914\";\n");
        turtleExpected.append("\trdfs:comment \"City of London Police\";\n");
        turtleExpected.append("\tadmingeo:LondonBorough \"Camden\";\n");
        turtleExpected.append("\tadmingeo:gssCode \"E09000007\".\n");

        String turtleResult = rdfConverter.createTurtle(rowEntry);
        assertEquals(turtleExpected.toString(), turtleResult);
        System.out.println(turtleResult);
    }

    @Test
    public void createTTLHeader(){
        StringBuffer headerExpected = new StringBuffer();
        headerExpected.append("PREFIX crime: <http://www.google.com/#>\n");
        headerExpected.append("PREFIX time: <http://www.w3.org/2006/time#>\n");
        headerExpected.append("PREFIX gn: <http://www.geonames.org/ontology#>\n");
        headerExpected.append("PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n");
        headerExpected.append("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n");
        headerExpected.append("PREFIX geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n");
        headerExpected.append("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
        headerExpected.append("PREFIX admingeo: <http://data.ordnancesurvey.co.uk/ontology/admingeo#>\n");
        headerExpected.append("\n\n");

        assertEquals(headerExpected.toString(),rdfConverter.createTTLHeader());
    }

    @Test
    public void createTTLFile() throws RDFConverterException {
        final RDFConverter rdfConverter = new RDFConverter();

        String csvFile = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Yearly Data (Crime)\\ttl autogenaration temp\\2014.csv";
        rdfConverter.readFiles("C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Yearly Data (Crime)\\ttl autogenaration temp\\");
        rdfConverter.convertCVSToTTL(new File(csvFile), 1);
    }
}