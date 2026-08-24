import React, { Fragment, PureComponent } from 'react';
import { Checkbox, Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';

/**
 * 员工明细-已分配岗位信息列表
 * @extends {PureComponent} - React.PureComponent
 //  * @reactProps {Boolean} primaryFlag - 是否已分配主岗标记
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Function} onChange - 维护岗位信息
 * @return React.element
 */

export default class PositionList extends PureComponent {
  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      match,
      dataSource = [],
      selectedRowKeys,
      onChange,
      onAdd,
      onDelete,
      onEditPrimary,
      customizeTable,
      employeeInfo = {},
    } = this.props;
    const columns = [
      {
        title: intl.get('hpfm.employee.model.employee.unitCompanyName').d('所属公司'),
        dataIndex: 'unitCompanyName',
        width: 250,
      },
      {
        title: intl.get('hpfm.employee.model.employee.unitName').d('所属部门'),
        dataIndex: 'unitName',
        width: 220,
      },
      {
        title: intl.get('hpfm.employee.model.employee.positionName').d('所属岗位'),
        dataIndex: 'positionName',
        width: 220,
      },
      {
        title: intl.get('hpfm.employee.model.employee.primaryPositionFlag').d('主岗'),
        dataIndex: 'primaryPositionFlag',
        width: 100,
        render: (val, record) => (
          <Checkbox
            checked={val}
            onChange={event => onEditPrimary(record, event.target.checked)}
            disabled={employeeInfo.status === 'LEAVE'}
          />
        ),
      },
    ];
    return (
      <Fragment>
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.change`,
                type: 'button',
                meaning: '员工定义明细-维护岗位',
              },
            ]}
            onClick={onAdd}
            disabled={employeeInfo.status === 'LEAVE'}
          >
            {intl.get('hpfm.employee.view.option.change').d('维护岗位')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.remove`,
                type: 'button',
                meaning: '员工定义明细-删除岗位',
              },
            ]}
            onClick={onDelete}
            disabled={selectedRowKeys.length === 0 || employeeInfo.status === 'LEAVE'}
          >
            {intl.get('hpfm.employee.view.option.remove').d('删除岗位')}
          </ButtonPermission>
        </div>
        {customizeTable(
          { code: 'HPFM.EMPLOYEE_DETAIL.POST_LINE' },
          <Table
            bordered
            rowKey="positionId"
            scroll={{ x: tableScrollWidth(columns) }}
            loading={loading}
            columns={columns}
            dataSource={dataSource}
            pagination={false}
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
