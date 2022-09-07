import React from 'react';
import Viewer from 'react-viewer';
import { Form, Button, Row, Col, Input, Card, Divider, InputNumber, DatePicker } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import notification from 'utils/notification';
import intl from 'utils/intl';
import { getDateFormat } from 'utils/utils';
import 'react-viewer/dist/index.css';
import {
  FORM_COL_3_LAYOUT,
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ROW_LAYOUT,
  DEFAULT_DATE_FORMAT,
  EDIT_FORM_ITEM_LAYOUT,
} from 'utils/constants';

const dateTimeFormat = getDateFormat();

@Form.create({ fieldNameProp: null })
export default class RenderTaxi extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: '',
      previewVisible: false,
      objectVersionNumber: 1,
    };
  }

  @Bind()
  handleSaveTaxiDetail() {
    const { form, params, onSaveTaxi } = this.props;
    const { objectVersionNumber } = this.state;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const dataSource = {
          ...params,
          ...fieldsValue,
          objectVersionNumber,
          date: fieldsValue.date.format(DEFAULT_DATE_FORMAT),
        };
        onSaveTaxi(dataSource).then(res => {
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
    const { previewVisible, previewImages } = this.state;
    return (
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
                initialValue: params.invoiceCode,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.number').d('发票号码')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('number', {
                initialValue: params.number,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.taxiNumber').d('车牌号')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('taxiNumber', {
                initialValue: params.taxiNumber,
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
                initialValue: params.date ? moment(params.date) : undefined,
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
                initialValue: params.time,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.fare').d('金额')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('fare', {
                initialValue: params.fare,
              })(<InputNumber min={0} step={0.01} precision={2} />)}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.fuelOilSurcharge').d('燃油附加费')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('fuelOilSurcharge', {
                initialValue: params.fuelOilSurcharge,
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
                initialValue: params.callServiceSurcharge,
              })(<InputNumber min={0} step={0.01} precision={2} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.totalFare').d('实收金额')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('totalFare', {
                initialValue: params.totalFare,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hocr.commonOcr.model.commonOcr.totalFare').d('实收金额'),
                    }),
                  },
                ],
              })(<InputNumber min={0} step={0.01} precision={2} />)}
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
              onClick={this.handleSaveTaxiDetail}
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
