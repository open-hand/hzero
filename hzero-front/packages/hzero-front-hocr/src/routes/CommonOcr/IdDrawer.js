import React, { Component } from 'react';
import Viewer from 'react-viewer';
import { Form, Modal, Button, Row, Col } from 'hzero-ui';
import { isNil } from 'lodash';
import { Bind } from 'lodash-decorators';

import Upload from 'components/Upload/UploadButton';

import intl from 'utils/intl';
import { FORM_COL_2_LAYOUT } from 'utils/constants';
import { BKT_OCR } from 'utils/config';

import 'react-viewer/dist/index.css';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class IdDrawer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: [],
      previewVisible: false,
    };
  }

  /**
   * 模态框onCancel回调
   */
  @Bind()
  onCancel() {
    const { onCancelId } = this.props;
    onCancelId();
  }

  // 上传文件成功
  @Bind()
  onUploadFrontSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        imageFront: file.response,
      });
    }
  }

  // 上传文件成功
  @Bind()
  onUploadBackSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        imageBack: file.response,
      });
    }
  }

  // 删除文件成功
  @Bind()
  onCancelFrontSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        imageFront: '',
      });
    }
  }

  /**
   * 删除身份证反面
   * @param {*} file
   */
  @Bind()
  onCancelBackSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        imageBack: '',
      });
    }
  }

  /**
   * 图片预览
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
    const params = {
      imageUrlBack: form.getFieldValue('imageBack'),
      imageUrlFront: form.getFieldValue('imageFront'),
      code: codeType,
    };
    onOk(params);
  }

  render() {
    const { form, title, visible, loading, fileList } = this.props;
    const { previewImages, previewVisible } = this.state;
    const formLayout = {
      labelCol: { span: 8 },
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
            disabled={
              isNil(form.getFieldValue('imageFront')) ||
              isNil(form.getFieldValue('imageBack')) ||
              form.getFieldValue('imageFront') === '' ||
              form.getFieldValue('imageBack') === ''
            }
          >
            {intl.get('hzero.common.button.identity').d('识别')}
          </Button>,
        ]}
      >
        <Form>
          <Row>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hocr.commonOcr.model.commonOcr.imageFront').d('身份证正面')}
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
                  onUploadSuccess={this.onUploadFrontSuccess}
                  onRemove={this.onCancelFrontSuccess}
                  showUploadList={{
                    removePopConfirmTitle: intl
                      .get('hocr.common.view.message.deleteConfirm')
                      .d('是否删除'),
                  }}
                />
              </FormItem>
              <FormItem wrapperCol={{ span: 15, offset: 6 }}>
                {form.getFieldDecorator('imageFront', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hocr.commonOcr.model.commonOcr.imageFront').d('身份证正面'),
                      }),
                    },
                  ],
                })(<div />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hocr.commonOcr.model.commonOcr.imageBack').d('身份证反面')}
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
                  onUploadSuccess={this.onUploadBackSuccess}
                  onRemove={this.onCancelBackSuccess}
                  showUploadList={{
                    removePopConfirmTitle: intl
                      .get('hocr.common.view.message.deleteConfirm')
                      .d('是否删除'),
                  }}
                />
              </FormItem>
              <FormItem wrapperCol={{ span: 15, offset: 6 }}>
                {form.getFieldDecorator('imageBack', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hocr.commonOcr.model.commonOcr.imageBack').d('身份证反面'),
                      }),
                    },
                  ],
                })(<div />)}
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
