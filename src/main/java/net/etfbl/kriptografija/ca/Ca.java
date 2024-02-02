package net.etfbl.kriptografija.ca;

import org.bouncycastle.x509.X509V2CRLGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

public class Ca {
    private static final String caDirectory = "src\\main\\resources\\net\\etfbl\\kriptografija\\CA";
    private static final String caCertificateFileName = caDirectory + File.separator + "ca.cer";
    private static final String caPrivateKeyFileName = caDirectory + File.separator + "ca-private.key";
    private static final String caPublicKeyFileName = caDirectory + File.separator + "ca-public.key";
    private static final String crlFileName = caDirectory + File.separator + "crl.crl";
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private X509Certificate caCertificate;
    public Ca(){
        File dir = new File(caDirectory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File caCertificateFile = new File(caCertificateFileName);
        if(!caCertificateFile.exists()){
            try {
                generateRSAKeys();
                this.caCertificate = generateCertificate("CA", this.publicKey, caCertificateFileName);
                generateCRL();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            readFromFiles();
        }

    }



    private void generateCRL() throws Exception{
        X509V2CRLGenerator crlGen = new X509V2CRLGenerator();
        crlGen.setIssuerDN(caCertificate.getSubjectX500Principal());
        crlGen.setThisUpdate(new Date());


        crlGen.setSignatureAlgorithm("SHA256WithRSAEncryption");
        crlGen.setIssuerDN(caCertificate.getSubjectX500Principal());
        crlGen.setThisUpdate(new Date());
        crlGen.setNextUpdate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 7));
        X509CRL crl = crlGen.generate(privateKey, "BC");

        writeToFile(crlFileName, crl.getEncoded());
    }

    private void generateRSAKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(4096);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        this.privateKey = privateKey;
        PublicKey publicKey = keyPair.getPublic();
        this.publicKey = publicKey;

        writeToFile(caPrivateKeyFileName, privateKey.getEncoded());
        writeToFile(caPublicKeyFileName, publicKey.getEncoded());
    }

    public X509Certificate generateCertificate(String username, PublicKey publicKey, String fileName)
            throws Exception {

        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
        SecureRandom secureRandom = new SecureRandom();
        BigInteger serialNumber = new BigInteger(64, secureRandom).abs();

        certGen.setSerialNumber(serialNumber);
        certGen.setIssuerDN(new X500Principal("CN=" + "CA"));
        certGen.setSubjectDN(new X500Principal("CN=" + username));
        certGen.setPublicKey(publicKey);
        certGen.setNotBefore(new Date(System.currentTimeMillis()));
        certGen.setNotAfter(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 10));

        certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

        X509Certificate certificate = certGen.generate(this.privateKey, "BC");
        writeToFile(fileName, certificate.getEncoded());
        return certificate;
        
    }

    private static void writeToFile(String fileName, byte[] keyBytes) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(keyBytes);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void readFromFiles() {
        try {
            this.privateKey = readPrivateKeyFromFile(caPrivateKeyFileName);
            this.publicKey = readPublicKeyFromFile(caPublicKeyFileName);
            this.caCertificate = readCertificateFromFile(caCertificateFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private PrivateKey readPrivateKeyFromFile(String filePath) throws Exception {
        KeyFactory keyFactory = null;
        PKCS8EncodedKeySpec keySpec = null;
        try {
            byte[] keyBytes = Files.readAllBytes(Path.of(filePath));

            // Učitavanje privatnog ključa iz bajtova
            keyFactory = KeyFactory.getInstance("RSA");
            keySpec = new PKCS8EncodedKeySpec(keyBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyFactory.generatePrivate(keySpec);
    }
    private PublicKey readPublicKeyFromFile(String filePath) throws Exception {
        KeyFactory keyFactory = null;
        X509EncodedKeySpec keySpec = null;
        try {
            byte[] keyBytes = Files.readAllBytes(Path.of(filePath));

            // Učitavanje privatnog ključa iz bajtova
            keyFactory = KeyFactory.getInstance("RSA");
            keySpec = new X509EncodedKeySpec(keyBytes);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyFactory.generatePublic(keySpec);
    }
    private X509Certificate readCertificateFromFile(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
