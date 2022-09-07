import React, { Component } from 'react';
import { Icon } from 'choerodon-ui/pro';
import styles from './confirmModal.less';

export default class ConfirmModal extends Component {
  render() {
    const { msgTitle, msgContent } = this.props;

    return (
      <div className={styles.container}>
        <div className={styles.MsgContent}>
          <Icon type="info_outline" className={styles.icon} />
          <span>{msgTitle}</span>
        </div>
        <div className={styles.InfoContent}>
          <span>{msgContent}</span>
        </div>
      </div>
    );
  }
}
