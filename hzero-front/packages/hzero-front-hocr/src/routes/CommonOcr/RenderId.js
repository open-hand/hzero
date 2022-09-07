import React from 'react';
import Viewer from 'react-viewer';
import { Button, Card, Col, Divider, Form, Input, Row } from 'hzero-ui';
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
export default class RenderId extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: '',
      previewVisible: false,
      objectVersionNumber: 1,
    };
  }

  @Bind()
  handleSaveIdDetail() {
    const { form, params, onSaveId } = this.props;
    const { objectVersionNumber } = this.state;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const dataSource = {
          ...params,
          ...fieldsValue,
          objectVersionNumber,
        };
        onSaveId(dataSource).then(res => {
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
              label={intl.get('hocr.commonOcr.model.commonOcr.name').d('姓名')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('name', {
                initialValue: params && params.name,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hocr.commonOcr.model.commonOcr.name').d('姓名'),
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.sex').d('性别')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('sex', {
                initialValue: params && params.sex,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hocr.commonOcr.model.commonOcr.sex').d('性别'),
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.ethnicity').d('民族')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('ethnicity', {
                initialValue: params && params.ethnicity,
              })(<Input />)}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.birth').d('出生日期')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('birth', {
                initialValue: params && params.birth,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hocr.commonOcr.model.commonOcr.birth').d('出生日期'),
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.address').d('地址')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('address', {
                initialValue: params && params.address,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.idNumber').d('身份证号')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('number', {
                initialValue: params && params.number,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hocr.commonOcr.model.commonOcr.idNumber').d('身份证号'),
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
              label={intl.get('hocr.commonOcr.model.commonOcr.issue').d('签发机关')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('issue', {
                initialValue: params && params.issue,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.validFrom').d('签发日期')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('validFrom', {
                initialValue: params && params.validFrom,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.validTo').d('失效日期')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('validTo', {
                initialValue: params && params.validTo,
              })(<Input />)}
            </Form.Item>
          </Col>
        </Row>
        <Divider />
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
              onClick={this.handleSaveIdDetail}
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
