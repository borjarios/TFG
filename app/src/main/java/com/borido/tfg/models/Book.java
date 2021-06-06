package com.borido.tfg.models;

import androidx.recyclerview.widget.ListUpdateCallback;

import java.io.Serializable;
import java.util.ArrayList;

public class Book implements Serializable {

    private String id;
    private String isbn;
    private String image;
    private String title;
    private String author;
    private String gender;
    private String editorial;
    private String idiom;
    private String descripcion;
    private int n_pages;
    private String libraries;

    public Book(String id, String isbn, String image, String title, String author, String gender, String editorial, String idiom, String descripcion, int n_pages, String libraries) {
        this.id = id;
        this.isbn = isbn;
        this.image = image;
        this.title = title;
        this.author = author;
        this.gender = gender;
        this.editorial = editorial;
        this.idiom = idiom;
        this.descripcion = descripcion;
        this.n_pages = n_pages;
        this.libraries = libraries;
    }

    public Book() {

    }

    public String getId() {
        return id;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEditorial() {
        return editorial;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getIdiom() {
        return idiom;
    }

    public void setIdiom(String idiom) {
        this.idiom = idiom;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getN_pages() {
        return n_pages;
    }

    public void setN_pages(int n_pages) {
        this.n_pages = n_pages;
    }

    public String getLibraries() {
        return libraries;
    }

    public void setLibraries(String libraries) {
        this.libraries = libraries;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                "isbn='" + isbn + '\'' +
                ", image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", gender='" + gender + '\'' +
                ", editorial='" + editorial + '\'' +
                ", idiom='" + idiom + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", n_pages=" + n_pages +
                ", libraries=" + libraries +
                '}';
    }

    /*------------------------------------------ OTHERS METHODS --------------------------------------------------*/

    public void update(Book b){
        if(this.id == b.id){
            this.id = b.id;
            this.isbn = b.isbn;
            this.title = b.title;
            this.author = b.author;
            this.image = b.image;
            this.gender = b.gender;
            this.editorial = b.editorial;
            this.idiom = b.idiom;
            this.descripcion = b.descripcion;
            this.n_pages = b.n_pages;
            this.libraries = b.libraries;
        }
    }
}
