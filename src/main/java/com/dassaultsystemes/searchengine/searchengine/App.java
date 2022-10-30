package com.dassaultsystemes.searchengine.searchengine;

import com.dassaultsystemes.searchengine.searchengine.exceptions.DocumentNotTokenizedException;
import com.dassaultsystemes.searchengine.searchengine.models.Document;
import com.dassaultsystemes.searchengine.searchengine.persistence.Persister;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final String stopExecution = "exit()";

    static Persister persister = new Persister();

    public static void main( String[] args ) throws IOException, DocumentNotTokenizedException {

        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your file path : ");
        String inputFilePath = scan.nextLine();

        // /Users/tiemoko7dembele/Documents/dassault/paris.txt
        // /Users/tiemoko7dembele/Documents/dassault/example.txt

        String filename = Paths.get(inputFilePath)
                .getFileName().toString();
        Document document = persister.getDocument(filename.replaceFirst("[.][^.]+$", ""));
        if(document==null){
            document = new Document(inputFilePath);
            System.out.println("Loading file ...");
            document.tokenize();
            persister.write(document);
            System.out.println("File loaded.");
        }else{
            System.out.println("Filename already exist : "+filename);
        }

        while(inputFilePath != null
                && !inputFilePath.isEmpty()
                && !inputFilePath.trim().isEmpty()
                && !inputFilePath.equalsIgnoreCase(stopExecution)){
            System.out.print("Enter your text : ");
            String textToSearch = scan.nextLine().toLowerCase();

            if(textToSearch.equals(stopExecution)) break;

            //Do search
            List<Integer> numberOfFoundLines = document.searchText(textToSearch);
            List<String> linesFound = document.foundLines(new HashSet<>(numberOfFoundLines));
            if(numberOfFoundLines==null || numberOfFoundLines.isEmpty()){
                System.out.println("Not found !");
            }else{
                System.out.println("Words found in : "+numberOfFoundLines);
                System.out.println("###### WORDS FOUND IN THESE LINES ######");
                for(String line : linesFound){
                    System.out.println(line);
                }
            }
        }
        System.out.println( "STOPPING ..." );
        System.out.println( "SEARCH ENGINE IS DOWN NOW" );
    }
}
