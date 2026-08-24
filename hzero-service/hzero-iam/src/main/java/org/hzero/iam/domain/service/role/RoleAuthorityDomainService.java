package org.hzero.iam.domain.service.role;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hzero.boot.platform.data.permission.util.DocRedisUtils;
import org.hzero.iam.api.dto.RoleAuthorityDTO;
import org.hzero.iam.app.assembler.RoleAuthorityAssembler;
import org.hzero.iam.domain.entity.RoleAuthority;
import org.hzero.iam.domain.entity.RoleAuthorityLine;
import org.hzero.iam.domain.repository.RoleAuthorityRepository;
import org.hzero.iam.infra.constant.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;

/**
 * @author mingke.yan@hand-china.com
 */
@Component
public class RoleAuthorityDomainService extends AbstractAuthorityCommonService {

    @Autowired
    private RoleAuthorityRepository roleAuthorityRepository;


    public void createRoleAuthority(RoleAuthorityDTO roleAuthorityDTO){
        RoleAuthority roleAuthority = RoleAuthorityAssembler.translateRoleAuthority(roleAuthorityDTO);
        // 新增角色数据权限
//        roleAuthorityRepository.insertSelective(roleAuthority);
        saveDefaultRoleAuth(roleAuthority);
        roleAuthorityDTO.setRoleAuthId(roleAuthority.getRoleAuthId());
        if (!CollectionUtils.isEmpty(roleAuthorityDTO.getRoleAuthorityLines())){
            // 给数据权限行添加头ID
            this.setRoleAuthorityId(roleAuthorityDTO.getRoleAuthorityLines(),roleAuthority.getRoleAuthId());
            // 新增角色数据权限行
//            roleAuthorityLineRepository.batchInsertSelective(roleAuthorityDTO.getRoleAuthorityLines());
            batchSaveDefalutRoleAuthLine(roleAuthorityDTO.getRoleAuthorityLines());
            // DOC 批量添加角色权限维度行缓存
            roleAuthorityDTO.getRoleAuthorityLines()
                            .forEach(roleAuthorityLine -> DocRedisUtils.setDocRoleAuthLineRedis(
                                            roleAuthorityLine.getDocTypeId(), roleAuthorityLine.getDimensionType(),
                                            roleAuthorityLine.getAuthTypeCode(), roleAuthorityLine.getRoleId()));
        }
        // DOC 设置角色单据权限头缓存
        DocRedisUtils.setDocRoleAuthHeaderRedis(roleAuthority.getAuthDocTypeId(), roleAuthority.getAuthScopeCode(),
                        roleAuthority.getRoleId());
    }

