package com.badun.akkaclusterdemo.worker;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import com.badun.akkaclusterdemo.manager.ManagerActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by Artsiom Badun.
 */
public class WorkerApplication {

    public static void main(String[] args) {
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.port=2554")
                .withFallback(ConfigFactory.parseString("akka.cluster.roles = [worker]"))
                .withFallback(ConfigFactory.load("cluster"));
        ActorSystem system = ActorSystem.create("ClusterSystem", config);
        system.actorOf(Props.create(WorkerActor.class), "worker");
    }
}
