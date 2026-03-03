package no.bachelor26.Game.Datatypes.ObjectDefinitions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServerObject extends TaskObject {

    private String hostname;

}
