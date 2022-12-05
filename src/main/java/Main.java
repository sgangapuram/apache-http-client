import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static String CLIENT_ID = System.getenv("okta_client_id");
    public static String CLIENT_SECRET = System.getenv("okta_client_secret");
    public static String OKTA_TOKEN_URL = "https://domain/oauth2/oktaserver/v1/token";
    public static String CHANGE_TICKET_SEARCH_URI = "https://api-gateway-url/StandardChangeTemplate?NameSearch=";
    public static String APP_NAME = System.getenv("app_name");
    
    public static void main(String args[]) throws IOException {
        String oktaToken = "Bearer "+StringUtils.substringBetween(getOktaToken(),"\"access_token\":\"", "\",\"scope\"");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {

            HttpGet request = new HttpGet(CHANGE_TICKET_SEARCH_URI.concat(APP_NAME));
            request.addHeader("Authorization",oktaToken);
            CloseableHttpResponse response = httpClient.execute(request);
            try {
                // Get HttpResponse Status
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    System.out.println("Get request response here" + result);
                }

            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }

    private static String getOktaToken() throws IOException {
        String result= "";
        HttpPost post = new HttpPost(OKTA_TOKEN_URL);
        post.addHeader("content-type", "application/x-www-form-urlencoded");
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("client_id", CLIENT_ID));
        urlParameters.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
        urlParameters.add(new BasicNameValuePair("grant_type", "client_credentials"));
        urlParameters.add(new BasicNameValuePair("scope", "token-provider-scope"));
        post.setEntity(new UrlEncodedFormEntity(urlParameters));
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)){
            result = EntityUtils.toString(response.getEntity());
        }
        return result;
    }

}
