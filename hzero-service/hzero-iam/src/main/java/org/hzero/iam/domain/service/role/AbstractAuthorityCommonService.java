package org.hzero.iam.domain.service.role;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import org.hzero.boot.platform.data.permission.util.DocRedisUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.*;
import org.hzero.iam.infra.constant.Constants;


/**
 * @author jianyuan.wei@hand-china.com
 * @date 2020/2/19 12:48
 */
public abstract class AbstractAuthorityCommonService {

    protected final String DEFAULT_DATA_SOURCE = Constants.SecGrpAssign.DEFAULT_DATA_SOURCE;
    protected final String SEC_GRP_DATA_SOURCE = Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE;
    protected final String DEFAULT_SEC_GRP_DATA_SOURCE = Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE;

    @Autowired
    protected RoleAuthorityRepository roleAuthorityRepository;
    @Autowired
    protected RoleAuthorityLineRepository roleAuthorityLineRepository;
    @Autowired
    protected RoleAuthDataRepository roleAuthDataRepository;
    @Autowired
    protected RoleAuthDataLineRepository roleAuthDataLineRepository;
    @Autowired
    protected RoleRepository roleRepository;
    /**
     * 批量删除默认的角色数据权限行
     *
     * @param roleAuthDataLines 角色数据权限行
     */
    protected void batchRemoveDefaultRoleAuthDataLine(List<RoleAuthDataLine> roleAuthDataLines) {
        if (roleAuthDataLines != null) {
            for (RoleAuthDataLine roleAuthDataLine : roleAuthDataLines) {
                removeDefaultRoleAuthDataLine(roleAuthDataLine);
            }
        }
    }

    protected void removeDefaultRoleAuthDataLine(RoleAuthDataLine roleAuthDataLine) {
        if (roleAuthDataLine != null) {
            if (roleAuthDataLine.getAuthDataLineId() != null) {
                //通过主键删除
                removeDefaultRoleAuthDataLineByPrimaryKey(roleAuthDataLine.getAuthDataLineId());
            } else {
                //通过唯一键删除
                RoleAuthDataLine dataLineUnique = new RoleAuthDataLine();
                dataLineUnique.setAuthDataId(roleAuthDataLine.getAuthDataId());
                dataLineUnique.setDataId(roleAuthDataLine.getDataId());
                RoleAuthDataLine hasDataLine = roleAuthDataLineRepository.selectOne(dataLineUnique);
                if (hasDataLine != null) {
                    if (equalDefaultSecGrpDataSource(hasDataLine.getDataSource())) {
                        // 具有两个权限，去掉默认权限
                        hasDataLine.setDataSource(SEC_GRP_DATA_SOURCE);
                        roleAuthDataLineRepository.updateByPrimaryKeySelective(hasDataLine);
                    } else if (equalDefaultDataSource(hasDataLine.getDataSource())) {
                        //只有默认的权限，直接删除
                        roleAuthDataLineRepository.delete(dataLineUnique);
                    }
                }
            }
        }
    }

    protected void removeDefaultRoleAuthDataLineByPrimaryKey(Long authDataLineId) {
        if (authDataLineId != null) {
            RoleAuthDataLine hasDataLine = roleAuthDataLineRepository.selectByPrimaryKey(authDataLineId);
            if (hasDataLine != null) {
                if (equalDefaultSecGrpDataSource(hasDataLine.getDataSource())) {
                    // 具有两个权限，去掉默认权限
                    hasDataLine.setDataSource(SEC_GRP_DATA_SOURCE);
                    roleAuthDataLineRepository.updateByPrimaryKeySelective(hasDataLine);
                } else if (equalDefaultDataSource(hasDataLine.getDataSource())) {
                    //只有默认的权限，直接删除
                    roleAuthDataLineRepository.deleteByPrimaryKey(authDataLineId);
                }
            }
        }
    }

