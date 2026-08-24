/**
 * ListTable - 租户初始化处理配置-列表页
 * @date: 2019-6-18
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import intl from 'utils/intl';

const modelPrompt = 'hiam.tenantConfig.model.tenantConfig';

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @return React.element
 */
export default class ListTable extends PureComponent {
  render() {
    const { dataSource, loading } = this.props;
    const columns = [
      {
        title: intl.get(`${modelPrompt}.serviceName`).d('服务名称'),
        dataIndex: 'serviceName',
        width: 150,
      },
      {
        title: intl.get(`${modelPrompt}.processorCode`).d('处理器代码'),
        dataIndex: 'processorCode',
        width: 150,
      },
      {
        title: intl.get(`${modelPrompt}.processorName`).d('处理器名称'),
        dataIndex: 'processorName',
        width: 150,
      },
      {
        title: intl.get(`${modelPrompt}.processorType`).d('处理器类型'),
        dataIndex: 'processorTypeMeaning',
        width: 150,
      },
      {
        title: intl.get(`${modelPrompt}.initType`).d('初始化类型'),
        dataIndex: 'initTypeMeaning',
        width: 120,
      },
      {
        title: intl.get(`${modelPrompt}.order`).d('排序'),
        dataIndex: 'orderSeq',
        width: 90,
      },
      {
        title: intl.get(`${modelPrompt}.remark`).d('说明'),
        dataIndex: 'remark',
        width: 150,
      },
    ];

    return (
      <Table
        bordered
        rowKey="tenantInitConfigId"
        loading={loading}
        columns={columns}
        dataSource={dataSource}
      />
    );
  }
}
