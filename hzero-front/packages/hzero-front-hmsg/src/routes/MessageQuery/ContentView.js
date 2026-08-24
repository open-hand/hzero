import React from 'react';
import { Modal, Spin } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';

import styles from './index.less';

/**
 * 接收人数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onOk - 确认之后的回调函数
 * @reactProps {Boolean} contentVisible - 收件人模态框是否可见
 * @reactProps {Object} content - 内容信息
 * @reactProps {Object} error - 错误信息
 * @return React.element
 */

export default class ContentView extends React.PureComponent {
  render() {
    const { contentVisible, onOk, content, isContent, error, loading, path } = this.props;
    // eslint-disable-next-line no-nested-ternary
    const modalContent = loading ? null : isContent ? (
      <div>
        <p>{content.subject}</p>
        <div className={styles.content} dangerouslySetInnerHTML={{ __html: content.content }} />
      </div>
    ) : (
      <pre style={{ height: '300px', overflowY: 'scroll' }}>{error.transactionMessage}</pre>
    );
    return (
      <Modal
        title={
          isContent
            ? intl.get('hmsg.messageQuery.model.messageQuery.content').d('内容')
            : intl.get('hmsg.messageQuery.model.messageQuery.error').d('错误')
        }
        width={700}
        destroyOnClose
        visible={contentVisible}
        closable={false}
        footer={
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.saveContent`,
                type: 'button',
                meaning: '消息查询-内容确定',
              },
            ]}
            type="primary"
            onClick={onOk}
          >
            {intl.get('hzero.common.button.ok').d('确定')}
          </ButtonPermission>
        }
      >
        <Spin spinning={loading}>{modalContent}</Spin>
      </Modal>
    );
  }
}
