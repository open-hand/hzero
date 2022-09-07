package org.hzero.iam.domain.service.secgrp.authority.impl;

import io.choerodon.core.exception.CommonException;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.FieldPermissionRepository;
import org.hzero.iam.domain.repository.FieldRepository;
import org.hzero.iam.domain.repository.SecGrpAclFieldRepository;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.hzero.iam.domain.service.secgrp.authority.AbstractSecGrpAuthorityService;
import org.hzero.iam.domain.service.secgrp.enums.SecGrpAuthorityType;
import org.hzero.iam.domain.service.secgrp.observer.field.RoleSecGrpFieldObserver;
import org.hzero.iam.domain.service.secgrp.observer.field.SecGrpFieldObserver;
import org.hzero.iam.domain.service.secgrp.observer.field.UserSecGrpFieldObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.hzero.iam.infra.constant.Constants.SecGrpAssignTypeCode.*;

/**
 * 安全组权限服务——字段权限
 *
 * @author bojiangzhou 2020/02/12
 */
@Component
public class SecGrpFieldAuthorityService extends AbstractSecGrpAuthorityService<SecGrpAclField> {

    private final Logger logger = LoggerFactory.getLogger(SecGrpFieldAuthorityService.class);

    @Autowired
    private SecGrpRepository secGrpRepository;
    @Autowired
    private SecGrpAclFieldRepository secGrpAclFieldRepository;
    @Autowired
    private FieldPermissionRepository fieldPermissionRepository;
    @Autowired
    private List<SecGrpFieldObserver> fieldObservers;
    @Autowired
    private RoleSecGrpFieldObserver roleSecGrpFieldObserver;
    @Autowired
    private UserSecGrpFieldObserver userSecGrpFieldObserver;
    @Autowired
    private FieldRepository fieldRepository;

    @Override
    public boolean support(@Nonnull SecGrpAuthorityType authorityType) {
        return SecGrpAuthorityType.ACL_FIELD.equals(authorityType);
    }

