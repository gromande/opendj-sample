package com.groman.opendj.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.groman.opendj.dao.PersonDAO;
import com.groman.opendj.dao.PersonDAOJNDIImpl;
import com.groman.opendj.service.JNDILdapService;

public class JNDILdapModule extends AbstractModule {

    public JNDILdapModule() {
    }

    @Override
    protected void configure() {
        bind(PersonDAO.class).to(PersonDAOJNDIImpl.class);
    }
    
    @Provides @Singleton
    JNDILdapService providesLdapService() {
        return new JNDILdapService();
    }

}
