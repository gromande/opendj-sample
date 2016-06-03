package com.groman.opendj.dao;

import org.forgerock.opendj.ldap.ConnectionFactory;

public interface ConnectionManager {
    
    ConnectionFactory setUpConnectionFactory();

}
