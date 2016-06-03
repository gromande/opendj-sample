package com.groman.opendj.dao;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;
import com.groman.opendj.model.Person;
import com.groman.opendj.service.LdapService;

public abstract class AbstractPersonDAOTestCase {
    
    private Random rand;
    private LdapService service;
    private PersonDAO dao;
    
    protected abstract Injector getGuiceInjector();
    
    @Before
    public void setUp() {
        Injector injector = getGuiceInjector();
        service = injector.getInstance(LdapService.class);
        service.start();
        dao = injector.getInstance(PersonDAO.class);
        rand = new Random(System.currentTimeMillis());
    }
    
    @After
    public void tearDown() {
        service.stop();
    }

    @Test
    public void testUpdateEmail() {
        Person person = generateRandomPerson();
        dao.addPerson(person);
        
        Person persistedPerson = dao.getPerson(person.getUsername());
        assertEquals(person.getEmail(), persistedPerson.getEmail());
        
        person.setEmail(person.getUsername() + "@exmaple.com");
        dao.updatePerson(person);
        
        persistedPerson = dao.getPerson(person.getUsername());
        assertEquals(person.getEmail(), persistedPerson.getEmail());
        
        dao.deletePerson(person.getUsername());
        
    }
    
    private Person generateRandomPerson() {
        Person person = new Person(generateRandomUsername());
        person.setEmail(person.getUsername() + "@groman.com");
        person.setFirstName("John");
        person.setLastName("Doe");
        return person;
    }
    
    private String generateRandomUsername() {
        return "user-" + rand.nextInt(1000);
    }

}
