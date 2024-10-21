package com.spamdetector.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;


/**
 * For each file, determine which words exist.
 * For simplicity, ignore case (all words are lowercased), and also ignore how many times a word
 * appears in a single file; just count how many files have the word.
 */
public class Parser {
    /**
     * Global variable that holds the merged frequency maps
     */
    private Map<String, Integer> frequencyMap;
    /**
     * Logger for easy debugging
     */
    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    public Parser() {
        frequencyMap = new TreeMap<String, Integer>();
    }

    /**
     * Gets the word frequency of all files
     * @param directory directory
     * @return Merged word map
     */
    public Map<String, Integer> getWordFrequency(File directory) {
        // For each file, you will determine which words exist in each file.
        // For simplicity, let’s ignore case, and let’s also ignore how many times a word
        // appears in a single file, and just count how many files have the word.
        logger.info("getWordFrequency received parameter directory: " + directory);

        File[] files = directory.listFiles();
        int filesCount = files.length;

        // Merge all frequency maps; remove duplicates
        for (File file: files) {
            Map<String, Integer> fileFrequencyMap = calculateWordFrequency(file);
            Set<String> words = fileFrequencyMap.keySet();
            Iterator<String> wordIterator = words.iterator();
            while (wordIterator.hasNext()) {
                String word = wordIterator.next();
                int wordCount = fileFrequencyMap.get(word);

                if (!frequencyMap.containsKey(word)) {
                    frequencyMap.put(word, wordCount);
                } else {
                    frequencyMap.put(word, frequencyMap.get(word) + wordCount);
                }
            }
        }
        return frequencyMap;
    }

    /**
     * Calculates the word frequency in a given file.
     * @param file Ham file
     * @return Word frequency map
     */
    private Map<String, Integer> calculateWordFrequency(File file) {
        Map<String, Integer> wordFreq = new TreeMap<>();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                String word = scanner.next();
                word = word.toLowerCase();

                if(!wordFreq.containsKey(word)) {
                    wordFreq.put(word, 1);
                }
                else {
                    wordFreq.put(word, wordFreq.get(word) + 1);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return wordFreq;
    }


    /**
     * Returns a merged map of the two ham maps as one
     * @param ham1
     * @param ham2
     * @return
     */
    public Map<String, Integer> mergeHamFrequencies(Map<String, Integer> ham1, Map<String, Integer> ham2) {
        TreeMap<String, Integer> mergedMap = new TreeMap<>();

        // Add all entries from map1 to the merged map
        for (Map.Entry<String, Integer> entry : ham1.entrySet()) {
            mergedMap.put(entry.getKey(), entry.getValue());
        }

        // Add or merge entries from map2 to the merged map
        for (Map.Entry<String, Integer> entry : ham2.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            // If the key already exists in the merged map, add the values
            if (mergedMap.containsKey(key)) {
//                mergedMap.put(key, mergedMap.get(key) + value);
                mergedMap.put(key, mergedMap.get(key) + value);
            } else {
                // Otherwise, simply put the entry into the merged map
                mergedMap.put(key, value);
            }
        }

        return mergedMap;
    }

    /**
     * Helper method to generate the probabilities based on the given spam and ham frequencies
     * @param trainSpamFreq
     * @param trainHamFreq
     * @param totalSpamEmails
     * @param totalHamEmails
     * @return a map of the probability of the certain word for appearing
     */
    public Map<String, Double> calculateProbabilities(Map<String, Integer> trainSpamFreq, Map<String, Integer> trainHamFreq, int totalSpamEmails, int totalHamEmails) {
        Map<String, Double> probabilities = new TreeMap<String, Double>();

        double totalEmails = totalSpamEmails + totalHamEmails;
        double spamProbability = (double) totalSpamEmails / totalEmails;
        double hamProbability = (double) totalHamEmails / totalEmails;

        Set<String> spamKeys = trainSpamFreq.keySet();
        Set<String> hamKeys = trainHamFreq.keySet();

        // Combine the key sets into a single set
        Set<String> uniqueKeys = new HashSet<>();
        uniqueKeys.addAll(spamKeys);
        uniqueKeys.addAll(hamKeys);

        // Get the count of unique keys
        int vocabularySize = uniqueKeys.size();

        for (String word : trainHamFreq.keySet()) {
            double wordInSpamProbability = calculateWordProbability(word, trainSpamFreq, trainSpamFreq.size(), vocabularySize);
            double wordInHamProbability = calculateWordProbability(word, trainHamFreq, trainHamFreq.size(), vocabularySize);

            double spamCurrentWordProbability = (wordInSpamProbability * spamProbability) /
                    ((wordInSpamProbability * spamProbability) + (wordInHamProbability * hamProbability));

            probabilities.put(word, spamCurrentWordProbability);
        }

        return probabilities;
    }

    /**
     * Does the actual calculation to get the word probability for the given word
     * @param word
     * @param wordCounts
     * @param totalWords
     * @return the probability of the given word
     */
    private double calculateWordProbability(String word, Map<String, Integer> wordCounts, int totalWords, double vocabularySize) {
        int count = wordCounts.getOrDefault(word, 0) + 1;
        return (double) count / (totalWords + vocabularySize);
    }
}
