package com.andreas.webapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.andreas.webapp.dao.AuthorRepo;
import com.andreas.webapp.model.Author;

@Service
public class AuthorService {

	@Autowired
	AuthorRepo authRepo;
	
	@Autowired
	DocumentService docService;
	
	static final String NOAUTHOR_ERROR_MSG = 
			"No Author found for the provided Author-ID!";
	
	/**
	 * Function to save a new author in the database
	 * beforehand, it is checked that the first and last name provided are valid, 
	 * 	e.g. not null
	 * @param athr - an Object of type "Author"
	 * @return - In case of successful creation, the new author object is returned 
	 * 				in the responseEntity
	 * 		- if first or last name are not valid, the ResponseEntity contains a 
	 * 			message informing the user of this
	 * @throws Exception 
	 */
	public Object createAuthor(Author athr) {
		
		if(athr.getFirstName() == null||athr.getLastName() == null) {
			return "First Name AND Last Name must be provided to create a new author!";
		}
		athr = authRepo.save(athr);
		
		return athr;
	}
	
	
	/**
	 * A function used to return an Author corresponding to a particular ID
	 * the functions checks whether the function ID is legit
	 * it also checks whether an Author corresponding to the ID was found or not 
	 * @param authorID - ID of the author we are searching for
	 * @return - If an author matching the ID is found, he is returned as an object 
	 * 				in the responseEntity
	 * 		- if author-ID not valid, or no Author with the ID is found, the 
	 * 			ResponseEntity contains a message informing the user of this
	 */
	public Object getAuthorById(int authorID) {
		
		if(authorID <= 0) {
			return "Invalid Author-ID provided!";
		}
		
		Optional<Author> optionalAuth = authRepo.findById(authorID);
		
		if(optionalAuth.isPresent()) {
			return optionalAuth.get();
		}else {
			return NOAUTHOR_ERROR_MSG;
		}
	}

	
	/**
	 * Returns ALL authors present in the database
	 * @return the ResponseEntity contains a LIST with all present Authors
	 */
	public Object getAllAuthors() {
		
		return authRepo.findAll();
	}

	
	/**
	 * returns a list of all authors whose first or last names match the input
	 * the input must not be complete, it's sufficient if the input is contained
	 * 
	 * first, it is checked that the provided names are not empty
	 * @param firstName - first name of the author we are searching for
	 * @param lastName - last name of the author we are searching for
	 * @return - the ResponseEntity contains a list of all authors matching the input
	 * 			- if the provided first or last name were not valid, a ResponseEntity 
	 * 			is returned informing the caller of this
	 */
	public Object getByNames(String firstName,String lastName) {
		
		if(firstName==null || firstName.equals("") || lastName==null || 
				lastName.equals("")) {
			return "A value fpr First Name AND Last Name must be provided for "
					+ "the author search!";
		}
	
		return authRepo.findByNames(firstName,lastName);
	}

	
	/**
	 * A function that can delete an author with the corresponding ID from the 
	 * 		database
	 * checks first if an author with that ID even exists
	 * @param authorID - ID of the author we want to delete
	 * @return - in case of successful deletion, a ResponseEntity is returned with a 
	 * 				message confirming the deletion
	 * 		   - in case an author with said ID didn't exist, the ResponseEntity 
	 * 				insteead informs the caller of this
	 * @throws Exception 
	 */
	public Object deleteAuthorById(int authorID){
		
		if(authRepo.existsById(authorID)) {
			authRepo.deleteAuthoredBy(authorID);
			authRepo.deleteById(authorID);
			return "Author successfully deleted!";
		}else {
			return NOAUTHOR_ERROR_MSG;
		}
		
	}
	
	
	/**
	 * A function that can delete ALL authors in the database
	 * @return - A ResponseEntity with a message confirming the deletion of all 
	 * 		authors is returned
	 */
	public Object deleteAll() {
		
		for(Author auth:authRepo.findAll()) {
			authRepo.deleteAuthoredBy(auth.getAuthorID());
		}
		
		authRepo.deleteAll();
		return "All Authors successfully deleted!";
	}

	
	/**
	 * A function used to update the first name of an existing author (has an 
	 * 		existing author ID)
	 * it is also checked whether the author to-be-updated even exists
	 * @param authorID - ID of the author whose first name we want to update
	 * @param firstName - first name we want to update to
	 * @return - If successful, a ResponseEntity with the updated author is returned
	 * 		   - if the author with the ID doesn't exist, the ResponseEntity informs 
	 * 				the caller of this with a message
	 */
	public Object updateFirstName(int authorID, String firstName) {
		Optional<Author> newAuth = authRepo.findById(authorID);
		
		if(newAuth.isPresent()) {
			Author result = newAuth.get();
			result.setFirstName(firstName);
			result = authRepo.save(result);
		
			return result;
		}else {
			return NOAUTHOR_ERROR_MSG;
		}
	}

	
	/**
	 * A function used to update the last name of an existing author (has an existing 
	 * 		author ID)
	 * it is also checked whether the author to-be-updated even exists
	 * @param authorID - ID of the author whose last name we want to update
	 * @param firstName - last name we want to update to
	 * @return - If successful, a ResponseEntity with the updated author is returned
	 * 		   - if the author with the ID doesn't exist, the ResponseEntity informs 
	 * 				the caller of this with a message
	 */
	public Object updateLastName(int authorID, String lastName) {
		Optional<Author> newAuth = authRepo.findById(authorID);
		
		if(newAuth.isPresent()) {
			Author result = newAuth.get();
			result.setLastName(lastName);
			result = authRepo.save(result);
		
			return result;
		}else {
			return NOAUTHOR_ERROR_MSG;
		}
	}
	
	/**
	 * Delete the author (with the spcified ID) as well as all documents he authored 
	 * 		from the database
	 * @param authorID - ID of the author whose entries you are removing
	 */
	public void deleteEverythingFromAuthor(int authorID) {
		
		if(!authRepo.existsById(authorID)) {
			return;
		}
		
		List<Integer> authoredDocuments = authRepo.findAuthoredDocuments(authorID);
		
		for(int i:authoredDocuments) {
			docService.deleteDocumentByID(i);
		}
		
		deleteAuthorById(authorID);
	}
}
