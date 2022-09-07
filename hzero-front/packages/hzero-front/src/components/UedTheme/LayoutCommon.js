import { css } from 'styled-components';
import { getRequiredData, hexToRgbaColor } from '@hzero-front-ui/cfg';

const getPageCss = ({
  titleFontSize,
  backIconSize,
  contentPadding,
  contentWrapMargin,
  headBg,
  headPadding,
  headRadius,
}) => css`
  .page-head.page-head {
    display: flex;
    width: 100%;
    padding: ${headPadding};
    border-radius: ${headRadius}px;
    background: ${headBg};
    border: none;
    .back-btn {
      margin: 0;
      padding: 0;
      font-size: ${backIconSize}px;
    }
    .page-head-title {
      margin: 0;
      font-size: ${titleFontSize}px;
      line-height: initial;
    }
    .page-head-operator {
      height: 100%;
      margin: 0;
      float: right;
      > .ant-btn,
      > .c7n-btn,
      > .c7n-pro-btn {
        margin-left: 10px;
      }
    }
    .title-wrapper {
      display: flex;
      width: 100%;
      justify-content: flex-end;
      align-items: center;
      margin-top: 12px;
      margin-bottom: 12px;
      .back-btn {
        width: 24px;
        height: 24px;
        padding: 0;
        margin: 0 8px 0 0;
      }
      .page-head-title {
        font-family: PingFangSC-Medium, sans-serif;
        font-size: 14px;
        color: #5a6677;
        line-height: 14px;
      }
    }
    .tabs-wrapper {
      box-sizing: border-box;
      /* padding-left: 8px; */
      width: 100%;
      margin-top: -12px;
      height: auto;
      .ant-tabs {
        .ant-tabs-bar {
          margin: 0;
        }
        .ant-tabs-tab {
          position: relative;
          height: 38px;
          padding: 0;
          margin: 0 16px;
          line-height: 38px;
          text-align: center;
          border-radius: 4px;
          transition: all 0.3s;
          :after {
            display: block;
            position: absolute;
            left: -8px;
            top: 0;
            content: '';
            width: calc(100% + 16px);
            height: 100%;
            border-radius: 4px;
            transition: all 0.3s;
            background-color: rgba(7, 17, 27, 0);
          }
          :hover:after {
            background-color: rgba(7, 17, 27, 0.05);
          }
        }
        .ant-tabs-ink-bar {
          height: 3px;
          background: #3b87f5;
        }
        .ant-tabs-nav-wrap {
          .ant-tabs-tab-active {
            color: #3b87f5;
          }
        }
      }
    }
  }
  .page-container {
    height: 100%;
    overflow: hidden;
    display: flex;
    flex-flow: column nowrap;
    .page-content-wrap {
      background: #fff;
      height: 100%;
      flex: 1;
      overflow: hidden;
      margin: ${contentWrapMargin};
      .page-content {
        padding: ${contentPadding}px;
        height: 100%;
        border-radius: 4px;
        margin: 0;
        position: relative;
        overflow: auto;
      }
    }
  }
`;

/**
 ::-webkit-scrollbar {
      width: 4px;
      height: 4px;
    }
 ::-webkit-scrollbar-thumb {
      border-radius: 5px;
      box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
      background: ${hexToRgbaColor(colors.primary, 0.8)};
    }
 ::-webkit-scrollbar-track {
      box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
      border-radius: 0;
      background: rgba(0, 0, 0, 0.1);
    }
 */
