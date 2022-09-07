/**
 * PurchaseList - 订购列表
 * @date: 2020-2-19
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table } from 'choerodon-ui/pro';
import React, { Component } from 'react';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';

import UserPurchaseDS from '../../../stores/Purchase/UserPurchaseDS';

const organizationId = getCurrentOrganizationId();

/**
 * 订购列表
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.UserPurchase'] })
export default class PurchaseList extends Component {
  // 用户已购买数据源
  tableDS = new DataSet({
    ...UserPurchaseDS(),
    autoQuery: false,
    transport: {
      read: function read({ data, params }) {
        const url = isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/user-purchases/purchases`
          : `${HZERO_HITF}/v1/user-purchases/purchases`;
        return {
          data,
          params,
          url,
          method: 'GET',
        };
      },
    },
  });

  componentDidMount() {
    const { match } = this.props;
    // 页面控制
    if (match && match.params) {
      // 设置查询参数
      this.tableDS.queryParameter.sourceId = match.params.id;
      this.tableDS.queryParameter.sourceTypeCode = match.params.typeCode;
    } else {
      // 组件控制
      // 设置查询参数
      this.tableDS.queryParameter.sourceId = this.props.id;
      this.tableDS.queryParameter.sourceTypeCode = this.props.typeCode;
    }
    this.tableDS.query();
  }

  render() {
    const { match } = this.props;
    let basePath = '';
    // 页面控制 设置返回页面URL
    if (match && match.params) {
      basePath = match.path.substring(0, match.path.indexOf('/purchase-list'));
    }
    return (
      <>
        {match && match.params && (
          <Header
            title={intl.get('hitf.purchase.view.message.title.purchaseList').d('购买列表')}
            backPath={`${basePath}/list`}
          />
        )}
        <Content>
          <Table
            dataSet={this.tableDS}
            columns={[
              {
                header: intl.get('hitf.purchase.model.purchaseList.sort').d('序号'),
                lock: 'left',
                width: 70,
                align: 'center',
                renderer: ({ record }) =>
                  (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
              },
              {
                name: 'userName',
              },
              {
                name: 'tenantName',
              },
              {
                name: 'chargeRuleName',
                width: 240,
              },
              {
                name: 'creationDate',
                width: 180,
              },
            ]}
            queryBar="none"
          />
        </Content>
      </>
    );
  }
}
