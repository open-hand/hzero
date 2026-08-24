/**
 * convertUtils.js
 * 对 设计器数据 和 存储数据的 相互转换
 * Extra: 存储数据    转化为  设计器数据
 * Parse: 设计器数据  转化为    存储数据
 * @date 2018/10/22
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

/* eslint-disable no-param-reassign */

import { forEach, cloneDeep } from 'lodash';

import intl from 'utils/intl';

import dynamicForm from './dynamicForm';
import dynamicTable from './dynamicTable';
import dynamicToolbar from './dynamicToolbar';
import dynamicModal from './dynamicModal';
import dynamicTabs from './dynamicTabs';
import { getNewUUIDKey } from './common';

/**
 * 将对应的数据 转成 设计器接收的数据
 * @export
 * @param {object[]} templates
 * @param {object[]}
 */
export function convertExtraTemplates(templates, options = {}) {
  const newTemplates = [];
  forEach(templates, template => {
    let newTemplate;
    switch (template.templateType) {
      case 'DynamicForm':
        newTemplate = dynamicForm.dirtyExtra(template, options);
        break;
      case 'DynamicTable':
        newTemplate = dynamicTable.dirtyExtra(template, options);
        break;
      case 'DynamicToolbar':
        newTemplate = dynamicToolbar.dirtyExtra(template, options);
        break;
      case 'DynamicModal':
        newTemplate = dynamicModal.dirtyExtra(template, options);
        break;
      case 'DynamicTabs':
        newTemplate = dynamicTabs.dirtyExtra(template, options);
        break;
      default:
        break;
    }
    if (newTemplate) {
      newTemplate.renderKey = getNewUUIDKey();
      // 删除公共的 template 对 设计器 多余的属性
      delete newTemplate.name;
      delete newTemplate.accepts;
      delete newTemplate.defaultIntlDescription;
      newTemplates.push(newTemplate);
    }
  });
  return newTemplates;
}

/**
 * 获取所有模板的保存数据
 * @export
 * @param {object[]} templates
 * @param {!object} options
 */
export function convertParseTemplates(templates, options = {}) {
  const { refs = {}, ...newOptions } = options;
  const newTemplates = [];
  let orderSeq = 10;
  const orderSeqIncrement = 100;
  forEach(templates, template => {
    let newTemplate;
    const { renderKey } = template;
    switch (template.templateType) {
      case 'DynamicForm':
        newTemplate = dynamicForm.dirtyParse(template, newOptions);
        break;
      case 'DynamicTable':
        newOptions.orderKeys = refs[renderKey].state.orderKeys;
        newOptions.originKeyMapIndex = refs[renderKey].state.originKeyMapIndex;
        newTemplate = dynamicTable.dirtyParse(template, newOptions);
        break;
      case 'DynamicToolbar':
        newTemplate = dynamicToolbar.dirtyParse(template, newOptions);
        break;
      case 'DynamicModal':
        newTemplate = dynamicModal.dirtyParse(template, newOptions);
        break;
      case 'DynamicTabs':
        newTemplate = dynamicTabs.dirtyParse(template, newOptions);
        break;
      default:
        break;
    }
    if (newTemplate) {
      newTemplate.orderSeq = orderSeq;
      orderSeq += orderSeqIncrement;
      if (newTemplate.isCreate) {
        delete newTemplate.isCreate;
        delete newTemplate.id;
      }
      delete newTemplate.rootNode;
      delete newTemplate.childNode;
      delete newTemplate.renderKey;
      newTemplates.push(newTemplate);
    }
  });
  return newTemplates;
}

/**
 * template 刚加进 设计器 的格式化
 * @export
 * @param {object} template
 */
export function convertInitExtraTemplate(template, options = {}) {
  let newTemplate;
  switch (template.templateType) {
    case 'DynamicForm':
      newTemplate = dynamicForm.initExtra(template, options);
      break;
    case 'DynamicTable':
      newTemplate = dynamicTable.initExtra(template, options);
      break;
    case 'DynamicToolbar':
      newTemplate = dynamicToolbar.initExtra(template, options);
      break;
    case 'DynamicModal':
      newTemplate = dynamicModal.initExtra(template, options);
      break;
    case 'DynamicTabs':
      newTemplate = dynamicTabs.initExtra(template, options);
      break;
    default:
      break;
  }
  if (newTemplate) {
    // 处理公共的属性
    newTemplate.templateCode = getNewUUIDKey();
    newTemplate.renderKey = getNewUUIDKey();
    newTemplate.isCreate = true;
    newTemplate.id = getNewUUIDKey();
    newTemplate.description = intl
      .get(newTemplate.description)
      .d(newTemplate.defaultIntlDescription);
    // 删除其他的属性
    delete newTemplate.accepts;
    delete newTemplate.className;
    delete newTemplate.name;
    delete newTemplate.defaultIntlDescription;
  }
  return newTemplate;
}

/**
 * field 刚加进 设计器 的格式化
 * @export
 * @param {object} template
 * @param {object} field
 * @param {object} options
 * @returns
 */
export function convertInitExtraField(template, field, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const updateField = noCloneDeep ? field : cloneDeep(field);
  updateField.isCreate = true;
  updateField.fieldId = getNewUUIDKey();
  updateField.fieldName = getNewUUIDKey();
  // 删除 field 的其他属性
  delete updateField.className;
  delete updateField.name;
  const newOptions = { ...options, noCloneDeep: true };
  switch (template.templateType) {
    case 'DynamicForm':
      dynamicForm.initExtraField(template, updateField, newOptions);
      break;
    case 'DynamicTable':
      dynamicTable.initExtraField(template, updateField, newOptions);
      break;
    case 'DynamicToolbar':
      dynamicToolbar.initExtraField(template, updateField, newOptions);
      break;
    case 'DynamicModal':
      dynamicModal.initExtraField(template, updateField, newOptions);
      break;
    case 'DynamicTabs':
      dynamicTabs.initExtraField(template, updateField, newOptions);
      break;
    default:
      break;
  }
  return updateField;
}
