/**
 * ListTable - 数据组管理列表页
 * @date: 2019-7-11
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component, Fragment } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Function} onEdit - 跳转详情页
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @return React.element
 */
export default class ListTable extends Component {
  /**
   * 编辑
   * @param {number} groupId - 用户组Id
   */
  @Bind()
  handleEdit(groupId) {
    this.props.onEdit(groupId);
  }

  render() {
    const { loading, dataSource = [], onChange, pagination, match } = this.props;
    const columns = [
      {
        title: intl.get('hpfm.dataGroup.model.dataGroup.code').d('代码'),
        width: 150,
        dataIndex: 'groupCode',
      },
      {
        title: intl.get('hpfm.dataGroup.model.dataGroup.name').d('名称'),
        dataIndex: 'groupName',
      },
      {
        title: intl.get('hpfm.dataGroup.model.dataGroup.remark').d('说明'),
        dataIndex: 'remark',
        width: 300,
      },
      {
        title: intl.get('hpfm.permission.model.permission.tenant').d('租户'),
        width: 130,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 60,
        key: 'edit',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '数据组管理-编辑',
                    },
                  ]}
                  onClick={() => this.handleEdit(record.groupId)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <Fragment>
        <Table
          bordered
          rowKey="groupId"
          loading={loading}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          pagination={pagination}
          onChange={page => onChange(page)}
        />
      </Fragment>
    );
  }
}
