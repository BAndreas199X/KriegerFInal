package com.andreas.webapp.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.andreas.webapp.model.Author;
import com.andreas.webapp.model.Document;

@DataJpaTest
class DocumentRepoTest {
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
    private DocumentRepo docRepo;
	
	@Autowired
    private AuthorRepo authRepo;
	
	static final Document MOCK_DOCUMENT = new Document(
			"Generic Title","Generic Body");

	@BeforeEach
	void setUpBeforeClass() {
		entityManager.clear();
		Author athr = new Author("Joe","Generic");
	    entityManager.persist(athr);
	    Document doc = new Document("Generic Title","Generic Body");
	    entityManager.persist(doc);
	}
	
	@Test
	void testInsertIntoAuthored() {
		
		int aID = fetchAuthorID();
		int dID = fetchDocumentID();
		assertDoesNotThrow(()->docRepo.insertIntoAuthored(aID, dID));
	}

	@Test
	void testInsertIntoReferenced() {
		
		Document doc = new Document("Fresh Title","Fresh Body");
	    entityManager.persist(doc);
	    
	    int dID = fetchDocumentID();
		
	    assertDoesNotThrow(()->docRepo.insertIntoReferenced(dID, dID-1));
	}

	@Test
	void testFindAuthorsOfDocument() throws Exception {
		int aID = fetchAuthorID();
		int dID = fetchDocumentID();
		docRepo.insertIntoAuthored(aID, dID);
		
		Set<Integer> resultSet = new HashSet<>();
		resultSet.add(aID);
		
		Set<Integer> authorSet = docRepo.findAuthorsOfDocument(dID);
		assertEquals(1,authorSet.size());
		assertEquals(authorSet,resultSet);
	}

	
	@Test
	void testFindReferencesOfDocument() throws Exception {
		Document doc = new Document("Fresh Title","Fresh Body");
	    entityManager.persist(doc);
	    
		int dID = fetchDocumentID();
		
		docRepo.insertIntoReferenced(dID, dID-1);
		
		Set<Integer> resultSet = new HashSet<>();
		resultSet.add(dID-1);
		
		Set<Integer> referenceSet = docRepo.findReferencesOfDocument(dID);
		assertEquals(1,referenceSet.size());
		assertEquals(referenceSet,resultSet);
	}

	@Test
	void testDeleteAuthoredEntries() throws Exception {
		int aID = fetchAuthorID();
		int dID = fetchDocumentID();
		docRepo.insertIntoAuthored(aID, dID);
		
		assertDoesNotThrow(()->docRepo.deleteAuthoredEntries( dID,aID));
	}

	@Test
	void testDeleteReferencesEntries() throws Exception {
		Document doc = new Document("Fresh Title","Fresh Body");
	    entityManager.persist(doc);
	    
		int dID = fetchDocumentID();
		
		docRepo.insertIntoReferenced(dID, dID-1);
		
		assertDoesNotThrow(()->docRepo.deleteReferencesEntries(dID,dID-1));
	}

	
	@Test
	void testSave() {
		Document doc = new Document("Fresh Title","Fresh Body");
		doc = this.docRepo.save(doc);
		
		assertFalse(doc.getDocumentID()==0);
		
		Optional<Document> resultDoc = docRepo.findById(doc.getDocumentID());
		assertTrue(resultDoc.isPresent());
		assertEquals(resultDoc.get(),doc);
	}

	
	@Test
	void testFindById() {
		Document doc = new Document("Fresh Title","Fresh Body");
	    entityManager.persist(doc);
	    
	    int ID = fetchDocumentID();
	    doc.setDocumentID(ID);
	    
	    Optional<Document> resultDoc = docRepo.findById(ID);
	    assertTrue(resultDoc.isPresent());
		assertEquals(resultDoc.get(),doc);
	}

	@Test
	void testExistsById() {
		int ID = fetchDocumentID();
		boolean result = docRepo.existsById(ID);
		
		assertTrue(result);
	}

	@Test
	void testFindAll() {
		Document doc = new Document("Fresh Title","Fresh Body");
	    entityManager.persist(doc);
	    
	    int ID = fetchDocumentID();
	    
	    doc.setDocumentID(ID);
	    MOCK_DOCUMENT.setDocumentID(ID-1);
	    List<Document> resultDoc = docRepo.findAll();
	    assertEquals(2,resultDoc.size());
	    assertEquals(MOCK_DOCUMENT,resultDoc.get(0));
	    assertEquals(doc,resultDoc.get(1));
	}

	@Test
	void testDeleteById() {
		int ID = fetchDocumentID();
		
		docRepo.deleteById(ID);
		
		boolean result = docRepo.existsById(ID);
		
		assertFalse(result);
	}

	@Test
	void testDeleteAll() {
		assertEquals(1,docRepo.count());
		docRepo.deleteAll();
		
		assertEquals(0,docRepo.count());
	}
	
	public int fetchAuthorID() {
		List<Author> authList = authRepo.findAll();
		Optional<Integer> max = authList.stream().map(Author::getAuthorID)
				.max(Comparator.comparing(x->x));
		
		return max.get();
	}
	
	public int fetchDocumentID() {
		List<Document> docList = docRepo.findAll();
		Optional<Integer> max = docList.stream().map(Document::getDocumentID)
				.max(Comparator.comparing(x->x));
		
		return max.get();
	}

}
