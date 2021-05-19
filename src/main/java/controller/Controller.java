package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.Resource;
import service.ClientService;

import java.io.*;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Controller {

    @FXML
    private ComboBox<String> methodBox;

    @FXML
    private TextField urlField;

    @FXML
    private ListView<Resource> filesBox;

    @FXML
    private Button sendButton;

    @FXML
    private TextField localFileField;

    @FXML
    private Button changeLocalFile;

    @FXML
    private Label statusCodeLabel;

    @FXML
    private Button getSelectedButton;

    @FXML
    void initialize() {
        methodBox.getItems().addAll("GET", "POST", "PUT", "DELETE");
    }

    @FXML
    private void handleSend(ActionEvent event) {
        if (urlField.getText().isEmpty()) {
            //TODO alert "url field is empty"
            return;
        }
        ClientService clientService = new ClientService();
        switch (methodBox.getSelectionModel().getSelectedItem()) {
            case "GET": {
                if (urlField.getText().matches("^.*/\\d+/?$")) {
                    HttpResponse<byte[]> response = clientService.getResource(urlField.getText());
                    try {
                        OutputStream out = new FileOutputStream(Long.toString(Instant.now().toEpochMilli()));
                        out.write(response.body());
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    statusCodeLabel.setText(Integer.toString(response.statusCode()));
                } else {
                    HttpResponse<String> response = clientService.getAllResources(urlField.getText());
                    List<Resource> resources = null;
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        resources = objectMapper.readValue(response.body(), new TypeReference<List<Resource>>() {
                        });
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    filesBox.getItems().addAll(resources);
                    statusCodeLabel.setText(Integer.toString(response.statusCode()));
                }
                break;
            }
            case "POST": {
                File toPost = new File(localFileField.getText());
                HttpResponse<?> response = clientService.postResource(urlField.getText(), toPost);
                statusCodeLabel.setText(Integer.toString(response.statusCode()));
                break;
            }
            case "PUT": {
                File toPut = new File(localFileField.getText());
                HttpResponse<?> response = clientService.putResource(urlField.getText(), toPut);
                statusCodeLabel.setText(Integer.toString(response.statusCode()));
                break;
            }
            case "DELETE": {
                HttpResponse<?> response = clientService.deleteResource(urlField.getText());
                statusCodeLabel.setText(Integer.toString(response.statusCode()));
                break;
            }
            default: {
                //TODO alert "need to select method"
            }
        }
    }

    @FXML
    private void handleChangeLocalFile(ActionEvent event) {

    }

    @FXML
    private void handleGetSelected(ActionEvent event) {

    }

}