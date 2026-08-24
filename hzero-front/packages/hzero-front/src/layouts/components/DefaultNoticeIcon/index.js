/**
 * @reactProps {!function} fetchCount - 获取消息数量
 * @reactProps {!function} gotoTab - 消息点击事件出发的页面跳转
 * @reactProps {!function} fetchNotices - 获取消息
 */
import React from 'react';
import { Badge, Icon, Tooltip, Popover, Spin, Tabs, Button } from 'hzero-ui';
import { connect } from 'dva';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import monent from 'moment';
import classNames from 'classnames';
import { routerRedux } from 'dva/router';

import intl from 'utils/intl';
import { getResponse } from 'utils/utils';
import { timeZone2UTC } from 'utils/renderer';
import { allRead, deleteAll, queryNotices, queryAnnounces } from '../../../services/api';
import emptyMessageIcon from '../../../assets/empty-message.png';

import List from './NoticeList';
import styles from './index.less';

const { TabPane } = Tabs;
const noCountOffset = [-2, 0];
const countLt10Offset = [18, 2];
const countGt10Offset = [18, 4];

class DefaultNoticeIcon extends React.PureComponent {
  static defaultProps = {
    emptyImage: emptyMessageIcon,
  };

  constructor(props) {
    super(props);
    const { title } = props;
    this.state = {
      visible: false,
      currentTab: 'NOTICE', // 当前tab页
    };
    if (title) {
      this.state.tabType = title;
    }
  }

  componentDidMount() {
    const { fetchCount } = this.props;
    fetchCount();
  }

