/**
 * ocr-record 识别记录
 * @date: 2019-9-3
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import Viewer from 'react-viewer';
import 'react-viewer/dist/index.css';
import { Button, Card, Col, Divider, Form, Input, Row, Spin } from 'hzero-ui';
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

const { TextArea } = Input;

@connect(({ ocrRecord, loading }) => ({
  ocrRecord,
  fetchLicenseDetailLoading: loading.effects['ocrRecord/fetchLicenseDetail'],
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@Form.create({ fieldNameProp: null })
export default class LicenseDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      licenseUrl: '',
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
        recognizeList: [],
      },
    });
    dispatch({
      type: 'ocrRecord/fetchLicenseDetail',
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
        licenseUrl: item,
      });
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewImg() {
    const { licenseUrl } = this.state;
    this.setState({
      previewImages: [
        {
          src: licenseUrl,
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
      ocrRecord: { licenseDetail = {} },
      fetchLicenseDetailLoading,
      redirectImageLoading,
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    return (
      <>
        <Header
          title={intl.get('hocr.ocrRecord.view.title.licenseDetail').d('营业执照详情')}
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
            bordered={fetchLicenseDetailLoading}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hocr.ocrRecord.view.title.licenseDetail').d('营业执照详情')}</h3>}
          >
            <Spin spinning={false}>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.companyName').d('名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{licenseDetail.name}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.type').d('类型')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{licenseDetail.type}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.address').d('地址')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{licenseDetail.address}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.legalPerson').d('法定代表人')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{licenseDetail.legalPerson}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.registeredCapital')
                      .d('注册资本')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{licenseDetail.registeredCapital}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.foundDate').d('成立日期')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{licenseDetail.foundDate}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.validPeriod').d('营业期限')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{licenseDetail.validPeriod}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.registrationNumber')
                      .d('证件编号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{licenseDetail.registrationNumber}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.uscc').d('社会信用代码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{licenseDetail.uscc}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.businessScope').d('经营范围')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <TextArea rows={5} readOnly value={licenseDetail.businessScope} />
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
