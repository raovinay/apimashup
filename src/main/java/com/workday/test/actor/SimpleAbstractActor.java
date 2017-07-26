package com.workday.test.actor;

import akka.actor.AbstractLoggingActor;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raovinay on 25-07-2017.
 */
public abstract class SimpleAbstractActor extends AbstractLoggingActor {
    protected List<ActorRef> downstream;
    protected SimpleAbstractActor(){
    }
    protected SimpleAbstractActor(ActorRef... downstream){
        this.downstream=new ArrayList<>();
        for(ActorRef actor:downstream){
            this.downstream.add(actor);
        }
    }
    protected void tellDownstreamActors(Object message, ActorRef sender){
        if(downstream!=null) {
            for (ActorRef actor : downstream) {
                actor.tell(message, sender);
            }
        }
    }
}
