/**
 * utils
 * @date 2018/9/28
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react'; // 这个 React 是为了上传组件, 当为预览模式时,上传组件还是要显示
import {
  forEach,
  cloneDeep,
  isInteger,
  min,
  isFunction,
  isString,
  startsWith,
  get,
  // toNumber,
  // isNaN,
  toInteger,
  isNil,
} from 'lodash';
import moment from 'moment';
import { Input, DatePicker, InputNumber } from 'hzero-ui';

import intl from 'utils/intl';
import { getDateFormat } from 'utils/utils';
import { DEFAULT_DATE_FORMAT, DEFAULT_DATETIME_FORMAT, DEFAULT_TIME_FORMAT } from 'utils/constants';

import Switch from '../../Switch';
import Checkbox from '../../Checkbox';
import Lov from '../../Lov';
import ValueList from '../../ValueList';
import Upload from '../../Upload';
import { getDisplayFieldCode } from '../utils';

const { TextArea } = Input;

// 为了 防止 webpack 过度优化
const components = {
  DatePicker,
  ValueList,
  TextArea,
  Input,
  InputNumber,
  Lov,
  Switch,
  Checkbox,
  Upload,
};

const contextPrefix = 'this.';

/**
 * 合并 Descriptor 后的属性
 * @param {Object} dealProps2 - 组件属性处理后的属性
 * @param {Object} contextProps - context 属性
 */
function mergeProps(dealProps2, contextProps) {
  const dealProps3 = cloneDeep(dealProps2);
  forEach(contextProps, (_, contextPropKey) => {
    Object.defineProperty(
      dealProps3,
      contextPropKey,
      Object.getOwnPropertyDescriptor(contextProps, contextPropKey)
    );
  });
  return dealProps3;
}

/**
 * 处理context属性 以及将 属性转为对象
 * @param {*} props - 属性
 * @param {*} context - 外面传进来的 this
 */
export function commonDealForProps(props, context) {
  const contextProps = {};
  const dealProps1 = {};
  forEach(props, prop => {
    let dealProp = prop.attributeValue;
    if (isString(dealProp) && startsWith(dealProp, contextPrefix)) {
      const attributePath = dealProp.substr(5);
      dealProp = undefined;
      Object.defineProperty(contextProps, prop.attributeName, {
        get: () => get(context, attributePath),
        enumerable: true,
      });
    }
    if (dealProp !== undefined) {
      dealProps1[prop.attributeName] = dealProp;
    }
  });
  return { contextProps, dealProps1 };
}

// TODO 数值 判断需要改一下
// 获取组件属性

export const propUtils = {
  Checkbox: getCheckboxProps,
  DatePicker: getDatePickerProps,
  DateTimePicker: getDateTimePickerProps,
  Lov: getLovProps,
  InputNumber: getInputNumberProps,
  Switch: getSwitchProps,
  TextArea: getTextAreaProps,
  Input: getInputProps,
  ValueList: getValueListProps,
  Upload: getUploadProps,
};

/**
 * 获取 Checkbox 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 */
function getCheckboxProps(props, context) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  return mergeProps(dealProps1, contextProps);
}

/**
 * 获取 Checkbox 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 */
function getUploadProps(props, context) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  return mergeProps(
    dealProps1,
    // {
    //   // templateAttachmentUUID: `${field.investigateTemplateId}-${field.investgCfLineId}`,
    //   ...dealProps1,
    // },
    contextProps
  );
}

/**
 * 获取 DatePicker 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 */
function getDatePickerProps(props, context) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  return mergeProps(
    { style: { width: '100%' }, format: DEFAULT_DATE_FORMAT, placeholder: '', ...dealProps1 },
    contextProps
  );
}

/**
 * 获取 TimePicker 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 */
function getDateTimePickerProps(props, context) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  return mergeProps(
    { style: { width: '100%' }, format: DEFAULT_DATETIME_FORMAT, placeholder: '', ...dealProps1 },
    contextProps
  );
}

