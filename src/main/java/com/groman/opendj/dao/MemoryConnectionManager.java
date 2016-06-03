package com.groman.opendj.dao;

import java.io.IOException;
import java.io.InputStream;

import org.forgerock.opendj.ldap.ConnectionFactory;
import org.forgerock.opendj.ldap.Connections;
import org.forgerock.opendj.ldap.MemoryBackend;
import org.forgerock.opendj.ldif.EntryReader;
import org.forgerock.opendj.ldif.LDIFEntryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.groman.opendj.util.IOUtil;

public class MemoryConnectionManager implements ConnectionManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MemoryConnectionManager.class);
    private static final String DIT_LDIF_FILE = "opendj/custom-dit.ldif";
    
    public MemoryConnectionManager() {
    }

    public ConnectionFactory setUpConnectionFactory() {
        LOGGER.debug("Instantiating In-Memory Connection Factory");
        EntryReader entryReader = null;
        try {
            entryReader = readDITEntries();
            MemoryBackend backend = new MemoryBackend(entryReader);
            return Connections.newInternalConnectionFactory(Connections.newServerConnectionFactory(backend), null);
        } catch (IOException e) {
            throw new RuntimeException("Unable to create InMemory connection factory", e);
        } finally {
            IOUtil.close(entryReader);
        }
    }
    
    private EntryReader readDITEntries() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(DIT_LDIF_FILE);
        if (in == null) {
            throw new IllegalStateException("Unable to find DIT file: " + DIT_LDIF_FILE);
        }
        return new LDIFEntryReader(in);
    }

}
