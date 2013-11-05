package edu.sjsu.cmpe.procurement.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.net.URL;
import java.util.List;

public class Book {




    @JsonProperty
    private List<Book> shipped_books;

    @JsonProperty
    private String category;

    @JsonProperty
    private URL coverimage;

    @JsonProperty
    private long isbn;

    @JsonProperty
    private String title;

    public List<Book> getShipped_books() {
        return shipped_books;
    }

    public void setShipped_books(List<Book> shipped_books) {
        this.shipped_books = shipped_books;
    }

    public URL getCoverimage() {
        return coverimage;
    }

    public void setCoverimage(URL coverimage) {
        this.coverimage = coverimage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    // add more fields here

    /**
     * @return the isbn
     */
    public long getIsbn() {
	return isbn;
    }

    /**
     * @param isbn
     *            the isbn to set
     */
    public void setIsbn(long isbn) {
	this.isbn = isbn;
    }

    /**
     * @return the title
     */
    public String getTitle() {
	return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
	this.title = title;
    }
}
