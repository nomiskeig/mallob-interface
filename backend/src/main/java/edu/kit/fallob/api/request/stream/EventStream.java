package edu.kit.fallob.api.request.stream;

import edu.kit.fallob.database.JobDao;
import edu.kit.fallob.mallobio.listeners.outputloglisteners.OutputLogLineListener;
import edu.kit.fallob.mallobio.outputupdates.Event;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * the event listener for the event stream
 * it implements the OutputLogLineListener interface and gets registered as listener for the mallob log lines
 */
public class EventStream implements OutputLogLineListener {
    private static final String EVENT_REGEX = "";

    private final ResponseBodyEmitter emitter;
    private final JobDao jobDao;

    /**
     * constructor of the class
     * @param emitter the ResponseBodyEmitter iver which the data is continuously given back to the user
     * @param jobDao a jobDao that is necessary to convert the mallob id into a regular jobId
     */
    public EventStream(ResponseBodyEmitter emitter, JobDao jobDao) {
        this.emitter = emitter;
        this.jobDao = jobDao;
    }

    /**
     * processes a new log line from mallob
     * the method gets called every time mallob writes a new log line
     * @param line the log line that should be processed
     */
    @Override
    public void processLine(String line) {
        if (Event.isEvent(line)) {
            Event event = new Event(line);
            int jobId = this.jobDao.getJobIdByMallobId(event.getJobID());

            //TODO: return event to emitter
        }
    }
}
