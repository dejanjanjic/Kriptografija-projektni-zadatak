package net.etfbl.kriptografija;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import net.etfbl.kriptografija.user.User;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class RegistrationWindowController {
    
    private String usersFileName = "src\\main\\resources\\net\\etfbl\\kriptografija\\users.txt";
    
    @FXML
    private TextField certificateField;

    

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button registerButton;

    @FXML
    private TextField rsaPrivateKeyField;

    @FXML
    private TextField rsaPublicKeyField;

    @FXML
    private TextField usernameField;

    @FXML
    private void initialize(){
        certificateField.setEditable(false);
        rsaPrivateKeyField.setEditable(false);
        rsaPublicKeyField.setEditable(false);
    }

    @FXML
    void register(MouseEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String hashedPassword = "";
        try {
            hashedPassword = hashPassword(password);
        } catch (NoSuchProviderException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        User user = new User(username, hashedPassword);
        boolean succesfulRegistration = tryRegister(user);
        if(!succesfulRegistration){
            showAlert("Registration Error", "Username already exists. Please choose a different username.");
        }

    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String hashPassword(String password) throws NoSuchProviderException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256", "BC");
        byte[] hashedBytes = digest.digest(password.getBytes());

        // Konverzija bajtova u heksadecimalni format
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hashedBytes) {
            stringBuilder.append(String.format("%02x", b));
        }

        return stringBuilder.toString();
    }
    
    private boolean tryRegister(User user) {
        File usersFile = new File(usersFileName);
        StringBuilder certificateFileName = new StringBuilder(), privateKeyFileName = new StringBuilder(), publicKeyFileName = new StringBuilder();
        if(usersFile.exists()){
            boolean userAlreadyExist = checkIfExist(user, usersFile);
            if(userAlreadyExist){
                return false;
            }
            else{
                //u ovom slucaju uspjesna registracija
                writeToFile(user, usersFile, true);
                try {
                    user.register(certificateFileName, privateKeyFileName, publicKeyFileName);

                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            //u ovom slucaju uspjesna registracija
            writeToFile(user, usersFile, false); //prvi korisnik pa kreira novi fajl
            try {
                user.register(certificateFileName, privateKeyFileName, publicKeyFileName);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        certificateField.setText(certificateFileName.toString());
        rsaPrivateKeyField.setText(privateKeyFileName.toString());
        rsaPublicKeyField.setText(publicKeyFileName.toString());
        return true;
    }

    private boolean checkIfExist(User user, File usersFile) {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(usersFile))){
            String line = "";
            String[] data;
            while((line = bufferedReader.readLine()) != null){
                data = line.split("#");
                if(user.getUsername().equals(data[0])){
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void writeToFile(User user, File usersFile, boolean append){
        try(PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(usersFile, append)))){
            printWriter.println(user.getUsername() + "#" + user.getHashedPassword());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
