package com.andreas.webapp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.andreas.webapp.dao.AuthorRepo;
import com.andreas.webapp.model.Author;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

	static final Author MOCK_AUTHOR1 = new Author(100,"Joe","Generic");
	static final Author MOCK_AUTHOR2 = new Author(101,"Max","Musterman");
	static final List<Author> MOCK_AUTHORS = Arrays.asList(MOCK_AUTHOR1,MOCK_AUTHOR2);
	static final int MOCK_ID = 1;
	
	@Mock
    private AuthorRepo authRepo;

    @InjectMocks
    private AuthorService authService;
	@Test
	void testCreateAuthorService()  {
		
		Mockito.when(this.authRepo.save(Mockito.any(Author.class)))
        .thenReturn(new Author(1,"Joe","Generic"));
		
		Author a = (Author) authService.createAuthor(MOCK_AUTHOR1);
		
		assertEquals(a.getClass(), Author.class);
		assertEquals(1, a.getAuthorID());
		assertEquals("Joe",a.getFirstName());
		assertEquals("Generic",a.getLastName());
	}

	@Test
	void testGetAuthorByIdService() {
		
		Mockito.when(this.authRepo.findById(MOCK_ID)).thenReturn(
				Optional.of(new Author(1,"Joe","Generic")));
		
		Author a = (Author) authService.getAuthorById(MOCK_ID);
		
		assertEquals(a.getClass(), Author.class);
		assertEquals(1, a.getAuthorID());
		assertEquals("Joe",a.getFirstName());
		assertEquals("Generic",a.getLastName());
	}
	
	
	@Test
	void testGetAllAuthorsService() {
		Mockito.when(this.authRepo.findAll()).thenReturn(MOCK_AUTHORS);
		
		@SuppressWarnings("unchecked")
		List<Author> a = (List<Author>) authService.getAllAuthors();
		
		assertTrue(a.size()==2);
		assertTrue(a.get(0).getAuthorID()==100);
		assertTrue(a.get(1).getAuthorID()==101);
	}
	
	
	@Test
	void testGetByNamesService() {
		
		Mockito.when(this.authRepo.findByNames("abc", "xyz")).
		thenReturn(MOCK_AUTHORS);
		
		@SuppressWarnings("unchecked")
		List<Author> a = (List<Author>) authService.getByNames("abc", "xyz");
		
		assertTrue(a.size()==2);
		assertTrue(a.get(0).getAuthorID()==100);
		assertTrue(a.get(1).getAuthorID()==101);
	}

	
	@Test
	void testDeleteAuthorByIdService()  {
		Mockito.when(authRepo.existsById(MOCK_ID)).thenReturn(true);
		
		String s = (String) authService.deleteAuthorById(MOCK_ID);
		assertEquals("Author successfully deleted!",s);
	}
	
	@Test
	void testDeleteAuthorByIdServiceFalse() {
		Mockito.when(authRepo.existsById(MOCK_ID)).thenReturn(false);
		
		String s = (String) authService.deleteAuthorById(MOCK_ID);
		assertEquals("No Author found for the provided Author-ID!",s);
	}

	
	@Test
	void testDeleteAllService() {
		String s = (String) authService.deleteAll();
		assertEquals("All Authors successfully deleted!",s);
	}

	@Test
	void testUpdateFirstNameService() {
		
		Mockito.when(this.authRepo.findById(MOCK_ID)).thenReturn(
				Optional.of(new Author(1,"Joe","Generic")));
		
		Mockito.when(this.authRepo.save(Mockito.any(Author.class)))
        .thenReturn(new Author(1,"Eoj","Generic"));
		
		Author a = (Author) authService.updateFirstName(MOCK_ID, "Eoj");
		
		assertEquals(a.getClass(), Author.class);
		assertEquals(1, a.getAuthorID());
		assertEquals("Eoj",a.getFirstName());
		assertFalse(a.getFirstName().equals("Joe"));
	}

	@Test
	void testUpdateLastNameService(){
		Mockito.when(this.authRepo.findById(MOCK_ID)).thenReturn(
				Optional.of(new Author(1,"Joe","Generic")));
		
		Mockito.when(this.authRepo.save(Mockito.any(Author.class)))
        .thenReturn(new Author(1,"Joe","Cireneg"));
		
		Author a = (Author) authService.updateLastName(MOCK_ID, "Eoj");
		
		assertEquals(a.getClass(), Author.class);
		assertEquals(1, a.getAuthorID());
		assertEquals("Cireneg",a.getLastName());
		assertFalse(a.getLastName().equals("Generic"));
	}
}
