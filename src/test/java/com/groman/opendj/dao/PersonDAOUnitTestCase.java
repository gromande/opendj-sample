package com.groman.opendj.dao;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.groman.opendj.guice.MemoryLdapModule;

public class PersonDAOUnitTestCase extends AbstractPersonDAOTestCase {

    @Override
    protected Injector getGuiceInjector() {
        return Guice.createInjector(new MemoryLdapModule());
    }

}
