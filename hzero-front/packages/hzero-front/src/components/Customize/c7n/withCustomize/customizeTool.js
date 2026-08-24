/* eslint-disable no-new-func */
/* eslint-disable eqeqeq */
import React from 'react';
import moment from 'moment';
import request from 'utils/request';
import {
  getCurrentOrganizationId,
  getResponse,
  getUserOrganizationId,
  filterNullValueObject,
} from 'utils/utils';
import {
  TextField,
  NumberField,
  Switch,
  Select,
  CheckBox,
  TextArea,
  Lov,
  Upload,
  DatePicker,
} from 'choerodon-ui/pro';
import { isArray, isNil } from 'lodash';
import { getEnvConfig } from 'utils/iocUtils';
import { FlexLink, FlexIntlField } from './FlexComponents';

const { HZERO_PLATFORM } = getEnvConfig('config');

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
 * 处理组件的特有配置
 * @param contentProps
 */
export function parseProps(props = {}, tools, oldConfig = {}) {
  const {
    numberMax,
    numberMin,
    numberPrecision = 1,
    areaMaxLine,
    fieldType,
    textMaxLength,
    textMinLength,
    defaultValue,
    paramList,
    lovCode,
    dateFormat,
    multipleFlag,
  } = props;

  const extraProps = {
    maxLength: textMaxLength,
    minLength: textMinLength,
    max: numberMax,
    min: numberMin,
    defaultValue: preAdapterInitValue(fieldType, defaultValue),
  };

  if (fieldType !== undefined) {
    extraProps.type = getComponentType(fieldType);
  }
  if (fieldType === 'TEXT_AREA') {
    extraProps.rows = areaMaxLine;
  }
  if (fieldType === 'INPUT_NUMBER') {
    extraProps.step = typeof numberPrecision === 'number' ? 1 / 10 ** numberPrecision : undefined;
  }
  if (fieldType === 'LOV') {
    extraProps.multiple = multipleFlag === undefined ? undefined : multipleFlag === 1;
    extraProps.lovPara = { ...oldConfig.lovPara, ...getContextParams(paramList, tools) };
    extraProps.lovCode = lovCode || oldConfig.lovCode;
  }
  if (fieldType === 'SELECT') {
    extraProps.multiple = multipleFlag === undefined ? undefined : multipleFlag === 1;
    extraProps.lovPara = { ...oldConfig.lovPara, ...getContextParams(paramList, tools) };
    extraProps.lookupCode = lovCode || oldConfig.lookupCode;
  }
  if (fieldType === 'DATE_PICKER') {
    extraProps.format = dateFormat && dateFormat.replace(/hh/, 'HH');
    extraProps.type = /hh|mm|ss|HH/g.test(dateFormat) ? 'dateTime' : 'date';
  }
  return {
    ...oldConfig,
    ...filterNullValueObject(extraProps),
  };
}
export function transformCompProps(props = {}) {
  const {
    fieldType,
    linkTitle,
    dateFormat,
    linkHref,
    linkNewWindow,
    editable,
    placeholder,
    uploadAccept = [],
  } = props;
  const commonProps = { placeholder };
  if (fieldType === 'LINK') {
    return {
      linkTitle,
      linkHref,
      linkNewWindow,
    };
  }
  if (fieldType === 'UPLOAD') {
    return {
      action: linkHref,
      accept: uploadAccept.join(','),
    };
  }
  if (fieldType === 'DATE_PICKER') {
    return {
      mode: /hh|mm|ss|HH/g.test(dateFormat) ? 'dateTime' : 'date',
    };
  }
  if (editable !== -1) {
    commonProps.disabled = !editable;
  }
  return commonProps;
}
/**
 * 根据类型参数生成不同的表单组件
 * @param type 组件类型
 */