function getCommonCss(props) {
  const colors = getRequiredData(props, 'colors');
  const {
    selectionFontColor,
    noticeIcon,
    tabIndent = 8,
    collapseTriggerColor,
    bodyContentGap = 8,
  } = getRequiredData(props, 'layout');
  return css`
    /* 通用样式 */
    body a {
      color: ${colors.primary};
      :hover {
        color: ${hexToRgbaColor(colors.primary, 0.8)};
      }
    }
    ::selection,
    body *::selection {
      color: ${selectionFontColor} !important;
      background: ${colors.primary} !important;
    }
    body .im-btn .im-btn-icon {
      background: ${colors.primary};
    }
    body .ant-select-allow-clear .ant-select-selection--multiple .ant-select-selection__rendered {
      margin-right: 11px;
    }
    #root {
      .hzero-layout .hzero-header-content .hzero-menu-trigger {
        left: ${tabIndent}px;
        font-size: 18px;
        color: ${collapseTriggerColor};
        top: 16px;
        padding-left: ${bodyContentGap + tabIndent}px;
        padding-right: ${bodyContentGap + tabIndent}px;
      }
      .hzero-common-layout-header .hzero-common-layout-header-toolbar-left {
        padding-left: ${bodyContentGap + tabIndent}px;
      }
      .hzero-side-layout-container .hzero-side-layout-body .hzero-side-layout-collapsed-trigger,
      .hzero-normal-collapsed-trigger {
        z-index: 1;
        top: -34px;
        font-size: 18px;
        cursor: pointer;
        position: absolute;
        left: ${tabIndent}px;
        color: ${collapseTriggerColor};
      }
      .hzero-normal-header-right-item-notice .ant-badge .anticon,
      .hzero-header-right > span > .ant-badge .anticon,
      .hzero-side-layout-header-right-item-notice .ant-badge .anticon,
      .hzero-common-layout-header-toolbar-item .ant-badge .anticon {
        background: url("${noticeIcon}") no-repeat;
        background-size: cover;
        transition: all .2s;
        :hover {
          opacity: 0.8;
        }
      }
    }
  `;
}

const getTabsStyle = (props) => {
  const {
    tabBg,
    tabFontColor,
    tabActiveBg,
    tabActiveFontColor,
    tabHoverBg,
    tabHoverFontColor,
    workspaceIconOpacity,
    workspaceIconHoverOpacity,
    workspaceIconActiveOpacity,
    workspaceIcon,
    workspaceActiveIcon,
    tabIndent = 8,
  } = getRequiredData(props, 'layout');
  return css`
    .hzero-normal-container.hzero-normal-container
      .hzero-normal-body
      .hzero-normal-content
      .hzero-normal-content-container,
    .hzero-common-layout-container.hzero-common-layout-container
      .hzero-common-layout-body
      .hzero-common-layout-content-wrap
      .hzero-common-layout-content,
    .hzero-side-layout-container > .hzero-side-layout-body >
      .hzero-side-layout-content
      > .hzero-side-layout-content-container,
    .hzero-layout .hzero-page.hzero-page {
      > .ant-tabs {
        margin-top: 0;
        &.ant-tabs-card > .ant-tabs-bar {
          background: none;
        }
      }
      > .ant-tabs.ant-tabs-top
        > .ant-tabs-content
        > .ant-tabs-tabpane
        > .page-container {
        background-color: transparent;
        .page-content-wrap > .page-content {
          height: 100%;
        }
      }
      > .ant-tabs.ant-tabs-top .ant-tabs-content {
        height: calc(100% - 48px);
      }
      > .ant-tabs.ant-tabs-top > .ant-tabs-bar {
        padding-left: ${tabIndent}px;
        margin-bottom: 12px;
        background-color: transparent;
        border-bottom: 0;
        > .ant-tabs-nav-container > .ant-tabs-nav-wrap > .ant-tabs-nav-scroll {
          background: none;
        }
        > .ant-tabs-nav-container {
          > .ant-tabs-tab-prev, > .ant-tabs-tab-next {
            span:before {
              color: ${tabFontColor};
            }
          }
        }
        > .ant-tabs-nav-container
          > .ant-tabs-nav-wrap
          > .ant-tabs-nav-scroll
          > .ant-tabs-nav
          > div
          > .ant-tabs-tab {
          margin: 0 5px 0 0;
          padding: 9px 12px;
          height: 36px;
          border: none;
          border-radius: 4px;
          background: ${tabBg};
          color: ${tabFontColor};
          :first-of-type {
            margin: 0 5px 0 0 !important;
            background: ${tabBg};
          }
          &.ant-tabs-tab-active:first-of-type {
            background: ${tabActiveBg};
          }
          > div > span > .anticon {
            vertical-align: middle;
          }
          :first-of-type .ant-tabs-tab-unclosable span .anticon:before {
            display: inline-block;
            width: 14px;
            height: 14px;
            background: url("${workspaceIcon}");
            content: '';
            background-size: cover;
            opacity: ${workspaceIconOpacity};
          }
          .anticon-close {
            color: #fff;
          }
          div {
            height: 18px;
            line-height: 0;
            color: ${tabFontColor};
            span {
              line-height: 18px;
              color: ${tabFontColor};
            }
          }
          :first-of-type.ant-tabs-tab-active .ant-tabs-tab-unclosable span .anticon:before {
            opacity: ${workspaceIconActiveOpacity};
            background: url("${workspaceActiveIcon}");
            background-size: cover;
          }
          :first-of-type:hover .ant-tabs-tab-unclosable span .anticon:before {
            opacity: ${workspaceIconHoverOpacity};
          }
          &.ant-tabs-tab-active {
            background-color: ${tabActiveBg};
            color: ${tabActiveFontColor};
            div span {
              color: #3b87f5;
            }
            .anticon-close {
              color: ${tabActiveFontColor};
            }
          }
          :hover {
            background-color: ${tabHoverBg};
            div {
              color: ${tabHoverFontColor};
            }
          }
          :after,
          :before {
            border-width: 0;
          }
        }
      }
    }
  `;
};

