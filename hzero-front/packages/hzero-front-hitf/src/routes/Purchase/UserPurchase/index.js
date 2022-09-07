/**
 * UserPurchase - 用户购买列表
 * @date: 2020-2-20
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import React, { Component } from 'react';

import { Header, Content } from 'components/Page';
import { TagRender } from 'utils/renderer';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import formatterCollections from 'utils/intl/formatterCollections';

import {
  PAYMENT_MODEL_TAGS,
  AVAILABLE_STATUS_TAGS,
  PURCHASE_TYPE_TAGS,
  PURCHASE_TYPE_FIELDS,
  CHARGE_METHOD_TAGS,
  CHARGE_METHOD_FIELDS,
} from '@/constants/CodeConstants';
import UserPurchaseDS from '../../../stores/Purchase/UserPurchaseDS';

import ServerAvailable from '../ServerAvailable';
import ChargeRule from '../../ChargeRule/Detail';

/**
 * 用户购买列表
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.UserPurchase'] })
export default class List extends Component {
  // 已购买列表数据源
  tableDS = new DataSet({
    ...UserPurchaseDS(),
    autoQuery: true,
  });

  /**
   * 组合计费明细模态框
   * @param record
   */
  @Bind()
  serverDetailModal(record) {
    // 组件参数
    const serverDetailProps = {
      userPurchaseId: record.get('userPurchaseId'),
    };
    Modal.open({
      drawer: false,
      key: 'serverDetail-userPurchase',
      style: { width: '60%' },
      destroyOnClose: true,
      closable: true,
      okCancel: false,
      title: intl.get('hitf.purchase.view.message.title.serverAvailable').d('组合列表'),
      children: <ServerAvailable {...serverDetailProps} />,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  /**
   * 计费规则明细模态框
   * @param record
   */
  @Bind()
  chargeRuleDetailModal(record) {
    // 组件参数
    const ruleDetailProps = {
      chargeRuleId: record.get('chargeRuleId'),
    };
    Modal.open({
      drawer: false,
      key: 'ruleDetail-userPurchase',
      style: { width: '60%' },
      destroyOnClose: true,
      closable: true,
      okCancel: false,
      title: intl.get('hitf.chargeGroup.model.chargeGroupHeader.chargeGroupRule').d('计费规则'),
      children: <ChargeRule {...ruleDetailProps} />,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  render() {
    return (
      <div>
        <Header title={intl.get('hitf.purchase.view.message.title.userPurchase').d('已购买列表')} />
        <Content>
          <Table
            dataSet={this.tableDS}
            columns={[
              {
                header: intl.get('hitf.purchase.model.userPurchase.sort').d('序号'),
                lock: 'left',
                width: 70,
                align: 'center',
                renderer: ({ record }) =>
                  (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
              },
              {
                name: 'sourceTypeCode',
                align: 'center',
                renderer: ({ value }) => TagRender(value, PURCHASE_TYPE_TAGS),
              },
              {
                name: 'serverName',
                align: 'center',
                renderer: ({ value, record }) =>
                  record.get('sourceTypeCode') === PURCHASE_TYPE_FIELDS.GROUP ? (
                    <a onClick={() => this.serverDetailModal(record)}>{value}</a>
                  ) : (
                    value
                  ),
              },
              {
                name: 'interfaceName',
              },
              {
                name: 'paymentModel',
                align: 'center',
                renderer: ({ value }) => TagRender(value, PAYMENT_MODEL_TAGS),
              },
              {
                name: 'statusCode',
                align: 'center',
                renderer: ({ value }) => TagRender(value, AVAILABLE_STATUS_TAGS),
              },
              {
                name: 'chargeRuleName',
                align: 'center',
                renderer: ({ value, record }) => (
                  <a onClick={() => this.chargeRuleDetailModal(record)}>{value}</a>
                ),
              },
              {
                name: 'chargeMethodCode',
                align: 'center',
                renderer: ({ value }) => TagRender(value, CHARGE_METHOD_TAGS),
              },
              {
                name: 'totalCount',
              },
              {
                name: 'useCount',
              },
              {
                name: 'remainCount',
                renderer: ({ value, record }) =>
                  record.get('chargeMethodCode') === CHARGE_METHOD_FIELDS.PACKAGE ? value : '',
              },
            ]}
            queryBar="none"
          />
        </Content>
      </div>
    );
  }
}
