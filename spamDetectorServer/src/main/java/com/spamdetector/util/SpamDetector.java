package com.spamdetector.util;

import com.spamdetector.domain.TestFile;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;


/**
 * TODO: This class will be implemented by you
 * You may create more methods to help you organize you strategy and make you code more readable
 */
public class SpamDetector {
    private final Parser parser;
    private static final Logger logger = Logger.getLogger(SpamDetector.class.getName());

    public SpamDetector() {
        parser = new Parser();
    }

    public List<TestFile> trainAndTest(File mainDirectory) {
        // TODO: main method of loading the directories and files, training and testing the model

        Map<String, Double> probabilities = calculateTrainingProbabilities(mainDirectory);

        // Test URLs
        // TestHamUrl = mainDirectory + "\test\ham"
        URL testHamUrl = this.getClass().getClassLoader().getResource("/data/test/ham");
        File testHamDirectory = null;
        try {
            testHamDirectory = new File(testHamUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        List<TestFile> testHamFiles = getTestFiles(testHamDirectory, "Ham", probabilities);

        // TestSpamUrl = mainDirectory + "\test\spam"
        URL testSpamUrl = this.getClass().getClassLoader().getResource("/data/test/spam");
        File testSpamDirectory = null;
        try {
            testSpamDirectory = new File(testSpamUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        List<TestFile> testSpamFiles = getTestFiles(testSpamDirectory, "Spam", probabilities);

        List<TestFile> combinedList = new ArrayList<TestFile>(testHamFiles);
        combinedList.addAll(testSpamFiles);

        return combinedList;
    }

    private List<TestFile> getTestFiles(File mainDirectory, String actualClass, Map<String, Double> probabilities) {
        List<TestFile> testFiles = new ArrayList<>();

        File[] files = mainDirectory.listFiles();

        for (File file: files) {
            // Calculate spam probability for current word
            double spamProbability = calculateSpamProbability(probabilities, fileToSet(file));

            // Create the new TestFile
            TestFile newTestFile = new TestFile(file.getName(), spamProbability, actualClass);

            testFiles.add(newTestFile);
        }

        return testFiles;
    }

    /**
     * Converts a given file to a Set of words (unique)
     * @param file
     * @return
     */
    private static Set<String> fileToSet(File file) {
        Set<String> stringSet = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                stringSet.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception according to your requirement
        }
        return stringSet;
    }

    private double calculateSpamProbability(Map<String, Double> prSpamGivenWi, Set<String> email) {
        double n = 0.0;

        for (String word : email) {
            Double prSWi = prSpamGivenWi.get(word); // Pr(S|Wi)

            if (prSWi != null) {
                n += Math.log(1 - prSWi) - Math.log(prSWi);
            }
        }

        double probabilityOfSpam = 1.0 / (1.0 + Math.exp(n));
        return probabilityOfSpam;
    }

    private Map<String, Double> calculateTrainingProbabilities(File mainDirectory) {
        // Train URLs
        // trainHamUrl = mainDirectory + "\train\ham"
        Map<String, Integer> ham1 = parser.getWordFrequency(new File(mainDirectory, "/train/ham"));

        // trainHam2Url = mainDirectory + "\train\ham2"
        Map<String, Integer> ham2 = parser.getWordFrequency(new File(mainDirectory, "/train/ham2"));

        // Merged ham frequencies
        Map<String, Integer> trainHamFreq = parser.mergeHamFrequencies(ham1, ham2);

        // trainSpamUrl = mainDirectory + "\train\spam"
        Map<String, Integer> trainSpamFreq = parser.getWordFrequency(new File(mainDirectory, "/train/spam"));

        return parser.calculateProbabilities(trainSpamFreq, trainHamFreq, 501, 2752);
    }

}