/**
 * 获取 Lov 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 * @param {Object} field - 字段
 */
function getLovProps(props, context, field) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  // 调查表 不会出现 租户id 动态的情况
  return mergeProps(
    {
      code: field.lovCode,
      // todo 不确定租户id 到底怎么弄
      // queryParams: {
      //   organizationId: dealProps1.organizationId,
      //   tenantId: dealProps1.organizationId,
      // },
      style: { width: '100%' },
      textField: getDisplayFieldCode(field), // lov 的 显示字段
      ...dealProps1,
    },
    contextProps
  );
}

/**
 * 获取 InputNumber 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 */
function getInputNumberProps(props, context) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  return mergeProps({ style: { width: '100%' }, ...dealProps1 }, contextProps);
}

/**
 * 获取 Switch 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 */
function getSwitchProps(props, context) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  return mergeProps(dealProps1, contextProps);
}

/**
 * 获取 TextArea 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 */
function getTextAreaProps(props, context) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  return mergeProps(dealProps1, contextProps);
}

/**
 * 获取 Input 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 */
function getInputProps(props, context) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  return mergeProps(dealProps1, contextProps);
}

/**
 * 获取 ValueList 组件的属性
 * @param {Object[]} props - 属性
 * @param {Object} context - 外面传进来的 this
 * @param {Object} field - 字段
 */
function getValueListProps(props, context, field) {
  const { contextProps, dealProps1 } = commonDealForProps(props, context);
  return mergeProps(
    {
      lovCode: field.lovCode,
      style: { width: '100%' },
      textField: getDisplayFieldCode(field), // lov 的 显示字段
      ...dealProps1,
    },
    contextProps
  );
}

// 获取组件属性

/**
 * colLayout - Col 的属性
 */
const colLayout = {
  1: {
    span: 24,
  },
  2: {
    span: 12,
  },
  3: {
    span: 8,
  },
  4: {
    span: 6,
  },
};

/**
 * getColLayout - 获取字段布局 包含对应的 style
 * @param {Number} [col=3] 每行 列数
 * @param {Number} colspan 字段宽度
 */
export function getColLayout(col = 3, colspan) {
  const colL = cloneDeep(colLayout[col] || colLayout[3]);
  if (isInteger(colspan) && colspan > 1) {
    colL.span = min([24, colL.span * colspan]);
  }
  return colL;
}

function getDatePickerFormat(field, defaultFormat) {
  let format = defaultFormat;
  forEach(field.props, prop => {
    if (prop.attributeName === 'format') {
      format = prop.attributeValue;
      return false;
    }
  });
  return format;
}

/**
 * 从组件的 onChange return String 给 Form 表单
 * @param dateMoment
 * @returns {*}
 */
function dateGetValueFromEventFunc(dateMoment) {
  return dateMoment && dateMoment.format(DEFAULT_DATE_FORMAT);
}

/**
 * 从组件的 onChange return String 给 Form 表单
 * @param dateMoment
 * @returns {*}
 */
function timeGetValueFromEventFunc(timeMoment) {
  return timeMoment && timeMoment.format(DEFAULT_TIME_FORMAT);
}

/**
 * 从 Form 表单 将 String 转为 moment 给组件
 * @param dateStr
 * @returns {{value: (*|moment.Moment)}}
 */
function dateGetValuePropFunc(dateStr) {
  return { value: dateStr && moment(dateStr, DEFAULT_DATE_FORMAT) };
}

/**
 * 从 Form 表单 将 String 转为 moment 给组件
 * @param dateStr
 * @returns {{value: (*|moment.Moment)}}
 */
function timeGetValuePropFunc(dateStr) {
  return { value: dateStr && moment(dateStr, DEFAULT_TIME_FORMAT) };
}

/**
 * Form 字段 获取 getValueProp 属性
 * @param field
 * @returns {*}
 */
export function getGetValuePropFunc(field) {
  switch (field.componentType) {
    case 'DatePicker':
      return dateGetValuePropFunc;
    case 'TimePicker':
      return timeGetValuePropFunc;
    default:
      return undefined;
  }
}