function getFullScreenStyle() {
  return css`
    .hzero-fullscreen {
      .hzero-content,
      .hzero-normal-body,
      .hzero-side-layout-body,
      .hzero-common-layout-body {
        height: 100% !important;
        margin: 0 !important;
        box-sizing: border-box;
        padding: 8px 0;
      }
      .hzero-page,
      .hzero-normal-content-container,
      .hzero-side-layout-content-container,
      .hzero-common-layout-content {
        > .ant-tabs > .ant-tabs-content {
          height: 100% !important;
        }
      }
    }
  `;
}

function getSelectMenuStyle(props) {
  const {
    dropdownItemBg,
    dropdownItemFontColor,
    dropdownItemHoverBg,
    dropdownItemFontHoverColor,
    searchInputBg,
    searchInputBorder,
    searchInputPlaceholderFontColor,
  } = getRequiredData(props, 'layout');
  return css`
    .ant-select-dropdown.hzero-common-layout-side-search-select-wrap,
    .ant-select-dropdown.hzero-side-layout-side-search-select-wrap,
    .hzero-common-layout-header-search-select-wrap,
    .hzero-normal-side-search-select-wrap {
      && .ant-select-dropdown-menu-item {
        background: ${dropdownItemBg};
        color: ${dropdownItemFontColor};
        :hover,
        &.ant-select-dropdown-menu-item-active {
          background: ${dropdownItemHoverBg};
          color: ${dropdownItemFontHoverColor};
        }
      }
    }

    body .ant-popover {
      .hzero-side-layout-side-search,
      .hzero-normal-side-search .hzero-normal-side-search-input,
      .hzero-common-layout-header-search .hzero-common-layout-header-search-input {
        .ant-select-sm {
          border-radius: 0;
        }
        background: ${searchInputBg};
        border-radius: 0;
        .hzero-side-layout-side-search-input {
          background: ${searchInputBg};
        }
      }
      .hzero-normal-side-search,
      .hzero-common-layout-header-search {
        background: none;
        border-radius: 2px;
      }
    }

    .hzero-normal-nav-normal-search .hzero-normal-side-search .hzero-normal-side-search-input {
      width: 160px;
      background: ${searchInputBg};
      border: ${searchInputBorder};
      .ant-select-selection__placeholder {
        color: ${searchInputPlaceholderFontColor};
      }
    }
  `;
}

