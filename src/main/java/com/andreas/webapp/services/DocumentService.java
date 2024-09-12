package com.andreas.webapp.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.andreas.webapp.dao.AuthorRepo;
import com.andreas.webapp.dao.DocumentRepo;
import com.andreas.webapp.model.Author;
import com.andreas.webapp.model.Document;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class DocumentService {
	
	static final Gson globGson = new Gson();
	@Autowired
	DocumentRepo docRepo;
	@Autowired
	AuthorRepo authRepo;
	
	static final String NODOCUMENT_ERROR_MSG = 
			"No Document found for the provided Document-ID!";
	static final String INVALID_DOCUMENTID = 
			"Invalid Document-ID provided!";
	
	/**
	 * Function used to create a new Document from a JSON string. Said JSON String 
	 * 		is checked to not be null or empty
	 * 		I decided to use JSON for Document creation specifically, because I 
	 * 			expect the Document Body in particular to be potentially longer.
	 * The JSON String must include (the function checks whether they're there):
	 * - documentID
	 * - documentTitle
	 * - documentBody
	 * - a List of Integers representing the author-IDs (must be present and must 
	 * 		not be empty)
	 * 
	 * The JSON String CAN include (function checks whether present, but document 
	 * 		creation is possible without)
	 * - a List of Integers representing document-IDs representing the references
	 * 
	 * For the lists of authors and (if existing) references, the function confirms 
	 * 		that they have ALL been previously registered
	 * @param payload - a JSON String
	 * @return - A responseEntity with the created Document (If successfully created)
	 *         - a ReponseEntity with a message informing the caller of an invalid 
	 *         		JSON String, and missing or previously non-registered attributes
	 * @throws Exception - for 
	 */
	public Object createDocument(String payload) throws Exception {
		
		//checking if data is legit
		if(payload == null || payload.equals("")) {
			return "The payload cannot be empty to create a new document!";
		}
		
		JsonObject jsonObj = globGson.fromJson(payload, JsonObject.class);
		
		String legit = isPayloadLegit(jsonObj);
		
		if(!legit.equals("")) {
			return legit;
		}
		
		//once we're sure EVERYTHING is legit, we create the entries
		int documentID = jsonObj.get("documentID").getAsInt();
		String documentTitle = jsonObj.get("documentTitle").getAsString();
		String documentBody = jsonObj.get("documentBody").getAsString();
		
		Document newDoc = new Document(documentID,documentTitle,documentBody);
		  
		
		newDoc = docRepo.save(newDoc);
		
		Set<Document> referenceSet = new HashSet<>();
		if(jsonObj.get("references")!=null) {
			JsonArray jsonArrReferences = jsonObj.get("references").getAsJsonArray();
			  
			for(JsonElement jsonEl: jsonArrReferences) {
				int newId = jsonEl.getAsInt();
				docRepo.insertIntoReferenced(newDoc.getDocumentID(),jsonEl.getAsInt());
				referenceSet.add(docRepo.findById(newId).get());
			}
		}
		
		JsonArray jsonArrAuthors = jsonObj.get("authors").getAsJsonArray();
		
		Set<Author> authorSet = new HashSet<>();
		for(JsonElement jsonEl: jsonArrAuthors) {
			int newId = jsonEl.getAsInt();
			docRepo.insertIntoAuthored(newId,newDoc.getDocumentID());
			authorSet.add(authRepo.findById(newId).get());
		}
		newDoc.setAuthors(authorSet);
		
		newDoc.setReferences(referenceSet);
		return newDoc;
		
	}
	
	private String isPayloadLegit(JsonObject jsonObj) {
		if(jsonObj.get("documentID")==null || jsonObj.get("documentTitle")==null 
				||jsonObj.get("documentBody")==null) {
			return "DocumentID, Document title, and document body must be included "
					+ "in the payload!";
		}else if(jsonObj.get("authors") == null) {
			return "A list with at least one author-ID must be included in the "
					+ "payload!";
		}
		
		JsonArray jsonArrAuthors = jsonObj.get("authors").getAsJsonArray();
		
		if(jsonArrAuthors.isEmpty()) {
			return "The author list must not be empty! Please make sure at least "
					+ "one valid author-ID included in the payload!";
		}
		
		for(JsonElement jsonEl: jsonArrAuthors) {
			if(!authRepo.existsById(jsonEl.getAsInt())) {
				return "At least one author listed is not in the system! Please make "
						+ "sure all listed authors have been registered in the "
						+ "system!";
			}
		}
		
		if(jsonObj.get("references")!=null) {
			JsonArray jsonArrReferences = jsonObj.get("references").getAsJsonArray();
			  
			for(JsonElement jsonEl: jsonArrReferences) {
				if(!docRepo.existsById(jsonEl.getAsInt())) {
					return "At least one document listed as reference is not in the "
							+ "system! Please make sure all listed documents have "
							+ "been registered in the system!";
				}
			}
		}
		
		return "";
	}

	/**
	 * Deletes the Document corresponding to the specified documentID
	 * It's checked whether the ID is legit and a Document with the ID exists
	 * Authors and references are added manually
	 * @param documentID - ID of the document you want to retrieve
	 * @return - a ReponseEntity with the retrieved Document
	 * 		   - a ReponseEntity with a message informing the caller that the ID is 
	 * 				not valid, or doesn't respond to any Document in the database
	 */
	public Object getDocumentById(int documentID) {
		
		if(documentID <= 0) {
			return INVALID_DOCUMENTID;
		}
		
		Document resultDoc;
		
		Optional<Document> optionalDoc = docRepo.findById(documentID);
		
		if(optionalDoc.isPresent()) {
			resultDoc = optionalDoc.get();
		}else {
			return NODOCUMENT_ERROR_MSG;
		}
		
		
		resultDoc.setAuthors(getAuthors(resultDoc.getDocumentID()));
		resultDoc.setReferences(getReferences(resultDoc.getDocumentID()));
		
		return resultDoc;
	}
	
	
	/**
	 * A Function to return ALL Documents in the database
	 * Authors and references are added manually
	 * @return - A ResponseEntity with a List of all retrieved Documents
	 */
	public Object getAllDocuments() {
		
		List<Document> documentResultList = this.docRepo.findAll();
		
		for(Document dcmnt:documentResultList) {
			dcmnt.setAuthors(getAuthors(dcmnt.getDocumentID()));
			dcmnt.setReferences(getReferences(dcmnt.getDocumentID()));
		}
		
		return documentResultList;
	}
	
	
	/**
	 * A function that deletes the document corresponding to the provided document-ID
	 * It is checked whether the Document-ID is valid and has an entry corresponding 
	 * 	to it in the database
	 * 
	 * @param documentID - ID of the document you want to delete
	 * @return - If successful, a ResponseEntity with a message confirming deletion i
	 * 				s returned
	 *         - If a Document-ID is invalid or doesn't have a corresponding entry, a 
	 *            ResponseEntity with a message confirming the caller thereof is 
	 *            returned
	 * @throws Exception 
	 */
	public Object deleteDocumentByID(int documentID){
		
		if(documentID <= 0) {
			return INVALID_DOCUMENTID;
		}else if(!docRepo.existsById(documentID)) {
			return "Document with that ID does not exist and can therefore"
					+ " not be deleted!";
		}
		
		authRepo.deleteByDocument(documentID);
		docRepo.deleteReferenceBy(documentID);
		
		docRepo.deleteById(documentID);
		return "Document successfully deleted!";
	}
	
	/**
	 * A function to delete ALL documents in the database
	 * @return - If successful, a ResponseEntity with a message confirming the 
	 * deletion is returned
	 */
	public Object deleteAllDocuments() {
		docRepo.deleteAll();
		return "All Documents successfully deleted!";
	}
	
	/**
	 * A function that can alter the title of an existing Document
	 * It checks that the new title is valid, and that the document-ID provided is 
	 * both valid and  has a corresponding entry
	 * 
	 * @param documentID - ID of the document whose title you want to change
	 * @param title - new title you want to change the document to
	 * @return - IF successful, the updated Document is returned in a ResponseEntity
	 *         - If the title or documentID are invalid, or there is no entry 
	 *         		corresponding to  the ID, a ResponseEntity with a message 
	 *         informing the caller thereof is returned
	 */
	public Object updateDocumentTitle(int documentID,String title) {
		
		if(documentID <= 0) {
			return INVALID_DOCUMENTID;
		}else if(title==null || title.equals("")) {
			return "Invalid new title provided!";
		}
		
		Optional<Document> result = docRepo.findById(documentID);
		if(result.isPresent()) {
			Document currDocument = result.get();
			currDocument.setDocumentTitle(title);
			currDocument = docRepo.save(currDocument);
			return currDocument;
		}else {
			return NODOCUMENT_ERROR_MSG;
		}
	}
	
	
	/**
	 * A function that can be used to update an existing document's body
	 * Therefore, the JSON String must not be null and must contain (the function 
	 * 	checks therefore):
	 * - documentID 
	 * - documentBody
	 * 
	 * I use JSON for Document update specifically, because I expect the Document 
	 * 		Body in particular to be potentially longer. Same reason as with Document 
	 * 		creation
	 * 
	 * The function also checks whether a document corresponding to the ID exists
	 * @param payload - a JSON String
	 * @return - if successful, a ResponseEntity with the newly updated Document is 
	 * 				returned
	 *         - If the document-body or ID are not present, the JSON String is 
	 *         		empty, or there is no entry corresponding to the ID, a 
	 *         		ResponseEntity with a message informing the caller thereof is 
	 *         returned
	 */
	public Object updateDocumentBody(@RequestBody String payload) {
		JsonObject jsonObj = globGson.fromJson(payload, JsonObject.class);
		
		if(payload == null || payload.equals("")) {
			return "The payload cannot be empty to update a document!";
		}else if(jsonObj.get("documentID")==null ||jsonObj.get("documentBody")==null) {
				return "DocumentID and Document-Body must be included in the payload!";
		}
		
		
		Optional<Document> result = docRepo.findById(jsonObj.get("documentID")
				.getAsInt());
		  
		if(result.isPresent()) {
			  Document currDocument = result.get();
			  currDocument.setDocumentBody(jsonObj.get("documentBody").getAsString());
			  currDocument = docRepo.save(currDocument);
			  return currDocument;
		}else {
			return NODOCUMENT_ERROR_MSG;
		}
	}
	
	/**
	 * A function that can add an author as author to a document
	 * Beforehand it checks that the provided Author and Document-IDs are valid
	 * It also checks that the entry that is to be added doesn't already exist
	 * It also confirms that both, the author and document you want to link, to exist
	 * @param documentID - ID of the dcument for which you want to add an author
	 * @param authorID - ID of the author which you want to add as author to the 
	 * 						document
	 * @return - if successful, a ResponseEntity with a message informing the caller 
	 * 				of the update is returned
	 *         - If the document-ID or author-ID are invalid, the entry already 
	 *         		exists, or either the author or document do NOT exist, a 
	 *         		ResponseEntity with a message informing the caller thereof 
	 *         		is returned
	 * @throws Exception
	 */
	public Object addAuthorToDocument(int documentID, int authorID) throws Exception {
		
		if(documentID <= 0 || authorID <=0) {
			return "Invalid Author-ID or Document-ID provided!";
		}else if(docRepo.findAuthorsOfDocument(documentID).contains(authorID)) {
			return "The author will not be added. He already is credited as an author!";
		}else if(!authRepo.existsById(authorID)) {
			return "The author does not exist in the database and can therefore "
					+ "not be added!";
		}else if(!docRepo.existsById(documentID)) {
			return "The document does not exist in the database and can therefore "
					+ "not have an author added!";
		}
		
		docRepo.insertIntoAuthored(authorID,documentID);
		return "Author successfully added as author to document!";
	}
	
	
	/**
	 * A function that can remove an author as author from a document
	 * Beforehand it checks that the provided Author and Document-IDs are valid
	 * It also checks that the entry that is to be removed actually exists
	 * @param documentID - ID of the document for which you want to remove an author
	 * @param authorID - ID of the author which you want to remove as author from the 
	 * 					document
	 * @return - if successful, a ResponseEntity with a message informing the caller 
	 * 				of the update is returned
	 *         - If the document-ID or author-ID are invalid, or the entry doesn't 
	 *         		exists, a ResponseEntity with a message informing the caller 
	 *         		thereof is returned
	 * @throws Exception
	 */
	public Object removeAuthorFromDocument(int documentID,int authorID) 
			throws Exception {
		if(documentID <= 0 || authorID <=0) {
			return "Invalid Author-ID or Document-ID provided!";
		}else if(!docRepo.findAuthorsOfDocument(documentID).contains(authorID)) {
			return "The author is not credited, and can therefore not be removed!";
		}
		
		docRepo.deleteAuthoredEntries(documentID, authorID);
		return "Author successfully removed as author from Document!";
	}
	
	
	/**
	 * A function that can add a document as reference from a document
	 * Beforehand it checks that the provided Document-IDs are valid
	 * It also checks that the entry that is to be added doesn't exist yet
	 * It also confirms that both documents you want to link already exist!
	 * @param referencingID - ID of the document that HAS references and for which 
	 * 							we are trying to add a new one
	 * @param referencedID - ID of the document that IS to be added as reference
	 * @return - if successful, a ResponseEntity with a message informing the caller 
	 * 				of the update is returned
	 *         - If the document-IDs are invalid, or the entry already exists, or one 
	 *         		of the documents does NOT exist a ResponseEntity with a message 
	 *         		informing the caller thereof is returned
	 * @throws Exception
	 */
	public Object addReferenceToDocument(int referencingID,int referencedID) 
			throws Exception {
		if(referencingID <= 0 || referencedID <=0) {
			return "Invalid Reference-IDs provided!";
		}else if(docRepo.findReferencesOfDocument(referencingID)
				.contains(referencedID)) {
			return "The reference is already listed, and can therefore not be added!";
		}else if(!docRepo.existsById(referencingID)) {
			return "The document for which you want to add a reference to does not "
					+ "exist in the database! Please make sure any document you want "
					+ "to link has been created!";
		}else if(!docRepo.existsById(referencedID)) {
			return "The document you want to add as reference does not exist in "
					+ "the database! Please make sure any document you want to link"
					+ "has been created!";
		}
		
		docRepo.insertIntoReferenced(referencingID, referencedID);
		return "Document successfully added as reference!";
	}
	
	
	/**
	 * A function that can remove a document as reference from another document
	 * Beforehand it checks that the provided Document-IDs are valid
	 * It also checks that the entry that is to be added actually exists
	 * @param referencingID - ID of the document that HAS references and for which 
	 * 							we are trying to remove one
	 * @param referencedID - ID of the document that IS to be removed as reference
	 * @return - if successful, a ResponseEntity with a message informing the caller 
	 * 				of the update is returned
	 *         - If the document-IDs are invalid, or the entry doesn't exist, a 
	 *         		ResponseEntity with a message informing the caller thereof is 
	 *         		returned
	 * @throws Exception
	 */
	public Object removeReferenceFromDocument(int referencingID,int referencedID) 
			throws Exception {
		if(referencingID <= 0 || referencedID <=0) {
			return "Invalid Reference-IDs provided!";
		}else if(!docRepo.findReferencesOfDocument(referencingID)
				.contains(referencedID)) {
			return "The reference is not listed, and can therefore not be removed!";
		}
		
		docRepo.deleteReferencesEntries(referencingID, referencedID);
		return "Document successfully removed as reference!";
	}
	
	/**
	 * A function that returns all the Authors credited as authors in the 
	 * 		document of the provided ID
	 * @param documentID - The ID of the document for which you want the Authors
	 * @return - a set of Authors corresponding to the authors who authored 
	 * 				the specified document
	 */
	Set<Author> getAuthors(int documentID){
		Set<Integer> authorInt = docRepo.findAuthorsOfDocument(documentID);

		Set<Author> authorSet = new HashSet<>();
		for(int i:authorInt) {
			Author currAuthor = authRepo.findById(i).get();
			authorSet.add(currAuthor);
		}
		
		return authorSet; 
	}
	
	/**
	 * A function that returns all the Documents given as references in the document 
	 * 	of the provided ID
	 * @param documentID - The ID of the document for which you want the
	 * 						 References/referencing documents
	 * @return - a set of Documents corresponding to the references of the specified 
	 * 				document
	 */
	Set<Document> getReferences(int documentID){
		Set<Integer> referenceInt = docRepo.findReferencesOfDocument(documentID);
		Set<Document> referenceSet = new HashSet<>();
		for(int i:referenceInt) {
			Document currDocument = docRepo.findById(i).get();
			currDocument.setReferences(null);
			referenceSet.add(currDocument);
		}
		return referenceSet;
	}
}
