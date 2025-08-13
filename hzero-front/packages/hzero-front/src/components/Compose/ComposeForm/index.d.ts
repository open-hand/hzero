import React from 'react';

type Flag = 1 | 0;

export interface Prop {
  // 属性名
  attributeName: string;
  // 属性值
  attributeValue: string;
}

export interface Field {
  fieldCode: string;
  fieldDescription: string;
  componentType: string;
  // 是否启用
  visualFlag: Flag;
  // 是否必输
  requiredFlag: Flag;
  // 跨列宽度
  colspan?: number;
  // 左偏移宽度
  leftOffset?: number;
  // 右偏移宽度
  rightOffset?: number;
  // lov 或 值集的 编码
  lovCode?: string;
  // 组件的属性
  props: Prop[];
}

export interface ComposeFormProps {
  // label 的宽度
  fieldLabelWidth: number | string; // = 150;
  // 是否可编辑
  editable: boolean; // = true;
  // 非编辑模式下的样式
  disableStyle: 'value' | 'disabled'; // = 'value';
  // 将 ComposeForm 的 this 传递出去.
  onRef: Function;
  // 表单 每行的列数
  col: number; // = 3;
  // 表单字段值
  fields: Field[];
  // 租户id
  organizationId: number;
  // 表单数据
  dataSource: object; // = {};
  // 获取 获取经过校验的数据 的方法
  onGetValidateDataSourceHook: Function<Promise<object>>;
}

export default class ComposeForm extends React.PureComponent<ComposeFormProps> {}
