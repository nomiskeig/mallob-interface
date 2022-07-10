package edu.kit.fallob.api.request.stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.servlet.http.HttpServletRequest;

public class MallobStreamController {

    public ResponseEntity<ResponseBodyEmitter> streamLogs(HttpServletRequest httpRequest, MallobStreamRequest request) {
        return null;
    }

    public ResponseEntity<ResponseBodyEmitter> streamEvents() {
        return null;
    }
}
