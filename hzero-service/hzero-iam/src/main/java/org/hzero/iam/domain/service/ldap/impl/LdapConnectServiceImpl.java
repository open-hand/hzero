package org.hzero.iam.domain.service.ldap.impl;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.*;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.CommunicationException;
import org.springframework.ldap.InvalidNameException;
import org.springframework.ldap.UncategorizedLdapException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import io.choerodon.core.ldap.DirectoryType;

import org.hzero.iam.api.dto.LdapConnectionDTO;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.service.ldap.LdapConnectService;

/**
 * @author superlee
 */
@Service
public class LdapConnectServiceImpl implements LdapConnectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LdapConnectServiceImpl.class);

    public static final String LDAP_CONNECTION_DTO = "ldapConnectionDTO";

    public static final String LDAP_TEMPLATE = "ldapTemplate";

    private static final String OBJECT_CLASS = "objectclass";

    @Override
    public Map<String, Object> testConnect(Ldap ldap) {
        ldap.connectValidate();
        boolean anonymous = StringUtils.isEmpty(ldap.getAccount()) || StringUtils.isEmpty(ldap.getLdapPassword());
        LdapConnectionDTO ldapConnectionDTO = new LdapConnectionDTO();
        Map<String, Object> returnMap = new HashMap<>(2);

        LdapTemplate ldapTemplate = initLdapTemplate(ldap, anonymous);
        returnMap.put(LDAP_TEMPLATE, ldapTemplate);
        //默认将account当作userDn,如果无法登陆，则去ldap服务器抓取ldapDO.getLoginNameField()==account的userDn，然后使用返回的userDn登陆
        accountAsUserDn(ldap, ldapConnectionDTO, ldapTemplate);
        //输入的账户无法登陆，去ldap服务器抓取userDn(例外hand ldap)
        if (!anonymous && ldapConnectionDTO.getCanConnectServer() && !ldapConnectionDTO.getCanLogin()) {
            returnMap.put(LDAP_TEMPLATE, fetchUserDn2Authenticate(ldap, ldapConnectionDTO));
        }
        returnMap.put(LDAP_CONNECTION_DTO, ldapConnectionDTO);
        return returnMap;
    }

    private LdapTemplate fetchUserDn2Authenticate(Ldap ldapDO, LdapConnectionDTO ldapConnectionDTO) {
        LdapContextSource contextSource = new LdapContextSource();
        String url = ldapDO.getServerAddress() + ":" + ldapDO.getPort();
        int connectionTimeout = ldapDO.getConnectionTimeout();
        contextSource.setUrl(url);
        contextSource.setBase(ldapDO.getBaseDn());
        contextSource.setAnonymousReadOnly(true);
        setConnectionTimeout(contextSource, connectionTimeout);
        contextSource.afterPropertiesSet();
        LdapTemplate ldapTemplate = new LdapTemplate(contextSource);
        if (DirectoryType.MICROSOFT_ACTIVE_DIRECTORY.value().equals(ldapDO.getDirectoryType())) {
            ldapTemplate.setIgnorePartialResultException(true);
        }

        String userDn = null;
        Filter filter = getFilterByObjectClassAndAttribute(ldapDO);
        try {
            List<String> names =
                    ldapTemplate.search(
                            query()
                                .searchScope(SearchScope.SUBTREE)
                                .filter(filter),
                            new AbstractContextMapper<String>() {
                                @Override
                                protected String doMapFromContext(DirContextOperations ctx) {
                                    return ctx.getNameInNamespace();
                                }
                            });
            if (names.size() == 1) {
                userDn = names.get(0);
            }
        } catch (UncategorizedLdapException e) {
            if (e.getRootCause() instanceof NamingException) {
                LOGGER.warn("baseDn or userDn may be wrong!");
            }
            LOGGER.warn("uncategorized ldap exception {}", e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.warn("can not find anything while filter is {}, exception {}", filter, e);
        }

        if (userDn == null) {
            LOGGER.error("can not find anything or find more than one userDn while filter is {}, login failed", filter);
            return null;
        } else {
            contextSource.setAnonymousReadOnly(false);
            contextSource.setUserDn(userDn);
            contextSource.setPassword(ldapDO.getLdapPassword());

            ldapConnectionDTO.setCanLogin(false);
            ldapConnectionDTO.setMatchAttribute(false);
            try {
                LdapTemplate newLdapTemplate = new LdapTemplate(contextSource);
                matchAttributes(ldapDO, ldapConnectionDTO, newLdapTemplate);
                ldapConnectionDTO.setCanLogin(true);
                return newLdapTemplate;
            } catch (InvalidNameException | AuthenticationException e) {
                LOGGER.error("userDn = {} or password is invalid, login failed, exception: {}", userDn, e);
                return null;
            } catch (Exception e) {
                LOGGER.error("unexpected exception: {} ", e.getMessage(), e);
                return null;
            }
        }
    }

    private Filter getFilterByObjectClassAndAttribute(Ldap ldapDO) {
        String account = ldapDO.getAccount();
        AndFilter andFilter = getAndFilterByObjectClass(ldapDO);
        andFilter.and(new EqualsFilter(ldapDO.getLoginNameField(), account));
        return andFilter;
    }

    private AndFilter getAndFilterByObjectClass(Ldap ldapDO) {
        String objectClass = ldapDO.getObjectClass();
        String[] arr = objectClass.split(",");
        AndFilter andFilter = new AndFilter();
        for (String str : arr) {
            andFilter.and(new EqualsFilter(OBJECT_CLASS, str));
        }
        return andFilter;
    }

    private void accountAsUserDn(Ldap ldapDO, LdapConnectionDTO ldapConnectionDTO, LdapTemplate ldapTemplate) {
        try {
            if (DirectoryType.MICROSOFT_ACTIVE_DIRECTORY.value().equals(ldapDO.getDirectoryType())) {
                ldapTemplate.setIgnorePartialResultException(true);
            }
            ldapConnectionDTO.setCanConnectServer(false);
            ldapConnectionDTO.setCanLogin(false);
            ldapConnectionDTO.setMatchAttribute(false);
            //使用管理员登陆，查询一个objectclass=ldapDO.getObjectClass的对象去匹配属性
            matchAttributes(ldapDO, ldapConnectionDTO, ldapTemplate);
            ldapConnectionDTO.setCanConnectServer(true);
            ldapConnectionDTO.setCanLogin(true);
        } catch (InvalidNameException | AuthenticationException e) {
            if (e.getRootCause() instanceof javax.naming.InvalidNameException
                    || e.getRootCause() instanceof javax.naming.AuthenticationException) {
                ldapConnectionDTO.setCanConnectServer(true);
                ldapConnectionDTO.setCanLogin(false);
            }
            LOGGER.warn("can not login when using account as userDn, so fetch userDn from ldap server, exception {}", e.getMessage(), e);
        } catch (UncategorizedLdapException | CommunicationException e) {
            if (e.getRootCause() instanceof MalformedURLException
                    || e.getRootCause() instanceof UnknownHostException) {
                //ldap连接失败
                ldapConnectionDTO.setCanConnectServer(false);
                ldapConnectionDTO.setCanLogin(false);
            }
            LOGGER.error("connect to ldap server failed, exception: {}", e.getMessage(), e);
        } catch (Exception e) {
            ldapConnectionDTO.setCanConnectServer(false);
            ldapConnectionDTO.setCanLogin(false);
            LOGGER.error("connect to ldap server failed, exception: {}", e.getMessage(), e);
        }
    }

    private LdapTemplate initLdapTemplate(Ldap ldapDO, boolean anonymous) {
        LdapContextSource contextSource = new LdapContextSource();
        String url = ldapDO.getServerAddress() + ":" + ldapDO.getPort();
        int connectionTimeout = ldapDO.getConnectionTimeout();
        contextSource.setUrl(url);
        contextSource.setBase(ldapDO.getBaseDn());
        setConnectionTimeout(contextSource, connectionTimeout);

        if (!anonymous) {
            contextSource.setUserDn(ldapDO.getAccount());
            contextSource.setPassword(ldapDO.getLdapPassword());
        } else {
            contextSource.setAnonymousReadOnly(true);
        }
        contextSource.afterPropertiesSet();
        return new LdapTemplate(contextSource);
    }

    private void setConnectionTimeout(LdapContextSource contextSource, int connectionTimeout) {
        Map<String, Object> environment = new HashMap<>(1);
        //设置ldap服务器连接超时时间为10s
        environment.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(connectionTimeout * 1000));
        contextSource.setBaseEnvironmentProperties(environment);
    }

    private void matchAttributes(Ldap ldapDO, LdapConnectionDTO ldapConnectionDTO, LdapTemplate ldapTemplate) {
        Map<String, String> attributeMap = initAttributeMap(ldapDO);
        Filter filter = getAndFilterByObjectClass(ldapDO);
        List<Attributes> attributesList =
                ldapTemplate.search(
                        query()
                                .searchScope(SearchScope.SUBTREE)
                                .countLimit(100).filter(filter),
                        new AttributesMapper<Attributes>() {
                            @Override
                            public Attributes mapFromAttributes(Attributes attributes) throws NamingException {
                                return attributes;
                            }
                        });
        if (attributesList.isEmpty()) {
            LOGGER.warn("can not get any attributes while the filter is {}", filter);
            ldapConnectionDTO.setLoginNameField(ldapDO.getLoginNameField());
            ldapConnectionDTO.setRealNameField(ldapDO.getRealNameField());
            ldapConnectionDTO.setPhoneField(ldapDO.getPhoneField());
            ldapConnectionDTO.setEmailField(ldapDO.getEmailField());
        } else {
            Set<String> keySet = new HashSet<>();
            for (Attributes attributes : attributesList) {
                NamingEnumeration<String> attributesIDs = attributes.getIDs();
                while (attributesIDs != null && attributesIDs.hasMoreElements()) {
                    keySet.add(attributesIDs.nextElement());
                }
            }
            fullMathAttribute(ldapConnectionDTO, attributeMap, keySet);
        }
    }

    private void fullMathAttribute(LdapConnectionDTO ldapConnectionDTO, Map<String, String> attributeMap, Set<String> keySet) {
        boolean match = true;
        for (Map.Entry<String, String> entry : attributeMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isNotBlank(value) && !keySet.contains(value)) {
                match = false;
                ldapConnectionDTO.fullFields(key, value);
            }
        }
        ldapConnectionDTO.setMatchAttribute(match);
    }

    private Map<String, String> initAttributeMap(Ldap ldap) {
        Map<String, String> attributeMap = new HashMap<>(10);
        attributeMap.put(Ldap.GET_LOGIN_NAME_FIELD, ldap.getLoginNameField());
        attributeMap.put(Ldap.GET_REAL_NAME_FIELD, ldap.getRealNameField());
        attributeMap.put(Ldap.GET_EMAIL_FIELD, ldap.getEmailField());
        attributeMap.put(Ldap.GET_PHONE_FIELD, ldap.getPhoneField());
        attributeMap.put(Ldap.GET_UUID_FIELD, ldap.getUuidField());
        return attributeMap;
    }

}
