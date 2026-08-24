/**
 * index.d
 * @date 2018/9/28
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Field } from '../ComposeForm';

export interface ComposeTableProps {
  // ref 编辑ComposeForm
  refEditComposeForm: Function;
  // 租户id
  organizationId: number;
  // 是否可编辑
  editable: boolean; // = false;
  // 编辑模式, 行内编辑 或 侧边栏编辑
  editMode: 'inline' | 'modal'; // = 'modal';
  // 是否可新增
  addable: boolean; // = false;
  // 是否可删除
  removable: boolean; // = false;
  // 删除方法, removeRecords 的 记录中 可能会参杂 isUpdate 属性
  // 返回 Promise, success: 是否删除成功, autoUpdate: 是否自动更新 dataSource。（如果为false,则需改变父组件的dataSource）
  onRemove: (removeIds: any[], removeRecords: object[], onOk: Function) => void;
  // 数据源, 半受控,当 属性的 dataSource 改变时,组件使用props, 之后使用 state
  dataSource: object[]; // = [];
  // 获得 获取 dataSource 的方法,
  onGetDataSourceHook: (batchUpdateDataSource: object[]) => void;
  // 获得 获取 dataSource 的方法
  onGetValidateDataSourceHook: Function;
  // loading, 在触发 onRemove 和 保存数据时 应该变为true
  loading: boolean; // = false;
  // border
  border: boolean; // = true;
  // rowKey
  rowKey: string; // = 'id';
  // 字段
  fields: Field[];
  // scroll rowSelection columns 不会受传入的同名属性影响,
  // scroll,columns 受 fields 影响
  // rowSelection 受 removable 影响
  // 其他table属性 会直接传给 Table
}

export default class ComposeTable extends React.PureComponent<ComposeTableProps> {}
