package com.groman.opendj.dao;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Injector;
import com.groman.opendj.model.Person;
import com.groman.opendj.service.LdapService;

public abstract class AbstractPersonDAOTestCase {
    
    private LdapService service;
    private PersonDAO dao;
    
    protected abstract Injector getGuiceInjector();
    
    @Before
    public void setUp() {
        Injector injector = getGuiceInjector();
        service = injector.getInstance(LdapService.class);
        service.start();
        dao = injector.getInstance(PersonDAO.class);
    }
    
    @After
    public void tearDown() {
        service.stop();
    }

    @Test
    public void testUpdateEmail() {
        Person person = PersonTestUtil.generateRandomPerson();
        dao.addPerson(person);
        
        Person persistedPerson = dao.getPerson(person.getUsername());
        assertEquals(person.getEmail(), persistedPerson.getEmail());
        
        person.setEmail(person.getUsername() + "@exmaple.com");
        dao.updatePerson(person);
        
        persistedPerson = dao.getPerson(person.getUsername());
        assertEquals(person.getEmail(), persistedPerson.getEmail());
        
        dao.deletePerson(person.getUsername());
        
    }

}
