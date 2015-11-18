package bg.alexander.company.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HelloWorldController {
	
	@RequestMapping("/hello-world")
	public String helloWorld(Model model){
		Bean b = new Bean();
		b.setMessage("Test message");
		model.addAttribute("messageBean",b);
		return "hello";
	}
}
