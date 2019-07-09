package Quellcode_URL;

import java.io.*;
import java.net.CookieHandler;
import java.net.CookieManager;

public class SourceCodeComperator implements Runnable {
    private HttpURLConnectionTest http;
    private MainFrame _mainFrame;
    private String _username;
    private String _password;
    private EmailSender _emailSender;

    public SourceCodeComperator(){
        http = new HttpURLConnectionTest();
        _emailSender = new EmailSender();
    }

    String url  = "https://a5.fhv.at/?loginReferrer=%2Fde%2Fstudium%2Fnoten.php";
    String actualPage = "https://a5.fhv.at/de/studium/noten.php";


    @Override
    public void run(){
        try {
            //turning on cookies
            CookieHandler.setDefault(new CookieManager());

            //1. Sending a 'GET' request - and fill the form
            String page = http.GetPageContent(url);
            System.out.println(page);
            String FilledForm = http.getFilledForm(page, _username, _password);

            //2. Sending the content with 'POST' request
            http.sendPost(url, FilledForm);

            //3. Richtige Page mit 'GET' holen und beim ersten mal abspeichern
            String fullPage = http.GetPageContent(actualPage);
            String shortenPage = getShortenPage(fullPage);
            safeTempPage(shortenPage);

        }catch(Exception e){
            System.out.println(e.fillInStackTrace());
        }

        while (true){
            try{
                String fullPage = http.GetPageContent(actualPage);
                String shortenPage = getShortenPage(fullPage);
                if(!isPageEqualToLastPage(shortenPage)){
                    //TODO - benachrichtigen
                    _mainFrame.appendAktionText("ÄNDERUNG GESCHEHEN!");
                    _emailSender.sendMail();
                    _mainFrame.appendAktionText("Email sent!");
                }

                safeTempPage(shortenPage);

                //mainFrame Veranschaulichen
                _mainFrame.setQuellCode(fullPage);
                _mainFrame.appendAktionText("page aktualisiert");
                _mainFrame.setPageDesignText(shortenPage);
                Thread.sleep(20000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
                _mainFrame.appendAktionText("Username or Password is wrong, please try again!");
            }

        }
    }

    private void safeTempPage(String actualPage){
        try(PrintWriter os = new PrintWriter(new FileOutputStream("sourceCode.html"))){
            os.write(actualPage);
        }catch (IOException e){
           e.printStackTrace();
        }
    }

    private boolean isPageEqualToLastPage(String actualPage){

        //gespeicherte Page mit neuer page vergleichen
        boolean isEqual = true;
        try(Reader in = new FileReader("sourceCode.html")){

            int index = 0;
            int input;
            while((input = in.read()) != -1 && isEqual == true){
                if(index < actualPage.length()){
                    if(actualPage.charAt(index) != (char)input){
                        isEqual = false;
                    }
                }else{
                    isEqual = false;
                }
                index++;
            }
            if(index != actualPage.length()) {
                isEqual = false;
            }
            System.out.println(index);
        }catch (IOException e){
            e.printStackTrace();
        }
        return isEqual;
    }

    private String getShortenPage(String page){
        String shortPage = page.substring(65570,78847);
        return shortPage;
    }

    public void setFrame(MainFrame frame){
        _mainFrame = frame;
    }
    public void setUsernameAndPassword(String username,String password){
        _username =username;
        _password = password;
    }
}
