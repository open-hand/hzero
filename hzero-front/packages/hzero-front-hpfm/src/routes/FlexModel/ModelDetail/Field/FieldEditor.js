import React, { Component } from 'react';
import { Drawer, Button, Form, Input, Select, Checkbox, InputNumber } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';
import isNil from 'lodash/isNil';
import { getSingleTenantValueCode } from '@/utils/constConfig';
import notification from 'utils/notification';
import intl from 'utils/intl';
import Lov from 'components/Lov';

import styles from '../../style/index.less';

const FormItem = Form.Item;
const { Option } = Select;
const moreOptionFields = [
  'bucketName',
  'bucketDirectory',
  'dateFormat',
  'numberDecimal',
  'numberMin',
  'numberMax',
  'selectSourceCode',
  'sourceCode',
  'switchValue',
  'textMinLength',
  'textMaxLength',
  'textAreaMaxLine',
  'linkTitle',
  'linkHref',
  'linkNewWindow',
];

@Form.create({ fieldNameProp: null })
@connect(({ loading = {} }) => ({
  saveFieldLoading: loading.effects['flexModel/saveField'],
}))
export default class FieldEditor extends Component {
  @Bind()
  saveField() {
    const { form, handleEdit, dispatch, data } = this.props;
    const { objectVersionNumber, modelId, fieldId, modelFieldWidget = {} } = data;
    form.validateFields((err, values) => {
      if (!err) {
        const params = {
          ...values,
          objectVersionNumber,
          fieldId,
          modelId,
        };
        // 组件高级选项如果不为空，需要传id, objectVersionNumber
        if (!isNil(modelFieldWidget)) {
          moreOptionFields.forEach(item => {
            modelFieldWidget[item] = null;
          });
          params.modelFieldWidget = {
            ...modelFieldWidget,
            ...values.modelFieldWidget,
            id: modelFieldWidget.id,
            objectVersionNumber: modelFieldWidget.objectVersionNumber,
          };
        }
        params.modelFieldWidget.fieldId = fieldId;
        dispatch({
          type: 'flexModel/saveField',
          params,
        }).then(res => {
          if (res) {
            notification.success();
            handleEdit();
          }
        });
      }
    });
  }

  @Bind()
  checkTextLengthMin(rule, value, callback) {
    const { form } = this.props;
    const max = form.getFieldValue('modelFieldWidget.textMaxLength');
    if (!isNil(value) && !isNil(max) && value > max) {
      callback(
        intl
          .get('hpfm.flexModelDetail.model.flexModelDetail.textMinNotBigMax')
          .d('文本最小长度不能大于最大长度')
      );
    } else {
      callback();
    }
  }

  @Bind()
  checkTextLengthMax(rule, value, callback) {
    const { form } = this.props;
    const min = form.getFieldValue('modelFieldWidget.textMinLength');
    if (!isNil(value) && !isNil(min) && value < min) {
      callback(
        intl
          .get('hpfm.flexModelDetail.model.flexModelDetail.textMaxNotLowMin')
          .d('文本最大长度不能小于最小长度')
      );
    } else {
      callback();
    }
  }

  @Bind()
  checkNumberMin(rule, value, callback) {
    const { form } = this.props;
    const max = form.getFieldValue('modelFieldWidget.numberMax');
    if (!isNil(value) && !isNil(max) && value > max) {
      callback(
        intl
          .get('hpfm.flexModelDetail.model.flexModelDetail.minNotBiggerMax')
          .d('最小值不能大于最大值')
      );
    } else {
      callback();
    }
  }

  @Bind()
  checkNumberMax(rule, value, callback) {
    const { form } = this.props;
    const min = form.getFieldValue('modelFieldWidget.numberMin');
    if (!isNil(value) && !isNil(min) && value < min) {
      callback(
        intl
          .get('hpfm.flexModelDetail.model.flexModelDetail.maxNotLowerMin')
          .d('最大值不能小于最小值')
      );
    } else {
      callback();
    }
  }

  @Bind()
  clearOtherOption(component = '') {
    if (component === 'SELECT' || component === 'LOV') {
      this.props.form.setFieldsValue({ 'modelFieldWidget.sourceCode': '' });
    }
  }

