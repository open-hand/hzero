/**
 * ApproverDrawer - 审批人模态框
 * @date: 2019-4-24
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Modal, Form, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import QueryForm from './QueryForm';

function getRefFieldsValue(ref) {
  if (ref.current) {
    return ref.current.props.form.getFieldsValue();
  }
  return {};
}

@Form.create({ fieldNameProp: null })
export default class ApproverDrawer extends PureComponent {
  constructor(props) {
    super(props);
    this.oneSearchFormRef = React.createRef();
    this.state = {
      selectedRowKeys: [],
      selectedRows: [],
    };
  }

  // static getDerivedStateFromProps(nextProps, prevState) {
  //   const { prevSelectedApproveList } = prevState;
  //   const { selectedApproveList = [] } = nextProps;
  //   if (prevSelectedApproveList !== selectedApproveList) {
  //     return {
  //       selectedRowKeys: selectedApproveList.map(item => item.employeeId),
  //       prevSelectedApproveList: selectedApproveList,
  //     };
  //   }
  //   return null;
  // }

  @Bind()
  handleSubmit() {
    const { onOk = e => e } = this.props;
    const { selectedRowKeys = [], selectedRows } = this.state;
    if (selectedRowKeys.length > 0) {
      onOk(selectedRows);
      this.handleCancel();
      this.setState({
        selectedRowKeys: [],
        selectedRows: [],
      });
    } else {
      Modal.warning({
        title: intl.get('hzero.common.validation.atLeast').d('请至少选择一条数据'),
      });
    }
  }

  /**
   * 表单查询
   */
  @Bind()
  handleOneSearchFormSearch() {
    const { onFormSearch } = this.props;
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    onFormSearch(fieldsValue);
  }

  /**
   * 客户端分页切换
   * @param {*} pagination
   */
  @Bind()
  onTableChange(pagination = {}) {
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    const { onPageChange } = this.props;
    onPageChange(pagination, fieldsValue);
  }

  @Bind()
  // 选中事件
  onSelectChange(selectedRowKeys, selectedRows) {
    this.setState({ selectedRowKeys, selectedRows });
  }

  @Bind()
  handleCancel() {
    const { onCancel = e => e } = this.props;
    onCancel();
    this.setState({
      selectedRowKeys: [],
      selectedRows: [],
    });
  }

  render() {
    const {
      title,
      loading,
      approveLoading,
      visible,
      dataSource,
      userPagination,
      currentAddApprover,
    } = this.props;
    const { selectedRowKeys } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.onSelectChange,
      type: 'checkbox',
    };

    const columns = [
      {
        title: intl.get('entity.employee.employeeCode').d('员工编码'),
        dataIndex: 'employeeCode',
        width: 120,
      },
      {
        title: intl.get('entity.employee.name').d('员工姓名'),
        dataIndex: 'name',
        width: 150,
      },
    ];
    return (
      <Modal
        destroyOnClose
        width={800}
        title={title}
        visible={visible}
        onCancel={this.handleCancel}
        onOk={this.handleSubmit}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        confirmLoading={approveLoading}
      >
        <QueryForm
          wrappedComponentRef={this.oneSearchFormRef}
          onSearch={this.handleOneSearchFormSearch}
          currentAddApprover={currentAddApprover}
        />
        <Table
          bordered
          rowKey="employeeId"
          loading={loading}
          dataSource={dataSource}
          pagination={userPagination}
          rowSelection={rowSelection}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={this.onTableChange}
        />
      </Modal>
    );
  }
}
