import React from 'react';
import { Modal, Input, Form, Row, Col, InputNumber, Spin, Select } from 'hzero-ui';
import { isEmpty, isFunction } from 'lodash';
import { Bind } from 'lodash-decorators';

import Upload from 'components/Upload/UploadButton';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { CODE } from 'utils/regExp';
import { BKT_PUBLIC } from 'utils/config';

const FormItem = Form.Item;
@Form.create({ fieldNameProp: null })
export default class AppDrawer extends React.PureComponent {
  /**
   * @function handleOK - 确认操作
   */
  @Bind()
  handleOK() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (isEmpty(err)) {
        if (isFunction(onOk)) {
          onOk(fieldsValue);
        }
      }
    });
  }

  /**
   * @function onUploadSuccess - 图片上传成功的回调函数
   * @param {object} file - 上传的文件对象
   */
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        appImage: file.response,
      });
    }
  }

  render() {
    const {
      form,
      modalVisible,
      openAppDetail,
      codeList,
      channelTypes,
      onCancel,
      fetchDetailLoading,
      saveDetailLoading,
      detailTitle,
      fileList = [],
    } = this.props;
    const {
      openAppId,
      appCode,
      appId,
      orderSeq,
      appName,
      appKey,
      appImage,
      channel,
      subAppId,
      scope,
      _token,
    } = openAppDetail;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        title={detailTitle}
        visible={modalVisible}
        width="1000px"
        confirmLoading={saveDetailLoading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={onCancel}
        onOk={this.handleOK}
      >
        <Spin spinning={openAppId ? fetchDetailLoading : false}>
          <Form>
            <Row>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.openApp.model.openApp.appCode').d('应用编码')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('appCode', {
                    initialValue: appCode,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.openApp.model.openApp.appCode').d('应用编码'),
                        }),
                      },
                      {
                        pattern: CODE,
                        message: intl
                          .get('hzero.common.validation.code')
                          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                      },
                    ],
                  })(
                    <Select disabled={!!openAppId} allowClear>
                      {codeList.map((item) => (
                        <Select.Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  label={intl.get('hiam.openApp.model.openApp.appId').d('APP ID')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('appId', {
                    initialValue: appId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.openApp.model.openApp.appId').d('APP ID'),
                        }),
                      },
                    ],
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label={intl.get('hiam.openApp.model.openApp.appName').d('应用名称')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('appName', {
                    initialValue: appName,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.openApp.model.openApp.appName').d('应用名称'),
                        }),
                      },
                    ],
                  })(
                    <TLEditor
                      label={intl.get('hiam.openApp.model.openApp.appName').d('应用名称')}
                      field="appName"
                      token={_token}
                    />
                  )}
                </FormItem>

                <FormItem
                  label={intl.get('hiam.openApp.model.openApp.subAppId').d('子应用ID')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('subAppId', {
                    initialValue: subAppId,
                  })(<Input />)}
                </FormItem>
                <FormItem
                  label={
                    <span className="ant-form-item-required">
                      {intl.get('hiam.openApp.model.openApp.appleImage').d('应用图片')}
                    </span>
                  }
                  extra={intl
                    .get('hiam.openApp.model.openApp.uploadSupport')
                    .d('上传格式：*.png;*.jpeg')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  <Upload
                    accept=".jpeg,.png"
                    fileType="image/jpeg;image/png"
                    single
                    fileList={fileList}
                    bucketName={BKT_PUBLIC}
                    bucketDirectory="hiam01"
                    onUploadSuccess={this.onUploadSuccess}
                  />
                </FormItem>
                <FormItem wrapperCol={{ span: 10, offset: 6 }}>
                  {getFieldDecorator('appImage', {
                    rules: [
                      {
                        validator: (rule, value, callback) => {
                          if (!value) {
                            callback(
                              new Error(
                                intl.get('hzero.common.validation.notNull', {
                                  name: intl
                                    .get('hiam.openApp.model.openApp.appImage')
                                    .d('应用图片'),
                                })
                              )
                            );
                          } else {
                            callback();
                          }
                        },
                      },
                    ],
                    initialValue: appImage,
                  })(<span />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.openApp.model.openApp.channel').d('登录渠道')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('channel', {
                    initialValue: channel,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.openApp.model.openApp.channel').d('登录渠道'),
                        }),
                      },
                    ],
                  })(
                    <Select disabled={!!openAppId} allowClear>
                      {channelTypes.map((item) => (
                        <Select.Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  label={intl.get('hiam.openApp.model.openApp.appKey').d('APP Key')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('appKey', {
                    initialValue: appKey,
                    rules: [
                      {
                        required: !appId,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.openApp.model.openApp.appKey').d('APP Key'),
                        }),
                      },
                    ],
                  })(
                    <Input
                      placeholder={
                        appId && intl.get('hiam.openApp.view.validation.notChange').d('未更改')
                      }
                    />
                  )}
                </FormItem>

                <FormItem
                  label={intl.get('hiam.openApp.model.openApp.orderSeq').d('序号')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('orderSeq', {
                    initialValue: orderSeq,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.openApp.model.openApp.orderSeq').d('序号'),
                        }),
                      },
                    ],
                  })(<InputNumber min={0} />)}
                </FormItem>
                <FormItem
                  label={intl.get('hiam.openApp.model.openApp.scope').d('授权列表')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('scope', {
                    rules: [
                      {
                        max: 240,
                        message: intl.get('hzero.common.validation.max', {
                          max: 240,
                        }),
                      },
                    ],
                    initialValue: scope,
                  })(<Input />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
