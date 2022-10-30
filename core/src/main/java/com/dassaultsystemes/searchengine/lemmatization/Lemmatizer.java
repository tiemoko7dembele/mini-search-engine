package com.dassaultsystemes.searchengine.lemmatization;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

import static com.dassaultsystemes.searchengine.common.Constants.DEFAULT_STOP_WORDS_FILE_PATH;

public class Lemmatizer {
    private static final Pattern PATTERN = Pattern.compile("\\P{IsAlphabetic}+");

    private String stopWordsFilePath;

    private boolean removeStopWords = true;

    public Lemmatizer() {
    }
    public Lemmatizer(boolean removeStopWords) {
        this.removeStopWords =removeStopWords;
    }

    public Lemmatizer(String stopWordsFilePath) {
        this.stopWordsFilePath = stopWordsFilePath;
    }

    public List<String> lemmatize(String textLine) throws FileNotFoundException {
        List<String> lemmas = new ArrayList<>();

        HashSet stopWords;
        if(this.stopWordsFilePath != null && !this.stopWordsFilePath.trim().isEmpty()){
            stopWords = getStopWords(this.stopWordsFilePath);
        }else if(removeStopWords){
            stopWords = getStopWords(DEFAULT_STOP_WORDS_FILE_PATH);
        }else{
            stopWords = new HashSet<>();
        }

        for(String word: PATTERN.split(textLine)) {
            if(!word.isEmpty() && !stopWords.contains(word))
                lemmas.add(Normalizer.normalize(word, Normalizer.Form.NFKD)
                        .toLowerCase()
                        .replaceAll("[\\p{InCombiningDiacriticalMarks}]", ""));
        }
        return lemmas;
    }

    private HashSet<String> getStopWords(String filepath) throws FileNotFoundException {
        HashSet<String> stopWords = new HashSet<>();
        Scanner stopWordsFile = new Scanner(new File(filepath));
        while (stopWordsFile.hasNext()) {
            stopWords.add(stopWordsFile.next().trim());
        }
        return stopWords;
    }
}
