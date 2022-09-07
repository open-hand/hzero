import React, { Component } from 'react';
import Viewer from 'react-viewer';
import 'react-viewer/dist/index.css';
import { Form, Modal, Button, Row, Col, Checkbox } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNil } from 'lodash';

import Upload from 'components/Upload/UploadButton';

import intl from 'utils/intl';
import { BKT_OCR } from 'utils/config';
import { FORM_COL_2_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class VatDrawer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      previewImages: [],
      previewVisible: false,
    };
  }

  @Bind()
  onCancel() {
    const { onCancelVat } = this.props;
    onCancelVat();
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
      const params = fieldsValue.roll
        ? {
            imageUrl: fieldsValue.imageUrl,
            code: codeType,
            vatType: 'roll',
          }
        : {
            imageUrl: fieldsValue.imageUrl,
            code: codeType,
          };
      if (!err) {
        onOk(params);
      }
    });
  }

  render() {
    const { form, title, visible, loading, fileList } = this.props;
    const { previewImages, previewVisible } = this.state;
    const { getFieldDecorator } = form;
    const formLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 16 },
    };
    return (
      <Modal
        width={520}
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
              isNil(form.getFieldValue('imageUrl')) || form.getFieldValue('imageUrl') === ''
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
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hocr.commonOcr.view.message.roll').d('卷式')}
                {...formLayout}
              >
                {getFieldDecorator('roll', {})(<Checkbox />)}
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
