import React from 'react';
import { Form, Modal, Tabs, Spin, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import 'codemirror/mode/javascript/javascript';

import CodeMirror from 'components/CodeMirror';

import intl from 'utils/intl';

import SagaImage from '../components/SageImage';

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const { title, modalVisible, loading, initLoading, initData = {} } = this.props;
    const codeMirrorProps = {
      value: JSON.stringify(initData, null, 4).replace(/\\/g, ''),
      options: {
        mode: 'application/json',
        autoFocus: false,
        readOnly: 'nocursor',
      },
    };
    return (
      <Modal
        destroyOnClose
        title={title}
        width="1200px"
        visible={modalVisible}
        confirmLoading={loading}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.handleCancel}
        footer={
          <Button type="primary" onClick={this.handleCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
      >
        <Spin spinning={initLoading}>
          <Tabs defaultActiveKey="1" animated={false}>
            <Tabs.TabPane tab={intl.get('hagd.saga.view.title.sagaChart').d('事务定义图')} key="1">
              <SagaImage data={initData} />
            </Tabs.TabPane>
            <Tabs.TabPane tab="Json" key="2">
              <CodeMirror codeMirrorProps={codeMirrorProps} />
            </Tabs.TabPane>
          </Tabs>
        </Spin>
      </Modal>
    );
  }
}
