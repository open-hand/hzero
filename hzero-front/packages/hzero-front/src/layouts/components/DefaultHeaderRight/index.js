/**
 * 组合 布局 中 右侧 导航条
 * 语言切换
 * 站内消息
 * 常用功能(租户切换/角色切换/个人中心/退出登录)
 * @date 2019-03-07
 * @author WY yang.wang06@hand-china.com
 * @copyright ® HAND 2019
 */

import React from 'react';
import { getEnvConfig } from 'utils/iocUtils';

import DefaultLanguageSelect from '../DefaultLanguageSelect';
import DefaultNoticeIcon from '../DefaultNoticeIcon';
import DefaultCommonSelect from '../DefaultCommonSelect';
import DefaultTraceLog from '../DefaultTraceLog';
import ThemeButton from '../../DefaultLayout/components/NormalHeader/ThemeButton';
import MarketClientButton from '../../DefaultLayout/components/NormalHeader/MarketClientButton';

class DefaultHeaderRight extends React.Component {
  config = getEnvConfig();

  render() {
    const { extraHeaderRight = [], dispatch } = this.props;
    const {
      // WEBSOCKET_URL,
      TRACE_LOG_ENABLE,
      MULTIPLE_SKIN_ENABLE,
      MULTIPLE_LANGUAGE_ENABLE,
    } = this.config;
    // let hasWebsocketUrl = false;
    // if (
    //   WEBSOCKET_URL !== ['BUILD_', 'WEBSOCKET_', 'HOST'].join('') &&
    //   WEBSOCKET_URL !== 'undefined'
    // ) {
    //   hasWebsocketUrl = WEBSOCKET_URL;
    // } else {
    //   hasWebsocketUrl = false;
    // }

    let isTraceLog = false;
    try {
      isTraceLog = TRACE_LOG_ENABLE ? JSON.parse(TRACE_LOG_ENABLE) : false;
    } catch (e) {
      isTraceLog = false;
    }

    let isUed = false;
    try {
      isUed = MULTIPLE_SKIN_ENABLE ? JSON.parse(MULTIPLE_SKIN_ENABLE) : false;
    } catch (e) {
      isUed = false;
    }

    let hasMultiLanguage = true;
    try {
      hasMultiLanguage = MULTIPLE_LANGUAGE_ENABLE ? JSON.parse(MULTIPLE_LANGUAGE_ENABLE) : true;
    } catch (e) {
      hasMultiLanguage = true;
    }

    let _extraHeaderRight;
    if (extraHeaderRight) {
      if (Array.isArray(extraHeaderRight)) {
        _extraHeaderRight = extraHeaderRight;
      } else {
        _extraHeaderRight = [extraHeaderRight];
      }
    }

    return (
      <>
        {_extraHeaderRight.map((eleOrComponent) =>
          React.isValidElement(eleOrComponent)
            ? eleOrComponent
            : React.createElement(eleOrComponent)
        )}
        {hasMultiLanguage && <DefaultLanguageSelect key="language-switch" />}
        {isTraceLog && <DefaultTraceLog dispatch={dispatch} />}
        <MarketClientButton />
        {isUed && <ThemeButton />}
        <DefaultNoticeIcon key="notice-message" popupAlign={{ offset: [25, -8] }} />
        <DefaultCommonSelect key="common-switch" />
      </>
    );
  }
}

export default DefaultHeaderRight;
