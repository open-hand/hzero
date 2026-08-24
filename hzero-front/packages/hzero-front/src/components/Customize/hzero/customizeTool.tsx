/* eslint-disable no-new-func */
/* eslint-disable eqeqeq */
/**
 * 个性化组件utils工具包
 * @date: 2019-12-15
 * @version: 0.0.1
 * @author: zhaotong <tong.zhao@hand-china.com>
 * @copyright Copyright (c) 2019, Hands
 */

import React from 'react';
import { Form, Input, InputNumber } from 'hzero-ui';
// import { groupBy, isNil, isArray, isEmpty } from 'lodash';
import { isNil, isArray, isEmpty, isNumber, omit } from 'lodash';
import moment from 'moment';
import TLEditor from 'components/TLEditor';

import Upload from 'components/Upload';
import intl from 'utils/intl';
import { yesOrNoRender, numberRender } from 'utils/renderer';
import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import { getCurrentOrganizationId, getResponse, getUserOrganizationId } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import template from '../../../utils/template';
import {
  FlexSelect,
  // FlexRadioGroup,
  FlexLov,
  FlexDatePicker,
  FlexCheckbox,
  FlexSwitch,
  FlexLink,
} from './FlexComponents';
import LovMulti from './LovMulti';
import { FieldConfig, ParamList, ConditionHeaderDTO, ConValid, UnitAlias } from './interfaces';
// FormItem组件初始化
const FormItem = Form.Item;
const { HZERO_PLATFORM } = getEnvConfig();
/* 接口部分 */
// const mockapi = '/api/hpfm';

export async function queryCode(params = {}) {
  return getResponse(
    await request(`${HZERO_PLATFORM}/v1/lovs/value`, {
      query: params,
    }),
    () => {}
  );
}
export async function queryUnitCustConfig(params = {}) {
  return getResponse(
    await request(`${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/ui-customize`, {
      query: params,
      method: 'GET',
    }),
    () => {}
  );
}
/**
 * 对拿到数据做进一步的渲染适配
 * @param renderOptions 渲染类别
 */
