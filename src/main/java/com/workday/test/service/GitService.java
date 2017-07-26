package com.workday.test.service;

import com.workday.test.exceptions.ResumeException;
import com.workday.test.exceptions.RetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by raovinay on 25-07-2017.
 */
public class GitService {
    private static Logger LOGGER = LoggerFactory.getLogger(GitService.class);
    public static final String GITHUB_API = "https://api.github.com";
    public static final String SEARCH_API = "search/repositories";

    public String getGitSearchResult(String query){
        String data= null;
        Client client = ClientBuilder.newClient();
        try {
            LOGGER.info("Getting github projects for search query: {}", query);
            Response response = client.target(GITHUB_API)
                    .path(SEARCH_API)
                    .queryParam("q", query).request(MediaType.APPLICATION_JSON_TYPE).get();
            if (response.getStatus() == 200) {
                LOGGER.debug("Successfully got github response.");
                response.bufferEntity();
                data = response.readEntity(String.class);
            } else {
                LOGGER.warn("Something wrong in github response. Retrying.");
                throw new RetryException();
            }
        }catch (Exception e){
            LOGGER.warn("Exception while getting github response: {} Retrying.", e);
            throw new RetryException();
        }
        return data;
    }

    public String getDummy(String d){
        String data = null;
        try (BufferedReader read = new BufferedReader(new FileReader("C:\\Users\\raovi\\IdeaProjects\\apimashup\\src\\test\\resources\\GitResponse"))){
            data = read.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
