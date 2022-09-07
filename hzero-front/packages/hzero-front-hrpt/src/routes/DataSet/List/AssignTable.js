import React from 'react';
import { Form, Modal, Table, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { enableRender } from 'utils/renderer';

@Form.create({ fieldNameProp: null })
export default class AssignTable extends React.PureComponent {
  @Bind()
  handleOk() {
    const { onOk = (e) => e } = this.props;
    onOk();
  }

  @Bind()
  handlePagination(pagination) {
    const { onSearch = (e) => e } = this.props;
    onSearch({
      page: pagination,
    });
  }

  render() {
    const { visible = false, fetchLoading = false, dataSource = [], pagination = {} } = this.props;
    const columns = [
      {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 100,
      },
      {
        title: intl.get('hrpt.common.report.reportCode').d('报表代码'),
        dataIndex: 'reportCode',
      },
      {
        title: intl.get('hrpt.common.report.reportName').d('报表名称'),
        dataIndex: 'reportName',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
    ];
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={intl.get('hrpt.reportDataSet.view.message.title.table').d('关联的报表')}
        width="820px"
        visible={visible}
        onCancel={this.handleOk}
        footer={
          <Button type="primary" onClick={this.handleOk}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
      >
        <Table
          bordered
          loading={fetchLoading}
          rowKey="reportId"
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={this.handlePagination}
        />
      </Modal>
    );
  }
}
