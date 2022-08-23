package edu.kit.fallob.api.request.stream;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * class that is responsible for creating the listener necessary for a custom line stream and starting the stream
 * the class implements the Runnable interface so that it can run in a separate thread
 * @author Valentin Schenk
 * @version 1.0
 */
public class LineStreamStarter implements Runnable{
    private final ResponseBodyEmitter emitter;
    private final int jobId;
    private final String[] regex;
    private final String username;

    /**
     * constructor of the class
     * @param emitter the ResponseBodyEmitter over which the data is continuously given back to the user
     * @param jobId the id of the job for whose log lines should be listened
     * @param regex the regex pattern that defines which log lines are relevant
     * @param username the name of the user that requested the stream
     */
    public LineStreamStarter(ResponseBodyEmitter emitter, int jobId, String[] regex, String username) {
        this.emitter = emitter;
        this.jobId = jobId;
        this.regex = regex;
        this.username = username;
    }

    /**
     * creates a new listener, registers the listener and then waits to prevent the method from returning
     */
    @Override
    public void run() {
        //TODO
    }
}
