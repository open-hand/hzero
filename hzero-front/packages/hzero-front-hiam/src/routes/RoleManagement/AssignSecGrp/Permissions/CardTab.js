/**
 * CardTab - 安全组工作台配置
 * @date: 2019-11-28
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Table } from 'hzero-ui';

import { yesOrNoRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

export default class CardTab extends Component {
  get columns() {
    return [
      {
        title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardCode').d('卡片代码'),
        dataIndex: 'code',
      },
      {
        title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardName').d('卡片名称'),
        dataIndex: 'name',
      },
      {
        title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardType').d('卡片类别'),
        dataIndex: 'catalogMeaning',
      },
      {
        title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardH').d('高度'),
        dataIndex: 'h',
      },
      {
        title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardW').d('长度'),
        dataIndex: 'w',
      },
      {
        title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardX').d('位置X'),
        dataIndex: 'x',
      },
      {
        title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardY').d('位置Y'),
        dataIndex: 'y',
      },
      {
        title: intl
          .get('hiam.securityGroup.model.securityGroup.defaultDisplayFlag')
          .d('是否初始化'),
        dataIndex: 'defaultDisplayFlag',
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.explain').d('说明'),
        dataIndex: 'remark',
      },
    ];
  }

  render() {
    const { dataSource, loading, pagination } = this.props;
    const tableProps = {
      rowKey: 'id',
      columns: this.columns,
      bordered: true,
      dataSource,
      loading,
      pagination,
      scroll: { x: tableScrollWidth(this.columns, 700) },
    };
    return <Table {...tableProps} />;
  }
}
