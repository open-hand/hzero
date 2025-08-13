package org.hzero.iam.infra.repository.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.infra.constant.HiamResourceLevel;
import org.hzero.iam.infra.mapper.RoleMapper;

/**
 * @author bojiangzhou
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RoleRepositoryImplTest {

    @Autowired
    private RoleRepository roleRepository;
    @MockBean
    private RoleMapper roleMapper;

    /**
     * 顶级角色根据 levelPath 确定，处于根部的角色即为顶级角色
     */
    @Test
    public void testSelectTopAdminRoles() {
        //given(roleMapper.selectAssignedRoles(anyLong(), anyString())).willReturn(generateAssignedRolesData());
        // 顶级角色
        //Set<Long> topRoleIds = Sets.newLinkedHashSet(2L, 11L);

        //List<RoleVO> topRoleList = roleRepository.selectTopAdminRoles(1L);

        //Set<Long> selectedTopRoleIds = topRoleList.stream().map(RoleVO::getId).collect(Collectors.toSet());

        //Assert.isTrue(selectedTopRoleIds.containsAll(topRoleIds), "top admin role select incorrect");
    }

    private List<RoleVO> generateAssignedRolesData() {
        List<RoleVO> roleList = new ArrayList<>();
        RoleVO r1 = new RoleVO()
                .setId(2L)
                .setName("租户管理员")
                .setCode("role/organization/default/administrator")
                .setLevel(HiamResourceLevel.ORGANIZATION.code())
                .setParentRoleId(0L)
                .setInheritRoleId(0L)
                .setLevelPath("role/organization/default/administrator")
                .setInheritLevelPath("role/organization/default/administrator")
                .setTenantId(0L)
                .setCreatedByTenantId(0L);
        roleList.add(r1);

        RoleVO r2 = new RoleVO()
                .setId(115141857839165441L)
                .setName("zc-001")
                .setCode("zc-001")
                .setLevel(HiamResourceLevel.ORGANIZATION.code())
                .setParentRoleId(2L)
                .setInheritRoleId(0L)
                .setLevelPath("role/organization/default/administrator|O20804.HZERO.zc-001")
                .setInheritLevelPath("O20804.HZERO.zc-001")
                .setTenantId(9L)
                .setCreatedByTenantId(0L);
        roleList.add(r2);

        RoleVO r3 = new RoleVO()
                .setId(23L)
                .setName("租户管理员")
                .setCode("administrator")
                .setLevel(HiamResourceLevel.ORGANIZATION.code())
                .setParentRoleId(2L)
                .setInheritRoleId(6L)
                .setLevelPath("role/organization/default/administrator|O20804.T.HZERO.administrator")
                .setInheritLevelPath("HZERO.T.role/organization/default/template/administrator|O20804.T.HZERO.administrator")
                .setTenantId(9L)
                .setCreatedByTenantId(0L);
        roleList.add(r3);

        RoleVO r4 = new RoleVO()
                .setId(27L)
                .setName("测试继承")
                .setCode("test01")
                .setLevel(HiamResourceLevel.ORGANIZATION.code())
                .setParentRoleId(23L)
                .setInheritRoleId(26L)
                .setLevelPath("role/organization/default/administrator|O20804.T.HZERO.administrator|T.test01")
                .setInheritLevelPath("T.testcopy|T.test01")
                .setTenantId(9L)
                .setCreatedByTenantId(0L);
        roleList.add(r4);

        RoleVO r5 = new RoleVO()
                .setId(28L)
                .setName("租户管理员")
                .setCode("administrator")
                .setLevel(HiamResourceLevel.ORGANIZATION.code())
                .setParentRoleId(2L)
                .setInheritRoleId(6L)
                .setLevelPath("role/organization/default/administrator|TEST-002.T.HZERO.administrator")
                .setInheritLevelPath("HZERO.T.role/organization/default/template/administrator|TEST-002.T.HZERO.administrator")
                .setTenantId(11L)
                .setCreatedByTenantId(0L);
        roleList.add(r5);

        RoleVO r6 = new RoleVO()
                .setId(89667875807563777L)
                .setName("租户管理员")
                .setCode("administrator")
                .setLevel(HiamResourceLevel.ORGANIZATION.code())
                .setParentRoleId(2L)
                .setInheritRoleId(6L)
                .setLevelPath("role/organization/default/administrator|ZC-TEST-RCH.HZERO.administrator")
                .setInheritLevelPath("role/organization/default/template/administrator|ZC-TEST-RCH.HZERO.administrator")
                .setTenantId(11L)
                .setCreatedByTenantId(0L);
        roleList.add(r6);

        RoleVO r7 = new RoleVO()
                .setId(115141857839165449L)
                .setName("甄云租户管理员")
                .setCode("administrator")
                .setLevel(HiamResourceLevel.ORGANIZATION.code())
                .setParentRoleId(2L)
                .setInheritRoleId(6L)
                .setLevelPath("role/organization/default/administrator|zhenyun.HZERO.administrator")
                .setInheritLevelPath("role/organization/default/template/administrator|zhenyun.HZERO.administrator")
                .setTenantId(43L)
                .setCreatedByTenantId(0L);
        roleList.add(r7);

        RoleVO r8 = new RoleVO()
                .setId(115141857839165453L)
                .setName("自建-甄云02")
                .setCode("zhenyun02")
                .setLevel(HiamResourceLevel.ORGANIZATION.code())
                .setParentRoleId(115141857839165449L)
                .setInheritRoleId(0L)
                .setLevelPath("role/organization/default/administrator|zhenyun.HZERO.administrator|zhenyun.zhenyun.zhenyun02")
                .setInheritLevelPath("zhenyun.zhenyun.zhenyun02")
                .setTenantId(43L)
                .setCreatedByTenantId(43L);
        roleList.add(r8);

        RoleVO r9 = new RoleVO()
                .setId(115141857839165477L)
                .setName("自建-甄云02-01-cxk用户")
                .setCode("zhenyun02-01")
                .setLevel(HiamResourceLevel.ORGANIZATION.code())
                .setParentRoleId(115141857839165453L)
                .setInheritRoleId(0L)
                .setLevelPath("role/organization/default/administrator|zhenyun.HZERO.administrator|zhenyun.zhenyun.zhenyun02|zhenyun.zhenyun.zhenyun02-01")
                .setInheritLevelPath("zhenyun.zhenyun.zhenyun02-01")
                .setTenantId(43L)
                .setCreatedByTenantId(43L);
        roleList.add(r9);

        RoleVO r10 = new RoleVO()
                .setId(11L)
                .setName("测试继承角色")
                .setCode("test003")
                .setLevel(HiamResourceLevel.SITE.code())
                .setParentRoleId(1L)
                .setInheritRoleId(9L)
                .setLevelPath("role/site/default/administrator|HZERO.T.test003")
                .setInheritLevelPath("HZERO.T.test001|HZERO.T.test003")
                .setTenantId(43L)
                .setCreatedByTenantId(0L);
        roleList.add(r10);

        return roleList;
    }


}
