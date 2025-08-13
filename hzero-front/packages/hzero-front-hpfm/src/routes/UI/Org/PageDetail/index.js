/**
 * 个性化配置 详情页
 * @date: 2018-12-3
 * @author: WangYang yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { connect } from 'dva';
// import {find, isEqual, isEmpty, omit} from 'lodash';
import { Bind } from 'lodash-decorators';

import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';

import PageDesigner from '../../designer';

@connect(({ uiPageOrg, loading }) => ({
  uiPage: uiPageOrg,
  fetching: loading.effects['uiPageOrg/fetchPageDetail'],
  updating: loading.effects['uiPageOrg/updatePageDetail'],
  organizationId: getCurrentOrganizationId(),
}))
export default class PageDetail extends React.Component {
  render() {
    return (
      <PageDesigner
        {...this.props}
        getPageDetail={this.getPageDetail}
        onSave={this.handleDesignerSave}
        bankPath="/hpfm/ui/page-org/list"
      />
    );
  }

  @Bind()
  handleDesignerSave(saveConfig) {
    const { dispatch, organizationId } = this.props;
    return dispatch({
      type: 'uiPageOrg/detailUpdate',
      payload: {
        organizationId,
        config: saveConfig,
      },
    }).then(res => {
      if (res) {
        // 保存成功
        notification.success();
      }
      return res;
    });
  }

  /**
   *
   */
  @Bind()
  getPageDetail() {
    const { dispatch, organizationId } = this.props;
    const pageCode = this.getPageCode();
    return dispatch({
      type: 'uiPageOrg/fetchDetail',
      payload: {
        organizationId,
        pageCode,
      },
    });
  }

  @Bind()
  getPageCode() {
    const {
      match: {
        params: { pageCode },
      },
    } = this.props;
    return pageCode;
  }
}
