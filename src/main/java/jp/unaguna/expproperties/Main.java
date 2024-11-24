package jp.unaguna.expproperties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {
    private static final String KEY_URL = "my.url";
    private static final String KEY_TLS = "my.tls";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        final List<String> argList = Arrays.asList(args);
        final boolean dump = argList.contains("dump");

        for (String key : propKeys(dump)) {
            printProp(key);
        }

        printSecurity("security.overridePropertiesFile");
        printSecurity("jdk.tls.disabledAlgorithms");
        printSecurity("jdk.xml.dsig.secureValidationPolicy");

        if (System.getProperties().containsKey(KEY_URL)) {
            connect(System.getProperty(KEY_URL));
        }
    }

    static List<String> propKeys(boolean dump) {
        List<String> keys = new ArrayList<>();

        if (dump) {
            for (Object key : Collections.list(System.getProperties().propertyNames())) {
                keys.add((String) key);
            }
        }

        if (!keys.contains("java.security.properties")) {
            keys.add("java.security.properties");
        }
        if (!keys.contains(KEY_URL)) {
            keys.add(KEY_URL);
        }
        if (!keys.contains(KEY_TLS)) {
            keys.add(KEY_TLS);
        }
        return keys;
    }

    static void printProp(final String key) {
        System.out.println("(Prop    ) " + key + " = " + System.getProperty(key, "none"));
    }

    static void printSecurity(final String key) {
        System.out.println("(Security) " + key + " = " + Security.getProperty(key));
    }

    static void connect(String urlStr) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        SSLContext ssl = SSLContext.getInstance(System.getProperty(KEY_TLS, "TLSv1"));
        ssl.init(null, null, new SecureRandom());
        URL url = new URL(urlStr);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setSSLSocketFactory(ssl.getSocketFactory());

        Object content = connection.getContent();
        System.out.println("content: " + content.toString());
    }
}
