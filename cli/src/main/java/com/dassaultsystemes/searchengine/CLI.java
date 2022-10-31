package com.dassaultsystemes.searchengine;

import com.dassaultsystemes.searchengine.exceptions.DocumentNotTokenizedException;
import com.dassaultsystemes.searchengine.lemmatization.Lemmatizer;
import com.dassaultsystemes.searchengine.models.Document;
import com.dassaultsystemes.searchengine.persistence.Persister;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import static com.dassaultsystemes.searchengine.Mode.BATCH;
import static com.dassaultsystemes.searchengine.Mode.CONSOLE;
import static com.dassaultsystemes.searchengine.common.Constants.*;

/**
 * Command-Line Interface
 */
public class CLI
{
    private static final String stopExecution = "exit()";

    private static final String usageCLI = "Usage:\r" + APP_NAME + "\n"
            + " --mode console \r\n"
            + " --mode <execution-mode> --input-file <input-file> --to-search <text-to-search> [options]\r\n"
            + " --version  \r\n" + "\r\n"
            + "     [REQUIRED]                       \r\n"
            + "     execution-mode:             \"console\" or \"batch\"\r\n\n"
            + "     input-file:                 Uri of \"txt\" or any other file \r\n"
            + "                                 File System Storage\r\n"
            + "                                 Argument required in \"batch\" mode \r\n\n"
            + "     text-to-search:             Full text to search in the file \r\n"
            + "                                 UTF-8\r\n"
            + "                                 Argument required in \"batch\" mode \r\n\n"
            + "     [Optional]                       \r\n"
            + "     stop-words-file:            Uri of \"txt\" or any other file that contains all stop words (only one by line)\r\n"
            + "                                 File System Storage\r\n\n";

    private static final String welcomeCLI =
            "$$$$$$$\\                                                    $$\\   $$\\            $$$$$$\\                        $$\\                                                 \n" +
            "$$  __$$\\                                                   $$ |  $$ |          $$  __$$\\                       $$ |                                                \n" +
            "$$ |  $$ | $$$$$$\\   $$$$$$$\\  $$$$$$$\\  $$$$$$\\  $$\\   $$\\ $$ |$$$$$$\\         $$ /  \\__|$$\\   $$\\  $$$$$$$\\ $$$$$$\\    $$$$$$\\  $$$$$$\\$$$$\\   $$$$$$\\   $$$$$$$\\ \n" +
            "$$ |  $$ | \\____$$\\ $$  _____|$$  _____| \\____$$\\ $$ |  $$ |$$ |\\_$$  _|        \\$$$$$$\\  $$ |  $$ |$$  _____|\\_$$  _|  $$  __$$\\ $$  _$$  _$$\\ $$  __$$\\ $$  _____|\n" +
            "$$ |  $$ | $$$$$$$ |\\$$$$$$\\  \\$$$$$$\\   $$$$$$$ |$$ |  $$ |$$ |  $$ |           \\____$$\\ $$ |  $$ |\\$$$$$$\\    $$ |    $$$$$$$$ |$$ / $$ / $$ |$$$$$$$$ |\\$$$$$$\\  \n" +
            "$$ |  $$ |$$  __$$ | \\____$$\\  \\____$$\\ $$  __$$ |$$ |  $$ |$$ |  $$ |$$\\       $$\\   $$ |$$ |  $$ | \\____$$\\   $$ |$$\\ $$   ____|$$ | $$ | $$ |$$   ____| \\____$$\\ \n" +
            "$$$$$$$  |\\$$$$$$$ |$$$$$$$  |$$$$$$$  |\\$$$$$$$ |\\$$$$$$  |$$ |  \\$$$$  |      \\$$$$$$  |\\$$$$$$$ |$$$$$$$  |  \\$$$$  |\\$$$$$$$\\ $$ | $$ | $$ |\\$$$$$$$\\ $$$$$$$  |\n" +
            "\\_______/  \\_______|\\_______/ \\_______/  \\_______| \\______/ \\__|   \\____/        \\______/  \\____$$ |\\_______/    \\____/  \\_______|\\__| \\__| \\__| \\_______|\\_______/ \n" +
            "                                                                                          $$\\   $$ |                          Welcome to our Full-Text Search Engine\n" +
            "                                                                                          \\$$$$$$  |                                                 Version : "+ APP_VERSION + "\n" +
            "                                                                                           \\______/                                                                 \n" ;

    static Persister persister = new Persister();

