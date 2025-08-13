/**
 * config.js
 * @date 2018-12-04
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import { kebabCase } from 'lodash';
import PropTypes from 'prop-types';

export const defaultFormCol = 2;

export const emptyFieldType = 'empty';

export const emptyField = {
  componentType: emptyFieldType,
};

export const fields = {
  // form
  Input: {
    componentType: 'Input',
    kebabCaseCode: kebabCase('Input'),
    config: [], // 默认属性
    fieldName: 'input',
    fieldLabel: '文本', // intl 国际化 的默认值
    description: '文本', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  InputNumber: {
    componentType: 'InputNumber',
    kebabCaseCode: kebabCase('InputNumber'),
    config: [], // 默认属性
    fieldName: 'inputNumber',
    fieldLabel: '数值框', // intl 国际化 的默认值
    description: '数值框', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  TextArea: {
    componentType: 'TextArea',
    kebabCaseCode: kebabCase('TextArea'),
    config: [], // 默认属性
    fieldName: 'textArea',
    fieldLabel: '多行文本', // intl 国际化 的默认值
    description: '多行文本', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  DatePicker: {
    componentType: 'DatePicker',
    kebabCaseCode: kebabCase('DatePicker'),
    config: [], // 默认属性
    fieldName: 'datePicker',
    fieldLabel: '日期', // intl 国际化 的默认值
    description: '日期', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  ValueList: {
    componentType: 'ValueList',
    kebabCaseCode: kebabCase('ValueList'),
    config: [], // 默认属性
    fieldName: 'valueList',
    fieldLabel: '下拉框', // intl 国际化 的默认值
    description: '下拉框', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  Lov: {
    componentType: 'Lov',
    kebabCaseCode: kebabCase('Lov'),
    config: [], // 默认属性
    fieldName: 'lov',
    fieldLabel: 'Lov', // intl 国际化 的默认值
    description: 'Lov', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  Switch: {
    componentType: 'Switch',
    kebabCaseCode: kebabCase('Switch'),
    config: [], // 默认属性
    fieldName: 'switch',
    fieldLabel: '开关', // intl 国际化 的默认值
    description: '开关', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  Checkbox: {
    componentType: 'Checkbox',
    kebabCaseCode: kebabCase('Checkbox'),
    config: [], // 默认属性
    fieldName: 'checkbox',
    fieldLabel: 'checkbox', // intl 国际化 的默认值
    description: 'checkbox', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  // toolbar
  Button: {
    componentType: 'Button',
    kebabCaseCode: kebabCase('Button'),
    config: [], // 默认属性
    fieldName: 'button',
    fieldLabel: '按钮', // intl 国际化 的默认值
    description: '按钮', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  // table
  LinkButton: {
    componentType: 'LinkButton',
    kebabCaseCode: kebabCase('LinkButton'),
    config: [], // 默认属性
    fieldName: 'linkButton',
    fieldLabel: '行内按钮', // intl 国际化 的默认值
    description: '行内按钮', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
  ColumnField: {
    componentType: 'ColumnField',
    kebabCaseCode: kebabCase('ColumnField'),
    config: [], // 默认属性
    fieldName: 'columnField',
    fieldLabel: '列字段', // intl 国际化 的默认值
    description: '列字段', // intl 国际化 的默认值
    requiredFlag: 0, // 是否必输
    enabledFlag: 1, // 是否启用
    visibleFlag: 1, // 是否显示（不隐藏）
  },
};

/**
 * 所有的 容器 tpl
 * 所有组件的国际化都使用 hpfm.uiPage.component.type[componentCode]
 * @example hpfm.uiPage.component.type.DynamicForm
 *
 * 在 pickBox 重的样式均为 .component-[to]
 *
 */
export const templates = {
  DynamicForm: {
    templateType: 'DynamicForm',
    kebabCaseCode: kebabCase('DynamicForm'),
    config: [], // 默认的属性
    accepts: [
      fields.Input,
      fields.InputNumber,
      fields.TextArea,
      fields.DatePicker,
      fields.ValueList,
      fields.Lov,
      fields.Switch,
      fields.Checkbox,
    ], // tpl 的字段
    enabledFlag: 1, // 是否启用
    description: '表单', // intl 国际化 的默认值
  },
  DynamicTable: {
    templateType: 'DynamicTable',
    kebabCaseCode: kebabCase('DynamicTable'),
    config: [], // 默认的属性
    accepts: [fields.LinkButton, fields.ColumnField], // tpl 的字段
    enabledFlag: 1, // 是否启用
    description: '表格', // intl 国际化 的默认值
  },
  DynamicToolbar: {
    templateType: 'DynamicToolbar',
    kebabCaseCode: kebabCase('DynamicToolbar'),
    config: [], // 默认的属性
    accepts: [fields.Button], // tpl 的字段
    enabledFlag: 1, // 是否启用
    description: '按钮组', // intl 国际化 的默认值
  },
};

export const dndTypes = {
  // drag
  pickBoxDragTemplate: 'PickBoxDragTemplate',
  pickBoxDragField: 'PickBoxDragTemplate',
  designerDragTemplate: 'DesignerDragTemplate',
  designerDragField: 'DesignerDragField',
  // designerDragComponent: 'DesignerDragComponent', // 拖动 组件 和 容器 二和一
  // drop
  designerDropTemplate: [], // 容器接收drop的组件
  designerDropField: [], // 容器接收drop的组件
};

dndTypes.designerDropTemplate = [
  dndTypes.pickBoxDragTemplate,
  dndTypes.pickBoxDragField,
  dndTypes.designerDragField,
  dndTypes.designerDragTemplate,
];
// 字段交换顺序
dndTypes.designerDropField = [dndTypes.designerDragField, dndTypes.pickBoxDragField];

/**
 * 页面设计区域的公共属性
 */
export const DesignerCommonPropTypes = {
  template: PropTypes.object.isRequired,
  currentEditTemplate: PropTypes.object,
  currentEditField: PropTypes.object,
  onUpdateCurrentEditTemplate: PropTypes.func,
  onUpdateCurrentEditField: PropTypes.func,
};
