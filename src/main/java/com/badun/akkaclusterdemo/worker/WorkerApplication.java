package com.badun.akkaclusterdemo.worker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.badun.akkaclusterdemo.message.PieceOfWork;
import com.badun.akkaclusterdemo.worker.WorkerActor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Date;

/**
 * Created by Artsiom Badun.
 */
public class WorkerApplication {

    public static void main(String[] args) {
        Config config = ConfigFactory
                .parseString("akka.remote.netty.tcp.port=" + args[0])
                .withFallback(ConfigFactory.load("worker"));

        ActorSystem system = ActorSystem.create("worker-system", config);

        ActorRef worker = system.actorOf(Props.create(WorkerActor.class), "worker");
        worker.tell(new PieceOfWork("test main", new Date(), "test message"), ActorRef.noSender());
    }
}
