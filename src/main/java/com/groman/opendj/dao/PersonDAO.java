package com.groman.opendj.dao;

import com.groman.opendj.model.Person;

public interface PersonDAO {
    
    void addPerson(Person person);
    public Person getPerson(String username);
    public void updatePerson(Person person);
    public void deletePerson(String username);

}
