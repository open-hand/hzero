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
  fetchTrainDetailLoading: loading.effects['ocrRecord/fetchTrainDetail'],
  redirectImageLoading: loading.effects['ocrRecord/redirect'],
}))
@Form.create({ fieldNameProp: null })
export default class TrainDetail extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      trainUrl: '',
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
        trainDetail: {},
      },
    });
    dispatch({
      type: 'ocrRecord/fetchTrainDetail',
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
        trainUrl: item,
      });
    });
  }

  // 识别结果图预览
  @Bind()
  handlePreviewImg() {
    const { trainUrl } = this.state;
    this.setState({
      previewImages: [
        {
          src: trainUrl,
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
      ocrRecord: { trainDetail = {} },
      fetchTrainDetailLoading,
      redirectImageLoading,
    } = this.props;
    const { previewVisible, previewImages } = this.state;
    return (
      <>
        <Header
          title={intl.get('hocr.ocrRecord.view.title.trainDetail').d('火车票详情')}
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
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hocr.ocrRecord.view.title.trainDetail').d('火车票详情')}</h3>}
          >
            <Spin spinning={fetchTrainDetailLoading}>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.ticketNumber').d('车票号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.ticketNumber}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.trainNumber').d('车次')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.trainNumber}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.checkPort').d('检票口')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.checkPort}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.startingStation').d('始发站')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.startingStation}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hocr.ocrRecord.model.ocrRecord.destinationStation')
                      .d('终点站')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.destinationStation}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.startingTime').d('开车时间')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.startingTime}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.ticketPrice').d('票价')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.ticketPrice}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.seatCategory').d('席别')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.seatCategory}</span>
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.passengerName').d('乘客姓名')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.name}</span>
                  </Form.Item>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.seatName').d('座位号')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    <span>{trainDetail.seatName}</span>
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
