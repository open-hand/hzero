package io.choerodon.core.ldap;

import java.util.*;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author wuguokai
 * @author superlee
 */
public class LdapUtil {
    private static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    private static final String SECURITY_AUTHENTICATION = "simple";
    private static final Logger LOGGER = LoggerFactory.getLogger(LdapUtil.class);


    private LdapUtil() {
        // this static utils class
    }

    /**
     * ldap用户通过ldap认证
     *
     * @param ldap     ldap配置
     * @return 返回认证结果
     */
    public static LdapContext authenticate(Ldap ldap) {
        String username = ldap.getAccount();
        String password = ldap.getPassword();
        String dirType = ldap.getDirectoryType();
        LdapContext ldapContext = ldapConnect(ldap);
        if (ldapContext == null) {
            return null;
        }
        if (dirType.equals(DirectoryType.OPEN_LDAP.value())) {
            String userDn;
            userDn = getUserDn(ldapContext, ldap, username);
            if (userDn.length() != 0 && ldapAuthenticate(ldapContext, userDn, password)) {
                return ldapContext;
            }
        } else if(dirType.equals(DirectoryType.MICROSOFT_ACTIVE_DIRECTORY.value())) {
            try {
                ldapContext.addToEnvironment(Context.SECURITY_PRINCIPAL, username);
                ldapContext.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
                ldapContext.reconnect(null);
                return ldapContext;
            } catch (NamingException e) {
                LOGGER.error("Microsoft Active Directory authenticate failed", e);
            }
        } else {
            LOGGER.error("not support directory type : {}", dirType);
        }
        return null;
    }

    /**
     * 使用匿名模式连接ldap初始化ldapContext
     *
     * @param ldap    Ldap ldap
     * @return 返回ldapContext
     */
    public static LdapContext ldapConnect(Ldap ldap) {
        String url = ldap.getServerAddress();
        String baseDn = ldap.getBaseDn();
        String port = ldap.getPort();
        HashMap<String, String> ldapEnv = new HashMap<>(10);
        ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        ldapEnv.put(Context.PROVIDER_URL, url + ":" + port + "/" + baseDn);
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION);
        if (ldap.getUseSSL()) {
            // Specify SSL
            ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
        }
        try {
            return new InitialLdapContext(new Hashtable<>(ldapEnv), null);
        } catch (NamingException e) {
            LOGGER.info("ldap connect fail", e);
        }
        return null;
    }

    /**
     * 根据ldap配置和用户名从ldapContext中搜寻该用户是否存在和得到userDn
     *
     * @param ldapContext ldapContext
     * @param ldap        该用户ldap配置
     * @param username    用户名
     * @return userDn
     */
    public static String getUserDn(LdapContext ldapContext, Ldap ldap, String username) {
        Set<String> attributeSet = initAttributeSet(ldap);
        attributeSet.remove("");
        attributeSet.remove(null);

        NamingEnumeration<?> namingEnumeration = getNamingEnumeration(ldapContext, username, attributeSet);
        StringBuilder userDn = new StringBuilder();
        while (namingEnumeration != null && namingEnumeration.hasMoreElements()) {
            //maybe more than one element
            Object obj = namingEnumeration.nextElement();
            if (obj instanceof SearchResult) {
                SearchResult searchResult = (SearchResult) obj;
                userDn.append(searchResult.getName()).append(",").append(ldap.getBaseDn());
            }
        }
        return userDn.toString();
    }

    private static Set<String> initAttributeSet(Ldap ldap) {
        Set<String> attributeSet = new HashSet<>(Arrays.asList("employeeNumber", "mail", "mobile"));
        if (ldap.getLoginNameField() != null) {
            attributeSet.add(ldap.getLoginNameField());
        }
        if (ldap.getRealNameField() != null) {
            attributeSet.add(ldap.getRealNameField());
        }
        if (ldap.getEmailField() != null) {
            attributeSet.add(ldap.getEmailField());
        }
        if (ldap.getPhoneField() != null) {
            attributeSet.add(ldap.getPhoneField());
        }
        return attributeSet;
    }

    public static NamingEnumeration<?> getNamingEnumeration(LdapContext ldapContext, String username, Set<String> attributeSet) {
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<?> namingEnumeration = null;
        for (String str : attributeSet) {
            try {
                namingEnumeration = ldapContext.search("",
                        str + "=" + username, constraints);
                if (namingEnumeration.hasMoreElements()) {
                    break;
                }
            } catch (NamingException e) {
                LOGGER.error("ldap search fail", e);
            }
        }
        return namingEnumeration;
    }

    /**
     * 根据ldapContext和userDn和密码进行认证
     *
     * @param ldapContext ldapContext
     * @param userDn      userDn
     * @param password    密码
     * @return 返回认证结果
     */
    public static boolean ldapAuthenticate(LdapContext ldapContext, String userDn, String password) {
        try {
            ldapContext.addToEnvironment(Context.SECURITY_PRINCIPAL, userDn);
            ldapContext.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ldapContext.reconnect(null);
        } catch (NamingException e) {
            LOGGER.info("ldap authenticate fail", e);
            return false;
        }
        return true;
    }

    /**
     * 匿名用户根据objectClass来获取一个entry返回
     */
    public static Attributes anonymousUserGetByObjectClass(Ldap ldap, LdapContext ldapContext) {
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        NamingEnumeration<?> namingEnumeration = null;
        try {
            namingEnumeration = ldapContext.search("", "objectClass=*", constraints);
            while (namingEnumeration != null && namingEnumeration.hasMoreElements()) {
                SearchResult searchResult = (SearchResult) namingEnumeration.nextElement();
                Attributes attributes = searchResult.getAttributes();
                if (attributes.get("objectClass") != null
                        && attributes.get("objectClass").contains(ldap.getObjectClass())) {
                    return attributes;
                }
            }
        } catch (NamingException e) {
            LOGGER.info("ldap search fail", e);
        }
        return null;
    }
}
