/**
 * ParamsDrawer - 创建/编辑参数弹窗
 * @date: 2019/5/20
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Input, Modal, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import Switch from 'components/Switch';
import Upload from 'components/Upload/UploadButton';
import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { BKT_HITF } from 'utils/config';

const FormItem = Form.Item;
const { Option } = Select;

/**
 * 创建/编辑参数弹窗
 * @extends {Component} - React.PureComponent
 * @reactProps {boolean} visible - 参数弹窗是否可见
 * @reactProps {string} actionType - HTTP操作类型(REQ/RESP)
 * @reactProps {string} mimeType - BODY的MIME类型
 * @reactProps {array} paramValueType - BODY参数值的类型值集
 * @reactProps {boolean} confirmLoading - 新建/编辑保存加载标志
 * @reactProps {obejct} currentParamData - 当前参数数据
 * @reactProps {string} paramType - 参数类型(HEADER/GET/PATH/BODY)
 * @reactProps {array} requestHeaderTypes - 请求头参数名值集
 * @reactProps {Function} onSave - 保存参数
 * @reactProps {Function} onCancel - 关闭弹窗
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class ParamsDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.scrollbars = React.createRef();
    this.state = {
      isEmptyFile: false,
    };
  }

  /**
   * 关闭创建/编辑参数信息侧滑
   */
  @Bind()
  handleClose() {
    const { onCancel } = this.props;
    onCancel();
    this.resetState();
  }

  @Bind()
  resetState() {
    this.setState({ isEmptyFile: false });
  }

  /**
   * 保存参数信息
   */
  @Bind()
  handleOk() {
    const { form, onSave, currentParamData, paramType, actionType, mimeType } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        let totalValues = {
          requiredFlag: 0,
          paramType,
          actionType,
          ...values,
        };
        if (mimeType) {
          totalValues.mimeType = mimeType;
        }
        // 新建
        if (isEmpty(currentParamData)) {
          // 校验通过，进行保存操作
          onSave({ values: totalValues, flag: 'create', cb: this.resetState });
          // 编辑
        } else {
          const { objectVersionNumber, _token, paramId } = currentParamData;
          totalValues = {
            objectVersionNumber,
            paramId,
            _token,
            ...totalValues,
          };
          onSave({ values: totalValues, flag: 'edit', cb: this.resetState });
        }
      }
    });
  }

  // 上传成功
  @Bind()
  onUploadSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        defaultValueLongtext: file.response,
      });
    }
  }

  // 删除成功
  @Bind()
  onCancelSuccess(file) {
    const { form } = this.props;
    if (file) {
      form.setFieldsValue({
        defaultValueLongtext: '',
      });
    }
  }

  /**
   * 切换类型
   */
  @Bind()
  handleChangeType() {
    const { setFieldsValue = (e) => e } = this.props.form;
    this.setState({ isEmptyFile: true });
    setFieldsValue({ defaultValueLongtext: '' });
  }

  /**
   * 渲然默认值表单项
   */
  @Bind()
  renderDefaultValueItem() {
    const {
      paramType,
      mimeType,
      currentParamData,
      form: { getFieldDecorator, getFieldValue },
    } = this.props;
    const isFileType = mimeType ? getFieldValue('paramValueType') === 'FILE' : false;
    const isEdit = !isEmpty(currentParamData);
    const { isEmptyFile } = this.state;
    const isShowBodyDefaultValue =
      paramType === 'BODY' && !['xml', 'json', 'raw'].includes(mimeType);
    const defaultValueLongtextFileProps = isFileType
      ? {
          // labelCol: { span: 0 },
          wrapperCol: {
            span: 0,
            offset: 7,
          },
        }
      : {
          label: intl.get('hitf.services.model.services.defaultValue').d('默认值'),
          labelCol: { span: 6 },
          wrapperCol: {
            span: 16,
            offset: 0,
          },
        };
    let fileList = [];
    if (isEdit && !isEmptyFile && currentParamData.defaultValueLongtext) {
      fileList = [
        {
          uid: '-1',
          status: 'done',
          name: currentParamData.defaultValueLongtext,
          url: currentParamData.defaultValueLongtext,
        },
      ];
    }
    if (paramType !== 'BODY') {
      return (
        <FormItem
          label={intl.get('hitf.services.model.services.defaultValue').d('默认值')}
          {...MODAL_FORM_ITEM_LAYOUT}
        >
          {getFieldDecorator('defaultValue', {
            rules: [
              {
                required: paramType === 'HEADER',
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hitf.services.model.services.defaultValue').d('默认值'),
                }),
              },
              {
                max: 128,
                message: intl.get('hzero.common.validation.max', {
                  max: 128,
                }),
              },
            ],
            initialValue: currentParamData.defaultValue,
          })(<Input />)}
        </FormItem>
      );
    }
    if (isFileType) {
      return (
        <FormItem
          label={intl.get('hitf.services.model.services.defaultValue').d('默认值')}
          {...MODAL_FORM_ITEM_LAYOUT}
        >
          <Upload
            single
            bucketName={BKT_HITF}
            bucketDirectory="hitf01"
            onUploadSuccess={this.onUploadSuccess}
            fileList={fileList}
            onRemove={this.onCancelSuccess}
          />
        </FormItem>
      );
    }
    if (isShowBodyDefaultValue) {
      return (
        <FormItem {...defaultValueLongtextFileProps}>
          {getFieldDecorator('defaultValueLongtext', {
            initialValue: currentParamData.defaultValueLongtext,
          })(isFileType ? <div /> : <Input />)}
        </FormItem>
      );
    }
  }

  render() {
    const {
      visible,
      actionType,
      mimeType,
      paramValueTypes,
      form: { getFieldDecorator },
      confirmLoading,
      currentParamData,
      paramType,
      requestHeaderTypes,
    } = this.props;
    const isEdit = !isEmpty(currentParamData);
    const handleType = isEdit ? 'isEdit' : 'isCreate';
    const titleMap = {
      REQ: {
        HEADER: {
          isCreate: intl.get('hitf.document.view.title.create.requestHeader').d('新建请求头部参数'),
          isEdit: intl.get('hitf.document.view.title.edit.requestHeader').d('编辑请求头部参数'),
        },
        GET: {
          isCreate: intl.get('hitf.document.view.title.create.queryParams').d('新建GET/URL参数'),
          isEdit: intl.get('hitf.document.view.title.edit.queryParams').d('编辑GET/URL参数'),
        },
        PATH: {
          isCreate: intl.get('hitf.document.view.title.create.pathParams').d('新建路径参数'),
          isEdit: intl.get('hitf.document.view.title.edit.pathParams').d('编辑路径参数'),
        },
        BODY: {
          isCreate: intl.get('hitf.document.view.title.create.bodyParams').d('新建BODY参数'),
          isEdit: intl.get('hitf.document.view.title.edit.bodyParams').d('编辑BODY参数'),
        },
      },
      RESP: {
        HEADER: {
          isCreate: intl.get('hitf.document.view.title.create.respHeader').d('新建响应头部参数'),
          isEdit: intl.get('hitf.document.view.title.edit.respHeader').d('编辑响应头部参数'),
        },
        BODY: {
          isCreate: intl.get('hitf.document.view.title.create.respParams').d('新建响应结果参数'),
          isEdit: intl.get('hitf.document.view.title.edit.respParams').d('编辑响应结果参数'),
        },
      },
    };
    const title = titleMap[actionType][paramType][handleType];
    const formDataTypes = paramValueTypes.filter(
      (item) => item.value === 'STRING' || item.value === 'FILE'
    );
    const urlencodedTypes = paramValueTypes.filter((item) => item.value === 'STRING');
    let types = paramValueTypes;
    if (mimeType === 'multipart/form-data') {
      types = formDataTypes;
    } else if (mimeType === 'application/x-www-form-urlencoded') {
      types = urlencodedTypes;
    }

    return (
      <Modal
        destroyOnClose
        title={title}
        visible={visible}
        onCancel={this.handleClose}
        onOk={this.handleOk}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        confirmLoading={confirmLoading}
      >
        <Form>
          <FormItem
            label={intl.get('hitf.services.model.services.paramName').d('参数名')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('paramName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hitf.services.model.services.paramName').d('参数名'),
                  }),
                },
                {
                  max: 128,
                  message: intl.get('hzero.common.validation.max', {
                    max: 128,
                  }),
                },
              ],
              initialValue: isEdit ? currentParamData.paramName : undefined,
            })(
              paramType === 'HEADER' ? (
                <Select allowClear>
                  {requestHeaderTypes.length &&
                    requestHeaderTypes.map(({ value, meaning }) => (
                      <Option key={value} value={value}>
                        {meaning}
                      </Option>
                    ))}
                </Select>
              ) : (
                <Input />
              )
            )}
          </FormItem>
          {mimeType && (
            <FormItem
              label={intl.get('hitf.services.model.services.paramValueType').d('类型')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('paramValueType', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.paramValueType').d('类型'),
                    }),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
                initialValue:
                  mimeType === 'application/x-www-form-urlencoded'
                    ? 'STRING'
                    : currentParamData.paramValueType,
              })(
                <Select allowClear onChange={this.handleChangeType}>
                  {types.length &&
                    types.map(({ value }) => (
                      <Option key={value} value={value}>
                        {value}
                      </Option>
                    ))}
                </Select>
              )}
            </FormItem>
          )}
          {paramType !== 'HEADER' && (
            <>
              <FormItem
                label={intl.get('hitf.services.model.services.requiredFlag').d('是否必填')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('requiredFlag', {
                  initialValue: isEdit ? currentParamData.requiredFlag : 1,
                })(<Switch />)}
              </FormItem>
              <FormItem
                label={intl.get('hitf.services.model.services.formatRegexp').d('格式限制')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('formatRegexp', {
                  initialValue: currentParamData.formatRegexp,
                  rules: [
                    {
                      max: 30,
                      message: intl.get('hzero.common.validation.max', {
                        max: 30,
                      }),
                    },
                  ],
                })(<Input />)}
              </FormItem>
              <FormItem
                label={intl.get('hitf.services.model.services.remark').d('说明')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('remark', {
                  initialValue: currentParamData.remark,
                })(<Input />)}
              </FormItem>
              <FormItem
                label={intl.get('hitf.services.model.services.demo').d('示例')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('valueDemo', {
                  initialValue: currentParamData.valueDemo,
                })(<Input />)}
              </FormItem>
            </>
          )}
          {this.renderDefaultValueItem()}
        </Form>
      </Modal>
    );
  }
}
