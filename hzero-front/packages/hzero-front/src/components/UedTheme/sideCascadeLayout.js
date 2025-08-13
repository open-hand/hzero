import { css } from 'styled-components';
import { getRequiredData } from '@hzero-front-ui/cfg';

export default (props) => {
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
    // menuItemHoverFontColor,
    // menuItemHoverBg,
    menuItemActiveFontColor,
    menuItemActiveBg,
    subMenuBg,
    subMenuHoverBg,
    subMenuHoverColor,
    // leafMenuBg,
    leafMenuHoverBg,
    leafMenuHoverColor,
    containerBg,
    headerHeight,
    headerBgImage,
    headerTitleFontSize,
    headerTitleColor,
    headerBg,
    bodyContentGap = 8,
  } = getRequiredData(props, 'layout');
  const colors = getRequiredData(props, 'colors');
  return css`
    /* 侧边级联 */
    .hzero-side-layout-container.hzero-side-layout-container {
      background: ${containerBg};
      .hzero-side-layout-menu-side-casacader {
        left: ${menuNormalWidth}px !important;
        &.hzero-side-layout-menu-side-casacader-leaf {
          left: ${menuNormalWidth + 220}px !important;
        }
      }
      &.hzero-side-layout-container-collapsed {
        .hzero-side-layout-body .hzero-side-layout-nav {
          width: ${collapsedWidth}px;
        }

        .hzero-side-layout-header-left {
          width: ${collapsedWidth}px;

          .hzero-side-layout-header-sign {
            width: ${collapsedWidth}px;
          }
        }
        .hzero-side-layout-body .hzero-side-layout-menu-side-casacader,
        .hzero-side-layout-menu .hzero-side-layout-menu-side-casacader-mask-right {
          left: ${collapsedWidth}px !important;
        }
        .hzero-side-layout-body
          .hzero-side-layout-menu-side-casacader.hzero-side-layout-menu-side-casacader-leaf {
          left: ${collapsedWidth + 220}px !important;
        }
        .hzero-side-layout-nav-container {
          width: ${collapsedWidth}px;
        }
        .hzero-side-layout-side-search-icon {
          background-color: #efefef;
        }
      }
      .hzero-side-layout-header {
        height: ${headerHeight}px;
        padding: 0;
        background: url(${headerBgImage}) no-repeat center;
        background-color: ${headerBg};
        background-size: cover;
        .hzero-side-layout-header-container {
          &:not(.hzero-side-layout-container-collapsed) {
            .hzero-side-layout-header-left {
              max-width: ${menuNormalWidth}px;
            }
          }
          .hzero-side-layout-header-left {
            background-color: #fff;
            transition: width 0.2s ease;
            .hzero-side-layout-header-logo-sign,
            .hzero-side-layout-header-logo {
              margin-left: 0;
              a {
                justify-content: center;
                .hzero-side-layout-header-title {
                  font-size: ${headerTitleFontSize}px;
                  color: ${headerTitleColor};
                  font-weight: bold;
                }
              }
            }
          }
          .hzero-side-layout-header-right {
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
            }
          }
          .hzero-side-layout-header-right div .ant-select-selection-selected-value,
          .hzero-side-layout-header-right span,
          .hzero-side-layout-header-right span.ant-badge {
            color: #fff;
          }
        }
      }
      .hzero-side-layout-body {
        background-color: unset;
        margin-top: -${headerHeight - 44}px;
        .hzero-side-layout-nav {
          max-width: ${menuNormalWidth}px;
          .hzero-side-layout-nav-container {
            background: ${menuBg};
            .hzero-side-layout-nav-normal-search {
              .hzero-side-layout-side-search {
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

            .hzero-side-layout-nav-menu {
              .hzero-side-layout-menu {
                width: 100%;
                .hzero-side-layout-menu-side-casacader-mask-right {
                  left: ${menuNormalWidth}px;
                }
                .hzero-side-layout-menu-side-casacader-mask-left-top {
                  display: none;
                }
                .hzero-side-layout-menu-main {
                  width: 100%;
                  .hzero-side-layout-menu-main-content {
                    color: unset;
                    > div {
                      font-size: ${menuItemFontSize}px;
                      color: ${menuItemFontColor};
                      width: calc(100% - 24px);
                    }
                    :after {
                      width: ${menuItemRightArrowSize}px;
                      height: ${menuItemRightArrowSize}px;
                      border-color: ${menuItemRightArrowColor};
                    }
                  }
                  .hzero-side-layout-menu-side-casacader {
                    background-color: ${subMenuBg};
                    & + & {
                      .hzero-side-layout-menu-side-casacader-content-wrap {
                        color: ${menuItemFontColor};
                        &.hzero-side-layout-menu-side-casacader-content-active,
                        &.hzero-side-layout-menu-side-casacader-content-current {
                          position: relative;
                          background: ${leafMenuHoverBg};
                          color: ${leafMenuHoverColor};
                          :after {
                            border-color: ${colors.primary};
                          }
                        }
                      }
                    }
                    .hzero-side-layout-menu-side-casacader-content-wrap {
                      .hzero-side-layout-menu-side-casacader-content {
                        color: ${menuItemFontColor};
                        &.hzero-side-layout-menu-side-casacader-content-active,
                        &.hzero-side-layout-menu-side-casacader-content-current {
                          position: relative;
                          background: ${subMenuHoverBg};
                          color: ${subMenuHoverColor};
                          :after {
                            border-color: ${colors.primary};
                          }
                        }
                      }
                    }
                  }
                  &.hzero-side-layout-menu-main-active,
                  &.hzero-side-layout-menu-main-current {
                    .hzero-side-layout-menu-main-content {
                      position: relative;
                      background: ${menuItemActiveBg};
                      color: ${menuItemActiveFontColor};
                      :after {
                        border-color: ${colors.primary};
                      }
                    }
                    .hzero-side-layout-menu-main-content:before {
                      content: '';
                      position: absolute;
                      top: 0;
                      bottom: 0;
                      left: 0;
                      width: 4px;
                      background: ${colors.primary}!important;
                      border: none;
                    }
                  }
                }
              }
            }
          }
        }
        .hzero-side-layout-content {
          margin: 0 ${bodyContentGap}px;
          width: calc(100% - ${menuNormalWidth + 2 * bodyContentGap}px);
          .hzero-side-layout-content-container {
          }
        }
      }
    }
  `;
};
