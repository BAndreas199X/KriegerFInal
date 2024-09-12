package com.andreas.webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.andreas.webapp.services.AuthorService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

@Controller
public class MainController implements ErrorController  {

	@Autowired
    private AuthorService authService;
	
	@RequestMapping("/")
	public ModelAndView getMainPage() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("home.jsp");
		return mv;
	}
	
	@KafkaListener(topics = "AndreasB4KriegerDeletion", groupId = "AndreasGroupID")
	public void listenDeletionAuthor(String message) {
	    
	    Gson gson = new Gson();
	    JsonObject jsonObj = gson.fromJson(message, JsonObject.class);
	    if(jsonObj.get("ID")==null) {
	    	
	    	return;
	    }
	    
	    int id = jsonObj.get("ID").getAsInt();
	    
	    authService.deleteEverythingFromAuthor(id);
	}
	
	//uncomment to immediately consume the update messages in the kafka queue
	/*@KafkaListener(topics = "AndreasB4KriegerUpdates",groupId = "AndreasGroupID")
		public void listenUpdates(String message) {
		    System.out.println("Received Message in queue AndreasB4Krieger: " 
		    + message);
	}*/
}
