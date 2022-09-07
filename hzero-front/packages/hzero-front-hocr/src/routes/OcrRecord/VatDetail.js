/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import 'react-viewer/dist/index.css';
import { Button, Card, Col, Divider, Form, Input, Row, Spin, Table } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { BKT_OCR } from 'utils/config';
import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

const { TextArea } = Input;

@connect(({ ocrRecord, loading }) => ({
  ocrRecord,
  fetchVatDetailLoading: loading.effects['ocrRecord/fetchVatDetail'],
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@Form.create({ fieldNameProp: null })
export default class VatDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      vatUrl: '',
      previewImages: '',
      previewVisible: '',
    };
  }

  componentDidMount() {
    const {
      dispatch,
      match: {
        params: { recordDetailId, resourceUrl1 },
      },
    } = this.props;
    dispatch({
      type: 'ocrRecord/updateState',
      payload: {
        vatDetail: {},
      },
    });
    dispatch({
      type: 'ocrRecord/fetchVatDetail',
      payload: {
        vatInvoiceHeaderId: recordDetailId,
      },
    });
    dispatch({
      type: 'ocrRecord/redirect',
      payload: {
        bucketName: BKT_OCR,
        directory: 'hocr01',
        url: resourceUrl1,
      },
    }).then(item => {
      this.setState({
        vatUrl: item,
      });
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewImg() {
    const { vatUrl } = this.state;
    this.setState({
      previewImages: [
        {
          src: vatUrl,
          alt: '',
        },
      ],
      previewVisible: true,
    });
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
    const {
      ocrRecord: { vatDetail = {} },
      fetchVatDetailLoading,
      redirectImageLoading,
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    const dataSource = vatDetail.itemList;
    const columns = [
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.productName').d('商品名称'),
        dataIndex: 'name',
      },
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.specification').d('规格型号'),
        dataIndex: 'specification',
        width: 180,
      },
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.unit').d('单位'),
        dataIndex: 'unit',
        width: 100,
      },
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.quantity').d('数量'),
        dataIndex: 'quantity',
        width: 100,
      },
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.unitPrice').d('单价(￥)'),
        dataIndex: 'unitPrice',
        width: 180,
      },
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.amountYuan').d('金额(￥)'),
        dataIndex: 'amount',
        width: 180,
      },
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.taxRate').d('税率(%)'),
        dataIndex: 'taxRate',
        width: 100,
      },
      {
        title: intl.get('hocr.ocrRecord.model.ocrRecord.tax').d('税额(￥)'),
        dataIndex: 'tax',
        width: 180,
      },
    ];
    return (
      <>
        <Header
          title={intl.get('hocr.ocrRecord.view.title.vat').d('增值税发票详情')}
          backPath="/hocr/ocr-record/list"
        >
          <Button
            icon="eye"
            type="primary"
            onClick={this.handlePreviewImg}
            loading={redirectImageLoading}
          >
            {intl.get('hzero.common.button.see').d('预览')}
          </Button>
        </Header>
        <Content>
          <Viewer
            noImgDetails
            noNavbar
            scalable={false}
            changeable={false}
            visible={previewVisible}
            onClose={this.handlePreviewCancel}
            images={previewImages}
          />
          <Card
            key="vat-detail"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hocr.ocrRecord.view.title.vatDetail').d('增值税发票详情')}</h3>}
          >
            <Spin spinning={fetchVatDetailLoading}>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.ocrType').d('发票种类')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.type}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.code').d('发票代码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.code}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.number').d('发票号码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.number}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.issueDate').d('开票日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.issueDate}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.checkCode').d('校验码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.checkCode}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.attribution').d('发票归属地')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.attribution}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.subtotalAmount').d('合计金额')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>￥{vatDetail.subtotalAmount}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.subtotalTax').d('合计税额')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>￥{vatDetail.subtotalTax}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.total').d('价税合计(小写)')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>￥{vatDetail.total}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.totalInWords')
                      .d('价税合计(大写)')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.totalInWords}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.receiver').d('收款人')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.receiver}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.reviewer').d('复核人')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.reviewer}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.issuer').d('开票人')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.issuer}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.remarks').d('备注')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.remarks}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.encryptionBlock').d('密码区')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <TextArea rows={3} value={vatDetail.encryptionBlock} readOnly />
                  </Form.Item>
                </Col>
              </Row>
              <Divider />
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.buyerName').d('购方名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.buyerName}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.sellerName').d('销售方名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.sellerName}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.buyerAddress')
                      .d('购方地址及电话')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.buyerAddress}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.sellerAddress')
                      .d('销售方地址及电话')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.sellerAddress}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.buyerId').d('购方纳税人识别号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.buyerId}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.sellerId')
                      .d('销售方纳税人识别号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.sellerId}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.buyerBank')
                      .d('购方开户行及账号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.buyerBank}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_2_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.sellerBank')
                      .d('销售方开户行及账号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{vatDetail.sellerBank}</span>
                  </Form.Item>
                </Col>
              </Row>
            </Spin>
          </Card>
          <Card
            key="vat-row"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hocr.ocrRecord.view.title.line').d('增值税发票行')}</h3>}
          >
            <Row>
              <Col>
                <Table
                  bordered
                  rowKey="vatInvoiceLineId"
                  columns={columns}
                  dataSource={dataSource}
                  pagination={false}
                  loading={fetchVatDetailLoading}
                  scroll={{ x: tableScrollWidth(columns) }}
                />
              </Col>
            </Row>
          </Card>
        </Content>
      </>
    );
  }
}
