package edu.crime;

import edu.crime.exceptions.RDFConverterException;
import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import edu.crime.turtles.Crime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by Jeilones on 21/11/2016.
 */

public class RDFConverterTest {


    private final RDFConverter<Crime> rdfConverter = new RDFConverter<>(Crime.class);
    private final String folderLocation = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\";

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
    public void createTTLHeader(){
        StringBuffer headerExpected = new StringBuffer();
        headerExpected.append("@prefix crime: <http://course.geoinfo2016.org/G3/>.\n");
        headerExpected.append("@prefix foaf: <http://xmlns.com/foaf/0.1/>.\n");
        headerExpected.append("@prefix time: <http://www.w3.org/2006/time#>.\n");
        headerExpected.append("@prefix owl: <https://www.w3.org/2002/07/owl#>.\n");
        headerExpected.append("@prefix dbpedia: <http://dbpedia.org/ontology/>.\n");
        headerExpected.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n");
        headerExpected.append("@prefix dbpedia-page: <http://dbpedia.org/page/>.\n");
        headerExpected.append("@prefix lode: <http://linkedevents.org/ontology/>.\n");
        headerExpected.append("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n");
        headerExpected.append("@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>.\n");
        headerExpected.append("@prefix gpowl: <http://aims.fao.org/aos/geopolitical.owl#>.\n");
        headerExpected.append("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n");
        headerExpected.append("@prefix ontotext: <http://www.ontotext.com/proton/protontop#>.\n");
        headerExpected.append("@prefix admingeo: <http://data.ordnancesurvey.co.uk/ontology/admingeo/>.\n");
        headerExpected.append("@prefix dc: <http://dublincore.org/documents/2012/06/14/dcmi-terms/?v=elements#>.\n");

        headerExpected.append("\n\n");

        assertEquals(headerExpected.toString(),rdfConverter.createTTLHeader());
    }

    @Test
    public void createCrimeTypes(){
        StringBuffer crimeTypesExpected = new StringBuffer();
        crimeTypesExpected.append("crime:TheftFromThePerson rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:BicycleTheft rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:Burglary rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:OtherTheft rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:AntiSocialBehaviour rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:ShopLifting rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:PublicOrder rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:Drugs rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:VehicleCrime rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:ViolenceAndSexualOffences rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:CriminalDamageAndArson rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:PossessionOfWeapons rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:Robbery rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("crime:OtherCrime rdfs:subClassOf crime:Crime.\n");
        crimeTypesExpected.append("\n");

        assertEquals(crimeTypesExpected.toString(),rdfConverter.createCrimeTypes());
    }

    @Test
    public void createCrimeClasses(){
        StringBuffer crimeClassesExpected =  new StringBuffer();
        crimeClassesExpected.append("crime:Crime rdfs:subClassOf rdfs:Class.");
        crimeClassesExpected.append("\n\n");

        assertEquals(crimeClassesExpected.toString(),rdfConverter.createCrimeClasses());
    }

    @Ignore
    public void createTTLFile() throws RDFConverterException {
        final RDFConverter<Crime> rdfConverter = new RDFConverter<>(Crime.class);

        String csvFile = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\Test Folder\\2014.csv";
        rdfConverter.readFiles("C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\Test Folder");
        rdfConverter.convertCVSToTTL(new File(csvFile), 1);
    }

    @Test
    public void whenGenericCrimeReturnCrime() throws RDFTurtleCreatorException {
        RDFConverter<Crime> rdfConverter= new RDFConverter<>(Crime.class);
        Crime crime = rdfConverter.createTurtleDefinition();
        assertTrue(crime instanceof Crime);
    }
}