    /**
     * 批量保存角色数据权限行
     *
     * @param roleAuthDataLines 角色数据权限行列表
     */
    protected void batchSaveDefaultRoleAuthDataLine(List<RoleAuthDataLine> roleAuthDataLines) {
        if (roleAuthDataLines != null) {
            for (RoleAuthDataLine roleAuthDataLine : roleAuthDataLines) {
                saveDefalutRoleAuthDataLine(roleAuthDataLine);
            }
        }
    }

    /**
     * 保存角色数据权限行
     *
     * @param roleAuthDataLine 角色数据权限行
     */
    protected void saveDefalutRoleAuthDataLine(RoleAuthDataLine roleAuthDataLine) {
        if (roleAuthDataLine == null
                || roleAuthDataLine.getAuthDataId() == null
                || roleAuthDataLine.getDataId() == null) {
            return;
        }
        RoleAuthDataLine dataLineUnique = new RoleAuthDataLine();
        dataLineUnique.setAuthDataId(roleAuthDataLine.getAuthDataId());
        dataLineUnique.setDataId(roleAuthDataLine.getDataId());
        RoleAuthDataLine hasDataLine = roleAuthDataLineRepository.selectOne(dataLineUnique);
        if (hasDataLine != null) {
            if (containSecGrpDataSource(hasDataLine.getDataSource())) {
                hasDataLine.setDataSource(DEFAULT_SEC_GRP_DATA_SOURCE);
                roleAuthDataLineRepository.updateByPrimaryKeySelective(hasDataLine);
                //供外部使用
                roleAuthDataLine.setAuthDataLineId(hasDataLine.getAuthDataId());
            }
        } else {
            roleAuthDataLine.setDataSource(DEFAULT_DATA_SOURCE);
            roleAuthDataLineRepository.insertSelective(roleAuthDataLine);
        }
    }

    /**
     * 保存角色数据权限
     *
     * @param roleAuthData 角色数据权限
     */
    protected void saveDefaultRoleAuthData(RoleAuthData roleAuthData) {
        if (roleAuthData == null || roleAuthData.getRoleId() == null
                || roleAuthData.getTenantId() == null
                || StringUtils.isEmpty(roleAuthData.getAuthorityTypeCode())) {
            return;
        }
        RoleAuthData authDataUnique = new RoleAuthData();
        authDataUnique.setRoleId(roleAuthData.getRoleId());
        authDataUnique.setTenantId(roleAuthData.getTenantId());
        authDataUnique.setAuthorityTypeCode(roleAuthData.getAuthorityTypeCode());
        RoleAuthData hasRoleAuthData = roleAuthDataRepository.selectOne(authDataUnique);
        if (hasRoleAuthData != null) {
            //存在，更新
            if (containSecGrpDataSource(hasRoleAuthData.getDataSource())) {
                hasRoleAuthData.setDataSource(DEFAULT_SEC_GRP_DATA_SOURCE);
                roleAuthDataRepository.updateByPrimaryKey(hasRoleAuthData);

                //供外部使用
                roleAuthData.setAuthDataId(hasRoleAuthData.getAuthDataId());
            }
        } else {
            //不存在，则创建
            roleAuthData.setDataSource(DEFAULT_DATA_SOURCE);
            roleAuthDataRepository.insert(roleAuthData);
        }
    }

