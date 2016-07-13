package com.groman.opendj.dao;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.groman.opendj.guice.JNDILdapModule;
import com.groman.opendj.model.Person;

@Category(IntegrationTest.class)
public class PersonDAOJNDITestCase {
    
    private PersonDAO dao;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new JNDILdapModule());
        dao = injector.getInstance(PersonDAO.class);
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
