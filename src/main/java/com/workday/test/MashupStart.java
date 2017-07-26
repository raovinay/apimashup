package com.workday.test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.workday.test.actor.MashupSupervisor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 * Created by raovinay on 23-07-2017.
 */
public class MashupStart {
    public static void main(String[] args) throws Exception {
        String query="reactive";
        if(args.length>0){
            query=args[0];
        }
        final ActorSystem system = ActorSystem.create("Mashup");
        final ActorRef supervisor = system.actorOf(MashupSupervisor.props(), "supervisor");
        supervisor.tell(query, ActorRef.noSender());
        new BufferedReader(new InputStreamReader(System.in)).read();
        system.terminate();
    }
}
