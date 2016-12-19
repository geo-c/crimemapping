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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Jeilones on 15/12/2016.
 */
public class CrimeTest {

    private final RDFConverter<Crime> rdfConverter = new RDFConverter<>(Crime.class);
    private Crime crime;

    @Before
    public void readFile() throws RDFTurtleCreatorException {
        crime = rdfConverter.createTurtleDefinition();
    }


    @Test
    public void givenEmptyStringsSeparatedByCommas13_ThenIsValid() throws RDFFormatException, RDFTurtleCreatorException {
        String rowEntry = ",,,,,,,,,,,,";
        assertTrue(crime.evaluateRow(rdfConverter.splitRowEntry(rowEntry)));
    }

    @Test
    public void givenStringSeparatedByCommasWithLastEmptyComma_ThenIsValid() throws RDFFormatException, RDFTurtleCreatorException {
        String rowEntry = "GL0120141,2014-01,City of London Police,City of London Police,-0.113767,51.517372,On or near Stone Buildings,E01000914,Camden 028B,Theft from the person,Camden,E09000007,London_Borough_of_Camden";

        assertTrue(crime.evaluateRow(rdfConverter.splitRowEntry(rowEntry)));
    }

    @Test
    public void givenStringSeparatedByCommasWithCommaInTheMiddleOfData_ThenIsValid() throws RDFFormatException, RDFTurtleCreatorException {
        String rowEntry = "GL0120141,2014-01,City of London Police,City of London Police,-0.113767,51.517372,\"On or near Stone Buildings, crossing the street\",E01000914,Camden 028B,Theft from the person,Camden,E09000007,London_Borough_of_Camden";

        assertTrue(crime.evaluateRow(rdfConverter.splitRowEntry(rowEntry)));
    }

