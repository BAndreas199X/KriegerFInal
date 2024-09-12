package com.andreas.webapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.webapp.model.Author;
import com.andreas.webapp.services.AuthorService;

import jakarta.servlet.http.HttpServletRequest;

@RestController("author")
public class AuthorController {
	
	@Autowired
	AuthorService authService;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	/**
	 * API endpoint that can be used to create a new author
	 * RequestMethod: POST
	 * @param athr: an Object of type "Author" it can be imitated by giving AuthorID, 
	 * FirstName, and LastName as parameters of a query
	 * @throws Exception 
	 * @returns:
	 * - In case the author is successfully created and stored in the database, 
	 * 		the created Author is returned as ResponseEntity
	 */
	@PostMapping("/author")
	public ResponseEntity<Object> createAuthor(Author athr) {
		

		HttpStatus httpStat = null;
		Object resultObj = authService.createAuthor(athr);
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
		}
		return new ResponseEntity<>(resultObj,httpStat);
		
	}
	
	
	/**
	 * API endpoint that is used to retrieve an existing author
	 * RequestMEthod: GET
	 * @param authorID - The ID of the author we're searching for
	 * @return
	 * - In case the author corresponding to the ID is found, he is returned 
	 * 		via ReponseEntity
	 */
	@GetMapping("/author/{id}")
	public ResponseEntity<Object> getAuthorById(@PathVariable("id") int authorID) {
		

		HttpStatus httpStat = null;
		Object resultObj = authService.getAuthorById(authorID);
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
		}
		return new ResponseEntity<>(resultObj,httpStat);
		
	}
	
	/**
	 * An API endpoint used to retrieve ALL existing authors
	 * RequestMethod: GET
	 * @return
	 * - If nothing unforseen happens, a ResponseEntity with a List of all the 
	 * 		authors is returned
	 */
	@GetMapping("/author")
	public ResponseEntity<Object> getAllAuthors() {

		HttpStatus httpStat = null;
		Object resultObj = authService.getAllAuthors();
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	/**
	 * API endpoint that can find an author based on first and last name, 
	 * first and last name don't need to be completed, it's sufficient if a 
	 * 	part corresponds
	 * RequestMethod: GET
	 * 
	 * @param firstName - (partial) first name of the author you are searching for
	 * @param lastName - (partial) last name of the author you are searching for
	 * @return
	 * - A ResponseEntity with a List of all the authors corresponding to the 
	 * 		input is returned
	 */
	@GetMapping("/author/{firstName}/{lastName}")
	public ResponseEntity<Object> getByNames(
			@PathVariable("firstName") String firstName, 
			@PathVariable("lastName") String lastName) {
		
		HttpStatus httpStat = null;
		Object resultObj = authService.getByNames(firstName, lastName);
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	/**
	 * An API endpoint to delete the author corresponding to the provided ID
	 * RequestMethod: DELETE
	 * 
	 * @param authorID - ID of the author that is to be deleted
	 * @return
	 * - If successful, the ResponseEntity contains a message confirming the deletion
	 * @throws Exception 
	 */
	@DeleteMapping("/author/{id}")
	public ResponseEntity<Object> deleteAuthorById(@PathVariable("id") int authorID) {

		HttpStatus httpStat = null;
		Object resultObj = authService.deleteAuthorById(authorID);
		if(resultObj.equals("Author successfully deleted!")) {
			httpStat = HttpStatus.OK;
		}else {
			httpStat = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	/**
	 * An API endpoint to delete all authors in the database
	 * RequestMethod: DELETE
	 * 
	 * @return
	 * - If successful, the ResponseEntity contains a message confirming the deletion
	 */
	@DeleteMapping("/author")
	public ResponseEntity<Object> deleteAll() {
		
		HttpStatus httpStat = null;
		Object resultObj = authService.deleteAll();
		if(resultObj.equals("All Authors successfully deleted!")) {
			httpStat = HttpStatus.OK;
		}else {
			httpStat = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(resultObj,httpStat);
		
	}
	
	/**
	 * An API endpoint used to update an author's first name
	 * RequestMethod: PUT
	 * @param authorID - ID of the author that is to be updated
	 * @param firstName - the first name the author should be updated to
	 * 
	 * @return
	 * - If successful, the ResponseEntity contains the updated Author-object
	 */
	@PutMapping("/author/updateFirstName/{id}/{firstName}")
	public ResponseEntity<Object> updateFirstName(@PathVariable("id") int authorID,
			@PathVariable("firstName") String firstName) {
		

		HttpStatus httpStat = null;
		Object resultObj = authService.updateFirstName(authorID, firstName);
			
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
				
			sendMessage(String.format(
					"Author with ID %d had his first name updated to %s", 
				authorID, firstName));
		}
		return new ResponseEntity<>(resultObj,httpStat);
		
	}
	
	/**
	 * An API endpoint used to update an author's last name
	 * RequestMethod: PUT
	 * @param authorID - ID of the author that is to be updated
	 * @param lastName - the last name the author should be updated to
	 * 
	 * @return
	 * - If successful, the ResponseEntity contains the updated Author-object
	 */
	@PutMapping("/author/updateLastName/{id}/{lastName}")
	public ResponseEntity<Object> updateLastName(@PathVariable("id") int authorID,
			@PathVariable("lastName") String lastName) {
		
		HttpStatus httpStat = null;
		Object resultObj = authService.updateLastName(authorID, lastName);
			
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
				
			sendMessage(String.format(
					"Author with ID %d had his last name updated to %s", 
					authorID, lastName));
		}
		return new ResponseEntity<>(resultObj,httpStat);
		
	}
	
	/**
	 * Function that facilitates the publishing to the Kafka-topic for updates
	 * @param msg - the message to be send
	 */
	public void sendMessage(String msg) {
		
	    kafkaTemplate.send("AndreasB4KriegerUpdates", msg);
	}
	
	/**
	 * A function that handles DataAccessExceptions (can happen in JPA) for 
	 * 		all functions of this controller
	 * @param req - The Request send that causes the exception
	 * @param e - The Exception thrown
	 * @return - ResponseEntity with a message explaining the error and the 
	 * 	Error Message
	 */
	@ExceptionHandler({DataAccessException.class})
	public ResponseEntity<String> dataAccessException(HttpServletRequest req,
			Exception e) {
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				"There was an error in context-path '"+req.getRequestURI()+
				"' with the JPA Data Access:\n "+e.toString());
	}
	
	/**
	 * A function that handles KafkaExceptions (caused by Kafka) for all functions 
	 * 		of this controller
	 * @param req - The Request send that causes the exception
	 * @param e - The Exception thrown
	 * @return - ResponseEntity with a message explaining the error and the 
	 * 		Error Message
	 */
	@ExceptionHandler({KafkaException.class})
	public ResponseEntity<String> kafkaException(HttpServletRequest req,Exception e) {
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				"There was a Kafka-error in context-path '"+req.getRequestURI()+
				"'. The last name was updated, but it could not be published to "
				+ "Kafka:\n "+e.toString());
	}
	
	/**
	 * A function that handles Exceptions not otherwise handled for all functions 
	 * 	of this controller controller
	 * @param req - The Request send that causes the exception
	 * @param e - The Exception thrown
	 * @return - ResponseEntity with a message explaining the error and the 
	 * 		Error Message
	 */
	@ExceptionHandler({Exception.class})
	public ResponseEntity<String> generalException(HttpServletRequest req,Exception e) {
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				"Unknown Error in context-path '"+req.getRequestURI()+"': "
		+e.toString());
	}
}