export function getRender(fieldType, componentProps: any = {}) {
  switch (fieldType) {
    case 'SWITCH':
    case 'CHECKBOX':
      return (val) => yesOrNoRender(Number(val));
    case 'INPUT_NUMBER':
      return isNil(componentProps.precision)
        ? (val) => val
        : (val) => numberRender(val, componentProps.precision || 0, true, true);
    case 'DATE_PICKER':
      return (val) => val && moment(val).format(componentProps.format);
    default:
      return (val) => val;
  }
}
export function getValuePropName(type) {
  if (type === 'UPLOAD') return 'attachmentUUID';
  if (type === 'CHECKBOX' || type === 'SWITCH') return 'checked';
  return 'value';
}
export function customizeFormOptions(options: FieldConfig) {
  const { fieldType, dateFormat = '', initialValue } = options || {};
  const rules = customizeFormRules(options);

  const formOptions: any = {
    rules,
    valuePropName: getValuePropName(fieldType),
    initialValue,
  };
  // if(fieldType === 'RADIO_GROUP'){
  //   formOptions.getValueProps = e => (e||{target: {}}).target.value;
  //   formOptions.getValueFromEvent = e => {
  //     return e.target.value;
  //   };
  // }
  if (fieldType === 'DATE_PICKER') {
    // const onlyDateFormat = dateFormat.match(/(^\S+DD)/) || ['YYYY/MM/DD'];
    const newDateFormat = /HH|hh|mm|ss/.test(dateFormat)
      ? dateFormat
      : `${DEFAULT_DATE_FORMAT} 00:00:00`;
    formOptions.getValueProps = (dateStr) => ({
      value: dateStr ? moment(dateStr, newDateFormat) : dateStr,
    });
    formOptions.getValueFromEvent = (e) => {
      if (!e || !e.target) {
        return e && e.format ? e.format(newDateFormat) : e;
      }
      const { target } = e;
      return target.type === 'checkbox' ? target.checked : target.value;
    };
  }
  return formOptions;
}
export function parseContentProps(contentProps: FieldConfig, code: string | undefined) {
  const {
    lovCode,
    editable,
    fieldCode,
    fieldName,
    numberMax,
    numberMin,
    numberPrecision,
    dateFormat = '',
    bucketName,
    bucketDirectory,
    areaMaxLine,
    fieldType,
    linkTitle,
    linkHref,
    linkNewWindow,
    lovMappings,
    dataSource = {},
    paramList = [],
    defaultValue,
    defaultValueMeaning,
    isGrid,
    placeholder,
    getValueFromCache,
    ...rest
  } = contentProps || {};
  const disabled = editable === 0;
  let value = dataSource[fieldCode];
  let meaning = dataSource[`${fieldCode}Meaning`];
  if (isNil(value)) {
    value = defaultValue;
    meaning = defaultValueMeaning;
  }
  if (!isNil(meaning)) {
    meaning =
      typeof meaning === 'object' && fieldType !== 'LOV'
        ? Object.values(meaning).join('/')
        : meaning;
  } else {
    meaning = value;
  }
  const commonProps: any = {
    initialValue: preAdapterInitValue(fieldType, value),
    initialMeaning: meaning,
    placeholder,
    disabled,
    dataSource,
    ...omit(rest, [
      'conditionHeaderDTOs',
      'renderOptions',
      'required',
      'visible',
      'standardField',
      'isStandardField',
    ]),
  };
  let tempProps: any = {};
  if (fieldType === 'TEXT_AREA') {
    tempProps = {
      rows: areaMaxLine || 2,
    };
  }
  if (fieldType === 'INPUT_NUMBER') {
    tempProps = {
      max: numberMax,
      min: numberMin,
      precision: numberPrecision || 0,
    };
  }
  if (fieldType === 'DATE_PICKER') {
    tempProps = {
      fieldCode,
      showTime: /HH|hh|mm|ss/.test(dateFormat),
      format: dateFormat,
    };
  }
  if (fieldType === 'LOV') {
    tempProps = {
      code: lovCode,
      lovMappings,
      textValue: meaning,
      queryParams: getContextParams(paramList, {
        code,
        getValueFromCache,
        isGrid,
        form: rest.form,
        dataSource,
      }),
    };
    if (rest.multipleFlag) {
      delete tempProps.lovMappings;
      tempProps.translateData = tempProps.textValue;
      delete tempProps.textValue;
    }
  }
  if (fieldType === 'TL_EDITOR') {
    tempProps = {
      label: fieldName,
      field: fieldCode,
      token: dataSource._token,
    };
  }
  if (fieldType === 'UPLOAD') {
    tempProps = {
      label: fieldName,
      field: fieldCode,
      viewOnly: disabled,
      bucketName,
      bucketDirectory,
    };
    delete commonProps.disabled;
  }
  if (fieldType === 'LINK') {
    tempProps = {
      linkTitle,
      linkHref,
      linkNewWindow,
      form: rest.form,
      dataSource,
    };
  }
  if (fieldType === 'SELECT') {
    tempProps = {
      lovCode,
      lovMappings,
      fieldCode,
      params: getContextParams(paramList, {
        getValueFromCache,
        isGrid,
        code,
        form: rest.form,
        dataSource,
      }),
    };
  }
  // eslint-disable-next-line no-param-reassign
  if (fieldType === 'SWITCH') delete commonProps.style;
  return {
    ...commonProps,
    ...tempProps,
  };
}
/**
 * 根据类型参数生成不同的表单组件
 * @param type 组件类型
 */
