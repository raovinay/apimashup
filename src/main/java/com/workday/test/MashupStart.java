package com.workday.test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.workday.test.actor.MashupSupervisor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by raovinay on 23-07-2017.
 */
public class MashupStart {
    private static final String RESOURCE_PROPERTIES = "resource.properties";
    public static void main(String[] args) throws Exception {
        String query="reactive";
        if(args.length>0){
            query=args[0];
        }
        setSystemProps();
        final ActorSystem system = ActorSystem.create("Mashup");
        final ActorRef supervisor = system.actorOf(MashupSupervisor.props(), "supervisor");
        supervisor.tell(query, ActorRef.noSender());
        new BufferedReader(new InputStreamReader(System.in)).read();
        system.terminate();
    }

    /**
     * Iterate through properties.
     * Any property that starts with system. will be set into the System properties.
     */
    private static void setSystemProps() {
        Properties prop = new Properties();
        try {
            prop.load(MashupStart.class.getClassLoader().getResourceAsStream(RESOURCE_PROPERTIES));
            Enumeration e = prop.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                if(key.startsWith("system.")){
                    System.setProperty(key.replace("system.", ""), prop.getProperty(key));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