export function getComponent(type, extra) {
  let Component = null;
  switch (type) {
    case 'EMPTY':
      Component = () => <div />;
      break;
    case 'INPUT':
      Component = (props) => <TextField {...props} />;
      break;
    case 'INPUT_NUMBER':
      Component = (props) => <NumberField {...props} />;
      break;
    case 'SELECT':
      Component = (props) => <Select {...props} />;
      break;
    // case 'RADIO_GROUP':
    //   Component = props => <FlexRadioGroup {...props} />;
    //   break;
    case 'CHECKBOX':
      Component = (props) => <CheckBox {...props} unCheckedValue={0} checkedValue={1} />;
      break;
    case 'SWITCH':
      Component = (props) => <Switch {...props} unCheckedValue={0} checkedValue={1} />;
      break;
    case 'LOV':
      Component = (props) => <Lov {...props} />;
      break;
    case 'DATE_PICKER':
      Component = (props) => <DatePicker {...props} />;
      break;
    case 'UPLOAD':
      Component = (props) => <Upload {...props} />;
      break;
    case 'TL_EDITOR':
      Component = FlexIntlField;
      break;
    case 'TEXT_AREA':
      Component = (props) => <TextArea {...props} />;
      break;
    case 'LINK':
      Component = FlexLink;
      break;
    default:
      Component = (props) => <TextField {...props} />;
  }
  return (options) => Component(options, extra);
}

/**
 * 根据类型参数生成不同的表单组件
 * 字段类型，可选值：boolean number string date dateTime time week month year email url intl object
 * @param type 组件类型
 */
export function getComponentType(type) {
  switch (type) {
    case 'INPUT':
      return 'string';
    case 'INPUT_NUMBER':
      return 'number';
    case 'SELECT':
      return 'string';
    case 'CHECKBOX':
    case 'SWITCH':
      return 'boolean';
    case 'LOV':
      return 'object';
    case 'DATE_PICKER':
      return 'date';
    case 'UPLOAD':
      return 'upload';
    case 'TL_EDITOR':
      return 'intl';
    case 'LINK':
      return 'url';
    default:
  }
}
// 查询用户个性化渲染数据
export async function queryUnitCustConfig(params = {}) {
  return getResponse(
    await request(`${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/ui-customize`, {
      query: params,
      method: 'GET',
    })
  );
}

// 查询用户个性化配置数据
export async function queryUserCustConfig(query) {
  return getResponse(
    await request(`${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/unit-config/user-ui`, {
      query,
    })
  );
}

// 保存用户个性化配置数据
export async function saveUserCustConfig(params) {
  return getResponse(
    await request(`${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/unit-config/user-ui`, {
      method: 'POST',
      body: params,
    })
  );
}

export function coverConfig(conditions = [], config, ignore = []) {
  const newConfig = {};
  conditions.forEach((i) => {
    let { conExpression = '' } = i;
    if (conExpression !== '') {
      const isErr = isErrConExpression(conExpression);
      if (!isErr && !ignore.includes(i.conType)) {
        const conNoList = conExpression.match(/\s?\d+\s?/g);
        const result = calculateExpression(i.lines || [], config);
        if ((i.lines || []).length > 0) {
          conNoList.forEach((k) => {
            const newKey = k.trim();
            conExpression = conExpression.replace(newKey, result[newKey] || false);
          });
          conExpression = conExpression.replace(/AND/g, '&&').replace(/OR/g, '||');
          // eslint-disable-next-line no-eval
          newConfig[i.conType] = eval(conExpression) ? 1 : 0;
        }
      }
    }
  });
  return newConfig;
}
function isErrConExpression(exp) {
  const leftBracketNum = exp.match(/\(/g) || [].length;
  const rightBracketNum = exp.match(/\)/g) || [].length;
  const ruleConNo = /\s*\d+\s+\d+\s*/g.test(exp);
  const ruleConLogic = /\s*(AND|OR)\s*(AND|OR)\s*/g.test(exp);
  const illegalChar = /^(?!AND|OR|\(|\)|\d)/g.test(exp);
  if (leftBracketNum !== rightBracketNum || ruleConNo || ruleConLogic || illegalChar) return true;
  return false;
}
function calculateExpression(conditionList, tools = {}) {
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
    const sourceUnitType = tools.getCacheType(sourceUnitCode);
    if (!sourceUnitCode || !sourceUnitType) return result;
    const left = getFieldValueByCode(sourceUnitCode, sourceFieldCode, tools);
    let right = targetValue;
    if (targetType === 'formNow') {
      right = getFieldValueByCode(tools.code, targetFieldCode, tools);
    }
    result[conCode] = logicCompute(conExpression, left, right);
  });
  return result;
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
    case 'AFTER':
      return moment(left).isAfter(moment(right));
    case 'SAME':
      return moment(left).isSame(moment(right));
    case 'NOTSAME':
      return !moment(left).isSame(moment(right));
    case '~BEFORE':
      return !moment(left).isBefore(moment(right));
    case '~AFTER':
      return !moment(left).isAfter(moment(right));
    case 'LIKE':
      return new RegExp(right, 'g').test(String(left));
    case 'UNLIKE':
      return !new RegExp(right, 'g').test(String(left));
    case '~LIKE':
      return new RegExp(left, 'g').test(String(right));
    case '~UNLIKE':
      return !new RegExp(left, 'g').test(String(right));
    default:
      return false;
  }
}
export function getContextParams(paramList = [], { isConfig, ...others } = {}) {
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
      paramObj[item.paramKey] = getFieldValueByCode(
        item.paramUnitCode,
        item.paramFieldCode,
        others
      );
    }
  });
  return paramObj;
}
// 还原用户个性化配置数据
export async function resetUserCustConfig(params) {
  return getResponse(
    await request(`${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/unit-config/user-ui`, {
      method: 'DELETE',
      query: params,
    })
  );
}
export function getContext() {
  return {
    organizationId: getCurrentOrganizationId(),
    tenantId: getUserOrganizationId(),
  };
}

