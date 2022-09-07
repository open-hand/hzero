/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import { Button, Card, Col, Divider, Form, Input, Row, Spin, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
// import moment from 'moment';

// import EditTable from 'components/EditTable';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';

import 'react-viewer/dist/index.css';
// import { numberRender } from 'utils/renderer';

import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

const { TextArea } = Input;

@Form.create({ fieldNameProp: null })
export default class RenderVat extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: '',
      previewVisible: false,
      editDataSource: [],
    };
  }

  componentDidMount() {
    const { params } = this.props;
    const { itemList } = params;
    this.setState({
      editDataSource: itemList.map(item => ({
        ...item,
        _status: 'update',
      })),
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewImg() {
    const { onPreview, originUrl } = this.props;
    if (originUrl) {
      onPreview(originUrl).then(res => {
        if (res) {
          this.setState({
            previewImages: [
              {
                src: res,
                alt: '',
              },
            ],
            previewVisible: true,
          });
        }
      });
    } else {
      notification.error({
        message: intl.get('hocr.ocrRecord.view.message.title.failedImg').d('图片加载失败'),
      });
    }
  }

  /**
   * 取消预览
   */
  @Bind()
  handlePreviewCancel() {
    this.setState({
      previewImages: [],
      previewVisible: false,
    });
  }

  render() {
    const { params, form, redirectImageLoading } = this.props;
    const { previewVisible, previewImages, editDataSource } = this.state;
    const columns = [
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.productName').d('商品名称'),
        dataIndex: 'name',
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.specification').d('规格型号'),
        dataIndex: 'specification',
        width: 180,
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.unit').d('单位'),
        dataIndex: 'unit',
        width: 100,
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.quantity').d('数量'),
        dataIndex: 'quantity',
        width: 100,
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.unitPrice').d('单价'),
        dataIndex: 'unitPrice',
        width: 180,
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.amount').d('金额'),
        dataIndex: 'amount',
        width: 180,
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.taxRate').d('税率'),
        dataIndex: 'taxRate',
        width: 120,
      },
      {
        title: intl.get('hocr.commonOcr.model.commonOcr.tax').d('税额'),
        dataIndex: 'tax',
        width: 180,
      },
    ];
    return (
      <>
        <Viewer
          noImgDetails
          noNavbar
          scalable={false}
          changeable={false}
          visible={previewVisible}
          onClose={this.handlePreviewCancel}
          images={previewImages}
        />
        <Spin spinning={false}>
          <Card
            key="vat-detail"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hocr.commonOcr.view.title.vatDetail').d('增值税发票详情')}</h3>}
          >
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.ocrType').d('发票种类')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.type}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.code').d('发票代码')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.code}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.number').d('发票号码')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.number}
                </Form.Item>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.issueDate').d('开票日期')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.issueDate}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.checkCode').d('校验码')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.checkCode}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.attribution').d('发票归属地')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.attribution}
                </Form.Item>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.subTotalAmount').d('合计金额')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.subtotalAmount}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.subTotalTax').d('合计税额')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.subtotalTax}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.total').d('价税合计(小写)')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.total}
                </Form.Item>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl
                    .get('hocr.commonOcr.model.commonOcr.totalInWords')
                    .d('价税合计(大写)')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.totalInWords}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.receiver').d('收款人')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.receiver}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.reviewer').d('复核人')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.reviewer}
                </Form.Item>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.issuer').d('开票人')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.issuer}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.remarks').d('备注')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.remarks}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.encryptionBlock').d('密码区')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('encryptionBlock', {
                    initialValue: params && params.encryptionBlock,
                  })(<TextArea rows={3} readOnly />)}
                </Form.Item>
              </Col>
            </Row>
            <Divider />
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.buyerName').d('购方名称')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.buyerName}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.sellerName').d('销售方名称')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.sellerName}
                </Form.Item>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl
                    .get('hocr.commonOcr.model.commonOcr.buyerAddress')
                    .d('购方地址及电话')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.buyerAddress}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl
                    .get('hocr.commonOcr.model.commonOcr.sellerAddress')
                    .d('销售方地址及电话')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.sellerAddress}
                </Form.Item>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.buyerId').d('购方纳税人识别号')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.buyerId}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl
                    .get('hocr.commonOcr.model.commonOcr.sellerId')
                    .d('销售方纳税人识别号')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.sellerId}
                </Form.Item>
              </Col>
            </Row>
            <Row {...EDIT_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl.get('hocr.commonOcr.model.commonOcr.buyerBank').d('购方开户行及账号')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.buyerBank}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <Form.Item
                  label={intl
                    .get('hocr.commonOcr.model.commonOcr.sellerBank')
                    .d('销售方开户行及账号')}
                  {...EDIT_FORM_ITEM_LAYOUT}
                >
                  {params.sellerBank}
                </Form.Item>
              </Col>
            </Row>
          </Card>
          <Card
            key="vat-row"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hocr.commonOcr.view.title.line').d('发票行')}</h3>}
          >
            <Row>
              <Col>
                <Table
                  bordered
                  rowKey="vatInvoiceLineId"
                  columns={columns}
                  pagination={false}
                  dataSource={editDataSource}
                  scroll={{ x: tableScrollWidth(columns) }}
                />
              </Col>
            </Row>
          </Card>
          <Row>
            <Col style={{ display: 'flex', justifyContent: 'flex-end', marginRight: 15 }}>
              <Button
                icon="eye"
                type="primary"
                onClick={this.handlePreviewImg}
                style={{ marginRight: '6px' }}
                loading={redirectImageLoading}
              >
                {intl.get('hzero.common.button.see').d('预览')}
              </Button>
            </Col>
          </Row>
          <Divider />
        </Spin>
      </>
    );
  }
}
