/**
 * utils
 * 个性化用到的方法和数据
 * @date 2018/9/27
 * @author WY yang.wang06@hand-china.com
 * @copyright (c) 2018 Hand
 */

import React from 'react';
import { forEach, isArray, map, some } from 'lodash';
import { Select } from 'hzero-ui';

import DynamicFormComponent from 'components/DynamicComponent/DynamicForm';
import DynamicTableComponent from 'components/DynamicComponent/DynamicTable';

import intl from 'utils/intl';

import DataType from './DataType';

import { fieldLabelProp, attributeTypeProp, attributeNameProp, attributeValueProp } from './config';

// const commonComponentInitialValues = {};
const commonFieldInitialValues = {
  enabledFlag: 1,
  visiableFlag: 1,
  requiredFlag: 0,
};
// 组件（表单）
const Input = {
  ...commonFieldInitialValues,
  name: '文本',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.input').d('文本'),
  componentType: 'Input',
  className: 'inputClassName',
  config: [],
};
const InputNumber = {
  ...commonFieldInitialValues,
  name: '数值',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.inputNumber').d('数值'),
  componentType: 'InputNumber',
  className: 'numberClassName',
  config: [],
};
const TextArea = {
  ...commonFieldInitialValues,
  name: '多行文本',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.textArea').d('多行文本'),
  componentType: 'TextArea',
  className: 'inputClassName',
  config: [],
};
const DatePicker = {
  ...commonFieldInitialValues,
  name: '日期',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.datePicker').d('日期'),
  componentType: 'DatePicker',
  className: 'dateClassName',
  config: [],
};
const ValueList = {
  ...commonFieldInitialValues,
  name: '下拉列表',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.valueList').d('下拉列表'),
  componentType: 'ValueList',
  className: 'selectClassName',
  config: [
    {
      [attributeNameProp]: 'lazyLoad',
      [attributeValueProp]: true,
      [attributeTypeProp]: DataType.Boolean,
    },
  ],
};
const Lov = {
  ...commonFieldInitialValues,
  name: 'Lov',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.lov').d('Lov'),
  componentType: 'Lov',
  className: 'lovClassName',
  config: [],
};
const Switch = {
  ...commonFieldInitialValues,
  name: '开关',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.switch').d('开关'),
  componentType: 'Switch',
  className: 'switchClassName',
  config: [],
};
const Checkbox = {
  ...commonFieldInitialValues,
  name: 'checkbox',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.checkbox').d('checkbox'),
  componentType: 'Checkbox',
  className: 'checkboxClassName',
  config: [],
};
const Button = {
  ...commonFieldInitialValues,
  name: '按钮',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.button').d('按钮'),
  componentType: 'Button',
  className: 'buttonClassName',
  config: [
    {
      [attributeNameProp]: 'style.marginRight',
      [attributeTypeProp]: DataType.Number,
      [attributeValueProp]: 20,
    },
  ],
};
// 组件（表格）
const LinkButton = {
  ...commonFieldInitialValues,
  name: '行按钮',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.linkButton').d('行按钮'),
  componentType: 'LinkButton',
  className: 'buttonClassName',
  config: [],
  btns: [],
};
const ColumnField = {
  ...commonFieldInitialValues,
  name: '列字段',
  [fieldLabelProp]: intl.get('hpfm.ui.model.fieldType.columnField').d('列字段'),
  componentType: 'ColumnField',
  className: 'buttonClassName',
  config: [],
};

// 组件（Tabs）
export const TabPane = {
  ...commonFieldInitialValues,
  name: '标签',
  fieldLabel: intl.get('hpfm.ui.model.fieldType.TabPane').d('标签'),
  componentType: 'TabPane',
  className: 'modalClassName',
  config: [],
};

// 导出的所有 TPL

export const templates = {};

// 表格
const DynamicTable = {
  name: '表格',
  templateType: 'DynamicTable',
  className: 'tableClassName',
  config: [
    {
      [attributeTypeProp]: DataType.Boolean,
      [attributeNameProp]: 'pagination',
      [attributeValueProp]: true,
    },
    {
      [attributeTypeProp]: DataType.Number,
      [attributeNameProp]: 'defaultPageSize',
      [attributeValueProp]: 10,
    },
  ],
  accepts: [LinkButton, ColumnField],
  enabledFlag: 1,
  description: 'hpfm.ui.component.dynamicTable',
  defaultIntlDescription: '表格',
};
// 表单
const DynamicForm = {
  templateType: 'DynamicForm',
  className: 'formClassName',
  config: [],
  accepts: {
    Input,
    TextArea,
    InputNumber,
    DatePicker,
    ValueList,
    Lov,
    Switch,
    Checkbox,
  },
  enabledFlag: 1,
  description: 'hpfm.ui.component.dynamicForm',
  defaultIntlDescription: '表单',
};
// 按钮组
const DynamicToolbar = {
  name: '按钮组',
  templateType: 'DynamicToolbar',
  className: 'toolbarClassName',
  config: [],
  accepts: {
    Button,
  },
  enabledFlag: 1,
  description: 'hpfm.ui.component.dynamicToolbar',
  defaultIntlDescription: '按钮组',
};
// 模态框
const DynamicModal = {
  name: '弹出框',
  templateType: 'DynamicModal',
  className: 'modalClassName',
  config: [],
  accepts: {
    // DynamicForm,
    // DynamicToolbar,
    // DynamicTable,
    // // eslint-disable-next-line
    // DynamicModal,
  },
  enabledFlag: 1,
  description: 'hpfm.ui.component.dynamicModal',
  defaultIntlDescription: '弹出框',
};
// Tabs
const DynamicTabs = {
  name: '标签页',
  templateType: 'DynamicTabs',
  className: 'modalClassName',
  config: [],
  fields: [], // 存储 tab 的信息
  children: [], // 储存 子组件的信息
  accepts: templates,
  enabledFlag: 1,
  description: 'hpfm.ui.component.dynamicTabs',
  defaultIntlDescription: '标签页',
};

