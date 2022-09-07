import React from 'react';
import { Bind } from 'lodash-decorators';
import { Modal } from 'hzero-ui';

import intl from 'utils/intl';

import SendMessage from './SendMessage';
import Content from './Content';
import styles from './index.less';

export default class IM extends React.Component {
  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const {
      id,
      visible,
      isSelected,
      currentKey,
      groupKey,
      ws,
      record,
      accessToken,
      organizationId,
      messageList,
      messageListDs,
      onUpdate,
      onChange,
      // onUpdateMessageList,
      fetchMessageListLoading,
      defaultStatus,
      onOpenModal = (e) => e,
      onPreview = (e) => e,
    } = this.props;
    const contentProps = {
      id,
      isSelected,
      currentKey,
      groupKey,
      record,
      messageList,
      onUpdate,
      onChange,
      messageListDs,
      fetchMessageListLoading,
      onOpenModal,
      accessToken,
      organizationId,
      onPreview,
      defaultStatus,
    };
    const sendMessageProps = {
      id,
      currentKey,
      groupKey,
      ws,
      // onUpdateMessageList,
      defaultStatus,
      organizationId,
    };
    return (
      <Modal
        keyboard
        destroyOnClose
        title={intl.get('hims.messageCenter.view.title.onlineCustomer').d('在线客服')}
        width="650px"
        visible={visible}
        closable
        footer={null}
        bodyStyle={{ padding: 0 }}
        onCancel={this.handleCancel}
        onClose={() => {
          messageListDs.isScrollToBottom = true;
        }}
      >
        <div
          className={
            // eslint-disable-next-line no-nested-ternary
            isSelected
              ? styles['message-center-selected']
              : defaultStatus === 'closed'
              ? styles['message-center-selected']
              : styles['message-center']
          }
        >
          <Content {...contentProps} />
          {!((isSelected && messageList.length > 0) || defaultStatus === 'closed') && (
            <SendMessage {...sendMessageProps} />
          )}
        </div>
      </Modal>
    );
  }
}
