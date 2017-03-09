package edu.crime.turtles;

import edu.crime.exceptions.RDFFormatException;
import edu.crime.exceptions.RDFNotDefinedException;
import edu.crime.exceptions.RDFTurtleCreatorException;
import edu.crime.abstractTurtles.Turtle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Jeilones on 10/12/2016.
 */
public class Crime extends Turtle {

    private List<String> IDENTIFIERS = new ArrayList<String>();
    private String timeTurtle;
    private String reportedByTurtle;
    private Map<String,String> timeMap = new HashMap<>();
    private Map<String,String> reportedByMap = new HashMap<>();

    {
        IDENTIFIERS.add("CrimeID");
        IDENTIFIERS.add("Longitude");
        IDENTIFIERS.add("Latitude");
        IDENTIFIERS.add("Month");
        IDENTIFIERS.add("CrimeType");
        IDENTIFIERS.add("ReportedBy");
        //IDENTIFIERS.add("LSOAName");
        //IDENTIFIERS.add("LSOACode");
        //IDENTIFIERS.add("FallsWithin");
        //IDENTIFIERS.add("Location");
        //IDENTIFIERS.add("LastOutcomeCategory");
        //IDENTIFIERS.add("Context");
        //IDENTIFIERS.add("BoroughName");
        //IDENTIFIERS.add("LAUACode");
        IDENTIFIERS.add("DBPedia");
    }

    private Map<String, String> SYNTAXIS = new HashMap<String, String>();
    {
        SYNTAXIS.put("CrimeID","crime:");
        //SYNTAXIS.put("Month","time:month \"(MONTH)\"^^xsd:integer;\n\ttime:year \"(YEAR)\"^^xsd:integer;");
        SYNTAXIS.put("Month","lode:atTime _:t(MONTH)(YEAR);");
        SYNTAXIS.put("ReportedBy","ontotext:statedBy _:reporter");
        //SYNTAXIS.put("FallsWithin","NOT_DEFINED");
        SYNTAXIS.put("Longitude","geo:long");
        SYNTAXIS.put("Latitude","geo:lat");
        //SYNTAXIS.put("Location","NOT_DEFINED");
        SYNTAXIS.put("LSOACode","admingeo:hasAreaCode");
        SYNTAXIS.put("LSOAName","gn:name");
        SYNTAXIS.put("CrimeType","rdf:type"); //Which kind of crimes exist in the vocabulary
        //SYNTAXIS.put("LastOutcomeCategory","NOT_DEFINED");
        //SYNTAXIS.put("Context","NOT_DEFINED");
        //SYNTAXIS.put("BoroughName","admingeo:LondonBorough");
        //SYNTAXIS.put("LAUACode","admingeo:gssCode");
        SYNTAXIS.put("DBPedia","lode:atPlace dbpedia-page:");
    }

    public Crime() {
        super(13);
    }

    @Override
    public List<String> getIdentifiers() {
        return IDENTIFIERS;
    }

    @Override
    public Map<String, String> getSyntaxis() {
        return SYNTAXIS;
    }

    @Override
    public Map extractRowData(String[] splittedRowEntry) throws RDFFormatException {

        HashMap<String,String> dataHashMap = new HashMap<>();

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
        dataHashMap.put("BoroughName",splittedRowEntry[10]);
        dataHashMap.put("BoroughCode",splittedRowEntry[11]);
        dataHashMap.put("DBPedia",splittedRowEntry[12]);
        return dataHashMap;
    }

    @Override
    public String createTurtleDefinition(String[] rowEntry) throws RDFTurtleCreatorException {
        return super.createTurtleDefinition(rowEntry) + this.reportedByTurtle + this.timeTurtle;
    }


    @Override
    public String createTurtleString(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        return (key.equals("CrimeID")?"":"\t") + createTurtleDefinition(key, value);
    }

