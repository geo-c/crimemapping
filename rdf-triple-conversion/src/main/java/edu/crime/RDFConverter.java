package edu.crime;

import edu.crime.exceptions.RDFConverterException;
import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFNotDefinedException;
import edu.crime.exceptions.RDFTurtleCreatorException;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jeilones on 21/11/2016.
 */
public class RDFConverter {


    private static final int VALID_LENGHT = 14;
    public static final String EXPRESSION_CVS_SPLITTER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";
    private File[] files;

    private List<String> IDENTIFIERS = new ArrayList<String>();
    private String folderLocation;

    {
        IDENTIFIERS.add("CrimeID");
        IDENTIFIERS.add("Longitude");
        IDENTIFIERS.add("Latitude");
        IDENTIFIERS.add("Month");
        IDENTIFIERS.add("CrimeType");
        IDENTIFIERS.add("LSOAName");
        IDENTIFIERS.add("LSOACode");
        IDENTIFIERS.add("ReportedBy");
        //IDENTIFIERS.add("FallsWithin");
        //IDENTIFIERS.add("Location");
        //IDENTIFIERS.add("LastOutcomeCategory");
        //IDENTIFIERS.add("Context");
        IDENTIFIERS.add("BoroughName");
        IDENTIFIERS.add("LAUACode");
    }

    private Map<String, String> TURTLES_SYNTAXIS = new HashMap<String, String>();
    {
        TURTLES_SYNTAXIS.put("CrimeID","crime:");
        TURTLES_SYNTAXIS.put("Month","time:month \"(MONTH)\"^^xsd:integer;\n\ttime:year \"(YEAR)\"^^xsd:integer;");
        TURTLES_SYNTAXIS.put("ReportedBy","rdfs:comment");
        //TURTLES_SYNTAXIS.put("FallsWithin","NOT_DEFINED");
        TURTLES_SYNTAXIS.put("Longitude","geo:long");
        TURTLES_SYNTAXIS.put("Latitude","geo:lat");
        //TURTLES_SYNTAXIS.put("Location","NOT_DEFINED");
        TURTLES_SYNTAXIS.put("LSOACode","admingeo:hasAreaCode");
        TURTLES_SYNTAXIS.put("LSOAName","gn:name");
        TURTLES_SYNTAXIS.put("CrimeType","rdf:type"); //Which kind of crimes exist in the vocabulary
        //TURTLES_SYNTAXIS.put("LastOutcomeCategory","NOT_DEFINED");
        //TURTLES_SYNTAXIS.put("Context","NOT_DEFINED");
        TURTLES_SYNTAXIS.put("BoroughName","admingeo:LondonBorough");
        TURTLES_SYNTAXIS.put("LAUACode","admingeo:gssCode");
    }

    public void readFiles(String folderLocation) {
        this.folderLocation = folderLocation;

        File localDir = new File(folderLocation);

        files = localDir.listFiles((dir, filename) -> filename.endsWith(".csv"));
    }

    public File[] getFiles() {
        return files;
    }

    public boolean evaluateRow(String rowEntry) throws RDFFormatException {
        boolean isValidLenght = rowEntry.split(EXPRESSION_CVS_SPLITTER, -1).length == VALID_LENGHT;
        if(!isValidLenght){
            throw new RDFFormatException("Not valid length, should contain " + VALID_LENGHT + " values separated by commas");
        }
        return isValidLenght;
    }

    public Map extractRowData(String rowEntry) throws RDFFormatException {
        evaluateRow(rowEntry);

        String[] splittedRowEntry = rowEntry.split(EXPRESSION_CVS_SPLITTER,-1);
        HashMap<String,String> dataHashMap = new HashMap<String, String>();
        dataHashMap.put("CrimeID",splittedRowEntry[0]);
        dataHashMap.put("Month",splittedRowEntry[1]);
        dataHashMap.put("ReportedBy",splittedRowEntry[2]);
        dataHashMap.put("FallsWithin",splittedRowEntry[3]);
        dataHashMap.put("Longitude",splittedRowEntry[4]);
        dataHashMap.put("Latitude",splittedRowEntry[5]);
        dataHashMap.put("Location",splittedRowEntry[6]);
        dataHashMap.put("LSOACode",splittedRowEntry[7]);
        dataHashMap.put("LSOAName",splittedRowEntry[8]);
        dataHashMap.put("CrimeType",splittedRowEntry[9]);
        dataHashMap.put("LastOutcomeCategory",splittedRowEntry[10]);
        dataHashMap.put("Context",splittedRowEntry[11]);
        dataHashMap.put("BoroughName",splittedRowEntry[12]);
        dataHashMap.put("LAUACode",splittedRowEntry[13]);
        return dataHashMap;
    }

    public String createTurtle(Map.Entry<String, String> dataEntry) throws RDFTurtleCreatorException, RDFNotDefinedException {
        return createTurtle(dataEntry.getKey(), dataEntry.getValue());
    }

