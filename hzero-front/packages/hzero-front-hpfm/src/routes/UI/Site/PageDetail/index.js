import React from 'react';
import {connect} from 'dva';
import {Bind} from 'lodash-decorators';

import notification from 'utils/notification';

import Designer from '../../designer';

@connect(({ loading, uiPage }) => {
  return {
    fetching: loading.effects['uiPage/fetchDetail'],
    saving: loading.effects['uiPage/detailUpdate'],
    uiPage,
  };
})
export default class PageDetail extends React.Component {
  render() {
    return (
      <Designer
        {...this.props}
        onSave={this.handleDesignerSave}
        getPageDetail={this.getPageDetail}
        bankPath="/hpfm/ui/page/list"
      />
    );
  }

  /**
   * 获取当前编辑的页面编码
   */
  @Bind()
  getPageCode() {
    const {
      match: {
        params: { pageCode },
      },
    } = this.props;
    return pageCode;
  }

  @Bind()
  getPageDetail() {
    const { dispatch } = this.props;
    const pageCode = this.getPageCode();
    return dispatch({
      type: 'uiPage/fetchDetail',
      payload: pageCode,
    });
  }

  @Bind()
  handleDesignerSave(saveConfig) {
    const { dispatch } = this.props;
    // console.debug(JSON.stringify(saveConfig, 0, 2));
    // 需要保存
    return dispatch({
      type: 'uiPage/detailUpdate',
      payload: saveConfig,
    }).then(res => {
      if (res) {
        // 保存成功
        notification.success();
      }
      return res;
    });
  }
}
