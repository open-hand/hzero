import React from 'react';
import { Form, Popconfirm, Icon, Input, Switch, InputNumber, DatePicker, Checkbox } from 'hzero-ui';
import intl from 'utils/intl';
import { connect } from 'dva';
import moment from 'moment';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { Bind } from 'lodash-decorators';
import { getContextParams } from 'components/Customize/hzero/customizeTool';
import { FlexSelect } from 'components/Customize/hzero/FlexComponents';
import LovMulti from 'components/Customize/hzero/LovMulti';
import Lov from 'components/Lov';
import { getEditTableData } from 'utils/utils';

import BaseCondition from './BaseCondition';

const FormItem = Form.Item;

@connect(({ configCustomize }) => {
  const { codes, defaultValueProps, defaultConList, defaultValidList } = configCustomize;
  return {
    codes,
    headerProps: defaultValueProps,
    conditionList: defaultConList,
    validatorList: defaultValidList,
  };
})
@Form.create({ fieldNameProp: null })
export default class DefaultValueModal extends BaseCondition {
  getDefaultValueComponent(record) {
    const { extForm, paramList } = this.props;
    const {
      fieldWidget,
      multipleFlag,
      sourceCode,
      format = DEFAULT_DATE_FORMAT,
    } = extForm.getFieldsValue();
    switch (fieldWidget) {
      case 'RADIO_GROUP':
      case 'SELECT':
        return (
          <FormItem>
            {record.$form.getFieldDecorator('value', {
              initialValue: record.value,
            })(
              <FlexSelect
                lovCode={sourceCode}
                fieldCode="value"
                multipleFlag={multipleFlag}
                params={getContextParams(paramList, { isConfig: true })}
              />
            )}
          </FormItem>
        );
      case 'LOV':
        return (
          <FormItem>
            {record.$form.getFieldDecorator('value', {
              initialValue: record.value,
            })(
              multipleFlag === 1 ? (
                <LovMulti
                  code={sourceCode}
                  queryParams={getContextParams(paramList, { isConfig: true })}
                  translateData={record.valueMeaning || {}}
                />
              ) : (
                <Lov
                  code={sourceCode}
                  queryParams={getContextParams(paramList, { isConfig: true })}
                  textValue={record.valueMeaning || record.value}
                />
              )
            )}
          </FormItem>
        );
      case 'INPUT_NUMBER':
        return (
          <FormItem>
            {record.$form.getFieldDecorator('value', {
              initialValue: record.value,
            })(<InputNumber />)}
          </FormItem>
        );
      case 'DATE_PICKER':
        return (
          <FormItem>
            {record.$form.getFieldDecorator('value', {
              initialValue: record.value ? moment(record.value) : '',
              getValueProps: (dateStr) => ({
                value: dateStr ? moment(dateStr, format) : dateStr,
              }),
              getValueFromEvent(e) {
                if (!e || !e.target) {
                  return e && e.format ? e.format(format) : e;
                }
                const { target } = e;
                return target.type === 'checkbox' ? target.checked : target.value;
              },
            })(<DatePicker format={format} showTime style={{ width: '100%' }} />)}
          </FormItem>
        );
      case 'CHECKBOX':
        return (
          <FormItem>
            {record.$form.getFieldDecorator('value', {
              initialValue: Number(record.value || 0),
            })(<Checkbox checkedValue={1} unCheckedValue={0} />)}
          </FormItem>
        );
      case 'SWITCH':
        return (
          <FormItem>
            {record.$form.getFieldDecorator('value', {
              initialValue: Number(record.value || 0),
            })(<Switch checkedValue={1} unCheckedValue={0} />)}
          </FormItem>
        );
      default:
        return (
          <FormItem>
            {record.$form.getFieldDecorator('value', {
              initialValue: record.value,
            })(<Input trim />)}
          </FormItem>
        );
    }
  }

