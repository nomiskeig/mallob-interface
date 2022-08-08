package edu.kit.fallob.api.request.stream;

import edu.kit.fallob.database.DaoFactory;
import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.OutputLogLineListener;
import edu.kit.fallob.mallobio.output.distributors.MallobOutput;
import edu.kit.fallob.springConfig.FallobException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * class that is responsible for creating the necessary listeners for an event stream and starting the stream
 * the class implements the Runnable interface to run on a separate thread
 * @author Valentin Schenk
 * @version 1.0
 */
public class EventStreamStarter implements Runnable{
    private static final String STREAM_ERROR = "An error occurred in the event stream";

    private final ResponseBodyEmitter emitter;
    private final JobDao jobDao;

    /**
     * constructor of the class
     * @param emitter the ResponseBodyEmitter over which the data is continuously given back to the user
     * @throws FallobException if something goes wring while getting the userDao
     */
    public EventStreamStarter(ResponseBodyEmitter emitter) throws FallobException {
        this.emitter = emitter;
        DaoFactory daoFactory = new DaoFactory();
        this.jobDao = daoFactory.getJobDao();
    }

    /**
     * creates a new listener, registers the listener and then waits to prevent the method from returning
     */
    @Override
    public void run() {
        OutputLogLineListener eventStream = new EventStream(this.emitter, this.jobDao);

        MallobOutput mallobOutput = MallobOutput.getInstance();
        mallobOutput.addOutputLogLineListener(eventStream);
        System.out.println("registered listener");

        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        }
    }
}
