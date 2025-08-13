import React, { PureComponent } from 'react';
import { Popover, Icon, Badge, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import monent from 'moment';
import classNames from 'classnames';
import { routerRedux } from 'dva/router';
// import SockJs from 'sockjs-client';
// import Stomp from 'webstomp-client';

// import messageIcon from '../../assets/notices.png';
import intl from 'utils/intl';
import emptyMessageIcon from '../../assets/empty-message.png';
// import bellIcon from '../../assets/userMessageIcon.pic';

import List from './NoticeList';
import styles from './index.less';

const noCountOffset = [-2, 0];
const countLt10Offset = [-2, 4];
const countGt10Offset = [-2, 6];

@connect(({ global = {}, loading }) => ({
  count: global.count,
  notices: global.notices,
  fetchingNotices: loading.effects['global/fetchNotices'],
}))
export default class NoticeIcon extends PureComponent {
  static defaultProps = {
    onTabChange: () => {},
    onClear: () => {},
    locale: {
      emptyText: intl.get('hzero.common.components.noticeIcon.null').d('暂无数据'),
      clear: intl.get('hzero.common.button.clear').d('清空'),
    },
    emptyImage: emptyMessageIcon,
  };

  state = {
    visible: false,
  };

  constructor(props) {
    super(props);
    const { title } = props;
    if (title) {
      this.state.tabType = title;
    }
  }

  /**
   * 点击查看更多跳转页面
   */
  @Bind()
  handleNoticeList(e) {
    e.preventDefault();
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hmsg/user-message/list`,
      })
    );
    this.setState({ visible: false });
  }

  /**
   * 点击每一行跳转到详情界面
   */
  handleItemClick = item => {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hmsg/user-message/detail/${item.id}`,
      })
    );
    this.setState({ visible: false });
  };

  handleNoticeVisibleChange = visible => {
    this.setState({ visible });
    if (visible) {
      this.props.dispatch({
        type: 'global/fetchNotices',
      });
    }
  };

  onTabChange = tabType => {
    this.setState({ tabType });
    this.props.onTabChange(tabType);
  };

  getNotificationBox() {
    const { fetchingNotices: loading, locale, ...childProps } = this.props;
    if (isEmpty(childProps)) {
      return null;
    }
    const { notices, contentTitle, emptyText, emptyImage, title } = childProps;

    const newNotices =
      notices &&
      notices.map(item => ({
        id: item.messageId,
        title: item.subject,
        datetime: monent(item.creationDate).fromNow(),
        // avatar: messageIcon,
      }));

    const panes = (
      <List
        data={newNotices}
        onClick={this.handleItemClick}
        onClear={() => this.props.onClear(title)}
        onMore={this.handleNoticeList}
        locale={locale}
        // 以下为item的内容
        title={title}
        emptyText={emptyText}
        emptyImage={emptyImage}
        contentTitle={contentTitle}
        contentTitleAction={
          <a onClick={this.handleNoticeList}>
            {intl.get('hzero.common.basicLayout.gotoUserMessage').d('进入消息中心')}
          </a>
        }
      />
    );
    return (
      <Spin spinning={loading} delay={0}>
        {panes}
      </Spin>
    );
  }

  render() {
    const { className, count, popupAlign } = this.props;
    const noticeButtonClass = classNames({
      [className]: true,
      [styles.noticeButton]: true,
      [styles.hasCount]: count > 0,
    });
    let offset = noCountOffset;
    if (count > 9) {
      offset = countGt10Offset;
    } else if (count > 0) {
      offset = countLt10Offset;
    }
    const notificationBox = this.getNotificationBox();
    const { visible } = this.state;
    const bellIconClass = visible ? `${styles['icon-active']} ${styles.icon}` : styles.icon;
    const trigger = (
      <span className={noticeButtonClass}>
        <Badge count={count} className={styles.badge} offset={offset}>
          <Icon className={bellIconClass} />
        </Badge>
      </span>
    );
    if (!notificationBox) {
      return trigger;
    }
    return (
      <Popover
        placement="bottomRight"
        content={notificationBox}
        popupClassName={styles.popover}
        trigger="click"
        arrowPointAtCenter
        popupAlign={popupAlign}
        onVisibleChange={this.handleNoticeVisibleChange}
        visible={visible}
      >
        {trigger}
      </Popover>
    );
  }
}
