import React from 'react';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import { Tag } from 'hzero-ui';

import intl from 'utils/intl';

/**
 * 阻止事件冒泡
 * 在这里是: 关闭事件 需要阻止冒泡 避免触发 打开事件
 * @param {React.SyntheticEvent} e
 */
function eventStopPropagation(e) {
  e.stopPropagation();
}

export default class HistoryItem extends React.Component {
  static propTypes = {
    onClick: PropTypes.func.isRequired,
    onCloseClick: PropTypes.func.isRequired,
    item: PropTypes.object.isRequired,
  };

  constructor(props) {
    super(props);
    this.state = {};
  }

  /**
   * 通过搜索历史 跳转页面
   */
  @Bind()
  handleHistoryItemClick() {
    const { onClick, item } = this.props;
    onClick(item);
  }

  /**
   * 清除一个历史记录
   */
  @Bind()
  handleHistoryClose() {
    const { item, onCloseClick } = this.props;
    onCloseClick(item);
  }

  render() {
    const { item } = this.props;
    return item.name ? (
      <Tag
        closable
        onClick={this.handleHistoryItemClick}
        onClose={eventStopPropagation}
        afterClose={this.handleHistoryClose}
      >
        {intl.get(item.name)}
      </Tag>
    ) : null;
  }
}
