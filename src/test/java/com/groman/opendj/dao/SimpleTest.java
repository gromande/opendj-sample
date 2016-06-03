package com.groman.opendj.dao;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.forgerock.opendj.ldap.Attribute;
import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.ConnectionFactory;
import org.forgerock.opendj.ldap.Connections;
import org.forgerock.opendj.ldap.Entry;
import org.forgerock.opendj.ldap.ErrorResultException;
import org.forgerock.opendj.ldap.LinkedHashMapEntry;
import org.forgerock.opendj.ldap.MemoryBackend;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.requests.Requests;
import org.forgerock.opendj.ldap.requests.SearchRequest;
import org.forgerock.opendj.ldap.responses.Result;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;
import org.forgerock.opendj.ldap.schema.Schema;
import org.forgerock.opendj.ldap.schema.SchemaBuilder;
import org.forgerock.opendj.ldif.EntryReader;
import org.forgerock.opendj.ldif.LDIFEntryReader;
import org.junit.Test;

public class SimpleTest {

    @Test
    public void test() {
        
        String[] ldifEntries = new String[] {
                "dn: dc=groman,dc=uim",
                "objectClass: domain",
                "objectClass: top",
                "dc: groman",
                "",
                "dn: ou=People,dc=groman,dc=uim",
                "objectClass: organizationalunit",
                "objectClass: top",
                "ou: People"
        };
                
        try {
            
            //Set up memory backend
            EntryReader entryReader = new LDIFEntryReader(ldifEntries);
            MemoryBackend backend = new MemoryBackend(entryReader);

            //Initialize connection factory
            ConnectionFactory factory = 
                    Connections.newInternalConnectionFactory(Connections.newServerConnectionFactory(backend), null);

            //Get a connection
            Connection connection = factory.getConnection();
            
            //Add new entry
            String userDN = "uid=user,ou=People,dc=groman,dc=uim";
            Entry userEntry = new LinkedHashMapEntry(userDN);
            userEntry.addAttribute("objectClass", "top", "person", "mycustomclass");
            userEntry.addAttribute("uid", "user");
            userEntry.addAttribute("cn", "John Doe");
            userEntry.addAttribute("sn", "Doe");
            Result addResult = connection.add(userEntry);
            assertTrue(addResult.isSuccess());
            
            //Search for new user
            SearchRequest request = Requests.newSearchRequest(
                    "ou=People,dc=groman,dc=uim", 
                    SearchScope.SINGLE_LEVEL, 
                    "(uid=user)");
            SearchResultEntry result = connection.searchSingleEntry(request);
            
            //Verify
            assertEquals(result.getName().toString(), userDN);
            assertEquals(result.getAttribute("uid").firstValueAsString(), "user");
            assertEquals(result.getAttribute("cn").firstValueAsString(), "John Doe");
            assertEquals(result.getAttribute("sn").firstValueAsString(), "Doe");
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
