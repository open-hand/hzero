package org.hzero.iam.domain.service.ldap;

import java.util.*;
import java.util.stream.Collectors;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.LdapErrorUserRepository;
import org.hzero.iam.domain.repository.LdapHistoryRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.infra.util.CollectionSubUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.ldap.control.PagedResultsDirContextProcessor;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapOperationsCallback;
import org.springframework.ldap.core.support.SingleContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * @author bojiangzhou 2018/08/02
 *
 * @author wuguokai
 */
@RefreshScope
@Component
public class LdapSyncUserTask {
    private static final Logger logger = LoggerFactory.getLogger(LdapSyncUserTask.class);

    private static final String OBJECT_CLASS = "objectclass";

    private final UserRepository userRepository;
    private final UserService userService;
    private final LdapHistoryRepository ldapHistoryRepository;
    private final LdapErrorUserRepository ldapErrorUserRepository;
    private final LdapUserService ldapUserService;


    public LdapSyncUserTask(UserRepository userRepository,
                            UserService userService,
                            LdapHistoryRepository ldapHistoryRepository,
                            LdapErrorUserRepository ldapErrorUserRepository,
                            LdapUserService ldapUserService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.ldapHistoryRepository = ldapHistoryRepository;
        this.ldapErrorUserRepository = ldapErrorUserRepository;
        this.ldapUserService = ldapUserService;
    }

    @Async("LdapExecutor")
    public void syncDisabledLDAPUser(LdapTemplate ldapTemplate, Ldap ldap, FinishFallback fallback, String syncType) {
        logger.info("@@@ start disable user");

        Long organizationId = ldap.getOrganizationId();

        LdapSyncReport ldapSyncReport = new LdapSyncReport(organizationId);
        ldapSyncReport.setLdapId(ldap.getId());
        ldapSyncReport.setStartTime(new Date(System.currentTimeMillis()));

        LdapHistory ldapHistory = new LdapHistory();
        ldapHistory.setLdapId(ldap.getId());
        ldapHistory.setSyncBeginTime(ldapSyncReport.getStartTime());
        ldapHistory.setSyncType(syncType);
        ldapHistory.setTenantId(organizationId);
        ldapHistoryRepository.insertSelective(ldapHistory);

        disabledUsersFromLdapServer(ldapTemplate, ldap, ldapSyncReport, ldapHistory.getId());

        logger.info("@@@total user count : {}", ldapSyncReport.getCount());
        ldapSyncReport.setEndTime(new Date(System.currentTimeMillis()));
        logger.info("async finished : {}", ldapSyncReport);
        fallback.callback(ldapSyncReport, ldapHistory);
    }


    @Async("LdapExecutor")
    public void syncLDAPUser(LdapTemplate ldapTemplate, Ldap ldap, FinishFallback fallback, String syncType) {
        logger.info("@@@ start async user");
        Long organizationId = ldap.getOrganizationId();
        LdapSyncReport ldapSyncReport = new LdapSyncReport(organizationId);
        ldapSyncReport.setLdapId(ldap.getId());
        ldapSyncReport.setStartTime(new Date(System.currentTimeMillis()));

        LdapHistory ldapHistory = new LdapHistory();
        ldapHistory.setLdapId(ldap.getId());
        ldapHistory.setSyncBeginTime(ldapSyncReport.getStartTime());
        ldapHistory.setSyncType(syncType);
        ldapHistory.setTenantId(organizationId);
        ldapHistoryRepository.insertSelective(ldapHistory);

        Long ldapHistoryId = ldapHistory.getId();
        getUsersFromLdapServer(ldapTemplate, ldap, ldapSyncReport, ldapHistoryId);
        logger.info("@@@total user count : {}", ldapSyncReport.getCount());
        ldapSyncReport.setEndTime(new Date(System.currentTimeMillis()));
        logger.info("async finished : {}", ldapSyncReport);

        fallback.callback(ldapSyncReport, ldapHistory);
    }

