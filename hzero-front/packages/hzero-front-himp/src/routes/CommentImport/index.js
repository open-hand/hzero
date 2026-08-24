/**
 * 通用导入模块
 * 由于 需要被几个页面用到, 所以需要将 model 换成 state
 * @since 2018-9-12
 * @version 0.0.1
 * @author  fushi.wang <fushi.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { toSafeInteger } from 'lodash';
import queryString from 'query-string';
import { withRouter } from 'dva/router';

import { getCurrentOrganizationId } from 'utils/utils';

import CommonImport from '../../components/CommonImport';

const AUTO_REFRESH_DEBOUNCE = 5000;

@withRouter
export default class CommonImportRoute extends React.Component {
  constructor(props) {
    super(props);
    const {
      location: { search = '' },
      match: { params },
    } = props;
    const {
      sync = false,
      auto = false,
      prefixPatch,
      args = 'null',
      autoRefreshInterval,
      backPath,
      tenantId = getCurrentOrganizationId(),
      action,
      actionParams,
      key,
      historyButton,
      refreshButton,
      dataImportButton,
    } = queryString.parse(search);
    this.state = {
      sync, // 是否是同步的接口
      auto, // 是否是 同步自动的接口
      backPath: backPath || undefined, // 返回地址, 如果返回地址为空, 设置为 undefined
      // 兼容 两个模式, 1: 使用者指定前缀, 2: 前缀由服务端确认
      prefixPatch, // 客户端路径前缀
      args, // 上传文件时传递的数据
      tenantId, // 租户id, 是可配置的
      autoRefreshInterval: toSafeInteger(autoRefreshInterval) || AUTO_REFRESH_DEBOUNCE,
      code: params.code,
      action,
      actionParams,
      key,
      historyButton,
      refreshButton,
      dataImportButton,
    };
  }

  render() {
    const {
      sync,
      auto,
      prefixPatch,
      args,
      autoRefreshInterval,
      backPath,
      tenantId,
      code,
      action,
      actionParams,
      key,
      historyButton,
      refreshButton,
      dataImportButton,
    } = this.state;
    return (
      <CommonImport
        sync={sync}
        auto={auto}
        prefixPatch={prefixPatch}
        args={args}
        autoRefreshInterval={autoRefreshInterval}
        backPath={backPath}
        tenantId={tenantId}
        code={code}
        action={action}
        actionParams={actionParams}
        pathKey={key}
        historyButton={historyButton}
        refreshButton={refreshButton}
        dataImportButton={dataImportButton}
      />
    );
  }
}