export function getFormItemComponent(fieldType, renderOptions, code?: string) {
  let Component;
  switch (fieldType) {
    case 'INPUT':
      Component = (props) => <Input {...props} />;
      break;
    case 'INPUT_NUMBER':
      Component = (props) => <InputNumber {...props} />;
      break;
    case 'SELECT':
      Component = (props) => <FlexSelect {...props} />;
      break;
    // case 'RADIO_GROUP':
    //   Component = props => <FlexRadioGroup {...props} />;
    //   break;
    case 'CHECKBOX':
      Component = FlexCheckbox;
      break;
    case 'SWITCH':
      Component = FlexSwitch;
      break;
    case 'LOV':
      Component = FlexLov;
      break;
    case 'DATE_PICKER':
      Component = FlexDatePicker;
      break;
    case 'UPLOAD':
      Component = (props) => <Upload {...props} />;
      break;
    case 'TL_EDITOR':
      Component = (props) => <TLEditor {...props} />;
      break;
    case 'TEXT_AREA':
      Component = (props) => <Input.TextArea {...props} />;
      break;
    case 'LINK':
      Component = FlexLink;
      break;
    default:
      Component = (props) => <Input {...props} />;
  }
  return (options) => {
    const {
      form,
      isEdit = true, // 表单默认true
      readOnly,
      wrapProps,
      contentProps,
      fieldCode,
      formOptions = {},
    } = options;
    const viewOnly = !isEdit || readOnly || renderOptions !== 'WIDGET' || !form;
    const hasFormDec = !!form;
    contentProps.form = form;
    const { initialValue, initialMeaning, ...componentProps } = parseContentProps(
      contentProps,
      code
    );
    let values = componentProps.dataSource;
    // 只读单元不需要用form中的值覆盖
    if (!readOnly) {
      values = { ...values, ...(form && form.getFieldsValue()) };
    }
    const newValue = preAdapterInitValue(fieldType, values[fieldCode]);
    let newMeaning = initialMeaning;
    if (newValue !== initialValue) {
      newMeaning = newValue;
    }
    const newFormOptions = customizeFormOptions({ ...formOptions, initialValue });
    let component =
      fieldType === 'LOV' && contentProps.multipleFlag ? (
        <LovMulti {...componentProps} viewOnly={viewOnly} />
      ) : (
        Component(componentProps)
      );
    const forceUseComponent =
      fieldType === 'UPLOAD' ||
      fieldType === 'LINK' ||
      (contentProps.multipleFlag && fieldType === 'LOV');

    // 调整component
    if (viewOnly && !forceUseComponent) {
      // 只读但不使用组件自带的只读模式
      const renderText = getRender(
        fieldType,
        componentProps
      )(fieldType === 'LOV' || fieldType === 'SELECT' ? newMeaning : newValue);
      component = <span>{renderText}</span>;
    } else if (viewOnly) {
      // 只读并使用组件自带的只读模式需手动设置组件对应的value
      // eslint-disable-next-line no-unused-expressions
      fieldType === 'UPLOAD'
        ? (component.props.attachmentUUID = initialValue)
        : (component.props.value = initialValue);
    }
    // 如果form存在，value将由form接管
    if (hasFormDec) {
      component = form.getFieldDecorator(fieldCode, newFormOptions)(component);
    }
    return isEdit ? <FormItem {...wrapProps}>{component}</FormItem> : component;
  };
}
/**
 * 对initialValue进行预处理
 * @param type 处理类型
 * @param value 表单值
 */
export function preAdapterInitValue(type, value) {
  switch (type) {
    case 'CHECKBOX':
    case 'SWITCH':
      // eslint-disable-next-line eqeqeq
      return isNil(value) ? value : Number(value);
    default:
      return value;
  }
}
/**
 * 标准字段（预定义）应用个性化样式
 * @param individual 个性化配置信息
 * @param item 存有标准字段formItem及其配置信息的对象
 * @param targetRows 解析后存储的目标对象
 * @param {Object} object 解析配置、包含{maxCol、form、tempRowBase}
 */
