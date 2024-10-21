package com.spamdetector.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spamdetector.domain.TestFile;
import com.spamdetector.util.Parser;
import com.spamdetector.util.SpamDetector;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jakarta.ws.rs.core.Response;

@Path("/spam")
public class SpamResource {

//    your SpamDetector Class responsible for all the SpamDetecting logic
    SpamDetector detector = new SpamDetector();
    private static final Logger logger = Logger.getLogger(SpamResource.class.getName());

    private List<TestFile> testFiles;
    SpamResource(){
        // TODO: load resources, train and test to improve performance on the endpoint calls
        logger.info("Training and testing the model, please wait");

        // TODO: call  this.trainAndTest();
        this.testFiles = this.trainAndTest();
    }

    @GET
    @Produces("application/json")
    public Response getSpamResults() throws JsonProcessingException {
        // TODO: return the test results list of TestFile, return in a Response object
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return Response.ok(objectMapper.writeValueAsString(testFiles))
                    .header("Content-Type", "application/json")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @GET
    @Path("/accuracy")
    @Produces("application/json")
    public Response getAccuracy() throws JsonProcessingException {
        // TODO: return the accuracy of the detector, return in a Response object
        int truePositives = 0;
        int trueNegatives = 0;
        int numFiles = testFiles.size();

        for (TestFile testFile : testFiles) {
            if(testFile.getActualClass().equals("Ham") && testFile.getSpamProbability() < 0.5) {
                truePositives += 1;
            } else if (testFile.getActualClass().equals("Spam") && testFile.getSpamProbability() >= 0.5) {
                trueNegatives += 1;
            }
        }

        double accuracy = (double) (truePositives + trueNegatives) / numFiles;

        Map<String, Object> json = new HashMap<>();
        json.put("val", accuracy);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return Response.ok(objectMapper.writeValueAsString(json))
                    .header("Content-Type", "application/json")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    @GET
    @Path("/precision")
    @Produces("application/json")
    public Response getPrecision() throws JsonProcessingException {
       // TODO: return the precision of the detector, return in a Response object
        int truePositives = 0;
        int falsePositives = 0;

        for (TestFile testFile : testFiles) {
            if(testFile.getActualClass().equals("Ham") && testFile.getSpamProbability() < 0.5) {
                truePositives += 1;
            } else if (testFile.getActualClass().equals("Ham") && testFile.getSpamProbability() >= 0.5) {
                falsePositives += 1;
            }
        }

        double precision = (double) truePositives / (truePositives + falsePositives);

        Map<String, Object> json = new HashMap<>();
        json.put("val", precision);

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return Response.ok(objectMapper.writeValueAsString(json))
                    .header("Content-Type", "application/json")
                    .header("Access-Control-Allow-Origin", "*")
                    .build();
        }
        catch (Exception e) {
            return Response.status(500).entity(e).build();
        }
    }

    private List<TestFile> trainAndTest()  {
        if (this.detector==null){
            this.detector = new SpamDetector();
        }

        // TODO: load the main directory "data" here from the Resources folder
        URL mainUrl = this.getClass().getClassLoader().getResource("/data");
        File mainDirectory = null;
        try {
            mainDirectory = new File(mainUrl.toURI());
            logger.info("mainDirectory: " + mainDirectory.toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return this.detector.trainAndTest(mainDirectory);
    }
}