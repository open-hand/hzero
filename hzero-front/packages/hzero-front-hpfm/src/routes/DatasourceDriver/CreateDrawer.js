/**
 * dataSourceDriver-数据源驱动
 * @date: 2019-08-22
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React, { Component } from 'react';
import { Form, Input, Modal, Select, Spin, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';

import Upload from 'components/Upload/UploadButton';
import TLEditor from 'components/TLEditor';
import Switch from 'components/Switch';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { CODE } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { BKT_PLATFORM } from 'utils/config';

const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class CreateDrawer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      uploadLoading: false,
    };
  }

  /**
   * 确定
   */
  @Bind()
  handleOk() {
    // const { fileList } = this.state;
    const { form, onOk, initData } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk({
          tenantId: getCurrentOrganizationId(),
          ...initData,
          ...fieldsValue,
        });
      }
    });
  }

  // 上传文件成功
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        driverPath: file.response,
      });
    }
    this.setState({ uploadLoading: false });
  }

  // 删除文件成功
  @Bind()
  onCancelSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        driverPath: '',
      });
    }
  }

  @Bind()
  beforeUpload(file) {
    const { fileType, fileSize = 10 * 1024 * 1024 } = this.props;
    this.setState({ uploadLoading: true });
    if (fileType && fileType.indexOf(file.type) === -1) {
      file.status = 'error'; // eslint-disable-line
      const res = {
        message: intl
          .get('hzero.common.upload.error.type', {
            fileType,
          })
          .d(`上传文件类型必须是：${fileType}`),
      };
      file.response = res; // eslint-disable-line
      return false;
    }
    if (file.size > fileSize) {
      file.status = 'error'; // eslint-disable-line
      const res = {
        message: intl
          .get('hzero.common.upload.error.size', {
            fileSize: fileSize / (1024 * 1024),
          })
          .d(`上传文件大小不能超过: ${fileSize / (1024 * 1024)} MB`),
      };
      file.response = res; // eslint-disable-line
      return false;
    }
    return true;
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form,
      title,
      fileList,
      initData,
      onCancel,
      isSiteFlag,
      initLoading,
      modalVisible,
      confirmLoading,
      dataSourceTypeList,
    } = this.props;
    const {
      tenantId,
      tenantName,
      driverId,
      driverName = '',
      driverVersion = '',
      databaseType = '',
      // mainClass = '',
      driverPath = '',
      description = '',
      enabledFlag = 1,
      _token,
    } = initData;
    const { getFieldDecorator } = form;
    const { uploadLoading } = this.state;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        transitionName="move-right"
        wrapClassName="ant-modal-sidebar-right"
        onCancel={onCancel}
        footer={[
          <Button key="back" onClick={onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
          <Button
            key="submit"
            type="primary"
            loading={confirmLoading}
            disabled={uploadLoading}
            onClick={this.handleOk}
          >
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
      >
        <Spin spinning={initLoading}>
          <Form>
            {isSiteFlag && (
              <Form.Item
                label={intl
                  .get('hpfm.dataSourceDriver.model.dataSourceDriver.tenantName')
                  .d('租户名称')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('tenantId', {
                  initialValue: tenantId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.dataSourceDriver.model.dataSourceDriver.tenantName')
                          .d('租户名称'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HPFM.TENANT"
                    textValue={tenantName}
                    disabled={tenantId !== undefined}
                    allowClear
                  />
                )}
              </Form.Item>
            )}
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpfm.dataSourceDriver.model.dataSourceDriver.driverName')
                .d('驱动名称')}
            >
              {getFieldDecorator('driverName', {
                initialValue: driverName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataSourceDriver.model.dataSourceDriver.driverName')
                        .d('驱动名称'),
                    }),
                  },
                  {
                    max: 255,
                    message: intl.get('hzero.common.validation.max', {
                      max: 255,
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl
                    .get('hpfm.dataSourceDriver.model.dataSourceDriver.driverName')
                    .d('驱动名称')}
                  field="driverName"
                  token={_token}
                  disabled={!isUndefined(driverId)}
                />
              )}
            </Form.Item>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpfm.dataSourceDriver.model.dataSourceDriver.driverVersion')
                .d('驱动版本')}
            >
              {getFieldDecorator('driverVersion', {
                initialValue: driverVersion,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataSourceDriver.model.dataSourceDriver.driverVersion')
                        .d('驱动版本'),
                    }),
                  },
                  {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 50,
                    message: intl.get('hzero.common.validation.max', {
                      max: 50,
                    }),
                  },
                ],
              })(<Input disabled={!isUndefined(driverId)} inputChinese={false} />)}
            </Form.Item>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpfm.dataSourceDriver.model.dataSourceDriver.databaseType')
                .d('驱动类型')}
            >
              {getFieldDecorator('databaseType', {
                initialValue: databaseType,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataSourceDriver.model.dataSourceDriver.databaseType')
                        .d('驱动类型'),
                    }),
                  },
                ],
              })(
                <Select allowClear disabled={!isUndefined(driverId)}>
                  {dataSourceTypeList.map((item) => (
                    <Option value={item.value} key={item.meaning}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </Form.Item>
            <Form.Item
              label={intl
                .get('hpfm.dataSourceDriver.model.dataSourceDriver.jarUpload')
                .d('jar上传')}
              {...MODAL_FORM_ITEM_LAYOUT}
              // eslint-disable-next-line
              required="true"
              extra={intl
                .get('hpfm.dataSourceDriver.model.dataSourceDriver.uploadSupport')
                .d('上传格式：*.jar')}
            >
              <Upload
                accept=".jar"
                single
                bucketName={BKT_PLATFORM}
                bucketDirectory="hpfm02"
                listType="text"
                fileList={fileList}
                onUploadSuccess={this.onUploadSuccess}
                onRemove={this.onCancelSuccess}
                beforeUpload={this.beforeUpload}
              />
            </Form.Item>
            <Form.Item wrapperCol={{ span: 15, offset: 6 }}>
              {getFieldDecorator('driverPath', {
                initialValue: driverPath,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataSourceDriver.model.dataSourceDriver.driverPath')
                        .d('jar包'),
                    }),
                  },
                ],
              })(<div />)}
            </Form.Item>
            {/* <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpfm.dataSourceDriver.model.dataSourceDriver.mainClass')
                .d('主类入口')}
            >
              {getFieldDecorator('mainClass', {
                initialValue: mainClass,
                rules: [
                  {
                    pattern: /[a-zA-Z]+[0-9a-zA-Z_]*(\.[a-zA-Z]+[0-9a-zA-Z_]*)*$/,
                    message: intl
                      .get('hpfm.dataSourceDriver.model.dataSourceDriver.message')
                      .d(
                        '入口主类格式错误。例子:(com.xx.xx）。包含大小写字母、数字和下划线，每段须以字母开头.'
                      ),
                  },
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataSourceDriver.model.dataSourceDriver.mainClass')
                        .d('主类入口'),
                    }),
                  },
                  {
                    max: 255,
                    message: intl.get('hzero.common.validation.max', {
                      max: 255,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item> */}
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.status').d('状态')}
            >
              {getFieldDecorator('enabledFlag', {
                initialValue: enabledFlag,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hzero.common.status').d('状态'),
                    }),
                  },
                ],
              })(<Switch />)}
            </Form.Item>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.description').d('描述')}
            >
              {getFieldDecorator('description', {
                initialValue: description || '',
                rules: [
                  {
                    max: 480,
                    message: intl.get('hzero.common.validation.max', {
                      max: 480,
                    }),
                  },
                ],
              })(<Input.TextArea rows={2} />)}
            </Form.Item>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
