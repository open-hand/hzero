import React from 'react';
import Viewer from 'react-viewer';
import { Button, Card, Col, Divider, Form, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import notification from 'utils/notification';

import 'react-viewer/dist/index.css';

@Form.create({ fieldNameProp: null })
export default class RenderQuota extends React.Component {
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
        title={<h3>{intl.get('hocr.commonOcr.view.title.quotaDetail').d('定额发票详情')}</h3>}
      >
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.code').d('发票代码')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.code}
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
              label={intl.get('hocr.commonOcr.model.commonOcr.ocrAmount').d('发票金额')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.amount}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hocr.commonOcr.model.commonOcr.location').d('地址')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {params.location}
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
