/**
 * utils
 * @date 2018/9/28
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { forEach, isEmpty, isString, max, min } from 'lodash';
import { DatePicker, Input, InputNumber, TimePicker } from 'hzero-ui';
import moment from 'moment';

import Lov from 'components/Lov';
import Checkbox from 'components/Checkbox';
import Switch from 'components/Switch';
import ValueList from 'components/ValueList';
import { DEFAULT_DATE_FORMAT, DEFAULT_TIME_FORMAT } from 'utils/constants';
import { getDateFormat } from 'utils/utils';
import Upload from 'components/Upload';

const { TextArea } = Input;

/**
 * dealFieldProps - 处理基本的属性
 * @param {Object} field - 字段
 */
function dealFieldProps(field = {}) {
  const props = {};
  forEach(field.props, prop => {
    props[prop.attributeName] = prop.attributeValue;
  });
  return props;
}

/**
 * getInputNumberElement - 获取 InputNumber 的 render
 * @param {Object} field - 字段
 */
function getInputNumberElement({ field }) {
  const props = dealFieldProps(field);
  return <InputNumber {...props} />;
}

/**
 * getCheckboxElement - 获取 Checkbox 的 render
 * @param {Object} field - 字段
 */
function getCheckboxElement({ field }) {
  const props = dealFieldProps(field);
  return <Checkbox {...props} />;
}

/**
 * getSwitchElement - 获取 Switch 的 render
 * @param {Object} field - 字段
 */
function getSwitchElement({ field }) {
  const props = dealFieldProps(field);
  return <Switch {...props} />;
}

/**
 * getLovElement - 获取 Lov 的 render
 * @param {Object} field - 字段
 */
function getLovElement({ field, organizationId, dataSource = {} }) {
  if (isEmpty(field.lovCode)) {
    return getInputElement({ field });
  } else {
    const props = dealFieldProps(field);
    return (
      <Lov
        {...props}
        code={field.lovCode}
        queryParams={{ organizationId, tenantId: organizationId }}
        textValue={dataSource[getDisplayFieldCode(field)]}
      />
    );
  }
}

/**
 * getValueListElement - 获取 ValueList 的 render
 * @param {Object} field - 字段
 */
function getValueListElement({ field, organizationId, dataSource = {} }) {
  if (isEmpty(field.lovCode)) {
    return getInputElement({ field });
  } else {
    const props = dealFieldProps(field);
    return (
      <ValueList
        {...props}
        lovCode={field.lovCode}
        organizationId={organizationId}
        textField={dataSource[getDisplayFieldCode(field)]}
        textValue={dataSource[getDisplayFieldCode(field)]}
      />
    );
  }
}

/**
 * getUploadElement - 获取 Upload 的 render
 * @param {Object} field - 字段
 */
function getUploadElement({ field, organizationId }) {
  if (isEmpty(field.lovCode)) {
    return getInputElement({ field });
  } else {
    const props = dealFieldProps(field);
    return <Upload {...props} organizationId={organizationId} />;
  }
}

/**
 * getInputElement - 获取 Input 的 render
 * @param {Object} field - 字段
 */
function getInputElement({ field }) {
  const props = dealFieldProps(field);
  return <Input {...props} />;
}

/**
 * getDatePickerElement - 获取 DatePicker 的 render
 * @param {Object} field - 字段
 */
function getDatePickerElement({ field }) {
  const props = dealFieldProps(field);
  return <DatePicker {...props} />;
}

/**
 * getDateTimePickerElement - 获取 TimePicker 的 render
 * @param {Object} field - 字段
 */
function getDateTimePickerElement({ field }) {
  const props = dealFieldProps(field);
  return <TimePicker {...props} />;
}

/**
 * getTextAreaElement - 获取 TextArea 的 render
 * @param {Object} field - 字段
 */
function getTextAreaElement({ field }) {
  const props = dealFieldProps(field);
  return <TextArea {...props} />;
}

/**
 * @param {!Object} config - 配置
 * @param {Object} config.field - 字段
 * @param {Number} config.organizationId - 租户id
 * @param {Object} config.dataSource - 表单值
 */
export function getFieldElement(config = {}) {
  switch (config.field.componentType) {
    case 'InputNumber':
      return getInputNumberElement(config);
    case 'TextArea':
      return getTextAreaElement(config);
    case 'DatePicker':
      return getDatePickerElement(config);
    case 'DateTimePicker':
      return getDateTimePickerElement(config);
    case 'Switch':
      return getSwitchElement(config);
    case 'Checkbox':
      return getCheckboxElement(config);
    case 'Lov':
      return getLovElement(config);
    case 'ValueList':
      return getValueListElement(config);
    case 'Upload':
      return getUploadElement(config);
    case 'Input':
    default:
      return getInputElement(config);
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
 * 获取属性中的 displayCode 没有这个属性的话, 会返回空字符串
 * @param props
 * @returns {string}
 */
function getDisplayFieldCodeAttrValue(props) {
  let displayFieldCode = '';
  forEach(props, prop => {
    if (prop.attributeName === 'displayFieldCode') {
      displayFieldCode = props.attributeValue;
      return false;
    }
  });
  return displayFieldCode;
}

/**
 * 获取属性上的 format, 如果没有, 返回 defaultFormat
 * @param field
 * @param defaultFormat
 * @returns {*}
 */
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
 * 通过 field 和 dataSource 获取显示值
 * @param {Object} field
 * @param {Object} dataSource
 * @returns
 */
export function getDisplayValue(field, dataSource) {
  const displayFieldCode = getDisplayFieldCodeAttrValue(field.props);
  let format;
  let dateStr;
  switch (field.componentType) {
    case 'ValueList':
    case 'Lov':
      return (
        dataSource[displayFieldCode] ||
        dataSource[`${field.fieldCode}Meaning`] ||
        dataSource[field.fieldCode]
      );
    case 'DatePicker':
      format = getDatePickerFormat(field, DEFAULT_DATE_FORMAT);
      dateStr = dataSource[displayFieldCode] || dataSource[field.fieldCode];
      return dateStr && moment(dateStr, format).format(getDateFormat());
    case 'TimePicker':
      format = getDatePickerFormat(field, DEFAULT_TIME_FORMAT);
      dateStr = dataSource[displayFieldCode] || dataSource[field.fieldCode];
      return dateStr && moment(dateStr, format).format(format);
    // Upload 单独自己处理
    // case 'Upload':
    //   return (
    //     dataSource[field.fieldCode] && (
    //       <Upload viewOnly attachmentUUID={dataSource[field.fieldCode]} />
    //     )
    //   );
    default:
      return dataSource[displayFieldCode] || dataSource[field.fieldCode];
  }
}

/**
 * 获取用来显示的字段
 * @param field
 * @returns {*}
 */
export function getDisplayFieldCode(field) {
  const displayFieldCode = getDisplayFieldCodeAttrValue(field.props);
  if (displayFieldCode) {
    return displayFieldCode;
  }
  switch (field.componentType) {
    case 'ValueList':
      return `${field.fieldCode}Meaning`;
    case 'Lov':
      return `${field.fieldCode}Meaning`;
    default:
      return field.fieldCode;
  }
}