    /**
     * 通过角色权限ID 移除默认的角色权限行
     *
     * @param roleAuthId 角色权限ID
     */
    protected void removeDefaultRoleAuthLineByRoleAuthId(Long roleAuthId) {
        if (roleAuthId == null) {
            return;
        }
        List<RoleAuthorityLine> delRoleAuthorityLines = new LinkedList<>();
        List<RoleAuthorityLine> hasRoleAuthLines = roleAuthorityLineRepository.selectByRoleAuthId(roleAuthId);
        for (RoleAuthorityLine roleAuthLine : hasRoleAuthLines) {
            if (equalDefaultDataSource(roleAuthLine.getDataSource())) {
                //只有默认的权限
                roleAuthorityLineRepository.deleteByPrimaryKey(roleAuthLine);
                delRoleAuthorityLines.add(roleAuthLine);
            } else if (equalDefaultSecGrpDataSource(roleAuthLine.getDataSource())) {
                // 具有默认和安全组双权限
                roleAuthLine.setDataSource(SEC_GRP_DATA_SOURCE);
                roleAuthorityLineRepository.updateByPrimaryKeySelective(roleAuthLine);
            }
        }
        delRoleAuthorityLines.forEach(delRoleAuthLine -> {
            // DOC 清除角色权限数据行缓存
            DocRedisUtils.delDocRoleAuthLineRedis(delRoleAuthLine.getDocTypeId(), delRoleAuthLine.getDimensionType(),
                    delRoleAuthLine.getAuthTypeCode(), delRoleAuthLine.getRoleId());
        });
    }

    /**
     * 处理个人用户类型的数据
     *
     * @param roleAuthorityDTO 角色权限DTO
     */
    protected void removeUserScopeRoleAuthLine(RoleAuthorityDTO roleAuthorityDTO) {
        if (roleAuthorityDTO.getRoleAuthId() == null) {
            return;
        }
        List<RoleAuthorityLine> roleAuthorityLines = roleAuthorityDTO.getRoleAuthorityLines();
        List<RoleAuthorityLine> delRoleAuthorityLines = new LinkedList<>();
        List<RoleAuthorityLine> hasRoleAuthLines = roleAuthorityLineRepository.selectByRoleAuthId(roleAuthorityDTO.getRoleAuthId());
        for (RoleAuthorityLine roleAuthLine : hasRoleAuthLines) {
            if (equalDefaultDataSource(roleAuthLine.getDataSource())) {
                //只有默认的权限,判断传入的角色权限行id是否存在且包含在需删除的角色权限行中，若存在则排除该条数据无需删除，用于后期对其进行更新
                if (!(!CollectionUtils.isEmpty(roleAuthorityLines)
                        && roleAuthorityLines.get(0).getRoleAuthLineId() != null
                        && roleAuthorityLines.get(0).getRoleAuthLineId().equals(roleAuthLine.getRoleAuthLineId()))) {
                    roleAuthorityLineRepository.deleteByPrimaryKey(roleAuthLine);
                    delRoleAuthorityLines.add(roleAuthLine);
                }
            } else if (equalDefaultSecGrpDataSource(roleAuthLine.getDataSource())) {
                // 具有默认和安全组双权限
                if (!(!CollectionUtils.isEmpty(roleAuthorityLines)
                        && roleAuthorityLines.get(0).getRoleAuthLineId() != null
                        && roleAuthorityLines.get(0).getRoleAuthLineId().equals(roleAuthLine.getRoleAuthLineId()))) {
                    roleAuthLine.setDataSource(SEC_GRP_DATA_SOURCE);
                    roleAuthorityLineRepository.updateByPrimaryKeySelective(roleAuthLine);
                }
            }
        }
        delRoleAuthorityLines.forEach(delRoleAuthLine -> {
            // DOC 清除角色权限数据行缓存
            DocRedisUtils.delDocRoleAuthLineRedis(delRoleAuthLine.getDocTypeId(), delRoleAuthLine.getDimensionType(),
                    delRoleAuthLine.getAuthTypeCode(), delRoleAuthLine.getRoleId());
        });
    }

