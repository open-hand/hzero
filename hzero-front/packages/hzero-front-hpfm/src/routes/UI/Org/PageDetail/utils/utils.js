/* eslint-disable no-param-reassign */
/**
 * utils.js
 * @date 2018-12-04
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */
import { cloneDeep, forEach, isNumber, map, set, startsWith } from 'lodash';

import { hiddenColumnPrefix } from 'components/DynamicComponent/DynamicTable/utils';

import { defaultFormCol, emptyField } from './config';

export function processPageConfig(pageConfig = {}) {
  const newPageConfig = cloneDeep(pageConfig);
  // config 的 fields 需要分别处理
  const tpls = newPageConfig.fields || [];
  const processedTpls = [];
  for (let tplIndex = 0; tplIndex < tpls.length; tplIndex += 1) {
    const tpl = tpls[tplIndex] || {};
    let processedTpl;
    switch (tpl.templateType) {
      case 'DynamicForm':
        processedTpl = processDynamicForm(tpl);
        break;
      case 'DynamicToolbar':
        processedTpl = processDynamicToolbar(tpl);
        break;
      case 'DynamicTable':
        processedTpl = processDynamicTable(tpl);
        break;
      default:
        processedTpl = tpl;
        break;
    }
    processedTpls.push(processedTpl);
  }
  newPageConfig.fields = processedTpls;
  return newPageConfig;
}

/**
 * 会直接改变参数
 * @param {object} dynamicForm
 * @returns {object} dynamicForm
 */
function processDynamicForm(dynamicForm) {
  // 认为组件的 col 和 组件字段的 leftOffset 与 rightOffset 对得上
  const newFields = [];
  // 处理组件的 属性
  const formProps = {};
  forEach(dynamicForm.config, prop => {
    formProps[prop.attributeName] = prop.value;
  });
  const col = formProps.col || defaultFormCol;
  let walkerCount = 0;
  let row = [];
  forEach(dynamicForm.fields, field => {
    let fieldWalkerCount = 0;
    if (isNumber(field.leftOffset)) {
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
      while (fieldWalkerCount < field.rightOffset) {
        row.push(cloneDeep(emptyField));
        fieldWalkerCount += 1;
      }
    }
    walkerCount += fieldWalkerCount;
    if (walkerCount >= col) {
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
    for (let index = 0; index < col; index += 1) {
      row.push(cloneDeep(emptyField));
    }
    newFields.push(row);
  }
  return {
    ...dynamicForm,
    fields: newFields,
  };
}

/**
 * 会直接改变参数
 * @param {object} dynamicToolbar
 * @returns {object} dynamicToolbar
 */
function processDynamicToolbar(dynamicToolbar) {
  dynamicToolbar.fields = map(dynamicToolbar.fields, field => {
    const props = {};
    forEach(field.config, prop => {
      props[prop.attributeName] = prop.value;
    });
    return {
      ...field,
      type: props.type,
      style: { marginRight: props['style.marginRight'] },
    };
  });
  return dynamicToolbar;
}

/**
 * 会直接改变参数
 * @param {object} dynamicTable
 * @returns {object} dynamicTable
 */
function processDynamicTable(dynamicTable) {
  const props = {};
  const hiddenColumns = [];
  const newConfig = [];
  forEach(dynamicTable.config, prop => {
    if (startsWith(prop.attributeName, hiddenColumnPrefix)) {
      hiddenColumns.push(prop);
    } else {
      newConfig.push(prop);
      props[prop.attributeName] = prop.value;
    }
  });
  dynamicTable.config = newConfig;
  dynamicTable.hiddenColumns = hiddenColumns;
  dynamicTable.fields = map(dynamicTable.fields, field => {
    switch (field.componentType) {
      case 'LinkButton':
        return processDynamicTableLinkButtonField(field, dynamicTable);
      default:
        return field;
    }
  });

  dynamicTable.pagination = props.pagination;
  if (props.pagination) {
    dynamicTable.defaultPageSize = props.defaultPageSize;
  }
  return dynamicTable;
}

/**
 * 处理 table 行按钮
 * @param {object} linkButton
 // * @param {object} dynamicTable
 * @returns {object}
 */
function processDynamicTableLinkButtonField(linkButton) {
  // todo 确定 DynamicTable 的 linkButton 没有其他的属性值, 且只有一级属性
  const props = {};
  const btns = [];
  forEach(linkButton.config, attr => {
    set(props, attr.attributeName, attr);
  });
  Object.keys(props).forEach(btnKey => {
    const btnProps = props[btnKey];
    const btnConfig = [];
    let orderSeq;
    Object.keys(btnProps).forEach(btnPropKey => {
      switch (btnPropKey) {
        case 'orderSeq':
          orderSeq = btnProps[btnPropKey].value;
          break;
        default:
          break;
      }
      // 将对应的 attr 放进按钮中
      btnProps[btnPropKey].attributeName = btnPropKey;
      btnConfig.push(btnProps[btnPropKey]);
    });
    btns[orderSeq] = btnConfig;
    // btns.push(btnConfig);
  });
  linkButton.btns = btns;
  return linkButton;
}

/**
 * 获取 字段组件的属性
 * @param component
 */
export function getFieldProps(component) {
  const fieldProps = {};
  if (component.enabledFlag !== 1) {
    fieldProps.disabled = true;
  }
  switch (component.componentType) {
    case 'DatePicker':
      fieldProps.placeholder = '';
      break;
    default:
      break;
  }
  return fieldProps;
}
