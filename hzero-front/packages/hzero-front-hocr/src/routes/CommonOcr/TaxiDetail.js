/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import {
  Form,
  Card,
  Row,
  Col,
  Input,
  Button,
  Divider,
  Spin,
  DatePicker,
  InputNumber,
} from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { BKT_OCR } from 'utils/config';
import { getDateFormat } from 'utils/utils';

import 'react-viewer/dist/index.css';

import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  DEFAULT_DATE_FORMAT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

const dateTimeFormat = getDateFormat();

@connect(({ commonOcr, loading, ocrRecord }) => ({
  commonOcr,
  ocrRecord,
  fetchOcrIdentifyDetailLoading: loading.effects['commonOcr/fetchOcrIdentifyDetail'], // OCR识别loading
  updateTaxiRecognizeDetailLoading: loading.effects['commonOcr/updateTaxiRecognizeDetail'], // OCR更新识别loading
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@formatterCollections({ code: ['hocr.commonOcr'] })
@Form.create({ fieldNameProp: null })
export default class TaxiDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: '',
      previewVisible: false,
      objectVersionNumber: 1,
    };
  }

  componentDidMount() {
    const {
      dispatch,
      match: {
        params: { imageUrl },
      },
    } = this.props;
    dispatch({
      type: 'commonOcr/updateState',
      payload: {
        ocrIdentifyDetail: [],
      },
    });
    dispatch({
      type: 'commonOcr/fetchOcrIdentifyDetail',
      payload: {
        imageUrl,
        code: 'TAXI_INVOICE',
      },
    }).then(res => {
      if (res) {
        notification.success();
      }
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewImg() {
    const {
      dispatch,
      match: {
        params: { imageUrl },
      },
    } = this.props;
    dispatch({
      type: 'ocrRecord/redirect',
      payload: {
        bucketName: BKT_OCR,
        directory: 'hocr01',
        url: imageUrl,
      },
    }).then(res => {
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

  @Bind()
  handleSaveDetail() {
    const {
      form,
      history,
      commonOcr: { ocrIdentifyDetail = [] },
      dispatch,
    } = this.props;
    const { objectVersionNumber } = this.state;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const initData = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
        dispatch({
          type: 'commonOcr/updateTaxiRecognizeDetail',
          payload: {
            ...initData,
            ...fieldsValue,
            date: fieldsValue.date.format(DEFAULT_DATE_FORMAT),
            objectVersionNumber,
          },
        }).then(res => {
          if (res) {
            this.setState({
              objectVersionNumber: res.objectVersionNumber,
            });
            notification.success();
            history.push(`/hocr/common-ocr/list`);
          }
        });
      }
    });
  }

  render() {
    const {
      form,
      fetchOcrIdentifyDetailLoading,
      updateTaxiRecognizeDetailLoading,
      redirectImageLoading,
      commonOcr: { ocrIdentifyDetail = [] },
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    const detailInfo = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
    return (
      <>
        <Header
          title={intl.get('hocr.commonOcr.view.title.taxiDetal').d('出租车票详情')}
          backPath="/hocr/common-ocr/list"
        >
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSaveDetail}
            loading={updateTaxiRecognizeDetailLoading || fetchOcrIdentifyDetailLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button icon="eye" onClick={this.handlePreviewImg} loading={redirectImageLoading}>
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
          <Spin spinning={fetchOcrIdentifyDetailLoading}>
            <Card
              key="vat-detail"
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{intl.get('hocr.commonOcr.view.title.taxiDetail').d('出租车票详情')}</h3>}
            >
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.invoiceCode').d('发票代码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('invoiceCode', {
                      initialValue: detailInfo && detailInfo.invoiceCode,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.number').d('发票号码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('number', {
                      initialValue: detailInfo && detailInfo.number,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.taxiNumber').d('车牌号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('taxiNumber', {
                      initialValue: detailInfo && detailInfo.taxiNumber,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.date').d('日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('date', {
                      initialValue:
                        detailInfo && detailInfo.date ? moment(detailInfo.date) : undefined,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hocr.commonOcr.model.commonOcr.date').d('日期'),
                          }),
                        },
                      ],
                    })(
                      <DatePicker
                        showTime={{ format: DEFAULT_DATE_FORMAT }}
                        format={dateTimeFormat}
                        placeholder=""
                      />
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.time').d('上下车时间')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('time', {
                      initialValue: detailInfo && detailInfo.time,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.fare').d('金额')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('fare', {
                      initialValue: detailInfo && detailInfo.fare,
                    })(<InputNumber min={0} step={0.01} precision={2} />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.fuelOilSurcharge')
                      .d('燃油附加费')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('fuelOilSurcharge', {
                      initialValue: detailInfo && detailInfo.fuelOilSurcharge,
                    })(<InputNumber min={0} step={0.01} precision={2} />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.callServiceSurcharge')
                      .d('叫车服务费')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('callServiceSurcharge', {
                      initialValue: detailInfo && detailInfo.callServiceSurcharge,
                    })(<InputNumber min={0} step={0.01} precision={2} />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.totalFare').d('实收金额')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('totalFare', {
                      initialValue: detailInfo && detailInfo.totalFare,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.totalFare')
                              .d('实收金额'),
                          }),
                        },
                      ],
                    })(<InputNumber min={0} step={0.01} precision={2} />)}
                  </Form.Item>
                </Col>
              </Row>
              <Divider />
            </Card>
          </Spin>
        </Content>
      </>
    );
  }
}