    /**
     * 通过角色权限行ID 删除角色权限行
     *
     * @param roleAuthLineId 角色权限行ID
     */
    protected void removeDefaultRoleAuthLineByRoleAuthLineId(Long roleAuthLineId) {
        if (roleAuthLineId == null) {
            return;
        }
        RoleAuthorityLine hasRoleAuthLine = roleAuthorityLineRepository.selectByPrimaryKey(roleAuthLineId);
        if (hasRoleAuthLine != null) {
            if (equalDefaultDataSource(hasRoleAuthLine.getDataSource())) {
                //只有默认权限
                roleAuthorityLineRepository.delete(hasRoleAuthLine);
            } else if (equalDefaultSecGrpDataSource(hasRoleAuthLine.getSourceMatchField())) {
                //默认和安全组双权限,则需要移除掉默认权限
                hasRoleAuthLine.setDataSource(SEC_GRP_DATA_SOURCE);
                roleAuthorityLineRepository.updateByPrimaryKeySelective(hasRoleAuthLine);
            }
        }
    }

    /**
     * 通过角色权限ID 删除角色权限
     *
     * @param roleAuthId 角色权限ID
     */
    protected void removeDefaultRoleAuthByRoleAuthId(Long roleAuthId) {
        if (roleAuthId == null) {
            return;
        }
        RoleAuthority roleAuthority = roleAuthorityRepository.selectByPrimaryKey(roleAuthId);
        if (roleAuthority != null) {
            if (equalDefaultDataSource(roleAuthority.getDataSource())) {
                roleAuthorityRepository.deleteByPrimaryKey(roleAuthority);
                DocRedisUtils.delDocRoleAuthHeaderRedis(roleAuthority.getAuthDocTypeId(), roleAuthority.getAuthScopeCode(), roleAuthority.getRoleId());
            } else if (equalDefaultSecGrpDataSource(roleAuthority.getDataSource())) {
                roleAuthority.setDataSource(SEC_GRP_DATA_SOURCE);
                roleAuthorityRepository.updateByPrimaryKeySelective(roleAuthority);
            }
        }
    }

    /**
     * 批量保存角色权限行
     *
     * @param roleAuthLines 角色权限列表
     */
    protected void batchSaveDefalutRoleAuthLine(List<RoleAuthorityLine> roleAuthLines) {
        if (roleAuthLines != null) {
            for (RoleAuthorityLine roleAuthLine : roleAuthLines) {
                saveDefaultRoleAuthLine(roleAuthLine);
            }
        }
    }

    /**
     * 保存角色权限行
     *
     * @param roleAuthorityLine 角色权限行
     */
    protected void saveDefaultRoleAuthLine(RoleAuthorityLine roleAuthorityLine) {
        if (!legalInsertRoleAuthorityLine(roleAuthorityLine)) {
            return;
        }
        Role role = roleRepository.selectByPrimaryKey(roleAuthorityLine.getRoleId());
        Assert.notNull(role, BaseConstants.ErrorCode.NOT_NULL);
        RoleAuthorityLine roleAuthLineParam = new RoleAuthorityLine();
        roleAuthLineParam.setRoleAuthId(roleAuthorityLine.getRoleAuthId());
        roleAuthLineParam.setAuthTypeCode(roleAuthorityLine.getAuthTypeCode());
        RoleAuthorityLine hasRoleAuthLine = roleAuthorityLineRepository.selectOne(roleAuthLineParam);
        if (hasRoleAuthLine != null) {
            if (containSecGrpDataSource(hasRoleAuthLine.getDataSource())) {

                hasRoleAuthLine.setDataSource(DEFAULT_SEC_GRP_DATA_SOURCE);

                roleAuthorityLineRepository.updateByPrimaryKeySelective(hasRoleAuthLine);
                //将更新的主键ID保存到原对象中，供外部使用
                roleAuthorityLine.setRoleAuthId(hasRoleAuthLine.getRoleAuthId());
            }
        } else {
            roleAuthorityLine.setDataSource(DEFAULT_DATA_SOURCE);
            roleAuthorityLine.setTenantId(role.getTenantId());
            roleAuthorityLineRepository.insertSelective(roleAuthorityLine);
        }
    }

