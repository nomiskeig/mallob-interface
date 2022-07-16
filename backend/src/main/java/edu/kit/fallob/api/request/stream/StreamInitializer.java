package edu.kit.fallob.api.request.stream;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.ExecutorService;

public class StreamInitializer {
    private ExecutorService executor;

    public void startEventStream(ResponseBodyEmitter emitter) {

    }

    public void startLineStream(ResponseBodyEmitter emitter, int jobId, String[] regex, String username) {

    }
}
