/**
 * userMessage 站内消息汇总
 * @date: 2018-8-4
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Form, Spin, Tabs } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import CacheComponent from 'components/CacheComponent';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';
import MessageTabPane from './MessageTabPane';

/**
 * 站内消息
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} userMessage - 数据源
 * @reactProps {loading} loading - 数据加载是否完成
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ userMessage, loading }) => ({
  userMessage,
  loading: loading.effects['userMessage/queryMessage'],
  deleting: loading.effects['userMessage/deleteMessage'],
  changeReading: loading.effects['userMessage/changeRead'],
  organizationId: getCurrentOrganizationId(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hmsg.userMessage', 'hmsg.common'] })
@CacheComponent({ cacheKey: '/hmsg/userMessage' })
export default class UserMessage extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {};
    this.filterFormRef = React.createRef();
    this.messageTabPaneRef = React.createRef();
    this.noticeTabPaneRef = React.createRef();
    this.announceTabPaneRef = React.createRef();
  }

  /**
   * 按条件查询
   */
  @Bind()
  handleSearchMessage(payload, type) {
    const { dispatch, organizationId } = this.props;
    return dispatch({
      type: 'userMessage/queryMessage',
      payload: {
        ...payload,
        organizationId,
        type,
      },
    });
  }

  /**
   * 跳转到详情界面
   * @param {object} record
   * @param {'message' | 'notice' | 'announce'} type
   */
  @Bind()
  handleDetails(record, type) {
    const { dispatch } = this.props;
    const gotoUrl =
      type === 'announce'
        ? `/hmsg/user-message/detail/${type}/${record.noticeId}`
        : `/hmsg/user-message/detail/${type}/${record.userMessageId}`;
    dispatch(routerRedux.push(gotoUrl));
  }

  /**
   * 标记已读
   * @param {*} number
   */
  @Bind()
  handleRead(number) {
    const { dispatch, organizationId } = this.props;
    let updatePromise;
    if (number) {
      updatePromise = dispatch({
        type: 'userMessage/changeRead',
        payload: {
          readAll: 1,
          organizationId,
        },
      });
    } else {
      const selectedRows = this.getCurrentSelectedRows();
      const userMessageId = selectedRows.map((item) => item.userMessageId);
      const userMessageIdList =
        // eslint-disable-next-line no-nested-ternary
        userMessageId.length > 0
          ? userMessageId.join(',')
          : userMessageId.length !== 0
          ? userMessageId[0]
          : [];
      updatePromise = dispatch({
        type: 'userMessage/changeRead',
        payload: {
          userMessageIdList,
          organizationId,
        },
      });
    }
    updatePromise.then((res) => {
      if (res) {
        notification.success();
        const {
          userMessage: { currentType = 'message' },
        } = this.props;
        const {
          userMessage: {
            [currentType]: { pagination = {} },
          },
        } = this.props;
        const readFlag = this.getReadFlag();
        this.handleSearchMessage({ page: pagination, readFlag }, currentType);
      }
    });
  }

  /**
   * 删除消息
   */
  @Bind()
  handleDelete() {
    const { dispatch, organizationId } = this.props;
    const selectedRows = this.getCurrentSelectedRows();
    const userMessageId = selectedRows.map((item) => item.userMessageId);
    const userMessageIdList =
      // eslint-disable-next-line no-nested-ternary
      userMessageId.length > 0
        ? userMessageId.join(',')
        : userMessageId.length !== 0
        ? userMessageId[0]
        : [];
    dispatch({
      type: 'userMessage/deleteMessage',
      payload: { userMessageIdList, organizationId },
    }).then((res) => {
      if (res) {
        notification.success();
        const {
          userMessage: { currentType = 'message' },
        } = this.props;
        const {
          userMessage: {
            [currentType]: { pagination = {} },
          },
        } = this.props;
        const readFlag = this.getReadFlag();
        this.handleSearchMessage({ page: pagination, readFlag }, currentType);
      }
    });
  }

  // 计算信息
  @Bind()
  getCurrentSelectedRows() {
    const {
      userMessage: { currentType = 'message' },
    } = this.props;
    let current;
    switch (currentType) {
      case 'message':
        ({ current } = this.messageTabPaneRef);
        break;
      case 'notice':
        ({ current } = this.noticeTabPaneRef);
        break;
      case 'announce':
        ({ current } = this.announceTabPaneRef);
        break;
      default:
        break;
    }
    if (current) {
      const { selectedRows = [] } = current.state;
      return selectedRows;
    } else {
      return [];
    }
  }

  // 获取 当前 tab 的组件中的 readFlag 状态
  @Bind()
  getReadFlag() {
    const {
      userMessage: { currentType = 'message' },
    } = this.props;
    let current;
    switch (currentType) {
      case 'message':
        ({ current } = this.messageTabPaneRef);
        break;
      case 'notice':
        ({ current } = this.noticeTabPaneRef);
        break;
      case 'announce':
        ({ current } = this.announceTabPaneRef);
        break;
      default:
        break;
    }
    if (current) {
      const { readTypeValue } = current.state;
      switch (readTypeValue) {
        case '0':
          return 0;
        case '1':
          return 1;
        case 'all':
        default:
          return undefined;
      }
    } else {
      return undefined;
    }
  }

  // 强制刷新index
  @Bind()
  callForceUpdate() {
    this.forceUpdate();
  }

  // Tabs
  @Bind()
  tabChange(tabKey) {
    const { dispatch } = this.props;
    dispatch({
      type: 'userMessage/updateState',
      payload: {
        currentType: tabKey,
      },
    });
  }

  render() {
    const {
      userMessage: { message = {}, notice = {}, announce = {}, currentType = 'message' },
      loading = false,
      deleting = false,
      changeReading = false,
      match: { path },
    } = this.props;
    const messageProps = {
      ...message,
      fetchMessage: this.handleSearchMessage,
      onGotoDetail: this.handleDetails,
      indexForceUpdate: this.callForceUpdate,
    };
    const noticeProps = {
      ...notice,
      fetchMessage: this.handleSearchMessage,
      onGotoDetail: this.handleDetails,
      indexForceUpdate: this.callForceUpdate,
    };
    const announceProps = {
      ...announce,
      fetchMessage: this.handleSearchMessage,
      onGotoDetail: this.handleDetails,
      // 公告 不能操作
      // indexForceUpdate: this.callForceUpdate,
    };

    return (
      <>
        <Header title={intl.get('hmsg.userMessage.view.message.title').d('站内消息')}>
          {currentType !== 'announce' && (
            <>
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.delete`,
                    type: 'text',
                    meaning: '站内消息-删除',
                  },
                ]}
                type="primary"
                icon="delete"
                loading={deleting}
                onClick={this.handleDelete}
                disabled={changeReading || loading || isEmpty(this.getCurrentSelectedRows())}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </ButtonPermission>
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.signRead`,
                    type: 'button',
                    meaning: '站内消息-标记已读',
                  },
                ]}
                icon="mail"
                onClick={() => {
                  this.handleRead();
                }}
                loading={changeReading}
                disabled={
                  deleting ||
                  loading ||
                  isEmpty(this.getCurrentSelectedRows().filter((record) => record.readFlag !== 1))
                }
              >
                {intl.get('hmsg.userMessage.view.option.signRead').d('标记已读')}
              </ButtonPermission>
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.addRead`,
                    type: 'button',
                    meaning: '站内消息-全部已读',
                  },
                ]}
                icon="mail"
                disabled={deleting || loading}
                loading={changeReading}
                onClick={() => {
                  this.handleRead(1);
                }}
              >
                {intl.get('hmsg.userMessage.view.option.allRead').d('全部已读')}
              </ButtonPermission>
            </>
          )}
        </Header>
        <Content>
          <Spin spinning={loading}>
            <Tabs activeKey={currentType} onChange={this.tabChange} animated={false}>
              <Tabs.TabPane
                tab={intl.get('hmsg.userMessage.view.title.message').d('消息')}
                key="message"
              >
                <MessageTabPane {...messageProps} type="message" ref={this.messageTabPaneRef} />
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={intl.get('hmsg.userMessage.view.title.notice').d('通知')}
                key="notice"
              >
                <MessageTabPane {...noticeProps} type="notice" ref={this.noticeTabPaneRef} />
              </Tabs.TabPane>
              <Tabs.TabPane
                tab={intl.get('hmsg.userMessage.view.title.announce').d('公告')}
                key="announce"
              >
                <MessageTabPane {...announceProps} type="announce" ref={this.announceTabPaneRef} />
              </Tabs.TabPane>
            </Tabs>
          </Spin>
        </Content>
      </>
    );
  }
}
