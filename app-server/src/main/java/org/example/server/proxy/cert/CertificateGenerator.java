package org.example.server.proxy.cert;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.StringWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

@Slf4j
public class CertificateGenerator {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static class CertAndKey {
        public final X509Certificate cert;
        public final PrivateKey key;

        public CertAndKey(X509Certificate cert, PrivateKey key) {
            this.cert = cert;
            this.key = key;
        }
    }

    public static CertAndKey generateCertForHost(String host, X509Certificate caCert, PrivateKey caKey) throws Exception {
        // 生成 RSA 密钥对
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
        kpGen.initialize(2048, new SecureRandom());
        KeyPair keyPair = kpGen.generateKeyPair();

        // 主体信息
        X500Name issuer = new X500Name(caCert.getSubjectX500Principal().getName());
        X500Name subject = new X500Name("CN=" + host);

        // 有效期
        Date notBefore = new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000L);
        Date notAfter = new Date(System.currentTimeMillis() + 365 * 24 * 60 * 60 * 1000L);

        BigInteger serial = new BigInteger(64, new SecureRandom());

        // 证书构建器
        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                issuer,
                serial,
                notBefore,
                notAfter,
                subject,
                keyPair.getPublic()
        );

        // 添加扩展（Subject Alternative Name）
        GeneralName[] subjectAltNames = new GeneralName[]{
                new GeneralName(GeneralName.dNSName, host)
        };
        certBuilder.addExtension(
                Extension.subjectAlternativeName,
                false,
                new GeneralNames(subjectAltNames)
        );

        certBuilder.addExtension(
                Extension.keyUsage,
                true,
                new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment)
        );

        certBuilder.addExtension(
                Extension.extendedKeyUsage,
                false,
                new ExtendedKeyUsage(KeyPurposeId.id_kp_serverAuth)
        );

        certBuilder.addExtension(
                Extension.basicConstraints,
                true,
                new BasicConstraints(false) // 非CA
        );

        JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

        certBuilder.addExtension(
                Extension.subjectKeyIdentifier,
                false,
                extUtils.createSubjectKeyIdentifier(keyPair.getPublic())
        );

        certBuilder.addExtension(
                Extension.authorityKeyIdentifier,
                false,
                extUtils.createAuthorityKeyIdentifier(caCert.getPublicKey())
        );

        // 签名生成器
        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption")
                .setProvider("BC")
                .build(caKey);

        // 生成证书
        X509CertificateHolder holder = certBuilder.build(signer);
        X509Certificate cert = new JcaX509CertificateConverter()
                .setProvider("BC")
                .getCertificate(holder);

        // 验证一下（可选）
        cert.verify(caCert.getPublicKey(), "BC");

        return new CertAndKey(cert, keyPair.getPrivate());
    }
}