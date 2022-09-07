import React, { PureComponent } from 'react';
import { Table, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, differenceBy } from 'lodash';

import { dateTimeRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { openTab } from 'utils/menuTab';

/**
 * 待办事项列表展示列表
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
  constructor(props) {
    super(props);
    this.state = {
      selectedRows: [],
    };
  }

  /**
   * 详情
   * @param {object} record - 头数据
   */
  changeDetail(record) {
    openTab({
      title: record.assigneeName
        ? `${record.processName}-${record.assigneeName}`
        : `${record.processName}`,
      key: `/hwfp/task/detail/${record.id}/${record.encryptProcInstId}`,
      path: `/hwfp/task/detail/${record.id}/${record.encryptProcInstId}`,
      icon: 'edit',
      closable: true,
    });
  }

  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 250,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: (e) => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  @Bind()
  handleSelectOne(record, selected, selectedRows) {
    const { onChangeSelected } = this.props;
    const { needAppoint } = record;
    if (selected && needAppoint === true) {
      Modal.warning({
        title: intl.get('hwfp.task.view.message.needAssignApprover').d('请先指定下一审批人'),
      });
    } else {
      this.setState(
        {
          selectedRows,
        },
        () => {
          onChangeSelected(selectedRows);
        }
      );
    }
  }

  @Bind()
  handleSelectAll(selected, selectedRows = []) {
    const { onChangeSelected } = this.props;
    const needAppointRows = selectedRows.filter((item) => item.needAppoint === true);
    const newSelectedRows = differenceBy(selectedRows, needAppointRows, 'id');
    this.setState(
      {
        selectedRows: newSelectedRows,
      },
      () => {
        onChangeSelected(newSelectedRows);
      }
    );
    if (selected && !isEmpty(needAppointRows)) {
      Modal.warning({
        title: intl.get('hwfp.task.view.message.needAssignApprover').d('请先指定下一审批人'),
      });
    }
  }

  @Bind()
  handleSelectRows(_, selectedRows) {
    const { onChangeSelected } = this.props;
    this.setState(
      {
        selectedRows,
      },
      () => {
        onChangeSelected(selectedRows);
      }
    );
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
      onChange,
      batchApproveTasksLoading,
    } = this.props;
    const { selectedRows } = this.state;
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.id),
      // onChange: this.handleSelectRows,
      onSelect: this.handleSelectOne,
      onSelectAll: this.handleSelectAll,
    };
    const columns = [
      {
        title: intl.get('hwfp.common.model.process.ID').d('流程标识'),
        dataIndex: 'processInstanceId',
        width: 100,
      },
      {
        title: intl.get('hwfp.common.model.process.name').d('流程名称'),
        dataIndex: 'processName',
      },
      {
        title: intl.get('hwfp.common.model.process.description').d('流程描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hwfp.common.model.approval.step').d('审批环节'),
        dataIndex: 'name',
        width: 150,
      },
      {
        title: intl.get('hwfp.common.model.apply.owner').d('申请人'),
        dataIndex: 'startUserName',
        width: 150,
        render: (val, record) =>
          val && (
            <span>
              {val}({record.startUserId})
            </span>
          ),
      },
      {
        title: intl.get('hwfp.task.model.task.creationTime').d('创建时间'),
        dataIndex: 'createTime',
        width: 160,
        render: dateTimeRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 100,
        align: 'center',
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'deal',
              ele: (
                <a onClick={() => this.changeDetail(record)}>
                  {intl.get('hwfp.task.view.option.deal').d('办理')}
                </a>
              ),
              len: 4,
              title: intl.get('hwfp.task.view.option.deal').d('办理'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <Table
        bordered
        rowKey="id"
        loading={loading || batchApproveTasksLoading}
        dataSource={dataSource}
        pagination={pagination}
        rowSelection={rowSelection}
        onChange={onChange}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
      />
    );
  }
}
