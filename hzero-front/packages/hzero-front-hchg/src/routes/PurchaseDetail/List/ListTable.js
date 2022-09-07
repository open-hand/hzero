/**
 * ListTable - 购买详单-列表页
 * @date: 2019-8-22
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
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
      isTenant,
      onRedirect = () => {},
    } = this.props;
    const columns = [
      {
        title: intl.get('hchg.purchaseDetail.model.purchaseDetail.tradeNo').d('交易流水'),
        width: 150,
        dataIndex: 'tradeNo',
      },
      {
        title: intl.get('hchg.serviceCharge.model.serviceCharge.groupName').d('计费组名称'),
        dataIndex: 'groupName',
        width: 150,
      },
      !isTenant && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hchg.purchaseDetail.model.purchaseDetail.chargeService').d('服务代码'),
        width: 200,
        dataIndex: 'chargeService',
      },
      {
        title: intl
          .get('hchg.serviceCharge.model.serviceCharge.chargeServiceMeaning')
          .d('服务说明'),
        width: 150,
        dataIndex: 'chargeServiceMeaning',
      },
      {
        title: intl.get('hchg.purchaseDetail.model.purchaseDetail.service').d('服务'),
        width: 200,
        dataIndex: 'serviceName',
      },
      {
        title: intl.get('hchg.purchaseDetail.model.purchaseDetail.ruleName').d('计费规则说明'),
        width: 200,
        dataIndex: 'ruleName',
      },
      {
        title: intl.get('hchg.purchaseDetail.model.purchaseDetail.orderTime').d('订购时间'),
        width: 200,
        dataIndex: 'orderTime',
      },
      {
        title: intl.get('hchg.purchaseDetail.model.purchaseDetail.orderAmount').d('订购数量'),
        width: 200,
        dataIndex: 'orderQuantity',
      },
      {
        title: intl.get('hchg.purchaseDetail.model.purchaseDetail.paidTime').d('付款时间'),
        width: 200,
        dataIndex: 'paidTime',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'orderStatusMeaning',
        width: 120,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 130,
        fixed: 'right',
        render: (_, record) => (
          <span className="action-link">
            <a onClick={() => onRedirect(record.chargeOrderId)}>
              {intl.get('hchg.purchaseDetail.view.button.detail').d('查看详情')}
            </a>
          </span>
        ),
      },
    ].filter(Boolean);
    return (
      <Table
        bordered
        rowKey="chargeOrderId"
        loading={loading}
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        scroll={{ x: tableScrollWidth(columns) }}
        onChange={page => onChange(page)}
      />
    );
  }
}
