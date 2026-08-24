/**
 * AssignedForm - 分配值表格
 * @date: 2019/7/15
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import intl from 'utils/intl';

export default class AssignedTable extends PureComponent {
  render() {
    const { onChange, dataSource, pagination, loading, rowSelection } = this.props;
    const columns = [{
      title: intl.get('hpfm.dataGroup.model.dataGroup.dimensionValue').d('维度值'),
      dataIndex: 'dimensionCode',
    }, {
      title: intl.get('hpfm.dataGroup.model.dataGroup.dimensionMeaning').d('维度值含义'),
      dataIndex: 'dimensionName',
    }];
    return (
      <Table
        bordered
        loading={loading}
        rowKey="dtlValueId"
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        rowSelection={rowSelection}
        onChange={page => onChange(page)}
      />
    );
  }
}