export function adapterStandardFormIndividual(
  individualProps,
  item,
  targetRows = {},
  { baseCol, form, unitLabelCol, unitWrapperCol, dataSource, tempItems }
) {
  const parseRows = targetRows;
  const { formItem, rowProps, colProps } = item;
  const rules = customizeFormRules(individualProps);
  const {
    formRow = item.row,
    formCol = item.col,
    visible,
    labelCol,
    wrapperCol,
    defaultValue,
    fieldType,
    colSpan = 1,
  } = individualProps;
  if (visible === 0 || isNil(item.row)) return;
  // const newRowProps = { ...rowProps, ..._rowProps };
  const newColProps = { ...colProps };
  if ((rowProps.className || '').indexOf('half-row') > -1) {
    newColProps.span = 12;
  } else {
    newColProps.span = colSpan * baseCol;
  }
  adjustRowAndCol(parseRows, formItem, {
    row: formRow,
    col: formCol,
    tempItems,
    rowProps,
    colProps: newColProps,
  });
  formItem.props.labelCol = { span: labelCol || unitLabelCol };
  formItem.props.wrapperCol = { span: wrapperCol || unitWrapperCol };
  traversalFormItems(formItem, {
    ...individualProps,
    defaultValue: preAdapterInitValue(fieldType, defaultValue),
    rules,
    form,
    dataSource,
  });
}
/**
 * 用于调整FormItem类型的react-element对象的配置
 * @param {object} individual 个性化配置属性
 */
export function customizeFormRules(individual: FieldConfig) {
  const { textMaxLength, textMinLength, required, fieldName } = individual || {};
  const rules: any[] = [];
  if (textMaxLength !== -1 && textMaxLength !== undefined) {
    rules.push({
      max: textMaxLength,
      message: intl.get('hzero.common.validation.max', {
        max: textMaxLength,
      }),
    });
  }
  if (textMinLength !== -1 && textMinLength !== undefined) {
    rules.push({
      min: textMinLength,
      message: intl.get('hzero.common.validation.min', {
        min: textMinLength,
      }),
    });
  }
  if (required !== -1 && required !== undefined) {
    rules.push({
      required: !!required,
      message: intl.get('hzero.common.validation.notNull', {
        name: fieldName,
      }),
    });
  }
  return rules;
}
/**
 * 调整FormItem的行列结构
 * @param targetRows 原存储FormItem行列信息的对象，key为行数
 * @param formItem 插入的表单项
 * @param Object 配置对象
 */
export function adjustRowAndCol(
  targetRows = {},
  formItem,
  { row, col, rowProps, colProps, tempItems }
) {
  const newRow = row - 1;
  const newCol = col - 1;
  if (row === undefined || col === undefined || !isNumber(newRow) || !isNumber(newCol)) {
    tempItems.push({ formItem, rowProps, colProps });
    return;
  }
  const parseRows = targetRows;
  // 如果被检测行不存在，会在targetRows中初始化一个行
  if (!parseRows[newRow]) {
    parseRows[newRow] = {
      rowProps: {},
      formItemList: [],
    };
  }
  if (parseRows[newRow].formItemList[newCol]) {
    tempItems.push({ formItem, rowProps, colProps });
    return;
  }
  parseRows[newRow].rowProps = rowProps;
  parseRows[newRow].formItemList[newCol] = {
    colProps,
    formItem,
  };
}
/**
 * 使用新规则替换FormItem原有的校验规则
 * @param formItem FormItem的reactElement
 * @param rules 新的规则项
 */
