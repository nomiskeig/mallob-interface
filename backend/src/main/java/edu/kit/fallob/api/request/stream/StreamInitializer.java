package edu.kit.fallob.api.request.stream;

import edu.kit.fallob.springConfig.FallobException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * class that is responsible for initializing new streams
 * @author Valentin Schenk
 * @version 1.0
 */
public class StreamInitializer {
    //thread pool on which the stream is run
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * initializes a new event stream from mallob
     * @param emitter the ResponseBodyEmitter over which the data is continuously given back to the user
     * @throws FallobException if something goes wrong while working with the database
     */
    public void startEventStream(ResponseBodyEmitter emitter) throws FallobException {
        System.out.println("Started initializer");
        Runnable eventStreamStarter = new EventStreamStarter(emitter);
        this.executor.execute(eventStreamStarter);
    }

    public void startLineStream(ResponseBodyEmitter emitter, int jobId, String[] regex, String username) {
        //TODO
    }
}
