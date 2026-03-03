package no.bachelor26.Game.Datatypes.ObjectDefinitions;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DirectoryObject extends TaskObject {

    private String name;
    private List<String> content;

}
