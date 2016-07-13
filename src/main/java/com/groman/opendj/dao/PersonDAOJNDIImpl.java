package com.groman.opendj.dao;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.groman.opendj.model.Person;
import com.groman.opendj.service.JNDILdapService;

public class PersonDAOJNDIImpl implements PersonDAO {
    
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
    
    private static final String DN_TEMPLATE = "uid={0},ou=people,dc=example,dc=com";
    private static final String ALWAYS_TRUE = "(objectClass=*)";
    
    private final JNDILdapService service;
    
    @Inject
    public PersonDAOJNDIImpl(JNDILdapService service) {
        this.service = service;
    }

    public void addPerson(Person person) {
        DirContext ctx = getContext();
        try {
            ctx.createSubcontext(getEntryDN(person.getUsername()), mapPersonToEntry(person));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            closeContext(ctx);
        }
    }

    public Person getPerson(String username) {
        DirContext ctx = getContext();
        
        try {
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.OBJECT_SCOPE);
            
            NamingEnumeration<SearchResult> ne = 
                    ctx.search(getEntryDN(username), ALWAYS_TRUE, sc);
            if (ne != null && ne.hasMore()) {
                return mapEntryToPerson(ne.next());
            } else {
                return null;
            }
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            closeContext(ctx);
        }
    }

    public void updatePerson(Person person) {
        DirContext ctx = getContext();
        Person oldPerson = getPerson(person.getUsername());
        try {
            ctx.modifyAttributes(getEntryDN(person.getUsername()), getModifications(oldPerson, person));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            closeContext(ctx);
        }
    }

    public void deletePerson(String username) {
        DirContext ctx = getContext();
        try {
            ctx.destroySubcontext(getEntryDN(username));
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            closeContext(ctx);
        }
    }
    
    private DirContext getContext() {
        return service.getContext();
    }
    
    private void closeContext(DirContext ctx) {
        if (ctx != null) {
            try {
                ctx.close();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    private String getEntryDN(String username) {
        return MessageFormat.format(DN_TEMPLATE, username);
    }
    
    private ModificationItem[] getModifications(Person oldPerson, Person newPerson) {
        List<ModificationItem> mods = new ArrayList<ModificationItem>();
        
        if (!StringUtils.equalsIgnoreCase(oldPerson.getEmail(), newPerson.getEmail())) {
            addModification(mods, EMAIL_ATTR, newPerson.getEmail());
        }
        
        if (!StringUtils.equalsIgnoreCase(oldPerson.getFirstName(), newPerson.getFirstName())) {
            addModification(mods, FIRST_NAME_ATTR, newPerson.getFirstName());
        }
        
        if (!StringUtils.equalsIgnoreCase(oldPerson.getLastName(), newPerson.getLastName())) {
            addModification(mods, LAST_NAME_ATTR, newPerson.getLastName());
        }
        
        return mods.toArray(new ModificationItem[]{});

    }
    
    private void addModification(List<ModificationItem> mods, String attrName, String attValue) {
        BasicAttribute attr = new BasicAttribute(attrName, attValue);
        mods.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,attr));
    }
    
    private Person mapEntryToPerson(SearchResult entry) {
        if (entry == null || !hasAttribute(entry, USERNAME_ATTR)) {
            throw new IllegalArgumentException("Invalid entry");
        }
        
        Person person = new Person(getStringAttribute(entry, USERNAME_ATTR));
        
        if (hasAttribute(entry,FIRST_NAME_ATTR)) {
            person.setFirstName(getStringAttribute(entry, FIRST_NAME_ATTR));
        }
        
        if (hasAttribute(entry,LAST_NAME_ATTR)) {
            person.setLastName(getStringAttribute(entry, LAST_NAME_ATTR));
        }
        
        if (hasAttribute(entry,EMAIL_ATTR)) {
            person.setEmail(getStringAttribute(entry, EMAIL_ATTR));
        }
        
        return person;
    }
    
    private Attributes mapPersonToEntry(Person person) {
        BasicAttributes attributes = new BasicAttributes();
        
        if (person == null || person.getUsername() == null) {
            throw new IllegalArgumentException("Invalid person");
        }
        
        attributes.put(USERNAME_ATTR, person.getUsername());
        
        for (String objectClass : OBJECT_CLASS_VALUES) {
            attributes.put(OBJECT_CLASS_ATTR, objectClass);
        }
        
        if (person.getFirstName() != null) {
            attributes.put(FIRST_NAME_ATTR, person.getFirstName());
        }
        
        if (person.getLastName() != null) {
            attributes.put(LAST_NAME_ATTR, person.getLastName());
        }
        
        if (person.getFullName() != null) {
            attributes.put(FULL_NAME_ATTR, person.getFullName());
        }
        
        if (person.getEmail() != null) {
            attributes.put(EMAIL_ATTR, person.getEmail());
        }
        
        return attributes;
    }
    
    private boolean hasAttribute(SearchResult entry, String attrName) {
        return entry.getAttributes().get(attrName) != null;
    }
    
    private String getStringAttribute(SearchResult entry, String attrName) {
        try {
            return (String) entry.getAttributes().get(attrName).get();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

}
