import { css, createGlobalStyle } from 'styled-components';
import { getRequiredData } from '@hzero-front-ui/cfg/lib/utils/utils';
import { hzeroFontSize, vars, hexToRgbaColor } from './utils';

const fitUed = () => {
  return css`
    .c7n-pro-table-cell.c7n-pro-table-cell-multiLine > .c7n-pro-output:before {
      display: none;
    }
  `;
};

const sectionOne = () => {
  return css`
    /* // 层级比正常的 antd 样式 高一层 */
    .ant-input {
      padding: 4px 8px;
      border-radius: 4px !important;
      font-size: 12px;
      line-height: 20px;
    }
    .ant-select-selection {
      border-radius: 4px !important;
    }
    /*  联合覆盖 Form.Item > Input */
    .c7n-pro-form-item-required
      .c7n-pro-form-item-children
      .c7n-pro-input:not(.c7n-pro-input-disabled):not(:disabled),
    .c7n-pro-form-item-required
      .c7n-pro-form-item-children
      .c7n-pro-input-number:not(.c7n-pro-input-number-disabled),
    .c7n-pro-form-item-required
      .c7n-pro-form-item-children
      .c7n-pro-cascader-picker:not(.c7n-pro-cascader-picker-disabled):not(:disabled),
    .c7n-pro-form-item-required
      .c7n-pro-form-item-children
      .c7n-pro-time-picker-input:not(:disabled),
    .c7n-pro-form-item-required
      .c7n-pro-form-item-children
      :not(.c7n-pro-select-disabled)
      > .c7n-pro-select-selection:not(:disabled),
    .c7n-pro-input-required:not(.c7n-pro-input-invalid):not(.c7n-pro-input-disabled) .c7n-pro-input,
    .c7n-pro-select-required:not(.c7n-pro-select-invalid):not(.c7n-pro-select-disabled)
      .c7n-pro-select,
    .c7n-pro-auto-complete-required:not(.c7n-pro-auto-complete-invalid):not(.c7n-pro-auto-complete-disabled)
      .c7n-pro-auto-complete,
    .c7n-pro-icon-picker-required:not(.c7n-pro-icon-picker-invalid):not(.c7n-pro-icon-pickere-disabled)
      .c7n-pro-icon-picker,
    .c7n-pro-calendar-picker-required:not(.c7n-pro-calendar-picker-invalid):not(.c7n-pro-calendar-picker-disabled)
      .c7n-pro-calendar-picker,
    .c7n-pro-password-required:not(.c7n-pro-password-invalid):not(.c7n-pro-password-disabled)
      .c7n-pro-password,
    .c7n-pro-textarea-required:not(.c7n-pro-textarea-invalid):not(.c7n-pro-textarea-disabled)
      .c7n-pro-textarea,
    .c7n-pro-input-number-required:not(.c7n-pro-input-number-invalid):not(.c7n-pro-input-number-disabled)
      .c7n-pro-input-number {
      border-color: ${vars['@hzero-primary-color-3-yellow']};
      box-shadow: none;
    }

    .c7n-pro-form-item-required.has-error
      .c7n-pro-form-item-children
      .c7n-pro-input:not(.c7n-pro-input-disabled):not(:disabled),
    .c7n-pro-form-item-required.has-error
      .c7n-pro-form-item-children
      .c7n-pro-input-number-input:not(:disabled),
    .c7n-pro-form-item-required.has-error
      .c7n-pro-form-item-children
      .c7n-pro-input-number:not(:disabled),
    .c7n-pro-form-item-required.has-error
      .c7n-pro-form-item-children
      .c7n-pro-cascader-picker:not(.c7n-pro-cascader-picker-disabled):not(:disabled),
    .c7n-pro-form-item-required.has-error
      .c7n-pro-form-item-children
      .c7n-pro-time-picker-input:not(:disabled),
    .c7n-pro-form-item-required.has-error
      .c7n-pro-form-item-children
      :not(.c7n-pro-select-disabled)
      > .c7n-pro-select-selection:not(:disabled) {
      border-color: ${vars['@form-item-has-error-color']};
      background-color: #fff;
    }

    .c7n-pro-form-item-required
      .c7n-pro-form-item-children
      .c7n-pro-input-number:not(.c7n-pro-input-disabled):not(:disabled):focus,
    .c7n-pro-form-item-required
      .c7n-pro-form-item-children
      .c7n-pro-input:not(.c7n-pro-input-disabled):not(:disabled):focus {
      /* 去掉 box-shadow */
      box-shadow: none;
    }
    .c7n-pro-field-label-vertical label,
    .c7n-pro-field-label label {
      line-height: 0.28rem;
    }

    .c7n-pro-auto-complete-multiple-float-label,
    .c7n-pro-auto-complete-float-label,
    .c7n-pro-input-multiple-float-label,
    .c7n-pro-icon-picker-float-label,
    .c7n-pro-calendar-picker-float-label,
    .c7n-pro-textarea-float-label,
    .c7n-pro-password-float-label,
    .c7n-pro-input-number-float-label,
    .c7n-pro-select-float-label,
    .c7n-pro-input-float-label {
      .c7n-pro-field-label-wrapper {
        // border-top-color: transparent !important;
        .c7n-pro-field-label {
          // background: #fff;
          padding-left: 8px;
        }
      }
    }

    .c7n-pro-input-focused .c7n-pro-input:focus,
    .c7n-pro-select-focused .c7n-pro-select:focus {
      box-shadow: none;
    }

    /* 单独的组件 */

    .c7n-pro-input {
      padding: 4px 8px;
      border-radius: 4px;
      ${hzeroFontSize(12)};

      &:focus {
        box-shadow: 0 0 2px 1px ${vars['@hzero-primary-color-2']};
      }
    }

    /* fix 输入框缩进去的问题 */
    .c7n-pro-input-multiple-input {
      box-sizing: initial;
    }

    /* FIXME: 待确定是否按照 UI 给的 设计 */
    .c7n-pro-input-spec {
      width: 238px;
      min-width: 138px;
      max-width: 438px;

      .c7n-pro-input-special {
        width: 180px;
      }
    }
    .c7n-pro-select-wrapper .c7n-pro-btn:not(.c7n-pro-btn-flat) {
      cursor: pointer;
    }
    .c7n-pro-btn:not(.c7n-pro-btn-flat) {
      line-height: 1.5;
      display: inline-block;
      font-weight: 400;
      text-align: center;
      touch-action: manipulation;
      background-image: none;
      white-space: nowrap;
      padding: 0 15px;
      font-size: 12px;
      border-radius: 2px;
      height: 28px;
      user-select: none;
      transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
      position: relative;
      border: 1px solid #d9d9d9;

      &.c7n-pro-btn-primary {
        // background-color: ${vars['@hzero-primary-color']};
        // border-color: ${vars['@hzero-primary-color']};
        color: #fff;

        &:hover {
          color: #fff;
          background-color: ${vars['@hzero-primary-btn-hover-color']};
          border-color: ${vars['@hzero-primary-btn-hover-color']};
        }
      }
    }

    .c7n-pro-btn-main {
      border-color: #666;
      color: #666;

      &:active,
      &:focus,
      &:hover {
        border-color: ${vars['@hzero-primary-color']};
        color: ${vars['@hzero-primary-color']};
      }
    }

    .c7n-pro-field-label,
    .c7n-pro-field-wrapper {
      padding: 0.1rem 0;
    }

    .c7n-pro-field-label:after {
      width: 0;
      margin: 0 10px 0 2px;
      color: #333;
      position: relative;
      top: -0.5px;
    }

    .c7n-popover-message {
      font-size: 13px;
    }

    .c7n-popover-message-title {
      padding-left: 21px;
    }

    .c7n-pro-field-required.c7n-pro-field-label:after {
      content: '';
    }
    .c7n-pro-field-required.c7n-pro-field-label-useColon.c7n-pro-field-label:after {
      content: ':';
    }

    .c7n-pro-field-required.c7n-pro-field-label:before {
      display: inline-block;
      margin-right: 4px;
      content: '*';
      line-height: 1;
      font-size: 12px;
      color: #f5222d;
    }

    .c7n-pro-select-clear-button,
    .c7n-pro-calendar-picker-clear-button {
      background: transparent;
    }

    .c7n-pro-select-suffix .icon-search:before {
      content: '\\E670';
      display: block;
      font-family: anticon, sans-serif;
      color: rgba(0, 0, 0, 0.25);
      font-size: 12px;
    }

    .c7n-pro-input-suffix .icon-search:before {
      content: '\\E670';
      display: block;
      font-family: anticon, sans-serif;
      color: rgba(0, 0, 0, 0.25);
      font-size: 12px;
    }

    .c7n-pro-select-suffix .icon-baseline-arrow_drop_down:before {
      content: '\\E61D';
      display: block;
      font-family: anticon, sans-serif;
      font-size: 12px;
      color: rgba(0, 0, 0, 0.25);
    }

    .c7n-pro-calendar-picker-suffix .icon-date_range:before {
      content: '\\E6BB';
      font-family: anticon, sans-serif;
      font-size: 12px;
      color: rgba(0, 0, 0, 0.25);
      display: inline-block;
      line-height: 1;
      position: absolute;
      top: 8px;
      right: 13px;
    }

    .c7n-pro-select-trigger {
      position: relative;
      top: 2px;
      right: 7px;
    }

    .c7n-pro-select-suffix > .icon-search {
      position: relative;
      top: 2px;
    }

    .c7n-pro-select-expand .c7n-pro-select-trigger {
      top: 2px;
    }

    .c7n-pro-pagination .c7n-pro-select {
      padding-right: 0.3rem !important;
    }

    .c7n-pro-checkbox {
      &.c7n-pro-checkbox-indeterminate {
        .c7n-pro-checkbox-inner {
          background-color: #fff;

          &:after {
            left: 3px;
            top: 3px;
            width: 8px;
            height: 8px;
            border-width: 8px;
            border-color: ${vars['@hzero-primary-color-2']};
          }
        }
      }
    }

    .c7n-pro-notification-notice {
      &.success {
        border-left: 3px solid ${vars['@hzero-notification-success-color']};
      }

      &.info {
        border-left: 3px solid ${vars['@hzero-notification-info-color']};
      }

      &.warn {
        border-left: 3px solid ${vars['@hzero-notification-warn-color']};
      }

      &.error {
        border-left: 3px solid ${vars['@hzero-notification-error-color']};
      }
    }

    .c7n-pro-table-wrapper {
      .c7n-pro-table-cell {
        padding: 0;
      }
      .c7n-pro-table {
        .c7n-pro-table-scroll > .c7n-pro-table-body {
          border-width: 0 1px;
          border-style: solid;
          border-color: #e8e8e8;
          border-collapse: collapse;

          > table,
          .c7n-pro-table-fixed {
            /* 去除table多出的 left border */
            border-left: 0;
          }
        }

        .c7n-pro-table-thead > tr > th.c7n-pro-table-selection-column,
        .c7n-pro-table-tbody > tr > td.c7n-pro-table-selection-column {
          width: ${62 - vars['@hzero-gutter']}px;
          min-width: ${62 - vars['@hzero-gutter']}px;
          text-align: initial;
        }

        /* table */
        .c7n-pro-table-thead > tr > th {
          ${hzeroFontSize()};
          line-height: 25px;
          min-height: 38px;
          height: 39px;

          &.c7n-pro-table-column-has-filters {
            position: relative;
          }
        }
      }
      .c7n-pro-btn:not(.c7n-pro-btn-flat).c7n-pro-btn-primary {
        background-color: ${vars['@hzero-primary-color-2']};
        border-color: ${vars['@hzero-primary-color-2']};
        color: #fff;

        &:hover {
          background-color: ${vars['@hzero-primary-color-2-hover']};
          border-color: ${vars['@hzero-primary-color-2-hover']};
          color: #fff;
        }
      }
      .c7n-pro-btn:not(.c7n-pro-btn-flat).c7n-pro-btn-primary.hzero-permission-btn-disabled {
        background-color: ${vars['@hzero-bgc-not-color']};
        border-color: ${vars['@hzero-bgc-border-color']};

        &:hover {
          background-color: ${vars['@hzero-bgc-not-color']};
          border-color: ${vars['@hzero-bgc-border-color']};
          color: #fff;
        }
      }
    }

    .c7n-pro-tabs {
      /* tabs
    editable-card */
      &.c7n-pro-tabs-card.c7n-pro-tabs-editable-card > .c7n-pro-tabs-bar .c7n-pro-tabs-tab {
        padding: ${vars['@hzero-tabs-card-tab-padding']};
        color: ${vars['@hzero-tabs-tab-color']};
        ${hzeroFontSize(14)}

        &.c7n-pro-tabs-tab-active {
          color: ${vars['@hzero-primary-color-2']};
        }

        &.c7n-pro-tabs-tab-disabled {
          color: ${vars['@hzero-color-disabled']};
        }
      }

      &.c7n-pro-tabs-line > .c7n-pro-tabs-bar .c7n-pro-tabs-tab {
        padding: ${vars['@hzero-tabs-line-vertical-tab-padding']};
        margin: ${vars['@hzero-tabs-line-vertical-tab-margin']};
        color: ${vars['@hzero-tabs-tab-color']};
        ${hzeroFontSize(14)};

        &.c7n-pro-tabs-tab-active {
          color: ${vars['@hzero-primary-color-2']};
        }

        &.c7n-pro-tabs-tab-disabled {
          color: ${vars['@hzero-color-disabled']};
        }
      }

      &.c7n-pro-tabs-vertical.c7n-pro-tabs.c7n-pro-tabs-line > .c7n-pro-tabs-bar .c7n-pro-tabs-tab {
        /* // tabs的tab在水平排列不需要margin */
        margin-right: 0;
      }

      &.c7n-pro-tabs-line.c7n-pro-tabs-minor > .c7n-pro-tabs-bar {
        /* // TODO: 等确定再做
      border: 0 none;
      background-color: #f4f6f8;
      box-shadow: #f4f6f8 inset 1px 0;
      min-width: 128px;

      .c7n-pro-tabs-tab {
        padding: @hzero-tabs-line-horizontal-tab-padding;
        color: @hzero-tabs-tab-color;
        margin: 0;

        &.c7n-pro-tabs-tab-active {
          font-weight: bold;
          color: @hzero-primary-color-2;
        }

        &.c7n-pro-tabs-tab-disabled {
          color: @hzero-color-disabled;
        }
      }

      .c7n-pro-tabs-ink-bar {
        // FIXME: 由于 是 style 的样式 所以这里使用 !important
        display: none !important;
      } */
      }
    }

    .c7n-pro-collapse {
      .c7n-pro-collapse-content-box .c7n-pro-form-item > .c7n-pro-form-item-label {
        text-align: left;
      }
    }
  `;
};

