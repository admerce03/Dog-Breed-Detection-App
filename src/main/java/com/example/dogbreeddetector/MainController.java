package com.example.dogbreeddetector;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML
    private ImageView imageView;

    @FXML
    private Button uploadButton;

    @FXML
    private Button selectFileButton;

    @FXML
    private Label label;

    private File selectedFile;

    private Alert processingAlert;


    @FXML
    public void initialize() {
        uploadButton.setDisable(true); // Disables uploadButton to prevent unnecessary API calls
    }

    // File selection when user interacts with selectFileButton
    @FXML
    private void handleSelectFileAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);

        // Use the class-level selectedFile variable here
        this.selectedFile = fileChooser.showOpenDialog(null);

        if (this.selectedFile != null) {
            uploadButton.setDisable(false);

            Image image = new Image(this.selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    // processing alert box
    private void showProcessingPopup() {
        processingAlert = new Alert(Alert.AlertType.INFORMATION);
        processingAlert.setTitle("Processing");
        processingAlert.setHeaderText(null);
        processingAlert.setContentText("Processing, please wait...");
        processingAlert.show();
    }

    @FXML
    private void handleUploadAction(ActionEvent event) {
        if (selectedFile != null) {
            System.out.println("Selected file: " + selectedFile.getAbsolutePath()); // Logs selected file path
            showProcessingPopup();

            new Thread(() -> {
                try {
                    ClarifaiService clarifaiService = new ClarifaiService();
                    String response = clarifaiService.detectDogBreed(selectedFile);
                    System.out.println("API Response: " + response); // Logs the API response

                    List<ClarifaiResponseParser.Concept> concepts = ClarifaiResponseParser.parseResponse(response);

                    Platform.runLater(() -> {
                        processingAlert.close();
                        displayConcepts(concepts);
                    });

                } catch (IOException | InterruptedException e) {
                    Platform.runLater(() -> {
                        processingAlert.close();
                        label.setText("Error: " + e.getMessage()); // Displays error message in the UI
                    });
                    e.printStackTrace();
                }
            }).start();
        } else {
            label.setText("No file selected."); // Update UI to indicate no file was selected
        }
    }

    private void displayConcepts(List<ClarifaiResponseParser.Concept> concepts) {
        if (concepts.isEmpty()) {
            label.setText("No breeds detected.");
            return;
        }

        // Updates UI with the element with the highest confidence score in sorted list
        ClarifaiResponseParser.Concept topConcept = concepts.get(0);
        String message = "This is a " + topConcept.getName();
        label.setText(message);
    }

}
