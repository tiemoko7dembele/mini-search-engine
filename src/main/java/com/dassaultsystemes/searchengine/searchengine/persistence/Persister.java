package com.dassaultsystemes.searchengine.searchengine.persistence;

import com.dassaultsystemes.searchengine.searchengine.exceptions.DocumentNotTokenizedException;
import com.dassaultsystemes.searchengine.searchengine.models.Document;
import com.dassaultsystemes.searchengine.searchengine.common.Constants;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class Persister implements Serializable {

    public Persister() {}

    public Document getDocument(String filename) throws IOException {
        File folder = new File(Constants.DATABASE+"/tokens");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().equals(filename+".txt")) {
                return new Document(filename, read(filename));
            }
        }
        return null;
    }

    public HashMap<String, List<Integer>> read(String filename) throws IOException {
        HashMap<String, List<Integer>> fileContent = new HashMap<>();
        Properties properties = new Properties();
        properties.load(new FileInputStream(Constants.DATABASE +"/tokens/"+filename+".txt"));

        for (String key : properties.stringPropertyNames()) {
            fileContent.put(key, Arrays.stream(properties.get(key).toString()
                                        .trim().split("-"))
                                        .map(x -> Integer.parseInt(x))
                                        .collect(Collectors.toList()));
        }
        return fileContent;
    }

    public void write(Document document) throws IOException, DocumentNotTokenizedException {
        HashMap<String, List<Integer>> mapToStore = document.getVocabulary();
        if(mapToStore==null) throw new DocumentNotTokenizedException("Document not Tokenized.");

        //Store inverted index
        Properties properties = new Properties();
        for (Map.Entry<String,List<Integer>> entry : mapToStore.entrySet()) {
            properties.put(entry.getKey(),
                            entry.getValue().stream()
                                .map(s -> s.toString())
                                .collect(Collectors.joining("-", "", "")));
        }
        properties.store(new FileOutputStream(Constants.DATABASE +"/tokens/"+document.getName()+".txt"), null);

        //Store original file
        Path filepath = Paths.get(Constants.DATABASE +"/originalFiles/"+document.getName()+".txt");
        if(!Files.exists(filepath)){
            byte[] lines = Files.readAllBytes(Paths.get(document.getFilepath()));
            Files.write(filepath, lines, StandardOpenOption.CREATE);
        }
    }

    /*public List<String> readLines(String filename, List<Integer> numberOfFoundLines) {
        List<String> originalFoundLines = new ArrayList<>();
        try{
            Scanner scan = new Scanner(new File(DATABASE +"/originalFiles/"+filename+".txt"));
            String line = "";
            int numberOfLine = 0;
            int lengthOfFoundLines = 0;
            while(scan.hasNext()){
                numberOfLine++;
                line = scan.nextLine();
                if(numberOfFoundLines.contains(numberOfLine)){
                    originalFoundLines.add(line);
                    lengthOfFoundLines++;
                }
                if(lengthOfFoundLines==numberOfFoundLines.size()) break;
            }
        }catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return originalFoundLines;
    }*/
}
