import { css } from 'styled-components';

let lessVars = {};

try {
  // eslint-disable-next-line global-require
  lessVars = require('@config/theme.js');
} catch (error) {
  // nothing
}

// form layout gutter
const vars = {
  '@screen-lg': '992',
  '@screen-md': '768',
  '@hzero-gutter-grow': 4,
  '@hzero-gutter': 16,
  /**
   * 字体
   */

  '@hzero-font-size-title': 14,
  '@hzero-font-size-normal': 12,

  // 颜色
  '@hzero-tabs-tab-color': '#4c4c4c',
  /**
   * 色彩
   */

  // 主要颜色 用于导航栏按钮 或者 其他需要强调的 文字 按钮 操作等
  '@hzero-primary-color': '#1e3255',

  // 次级主要颜色 用于页面内部的主要按钮等
  '@hzero-primary-color-2': '#29bece',

  // 次级主要颜色 用于页面内部的主要按钮hover等
  '@hzero-primary-color-2-hover': '#4fd2db',

  // 次要颜色 用于 icon 图表等
  '@hzero-primary-color-3-blue': '#0687ff',
  '@hzero-primary-color-3-purple': '#cb38ad',
  '@hzero-primary-color-3-yellow': '#ffbc00',
  '@hzero-primary-color-3-red': '#f02b2b',

  // 低频颜色
  '@hzero-minor-color-blue': '#daedfe',
  '@hzero-minor-color-purple': '#f8e1f3',
  '@hzero-minor-color-yellow': '#fff6d9',
  '@hzero-minor-color-red': '#fddfdf',

  // 背景色
  '@hzero-bgc-color': '#b9d3ff',
  '@hzero-bgc-not-color': '#fff',
  '@hzero-bgc-border-color': '#d9d9d9',
  '@hzero-bgc-color-light': '#eee',
  '@hzero-bgc-color-lightest': '#f0f0f0',
  '@hzero-bgc-color-dark': '#d5dae0',
  '@hzero-simple-bgc-color': '#f4f6f8',
  // 标题颜色
  '@hzero-title-second-color': '#6d7a80',

  // 主按钮颜色
  '@hzero-primary-btn-hover-color': '#405477',

  // disabled color

  '@hzero-color-disabled': '#ccc',
  '@hzero-bgc-color-disabled': '#f5f5f5',

  // 表单相关
  '@form-item-has-error-color': '#f13131',
  // 表单相关
  '@hzero-form-required-color': '#fffbdf',

  // 表格相关
  '@hzero-form-disabled-label-color': '#666',
  '@hzero-form-disabled-wrapper-color': '#333',

  // notification
  '@hzero-notification-success-color': '#4aa44e',
  '@hzero-notification-info-color': '#3689f7',
  '@hzero-notification-warn-color': '#f6bd41',
  '@hzero-notification-error-color': '#dd4037',
  // Tabs 相关
  // FIXME: 暂时不能覆盖父级元素的 border 因为 overflow: hidden 了
  '@hzero-tabs-card-tab-padding': '9px 14px',
  '@hzero-tabs-card-tab-active-padding': '9px 14px',
  // 所有的 z-index 必须在这里声明

  // 菜单的 z-index
  '@hzero-layout-menu-z-index': 1100,
};

vars['@hzero-gutter-md'] = vars['@hzero-gutter'] - vars['@hzero-gutter-grow'];
vars['@hzero-gutter-sm'] = vars['@hzero-gutter-md'] - vars['@hzero-gutter-grow'];
vars['@hzero-gutter-xs'] = vars['@hzero-gutter-sm'] - vars['@hzero-gutter-grow'];
vars['@hzero-gutter-lg'] = vars['@hzero-gutter'] + vars['@hzero-gutter-grow']; // 20px
vars['@hzero-gutter-xl'] = vars['@hzero-gutter-lg'] + vars['@hzero-gutter-grow']; // 24px
vars['@hzero-gutter-xxl'] = vars['@hzero-gutter-xl'] + vars['@hzero-gutter-grow']; // 28px
vars['@hzero-gutter-form-search'] = vars['@hzero-gutter-md']; // 12px
vars['@hzero-gutter-form-edit'] = vars['@hzero-gutter-xl']; // 24px

// block gutter
vars['@hzero-gutter-block'] = vars['@hzero-gutter-xl']; // 24px connect is small
vars['@hzero-gutter-inline'] = vars['@hzero-gutter']; // 16px connect is big

vars['@hzero-gutter-bgc'] = '#b9d3ff';
vars['@hzero-gutter-bgc-light-1'] = '#eee';
vars['@hzero-gutter-bgc-light-2'] = '#f0f0f0';

// Input 颜色

// 水平
vars['@hzero-tabs-line-vertical-tab-padding'] = `13px ${vars['@hzero-gutter']}px`;
vars['@hzero-tabs-line-vertical-tab-margin'] = `0 ${vars['@hzero-gutter']}px 0 0`;

// 垂直
vars['@hzero-tabs-line-horizontal-tab-padding'] = `10px ${vars['@hzero-gutter']}px`;

Object.keys(vars).forEach((key) => {
  const realKey = key.slice(1);
  const realLessValue = lessVars[realKey];
  if (realLessValue) {
    vars[key] = realLessValue;
  }
});

export { vars };

export function hzeroFontSize(size = 12) {
  return css`
    font-size: ${size}px;
    line-height: ${size + 8}px;
  `;
}

export function textOverflow() {
  return css`
    overflow: hidden;
    text-overflow: ellipsis;
    word-break: break-all;
    white-space: nowrap;
  `;
}

export function hexToRgbaColor(color = '#000000', opacity = 1) {
  if (color.indexOf('rgba') > -1) {
    return color;
  } else if (color.indexOf('rgb') > -1) {
    /rgb\((.+)\)/.exec(color);
    return `rgba(${RegExp.$1}, ${opacity})`;
  }
  const hex = color.slice(1);
  if (hex.length !== 3 && hex.length !== 6) {
    return 'rgba(0, 0, 0, 0)';
  }
  if (hex.length === 3) {
    return `rgba(${parseInt(hex.slice(0, 1), 16)}, ${parseInt(hex.slice(1, 2), 16)}, ${parseInt(
      hex.slice(2, 3),
      16
    )}, ${opacity})`;
  }
  const r = parseInt(`0x${hex.slice(0, 2)}`, 16);
  const g = parseInt(`0x${hex.slice(2, 4)}`, 16);
  const b = parseInt(`0x${hex.slice(4)}`, 16);
  return `rgba(${r}, ${g}, ${b}, ${opacity})`;
}
