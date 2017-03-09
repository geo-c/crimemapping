package edu.crime;

import edu.crime.exceptions.RDFConverterException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import edu.crime.abstractTurtles.Turtle;
import edu.crime.turtles.Crime;

import java.io.*;

/**
 * Created by Jeilones on 21/11/2016.
 */
public class RDFConverter<T extends Turtle>{

    public static final String EXPRESSION_CVS_SPLITTER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private final Class<T> clazz;
    private File[] files;
    private String folderLocation;

    private long MEGA_BYTES_LIMIT_SIZE = 30*1024*1024;

    /**
     * Constructor receiving the root turtle
     * */
    public RDFConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Path for the CSV file to be converted to RDF Turtles
     * */
    public void readFiles(String folderLocation) {
        this.folderLocation = folderLocation;

        File localDir = new File(folderLocation);

        files = localDir.listFiles((dir, filename) -> filename.endsWith(".csv"));
    }

    /**
     * Return the CSV file collection into a folder to be converted to RDF Turtles
     * */
    public File[] getFiles() {
        return files;
    }

    /**
     * Spit every entry into several columns, each turtle definition has a column limit restriction
     * */
    public String[] splitRowEntry(String rowEntry) {
        return rowEntry.split(EXPRESSION_CVS_SPLITTER, -1);
    }