    @Test(expected = RDFFormatException.class)
    public void givenNotValidString_ThenThrow() throws RDFFormatException, RDFTurtleCreatorException {
        crime.evaluateRow(rdfConverter.splitRowEntry(""));
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnValuesInAHashMap() throws RDFFormatException {
        String rowEntry = "GL0120141,2014-01,City of London Police,City of London Police,-0.113767,51.517372,On or near Stone Buildings,E01000914,Camden 028B,Theft from the person,Camden,E09000007,London_Borough_of_Camden";

        //CrimeID,Month,Reported by,Falls within,Longitude,Latitude,Location,LSOA code,LSOA name,Crime type,Borough Name,Borough Code,DBPedia

        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("CrimeID","GL0120141");
        dataHashMap.put("Month","2014-01");
        dataHashMap.put("ReportedBy","City of London Police");
        dataHashMap.put("FallsWithin","City of London Police");
        dataHashMap.put("Longitude","-0.113767");
        dataHashMap.put("Latitude","51.517372");
        dataHashMap.put("Location","On or near Stone Buildings");
        dataHashMap.put("LSOACode","E01000914");
        dataHashMap.put("LSOAName","Camden 028B");
        dataHashMap.put("CrimeType","Theft from the person");

        dataHashMap.put("BoroughName","Camden");
        dataHashMap.put("BoroughCode","E09000007");

        dataHashMap.put("DBPedia","London_Borough_of_Camden");
        //dataHashMap.put("LastOutcomeCategory","Investigation complete; no suspect identified");

        //dataHashMap.put("Context","");

        //dataHashMap.put("LAUACode","E09000007");

        assertEquals(dataHashMap, crime.extractRowData(rdfConverter.splitRowEntry(rowEntry)));

    }

    @Test
    public void givenCrimeTypeEntry_CreateCrimeTypeTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("CrimeType","Theft from the person");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("rdf:type crime:TheftFromThePerson;", crime.createTurtleDefinition(dataEntry));
        }
    }
    @Test
    public void givenMonthEntry_CreateTimeTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("Month","2013-01");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            //assertEquals("time:month \"1\"^^xsd:integer;\n\ttime:year \"2013\"^^xsd:integer;", crime.createTurtleDefinition(dataEntry));
            assertEquals("lode:atTime _:t012013;", crime.createTurtleDefinition(dataEntry));
        }
    }

    @Test
    public void givenLongitudEntry_CreateGeoLonTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("Longitude","-0.111497");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("geo:long \"-0.111497\";", crime.createTurtleDefinition(dataEntry));
        }
    }

    @Test
    public void givenCrimeEntry_CreateCrimeIDTurtle() throws Exception {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("CrimeID","GL0120141");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("crime:GL0120141", crime.createTurtleDefinition(dataEntry));
        }
    }

    @Test(expected = RDFNotDefinedException.class)
    public void givenNotDefinedEntrySet_ThrowException() throws RDFTurtleCreatorException, RDFNotDefinedException {
        Map<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("AnyTurtle","AnyResult");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            assertEquals("AnyResult", crime.createTurtleDefinition(dataEntry));
        }
    }
    @Test
    public void givenAllEntrySet_CreateRespectivelyTurtle() {
        Map<String,String> dataHashMap = new HashMap<String, String>();

        dataHashMap.put("CrimeID","GL0120141");
        dataHashMap.put("Month","2014-01");
        dataHashMap.put("ReportedBy","City of London Police");
        dataHashMap.put("FallsWithin","City of London Police");
        dataHashMap.put("Longitude","-0.113767");
        dataHashMap.put("Latitude","51.517372");
        dataHashMap.put("Location","On or near Stone Buildings");
        dataHashMap.put("LSOACode","E01000914");
        dataHashMap.put("LSOAName","Camden 028B");
        dataHashMap.put("CrimeType","Theft from the person");
        dataHashMap.put("BoroughName","Camden");
        dataHashMap.put("BoroughCode","E09000007");
        dataHashMap.put("DBPedia","London_Borough_of_Camden");

        Map<String,String> turtleHashMap = new HashMap<String, String>();
        turtleHashMap.put("CrimeID","crime:GL0120141");
        //turtleHashMap.put("Month","time:month \"1\"^^xsd:integer;\n\ttime:year \"2014\"^^xsd:integer;");
        turtleHashMap.put("Month","lode:atTime _:t012014;");
        turtleHashMap.put("ReportedBy","ontotext:statedBy _:reporterCOLP;");
        turtleHashMap.put("FallsWithin","NOT_DEFINE");
        turtleHashMap.put("Longitude","geo:long \"-0.113767\";");
        turtleHashMap.put("Latitude","geo:lat \"51.517372\";");
        turtleHashMap.put("Location","NOT_DEFINE");
        turtleHashMap.put("LSOACode","admingeo:hasAreaCode \"E01000914\";");
        turtleHashMap.put("LSOAName","gn:name \"Camden 028B\";");
        turtleHashMap.put("CrimeType","rdf:type crime:TheftFromThePerson;");
        //turtleHashMap.put("BoroughName","admingeo:LondonBorough \"Camden\";");
        turtleHashMap.put("BoroughName","NOT_DEFINE");
        turtleHashMap.put("BoroughCode","NOT_DEFINE");
        turtleHashMap.put("DBPedia","lode:atPlace dbpedia-page:London_Borough_of_Camden;");

        for(Map.Entry dataEntry:dataHashMap.entrySet()){
            try {
                assertEquals(turtleHashMap.get(dataEntry.getKey()), crime.createTurtleDefinition(dataEntry));
            } catch (RDFTurtleCreatorException rdfTurtleCreator) {
                rdfTurtleCreator.printStackTrace();
            } catch (RDFNotDefinedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void whenStringWithDataSeparatedByCommas_ThenReturnCompleteTurtle() throws RDFTurtleCreatorException {
        String rowEntry = "GL0120141,2014-01,City of London Police,City of London Police,-0.113767,51.517372,On or near Stone Buildings,E01000914,Camden 028B,Theft from the person,Camden,E09000007,London_Borough_of_Camden";

        StringBuffer turtleExpected = new StringBuffer();
        turtleExpected.append("crime:GL0120141\n");
        turtleExpected.append("\tgeo:long \"-0.113767\";\n");
        turtleExpected.append("\tgeo:lat \"51.517372\";\n");
        turtleExpected.append("\tlode:atTime _:t012014;\n");
        turtleExpected.append("\trdf:type crime:TheftFromThePerson;\n");
        turtleExpected.append("\tontotext:statedBy _:reporterCOLP;\n");
        turtleExpected.append("\tlode:atPlace dbpedia-page:London_Borough_of_Camden.\n");
        turtleExpected.append("\n");
        turtleExpected.append("_:reporterCOLP\n");
        turtleExpected.append("\tfoaf:name \"City of London Police\".\n");
        turtleExpected.append("\n");
        turtleExpected.append("_:t012014\n");
        turtleExpected.append("\ttime:month \"--01\"^^xsd:gMonth;\n");
        turtleExpected.append("\ttime:year \"2014\"^^xsd:gYear.\n");
        turtleExpected.append("\n");

        String turtleResult = crime.createTurtleDefinition(rdfConverter.splitRowEntry(rowEntry));
        assertEquals(turtleExpected.toString(), turtleResult);
        System.out.println(turtleResult);
    }
}
