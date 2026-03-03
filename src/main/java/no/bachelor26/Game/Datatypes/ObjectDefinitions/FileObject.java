package no.bachelor26.Game.Datatypes.ObjectDefinitions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class FileObject extends TaskObject {

    private String name;
    private String metadata;
    private String content;

}
