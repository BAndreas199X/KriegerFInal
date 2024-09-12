package com.andreas.webapp.dao;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.andreas.webapp.model.Document;

import jakarta.transaction.Transactional;

public interface DocumentRepo extends JpaRepository<Document,Integer>{

	/**
	 * Establish the authorship of an author over a document, e.g. add the authoring 
	 * 	authors authorID and the document he authored to the AUTHORED_BY table (
	 * 	which establishes authorship)
	 * @param insertAuth - authoring author's ID
	 * @param insertDoc - authored document's ID
	 * @throws Exception
	 */
	@Modifying
    @Query(value = "INSERT INTO AUTHORED_BY(AUTHORID,DOCUMENTID) "
    		+ "VALUES (:newAuthor,:newDocument)", nativeQuery = true)
    @Transactional
    void insertIntoAuthored(@Param("newAuthor") int insertAuth, 
    		@Param("newDocument") int insertDoc) throws Exception;
	
	/**
	 * Establish a document uses another document as reference, e.g. the refering 
	 * 	document's ID and the referenced document's ID are added to table 
	 * 	REFERENCED_BY (which establishes referencing)
	 * @param referencing - The ID of the document that is using another as reference
	 * @param referenced - The ID of the document used as reference
	 * @throws Exception
	 */
	@Modifying
    @Query(value = 
    		"INSERT INTO REFERENCED_BY(DOCUMENT_REFERENCED,REFERENCING_DOCUMENT) "
    		+ "VALUES (:referenced,:referencing)", nativeQuery = true)
    @Transactional
    void insertIntoReferenced(@Param("referencing") int referencing,
    		@Param("referenced") int referenced) throws Exception;
	

	/**
	 * Select all authors (their IDs) credited as having authored a particular 
	 * 	document (with specified ID)
	 * @param documentId - ID of the document whose authors you want
	 * @return - Set of IDs of the authors who authored the documents
	 */
	@Query(value="SELECT AUTHORID FROM AUTHORED_BY "
			+ "WHERE DOCUMENTID = ?1", nativeQuery = true)
	Set<Integer> findAuthorsOfDocument(int documentId);
	
	/**
	 * SELECT all documents (their IDs) used as reference for a particular document (
	 * 	with specified ID)
	 * @param documentId
	 * @return
	 */
	@Query(value="SELECT DOCUMENT_REFERENCED "
			+ "FROM REFERENCED_BY WHERE REFERENCING_DOCUMENT = ?1", 
			nativeQuery = true)
	Set<Integer> findReferencesOfDocument(int documentId);
	
	/**
	 * Delete the authorship of an author over a particular document
	 * @param documentId - ID of the document whose author you want to remove
	 * @param authorId - ID of the author whose authorship you want to remove
	 * @throws Exception
	 */
	@Modifying
	@Query(value="DELETE FROM AUTHORED_BY a WHERE a.DOCUMENTID = :documentId AND "
			+ "a.AUTHORID = :authorId",nativeQuery = true)
	@Transactional
	void deleteAuthoredEntries(@Param("documentId") int documentId, 
			@Param("authorId") int authorId) throws Exception;
	
	/**
	 * remove a document as reference from another document
	 * @param referencing - ID of the document from which you want to remove a 
	 * 		reference
	 * @param referenced - ID of the document you want to remove as reference
	 * @throws Exception
	 */
	@Modifying
	@Query(value="DELETE FROM REFERENCED_BY WHERE REFERENCING_DOCUMENT= :referencing "
			+ "AND DOCUMENT_REFERENCED= :referenced", nativeQuery = true)
	@Transactional
	void deleteReferencesEntries(@Param("referencing") int referencing, 
			@Param("referenced") int referenced) throws Exception;
	
	/**
	 * Delete all entries that either reference the document or belong to the 
	 * document itself
	 * all reference entries belonging to that ID are removed
	 * @param documentID - ID of the document you're purging the references of
	 */
	@Modifying
	@Query(value="DELETE FROM REFERENCED_BY WHERE REFERENCING_DOCUMENT= :documentID "
			+ "OR DOCUMENT_REFERENCED= :documentID", nativeQuery = true)
	@Transactional
	void deleteReferenceBy(@Param("documentID") int documentID);
}
