/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2020/1/13
 * @copyright HAND ® 2019
 */

import * as React from 'react';
import { RouteComponentProps } from 'dva/router';

import { SharedComponent } from '../../customize/himp';

interface CommonImportProps
  extends RouteComponentProps<
    { code: string },
    {},
    // there is inject into Location.State, but is desc for Location.Search
    {
      sync?: boolean; // 是否同步
      auto?: boolean; // 是否自动(同步模式下生效)
      prefixPatch: string;
      args?: string; // JSON stringify
      autoRefreshInterval?: number; // Integer
      backPath: string; // backIcon
      tenantId?: number;
      code: string; // 通用导入编码
      key?: string; // 通用导出使用组件的key
      action?: string; // 国际化编码(配合key使用)
      actionParams?: string; // 国际化编码参数
    }
  > {}

const CommonImportRoute: React.FC<CommonImportProps> = () => {
  return <SharedComponent componentCode="CommonImportRoute" componentProps={{}} />;
};

export default CommonImportRoute;
