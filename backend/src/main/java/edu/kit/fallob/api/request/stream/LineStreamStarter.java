package edu.kit.fallob.api.request.stream;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public class LineStreamStarter implements Runnable{
    private final ResponseBodyEmitter emitter;
    private final int jobId;
    private final String[] regex;
    private final String username;

    public LineStreamStarter(ResponseBodyEmitter emitter, int jobId, String[] regex, String username) {
        this.emitter = emitter;
        this.jobId = jobId;
        this.regex = regex;
        this.username = username;
    }


    @Override
    public void run() {

    }
}
