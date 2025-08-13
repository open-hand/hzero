/*
 * FieldPermission - 字段权限
 * @date: 2019-10-30
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';

import { fieldPermissionDS } from '@/stores/SecurityGroupDS';
import openConfigDrawer from './Drawer';

const organizationId = getCurrentOrganizationId();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';

export default class FieldPermission extends Component {
  fieldPermissionDS = new DataSet({
    ...fieldPermissionDS(),
    transport: {
      read: ({ data, params }) => {
        const { secGrpId, isSelf = false, roleId, secGrpSource } = this.props;
        const url = isSelf
          ? `${HZERO_IAM}/v1${levelUrl}/sec-grp-acl-fields/${secGrpId}`
          : `${HZERO_IAM}/v1${levelUrl}/sec-grp-acl-fields/${secGrpId}/assigned`;
        let newParams = { ...data, ...params };
        if (!isSelf) {
          newParams = {
            ...newParams,
            roleId,
            secGrpSource,
          };
        }
        return {
          url,
          params: newParams,
          method: 'get',
        };
      },
    },
  });

  /**
   * 显示配置弹窗W
   * @param {object} record - 表格行数据
   */
  @Bind()
  handleOpenConfigModal(record) {
    const { secGrpId, isSelf, roleId, secGrpSource } = this.props;
    openConfigDrawer(record, secGrpId, isSelf, roleId, secGrpSource);
  }

  get columns() {
    return [
      { name: 'serviceName', width: 200 },
      { name: 'fieldCount', width: 80 },
      { name: 'method', width: 120 },
      { name: 'path' },
      { name: 'description' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 130,
        command: ({ record }) => [
          <a onClick={() => this.handleOpenConfigModal(record)}>
            {intl.get('hiam.roleManagement.view.button.permissionMaintain').d('字段权限维护')}
          </a>,
        ],
        lock: 'right',
      },
    ];
  }

  render() {
    return <Table dataSet={this.fieldPermissionDS} columns={this.columns} queryFieldsLimit={3} />;
  }
}
