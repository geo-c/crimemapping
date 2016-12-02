package edu.crime;

import edu.crime.exceptions.RDFConverterException;

import java.io.File;

/**
 * Created by Jeilones on 24/11/2016.
 */
public class RDFConverterExecutor {
    public static void main(String[] args){
        RDFConverter rdfConverter = new RDFConverter();

        //CSV File converter
        try {

            String csvFile = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\Test Folder\\2014.csv";
            rdfConverter.readFiles("C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\Test Folder\\");

            /*String csvFile = "C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\ttl autogenaration temp\\2014.csv";
            rdfConverter.readFiles("C:\\Users\\Jeilones\\Google Drive\\Crime Project\\Processed Data By Year\\ttl autogenaration temp\\");
*/
            rdfConverter.convertCVSToTTL(new File(csvFile), 1);
        } catch (RDFConverterException e) {
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
