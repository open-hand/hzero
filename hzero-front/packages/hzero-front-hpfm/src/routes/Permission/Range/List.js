/**
 * Platform - 菜单管理 - 平台tab页
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table, Popconfirm, Badge, Tag } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, getCurrentOrganizationId, tableScrollWidth } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';

export default class List extends PureComponent {
  state = {
    currentTenantId: getCurrentOrganizationId(),
  };

  defaultTableRowKey = 'rangeId';

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
        onCell: this.onCell.bind(this),
        render: (text, record) => record.tenantName,
      },
      {
        title: intl.get('hpfm.permission.model.permission.tableName').d('屏蔽表名'),
        dataIndex: 'tableName',
        width: 160,
        onCell: this.onCell.bind(this),
      },
      {
        title: intl.get('hpfm.permission.model.permission.sqlId').d('SQLID'),
        dataIndex: 'sqlId',
        width: 120,
      },
      {
        title: intl.get('hpfm.permission.model.permission.serviceName').d('服务名'),
        dataIndex: 'serviceName',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 80,
        render: enableRender,
      },
      {
        title: intl.get('hpfm.permission.model.permission.customRuleFlag').d('自定义规则标识'),
        dataIndex: 'customRuleFlag',
        width: 120,
        render: (text, record) => (
          <Badge
            status={record.customRuleFlag === 1 ? 'success' : 'error'}
            text={
              record.customRuleFlag === 1
                ? intl.get('hzero.common.status.yes').d('是')
                : intl.get('hzero.common.status.no').d('否')
            }
          />
        ),
      },
      {
        title: intl.get('hpfm.permission.model.permission.description').d('描述'),
        dataIndex: 'description',
      },
      isTenantRoleLevel() && {
        title: intl.get('hzero.common.source').d('来源'),
        width: 100,
        render: (_, record) =>
          currentTenantId === record.tenantId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        // fixed: 'right',
        width: 150,
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
                        code: `${match.path}.button.rangeEdit`,
                        type: 'button',
                        meaning: '数据权限范围-编辑',
                      },
                    ]}
                    onClick={() => openEditor(record)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                  {/* &nbsp;
                  {record.editableFlag === 0 && (
                    <Tooltip
                      title={intl
                        .get('hpfm.permission.view.message.tip.disable')
                        .d('自动生成，禁止操作')}
                    >
                      <Icon type="question-circle" style={{ color: 'rgb(218, 213, 213)' }} />
                    </Tooltip>
                  )} */}
                </>
              ),
              len: 3,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (!isTenantRoleLevel() || VERSION_IS_OP) {
            operators.push(
              record.editableFlag && {
                key: 'delete',
                ele: (
                  <Popconfirm
                    title={intl
                      .get('hpfm.permission.view.message.confirm.delete')
                      .d('确定删除该屏蔽范围吗?')}
                    onConfirm={this.deleteRecord.bind(this, record)}
                  >
                    <ButtonPermission
                      className="delete"
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.rangeDelete`,
                          type: 'button',
                          meaning: '数据权限范围-删除',
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
      scroll: { x: tableScrollWidth(columns) },
      pagination: false,
      rowKey: this.defaultTableRowKey,
      bordered: true,
      ...others,
    };
    return <Table {...tableProps} />;
  }
}
