package com.workday.test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import com.workday.test.actor.GithubActor;
import com.workday.test.actor.GithubActor.Start;
import com.workday.test.actor.LoggerActor;
import com.workday.test.actor.TwitterActor;
import com.workday.test.model.GithubData;
import com.workday.test.model.MashedData;
import com.workday.test.service.GitService;
import com.workday.test.service.TwitterService;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Created by cc32844 on 27.07.2017.
 */
public class ActorTests {
    static ActorSystem system;
    @Mock
    private TwitterService twitterService;
    @Mock
    private GitService gitService;
    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }
    @Before
    public void initMocks(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testLoggerActor() {
        TestKit testKit = new TestKit(system);
        Props props = LoggerActor.props();
        ActorRef loggerActor = system.actorOf(props);

        loggerActor.tell(new MashedData(null, null), testKit.getRef());
        testKit.expectNoMsg();
    }

    @Test
    public void testTwitterActor(){
        Mockito.when(twitterService.getBearerToken(Mockito.anyString(), Mockito.anyString())).thenReturn("SampleBearerToken");
        Mockito.when(twitterService.getTwitterSearchResult(Mockito.anyString(), Mockito.anyString())).thenReturn(getDummyTwitterData());
        TestKit testKit = new TestKit(system);
        Props props = TwitterActor.props(twitterService, testKit.getRef());
        ActorRef twitterActor = system.actorOf(props);
        twitterActor.tell(new GithubData(1l, "test", "test", "test"), ActorRef.noSender());
        testKit.expectMsgClass(MashedData.class);
    }

    @Test
    public void testGithubActor(){
        Mockito.when(gitService.getGitSearchResult("Query")).thenReturn(getDummyGithubData());
        TestKit testKit = new TestKit(system);
        Props props = GithubActor.props(gitService, testKit.getRef());
        ActorRef gitActor = system.actorOf(props);
        gitActor.tell(new Start("Query"), testKit.getRef());
        testKit.expectMsgClass(GithubData.class);
        testKit.receiveN(29);
    }

    private String getDummyGithubData() {
        StringBuilder data = new StringBuilder();
        try (BufferedReader read = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("GitResponse")))){
            String currLine;
            while ((currLine=read.readLine())!=null){
                data.append(currLine);
            }        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    private String getDummyTwitterData() {
        StringBuilder data = new StringBuilder();
        try (BufferedReader read = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("TwitterResponse")))){
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