  @Bind()
  renderOtherOptions() {
    const {
      data: { modelFieldWidget = {} },
      form,
      dateFormatOptions = [],
    } = this.props;
    const {
      sourceCode,
      textMinLength,
      textMaxLength,
      textAreaMaxLine,
      dateFormat,
      numberDecimal,
      numberMin,
      numberMax,
      fieldCategory,
      switchValue,
      bucketName,
      bucketDirectory,
      linkTitle,
      linkHref,
      linkNewWindow,
    } = modelFieldWidget || {};
    const disabledFlag = fieldCategory === 'FLX';
    const { getFieldValue, getFieldDecorator } = form;
    const component = getFieldValue('modelFieldWidget.fieldWidget');
    let moreOptions = null;
    if (component === 'INPUT') {
      moreOptions = (
        <>
          <FormItem
            label={intl
              .get('hpfm.flexModelDetail.model.flexModelDetail.textMinLength')
              .d('文本最小长度')}
          >
            {getFieldDecorator('modelFieldWidget.textMinLength', {
              initialValue: textMinLength,
              rules: [
                {
                  validator: this.checkTextLengthMin,
                },
              ],
            })(<InputNumber min={0} max={2000} disabled={disabledFlag} />)}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.flexModelDetail.model.flexModelDetail.textMaxLength')
              .d('文本最大长度')}
          >
            {getFieldDecorator('modelFieldWidget.textMaxLength', {
              initialValue: textMaxLength,
              rules: [
                {
                  validator: this.checkTextLengthMax,
                },
              ],
            })(<InputNumber min={1} max={2000} disabled={disabledFlag} />)}
          </FormItem>
        </>
      );
    } else if (component === 'TEXT_AREA') {
      moreOptions = (
        <>
          <FormItem
            label={intl
              .get('hpfm.flexModelDetail.model.flexModelDetail.textMinLength')
              .d('文本最小长度')}
          >
            {getFieldDecorator('modelFieldWidget.textMinLength', {
              initialValue: textMinLength,
              rules: [
                {
                  validator: this.checkTextLengthMin,
                },
              ],
            })(<InputNumber min={0} max={2000} disabled={disabledFlag} />)}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.flexModelDetail.model.flexModelDetail.textMaxLength')
              .d('文本最大长度')}
          >
            {getFieldDecorator('modelFieldWidget.textMaxLength', {
              initialValue: textMaxLength,
              rules: [
                {
                  validator: this.checkTextLengthMax,
                },
              ],
            })(<InputNumber min={1} max={2000} disabled={disabledFlag} />)}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.flexModelDetail.model.flexModelDetail.textAreaMaxLine')
              .d('文本最大行数')}
          >
            {getFieldDecorator('modelFieldWidget.textAreaMaxLine', {
              initialValue: textAreaMaxLine,
            })(<InputNumber min={1} max={2000} disabled={disabledFlag} />)}
          </FormItem>
        </>
      );
    } else if (component === 'SELECT' || component === 'LOV') {
      moreOptions = (
        <FormItem
          label={intl
            .get('hpfm.flexModelDetail.model.flexModelDetail.sourceCode')
            .d('数据来源值集')}
        >
          {getFieldDecorator('modelFieldWidget.sourceCode', {
            initialValue: sourceCode,
          })(
            <Lov
              disabled={disabledFlag}
              code={getSingleTenantValueCode(
                component === 'SELECT' ? 'HPFM.LOV.LOV_DETAIL_CODE' : 'HPFM.LOV_VIEW'
              )}
              textField="modelFieldWidget.sourceCode"
              lovOptions={{
                displayField: component === 'SELECT' ? 'lovCode' : 'viewCode',
                valueField: component === 'SELECT' ? 'lovCode' : 'viewCode',
              }}
            />
          )}
        </FormItem>
      );
    } else if (component === 'INPUT_NUMBER') {
      moreOptions = (
        <>
          <Form.Item
            label={intl
              .get('hpfm.flexModelDetail.model.flexModelDetail.numberDecimal')
              .d('数据精度')}
          >
            {getFieldDecorator('modelFieldWidget.numberDecimal', {
              initialValue: numberDecimal,
            })(<InputNumber disabled={disabledFlag} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.numberMin').d('最小值')}
          >
            {getFieldDecorator('modelFieldWidget.numberMin', {
              initialValue: numberMin,
              rules: [
                {
                  validator: this.checkNumberMin,
                },
              ],
            })(<InputNumber disabled={disabledFlag} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.numberMax').d('最大值')}
          >
            {getFieldDecorator('modelFieldWidget.numberMax', {
              initialValue: numberMax,
              rules: [
                {
                  validator: this.checkNumberMax,
                },
              ],
            })(<InputNumber disabled={disabledFlag} />)}
          </Form.Item>
        </>
      );
    } else if (component === 'DATE_PICKER') {
      moreOptions = (
        <Form.Item
          label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.dateFormat').d('时间格式')}
        >
          {getFieldDecorator('modelFieldWidget.dateFormat', {
            initialValue: dateFormat,
          })(
            <Select>
              {dateFormatOptions.map(item => (
                <Option value={item.value}>{item.meaning}</Option>
              ))}
            </Select>
          )}
        </Form.Item>
      );
    } else if (component === 'SWITCH') {
      moreOptions = (
        <Form.Item
          label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.switchValue').d('默认布尔值')}
        >
          {getFieldDecorator('modelFieldWidget.switchValue', {
            initialValue: switchValue,
          })(<Input disabled={disabledFlag} />)}
        </Form.Item>
      );
    } else if (component === 'UPLOAD') {
      moreOptions = (
        <>
          <Form.Item
            label={intl
              .get('hpfm.flexModelDetail.model.flexModelDetail.bucketName')
              .d('上传文件名')}
          >
            {getFieldDecorator('modelFieldWidget.bucketName', {
              initialValue: bucketName,
            })(<Input disabled={disabledFlag} />)}
          </Form.Item>
          <Form.Item
            label={intl
              .get('hpfm.flexModelDetail.model.flexModelDetail.bucketDirectory')
              .d('上传文件目录')}
          >
            {getFieldDecorator('modelFieldWidget.bucketDirectory', {
              initialValue: bucketDirectory,
            })(<Input disabled={disabledFlag} />)}
          </Form.Item>
        </>
      );
    } else if (component === 'LINK') {
      moreOptions = (
        <>
          <Form>
            <Form.Item
              label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.linkTitle').d('链接标题')}
            >
              {getFieldDecorator('modelFieldWidget.linkTitle', {
                initialValue: linkTitle,
              })(<Input disabled={disabledFlag} />)}
            </Form.Item>
            <Form.Item
              label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.linkHref').d('链接地址')}
            >
              {getFieldDecorator('modelFieldWidget.linkHref', {
                initialValue: linkHref,
              })(<Input disabled={disabledFlag} />)}
            </Form.Item>
            <Form.Item>
              {getFieldDecorator('modelFieldWidget.linkNewWindow', {
                initialValue: linkNewWindow,
              })(
                <Checkbox disabled={disabledFlag} checkedValue={1} unCheckedValue={0}>
                  {intl
                    .get('hpfm.flexModelDetail.model.flexModelDetail.linkNewWindow')
                    .d('在新窗口中打开')}
                </Checkbox>
              )}
            </Form.Item>
          </Form>
        </>
      );
    }
    if (component && component !== 'TL_EDITOR') {
      return moreOptions;
    }
    return '';
  }

  render() {
    const {
      visible,
      data,
      handleClose,
      saveFieldLoading,
      form: { getFieldDecorator = () => {} },
      fieldTypeOptions = [],
      fieldCategoryOptions = [],
      fieldComponentOptions = [],
    } = this.props;
    const {
      fieldName,
      fieldCode,
      fieldType,
      fieldCategory,
      notNull,
      fieldMultiLang,
      defaultValue,
      modelFieldWidget = {},
    } = data;
    const isEdit = Object.keys(data).length === 0;
    const componentOptions =
      fieldMultiLang === 1
        ? fieldComponentOptions.filter(item => item.value === 'TL_EDITOR')
        : fieldComponentOptions;
    return (
      <Drawer
        title={
          isEdit
            ? intl.get('hpfm.flexModelDetail.view.message.title.addField').d('新建字段')
            : intl.get('hpfm.flexModelDetail.view.message.title.editField').d('编辑字段')
        }
        closable
        onClose={handleClose}
        visible={visible}
        destroyOnClose
        width={400}
      >
        <Form layout="vertical" className={styles['field-editor-form']}>
          <FormItem
            label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.fieldCode').d('字段编码')}
          >
            {getFieldDecorator('fieldCode', {
              initialValue: fieldCode,
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.fieldName').d('字段名称')}
          >
            {getFieldDecorator('fieldName', {
              initialValue: fieldName,
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.flexModelDetail.model.flexModelDetail.fieldName')
                        .d('字段名称'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.flexModelDetail.model.flexModelDetail.fieldName')
                        .d('字段名称')}不能为空`
                    ),
                },
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
            label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.fieldType').d('数据类型')}
          >
            {getFieldDecorator('fieldType', {
              initialValue: fieldType,
            })(
              <Select allowClear disabled>
                {fieldTypeOptions.map(item => (
                  <Option value={item.value}>{item.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>

          <FormItem
            label={intl
              .get('hpfm.flexModelDetail.model.flexModelDetail.fieldCategory')
              .d('字段类型')}
          >
            {getFieldDecorator('fieldCategory', {
              initialValue: fieldCategory,
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.flexModelDetail.model.flexModelDetail.fieldCategory')
                        .d('字段类型'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.flexModelDetail.model.flexModelDetail.fieldCategory')
                        .d('字段类型')}不能为空`
                    ),
                },
              ],
            })(
              <Select
                defaultValue={fieldCategory}
                disabled={fieldCategory === 'FLX' || fieldCategory === 'VIR'}
              >
                {fieldCategoryOptions.map(item => {
                  if (fieldCategory !== 'VIR' && item.value === 'VIR') {
                    return null;
                  }
                  if (fieldCategory !== 'FLX' && item.value === 'FLX') {
                    return null;
                  }
                  return <Option value={item.value}>{item.meaning}</Option>;
                })}
              </Select>
            )}
          </FormItem>
        </Form>
        <Form layout="inline" className={styles['model-field-editor-form-inline']}>
          <FormItem>
            {getFieldDecorator('notNull', {
              initialValue: notNull,
            })(
              <Checkbox
                disabled
                checkedValue={1}
                unCheckedValue={0}
                className={styles['model-field-editor-notNull-checkbox']}
              >
                {intl
                  .get('hpfm.flexModelDetail.model.flexModelDetail.notCanBeNull')
                  .d('不允许为空')}
              </Checkbox>
            )}
          </FormItem>
          <Form layout="inline" className={styles['model-field-editor-form-inline']}>
            <FormItem>
              {getFieldDecorator('fieldMultiLang', {
                initialValue: fieldMultiLang,
              })(
                <Checkbox
                  disabled
                  checkedValue={1}
                  unCheckedValue={0}
                  className={styles['model-field-editor-notNull-checkbox']}
                >
                  {intl
                    .get('hpfm.flexModelDetail.model.flexModelDetail.MultileField')
                    .d('多语言字段')}
                </Checkbox>
              )}
            </FormItem>
          </Form>
        </Form>
        <Form
          layout="vertical"
          style={{ marginBottom: '50px' }}
          className={styles['field-editor-form']}
        >
          <FormItem
            label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.defaultValue').d('默认值')}
          >
            {getFieldDecorator('defaultValue', {
              initialValue: defaultValue,
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.fieldWidget').d('默认组件')}
          >
            {getFieldDecorator('modelFieldWidget.fieldWidget', {
              initialValue: (modelFieldWidget || {}).fieldWidget,
            })(
              <Select
                allowClear
                defaultValue={(modelFieldWidget || {}).fieldWidget}
                disabled={fieldCategory === 'FLX'}
                onChange={this.clearOtherOption}
              >
                {componentOptions.map(item => (
                  <Option value={item.value}>{item.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
          {this.renderOtherOptions()}
        </Form>
        <div className={styles['model-bottom-button']}>
          <Button onClick={handleClose} style={{ marginRight: 8 }} disabled={saveFieldLoading}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button type="primary" loading={saveFieldLoading} onClick={this.saveField}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
