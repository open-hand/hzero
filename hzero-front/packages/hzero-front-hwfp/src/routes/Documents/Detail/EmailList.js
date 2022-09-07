/*
 * EmailList.js - 详情页邮件列表
 * @date: 2019-09-14
 * @author: ZTC <tangchen.zhou@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { enableRender } from 'utils/renderer';

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
export default class EmailList extends Component {
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
        title: intl.get('hwfp.common.model.common.templateCode').d('模板编码'),
        dataIndex: 'templateCode',
        width: 200,
      },
      {
        title: intl.get('hwfp.common.model.common.templateName').d('模板名称'),
        dataIndex: 'templateName',
        width: 200,
      },
      {
        title: intl.get('hwfp.common.model.common.interfaceCode').d('数据来源'),
        dataIndex: 'interfaceCode',
        // width: 150,
      },
      // {
      //   title: intl.get('hwfp.common.model.common.receiptContent').d('反馈文本'),
      //   dataIndex: 'receiptContent',
      // },
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
        width: 150,
        render: (val, record) => (
          <span className="action-link">
            {!predefined && (
              <>
                <a onClick={() => this.editOption(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
                <Popconfirm
                  placement="topRight"
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => this.deleteOption(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              </>
            )}
          </span>
        ),
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
