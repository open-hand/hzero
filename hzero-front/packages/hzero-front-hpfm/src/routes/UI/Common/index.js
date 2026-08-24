/**
 * index.js
 * @todo WY yang.wang06@hand-china.com
 * 用于在页面上使用
 * @date 2018-12-10
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { withRouter } from 'dva/router';

import DynamicPage from 'components/DynamicComponent/DynamicPage';

@withRouter
export default class PageCommon extends React.Component {
  render() {
    const {
      match: {
        params: { pageCode },
      },
    } = this.props;
    return <DynamicPage pageCode={pageCode} />;
  }
}
