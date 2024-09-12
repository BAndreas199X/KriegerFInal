package com.andreas.webapp.controllers;

import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import com.andreas.webapp.dao.AuthorRepo;
import com.andreas.webapp.dao.DocumentRepo;
import com.andreas.webapp.model.Author;
import com.andreas.webapp.services.AuthorService;
import com.andreas.webapp.services.DocumentService;

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthorControllerTest {

	static final int MOCK_ID = 1;
	static final Author MOCK_AUTHOR1 = new Author(100,"Joe","Generic");
	static final  Author MOCK_AUTHOR2 = new Author(101,"Max","Musterman");
	static final List<Author> MOCK_AUTHORS = Arrays.asList(MOCK_AUTHOR1,MOCK_AUTHOR2);
	@MockBean
    AuthorService authService;
	
	@MockBean
    DocumentService docService;
	
	@MockBean
    DocumentRepo docRepo;
	
	@MockBean
    AuthorRepo authRepo;
	
	@MockBean
	private KafkaTemplate<String, String> kafkaTemplateMock;

	@Autowired
    private MockMvc mockMvc;
	
	@Test
	void testCreateAuthor() throws Exception {
		
		
		Mockito.when(this.authService.createAuthor(any(Author.class)))
			.thenReturn(MOCK_AUTHOR1);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/author").param("authorID", 
				String.valueOf(MOCK_ID)).param("firstName", 
						MOCK_AUTHOR1.getFirstName())
				.param("lastName", MOCK_AUTHOR1.getLastName()))
				.andExpect(status().isOk());
	}
	
	
	@Test
	void testGetAuthorById() throws Exception {
		
		Mockito.when(this.authService.getAuthorById(MOCK_ID))
		.thenReturn(MOCK_AUTHOR1);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/author/"+String.valueOf(MOCK_ID)))
			.andExpect(status().isOk());
	}
	
	
	@Test
	void testGetAllAuthors() throws Exception {
		
		Mockito.when(this.authService.getAllAuthors()).thenReturn(MOCK_AUTHORS);
		
		mockMvc.perform(MockMvcRequestBuilders.get("/author"))
		.andExpect(status().isOk());
		
	}

	
	@Test
	void testGetByNames() throws Exception {
		Mockito.when(this.authService.getByNames("Joe","Generic"))
		.thenReturn(MOCK_AUTHORS);
		String context = 
				"/author/"+MOCK_AUTHOR1.getFirstName()+"/"+MOCK_AUTHOR1.getLastName();
		mockMvc.perform(MockMvcRequestBuilders.get(context))
		.andExpect(status().isOk());
	}

	@Test
	void testDeleteAuthorById() throws Exception {
		
		Mockito.when(this.authService.deleteAuthorById(MOCK_ID)).thenReturn(
				"Author successfully deleted!");
		
		mockMvc.perform(
				MockMvcRequestBuilders.delete("/author/"+String.valueOf(MOCK_ID)))
		.andExpect(status().isOk());
		
	}

	@Test
	void testDeleteAll() throws Exception {
		
		Mockito.when(this.authService.deleteAll())
		.thenReturn("All Authors successfully deleted!");
		
		mockMvc.perform(MockMvcRequestBuilders.delete("/author"))
                .andExpect(status().isOk());
	}

	@Test
	void testUpdateFirstName() throws Exception {
		Mockito.when(this.authService.updateFirstName(
				MOCK_ID, MOCK_AUTHOR1.getFirstName()))
		.thenReturn(MOCK_AUTHOR1);
		
		String context = 
				"/author/updateFirstName/"+
		String.valueOf(MOCK_ID)+"/"+MOCK_AUTHOR1.getFirstName();
		
		mockMvc.perform(MockMvcRequestBuilders.put(context))
		.andExpect(status().isOk());
	}
	
	@Test
	void testUpdateLastName() throws Exception {
		Mockito.when(this.authService.updateLastName(
				MOCK_ID, MOCK_AUTHOR1.getLastName())).thenReturn(MOCK_AUTHOR1);
		
		String context = "/author/updateLastName/"+
		String.valueOf(MOCK_ID)+"/"+MOCK_AUTHOR1.getLastName();
		
		mockMvc.perform(MockMvcRequestBuilders.put(context))
		.andExpect(status().isOk());
	}

}
