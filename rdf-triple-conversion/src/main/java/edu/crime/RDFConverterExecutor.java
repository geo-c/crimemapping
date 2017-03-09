package edu.crime;

import edu.crime.turtles.Borough;
import edu.crime.turtles.Crime;

import java.io.File;

/**
 * Created by Jeilones on 24/11/2016.
 */
public class RDFConverterExecutor {

    /**
     * Main program that start the RDF Convertor, needs to declare the root Turtle to be created
     * */
    public static void main(String[] args){

        RDFConverter<Crime> rdfConverter = new RDFConverter(Crime.class); //Here the root turtle is the Crime turtle

        //CSV File converter
        try {

            String csvFile = "D:\\Temp\\2014_Final.csv";
            rdfConverter.readFiles("D:\\Temp\\");


            /*String csvFile = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\Test Folder\\2014_Final.csv";
            rdfConverter.readFiles("C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\Test Folder\\");
            */

            //RDF Convertor needs the CSV file path to be converted into RDF Turtles
            rdfConverter.convertCVSToTTL(new File(csvFile), 1);

            //Conversion for the Borough socio economics turtles
            RDFConverter<Borough> rdfConverter2 = new RDFConverter(Borough.class); //Here the root turtle is the Borough turtle

            csvFile = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Socioeconomic data\\Information London Boroughs.csv";
            rdfConverter2.readFiles("C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Socioeconomic data\\");

            //rdfConverter2.convertCVSToTTL(new File(csvFile), 1);

            //End conversion for the Borough socio economics turtles

            File file = new File(csvFile);
            System.out.print("File size: " + file.length());

        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        //CSV Folder converter
        try {
            String csvFolder = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\";
            rdfConverter.convertCVSToTTL(csvFolder);
        } catch (RDFConverterException e) {
            e.printStackTrace();
        }*/
    }
}
