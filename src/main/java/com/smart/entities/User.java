package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 20, message = "Name must be between 2 and 20 characters")
    private String name;

    @Column(unique = true)
    @Email(message = "Invalid email address")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String role;
    private boolean enabled;
    private String imageUrl;

    @Size(max = 100, message = "About must be less than 100 characters")
    private String about;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user",orphanRemoval=true)
    private List<Contact> contacts = new ArrayList<>();

    // Getters, Setters, Constructors, toString() (unchanged)
	
	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	@Override
	public String toString() {
	    return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
	            + ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", about=" + about
	            + ", contactsCount=" + (contacts != null ? contacts.size() : 0) + "]";
	}

	
	
	
	
}
