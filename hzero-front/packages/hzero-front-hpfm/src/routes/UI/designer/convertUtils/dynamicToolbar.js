/**
 * Extra: 存储数据    转化为  设计器数据
 * Parse: 设计器数据  转化为    存储数据
 */
/* eslint-disable no-param-reassign */

import { cloneDeep, forEach, map } from 'lodash';

import { commonParseForField } from './common';

/**
 * 将 存储的 dynamicToolbar 转换成 设计器 可以识别的
 * @param {object} template
 * @param {object} options
 * @returns {object}
 */
function dynamicToolbarDirtyExtra(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  newTemplate.fields = map(newTemplate.fields, field => {
    const props = {};
    forEach(field.config, prop => {
      props[prop.attributeName] = prop.value;
    });
    return {
      ...field,
      type: props.type,
      style: {
        marginRight: props['style.marginRight'],
      },
    };
  });
  return newTemplate;
}

function dynamicToolbarDirtyParse(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  let orderSeq = 0;
  const orderSeqStep = 10;
  const newFields = [];
  forEach(newTemplate.fields, field => {
    commonParseForField(newTemplate, field);

    delete field.style; // warn no style on field

    delete field.type; // warn no type on field
    newFields.push({
      ...field,
      orderSeq: (orderSeq += orderSeqStep),
      // todo toolbar是否没有这两个字段
      enabledFlag: 1,
      requiredFlag: 0,
    });
  });
  return {
    ...newTemplate,
    fields: newFields,
  };
}

function dynamicToolbarInitExtra(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
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
function dynamicToolbarInitExtraField(template, field, options) {}

const dynamicToolbar = {
  initExtra: dynamicToolbarInitExtra,
  initExtraField: dynamicToolbarInitExtraField,
  dirtyExtra: dynamicToolbarDirtyExtra,
  dirtyParse: dynamicToolbarDirtyParse,
};

export default dynamicToolbar;
