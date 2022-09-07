import React from 'react';
import { Form, Modal } from 'hzero-ui';

import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class DetailDrawer extends React.PureComponent {
  render() {
    const { detailData = {}, title, modalVisible, ...other } = this.props;
    const { executorAddress, glueTypeMeaning, executorParam } = detailData;
    return (
      <Modal
        destroyOnClose
        title={title}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={modalVisible}
        {...other}
      >
        <div>
          {intl.get('hsdr.jobLog.model.jobLog.executorAddress').d('执行器地址')}: {executorAddress}
        </div>
        <div>
          {intl.get('hsdr.jobLog.model.jobLog.glueTypeMeaning').d('运行模式')}: {glueTypeMeaning}
        </div>
        <div>
          {intl.get('hsdr.jobLog.model.jobLog.executorParam').d('任务参数')}: {executorParam}
        </div>
      </Modal>
    );
  }
}
