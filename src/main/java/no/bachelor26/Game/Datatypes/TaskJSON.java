package no.bachelor26.Game.Datatypes;

import java.util.Map;

import lombok.Data;
import no.bachelor26.Game.Datatypes.ObjectDefinitions.TaskObject;

@Data
public class TaskJSON {

    // Objekter som eksisterer i oppgaven
    private Map<String, TaskObject> objects;

    

}
