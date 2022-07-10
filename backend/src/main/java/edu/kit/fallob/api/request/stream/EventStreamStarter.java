package edu.kit.fallob.api.request.stream;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public class EventStreamStarter implements Runnable{

    private final ResponseBodyEmitter emitter;

    public EventStreamStarter(ResponseBodyEmitter emitter) {
        this.emitter = emitter;
    }

    @Override
    public void run() {

    }
}
