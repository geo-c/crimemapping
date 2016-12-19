package edu.crime;

import edu.crime.turtles.Borough;
import edu.crime.turtles.Crime;

import java.io.File;

/**
 * Created by Jeilones on 24/11/2016.
 */
public class RDFConverterExecutor {
    public static void main(String[] args){
        RDFConverter<Crime> rdfConverter = new RDFConverter(Crime.class);

        //CSV File converter
        try {

            String csvFile = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\2013_Final.csv";
            rdfConverter.readFiles("C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\");


            /*String csvFile = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\Test Folder\\2014_Final.csv";
            rdfConverter.readFiles("C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\Test Folder\\");
            */

            rdfConverter.convertCVSToTTL(new File(csvFile), 1);

            RDFConverter<Borough> rdfConverter2 = new RDFConverter(Borough.class);

            csvFile = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Socioeconomic data\\Information London Boroughs.csv";
            rdfConverter2.readFiles("C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Socioeconomic data\\");

            //rdfConverter2.convertCVSToTTL(new File(csvFile), 1);

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
