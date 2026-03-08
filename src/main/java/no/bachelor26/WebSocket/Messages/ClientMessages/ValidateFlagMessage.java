package no.bachelor26.WebSocket.Messages.ClientMessages;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ValidateFlagMessage extends ClientMessage {

    private String flag;

}
