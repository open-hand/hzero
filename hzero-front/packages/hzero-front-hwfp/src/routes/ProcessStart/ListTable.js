import React, { PureComponent } from 'react';
import { Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { yesOrNoRender, operatorRender } from 'utils/renderer';

/**
 * 流程启动数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Array} dataSource - Table数据源
 * @return React.element
 */
export default class ListTable extends PureComponent {
  // 删除
  @Bind()
  deleteData(record) {
    const { onDelete } = this.props;
    onDelete(record);
  }

  @Bind()
  handleEdit(record) {
    const { onEdit = e => e } = this.props;
    onEdit(record);
  }

  render() {
    const { loading, variables = [] } = this.props;
    const columns = [
      {
        title: intl.get('hwfp.common.model.param.name').d('参数名称'),
        dataIndex: 'name',
      },
      {
        title: intl.get('hwfp.common.model.param.value').d('参数值'),
        dataIndex: 'value',
        width: 250,
      },
      {
        title: intl.get('hwfp.common.model.param.requiredFlag').d('必输'),
        dataIndex: 'requiredFlag',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        fixed: 'right',
        width: 120,
        render: (val, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.handleEdit(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  placement="topRight"
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => this.deleteData(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <Table
        bordered
        rowKey="variableId"
        loading={loading}
        pagination={false}
        dataSource={variables}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
      />
    );
  }
}
