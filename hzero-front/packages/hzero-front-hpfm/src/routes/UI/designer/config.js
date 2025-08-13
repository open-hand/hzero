import React from 'react';

// react-dnd 用到的属性
export const DragType = {
  dragComponent: 'dragComponent', // 新增的容器
  dragField: 'dragField', // 新增的字段
  drawDragComponent: 'drawDragComponent', // 换位的容器
  drawDragField: 'drawDragField', // 换位的字段
};

export const DropType = {
  draw: [DragType.dragComponent], // 接收新增的容器
  drawComponent: [DragType.dragComponent, DragType.drawDragComponent], // 接收 新增的容器 换位的容器
  drawField: [DragType.dragField, DragType.drawDragField], // 接收 新增的字段 换位的字段
};

// react-dnd 用到的属性
export const emptyFieldType = 'empty';

export const emptyField = {
  componentType: emptyFieldType,
};

// 属性

export const attributeNameProp = 'attributeName';
export const attributeValueProp = 'value';
export const attributeTypeProp = 'attributeType';

// 属性

// Form 相关
export const fieldNameProp = 'fieldName';
export const fieldLabelProp = 'fieldLabel';
export const maxFormCol = 4;
export const defaultFormCol = 2;
// Form 相关

// Table 相关
export const autoSizeWidth = 120; // 字段自适应宽度的显示宽度
// Table 相关

// Context
export const DynamicConfigContext = React.createContext({});
//
