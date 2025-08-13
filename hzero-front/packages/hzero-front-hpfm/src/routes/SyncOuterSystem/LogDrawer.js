import React from 'react';
import { withRouter } from 'react-router';
import { Bind } from 'lodash-decorators';
import { ModalContainer, Modal, DataSet } from 'choerodon-ui/pro';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';

import { logDetailDs } from '../../stores/syncOuterSystemDS';

@withRouter
export default class LogDrawer extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      logContent: '',
    };
  }

  logDetailDs = new DataSet(logDetailDs());

  async componentDidMount() {
    await this.queryData();
    this.renderDrawer();
  }

  @Bind()
  async queryData() {
    const {
      currentEditData: { syncLogId },
    } = this.props;
    this.logDetailDs.logId = syncLogId;
    const data = await this.logDetailDs.query();
    this.setState({
      logContent: data && data.logContent,
    });
  }

  /**
   * 渲染模态框
   */
  @Bind()
  renderDrawer() {
    const {
      match: { path },
    } = this.props;
    const { logContent } = this.state;
    const arrString = logContent.split('\n');
    Modal.open({
      key: 'createModelType',
      fullScreen: true,
      destroyOnClose: true,
      closable: true,
      title: intl.get('hpfm.syncOuterSystem.view.message.title.logDetail').d('日志详情'),
      children: arrString.map((item) => (
        <pre
          style={{ color: item.startsWith('error') ? 'red' : 'black', overflow: 'visible' }}
          dangerouslySetInnerHTML={{ __html: item }}
        />
      )),
      footer: [
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/close`,
              type: 'button',
              meaning: '组织信息同步-关闭',
            },
          ]}
          key="cancel"
          onClick={this.handleClose}
        >
          {intl.get('hzero.common.button.close').d('关闭')}
        </ButtonPermission>,
      ],
      onClose: this.handleClose,
    });
  }

  @Bind()
  handleClose() {
    const { onClose } = this.props;
    this.setState({
      logContent: '',
    });
    onClose();
  }

  render() {
    return (
      <>
        <ModalContainer location={location} />
      </>
    );
  }
}
