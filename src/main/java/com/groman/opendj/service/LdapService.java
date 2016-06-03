package com.groman.opendj.service;

import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.ConnectionFactory;
import org.forgerock.opendj.ldap.ErrorResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.groman.opendj.dao.ConnectionManager;
import com.groman.opendj.util.IOUtil;

public class LdapService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapService.class);
    
    private boolean isRunning = false;
    private ConnectionFactory connectionFactory;    
    private ConnectionManager manager;
    
    @Inject
    public LdapService(ConnectionManager manager) {
        LOGGER.debug("Creating service");
        this.manager = manager;
    }

    public synchronized void start() {
        if (!isRunning) {
            LOGGER.debug("Starting service");
            connectionFactory = manager.setUpConnectionFactory();
            isRunning = true;
        } else {
            throw new IllegalStateException("Service is already running");
        }
    }

    public synchronized void stop() {
        if (isRunning) {
            LOGGER.debug("Stopping service");
            if (connectionFactory == null) {
                throw new IllegalStateException("Connection factory was null");
            }
            IOUtil.close(connectionFactory);
            isRunning = false;
        } else {
            throw new IllegalStateException("Service is not running");
        }
    }

    public Connection getConnection() {
        if (connectionFactory == null) {
            throw new IllegalStateException("Connection factory was null. Service hasn't started yet?");
        }
        try {
            return connectionFactory.getConnection();
        } catch (ErrorResultException e) {
            throw new RuntimeException("Unable to get connection", e);
        }
    }
    
}
