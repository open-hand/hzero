/*
 * List.js - 流程分类列表
 * @date: 2019-04-30
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Table, Popconfirm, Tag } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';

const rowKey = 'categoryId';
/**
 * 流程分类数据展示列表
 * @extends {Component} - React.Component
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */

export default class List extends Component {
  // 编辑
  @Bind()
  handleEdit(record) {
    const { onDirectToDetail } = this.props;
    onDirectToDetail(record[rowKey]);
  }

  // 删除
  @Bind()
  deleteData(record) {
    const { onDelete } = this.props;
    onDelete(record);
  }

  render() {
    const { dataSource, currentTenantId, isSiteFlag, loading, pagination, onChange } = this.props;
    const columns = [
      isSiteFlag && {
        title: intl.get('hzero.common.model.tenantName').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hwfp.categories.model.categories.categoryCode').d('流程分类编码'),
        dataIndex: 'categoryCode',
        width: 260,
      },
      {
        title: intl.get('hwfp.categories.model.categories.description').d('流程分类描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        align: 'center',
        width: 100,
        render: enableRender,
      },
      !isSiteFlag && {
        title: intl.get('hzero.common.source').d('来源'),
        align: 'center',
        width: 100,
        render: (_, record) =>
          currentTenantId.toString() === record.tenantId.toString() ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 130,
        fixed: 'right',
        key: 'error',
        render: (val, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a
                  onClick={() => {
                    this.handleEdit(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 3,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (currentTenantId.toString() === record.tenantId.toString() || isSiteFlag) {
            operators.push({
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => {
                    this.deleteData(record);
                  }}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 3,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);
    return (
      <Table
        bordered
        rowKey={rowKey}
        loading={loading}
        pagination={pagination}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        onChange={(page) => onChange(page)}
      />
    );
  }
}
