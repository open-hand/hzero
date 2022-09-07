/**
 * InvoiceDetail 发票详情
 * @date: 2019-8-30
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { Card, Col, Divider, Form, Row, Table } from 'hzero-ui';

import intl from 'utils/intl';
import { numberRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_2,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import { BKT_INVOICE } from 'utils/config';

// import styles from './index.less';

const FORM_ITEM_LAYOUT_1 = {
  labelCol: {
    span: 2,
  },
  wrapperCol: {
    span: 22,
  },
};

/**
 *
 *
 * @export
 * @class InvoiceDetail
 * @extends {PureComponent}
 */
export default class InvoiceDetail extends PureComponent {
  state = { hidden: false, picUrl: '' };

  componentDidMount() {
    const {
      dataSource: {
        checkHistResult: { picUrl },
      },
      dispatch,
    } = this.props;
    dispatch({
      type: 'manualInspection/redirect',
      payload: {
        bucketName: BKT_INVOICE,
        directory: 'hivc01',
        url: picUrl,
      },
    }).then((res) => {
      this.setState({ picUrl: res });
    });
  }

  @Bind()
  handleToggle() {
    const { hidden } = this.state;
    this.setState({
      hidden: !hidden,
    });
  }

  @Bind()
  dateRender(dateStr) {
    const date = new Date(dateStr.billingTime * 1000);
    const Y = `${date.getFullYear()}/`;
    const M = `${date.getMonth() + 1 < 10 ? `0${date.getMonth() + 1}` : date.getMonth() + 1}/`;
    const D = `${date.getDate()} `;
    return Y + M + D;
  }

