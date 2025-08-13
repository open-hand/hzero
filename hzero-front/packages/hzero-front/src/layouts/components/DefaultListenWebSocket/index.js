/**
 * 监听 websocket
 */

import React from 'react';
import { isEmpty } from 'lodash';
import { connect } from 'dva';
import Icons from 'components/Icons';
import { Bind } from 'lodash-decorators';
import { notification as Notification } from 'hzero-ui';
import uuid from 'uuid/v4';

import webSocketManager from 'utils/webSoket';
import notification from 'utils/notification';
import { getCurrentOrganizationId } from 'utils/utils';
import { openTab } from 'utils/menuTab';
import intl from '../../../utils/intl';

class DefaultListenWebSocket extends React.Component {
  componentDidMount() {
    webSocketManager.initWebSocket();
    webSocketManager.addListener('hzero-web', (messageData) => {
      const { saveNotices, count } = this.props;
      const { message } = messageData;
      const messageJson = isEmpty(message) ? undefined : JSON.parse(message);
      if (!isEmpty(messageJson)) {
        const { tenantId, number } = messageJson;
        let newCount = count;
        if (tenantId === 0) {
          newCount = Number(count) + Number(number);
        } else if (tenantId === getCurrentOrganizationId()) {
          newCount = Number(count) + Number(number);
        }
        saveNotices({ count: newCount > 0 ? newCount : 0 });
        if (Number(number) > 0) {
          const key = uuid();
          notification.info({
            key,
            icon: <></>,
            message: (
              <>
                <Icons
                  type="bell"
                  size={30}
                  color="red"
                  style={{ position: 'relative', top: 4, right: 15 }}
                />
                {intl.get('hzero.common.basicLayout.newMessage').d('您有新的消息')}
              </>
            ),
            description: (
              <a
                onClick={() => {
                  this.handleClick(key);
                }}
                style={{ paddingLeft: 35 }}
              >
                {intl.get('hzero.common.basicLayout.watchMessage').d('查看消息')}
              </a>
            ),
            className: 'request info',
          });
        }
      }
    });
  }

  @Bind()
  handleClick(key) {
    openTab({
      key: '/hmsg/user-message',
      path: '/hmsg/user-message/list',
      title: 'hzero.common.title.userMessage',
      icon: undefined,
      closable: true,
      search: '',
    });
    Notification.close(key);
  }

  componentWillUnmount() {
    webSocketManager.destroyWebSocket();
  }

  render() {
    return null;
  }
}

export default connect(
  ({ global = {} }) => ({
    count: global.count,
  }),
  (dispatch) => ({
    saveNotices: (payload) =>
      dispatch({
        type: 'global/saveNotices',
        payload,
      }),
  })
)(DefaultListenWebSocket);