    /**
     * 保存默认的角色权限
     *
     * @param roleAuthority 角色权限
     */
    protected void saveDefaultRoleAuth(RoleAuthority roleAuthority) {
        if (!legalInsertRoleAuthority(roleAuthority)) {
            return;
        }
        Role role = roleRepository.selectByPrimaryKey(roleAuthority.getRoleId());
        Assert.notNull(role, BaseConstants.ErrorCode.NOT_NULL);
        RoleAuthority roleAuthorityParam = new RoleAuthority();
        roleAuthorityParam.setRoleId(roleAuthority.getRoleId());
        roleAuthorityParam.setAuthDocTypeId(roleAuthority.getAuthDocTypeId());
        roleAuthorityParam.setAuthScopeCode(roleAuthority.getAuthScopeCode());
        RoleAuthority hasRoleAuthority = roleAuthorityRepository.selectOne(roleAuthorityParam);
        if (hasRoleAuthority != null) {
            if (containSecGrpDataSource(hasRoleAuthority.getDataSource())) {
                hasRoleAuthority.setDataSource(DEFAULT_SEC_GRP_DATA_SOURCE);
                hasRoleAuthority.setAuthScopeCode(roleAuthority.getAuthScopeCode());
                hasRoleAuthority.setMsgFlag(roleAuthority.getMsgFlag());
                roleAuthorityRepository.updateByPrimaryKeySelective(hasRoleAuthority);
                //将更新的主键ID保存到原对象中，供外部使用
                roleAuthority.setRoleAuthId(hasRoleAuthority.getRoleAuthId());
            }
        } else {
            roleAuthority.setDataSource(DEFAULT_DATA_SOURCE);
            // 设置租户Id
            roleAuthority.setTenantId(role.getTenantId());
            roleAuthorityRepository.insertSelective(roleAuthority);
        }
    }

    private boolean legalInsertRoleAuthorityLine(RoleAuthorityLine roleAuthorityLine) {
        return roleAuthorityLine != null && roleAuthorityLine.getRoleAuthId() != null
                && !StringUtils.isEmpty(roleAuthorityLine.getAuthTypeCode());
    }

    /**
     * 检查创建的RoleAuthority是否合法
     *
     * @param roleAuthority 角色权限
     * @return
     */
    private boolean legalInsertRoleAuthority(RoleAuthority roleAuthority) {
        return roleAuthority != null && roleAuthority.getRoleId() != null
                && roleAuthority.getAuthDocTypeId() != null;
    }

    /**
     * 包含默认数据权限
     *
     * @param dataSource 数据来源标识
     * @return true: 包含
     */
    protected boolean containDefaultDataSource(String dataSource) {
        return StringUtils.contains(dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
    }

    /**
     * 包含安全组的数据权限
     *
     * @param dataSource 数据来源标识
     * @return true:包含
     */
    protected boolean containSecGrpDataSource(String dataSource) {
        return StringUtils.contains(dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
    }

    /**
     * 等于安全组的数据权限
     *
     * @param dataSource 数据来源标识
     * @return true:相等
     */
    protected boolean equalSecGrpDataSource(String dataSource) {
        return StringUtils.equals(dataSource, Constants.SecGrpAssign.SEC_GRP_DATA_SOURCE);
    }


    /**
     * 等于默认的数据权限
     *
     * @param dataSource 数据来源标识
     * @return true:相等
     */
    protected boolean equalDefaultDataSource(String dataSource) {
        return StringUtils.equals(dataSource, Constants.SecGrpAssign.DEFAULT_DATA_SOURCE);
    }


    /**
     * 等于默认和安全组的数据权限
     *
     * @param dataSource 数据来源标识
     * @return true:相等
     */
    protected boolean equalDefaultSecGrpDataSource(String dataSource) {
        return StringUtils.equals(dataSource, Constants.SecGrpAssign.DEFAULT_SEC_GRP_DATA_SOURCE);
    }
}
