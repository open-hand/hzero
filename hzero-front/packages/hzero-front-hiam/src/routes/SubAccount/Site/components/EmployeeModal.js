import React from 'react';
import { Modal, Table, Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
} from 'utils/constants';
import Lov from 'components/Lov';

/**
 * GroupModal-选择新的用户组 弹框
 * @reactProps {Boolean} visible 模态框是否显示
 * @reactProps {Boolean} dataSource 用户组的数据源
 * @reactProps {Function(selectedRows:Object[]):Promise|*} onSave 确认按钮的回调,接收选中的用户组,返回一个Promise对象或者任意值
 * @reactProps {Function} onCancel 取消按钮的回调
 */
@Form.create({ fieldNameProp: null })
export default class GroupModal extends React.Component {
  @Bind()
  handleReset() {
    const {
      form: { resetFields },
    } = this.props;
    resetFields();
  }

  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    form.validateFields((_, value) => {
      onSearch(value);
    });
  }

  @Bind()
  handleTableChange(pagination) {
    const { onTableChange, form } = this.props;
    form.validateFields((_, value) => {
      onTableChange({
        page: pagination,
        ...value,
      });
    });
  }

  render() {
    const { visible, dataSource, pagination, form, fetchEmployeeLoading } = this.props;
    const columns = [
      {
        title: intl.get('hiam.subAccount.model.employee.tenantName').d('租户名称'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hiam.subAccount.model.employee.employeeName').d('员工名称'),
        dataIndex: 'employeeName',
        width: 150,
      },
      {
        title: intl.get('hiam.subAccount.model.employee.employeeNum').d('员工编码'),
        dataIndex: 'employeeNum',
      },
    ];
    return (
      <Modal
        visible={visible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        onCancel={this.handleCancel}
        width={720}
        title={intl.get('hiam.subAccount.view.message.title.employee').d('查看员工')}
        footer={null}
      >
        <Form className="more-fields-search-form">
          <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
            {!isTenantRoleLevel() && (
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hiam.subAccount.model.tenantMenu.tenantId').d('租户名称')}
                >
                  {form.getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
                </Form.Item>
              </Col>
            )}
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.tenantMenu.employeeName').d('员工姓名')}
              >
                {form.getFieldDecorator('employeeName')(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.tenantMenu.employeeNum').d('员工编码')}
              >
                {form.getFieldDecorator('employeeNum')(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
              <Form.Item>
                <Button onClick={this.handleReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
        <Table
          bordered
          rowKey="employeeId"
          loading={fetchEmployeeLoading}
          dataSource={dataSource}
          pagination={pagination}
          columns={columns}
          style={{ marginTop: 14 }}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={this.handleTableChange}
        />
      </Modal>
    );
  }
}
