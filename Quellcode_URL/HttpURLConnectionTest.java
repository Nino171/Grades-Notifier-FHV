package Quellcode_URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HttpURLConnectionTest {
    private List<String> _cookies;
    private HttpsURLConnection conn;

    private final String USER_AGENT = "Mozilla/5.0";


    public String GetPageContent(String url) throws Exception{

        //verbindung aufbauen
        URL urlObj = new URL(url);
        conn =  (HttpsURLConnection) urlObj.openConnection();

        conn.setRequestMethod("GET"); //default wäre auch GET
        conn.setUseCaches(false);

        //act like a browser
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language","de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7");


        //response code bekommen
        int responseCode = conn.getResponseCode();
        System.out.println("Sending 'GET' request to "+ url);
        System.out.println("response Code: "+ responseCode);



        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();

        // Get the response cookies -> Cookies vom server bekommen und abspeichern
        setCookies(conn.getHeaderFields().get("Set-Cookie"));

        return response.toString();
    }



    public String getFilledForm(String html, String username, String password) throws UnsupportedEncodingException {
        System.out.println("Changing Form with params...");

        //html code in ein document "parsen"
        Document doc = Jsoup.parse(html);

        Element loginform = doc.getElementById("dialog-login-form");
        Elements inputElements = loginform.getElementsByTag("input");
        List<String> paramList = new ArrayList<String>();


        //Form Data Richtig bearbeiten
        for(Element inputElement : inputElements){
            String key = inputElement.attr("name");
            String value = inputElement.attr("value");

            if(key.equals("user")){
                value = username;
            }else if(key.equals("password")){
                value = password;
            }
            paramList.add(key+ "=" + URLEncoder.encode(value,"UTF-8"));

        }

        //für übermittlung alle params mit einem & verknüpfen
        StringBuilder result = new StringBuilder();
        for(String param : paramList){
            if(result.length() == 0){
                result.append(param);
            }else{
                result.append("&" + param);
            }
        }
        return result.toString();

    }

    public void sendPost(String url, String postParams)throws Exception{

        System.out.println("Sending 'POST' request to :" + url);
        System.out.println("Post parameters: "+ postParams);
        URL urlObj = new URL(url);
        conn = (HttpsURLConnection) urlObj.openConnection();

        //acting like a browser
        conn.setUseCaches(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Host","a5.fhv.at");
        conn.setRequestProperty("User-Agent",USER_AGENT);
        conn.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        conn.setRequestProperty("Accept-Language","de-DE,de;q=0.9,en-US;q=0.8,en;q=0.7");

        for(String cookie: _cookies){
            conn.addRequestProperty("Cookie",cookie);
        }
        conn.setRequestProperty("Connection","keep-alive");
        conn.setRequestProperty("Referer",url);
        conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length",Integer.toString(postParams.length())); //content länge -> abhängig von der ausgefüllten form(länge)

        conn.setDoOutput(true);
        conn.setDoInput(true);


        //Sending post request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(postParams);
        wr.flush();
        wr.close();

        int responseCode = conn.getResponseCode();
        System.out.println("Response Code: "+responseCode);



        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while((inputLine = in.readLine()) != null){
            response.append(inputLine);
        }
        in.close();
        System.out.println("Response in Source: "+ response.toString());


    }



    /*
    private Document convertStringToXMLDocument(String xmlString){

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        DocumentBuilder builder = null;

        try{
            //create önew DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //parse the content to a Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }catch (Exception e){
            System.out.println(e.fillInStackTrace());
        }
        return null;
    }
*/

    public void setCookies(List<String> cookies){
        _cookies = cookies;
    }
}