/**
 * Form 字段 获取 getValueFromEvent 属性
 * @param componentType
 * @returns {*}
 */
export function getGetValueFromEventFunc(componentType) {
  switch (componentType) {
    case 'DatePicker':
      return dateGetValueFromEventFunc;
    case 'TimePicker':
      return timeGetValueFromEventFunc;
    default:
      return undefined;
  }
}

/**
 * 从 dataSource 获取初始值
 * @param {*} field - 字段
 * @param {*} dataSource - 数据源
 */
export function getInitialValue({ field, dataSource = {} }) {
  const v = dataSource[field.fieldCode];

  switch (field.componentType) {
    case 'Switch':
    case 'Checkbox':
      if (typeof v === 'string') {
        return +v ? 1 : 0;
      } else {
        return v || 0;
      }
    // return dataSource[field.fieldCode] || 0;
    default:
      return v;
  }
}

/**
 * 获取组件的类型
 * @param {*} field - 字段
 */
export function getComponentType(field) {
  switch (field.componentType) {
    case 'InputNumber':
      return components.InputNumber;
    case 'TextArea':
      return TextArea;
    case 'DatePicker':
      return components.DatePicker;
    case 'DateTimePicker':
      return components.DatePicker;
    case 'Switch':
      return components.Switch;
    case 'Checkbox':
      return components.Checkbox;
    case 'Lov':
      return components.Lov;
    case 'ValueList':
      return components.ValueList;
    case 'Upload':
      return components.Upload;
    case 'Input':
    default:
      return components.Input;
  }
}

/**
 * 获取组件的属性
 * @param {Object} field - 字段
 * @param {String} componentType - 组件类型
 * @param {Object} context - Page的this
 */
export function getComponentProps({ field = {}, componentType = 'Input', context }) {
  const propFunc = propUtils[componentType];
  if (isFunction(propFunc)) {
    return propFunc(field.props, context, field);
  } else {
    return getInputProps(field.props, context, field);
  }
}

function getDisplayValue({ field, dataSource = {}, componentProps = {} }) {
  let format;
  let dateStr;
  let value;
  switch (field.componentType) {
    case 'ValueList':
    case 'Lov':
      value =
        dataSource[componentProps.displayField] ||
        dataSource[`${field.fieldCode}Meaning`] ||
        dataSource[field.fieldCode];
      return value;
    case 'DatePicker':
      format = getDatePickerFormat(field, getDateFormat());
      dateStr = dataSource[componentProps.displayField] || dataSource[field.fieldCode];
      return dateStr && moment(dateStr, DEFAULT_DATE_FORMAT).format(format);
    case 'TimePicker':
      format = getDatePickerFormat(field, DEFAULT_TIME_FORMAT);
      dateStr = dataSource[componentProps.displayField] || dataSource[field.fieldCode];
      return dateStr && moment(dateStr, DEFAULT_TIME_FORMAT).format(format);
    case 'Checkbox':
    case 'Switch':
      value = dataSource[componentProps.displayField] || dataSource[field.fieldCode];
      if (isNil(value)) {
        return;
      }
      return toInteger(value) === 1
        ? intl.get('hzero.common.status.yes')
        : intl.get('hzero.common.status.no');
    default:
      return dataSource[componentProps.displayField] || dataSource[field.fieldCode];
  }
}

export function renderDisabledField({
  field,
  dataSource,
  organizationId,
  disableStyle = 'value',
  componentProps = {},
}) {
  switch (field.componentType) {
    case 'Upload':
      return (
        <Upload
          {...componentProps}
          viewOnly
          attachmentUUID={getDisplayValue({ field, dataSource, componentProps })}
          organizationId={organizationId}
        />
      );
    default:
      return (
        <div
          className={` compose-field-value-disabled ${
            disableStyle === 'value' ? '' : 'ant-input ant-input-disabled'
          }`}
        >
          {getDisplayValue({ field, dataSource, componentProps })}
        </div>
      );
  }
}
