package com.redhat.ecs;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import javax.net.ssl.*;
import java.net.URL;
import java.security.cert.X509Certificate;

public class App 
{
    public static void main( final String[] args )
    {
        if (args.length < 3) {
            System.out.println("Need to supply XML-RPC endpoint, username and password.");
            return;
        }
        try {
            // trust all ssl certs. See https://ws.apache.org/xmlrpc/ssl.html

            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                            // Trust always
                        }

                        public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                            // Trust always
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sc = SSLContext.getInstance("SSL");
            // Create empty HostnameVerifier
            final HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(final String arg0, final SSLSession arg1) {
                    return true;
                }
            };

            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);

            // Call the XML-RPC service

            final XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(args[0]));
            config.setBasicUserName(args[1]);
            config.setBasicPassword(args[2]);

            final XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);

            final Object[] params = new Object[] {"1376"};
            final String retValue = (String) client.execute("ContentAPI.queryResult", params);

            System.out.println(retValue);
        } catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
}
