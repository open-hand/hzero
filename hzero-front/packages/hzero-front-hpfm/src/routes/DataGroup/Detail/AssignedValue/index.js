/**
 * AssignedValue - 分配值弹窗
 * @date: 2019/7/15
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Spin, Modal, Form, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { DETAIL_DEFAULT_CLASSNAME } from 'utils/constants';
import notification from 'utils/notification';

import AssignedForm from './Form';
import AssignedTable from './Table';
import AddDataModal from '../AddDataModal';

@Form.create({ fieldNameProp: null })
export default class AssignedValue extends Component {
  state = {
    modalVisible: false,
    selectedRowKeys: [], // 选中行key集合
    selectedRows: {}, // 选中行集合
  };

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, currentLine = {} } = this.props;
    const { groupLineId } = currentLine;

    return (
      visible && !isEmpty(groupLineId) && groupLineId !== (prevProps.currentLine || {}).groupLineId
    );
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.handleFetchValueList();
    }
  }

  /**
   * fetchList - 获取列表数据
   * @param {Object} page - 分页参数
   */
  @Bind()
  handleFetchValueList(page = {}) {
    const { fetchAssignedValueList = () => {}, currentLine = {} } = this.props;
    fetchAssignedValueList(currentLine.groupLineId, page);
  }

  /**
   * 关闭分配值弹窗
   */
  @Bind()
  handleCancel() {
    const { onCancel = () => {} } = this.props;
    onCancel();
  }

  /**
   * 开启选择维度值模态框
   */
  @Bind()
  handleOpenModal() {
    this.fetchModalData();
    this.setState({
      modalVisible: true,
    });
  }

  /**
   * 关闭选择维度值模态框
   */
  @Bind()
  handleCloseModal() {
    this.dimensionValueRef.state.addRows = [];
    this.setState({
      modalVisible: false,
    });
  }

  /**
   * 查询维度值模态框列表
   * @param {object} queryData - 分页参数
   */
  @Bind()
  fetchModalData(queryData = {}) {
    const { fetchValueModalData = () => {}, currentLine = {} } = this.props;
    fetchValueModalData({ ...queryData, lovId: currentLine.lineValueId });
  }

  @Bind()
  handleBindRef(ref = {}) {
    this.dimensionValueRef = ref;
  }

  /**
   * 创建维度值
   * @param {array[object]} addRows - 选中的维度值行
   */
  @Bind()
  handleCreateDimensionValue(addRows) {
    const { currentLine = {}, createAssignedValue = () => {} } = this.props;
    const { groupLineId, tenantId } = currentLine;
    const nextAddRows = [];
    addRows.forEach((item) => {
      nextAddRows.push({
        dtlValueId: item.lovValueId,
        tenantId,
      });
    });
    const payload = {
      groupLineId,
      nextAddRows,
    };
    createAssignedValue(payload).then((res = {}) => {
      if (!isEmpty(res)) {
        this.handleCloseModal();
        notification.success();
        this.dimensionValueRef.state.addRows = [];
        this.handleFetchValueList();
      }
    });
  }

  /**
   * 获取选中行
   * @param {array} selectedRowKeys 选中行的key值集合
   * @param {object} selectedRows 选中行集合
   */
  @Bind()
  handleRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRowKeys,
      selectedRows,
    });
  }

  /**
   * 删除分配值
   */
  @Bind()
  handleDeleteAssignedValue() {
    const { selectedRows } = this.state;
    const {
      deleteAssignedValue = () => {},
      currentLine: { groupLineId },
    } = this.props;
    const payload = {
      groupLineId,
      dataGroupDtlList: selectedRows,
    };
    const that = this;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk() {
        deleteAssignedValue(payload).then((res = {}) => {
          if (res) {
            notification.success();
            that.setState(
              {
                selectedRowKeys: [],
                selectedRows: {},
              },
              () => {
                that.handleFetchValueList();
              }
            );
          }
        });
      },
    });
  }

  render() {
    const {
      visible,
      match,
      currentLine = {},
      valueDataSource,
      valuePagination,
      fetchValueModalLoading,
      fetchAssignedValueLoading,
      createAssignedValueLoading,
      assignedValueList,
      assignedValuePagination,
    } = this.props;
    const { modalVisible, selectedRowKeys } = this.state;
    const formProps = { currentLine };
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const tableProps = {
      onChange: this.handleFetchValueList,
      dataSource: assignedValueList,
      pagination: assignedValuePagination,
      loading: fetchAssignedValueLoading,
      rowSelection,
    };
    const columns = [
      {
        title: intl.get('hpfm.dataGroup.model.dataGroup.dimensionValue').d('维度值'),
        dataIndex: 'value',
      },
      {
        title: intl.get('hpfm.dataGroup.model.dataGroup.dimensionMeaning').d('维度值含义'),
        dataIndex: 'meaning',
        width: 300,
      },
    ];
    const addModalOptions = {
      columns,
      confirmLoading: createAssignedValueLoading,
      loading: fetchValueModalLoading,
      title: intl.get('hpfm.dataGroup.view.message.modal.value.choose').d('选择维度值'),
      rowKey: 'lovValueId',
      isQuery: false,
      dataSource: valueDataSource,
      pagination: valuePagination,
      modalVisible,
      addData: this.handleCreateDimensionValue,
      onHideAddModal: this.handleCloseModal,
      fetchModalData: this.fetchModalData,
      onRef: this.handleBindRef,
    };

    return (
      <Modal
        destroyOnClose
        width={620}
        title={intl.get('hpfm.dataGroup.view.message.dataGroup.value').d('分配值')}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        onOk={this.handleSave}
        onCancel={this.handleCancel}
        footer={
          <Button onClick={this.handleCancel}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
      >
        <Spin spinning={false} wrapperClassName={DETAIL_DEFAULT_CLASSNAME}>
          <AssignedForm {...formProps} />
          <div className="table-operator">
            <ButtonPermission
              type="primary"
              permissionList={[
                {
                  code: `${match.path}.button.valueCreate`,
                  type: 'button',
                  meaning: '数据组详情分配值-新建',
                },
              ]}
              onClick={this.handleOpenModal}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
            <ButtonPermission
              disabled={isEmpty(selectedRowKeys)}
              permissionList={[
                {
                  code: `${match.path}.button.valueDelete`,
                  type: 'button',
                  meaning: '数据组详情分配值-删除',
                },
              ]}
              onClick={this.handleDeleteAssignedValue}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
          </div>
          <AssignedTable {...tableProps} />
        </Spin>
        <AddDataModal {...addModalOptions} />
      </Modal>
    );
  }
}