// 所有的容器（component）, 左侧组件区域用到
templates.DynamicForm = DynamicForm;
templates.DynamicToolbar = DynamicToolbar;
templates.DynamicTable = DynamicTable;
templates.DynamicModal = DynamicModal;
templates.DynamicTabs = DynamicTabs;

// 为 toolbar 的 按钮可以触发的事件
export function getTemplateTriggerOptions(templatesConfig) {
  const optionGroups = [];
  forEach(templatesConfig, template => {
    let optGroupOptions = null;
    switch (template.templateType) {
      case 'DynamicForm':
        optGroupOptions = [
          <Select.Option key="reset" value={`this.ref['${template.templateCode}'].reset`}>
            reset
          </Select.Option>,
        ];
        break;
      case 'DynamicTable':
        optGroupOptions = [
          <Select.Option key="query" value={`this.ref['${template.templateCode}'].query`}>
            query
          </Select.Option>,
          <Select.Option key="reload" value={`this.ref['${template.templateCode}'].reload`}>
            reLoad
          </Select.Option>,
        ];
        break;
      default:
        break;
    }
    if (optGroupOptions !== null) {
      optionGroups.push(
        <Select.OptGroup label={template.description} key={template.templateCode}>
          {optGroupOptions}
        </Select.OptGroup>
      );
    }
  });
  return optionGroups;
}

export function getQueryFormOptions(templatesConfig) {
  const formOptions = [];
  forEach(templatesConfig, template => {
    switch (template.templateType) {
      case 'DynamicForm':
        // todo
        formOptions.push(
          <Select.Option
            key={template.templateCode}
            value={`this.ref['${template.templateCode}'].props.form`}
          >
            {template.description}
          </Select.Option>
        );
        break;
      default:
        break;
    }
  });
  return formOptions;
}

/**
 * todo 每次新增组件都需要修改
 * 获取 tpl 对应的 export 的方法
 */
export function getTemplateFuncs(templatesConfig) {
  const templateFuncOptions = [];
  forEach(templatesConfig, template => {
    let templateComponent;
    switch (template.templateType) {
      case 'DynamicForm':
        templateComponent = DynamicFormComponent;
        break;
      case 'DynamicTable':
        templateComponent = DynamicTableComponent;
        break;
      default:
        break;
    }
    // todo
    if (templateComponent) {
      templateFuncOptions.push(
        <Select.OptGroup key={template.templateCode} label={template.description}>
          {map(templateComponent.triggers, func => {
            return (
              <Select.Option key={func} value={`this.ref['${template.templateCode}'].${func}`}>
                {templateComponent.triggerMeaning[func]}
              </Select.Option>
            );
          })}
        </Select.OptGroup>
      );
    }
  });
  return templateFuncOptions;
}

/**
 * todo 每次新增tpl可能都需要更改
 * 用于比较之后更新和template相关的事情
 * 顺序 和 templateCode相等才认为是相等的
 * @param {Object[]} prevTemplates
 * @param {Object[]} nextTemplates
 * @returns {boolean}
 */
export function templatesIsNoEqual(prevTemplates, nextTemplates) {
  if (isArray(prevTemplates) && isArray(nextTemplates)) {
    if (!(prevTemplates.length === 0 && nextTemplates.length === 0)) {
      if (prevTemplates.length !== nextTemplates.length) {
        return true;
      } else {
        let isEq = true;
        for (let i = 0; i < prevTemplates.length; i++) {
          isEq = prevTemplates[i].templateCode === nextTemplates[i].templateCode;
          if (!isEq) {
            break;
          }
        }
        return !isEq;
      }
    }
  }
  return true;
}

/**
 * todo 每次新增 tpl 都需要判断这里需不需要更改
 * 能接收 tpl 作为 field 的组件 是否包含 child tpl
 * @param {object} parent - 能接收 tpl 的 tpl
 * @param {object} child - tpl
 * @returns {boolean} parent 包含 child
 */
export function hasTemplate(parent, child) {
  switch (parent.templateType) {
    case 'DynamicTabs':
      return some(parent.fields, field => {
        return some(field.children, tpl => hasTemplate(tpl, child));
      });
    default:
      return parent === child;
  }
}
