package ml.dvnlabs.animize.driver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class RequestHandler {
    //Method to send httpPostRequest
    //This method is taking two arguments
    //First argument is the URL of the script to which we will send the request
    //Other is an HashMap with name value pairs containing the data to be send with the request
    public String sendPostRequest(String requestURL,
                                  HashMap<String, String> postDataParams) {
        //Creating a URL
        URL url;

        //StringBuilder object to store the message retrieved from the server
        StringBuilder sb = new StringBuilder();
        try {
            //Initializing Url
            url = new URL(requestURL);
            //System.out.println("PROCESSED URL:"+url);
            //Creating an httmlurl connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            //Configuring connection properties
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //Creating an output stream
            OutputStream os = conn.getOutputStream();

            //Writing parameters to the request
            //We are using a method getPostDataString which is defined below
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            //System.out.println("HTP CODE:"+String.valueOf(responseCode));

            if (responseCode == HttpsURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                sb = new StringBuilder();
                String response;
                //Reading server response
                int ia = 1;
                //System.out.println("A"+br.readLine());
                while ((response = br.readLine()) != null) {
                    sb.append(response);
                    //System.out.println("LINE:"+ia+ response);
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR!!!!!!"+e);
            e.printStackTrace();
        }
        Log.e("DATA:",sb.toString());
        return sb.toString();
    }

    public String sendGetRequest(String requestURL) {
        StringBuilder sb = new StringBuilder();

        try {
            if(connectionCheckisHTTPS(requestURL)){
                System.out.println("HTTPS:");
                URL url = new URL(requestURL);
                HttpsURLConnection con = (HttpsURLConnection) url.openConnection();


                SSLContext ctc = SSLContext.getInstance("TLS");
                ctc.init(new KeyManager[0],new TrustManager[]{new DefaultTrustManager()},new SecureRandom());
                final SSLSocketFactory sslSocketFactory = ctc.getSocketFactory();
                SSLContext.setDefault(ctc);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection)con;
                httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
                //InputStream in = con.getInputStream();
                //System.out.print("AHSJD"+in);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    sb.append(s + "\n");
                }
            }else{
                URL url = new URL(requestURL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String s;
                while ((s = bufferedReader.readLine()) != null) {
                    sb.append(s + "\n");
                }
            }

        } catch (Exception e) {
            System.out.println("ERROR:"+e);
        }
        //Log.e("DATA:",sb.toString());
        return sb.toString();
    }



    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        //System.out.println("PARAMS: "+result.toString());
        return result.toString();
    }

    public static Boolean connectionCheckisHTTPS(String urls){
        if (urls.regionMatches(0, "https", 0, 5)){
            return true;
        }
        else {
            return false;
        }
    }
    private static class DefaultTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}

