package no.bachelor26.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/* 
 * Hovedkontrolleren: Returnerer enkle htmldokumenter
 */

@Controller
public class MainController {

    @GetMapping("/")
    public String getIndex(){
        return "index";
    }

}
