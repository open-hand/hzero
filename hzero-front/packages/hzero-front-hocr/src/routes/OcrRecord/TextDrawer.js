import React, { Component } from 'react';
import { Button, Card, Col, Form, Input, Modal, Row, Spin } from 'hzero-ui';

import {
  DETAIL_CARD_CLASSNAME,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_3_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import Viewer from 'react-viewer';
import { Bind } from 'lodash-decorators';

import 'react-viewer/dist/index.css';

const FormItem = Form.Item;
const { TextArea } = Input;
@Form.create({ fieldNameProp: null })
export default class TextDrawer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: [],
      previewVisible: false,
    };
  }

  @Bind()
  onCancel() {
    const { onTextCancel } = this.props;
    onTextCancel();
  }

  /**
   * 图片预览
   * @param {*} file
   */
  @Bind()
  handlePreview() {
    const { textUrl } = this.props;
    this.setState({
      previewImages: [
        {
          src: textUrl,
          alt: '', // 由于下方会显示 alt 所以这里给空字符串 file.name,
        },
      ],
      previewVisible: true,
    });
  }

  /**
   * 图片预览取消
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
      title,
      visible,
      textUrl,
      fetchOcrRecordDetailLoading,
      ocrRecordDetail = {},
      textDetail = {},
    } = this.props;
    const { previewImages, previewVisible } = this.state;
    const { typeMeaning, realName, recognizeDate, resourceUrl1 } = ocrRecordDetail;
    const formLayout = {
      labelCol: { span: 3 },
      wrapperCol: { span: 19 },
    };
    return (
      <Modal
        width={700}
        title={title}
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.onCancel}
        destroyOnClose
        footer={[
          <Button key="cancel" onClick={this.onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
        ]}
      >
        <Spin spinning={fetchOcrRecordDetailLoading}>
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hocr.ocrRecord.view.title.head').d('基本信息')}</h3>}
          >
            <Form>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col span={24}>
                  <FormItem
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.typeMeaning').d('识别类型')}
                    {...formLayout}
                  >
                    {typeMeaning}
                  </FormItem>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col span={24}>
                  <FormItem
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.realName').d('识别人')}
                    {...formLayout}
                  >
                    {realName}
                  </FormItem>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col span={24}>
                  <FormItem
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.recognizeDate').d('识别日期')}
                    {...formLayout}
                  >
                    {recognizeDate}
                  </FormItem>
                </Col>
              </Row>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col span={24}>
                  <FormItem
                    label={intl.get('hocr.ocrRecord.model.ocrRecord.resourceUrl').d('资源路径')}
                    {...formLayout}
                  >
                    {resourceUrl1}
                  </FormItem>
                </Col>
              </Row>
            </Form>
          </Card>
        </Spin>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hocr.ocrRecord.view.title.resultInfo').d('识别结果')}</h3>}
        >
          <Form>
            <Row>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem {...formLayout}>
                  <Spin spinning={fetchOcrRecordDetailLoading}>
                    <img
                      src={textUrl}
                      style={{ cursor: 'pointer' }}
                      alt=""
                      width="160px"
                      height="120px"
                      onClick={this.handlePreview}
                    />
                  </Spin>
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_3_LAYOUT}>
                <TextArea rows={9} value={textDetail.textContent} readOnly />
              </Col>
              <Viewer
                noImgDetails
                noNavbar
                scalable={false}
                changeable={false}
                visible={previewVisible}
                onClose={this.handlePreviewCancel}
                images={previewImages}
              />
            </Row>
          </Form>
        </Card>
      </Modal>
    );
  }
}
