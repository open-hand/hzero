/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import { Button, Card, Col, Divider, Form, Input, Row, Spin } from 'hzero-ui';
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

@connect(({ commonOcr, loading, ocrRecord }) => ({
  commonOcr,
  ocrRecord,
  fetchOcrIdentifyDetailLoading: loading.effects['commonOcr/fetchOcrIdentifyDetail'], // OCR识别loading
  updateIdRecognizeDetailLoading: loading.effects['commonOcr/updateIdRecognizeDetail'], // OCR更新识别loading
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
      objectVersionNumber: 1, // 更新时的版本号
    };
  }

  componentDidMount() {
    const {
      dispatch,
      match: {
        params: { imageUrlBack, imageUrlFront },
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
        imageUrlBack,
        imageUrlFront,
        code: 'ID_CARD',
      },
    }).then(res => {
      if (res) {
        notification.success();
      }
    });
  }

  /**
   * 正面预览
   */
  @Bind()
  handlePreviewImgFront() {
    const {
      dispatch,
      match: {
        params: { imageUrlFront },
      },
    } = this.props;
    dispatch({
      type: 'ocrRecord/redirect',
      payload: {
        bucketName: BKT_OCR,
        directory: 'hocr01',
        url: imageUrlFront,
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
   * 反面预览
   */
  @Bind()
  handlePreviewImgBack() {
    const {
      dispatch,
      match: {
        params: { imageUrlBack },
      },
    } = this.props;
    dispatch({
      type: 'ocrRecord/redirect',
      payload: {
        bucketName: BKT_OCR,
        directory: 'hocr01',
        url: imageUrlBack,
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

  /**
   * 保存
   */
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
          type: 'commonOcr/updateIdRecognizeDetail',
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
      updateIdRecognizeDetailLoading,
      redirectImageLoading,
      commonOcr: { ocrIdentifyDetail = [] },
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    const detailInfo = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
    return (
      <>
        <Header
          title={intl.get('hocr.commonOcr.view.title.id').d('身份证详情')}
          backPath="/hocr/common-ocr/list"
        >
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSaveDetail}
            loading={updateIdRecognizeDetailLoading || fetchOcrIdentifyDetailLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button icon="eye" onClick={this.handlePreviewImgBack} loading={redirectImageLoading}>
            {intl.get('hzero.common.button.seeFront').d('反面预览')}
          </Button>
          <Button icon="eye" onClick={this.handlePreviewImgFront} loading={redirectImageLoading}>
            {intl.get('hzero.common.button.seeBack').d('正面预览')}
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
              title={<h3>{intl.get('hocr.commonOcr.view.title.idDetail').d('身份证详情')}</h3>}
            >
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.name').d('姓名')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('name', {
                      initialValue: detailInfo && detailInfo.name,
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
                      initialValue: detailInfo && detailInfo.sex,
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
                      initialValue: detailInfo && detailInfo.ethnicity,
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
                      initialValue: detailInfo && detailInfo.birth,
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
                      initialValue: detailInfo && detailInfo.address,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.idNumber').d('身份证号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('number', {
                      initialValue: detailInfo && detailInfo.number,
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
                      initialValue: detailInfo && detailInfo.issue,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.validFrom').d('签发日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('validFrom', {
                      initialValue: detailInfo && detailInfo.validFrom,
                    })(<Input />)}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.commonOcr.model.commonOcr.validTo').d('失效日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('validTo', {
                      initialValue: detailInfo && detailInfo.validTo,
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
