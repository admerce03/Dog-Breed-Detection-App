package com.example.dogbreeddetector;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.util.Base64;

public class ClarifaiService {

    private static final String API_KEY = "875dd4353f914acdbe846601e6a7e68e";
    private static final String API_ENDPOINT = "https://api.clarifai.com/v2/models/dog-breeds-classifier/versions/2264a9846bae46f3956f1b8733469883/outputs";

    public String detectDogBreed(File imageFile) throws IOException, InterruptedException {
        String base64Image = encodeFileToBase64Binary(imageFile);
        String jsonPayload = createJsonPayload(base64Image);
        return sendHttpRequest(jsonPayload);
    }

    private String encodeFileToBase64Binary(File file) throws IOException {
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }

    private String createJsonPayload(String base64Image) { // ignore format pls. was debugging.
        return "{"
                + "\"inputs\": ["
                + "    {"
                + "        \"data\": {"
                + "            \"image\": {"
                + "                \"base64\": \"" + base64Image + "\""
                + "            }"
                + "        }"
                + "    }"
                + "],"
                + "\"user_app_id\": {"
                + "    \"user_id\": \"datastrategy\","
                + "    \"app_id\": \"dog-breeds\""
                + "}"
                + "}";
    }

    private String sendHttpRequest(String jsonPayload) throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT))
                .header("Authorization", "Key " + API_KEY)
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) { //API call status check
            System.out.println("API call successful.");
        } else {
            System.out.println("API call failed with status code: " + response.statusCode() + " and response: " + response.body());
        }

        return response.body();
    }

}
