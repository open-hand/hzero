import React, { PureComponent } from 'react';
import { Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { yesOrNoRender, valueMapMeaning, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';

/**
 * 表单管理数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class ListTable extends PureComponent {
  // 编辑
  @Bind()
  editModal(record) {
    const { onGetRecord } = this.props;
    onGetRecord(record);
  }

  // 删除
  @Bind()
  deleteData(record) {
    const { onDelete } = this.props;
    onDelete(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, formManageList = {}, category, pagination, onChange } = this.props;

    const columns = [
      {
        title: intl.get('hwfp.common.model.process.class').d('流程分类'),
        dataIndex: 'category',
        width: 150,
        render: val => val && valueMapMeaning(category, val),
      },
      {
        title: intl.get('hwfp.common.model.common.code').d('编码'),
        dataIndex: 'code',
        width: 150,
      },
      {
        title: intl.get('hwfp.common.model.common.description').d('描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hwfp.formManage.model.formManage.invokeFlag').d('是否回调'),
        dataIndex: 'invokeFlag',
        width: 100,
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        fixed: 'right',
        width: 110,
        render: (val, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a
                  onClick={() => {
                    this.editModal(record);
                  }}
                >
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
        rowKey="formDefinitionId"
        loading={loading}
        pagination={pagination}
        dataSource={formManageList.content}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        onChange={page => onChange(page)}
      />
    );
  }
}
