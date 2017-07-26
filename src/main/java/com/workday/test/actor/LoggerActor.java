package com.workday.test.actor;

import akka.actor.Props;
import com.google.gson.Gson;
import com.workday.test.model.MashedData;

/**
 * Created by raovinay on 25-07-2017.
 * Simple Actor that converts the incoming data into JSON format and prints it to System.out console.
 * Nothing too fancy here, but can just as easily write actors to log to other things and plug into the system.
 */
public class LoggerActor extends SimpleAbstractActor{
    Gson gson = new Gson();
    @Override
    public Receive createReceive() {
        return receiveBuilder().match(MashedData.class, x->{
            System.out.println(gson.toJson(x));
        }).build();
    }

    public static Props props() {
        return Props.create(LoggerActor.class);
    }
}
