package net.etfbl.kriptografija;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import net.etfbl.kriptografija.algoritmi.Algoritam;

public class AlgorithmWindowController {

    ObservableList<String> algorithmList = FXCollections.observableArrayList("Rail fence", "Myszkowski", "Play fair");

    @FXML
    private ChoiceBox<String> algorithmPicker;
    @FXML
    private TextField cipherTextBox;

    @FXML
    private Button encryptButton;

    @FXML
    private TextField keyTextBox;

    @FXML
    private TextField textTextBox;
    @FXML
    private void initialize(){
        cipherTextBox.setEditable(false);

        algorithmPicker.setItems(algorithmList);
        algorithmPicker.setValue("Choose an algorithm");
    }

    @FXML
    private void encrypt(){
        String text = textTextBox.getText();
        String key = keyTextBox.getText();
        String algorithm = algorithmPicker.getValue();
        String cipher;

        switch (algorithm){
            case "Rail fence":
                cipher = Algoritam.railFence(text, key);
                cipherTextBox.setText(cipher);
                break;
            case "Myszkowski":
                cipher = Algoritam.myszkowski(text, key);
                cipherTextBox.setText(cipher);
                break;
            case "Play fair":
                cipher = Algoritam.playFair(text, key);
                cipherTextBox.setText(cipher);
                break;
            default:
                cipherTextBox.setText("CHOOSE ALGORITHM PLEASE!!!");
        }
    }


}