export function traversalFormItems(formItem: any = {}, fieldConfig: FieldConfig) {
  const { fieldName } = fieldConfig || {};
  const children = formItem && formItem.props ? formItem.props.children : null;
  let isInput = false;
  if (children) {
    if (!isNil(fieldName)) {
      // eslint-disable-next-line no-param-reassign
      formItem.props.label = fieldName;
    }
    if (isArray(children)) {
      for (let i = 0; i < children.length; i++) {
        if (isNil(children[i])) {
          return;
        }
        const { props } = children[i];
        isInput = mergeFormItemIndividual(props, fieldConfig);
        // if (props && props['data-__meta']) {
        //   return;
        // }
      }
    } else {
      const { props } = children;
      isInput = mergeFormItemIndividual(props, fieldConfig);
    }
  }
  return isInput;
}
// 表格个性化需要用返回值确定原render是否为一个输入组件
function mergeFormItemIndividual(props, fieldConfig: FieldConfig) {
  const {
    rules,
    lovMappings = [],
    form,
    editable,
    defaultValue,
    paramList = [],
    numberPrecision,
    defaultValueMeaning,
    dataSource = {},
    fieldType,
    multipleFlag,
    placeholder,
    textField,
  } = fieldConfig;
  const newRulesCollection: any = { others: [], required: false, max: false, min: false };
  const newProps = props;
  if (newProps && newProps['data-__meta']) {
    const { rules: oldRules = [] } = newProps['data-__meta'];
    const { name } = newProps['data-__meta'];
    if (!isEmpty(rules)) {
      oldRules.forEach((k, index) => {
        if (k.required !== undefined) {
          newRulesCollection.required = oldRules[index];
        } else if (k.max !== undefined) {
          newRulesCollection.max = oldRules[index];
        } else if (k.min !== undefined) {
          newRulesCollection.min = oldRules[index];
        } else {
          newRulesCollection.others.push(oldRules[index]);
        }
      });
      rules.forEach((k, index) => {
        if (k.required !== undefined) {
          newRulesCollection.required = rules[index];
        } else if (k.max !== undefined) {
          newRulesCollection.max = rules[index];
        } else if (k.min !== undefined) {
          newRulesCollection.min = rules[index];
        }
      });
      const { required, max, min, others } = newRulesCollection;
      const newRules = [required, max, min, ...others].filter(Boolean);
      newProps['data-__meta'].rules = newRules;
      newProps['data-__meta'].validate = [
        {
          rules: newRules,
          trigger: ['onChange'],
        },
      ];
    }
    if (lovMappings.length > 0) {
      const oldOnChange = newProps.onChange;
      newProps.onChange = (val, record, ...others) => {
        const newFields = lovMappings.reduce(
          (prev, cur) => ({
            ...prev,
            [cur.targetCode]: record[cur.sourceCode],
          }),
          {}
        );
        form.setFieldsValue(newFields);
        // eslint-disable-next-line no-unused-expressions
        oldOnChange && oldOnChange(val, record, ...others);
      };
    }
    if (
      isNil(newProps['data-__field'].value) &&
      !isNil(defaultValue) &&
      // defaultValue != initialValue &&
      isNil(dataSource[name])
    ) {
      newProps['data-__meta'].initialValue = defaultValue;
      newProps.value = defaultValue;
      if (fieldType === 'LOV' && multipleFlag === 1) {
        newProps.translateData = defaultValueMeaning;
      } else if (fieldType === 'LOV') {
        // eslint-disable-next-line no-unused-expressions
        !!textField && (newProps.textField = textField);
        newProps.textValue = isNil(defaultValueMeaning) ? defaultValue : defaultValueMeaning;
      }
    }
    if (paramList && paramList.length > 0) {
      newProps.queryParams = getContextParams(paramList);
    }
    if (typeof placeholder === 'string' && placeholder !== '') {
      newProps.placeholder = placeholder;
    }
    // eslint-disable-next-line no-unused-expressions
    editable !== -1 && !isNil(editable) && (newProps.disabled = !editable);
    // eslint-disable-next-line no-unused-expressions
    !isNil(numberPrecision) && (newProps.precision = numberPrecision);
    return true;
  }
  return false;
}

