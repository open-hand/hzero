/**
 * Table - 角色管理 - 列表页面表格
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { Badge, Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { getCodeMeaning, isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';
import { operatorRender } from 'utils/renderer';

class List extends PureComponent {
  /**
   * defaultTableRowKey - 默认table rowKey
   */
  defaultTableRowKey = 'id';

  @Bind()
  optionsRender(text, record) {
    const { handleAction = (e) => e, path } = this.props;
    // assignedFlag   3 分配标志        -> 创建角色
    // adminFlag      2 管理标志
    // haveAdminFlag  1 有父级角色标志
    const branch =
      (record.assignedFlag === 1 ? 3 : 0) +
      // (record.adminFlag === 1 ? 2 : 0) +
      (record.superAdminFlag === 1 ? 1 : 0);
    const operators = [];
    // 编辑
    const editBtn = {
      key: 'edit',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.edit`,
              type: 'button',
              meaning: '角色管理-编辑',
            },
          ]}
          onClick={() => handleAction('edit', record)}
        >
          {intl.get(`hzero.common.button.edit`).d('编辑')}
        </ButtonPermission>
      ),
      len: 2,
      title: intl.get(`hzero.common.button.edit`).d('编辑'),
    };
    // 复制
    const copyBtn = {
      key: 'copy',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.copy`,
              type: 'button',
              meaning: '角色管理-复制',
            },
          ]}
          onClick={() => handleAction('copy', record)}
        >
          {intl.get(`hzero.common.button.copy`).d('复制')}
        </ButtonPermission>
      ),
      len: 2,
      title: intl.get(`hzero.common.button.copy`).d('复制'),
    };
    // 继承
    const inheritBtn = record.inheritable === 1 && {
      key: 'inherit',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.inherit`,
              type: 'button',
              meaning: '角色管理-继承',
            },
          ]}
          onClick={() => handleAction('inherit', record)}
        >
          {intl.get(`hiam.roleManagement.view.title.button.inherit`).d('继承')}
        </ButtonPermission>
      ),
      len: 2,
      title: intl.get(`hiam.roleManagement.view.title.button.inherit`).d('继承'),
    };
    // 启用/禁用
    const enableBtn = {
      key: 'enabled',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.status`,
              type: 'button',
              meaning: '角色管理-状态',
            },
          ]}
          onClick={() => handleAction('enabled', record)}
        >
          {record.enabled
            ? intl.get(`hzero.common.status.disable`).d('禁用')
            : intl.get(`hzero.common.status.enable`).d('启用')}
        </ButtonPermission>
      ),
      len: 2,
      title: record.enabled
        ? intl.get(`hzero.common.status.disable`).d('禁用')
        : intl.get(`hzero.common.status.enable`).d('启用'),
    };
    // 分配用户
    const assignMemberBtn = {
      key: 'assign-members',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.members`,
              type: 'button',
              meaning: '角色管理-分配用户',
            },
          ]}
          onClick={() => handleAction('editMembers', record)}
        >
          {intl.get(`hiam.roleManagement.view.title.members`).d('分配用户')}
        </ButtonPermission>
      ),
      len: 4,
      title: intl.get(`hiam.roleManagement.view.title.members`).d('分配用户'),
    };
    const assignClientBtn = {
      key: 'assign-client',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.clients`,
              type: 'button',
              meaning: '角色管理-分配客户端',
            },
          ]}
          onClick={() => handleAction('editClients', record)}
        >
          {intl.get(`hiam.roleManagement.view.title.clients`).d('分配客户端')}
        </ButtonPermission>
      ),
      len: 5,
      title: intl.get(`hiam.roleManagement.view.title.clients`).d('分配客户端'),
    };
    // 分配权限
    const assignPermission = {
      key: 'assign-permissions',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.assignPermissions`,
              type: 'button',
              meaning: '角色管理-分配权限',
            },
          ]}
          onClick={() => handleAction('assignPermissions', record)}
        >
          {intl.get(`hiam.roleManagement.view.button.assignPermissions`).d('分配权限')}
        </ButtonPermission>
      ),
      len: 4,
      title: intl.get(`hiam.roleManagement.view.button.assignPermissions`).d('分配权限'),
    };
    // 工作台配置-分配卡片
    const cardBtn = {
      key: 'assign-role',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.assignCards`,
              type: 'button',
              meaning: '角色管理-工作台配置',
            },
          ]}
          onClick={() => handleAction('assignCards', record)}
        >
          {intl.get(`hiam.roleManagement.view.title.assignCards`).d('工作台配置')}
        </ButtonPermission>
      ),
      len: 5,
      title: intl.get(`hiam.roleManagement.view.title.assignCards`).d('工作台配置'),
    };
    // 维护数据权限
    const dataPermissionBtn = {
      key: 'editPermission',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.editPermission`,
              type: 'button',
              meaning: '角色管理-维护数据权限',
            },
          ]}
          onClick={() => handleAction('editPermission', record)}
        >
          {intl.get(`hiam.roleManagement.view.button.button.editPermission`).d('维护数据权限')}
        </ButtonPermission>
      ),
      len: 6,
      title: intl.get(`hiam.roleManagement.view.button.button.editPermission`).d('维护数据权限'),
    };

    // 字段权限维护-Api字段权限维护-角色
    const fieldPermissionBtn = {
      key: 'field-permission-maintain',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.fieldPermission`,
              type: 'button',
              meaning: '角色管理-维护字段权限',
            },
          ]}
          onClick={() => handleAction('editFieldPermission', record)}
        >
          {intl.get('hiam.roleManagement.view.button.fieldPermission').d('维护字段权限')}
        </ButtonPermission>
      ),
      len: 6,
      title: intl.get('hiam.roleManagement.view.button.fieldPermission').d('维护字段权限'),
    };

    switch (branch) {
      case 1:
        operators.push(editBtn, enableBtn);
        break;
      case 3:
        operators.push(
          copyBtn,
          inheritBtn,
          assignMemberBtn,
          assignClientBtn,
          cardBtn,
          dataPermissionBtn,
          fieldPermissionBtn
        );
        break;
      case 4:
        operators.push(
          editBtn,
          enableBtn,
          copyBtn,
          inheritBtn,
          assignMemberBtn,
          assignClientBtn,
          cardBtn,
          dataPermissionBtn,
          fieldPermissionBtn
        );
        break;
      default:
        operators.push(
          editBtn,
          copyBtn,
          inheritBtn,
          enableBtn,
          assignMemberBtn,
          assignClientBtn,
          assignPermission,
          cardBtn,
          dataPermissionBtn,
          fieldPermissionBtn
        );
    }

    const newOperators = operators.filter(Boolean);
    return operatorRender(newOperators, record, { limit: 4 });
  }

  render() {
    const {
      code,
      dataSource = [],
      loading,
      organizationId,
      onListChange = (e) => e,
      tenantsMulti,
      ...others
    } = this.props;
    const isTenant = isTenantRoleLevel();

    const columns = [
      {
        dataIndex: 'name',
        title: intl.get(`hiam.roleManagement.model.roleManagement.name`).d('角色名称'),
        width: 200,
      },
      {
        dataIndex: 'code',
        title: intl.get(`hiam.roleManagement.model.roleManagement.code`).d('角色编码'),
        width: isTenant ? 300 : 150,
      },
      !VERSION_IS_OP &&
        !isTenant && {
          dataIndex: 'levelMeaning',
          title: intl.get(`hiam.roleManagement.model.roleManagement.level`).d('角色层级'),
          width: 120,
        },
      // {
      //   dataIndex: 'parentRoleName',
      //   title: intl.get('hiam.roleManagement.model.roleManagement.topRole').d('上级角色'),
      //   width: 150,
      // },
      !isTenant && {
        dataIndex: 'roleSource',
        title: intl.get(`hiam.roleManagement.model.roleManagement.roleSource`).d('角色来源'),
        width: 120,
        render: (text) => getCodeMeaning(text, code),
      },
      !VERSION_IS_OP &&
        (!isTenant || tenantsMulti) && {
          dataIndex: 'tenantName',
          title: intl.get(`hiam.roleManagement.model.roleManagement.tenant`).d('所属租户'),
          width: 150,
        },
      {
        dataIndex: 'inheritedRoleName',
        title: intl.get(`hiam.roleManagement.model.roleManagement.inheritedRole`).d('继承自'),
        width: 150,
      },
      {
        dataIndex: 'enabled',
        title: intl.get(`hzero.common.status`).d('状态'),
        width: 90,
        render: (text, record) => (
          <Badge
            status={record.enabled ? 'success' : 'error'}
            text={
              record.enabled
                ? intl.get(`hzero.common.status.enable`).d('启用')
                : intl.get(`hiam.roleManagement.view.title.disable`).d('禁用')
            }
          />
        ),
      },
      // {
      //   dataIndex: 'assignedFlag',
      //   title: intl.get('hiam.roleManagement.model.roleManagement.assignedFlag').d('分配标志'),
      //   width: 100,
      //   render: assignedRole => {
      //     if (assignedRole) {
      //       return <Tag color="green">{intl.get('hzero.common.status.yes').d('是')}</Tag>;
      //     }
      //     return <Tag color="orange">{intl.get('hzero.common.status.no').d('否')}</Tag>;
      //   },
      // },
      // {
      //   dataIndex: 'adminFlag',
      //   title: intl.get('hiam.roleManagement.model.roleManagement.adminFlag').d('管理标志'),
      //   width: 100,
      //   render: adminRole => {
      //     if (adminRole) {
      //       return <Tag color="green">{intl.get('hzero.common.status.yes').d('是')}</Tag>;
      //     }
      //     return <Tag color="orange">{intl.get('hzero.common.status.no').d('否')}</Tag>;
      //   },
      // },
      // {
      //   dataIndex: 'adminRoleName',
      //   title: intl.get('hiam.roleManagement.model.roleManagement.adminRole').d('父级管理角色'),
      //   width: 150,
      //   render: (adminRoleName, record) => {
      //     if (record.haveAdminFlag) {
      //       return adminRoleName;
      //     }
      //   },
      // },
      {
        dataIndex: 'createdUserName',
        title: intl.get('hiam.roleManagement.model.roleManagement.createdUserName').d('创建人'),
        width: 200,
      },
      // {
      //   dataIndex: 'levelPath',
      //   title: intl.get('hiam.roleManagement.model.roleManagement.levelPath').d('角色路径'),
      // },
      {
        key: 'operator',
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 290,
        // fixed: 'right',
        render: this.optionsRender,
      },
    ].filter(Boolean);
    const tableProps = {
      dataSource,
      loading,
      columns,
      pagination: false,
      bordered: true,
      childrenColumnName: 'childRoles',
      rowKey: this.defaultTableRowKey,
      scroll: { x: tableScrollWidth(columns) },
      onChange: onListChange,
      ...others,
    };
    return <Table {...tableProps} />;
  }
}

export default List;
