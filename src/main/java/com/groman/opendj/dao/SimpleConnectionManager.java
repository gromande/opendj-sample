package com.groman.opendj.dao;

import org.forgerock.opendj.ldap.ConnectionFactory;
import org.forgerock.opendj.ldap.Connections;
import org.forgerock.opendj.ldap.LDAPConnectionFactory;
import org.forgerock.opendj.ldap.requests.BindRequest;
import org.forgerock.opendj.ldap.requests.Requests;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleConnectionManager implements ConnectionManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleConnectionManager.class);

    public SimpleConnectionManager() {
    }

    public ConnectionFactory setUpConnectionFactory() {
        
        //Get connection parameters from system properties
        String hostname = System.getProperty("opendj.hostname");
        int port = Integer.parseInt(System.getProperty("opendj.ldap.port"));
        String bindDN = System.getProperty("opendj.bindDN");
        String bindPassword = System.getProperty("opendj.bindPassword");
        
        ConnectionFactory connectionFactory = new LDAPConnectionFactory(hostname, port);
        LOGGER.debug("Setting up Simple LDAP Connection factory: " + connectionFactory);

        BindRequest request = Requests.newSimpleBindRequest(bindDN, bindPassword.toCharArray());
        return Connections.newAuthenticatedConnectionFactory(connectionFactory, request);
    }

}