export function coverConfig(originConfig, conditions: ConditionHeaderDTO[] = [], config) {
  const newConfig = originConfig;
  conditions.forEach((i) => {
    let { conExpression = '' } = i;
    if (conExpression !== '') {
      const isErr = isErrConExpression(conExpression);
      if (!isErr) {
        const conNoReg = /(\d+)/g;
        const result = calculateExpression(i.lines || [], config);
        if ((i.lines || []).length > 0) {
          conExpression = conExpression.replace(conNoReg, (_, m) => result[m] || false);
          conExpression = conExpression.replace(/AND/g, '&&').replace(/OR/g, '||');
          // eslint-disable-next-line no-new-func
          newConfig[i.conType] = new Function(`return ${conExpression};`)() ? 1 : 0;
        }
      }
    }
  });
  return newConfig;
}
function isErrConExpression(exp) {
  const leftBracketNum = (exp.match(/\(/g) || []).length;
  const rightBracketNum = (exp.match(/\)/g) || []).length;
  const ruleConNo = /\s*\d+\s+\d+\s*/g.test(exp);
  const ruleConLogic = /\s*(AND|OR)\s*(AND|OR)\s*/g.test(exp);
  const illegalChar = /^(?!AND|OR|\(|\)|\d)/g.test(exp);
  if (leftBracketNum !== rightBracketNum || ruleConNo || ruleConLogic || illegalChar) return true;
  return false;
}
function calculateExpression(
  conditionList,
  { getValueFromCache, isGrid, code, isGridVisible, ...others }
) {
  const result = {};
  conditionList.forEach((i) => {
    const {
      conCode,
      sourceFieldCode = '',
      sourceUnitCode,
      conExpression,
      targetType,
      targetFieldCode = '',
      targetValue,
    } = i;
    if (!sourceUnitCode || !sourceFieldCode) return result;
    if (isGridVisible && (code === sourceUnitCode || targetType === 'formNow')) {
      result[conCode] = true;
      return result;
    }
    let left;
    let right = targetValue;
    const { targetForm, targetDataSource } = others;
    const targetAllValue = {
      ...targetDataSource,
      ...(targetForm ? targetForm.getFieldsValue() : {}),
    };
    if (isGrid && code === sourceUnitCode) {
      left = targetAllValue[sourceFieldCode];
    } else {
      left = getValueFromCache(sourceUnitCode, sourceFieldCode);
    }
    if (isGrid && targetType === 'formNow') {
      if (!targetFieldCode) return result;
      right = targetAllValue[targetFieldCode];
    } else if (targetType === 'formNow') {
      right = getValueFromCache(code, targetFieldCode);
    }
    result[conCode] = logicCompute(conExpression, left, right);
  });
  return result;
}
export function getContextParams(paramList: ParamList[] | never[] = [], options?: any) {
  const { getValueFromCache = () => {}, isGrid, form, dataSource, isConfig, code } = options || {};
  const paramObj = {};
  const { search } = window.location;
  const urlParams = {};
  if (search) {
    search
      .substr(1)
      .split('&')
      .forEach((item) => {
        if (item) {
          const [key, value] = item.split('=');
          urlParams[key] = value;
        }
      });
  }
  const c = getContext();
  paramList.forEach((item) => {
    if (item.paramType === 'context') {
      switch (item.paramValue) {
        case 'organizationId':
          paramObj[item.paramKey] = c.organizationId;
          break;
        case 'tenantId':
          paramObj[item.paramKey] = c.tenantId;
          break;
        default:
      }
    } else if (item.paramType === 'url') {
      paramObj[item.paramKey] = urlParams[item.paramKey];
    } else if (item.paramType === 'fixed') {
      paramObj[item.paramKey] = item.paramValue;
    } else if (!isConfig) {
      // eslint-disable-next-line prefer-const
      let value = getValueFromCache(item.paramUnitCode, item.paramFieldCode);
      if (isGrid && code === item.paramUnitCode) {
        value = { ...dataSource, ...(form ? form.getFieldsValue() : {}) }[
          item.paramFieldCode || ''
        ];
      }
      paramObj[item.paramKey] = value;
    }
  });
  return paramObj;
}
function logicCompute(type, left, right) {
  switch (type) {
    case '=':
      return left == right;
    case '>=':
      return left >= right;
    case '<=':
      return left <= right;
    case '!=':
      return left != right;
    case '>':
      return left > right;
    case '<':
      return left < right;
    case 'ISNULL':
      return left === undefined || left === null;
    case 'NOTNULL':
      return left !== undefined && left !== null;
    case 'BEFORE':
      return moment(left).isBefore(moment(right));
    case 'SAME':
      return moment(left).isSame(moment(right));
    case 'NOTSAME':
      return !moment(left).isSame(moment(right));
    case 'AFTER':
      return moment(left).isAfter(moment(right));
    case '~BEFORE':
      return !moment(left).isBefore(moment(right));
    case '~AFTER':
      return !moment(left).isAfter(moment(right));
    case 'LIKE':
      return new RegExp(right, 'g').test(String(left));
    case 'UNLIKE':
      return !new RegExp(right, 'g').test(String(left));
    case '~LIKE':
      return new RegExp(right, 'g').test(String(left));
    case '~UNLIKE':
      return !new RegExp(right, 'g').test(String(left));
    default:
      return false;
  }
}
export function getFieldValueObject(relatedList: UnitAlias[] = [], getCache, code) {
  const obj = {
    c: getContext(),
  };
  relatedList.forEach(({ unitCode, alias }) => {
    const newAlias = unitCode === code ? 'self' : alias;
    const { form, dataSource } = getCache(unitCode);
    obj[newAlias] = { ...dataSource, ...(form && form.getFieldsValue()) };
  });
  return obj;
}

