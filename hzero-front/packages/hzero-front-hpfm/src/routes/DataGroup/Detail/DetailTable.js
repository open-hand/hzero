/**
 * DataGroup- 数据组管理-详情表格
 * @date: 2019/7/15
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component, Fragment } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import AssignedValue from './AssignedValue';

/**
 * 数据组管理-详情表格
 * @extends {Component} - React.Component
 * @reactProps {boolean} loading - 表格是否在加载中
 * @reactProps {array[Object]} dataSource - 表格数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Object} rowSelection - 表格行选择
 * @reactProps {Function} onChange - 表格变化时的回调
 * @reactProps {Function} onClean - 清空分配值数据
 * @reactProps {Function} fetchAssignedValueList - 查询分配值列表数据
 * @reactProps {Function} createAssignedValue - 创建分配值
 * @reactProps {Function} deleteAssignedValue - 删除分配值
 * @reactProps {Function} fetchValueModalData - 查询维度代码弹窗列表数据
 * @reactProps {Boolean} fetchValueModalLoading - 选择维度值弹窗列表加载标志
 * @reactProps {Boolean} createAssignedValueLoading - 创建分配值加载标志
 * @reactProps {Boolean} assignedValueList - 分配值列表
 * @reactProps {Boolean} assignedValuePagination - 分配值分页器
 * @reactProps {Boolean} valueDataSource - 维度代码弹窗列表数据
 * @reactProps {Boolean} valuePagination - 维度代码弹窗列表分页
 * @return React.element
 */

export default class DetailTable extends Component {
  constructor(props) {
    super(props);
    this.state = {
      assignedDrawerVisible: false,
      currentLine: {},
    };
  }

  /**
   * 显示分配值侧滑
   * @param {object} record - 当前行数据
   */
  @Bind()
  openAssignedDrawer(record) {
    this.setState({
      assignedDrawerVisible: true,
      currentLine: record,
    });
  }

  /**
   * 关闭分配值侧滑
   */
  @Bind()
  closeAssignedDrawer() {
    const { onClean } = this.props;
    this.setState(
      {
        assignedDrawerVisible: false,
        currentLine: {},
      },
      () => {
        onClean();
      }
    );
  }

  render() {
    const {
      loading,
      dataSource,
      pagination,
      rowSelection,
      onChange,
      match,
      fetchAssignedValueList = () => {},
      createAssignedValue = () => {},
      fetchValueModalData = () => {},
      deleteAssignedValue = () => {},
      fetchValueModalLoading,
      fetchAssignedValueLoading,
      createAssignedValueLoading,
      assignedValueList,
      assignedValuePagination,
      valueDataSource,
      valuePagination,
    } = this.props;
    const { assignedDrawerVisible, currentLine } = this.state;
    const columns = [
      {
        title: intl.get('hpfm.dataGroup.model.dataGroup.dimensionCode').d('维度代码'),
        dataIndex: 'dimensionCode',
      },
      {
        title: intl.get('hpfm.dataGroup.model.dataGroup.dimensionName').d('维度名称'),
        dataIndex: 'dimensionName',
        width: 350,
      },
      {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 300,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 108,
        render: (_, record) => {
          const operators = [
            {
              key: 'value',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.value`,
                      type: 'button',
                      meaning: '数据组详情-分配值',
                    },
                  ]}
                  onClick={() => this.openAssignedDrawer(record)}
                >
                  {intl.get('hpfm.dataGroup.view.message.dataGroup.value').d('分配值')}
                </ButtonPermission>
              ),
              len: 3,
              title: intl.get('hpfm.dataGroup.view.message.dataGroup.value').d('分配值'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    const tableProps = {
      loading,
      columns,
      dataSource,
      pagination,
      bordered: true,
      rowSelection,
      rowKey: 'lovId',
      onChange: page => onChange(page),
    };
    const assignedValueProps = {
      visible: assignedDrawerVisible,
      onCancel: this.closeAssignedDrawer,
      match,
      currentLine,
      fetchAssignedValueList,
      valueDataSource,
      valuePagination,
      fetchValueModalData,
      createAssignedValue,
      deleteAssignedValue,
      fetchValueModalLoading,
      fetchAssignedValueLoading,
      createAssignedValueLoading,
      assignedValueList,
      assignedValuePagination,
    };
    return (
      <Fragment>
        <Table {...tableProps} />
        <AssignedValue {...assignedValueProps} />
      </Fragment>
    );
  }
}
