//package org.example.server.proxy;
//
//import lombok.extern.slf4j.Slf4j;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.math.BigInteger;
//import java.security.*;
//import java.security.cert.X509Certificate;
//import java.util.Date;
//
//import sun.security.x509.*;
//
//@Slf4j
//public class CertificateGenerator {
//
//    /**
//     * 生成自签名根证书
//     * 注意：在实际应用中，这需要使用适当的加密库来生成真正的证书
//     * 这里只是一个示例说明
//     */
//    public static void generateRootCertificate() {
//        log.info("===== Root Certificate Information =====");
//        log.info("To use this org.example.server.proxy, you need to install a root certificate on your device.");
//        log.info("In a real implementation, this would generate an actual X.509 certificate.");
//        log.info("");
//        log.info("Example certificate details:");
//        log.info("Subject: CN=AppAgent Proxy CA, OU=AppAgent, O=Example Corp, L=San Francisco, ST=California, C=US");
//        log.info("Issuer: CN=AppAgent Proxy CA, OU=AppAgent, O=Example Corp, L=San Francisco, ST=California, C=US");
//        log.info("Serial Number: {}", new BigInteger(64, new SecureRandom()).toString(16));
//        log.info("Valid From: {}", new Date());
//        log.info("Valid To: {}", new Date(System.currentTimeMillis() + 365L * 24 * 60 * 60 * 1000));
//        log.info("Signature Algorithm: SHA256withRSA");
//        log.info("");
//        log.info("To install the certificate on devices:");
//        log.info("1. Android: Settings -> Security -> Install from storage -> Select certificate file");
//        log.info("2. iOS: Email the certificate to yourself -> Open on device -> Install -> Trust in Settings");
//        log.info("========================================");
//
//        // 在实际应用中，这里会生成真正的证书文件
//        generateSampleCertificateFile();
//    }
//
//    private static void generateSampleCertificateFile() {
//        String certContent = "-----BEGIN CERTIFICATE-----\n" +
//                "MIIDXTCCAkWgAwIBAgIJAKoK/hpZcrDlMA0GCSqGSIb3DQEBCwUAMEUxCzAJBgNV\n" +
//                "BAYTAkFVMRMwEQYDVQQIDApTb21lLVN0YXRlMSEwHwYDVQQKDBhJbnRlcm5ldCBX\n" +
//                "aWRnaXRzIFB0eSBMdGQwHhcNMTkwNTIyMjE1MzIyWhcNMjAwNTIxMjE1MzIyWjBF\n" +
//                "MQswCQYDVQQGEwJBVTETMBEGA1UdCAwKU29tZS1TdGF0ZTEhMB8GA1UECgwYSW50\n" +
//                "ZXJuZXQgV2lkZ2l0cyBQdHkgTHRkMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIB\n" +
//                "CgKCAQEAuIHE5DmVpV5fFQGN45x3bCNRfYdG3Nqfv5XjhfPHqF6yZmWJ85f6Nzkd\n" +
//                "Jq9nccYaEU980XfqF9XpMxD1r5pfQ76QzS5XsGQ3vDuv5oZe4G74YOLdGSevjGvF\n" +
//                "38NjcxuxH8NqRyfF9xGY5MD6yDImbjjF0dxJpHgZne7R5HjLhExd9ibXqZtWbIjx\n" +
//                "3zqTkwlZH1pTGA8sdR0p37YxP3A2YsdoO99VWt2Tl9uU1l9R3qQ1Wb1bVTrRJvcR\n" +
//                "9mK6LxeRnLCCXZpK5+XEVgLwqdvPlUQwPwX8rPyQr5O9nqmKo5HlYh0CAwEAAaNT\n" +
//                "MFEwHQYDVR0OBBYEFDjui1s1y792pQ1SRJ7Ji2wjfEvkMB8GA1UdIwQYMBaAFDju\n" +
//                "i1s1y792pQ1SRJ7Ji2wjfEvkMA8GA1UdEwEB/wQFMAMBAf8wDQYJKoZIhvcNAQEL\n" +
//                "BQADggEBAHlGsaixcD5eTOKpSjJXZrj0iC9VtkMp40Y8F9o3G955QWdVYZOjHh9H\n" +
//                "8WzON3X/SjXZqJj9H3g01D7Ml3qZqF9Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39D\n" +
//                "j39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39D\n" +
//                "j39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39D\n" +
//                "j39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39Dj39D\n" +
//                "-----END CERTIFICATE-----\n";
//
//        try (FileWriter writer = new FileWriter("appagent-org.example.server.proxy-ca.crt")) {
//            writer.write(certContent);
//            log.info("Sample certificate written to appagent-org.example.server.proxy-ca.crt");
//        } catch (IOException e) {
//            log.error("Failed to write certificate file: {}", e.getMessage());
//        }
//    }
//
//
//    public static void main(String[] args) {
//        generateRootCertificate();
//    }
//}