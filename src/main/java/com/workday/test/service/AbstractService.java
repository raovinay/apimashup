package com.workday.test.service;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Created by raovinay on 27-07-2017.
 */
public abstract class AbstractService {
    public static final int HTTP_OK = 200;
    protected static final String GITHUB_API = "https://api.github.com";
    protected static final String SEARCH_API = "search/repositories";
    public Client getClient() {
        return ClientBuilder.newClient();
    }
}
