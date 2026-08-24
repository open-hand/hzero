/**
 * VersionList - 数据审计模态框-版本列表
 * @date: 2019/5/9
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import intl from 'utils/intl';

/**
 * 数据审计模态框-版本列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Array} dataSource - 版本列表数据
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Function} onChange - 切换版本列表分页
 * @reactProps {Function} onFetch - 查询详情列表
 * @return React.element
 */
export default class VersionList extends PureComponent {
  render() {
    const { dataSource, loading, onFetch, pagination, onChange } = this.props;
    const columns = [
      {
        title: intl.get('hzero.common.components.dataAudit.version').d('版本'),
        dataIndex: 'entityVersion',
        width: 200,
        render: (text, { auditDataId }) => <a onClick={() => onFetch(auditDataId)}>{text}</a>,
      },
      {
        title: intl.get('hzero.common.components.dataAudit.operationType').d('操作类型'),
        dataIndex: 'auditTypeMeaning',
        width: 200,
      },
      {
        title: intl.get('hzero.common.explain').d('说明'),
        dataIndex: 'remark',
        width: 100,
      },
    ];
    return (
      <Table
        rowKey="auditDataId"
        bordered
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        loading={loading}
        onChange={page => onChange(page)}
      />
    );
  }
}
