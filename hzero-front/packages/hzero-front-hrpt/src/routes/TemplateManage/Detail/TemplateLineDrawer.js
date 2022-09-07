import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal, Button, Form, Select } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import Upload from 'components/Upload/UploadButton';

import intl from 'utils/intl';
import { BKT_RPT } from 'utils/config';

import XmlCodemirror from './XmlCodemirror';
import PreviewDrawer from './PreviewDrawer';
import style from './index.less';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 4 },
  wrapperCol: { span: 20 },
};
const { Option } = Select;
/**
 * 列信息-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 操作对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class TemplateLineDrawer extends PureComponent {
  /**
   * state初始化
   */
  state = {};

  /**
   * 组件属性定义
   */
  static propTypes = {
    anchor: PropTypes.oneOf(['left', 'right', 'top', 'bottom']),
    title: PropTypes.string,
    visible: PropTypes.bool,
    onOk: PropTypes.func,
    onCancel: PropTypes.func,
  };

  /**
   * 组件属性默认值设置
   */
  static defaultProps = {
    anchor: 'right',
    title: '',
    visible: false,
    onOk: e => e,
    onCancel: e => e,
  };

  /**
   * 确定操作
   */
  @Bind()
  saveBtn() {
    const { form, onOk, onEditOk, itemData, templateTypeCodeValue } = this.props;
    if (onOk) {
      if (templateTypeCodeValue === 'html') {
        form.validateFields(['templateContent', 'lang'], (err, values) => {
          if (isEmpty(err)) {
            // 校验通过，进行保存操作
            if (isEmpty(itemData)) {
              onOk(values);
            } else {
              onEditOk(values);
            }
          }
        });
      } else {
        form.validateFields(['templateUrl', 'lang'], (err, values) => {
          if (isEmpty(err)) {
            // 校验通过，进行保存操作
            if (isEmpty(itemData)) {
              onOk(values);
            } else {
              onEditOk(values);
            }
          }
        });
      }
    }
  }

  // 上传图片成功
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        templateUrl: file.response,
      });
    }
  }

  // 删除图片成功
  @Bind()
  onCancelSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        templateUrl: undefined,
      });
    }
  }

  /**
   * @function setCodeMirror - 获取CodeMirror实例
   * @param {object} editor - CodeMirror实例
   */
  setCodeMirror(editor) {
    this.codeMirrorEditor = editor;
  }

  /**
   * 编辑代码后更新数据
   * @param {object} editor - 编辑器对象
   * @param {object} data - 数据对象
   * @param {string} value - 编辑后的代码
   */
  @Bind()
  codeChange(editor, data, value) {
    const { form } = this.props;
    form.setFieldsValue({ templateContent: value });
  }

  @Bind()
  openPreviewModal() {
    this.setState({
      previewVisible: true,
    });
  }

  @Bind()
  closePreviewModal() {
    this.setState({
      previewVisible: false,
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      anchor,
      visible,
      templateLineTitle,
      form,
      onCancel,
      itemData = {},
      supportLanguage,
      fileList,
      templateTypeCodeValue,
      saving,
    } = this.props;
    const { previewVisible = false } = this.state;
    const { getFieldDecorator } = form;
    const codeMirrorProps = {
      value: form.getFieldValue('templateContent'),
      onBeforeChange: this.codeChange,
    };
    const previewProps = {
      visible: previewVisible,
      value: form.getFieldValue('templateContent'),
      title: intl.get('hrpt.templateManage.view.message.title.preview').d('预览html内容'),
      onCancel: this.closePreviewModal,
      footer: [
        <Button key="ok" type="primary" onClick={this.closePreviewModal}>
          {intl.get('hzero.common.button.close').d('关闭')}
        </Button>,
      ],
    };
    return (
      <Modal
        destroyOnClose
        width={600}
        title={templateLineTitle}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.saveBtn}
        onCancel={onCancel}
        confirmLoading={saving}
      >
        <Form>
          <Form.Item label={intl.get('entity.lang.tag').d('语言')} {...formLayout}>
            {getFieldDecorator('lang', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('entity.lang.tag').d('语言'),
                  }),
                },
              ],
              initialValue: itemData.lang,
            })(
              <Select allowClear style={{ width: '40%' }}>
                {supportLanguage &&
                  supportLanguage.map(item => (
                    <Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
              </Select>
            )}
          </Form.Item>
          {templateTypeCodeValue === 'html' ? (
            <Form.Item
              label={intl
                .get('hrpt.templateManage.model.templateManage.templateContent')
                .d('html内容')}
              {...formLayout}
            >
              {getFieldDecorator('templateContent', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hrpt.templateManage.model.templateManage.templateContent')
                        .d('html内容'),
                    }),
                  },
                ],
                initialValue: itemData.templateContent,
              })(
                <div className={style['preview-box']}>
                  <Button className={style['preview-btn']} onClick={this.openPreviewModal}>
                    {intl.get('hzero.common.button.see').d('预览')}
                  </Button>
                  <XmlCodemirror
                    codeMirrorProps={codeMirrorProps}
                    fetchCodeMirror={editor => this.setCodeMirror(editor)}
                  />
                </div>
              )}
            </Form.Item>
          ) : (
            <>
              <Form.Item
                label={intl
                  .get('hrpt.templateManage.model.templateManage.fileUpload')
                  .d('文件上传')}
                {...formLayout}
                // eslint-disable-next-line
                required={true}
                extra={intl
                  .get('hzero.common.upload.support', {
                    type: '*.doc;*.docx;*.rtf;*.xls',
                  })
                  .d('上传格式：*.doc;*.docx;*.rtf;*.xls')}
              >
                <Upload
                  single
                  disabled={isUndefined(templateTypeCodeValue)}
                  accept={
                    templateTypeCodeValue === 'rtf'
                      ? '.rtf'
                      : templateTypeCodeValue === 'xls'
                      ? 'application/vnd.ms-excel'
                      : null
                  }
                  bucketName={BKT_RPT}
                  bucketDirectory="hrpt01"
                  onUploadSuccess={this.onUploadSuccess}
                  fileList={fileList}
                  onRemove={this.onCancelSuccess}
                />
              </Form.Item>
              <Form.Item wrapperCol={{ span: 20, offset: 4 }} style={{ marginBottom: '0px' }}>
                {getFieldDecorator('templateUrl', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hrpt.templateManage.model.templateManage.fileUpload')
                          .d('文件上传'),
                      }),
                    },
                  ],
                  initialValue: itemData.templateUrl,
                })(<div />)}
              </Form.Item>
            </>
          )}
        </Form>
        <PreviewDrawer {...previewProps} />
      </Modal>
    );
  }
}