export function getComputeComp(renderRule, options: any) {
  const { wrapProps, unitData = {}, isGrid, form, dataSource } = options;
  if (isGrid) {
    // eslint-disable-next-line no-param-reassign
    unitData.self = { ...dataSource, ...(form && form.getFieldsValue()) };
    return <div dangerouslySetInnerHTML={{ __html: template.render(renderRule, unitData) }} />;
  }
  return (
    <FormItem {...wrapProps}>
      {/* eslint-disable-next-line react/no-danger */}
      <div dangerouslySetInnerHTML={{ __html: template.render(renderRule, unitData) }} />
    </FormItem>
  );
}

export function getContext() {
  return {
    organizationId: getCurrentOrganizationId(),
    tenantId: getUserOrganizationId(),
  };
}

export function selfValidator(conValid: ConValid | undefined, config) {
  let { conLineList = [], conValidList = [] } = conValid || {};
  conLineList = isArray(conLineList) ? conLineList : [];
  conValidList = isArray(conValidList) ? conValidList : [];
  const result = calculateExpression(conLineList, config);
  let key = '';
  const errors: any[] = [];
  conValidList.forEach((i) => {
    let newExpression = i.conExpression || '';
    const isErr = isErrConExpression(newExpression);
    if (!isErr) {
      const conNoReg = /(\d+)/g;
      newExpression = newExpression.replace(conNoReg, (_, m) => result[m] || false);
      newExpression = newExpression.replace(/AND/g, '&&').replace(/OR/g, '||');
      const isCorrect = new Function(`return ${newExpression};`)();
      key = `${key}${isCorrect}`;
      if (!isCorrect) {
        const error = new Error(i.errorMessage);
        error.name = 'customize';
        errors.push(error);
      }
    }
  });
  return errors;
}

export function defaultValueFx(config, fieldConfig: FieldConfig) {
  let { conLineList = [], conValidList = [] } = fieldConfig.defaultValueConDTO || {};
  conLineList = isArray(conLineList) ? conLineList : [];
  conValidList = isArray(conValidList) ? conValidList : [];
  const result = calculateExpression(conLineList, config);
  for (let i = 0; i < conValidList.length; i++) {
    const condition: any = conValidList[i];
    let { conExpression = '' } = condition;
    const isErr = isErrConExpression(conExpression);
    if (!isErr) {
      const conNoReg = /(\d+)/g;
      conExpression = conExpression.replace(conNoReg, (_, m) => result[m] || false);
      conExpression = conExpression.replace(/AND/g, '&&').replace(/OR/g, '||');
      // eslint-disable-next-line no-new-func
      if (new Function(`return ${conExpression};`)()) {
        const { value: defaultValue, valueMeaning: defaultValueMeaning } = condition;
        return { defaultValue, defaultValueMeaning };
      }
    }
  }
  const { defaultValue, defaultValueMeaning } = fieldConfig;
  return { defaultValue, defaultValueMeaning };
}
