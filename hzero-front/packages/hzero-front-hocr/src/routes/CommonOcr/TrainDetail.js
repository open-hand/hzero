/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import { Button, Card, Col, Divider, Form, Input, InputNumber, Row, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { BKT_OCR } from 'utils/config';

import 'react-viewer/dist/index.css';

import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

// const FormItem = Form.Item;
// const { TextArea } = Input;

@connect(({ commonOcr, loading, ocrRecord }) => ({
  commonOcr,
  ocrRecord,
  fetchOcrIdentifyDetailLoading: loading.effects['commonOcr/fetchOcrIdentifyDetail'], // OCR识别loading
  updateTrainRecognizeDetailLoading: loading.effects['commonOcr/updateTrainRecognizeDetail'], // OCR更新识别loading
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@formatterCollections({ code: ['hocr.commonOcr'] })
@Form.create({ fieldNameProp: null })
export default class TrainDetail extends React.Component {
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
        code: 'TRAIN_TICKET',
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
          type: 'commonOcr/updateTrainRecognizeDetail',
          payload: {
            ...initData,
            ...fieldsValue,
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
      updateTrainRecognizeDetailLoading,
      redirectImageLoading,
      commonOcr: { ocrIdentifyDetail = [] },
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    const detailInfo = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
    return (
      <>
        <Header
          title={intl.get('hocr.commonOcr.view.title.trainDetail').d('火车票详情')}
          backPath="/hocr/common-ocr/list"
        >
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSaveDetail}
            loading={updateTrainRecognizeDetailLoading || fetchOcrIdentifyDetailLoading}
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
              title={<h3>{intl.get('hocr.commonOcr.view.title.trainDetail').d('火车票详情')}</h3>}
            >
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.ticketNumber').d('车票号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('ticketNumber', {
                      initialValue: detailInfo && detailInfo.ticketNumber,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.trainNumber').d('车次')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('trainNumber', {
                      initialValue: detailInfo && detailInfo.trainNumber,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.checkPort').d('检票口')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('checkPort', {
                      initialValue: detailInfo && detailInfo.checkPort,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.startingStation').d('始发站')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('startingStation', {
                      initialValue: detailInfo && detailInfo.startingStation,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.startingStation')
                              .d('始发站'),
                          }),
                        },
                      ],
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.destinationStation')
                      .d('终点站')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('destinationStation', {
                      initialValue: detailInfo && detailInfo.destinationStation,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.destinationStation')
                              .d('终点站'),
                          }),
                        },
                      ],
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.startingTime').d('开车时间')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('startingTime', {
                      initialValue: detailInfo && detailInfo.startingTime,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.ticketPrice').d('票价')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('ticketPrice', {
                      initialValue: detailInfo && detailInfo.ticketPrice,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hocr.commonOcr.model.commonOcr.ticketPrice').d('票价'),
                          }),
                        },
                      ],
                    })(<InputNumber min={0} step={0.01} precision={2} />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.seatCategory').d('席别')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('seatCategory', {
                      initialValue: detailInfo && detailInfo.seatCategory,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.passengerName').d('乘客姓名')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('name', {
                      initialValue: detailInfo && detailInfo.name,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.passengerName')
                              .d('乘客姓名'),
                          }),
                        },
                      ],
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.seatName').d('座位号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('seatName', {
                      initialValue: detailInfo && detailInfo.seatName,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hocr.commonOcr.model.commonOcr.seatName').d('座位号'),
                          }),
                        },
                      ],
                    })(<Input />)}
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
