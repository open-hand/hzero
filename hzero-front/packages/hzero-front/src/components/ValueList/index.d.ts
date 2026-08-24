import * as React from 'react';

/**
 * 必须是受控的
 */
export interface ValueListProps {
  // 值集的code
  lovCode?: string;
  // 下拉框配置
  options?: Array<{ value: any; meaning: string }>;
  // 值字段
  valueField?: string; // value
  // 显示字段
  displayField?: string; // meaning
  // 值集没有加载时显示的值
  // @deprecated
  textValue?: string;
  // 显示值 回写进form的field
  textField?: string;
  // 组件值,该组件必须是受控组件
  value?: string;
  // // 是否懒加载
  lazyLoad?: boolean; // true
  // // 是否是独立值集
  // idp: boolean = false;
  // 组件值变化回调
  onChange?: (value: string, option: any) => void;
  queryUrl?: string;
}

export default class ValueList extends React.PureComponent<ValueListProps> {}