    /**
     * This method take the CSV file, use a buffer reader to read every entry of the file
     * and create segmented TTL files, and create an instance of the root Turtle that contains the appropiate syntaxes
     *
     * */
    synchronized public void convertCVSToTTL(File csvFile, int indexFile) throws RDFConverterException {

        BufferedReader br = null;

        int rowEntryNumber = 0;
        int filesCounter = 0;


        try {
            T turtleDefinition = this.createTurtleDefinition();
            br = new BufferedReader(new FileReader(csvFile));

            createSegmentedTTLFile(turtleDefinition, csvFile, br, filesCounter, rowEntryNumber);
        } catch (FileNotFoundException e) {
            throw new RDFConverterException("Row Entry: " + rowEntryNumber, e);
        } catch (IOException e) {
            throw new RDFConverterException("Row Entry: " + rowEntryNumber, e);
        } catch (RDFTurtleCreatorException e) {
            throw new RDFConverterException("Row Entry: " + rowEntryNumber, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RDFConverterException("Error in closing the BufferedReader. Row Entry: " + rowEntryNumber, e);
                }
            }
        }
    }

    /**
     * Given a series of entries this method create segmented TTL files, once the maximum per file is reach,
     * the method create a new TTL file until complete the total entries of the CSV file
     * */
    private void createSegmentedTTLFile(T turtleDefinition, File csvFile, BufferedReader br, int filesCounter, int rowEntryNumber) throws RDFConverterException {
        FileOutputStream fos = null;
        String ttlPath = getTTLFileName(csvFile.getName(), filesCounter);
        String rowEntry = "";
        boolean isNecessaryNewFile = false;

        try {
            //////Write TTL File
            File file = new File(ttlPath);
            if (!file.exists()) {
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            fos.write(createTTLHeader().getBytes());
            fos.write(createCrimeTypes().getBytes());
            fos.write(createCrimeClasses().getBytes());
            ////

            while ((rowEntry = br.readLine()) != null) {
                rowEntryNumber++;

                if (rowEntryNumber > 1) {
                    fos.write(turtleDefinition.createTurtleDefinition(splitRowEntry(rowEntry)).getBytes());
                    fos.write("\n".getBytes());
                }

                if(isNecessaryNewFile = file.length() > MEGA_BYTES_LIMIT_SIZE){
                    break;
                }
            }

            fos.flush();
        } catch (FileNotFoundException e) {
            throw new RDFConverterException("Row Entry: " + rowEntryNumber, e);
        } catch (IOException e) {
            throw new RDFConverterException("Row Entry: " + rowEntryNumber, e);
        } catch (RDFTurtleCreatorException e) {
            throw new RDFConverterException("Row Entry: " + rowEntryNumber, e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    throw new RDFConverterException("Error in closing the FileOutPutStream. Row Entry: " + rowEntryNumber, ioe);
                }
            }
        }

        if(isNecessaryNewFile){
            createSegmentedTTLFile(turtleDefinition, csvFile, br, ++filesCounter, rowEntryNumber);
        }
    }

    /**
     * this method create the new TTL file name given the CSV file name and the segmented file counter
     * */
    private String getTTLFileName(String csvFileName, int filesCounter) {
        return folderLocation + csvFileName + filesCounter +".ttl";
    }

    /**
     * Create the TTL head, this is common for all crime turtles
     * */
    public String createTTLHeader() {
        StringBuffer ttlHeader = new StringBuffer();
        ttlHeader.append("@prefix crime: <http://course.geoinfo2016.org/G3/>.\n");
        ttlHeader.append("@prefix foaf: <http://xmlns.com/foaf/0.1/>.\n");
        ttlHeader.append("@prefix time: <http://www.w3.org/2006/time#>.\n");
        ttlHeader.append("@prefix owl: <https://www.w3.org/2002/07/owl#>.\n");
        ttlHeader.append("@prefix dbpedia: <http://dbpedia.org/ontology/>.\n");
        ttlHeader.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n");
        ttlHeader.append("@prefix dbpedia-page: <http://dbpedia.org/page/>.\n");
        ttlHeader.append("@prefix lode: <http://linkedevents.org/ontology/>.\n");
        ttlHeader.append("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n");
        ttlHeader.append("@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>.\n");
        ttlHeader.append("@prefix gpowl: <http://aims.fao.org/aos/geopolitical.owl#>.\n");
        ttlHeader.append("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n");
        ttlHeader.append("@prefix ontotext: <http://www.ontotext.com/proton/protontop#>.\n");
        ttlHeader.append("@prefix admingeo: <http://data.ordnancesurvey.co.uk/ontology/admingeo/>.\n");
        ttlHeader.append("@prefix dc: <http://dublincore.org/documents/2012/06/14/dcmi-terms/?v=elements#>.\n");

        ttlHeader.append("\n\n");
        return ttlHeader.toString();
    }

    public void convertCVSToTTL(String csvFolder) throws RDFConverterException {
        this.readFiles(csvFolder);
        for (int i = 0; i < files.length; i++) {
            this.convertCVSToTTL(files[i], i+1);
        }
    }

    /**
     * Create the new instance of the turtle definition object
     * */
    public T createTurtleDefinition() throws RDFTurtleCreatorException {
        //Class<T> clazz = (Class<T>) (getClass().getTypeParameters()[0].getBounds()[0]);
        //TurtleFactory turtleFactory = TurtleInjector.getInstance().getInjector().getInstance(TurtleFactory.class);
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RDFTurtleCreatorException(e);
        } catch (IllegalAccessException e) {
            throw new RDFTurtleCreatorException(e);
        }
    }

    /**
     * Create all the crime types, this is necessary for every crime TTL file
     * */
    public String createCrimeTypes() {
        StringBuffer crimeTypes = new StringBuffer();
        crimeTypes.append("crime:TheftFromThePerson rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:BicycleTheft rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:Burglary rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:OtherTheft rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:AntiSocialBehaviour rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:ShopLifting rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:PublicOrder rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:Drugs rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:VehicleCrime rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:ViolenceAndSexualOffences rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:CriminalDamageAndArson rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:PossessionOfWeapons rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:Robbery rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("crime:OtherCrime rdfs:subClassOf crime:Crime.\n");
        crimeTypes.append("\n");

        return crimeTypes.toString();
    }

    /**
     * Create the Crime class as a subclass of rdfs:class, necessary once per Crime TTL File
     * */
    public String createCrimeClasses() {
        StringBuffer crimeClasses =  new StringBuffer();
        crimeClasses.append("crime:Crime rdfs:subClassOf rdfs:Class.");
        crimeClasses.append("\n\n");

        return crimeClasses.toString();
    }
}
