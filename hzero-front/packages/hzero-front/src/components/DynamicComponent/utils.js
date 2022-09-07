/**
 * utils.js
 * @date 2018/10/24
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import {
  cloneDeep,
  forEach,
  get,
  isFunction,
  isInteger,
  isString,
  max,
  min,
  set,
  startsWith,
  toInteger,
} from 'lodash';
import moment from 'moment';
import { Button, DatePicker, Input, InputNumber, TimePicker } from 'hzero-ui';

import intl from 'utils/intl';
import { getDateFormat, getTimeFormat } from 'utils/utils';
import { DEFAULT_DATE_FORMAT, DEFAULT_TIME_FORMAT } from 'utils/constants';
import { mapCustomizeBuilder } from 'utils/customize/helpers';

import Switch from '../Switch';
import Checkbox from '../Checkbox';
import Lov from '../Lov';
import ValueList from '../ValueList';
import Upload from '../Upload';

import { contextPrefix } from './config';

const { TextArea } = Input;

// 支持的 输入组件类型
const componentTypes = {
  Checkbox,
  DatePicker,
  TimePicker,
  Lov,
  InputNumber,
  Switch,
  TextArea,
  Input,
  ValueList,
  Button,
};

/**
 * 后处理组件属性
 */

export const postPropProcesses = {
  ValueList: postValueListPropProcess,
  Lov: postLovPropProcess,
};

function postValueListPropProcess(props, dataSource, field) {
  const postProps = props;
  postProps.textValue = getDisplayValue({ field, dataSource, componentProps: props });
  if (!postProps.textField) {
    postProps.textField = `${field.fieldName}Meaning`;
  }
  return postProps;
}

function postLovPropProcess(props, dataSource, field) {
  const postProps = props;
  postProps.textValue = getDisplayValue({ field, dataSource, componentProps: props });
  if (!postProps.textField) {
    postProps.textField = `${field.fieldName}Meaning`;
  }
  return postProps;
}

export function defaultPostPropProcess(props) {
  return props;
}

/**
 * 后处理组件属性
 */

/**
 * 处理组件属性
 */

export const prePropProcesses = {
  Checkbox: getCheckboxProps,
  DatePicker: getDatePickerProps,
  Lov: getLovProps,
  InputNumber: getInputNumberProps,
  Switch: getSwitchProps,
  TextArea: getTextAreaProps,
  Input: getInputProps,
  ValueList: getValueListProps,
};

export function defaultPrePropProcess(props, context) {
  const dealProps = dealObjectProps(props, context);
  return dealProps;
}

/**
 * 获取 Checkbox 组件的属性
 * @param {object} props - 属性
 * @param {object} context - 外面传进来的 this
 */
function getCheckboxProps(props, context) {
  return defaultPrePropProcess(props, context);
}

/**
 * 获取 DatePicker 组件的属性
 * @param {object} props - 属性
 * @param {object} context - 外面传进来的 this
 */
function getDatePickerProps(props, context) {
  const dealProps = dealObjectProps(props, context);
  const mergeStyle = dealProps.style || {};
  if (!mergeStyle.width) {
    mergeStyle.width = '100%';
  }
  return {
    placeholder: '',
    format: getDateFormat(),
    ...dealProps,
    style: mergeStyle,
  };
}

/**
 * 获取 Lov 组件的属性
 * @param {object} props - 属性
 * @param {object} context - 外面传进来的 this
 */
function getLovProps(props, context) {
  const dealProps = dealObjectProps(props, context);
  const mergeStyle = dealProps.style || {};
  if (!mergeStyle.width) {
    mergeStyle.width = '100%';
  }
  return {
    ...dealProps,
    style: mergeStyle,
  };
}

/**
 * 获取 InputNumber 组件的属性
 * @param {object} props - 属性
 * @param {object} context - 外面传进来的 this
 */
function getInputNumberProps(props, context) {
  const dealProps = dealObjectProps(props, context);
  const mergeStyle = dealProps.style || {};
  if (!mergeStyle.width) {
    mergeStyle.width = '100%';
  }
  return {
    ...dealProps,
    style: mergeStyle,
  };
}

