/**
 * 二级域名模板分配--模板配置
 * @date: 2019-7-11
 * @author: XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Input, Select, Modal, Spin, Row, Col, InputNumber } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Upload from 'components/Upload/UploadButton';
import RichTextEditor from 'components/RichTextEditor';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { BKT_PUBLIC } from 'utils/config';

const FormItem = Form.Item;
const { Option } = Select;
const { TextArea } = Input;

@Form.create({ fieldNameProp: null })
export default class TemplateConfigDrawer extends Component {
  constructor(props) {
    super(props);
    this.staticTextEditor = React.createRef();
    const { initData } = this.props;
    const { configValue } = initData;
    this.state = {
      configValueVisible: false, // 配置值是否显示
      prevContent: configValue,
    };
  }

  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { initData } = nextProps;
    const { configValue } = initData;
    if ((configValue || '') !== prevState.prevContent) {
      return {
        prevContent: configValue || '',
      };
    }
    return null;
  }

  // 点击确定回调
  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
        this.setState({
          prevContent: '',
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
        configValue: file.response,
      });
    }
  }

  // 删除文件成功
  @Bind()
  onCancelSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        configValue: '',
      });
    }
  }

  render() {
    const {
      initData,
      templateConfigsTypeList,
      form,
      modalVisible,
      loading,
      onCancel,
      initLoading,
      fileList = [],
    } = this.props;
    const { configId, configCode, remark, configTypeCode, configValue, orderSeq, link } = initData;
    const { getFieldDecorator } = form;
    const { configValueVisible, prevContent } = this.state;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={
          configId !== undefined
            ? intl.get('hiam.message.view.message.edit').d('编辑模板配置')
            : intl.get('hiam.message.view.message.create').d('新建模板配置')
        }
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOk}
        width="1000px"
      >
        <Spin spinning={initLoading}>
          <Form>
            <Row>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ssoConfig.model.ssoConfig.configCode').d('配置编码')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('configCode', {
                    initialValue: configCode,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.ssoConfig.model.ssoConfig.configCode').d('配置编码'),
                        }),
                      },
                      {
                        max: 60,
                        message: intl.get('hzero.common.validation.max', {
                          max: 60,
                        }),
                      },
                      {
                        pattern: CODE_UPPER,
                        message: intl
                          .get('hzero.common.validation.codeUpper')
                          .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                      },
                    ],
                  })(
                    <Input
                      disabled={configId !== undefined}
                      trim
                      inputChinese={false}
                      typeCase="upper"
                    />
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ssoConfig.model.ssoConfig.orderSeq').d('排序号')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('orderSeq', {
                    initialValue: orderSeq || 1,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hiam.openApp.model.openApp.orderSeq').d('序号'),
                        }),
                      },
                    ],
                  })(<InputNumber min={1} style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ssoConfig.model.ssoConfig.configTypeCode').d('配置类型')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('configTypeCode', {
                    initialValue: configTypeCode,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hiam.ssoConfig.model.ssoConfig.configTypeCode')
                            .d('配置类型'),
                        }),
                      },
                    ],
                  })(
                    <Select disabled={configId !== undefined}>
                      {templateConfigsTypeList.map(item => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.ssoConfig.model.ssoConfig.remark').d('备注信息')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('remark', {
                    initialValue: remark,
                    rules: [
                      {
                        max: 1200,
                        message: intl.get('hzero.common.validation.max', {
                          max: 1200,
                        }),
                      },
                    ],
                  })(<TextArea rows={4} />)}
                </FormItem>
              </Col>
            </Row>
            {form.getFieldValue('configTypeCode') === 'TEXT' &&
              (!configValueVisible && (
                <Row>
                  <Col>
                    <FormItem
                      required
                      label={intl.get('hiam.ssoConfig.model.ssoConfig.configValue').d('配置值')}
                      labelCol={{ span: 3 }}
                      wrapperCol={{ span: 20 }}
                    >
                      <RichTextEditor
                        key={configId === undefined ? 'new' : configValue}
                        content={configId === undefined ? undefined : prevContent}
                        ref={this.staticTextEditor}
                      />
                    </FormItem>
                  </Col>
                </Row>
              ))}
            {form.getFieldValue('configTypeCode') === 'FILE' &&
              (!configValueVisible && (
                <Row>
                  <Col {...FORM_COL_2_LAYOUT}>
                    <FormItem
                      label={intl.get('hiam.ssoConfig.model.ssoConfig.configValue').d('配置值')}
                      {...MODAL_FORM_ITEM_LAYOUT}
                      // eslint-disable-next-line
                      required={true}
                      extra={intl
                        .get('hiam.ssoConfig.model.ssoConfig.uploadSupport')
                        .d('上传格式：*.png;*.jpeg')}
                    >
                      <Upload
                        accept="image/jpeg,image/png"
                        single
                        bucketName={BKT_PUBLIC}
                        bucketDirectory="hpfm04"
                        fileList={fileList}
                        onUploadSuccess={this.onUploadSuccess}
                        onRemove={this.onCancelSuccess}
                      />
                    </FormItem>
                    <FormItem wrapperCol={{ span: 15, offset: 6 }}>
                      {getFieldDecorator('configValue', {
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hiam.ssoConfig.model.ssoConfig.configValue')
                                .d('配置值'),
                            }),
                          },
                        ],
                        initialValue: configValue,
                      })(<div />)}
                    </FormItem>
                  </Col>
                  <Col {...FORM_COL_2_LAYOUT}>
                    <FormItem
                      label={intl.get('hiam.ssoConfig.model.ssoConfig.link').d('链接')}
                      {...MODAL_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('link', {
                        initialValue: link,
                        rules: [
                          {
                            max: 480,
                            message: intl.get('hzero.common.validation.max', {
                              max: 480,
                            }),
                          },
                        ],
                      })(<Input />)}
                    </FormItem>
                  </Col>
                </Row>
              ))}
          </Form>
        </Spin>
      </Modal>
    );
  }
}
