package com.badun.akkaclusterdemo.manager;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by Artsiom Badun.
 */
public class ManagerApplication {

    public static void main(String[] args) {
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.port=2551")
                .withFallback(ConfigFactory.parseString("akka.cluster.roles = [manager]"))
                .withFallback(ConfigFactory.load("cluster"));
        ActorSystem system = ActorSystem.create("ClusterSystem", config);
        system.actorOf(Props.create(ManagerActor.class), "manager");
    }
}
