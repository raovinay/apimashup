package com.workday.test.service;

import com.jayway.jsonpath.JsonPath;
import com.workday.test.exceptions.ResumeException;
import com.workday.test.exceptions.RetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * Created by raovinay on 25-07-2017.
 */
public class TwitterService {
    private static Logger LOGGER = LoggerFactory.getLogger(TwitterService.class);
    public static final String TWITTER_API_1_1 = "https://api.twitter.com/1.1";
    public static final String TWITTER_SEARCH_API_PATH = "search/tweets.json";
    public static final String TWITTER_API = "https://api.twitter.com";
    public static final String OAUTH2_PATH = "oauth2/token";

    public String getTwitterSearchResult(String query, String bearerToken){
        String data;
        Client client = ClientBuilder.newClient();
        try {
            LOGGER.debug("Connecting to twitter api to search repo for query string: {}", query);
            Response response = client.target(TWITTER_API_1_1)
                    .path(TWITTER_SEARCH_API_PATH)
                    .queryParam("q", query).request(MediaType.APPLICATION_JSON_TYPE)
                    .header("Authorization", "Bearer " + bearerToken)
                    .get();
            if (response.getStatus() == 200) {
                LOGGER.debug("Successful response from twitter.");
                response.bufferEntity();
                data = response.readEntity(String.class);
            } else {
                LOGGER.warn("Status from twitter is {}. Trying again.", response.getStatus());
                throw new ResumeException();
            }
        }catch (Exception e){
            LOGGER.warn("Exception encountered {}. Resuming.", e);
            throw new ResumeException();
        }
        return data;
    }

    public String getBearerToken(String consumerKey, String consumerSecret) {
        try {
            consumerKey = URLEncoder.encode(consumerKey, java.nio.charset.StandardCharsets.UTF_8.toString());
            consumerSecret = URLEncoder.encode(consumerSecret, java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("ERROR DURING ENCODING CONSUMER KEY AND SECRET. TWITTER API MIGHT NOT WORK. {}", e);
        }
        byte[] encode = Base64.getEncoder().encode((consumerKey + ":" + consumerSecret).getBytes());
        Client client = ClientBuilder.newClient();
        Form postBody = new Form("grant_type", "client_credentials");
        LOGGER.debug("Getting twitter bearer token.");
        Response response = client.target(TWITTER_API)
                .path(OAUTH2_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .header("Authorization","Basic "+new String(encode)).
                post(Entity.form(postBody));
        String token;
        if(response.getStatus()==200){
            LOGGER.debug("GOT bearer token.");
            response.bufferEntity();
            String data = response.readEntity(String.class);
            token = JsonPath.read(data, "$.access_token");
        }else{
            LOGGER.warn("Something wrong in getting bearer token. Retrying.");
            throw new RetryException();
        }
        return token;
    }

    public String getDummy(String d){
        StringBuilder data = new StringBuilder();
        try (BufferedReader read = new BufferedReader(new FileReader("C:\\Users\\raovi\\IdeaProjects\\apimashup\\src\\test\\resources\\TwitterResponse"))){
            String currLine;
            while ((currLine=read.readLine())!=null){
                data.append(currLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }
}
