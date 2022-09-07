/**
 * paymentOrderDrawer 弹出框
 * @author jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-06-14
 * @copyright 2018 © HAND
 */
import React from 'react';
import { Form, Input, Modal, Spin, Button, Row, Col } from 'hzero-ui';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT, FORM_COL_2_LAYOUT } from 'utils/constants';
import { TagRender } from 'utils/renderer';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class PaymentOrderDrawer extends React.PureComponent {
  render() {
    const { form, initData, title, modalVisible, onCancel, initLoading = false } = this.props;
    const {
      paymentOrderNum,
      merchantOrderNum,
      channelMeaning,
      paymentSubject,
      currencyCode,
      paymentAmount,
      status,
      channelTradeNo,
      paymentDatetime,
      paymentDescription,
      paymentCustomer,
      returnUrl,
      cancelReason,
      expireTime,
      channelRespCode,
      channelRespMsg,
    } = initData;
    const { getFieldDecorator } = form;
    const statusLists = [
      {
        status: 'PAID',
        color: 'green',
        text: intl.get('hpay.paymentOrder.model.paymentOrder.paid').d('已支付'),
      },
      {
        status: 'UNPAID',
        color: 'red',
        text: intl.get('hpay.paymentOrder.model.paymentOrder.unpaid').d('待支付'),
      },
      {
        status: 'CANCELLED',
        text: intl.get('hpay.paymentOrder.model.paymentOrder.canceled').d('取消'),
      },
    ];
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        width="1000px"
        title={title}
        visible={modalVisible}
        onCancel={onCancel}
        footer={
          <Button type="primary" onClick={onCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
      >
        <Spin spinning={initLoading}>
          <Form>
            <Row>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.paymentOrderNum')
                    .d('支付订单号')}
                >
                  {getFieldDecorator('paymentOrderNum', {
                    initialValue: paymentOrderNum,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.merchantOrderNum')
                    .d('商户订单号')}
                >
                  {getFieldDecorator('merchantOrderNum', {
                    initialValue: merchantOrderNum,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.paymentOrder.model.paymentOrder.channelCode').d('支付渠道')}
                >
                  {getFieldDecorator('channelCode', {
                    initialValue: channelMeaning,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.paymentSubject')
                    .d('订单标题')}
                >
                  {getFieldDecorator('paymentSubject', {
                    initialValue: paymentSubject,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.paymentOrder.model.paymentOrder.currencyCode').d('币种')}
                >
                  {getFieldDecorator('currencyCode', {
                    initialValue: currencyCode,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.paymentOrder.model.paymentOrder.paymentAmount').d('金额')}
                >
                  {getFieldDecorator('paymentAmount', {
                    initialValue: paymentAmount,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.paymentOrder.model.paymentOrder.status').d('状态')}
                >
                  {TagRender(status, statusLists)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.channelTradeNo')
                    .d('支付流水号')}
                >
                  {getFieldDecorator('channelTradeNo', {
                    initialValue: channelTradeNo,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.paymentDatetime')
                    .d('支付成功时间')}
                >
                  {getFieldDecorator('paymentDatetime', {
                    initialValue: paymentDatetime,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.paymentDescription')
                    .d('支付订单描述')}
                >
                  {getFieldDecorator('paymentDescription', {
                    initialValue: paymentDescription,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.paymentCustomer')
                    .d('支付用户')}
                >
                  {getFieldDecorator('paymentCustomer', {
                    initialValue: paymentCustomer,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.returnUrl')
                    .d('页面回调地址')}
                >
                  {getFieldDecorator('returnUrl', {
                    initialValue: returnUrl,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.cancelReason')
                    .d('订单撤销原因')}
                >
                  {getFieldDecorator('cancelReason', {
                    initialValue: cancelReason,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.paymentOrder.model.paymentOrder.expireTime').d('到期时间')}
                >
                  {getFieldDecorator('expireTime', {
                    initialValue: expireTime,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.channelRespCode')
                    .d('支付消息码')}
                >
                  {getFieldDecorator('channelRespCode', {
                    initialValue: channelRespCode,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.paymentOrder.model.paymentOrder.channelRespMsg')
                    .d('支付消息')}
                >
                  {getFieldDecorator('channelRespMsg', {
                    initialValue: channelRespMsg,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
