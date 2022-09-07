import React, { PureComponent, Fragment } from 'react';
import { Table } from 'hzero-ui';

import { enableRender } from 'utils/renderer';
import intl from 'utils/intl';

/**
 * 租户期间定义数据展示组件
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChangeFlag - 行编辑
 * @reactProps {Function} onCleanLine - 行清除操作
 * @reactProps {Function} onSearch - 分页查询
 * @reactProps {Array} dataSource - table数据源
 * @reactProps {object} pagination - 分页器
 * @reactProps {object} [pagination.current] - 当前页码
 * @reactProps {object} [pagination.pageSize] - 分页大小
 * @reactProps {object} [pagination.total] - 数据总量
 * @return React.element
 */
export default class ListTable extends PureComponent {
  /**
   * render
   * @returns React.element
   */
  render() {
    const { dataSource, pagination, loading, onSearch, onEdit } = this.props;
    const columns = [
      {
        title: intl.get(`hpfm.costCenter.model.project.companyName`).d('公司'),
        dataIndex: 'companyName',
        width: 250,
      },
      {
        title: intl.get(`hpfm.costCenter.model.project.ouId`).d('业务实体'),
        dataIndex: 'ouName',
        width: 250,
      },
      {
        title: intl.get(`hpfm.costCenter.model.costCenter.code`).d('成本中心编码'),
        dataIndex: 'costCode',
        width: 150,
      },
      {
        title: intl.get(`hpfm.costCenter.model.costCenter.name`).d('成本中心名称'),
        dataIndex: 'costName',
        width: 250,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        align: 'left',
        render: (val) => enableRender(val),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'enabledFlag',
        width: 90,
        align: 'left',
        render: (val, record) => (
          <a style={{ cursor: 'pointer' }} onClick={() => onEdit(record, true)}>
            {intl.get('hzero.common.button.edit').d('编辑')}
          </a>
        ),
      },
    ];
    return (
      <Fragment>
        <Table
          bordered
          loading={loading}
          rowKey="costId"
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={(page) => onSearch(page)}
        />
      </Fragment>
    );
  }
}
