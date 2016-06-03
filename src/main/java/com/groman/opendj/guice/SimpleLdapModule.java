package com.groman.opendj.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.groman.opendj.dao.ConnectionManager;
import com.groman.opendj.dao.PersonDAO;
import com.groman.opendj.dao.PersonDAOImpl;
import com.groman.opendj.dao.SimpleConnectionManager;
import com.groman.opendj.service.LdapService;

public class SimpleLdapModule extends AbstractModule {

    public SimpleLdapModule() {
    }

    @Override
    protected void configure() {
        bind(ConnectionManager.class).to(SimpleConnectionManager.class);
        bind(PersonDAO.class).to(PersonDAOImpl.class);
    }
    
    @Provides @Singleton @Inject
    LdapService providesLdapService(ConnectionManager manager) {
        return new LdapService(manager);
    }

}
