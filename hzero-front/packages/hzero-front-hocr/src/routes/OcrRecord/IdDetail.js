/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import 'react-viewer/dist/index.css';
import { Button, Card, Col, Divider, Form, Row, Spin } from 'hzero-ui';
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
  fetchIdDetailLoading: loading.effects['ocrRecord/fetchIdDetail'],
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@Form.create({ fieldNameProp: null })
export default class IdDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      frontUrl: '',
      backUrl: '',
      previewImages: '',
      previewVisible: '',
    };
  }

  componentDidMount() {
    const {
      dispatch,
      match: {
        params: { recordDetailId, recordId },
      },
    } = this.props;
    dispatch({
      type: 'ocrRecord/updateState',
      payload: {
        idDetail: {},
      },
    });
    dispatch({
      type: 'ocrRecord/fetchIdDetail',
      payload: {
        recordDetailId,
      },
    });
    dispatch({
      type: 'ocrRecord/fetchOcrRecordDetail',
      payload: {
        recordId,
      },
    }).then(res => {
      if (res) {
        dispatch({
          type: 'ocrRecord/redirect',
          payload: {
            bucketName: BKT_OCR,
            directory: 'hocr01',
            url: res.resourceUrl1,
          },
        }).then(item => {
          this.setState({
            frontUrl: item,
          });
        });
        dispatch({
          type: 'ocrRecord/redirect',
          payload: {
            bucketName: BKT_OCR,
            directory: 'hocr01',
            url: res.resourceUrl2,
          },
        }).then(item => {
          this.setState({
            backUrl: item,
          });
        });
      }
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewFrontImg() {
    const { frontUrl } = this.state;
    this.setState({
      previewImages: [
        {
          src: frontUrl,
          alt: '',
        },
      ],
      previewVisible: true,
    });
  }

  @Bind()
  handlePreviewBackImg() {
    const { backUrl } = this.state;
    this.setState({
      previewImages: [
        {
          src: backUrl,
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
      ocrRecord: { idDetail = {} },
      fetchIdDetailLoading,
      redirectImageLoading,
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    return (
      <>
        <Header
          title={intl.get('hocr.ocrRecord.view.title.idDetail').d('身份证详情')}
          backPath="/hocr/ocr-record/list"
        >
          <Button
            icon="eye"
            type="primary"
            onClick={this.handlePreviewBackImg}
            loading={redirectImageLoading}
          >
            {intl.get('hzero.common.button.seeFront').d('反面预览')}
          </Button>
          <Button
            icon="eye"
            type="primary"
            onClick={this.handlePreviewFrontImg}
            loading={redirectImageLoading}
          >
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
          <Card
            key="vat-detail"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hocr.ocrRecord.view.title.idDetail').d('身份证详情')}</h3>}
          >
            <Spin spinning={fetchIdDetailLoading}>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.name').d('姓名')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{idDetail.name}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.sex').d('性别')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{idDetail.sex}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.ethnicity').d('民族')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{idDetail.ethnicity}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.birth').d('出生日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{idDetail.birth}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.address').d('地址')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{idDetail.address}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.idNumber').d('身份证号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{idDetail.number}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.issue').d('签发机关')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{idDetail.issue}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.validFrom').d('签发日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{idDetail.validFrom}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.validTo').d('失效日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{idDetail.validTo}</span>
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
