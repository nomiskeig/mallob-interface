package edu.kit.fallob.api.request.stream;

import edu.kit.fallob.mallobio.listeners.outputloglisteners.OutputLogLineListener;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * listener for the custom log line stream
 * it implements the OutputLogLineListener interface and gets registered as listener for the mallob log lines
 * @author Valentin Schenk
 * @version 1.0
 */
public class LineStream implements OutputLogLineListener {

    private final ResponseBodyEmitter emitter;
    private final int jobId;
    private final String[] regex;

    /**
     * constructor of the class
     * @param emitter the ResponseBodyEmitter over which the data is continuously given back to the user
     * @param jobId the id of job for whose log lines should be listened
     * @param regex the regex pattern that defines which log lines are relevant
     */
    public LineStream(ResponseBodyEmitter emitter, int jobId, String[] regex) {
        this.emitter = emitter;
        this.jobId = jobId;
        this.regex = regex;
    }


    /**
     * processes a new log line from mallob and gives it to the user if it is relevant
     * @param line the log line
     */
    @Override
    public void processLine(String line) {
        //TODO
    }
}
