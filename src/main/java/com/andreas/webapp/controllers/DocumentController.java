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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.webapp.dao.AuthorRepo;
import com.andreas.webapp.dao.DocumentRepo;
import com.andreas.webapp.model.Document;
import com.andreas.webapp.services.DocumentService;
import com.google.gson.Gson;

import jakarta.servlet.http.HttpServletRequest;

@RestController("document")
public class DocumentController {
	static final Gson globGson = new Gson();
	@Autowired
	DocumentRepo docRepo;
	@Autowired
	AuthorRepo authRepo;
	@Autowired
	DocumentService docService;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	/**
	 * An API endpoint used for creating a new document
	 * RequestPArameter: POST
	 * @param payload - a JSON String (won't accept any other String)
	 * @returns:
	 * - In case the document is successfully created and stored in the database, 
	 * 		the created Document is returned as ResponseEntity
	 * - In case there is an undesired output, a ResponseEntity with a remark is 
	 * 				returned
	 */
	@PostMapping(path="/document", consumes = "application/json")
	public ResponseEntity<Object> createDocument(@RequestBody String payload) 
			throws Exception {

			HttpStatus httpStat = null;
			Object resultObj = docService.createDocument(payload);
			if(resultObj instanceof String) {
				httpStat = HttpStatus.BAD_REQUEST;
			}else {
				httpStat = HttpStatus.OK;
			}
			return new ResponseEntity<>(resultObj,httpStat);
	}
	
	
	/**
	 * An API endpoint that can find and return a document corresponding the the 
	 * 		provided Document_ID
	 * RequestPArameter: GET
	 * @param documentID
	 * @return - if a Document corresponding to the ID is found, it is returned via 
	 * 				ReponseEntity
	 * 	  		- In case there is an undesired output, a ResponseEntity with a 
	 * 				remark is returned
	 */
	@GetMapping("/document/{id}")
	public Object getDocumentById(@PathVariable("id") int documentID) {
		
		HttpStatus httpStat = null;
		Object resultObj = docService.getDocumentById(documentID);
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	
	/**
	 * An API endpoint that will return ALL Documents present in the database
	 * RequestPArameter: GET
	 * @return - If successful, a List with all Documents present in the databse 
	 * 				will be returned
	 * 	  		- In case there is an undesired output, a ResponseEntity with a 
	 * 				remark is returned
	 */
	@GetMapping("/document")
	public Object getAllDocuments() {
		
		HttpStatus httpStat = null;
		Object resultObj = docService.getAllDocuments();
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	
	/**
	 * An API endpoint, that will delete a Document corresponding to the provided 
	 * 		Document-ID
	 * RequestPArameter: DELETE
	 * @param documentID - ID of the document you want to delete
	 * @return - If successful, a ResponseEntity with a message confirming deletion 
	 * 				is returned
	 * 	  		- In case there is an undesired output, a ResponseEntity with a 
	 * 				remark is returned
	 * @throws Exception 
	 */
	@DeleteMapping("/document/{id}")
	public ResponseEntity<Object> deleteDocumentByID(
			@PathVariable("id") int documentID){
		
		HttpStatus httpStat = null;
		Object resultObj = docService.deleteDocumentByID(documentID);
		if(resultObj.equals("Document successfully deleted!")) {
			httpStat = HttpStatus.OK;
		}else {				
			httpStat = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	
	/**
	 * An API-endpoitn used to delete ALL documents present in the database
	 * RequestMEthod: DELETE
	 * @return - If successful, a ResponseEntity with a message confirming deletion 
	 * 				is returned 
	 * 	  		- In case there is an undesired output, a ResponseEntity with a 
	 * 				remark is returned
	 */
	@DeleteMapping("/document")
	public ResponseEntity<Object> deleteAllDocuments(){
		
		HttpStatus httpStat = null;
		Object resultObj = docService.deleteAllDocuments();
		if(resultObj.equals("All Documents successfully deleted!")) {
			httpStat = HttpStatus.OK;
		}else {
			httpStat = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	
	/**
	 * An API-endpoint to update the title of a document corresponding to the 
	 * 		provided ID
	 * RequestMethod: PUT
	 * @param documentID - ID of the document you want to update
	 * @param title - the title you want to update the document with
	 * @return - If successful, a ResponseEntity with the newly updated Document 
	 * 				is returned
	 * 	  		- In case there is an undesired output, a ResponseEntity with a 
	 * 				remark is returned
	 */
	@PutMapping(path="/document/updateTitle/{documentID}/{title}")
	public ResponseEntity<Object> updateDocumentTitle(
				@PathVariable("documentID") int documentID,
				@PathVariable("title") String title) {

		HttpStatus httpStat = null;
		Object resultObj = docService.updateDocumentTitle(documentID,title);
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
				
			sendMessage(String.format(
					"Document with ID %d had its title updated to %s",
					documentID, title));
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	
	/**
	 * An API endpoint used to update an existing Document's Body
	 * RequestMethod: PUT
	 * @param payload  - a JSON String (won't accept any other String)
	 * @return - If successful, a ResponseMethod with the newly updated Document is 
	 * 					returned
	 * 	 * 	  		- In case there is an undesired output, a ResponseEntity with a 
	 * 					remark is returned
	 */
	@PutMapping(path="/document/updateBody", consumes = "application/json")
	public ResponseEntity<Object> updateDocumentBody(@RequestBody String payload){

		HttpStatus httpStat = null;
		Object resultObj = docService.updateDocumentBody(payload);
		if(resultObj instanceof String) {
			httpStat = HttpStatus.BAD_REQUEST;
		}else {
			httpStat = HttpStatus.OK;
			
			Document d = (Document) resultObj;
			sendMessage(String.format("Document with ID %d had its body updated!", 
					d.getDocumentID()));
		}
		return new ResponseEntity<>(resultObj,httpStat);	
	}
	
	/**
	 * An API endpoint that can add an author as author to a document
	 * RequestMethod: PUT 
	 * @param documentID - ID of the document you want to add the author to
	 * @param authorID - The ID of the author you want to add as author to a document
	 * @return - if successful, a ResponseEntity with a message confirming the update 
	 * 				is returned
	 * 	  		- In case there is an undesired output, a ResponseEntity with a 
	 * 				remark is returned
	 * @throws Exception 
	 */
	@PutMapping(path="/document/addAuthor/{documentID}/{authorID}")
	public ResponseEntity<Object> addAuthorToDocument(
			@PathVariable("documentID") int documentID,
			@PathVariable("authorID") int authorID) throws Exception {
		

		HttpStatus httpStat = null;
		Object resultObj = docService.addAuthorToDocument(documentID,authorID);
		if(resultObj.equals("Author successfully added as author to document!")) {
			httpStat = HttpStatus.OK;
			
			sendMessage(String.format("Document with ID %d had author with ID %d "
					+ "added as new author!", 
					documentID,authorID));
		}else {
			httpStat = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	/**
	 * An API endpoint that can remove an author as author from a document
	 * RequestMethod: PUT 
	 * @param documentID - ID of the document you want to remove the author from
	 * @param authorID - The ID of the author you want to remove as author from the 
	 * 						document
	 * @return - if successful, a ResponseEntity with a message confirming the 
	 * 				update is returned
	 * 	  		- In case there is an undesired output, a ResponseEntity with a 
	 * 				remark is returned
	 * @throws Exception 
	 */
	@PutMapping(path="/document/removeAuthor/{documentID}/{authorID}")
	public ResponseEntity<Object> removeAuthorFromDocument(
			@PathVariable("documentID") 
		int documentID, @PathVariable("authorID") int authorID) throws Exception {
		
		HttpStatus httpStat = null;
		Object resultObj = docService.removeAuthorFromDocument(documentID,authorID);
		if(resultObj.equals("Author successfully removed as author from Document!")) {
			httpStat = HttpStatus.OK;
			
			sendMessage(String.format("Document with ID %d had author with ID %d "
					+ "removed as author!",documentID,authorID));
		}else {
			httpStat = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	
	/**
	 * An API endpoint that can add a document as reference to a document
	 * RequestMethod: PUT 
	 * @param referencingID - ID of the document you want to add a new reference to
	 * @param referencedID - ID of the document you want to add as reference
	 * @return - if successful, a ResponseEntity with a message confirming the 
	 * 				update is returned
	 * 	  		- In case there is an undesired output, a ResponseEntity with a 
	 * 				remark is returned
	 * @throws Exception 
	 */
	@PutMapping(path="/document/addReference/{referencingID}/{referencedID}")
	public ResponseEntity<Object> addReferenceToDocument(
			@PathVariable("referencingID") int referencingID,
			@PathVariable("referencedID") int referencedID) throws Exception {
		
		HttpStatus httpStat = null;
		Object resultObj = docService.addReferenceToDocument(
				referencingID,referencedID);
		if(resultObj.equals("Document successfully added as reference!")) {
			httpStat = HttpStatus.OK;
				
			sendMessage(String.format("Document with ID %d had document with ID "
					+ "%d added as reference!",referencingID,referencedID));
		}else {
			httpStat = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<>(resultObj,httpStat);
	}
	
	
	/**
	 * An API endpoint that can remove a document as reference from a document
	 * RequestMethod: PUT 
	 * @param referencingID - ID of the document you want to remove a new reference 
	 * 							from
	 * @param referencedID - ID of the document you want to remove as reference
	 * @return - if successful, a ResponseEntity with a message confirming the update 
	 * 				is returned
	 * 	  		- In case there is an error, or another undesired output, a 
	 * 				ResponseEntity with the ErrorMessage or another remark is returned
	 * @throws Exception 
	 */
	@PutMapping(path="/document/removeReference/{referencingID}/{referencedID}")
	public ResponseEntity<Object> removeReferenceFromDocument(
			@PathVariable("referencingID") int referencingID,
			@PathVariable("referencedID") int referencedID) throws Exception {
		

		HttpStatus httpStat = null;
		Object resultObj = docService.removeReferenceFromDocument(
				referencingID,referencedID);
		
		if(resultObj.equals("Document successfully removed as reference!")) {
			httpStat = HttpStatus.OK;
				
			sendMessage(String.format("Document with ID %d had document with ID %d "
					+ "removed as reference!", referencingID,referencedID));
		}else {
			httpStat = HttpStatus.BAD_REQUEST;
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
	 * A function that handles DataAccessExceptions (can happen in JPA) for all 
	 * 		functions of this controller
	 * @param req - The Request send that causes the exception
	 * @param e - The Exception thrown
	 * @return - ResponseEntity with a message explaining the error and the 
	 * 				Error Message
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
	 * 				Error Message
	 */
	@ExceptionHandler({KafkaException.class})
	public ResponseEntity<String> kafkaException(HttpServletRequest req,Exception e) {
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				"There was a Kafka-error in context-path '"+req.getRequestURI()+
				"'. The last name was updated, but it could not be published to "
				+ "Kafka:\n "+e.toString());
	}
	
	/**
	 * A function that handles Exceptions not otherwise handled for all functions of 
	 * 		this controller
	 * @param req - The Request send that causes the exception
	 * @param e - The Exception thrown
	 * @return - ResponseEntity with a message explaining the error and the 
	 * 				Error Message
	 */
	@ExceptionHandler({Exception.class})
	public ResponseEntity<String> generalException(HttpServletRequest req,
			Exception e) {
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
				"Unknown Error in context-path '"+req.getRequestURI()+"': "
						+e.toString());
	}
}