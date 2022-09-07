/**
 * ListTable - 应用类型定义-列表页
 * @date: 2019-8-22
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth, isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';

const isTenant = isTenantRoleLevel();

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Object} rowSelection - 行选择
 * @reactProps {Function} onEdit - 编辑
 * @reactProps {Function} onChange - 分页查询
 * @return React.element
 */
export default class ListTable extends Component {
  render() {
    const {
      loading,
      dataSource,
      pagination,
      onChange,
      rowSelection,
      onEdit = () => {},
    } = this.props;
    const columns = [
      !isTenant && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.applicationCode').d('应用代码'),
        width: 200,
        dataIndex: 'applicationCode',
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.name').d('应用名称'),
        dataIndex: 'applicationName',
        width: 200,
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.majorCategory').d('应用大类'),
        dataIndex: 'majorCategoryMeaning',
        width: 200,
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.minorCategory').d('应用小类'),
        width: 200,
        dataIndex: 'minorCategoryMeaning',
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.interfaceId').d('开放接口'),
        width: 200,
        dataIndex: 'interfaceName',
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.composePolicy').d('编排策略'),
        width: 200,
        dataIndex: 'composePolicyMeaning',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 120,
        render: enableRender,
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.remark').d('说明'),
        width: 200,
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        key: 'edit',
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => onEdit(record.applicationId)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);
    return (
      <>
        <Table
          bordered
          rowKey="applicationId"
          loading={loading}
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={page => onChange(page)}
          rowSelection={rowSelection}
        />
      </>
    );
  }
}