const sectionTwo = () => {
  return css`
    .c7n-pro-calendar {
      /* // header */
      .c7n-pro-calendar-header {
        .c7n-pro-calendar-prev-month-btn,
        .c7n-pro-calendar-next-month-btn,
        .c7n-pro-calendar-prev-year-btn,
        .c7n-pro-calendar-next-year-btn {
          padding: 0;
        }
      }
      .c7n-pro-calendar-header-prev-month-btn {
        left: ${vars['@hzero-gutter'] + vars['@hzero-gutter-md'] + 8}px;
      }

      .c7n-pro-calendar-header-next-month-btn {
        right: ${vars['@hzero-gutter'] + vars['@hzero-gutter-md'] + 8}px;
      }

      .c7n-pro-calendar-header-prev-year-btn {
        left: ${vars['@hzero-gutter']}px;
      }

      .c7n-pro-calendar-header-next-year-btn {
        right: ${vars['@hzero-gutter']}px;
      }
      .c7n-pro-calendar-year-panel-header {
        .c7n-pro-calendar-year-panel-prev-decade-btn,
        .c7n-pro-calendar-year-panel-next-decade-btn {
          padding: 0;
        }
      }
      .c7n-pro-calendar-year-panel-header-prev-decade-btn {
        left: ${vars['@hzero-gutter']}px;
      }

      .c7n-pro-calendar-year-panel-header-next-decade-btn {
        right: ${vars['@hzero-gutter']}px;
      }
      .c7n-pro-calendar-decade-panel-header {
        .c7n-pro-calendar-decade-panel-prev-century-btn,
        .c7n-pro-calendar-decade-panel-next-century-btn {
          padding: 0;
        }

        .c7n-pro-calendar-decade-panel-prev-century-btn {
          left: ${vars['@hzero-gutter']}px;
        }

        .c7n-pro-calendar-decade-panel-next-century-btn {
          right: ${vars['@hzero-gutter']}px;
        }
      }
    }
  `;
};

