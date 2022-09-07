/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import 'react-viewer/dist/index.css';
import { Form, Card, Row, Col, Divider, Spin, Button } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import { BKT_OCR } from 'utils/config';
import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

@connect(({ ocrRecord, loading }) => ({
  ocrRecord,
  fetchOcrTaxiDetailLoading: loading.effects['ocrRecord/fetchTaxiDetail'],
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@Form.create({ fieldNameProp: null })
export default class TaxiDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      taxiUrl: '',
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
        taxiDetail: {},
      },
    });
    dispatch({
      type: 'ocrRecord/fetchTaxiDetail',
      payload: {
        recordDetailId,
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
        taxiUrl: item,
      });
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewImg() {
    const { taxiUrl } = this.state;
    this.setState({
      previewImages: [
        {
          src: taxiUrl,
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
      ocrRecord: { taxiDetail = {} },
      fetchOcrTaxiDetailLoading,
      redirectImageLoading,
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    return (
      <>
        <Header
          title={intl.get('hocr.ocrRecord.view.title.taxiDetail').d('出租车票详情')}
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
            bordered={fetchOcrTaxiDetailLoading}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hocr.ocrRecord.view.title.taxiDetail').d('出租车票详情')}</h3>}
          >
            <Spin spinning={false}>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.invoiceCode').d('发票代码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{taxiDetail.invoiceCode}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.number').d('发票号码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{taxiDetail.number}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.taxiNumber').d('车牌号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{taxiDetail.taxiNumber}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.date').d('日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{taxiDetail.date}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.time').d('上下车时间')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{taxiDetail.time}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.fare').d('金额')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{taxiDetail.fare}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.fuelOilSurcharge')
                      .d('燃油附加费')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{taxiDetail.fuelOilSurcharge}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.callServiceSurcharge')
                      .d('叫车服务费')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{taxiDetail.callServiceSurcharge}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.totalFare').d('实收金额')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{taxiDetail.totalFare}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Divider />
            </Spin>
          </Card>
        </Content>
      </>
    );
  }
}
