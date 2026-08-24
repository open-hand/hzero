/*
 * 全局样式复写，覆盖就项目中的全局样式
 * 只作用于非默认主题，即非theme2 schema
 * */
import { css } from 'styled-components';
import { getRequiredData } from '@hzero-front-ui/cfg';

function getTopFormStyle(props) {
  const { layout, inputHeight } = getRequiredData(props, 'input');
  return css`
    .ant-form-inline .ant-form-item {
      min-width: ${layout === 'vertical' ? '' : '320px'};
    }
    .more-fields-search-form {
      .ant-form-item {
        margin-bottom: 0;
        padding-bottom: 8px;
      }
      .search-btn-more {
        margin-top: ${layout === 'vertical' ? `${inputHeight}px` : ''};
      }
    }
    .c7n-form-line-with-btn {
      .c7n-form-btn {
        padding-bottom: 8px;
      }
    }
    .ant-modal-wrap.lov-modal {
      .ant-form {
        > div:not(.ant-table-wrapper) {
          .lov-modal-btn-container {
            height: initial;
            margin-top: ${layout === 'vertical' ? `${inputHeight}px` : ''};
          }
        }
      }
    }
    /* Table 内嵌表单的布局适配 */
    .c7n-pro-table-wrapper {
      > div > div {
        .c7n-pro-form + div {
          padding-bottom: 8px;
          margin-left: 8px;
          margin-top: ${layout === 'vertical' ? inputHeight : 0}px !important;
        }
      }
    }
  `;
}

function getDashboardCss(props) {
  const { primary } = getRequiredData(props, 'colors');
  return css`
    body .hzero-dashboard-card-setting {
      .hzero-dashboard-card-row {
        .hzero-dashboard-card-col {
          .hzero-dashboard-card-col-tag {
            position: relative;
          }
          .ant-tag-checkable:not(.ant-tag-checkable-checked):hover {
            color: ${primary};
          }
          .ant-tag-checkable-checked {
            border: 1px solid ${primary};
            color: ${primary};
            &:after {
              font-family: anticon !important;
              content: '\\E6C4';
              padding: 0;
              margin: 0;
              bottom: 0;
              right: 0;
              width: 12px;
              height: 12px;
              line-height: 12px;
              background-image: none;
              color: ${primary};
            }
          }
        }
      }
    }
  `;
}

function getCss(props) {
  const { primary } = getRequiredData(props, 'colors');
  const { labelAlign, layout } = getRequiredData(props, 'input');

  return css`
    .page-head .ant-form-item .ant-form-item-label {
      display: ${layout === 'vertical' ? 'none' : ''} !important;
    }
    .ant-input-group-addon .ant-select-open .ant-select-selection,
    .ant-input-group-addon .ant-select-focused .ant-select-selection {
      color: ${primary};
    }
    .ant-form-explain,
    .ant-form-extra {
      margin-top: 0;
    }
    .ant-card.ued-detail-card-table > .ant-card-body .ant-form-item-label,
    .ant-card.ued-detail-card > .ant-card-body .ant-form-item-label {
      text-align: ${labelAlign};
    }
    .ant-collapse .ant-collapse-content-box .ant-form-item > .ant-form-item-label {
      text-align: ${labelAlign};
    }
    .ued-detail-card,
    .ued-detail-card-table {
      .ant-card-head {
        position: relative;
        &:before {
          content: '';
          position: absolute;
          left: 0;
          top: 50% !important;
          width: 3px;
          height: 20px;
          transform: translateY(-50%);
          background: ${primary} !important;
        }
      }
    }
  `;
}

export default css`
  ${getDashboardCss}
  /* 适配Lov */
  .lov-modal .ant-form-item.ant-form-item {
    display: block;
    margin-bottom: 8px;
    margin-top: 0px;
  }
  .default-menu-tabs-context-menu .ant-list-item {
    padding: 0 !important;
  }
  body {
    // 修复Pro/Table固定列由于BFC引起的错位问题
    .c7n-pro-table-cell-fix-left-last:after,
    .c7n-pro-table-cell-fix-right-first:after {
      bottom: 0;
    }
    ${getTopFormStyle};
    ${getCss};
    .page-tabs {
      height: 100%;
      .c7n-tabs-content {
        height: calc(100% - 60px);
        .c7n-tabs-tabpane {
          height: 100%;
        }
      }
    }

    .ant-btn.ant-btn-circle {
      padding: 0;
    }
    .c7n-pro-select:focus {
      box-shadow: none;
    }
    .c7n-pro-table-content {
      /* FIXME: 是否需要这个样式 */
      /* 因为modal中table存在滚动，暂时去除 */
      /* overflow: visible; */
    }
    .c7n-pro-table-cell-editable {
      padding: 0 8px !important;
    }
    .c7n-pro-form.c7n-pro-form-horizontal table tbody tr td .c7n-pro-field-wrapper {
      text-align: left;
    }
    .ant-form-item-label label:after {
      margin: 0;
    }
    .c7n-pro-select-suffix .icon-search:before {
      display: block;
      font-size: 12px;
      font-family: anticon, sans-serif !important;
      content: '\\E670' !important;
    }
    .ant-modal-wrap .ant-form-inline .ant-form-item {
      min-width: initial;
    }
    .ant-card.ued-detail-card > .ant-card-body .ant-form-item {
      margin-bottom: 16px;
    }
    .ant-card.ued-detail-card-table > .ant-card-body .ant-form-item {
      margin-bottom: 0;
    }
    .ant-input-affix-wrapper .ant-input-prefix,
    .ant-input-affix-wrapper .ant-input-suffix,
    .ant-form-item-required .lov-suffix:hover .ant-input-suffix {
      background: none;
    }
    /* 覆盖hzero-ui-override, 只在新主题下生效 */
    .ant-notification {
      // notification
      &-notice {
        &.success {
          border-left: none;
        }

        &.info {
          border-left: none;
        }

        &.warn {
          border-left: none;
        }

        &.error {
          border-left: none;
        }
      }
    }
  }
  // 多语言下的显示问题
  html .global-layout .ant-form-item .ant-form-item-label .ant-form-item-required:before,
  html .global-layout .ant-row .ant-form-item-label .ant-form-item-required:before {
    content: '*' !important;
  }
  html
    .global-layout
    .ant-form-item-control-wrapper
    .ant-form-item-required
    .ant-form-item-children:before {
    display: none;
  }
`;
