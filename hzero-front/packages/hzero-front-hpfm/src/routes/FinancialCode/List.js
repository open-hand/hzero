import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import moment from 'moment';

import { Button as ButtonPermission } from 'components/Permission';

import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';

/**
 * List - 供应商绩效标准指标定义 - 列表组件
 * @extends {Component} - React.Component
 * @reactProps {boolean} [loading=false] - 表格处理状态
 * @reactProps {function} [onChange= (e => e)] - 表格onChange事件
 * @reactProps {object} [pagination={}] - 分页数据
 * @reactProps {Array<Object>} [dataSource=[]] - 表格数据源
 * @return React.element
 */
export default class List extends Component {
  render() {
    const { onChange, dataSource, onEdit, loading, match, typeList = [] } = this.props;
    const typeFilters = typeList.map(item => {
      return {
        value: item.value,
        text: item.meaning,
      };
    });
    const statusFilters = [
      {
        text: intl.get('hzero.common.status.enable').d('启用'),
        value: 1,
      },
      {
        text: intl.get('hzero.common.status.disable').d('禁用'),
        value: 0,
      },
    ];
    const columns = [
      {
        title: intl.get('hpfm.financialCode.model.financialCode.financialCode').d('代码'),
        dataIndex: 'code',
      },
      {
        title: intl.get('hpfm.financialCode.model.financialCode.financialName').d('名称'),
        dataIndex: 'name',
      },
      {
        title: intl.get('hpfm.financialCode.model.financialCode.financialType').d('类型'),
        dataIndex: 'typeMeaning',
        width: 90,
        filters: typeFilters,
        onFilter: (value, record) => record.type === value,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        filters: statusFilters,
        onFilter: (value, record) => Number(record.enabledFlag) === Number(value),
        render: enableRender,
      },
      {
        title: intl.get('hpfm.financialCode.model.financialCode.description').d('描述'),
        dataIndex: 'remark',
        sorter: (a, b) => moment(a.financialDes).isBefore(b.financialDes),
        sortDirections: ['descend', 'ascend'],
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        dataIndex: 'operate',
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${match.path}.button.edit`,
                    type: 'button',
                    meaning: '财务代码设置-编辑',
                  },
                ]}
                onClick={() => onEdit(record)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators);
        },
      },
    ];
    return (
      <Table
        bordered
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={false}
        onChange={page => onChange(page)}
        rowKey="codeId"
      />
    );
  }
}