  getValidatorColumns() {
    const { conditionNo } = this.state;
    return [
      {
        title: intl.get('hpfm.individual.view.message.title.calculatLogic').d('筛选逻辑'),
        dataIndex: 'conExpression',
        width: 350,
        render: (val, record) => (
          <FormItem wrapperCol={{ span: 24 }}>
            {record.$form.getFieldDecorator(`conExpression`, {
              initialValue: val,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hpfm.individual.view.message.title.calculatLogic')
                      .d('筛选逻辑'),
                  }),
                },
                {
                  validator: (_, value, cb) => {
                    const array = (value !== undefined && value.match(/\s?\d+\s?/g)) || [];
                    for (let i = 0; i < array.length; i += 1) {
                      const no = array[i].match(/(\d+)/)[0];
                      if (conditionNo[no] !== 1) {
                        cb(
                          intl
                            .get('hpfm.individual.model.config.conditionValidator', {
                              no,
                            })
                            .d(`条件${no}不存在`)
                        );
                        return;
                      }
                    }
                    cb();
                  },
                },
                {
                  validator: (_, value, cb) => {
                    const array = (value !== undefined && value.match(/[^0-9()\s]+/g)) || [];
                    const equalOrAnd =
                      array.length > 0
                        ? array.reduce((prev, next) => prev && /OR|AND/.test(next), true)
                        : false;
                    if (array.length > 0 && !equalOrAnd) {
                      cb(
                        intl
                          .get('hpfm.individual.model.config.conditionValidator.tips1')
                          .d('不允许输入字母及 ( )  OR AND 以外的字符')
                      );
                      return;
                    }
                    cb();
                  },
                },
              ],
            })(<Input inputChinese={false} />)}
          </FormItem>
        ),
      },
      {
        title: intl.get('hpfm.individual.model.config.defaultValue').d('默认值'),
        dataIndex: 'value',
        render: (_, record) => this.getDefaultValueComponent(record),
      },
      {
        title: intl.get('hzero.common.action').d('操作'),
        dataIndex: '_op',
        width: 60,
        render: (_, record) => (
          <Popconfirm
            title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录')}
            okText={intl.get('hzero.common.status.yes').d('是')}
            cancelText={intl.get('hzero.common.status.no').d('否')}
            onConfirm={() => this.delValidator(record.conCode)}
          >
            <a className="delete" role="button" style={{ color: '#333' }}>
              <Icon type="delete" />
            </a>
          </Popconfirm>
        ),
      },
    ];
  }

  @Bind()
  onOk() {
    const { form, onClose, updateSelfValidator, headerProps } = this.props;
    const { conditionList, validatorList } = this.state;
    let validateData = getEditTableData(validatorList, ['_status']) || [];
    if (validatorList.length > 0 && validateData.length === 0) {
      return;
    }
    // 逻辑复用，后端必输，故这里给空字符串
    validateData = validateData.map((i) => ({ ...i, errorMessage: '' }));
    form.validateFields((err, values) => {
      if (err) return;
      const newConditionList = conditionList
        .map((lineData) => {
          if (!lineData) return false;
          const { conCode } = lineData;
          return {
            ...lineData,
            sourceUnitId: values[`sourceUnitId#${conCode}`],
            sourceUnitCode: values[`sourceUnitCode#${conCode}`],
            sourceFieldId: values[`sourceFieldId#${conCode}`],
            sourceFieldCode: values[`sourceFieldCode#${conCode}`],
            sourceModelId: values[`sourceModelId#${conCode}`],
            sourceFieldValueCode: values[`sourceFieldValueCode#${conCode}`],
            conExpression: values[`conExpression#${conCode}`],
            targetType: values[`targetType#${conCode}`],
            targetValue: values[`targetValue#${conCode}`],
            targetValueMeaning: values[`targetValueMeaning#${conCode}`],
            targetFieldId: values[`targetFieldId#${conCode}`],
            targetFieldCode: values[`targetFieldCode#${conCode}`],
            targetModelId: values[`targetModelId#${conCode}`],
          };
        })
        .filter(Boolean);
      if (typeof updateSelfValidator === 'function') {
        updateSelfValidator({
          ...headerProps,
          conType: 'defaultValue',
          lines: newConditionList,
          valids: validateData,
        });
      }
      onClose();
    });
  }
}
