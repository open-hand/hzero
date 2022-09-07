import React from 'react';
import { Button, Modal, Icon, Tooltip } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import notification from 'utils/notification';

import styles from './index.less';

export default class RouteDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.ref = React.createRef();
  }

  @Bind()
  hanldeCopy() {
    const range = document.createRange(); // 创造range
    window.getSelection().removeAllRanges(); // 清除页面中已有的selection
    range.selectNode(this.ref.current); // 选中需要复制的节点
    window.getSelection().addRange(range); // 执行选中元素
    const copyStatus = document.execCommand('Copy'); // 执行copy操作
    // 对成功与否定进行提示
    if (copyStatus) {
      notification.success({
        message: intl.get('hrpt.reportDefinition.view.message.copySuccess').d('复制成功'),
      });
    } else {
      notification.error({
        message: intl.get('hrpt.reportDefinition.view.message.copyFail').d('复制失败'),
      });
    }
    window.getSelection().removeAllRanges(); // 清除页面中已有的selection
  }

  render() {
    const { visible, content, onCancel = e => e } = this.props;
    return (
      <Modal
        title={
          <span>
            {intl.get('hrpt.reportDefinition.view.title.menuRoute').d('菜单路由')}
            <Tooltip
              title={intl
                .get('hrpt.reportDefinition.view.message.menuRouteMsg')
                .d('用于新建或编辑菜单时进行路由配置')}
            >
              <Icon type="question-circle-o" style={{ marginLeft: '5px' }} />
            </Tooltip>
          </span>
        }
        visible={visible}
        onCancel={onCancel}
        footer={
          <Button onClick={onCancel} type="primary">
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
      >
        <div className={styles['route-wrap']}>
          <span ref={this.ref} className={styles['route-content']}>
            {content}
          </span>
          <Tooltip title={intl.get('hzero.common.button.copy').d('复制')}>
            <Icon type="copy" className={styles['route-icon']} onClick={this.hanldeCopy} />
          </Tooltip>
        </div>
      </Modal>
    );
  }
}
