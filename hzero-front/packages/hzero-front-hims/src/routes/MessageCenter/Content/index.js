import React from 'react';
import Chat from './Chat';

import styles from './index.less';

export default class Content extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const {
      onUpdate,
      groupKey,
      record,
      messageListDs,
      messageList,
      id,
      isSelected,
      fetchMessageListLoading,
      onOpenModal,
      onChange,
      accessToken,
      organizationId,
      onPreview,
      defaultStatus,
    } = this.props;
    const chatProps = {
      groupKey,
      record,
      messageList,
      id,
      isSelected,
      fetchMessageListLoading,
      onOpenModal,
      onChange,
      accessToken,
      organizationId,
      onUpdate,
      messageListDs,
      onPreview,
      defaultStatus,
    };
    return (
      <div className={styles['message-center-content']}>
        <Chat {...chatProps} />
      </div>
    );
  }
}
