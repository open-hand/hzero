/**
 * ListTable - 数据变更审计配置-列表页
 * @date: 2019-7-10
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Icon, Table } from 'hzero-ui';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import { isTenantRoleLevel } from 'utils/utils';
import { operatorRender } from 'utils/renderer';

const isTenant = isTenantRoleLevel();

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Function} onEdit - 跳转详情页
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} configList - Table数据源
 * @reactProps {Array} configPage - Table分页
 * @return React.element
 */

export default class ListTable extends Component {
  state = {
    currentEntityTableId: null,
  };

  /**
   * 编辑
   * @param {number} auditDataConfigId - 实体ID
   */
  @Bind()
  handleViewDetail(auditDataConfigId) {
    this.props.onAudit(auditDataConfigId);
  }

  /**
   * 启停用
   * @param {object} record - 表格行数组
   */
  @Bind()
  handleEnable(record) {
    this.setState(
      {
        currentEntityTableId: record.entityTableId,
      },
      () => {
        this.props.onEnable(record);
      }
    );
  }

  render() {
    const { onChange, configList, configPage, loading, onEdit, isHandling } = this.props;
    const { currentEntityTableId } = this.state;
    const columns = [
      !isTenant && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        dataIndex: 'tenantName',
        width: 120,
      },
      {
        title: intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.serviceName').d('服务名'),
        dataIndex: 'serviceName',
        width: 200,
      },
      {
        title: intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.tableName').d('表名'),
        dataIndex: 'tableName',
        width: 200,
      },
      {
        title: intl.get('hmnt.dataAuditConfig.model.dataAuditConfig.displayName').d('展示名称'),
        dataIndex: 'displayName',
        width: 200,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        key: 'edit',
        render: (_, record) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele:
                // eslint-disable-next-line no-nested-ternary
                record._status === 'update' ? (
                  <a onClick={() => onEdit(record, false)}>
                    {intl.get('hzero.common.button.cancel').d('取消')}
                  </a>
                ) : record.auditFlag === 1 ? (
                  <a onClick={() => onEdit(record, true)}>
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </a>
                ) : (
                  <a style={{ pointerEvents: 'none', color: 'rgba(0, 0, 0, 0.25)' }}>
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </a>
                ),
              len: 2,
              title:
                // eslint-disable-next-line no-nested-ternary
                record._status === 'update'
                  ? intl.get('hzero.common.button.cancel').d('取消')
                  : record.auditFlag === 1
                  ? intl.get('hzero.common.button.edit').d('编辑')
                  : intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'enableOrDisable',
              ele:
                currentEntityTableId === record.entityTableId && (isHandling || loading) ? (
                  <a>
                    <Icon type="loading" />
                  </a>
                ) : (
                  <a onClick={() => this.handleEnable(record)}>
                    {record.auditFlag === 1
                      ? intl.get('hzero.common.button.disable').d('禁用')
                      : intl.get('hzero.common.button.enable').d('启用')}
                  </a>
                ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            }
          );
          if (record.auditFlag === 1) {
            operators.push({
              key: 'audit',
              ele: (
                <a onClick={() => this.handleViewDetail(record.auditDataConfigId)}>
                  {intl.get('hmnt.dataAuditConfig.view.message.title.audit').d('审计')}
                </a>
              ),
              len: 2,
              title: intl.get('hmnt.dataAuditConfig.view.message.title.audit').d('审计'),
            });
          }
          if (record.auditFlag === 0) {
            operators.push({
              key: 'audit2',
              ele: (
                <a style={{ pointerEvents: 'none', color: 'rgba(0, 0, 0, 0.25)' }}>
                  {intl.get('hmnt.dataAuditConfig.view.message.title.audit').d('审计')}
                </a>
              ),
              len: 2,
              title: intl.get('hmnt.dataAuditConfig.view.message.title.audit').d('审计'),
            });
          }
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter(Boolean);

    return (
      <Table
        bordered
        loading={loading}
        rowKey="entityTableId"
        columns={columns}
        dataSource={configList}
        pagination={configPage}
        onChange={(page) => onChange(page)}
      />
    );
  }
}