export function getFieldValueObject(relatedList = [], tools = {}, code) {
  const obj = {
    c: getContext(),
  };
  relatedList.forEach(({ unitCode, alias }) => {
    const newAlias = unitCode === code ? 'self' : alias;
    obj[newAlias] = getUnitDataByCode(unitCode, tools);
  });
  return obj;
}

function getFieldValueByCode(
  unitCode,
  fieldCode,
  { getArrayDataValue, getDataValue, getCacheType, index } = {}
) {
  const unitType = getCacheType(unitCode);
  switch (unitType) {
    case 'form':
      return getDataValue(unitCode)[fieldCode];
    case 'table':
      return getArrayDataValue(unitCode, index)[fieldCode];
    default:
  }
}
function getUnitDataByCode(
  unitCode,
  { getArrayDataValue, getDataValue, getCacheType, index } = {}
) {
  const unitType = getCacheType(unitCode);
  switch (unitType) {
    case 'form':
      return getDataValue(unitCode);
    case 'table':
      return getArrayDataValue(unitCode, index);
    default:
      return {};
  }
}
export function selfValidator(conValid = {}, config) {
  let { conLineList = [], conValidList = [] } = conValid;
  conLineList = isArray(conLineList) ? conLineList : [];
  conValidList = isArray(conValidList) ? conValidList : [];
  const result = calculateExpression(conLineList, config);
  const validation = [];
  conValidList.forEach((i) => {
    let newExpression = i.conExpression;
    const isErr = isErrConExpression(newExpression);
    if (!isErr) {
      const conNoReg = /(\d+)/g;
      newExpression = newExpression.replace(conNoReg, (_, m) => result[m] || false);
      newExpression = newExpression.replace(/AND/g, '&&').replace(/OR/g, '||');
      // eslint-disable-next-line no-new-func
      validation.push({
        // eslint-disable-next-line no-new-func
        isCorrect: new Function(`return ${newExpression};`)(),
        message: i.errorMessage,
      });
    }
  });
  if (validation.length > 0) {
    return {
      customize: true,
      validator: () => {
        for (let k = 0; k < validation.length; k += 1) {
          if (!validation[k].isCorrect) {
            // eslint-disable-next-line no-useless-return
            return validation[k].message;
          }
        }
        return true;
      },
    };
  }
  return {};
}

export function defaultValueFx(config, fieldConfig) {
  let { conLineList = [], conValidList = [] } = fieldConfig.defaultValueConDTO || {};
  conLineList = isArray(conLineList) ? conLineList : [];
  conValidList = isArray(conValidList) ? conValidList : [];
  const result = calculateExpression(conLineList, config);
  for (let i = 0; i < conValidList.length; i++) {
    const condition = conValidList[i];
    let { conExpression = '' } = condition;
    const isErr = isErrConExpression(conExpression);
    if (!isErr) {
      const conNoReg = /(\d+)/g;
      conExpression = conExpression.replace(conNoReg, (_, m) => result[m] || false);
      conExpression = conExpression.replace(/AND/g, '&&').replace(/OR/g, '||');
      // eslint-disable-next-line no-extra-boolean-cast
      if (!!new Function(`return ${conExpression};`)()) {
        const { value: defaultValue, valueMeaning: defaultValueMeaning } = condition;
        return { defaultValue, defaultValueMeaning };
      }
    }
  }
  const { defaultValue, defaultValueMeaning } = fieldConfig;
  return { defaultValue, defaultValueMeaning };
}
