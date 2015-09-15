package com.badun.akkaclusterdemo.manager;

import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.badun.akkaclusterdemo.message.ManagerTimeout;
import com.badun.akkaclusterdemo.message.PieceOfWork;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * Created by Artsiom Badun.
 */
public class ManagerActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    private final Cluster cluster = Cluster.get(getContext().system());
    private final Cancellable timeoutScheduler = getContext().system().scheduler().schedule(
            Duration.create(5, TimeUnit.SECONDS),
            Duration.create(5, TimeUnit.SECONDS),
            getSelf(),
            new ManagerTimeout(),
            getContext().dispatcher(),
            null);

    @Override
    public void preStart() {
        cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() {
        timeoutScheduler.cancel();
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof PieceOfWork) {
            handleMessage((PieceOfWork) message);
        } else {
            unhandled(message);
        }
    }

    private void handleMessage(PieceOfWork message) {
        log.info("[WORKER] Actor handled message: " + message.getMessage());
    }
}
