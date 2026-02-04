package no.bachelor26.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TestController {

    @GetMapping("/api/me")
    public String me() {
        return "You are authenticated :D";
    }

    @GetMapping("/api/admin/panel")
    public String adminPanel() {
        return "You are admin!";
    }

}
