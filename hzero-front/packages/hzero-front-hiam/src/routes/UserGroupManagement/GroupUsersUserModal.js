/**
 * GroupUsersUserModal
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-06
 * @copyright 2019-06-06 © HAND
 */

import React, { Component } from 'react';
import { Button, Col, Form, Input, Modal, Row, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { createPagination, tableScrollWidth } from 'utils/utils';
import {
  FORM_COL_3_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class GroupUsersUserModal extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    this.getCurrentRestGroupUsers();
  }

  getCurrentRestGroupUsers(query) {
    const { onSearch } = this.props;
    onSearch(query).then((res) => {
      if (res) {
        this.setState({
          selectedRowKeys: [],
          selectedRows: [],
          dataSource: res.content,
          pagination: createPagination(res),
        });
      }
    });
  }

  // Button
  @Bind()
  handleSearchBtnClick(e) {
    e.preventDefault();
    const { form } = this.props;
    this.getCurrentRestGroupUsers(form.getFieldsValue());
  }

  @Bind()
  handleResetBtnClick(e) {
    e.preventDefault();
    const { form } = this.props;
    form.resetFields();
  }

  // Table
  getColumns() {
    return [
      {
        title: intl.get('hiam.userGroupManagement.model.userGroup.loginName').d('用户账号'),
        dataIndex: 'loginName',
        width: 300,
      },
      {
        title: intl.get('hiam.userGroupManagement.model.userGroup.realName').d('用户名称'),
        dataIndex: 'realName',
        width: 300,
      },
    ];
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    this.setState({
      selectedRows,
      selectedRowKeys: selectedRows.map((r) => r.userId),
    });
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    const { form } = this.props;
    this.getCurrentRestGroupUsers({ page, sort, ...form.getFieldsValue() });
  }

  // Modal
  @Bind()
  handleModalOk() {
    const { selectedRows } = this.state;
    if (selectedRows.length > 0) {
      const { onOk } = this.props;
      onOk(
        selectedRows.map((r) => ({
          userId: r.userId,
        }))
      );
    } else {
      this.handleModalCancel();
    }
  }

  @Bind()
  handleModalCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const {
      form,
      visible = false,
      assignUsersToGroupLoading = false,
      getCurrentRestGroupUsersLoading = false,
    } = this.props;
    const { dataSource = [], selectedRowKeys = [], pagination = false } = this.state;
    const columns = this.getColumns();
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectionChange,
    };
    return (
      <Modal
        destroyOnClose
        width={720}
        title={intl.get('hiam.userGroupManagement.view.title.usersAdd').d('新增用户')}
        visible={visible}
        confirmLoading={assignUsersToGroupLoading}
        cancelButtonProps={{ disabled: assignUsersToGroupLoading }}
        onOk={this.handleModalOk}
        onCancel={this.handleModalCancel}
      >
        <Form className={SEARCH_FORM_CLASSNAME}>
          <Row {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.userGroupManagement.model.userGroup.loginName').d('用户账号')}
              >
                {form.getFieldDecorator('loginName')(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.userGroupManagement.model.userGroup.realName').d('用户名称')}
              >
                {form.getFieldDecorator('realName')(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT} className={SEARCH_COL_CLASSNAME}>
              <Form.Item>
                <Button htmlType="button" onClick={this.handleResetBtnClick}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button type="primary" htmlType="submit" onClick={this.handleSearchBtnClick}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
        <Table
          bordered
          rowKey="userId"
          loading={getCurrentRestGroupUsersLoading}
          dataSource={dataSource}
          rowSelection={rowSelection}
          pagination={pagination}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={this.handleTableChange}
        />
      </Modal>
    );
  }
}
