package no.bachelor26.WebSocket.Messages.ServerMessages;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import tools.jackson.databind.JsonNode;

@Data
public class Task {

    private JsonNode objects;

    @JsonProperty("key-events")
    private JsonNode keyEvents;

    private JsonNode triggers;

    private JsonNode details;

}