/**
 * 获取 Switch 组件的属性
 * @param {object} props - 属性
 * @param {object} context - 外面传进来的 this
 */
function getSwitchProps(props, context) {
  return defaultPrePropProcess(props, context);
}

/**
 * 获取 TextArea 组件的属性
 * @param {object[]} props - 属性
 * @param {object} context - 外面传进来的 this
 */
function getTextAreaProps(props, context) {
  return defaultPrePropProcess(props, context);
}

/**
 * 获取 Input 组件的属性
 * @param {object} props - 属性
 * @param {object} context - 外面传进来的 this
 */
function getInputProps(props, context) {
  return defaultPrePropProcess(props, context);
}

/**
 * 获取 ValueList 组件的属性
 * @param {object} props - 属性
 * @param {object} context - 外面传进来的 this
 */
function getValueListProps(props, context) {
  const dealProps = dealObjectProps(props, context);
  const mergeStyle = dealProps.style || {};
  if (!mergeStyle.width) {
    mergeStyle.width = '100%';
  }
  return {
    ...dealProps,
    style: mergeStyle,
  };
}

/**
 * 处理组件属性
 */

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
 * @param {number} [col=3] 每行 列数
 * @param {number} colspan 字段宽度
 */
export function getColLayout(col = 2, colspan) {
  const colL = cloneDeep(colLayout[col] || colLayout[3]);
  if (isInteger(colspan) && colspan > 1) {
    colL.span = min([24, colL.span * colspan]);
  }
  return colL;
}

function dateGetValueFromEventFunc(dateMoment) {
  return dateMoment && dateMoment.format(DEFAULT_DATE_FORMAT);
}

function timeGetValueFromEventFunc(timeMoment) {
  return timeMoment && timeMoment.format(DEFAULT_TIME_FORMAT);
}

function dateGetValuePropFunc(dateStr) {
  return { value: dateStr && moment(dateStr, DEFAULT_DATE_FORMAT) };
}

function timeGetValuePropFunc(dateStr) {
  return { value: dateStr && moment(dateStr, DEFAULT_TIME_FORMAT) };
}

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
 * @param {object} field - 字段
 * @param {object} dataSource - 数据源
 * Checkbox Switch 默认返回 0
 */
export function getInitialValue({ field, dataSource = {} }) {
  switch (field.componentType) {
    case 'Switch':
    case 'Checkbox':
      return dataSource[field.fieldName] || 0;
    default:
      return dataSource[field.fieldName];
  }
}

/**
 * 获取组件的类型
 * @param {object} field - 字段
 * 默认返回 Input
 */
export function getComponentType(field) {
  return componentTypes[field.componentType] || Input;
}

/**
 * 处理组件(字段)的属性,
 * @param {object} field - 字段
 * @param {string} field.componentType - 组件类型
 * @param {object} context - 页面 context
 */
export function preProcessComponentProps({ field = {}, context }) {
  const prePropProcess = prePropProcesses[field.componentType];
  if (isFunction(prePropProcess)) {
    return prePropProcess(field.props, context, field);
  }
  return defaultPrePropProcess(field.props, context, field);
}

export function postProcessComponentProps({ field = {}, dealProps, dataSource }) {
  const postPropProcess = postPropProcesses[field.componentType];
  if (isFunction(postPropProcess)) {
    return postPropProcess(dealProps, dataSource, field);
  }
  return defaultPostPropProcess(dealProps, dataSource, field);
}

/**
 *
 * @param {{field: object, dataSource: object, componentProps: object}}
 * @param {object} field - 字段
 * @param {object} [dataSource={}] - 数据
 * @param {object} componentProps - 属性
 */
