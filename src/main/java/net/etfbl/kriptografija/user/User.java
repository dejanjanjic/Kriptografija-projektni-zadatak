package net.etfbl.kriptografija.user;

import net.etfbl.kriptografija.ca.Ca;

import java.io.*;
import java.security.*;

public class User {
    private String username;
    private String hashedPassword;
    private PublicKey publicKey;
    public static final String usersDirectory = "src\\main\\resources\\net\\etfbl\\kriptografija\\Users";

    public User(String username, String hashedPassword){
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void register(StringBuilder certificateFileName, StringBuilder privateKeyFileName, StringBuilder publicKeyFileName) throws NoSuchAlgorithmException {
        File dir = new File(usersDirectory);
        if(!dir.exists()){
            dir.mkdir();
        }
        KeyPair keyPair = generateRSAKeys(privateKeyFileName, publicKeyFileName);
        try {
            generateCertificate(certificateFileName, keyPair.getPublic());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateCertificate(StringBuilder certificateFileName, PublicKey publicKey) throws Exception {
        certificateFileName.setLength(0);
        File certFile = new File(usersDirectory + File.separator + username + File.separator + username + "Cert.cer");
        certificateFileName.append(certFile.getAbsolutePath());
        new Ca().generateCertificate(username, publicKey, certificateFileName.toString());
    }

    private KeyPair generateRSAKeys(StringBuilder privateKeyFileName, StringBuilder publicKeyFileName) throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        this.publicKey = publicKey;



        File directoryOfUser = new File(usersDirectory + "\\" + username);
        directoryOfUser.mkdir();

        privateKeyFileName.setLength(0); // Brišemo sadržaj StringBuildera
        privateKeyFileName.append(directoryOfUser.getAbsolutePath()).append("\\private_key.pem");

        publicKeyFileName.setLength(0); // Brišemo sadržaj StringBuildera
        publicKeyFileName.append(directoryOfUser.getAbsolutePath()).append("\\public_key.pem");

        writeKeyToFile(privateKeyFileName.toString(), privateKey.getEncoded()); //upisujemo kljuc u DER formatu
        writeKeyToFile(publicKeyFileName.toString(), publicKey.getEncoded());

        return keyPair;
//  TODO : zastititi simetricnim algoritmom

//        try {
//            encryptFileWithAes(privateKeyFileName.toString());
//            encryptFileWithAes(publicKeyFileName.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

//    private void encryptFileWithAes(String fileName) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
//
//    }

    private static void writeKeyToFile(String fileName, byte[] keyBytes) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(keyBytes);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
