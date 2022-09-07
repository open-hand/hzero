/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import { Button, Card, Col, DatePicker, Divider, Form, Input, Row, Spin } from 'hzero-ui';
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
  DEFAULT_DATE_FORMAT,
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

const { TextArea } = Input;
const dateTimeFormat = getDateFormat();

@connect(({ commonOcr, loading, ocrRecord }) => ({
  commonOcr,
  ocrRecord,
  fetchOcrIdentifyDetailLoading: loading.effects['commonOcr/fetchOcrIdentifyDetail'], // OCR识别loading
  updateLicenseRecognizeDetailLoading: loading.effects['commonOcr/updateLicenseRecognizeDetail'], // OCR更新识别loading
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@formatterCollections({ code: ['hocr.commonOcr'] })
@Form.create({ fieldNameProp: null })
export default class LicenseDetail extends React.Component {
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
        code: 'BUSINESS_LICENSE',
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
          type: 'commonOcr/updateLicenseRecognizeDetail',
          payload: {
            ...initData,
            ...fieldsValue,
            foundDate: fieldsValue.foundDate.format(DEFAULT_DATE_FORMAT),
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
      updateLicenseRecognizeDetailLoading,
      redirectImageLoading,
      commonOcr: { ocrIdentifyDetail = [] },
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    const detailInfo = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
    return (
      <>
        <Header
          title={intl.get('hocr.commonOcr.view.title.licenseDetail').d('营业执照详情')}
          backPath="/hocr/common-ocr/list"
        >
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSaveDetail}
            loading={updateLicenseRecognizeDetailLoading || fetchOcrIdentifyDetailLoading}
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
              title={
                <h3>{intl.get('hocr.commonOcr.view.title.licenseDetail').d('营业执照详情')}</h3>
              }
            >
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.companyName').d('名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('name', {
                      initialValue: detailInfo && detailInfo.name,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hocr.commonOcr.model.commonOcr.companyName').d('名称'),
                          }),
                        },
                      ],
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.type').d('类型')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('type', {
                      initialValue: detailInfo && detailInfo.type,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hocr.commonOcr.model.commonOcr.type').d('类型'),
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
                      initialValue: detailInfo && detailInfo.address,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.legalPerson').d('法定代表人')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('legalPerson', {
                      initialValue: detailInfo && detailInfo.legalPerson,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.legalPerson')
                              .d('法定代表人'),
                          }),
                        },
                      ],
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.registeredCapital')
                      .d('注册资本')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('registeredCapital', {
                      initialValue: detailInfo && detailInfo.registeredCapital,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.registeredCapital')
                              .d('注册资本'),
                          }),
                        },
                      ],
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.foundDate').d('成立日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('foundDate', {
                      initialValue:
                        detailInfo && detailInfo.foundDate
                          ? moment(detailInfo.foundDate)
                          : undefined,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.foundDate')
                              .d('成立日期'),
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
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.validPeriod').d('营业期限')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('validPeriod', {
                      initialValue: detailInfo && detailInfo.validPeriod,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.commonOcr.model.commonOcr.registrationNumber')
                      .d('证件编号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('registrationNumber', {
                      initialValue: detailInfo && detailInfo.registrationNumber,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hocr.commonOcr.model.commonOcr.registrationNumber')
                              .d('证件编号'),
                          }),
                        },
                      ],
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.uscc').d('社会信用代码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('uscc', {
                      initialValue: detailInfo && detailInfo.uscc,
                    })(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.businessScope').d('经营范围')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('businessScope', {
                      initialValue: detailInfo && detailInfo.businessScope,
                    })(<TextArea rows={5} />)}
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
