package springplus.springplus.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j // Logger
public class HomeController {

    @RequestMapping("/")
    public String home(){
        log.info("home Controller");
        return "home";
    }
}
