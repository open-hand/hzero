import React from 'react';
import { Button, Form, Input, InputNumber, Modal, Select, Upload, Switch } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { omit } from 'lodash';

import intl from 'utils/intl';
import Lov from 'components/Lov';

import styles from './style/index.less';

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

@Form.create({ fieldNameProp: null })
export default class ImportModal extends React.PureComponent {
  state = {
    fileList: [],
  };

  @Bind()
  handleCancel() {
    const { form, onCancel = (e) => e } = this.props;
    this.setState({ fileList: [] });
    form.resetFields();
    onCancel();
  }

  @Bind()
  handleUploadChange({ file }) {
    const { form } = this.props;
    if (file) {
      this.setState({
        fileList: [file],
      });
      form.setFieldsValue({
        file,
      });
    }
  }

  @Bind()
  handleUploadBefore(file) {
    const isXml = file.type === 'application/xml';
    return isXml;
  }

  @Bind()
  handleRemove() {
    const { form } = this.props;
    this.setState({ fileList: [] });
    form.setFieldsValue({ file: undefined });
  }

  @Bind()
  handleOk() {
    const { form, onOk = (e) => e } = this.props;
    const formData = new FormData();
    form.validateFields((error, values) => {
      if (!error) {
        const { overtime } = values;
        let fieldsValue = values;
        if (!overtime) {
          fieldsValue = omit(fieldsValue, ['overtime', 'overtimeUnit']);
        }
        for (const key of Object.keys(fieldsValue)) {
          if (key === 'file') {
            formData.append(key, fieldsValue[key]);
            // eslint-disable-next-line
            continue;
          }
          formData.append(key, fieldsValue[key]);
        }
        onOk(formData, () => {
          this.setState({ fileList: [] });
          form.resetFields();
        });
      }
    });
  }

  /**
   * 流程分类改变回调
   * @param {*} value
   */
  @Bind()
  handleCategoryChange(value) {
    const {
      onFetchDocuments,
      form: { resetFields },
    } = this.props;
    if (value) {
      onFetchDocuments(value);
    }
    resetFields(['documentId']);
  }

  render() {
    const { form, importVisible, importLoading, documents = [], isSiteFlag, tenantId } = this.props;
    const { fileList } = this.state;
    const { getFieldDecorator, getFieldValue } = form;

    return (
      <Modal
        title={intl.get('hwfp.processDefine.view.option.import').d('导入')}
        width={520}
        destroyOnClose
        visible={importVisible}
        confirmLoading={importLoading}
        onOk={this.handleOk}
        onCancel={this.handleCancel}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <Form>
          <Form.Item
            {...formLayout}
            style={{ marginBottom: 0 }}
            label={
              <span className="ant-form-item-required">
                {intl.get('hwfp.common.model.process.file').d('BPMN定义文件')}
              </span>
            }
          >
            <Upload
              accept="application/xml"
              name="file"
              fileList={fileList}
              onRemove={this.handleRemove}
              onChange={this.handleUploadChange}
              beforeUpload={this.handleUploadBefore}
            >
              <Button>{intl.get('hwfp.common.model.process.select').d('选择文件')}</Button>
            </Upload>
          </Form.Item>
          <Form.Item wrapperCol={{ span: 15, offset: 6 }}>
            {getFieldDecorator('file', {
              initialValue: fileList,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.process.file').d('BPMN定义文件'),
                  }),
                },
              ],
            })(<div />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.process.code').d('流程编码')}
            {...formLayout}
          >
            {getFieldDecorator('key', {
              initialValue: '',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.process.code').d('流程编码'),
                  }),
                },
                {
                  max: 60,
                  message: intl.get('hzero.common.validation.max', {
                    max: 60,
                  }),
                },
                {
                  pattern: /^[A-Z][A-Z0-9-_./]*$/,
                  message: intl
                    .get('hzero.common.validation.codeUpperBegin')
                    .d('全大写及数字，必须以字母开头，可包含“-”、“_”、“.”、“/”'),
                },
              ],
            })(<Input inputChinese={false} typeCase="upper" />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.process.name').d('流程名称')}
            {...formLayout}
          >
            {getFieldDecorator('name', {
              initialValue: '',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.process.name').d('流程名称'),
                  }),
                },
                {
                  max: 240,
                  message: intl.get('hzero.common.validation.max', {
                    max: 240,
                  }),
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.process.class').d('流程分类')}
            {...formLayout}
          >
            {getFieldDecorator('categoryId', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.process.class').d('流程分类'),
                  }),
                },
              ],
            })(
              <Lov
                code="HWFP.PROCESS_CATEGORY"
                queryParams={isSiteFlag ? {} : { tenantId }}
                onChange={this.handleCategoryChange}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.documents.class').d('流程单据')}
            {...formLayout}
          >
            {getFieldDecorator('documentId', {
              rules: [
                {
                  required: getFieldValue('categoryId'),
                  message: intl.get('hwfp.common.model.documents.class').d('流程单据'),
                },
              ],
            })(
              <Select allowClear style={{ width: '100%' }} disabled={!getFieldValue('categoryId')}>
                {documents.map((item) => (
                  <Select.Option value={item.value} key={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          {!isSiteFlag && (
            <Form.Item
              className={styles['inline-form-item']}
              label={intl.get('hwfp.processDefine.model.processDefine.timeout').d('超时时间')}
              {...formLayout}
            >
              <span>
                {getFieldDecorator('overtime', {
                  rules: [
                    {
                      required: getFieldValue('overtimeEnabled'),
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hwfp.processDefine.model.processDefine.timeout')
                          .d('超时时间'),
                      }),
                    },
                  ],
                })(<InputNumber min={0} />)}
              </span>
              <span>
                {getFieldDecorator('overtimeUnit', {
                  initialValue: 'hour',
                })(
                  <Select allowClear style={{ width: '30%' }}>
                    <Select.Option value="hour">
                      {intl.get('hzero.common.date.unit.hours').d('小时')}
                    </Select.Option>
                    <Select.Option value="day">
                      {intl.get('hzero.common.date.unit.day').d('小时')}
                    </Select.Option>
                  </Select>
                )}
              </span>
            </Form.Item>
          )}
          {!isSiteFlag && (
            <Form.Item
              label={intl
                .get('hwfp.processDefine.model.processDefine.overtimeEnabled')
                .d('超时设置启用')}
              {...formLayout}
            >
              {getFieldDecorator('overtimeEnabled', {
                initialValue: 0,
              })(<Switch checkedValue={1} unCheckedValue={0} />)}
            </Form.Item>
          )}
        </Form>
      </Modal>
    );
  }
}
