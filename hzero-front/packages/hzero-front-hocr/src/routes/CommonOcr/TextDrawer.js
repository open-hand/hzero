import React, { Component } from 'react';
import { Form, Input, Modal, Button, Row, Col } from 'hzero-ui';
import Viewer from 'react-viewer';
import { Bind } from 'lodash-decorators';
import { isNil } from 'lodash';

import Upload from 'components/Upload/UploadButton';

import intl from 'utils/intl';
import { FORM_COL_4_LAYOUT, FORM_COL_3_4_LAYOUT } from 'utils/constants';
import { BKT_OCR } from 'utils/config';

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
    const { onCancelText } = this.props;
    onCancelText();
  }

  // 上传文件成功
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        imageUrl: file.response,
      });
    }
  }

  // 删除文件成功
  @Bind()
  onCancelSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        imageUrl: '',
      });
    }
  }

  /**
   * 文件预览
   * @param {*} file
   */
  @Bind()
  handlePreview(file) {
    this.setState({
      previewImages: [
        {
          src: file.url || file.thumbUrl,
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

  /**
   * 识别
   */
  @Bind()
  handleIdentity() {
    const { form, codeType, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      const params = {
        imageUrl: fieldsValue.imageUrl,
        code: codeType,
      };
      if (!err) {
        onOk(params);
      }
    });
  }

  /**
   * 保存
   */
  @Bind()
  handleSave() {
    const { form, onSave, ocrIdentifyDetail = [] } = this.props;
    form.validateFields((err, fieldsValue) => {
      const filterInfo = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
      const params = {
        ...filterInfo,
        textContent: fieldsValue.textContent,
      };
      if (!err) {
        onSave(params);
        this.onCancel();
      }
    });
  }

  render() {
    const {
      form,
      title,
      visible,
      loading,
      fileList,
      ocrIdentifyDetail = [],
      updateTextRecognizeDetail,
      fetchOcrIdentifyDetailLoading,
    } = this.props;
    const filterInfo = ocrIdentifyDetail[0] && ocrIdentifyDetail[0].resultInfo;
    const dataSource = filterInfo && filterInfo.textContent;
    const { previewImages, previewVisible } = this.state;
    const { getFieldDecorator } = form;
    const formLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 16 },
    };
    return (
      <Modal
        width={700}
        title={title}
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.onCancel}
        confirmLoading={loading}
        destroyOnClose
        footer={[
          <Button key="cancel" onClick={this.onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
          <Button
            key="back"
            type="primary"
            onClick={this.handleIdentity}
            loading={fetchOcrIdentifyDetailLoading}
            disabled={
              isNil(form.getFieldValue('imageUrl')) || form.getFieldValue('imageUrl') === ''
            }
          >
            {intl.get('hzero.common.button.identity').d('识别')}
          </Button>,
          <Button
            key="submit"
            type="primary"
            onClick={this.handleSave}
            loading={updateTextRecognizeDetail}
            disabled={
              isNil(form.getFieldValue('textContent')) ||
              form.getFieldValue('textContent') === dataSource
            }
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>,
        ]}
      >
        <Form>
          <Row>
            <Col {...FORM_COL_4_LAYOUT}>
              <FormItem
                {...formLayout}
                // eslint-disable-next-line
                required={true}
              >
                <Upload
                  accept="image/jpeg,image/png"
                  single
                  bucketName={BKT_OCR}
                  bucketDirectory="hocr02"
                  listType="picture-card"
                  fileList={fileList}
                  onPreview={this.handlePreview}
                  onUploadSuccess={this.onUploadSuccess}
                  onRemove={this.onCancelSuccess}
                  showUploadList={{
                    removePopConfirmTitle: intl
                      .get('hocr.commonOcr.view,message.deleteConfirm')
                      .d('是否删除'),
                  }}
                />
              </FormItem>
              <FormItem wrapperCol={{ span: 15, offset: 6 }}>
                {getFieldDecorator('imageUrl', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hocr.commonOcr.model.commonOcr.imageUrl').d('识别图片'),
                      }),
                    },
                  ],
                })(<div />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_3_4_LAYOUT}>
              <FormItem
                {...formLayout}
                label={intl.get('hocr.commonOcr.model.commonOcr.textContent').d('识别结果')}
              >
                {getFieldDecorator('textContent', {
                  initialValue: dataSource,
                })(<TextArea rows={26} />)}
              </FormItem>
            </Col>
          </Row>
        </Form>
        <Viewer
          noImgDetails
          noNavbar
          scalable={false}
          changeable={false}
          visible={previewVisible}
          onClose={this.handlePreviewCancel}
          images={previewImages}
        />
      </Modal>
    );
  }
}
