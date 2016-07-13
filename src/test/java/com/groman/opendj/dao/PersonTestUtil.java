package com.groman.opendj.dao;

import java.util.Random;

import com.groman.opendj.model.Person;

public class PersonTestUtil {
    
    private static Random rand = new Random(System.currentTimeMillis());

    private PersonTestUtil() {
    }
    
    public static Person generateRandomPerson() {
        Person person = new Person(generateRandomUsername());
        person.setEmail(person.getUsername() + "@groman.com");
        person.setFirstName("John");
        person.setLastName("Doe");
        return person;
    }
    
    public static String generateRandomUsername() {
        return "user-" + rand.nextInt(1000);
    }

}
