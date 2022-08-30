package edu.kit.fallob.api.request.controller;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class KeepAsJsonDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        TreeNode tree = jp.getCodec().readTree(jp);
        return tree.toString();

    }
}
