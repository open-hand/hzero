import React from 'react';
import Viewer from 'react-viewer';
import { Button, Card, Col, Divider, Form, Input, InputNumber, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import notification from 'utils/notification';
import intl from 'utils/intl';
import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

import 'react-viewer/dist/index.css';

@Form.create({ fieldNameProp: null })
export default class RenderTrain extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: '',
      previewVisible: false,
      objectVersionNumber: 1,
    };
  }

  @Bind()
  handleSaveTrainDetail() {
    const { form, params, onSaveTrain } = this.props;
    const { objectVersionNumber } = this.state;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const dataSource = {
          ...params,
          ...fieldsValue,
          objectVersionNumber,
        };
        onSaveTrain(dataSource).then(res => {
          if (res) {
            notification.success();
            this.setState({
              objectVersionNumber: res.objectVersionNumber,
            });
          }
        });
      }
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
    const {
      form,
      params,
      redirectImageLoading,
      fetchOcrIdentifyDetailLoading,
      updateTrainRecognizeDetailLoading,
    } = this.props;
    const { previewImages, previewVisible } = this.state;
    return (
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
                initialValue: params.ticketNumber,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.trainNumber').d('车次')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('trainNumber', {
                initialValue: params.trainNumber,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.checkPort').d('检票口')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('checkPort', {
                initialValue: params.checkPort,
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
                initialValue: params.startingStation,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hocr.commonOcr.model.commonOcr.startingStation').d('始发站'),
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.destinationStation').d('终点站')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('destinationStation', {
                initialValue: params.destinationStation,
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
                initialValue: params.startingTime,
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
                initialValue: params.ticketPrice,
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
                initialValue: params.seatCategory,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.passengerName').d('乘客姓名')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('name', {
                initialValue: params.name,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hocr.commonOcr.model.commonOcr.passengerName').d('乘客姓名'),
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
                initialValue: params.seatName,
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
        <Row>
          <Col style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              type="eye"
              onClick={this.handlePreviewImg}
              style={{ marginRight: '6px' }}
              loading={redirectImageLoading}
            >
              {intl.get('hzero.common.button.see').d('预览')}
            </Button>
            <Button
              type="primary"
              loading={fetchOcrIdentifyDetailLoading || updateTrainRecognizeDetailLoading}
              onClick={this.handleSaveTrainDetail}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          </Col>
        </Row>
        <Divider />
        <Viewer
          noImgDetails
          noNavbar
          scalable={false}
          changeable={false}
          visible={previewVisible}
          onClose={this.handlePreviewCancel}
          images={previewImages}
        />
      </Card>
    );
  }
}
