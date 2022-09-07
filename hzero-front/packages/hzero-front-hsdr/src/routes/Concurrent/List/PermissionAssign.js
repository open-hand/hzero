/**
 * PermissionAssign - 请求定义/权限分配
 * @date: 2019-2-14
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Col, Form, Input, Modal, Row, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import { dateRender, enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';

import Drawer from './Drawer';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

@Form.create({ fieldNameProp: null })
export default class PermissionAssign extends Component {
  /**
   * state初始化
   */
  state = {
    drawerVisible: false, // 模态框
    targetItem: {},
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const { onSearch } = this.props;
    if (onSearch) {
      onSearch({ page: 0, size: 10 });
    }
  }

  /**
   * 新增权限
   */
  @Bind()
  handleAdd() {
    this.setState({ drawerVisible: true, targetItem: {} });
  }

  /**
   * 编辑权限
   * @param {object} record
   */
  @Bind()
  handleEdit(record) {
    this.setState({ drawerVisible: true, targetItem: record });
  }

  // /**
  //  * 权限删除
  //  */
  // @Bind()
  // handleDelete(record) {
  //   const { onDelete, onSearch, permissionsPagination } = this.props;
  //   if (onDelete) {
  //     onDelete(record).then(res => {
  //       if (res) {
  //         notification.success();
  //         if (onSearch) {
  //           onSearch({
  //             page: permissionsPagination.current - 1,
  //             size: permissionsPagination.pageSize,
  //           });
  //         }
  //       }
  //     });
  //   }
  // }
  // 新增权限分配
  @Bind()
  handleSave(params) {
    const { onSave, onSearch, permissionsPagination } = this.props;
    onSave(params).then(res => {
      if (res) {
        notification.success();
        this.setState(
          {
            drawerVisible: false,
            targetItem: {},
          },
          () => {
            if (onSearch) {
              onSearch({
                page: permissionsPagination.current - 1,
                size: permissionsPagination.pageSize,
              });
            }
          }
        );
      }
    });
  }

  // 编辑权限
  @Bind()
  handlePermissionEdit(params) {
    const { onEdit, onSearch, permissionsPagination } = this.props;
    onEdit(params).then(res => {
      if (res) {
        notification.success();
        this.setState(
          {
            drawerVisible: false,
            targetItem: {},
          },
          () => {
            if (onSearch) {
              onSearch({
                page: permissionsPagination.current - 1,
                size: permissionsPagination.pageSize,
              });
            }
          }
        );
      }
    });
  }

  @Bind()
  handleChange(pagination) {
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
    };
    this.props.onSearch(params);
  }

  /**
   * 滑窗取消操作
   */
  @Bind()
  hiddenDrawer() {
    this.setState({
      drawerVisible: false,
      targetItem: {},
    });
  }

  renderHeaderForm() {
    const {
      form: { getFieldDecorator },
      currentConcurrentData,
      path,
    } = this.props;
    return (
      <Form>
        <Row>
          <Col span={8}>
            <Form.Item
              label={intl.get('hsdr.concurrent.model.concurrent.concCode').d('请求编码')}
              {...formLayout}
            >
              {getFieldDecorator('concCode', {
                initialValue: currentConcurrentData.concCode,
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              label={intl
                .get('hsdr.concPermission.model.concPermission.concPragramId')
                .d('请求名称')}
              {...formLayout}
            >
              {getFieldDecorator('concurrentId', {
                initialValue: currentConcurrentData.concName,
              })(<Input disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={8}>
            <Form.Item
              label={intl.get('hsdr.concPermission.view.message.title.permission').d('权限')}
              {...formLayout}
            >
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.createAssign`,
                    type: 'button',
                    meaning: '请求定义-新建权限',
                  },
                ]}
                onClick={this.handleAdd}
                icon="plus"
              >
                {intl.get('hzero.common.button.create').d('新建')}
              </ButtonPermission>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      visible,
      path,
      currentTenantId,
      tenantRoleLevel,
      onOk,
      onCancel,
      permissionsList,
      permissionsPagination,
      fetchListLoading,
      fetchDetailLoading,
      createLoading,
      updateLoading,
      deleteLoading,
    } = this.props;
    const { targetItem = {}, drawerVisible = false } = this.state;
    const title = targetItem.permissionId
      ? intl.get('hsdr.concPermission.view.message.permission.edit').d('编辑权限')
      : intl.get('hsdr.concPermission.view.message.permission.add').d('新增权限');
    const drawerProps = {
      title,
      currentTenantId,
      tenantRoleLevel,
      fetchDetailLoading,
      createLoading,
      updateLoading,
      visible: drawerVisible,
      itemData: targetItem,
      onSave: this.handleSave,
      onEdit: this.handlePermissionEdit,
      onCancel: this.hiddenDrawer,
    };

    const columns = [
      {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hsdr.concPermission.model.concPermission.roleId').d('角色'),
        dataIndex: 'roleName',
      },
      {
        title: intl.get('hsdr.concPermission.model.concPermission.startDate').d('有效期从'),
        width: 150,
        dataIndex: 'startDate',
        render: dateRender,
      },
      {
        title: intl.get('hsdr.concPermission.model.concPermission.endDate').d('有效期至'),
        width: 150,
        dataIndex: 'endDate',
        render: dateRender,
      },
      // {
      //   title: intl
      //     .get('hrpt.concPermission.model.concPermission.permissionDesc')
      //     .d('权限说明'),
      //   dataIndex: 'remark',
      //   // width: 150,
      // },
      {
        title: intl.get('hsdr.concPermission.model.concPermission.limitQuantity').d('限制次数'),
        dataIndex: 'limitQuantity',
        width: 100,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
        fixed: 'right',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        width: 85,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          operators.push({
            key: 'editAssign',
            ele: (
              <a
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.editAssign`,
                    type: 'button',
                    meaning: '请求定义-编辑权限',
                  },
                ]}
                onClick={() => this.handleEdit(record)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter(col => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    return (
      <Modal
        title={intl.get('hsdr.concPermission.modal.concPermission.assignPermission').d('分配权限')}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        onOk={this.saveBtn}
        onCancel={onCancel}
        width={1000}
        destroyOnClose
        footer={[
          <Button key="detail" type="primary" onClick={onOk}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>,
        ]}
      >
        <>
          {this.renderHeaderForm()}
          <Row type="flex">
            <Col span={2} />
            <Col span={22}>
              <Table
                bordered
                rowKey="permissionId"
                columns={columns}
                scroll={{ x: tableScrollWidth(columns) }}
                dataSource={permissionsList}
                loading={fetchListLoading || deleteLoading}
                pagination={permissionsPagination}
                onChange={this.handleChange}
              />
            </Col>
          </Row>
          {drawerVisible && <Drawer {...drawerProps} />}
        </>
      </Modal>
    );
  }
}
