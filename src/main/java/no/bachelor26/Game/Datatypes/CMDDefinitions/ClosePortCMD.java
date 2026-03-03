package no.bachelor26.Game.Datatypes.CMDDefinitions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ClosePortCMD extends TaskCMD {

    private String server;
    private Integer port;
    
}
