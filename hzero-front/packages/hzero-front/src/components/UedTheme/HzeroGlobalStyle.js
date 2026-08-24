/**
 * 全局样式，作用于所有主题
 */
import { createGlobalStyle, css } from 'styled-components';
// 配置中心没有导入样式，这些在配置中心使用的组件的样式手动导入一下
import 'hzero-ui/lib/anchor/style';
import 'hzero-ui/lib/switch/style';
import 'hzero-ui/lib/slider/style';

const modalWrapCss = css`
  text-align: center;
  &&:before {
    content: '';
    display: inline-block;
    width: 0;
    height: 100%;
    vertical-align: middle;
  }
`;

const modalContentCss = css`
  position: static;
  display: inline-block;
  vertical-align: middle;
  text-align: left;
`;

const globalCss = css`
  body {
    .action-link > * {
      vertical-align: middle;
    }
    .c7n-form-line-with-btn {
      display: flex;
      flex-direction: row;
      padding-bottom: 8px;
      align-items: flex-end;
      .c7n-form-btn {
        display: flex;
        align-items: flex-end;
        padding-bottom: 8px;
      }
      .c7n-pro-form {
        padding-right: 8px;
        .c7n-pro-field-wrapper {
          padding-bottom: 0;
        }
      }
    }
    .form-btn-expand {
      display: flex;
      flex-flow: row nowrap;
      button {
        flex: 1;
      }
    }
    .c7n-form-float-row {
      display: flex;
      flex-flow: row nowrap;
      align-items: flex-start;
    }
  }

  /* .c7n-pro-modal-container-pristine {
    position: fixed;
    width: 100%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    z-index: 5;
    .c7n-pro-modal {
      position: static;
      .c7n-pro-modal-movable {
        cursor: initial;
      }
      &.c7n-pro-modal-fullscreen {
        position: absolute;
        top: calc(50% - 50vh);
        height: 100vh;
      }
    }
  } */
  .c7n-modal-wrap {
    ${modalWrapCss}
    .c7n-modal-sidebar {
      text-align: left;
    }
    && .c7n-modal:not(.c7n-modal-sidebar) {
      ${modalContentCss}
    }
  }
  /* .ant-modal-wrap {
    ${modalWrapCss}
    && .ant-modal {
      ${modalContentCss}
    }
  } */
`;

export default createGlobalStyle`
  ${globalCss};
`;
