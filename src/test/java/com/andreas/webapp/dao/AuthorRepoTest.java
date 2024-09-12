package com.andreas.webapp.dao;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.andreas.webapp.model.Author;

@DataJpaTest
class AuthorRepoTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
    private AuthorRepo authRepo;
	static final Author MOCK_AUTHOR = new Author("Joe","Generic");
	
	@BeforeEach
	void setUpBeforeClass() {
		entityManager.clear();
		Author athr = new Author("Joe","Generic");
	    entityManager.persist(athr);
	}

	@Test
	void testFindByNames() {
		List<Author> resultAuth = authRepo.findByNames("Joe", "Generic");
		
		int ID = fetchID();
		
		assertFalse(resultAuth==null);
		assertEquals(1,resultAuth.size());
		assertEquals(resultAuth.get(0).getAuthorID(),ID);
		assertEquals("Joe",resultAuth.get(0).getFirstName());
		assertEquals("Generic",resultAuth.get(0).getLastName());
	}

	@Test
	void testFindAll() {
		Author athr = new Author("Max","Mustermann");
	    entityManager.persist(athr);
	    
	    int ID = fetchID();
	    
	    athr.setAuthorID(ID);
	    MOCK_AUTHOR.setAuthorID(ID-1);
	    List<Author> resultAuth = authRepo.findAll();
	    assertEquals(2,resultAuth.size());
	    assertEquals(MOCK_AUTHOR,resultAuth.get(0));
	    assertEquals(athr,resultAuth.get(1));
	}

	
	@Test
	void testSave() {
		Author athr = new Author("Max","Mustermann");
		
		athr = authRepo.save(athr);
		
		assertFalse(athr.getAuthorID()==0);
		
		Optional<Author> resultAuth = authRepo.findById(athr.getAuthorID());
		assertTrue(resultAuth.isPresent());
		assertEquals(resultAuth.get(),athr);
	}

	
	@Test
	void testFindById() {
		Author athr = new Author("Max","Mustermann");
	    entityManager.persist(athr);
	    
	    int ID = fetchID();
	    athr.setAuthorID(ID);
	    
	    Optional<Author> resultAuth = authRepo.findById(ID);
	    assertTrue(resultAuth.isPresent());
		assertEquals(resultAuth.get(),athr);
	}

	
	@Test
	void testExistsById() {
		int ID = fetchID();
		boolean result = authRepo.existsById(ID);
		
		assertTrue(result);
	}

	
	

	@Test
	void testDeleteById() {
		int ID = fetchID();
		
		authRepo.deleteById(ID);
		
		boolean result = authRepo.existsById(ID);
		
		assertFalse(result);
	}

	
	@Test
	void testDeleteAll() {
		assertEquals(1,authRepo.count());
		authRepo.deleteAll();
		
		assertEquals(0,authRepo.count());
	}

	public int fetchID() {
		List<Author> authList = authRepo.findAll();
		Optional<Integer> max = authList.stream().map(Author::getAuthorID)
				.max(Comparator.comparing(x->x));
		
		return max.get();
	}
}