    private String createTurtle(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        String turtle;

        if(TURTLES_SYNTAXIS.get(key) == null) throw new RDFNotDefinedException(key);

        if("CrimeID".equals(key)){
            turtle = createIDTurtle(key, value);
        }else if("Month".equals(key)){
            turtle = createTimeTurtle(key, value);
        }else if("CrimeType".equals(key)){
            turtle = createCrimeTypeTurtle(key, value);
        }else {
            turtle = TURTLES_SYNTAXIS.get(key);
            turtle = turtle + " \"" + value + "\";";
        }
        return turtle;
    }

    private String createCrimeTypeTurtle(String key, String value) {
        if(value == null || "".equals(value.trim())){
            return "crime:NOT_DEFINED";
        }

        final String[] crimeTypeTurtle = {TURTLES_SYNTAXIS.get(key)};
        crimeTypeTurtle[0] += " crime:";

        List<String> crimeType = Arrays.asList(value.split(" "));
        crimeType.forEach(item ->
                crimeTypeTurtle[0] = crimeTypeTurtle[0]+ (item.substring(0,1).toUpperCase() + item.substring(1,item.length()))
        );

        crimeTypeTurtle[0] += ";";
        return crimeTypeTurtle[0];
    }

    private String createIDTurtle(String key, String value) {
        String idTurtle = TURTLES_SYNTAXIS.get(key);
        idTurtle = idTurtle + value;
        return idTurtle;
    }

    private String createTimeTurtle(String key, String value) throws RDFTurtleCreatorException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar calendar = new GregorianCalendar();
        String dateTurtle = TURTLES_SYNTAXIS.get(key);
        try {
            Date entryDate = dateFormat.parse(value + "-01");

            calendar.setTime(entryDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;// adding one, gregorian calendar months are 0-11

            dateTurtle = dateTurtle.replace("(MONTH)", String.valueOf(month))
                    .replace("(YEAR)",String.valueOf(year));
        } catch (ParseException e) {
            throw new RDFTurtleCreatorException(e);
        }
        return dateTurtle;
    }

    public String createTurtle(String rowEntry) throws RDFTurtleCreatorException {
        String turtle = "";
        try {
            Map<String, String> dataHashMap = extractRowData(rowEntry);
            for (int i = 0; i < IDENTIFIERS.size(); i++) {
                String key = IDENTIFIERS.get(i);
                turtle = turtle + (key.equals("CrimeID")?"":"\t") + createTurtle(key, dataHashMap.get(key));

                if (i == IDENTIFIERS.size()-1){
                    turtle = turtle.substring(0,turtle.length()-1) + ".";
                }

                turtle += "\n";
            }
        } catch (RDFFormatException e) {
            throw new RDFTurtleCreatorException(e);
        } catch (RDFNotDefinedException e) {
            throw new RDFTurtleCreatorException(e);
        }

        return turtle;
    }

    synchronized public String convertCVSToTTL(File csvFile, int indexFile) throws RDFConverterException {

        BufferedReader br = null;
        FileOutputStream fos = null;

        String rowEntry = "";
        int rowEntryNumber = 0;
        String ttlPath = folderLocation + csvFile.getName() + ".ttl";

        try {
            //////Write TTL File
            File file = new File(ttlPath);
            if (!file.exists()) {
                file.createNewFile();
            }

            fos = new FileOutputStream(file);
            fos.write(createTTLHeader().getBytes());
            ////

            br = new BufferedReader(new FileReader(csvFile));
            while ((rowEntry = br.readLine()) != null) {
                rowEntryNumber++;

                if (rowEntryNumber > 1) {
                    fos.write(this.createTurtle(rowEntry).getBytes());
                    fos.write("\n".getBytes());
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
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RDFConverterException("Error in closing the BufferedReader. Row Entry: " + rowEntryNumber, e);
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ioe) {
                    throw new RDFConverterException("Error in closing the FileOutPutStream. Row Entry: " + rowEntryNumber, ioe);
                }
            }
        }
        return ttlPath;
    }

    public String createTTLHeader() {
        StringBuffer ttlHeader = new StringBuffer();
        ttlHeader.append("@prefix crime: <http://www.google.com/#>.\n");
        ttlHeader.append("@prefix time: <http://www.w3.org/2006/time#>.\n");
        ttlHeader.append("@prefix gn: <http://www.geonames.org/ontology#>.\n");
        ttlHeader.append("@prefix xsd: <http://www.w3.org/2001/XMLSchema#>.\n");
        ttlHeader.append("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.\n");
        ttlHeader.append("@prefix geo: <http://www.w3.org/2003/01/geo/wgs84_pos#>.\n");
        ttlHeader.append("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>.\n");
        ttlHeader.append("@prefix admingeo: <http://data.ordnancesurvey.co.uk/ontology/admingeo#>.\n");
        ttlHeader.append("\n\n");
        return ttlHeader.toString();
    }

    public void convertCVSToTTL(String csvFolder) throws RDFConverterException {
        this.readFiles(csvFolder);
        for (int i = 0; i < files.length; i++) {
            this.convertCVSToTTL(files[i], i+1);
        }
    }
}
