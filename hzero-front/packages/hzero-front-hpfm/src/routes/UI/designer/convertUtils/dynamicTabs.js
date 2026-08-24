/**
 * Extra: 存储数据    转化为  设计器数据
 * Parse: 设计器数据  转化为    存储数据
 */

import { cloneDeep, forEach, isUndefined, slice } from 'lodash';
import { getNewUUIDKey, noop, commonParseForField } from './common';
import DataType from '../DataType';
import { convertParseTemplates, convertExtraTemplates } from '.';

/**
 * templateType 为 Tabs 的 tpl 添加进 设计器 需要做的格式转化操作
 * @param {object} template
 * @param {object} options
 * @param {boolean} options.noCloneDeep 不需要将 template 复制一份(即不改变原数据)
 * @returns {object} 经过转化后的 tpl
 */
function dynamicTabsInitExtra(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  return newTemplate;
}

/**
 * 当向 templateType 为 Tabs 的 tpl 添加 field 时 需要做的格式转化操作
 * @param {object} template - 设计器中的 tpl 数据
 * @param {object} field - 添加进 tpl 的 field
 * @param {object} options
 * @param {boolean} options.noCloneDeep - 不需要将 field 复制一份(即不改变原数据)
 * @returns {object} 经过转化后的 field
 */
function dynamicTabsInitExtraField(template, field, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newField = noCloneDeep ? field : cloneDeep(field);
  // newField.fieldLabel
  newField.fieldName = getNewUUIDKey();
  return newField;
}

/**
 * 从服务器中拿到的 templateType 为 Tabs 的tpl 转化成设计器的 格式转化
 * @param {object} template - 服务器中 存储的 tpl
 * @param {object} options
 * @param {boolean} options.noCloneDeep - 不需要将 template 复制一份(即不改变原数据)
 * @returns {object} 设计器需要的 tpl
 */
function dynamicTabsDirtyExtra(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  const { fields = [], children = [] } = newTemplate;
  forEach(fields, field => {
    const extraField = dynamicTabs.dirtyExtraFields[field.componentType] || noop;
    extraField(newTemplate, field);
    const tabsTemplates = slice(children, field.tplFrom, field.tplTo);
    // eslint-disable-next-line no-param-reassign
    field.children = convertExtraTemplates(tabsTemplates);
    return field;
  });
  delete newTemplate.children;
  return newTemplate;
}

/**
 * 从设计器中的数据 转化成 服务器需要的提交数据
 * @param {object} template - 需要转化的tpl
 * @param {?object} options
 * @param {?boolean} options.noCloneDeep - 不需要将 template 复制一份(即不改变原数据)
 * @returns {object} 服务器提交需要的 tpl
 */
function dynamicTabsDirtyParse(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  const newChildren = [];
  let orderSeq = 0;
  const orderSeqInc = 10;
  let tplFrom = -1;
  let tplTo = -1;
  forEach(newTemplate.fields, field => {
    orderSeq += orderSeqInc;
    // eslint-disable-next-line no-param-reassign
    field.orderSeq = orderSeq;
    const children = field.children || [];
    if (children.length > 0) {
      tplFrom = tplTo === -1 ? 0 : tplTo;
      tplTo = tplFrom + children.length;
      // 将 所有的 children 打平 放到 tabs 的 children 上
      newChildren.push(...convertParseTemplates(children));
      // eslint-disable-next-line no-param-reassign
      field.tplFrom = tplFrom;
      // eslint-disable-next-line no-param-reassign
      field.tplTo = tplTo;
    }
    // eslint-disable-next-line no-param-reassign
    delete field.children;
    commonParseForField(newTemplate, field);
    const parseField = dynamicTabs.dirtyParseFields[field.componentType] || noop;
    parseField(newTemplate, field);
  });
  newTemplate.children = newChildren;
  return newTemplate;
}

/**
 * 将 componentType 为 TabPane 的 额外数据 转化成 设计器数据
 * @param {object} template - 需要转化的field 所属的 template
 * @param {object} field - 需要转化的field
 * @param {?object} options
 */
// eslint-disable-next-line no-unused-vars
function dynamicTabsDirtyExtraFieldTabPane(template, field, options) {
  let tplFrom;
  let tplTo;
  const newConfig = [];
  forEach(field.config, cnf => {
    switch (cnf.attributeName) {
      case 'tplFrom':
        tplFrom = cnf.value;
        break;
      case 'tplTo':
        tplTo = cnf.value;
        break;
      default:
        newConfig.push(cnf);
        break;
    }
  });
  // eslint-disable-next-line no-param-reassign
  field.config = newConfig;
  // eslint-disable-next-line no-param-reassign
  field.tplFrom = isUndefined(tplFrom) ? -1 : tplFrom;
  // eslint-disable-next-line no-param-reassign
  field.tplTo = isUndefined(tplTo) ? -1 : tplTo;
}

/**
 * 将从服务器取得的数据 Tabs 的 field componentType 为 TabPane 的 field 转化成 设计器数据
 * @param {object} template- 需要转化的field 所属的 template
 * @param {object} field - 需要转化的field
 * @param {?object} options
 */
// eslint-disable-next-line no-unused-vars
function dynamicTabsDirtyParseFieldTabPane(template, field, options) {
  const { config: newConfig = [], tplFrom = -1, tplTo = -1 } = field;
  newConfig.push({
    attributeName: 'tplFrom',
    attributeType: DataType.Number,
    value: tplFrom,
  });
  newConfig.push({
    attributeName: 'tplTo',
    attributeType: DataType.Number,
    value: tplTo,
  });
  // eslint-disable-next-line no-param-reassign
  field.config = newConfig;
  // eslint-disable-next-line no-param-reassign
  delete field.tplFrom;
  // eslint-disable-next-line no-param-reassign
  delete field.tplTo;
}

const dynamicTabs = {
  initExtra: dynamicTabsInitExtra,
  initExtraField: dynamicTabsInitExtraField,
  dirtyExtra: dynamicTabsDirtyExtra,
  dirtyParse: dynamicTabsDirtyParse,
  dirtyExtraFields: {
    TabPane: dynamicTabsDirtyExtraFieldTabPane,
  },
  dirtyParseFields: {
    TabPane: dynamicTabsDirtyParseFieldTabPane,
  },
};

export default dynamicTabs;
