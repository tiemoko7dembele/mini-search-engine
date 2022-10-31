package com.dassaultsystemes.searchengine.persistence;

import com.dassaultsystemes.searchengine.common.Constants;
import com.dassaultsystemes.searchengine.exceptions.DocumentNotTokenizedException;
import com.dassaultsystemes.searchengine.models.Document;

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
        File folder = new File(Constants.STORAGE+"/data/tokens");
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
        properties.load(new FileInputStream(Constants.STORAGE +"/data/tokens/"+filename+".txt"));

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
        properties.store(new FileOutputStream(Constants.STORAGE +"/data/tokens/"+document.getName()+".txt"), null);

        //Store original file
        Path filepath = Paths.get(Constants.STORAGE +"/data/originalFiles/"+document.getName()+".txt");
        if(!Files.exists(filepath)){
            byte[] lines = Files.readAllBytes(Paths.get(document.getFilepath()));
            Files.write(filepath, lines, StandardOpenOption.CREATE);
        }
    }
}
