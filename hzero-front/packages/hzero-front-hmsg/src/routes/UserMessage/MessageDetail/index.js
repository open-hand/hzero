/**
 * 当详情页展示的公告信息时, userMessageId 时 noticeId
 * userMessage 站内消息详情
 * @date: 2018-8-4
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Spin, Divider } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import UploadModal from 'components/Upload';

import { getCurrentOrganizationId } from 'utils/utils';
import { dateTimeRender } from 'utils/renderer';
import { BKT_MSG } from 'utils/config';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import styles from '../index.less';

@formatterCollections({ code: ['hmsg.userMessage'] })
@connect(({ userMessage, loading }) => ({
  userMessage,
  organizationId: getCurrentOrganizationId(),
  queryDetailLoading: loading.effects['userMessage/queryDetail'],
}))
export default class BadgeIcon extends PureComponent {
  state = { file: [] };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'userMessage/updateState',
      payload: {
        messageDetail: {},
      },
    });
    this.getMessageDetail();
  }

  componentDidUpdate(prevProps) {
    const {
      match: {
        params: { type: prevType = 'message', userMessageId: prevUserMessageId },
      },
    } = prevProps;
    const {
      match: {
        params: { type = 'message', userMessageId },
      },
    } = this.props;
    if (type !== prevType || userMessageId !== prevUserMessageId) {
      // 需要更新详情
      this.getMessageDetail();
    }
  }

  /**
   * 获取消息详情
   */
  @Bind()
  getMessageDetail() {
    const { dispatch, match, organizationId } = this.props;
    const {
      params: { userMessageId, type = 'message' },
    } = match;
    dispatch({
      type: 'userMessage/queryDetail',
      payload: { userMessageId, organizationId, type },
    }).then((res) => {
      if (res && res.attachmentUuid) {
        dispatch({
          type: 'userMessage/queryFileList',
          payload: {
            attachmentUUID: res.attachmentUuid,
            bucketName: BKT_MSG,
            directory: 'hmsg01',
          },
        }).then((response) => {
          if (response) {
            this.setState({ file: response });
          }
        });
      }
    });
  }

  @Bind()
  handleSaveMessageId() {
    const {
      dispatch,
      match: {
        params: { userMessageId },
      },
    } = this.props;
    dispatch({
      type: 'userMessage/updateState',
      payload: { userMessageId },
    });
  }

  render() {
    const {
      userMessage: { messageDetail = {} },
      queryDetailLoading = false,
      match: {
        params: { type = 'message' },
      },
    } = this.props;
    const { file } = this.state;
    const title = type === 'announce' ? messageDetail.title : messageDetail.subject;
    const datetime = dateTimeRender(
      type === 'announce' ? messageDetail.publishedDate : messageDetail.creationDate
    );
    const content = type === 'announce' ? messageDetail.noticeBody : messageDetail.content;
    return (
      <>
        <Header
          title={intl.get('hmsg.userMessage.view.message.title.detail').d('消息详情')}
          backPath="/hmsg/user-message/list"
        />
        <Content>
          <Spin spinning={queryDetailLoading}>
            <div style={{ borderBottom: 'solid 1px #e8e8e8' }}>
              <p
                style={{
                  margin: 0,
                  fontWeight: 'bold',
                  fontSize: 20,
                }}
              >
                {title}
              </p>
              <p>{datetime}</p>
            </div>
            <div
              className={styles.content}
              onClick={this.handleSaveMessageId}
              dangerouslySetInnerHTML={{ __html: content }}
            />
            {file.length > 0 && (
              <div className={styles.upload}>
                <Divider dashed style={{ margin: '10px 0' }} />
                <UploadModal
                  attachmentUUID={messageDetail.attachmentUuid}
                  bucketName={BKT_MSG}
                  bucketDirectory="hmsg01"
                  viewOnly
                />
              </div>
            )}
          </Spin>
        </Content>
      </>
    );
  }
}
