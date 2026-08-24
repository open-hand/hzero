/**
 * 动态表单配置行
 * @author liang.xiong@hand-china.com
 * @date 2019-07-15
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, InputNumber, Modal, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined, isEqual } from 'lodash';
import intl from 'utils/intl';
import Lov from 'components/Lov';
import { CODE } from 'utils/regExp';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';

const { Option } = Select;

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

const keyCodeConstant = {
  LOV: 'LOV',
  LOV_VIEW: 'LOV_VIEW',
};

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.PureComponent {
  constructor(props) {
    super(props);
    const { initData } = this.props;
    this.state = {
      fieldDisabled:
        isEqual(initData.itemTypeCode, keyCodeConstant.LOV) ||
        isEqual(initData.itemTypeCode, keyCodeConstant.LOV_VIEW),
    };
  }

  /**
   * 确定
   */
  @Bind()
  handleOk() {
    const { form, onOk } = this.props;
    const { fieldDisabled } = this.state;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        if (!fieldDisabled) {
          const { valueSet, ...otherFields } = fieldsValue;
          onOk(otherFields);
        } else {
          onOk(fieldsValue);
        }
      }
    });
  }

  @Bind()
  handleChangeKeyType(value) {
    if (keyCodeConstant[value]) {
      this.setState({
        fieldDisabled: true,
      });
    } else {
      this.setState({
        fieldDisabled: false,
      });
    }
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form,
      anchor,
      initData,
      title,
      confirmLoading,
      onCancel,
      modalVisible,
      keyTypeList,
    } = this.props;
    const { fieldDisabled } = this.state;
    const { getFieldDecorator, getFieldValue } = form;
    const isEditing = !isUndefined(initData.formLineId);
    console.log('==========isEditing', isEditing);
    return (
      <Modal
        destroyOnClose
        title={title}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={modalVisible}
        confirmLoading={confirmLoading}
        onCancel={onCancel}
        onOk={this.handleOk}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <Form>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.line.itemCode').d('配置编码')}
          >
            {getFieldDecorator('itemCode', {
              initialValue: initData.itemCode,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.dynamicForm.line.itemCode').d('配置编码'),
                  }),
                },
                {
                  pattern: CODE,
                  message: intl
                    .get('hzero.common.validation.code')
                    .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(<Input disabled={isEditing} inputChinese={false} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.line.itemName').d('配置名称')}
          >
            {getFieldDecorator('itemName', {
              initialValue: initData.itemName,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.dynamicForm.line.itemName').d('配置名称'),
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
                label={intl.get('hpfm.dynamicForm.line.itemName').d('配置名称')}
                field="itemName"
                token={initData._token}
              />
            )}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.line.keyType').d('配置类型')}
          >
            {getFieldDecorator('itemTypeCode', {
              initialValue: initData.itemTypeCode,
              rules: [
                {
                  type: 'string',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.dynamicForm.line.itemTypeCode').d('配置类型'),
                  }),
                },
              ],
            })(
              <Select disabled={isEditing} onChange={this.handleChangeKeyType}>
                {keyTypeList.map((itemTypeCode) => (
                  <Option key={itemTypeCode.value} value={itemTypeCode.value}>
                    {itemTypeCode.meaning}
                  </Option>
                ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item {...formLayout} label={intl.get('hpfm.dynamicForm.line.orderSeq').d('排序号')}>
            {getFieldDecorator('orderSeq', {
              initialValue: initData.orderSeq,
              rules: [
                {
                  type: 'number',
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.dynamicForm.line.orderSeq').d('排序号'),
                  }),
                },
              ],
            })(<InputNumber min={1} precision={0} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.line.defaultValue').d('默认值')}
          >
            {getFieldDecorator('defaultValue', {
              initialValue: initData.defaultValue,
              rules: [
                {
                  max: 255,
                  message: intl.get('hzero.common.validation.max', {
                    max: 255,
                  }),
                },
              ],
            })(<Input disabled={fieldDisabled} />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.line.keyDescription').d('配置说明')}
          >
            {getFieldDecorator('itemDescription', {
              initialValue: initData.itemDescription,
              rules: [
                {
                  max: 480,
                  message: intl.get('hzero.common.validation.max', {
                    max: 480,
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.dynamicForm.line.keyDescription').d('配置说明')}
                field="itemDescription"
                token={initData._token}
              />
            )}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.line.valueConstraint').d('值约束')}
          >
            {getFieldDecorator('valueConstraint', {
              initialValue: initData.valueConstraint,
              rules: [
                {
                  max: 480,
                  message: intl.get('hzero.common.validation.lengthExceeded').d('长度超过480'),
                },
                {
                  pattern: /^[/]{1}.*(\$?[/|/g|/gi|/i|/ig])$/g,
                  message: intl
                    .get('hzero.common.validation.correctRegular')
                    .d('请输入正确的正则表达式'),
                },
              ],
            })(<Input disabled={fieldDisabled} />)}
          </Form.Item>
          {isEditing ? (
            <Form.Item
              {...formLayout}
              label={intl.get('hpfm.dynamicForm.line.valueSet').d('值集/视图编码')}
            >
              {getFieldDecorator('valueSet', {
                initialValue: initData.valueSet,
              })(
                <Lov
                  code={
                    isEqual(getFieldValue('itemTypeCode'), keyCodeConstant.LOV)
                      ? 'HPFM.SITE.LOV_IDP'
                      : 'HPFM.LOV_VIEW.CODE'
                  }
                  lovOptions={{
                    valueField: isEqual(getFieldValue('itemTypeCode'), keyCodeConstant.LOV)
                      ? 'lovCode'
                      : 'viewCode',
                    displayField: isEqual(getFieldValue('itemTypeCode'), keyCodeConstant.LOV)
                      ? 'lovCode'
                      : 'viewCode',
                  }}
                  textValue={initData.valueSet}
                  disabled
                />
              )}
            </Form.Item>
          ) : (
            <Form.Item
              {...formLayout}
              label={intl.get('hpfm.dynamicForm.line.valueSet').d('值集/视图编码')}
            >
              {getFieldDecorator('valueSet', {
                initialValue: initData.valueSet,
                rules: [
                  {
                    required: getFieldValue('itemTypeCode') ? fieldDisabled : false,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.dynamicForm.line.valueSet').d('值集/视图编码'),
                    }),
                  },
                ],
              })(
                <Lov
                  code={
                    isEqual(getFieldValue('itemTypeCode'), keyCodeConstant.LOV)
                      ? 'HPFM.SITE.LOV_IDP'
                      : 'HPFM.LOV_VIEW.CODE'
                  }
                  lovOptions={{
                    valueField: isEqual(getFieldValue('itemTypeCode'), keyCodeConstant.LOV)
                      ? 'lovCode'
                      : 'viewCode',
                    displayField: isEqual(getFieldValue('itemTypeCode'), keyCodeConstant.LOV)
                      ? 'lovCode'
                      : 'viewCode',
                  }}
                  textValue={initData.valueSet}
                  disabled={getFieldValue('itemTypeCode') ? !fieldDisabled : false}
                />
              )}
            </Form.Item>
          )}
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.line.requiredFlag').d('是否必输')}
          >
            {getFieldDecorator('requiredFlag', {
              initialValue: isEmpty(initData) ? 1 : initData.requiredFlag,
            })(<Switch />)}
          </Form.Item>
          <Form.Item {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
            {getFieldDecorator('enabledFlag', {
              initialValue: isEmpty(initData) ? 1 : initData.enabledFlag,
            })(<Switch />)}
          </Form.Item>
          <Form.Item
            {...formLayout}
            label={intl.get('hpfm.dynamicForm.line.isAllowUpdate').d('是否允许更新')}
          >
            {getFieldDecorator('updatableFlag', {
              initialValue: isEmpty(initData) ? 1 : initData.updatableFlag,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
