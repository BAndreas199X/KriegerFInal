package com.andreas.webapp.controllers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.andreas.webapp.dao.AuthorRepo;
import com.andreas.webapp.dao.DocumentRepo;
import com.andreas.webapp.model.Author;
import com.andreas.webapp.model.Document;
import com.andreas.webapp.services.AuthorService;
import com.andreas.webapp.services.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(DocumentController.class)
@AutoConfigureMockMvc(addFilters = false)
class DocumentControllerTest {

	@Autowired
    private MockMvc mockMvc;
	@MockBean
	private DocumentService docService;
	@MockBean
	private AuthorService authService;
	@MockBean
	private DocumentRepo docRepo;
	@MockBean
	private AuthorRepo authRepo;
	@MockBean
	private KafkaTemplate<String, String> kafkaTemplateMock;
	
	static String MOCK_PAYLOAD;
	final static Author MOCK_AUTHOR1 = new Author(100,"Joe","Generic");
	final static Author MOCK_AUTHOR2 = new Author(101,"Max","Musterman");
	final static List<Author> MOCK_AUTHORS = Arrays.asList(MOCK_AUTHOR1,MOCK_AUTHOR2);
	final static Set<Author> AUTHORS_SET = new HashSet<>(MOCK_AUTHORS);
	final static Document MOCK_DOCUMENT_BASIC = 
			new Document(1,"Mock title","Mock Body");
	final static Document MOCK_DOCUMENT_SPARE = 
			new Document(2,"Spare title","SPare Body");
	final static Document MOCK_DOCUMENT_FULL = 
			new Document(3,"Generic title","Generic Body");
	//static ResponseEntity<Object> ASPIRING_RESULT;
	final static List<Document> DOCUMENT_LIST = new ArrayList<>();
	
	final static int MOCK_ID = 1;
	
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		MOCK_DOCUMENT_FULL.setAuthors(AUTHORS_SET);
		Set<Document> references = new HashSet<>();
		references.add(MOCK_DOCUMENT_BASIC);
		MOCK_DOCUMENT_FULL.setReferences(references);
		
		//ASPIRING_RESULT = new ResponseEntity<>(MOCK_DOCUMENT_FULL,HttpStatus.OK);
		
		DOCUMENT_LIST.add(MOCK_DOCUMENT_BASIC);
		DOCUMENT_LIST.add(MOCK_DOCUMENT_FULL);
		
		MOCK_PAYLOAD = asJsonString(MOCK_DOCUMENT_FULL);
	}
	
	@Test
	void testCreateDocument() throws Exception {
		Mockito.when(this.docService.createDocument(MOCK_PAYLOAD)).thenReturn(
				MOCK_DOCUMENT_FULL);
		
		mockMvc.perform( MockMvcRequestBuilders.post("/document")
				.content(MOCK_PAYLOAD).contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	void testGetDocumentById() throws Exception {
		Mockito.when(this.docService.getDocumentById(MOCK_ID))
		.thenReturn(MOCK_DOCUMENT_FULL);
		
		mockMvc.perform(
				MockMvcRequestBuilders.get("/document/"+String.valueOf(MOCK_ID)))
			.andExpect(status().isOk());
	}

	@Test
	void testGetAllDocuments() throws Exception {
		Mockito.when(this.docService.getAllDocuments()).thenReturn(DOCUMENT_LIST);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/document"))
		.andExpect(status().isOk());
	}

	
	@Test
	void testDeleteDocumentByID() throws Exception {
		
		Mockito.when(this.docService.deleteDocumentByID(MOCK_ID)).thenReturn(
				"Document successfully deleted!");
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/document/"
				+String.valueOf(MOCK_ID))).andExpect(status().isOk());
	}

	
	@Test
	void testDeleteAllDocuments() throws Exception {
		
		Mockito.when(this.docService.deleteAllDocuments()).thenReturn(
				"All Documents successfully deleted!");
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/document"))
                .andExpect(status().isOk());
	}

	
	@Test
	void testUpdateDocumentTitle() throws Exception {
		Mockito.when(this.docService.updateDocumentTitle(MOCK_ID, "Generic title")).
		thenReturn(MOCK_DOCUMENT_FULL);
		
		String context = "/document/updateTitle/"+
				String.valueOf(MOCK_ID)+"/Generic title";
		
		mockMvc.perform(MockMvcRequestBuilders.put(context))
			.andExpect(status().isOk());
	}

	
	@Test
	void testUpdateDocumentBody() throws Exception {
		Document updateDocument = new Document(1,null,"Updated Body");
		
		Mockito.when(this.docService.updateDocumentBody(asJsonString(updateDocument))).
			thenReturn(MOCK_DOCUMENT_FULL);
		
		mockMvc.perform( MockMvcRequestBuilders.put("/document/updateBody")
				.content(asJsonString(updateDocument))
				.contentType(MediaType.APPLICATION_JSON)
			    .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	
	@Test
	void testAddAuthorToDocument() throws Exception {
		
		Mockito.when(this.docService.addAuthorToDocument(
				MOCK_ID, MOCK_AUTHOR2.getAuthorID())).
		thenReturn("Author successfully added as author to document!");
		
		String context = "/document/addAuthor/"+String.valueOf(MOCK_ID)+"/"
				+String.valueOf(MOCK_AUTHOR2.getAuthorID());
		
		mockMvc.perform(MockMvcRequestBuilders.put(context))
			.andExpect(status().isOk());
	}

	
	@Test
	void testRemoveAuthorFromDocument() throws Exception {
		
		Mockito.when(this.docService.removeAuthorFromDocument(
				MOCK_ID, MOCK_AUTHOR1.getAuthorID()))
		.thenReturn("Author successfully removed as author from Document!");
		
		String context = "/document/removeAuthor/"+String.valueOf(MOCK_ID)+"/"
				+String.valueOf(MOCK_AUTHOR1.getAuthorID());
		
		mockMvc.perform(MockMvcRequestBuilders.put(context))
			.andExpect(status().isOk());
	}

	
	@Test
	void testAddReferenceToDocument() throws Exception {
		
		Mockito.when(this.docService.addReferenceToDocument(
				MOCK_ID, MOCK_DOCUMENT_SPARE.getDocumentID())).thenReturn(
						"Document successfully added as reference!");
		
		String context = "/document/addReference/"+String.valueOf(MOCK_ID)+"/"
				+String.valueOf(String.valueOf(MOCK_DOCUMENT_SPARE.getDocumentID()));
		
		mockMvc.perform(MockMvcRequestBuilders.put(context))
			.andExpect(status().isOk());
	}

	
	@Test
	void testRemoveReferenceFromDocument() throws Exception {
		
		Mockito.when(this.docService.removeReferenceFromDocument(MOCK_ID, 
				MOCK_DOCUMENT_BASIC.getDocumentID())).thenReturn(
						"Document successfully removed as reference!");
		
		String context = "/document/removeReference/"+String.valueOf(MOCK_ID)+"/"
				+String.valueOf(String.valueOf(MOCK_DOCUMENT_BASIC.getDocumentID()));
		
		mockMvc.perform(MockMvcRequestBuilders.put(context))
			.andExpect(status().isOk());
	}
	
	public static String asJsonString(final Object obj) {
	    try {
	        return new ObjectMapper().writeValueAsString(obj);
	    } catch (Exception e) {
	        throw new RuntimeException(e);
	    }
	}

}


