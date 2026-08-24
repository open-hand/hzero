/**
 * reportPerssion-detail - 报表平台/报表权限
 * @date: 2018-11-29
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Col, Form, Input, Modal, Popconfirm, Row, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
// import { enableRender } from 'utils/renderer';
import intl from 'utils/intl';
import { dateRender, operatorRender } from 'utils/renderer';
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

  /**
   * 权限删除
   */
  @Bind()
  handleDelete(record) {
    const { onDelete, onSearch, permissionsPagination } = this.props;
    if (onDelete) {
      onDelete(record).then((res) => {
        if (res) {
          notification.success();
          if (onSearch) {
            onSearch({
              page: permissionsPagination.current - 1,
              size: permissionsPagination.pageSize,
            });
          }
        }
      });
    }
  }

  @Bind()
  handleSave(params) {
    const { onSave, onSearch, permissionsPagination } = this.props;
    onSave(params).then((res) => {
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
  handlePermissionEdit(params) {
    const { onEdit, onSearch, permissionsPagination } = this.props;
    onEdit(params).then((res) => {
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
      currentReportData,
    } = this.props;
    return (
      <Form>
        <Row>
          <Col span={8}>
            <Form.Item
              label={intl.get('hrpt.common.report.reportCode').d('报表代码')}
              {...formLayout}
            >
              {getFieldDecorator('reportCode', {
                initialValue: currentReportData.reportCode,
              })(<Input disabled />)}
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              label={intl.get('hrpt.common.report.reportName').d('报表名称')}
              {...formLayout}
            >
              {getFieldDecorator('reportId', {
                initialValue: currentReportData.reportId,
              })(
                <Lov
                  textValue={currentReportData.reportName}
                  queryParams={{ enabledFlag: 1 }}
                  code="HRPT.REPORT"
                  disabled
                />
              )}
            </Form.Item>
          </Col>
          <Col span={8}>
            <Form.Item
              label={intl
                .get('hrpt.reportDefinition.model.reportDefinition.reportType')
                .d('报表类型')}
              {...formLayout}
            >
              {getFieldDecorator('reportTypeMeaning', {
                initialValue: currentReportData.reportTypeMeaning,
              })(<Input disabled />)}
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
      ? intl.get('hrpt.reportDefinition.view.message.permission.edit').d('编辑权限')
      : intl.get('hrpt.reportDefinition.view.message.permission.add').d('新增权限');
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
        width: 150,
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.roleId').d('角色'),
        dataIndex: 'roleName',
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.startDate').d('有效期从'),
        width: 150,
        dataIndex: 'startDate',
        render: dateRender,
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.endDate').d('有效期至'),
        width: 150,
        dataIndex: 'endDate',
        render: dateRender,
      },
      {
        title: intl.get('hrpt.reportDefinition.modal.reportDefinition.desc').d('权限说明'),
        dataIndex: 'remark',
        // width: 150,
      },
      // {
      //   title: intl.get('hzero.common.status').d('状态'),
      //   width: 100,
      //   align: 'center',
      //   dataIndex: 'enabledFlag',
      //   render: enableRender,
      // },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        width: 120,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.handleEdit(record)}>
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
                  onConfirm={() => this.handleDelete(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators);
        },
      },
    ].filter((col) => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    return (
      <Modal
        title={intl.get('hrpt.common.view.assignPermission').d('分配权限')}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        onOk={this.saveBtn}
        onCancel={onCancel}
        width={1000}
        destroyOnClose
        footer={[
          <Button key="detail" type="primary" onClick={onOk}>
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
      >
        <>
          {this.renderHeaderForm()}
          <Row type="flex">
            <Col span={2} />
            <Col span={22}>
              <div className="table-operator">
                <Button onClick={this.handleAdd}>
                  {intl.get('hzero.common.button.create').d('新建')}
                </Button>
              </div>
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
