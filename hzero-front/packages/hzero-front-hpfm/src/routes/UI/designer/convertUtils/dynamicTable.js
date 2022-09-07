/**
 * Extra: 存储数据    转化为  设计器数据
 * Parse: 设计器数据  转化为    存储数据
 */

/* eslint-disable no-param-reassign */
import { cloneDeep, forEach, map, set, startsWith } from 'lodash';

import { modalBtnPrefix, subEventPrefix } from 'components/DynamicComponent/config';
import { hiddenColumnPrefix } from 'components/DynamicComponent/DynamicTable/utils';

import DataType from '../DataType';

import { noop, commonParseForField, getNewUUIDKey } from './common';

/**
 * 将 存储的 dynamicTable 转换成 设计器 可以识别的
 * @param {object} template
 * @param {object} options
 * @returns {object}
 */
function dynamicTableDirtyExtra(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  const props = {};
  const hiddenColumns = [];
  const newConfig = [];
  forEach(newTemplate.config, prop => {
    if (startsWith(prop.attributeName, hiddenColumnPrefix)) {
      hiddenColumns.push(prop);
    } else {
      newConfig.push(prop);
      props[prop.attributeName] = prop.value;
    }
  });
  newTemplate.config = newConfig;
  newTemplate.hiddenColumns = hiddenColumns;
  // 批量处理 DynamicTable 的 字段 (虽然只处理了 LinkButton)
  newTemplate.fields = map(newTemplate.fields, field => {
    const extraField = dynamicTable.dirtyExtraFields[field.componentType] || noop;
    extraField(newTemplate, field);
    return field;
  });

  newTemplate.pagination = props.pagination;
  if (props.pagination) {
    newTemplate.defaultPageSize = props.defaultPageSize;
  }
  return newTemplate;
}

function dynamicTableInitExtra(template, options = {}) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  return newTemplate;
}

function dynamicTableDirtyParse(template, options = {}) {
  const { orderKeys, originKeyMapIndex, noCloneDeep } = options || {};
  const newTemplate = noCloneDeep ? template : cloneDeep(template);

  let orderSeq = 0;
  const orderSeqStep = 10;
  const newFields = [];
  forEach(orderKeys, key => {
    const field = newTemplate.fields[originKeyMapIndex[key]];
    commonParseForField(newTemplate, field);
    const parseField = dynamicTable.dirtyParseFields[field.componentType] || noop;
    parseField(newTemplate, field);
    newFields.push({
      ...field,
      orderSeq: (orderSeq += orderSeqStep),
      // todo table是否没有这两个字段
      enabledFlag: 1,
      requiredFlag: 0,
    });
  });
  newTemplate.fields = newFields;
  newTemplate.enabledFlag = 1; // warn dynamicTable enabledFlag 字段必输, 但是没有地方编辑
  newTemplate.config = newTemplate.config || [];
  forEach(newTemplate.hiddenColumns, hiddenColumn => {
    newTemplate.config.push(hiddenColumn);
  });
  delete newTemplate.hiddenColumns;
  delete newTemplate.pagination; // warn dynamicTable's attr of pagination use in designer
  delete newTemplate.defaultPageSize; // warn dynamicTable's attr of defaultPageSize use in designer
  return newTemplate;
}

/**
 * 新加进来的 字段 宽度统一为 120
 * @param {object} template
 * @param {object} field
 * @param {object} options
 * @returns
 */
// eslint-disable-next-line no-unused-vars
function dynamicTableInitExtraField(template, field, options) {
  field.width = 120;
  // 每次 Table 的 fields 顺序 和 数量 发生变化 都必须是新的 设计器
  template.renderKey = getNewUUIDKey();
}

/**
 * @param {object} template
 * @param {object} field
 * @param {object} options
 * @returns
 */
// eslint-disable-next-line no-unused-vars
function dynamicTableDirtyExtraFieldLinkButton(template, field, options) {
  // todo 确定 DynamicTable 的 linkButton 没有其他的属性值, 且只有一级属性
  const props = {};
  const btns = [];
  forEach(field.config, attr => {
    set(props, attr.attributeName, attr);
  });
  Object.keys(props).forEach(btnKey => {
    const btnProps = props[btnKey];
    const btnConfig = [];
    let orderSeq;
    Object.keys(btnProps).forEach(btnPropKey => {
      if (btnPropKey === 'orderSeq') {
        orderSeq = btnProps[btnPropKey].value;
      } else if (btnPropKey === 'modalBtns') {
        btnConfig.push({
          attributeName: 'modalBtns',
          value: btnProps.modalBtns,
        });
        return;
      } else if (btnPropKey === 'subEvents') {
        btnConfig.push({
          attributeName: 'subEvents',
          value: btnProps.subEvents,
        });
        return;
      }
      // 将对应的 attr 放进按钮中
      btnProps[btnPropKey].attributeName = btnPropKey;
      btnConfig.push(btnProps[btnPropKey]);
    });
    btns[orderSeq] = btnConfig;
    // btns.push(btnConfig);
  });
  field.btns = btns;
  return field;
}

function dynamicTableDirtyParseFieldLinkButton(template, field) {
  // todo 确定 DynamicTable 的 linkButton 没有其他的属性值, 且只有一级属性
  const config = [];
  forEach(field.btns, (btnConfigs, index) => {
    let btnKey;
    forEach(btnConfigs, attr => {
      switch (attr.attributeName) {
        case 'btnKey':
          btnKey = attr.value; // 找到 btnKey 的属性
          break;
        default:
          break;
      }
    });
    config.push({
      attributeName: `[${btnKey}][orderSeq]`,
      attributeType: DataType.Number,
      value: index,
    });
    // 过滤掉 orderSeq 属性
    forEach(btnConfigs, btnConfig => {
      if (btnConfig.attributeName === 'modalBtns') {
        // 设置 modalBtns 的属性
        btnConfig.attributeName = `[${btnKey}]${btnConfig.attributeName}`;
        const modalBtns = btnConfig.value;
        for (let i = 0; i < modalBtns.length; i += 1) {
          config.push({
            attributeName: `[${btnKey}]${modalBtnPrefix}[${i}]`,
            value: modalBtns[i].value,
            attributeType: DataType.String,
          });
        }
      } else if (btnConfig.attributeName === 'subEvents') {
        // 设置 subEvents 的属性
        btnConfig.attributeName = `[${btnKey}]${btnConfig.attributeName}`;
        const subEvents = btnConfig.value;
        for (let i = 0; i < subEvents.length; i += 1) {
          config.push({
            attributeName: `[${btnKey}]${subEventPrefix}[${i}]`,
            attributeType: DataType.String,
            value: subEvents[i].value,
          });
        }
      } else if (btnConfig.attributeName !== 'orderSeq') {
        btnConfig.attributeName = `[${btnKey}][${btnConfig.attributeName}]`;
        config.push(btnConfig);
      }
    });
  });
  delete field.btns;
  field.config = config;
}
const dynamicTable = {
  initExtra: dynamicTableInitExtra,
  initExtraField: dynamicTableInitExtraField,
  dirtyExtra: dynamicTableDirtyExtra,
  dirtyParse: dynamicTableDirtyParse,
  dirtyExtraFields: {
    LinkButton: dynamicTableDirtyExtraFieldLinkButton,
  },
  dirtyParseFields: {
    LinkButton: dynamicTableDirtyParseFieldLinkButton,
  },
};
export default dynamicTable;