  render() {
    const { dataSource = {}, onReview } = this.props;
    const { picUrl = '' } = this.state;
    const {
      checkHistResult: {
        // type = '', // 发票类型
        // invoiceTypeNo = '', // 发票类型代码
        invoiceNo = '', // 发票号码
        invoiceCode = '', // 发票代码
        billingTime = '', // 开票日期
        // invoiceAmount = '', // 不含税金额
        checkCode = '', // 校验码
        machineNo = '', // 机器编码
        // fee = '', // 价税合计
        // feeWithoutTax = '', // 金额合计（不含税）
        // tax = '', // 税额合计
        totalAmount = '',
        remark = '', // 备注
        buyerName = '', // 购买方
        title = '',
        buyerNo = '', // 纳税人识别号
        buyerAddressPhone = '', // 购方地址/电话
        buyerAccount = '', // 购方开户行及账号
        salerName = '', // 销售方
        salerNo = '', // 纳税人识别号
        salerAddressPhone = '', // 销方地址/电话
        salerAccount = '', // 销方开户行及账号
      } = {},
    } = dataSource;
    const { hidden } = this.state;
    const columns = [
      {
        title: intl
          .get('hivc.manualInspection.model.manualInspection.goodsName')
          .d('商品/服务名称'),
        dataIndex: 'goodsName',
        width: 200,
      },
      {
        title: intl.get('hivc.manualInspection.model.manualInspection.model').d('规格型号'),
        dataIndex: 'specificationModel',
      },
      {
        title: intl.get('hivc.manualInspection.model.manualInspection.unit').d('单位'),
        dataIndex: 'unit',
        width: 100,
      },
      {
        title: intl.get('hivc.manualInspection.model.manualInspection.count').d('数量'),
        dataIndex: 'quantity',
        align: 'right',
        width: 100,
      },
      {
        title: intl.get('hivc.manualInspection.model.manualInspection.unitPrice').d('单价'),
        dataIndex: 'unitPrice',
        align: 'right',
        width: 100,
      },
      {
        title: intl.get('hivc.manualInspection.model.manualInspection.amount').d('金额'),
        dataIndex: 'amount',
        align: 'right',
        width: 100,
      },
      {
        title: intl.get('hivc.manualInspection.model.manualInspection.taxRate').d('税率(%)'),
        dataIndex: 'taxRate',
        align: 'right',
        width: 100,
        render: (value) => numberRender(value, 2, false),
      },
      {
        title: intl.get('hivc.manualInspection.model.manualInspection.taxPrice').d('税额'),
        dataIndex: 'taxAmount',
        align: 'right',
        width: 100,
      },
    ];
    return (
      <Card
        key="invoice-line"
        bordered={false}
        className={DETAIL_CARD_CLASSNAME}
        title={<h3>{title || intl.get('hivc.manualInspection.view.title.line').d('发票详情')}</h3>}
        extra={
          <div>
            <span onClick={this.handleToggle} style={{ cursor: 'pointer' }}>
              {hidden
                ? `∨${intl.get('hzero.common.button.expand').d('展开')}`
                : `∧${intl.get(`hzero.common.button.up`).d('收起')}`}
            </span>
          </div>
        }
      >
        <div style={hidden ? { display: 'none' } : { display: 'block' }}>
          {picUrl && (
            // <p onClick={() => onReview(picUrl)} style={{ cursor: 'pointer', color: 'blue' }}>
            //   查看附件
            // </p>
            <div
              style={{
                maxHeight: '80px',
                width: 128,
                position: 'absolute',
                right: '19%',
                top: '26%',
                zIndex: 999,
                overflow: 'hidden',
              }}
            >
              <img
                src={picUrl}
                alt=""
                onClick={() => onReview(picUrl)}
                style={{
                  cursor: 'pointer',
                  width: 128,
                  // maxHeight: '100px',
                }}
              />
            </div>
          )}
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.billingDate')
                  .d('开票日期')}
                {...EDIT_FORM_ITEM_LAYOUT_COL_2}
              >
                <span>{`${this.dateRender({ billingTime })}`}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.invoiceCode')
                  .d('发票代码')}
                {...EDIT_FORM_ITEM_LAYOUT_COL_2}
              >
                <span>{invoiceCode}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.invoiceNo')
                  .d('发票号码')}
                {...EDIT_FORM_ITEM_LAYOUT_COL_2}
              >
                <span>{invoiceNo}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.deviceNumber')
                  .d('机器编号')}
                {...EDIT_FORM_ITEM_LAYOUT_COL_2}
              >
                <span>{machineNo}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.checkCode')
                  .d('校验码')}
                {...EDIT_FORM_ITEM_LAYOUT_COL_2}
              >
                <span>{checkCode}</span>
              </Form.Item>
            </Col>
            <Col span={22}>
              <Form.Item
                label={intl.get('hivc.manualInspection.model.manualInspection.fee').d('价税合计')}
                {...FORM_ITEM_LAYOUT_1}
              >
                {totalAmount && <span style={{ color: 'blue' }}>CNY</span>}
                {totalAmount}
                {/* {fee && (
                  <span>
                    {intl
                      .get('hivc.manualInspection.view.sug', { fee, feeWithoutTax, tax })
                      .d(`${fee} （金额合计（不含税）： ${feeWithoutTax} 税额合计： ${tax}）`)}
                  </span>
                )} */}
              </Form.Item>
            </Col>
            <Col span={24}>
              <Form.Item
                label={intl.get('hivc.manualInspection.model.manualInspection.remark').d('备注')}
                {...FORM_ITEM_LAYOUT_1}
              >
                <span>{remark}</span>
              </Form.Item>
            </Col>
          </Row>
          <Table
            bordered
            rowKey="id"
            columns={columns}
            pagination={false}
            dataSource={dataSource.checkHistResultLines}
            scroll={{ x: tableScrollWidth(columns) }}
          />
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_2_LAYOUT}>
              <Form.Item
                label={intl.get('hivc.manualInspection.model.manualInspection.title').d('购买方')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                <span>{buyerName}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.TaxpayerNumber')
                  .d('纳税人识别号')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                <span>{buyerNo}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.draweeAddPhone')
                  .d('购方地址/电话')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                <span>{buyerAddressPhone}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.BankAccount')
                  .d('开户/账号')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                <span>{buyerAccount}</span>
              </Form.Item>
            </Col>
            <Divider />
            <Col {...FORM_COL_2_LAYOUT}>
              <Form.Item
                label={intl.get('hivc.manualInspection.model.manualInspection.payee').d('销售方')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                <span>{salerName}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.TaxpayerNumber')
                  .d('纳税人识别号')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                <span>{salerNo}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.payeeAddPhone')
                  .d('销方地址/电话')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                <span>{salerAddressPhone}</span>
              </Form.Item>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hivc.manualInspection.model.manualInspection.BankAccount')
                  .d('开户/账号')}
                {...EDIT_FORM_ITEM_LAYOUT}
              >
                <span>{salerAccount}</span>
              </Form.Item>
            </Col>
          </Row>
        </div>
      </Card>
    );
  }
}
