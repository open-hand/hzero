/**
 * utils.js
 * @date 2018/10/24
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

export const actionCode = {
  page: 'page',
  action: 'action',
};

export const paramSep = ',';

export const paramTypeCode = {
  fixParam: 'a',
  urlParam: 'b',
  columnParam: 'c',
};

export const paramTypeOptions = [
  { value: 'a', meaning: '固定值' },
  { value: 'b', meaning: '页面参数' },
  { value: 'c', meaning: '列参数' },
];

export const openPageModalMap = {
  w1: '720*360',
  w2: '760*600',
  w3: '860*600',
  w4: '960*600',
};

export const openPageModalBodyStyle = {
  w1: {
    width: 720,
    bodyStyle: {
      overflow: 'auto',
      height: 360,
    },
  },
  w2: {
    width: 760,
    bodyStyle: {
      overflow: 'auto',
      height: 600,
    },
  },
  w3: {
    width: 860,
    bodyStyle: {
      overflow: 'auto',
      height: 600,
    },
  },
  w4: {
    width: 960,
    bodyStyle: {
      overflow: 'auto',
      height: 600,
    },
  },
};

export const openTypeCode = {
  inner: 'inner', // 页面跳转
  modal: 'modal', // 模态框(弹出)
  drawer: 'drawer', // 模态框(滑出)
};

export const hiddenColumnKey = 'hiddenColumn';
export const hiddenColumnPrefix = `[${hiddenColumnKey}]`;
export const hiddenColumnSep = ',';
