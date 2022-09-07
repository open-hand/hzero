import React from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { enableRender, yesOrNoRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

export default class List extends React.Component {
  @Bind()
  handlePagination(page) {
    const { onChange = e => e } = this.props;
    onChange({ page });
  }

  @Bind()
  getColumns() {
    if (!this.columns) {
      this.columns = [
        {
          title: intl.get('hpay.payConfig.model.payConfig.channelMeaning').d('支付渠道'),
          dataIndex: 'channelMeaning',
          width: 150,
        },
        {
          title: intl.get('hpay.payConfig.model.payConfig.configCode').d('支付配置编码'),
          dataIndex: 'configCode',
        },
        {
          title: intl.get('hpay.payConfig.model.payConfig.configName').d('支付配置名称'),
          dataIndex: 'configName',
        },
        {
          title: intl.get('hpay.payConfig.model.payConfig.defaultFlag').d('默认标识'),
          dataIndex: 'defaultFlag',
          width: 100,
          render: yesOrNoRender,
        },
        {
          title: intl.get('hzero.common.status').d('状态'),
          width: 80,
          dataIndex: 'enabledFlag',
          render: enableRender,
        },
        {
          title: intl.get('hzero.common.remark').d('备注'),
          dataIndex: 'remark',
          width: 150,
        },
        {
          title: intl.get('hzero.common.button.action').d('操作'),
          dataIndex: 'operator',
          width: 120,
          fixed: 'right',
          render: (val, record) => {
            const { onEdit = e => e, onCert = e => e } = this.props;
            const operators = [];
            operators.push({
              key: 'edit',
              ele: (
                <a
                  onClick={() => {
                    onEdit(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
            if (record.channelCode !== 'alipay') {
              operators.push({
                key: 'cert',
                ele: (
                  <a
                    onClick={() => {
                      onCert(record);
                    }}
                  >
                    {intl.get('hpay.payConfig.button.cert').d('证书')}
                  </a>
                ),
                len: 2,
                title: intl.get('hpay.payConfig.button.cert').d('证书'),
              });
            }
            return operatorRender(operators, record, { limit: 3 });
          },
        },
      ].filter(Boolean);
    }
    return this.columns;
  }

  render() {
    const { loading = false, dataSource = [], pagination = {} } = this.props;
    return (
      <Table
        bordered
        rowKey="configId"
        columns={this.getColumns()}
        scroll={{ x: tableScrollWidth(this.getColumns()) }}
        dataSource={dataSource}
        pagination={pagination}
        loading={loading}
        onChange={this.handlePagination}
      />
    );
  }
}
