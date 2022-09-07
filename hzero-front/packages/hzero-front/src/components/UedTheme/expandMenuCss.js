import { css } from 'styled-components';
import { getRequiredData } from '@hzero-front-ui/cfg';

export default props => {
  const colors = getRequiredData(props, 'colors');
  const {
    menuItemRightArrowColor,
    menuItemRightArrowSize,
    subMenuBg,
    subMenuHoverBg,
    subMenuHoverColor,
    leafMenuFontColor,
    subMenuFontColor,
    leafMenuHoverBg,
    leafMenuHoverColor,
    dividerLineColor = '#f1f1f1',
    subMenuTitleColor = '#333',
  } = getRequiredData(props, 'layout');
  return css`
    .hzero-common-layout-container {
      .hzero-common-layout-aside .hzero-common-layout-menu-wrap .hzero-common-layout-menu-cascader {
        position: fixed;
        top: 0;
        bottom: 0;
        padding-top: 32px;
        .hzero-common-layout-menu-cascader-second-title {
          font-family: PingFangSC-Medium;
          font-size: 16px;
          /* font-weight: normal; */
          color: ${subMenuTitleColor};
          letter-spacing: 0;
          line-height: 40px;
          height: 40px;
          border-bottom: 1px solid ${dividerLineColor};
        }
        .hzero-common-layout-menu-cascader-second-item-wrap {
          margin-top: 8px;
          .hzero-common-layout-menu-cascader-second-item {
            height: 38px;
            margin-bottom: 8px;
            line-height: 38px;
            font-family: MicrosoftYaHei;
            font-size: 14px;
            color: ${subMenuFontColor};
            letter-spacing: 0;
            &-active,
            &-current {
              background: ${subMenuHoverBg};
              border-radius: 2px;
              border-radius: 2px;
              color: ${subMenuHoverColor} !important;
            }
          }
        }
      }
    }
    .hzero-normal-container {
      .hzero-sub-menu-wrap {
        background: ${subMenuBg};
        .hzero-sub-menu,
        .hzero-leaf-menu {
          opacity: 1;
          .hzero-leaf-menu-item-wrap-line {
            background-color: ${dividerLineColor};
          }
        }
        .hzero-sub-menu-item {
          font-family: MicrosoftYaHei;
          font-size: 14px;
          color: #5a6677;
          line-height: 22px;
          &-content {
            :after {
              border-top: 1px solid ${menuItemRightArrowColor};
              border-right: 1px solid ${menuItemRightArrowColor};
              border-left: none;
              border-bottom: none;
              transform: rotate(45deg);
              width: ${menuItemRightArrowSize}px;
              height: ${menuItemRightArrowSize}px;
              background: none;
            }
            .hzero-sub-menu-item-title {
              color: ${subMenuFontColor};
            }
          }
          :hover,
          &-active,
          &-hover {
            .hzero-sub-menu-item-content {
              background: ${subMenuHoverBg};
              :after {
                border-color: ${colors.primary};
              }
              .hzero-sub-menu-item-title {
                color: ${subMenuHoverColor};
              }
            }
          }
        }
        .hzero-leaf-menu-item {
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: ${leafMenuFontColor};
          line-height: 42px;
          height: 42px;
          padding: 0 20px;
          :hover,
          &-active {
            background: ${leafMenuHoverBg};
            color: ${leafMenuHoverColor};
          }
        }
      }
    }
  `;
};
