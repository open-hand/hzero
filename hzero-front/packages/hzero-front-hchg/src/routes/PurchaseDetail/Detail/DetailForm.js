/**
 * DetailForm - 购买详单 - 详情表单
 * @date: 2019/9/1
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Row, Col } from 'hzero-ui';
import intl from 'utils/intl';
import { EDIT_FORM_ROW_LAYOUT, FORM_COL_3_LAYOUT, SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';
import { isTenantRoleLevel } from 'utils/utils';

const isTenant = isTenantRoleLevel();
const FormItem = Form.Item;

/**
 * 详情表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {!Object} dataSource - 数据源
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
export default class DetailForm extends PureComponent {
  render() {
    const { dataSource } = this.props;
    const {
      tradeNo,
      groupName,
      tenantName,
      chargeService,
      chargeServiceMeaning,
      serviceName,
      ruleName,
      orderTime,
      orderQuantity,
      paidTime,
      orderStatusMeaning,
    } = dataSource;
    return (
      <Form layout="inline" className="more-fields-search-form">
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hchg.purchaseDetail.model.purchaseDetail.tradeNo').d('交易流水')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {tradeNo}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hchg.serviceCharge.model.serviceCharge.groupName').d('计费组名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {groupName}
            </FormItem>
          </Col>
          {!isTenant && (
            <Col {...FORM_COL_3_LAYOUT}>
              <FormItem
                label={intl.get('hzero.common.model.common.tenantId').d('租户')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {tenantName}
              </FormItem>
            </Col>
          )}
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl
                .get('hchg.purchaseDetail.model.purchaseDetail.chargeService')
                .d('服务代码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {chargeService}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl
                .get('hchg.purchaseDetail.model.serviceCharge.chargeServiceMeaning')
                .d('服务说明')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {chargeServiceMeaning}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hchg.purchaseDetail.model.purchaseDetail.service').d('服务')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {serviceName}
            </FormItem>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl
                .get('hchg.purchaseDetail.model.purchaseDetail.ruleName')
                .d('计费规则说明')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {ruleName}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hchg.purchaseDetail.model.purchaseDetail.orderTime').d('订购时间')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {orderTime}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hchg.purchaseDetail.model.purchaseDetail.orderAmount').d('订购数量')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {orderQuantity}
            </FormItem>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hchg.purchaseDetail.model.purchaseDetail.paidTime').d('付款时间')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {paidTime}
            </FormItem>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hzero.common.status').d('状态')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {orderStatusMeaning}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
