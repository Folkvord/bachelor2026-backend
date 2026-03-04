package no.bachelor26.Game.Datatypes.CMDDefinitions;

import java.util.Map;

import lombok.Data;

@Data
public class KeyEvent {

    private Map<String, TaskCMD> cmd;

}
