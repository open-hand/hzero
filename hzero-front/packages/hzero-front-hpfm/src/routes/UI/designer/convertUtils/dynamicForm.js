/**
 * Extra: 存储数据    转化为  设计器数据
 * Parse: 设计器数据  转化为    存储数据
 */

/* eslint-disable no-param-reassign */

import { cloneDeep, forEach, isArray, isNumber } from 'lodash';

import DataType from '../DataType';

import { defaultFormCol, emptyField, emptyFieldType } from '../config';

import { commonParseForField } from './common';

/**
 * @param {object} template
 * @return {object} 将初始化的 template 转为 设计器 适用的数据
 */
function dynamicFormInitExtra(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  // todo 删除表格多余的属性

  const newFields = [];
  // 查找 form 的 col 属性
  let col;
  forEach(newTemplate.config, prop => {
    if (prop.attributeName === 'col') {
      col = prop.value;
      return false;
    }
  });
  col = col || defaultFormCol;
  const row = [];
  // 如果没有字段, 则将 第一行 都补上空字段
  for (let index = 0; index < col; index += 1) {
    row.push(cloneDeep(emptyField));
  }
  newFields.push(row);
  return {
    ...newTemplate,
    fields: newFields,
  };
}

/**
 * 将 存储的 dynamicForm 转换成 设计器 可以识别的
 * 认为组件的 col 和 组件字段的 leftOffset 与 rightOffset 对得上
 * @param {object} template - 新增 或 从服务器查询出来的 dynamicForm
 * @return {object} newTemplate 新的为设计器准备好的字段
 */
function dynamicFormDirtyExtra(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  // todo 删除表格多余的属性

  const newFields = [];
  // 查找 form 的 col 属性
  let col;
  forEach(newTemplate.config, prop => {
    if (prop.attributeName === 'col') {
      col = prop.value;
      return false;
    }
  });
  col = col || defaultFormCol;
  let walkerCount = 0;
  let row = [];
  forEach(newTemplate.fields, field => {
    let fieldWalkerCount = 0;
    if (isNumber(field.leftOffset)) {
      // 字段的左空位 其实就是编辑时候的空字段
      // 在字段有左空位的时候 补 空字段
      while (fieldWalkerCount < field.leftOffset) {
        row.push(cloneDeep(emptyField));
        fieldWalkerCount += 1;
      }
    }
    walkerCount += fieldWalkerCount;
    fieldWalkerCount = 0;
    row.push(field);
    if (isNumber(field.colspan) && field.colspan) {
      walkerCount += field.colspan;
    } else {
      walkerCount += 1;
    }
    if (isNumber(field.rightOffset)) {
      // 字段的右空位 其实就是编辑时候的空字段
      // 在字段有右空位的时候 补 空字段
      while (fieldWalkerCount < field.rightOffset) {
        row.push(cloneDeep(emptyField));
        fieldWalkerCount += 1;
      }
    }
    walkerCount += fieldWalkerCount;
    if (walkerCount >= col) {
      // 将 表单的 字段 转换成 二维数组
      newFields.push(row);
      row = [];
      walkerCount = 0;
    }
  });
  // 也许数据库中出现问题了，leftOffset rightOffset 和字段对不上
  // if(row.length > 0){
  //   newFields.push(row);
  // }
  if (newFields.length === 0) {
    // 如果没有字段, 则将 第一行 都补上空字段
    for (let index = 0; index < col; index += 1) {
      row.push(cloneDeep(emptyField));
    }
    newFields.push(row);
  }
  return {
    ...newTemplate,
    fields: newFields,
  };
}

/**
 * 将 设计器的 dynamicForm 转换成 存储的数据
 * @param {object} template - 设计器的数据
 * @return {object} newTemplate 准备存储的数据
 */
function dynamicFormDirtyParse(template, options) {
  const noCloneDeep = options && options.noCloneDeep;
  const newTemplate = noCloneDeep ? template : cloneDeep(template);
  const newFields = [];
  // 从 第一行列 中 得到新的 col 值
  let col = 0;
  forEach(newTemplate.fields[0], f => {
    if (f.colspan && isNumber(f.colspan)) {
      col += f.colspan;
    } else {
      col += 1;
    }
  });
  let hasColAttribute = false;
  forEach(newTemplate.config, prop => {
    if (prop.attributeName === 'col') {
      prop.value = col;
      hasColAttribute = true;
      return false;
    }
  });
  if (!hasColAttribute) {
    if (isArray(newTemplate.config)) {
      newTemplate.config.push({
        attributeName: 'col',
        value: col,
        attributeType: DataType.Number,
      });
    } else {
      newTemplate.config = [
        {
          attributeName: 'col',
          value: col,
          attributeType: DataType.Number,
        },
      ];
    }
  }

  // reset fields, and remove some attr
  let orderSeq = 0;
  const orderSeqStep = 10;
  forEach(newTemplate.fields, fArr => {
    let leftOffset = 0;
    let rightOffset = 0;
    let prevField = null;
    forEach(fArr, f => {
      commonParseForField(newTemplate, f);
      if (f.componentType === emptyFieldType) {
        if (prevField === null) {
          leftOffset += 1;
        } else {
          rightOffset += 1;
        }
      } else {
        f.leftOffset = leftOffset;
        if (prevField !== null) {
          prevField.rightOffset = rightOffset;
        }
        prevField = f;
        leftOffset = 0;
        rightOffset = 0;
        orderSeq += orderSeqStep;

        f.orderSeq = orderSeq;
        newFields.push(f);
      }
    });
    if (prevField !== null) {
      prevField.rightOffset = rightOffset;
    }
  });

  newTemplate.fields = newFields;

  return newTemplate;
}

/**
 * DynamicForm 的 Field 新增 在 组件内部处理
 * @param {object} template
 * @param {object} field
 * @param {object} options
 * @returns
 */
// eslint-disable-next-line no-unused-vars
function dynamicFormInitExtraField(template, field, options) {}

const dynamicForm = {
  initExtra: dynamicFormInitExtra,
  initExtraField: dynamicFormInitExtraField,
  dirtyExtra: dynamicFormDirtyExtra,
  dirtyParse: dynamicFormDirtyParse,
};

export default dynamicForm;
