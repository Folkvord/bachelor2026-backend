package no.bachelor26.Game.Datatypes.ObjectDefinitions;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServerProcessObject extends TaskObject {

    private String processType;
    private List<String> resources;

}
