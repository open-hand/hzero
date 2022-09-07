/**
 * 工作区 Header
 *
 * @date: 2018-6-30
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { withRouter } from 'dva/router';
import { Tooltip, Icon, Modal } from 'hzero-ui';
import { split, isString, isFunction } from 'lodash';
import intl from 'utils/intl';

@withRouter
export default class PageHeader extends Component {
  onBackBtnClick = () => {
    const { backPath, history, isChange, onBack } = this.props;
    if (isString(backPath)) {
      if (isChange) {
        Modal.confirm({
          title: intl
            .get('hzero.common.message.confirm.giveUpTip')
            .d('你有修改未保存，是否确认离开？'),
          onOk: () => {
            this.linkToChange(this.props.backPath);
            if (isFunction(onBack)) {
              onBack();
            }
          },
        });
      } else {
        this.linkToChange(this.props.backPath);
        if (isFunction(onBack)) {
          onBack();
        }
      }
    } else {
      history.goBack();
    }
  };

  linkToChange = url => {
    const { history } = this.props;
    // const newUrl = `${url}${url.indexOf('?') === -1 ? '?' : '&'}_back=1`;
    // history.push(newUrl);
    const [pathname, search] = split(url, '?');
    history.push({
      pathname,
      search,
      state: {
        _back: -1,
      },
    });
  };

  render() {
    const {
      title,
      backPath,
      backTooltip = intl.get('hzero.common.button.back').d('返回'),
      children,
    } = this.props;
    let backBtn = '';
    if (backPath) {
      backBtn = (
        <div key="page-head-back-btn" className="page-head-back-btn">
          <Tooltip title={backTooltip} placement="bottom" getTooltipContainer={that => that}>
            <Icon type="arrow-left" className="back-btn" onClick={this.onBackBtnClick} />
          </Tooltip>
        </div>
      );
    }
    return (
      <div className="page-head">
        {backBtn}
        {title && (
          <span key="page-head-title" className="page-head-title">
            {title}
          </span>
        )}
        <div key="page-head-operator" className="page-head-operator">
          {children}
        </div>
      </div>
    );
  }
}
