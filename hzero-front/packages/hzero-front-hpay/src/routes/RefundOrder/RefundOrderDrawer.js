/**
 * RefundOrderDrawer 弹出框
 * @author jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-06-17
 * @copyright 2018 © HAND
 */

import React from 'react';
import { Form, Input, Modal, Spin, Button, Row, Col, Select } from 'hzero-ui';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT, FORM_COL_2_LAYOUT } from 'utils/constants';
import { TagRender } from 'utils/renderer';

const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class RefundOrderDrawer extends React.PureComponent {
  render() {
    const {
      form,
      initData,
      title,
      modalVisible,
      onCancel,
      initLoading = false,
      wayList,
    } = this.props;
    const {
      currencyCode,
      totalAmount,
      refundWay,
      channelRespCode,
      channelRespMsg,
      refundOrderNum,
      paymentOrderNum,
      merchantOrderNum,
      channelTradeNo,
      channelMeaning,
      refundAmount,
      refundReason,
      status,
      refundDatetime,
    } = initData;
    const { getFieldDecorator } = form;
    const statusLists = [
      { status: 'UNREF', text: intl.get('hpay.refundOrder.model.refundOrder.unref').d('待退款') },
      {
        status: 'REFUNDING',
        text: intl.get('hpay.refundOrder.model.refundOrder.refunding').d('退款中'),
      },
      {
        status: 'REFUNDED',
        color: 'green',
        text: intl.get('hpay.refundOrder.model.refundOrder.refunded').d('已退款'),
      },
      {
        status: 'FAILED',
        color: 'red',
        text: intl.get('hpay.refundOrder.model.refundOrder.failed').d('退款失败'),
      },
      {
        status: 'REFUSED',
        color: 'red',
        text: intl.get('hpay.refundOrder.model.refundOrder.refused').d('退款拒接'),
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
                    .get('hpay.refundOrder.model.refundOrder.refundOrderNum')
                    .d('退款订单号')}
                >
                  {getFieldDecorator('refundOrderNum', {
                    initialValue: refundOrderNum,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.refundOrder.model.refundOrder.paymentOrderNum')
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
                    .get('hpay.refundOrder.model.refundOrder.merchantOrderNum')
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
                  label={intl
                    .get('hpay.refundOrder.model.refundOrder.channelTradeNo')
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
                  label={intl.get('hpay.refundOrder.model.refundOrder.channelCode').d('支付渠道')}
                >
                  {getFieldDecorator('channelCode', {
                    initialValue: channelMeaning,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.refundOrder.model.refundOrder.refundAmount').d('退款金额')}
                >
                  {getFieldDecorator('refundAmount', {
                    initialValue: refundAmount,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.refundOrder.model.refundOrder.refundReason').d('退款原因')}
                >
                  {getFieldDecorator('refundReason', {
                    initialValue: refundReason,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.refundOrder.model.refundOrder.status').d('退款状态')}
                >
                  {TagRender(status, statusLists)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.refundOrder.model.refundOrder.refundDatetime')
                    .d('退款成功时间')}
                >
                  {getFieldDecorator('refundDatetime', {
                    initialValue: refundDatetime,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.refundOrder.model.refundOrder.currencyCode').d('币种')}
                >
                  {getFieldDecorator('currencyCode', {
                    initialValue: currencyCode,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.refundOrder.model.refundOrder.totalAmount').d('订单总金额')}
                >
                  {getFieldDecorator('totalAmount', {
                    initialValue: totalAmount,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl.get('hpay.refundOrder.model.refundOrder.refundWay').d('退款方式')}
                >
                  {getFieldDecorator('refundWay', {
                    initialValue: refundWay,
                  })(
                    <Select disabled>
                      {wayList.map(item => (
                        <Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...MODAL_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpay.refundOrder.model.refundOrder.channelRespCode')
                    .d('退款消息码')}
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
                    .get('hpay.refundOrder.model.refundOrder.channelRespMsg')
                    .d('退款消息')}
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
