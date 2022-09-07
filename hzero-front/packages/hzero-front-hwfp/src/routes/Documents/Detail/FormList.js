/*
 * FormList.js - 详情页表单列表
 * @date: 2019-04-29
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';

import styles from './index.less';
/**
 * 流程单据详情页表单列表
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
export default class FormList extends Component {
  /**
   * 编辑
   * @param {object} record - 数据对象
   */
  @Bind()
  editOption(record) {
    this.props.onEdit(record);
  }

  /**
   * 删除
   * @param {object} record - 数据对象
   */
  @Bind()
  deleteOption(record) {
    this.props.onDelete(record);
  }

  render() {
    const { loading, predefined, dataSource = [] } = this.props;
    const columns = [
      {
        title: intl.get('hwfp.common.model.common.formDescription').d('表单描述'),
        dataIndex: 'formCode',
        width: 200,
        render: (val, record) => (
          <>
            <div className={styles['form-desc']}>{record.description}</div>
            <div className={styles['form-code']}>{val}</div>
          </>
        ),
      },
      {
        title: intl.get('hwfp.common.model.common.pcFormUrl').d('PC端表单URL'),
        dataIndex: 'formUrl',
      },
      {
        title: intl.get('hwfp.common.model.common.mobileFormUrl').d('移动端表单URL'),
        dataIndex: 'mobileFormUrl',
        width: 250,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        fixed: 'right',
        width: 120,
        render: (val, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.editOption(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  placement="topRight"
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => this.deleteOption(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          if (!predefined) {
            return operatorRender(operators, record);
          }
        },
      },
    ];
    return (
      <>
        <Table
          bordered
          scroll={{ x: tableScrollWidth(columns) }}
          rowKey={(val, index) => index + 1}
          loading={loading}
          columns={columns}
          dataSource={dataSource}
          pagination={false}
        />
      </>
    );
  }
}
