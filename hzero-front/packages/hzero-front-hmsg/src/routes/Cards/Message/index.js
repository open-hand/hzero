/**
 * Message -系统消息、平台公告
 * @date: 2019-08-28
 * @author LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import intl from 'utils/intl';
import { Link, routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { Tabs, Icon, Spin } from 'hzero-ui';
import formatterCollections from 'utils/intl/formatterCollections';
import System from './System';
import styles from './index.less';
import Announcement from './Announcement';
import Notification from './Notification';

@connect(({ messageCard, loading }) => ({
  messageCard,
  sysLoading: loading.effects['messageCard/hzeroQueryUserMessage'],
  noticeLoading: loading.effects['messageCard/hzeroQueryAnnouncement'],
}))
@formatterCollections({ code: ['hmsg.cards'] })
export default class Message extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      tabBar: 'message',
    };
  }

  /**
   * 查询系统消息
   */
  @Bind()
  handleUserMessage(type) {
    const { dispatch } = this.props;
    dispatch({
      type: 'messageCard/hzeroQueryUserMessage',
      payload: {
        readFlag: 0,
        userMessageTypeCode: type,
        size: 10,
      },
    });
  }

  /**
   * 查询平台公告
   */
  @Bind()
  handleAnnouncementList() {
    const { dispatch } = this.props;
    dispatch({
      type: 'messageCard/hzeroQueryAnnouncement',
      payload: {
        receiverTypeCode: 'ANNOUNCE',
        size: 10,
        page: 0,
        statusCode: 'PUBLISHED',
        sort: 'publishedDate,DESC',
        userNotice: true,
      },
    });
  }

  /**
   *标记已读
   * @param {number} number
   * @param {string} type
   * @memberof Message
   */
  @Bind()
  handleRead(number) {
    const { dispatch } = this.props;
    dispatch({
      type: 'messageCard/hzeroChangeRead',
      payload: {
        userMessageIdList: number,
        readAll: 0,
      },
    });
  }

  /**
   * 标签栏附加内容的显示
   */
  @Bind()
  handleCallback(key) {
    this.setState({
      tabBar: key,
    });
  }

  /**
   * 跳转到详情界面
   * @param {number} record
   * @param {'message' | 'announce'} type
   */
  @Bind()
  handleDetails(record, type) {
    const { dispatch } = this.props;
    const gotoUrl = `/hmsg/user-message/detail/${type}/${record}`;
    dispatch(routerRedux.push(gotoUrl));
  }

  render() {
    const {
      sysLoading = false,
      noticeLoading = false,
      messageCard: { systemMessageList = [], announcementList = [], notificationList = [] } = {},
    } = this.props;
    const { tabBar } = this.state;
    const systemProps = {
      systemMessageList,
      handleUserMessage: this.handleUserMessage,
      handleRead: this.handleRead,
      handleDetails: this.handleDetails,
    };
    const announcementProps = {
      announcementList,
      handleAnnouncementList: this.handleAnnouncementList,
      handleDetails: this.handleDetails,
    };
    const notificationProps = {
      notificationList,
      handleUserMessage: this.handleUserMessage,
      handleRead: this.handleRead,
      handleDetails: this.handleDetails,
    };
    const operations = (
      <div className={styles['tab-right']}>
        {tabBar === 'message' && (
          <div className={styles['tab-right-content']}>
            <Link to="/hmsg/user-message/list">
              {intl.get('hmsg.cards.message.messageCenter').d('消息中心')}
              <Icon type="double-right" className={styles['tab-icon-padding']} />
            </Link>
            <a onClick={() => this.handleUserMessage('MSG')} className={styles['tab-a']}>
              {intl.get('hzero.common.button.reload').d('重新加载')}
              <Icon type="reload" className={styles['tab-icon-padding']} />
            </a>
          </div>
        )}
        {tabBar === 'notification' && (
          <div className={styles['tab-right-content']}>
            <Link to="/hmsg/user-message/list">
              {intl.get('hmsg.cards.message.messageCenter').d('消息中心')}
              <Icon type="double-right" className={styles['tab-icon-padding']} />
            </Link>
            <a onClick={() => this.handleUserMessage('NOTICE')} className={styles['tab-a']}>
              {intl.get('hzero.common.button.reload').d('重新加载')}
              <Icon type="reload" className={styles['tab-icon-padding']} />
            </a>
          </div>
        )}
        {tabBar === 'announcement' && (
          <div className={styles['tab-right-content']}>
            <Link to="/hmsg/user-message/list">
              {intl.get('hmsg.cards.message.messageCenter').d('消息中心')}
              <Icon type="double-right" className={styles['tab-icon-padding']} />
            </Link>
            <a onClick={() => this.handleAnnouncementList()} className={styles['tab-a']}>
              {intl.get('hzero.common.button.reload').d('重新加载')}
              <Icon type="reload" className={styles['tab-icon-padding']} />
            </a>
          </div>
        )}
      </div>
    );
    return (
      <Tabs
        tabBarExtraContent={operations}
        size="large"
        tabBarGutter={0}
        onChange={this.handleCallback}
        className={styles.height}
        animated={false}
        defaultActiveKey="message"
      >
        <Tabs.TabPane
          tab={intl.get('hmsg.cards.message.stationMessages').d('站内消息')}
          key="message"
          className={styles.message}
        >
          <div className={styles['message-overflow']}>
            <Spin spinning={sysLoading}>
              <System {...systemProps} />
            </Spin>
          </div>
        </Tabs.TabPane>
        <Tabs.TabPane
          tab={intl.get('hmsg.cards.message.systemNotification').d('系统通知')}
          key="notification"
          className={styles.notification}
        >
          <div className={styles['notification-overflow']}>
            <Spin spinning={sysLoading}>
              <Notification {...notificationProps} />
            </Spin>
          </div>
        </Tabs.TabPane>
        <Tabs.TabPane
          tab={intl.get('hmsg.cards.message.platformAnnounce').d('平台公告')}
          key="announcement"
          className={styles.notice}
        >
          <div className={styles['notice-overflow']}>
            <Spin spinning={noticeLoading}>
              <Announcement {...announcementProps} />
            </Spin>
          </div>
        </Tabs.TabPane>
      </Tabs>
    );
  }
}
