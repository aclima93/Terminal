package pt.uc.student.aclima.device_agent.Publisher;

/**
 * Created by aclima on 01/03/2017.
 * Source: <a>https://gist.github.com/sharonbn/4104301</a>
 */

import android.net.Uri;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class SslUtil {

    static SSLSocketFactory getSocketFactory (final String caCrtRelativePath, final String crtRelativePath, final String keyRelativePath,
                                              final String password) throws Exception {

        final String caCrtFilePath = getFilePath(caCrtRelativePath);
        final String crtFilePath = getFilePath(crtRelativePath);
        final String keyFilePath = getFilePath(keyRelativePath);

        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        PEMReader reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(readAllBytes(caCrtFilePath))));
        X509Certificate caCert = (X509Certificate)reader.readObject();
        reader.close();

        // load client certificate
        reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(readAllBytes(crtFilePath))));
        X509Certificate cert = (X509Certificate)reader.readObject();
        reader.close();

        // load client private key
        reader = new PEMReader(
                new InputStreamReader(new ByteArrayInputStream(readAllBytes(keyFilePath))),
                new PasswordFinder() {
                    @Override
                    public char[] getPassword() {
                        return password.toCharArray();
                    }
                }
        );
        KeyPair key = (KeyPair)reader.readObject();
        reader.close();

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        // client key and certificates are sent to server so it can authenticate us
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", cert);
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }

    private static byte[] readAllBytes(String path){

        File file = new File(path);
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        int numBytesRead = 0;
        BufferedInputStream buf = null;

        try {
            buf = new BufferedInputStream(new FileInputStream(file));
            numBytesRead = buf.read(bytes, 0, bytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (buf != null) {
                    buf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }

    private static String getFilePath(String relativePath){
        Uri uri = Uri.parse("android.resource://" + "pt.uc.student.aclima" + "/" + relativePath);
        return uri.getPath();
    }

}