    public static void main( String[] args ) throws IOException, DocumentNotTokenizedException {
        //Load arguments values
        // No args
        if (args.length == 0) {
            System.out.println(usageCLI);
            System.exit(0);
        }

        // --version
        if ((args.length == 1) && (args[0].compareTo("--version") == 0)) {
            System.out.println(APP_NAME + " version: " + APP_VERSION);
            System.exit(0);
        }

        HashMap<String, String> argMap = new HashMap<>();
        int iArg = 0;
        String key = null;
        String arg;
        String value = "";

        while (iArg < args.length) {
            arg = args[iArg];
            if (arg.startsWith("--")) {
                key = arg.trim();
                value = "";
            } else {
                value += arg.trim()+" ";
            }
            argMap.put(key, value);
            iArg++;
        }

        String inputFile = argMap.containsKey("--input-file") ? argMap.get("--input-file").trim() : null;
        String stopWordsFile = argMap.containsKey("--stop-words-file") ? argMap.get("--stop-words-file").trim() : null;
        String toSearch = argMap.containsKey("--to-search") ? argMap.get("--to-search").trim() : null;
        String mode = argMap.containsKey("--mode") ? argMap.get("--mode").trim() : "";

        Document document = null;

        // --mode
        if (mode.equals(CONSOLE.toString().toLowerCase())) {
            System.out.println(welcomeCLI);

            Scanner scan = new Scanner(System.in);
            System.out.print("Enter your file path : ");
            String inputFilePath = scan.nextLine().trim();

            if(!inputFilePath.equals(stopExecution)){
                File file = new File(inputFilePath);
                while(!file.exists() || file.isDirectory()){
                    System.out.print("The file \""+inputFilePath+"\" doesn't exist. ");
                    System.out.print("Enter your file path : ");
                    inputFilePath = scan.nextLine().trim();
                    if(inputFilePath.equals(stopExecution)) break;
                    file = new File(inputFilePath);
                }
            }

            if(!inputFilePath.equals(stopExecution)){
                String filename = Paths.get(inputFilePath)
                        .getFileName().toString();
                document = persister.getDocument(filename.replaceFirst("[.][^.]+$", ""));
                if(document==null){
                    document = new Document(inputFilePath);
                    System.out.println("Loading file ...");
                    document.tokenize();
                    persister.write(document);
                    System.out.println("File loaded.");
                }else{
                    System.out.println("Filename already exist : "+filename);
                }
            }

            while(inputFilePath != null
                    && !inputFilePath.isEmpty()
                    && !inputFilePath.trim().isEmpty()
                    && !inputFilePath.equals(stopExecution)){
                System.out.print("Enter the text to search : ");
                String textToSearch = scan.nextLine().toLowerCase();

                if(textToSearch.equals(stopExecution)) break;

                //Do search
                long startTime = System.currentTimeMillis();
                List<Integer> numberOfFoundLines = document.searchText(textToSearch);
                long stopTime = System.currentTimeMillis();
                System.out.println("Results found in " + (stopTime - startTime) + " ms");
                if(numberOfFoundLines==null || numberOfFoundLines.isEmpty()){
                    System.out.println("Not found !");
                }else{
                    System.out.println("Words found in : "+numberOfFoundLines);
                    List<String> linesFound = document.foundLines(new HashSet<>(numberOfFoundLines));
                    System.out.println("###### WORDS FOUND IN THESE LINES ######");
                    for(String line : linesFound){
                        System.out.println(line);
                    }
                }
            }
        }else if(mode.equals(BATCH.toString().toLowerCase())){
            if(inputFile!=null) {
                File file = new File(inputFile);
                if (!file.exists() || file.isDirectory() || toSearch == null || toSearch.trim().isEmpty()) {
                    System.out.println("Please, check your --input-file and/or --to-search value");
                    System.exit(0);
                }
            }else{
                System.out.println("No <input-file> given.");
                System.exit(0);
            }

            String filename = Paths.get(inputFile)
                    .getFileName().toString();
            document = persister.getDocument(filename.replaceFirst("[.][^.]+$", ""));

            Lemmatizer lemmatizer = new Lemmatizer(DEFAULT_STOP_WORDS_FILE_PATH);
            if(stopWordsFile!=null){
                File f = new File(stopWordsFile);
                if (f.exists() && !f.isDirectory()) {
                    lemmatizer = new Lemmatizer(stopWordsFile);
                }else if (stopWordsFile.equals("none")){
                    lemmatizer = new Lemmatizer(false);
                }
            }

            if(document==null){
                document = new Document(inputFile);
                System.out.println("Loading file ...");
                document.setLemmatizer(lemmatizer);
                document.tokenize();
                persister.write(document);
                System.out.println("File loaded.");
            }else{
                System.out.println("Filename already exist : "+filename);
            }

            //Do search
            long startTime = System.currentTimeMillis();
            List<Integer> numberOfFoundLines = document.searchText(toSearch);
            long stopTime = System.currentTimeMillis();
            System.out.println("Results found in " + (stopTime - startTime) + " ms");
            if(numberOfFoundLines==null || numberOfFoundLines.isEmpty()){
                System.out.println("Not found !");
            }else{
                System.out.println("Words found in : "+numberOfFoundLines);
                List<String> linesFound = document.foundLines(new HashSet<>(numberOfFoundLines));
                System.out.println("###### WORDS FOUND IN THESE LINES ######");
                for(String line : linesFound){
                    System.out.println(line);
                }
            }
        }else {
            System.out.println("Execution mode not specified.");
            System.exit(0);
        }

        System.out.println("END OF SEARCH");
    }
}