    public void updateRoleAuthority(RoleAuthorityDTO roleAuthorityDTO){
        List<RoleAuthorityLine> roleAuthorityLines = roleAuthorityDTO.getRoleAuthorityLines();
        this.setRoleAuthorityId(roleAuthorityLines,roleAuthorityDTO.getRoleAuthId());
        RoleAuthority roleAuthority = roleAuthorityRepository.selectByPrimaryKey(roleAuthorityDTO.getRoleAuthId());
        if (roleAuthority == null){
            throw new CommonException("error.data_not_exists");
        }
        // 改变SCOPE时,删除原来所有的数据权限行
        if (!StringUtils.equals(roleAuthorityDTO.getAuthScopeCode(),roleAuthority.getAuthScopeCode())){
//            RoleAuthorityLine roleAuthorityLine = new RoleAuthorityLine();
//            roleAuthorityLine.setRoleAuthId(roleAuthorityDTO.getRoleAuthId());
//            roleAuthorityLineRepository.delete(roleAuthorityLine);
            removeDefaultRoleAuthLineByRoleAuthId(roleAuthorityDTO.getRoleAuthId());
        }

        // 个人用户,则互斥
        if (Constants.AUTHORITY_SCOPE_CODE.USER.equals(roleAuthorityDTO.getAuthScopeCode())){
            // 行数据不能大于1
            if (roleAuthorityLines != null && roleAuthorityLines.size()>1){
                throw new CommonException("error.data_overmuch");
            }
            // 删除改单据下其他所有数据权限行
//            RoleAuthorityLine roleAuthorityLine = new RoleAuthorityLine();
//            roleAuthorityLine.setRoleAuthId(roleAuthorityDTO.getRoleAuthId());
//            roleAuthorityLineRepository.delete(roleAuthorityLine);
            removeUserScopeRoleAuthLine(roleAuthorityDTO);
        }

        // 更新数据权限
        roleAuthorityRepository.updateByPrimaryKeySelective(RoleAuthorityAssembler.translateRoleAuthority(roleAuthorityDTO));

        // 更新数据权限行
        if (!CollectionUtils.isEmpty(roleAuthorityLines)) {
            List<RoleAuthorityLine> delRoleAuthLines = new LinkedList<>();
            List<RoleAuthorityLine> addRoleAuthLines = new LinkedList<>();
            roleAuthorityLines.forEach(roleAuthorityLine -> {
                if (roleAuthorityLine.getRoleAuthLineId() == null && 1L == roleAuthorityLine.getEnabledFlag()) {
                    // 无ID新增
//                    roleAuthorityLineRepository.insertSelective(roleAuthorityLine);
                    saveDefaultRoleAuthLine(roleAuthorityLine);
                    addRoleAuthLines.add(roleAuthorityLine);
                } else if (roleAuthorityLine.getRoleAuthLineId() != null && 0L == roleAuthorityLine.getEnabledFlag()){
                    // 有ID删除
//                    roleAuthorityLineRepository.deleteByPrimaryKey(roleAuthorityLine);
                    removeDefaultRoleAuthLineByRoleAuthLineId(roleAuthorityLine.getRoleAuthLineId());
                    delRoleAuthLines.add(roleAuthorityLine);
                }
            });
            // DOC 处理角色权限数据行缓存
            if (!CollectionUtils.isEmpty(addRoleAuthLines)) {
                addRoleAuthLines.forEach(addRoleAuthLine -> DocRedisUtils.setDocRoleAuthLineRedis(
                                addRoleAuthLine.getDocTypeId(), addRoleAuthLine.getDimensionType(),
                                addRoleAuthLine.getAuthTypeCode(), addRoleAuthLine.getRoleId()));
            }
            // DOC 删除角色权限数据行缓存
            if (!CollectionUtils.isEmpty(delRoleAuthLines)) {
                delRoleAuthLines.forEach(delRoleAuthLine -> DocRedisUtils.delDocRoleAuthLineRedis(
                        delRoleAuthLine.getDocTypeId(), delRoleAuthLine.getDimensionType(),
                        delRoleAuthLine.getAuthTypeCode(), delRoleAuthLine.getRoleId()));
            }
        }
    }

    public void deleteRoleAuthority(Long roleAuthId){
        // 删除角色数据权限行
//        RoleAuthorityLine roleAuthorityLine = new RoleAuthorityLine();
//        roleAuthorityLine.setRoleAuthId(roleAuthId);
//        roleAuthorityLineRepository.delete(roleAuthorityLine);
        removeDefaultRoleAuthLineByRoleAuthId(roleAuthId);
        // 删除角色数据权限
//        roleAuthorityRepository.deleteByPrimaryKey(roleAuthId);
        removeDefaultRoleAuthByRoleAuthId(roleAuthId);
    }

    public void setRoleId(List<RoleAuthorityDTO> roleAuthorityDtos,Long roleId){
        if (!CollectionUtils.isEmpty(roleAuthorityDtos)){
            roleAuthorityDtos.forEach( roleAuthorityDTO -> {
                roleAuthorityDTO.setRoleId(roleId);
                if (!CollectionUtils.isEmpty(roleAuthorityDTO.getRoleAuthorityLines())){
                    roleAuthorityDTO.getRoleAuthorityLines().forEach( roleAuthorityLine -> roleAuthorityLine.setRoleId(roleId));
                }
            });
        }
    }

    public void setRoleAuthorityId(List<RoleAuthorityLine> roleAuthorityLines,Long roleAuthorityId){
        if (!CollectionUtils.isEmpty(roleAuthorityLines)){
            roleAuthorityLines.forEach( roleAuthorityLine -> roleAuthorityLine.setRoleAuthId(roleAuthorityId));
        }
    }
}
