package com.workday.test;

import com.workday.test.exceptions.ResumeException;
import com.workday.test.exceptions.RetryException;
import com.workday.test.service.GitService;
import com.workday.test.service.TwitterService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by raovinay on 27-07-2017.
 */
public class ServiceTests {
    @Mock
    Client client;
    @Mock
    Invocation.Builder builder;
    @Mock
    WebTarget webTarget;
    @Mock
    Response response;
    @Spy
    @InjectMocks
    TwitterService twitterService;
    @Spy
    @InjectMocks
    GitService gitService;
    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testTwitterServiceSearch(){
        twitterServiceBoilerplate();
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("DUMMY");
        String response = twitterService.getTwitterSearchResult("TEST", "TEST");
        Assert.assertEquals("DUMMY", response);
    }
    @Test(expected = ResumeException.class)
    public void testTwitterServiceSearchException(){
        twitterServiceBoilerplate();
        when(response.getStatus()).thenReturn(500);
        twitterService.getTwitterSearchResult("TEST", "TEST");
    }
    @Test
    public void testTwitterServiceGetBearerToken(){
        twitterServiceBoilerplate();
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("{\"access_token\":\"DUMMY\"}");
        String response = twitterService.getBearerToken("TEST", "TEST");
        Assert.assertEquals("DUMMY", response);
    }

    @Test
    public void testGitServiceSearch(){
        gitServiceBoilerplate();
        when(response.getStatus()).thenReturn(200);
        when(response.readEntity(String.class)).thenReturn("DUMMY");
        String response = gitService.getGitSearchResult("TEST");
        Assert.assertEquals("DUMMY", response);
    }

    @Test(expected = RetryException.class)
    public void testGitServiceSearchException(){
        gitServiceBoilerplate();
        when(response.getStatus()).thenReturn(500);
        gitService.getGitSearchResult("TEST");
    }

    private void twitterServiceBoilerplate() {
        when(twitterService.getClient()).thenReturn(client);
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.path(anyString())).thenReturn(webTarget);
        when(webTarget.queryParam(anyString(), anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builder);
        when(builder.header(anyString(), anyObject())).thenReturn(builder);
        when(builder.get()).thenReturn(response);
        when(builder.post(anyObject())).thenReturn(response);
        when(twitterService.getWebClientBuilder(Mockito.anyString(), Mockito.anyString())).
                thenReturn(builder);
    }
    private void gitServiceBoilerplate() {
        when(gitService.getClient()).thenReturn(client);
        when(client.target(anyString())).thenReturn(webTarget);
        when(webTarget.path(anyString())).thenReturn(webTarget);
        when(webTarget.queryParam(anyString(), anyString())).thenReturn(webTarget);
        when(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).thenReturn(builder);
        when(builder.header(anyString(), anyObject())).thenReturn(builder);
        when(builder.get()).thenReturn(response);
        when(builder.post(anyObject())).thenReturn(response);
        }
}
