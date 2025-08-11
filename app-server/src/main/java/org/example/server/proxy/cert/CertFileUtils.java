package org.example.server.proxy.cert;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertFileUtils {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static CertificateGenerator.CertAndKey generateRootCA(String commonName) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", "BC");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        long now = System.currentTimeMillis();
        Date notBefore = new Date(now - 1000L * 60);
        Date notAfter = new Date(now + 10L * 365 * 24 * 60 * 60 * 1000); // 有效期10年

        X500Name issuer = new X500Name("CN=" + commonName + ", O=MyCA, C=US");

        BigInteger serial = BigInteger.valueOf(now);
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                notBefore,
                notAfter,
                issuer, // 自签名证书：issuer = subject
                keyPair.getPublic()
        );

// 标记为 CA
        certBuilder.addExtension(
                Extension.basicConstraints,
                true, // critical
                new BasicConstraints(true) // true 表示是 CA
        );

// 使用者密钥标识符（Subject Key Identifier）
        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();
        certBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                extUtils.createSubjectKeyIdentifier(keyPair.getPublic())
        );

// 用途：签发证书、CRL
        certBuilder.addExtension(
                Extension.keyUsage,
                true,
                new KeyUsage(KeyUsage.keyCertSign | KeyUsage.cRLSign)
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .setProvider("BC")
                .build(keyPair.getPrivate());

        X509CertificateHolder holder = certBuilder.build(signer);

        X509Certificate certificate = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(holder);

        certificate.verify(keyPair.getPublic()); // 验证自签名是否成功

        return new CertificateGenerator.CertAndKey(certificate, keyPair.getPrivate());
    }

    // 生成 RSA 自签名证书并写入文件
    public static void generateAndSave(String certPath, String keyPath, String cn) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();

        X500Name subject = new X500Name("CN=" + cn);
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());
        Date notBefore = new Date(System.currentTimeMillis() - 86400000L);
        Date notAfter = new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000);

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                subject, serial, notBefore, notAfter, subject, keyPair.getPublic()
        );

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        X509CertificateHolder holder = certBuilder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);

        // 写入证书文件
        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(certPath))) {
            writer.writeObject(cert);
        }

        // 写入私钥文件
        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(keyPath))) {
            writer.writeObject(keyPair.getPrivate());
        }

        System.out.println("证书和私钥已生成并保存到:");
        System.out.println(" - Cert: " + certPath);
        System.out.println(" - Key:  " + keyPath);
    }

    // 从文件读取证书
    public static X509Certificate loadCert(String certPath) throws Exception {
        try (Reader reader = Files.newBufferedReader(Paths.get(certPath));
             PEMParser parser = new PEMParser(reader)) {
            Object obj = parser.readObject();
            if (obj instanceof X509CertificateHolder holder) {
                // ✅ 获取 issuer
                X500Name issuer = holder.getIssuer();
                System.out.println("Issuer: " + issuer);

                // ✅ 获取 subject
                X500Name subject = holder.getSubject();
                System.out.println("Subject: " + subject);
                return new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
            } else {
                throw new IllegalArgumentException("Invalid certificate file.");
            }
        }
    }

    // 从文件读取私钥
    public static PrivateKey loadPrivateKey(String keyPath) throws Exception {
        try (Reader reader = Files.newBufferedReader(Paths.get(keyPath));
             PEMParser parser = new PEMParser(reader)) {

            Object obj = parser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");

            if (obj instanceof PEMKeyPair pemKeyPair) {
                return converter.getKeyPair(pemKeyPair).getPrivate();
            } else if (obj instanceof PrivateKeyInfo privateKeyInfo) {
                return converter.getPrivateKey(privateKeyInfo);
            } else {
                throw new IllegalArgumentException("Unsupported key format in: " + keyPath);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String certFile = "cert.pem";
        String keyFile = "key.pem";

        // 生成并保存
//        CertFileUtils.generateAndSave(certFile, keyFile, "My Local CA");
        CertificateGenerator.CertAndKey ca = CertFileUtils.generateRootCA("CA202508081226");

        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(certFile))) {
            writer.writeObject(ca.cert);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(keyFile))) {
            writer.writeObject(ca.key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 读取回来
        X509Certificate cert = CertFileUtils.loadCert(certFile);
        PrivateKey key = CertFileUtils.loadPrivateKey(keyFile);

        System.out.println("读取成功：");
        System.out.println("证书主题: " + cert.getSubjectX500Principal());
        System.out.println("私钥算法: " + key.getAlgorithm());
    }

    public static void save(CertificateGenerator.CertAndKey cert, String host) {
        // 写入证书文件
        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(host + "certPath.pem"))) {
            writer.writeObject(cert.cert);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 写入私钥文件
        try (JcaPEMWriter writer = new JcaPEMWriter(new FileWriter(host + "keyPath.pem"))) {
            writer.writeObject(cert.key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
