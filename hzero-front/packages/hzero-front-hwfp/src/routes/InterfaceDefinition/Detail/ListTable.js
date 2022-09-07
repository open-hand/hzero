import React, { PureComponent } from 'react';
import { Table, Popconfirm, Button } from 'hzero-ui';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

export default class ListTable extends PureComponent {
  /**
   * 编辑
   * @param {object} record - 数据对象
   */
  handleEdit(record) {
    const { onEdit = e => e } = this.props;
    onEdit(record);
  }

  /**
   * 删除
   * @param {object} record - 数据对象
   */
  handleDelete(record) {
    const { onDelete = e => e } = this.props;
    onDelete(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      dataSource = [],
      pagination = {},
      onCreate = e => e,
      detail = {},
      isSiteFlag,
      isPredefined,
    } = this.props;
    const columns = [
      {
        title: intl.get('hwfp.interfaceDefinition.model.param.parameterName').d('参数名称'),
        dataIndex: 'parameterName',
        width: 150,
      },
      {
        title: intl.get('hwfp.interfaceDefinition.model.param.parameterType').d('参数类型'),
        dataIndex: 'parameterTypeMeaning',
        width: 150,
      },
      {
        title: intl.get('hwfp.interfaceDefinition.model.param.description').d('参数描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hwfp.interfaceDefinition.model.param.defaultValue').d('默认值'),
        dataIndex: 'defaultValue',
        width: 100,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        fixed: 'right',
        width: 130,
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
                  onConfirm={() => this.handleDelete(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          if (isSiteFlag || !isPredefined) {
            return operatorRender(operators, record);
          } else {
            return null;
          }
        },
      },
    ];
    return (
      <>
        <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 16 }}>
          <Button
            onClick={onCreate}
            disabled={!isSiteFlag ? isPredefined : false || detail.interfaceId === undefined}
          >
            {intl.get('hwfp.common.button.params.add').d('添加参数')}
          </Button>
        </div>
        <Table
          bordered
          rowKey="parameterId"
          loading={loading}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          pagination={pagination}
        />
      </>
    );
  }
}
