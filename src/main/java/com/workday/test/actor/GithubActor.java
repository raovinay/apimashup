package com.workday.test.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.jayway.jsonpath.JsonPath;
import com.workday.test.model.GithubData;
import com.workday.test.service.GitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * Created by raovinay on 23-07-2017.
 * This actor will use the git search api for the given query string.
 * For each project that is returned by Github, this will then 'tell' (async) the downstream actors to proceed.
 * The actor itself does not know who the downstream actors are, and will be configured by the supervisor.
 */
public class GithubActor extends SimpleAbstractActor{
    private static Logger LOGGER = LoggerFactory.getLogger(GitService.class);
    private GitService gitService;

    private GithubActor(GitService gitService, ActorRef... downstream){
        super(downstream);
        this.gitService = gitService;
    }

    @Override
    public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
        super.preRestart(reason, message);
        LOGGER.warn("RESTARTING ACTOR: {}, {}", reason, message);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Start.class, x->{
            LOGGER.debug("got message. Github actor doing its thing.");
            //get github data
            List<GithubData> githubData = getGithubData(x.query);
            //for each one, tell downstream to do their jobs.
            LOGGER.debug("Github part done. Telling downstream actors.");
            for(GithubData data:githubData){
                tellDownstreamActors(data, self());
            }
        }).build();
    }

    /**
     * Invoke Github service.
     * @param query
     * @return
     */
    private List<GithubData> getGithubData(String query) {
        List<GithubData> githubData  = jsonToGithubDataObject(gitService.getGitSearchResult(query));
        return githubData;
    }

    /**
     * Convert string to GithubData object. Uses JsonPath library for querying Json.
     * @param json
     * @return
     */
    private List<GithubData> jsonToGithubDataObject(String json){
        List<Map> items = JsonPath.read(json, "$.items");
        List<GithubData> githubData = new ArrayList<>();
        for(Map item:items){
            githubData.add(new GithubData(
                    Long.parseLong(item.get("id").toString()),
                    (String)item.get("name"), (String)item.get("full_name"), (String)item.get("description")));
        }
        return githubData;
    }

    /**
     * This is the trigger message. Use this to start the actor and pass your query string through this.
     */
    public static class Start{
        private String query;
        public Start(String query) {
            this.query = query;
        }
    }

    /**
     * Return the Props object needed by Akka.
     * @param gitService
     * @param twitterActor
     * @return
     */
    public static Props props(GitService gitService, ActorRef twitterActor){
        return Props.create(GithubActor.class, ()->new GithubActor(gitService, twitterActor));
    }
}
