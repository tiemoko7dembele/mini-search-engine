package com.dassaultsystemes.searchengine.searchengine.models;

import com.dassaultsystemes.searchengine.searchengine.lemmatization.Lemmatizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.dassaultsystemes.searchengine.searchengine.common.Constants.DATABASE;
import static com.dassaultsystemes.searchengine.searchengine.common.Constants.DEFAULT_STOP_WORDS_FILE_PATH;


public class Document {
    private static final Lemmatizer defaultLemmatizer = new Lemmatizer(DEFAULT_STOP_WORDS_FILE_PATH);
    private String filepath;
    private String name;
    private HashMap<String, List<Integer>> vocabulary;

    public Document(String filepath) throws FileNotFoundException {
        File file = new File(filepath);
        if(file.exists() && !file.isDirectory()){
            this.filepath = filepath;
            this.name = Paths.get(filepath).getFileName().toString().replaceFirst("[.][^.]+$", "");
            this.vocabulary = new HashMap<>();
        }else{
            throw new FileNotFoundException("File not found. Check the given path.");
        }
    }

    public Document(String filename, HashMap<String, List<Integer>> vocabulary) throws FileNotFoundException {
        this.filepath = DATABASE+"/originalFiles/"+filename+".txt";
        this.name = filename;
        this.vocabulary = vocabulary;
    }
    public List<Integer> searchText(String textToSearch) throws FileNotFoundException {
        //Lemmatization of text to search
        List<String> wordsToSearch = defaultLemmatizer.lemmatize(textToSearch);

        //Sorted by frequency
        HashMap<String, Integer> orderedWordsToSearch = new HashMap<String, Integer>();
        for(String wordToSearch : wordsToSearch){
            if(!vocabulary.containsKey(wordToSearch)) return List.of();
            orderedWordsToSearch.put(wordToSearch, vocabulary.get(wordToSearch).size());
        }

        wordsToSearch = orderedWordsToSearch
                            .entrySet().stream()
                            .sorted(Comparator.comparingInt(e -> e.getValue()))
                            .map(e -> e.getKey())
                            .collect(Collectors.toList());

        //Search common lines
        List<Integer> linesFound = vocabulary.get(wordsToSearch.get(0));
        for(String wordToSearch : wordsToSearch){
            if(linesFound==null || linesFound.isEmpty()) return linesFound;
            linesFound = commonItems(linesFound, vocabulary.get(wordToSearch));
        }
        return linesFound;
    }

    public List<Integer> searchText(List<String> lemmasToSearch){
        //Lemmatization of text to search
        List<String> wordsToSearch = lemmasToSearch;

        //Sorted by frequency
        HashMap<String, Integer> orderedWordsToSearch = new HashMap<String, Integer>();
        for(String wordToSearch : wordsToSearch){
            if(!vocabulary.containsKey(wordToSearch)) return List.of();
            orderedWordsToSearch.put(wordToSearch, vocabulary.get(wordToSearch).size());
        }

        wordsToSearch = orderedWordsToSearch
                            .entrySet().stream()
                            .sorted(Comparator.comparingInt(e -> e.getValue()))
                            .map(e -> e.getKey())
                            .collect(Collectors.toList());

        //Search common lines
        List<Integer> linesFound = vocabulary.get(wordsToSearch.get(0));
        for(String wordToSearch : wordsToSearch){
            if(linesFound==null || linesFound.isEmpty()) return linesFound;
            linesFound = commonItems(linesFound, vocabulary.get(wordToSearch));
        }
        return linesFound;
    }

    private List<Integer> commonItems(List<Integer> array1, List<Integer> array2){
        List<Integer> commonItems = new ArrayList<>();
        HashSet<Integer> hashset2 = new HashSet<>(array2);
        for (int item : array1){
            if(hashset2.contains(item)) commonItems.add(item);
        }
        return commonItems;
    }

    public void tokenize() throws FileNotFoundException {
        Scanner scan = new Scanner(new File(this.filepath));
        List<String> words;
        int lineNumber = 0;
        while(scan.hasNext()){
            lineNumber++;
            words = defaultLemmatizer.lemmatize(scan.nextLine());
            updateVocabulary(words, lineNumber);
        }
    }

    public void tokenize(Lemmatizer lemmatizer) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(this.filepath));
        List<String> words;
        int lineNumber = 0;
        while(scan.hasNext()){
            lineNumber++;
            words = lemmatizer.lemmatize(scan.nextLine());
            updateVocabulary(words, lineNumber);
        }
    }

    private void updateVocabulary(List<String> words, int lineNumber) {
        //Inverted index
        for(String word : words){
            if(vocabulary.containsKey(word)){
                List<Integer> wordLinesIndex = new ArrayList<>(vocabulary.get(word));
                wordLinesIndex.add(lineNumber);
                vocabulary.put(word, wordLinesIndex);
            }else{
                vocabulary.put(word, List.of(lineNumber));
            }
        }
    }

    public List<String> foundLines(HashSet numberOfFoundLines) throws FileNotFoundException {
        List<String> originalFoundLines = new ArrayList<>();
        Scanner scan = new Scanner(new File(DATABASE +"/originalFiles/"+this.name+".txt"));
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
        return originalFoundLines;
    }

    public void save(){
        // save original file
        // save tokenize hashMap
    }


    public String getFilepath() {
        return filepath;
    }

    public HashMap<String, List<Integer>> getVocabulary() {
        return vocabulary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void setVocabulary(HashMap<String, List<Integer>> vocabulary) {
        this.vocabulary = vocabulary;
    }
}
