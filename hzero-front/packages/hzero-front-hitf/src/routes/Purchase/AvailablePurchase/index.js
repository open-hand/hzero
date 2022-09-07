/**
 * AvailablePurchase - 用户可购买列表
 * @date: 2020-2-20
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table, Modal } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import notification from 'utils/notification';
import React, { Component } from 'react';

import { Header, Content } from 'components/Page';
import { TagRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_HITF } from 'hzero-front/lib/utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'hzero-front/lib/utils/utils';
import { routerRedux } from 'dva/router';
import axios from 'axios';
import {
  PAYMENT_MODEL_FIELDS,
  PURCHASE_TYPE_TAGS,
  PURCHASE_TYPE_FIELDS,
  PAYMENT_MODEL_TAGS,
  CHARGE_METHOD_TAGS,
} from '@/constants/CodeConstants';

import AvailablePurchaseDS from '../../../stores/Purchase/AvailablePurchaseDS';

import Server from '../../ChargeGroup/Server';
import ChargeRule from '../../ChargeRule/Detail';
import PurchaseList from '../PurchaseList';

const organizationId = getCurrentOrganizationId();

/**
 * 用户可购买列表
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.UserPurchase'] })
export default class AvailablePurchase extends Component {
  // 可购买列表数据源
  tableDS = new DataSet({ ...AvailablePurchaseDS() });

  // 购买列表模态框
  purchaseListModal = null;

  /**
   * 组合计费明细模态框
   * @param record
   */
  @Bind()
  openServerDetailModal(record) {
    // 组件参数
    const serverDetailProps = {
      groupHeaderId: record.get('headerId'),
    };
    Modal.open({
      drawer: false,
      key: 'serverDetail-availablePurchase',
      style: { width: '60%' },
      destroyOnClose: true,
      closable: true,
      okCancel: false,
      title: intl.get('hitf.purchase.view.message.title.serverAvailable').d('组合列表'),
      children: <Server {...serverDetailProps} />,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  /**
   * 计费规则明细模态框
   * @param record
   */
  @Bind()
  openChargeRuleDetailModal(record) {
    // 组件参数
    const ruleDetailProps = {
      chargeRuleId: record.get('chargeRuleId'),
    };
    Modal.open({
      drawer: false,
      key: 'ruleDetail-availablePurchase',
      style: { width: '60%' },
      destroyOnClose: true,
      closable: true,
      okCancel: false,
      title: intl.get('hitf.chargeGroup.model.chargeGroupHeader.chargeGroupRule').d('计费规则'),
      children: <ChargeRule {...ruleDetailProps} />,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  /**
   * 购买详情模态框
   * @param record
   */
  @Bind()
  openPurchaseListModal(record) {
    // 组件参数
    const purchaseListProps = {
      data: record.data,
      closeModal: this.closePurchaseListModal,
    };
    this.purchaseListModal = Modal.open({
      drawer: false,
      key: 'purchaseList-availablePurchase',
      style: { width: '60%' },
      destroyOnClose: true,
      closable: true,
      okCancel: false,
      title: intl.get('hitf.purchase.view.message.title.purchaseList').d('购买列表'),
      children: <PurchaseList {...purchaseListProps} />,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  /**
   * 关闭购买详情模态框
   */
  @Bind()
  closePurchaseListModal(data, billNum) {
    this.purchaseListModal.close();
    this.tableDS.query();
    // 如果是预付费，需要打开账单详情界面 支付
    if (PAYMENT_MODEL_FIELDS.BEFORE === data.paymentModel) {
      this.openBillDetail(billNum);
    }
  }

  /**
   * 打开账单详情页面
   */
  @Bind()
  openBillDetail(billNum) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hchg/bill/line/${billNum}`,
      })
    );
  }

  /**
   * 购买
   * @param record
   */
  @Bind()
  purchase(record) {
    Modal.confirm({
      title: intl.get('hitf.purchase.view.meaasge.confirm.purchase').d('确定购买？'),
      onOk: async () => {
        const url = isTenantRoleLevel()
          ? `${HZERO_HITF}/v1/${organizationId}/user-purchases`
          : `${HZERO_HITF}/v1/user-purchases`;
        try {
          const res = await axios.post(url, record.data);
          if (res && res.failed) {
            // intl.get('hitf.purchase.view.meaasge.purchase.failed').d('购买失败')
            notification.error({
              message: res.message,
            });
          } else {
            notification.success({
              message: intl.get('hitf.purchase.view.meaasge.purchase.success').d('购买成功'),
            });
            this.tableDS.query();
            // 如果是预付费，需要打开账单详情界面 支付
            if (PAYMENT_MODEL_FIELDS.BEFORE === record.get('paymentModel')) {
              this.openBillDetail(res.billNum);
            }
          }
        } catch (err) {
          notification.error({
            message: intl
              .get('hitf.purchase.view.meaasge.purchase.wait')
              .d('购买失败，请稍后再试。'),
          });
        }
      },
    });
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <div>
        <Header
          title={intl.get('hitf.purchase.view.message.title.availablePurchase').d('可购买列表')}
        />
        <Content>
          <Table
            dataSet={this.tableDS}
            columns={[
              {
                header: intl.get('hitf.purchase.model.availablePurchase.sort').d('序号'),
                lock: 'left',
                width: 70,
                align: 'center',
                renderer: ({ record }) =>
                  (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
              },
              {
                name: 'typeCode',
                align: 'center',
                renderer: ({ value }) => TagRender(value, PURCHASE_TYPE_TAGS),
              },
              {
                name: 'chargeName',
                align: 'center',
                renderer: ({ value, record }) =>
                  record.get('typeCode') === PURCHASE_TYPE_FIELDS.GROUP ? (
                    <a onClick={() => this.openServerDetailModal(record)}>{value}</a>
                  ) : (
                    value
                  ),
              },
              {
                name: 'serverName',
              },
              {
                name: 'interfaceName',
              },
              {
                name: 'chargeRuleName',
                align: 'center',
                renderer: ({ value, record }) => (
                  <a onClick={() => this.openChargeRuleDetailModal(record)}>{value}</a>
                ),
              },
              {
                name: 'paymentModel',
                align: 'center',
                renderer: ({ value }) => TagRender(value, PAYMENT_MODEL_TAGS),
              },
              {
                name: 'chargeMethodCode',
                align: 'center',
                renderer: ({ value }) => TagRender(value, CHARGE_METHOD_TAGS),
              },
              {
                header: intl.get('hzero.common.button.action').d('操作'),
                lock: 'right',
                align: 'center',
                width: 200,
                renderer: ({ record }) => {
                  const actions = [
                    {
                      ele: (
                        <ButtonPermission
                          type="text"
                          permissionList={[
                            {
                              code: `${path}.button.purchased`,
                              type: 'button',
                              meaning: '可购买列表-已购买',
                            },
                          ]}
                        >
                          {intl.get('hitf.purchase.view.meaasge.purchased').d('已购买')}
                        </ButtonPermission>
                      ),
                      key: 'purchased',
                      len: 3,
                      title: intl.get('hitf.purchase.view.meaasge.purchased').d('已购买'),
                    },
                    {
                      ele: (
                        <ButtonPermission
                          type="text"
                          permissionList={[
                            {
                              code: `${path}.button.purchaseList`,
                              type: 'button',
                              meaning: '可购买列表-购买列表',
                            },
                          ]}
                          onClick={() => this.openPurchaseListModal(record)}
                        >
                          {intl
                            .get('hitf.purchase.view.message.operation.purchaseList')
                            .d('购买列表')}
                        </ButtonPermission>
                      ),
                      key: 'purchaseList',
                      len: 4,
                      title: intl
                        .get('hitf.purchase.view.message.operation.purchaseList')
                        .d('购买列表'),
                    },
                    {
                      ele: (
                        <ButtonPermission
                          type="text"
                          permissionList={[
                            {
                              code: `${path}.button.purchase`,
                              type: 'button',
                              meaning: '可购买列表-购买',
                            },
                          ]}
                          onClick={() => this.purchase(record)}
                        >
                          {intl.get('hitf.purchase.view.message.operation.purchase').d('购买')}
                        </ButtonPermission>
                      ),
                      key: 'purchase',
                      len: 2,
                      title: intl.get('hitf.purchase.view.message.operation.purchase').d('购买'),
                    },
                  ];
                  // userPurchaseId有值显示已购买，userPurchaseId没有值且付款模式=预付款显示购买列表按钮，否则显示购买按钮
                  const tempActions = actions.filter((item) =>
                    // eslint-disable-next-line no-nested-ternary
                    record.get('userPurchaseId')
                      ? ['purchased'].includes(item.key)
                      : record.get('paymentModel') === PAYMENT_MODEL_FIELDS.BEFORE
                      ? ['purchaseList'].includes(item.key)
                      : ['purchase'].includes(item.key)
                  );
                  return operatorRender(tempActions);
                },
              },
            ]}
            queryBar="none"
          />
        </Content>
      </div>
    );
  }
}
