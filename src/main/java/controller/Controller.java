package controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import model.PackedResource;
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
    private TextField remoteStorePathField;

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
                    HttpResponse<String> response = clientService.getResource(urlField.getText());
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        PackedResource packedResource = objectMapper.readValue(response.body(), PackedResource.class);
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Specify save path");
                        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("All Files", "*");
                        fileChooser.getExtensionFilters().add(extensionFilter);
                        fileChooser.setInitialFileName(packedResource.getName());
                        File file = fileChooser.showSaveDialog(Client.primaryStage);
                        if (file != null) {
                            OutputStream out = new FileOutputStream(file);
                            out.write(packedResource.getData());
                            out.flush();
                            out.close();
                        }
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
                    filesBox.getItems().clear();
                    filesBox.getItems().addAll(resources);
                    statusCodeLabel.setText(Integer.toString(response.statusCode()));
                }
                break;
            }
            case "POST": {
                if (localFileField.getText().isEmpty()) {
                    return;
                }
                File toPost = new File(localFileField.getText());
                HttpResponse<?> response = clientService.postResource(
                        urlField.getText().concat("?").concat("name=" + toPost.getName().replaceAll("\\s+", ""))
                                .concat((remoteStorePathField.getText().isEmpty())
                                ? "&".concat("storePath=") + remoteStorePathField.getText() : ""),
                        toPost);
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose file to store");
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("All Files", "*");
        fileChooser.getExtensionFilters().add(extensionFilter);
        File file = fileChooser.showOpenDialog(Client.primaryStage);
        if (file != null) {
            localFileField.setText(file.getPath());
        }
    }

}