const sectionThree = () => {
  return css`
    .c7n-pro-tabs {
      &.c7n-pro-tabs-vertical {
        > .c7n-pro-tabs-bar {
          background-color: ${vars['@hzero-simple-bgc-color']};

          > .c7n-pro-tabs-nav-container {
            > .c7n-pro-tabs-nav-wrap {
              > .c7n-pro-tabs-nav-scroll {
                > .c7n-pro-tabs-nav {
                  .c7n-pro-tabs-tab {
                    ${hzeroFontSize(14)};
                    color: ${vars['@hzero-tabs-tab-color']};
                    text-align: left;
                    padding: 9px 9px 9px ${vars['@hzero-gutter']}px;
                  }
                  .c7n-pro-tabs-tab-active {
                    background-color: #fff;
                    color: ${vars['@hzero-primary-color-2']};
                  }

                  .c7n-pro-tabs-tab-disabled {
                    color: ${vars['@hzero-color-disabled']};
                  }
                  > .c7n-pro-tabs-ink-bar {
                    /* 要操作 */
                    display: none !important;
                  }
                }
              }
            }
          }
        }
      }
    }
  `;
};

const sectionFour = (props) => {
  const { primary } = getRequiredData(props, 'colors');
  return css`
    /* c7n-pro-table */
    .c7n-pro-checkbox-inner {
      border: 1px solid #d9d9d9;
    }

    .c7n-pro-checkbox-inner:after {
      top: 0;
      left: 0.04rem;
    }

    .c7n-pro-checkbox-indeterminate .c7n-pro-checkbox-inner::after {
      top: 0.06rem;
      left: 0.02rem;
    }

    .c7n-pro-table-row-height-fixed td {
      /* padding-top: 4px;
      padding-bottom: 4px; */
    }

    .c7n-pro-table-wrapper .c7n-pro-table .c7n-pro-table-thead > tr > th:first-child {
      padding: 0;
    }

    .c7n-pro-table-wrapper .c7n-pro-table .c7n-pro-table-thead > tr > th:not(:first-child) {
      padding: 0;
    }

    .c7n-pro-table-wrapper
      .c7n-pro-table
      table
      .c7n-pro-table-tbody
      > tr
      > td.c7n-pro-table-selection-column {
      padding-left: 0.01rem;
      padding-right: 0.01rem;
    }

    .c7n-pro-table-row > td {
      background-color: #fff;
    }
    .c7n-pro-table-row.c7n-pro-table-row-selected td {
      background-color: rgb(241,245,253);
    }
    .c7n-pro-table-row-highlight.c7n-pro-table-row:hover > td,
    .c7n-pro-table-row-hover > td,
    .c7n-pro-table-row-highlight.c7n-pro-table-row:hover + .c7n-pro-table-expanded-row > td,
    .c7n-pro-table-row-hover + .c7n-pro-table-expanded-row > td {
      background: rgb(241,245,253) !important;
    }

    .c7n-pro-table-cell-editable .c7n-pro-table-cell-inner {
      &:not([class$='-required']):not([disabled]):not([class$='-invalid']) {
        border: 1px solid #d9d9d9;
      }
    }

    .c7n-pro-table-cell-inner {
      /* border: none; */
      border-radius: 4px;

      .c7n-pro-input-wrapper {
        background-color: inherit;
      }
    }

    .c7n-pro-table-cell-editable.c7n-pro-table-cell-required .c7n-pro-table-cell-inner {
      border-radius: 4px;
    }

    .c7n-pro-table-parity-row .c7n-pro-table-row:nth-child(even) > td {
      background-color: rgb(250, 250, 250);
    }

    .c7n-pro-table-row-current.c7n-pro-table-row-highlight > td,
    .c7n-pro-table-row-current:hover.c7n-pro-table-row-highlight > td,
    .c7n-pro-table-row-current.c7n-pro-table-row-highlight + .c7n-pro-table-expanded-row > td,
    .c7n-pro-table-row-current:hover.c7n-pro-table-row-highlight
      + .c7n-pro-table-expanded-row
      > td {
      background: rgb(241,245,253) !important;
    }

    /* c7n-pro-table end */

    /* c7n-pro-btn */

    .page-head-operator .c7n-pro-btn-primary,
    .page-head-operator .c7n-pro-btn-primary:active,
    .page-head-operator .c7n-pro-btn-primary:focus {
      background-color: ${vars['@hzero-primary-color']};
      border-color: ${vars['@hzero-primary-color']};
    }

    .page-head-operator > .c7n-pro-btn:not(:last-child),
    .page-head-operator > .page-head-operation:not(:last-child) {
      margin-left: 8px;
    }

    .page-head .c7n-pro-btn:not(.c7n-pro-btn-flat).c7n-pro-btn-primary {
      background-color: ${vars['@hzero-primary-color']};
      border-color: ${vars['@hzero-primary-color']};
    }
    .page-head .c7n-pro-btn:not(.c7n-pro-btn-flat).c7n-pro-btn-primary.hzero-permission-btn-disabled {
      background-color: ${vars['@hzero-bgc-not-color']};
      border-color: ${vars['@hzero-bgc-border-color']};
    }

    .c7n-pro-btn.c7n-pro-btn-icon-only,
    .c7n-pro-btn.c7n-pro-btn-icon-only:not(.c7n-pro-btn-flat) {
      border-radius: 50%;
      padding: 0;
    }

    .c7n-pro-btn:not(.c7n-pro-btn-flat) {
      box-shadow: none;
      box-sizing: border-box;
      border-radius: 2px;
    }

    .c7n-pro-btn-default:focus {
      border-color: ${vars['@hzero-primary-color-2']};
    }

    .c7n-pro-btn-primary {
      color: #fff;
      background-color: ${vars['@hzero-primary-color-2']};
      border-color: ${vars['@hzero-primary-color-2']};
    }

    .c7n-pro-btn-primary.c7n-pro-btn-flat {
      color: ${vars['@hzero-primary-color-2']};
      background-color: transparent;
      border-color: transparent;
    }

    .c7n-pro-btn-primary.c7n-pro-btn-raised:enabled:hover {
      color: #fff;
      background-color: ${vars['@hzero-primary-btn-hover-color']};
      border-color: ${vars['@hzero-primary-btn-hover-color']};
    }

    .c7n-pro-btn + .c7n-pro-btn {
      margin-left: 0.08rem;
    }

    .c7n-pro-pagination-pager {
      border-color: transparent;
      padding: 0;
      top: -2px;
    }

    /* c7n-pro-btn end */

    /* c7n-pro-select */
    .c7n-pro-select {
      border-radius: 4px;
      &:focus {
        box-shadow: 0 0 2px 1px ${vars['@hzero-primary-color-2']};
      }
    }

    [class$='-required']:not([disabled]):not([class$='-invalid']) {
      .c7n-pro-input-number:not([disabled]) {
        background-color: #fffbdf;
        border-color: #ffbc00;
      }
    }
    .c7n-pro-input-number-float-label.c7n-pro-input-number-required:not(.c7n-pro-input-number-invalid):not(.c7n-pro-input-number-disabled) .c7n-pro-input-number {
      background-color: transparent;
    }
    .c7n-pro-table-toolbar {
      padding-top: 2px;
    }
    .c7n-pro-select-dropdown-menu-item-active,
    .c7n-pro-select-dropdown-menu-item-selected,
    .c7n-pro-select-dropdown-menu-item:hover {
      background: ${hexToRgbaColor(primary, 0.1)};
    }

    // .c7n-pro-input-number,
    // .c7n-pro-calendar-picker {
    //   border-radius: 4px;
    //   &:focus {
    //     box-shadow: 0 0 2px 1px ${vars['@hzero-primary-color-2']};
    //   }
    // }

    .c7n-pro-table {

      .c7n-pro-table-placeholder {
        border-bottom: 1px solid #e8e8e8;
      }

      .c7n-pro-table-row {
        &:last-child {
          td {
            border-bottom: 1px solid #e8e8e8;
          }
        }
      }

      .c7n-pro-table-body {
        & > table {
          padding: 0;
        }

        & > tr {
          & > td {
            border-bottom: 1px solid #e8e8e8;
          }
        }
      }

      .c7n-pro-table-thead {
        > tr {
          > th {
            /* // text-align: left; */
            background-color: rgba(0, 0, 0, 0.04) !important;
            color: #333;
            /* // FIXME: 不知道为什么会有这个加粗 */
            font-weight: 500;
            padding: 5px 8px;
            border-bottom: 1.1px solid rgba(0, 0, 0, 0.16);
          }
        }
      }
    }
    .c7n-pro-table-thead.c7n-pro-table-column-resizable .c7n-pro-table-cell {
      .c7n-pro-table-cell-inner {
        &:before {
          display: inline-block;
          content: '';
          height: 100%;
          vertical-align: middle;
        }
      }
    }
    .c7n-pro-table-bordered .c7n-pro-table-cell,
    .c7n-pro-table-bordered .c7n-pro-table-cell[colspan] {
      border-bottom: 1px solid #e0e0e0;
      /* border-left: none; */
    }

    .c7n-pro-table-cell-editable {
      padding-left: 16px !important;
      padding-right: 16px !important;
    }

    /* c7n-pro-switch */
    .c7n-pro-switch-wrapper {
      padding: 0;
      height: 22px;
      min-width: 44px;
      line-height: 20px;
    }

    .c7n-pro-switch:checked + .c7n-pro-switch-label {
      background-color: ${vars['@hzero-primary-color-2']};

      &:after {
        background-color: #fff;
        transform: translateX(-100%);
        margin-left: -2px;
      }
    }

    .c7n-pro-switch-label {
      min-width: 44px;
      height: 22px;
      background-color: rgba(0, 0, 0, 0.25);
      left: 2px;

      &:after {
        width: 18px;
        height: 18px;
      }
    }

    /* c7n-pro-switch end */
  `;
};