    @Override
    public void initSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull Long roleId) {
        // 查询当前角色被限制的字段权限
        List<SecGrpAclField> fields = secGrpAclFieldRepository.listRoleSecGrpField(roleId);
        fields.parallelStream().forEach(item -> {
            //切记不要修改入参的数据
            SecGrpAclField field = SecGrpAclField.initFrom(secGrp, item);
            field.setAssignTypeCode(PARENT);
            secGrpAclFieldRepository.insertSelective(field);
        });
    }

    /**
     * 说明一下这里的逻辑。对应字段权限而言，是黑名单机制，也就是说如果角色被分配的安全组中有字段限制， 那么角色自建的安全组中一定有此字段限制。
     */
    @Override
    public void copySecGrpAuthority(@Nonnull List<SecGrp> sourceSecGrps, @Nonnull SecGrp targetSecGrp) {
        List<SecGrpAclField> secGrpAclFields;

        List<Long> selfSecGrpId = sourceSecGrps.stream().filter(SecGrp::isSelfBuild).map(SecGrp::getSecGrpId)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(selfSecGrpId)) {
            // 有自建安全组的情况：只需要合并所有自建安全组中限定的字段权限即可，其一定包含了分配安全组中的字段权限，且可能包含新增的
            secGrpAclFields = secGrpAclFieldRepository.listSecGrpFields(selfSecGrpId, null);
        } else {
            // 当全部是分配安全组的情况：这个时候应该取角色被限制的字段
            secGrpAclFields = secGrpAclFieldRepository.listRoleSecGrpField(targetSecGrp.getRoleId());
            if (CollectionUtils.isNotEmpty(secGrpAclFields)) {
                // 因为查出来的都来自父级安全组，对于当前角色而言全部是分配的
                secGrpAclFields.forEach(item -> item.setAssignTypeCode(PARENT));
            }
        }

        if (CollectionUtils.isEmpty(secGrpAclFields)) {
            return;
        }

        // 去重
        Map<Long, SecGrpAclField> distinctMap = new HashMap<>(16);
        secGrpAclFields.forEach(item -> {
            SecGrpAclField aclField = distinctMap.get(item.getFieldId());
            // 若有重复的以后添加的AclField为准
            if (aclField == null || item.getCreationDate().after(aclField.getCreationDate())) {
                item.setSecGrpId(targetSecGrp.getSecGrpId());
                item.setSecGrpAclFieldId(null);
                distinctMap.put(item.getFieldId(), item);
            }
        });
        distinctMap.values().parallelStream().forEach(field -> secGrpAclFieldRepository.insertSelective(field));
    }

    @Override
    public void deleteAuthorityBySecGrpId(@Nonnull Long secGrpId) {
        secGrpAclFieldRepository.delete(new SecGrpAclField(secGrpId));
    }

    @Override
    public void assignRoleSecGrpAuthority(@Nonnull Role role, @Nonnull SecGrp secGrp) {
        // 查询安全组的字段权限
        List<SecGrpAclField> secGrpAclFields = this.secGrpAclFieldRepository.select(secGrp.getSecGrpId(), secGrp.getTenantId());

        // 给子安全组分配权限
        this.assignChildSecGrpField(secGrp, secGrpAclFields, (parentSecGrp, assignedRoles, fields) ->
                        this.roleSecGrpFieldObserver.assignRolesField(assignedRoles, fields),
                Collections.singletonList(role));
    }

    @Override
    public void recycleRoleSecGrpAuthority(@Nonnull Role role, @Nonnull SecGrp secGrp) {
        // 查询安全组的字段权限
        List<SecGrpAclField> secGrpAclFields = this.secGrpAclFieldRepository.select(secGrp.getSecGrpId(), secGrp.getTenantId());

        // 回收子安全组权限
        this.recycleChildSecGrpField(secGrp, secGrpAclFields, (parentSecGrp, assignedRoles, innerFields) ->
                        this.roleSecGrpFieldObserver.recycleRolesField(assignedRoles, innerFields),
                Collections.singletonList(role));
    }

    @Override
    public void assignUserSecGrpAuthority(@Nonnull User user, @Nonnull SecGrp secGrp) {
        // 查询角色分配的其他安全组没有包含的字段权限
        List<SecGrpAclField> notIncludedFields = this.getNotIncludedFields(secGrp.getSecGrpId(), user.getId());

        if (CollectionUtils.isNotEmpty(notIncludedFields)) {
            // 分配用户字段权限
            this.userSecGrpFieldObserver.assignUsersField(Collections.singletonList(this.buildSecGrpAssign(user)), notIncludedFields);
        }
    }

    @Override
    public void recycleUserSecGrpAuthority(@Nonnull User user, @Nonnull SecGrp secGrp) {
        // 查询角色分配的其他安全组没有包含的字段权限
        List<SecGrpAclField> notIncludedFields = this.getNotIncludedFields(secGrp.getSecGrpId(), user.getId());

        if (CollectionUtils.isNotEmpty(notIncludedFields)) {
            // 回收用户字段权限
            this.userSecGrpFieldObserver.recycleUsersField(Collections.singletonList(this.buildSecGrpAssign(user)), notIncludedFields);
        }
    }

    @Override
    protected void addSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpAclField> authorities) {
        if (CollectionUtils.isNotEmpty(authorities)) {
            Long secGrpId = secGrp.getSecGrpId();
            Long tenantId = secGrp.getTenantId();

            authorities.parallelStream().forEach(field -> {
                field.setTenantId(tenantId);
                field.setSecGrpId(secGrpId);
                field.setAssignTypeCode(SELF);
            });

            // 给子安全组分配权限
            this.assignChildSecGrpField(secGrp, authorities, (parentSecGrp, assignedRoles, fields) -> {
                for (SecGrpFieldObserver fieldObserver : this.fieldObservers) {
                    fieldObserver.assignSecGrpField(parentSecGrp, fields);
                }
            }, null);

            secGrpAclFieldRepository.batchAdd(authorities);
        }
    }

    @Override
    protected void updateSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpAclField> authorities) {
        if (CollectionUtils.isNotEmpty(authorities)) {
            Long secGrpId = secGrp.getSecGrpId();

            Set<Long> secGrpFieldIds = authorities.stream().map(SecGrpAclField::getSecGrpAclFieldId).collect(Collectors.toSet());
            // 查询待移除的字段权限
            List<SecGrpAclField> fields = secGrpAclFieldRepository.listSecGrpFields(Collections.singletonList(secGrpId), secGrpFieldIds);

            Map<Long, SecGrpAclField> secGrpAclFieldMap;
            if (CollectionUtils.isNotEmpty(fields)) {
                // 处理数据
                secGrpAclFieldMap = fields.stream()
                        .peek(secGrpAclField -> {
                            if (secGrpAclField.isAutoAssign()) {
                                throw new CommonException("hiam.error.secgrp.field.noAuthority");
                            }
                        })
                        .collect(toMap(SecGrpAclField::getSecGrpAclFieldId, t -> t));
            } else {
                secGrpAclFieldMap = Collections.emptyMap();
            }

            // 数据库的字段对象
            SecGrpAclField dbField;
            // 回收的字段
            List<SecGrpAclField> recycledField = new ArrayList<>();
            // 分配的字段
            List<SecGrpAclField> assignedField = new ArrayList<>();
            for (SecGrpAclField authority : authorities) {
                dbField = secGrpAclFieldMap.get(authority.getSecGrpAclFieldId());
                if (Objects.nonNull(dbField)) {
                    dbField.setPermissionRule(authority.getPermissionRule());
                    dbField.setRemark(authority.getRemark());
                    dbField.setPermissionType(authority.getPermissionType());
                    // 更新字段
                    this.secGrpAclFieldRepository.updateOptional(dbField,
                            SecGrpAclField.FIELD_PERMISSION_RULE,
                            SecGrpAclField.FIELD_REMARK,
                            SecGrpAclField.FIELD_PERMISSION_TYPE
                    );

                    recycledField.add(authority);
                    assignedField.add(dbField);
                }
            }

            // 回收分给子安全组的权限
            this.recycleChildSecGrpField(secGrp, recycledField, (parentSecGrp, assignedRoles, innerFields) -> {
                for (SecGrpFieldObserver fieldObserver : this.fieldObservers) {
                    fieldObserver.recycleSecGrpField(parentSecGrp, innerFields);
                }
            }, null);
            // 给子安全组分配权限
            this.assignChildSecGrpField(secGrp, assignedField, (parentSecGrp, assignedRoles, innerFields) -> {
                for (SecGrpFieldObserver fieldObserver : this.fieldObservers) {
                    fieldObserver.assignSecGrpField(parentSecGrp, innerFields);
                }
            }, null);
        }
    }

    @Override
    protected void removeSecGrpAuthority(@Nonnull SecGrp secGrp, @Nonnull List<SecGrpAclField> authorities) {
        if (CollectionUtils.isNotEmpty(authorities)) {
            Long secGrpId = secGrp.getSecGrpId();
            Set<Long> secGrpFieldIds = authorities.stream().map(SecGrpAclField::getSecGrpAclFieldId).collect(Collectors.toSet());
            // 查询待移除的字段权限
            List<SecGrpAclField> fields = secGrpAclFieldRepository.listSecGrpFields(Collections.singletonList(secGrpId), secGrpFieldIds);

            for (SecGrpAclField field : fields) {
                if (field.isAutoAssign()) {
                    throw new CommonException("hiam.error.secgrp.field.noAuthority");
                }
            }

            // 移除安全组字段权限
            fields.parallelStream().forEach(field -> secGrpAclFieldRepository.deleteByPrimaryKey(field));

            // 回收子安全组字段权限
            this.recycleChildSecGrpField(secGrp, fields, (parentSecGrp, assignedRoles, innerFields) -> {
                for (SecGrpFieldObserver fieldObserver : this.fieldObservers) {
                    fieldObserver.recycleSecGrpField(parentSecGrp, innerFields);
                }
            }, null);
        }
    }

    @Override
    protected void enableSecGrpAuthority(@Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        // 查询安全组的字段权限
        List<SecGrpAclField> secGrpAclFields = this.secGrpAclFieldRepository.listSecGrpAclField(secGrpId);
        if (CollectionUtils.isNotEmpty(secGrpAclFields)) {
            // 给子安全组分配权限
            this.assignChildSecGrpField(secGrp, secGrpAclFields, (parentSecGrp, assignedRoles, fields) -> {
                for (SecGrpFieldObserver fieldObserver : this.fieldObservers) {
                    fieldObserver.assignSecGrpField(parentSecGrp, fields);
                }
            }, null);
        }
    }

    @Override
    protected void disableSecGrpAuthority(@Nonnull SecGrp secGrp) {
        Long secGrpId = secGrp.getSecGrpId();
        // 查询安全组的字段权限
        List<SecGrpAclField> secGrpAclFields = this.secGrpAclFieldRepository.listSecGrpAclField(secGrpId);
        if (CollectionUtils.isNotEmpty(secGrpAclFields)) {
            // 回收子安全组字段权限
            this.recycleChildSecGrpField(secGrp, secGrpAclFields, (parentSecGrp, assignedRoles, innerFields) -> {
                for (SecGrpFieldObserver fieldObserver : this.fieldObservers) {
                    fieldObserver.recycleSecGrpField(parentSecGrp, innerFields);
                }
            }, null);
        }
    }

    /**
     * 分配子安全组字段权限
     *
     * @param parentSecGrp 父安全组
     * @param fields       字段权限数据
     */
    private void assignChildSecGrpField(SecGrp parentSecGrp, List<SecGrpAclField> fields,
                                        FieldObserverConsumer roleNotIncludedFieldObserver,
                                        List<Role> assignedRoles) {
        if (CollectionUtils.isNotEmpty(fields)) {
            // 安全组ID
            Long secGrpId = parentSecGrp.getSecGrpId();
            if (Objects.isNull(assignedRoles)) {
                // 查询安全组分配的角色
                assignedRoles = this.secGrpRepository.listSecGrpAssignedRole(secGrpId);
            }
            // 角色ID
            Long roleId;
            List<SecGrpAclField> allNotIncludedFields = fields;
            if (CollectionUtils.isNotEmpty(assignedRoles)) {
                allNotIncludedFields = new ArrayList<>();
                for (Role assignedRole : assignedRoles) {
                    roleId = assignedRole.getId();
                    // 查询角色分配的安全组中不包含这些字段的字段权限
                    List<SecGrpAclField> notIncludedFields = this.secGrpAclFieldRepository
                            .listRoleNotIncludedFields(roleId, secGrpId, fields);
                    if (CollectionUtils.isNotEmpty(notIncludedFields)) {
                        allNotIncludedFields.addAll(notIncludedFields);
                        // 查询角色创建的安全组
                        List<SecGrp> secGrps = secGrpRepository.listRoleCreatedSecGrp(roleId);
                        if (CollectionUtils.isNotEmpty(secGrps)) {
                            SecGrpAclField param = new SecGrpAclField();
                            for (SecGrp secGrp : secGrps) {
                                Long childSecGrpId = secGrp.getSecGrpId();
                                List<SecGrpAclField> newFields = new ArrayList<>(notIncludedFields.size());
                                for (SecGrpAclField field : notIncludedFields) {
                                    param.setSecGrpId(childSecGrpId);
                                    param.setFieldId(field.getFieldId());
                                    // 查询字段对象
                                    SecGrpAclField secGrpAclField = this.secGrpAclFieldRepository.selectOne(param);
                                    if (Objects.nonNull(secGrpAclField)) {
                                        if (SELF.equals(secGrpAclField.getAssignTypeCode())) {
                                            // 更新数据
                                            secGrpAclField.setAssignTypeCode(SELF_PARENT);
                                            this.secGrpAclFieldRepository.updateOptional(secGrpAclField,
                                                    SecGrpAclField.FIELD_ASSIGN_TYPE_CODE);
                                        }
                                    } else {
                                        SecGrpAclField newField = SecGrpAclField.initFrom(secGrp, field);
                                        newField.setAssignTypeCode(PARENT);
                                        secGrpAclFieldRepository.insertSelective(newField);
                                        newFields.add(newField);
                                    }
                                }

                                // 给子类分配权限
                                this.assignChildSecGrpField(secGrp, newFields, roleNotIncludedFieldObserver, null);
                            }
                        }
                    }
                }
            }

            // 处理父安全组分配的角色的字段权限(需要处理的权限是角色分配的所有安全组中不存在的权限)
            roleNotIncludedFieldObserver.operate(parentSecGrp, assignedRoles, allNotIncludedFields);
        }
    }

    /**
     * 回收子安全组字段权限
     *
     * @param parentSecGrp 安全组
     * @param fields       字段权限
     */
    private void recycleChildSecGrpField(SecGrp parentSecGrp, List<SecGrpAclField> fields,
                                         FieldObserverConsumer roleNotIncludedFieldObserver,
                                         List<Role> assignedRoles) {
        if (CollectionUtils.isNotEmpty(fields)) {
            // 安全组ID
            Long secGrpId = parentSecGrp.getSecGrpId();
            if (Objects.isNull(assignedRoles)) {
                // 查询安全组分配的角色
                assignedRoles = this.secGrpRepository.listSecGrpAssignedRole(secGrpId);
            }

            List<SecGrpAclField> allNotIncludedFields = fields;
            if (CollectionUtils.isNotEmpty(assignedRoles)) {
                allNotIncludedFields = new ArrayList<>();
                for (Role assignedRole : assignedRoles) {
                    Long roleId = assignedRole.getId();
                    // 查询角色分配的安全组中不包含这些字段的字段权限
                    List<SecGrpAclField> notIncludedFields = secGrpAclFieldRepository.listRoleNotIncludedFields(roleId, secGrpId, fields);
                    if (CollectionUtils.isNotEmpty(notIncludedFields)) {
                        allNotIncludedFields.addAll(notIncludedFields);
                        // 回收父安全组分配的角色的字段权限(需要回收的权限是角色分配的所有安全组中不存在的安全组)
                        for (SecGrpFieldObserver fieldObserver : fieldObservers) {
                            fieldObserver.recycleSecGrpField(parentSecGrp, notIncludedFields);
                        }

                        // 查询角色创建的安全组
                        List<SecGrp> secGrps = secGrpRepository.listRoleCreatedSecGrp(roleId);
                        if (CollectionUtils.isNotEmpty(secGrps)) {
                            SecGrpAclField param = new SecGrpAclField();
                            for (SecGrp secGrp : secGrps) {
                                Long childSecGrpId = secGrp.getSecGrpId();
                                // 回收的字段
                                List<SecGrpAclField> recycledFields = new ArrayList<>(notIncludedFields.size());
                                for (SecGrpAclField field : notIncludedFields) {
                                    param.setSecGrpId(childSecGrpId);
                                    param.setFieldId(field.getFieldId());
                                    // 查询字段对象
                                    SecGrpAclField secGrpAclField = this.secGrpAclFieldRepository.selectOne(param);
                                    if (Objects.nonNull(secGrpAclField)) {
                                        if (SELF_PARENT.equals(secGrpAclField.getAssignTypeCode())) {
                                            // 更新数据
                                            secGrpAclField.setAssignTypeCode(SELF);
                                            this.secGrpAclFieldRepository.updateOptional(secGrpAclField,
                                                    SecGrpAclField.FIELD_ASSIGN_TYPE_CODE);
                                        } else if (PARENT.equals(secGrpAclField.getAssignTypeCode())) {
                                            this.secGrpAclFieldRepository.deleteByPrimaryKey(secGrpAclField);
                                            recycledFields.add(secGrpAclField);
                                        }
                                    }
                                }

                                // 回收子类字段权限
                                this.recycleChildSecGrpField(secGrp, recycledFields, roleNotIncludedFieldObserver, null);
                            }
                        }
                    }
                }
            }

            // 处理父安全组分配的角色的字段权限(需要处理的权限是角色分配的所有安全组中不存在的权限)
            roleNotIncludedFieldObserver.operate(parentSecGrp, assignedRoles, allNotIncludedFields);
        }
    }

    /**
     * 获取指定用户分配的安全组中，指定安全组中，排除指定安全之外的其他安全组不包含的字段权限数据
     *
     * @param secGrpId 安全组ID
     * @param userId   用户ID
     * @return 满足条件的数据
     */
    private List<SecGrpAclField> getNotIncludedFields(Long secGrpId, Long userId) {
        // 获取安全组数据权限
        List<SecGrpAclField> allSecGrpAclFields = secGrpAclFieldRepository.select(SecGrpAclField.FIELD_SEC_GRP_ID, secGrpId);
        if (CollectionUtils.isEmpty(allSecGrpAclFields)) {
            return Collections.emptyList();
        }

        // 查询角色分配的其他安全组没有包含的字段权限
        return this.secGrpAclFieldRepository
                .listUserNotIncludedFields(userId, secGrpId, allSecGrpAclFields);
    }

    /**
     * <p>
     * Field的observer操作消费接口
     * </p>
     *
     * @author bo.he02@hand-china.com 2020/04/21 14:05
     */
    @FunctionalInterface
    private interface FieldObserverConsumer {
        /**
         * Field的observer操作
         *
         * @param secGrp 安全组
         * @param roles  处理的角色
         * @param fields 处理的字段权限
         */
        void operate(SecGrp secGrp, List<Role> roles, List<SecGrpAclField> fields);
    }
}
