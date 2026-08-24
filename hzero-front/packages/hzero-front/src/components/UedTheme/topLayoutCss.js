import { css } from 'styled-components';
import { getRequiredData } from '@hzero-front-ui/cfg';

function getLayoutCss(props) {
  const colors = getRequiredData(props, 'colors');
  const {
    menuBg,
    menuItemFontColor,
    menuItemActiveFontColor,
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
    menuItemRightArrowColor,
    topMenuFontSize = 14,
    topMenuItemRadius = 0,
    topMenuFontColor = '#fff',
    topMenuOpacity = '0.6',
    topMenuActiveFontColor = '#fff',
    topMenuActiveOpacity = '1',
    topMenuBg = 'none',
    topMenuActiveBg = 'none',
    bodyContentGap = 8,
  } = getRequiredData(props, 'layout');
  return css`
    .hzero-layout.hzero-layout {
      .hzero-content .hzero-page > .ant-tabs.ant-tabs-top > .ant-tabs-content {
        height: calc(100% - 48px);
      }
      background: ${containerBg};
      .hzero-header {
        background-color: ${headerBg};
        background-image: url(${headerBgImage});
        height: ${headerHeight}px;
        .hzero-header-left .hzero-header-logo-sign,
        .hzero-header-left .hzero-header-logo {
          background: ${menuBg};
          a {
            justify-content: center;
          }
          .hzero-header-title {
            color: ${headerTitleColor};
            font-size: ${headerTitleFontSize}px;
          }
        }
        .hzero-header-content {
          background: ${headerBg};
          .hzero-menu-trigger {
            color: ${collapseTriggerColor};
          }

          .hzero-main-menu {
            .ant-tabs-extra-content {
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
              span.ant-badge {
                color: #fff;
              }
            }
            .ant-tabs-nav-container {
              .ant-tabs-nav-wrap {
                margin: 4px 0;
                .ant-tabs-tab {
                  height: 100%;
                  padding: 0 12px;
                  background: ${topMenuBg};
                  color: ${topMenuFontColor};
                  border-radius: ${topMenuItemRadius}px;
                  opacity: ${topMenuOpacity};
                  font-family: PingFangSC-Regular;
                  font-size: ${topMenuFontSize}px;
                  &.ant-tabs-tab-active {
                    color: ${topMenuActiveFontColor};
                    opacity: ${topMenuActiveOpacity};
                    background: ${topMenuActiveBg};
                  }
                }
              }
            }
          }
        }
      }
      .hzero-content {
        margin-top: -${headerHeight - 44}px;
        .hzero-page {
          overflow: hidden;
        }
        .hzero-menu {
          background: ${subMenuBg};
        }
        .hzero-menu .ant-menu {
          background: ${subMenuBg};
          &.ant-menu-dark {
            .ant-menu-item {
              position: relative;
              color: ${menuItemFontColor};
            }
            .ant-menu-item-selected {
              background: ${subMenuHoverBg};
              :before {
                position: absolute;
                content: '';
                left: 0;
                top: 0;
                bottom: 0;
                width: 4px;
                background: ${colors.primary};
              }
            }
          }
          .ant-menu-submenu {
            &-title {
              color: ${menuItemFontColor};
              margin: 0;
              position: relative;
              height: 44px;
              line-height: 28px;
            }
            &-arrow {
              :before,
              :after {
                background: ${menuItemRightArrowColor};
                opacity: 1;
              }
            }
            &-open .ant-menu-submenu-arrow {
              :before,
              :after {
                background: ${colors.primary}!important;
              }
            }
            &-active {
              color: ${subMenuHoverColor};
              background: ${subMenuHoverBg};
              position: relative;
              .ant-menu-submenu-title:before {
                content: '';
                display: block;
                position: absolute;
                top: 0;
                bottom: 0;
                left: 0;
                width: 4px;
                border: none;
                background: ${colors.primary};
              }
            }
            .ant-menu {
              background: #fefefe;
              box-shadow: none;
              .ant-menu-item {
                color: ${menuItemFontColor};
                margin: 0;
                &-active,
                &-selected {
                  background: ${subMenuHoverBg};
                  color: ${subMenuHoverColor};
                }
                &-selected {
                  color: ${menuItemActiveFontColor};
                }
              }
            }
          }
        }
      }
    }
    .hzero-layout {
      && .hzero-content .hzero-page > .ant-tabs.ant-tabs-card > .ant-tabs-bar {
        margin-bottom: 12px;
      }
    }
    .hzero-page.hzero-page {
      height: 100%;
      padding: 0 ${bodyContentGap}px;
      > .ant-tabs {
        height: 100%;
      }
    }
  `;
}

export default css`
  ${getLayoutCss}
`;
