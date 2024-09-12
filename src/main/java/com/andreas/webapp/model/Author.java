package com.andreas.webapp.model;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;

@Entity
public class Author {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int authorID;
	private String first_Name;
	private String last_Name;
	
	/**
	 * exists to be able to link Authors to Documents as authors
	 * partially responsible for auto-generating the 'AUTHORED_BY' table
	 * Authors to Document have a many to many relationship, e.g.
	 * One author can author several documents
	 * One document can be authored by several authors
	 */
	@ManyToMany(mappedBy = "authors")
	@Transient
	private Set<Document> authored;
	
	public Author() {
		
	}
	
	public Author(String first_Name, String last_Name) {
		this.first_Name = first_Name;
		this.last_Name = last_Name;
	}
	
	public Author(int authorID, String first_Name, String last_Name) {
		this.authorID = authorID;
		this.first_Name = first_Name;
		this.last_Name = last_Name;
	}
	
	public int getAuthorID() {
		return authorID;
	}
	
	public void setAuthorID(int authorID) {
		this.authorID = authorID;
	}
	public String getFirstName() {
		return first_Name;
	}
	
	public void setFirstName(String firstName) {
		this.first_Name = firstName;
	}
	
	public String getLastName() {
		return last_Name;
	}
	
	public void setLastName(String lastName) {
		this.last_Name = lastName;
	}

	@Override
	public int hashCode() {
		return Objects.hash(authorID, first_Name, last_Name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		return authorID == other.authorID && Objects.equals(first_Name, 
				other.first_Name) && Objects.equals(last_Name, other.last_Name);
	}

	@Override
	public String toString() {
		return "Author [authorID=" + authorID + ", firstName=" + first_Name + 
				", lastName=" + last_Name + "]";
	}
}
