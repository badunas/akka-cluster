package com.badun.akkaclusterdemo.manager;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.FromConfig;
import com.badun.akkaclusterdemo.message.Msg;
import com.badun.akkaclusterdemo.util.Selector;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by Artsiom Badun.
 */
public class ManagerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private int taskNumber = 1000;

    ActorRef workerClusterRouter = getContext().actorOf(
            FromConfig.getInstance().props(),   // properties;
            "workerClusterRouter");             // actor name;

    @Override
    public void preStart() {
        context().setReceiveTimeout(Duration.create(5, TimeUnit.SECONDS));
    }

    public ManagerActor() {
        receive(ReceiveBuilder
                .match(ReceiveTimeout.class, this::handleTimeoutMessage)
                .match(Msg.WorkDone.class, this::handleWorkerMessage)
                .matchAny(this::unhandled)
                .build());
        log.debug("Created manager actor.");
    }

    private void handleTimeoutMessage(ReceiveTimeout timeout) {
        workerClusterRouter.tell(buildWorkMessage(), self());
        log.info("[MANAGER] Manager send a peace of work to worker after timeout.");
    }

    private void handleWorkerMessage(Msg.WorkDone message) {
        workerClusterRouter.tell(buildWorkMessage(), self());
        log.info("[MANAGER] Manager send a peace of work to worker by worker request.");
    }

    private Msg.PieceOfWork buildWorkMessage() {
        return new Msg.PieceOfWork("Task: " + self().path() + "_" + taskNumber++);
    }
}
