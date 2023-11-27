package com.example.dogbreeddetector;

import java.util.ArrayList;
import java.util.List;

public class ClarifaiResponseParser {

    public static class Concept {
        private String id;
        private String name;
        private double value;

        public Concept(String id, String name, double value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        // Getters
        public String getId() { return id; }
        public String getName() { return name; }
        public double getValue() { return value; }
    }

    public static List<Concept> parseResponse(String jsonResponse) {
        List<Concept> concepts = new ArrayList<>();
        String conceptsKey = "\"concepts\":";
        int conceptsStart = jsonResponse.indexOf(conceptsKey) + conceptsKey.length();

        if (conceptsStart == -1) {
            return concepts;        // Returns empty concepts list if not found
        }

        // Trim the string to start from the concepts array
        String conceptsStr = jsonResponse.substring(conceptsStart);
        conceptsStr = conceptsStr.substring(conceptsStr.indexOf("[") + 1, conceptsStr.indexOf("]"));

        while (conceptsStr.contains("{")) {
            int conceptStart = conceptsStr.indexOf("{");
            int conceptEnd = conceptsStr.indexOf("}", conceptStart) + 1;
            String conceptStr = conceptsStr.substring(conceptStart, conceptEnd);

            // Extract fields from the conceptStr
            Concept concept = extractConcept(conceptStr);
            if (concept != null) {
                concepts.add(concept);
            }

            // Move to the next concept
            conceptsStr = conceptsStr.substring(conceptEnd);
        }

        return concepts;
    }

    private static Concept extractConcept(String conceptStr) {
        try {
            String id = extractField(conceptStr, "\"id\":");
            String name = extractField(conceptStr, "\"name\":");
            double value = Double.parseDouble(extractField(conceptStr, "\"value\":"));

            return new Concept(id, name, value);

        } catch (Exception e) {                        //should probably handle
            System.out.println("parsing error");
            return null;
        }
    }

    private static String extractField(String str, String fieldName) {
        int start = str.indexOf(fieldName) + fieldName.length();
        int end = str.indexOf(",", start);
        end = (end == -1) ? str.indexOf("}", start) : end;

        String field = str.substring(start, end).trim();
        field = field.replaceAll("\"", "");     // Removes quotes
        return field;
    }

}
