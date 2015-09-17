package com.badun.akkaclusterdemo.manager;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ReceiveTimeout;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import akka.routing.FromConfig;
import com.badun.akkaclusterdemo.message.Msg;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by Artsiom Badun.
 */
public class ManagerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private int taskNumber = 1000;

    private Cluster cluster = Cluster.get(getContext().system());
    private ActorRef workerClusterRouter = getContext().actorOf(
            FromConfig.getInstance().props(),   // properties;
            "workerClusterRouter");             // actor name;

    @Override
    public void preStart() {
        cluster.subscribe(self(),
                ClusterEvent.initialStateAsEvents(),
                MemberEvent.class);
        context().setReceiveTimeout(Duration.create(5, TimeUnit.SECONDS));
    }

    public ManagerActor() {
        receive(ReceiveBuilder
                .match(ReceiveTimeout.class, this::handleTimeoutMessage)
                .match(Msg.WorkDone.class, this::handleWorkerMessage)
                .match(MemberUp.class, this::handleMemberUpEvent)
                .match(MemberRemoved.class, this::handleMemberRemovedEvent)
                .match(MemberEvent.class, this::handleMemberEvent)
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

    private void handleMemberUpEvent(MemberUp memberUp) {
        log.info("\n============================================================================================\n" +
                "[MANAGER] New member detected: " + memberUp.member().address() +
                "\n============================================================================================");
    }

    private void handleMemberRemovedEvent(MemberRemoved memberRemoved) {
        log.info("\n============================================================================================\n" +
                "[MANAGER] Member removal detected: " + memberRemoved.member().address() +
                "\n============================================================================================");
    }

    private void handleMemberEvent(MemberEvent memberEvent) {
    }
}
