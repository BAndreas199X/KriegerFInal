package com.andreas.webapp.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.andreas.webapp.model.Author;

import jakarta.transaction.Transactional;

public interface AuthorRepo extends JpaRepository<Author,Integer>{

	
	/**
	 * returns a list of all authors whose first or last names match the input
	 * the input must not be complete, it's sufficient if the input is contained
	 * 
	 * first, it is checked that the provided names are not empty
	 * @param firstName - first name of the author we are searching for
	 * @param lastName - last name of the author we are searching for
	 * @return - List of authors whose names matched the input
	 */
	@Query(value = "SELECT * FROM AUTHOR WHERE "
			+ "FIRST_NAME LIKE %:firstName% OR LAST_NAME LIKE %:lastName%", 
			nativeQuery = true)
	List<Author> findByNames(@Param("firstName") String firstName,
			@Param("lastName")String lastName);
	
	/**
	 * Deletes all entries in AUTHORED_BY belonging to a particular author
	 * e.g. his authorship of all documents is removed
	 * @param authorId - ID of th author whose authoring credits you're removing
	 */
	@Modifying
	@Query(value="DELETE FROM AUTHORED_BY a WHERE a.AUTHORID = :authorId", 
	nativeQuery = true)
	@Transactional
	void deleteAuthoredBy(@Param("authorId") int authorId);
	
	/**
	 * Deletes all entries in AUTHORED_BY assigned to a particular document
	 * e.g. his authorship of all authors of that document is removed
	 * @param documentId - ID of the document whose authors you are deleting
	 */
	@Modifying
	@Query(value="DELETE FROM AUTHORED_BY a WHERE a.DOCUMENTID = :documentID", 
	nativeQuery = true)	  
	@Transactional
	void deleteByDocument(@Param("documentID") int documentID);
	
	/**
	 * fetches the IDs of all documents a particular author has authored
	 * @param authorId - ID of the author whose documents/document-IDs you want 
	 * 	to fetch
	 * @return - List of IDs of all documents the author has authored
	 */
	@Query(value = "SELECT DISTINCT DOCUMENTID FROM AUTHORED_BY WHERE "
			+ "AUTHORID = :authorId",nativeQuery = true)
	List<Integer> findAuthoredDocuments(@Param("authorId") int authorId);
}
