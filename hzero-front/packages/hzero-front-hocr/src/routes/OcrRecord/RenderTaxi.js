import React from 'react';
import Viewer from 'react-viewer';
import { Form, Button, Row, Col, Card, Divider } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import 'react-viewer/dist/index.css';
import notification from 'utils/notification';
import {
  FORM_COL_3_LAYOUT,
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ROW_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT,
} from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class RenderTaxi extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: '',
      previewVisible: false,
    };
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
    const { params, redirectImageLoading } = this.props;
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
              {params.invoiceCode}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.number').d('发票号码')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.number}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.taxiNumber').d('车牌号')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.taxiNumber}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.date').d('日期')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.date}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.time').d('上下车时间')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.time}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.fare').d('金额')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.fare}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.fuelOilSurcharge').d('燃油附加费')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.fuelOilSurcharge}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hocr.commonOcr.model.commonOcr.callServiceSurcharge')
                .d('叫车服务费')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.callServiceSurcharge}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.totalFare').d('实收金额')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.totalFare}
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col style={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              icon="eye"
              type="primary"
              onClick={this.handlePreviewImg}
              style={{ marginRight: '6px' }}
              loading={redirectImageLoading}
            >
              {intl.get('hzero.common.button.see').d('预览')}
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
