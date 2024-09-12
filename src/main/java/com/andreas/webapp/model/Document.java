package com.andreas.webapp.model;

import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;

@Entity
public class Document {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int documentID;
	private String document_Title;
	private String document_Body;
	@ManyToMany
	@JoinTable(
	name = "authored_by", 
	joinColumns = @JoinColumn(name = "documentID"), 
	inverseJoinColumns = @JoinColumn(name = "authorID"))
	private Set<Author> authors;
	@ManyToMany
	@JoinTable(
	name = "referenced_by", 
	joinColumns = @JoinColumn(name = "referencing_document",
		referencedColumnName = "documentID"), 
	inverseJoinColumns = @JoinColumn(name = "document_referenced",
		referencedColumnName = "documentID"))
	private Set<Document> references;
	@ManyToMany(mappedBy = "references")
	private Set<Document> referencedBy;  
	
	public Document() {
		
	}
	
	public Document(String document_Title, String document_Body) {
		this.document_Title = document_Title;
		this.document_Body = document_Body;
	}
	
	public Document(int documentID, String document_Title, String document_Body) {
		this.documentID = documentID;
		this.document_Title = document_Title;
		this.document_Body = document_Body;
	}

	public Document(int documentID, String document_Title, String document_Body, 
			Set<Author> authors,Set<Document> references) {
		this.documentID = documentID;
		this.document_Title = document_Title;
		this.document_Body = document_Body;
		this.authors = authors;
		this.references = references;
	}

	public int getDocumentID() {
		return documentID;
	}

	public void setDocumentID(int documentID) {
		this.documentID = documentID;
	}

	public String getDocumentTitle() {
		return document_Title;
	}

	public void setDocumentTitle(String document_Title) {
		this.document_Title = document_Title;
	}

	public String getDocumentBody() {
		return document_Body;
	}

	public void setDocumentBody(String document_Body) {
		this.document_Body = document_Body;
	}

	public Set<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
	}

	public Set<Document> getReferences() {
		return references;
	}

	public void setReferences(Set<Document> references) {
		this.references = references;
	}
	
	private String flattenReferences() {
		if(this.references!=null && !this.references.isEmpty()) {
			StringBuilder sbBuilder = new StringBuilder();
			this.references.forEach(x -> sbBuilder.append("; "+x.getDocumentTitle()));
			return sbBuilder.toString();
		}
		return "";
		
	}
	
	private String flattenAuthors() {
		StringBuilder sbBuilder = new StringBuilder();
		this.authors.forEach(x -> sbBuilder.append("; "+x.getLastName()+","
				+x.getFirstName()));
		return sbBuilder.toString();
		
	}

	@Override
	public String toString() {
		return "Document [documentID=" + documentID + ", document_Title=" +
				document_Title + ", document_Body=" + document_Body + ", authors=" 
				+ flattenAuthors() + ", references=" + flattenReferences() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(authors, document_Body, documentID, document_Title, 
				references);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Document other = (Document) obj;
		return Objects.equals(authors, other.authors) && Objects.equals(document_Body,
				other.document_Body)
				&& Objects.equals(documentID, other.documentID) && Objects.equals(
						document_Title, other.document_Title)
				&& Objects.equals(references, other.references);
	}
}