    public void disabledUsersFromLdapServer(LdapTemplate ldapTemplate, Ldap ldap, LdapSyncReport ldapSyncReport, Long ldapHistoryId) {
        //搜索控件
        final SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        //Filter
        AndFilter andFilter = getAndFilterByObjectClass(ldap);
        HardcodedFilter hardcodedFilter = new HardcodedFilter(ldap.getCustomFilter());
        andFilter.and(hardcodedFilter);

        int batchSize = ldap.getSagaBatchSize();
        //分页PagedResultsDirContextProcessor
        final PagedResultsDirContextProcessor processor =
                new PagedResultsDirContextProcessor(batchSize);
        SingleContextSource.doWithSingleContext(
                ldapTemplate.getContextSource(), new LdapOperationsCallback<List<User>>() {
                    @Override
                    public List<User> doWithLdapOperations(LdapOperations operations) {
                        Integer page = 1;
                        AttributesMapper attributesMapper = new AttributesMapper() {
                            @Override
                            public Object mapFromAttributes(Attributes attributes) {
                                return attributes;
                            }
                        };

                        do {
                            List<Attributes> attributesList = operations.search("", andFilter.toString(), searchControls, attributesMapper, processor);
                            //将当前分页的数据做插入处理
                            List<User> users = new ArrayList<>(256);
                            List<LdapErrorUser> errorUsers = new ArrayList<>(10);
                            if (attributesList.isEmpty()) {
                                logger.warn("can not find any attributes while filter is {}, page is {}", andFilter, page);
                                break;
                            } else {
                                processUserFromAttributes(ldap, attributesList, users, ldapSyncReport, errorUsers);
                            }
                            //当前页做用户停用
                            if (!users.isEmpty()) {
                                Long disabledCount = compareWithDbAndDisabled(users);
                                ldapSyncReport.incrementUpdate(disabledCount);
                            }
                            insertErrorUser(errorUsers, ldapHistoryId);
                            int legalUserSize = users.size();
                            attributesList.clear();
                            users.clear();
                            errorUsers.clear();
                            ldapSyncReport.incrementCount((long) legalUserSize);
                        } while (processor.hasMore());
                        return null;
                    }
                });
    }

    private void getUsersFromLdapServer(LdapTemplate ldapTemplate, Ldap ldap, LdapSyncReport ldapSyncReport, Long ldapHistoryId) {
        //搜索控件
        final SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        //Filter
        AndFilter andFilter = getAndFilterByObjectClass(ldap);
        HardcodedFilter hardcodedFilter = new HardcodedFilter(ldap.getCustomFilter());
        andFilter.and(hardcodedFilter);

        int batchSize = ldap.getSagaBatchSize();
        //分页PagedResultsDirContextProcessor
        final PagedResultsDirContextProcessor processor = new PagedResultsDirContextProcessor(batchSize);
        SingleContextSource.doWithSingleContext(
                ldapTemplate.getContextSource(), (LdapOperationsCallback<List<User>>) operations -> {
                    Integer page = 1;
                    AttributesMapper attributesMapper = attributes -> attributes;

                    do {
                        List<Attributes> attributesList = operations.search("", andFilter.toString(), searchControls, attributesMapper, processor);
                        //将当前分页的数据做插入处理
                        List<User> users = new ArrayList<>(256);
                        List<LdapErrorUser> errorUsers = new ArrayList<>(10);
                        if (attributesList.isEmpty()) {
                            logger.warn("can not find any attributes while filter is {}, page is {}", andFilter, page);
                            break;
                        } else {
                            processUserFromAttributes(ldap, attributesList, users, ldapSyncReport, errorUsers);
                        }
                        //当前页做数据写入
                        if (!users.isEmpty()) {
                            compareWithDbAndInsert(users, ldapSyncReport, errorUsers);
                        }
                        insertErrorUser(errorUsers, ldapHistoryId);
                        attributesList.clear();
                        users.clear();
                        errorUsers.clear();
                        ldapSyncReport.incrementCount((long) users.size());
                        page++;
                    } while (processor.hasMore());
                    return null;
                });
    }

