import { css } from 'styled-components';
import { getRequiredData } from '@hzero-front-ui/cfg';

/* 侧边展开布局的样式 */
export default function getSideExpandLayoutCss(props) {
  const colors = getRequiredData(props, 'colors');
  const {
    searchInputBg,
    searchInputBorder,
    searchInputPlaceholderFontColor,
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
    menuItemActiveBg,
    subMenuBg,
    subMenuHoverBg,
    subMenuHoverColor,
    containerBg,
    headerHeight,
    headerBgImage,
    headerTitleFontSize,
    headerTitleColor,
    headerBg,
    collapseTriggerColor,
    bodyContentGap = 8,
  } = getRequiredData(props, 'layout');
  return css`
    .hzero-common-layout-container.hzero-common-layout-container {
      background: ${containerBg};
      .hzero-common-layout-aside {
        .hzero-common-layout-header-search .hzero-common-layout-header-search-input {
          width: 160px;
          background: ${searchInputBg};
          border: ${searchInputBorder};
          .ant-select-selection__placeholder {
            color: ${searchInputPlaceholderFontColor};
          }
        }
        .hzero-common-layout-header-search-icon {
          background-color: #efefef;
        }
      }
      &.hzero-common-layout-container-collapsed {
        .hzero-common-layout-header .hzero-common-layout-header-container {
          .hzero-common-layout-header-left,
          .hzero-common-layout-header-logo {
            width: ${collapsedWidth}px;
            flex-basis: ${collapsedWidth}px;
            a .hzero-common-layout-header-logo-title {
              flex: 0 0 0;
            }
          }
          .hzero-common-layout-header-left .hzero-common-layout-header-sign {
            width: ${collapsedWidth}px !important;
          }
        }
        .hzero-common-layout-body .hzero-common-layout-aside {
          width: ${collapsedWidth}px;
          flex: 0 0 ${collapsedWidth}px;
          .hzero-common-layout-menu-main-content {
            .hzero-common-layout-menu-main-content-title {
              display: none;
            }
          }
        }
        .hzero-common-layout-body .hzero-common-layout-menu-cascader,
        .hzero-common-layout-menu-wrap
          .hzero-common-layout-menu
          .hzero-common-layout-menu-mask-right,
        .hzero-common-layout-menu-wrap
          .hzero-common-layout-menu
          .hzero-common-layout-menu-mask-top {
          left: ${collapsedWidth}px !important;
        }
      }
      .hzero-common-layout-header {
        height: ${headerHeight}px;
        padding: 0;
        background: url(${headerBgImage}) no-repeat center;
        background-color: ${headerBg};
        background-size: cover;
        &:not(.hzero-common-layout-container-collapsed) {
          .hzero-common-layout-header-left,
          .hzero-common-layout-header-logo {
            max-width: ${menuNormalWidth}px;
          }
        }
        .hzero-common-layout-header-container {
          height: 48px;
          transition: width 0.2s ease;
          .hzero-common-layout-header-logo-sign,
          .hzero-common-layout-header-logo {
            margin-left: 0;
            background: #fff;
            padding: 0;
            a {
              width: 100%;
              display: flex;
              flex-direction: row;
              align-items: center;
              justify-content: center;
              .hzero-common-layout-header-logo-title {
                font-size: ${headerTitleFontSize}px;
                color: ${headerTitleColor};
                font-weight: bold;
                margin-left: 12px;
                flex: 0 0 calc(100% - 52px);
                overflow: hidden;
                text-overflow: ellipsis;
              }
            }
          }
        }
        .hzero-common-layout-header-toolbar {
          .hzero-common-layout-header-collapsed-icon {
            color: ${collapseTriggerColor};
          }
          .ant-select {
            border: none;
            :after,
            :before {
              display: none;
            }
          }
          span.ant-dropdown-trigger {
            white-space: nowrap;
            .ant-avatar {
              margin: 0 12px;
            }
            > span {
              color: #fff;
            }
          }
          div .ant-select-selection-selected-value,
          .hzero-common-layout-header-right span,
          .hzero-common-layout-header-right span.ant-badge {
            color: #fff;
          }
        }
      }
      .hzero-common-layout-body {
        margin-top: -${headerHeight - 48}px;
        overflow: visible;
        .hzero-common-layout-aside {
          background: ${menuBg};
          max-width: ${menuNormalWidth}px;
          width: ${menuNormalWidth}px;
          & {
            background: ${menuBg};
            .hzero-common-layout-nav-normal-search {
              .hzero-common-layout-side-search {
                .ant-select {
                  width: 160px;
                  background: ${searchInputBg};
                  border: ${searchInputBorder};
                  .ant-select-selection__placeholder {
                    color: ${searchInputPlaceholderFontColor};
                  }
                }
              }
            }

            .hzero-common-layout-menu-wrap {
              width: 100%;
              .hzero-common-layout-menu {
                width: 100%;
                background: ${menuBg};
                .hzero-common-layout-menu-mask-top,
                .hzero-common-layout-menu-mask-right {
                  left: ${menuNormalWidth}px;
                }
                .hzero-common-layout-menu-main {
                  width: 100%;
                  .hzero-common-layout-menu-main-content {
                    color: unset;
                    height: 44px;
                    :after {
                      display: block;
                      top: calc(50% - 3px);
                      width: ${menuItemRightArrowSize}px;
                      height: ${menuItemRightArrowSize}px;
                      border-color: ${menuItemRightArrowColor};
                    }
                    :before {
                      display: block;
                      content: '';
                      position: absolute;
                      top: 0;
                      left: 0;
                      bottom: 0;
                      width: 4px;
                      border: none;
                      background: none;
                    }
                    .hzero-icon,
                    > div {
                      font-size: ${menuItemFontSize}px;
                      color: ${menuItemFontColor};
                    }
                    &.hzero-common-layout-menu-main-content-active {
                      background: ${menuItemActiveBg};
                      :before {
                        background: ${colors.primary};
                      }
                      .hzero-icon,
                      .hzero-common-layout-menu-main-content-title {
                        color: ${menuItemActiveFontColor};
                      }
                    }
                    &.hzero-common-layout-menu-main-content-current {
                      position: relative;
                      background: ${menuItemHoverBg};
                      .hzero-icon,
                      .hzero-common-layout-menu-main-content-title {
                        color: ${menuItemHoverFontColor};
                      }
                      :before {
                        background: ${colors.primary};
                      }
                      :after {
                        border-color: ${colors.primary};
                      }
                    }
                  }
                  .hzero-common-layout-menu-cascader {
                    background: ${subMenuBg};
                    left: ${menuNormalWidth}px;
                    .hzero-common-layout-menu-cascader-second {
                      .hzero-common-layout-menu-cascader-second-item-wrap {
                        .hzero-common-layout-menu-cascader-second-item {
                          color: ${menuItemFontColor};
                          &.hzero-common-layout-menu-cascader-second-item-active,
                          &.hzero-common-layout-menu-cascader-second-item-current {
                            color: ${subMenuHoverColor};
                            background: ${subMenuHoverBg};
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        .hzero-common-layout-content-wrap {
          margin: 0 ${bodyContentGap}px;
          flex: 1;
          padding: 0;
          box-sizing: border-box;
          overflow: hidden;
          position: relative;
          height: 100%;
          .hzero-common-layout-content {
            height: 100%;
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            right: 0;
          }
        }
      }
    }
  `;
}
