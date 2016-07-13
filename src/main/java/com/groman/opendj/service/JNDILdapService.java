package com.groman.opendj.service;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class JNDILdapService {

    public JNDILdapService() {
    }
    
    public DirContext getContext() {
        //Get connection parameters from system properties
        String hostname = System.getProperty("opendj.hostname");
        int port = Integer.parseInt(System.getProperty("opendj.ldap.port"));
        String bindDN = System.getProperty("opendj.bindDN");
        String bindPassword = System.getProperty("opendj.bindPassword");
        
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        String ldapURL = "ldap://" + hostname + ":" + port;
        env.put(Context.PROVIDER_URL, ldapURL);
        env.put(Context.SECURITY_PRINCIPAL, bindDN);
        env.put(Context.SECURITY_CREDENTIALS, bindPassword);

        // use pooling
        env.put("com.sun.jndi.ldap.connect.pool", "true");
        
        try {
            return new InitialDirContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

}
