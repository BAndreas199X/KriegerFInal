package com.andreas.webapp.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.andreas.webapp.dao.AuthorRepo;
import com.andreas.webapp.dao.DocumentRepo;
import com.andreas.webapp.model.Author;
import com.andreas.webapp.model.Document;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {
	
	@Mock
    private DocumentRepo docRepo;
	@Mock
    private AuthorRepo authRepo;

    @InjectMocks
    private DocumentService docService;
    
    static String MOCK_PAYLOAD;
	static final Author MOCK_AUTHOR1 = new Author(1,"Joe","Generic");
	static final Author MOCK_AUTHOR2 = new Author(2,"Max","Musterman");
	static final List<Author> MOCK_AUTHORS = Arrays.asList(MOCK_AUTHOR1,MOCK_AUTHOR2);
	static final Set<Author> AUTHORS_SET = new HashSet<>(MOCK_AUTHORS);
	static final Document MOCK_DOCUMENT_BASIC = 
			new Document(1,"Mock title","Mock Body");
	static final Document MOCK_DOCUMENT_SPARE = 
			new Document(2,"Spare title","Spare Body");
	static final Document MOCK_DOCUMENT_FULL = 
			new Document(3,"Generic title","Generic Body");
	static List<Document> MOCK_DOCUMENTS;
	
	static ResponseEntity<Object> ASPIRING_RESULT;
	static final List<Document> DOCUMENT_LIST = new ArrayList<>();
	static ResponseEntity<Object> RESULT_LIST;
	static final Map<String,Object> PAYLOAD_MAP = new HashMap<>();
	static final Set<Integer> AUTHOR_SET = new HashSet<>();
	static final Set<Integer> REFERENCE_SET = new HashSet<>();
	
	static final int MOCK_ID = 3;
	
	@BeforeAll
	static void setUpBeforeClass() {
		AUTHOR_SET.add(1);
		AUTHOR_SET.add(2);
		
		REFERENCE_SET.add(1);
		
		PAYLOAD_MAP.put("documentID", 3);
		PAYLOAD_MAP.put("documentTitle", "Generic title");
		PAYLOAD_MAP.put("documentBody", "Generic Body");
		PAYLOAD_MAP.put("authors", AUTHOR_SET);
		PAYLOAD_MAP.put("references", REFERENCE_SET);
		
		MOCK_PAYLOAD = asJsonString(PAYLOAD_MAP);
		
		
		Set<Document> references = new HashSet<>();
		references.add(MOCK_DOCUMENT_BASIC);
		MOCK_DOCUMENT_FULL.setReferences(references);
		MOCK_DOCUMENT_FULL.setAuthors(new HashSet<>(MOCK_AUTHORS));
		
		ASPIRING_RESULT = new ResponseEntity<>(MOCK_DOCUMENT_FULL,HttpStatus.OK);
		
		DOCUMENT_LIST.add(MOCK_DOCUMENT_BASIC);
		DOCUMENT_LIST.add(MOCK_DOCUMENT_SPARE);
		DOCUMENT_LIST.add(MOCK_DOCUMENT_FULL);
		RESULT_LIST = new ResponseEntity<>(DOCUMENT_LIST,HttpStatus.OK);
		
		MOCK_DOCUMENTS = Arrays.asList(
				MOCK_DOCUMENT_BASIC,MOCK_DOCUMENT_SPARE,MOCK_DOCUMENT_FULL);
	}
	
	@Test
	void testCreateDocumentService() throws Exception {
		Document testDocument = new Document(3,"Generic title","Generic Body");
		Mockito.when(this.docRepo.save(any(Document.class))).thenReturn(testDocument);
		Mockito.when(this.docRepo.existsById(any(Integer.class))).thenReturn(true);
		Mockito.when(this.authRepo.existsById(any(Integer.class))).thenReturn(true);
		
		Mockito.when(docRepo.findById(1)).thenReturn(Optional.of(MOCK_DOCUMENT_BASIC));
		
		Mockito.when(authRepo.findById(1)).thenReturn(Optional.of(MOCK_AUTHOR1));
		Mockito.when(authRepo.findById(2)).thenReturn(Optional.of(MOCK_AUTHOR2));
		
		
		Object result = docService.createDocument(MOCK_PAYLOAD);
		
		
		Document d = (Document) result;
		
		assertEquals(MOCK_DOCUMENT_FULL,d);
	}
	
	@Test
	void testCreateDocumentServiceAllErrorScenarios() throws Exception {
		
		//checks for missing document-ID
		PAYLOAD_MAP.remove("documentID");
		Object result = (String) docService.createDocument(asJsonString(PAYLOAD_MAP));
		assertEquals(result,"DocumentID, Document title, and document body must be "
				+ "included in the payload!");
		
		//checks for missing document-Title
		PAYLOAD_MAP.remove("documentTitle");
		PAYLOAD_MAP.put("documentID", 3);
		result = (String) docService.createDocument(asJsonString(PAYLOAD_MAP));
		assertEquals(result,"DocumentID, Document title, and document body must be "
				+ "included in the payload!");
		
		//checks for missing document-Body
		PAYLOAD_MAP.remove("documentBody");
		PAYLOAD_MAP.put("documentTitle", "Generic title");
		result = (String) docService.createDocument(asJsonString(PAYLOAD_MAP));
		assertEquals(result,"DocumentID, Document title, and document body must be "
				+ "included in the payload!");
		
		//checks for missing author-List
		PAYLOAD_MAP.put("documentBody", "Generic Body");
		PAYLOAD_MAP.remove("authors");
		result = (String) docService.createDocument(asJsonString(PAYLOAD_MAP));
		assertEquals(result,"A list with at least one author-ID must be "
				+ "included in the payload!");
		
		//checks for empty author-List
		PAYLOAD_MAP.put("authors", new HashSet<>());
		result = (String) docService.createDocument(asJsonString(PAYLOAD_MAP));
		assertEquals(result,"The author list must not be empty! Please make sure at"
				+ " least one valid author-ID included in the payload!");
		
		//invalid Author-ID in author-list
		Mockito.when(this.authRepo.existsById(any(Integer.class))).thenReturn(false);
		PAYLOAD_MAP.put("authors", AUTHOR_SET);
		result = (String) docService.createDocument(asJsonString(PAYLOAD_MAP));
		assertEquals(result,"At least one author listed is not in the system! Please "
				+ "make sure all listed authors have been registered in the system!");

		//invalid Document-ID in reference-list
		Mockito.when(this.docRepo.existsById(any(Integer.class))).thenReturn(false);
		Mockito.when(this.authRepo.existsById(any(Integer.class))).thenReturn(true);
		result = (String) docService.createDocument(asJsonString(PAYLOAD_MAP));
		assertEquals(
				result,"At least one document listed as reference is not in the system! "
				+ "Please make sure all listed documents have been registered "
				+ "in the system!");
	}

	@Test
	void testGetDocumentByIdService() {
		Mockito.when(this.docRepo.findById(MOCK_ID)).thenReturn(Optional.of(
				new Document(3,"Generic title","Generic Body")));
		Mockito.when(docRepo.findById(1)).thenReturn(Optional.of(MOCK_DOCUMENT_BASIC));
		
		Mockito.when(authRepo.findById(1)).thenReturn(Optional.of(MOCK_AUTHOR1));
		Mockito.when(authRepo.findById(2)).thenReturn(Optional.of(MOCK_AUTHOR2));
		
		Mockito.when(docRepo.findAuthorsOfDocument(MOCK_ID)).thenReturn(AUTHOR_SET);
		Mockito.when(docRepo.findReferencesOfDocument(MOCK_ID))
		.thenReturn(REFERENCE_SET);	
		
		Object result = this.docService.getDocumentById(MOCK_ID);
		
		Document d = (Document) result;
		assertEquals(MOCK_DOCUMENT_FULL,d);
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void testGetAllDocumentsService() {
		Mockito.when(this.docRepo.findAll()).thenReturn(DOCUMENT_LIST);
		
		Mockito.when(docRepo.findAuthorsOfDocument(any(Integer.class)))
		.thenReturn(AUTHOR_SET);
		Mockito.when(authRepo.findById(1)).thenReturn(Optional.of(MOCK_AUTHOR1));
		Mockito.when(authRepo.findById(2)).thenReturn(Optional.of(MOCK_AUTHOR2));
		
		Mockito.when(docRepo.findReferencesOfDocument(any(Integer.class)))
		.thenReturn(
				REFERENCE_SET);
		Mockito.when(docRepo.findById(1)).thenReturn(
				Optional.of(MOCK_DOCUMENT_BASIC));
		
		Object result = this.docService.getAllDocuments();
		List<Document> docList = (List<Document>) result;
		
		assertEquals(3,docList.size());
	}

	
	@Test
	void testDeleteDocumentByIDService() {
		Mockito.when(docRepo.existsById(MOCK_ID)).thenReturn(true);
		
		String s = (String) docService.deleteDocumentByID(MOCK_ID);
		assertEquals("Document successfully deleted!",s);
	}
	
	@Test
	void testDeleteDocumentByIDServiceFalse() {
		Mockito.when(docRepo.existsById(MOCK_ID)).thenReturn(false);
		
		String s = (String) docService.deleteDocumentByID(MOCK_ID);
		assertEquals(
				"Document with that ID does not exist and can therefore not be "
				+ "deleted!",s);
	}

	@Test
	void testDeleteAllDocumentsService() {
		String s = (String) docService.deleteAllDocuments();
		assertEquals("All Documents successfully deleted!",s);
	}

	
	@Test
	void testUpdateDocumentTitleService() {
		Mockito.when(this.docRepo.findById(2)).thenReturn(Optional.of(
				MOCK_DOCUMENT_SPARE));

		Document newDocument = new Document(2,"Updated title","Spare Body");
		Mockito.when(this.docRepo.save(any(Document.class))).thenReturn(newDocument);
		
		Document resultDoc = (Document) docService.updateDocumentTitle(2, 
				"Updated title");
		
		assertEquals(newDocument,resultDoc);
		assertEquals("Updated title",resultDoc.getDocumentTitle());		
	}

	
	@Test
	void testUpdateDocumentBodyService() {
		
		Map<String,Object> newPayload = new HashMap<>();
		newPayload.put("documentID", 2);
		newPayload.put("documentBody", "Updated Body");
		
		Document newDocument = new Document(2,"Spare title","Updated Body");
		
		Mockito.when(this.docRepo.findById(2)).thenReturn(Optional.of(
				MOCK_DOCUMENT_SPARE));
		Mockito.when(this.docRepo.save(any(Document.class))).thenReturn(newDocument);
		
		Document d = (Document) docService.updateDocumentBody(
				asJsonString(newPayload));
		
		assertEquals(d,newDocument);
	}

	
	@Test
	void testAddAuthorToDocumentService() throws Exception {
		Mockito.when(docRepo.findAuthorsOfDocument(2)).thenReturn(new HashSet<>());
		Mockito.when(docRepo.existsById(2)).thenReturn(true);
		Mockito.when(authRepo.existsById(1)).thenReturn(true);
		
		String s = (String) docService.addAuthorToDocument(2, 1);
		
		assertEquals("Author successfully added as author to document!",s);
		
	}
	
	
	@Test
	void testRemoveAuthorFromDocumentService() throws Exception {
		Mockito.when(docRepo.findAuthorsOfDocument(2)).thenReturn(AUTHOR_SET);
		
		String s = (String) docService.removeAuthorFromDocument(2, 1);
		
		assertEquals("Author successfully removed as author from Document!",s);
	}

	
	@Test
	void testAddReferenceToDocumentService() throws Exception {
		Mockito.when(docRepo.existsById(2)).thenReturn(true);
		Mockito.when(docRepo.existsById(1)).thenReturn(true);
		Mockito.when(docRepo.findReferencesOfDocument(2)).thenReturn(new HashSet<>());
		
		String s = (String) docService.addReferenceToDocument(2, 1);
		
		assertEquals("Document successfully added as reference!",s);
		
	}

	
	@Test
	void testRemoveReferenceFromDocumentService() throws Exception {
		Mockito.when(docRepo.findReferencesOfDocument(2)).thenReturn(REFERENCE_SET);
		String s = (String)  docService.removeReferenceFromDocument(2, 1);
		
		assertEquals("Document successfully removed as reference!",s);
	}
	
	
	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

}