  render() {
    const { className, count } = this.props;
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
    const bellIconClass = visible
      ? `${styles['icon-active']} ${styles['notification-icon']}`
      : styles['notification-icon'];
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
        onVisibleChange={this.handleNoticeVisibleChange}
        visible={visible}
      >
        {trigger}
      </Popover>
    );
  }

  /**
   * 点击查看更多跳转页面
   */
  @Bind()
  handleNoticeList(e) {
    e.preventDefault();
    const { gotoTab } = this.props;
    gotoTab({ pathname: `/hmsg/user-message/list` });
    this.setState({ visible: false });
  }

  /**
   * 点击查看更多跳转页面
   */
  @Bind()
  handleNoticeItem(e) {
    e.preventDefault();
    this.setState({ visible: false });
  }

  /**
   * 点击每一行跳转到详情界面
   */
  handleItemClick = (item) => {
    const { gotoTab } = this.props;
    switch (item.userMessageTypeCode) {
      case 'NOTICE':
        gotoTab({ pathname: `/hmsg/user-message/detail/notice/${item.id}` });
        break;
      case 'ANNOUNCE':
        gotoTab({ pathname: `/hmsg/user-message/detail/announce/${item.id}` });
        break;
      case 'MSG':
        gotoTab({ pathname: `/hmsg/user-message/detail/message/${item.id}` });
        break;
      default:
        // should throw error, unKnow message type,
        break;
    }
    this.setState({ visible: false });
  };

  handleAnnounceItemClick = (item) => {
    const { gotoTab } = this.props;
    gotoTab({ pathname: `/hmsg/user-message/detail/announce/${item.id}` });
  };

  handleNoticeVisibleChange = (visible) => {
    this.setState({ visible });
    if (visible) {
      this.fetchNotices();
      this.fetchAnnounces();
    }
  };

  onTabChange = (tabType) => {
    this.setState({ tabType });
    this.props.onTabChange(tabType);
  };

  @Bind()
  handleChangeTab(type) {
    this.setState({ currentTab: type });
    switch (type) {
      case 'NOTICE':
        this.fetchNotices();
        break;
      case 'ANNOUNCE':
        this.fetchAnnounces();
        break;
      default:
    }
  }

  /**
   * 清除全部消息
   */
  @Bind()
  async handleDeleteAll() {
    await deleteAll();
    await this.fetchNotices();
  }

  /**
   * 获取公告数据
   */
  @Bind()
  fetchAnnounces() {
    this.setState({
      fetchingAnnounces: true,
    });
    queryAnnounces({ previewCount: 3 }).then((res) => {
      const data = getResponse(res);
      this.setState({
        announces: data,
        fetchingAnnounces: false,
      });
    });
  }

  /**
   * 获取消息数据
   */
  @Bind()
  fetchNotices() {
    this.setState({
      fetchingNotices: true,
    });
    queryNotices({ previewMessageCount: 3, readFlag: 0 }).then((res) => {
      const data = getResponse(res);
      this.setState({
        notices: data,
        fetchingNotices: false,
      });
    });
  }

  /**
   * 已读全部消息
   */
  @Bind()
  async handleAllRead() {
    // const { allRead } = this.props;
    await allRead({ readAll: 1 });
    await this.fetchNotices();
  }

  getNotificationBox() {
    const { locale, count, timeZone, ...childProps } = this.props;
    if (isEmpty(childProps)) {
      return null;
    }
    const { emptyImage, title } = childProps;
    const {
      currentTab,
      announces,
      notices,
      fetchingNotices = false,
      fetchingAnnounces = false,
    } = this.state;
    // TODO: 需要之后审查下为什么这里需要自己转化一下数据结构
    const newNotices =
      notices &&
      notices.map((item) => {
        const utc = timeZone2UTC(timeZone) || 8;
        return {
          id: item.userMessageId,
          key: `${item.userMessageTypeCode}_${item.userMessageId}`,
          title: item.subject,
          datetime: monent(`${item.creationDate}`).utcOffset(utc, true).fromNow(),
          userMessageTypeMeaning: item.userMessageTypeMeaning,
          userMessageTypeCode: item.userMessageTypeCode,
          // avatar: messageIcon,
        };
      });

    const newAnnounces = Array.isArray(announces)
      ? announces.map((item) => {
          const utc = timeZone2UTC(timeZone) || 8;
          return {
            id: item.noticeId,
            key: `${item.receiverTypeCode}_${item.noticeId}`,
            title: item.title,
            datetime: monent(`${item.publishedDate}`).utcOffset(utc, true).fromNow(),
            userMessageTypeMeaning: item.receiverTypeMeaning,
            userMessageTypeCode: item.noticeTypeCode,
            // avatar: messageIcon,
          };
        })
      : [];

    const panes = (
      <List
        data={newNotices}
        onClick={this.handleItemClick}
        onClear={() => this.props.onClear(title)}
        onMore={this.handleNoticeList}
        locale={locale}
        // 以下为item的内容
        title={title}
        emptyImage={emptyImage}
        contentItemAction={this.handleNoticeItem}
        contentTitleAction={
          <a onClick={this.handleNoticeList}>
            {intl.get('hzero.common.basicLayout.gotoUserMessage').d('进入消息中心')}
          </a>
        }
      />
    );

    const announcePanes = (
      <List
        data={newAnnounces}
        onClick={this.handleAnnounceItemClick}
        locale={locale}
        // 以下为item的内容
        title={title}
        emptyImage={emptyImage}
        contentItemAction={this.handleNoticeItem}
        contentTitleAction={
          <a onClick={this.handleNoticeList}>
            {intl.get('hzero.common.basicLayout.gotoUserMessage').d('进入消息中心')}
          </a>
        }
      />
    );

    const operations = (
      <div className={styles['icon-btns']}>
        <Tooltip title={intl.get('hzero.common.basicLayout.option.allRead').d('全部已读')}>
          <Button shape="circle" className={styles['icon-button']} onClick={this.handleAllRead}>
            <Icon type="mail" style={{ fontSize: 16 }} />
          </Button>
        </Tooltip>
        <Tooltip title={intl.get('hzero.common.basicLayout.option.deleteAll').d('全部清除')}>
          <Button shape="circle" className={styles['icon-button']} onClick={this.handleDeleteAll}>
            <Icon type="delete" style={{ fontSize: 16 }} />
          </Button>
        </Tooltip>
      </div>
    );

    return (
      <Tabs
        defaultActiveKey="NOTICE"
        animated={false}
        onChange={this.handleChangeTab}
        tabBarExtraContent={currentTab === 'NOTICE' ? operations : ''}
        className={styles['layout-tabs']}
      >
        <TabPane
          tab={
            <Badge count={count}> {intl.get('hzero.common.view.title.tabNotice').d('消息')}</Badge>
          }
          key="NOTICE"
        >
          <Spin spinning={fetchingNotices} delay={0}>
            {fetchingNotices ? <div style={{ height: '258px' }} /> : panes}
          </Spin>
        </TabPane>
        <TabPane
          tab={<Badge>{intl.get('hzero.common.view.title.tabAnnounce').d('公告')}</Badge>}
          key="ANNOUNCE"
        >
          <Spin spinning={fetchingAnnounces} delay={0}>
            {fetchingAnnounces ? <div style={{ height: '258px' }} /> : announcePanes}
          </Spin>
        </TabPane>
      </Tabs>
    );
  }
}

export default connect(
  ({ global = {}, user = {} }) => ({
    count: global.count,
    timeZone: user.currentUser.timeZone,
  }),
  (dispatch) => ({
    // 跳转到页面
    gotoTab: (location, state) => dispatch(routerRedux.push(location, state)),
    // 获取消息数量
    fetchCount: () =>
      dispatch({
        type: 'global/fetchCount',
      }),
    deleteAll: () =>
      dispatch({
        type: 'global/deleteAll',
      }),
    allRead: (payload) =>
      dispatch({
        type: 'global/allRead',
        payload,
      }),
  })
)(DefaultNoticeIcon);
