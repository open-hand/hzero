/*
 * List - 租户级子账户列表
 * @date: 2018/11/19 20:31:56
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Form, Table, Badge } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { dateRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

/**
 * 租户级子账户列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} showEditModal 显示编辑模态框
 * @reactProps {Object} form 表单
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class ListTable extends React.Component {
  render() {
    const {
      path,
      currentUserId,
      loading,
      searchPaging,
      handleRecordEditBtnClick,
      showGroupModal,
      handleRecordAuthManageBtnClick,
      onApiFieldPermission,
      // handleRecordGrantBtnClick,
      handleRecordUpdatePassword,
      handleRecordResetPassword,
      dataSource = [],
      pagination = {},
      openSecurityGroupDrawer = (e) => e,
      handleUnlock,
      customizeTable,
      handleViewEmployee,
    } = this.props;
    const columns = [
      {
        title: intl.get('hiam.subAccount.model.user.loginName').d('账号'),
        dataIndex: 'loginName',
        width: 120,
      },
      {
        title: intl.get('hiam.subAccount.model.user.realName').d('名称'),
        dataIndex: 'realName',
        // width: 220,
      },
      {
        title: intl.get('hiam.subAccount.model.user.email').d('邮箱'),
        dataIndex: 'email',
        width: 300,
      },
      {
        title: intl.get('hiam.subAccount.model.user.phone').d('手机号码'),
        dataIndex: 'phone',
        width: 200,
        render: (phone, record) => {
          if (record.internationalTelMeaning && record.phone) {
            // todo 需要改成好看的样子
            return `${record.internationalTelMeaning} | ${record.phone}`;
          }
          return phone;
        },
      },
      {
        title: intl.get('hzero.common.date.active.from').d('有效日期从'),
        dataIndex: 'startDateActive',
        width: 120,
        render: dateRender,
      },
      {
        title: intl.get('hzero.common.date.active.to').d('有效日期至'),
        dataIndex: 'endDateActive',
        width: 120,
        render: dateRender,
      },
      {
        title: intl.get('hiam.subAccount.model.user.enabled').d('冻结'),
        dataIndex: 'enabled',
        width: 80,
        render: (item) => (
          <Badge
            status={item === true ? 'success' : 'error'}
            text={
              item === false
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        title: intl.get('hiam.subAccount.model.user.locked').d('锁定'),
        dataIndex: 'locked',
        width: 80,
        render: (item) => (
          <Badge
            status={item === false ? 'success' : 'error'}
            text={
              item === true
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        title: intl.get('hiam.subAccount.model.user.userType').d('用户类型'),
        dataIndex: 'userTypeMeaning',
        width: 90,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'edit',
        width: 220,
        fixed: 'right',
        render: (_, record) => {
          const { admin = false } = record;
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.editList`,
                      type: 'button',
                      meaning: '子账户管理-编辑列表',
                    },
                  ]}
                  onClick={() => {
                    handleRecordEditBtnClick(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'employee',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.employee`,
                      type: 'button',
                      meaning: '子账户管理-查看员工',
                    },
                  ]}
                  key="edit"
                  onClick={() => {
                    handleViewEmployee(record);
                  }}
                >
                  {intl.get('hzero.common.button.viewEmployee').d('查看员工')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'team',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.userGroup`,
                      type: 'button',
                      meaning: '子账户管理-用户组分配',
                    },
                  ]}
                  onClick={() => {
                    showGroupModal(record);
                  }}
                >
                  {intl.get('hiam.subAccount.view.option.userGroup').d('用户组')}
                </ButtonPermission>
              ),
              len: 3,
              title: intl.get('hiam.subAccount.view.option.userGroup').d('用户组'),
            },
            {
              key: 'solution',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.authMaintain`,
                      type: 'button',
                      meaning: '子账户管理-维护数据权限',
                    },
                  ]}
                  onClick={() => {
                    handleRecordAuthManageBtnClick(record);
                  }}
                >
                  {intl.get('hiam.subAccount.view.option.authMaintain').d('维护数据权限')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hiam.subAccount.view.option.authMaintain').d('维护数据权限'),
            },
            {
              key: 'field-maintain',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.fieldMaintain`,
                      type: 'button',
                      meaning: '子账户管理-维护字段权限',
                    },
                  ]}
                  onClick={() => {
                    onApiFieldPermission(record);
                  }}
                >
                  {intl.get('hiam.subAccount.view.button.fieldMaintain').d('维护字段权限')}
                </ButtonPermission>
              ),
              len: 6,
              title: intl.get('hiam.subAccount.view.button.fieldMaintain').d('维护字段权限'),
            },
            {
              key: 'security-group',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.securityGroup`,
                      type: 'button',
                      meaning: '子账户管理-分配安全组',
                    },
                  ]}
                  onClick={() => openSecurityGroupDrawer(record)}
                >
                  {intl.get('hiam.subAccount.view.button.secGrp').d('分配安全组')}
                </ButtonPermission>
              ),
              len: 5,
              title: intl.get('hiam.subAccount.view.button.secGrp').d('分配安全组'),
            },
            {
              key: 'password',
              ele:
                admin || record.id === currentUserId ? null : (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.passwordUpdate`,
                        type: 'button',
                        meaning: '子账户管理-修改密码',
                      },
                    ]}
                    onClick={() => {
                      handleRecordUpdatePassword(record);
                    }}
                  >
                    {intl.get('hiam.subAccount.view.option.passwordUpdate').d('修改密码')}
                  </ButtonPermission>
                ),
              len: 4,
              title: intl.get('hiam.subAccount.view.option.passwordUpdate').d('修改密码'),
            },
            {
              key: 'reset',
              ele:
                admin || record.id === currentUserId ? null : (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.reset`,
                        type: 'button',
                        meaning: '子账户管理-重置密码',
                      },
                    ]}
                    key="password"
                    onClick={() => {
                      handleRecordResetPassword(record);
                    }}
                  >
                    {intl.get('hiam.subAccount.view.option.resetPassword').d('重置密码')}
                  </ButtonPermission>
                ),
              len: 4,
              title: intl.get('hiam.subAccount.view.option.resetPassword').d('重置密码'),
            },
            {
              key: 'unlock',
              ele: record.locked && (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.unlock`,
                      type: 'button',
                      meaning: '子账户管理-解除锁定',
                    },
                  ]}
                  key="unlock"
                  onClick={() => {
                    handleUnlock(record);
                  }}
                >
                  {intl.get('hiam.subAccount.view.option.unlock').d('解除锁定')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hiam.subAccount.view.option.unlock').d('解除锁定'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    const tableProps = {
      loading,
      dataSource,
      pagination,
      columns,
      bordered: true,
      rowKey: 'id',
      scroll: { x: tableScrollWidth(columns) },
      onChange: searchPaging,
    };
    return customizeTable({ code: 'HIAM.SUB_ACCOUND.GRID' }, <Table {...tableProps} />);
  }
}
