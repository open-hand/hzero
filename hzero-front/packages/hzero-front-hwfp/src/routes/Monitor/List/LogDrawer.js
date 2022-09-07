import React from 'react';
import { Modal, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { getEditTableData, tableScrollWidth } from 'utils/utils';

export default class LogDrawer extends React.PureComponent {
  @Bind()
  handleResult() {
    const {
      onResult = e => e,
      currentRecord: { interfaceCode },
      parameterList,
    } = this.props;
    const params = getEditTableData(parameterList, ['parameterId']);
    if (parameterList.length === 0) {
      return onResult({ interfaceCode });
    }
    if (Array.isArray(params) && params.length > 0) {
      const args = {};
      params.forEach(item => {
        args[item.parameterName] = item.paramterValue;
      });
      onResult({ interfaceCode, args });
    }
  }

  render() {
    const { visible, loading, onCancel, exceptionList = false, logLoading = false } = this.props;
    const columns = [
      {
        title: intl.get('hwfp.monitor.model.monitor.messageHead').d('消息头'),
        dataIndex: 'messageHead',
        width: 200,
      },
      {
        title: intl.get('hwfp.monitor.model.monitor.procDefName').d('流程名称'),
        dataIndex: 'procDefName',
        width: 200,
      },
      {
        title: intl.get('hwfp.monitor.model.monitor.duedate').d('日期'),
        dataIndex: 'duedate',
        width: 200,
      },
    ];
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={intl.get('hwfp.monitor.view.message.title.log').d('异常信息')}
        width="820px"
        visible={visible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={onCancel}
      >
        <Table
          bordered
          expandRowByClick
          rowKey="id"
          scroll={{ x: tableScrollWidth(columns, 200) }}
          columns={columns}
          loading={logLoading}
          dataSource={exceptionList}
          expandedRowRender={record => (
            <p style={{ margin: 0 }}>
              <pre>{record.messStr}</pre>
            </p>
          )}
          pagination={false}
        />
      </Modal>
    );
  }
}
