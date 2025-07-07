package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    // For pagination with correct comparison
	@Query("from Contact as c where c.user.id = :userId")
	Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);


    // Optional if you need full list
    @Query("from Contact c where c.user.id = :userId")
    List<Contact> findAllContactsByUser(@Param("userId") int userId);
    
    //search
    public List<Contact> findByNameContainingAndUser(String name,User user);
}
