package com.groman.opendj.dao;

import org.junit.experimental.categories.Category;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.groman.opendj.guice.SimpleLdapModule;

@Category(IntegrationTest.class)
public class PersonDAOIntegrationTestCase extends AbstractPersonDAOTestCase {

    @Override
    protected Injector getGuiceInjector() {
        return Guice.createInjector(new SimpleLdapModule());
    }

    

}
