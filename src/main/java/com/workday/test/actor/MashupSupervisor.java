package com.workday.test.actor;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import com.workday.test.exceptions.ResumeException;
import com.workday.test.exceptions.RetryException;
import com.workday.test.exceptions.StopException;
import com.workday.test.service.GitService;
import com.workday.test.service.TwitterService;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.*;

/**
 * Created by raovinay on 25-07-2017.
 */
public class MashupSupervisor extends AbstractLoggingActor {
    final ActorRef loggerActor = context().actorOf(LoggerActor.props(), "loggerActor");
    final ActorRef twitterActor = context().actorOf(TwitterActor.props(new TwitterService(), loggerActor), "twitterActor");
    final ActorRef githubActor = context().actorOf(GithubActor.props(new GitService(), twitterActor), "githubActor");

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(String.class, query->{
            githubActor.tell(new GithubActor.Start(query), ActorRef.noSender());
        }).build();
    }
    //strategy
    //one-for-one strategy. Simple restart. Default 10 restarts.
    private static SupervisorStrategy strategy = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            match(ResumeException.class, e -> resume()).
            match(RetryException.class, e -> restart()).
            match(StopException.class, e -> stop()).
            matchAny(o -> escalate()).build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }

    public static Props props(){
        return Props.create(MashupSupervisor.class);
    }
}
