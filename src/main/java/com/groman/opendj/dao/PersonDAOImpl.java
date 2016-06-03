package com.groman.opendj.dao;

import org.forgerock.opendj.ldap.Connection;
import org.forgerock.opendj.ldap.DN;
import org.forgerock.opendj.ldap.Entry;
import org.forgerock.opendj.ldap.ErrorResultException;
import org.forgerock.opendj.ldap.Filter;
import org.forgerock.opendj.ldap.LinkedHashMapEntry;
import org.forgerock.opendj.ldap.SearchScope;
import org.forgerock.opendj.ldap.requests.ModifyRequest;
import org.forgerock.opendj.ldap.requests.Requests;
import org.forgerock.opendj.ldap.responses.Result;
import org.forgerock.opendj.ldap.responses.SearchResultEntry;

import com.google.inject.Inject;
import com.groman.opendj.model.Person;
import com.groman.opendj.service.LdapService;
import com.groman.opendj.util.IOUtil;

public class PersonDAOImpl implements PersonDAO {

    // LDAP Attributes
    private static final String USERNAME_ATTR = "uid";
    private static final String FIRST_NAME_ATTR = "givenName";
    private static final String LAST_NAME_ATTR = "sn";
    private static final String FULL_NAME_ATTR = "cn";
    private static final String EMAIL_ATTR = "mail";
    private static final String OBJECT_CLASS_ATTR = "objectClass";
    private static final String[] OBJECT_CLASS_VALUES =
            new String[] {
                    "top",
                    "person",
                    "inetOrgPerson"
            };

    // Base DN
    private static final DN baseDN = DN.valueOf("ou=people,dc=example,dc=com");

    private final LdapService service;

    @Inject
    public PersonDAOImpl(LdapService service) {
        this.service = service;
    }
    
    public void addPerson(Person person) {
        Connection conn = null;
        try {
            conn = getConnection();
            Result result = conn.add(mapPersonToEntry(person));
            if (!result.isSuccess()) {
                throw new RuntimeException(result.getDiagnosticMessage(), result.getCause());
            }
        } catch (ErrorResultException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.close(conn);
        }
    }

    public Person getPerson(String username) {
        Connection conn = null;
        try {
            conn = getConnection();
            SearchResultEntry entry = conn.searchSingleEntry(
                    Requests.newSearchRequest(getEntryDN(username), SearchScope.BASE_OBJECT, Filter.alwaysTrue()));
            return mapEntryToPerson(entry);
        } catch (ErrorResultException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.close(conn);
        }
    }

    public void updatePerson(Person person) {
        Person oldPerson = getPerson(person.getUsername());
        Connection conn = null;
        try {
            conn = getConnection();
            Entry oldEntry = mapPersonToEntry(oldPerson);
            Entry newEntry = mapPersonToEntry(person);
            ModifyRequest request = Requests.newModifyRequest(oldEntry, newEntry);
            Result result = conn.modify(request);
            if (!result.isSuccess()) {
                throw new RuntimeException(result.getDiagnosticMessage(), result.getCause());
            }
        } catch (ErrorResultException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.close(conn);
        }

    }

    public void deletePerson(String username) {
        Connection conn = null;
        try {
            conn = getConnection();
            Result result = conn.delete(getEntryDN(username).toString());
            if (!result.isSuccess()) {
                throw new RuntimeException(result.getDiagnosticMessage(), result.getCause());
            }
        } catch (ErrorResultException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtil.close(conn);
        }
    }

    private Entry mapPersonToEntry(Person person) {
        if (person == null || person.getUsername() == null) {
            throw new IllegalArgumentException("Invalid person");
        }
        
        Entry entry = new LinkedHashMapEntry(getEntryDN(person.getUsername()));
        entry.addAttribute(USERNAME_ATTR, person.getUsername());
        entry.addAttribute(OBJECT_CLASS_ATTR, (Object[]) OBJECT_CLASS_VALUES);
        
        if (person.getFirstName() != null) {
            entry.addAttribute(FIRST_NAME_ATTR, person.getFirstName());
        }
        
        if (person.getLastName() != null) {
            entry.addAttribute(LAST_NAME_ATTR, person.getLastName());
        }
        
        if (person.getFullName() != null) {
            entry.addAttribute(FULL_NAME_ATTR, person.getFullName());
        }
        
        if (person.getEmail() != null) {
            entry.addAttribute(EMAIL_ATTR, person.getEmail());
        }
        
        return entry;
        
    }

    private Person mapEntryToPerson(Entry entry) {
        if (entry == null || entry.getAttribute(USERNAME_ATTR) == null) {
            throw new IllegalArgumentException("Invalid entry");
        }
        
        Person person = new Person(entry.getAttribute(USERNAME_ATTR).firstValueAsString());
        
        if (entry.getAttribute(FIRST_NAME_ATTR) != null) {
            person.setFirstName(entry.getAttribute(FIRST_NAME_ATTR).firstValueAsString());
        }
        
        if (entry.getAttribute(LAST_NAME_ATTR) != null) {
            person.setLastName(entry.getAttribute(LAST_NAME_ATTR).firstValueAsString());
        }
        
        if (entry.getAttribute(EMAIL_ATTR) != null) {
            person.setEmail(entry.getAttribute(EMAIL_ATTR).firstValueAsString());
        }
        
        return person;
    }

    private DN getEntryDN(String username) {
        return DN.valueOf(USERNAME_ATTR + "=" + username + "," + baseDN);
    }
    
    private Connection getConnection() {
        return service.getConnection();
    }

}
