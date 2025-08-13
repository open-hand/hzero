import React, { Fragment, PureComponent } from 'react';
import { Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
/**
 * 员工明细-已分配用户信息列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @return React.element
 */
export default class UserList extends PureComponent {
  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      match,
      deleteUserLoading = false,
      dataSource = [],
      selectedRowKeys,
      onAdd,
      onDelete,
      onChange,
      customizeTable,
      employeeInfo = {},
    } = this.props;
    const columns = [
      {
        title: intl.get('entity.user.code').d('用户编码'),
        dataIndex: 'loginName',
        width: 200,
      },
      {
        title: intl.get('entity.user.name').d('用户姓名'),
        dataIndex: 'realName',
      },
    ];
    return (
      <Fragment>
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.add`,
                type: 'button',
                meaning: '员工定义明细-新增用户',
              },
            ]}
            onClick={onAdd}
            disabled={employeeInfo.status === 'LEAVE'}
          >
            {intl.get('hpfm.employee.view.option.user.add').d('新增用户')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.delete`,
                type: 'button',
                meaning: '员工定义明细-删除用户',
              },
            ]}
            onClick={onDelete}
            loading={deleteUserLoading}
            disabled={selectedRowKeys.length === 0 || employeeInfo.status === 'LEAVE'}
          >
            {intl.get('hpfm.employee.view.option.user.delete').d('删除用户')}
          </ButtonPermission>
        </div>
        {customizeTable(
          { code: 'HPFM.EMPLOYEE_DETAIL.USER_LINE' },
          <Table
            bordered
            rowKey="employeeUserId"
            loading={loading}
            columns={columns}
            dataSource={dataSource}
            pagination={false}
            scroll={{ x: tableScrollWidth(columns) }}
            rowSelection={{
              selectedRowKeys,
              onChange,
            }}
          />
        )}
      </Fragment>
    );
  }
}
