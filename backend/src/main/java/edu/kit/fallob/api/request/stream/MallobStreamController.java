package edu.kit.fallob.api.request.stream;

import edu.kit.fallob.springConfig.FallobException;
import edu.kit.fallob.springConfig.FallobWarning;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * controller class for the event stream and the custom log line stream
 * @author Valentin Schenk
 * @version 1.0
 */
@RestController
@CrossOrigin
@RequestMapping("/api/v1")
public class MallobStreamController {

    /**
     * api endpoint for the custom log line stream
     * is responsible for initializing a new line stream
     * @param httpRequest is required to get information about the user
     * @param request holds all the information that is necessary to open the stream
     * @return a ResponseBodyEmitter with which data is continuously given back to the user
     */
    @GetMapping("/api/v1/system/logStream")
    public ResponseEntity<ResponseBodyEmitter> streamLogs(HttpServletRequest httpRequest, MallobStreamRequest request) {
        //TODO
        return null;
    }

    /**
     * api endpoint for the event stream of mallob
     * @return a ResponseBodyEmitter with which data is continuously given back to the user
     */
    @GetMapping("/events/eventStream")
    public ResponseEntity<ResponseBodyEmitter> streamEvents() {
        StreamInitializer initializer = new StreamInitializer();
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();

        try {
            initializer.startEventStream(emitter);
        } catch (FallobException e) {
            e.printStackTrace();
            FallobWarning warning = new FallobWarning(e.getStatus(), e.getMessage());
            try {
                emitter.send(warning);
            } catch (IOException ex) {
                emitter.complete();
            }
            emitter.complete();
        }

        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }
}
