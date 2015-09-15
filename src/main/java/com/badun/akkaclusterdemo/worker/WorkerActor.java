package com.badun.akkaclusterdemo.worker;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.badun.akkaclusterdemo.message.PieceOfWork;

/**
 * Created by Artsiom Badun.
 */
public class WorkerActor extends UntypedActor {
    LoggingAdapter log = Logging.getLogger(getContext().system(), this);

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
