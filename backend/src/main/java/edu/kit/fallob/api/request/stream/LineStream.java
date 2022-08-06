package edu.kit.fallob.api.request.stream;

import edu.kit.fallob.mallobio.listeners.outputloglisteners.OutputLogLineListener;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

public class LineStream implements OutputLogLineListener {

    private final ResponseBodyEmitter emitter;
    private final int jobId;
    private final String[] regex;

    public LineStream(ResponseBodyEmitter emitter, int jobId, String[] regex) {
        this.emitter = emitter;
        this.jobId = jobId;
        this.regex = regex;
    }


    @Override
    public void processLine(String line) {
        //TODO
    }
}
