/**
 * Platform - 菜单管理 - 平台tab页
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table, Popconfirm, Tag, Tooltip, Icon } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import { VERSION_IS_OP } from 'utils/config';
import intl from 'utils/intl';
import { isTenantRoleLevel, getCurrentOrganizationId, tableScrollWidth } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';

export default class List extends PureComponent {
  state = {
    currentTenantId: getCurrentOrganizationId(),
  };

  defaultTableRowKey = 'ruleId';

  deleteRecord(record) {
    const { handleDelete = (e) => e } = this.props;
    handleDelete(record);
  }

  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 180,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: (e) => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  render() {
    const { dataSource = [], rowKey, openEditor = (e) => e, match, ...others } = this.props;
    const { currentTenantId } = this.state;
    const columns = [
      !isTenantRoleLevel() && {
        title: intl.get('hpfm.permission.model.permission.tenant').d('租户'),
        width: 150,
        dataIndex: 'tenantId',
        render: (text, record) => record.tenantName,
      },
      {
        title: intl.get('hpfm.permission.model.permission.ruleCode').d('规则编码'),
        width: 160,
        dataIndex: 'ruleCode',
        onCell: this.onCell.bind(this),
      },
      {
        title: intl.get('hpfm.permission.model.permission.ruleName').d('规则名称'),
        dataIndex: 'ruleName',
        width: 120,
        onCell: this.onCell.bind(this),
      },
      {
        title: intl.get('hpfm.permission.model.permission.ruleType').d('规则类型'),
        dataIndex: 'ruleTypeCodeMeaning',
        width: 100,
        onCell: this.onCell.bind(this),
      },
      {
        title: intl.get('hpfm.permission.model.permission.SQL').d('SQL'),
        dataIndex: 'sqlValue',
        width: 180,
        onCell: this.onCell.bind(this),
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 80,
      },
      {
        title: intl.get('hpfm.permission.model.permission.description').d('描述'),
        dataIndex: 'description',
        width: 180,
        onCell: this.onCell.bind(this),
      },
      isTenantRoleLevel() && {
        title: intl.get('hzero.common.source').d('来源'),
        width: 100,
        render: (_, record) => {
          return currentTenantId === record.tenantId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          );
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 110,
        // fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <>
                  <ButtonPermission
                    className="edit"
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.ruleEdit`,
                        type: 'button',
                        meaning: '数据权限规则-编辑',
                      },
                    ]}
                    onClick={() => openEditor(record)}
                    disabled={record.editableFlag === 0}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                  &nbsp;
                  {record.editableFlag === 0 && (
                    <Tooltip
                      title={intl
                        .get('hpfm.permission.view.message.tip.disable')
                        .d('自动生成，禁止操作')}
                    >
                      <Icon type="question-circle" style={{ color: 'rgb(218, 213, 213)' }} />
                    </Tooltip>
                  )}
                </>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (VERSION_IS_OP || !isTenantRoleLevel()) {
            operators.push(
              record.editableFlag && {
                key: 'delete',
                ele: (
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    onConfirm={this.deleteRecord.bind(this, record)}
                  >
                    <ButtonPermission
                      className="delete"
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.ruleDelete`,
                          type: 'button',
                          meaning: '数据权限规则-删除',
                        },
                      ]}
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              }
            );
          }
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    const tableProps = {
      dataSource,
      columns,
      scroll: { x: tableScrollWidth(columns, 1) },
      rowKey: this.defaultTableRowKey,
      bordered: true,
      ...others,
    };
    return <Table {...tableProps} />;
  }
}