const sectionFive = () => {
  return css`
    /* // Select 不要边框 */
    .select-no-border.c7n-pro-select {
      /* // 没有边框的select */
      > div {
        box-shadow: none;
        background-color: transparent;
        border: 0 none;
      }
    }
    /* // 高度问题 */
    .c7n-select-auto-complete.c7n-select {
      .c7n-select-selection--single {
        line-height: 0;
      }
    }
    /* // treeSelect增加边框 */
    .c7n-select-selection__rendered {
      display: inline-block;
      height: 28px;
      border: 0.01rem solid rgba(0, 0, 0, 0.2);
      border-radius: 4px;
      line-height: 24px;
    }

    /* 详情页的表单和折叠面板样式 */

    .ued-detail-wrapper {
      /* // 折叠面板统一样式 */
      .form-collapse,
      .c7n-pro-collapse {
        border: none;
        background-color: #fff;

        .c7n-pro-collapse-item.c7n-pro-collapse-no-arrow .c7n-pro-collapse-header {
          padding: 0;
        }

        .c7n-pro-collapse-item {
          border-bottom: none;
          margin-bottom: 24px;
          background-color: #fafafa;

          .c7n-pro-collapse-header {
            height: 48px;
            line-height: 48px;

            > i {
              font-weight: bold;
              margin-left: 8px;
              color: rgb(41, 190, 206);
            }

            > h3 {
              text-indent: 16px;
              display: inline-block;
              line-height: 1.17em;
            }

            > a {
              margin-left: 16px;
            }
          }

          .c7n-pro-collapse-content {
            border-top: none;

            .c7n-pro-collapse-content-box {
              padding-bottom: 0;

              .last-form-item {
                margin-bottom: 0;

                .c7n-pro-row {
                  margin-bottom: 0;
                }

                .c7n-pro-form-item-control-wrapper
                  .c7n-pro-form-item-control
                  .c7n-pro-form-item-children
                  pre {
                  margin-bottom: 0;
                }
              }
            }
          }
        }

        /* UED行样式 */
        /* // 纯展示行 */
        .read-row {
          line-height: 20px;
          margin-bottom: 16px;

          .c7n-pro-col-8 > .c7n-pro-row.c7n-pro-form-item {
            > .c7n-pro-col-9.c7n-pro-form-item-label {
              line-height: 20px;

              label {
                color: #666;
              }
            }

            > .c7n-pro-col-15.c7n-pro-form-item-control-wrapper {
              > .c7n-pro-form-item-control {
                line-height: 20px;
              }
            }

            margin-bottom: 0;
          }
        }

        /* // 纯编辑行 */
        .writable-row {
          margin-bottom: 16px;

          > .c7n-pro-col-8 {
            .c7n-pro-calendar-picker,
            .c7n-pro-input-number {
              width: 100%;
            }
            > .c7n-pro-row.c7n-pro-form-item {
              margin-bottom: 0;

              > .c7n-pro-col-9.c7n-pro-form-item-label {
                line-height: 28px;
                color: #666;

                label {
                  color: #666;
                }
              }

              > .c7n-pro-col-15.c7n-pro-form-item-control-wrapper > .c7n-pro-form-item-control {
                line-height: 28px;
              }
            }
          }
        }

        /* // 混合行 */
        .inclusion-row > .c7n-pro-col-8 {
          .c7n-pro-calendar-picker,
          .c7n-pro-input-number {
            width: 100%;
          }
          > .c7n-pro-row {
            margin-bottom: 16px;

            > div {
              line-height: 28px;
            }
          }

          > .c7n-pro-row.c7n-pro-form-item {
            > .c7n-pro-col-9.c7n-pro-form-item-label {
              line-height: 28px;
              color: #666;

              label {
                color: #666;
              }
            }

            > .c7n-pro-col-15.c7n-pro-form-item-control-wrapper > .c7n-pro-form-item-control {
              line-height: 28px;
            }
          }
        }

        /* // 编辑状态半行 */
        .half-row {
          display: flex;
          margin-bottom: 16px;

          .c7n-pro-row.c7n-pro-form-item {
            margin-bottom: 0;

            > .c7n-pro-form-item-label {
              width: 25%;
              margin-right: -6px;
              float: left;
              line-height: 28px;

              label {
                color: #666;
              }
            }

            > .c7n-pro-form-item-control-wrapper {
              width: 75%;
              float: left;
              color: #333;
              line-height: 28px;

              .c7n-pro-form-item-control {
                line-height: 28px;
              }
            }
          }
        }

        /* // 纯展示半行 */
        .read-half-row {
          display: flex;
          margin-bottom: 16px;

          .c7n-pro-row.c7n-pro-form-item {
            margin-bottom: 0;

            > .c7n-pro-form-item-label {
              width: 25%;
              margin-right: -6px;
              float: left;
              line-height: 20px;

              label {
                color: #666;
              }
            }

            > .c7n-pro-form-item-control-wrapper {
              width: 75%;
              float: left;
              color: #333;
              line-height: 20px;

              .c7n-pro-form-item-control {
                line-height: 20px;
              }
            }
          }
        }

        /* // 表单校验文字样式 */
        .c7n-pro-form-item-control-wrapper {
          .c7n-pro-form-item-control.has-error.c7n-pro-form-item-required .c7n-pro-form-explain {
            margin-top: 2px;
          }
        }
      }
    }

    /* // 表单字段 通用样式 */
    .ued-form-field {
      width: 100%;
    }

    /* // ------------ List Page ----------- */

    .search-btn-more {
      padding-left: 24px !important;

      .c7n-pro-form-item-children {
        > button:not(:last-child),
        > a:not(:last-child) {
          margin-right: 8px;
        }
      }
    }

    .more-fields-form {

      .c7n-pro-input-number,
      .c7n-pro-calendar-picker,
      .c7n-pro-form-item {
        width: 100%;
      }


      > div.c7n-pro-form-item {
        margin-bottom: 3px;

        &:last-child {
          position: absolute;
          bottom: 0;
          left: 0;
          padding: 4px 0;
          margin-bottom: 0;
          z-index: 1;
          border-top: 1px solid #e8e8e8;
          background: rgba(255, 255, 255, 1);

          .c7n-pro-form-item-children {
            float: right;
            margin-right: 12px;
          }
        }
      }
    }

    /* // 查询表单 */
    .table-list-search {
      margin-bottom: 16px;

      .c7n-pro-form-item {
        margin-bottom: 2px;

        .c7n-pro-form-item-with-help {
          margin-bottom: 14px;
        }
      }
    }

    /* // 查询表单样式(集合table-list-search、more-fields-form) */
    .more-fields-search-form {
      &.c7n-pro-form {
        margin-bottom: 16px;
      }
      .c7n-pro-input-number,
      .c7n-pro-calendar-picker,
      .c7n-pro-form-item {
        width: 100%;
        margin-bottom: 0;
      }
    }

    .table-list-operator {
      margin-bottom: 16px;

      button {
        margin-right: 8px;
      }
    }

    .table-list-form {
      .c7n-pro-form-inline .c7n-pro-form-item {
        margin-bottom: 24px;
        margin-right: 0;
        display: flex;

        > .c7n-pro-form-item-label {
          width: auto;
          line-height: 32px;
          padding-right: 8px;
        }

        .c7n-pro-form-item-control {
          line-height: 32px;
        }
      }

      .c7n-pro-form-item-control-wrapper {
        flex: 1;
      }

      .submit-buttons {
        white-space: nowrap;
        margin-bottom: 24px;
      }
    }

    @media screen and (max-width: ${vars['@screen-lg']}px) {
      .tableListForm .c7n-pro-form-item {
        margin-right: 24px;
      }
    }

    @media screen and (max-width: ${vars['@screen-md']}px) {
      .tableListForm .c7n-pro-form-item {
        margin-right: 8px;
      }
    }

    /* // ------------ List Page ----------- */

    /* 详情页面 Card, 二级标题 */

    .c7n-card {
      /* // 表单, 表格 */
      &.ued-detail-card,
      &.ued-detail-card-table {
        > .c7n-card-head {
          border-bottom-color: ${vars['@hzero-bgc-color-dark']};
          padding: ${vars['@hzero-gutter-md']}px ${vars['@hzero-gutter']}px;
          position: relative;

          &:before {
            content: '';
            position: absolute;
            left: 0;
            top: 16px;
            width: 3px;
            height: 14px;
            background-color: ${vars['@hzero-primary-color']};
            pointer-events: none;
          }

          > .c7n-card-head-wrapper {
            > .c7n-card-head-title {
              padding: 0;

              > h3 {
                margin: 0;
                color: ${vars['@hzero-form-disabled-wrapper-color']};
                font-weight: normal;
                ${hzeroFontSize(14)}
              }
            }
          }
        }

        > .c7n-card-body {
          /* // normal form */
          .c7n-pro-form-item {
            margin-bottom: 0;
            &.c7n-pro-form-item-with-help {
              margin-bottom: ${vars['@hzero-gutter-inline']}px;
            }
          }
          .c7n-pro-form-item-label {
            text-align: left;
            color: ${vars['@hzero-form-disabled-label-color']};
          }

          .c7n-pro-form-item-control {
            color: ${vars['@hzero-form-disabled-wrapper-color']};

            .c7n-pro-calendar-picker,
            .c7n-pro-input-number {
              width: 100%;
            }

            .c7n-pro-form-explain {
              margin-top: 2px;
            }
          }

          /* UED行样式 */
          /* // 纯展示行 */
          .read-row {
            line-height: 20px;
            margin-bottom: 16px;

            .c7n-pro-col-8 > .c7n-pro-row.c7n-pro-form-item {
              > .c7n-pro-col-9.c7n-pro-form-item-label {
                line-height: 20px;

                label {
                  color: #666;
                }
              }

              > .c7n-pro-col-15.c7n-pro-form-item-control-wrapper {
                > .c7n-pro-form-item-control {
                  line-height: 20px;
                }
              }

              margin-bottom: 0;
            }
          }

          /* // 纯编辑行 */
          .writable-row {
            margin-bottom: 16px;

            > .c7n-pro-col-8 {
              > .c7n-pro-row.c7n-pro-form-item {
                margin-bottom: 0;

                > .c7n-pro-col-9.c7n-pro-form-item-label {
                  line-height: 28px;
                  color: #666;

                  label {
                    color: #666;
                  }
                }

                > .c7n-pro-col-15.c7n-pro-form-item-control-wrapper > .c7n-pro-form-item-control {
                  line-height: 28px;
                }
              }
            }
          }

          /* 混合行 */
          .inclusion-row > .c7n-pro-col-8 {
            > .c7n-pro-row {
              margin-bottom: 16px;

              > div {
                line-height: 28px;
              }
            }

            > .c7n-pro-row.c7n-pro-form-item {
              > .c7n-pro-col-9.c7n-pro-form-item-label {
                line-height: 28px;
                color: #666;

                label {
                  color: #666;
                }
              }

              > .c7n-pro-col-15.c7n-pro-form-item-control-wrapper > .c7n-pro-form-item-control {
                line-height: 28px;
              }
            }
          }

          /* // 编辑状态半行 */
          .half-row {
            display: flex;
            margin-bottom: 16px;

            .c7n-pro-row.c7n-pro-form-item {
              margin-bottom: 0;

              > .c7n-pro-form-item-label {
                width: 25%;
                margin-right: -6px;
                float: left;
                line-height: 28px;

                label {
                  color: #666;
                }
              }

              > .c7n-pro-form-item-control-wrapper {
                width: 75%;
                float: left;
                color: #333;
                line-height: 28px;

                .c7n-pro-form-item-control {
                  line-height: 28px;
                }
              }
            }
          }

          /* // 纯展示半行 */
          .read-half-row {
            display: flex;
            margin-bottom: 16px;

            .c7n-pro-row.c7n-pro-form-item {
              margin-bottom: 0;

              > .c7n-pro-form-item-label {
                width: 25%;
                margin-right: -6px;
                float: left;
                line-height: 20px;

                label {
                  color: #666;
                }
              }

              > .c7n-pro-form-item-control-wrapper {
                width: 75%;
                float: left;
                color: #333;
                line-height: 20px;

                .c7n-pro-form-item-control {
                  line-height: 20px;
                }
              }
            }
          }

          /* // 最后一行表单 */
          .last-form-item {
            margin-bottom: 0;

            .c7n-pro-row {
              margin-bottom: 0;
            }

            .c7n-pro-form-item-control-wrapper
              .c7n-pro-form-item-control
              .c7n-pro-form-item-children
              pre {
              margin-bottom: 0;
            }
          }
        }
      }

      &.ued-detail-card {
        > .c7n-card-body {
          padding: ${vars['@hzero-gutter']}px;
          padding-bottom: 24px;
        }
      }

      &.ued-detail-card-table {
        > .c7n-card-body {
          padding: ${vars['@hzero-gutter']}px 0;

          .table-list-operator {
            display: flex;

            flex-direction: row-reverse;
            justify-content: flex-start;
            align-items: center;

            /* // TODO: 还需修改样式 间距
            // 所有按钮给一个左间距
            // 由于方向反转了 所以 是 last-child 没有 margin-left */
            > .c7n-pro-btn,
            > .table-list-operation {
              margin: 0;
            }

            > .c7n-pro-btn:not(:last-child),
            > .table-list-operation:not(:last-child) {
              margin-left: ${vars['@hzero-gutter-sm']}px;
            }
          }
        }
      }
    }

    /* Card 三级标题 */

    .c7n-card {
      /* // 表单, 表格 */
      &.ued-detail-card-third,
      &.ued-detail-card-table-third {
        > .c7n-card-head {
          /* //border-bottom-color: ${vars['@hzero-bgc-color-dark']}; */
          border: 0;
          padding: ${vars['@hzero-gutter-md']}px ${vars['@hzero-gutter']}px;
          position: relative;

          > .c7n-card-head-wrapper {
            > .c7n-card-head-title {
              padding: 0;

              > h3 {
                margin: 0;
                color: #333;
                font-weight: normal;
                ${hzeroFontSize(14)};
              }
            }
          }
        }
      }

      &.ued-detail-card-third {
        > .c7n-card-body {
          padding: ${vars['@hzero-gutter']}px;
        }
      }

      &.ued-detail-card-table-third {
        > .c7n-card-body {
          padding: ${vars['@hzero-gutter']}px 0;

          .table-list-operator {
            display: flex;

            flex-direction: row-reverse;
            justify-content: flex-start;
            align-items: center;

            /* // TODO: 还需修改样式 间距
            // 所有按钮给一个左间距
            // 由于方向反转了 所以 是 last-child 没有 margin-left */
            > .c7n-pro-btn,
            > .table-list-operation {
              margin: 0;
            }

            > .c7n-pro-btn:not(:last-child),
            > .table-list-operation:not(:last-child) {
              margin-left: ${vars['@hzero-gutter-sm']}px;
            }
          }
        }
      }
    }

    /* // 表格的操作按钮 的 父元素的class */
    .table-operator {
      margin-bottom: 16px;
      display: flex;

      flex-direction: row-reverse;
      justify-content: flex-start;
      align-items: center;

      /* // TODO: 还需修改样式 间距
      // 所有按钮给一个左间距
      // 由于方向反转了 所以 是 lat-child 没有 margin-left */
      > .c7n-pro-btn:not(:last-child),
      > .table-list-operation:not(:last-child) {
        margin: 0 0 0 ${vars['@hzero-gutter-sm']}px;
      }
    }

    /* 编辑表格样式 */

    .ued-edit-form {
      /* UED行样式 */
      /* // normal form */
      .c7n-pro-form-item-label {
        text-align: left;
        color: ${vars['@hzero-form-disabled-label-color']};
      }

      .c7n-pro-form-item-control {
        color: ${vars['@hzero-form-disabled-wrapper-color']};
      }

      margin-bottom: 0;

      .c7n-pro-form-item.c7n-pro-form-item-with-help {
        margin-bottom: ${vars['@hzero-gutter-inline']}px;
      }

      /* UED行样式 */
      /* // 纯展示行 */
      .read-row {
        line-height: 20px;
        margin-bottom: 16px;

        .c7n-pro-col-8 > .c7n-pro-row.c7n-pro-form-item {
          > .c7n-pro-col-9.c7n-pro-form-item-label {
            line-height: 20px;

            label {
              color: #666;
            }
          }

          > .c7n-pro-col-15.c7n-pro-form-item-control-wrapper {
            > .c7n-pro-form-item-control {
              line-height: 20px;
            }
          }

          margin-bottom: 0;
        }
      }

      /* // 纯编辑行 */
      .writable-row {
        margin-bottom: 16px;

        > .c7n-pro-col-8 {
          > .c7n-pro-row.c7n-pro-form-item {
            margin-bottom: 0;

            > .c7n-pro-col-9.c7n-pro-form-item-label {
              line-height: 28px;
              color: #666;

              label {
                color: #666;
              }
            }

            > .c7n-pro-col-15.c7n-pro-form-item-control-wrapper > .c7n-pro-form-item-control {
              line-height: 28px;
            }
          }
        }
      }

      /* // 混合行 */
      .inclusion-row > .c7n-pro-col-8 {
        > .c7n-pro-row {
          margin-bottom: 16px;

          > div {
            line-height: 28px;
          }
        }

        > .c7n-pro-row.c7n-pro-form-item {
          > .c7n-pro-col-9.c7n-pro-form-item-label {
            line-height: 28px;
            color: #666;

            label {
              color: #666;
            }
          }

          > .c7n-pro-col-15.c7n-pro-form-item-control-wrapper > .c7n-pro-form-item-control {
            line-height: 28px;
          }
        }
      }

      /* // 编辑状态半行 */
      .half-row {
        display: flex;
        margin-bottom: 16px;

        .c7n-pro-row.c7n-pro-form-item {
          margin-bottom: 0;

          > .c7n-pro-form-item-label {
            width: 25%;
            margin-right: -6px;
            float: left;
            line-height: 28px;

            label {
              color: #666;
            }
          }

          > .c7n-pro-form-item-control-wrapper {
            width: 75%;
            float: left;
            color: #333;
            line-height: 28px;

            .c7n-pro-form-item-control {
              line-height: 28px;
            }
          }
        }
      }

      /* // 纯展示半行 */
      .read-half-row {
        display: flex;
        margin-bottom: 16px;

        .c7n-pro-row.c7n-pro-form-item {
          margin-bottom: 0;

          > .c7n-pro-form-item-label {
            width: 25%;
            margin-right: -6px;
            float: left;
            line-height: 20px;

            label {
              color: #666;
            }
          }

          > .c7n-pro-form-item-control-wrapper {
            width: 75%;
            float: left;
            color: #333;
            line-height: 20px;

            .c7n-pro-form-item-control {
              line-height: 20px;
            }
          }
        }
      }

      /* // 最后一行表单 */
      .last-row {
        margin-bottom: 0;

        .c7n-pro-row {
          margin-bottom: 0;
        }

        .c7n-pro-form-item-control-wrapper
          .c7n-pro-form-item-control
          .c7n-pro-form-item-children
          pre {
          margin-bottom: 0;
        }
      }
    }
  `;
};

export default createGlobalStyle`
  /* hzero-c7n-polyfill-style */
  html body {
    .c7n-pro-modal-title {
      font-size: 14px;
    }
    ${fitUed}
    ${sectionFour}
    ${sectionOne}
    ${sectionTwo}
    ${sectionThree}
    ${sectionFive}
  }

`;