function getPageLayoutCss(props) {
  const {
    searchInputBg,
    menuBg,
    collapsedWidth,
    menuNormalWidth,
    menuItemFontColor,
    menuItemFontSize,
    menuItemRightArrowColor,
    menuItemRightArrowSize,
    menuItemHoverFontColor,
    menuItemHoverBg,
    menuItemActiveFontColor,
    // menuItemActiveBg,
    subMenuBg,
    subMenuHoverBg,
    subMenuHoverColor,
    leafMenuBg,
    leafMenuHoverBg,
    leafMenuHoverColor,
    containerBg,
    headerHeight,
    headerBgImage,
    headerTitleFontSize,
    headerTitleColor,
    headerBg,
    bodyContentGap = 8,
    extraCss = '',
  } = getRequiredData(props, 'layout');
  const colors = getRequiredData(props, 'colors');
  return css`
    /* 侧边平铺 */
    .hzero-normal-container.hzero-normal-container {
      background: ${containerBg};
      &.hzero-normal-container-collapsed {
        .hzero-normal-header > .hzero-normal-header-container .hzero-normal-header-left {
          width: ${collapsedWidth}px;
        }
      }
      .hzero-normal-header {
        height: ${headerHeight}px;
        padding: 0;
        background: url(${headerBgImage}) no-repeat center;
        background-color: ${headerBg};
        background-size: cover;
        .hzero-normal-header-container {
          .hzero-normal-header-left {
            width: ${menuNormalWidth}px;
            background-color: #fff;
            transition: width 0.2s linear;
            .hzero-normal-header-logo-sign,
            .hzero-normal-header-logo {
              margin-left: 0;
              a {
                justify-content: center;
                .hzero-normal-header-title {
                  font-size: ${headerTitleFontSize}px;
                  color: ${headerTitleColor};
                  font-weight: bold;
                }
              }
            }
          }
          .hzero-normal-header-right {
            .ant-select {
              border: none;
              :after,
              :before {
                display: none;
              }
            }
            .hzero-normal-header-right-item-avatar {
              white-space: nowrap;
              .ant-avatar {
                margin: 0 12px;
              }
            }
          }
          .hzero-normal-header-right div .ant-select-selection-selected-value,
          .hzero-normal-header-right span,
          .hzero-normal-header-right span.ant-badge {
            color: #fff;
          }
        }
      }
      .hzero-normal-body {
        margin-top: -${headerHeight - 44}px;
        background: unset;
        .hzero-normal-nav {
          width: ${menuNormalWidth}px;
          .hzero-normal-nav-container {
            background: ${menuBg};
          }
          .hzero-normal-nav-normal-search {
            .hzero-normal-side-search-icon {
              background-color: #efefef;
            }
            .ant-select-selection.ant-select-selection--single:focus,
            .ant-select-selection.ant-select-selection--single {
              outline: none;
              box-shadow: none;
            }
          }
        }
        .hzero-normal-content {
          flex-shrink: initial;
          margin: 0 ${bodyContentGap}px;
          .hzero-normal-content-container {
          }
        }
      }
      &.hzero-normal-container-collapsed {
        .hzero-normal-header-container .hzero-normal-header-left .hzero-normal-header-logo {
          margin-left: 0;
        }
        .hzero-normal-body {
          .hzero-normal-nav {
            width: ${collapsedWidth}px;
            .hzero-normal-nav-container {
              background-color: #fff;
              .hzero-normal-nav-normal-search {
                .hzero-normal-side-search {
                  .ant-select-selection.ant-select-selection--single {
                    background: ${searchInputBg};
                    border-radius: 4px;
                  }
                }
              }
            }
          }
        }
      }
    }
    .hzero-normal-nav-menu {
      .hzero-main-menu-wrap {
        .hzero-main-menu {
          background: ${menuBg};
          &.hzero-main-menu-collapsed {
            .hzero-sub-menu-wrap {
              left: ${collapsedWidth}px;
            }
            .hzero-main-menu-item {
              &.hzero-main-menu-item-active {
                .hzero-sub-menu-mask {
                  display: block;
                  left: ${collapsedWidth}px;
                  width: calc(100% - ${collapsedWidth}px);
                }
              }
            }
          }
          .hzero-sub-menu-wrap {
            left: ${menuNormalWidth}px;
          }
          .hzero-main-menu-item {
            width: 100%;
            .hzero-sub-menu-mask {
              display: none;
            }
            &.hzero-main-menu-item-active {
              .hzero-sub-menu-mask {
                display: block;
                position: fixed;
                top: 0;
                left: ${menuNormalWidth}px;
                width: calc(100% - ${menuNormalWidth}px);
              }
            }
            &.hzero-main-menu-item-active,
            &.hzero-main-menu-item-tab-active {
              /* position: relative; */
              .hzero-main-menu-item-content {
                position: relative;
                color: ${menuItemActiveFontColor};
                background: ${hexToRgbaColor(colors.primary, 0.1)};
                :after {
                  border-color: ${colors.primary};
                }
              }
              .hzero-main-menu-item-content:before {
                background: ${colors.primary}!important;
              }
            }
            .hzero-main-menu-item-content {
              color: ${menuItemFontColor};
              font-family: MicrosoftYaHei;
              font-size: ${menuItemFontSize}px;
              letter-spacing: 0;
              &:before {
                content: '';
                position: absolute;
                top: 0;
                bottom: 0;
                left: 0;
                width: 4px;
                background: none !important;
                border: none !important;
              }
              &:after {
                margin-right: 8px;
                content: '';
                height: ${menuItemRightArrowSize}px;
                width: ${menuItemRightArrowSize}px;
                overflow: hidden;
                font-size: 0;
                line-height: 0;
                border: none;
                border-left: 1px solid ${menuItemRightArrowColor};
                border-bottom: 1px solid ${menuItemRightArrowColor};
                transform: rotate(-135deg);
              }
            }
          }
          .hzero-main-menu-item-hover .hzero-main-menu-item-content {
            color: ${menuItemHoverFontColor};
            background: ${menuItemHoverBg};
            :before {
              background: ${colors.primary}!important;
            }
          }
          .hzero-sub-menu-wrap {
            .hzero-sub-menu {
              background: ${subMenuBg};
              .hzero-sub-menu-item .hzero-sub-menu-item-content .hzero-sub-menu-item-title {
                font-family: MicrosoftYaHei;
                font-size: ${menuItemFontSize}px;
                color: ${menuItemFontColor};
                letter-spacing: 0;
              }
              .hzero-sub-menu-item-active .hzero-sub-menu-item-content,
              .hzero-sub-menu-item-hover .hzero-sub-menu-item-content,
              .hzero-sub-menu-item .hzero-sub-menu-item-content:hover {
                background: ${subMenuHoverBg} !important;
                .hzero-sub-menu-item-title {
                  color: ${subMenuHoverColor};
                }
              }
              .hzero-sub-menu-item-active {
                border-left-color: ${colors.primary};
              }
            }
            .hzero-leaf-menu {
              background: ${leafMenuBg};
              .hzero-leaf-menu-item-wrap .hzero-leaf-menu-item {
                font-family: MicrosoftYaHei;
                font-size: ${menuItemFontSize}px;
                color: ${menuItemFontColor};
                letter-spacing: 0;
                &.hzero-leaf-menu-item-active,
                :hover {
                  background: ${leafMenuHoverBg};
                  color: ${leafMenuHoverColor};
                }
              }
            }
          }
        }
      }
    }

    ${extraCss};
  `;
}

const getCommonLayoutStyle = (props) => {
  const d = getRequiredData(props, 'layout');
  const pageStyle = getPageCss(d);
  const commonCss = getCommonCss(props);
  const layoutCss = getPageLayoutCss(props);
  return css`
    /* 页面布局样式 */
    ${pageStyle}
    ${commonCss}
    ${layoutCss}
    ${getTabsStyle}
    ${getFullScreenStyle}
    ${getSelectMenuStyle}
  `;
};

export default getCommonLayoutStyle;
