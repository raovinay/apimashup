package com.workday.test.service;

import com.workday.test.exceptions.RetryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by raovinay on 25-07-2017.
 */
public class GitService extends AbstractService{
    private static Logger LOGGER = LoggerFactory.getLogger(GitService.class);

    public String getGitSearchResult(String query){
        String data;
        Client client = getClient();
        try {
            LOGGER.info("Getting github projects for search query: {}", query);
            Response response = client.target(GITHUB_API)
                    .path(SEARCH_API)
                    .queryParam("q", query).request(MediaType.APPLICATION_JSON_TYPE).get();
            if (response.getStatus() == HTTP_OK) {
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
}