    private void processUserFromAttributes(Ldap ldap, List<Attributes> attributesList,
                                           List<User> users, LdapSyncReport ldapSyncReport,
                                           List<LdapErrorUser> errorUsers) {
        Long organizationId = ldap.getOrganizationId();
        String loginNameFiled = ldap.getLoginNameField();
        String uuidField = ldap.getUuidField();
        String realNameFiled = ldap.getRealNameField();
        String phoneField = ldap.getPhoneField();
        String emailField = ldap.getEmailField();
        attributesList.forEach(attributes -> {
            Attribute uuidAttribute = attributes.get(uuidField);
            Attribute loginNameAttribute = attributes.get(loginNameFiled);
            Attribute realNameAttribute = null;
            if (StringUtils.isNotBlank(realNameFiled)) {
                realNameAttribute = attributes.get(realNameFiled);
            }
            Attribute phoneAttribute = null;
            if (StringUtils.isNotBlank(phoneField)) {
                phoneAttribute = attributes.get(phoneField);
            }
            Attribute emailAttribute = null;
            if (StringUtils.isNotBlank(emailField)) {
                emailAttribute = attributes.get(emailField);
            }

            String uuid;
            String loginName;
            String email = null;
            String realName = null;
            String phone = null;
            if (uuidAttribute == null) {
                ldapSyncReport.incrementError();
                logger.error("the uuid {} of attributes {} can not be null, skip the user", ldap.getUuidField(), attributes);
                return;
            }
            try {
                uuid = uuidAttribute.get().toString();
            } catch (NamingException e) {
                ldapSyncReport.incrementError();
                logger.error("attributes {} get uuid attribute exception {}, skip the user", attributes, e);
                return;
            }

            if (loginNameAttribute == null) {
                ldapSyncReport.incrementError();
                LdapErrorUser errorUser = new LdapErrorUser()
                                .setUuid(uuid)
                                .setCause(LdapErrorUserCause.LOGIN_NAME_FIELD_NULL.value());
                errorUsers.add(errorUser);
                return;
            }
            try {
                loginName = loginNameAttribute.get().toString();
            } catch (NamingException e) {
                ldapSyncReport.incrementError();
                LdapErrorUser errorUser =
                        new LdapErrorUser().setUuid(uuid)
                                .setCause(LdapErrorUserCause.LOGIN_NAME_GET_EXCEPTION.value());
                errorUsers.add(errorUser);
                return;
            }

            try {
                if (realNameAttribute != null) {
                    realName = realNameAttribute.get().toString();
                }
                if (phoneAttribute != null) {
                    phone = phoneAttribute.get().toString();
                }
                if (emailAttribute != null) {
                    email = emailAttribute.get().toString();
                }
            } catch (NamingException e) {
                logger.warn("realName or phone field attribute get exception {}", e.getMessage(), e);
            }

            User user = new User();
            user.setUuid(uuid);
            user.setOrganizationId(organizationId);
            user.setLanguage(User.DEFAULT_LANGUAGE);
            user.setTimeZone(User.DEFAULT_TIME_ZONE);
            user.setEnabled(true);
            user.setLocked(false);
            user.setLdap(true);
            user.setAdmin(false);
            user.setPassword("ldap-users-do-not-have-password");
            user.setLastPasswordUpdatedAt(new Date(System.currentTimeMillis()));
            user.setLoginName(loginName);
            user.setRealName(StringUtils.defaultIfBlank(realName, "empty-name"));
            user.setEmail(StringUtils.defaultIfBlank(email, null));
            user.setPhone(StringUtils.defaultIfBlank(phone, null));
            user.setEmailCheckFlag(Optional.ofNullable(user.getEmailCheckFlag()).orElse(BaseConstants.Flag.YES));
            user.setPhoneCheckFlag(Optional.ofNullable(user.getPhoneCheckFlag()).orElse(BaseConstants.Flag.YES));
            user.setPasswordResetFlag(Optional.ofNullable(user.getPasswordResetFlag()).orElse(BaseConstants.Flag.YES));
            // 设置不校验密码策略
            user.setCheckPasswordPolicy(false);

            users.add(user);
        });
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

    private void compareWithDbAndInsert(List<User> paramUsers, LdapSyncReport ldapSyncReport,
                                        List<LdapErrorUser> errorUsers) {
        // 按用户类型分类
        Map<String, List<User>> typeUsers = paramUsers.stream()
                .peek(u -> u.setUserType(StringUtils.defaultIfBlank(u.getUserType(), User.DEFAULT_USER_TYPE)))
                .collect(Collectors.groupingBy(User::getUserType));

        typeUsers.forEach((userType, users) -> {
            List<User> insertUsers = new ArrayList<>(256);
            List<User> updateUsers = new ArrayList<>(256);

            Set<String> nameSet = users.stream().map(User::getLoginName).collect(Collectors.toSet());
            Set<String> emailSet = users.stream().map(User::getEmail).collect(Collectors.toSet());
            Set<String> phoneSet = users.stream().map(User::getPhone).collect(Collectors.toSet());
            //oracle In-list上限为1000，这里List size要小于1000
            List<Set<String>> subNameSet = CollectionSubUtils.subSet(nameSet, 999);
            List<Set<String>> subEmailSet = CollectionSubUtils.subSet(emailSet, 999);
            List<Set<String>> subPhoneSet = CollectionSubUtils.subSet(phoneSet, 999);
            Set<String> existedNames = new HashSet<>(nameSet.size());
            Set<String> existedEmails = new HashSet<>(emailSet.size());
            Set<String> existedPhones = new HashSet<>(phoneSet.size());
            subNameSet.forEach(set -> existedNames.addAll(userRepository.matchLoginName(set)));
            subEmailSet.forEach(set -> existedEmails.addAll(userRepository.matchEmail(set, userType)));
            subPhoneSet.forEach(set -> existedPhones.addAll(userRepository.matchPhone(set, userType)));

            users.forEach(user -> {
                String loginName = user.getLoginName();
                if (!existedNames.contains(loginName)) {
                    if (existedEmails.contains(user.getEmail()) || existedPhones.contains(user.getPhone())) {
                        // 邮箱或手机重复，报错
                        ldapSyncReport.incrementError();
                        LdapErrorUser errorUser = new LdapErrorUser()
                                .setUuid(user.getUuid())
                                .setLoginName(loginName)
                                .setEmail(user.getEmail())
                                .setRealName(user.getRealName())
                                .setPhone(user.getPhone())
                                .setCause(LdapErrorUserCause.EMAIL_OR_PHONE_ALREADY_EXISTED.value());
                        errorUsers.add(errorUser);
                    } else {
                        insertUsers.add(user);
                        ldapSyncReport.incrementNewInsert();
                    }
                } else {
                    User exist = userRepository.selectByLoginName(loginName);
                    if (exist.ldapUser()) {
                        UserInfo userInfo = userRepository.selectUserInfoByPrimaryKey(exist.getId());
                        BeanUtils.copyProperties(userInfo, user);
                        user.setId(exist.getId());
                        user.setObjectVersionNumber(exist.getObjectVersionNumber());
                        // 更新Ldap用户
                        updateUsers.add(user);
                        ldapSyncReport.incrementUpdate();
                    } else {
                        // 系统中存在同名的非Ldap用户
                        ldapSyncReport.incrementError();
                        LdapErrorUser errorUser = new LdapErrorUser()
                                .setUuid(user.getUuid())
                                .setLoginName(loginName)
                                .setEmail(user.getEmail())
                                .setRealName(user.getRealName())
                                .setPhone(user.getPhone())
                                .setCause(LdapErrorUserCause.EXISTS_NON_LDAP_SAME_LOGIN_NAME.value());
                        errorUsers.add(errorUser);
                    }
                }

            });
            insertUser(ldapSyncReport, errorUsers, insertUsers);
            updateUser(ldapSyncReport, errorUsers, updateUsers);
            cleanAfterDataPersistence(insertUsers, updateUsers, nameSet, emailSet, subNameSet, subEmailSet, existedNames, existedEmails);
        });
    }


    private Long compareWithDbAndDisabled(List<User> paramUsers) {
        // 按用户类型分类
        Map<String, List<User>> typeUsers = paramUsers.stream()
                .peek(u -> u.setUserType(StringUtils.defaultIfBlank(u.getUserType(), User.DEFAULT_USER_TYPE)))
                .collect(Collectors.groupingBy(User::getUserType));

        //获取数据库中已存在登录名的userIdSet
        Set<Long> idsByExistedNames = new HashSet<>();

        typeUsers.forEach((userType, users) -> {
            if (CollectionUtils.isEmpty(users)) {
                return;
            }
            Long organizationId = users.get(0).getOrganizationId();
            //获取同步列表中的loginNameSet
            Set<String> nameSet = users.stream().map(User::getLoginName).collect(Collectors.toSet());
            //oracle In-list上限为1000，这里List size要小于1000
            List<Set<String>> subNameSet = CollectionSubUtils.subSet(nameSet, 999);

            subNameSet.forEach(set -> idsByExistedNames.addAll(userRepository.getIdsByMatchLoginName(set)));
            //oracle In-list上限为1000，这里List size要小于1000
            List<Set<Long>> idsByExistedNamesList = CollectionSubUtils.subSet(idsByExistedNames, 999);

            if (CollectionUtils.isNotEmpty(idsByExistedNamesList)) {
                for (Set<Long> disableUserIds : idsByExistedNamesList) {
                    for (Long disableUserId : disableUserIds) {
                        userService.frozenUser(disableUserId, organizationId);
                    }
                }
            }
            //idsByExistedNamesList.forEach(userRepository::disableByIdList);

            nameSet.clear();
            subNameSet.clear();
        });

        return (long) idsByExistedNames.size();
    }


    private void insertErrorUser(List<LdapErrorUser> errorUsers, Long ldapHistoryId) {
        if (!errorUsers.isEmpty()) {
            LdapHistory ldapHistory = ldapHistoryRepository.selectByPrimaryKey(ldapHistoryId);
            errorUsers.forEach(errorUser -> {
                errorUser.setLdapHistoryId(ldapHistoryId);
                errorUser.setTenantId(ldapHistory.getTenantId());
                ldapErrorUserRepository.insertSelective(errorUser);
            });
        }
    }

    private void insertUser(LdapSyncReport ldapSyncReport, List<LdapErrorUser> errorUsers, List<User> insertUsers) {
        if (!insertUsers.isEmpty()) {
            List<LdapErrorUser> errorUserList = ldapUserService.batchCreateUsers(insertUsers);
            errorUsers.addAll(errorUserList);
            Long errorCount = (long) errorUserList.size();
            ldapSyncReport.reduceInsert(errorCount);
            ldapSyncReport.incrementError(errorCount);
        }
    }

    private void updateUser(LdapSyncReport ldapSyncReport, List<LdapErrorUser> errorUsers, List<User> updateUsers) {
        if (!updateUsers.isEmpty()) {
            List<LdapErrorUser> errorUserList = ldapUserService.batchUpdateUsers(updateUsers);
            errorUsers.addAll(errorUserList);
            Long errorCount = (long) errorUserList.size();
            ldapSyncReport.reduceUpdate(errorCount);
            ldapSyncReport.incrementError(errorCount);
        }
    }

    private void cleanAfterDataPersistence(List<User> insertUsers, List<User> updateUsers, Set<String> nameSet, Set<String> emailSet,
                                           List<Set<String>> subNameSet, List<Set<String>> subEmailSet,
                                           Set<String> existedNames, Set<String> existedEmails) {
        insertUsers.clear();
        updateUsers.clear();
        nameSet.clear();
        emailSet.clear();
        subNameSet.clear();
        subEmailSet.clear();
        existedNames.clear();
        existedEmails.clear();
    }

    public interface FinishFallback {
        /**
         * 同步完成后回调
         *
         * @param ldapSyncReport 同步结果
         */
        LdapHistory callback(LdapSyncReport ldapSyncReport, LdapHistory ldapHistory);
    }


    @Component
    public static class FinishFallbackImpl implements FinishFallback {

        private LdapHistoryRepository ldapHistoryRepository;

        public FinishFallbackImpl(LdapHistoryRepository ldapHistoryRepository) {
            this.ldapHistoryRepository = ldapHistoryRepository;
        }

        @Override
        public LdapHistory callback(LdapSyncReport ldapSyncReport, LdapHistory ldapHistory) {
            ldapHistory.setSyncEndTime(ldapSyncReport.getEndTime());
            ldapHistory.setNewUserCount(ldapSyncReport.getInsert());
            ldapHistory.setUpdateUserCount(ldapSyncReport.getUpdate());
            ldapHistory.setErrorUserCount(ldapSyncReport.getError());
            ldapHistory.setTenantId(ldapSyncReport.getOrganizationId());
            ldapHistoryRepository.updateByPrimaryKeySelective(ldapHistory);
            return ldapHistory;
        }
    }
}
