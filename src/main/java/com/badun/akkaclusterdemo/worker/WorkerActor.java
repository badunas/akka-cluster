package com.badun.akkaclusterdemo.worker;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import com.badun.akkaclusterdemo.message.PieceOfWork;
import com.badun.akkaclusterdemo.message.WorkDone;
import com.badun.akkaclusterdemo.util.Sleeper;

/**
 * Created by Artsiom Badun.
 */
public class WorkerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);

    public WorkerActor() {
        receive(ReceiveBuilder
                .match(PieceOfWork.class, this::handleWorkMessage)
                .matchAny(this::unhandled)
                .build());
        log.debug("Created worker actor.");
    }

    private void handleWorkMessage(PieceOfWork message) {
        Sleeper.sleep(1);
        log.info("[WORKER] Actor handled message: " + message);
        sender().tell(new WorkDone(), context().parent());
        sender().tell(new WorkDone(), context().parent());
    }
}