export function getDisplayValue({ field, dataSource = {}, componentProps }) {
  const displayFieldName = componentProps.displayField;
  let format;
  let dateStr;
  switch (field.componentType) {
    case 'ValueList':
    case 'Lov':
      return (
        dataSource[displayFieldName] ||
        dataSource[`${field.fieldName}Meaning`] ||
        dataSource[field.fieldName]
      );
    case 'DatePicker':
      format = getDateFormat();
      dateStr = dataSource[displayFieldName] || dataSource[field.fieldName];
      return dateStr && moment(dateStr, DEFAULT_DATE_FORMAT).format(format);
    case 'TimePicker':
      format = getTimeFormat();
      dateStr = dataSource[displayFieldName] || dataSource[field.fieldName];
      return dateStr && moment(dateStr, DEFAULT_DATE_FORMAT).format(format);
    case 'Checkbox':
    case 'Switch':
      return toInteger(dataSource[displayFieldName] || dataSource[field.fieldName]) === 1
        ? intl.get('hzero.common.status.yes')
        : intl.get('hzero.common.status.no');
    default:
      return dataSource[displayFieldName] || dataSource[field.fieldName];
  }
}

/**
 * @param {{field: object, dataSource: object, organizationId: number, disableStyle: 'value' | '', componentProps: object}}
 * @param {object} field - 字段
 * @param {object} dataSource - 数据
 * @param {number} organizationId - 租户id
 * @param {object} componentProps - 组件的属性
 */
export function renderDisabledField({ field, dataSource, organizationId, componentProps = {} }) {
  switch (field.componentType) {
    case 'Upload':
      return (
        <Upload
          {...componentProps}
          disabled
          viewOnly
          value={getDisplayValue({ field, dataSource, componentProps })}
          organizationId={organizationId}
        />
      );
    default:
      return (
        <div className="compose-field-value-disabled">
          {getDisplayValue({ field, dataSource, componentProps })}
        </div>
      );
  }
}

/**
 * getWidthFromWord - 通过字符串确定宽度
 * @param {String} word - 字符串
 * @param {Number} minWidth - 最小宽度
 * @param {Number} maxWidth - 最大宽度
 * @param {Number} [defaultWidth=100] - 默认宽度
 * @param {Number} [fontWidth=12] - 每个字符的宽度
 * @param {Number} [paddingWidth=36] - 补偿额外宽度
 */
export function getWidthFromWord({
  word,
  minWidth,
  maxWidth,
  defaultWidth = 100,
  fontWidth = 12,
  paddingWidth = 36,
}) {
  let ret = defaultWidth;
  if (isString(word)) {
    ret = word.length * fontWidth;
    if (min) {
      ret = max([ret, minWidth]);
    }
    if (max) {
      ret = min([ret, maxWidth]);
    }
    ret += paddingWidth;
  }
  return ret;
}
/**
 * 将属性解构成 对应的
 * @param {object} config - 属性
 * @param {object} context - 上下文
 * @param {{[excludeKey: string]: true}} excludeProps - 要排除的属性
 */
export function dealObjectProps(config, context, excludeProps) {
  const dealProps = {};
  const skipProps = excludeProps || {};
  forEach(config, (v, k) => {
    if (!skipProps[k]) {
      set(dealProps, k, getContextValue(context, v));
    }
  });
  return dealProps;
}

/**
 * 处理 key
 * @param {object} props - 属性
 * @param {{[excludeKey: string]: true}} excludeProps - 要排除的属性
 */
export function setProps(props, excludeProps) {
  const hasSetProps = {};
  const skipProps = excludeProps || {};
  forEach(props, (v, k) => {
    if (!skipProps[k]) {
      hasSetProps[k] = v;
    }
  });
  return hasSetProps;
}

/**
 * 如果是context属性 则返回 context属性上的内容
 * 否则返回 pathOrValue
 * @param {object} context
 * @param {string} pathOrValue
 */
export function getContextValue(context, pathOrValue) {
  if (isString(pathOrValue) && startsWith(pathOrValue, contextPrefix)) {
    return get(context, pathOrValue.substr(5));
  }
  return pathOrValue;
}

const { set: customizeSet, get: customizeGet } = mapCustomizeBuilder(
  'hpfm',
  'ui_dynamic_component'
);
export { customizeSet as set, customizeGet as get };