    @Override
    public String createTurtleDefinition(String key, String value) throws RDFNotDefinedException, RDFTurtleCreatorException {
        String turtle;

        if(this.getSyntaxis().get(key) == null) throw new RDFNotDefinedException(key);

        if("CrimeID".equals(key)){
            turtle = createIDTurtle(key, value);
        }else if("Month".equals(key)){
            turtle = createTimeTurtle(key, value);
        }else if("CrimeType".equals(key)){
            turtle = createCrimeTypeTurtle(key, value);
        }else if("ReportedBy".equals(key)){
            turtle = createCrimeReportedBy(key, value);
        }else if("DBPedia".equals(key)){
            turtle = createCrimeDBPediaBorough(key, value);
        }else {
            turtle = createTriple(key, value);
        }
        return turtle;
    }

    @Override
    public boolean evaluateRow(String[] rowEntry) throws RDFFormatException {
        return super.evaluateRow(rowEntry);
    }


    /**
     * Create the Reported By triple
     * */
    private String createCrimeReportedBy(String key, String value) throws RDFTurtleCreatorException {
        String reportedByTurtle = this.getSyntaxis().get(key);
        try{
            String[] reportedBy = value.trim().split(" ");
            String reporterID = "";
            for (String reporterSplitted : reportedBy) {
                reporterID += reporterSplitted.substring(0,1).toUpperCase();
            }

            reportedByTurtle += reporterID + ";";

            ReportedByTurtle reportedByTurtle1 = new ReportedByTurtle();
            String[] reportedRowEntry = {value};
            Map<String,String> localTimeMap = reportedByTurtle1.extractRowData(reportedRowEntry);
            String reportedByID = localTimeMap.get("ReportedByID");

            this.reportedByTurtle = "";

            if(!this.reportedByMap.containsKey(reportedByID)){
                this.reportedByTurtle = reportedByTurtle1.createTurtleDefinition(reportedRowEntry);
                this.reportedByMap.put(reportedByID,this.reportedByTurtle);
            }
        } catch (RDFFormatException e) {
            throw new RDFTurtleCreatorException(e);
        }
        return reportedByTurtle;
    }

    /**
     * Create the DBPedia borough triple
     * */
    private String createCrimeDBPediaBorough(String key, String value) {
        String turtle;
        turtle = this.getSyntaxis().get(key);
        turtle = turtle + value + ";";
        return turtle;
    }

    /**
     * Create the crime type triple
     * */
    private String createCrimeTypeTurtle(String key, String value) {
        if(value == null || "".equals(value.trim())){
            return "crime:NOT_DEFINED";
        }

        final String[] crimeTypeTurtle = {this.getSyntaxis().get(key)};
        crimeTypeTurtle[0] += " crime:";

        List<String> crimeType = Arrays.asList(value.split(" "));
        crimeType.forEach(item ->
                crimeTypeTurtle[0] = crimeTypeTurtle[0]+ (item.substring(0,1).toUpperCase() + item.substring(1,item.length()))
        );

        crimeTypeTurtle[0] += ";";
        return crimeTypeTurtle[0];
    }

    /**
     * Create the time blank node for the crime turtle
     * */
    private String createTimeTurtle(String key, String value) throws RDFTurtleCreatorException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar calendar = new GregorianCalendar();
        String dateTurtle = this.getSyntaxis().get(key);
        try {
            Date entryDate = dateFormat.parse(value + "-01");

            calendar.setTime(entryDate);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;// adding one, gregorian calendar months are 0-11

            String monthString = month < 10? "0" + String.valueOf(month):String.valueOf(month);
            dateTurtle = dateTurtle.replace("(MONTH)", monthString)
                    .replace("(YEAR)",String.valueOf(year));

            TimeTurtle timeTurtle = new TimeTurtle();
            String[] timeRowEntry = {monthString, String.valueOf(year)};
            Map<String,String> localTimeMap = timeTurtle.extractRowData(timeRowEntry);
            String timeID = localTimeMap.get("TimeID");

            this.timeTurtle = "";

            if(!this.timeMap.containsKey(timeID)){
                this.timeTurtle = timeTurtle.createTurtleDefinition(timeRowEntry);
                this.timeMap.put(timeID,this.timeTurtle);
            }
        } catch (ParseException e) {
            throw new RDFTurtleCreatorException(e);
        } catch (RDFFormatException e) {
            throw new RDFTurtleCreatorException(e);
        }
        return dateTurtle;
    }
}
