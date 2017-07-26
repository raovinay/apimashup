package com.workday.test.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.jayway.jsonpath.JsonPath;
import com.workday.test.model.GithubData;
import com.workday.test.model.MashedData;
import com.workday.test.model.TwitterData;
import com.workday.test.service.GitService;
import com.workday.test.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by raovinay on 23-07-2017.
 * This actor will use the twitter search api for the given search string.
 * For each tweet data that is returned by Twitter, this will then 'tell' (async) the downstream actors to proceed.
 * The actor itself does not know who the downstream actors are, and will be configured by the supervisor.
 */
public class TwitterActor extends SimpleAbstractActor{
    public static final String RESOURCE_PROPERTIES = "resource.properties";
    private static Logger LOGGER = LoggerFactory.getLogger(TwitterActor.class);
    private int RATE_LIMIT;
    private String TWITTER_BEARER_TOKEN;
    private TwitterService twitterService;
    private Properties prop;

    private TwitterActor(TwitterService twitterService, ActorRef... downstream){
        super(downstream);
        this.twitterService=twitterService;
        //get consumer secret and key from properties.
        prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream(RESOURCE_PROPERTIES));
        } catch (IOException e) {
            LOGGER.error("Error getting properties. Will exit.");
            throw new RuntimeException(e);
        }
    }

    /**
     * Set the Rate limit here.
     * Set the twitter search API bearer token here.
     * @throws Exception
     */
    @Override
    public void preStart() throws Exception {
        super.preStart();
        LOGGER.debug("Starting twitter actor. Setting rate limit and getting bearer token.");
        //reset on restart.
        RATE_LIMIT=10;
        //create twitter bearer token.
        TWITTER_BEARER_TOKEN = twitterService.getBearerToken(prop.getProperty("twitter.consumer.key"),prop.getProperty("twitter.consumer.secret"));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(GithubData.class, x->{
            LOGGER.debug("Got request. Getting twitter data.");
            //get twitter data
            List<TwitterData> tweets = getTwitterData(x.getProjectFullName());
            //create mashed data class
            MashedData mashedData = new MashedData(x, tweets);
            //tell downstream
            LOGGER.debug("Request done. Telling downstream.");
            tellDownstreamActors(mashedData, self());
        }).build();
    }

    /**
     * Invoke Twitter search API here.
     * @param projectFullName
     * @return data
     */
    private List<TwitterData> getTwitterData(String projectFullName) {
        List<TwitterData> data = new ArrayList<>();
        if(RATE_LIMIT-- > 0) {
            data = jsonToTwitterData(twitterService.getTwitterSearchResult(projectFullName, TWITTER_BEARER_TOKEN));
        }
        return data;
    }

    /**
     * Convert JSON string returned from Twitter search API into TwitterData object.
     * @param json
     * @return tweetData
     */
    private List<TwitterData> jsonToTwitterData(String json) {
        List<Map> tweets = JsonPath.read(json, "$.statuses");
        List<TwitterData> tweetData = new ArrayList<>();
        for(Map tweet:tweets){
            tweetData.add(new TwitterData((String) tweet.get("text")));
        }
        return tweetData;
    }

    /**
     * Props object for the actor. This is needed by Akka.
     * @param twitterService
     * @param loggerActor
     * @return
     */
    public static Props props(TwitterService twitterService, ActorRef loggerActor){
        return Props.create(TwitterActor.class, ()->new TwitterActor(twitterService, loggerActor));
    }
}
