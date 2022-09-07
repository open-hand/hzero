/**
 * GroupsModal
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-14
 * @copyright 2019-06-14 © HAND
 */

import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Button, Col, Form, Input, Modal, Row } from 'hzero-ui';

import EditTable from 'components/EditTable';

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
export default class GroupsModal extends Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
      pagination: {},
      selectedRows: [],
      selectedRowKeys: [],
    };
  }

  componentDidMount() {
    this.handleSearch();
  }

  @Bind()
  handleSearch(pagination) {
    const { typeModeCode, form, receiverTypeId, tenantId } = this.props;
    let queryFunc;
    if (typeModeCode === 'USER_GROUP') {
      const { queryNoAssignUserGroupList } = this.props;
      queryFunc = queryNoAssignUserGroupList;
    }
    if (typeModeCode === 'UNIT') {
      const { queryNoAssignUnitList } = this.props;
      queryFunc = queryNoAssignUnitList;
    }
    if (queryFunc) {
      queryFunc({
        id: receiverTypeId,
        query: {
          tenantId,
          ...form.getFieldsValue(),
          ...pagination,
        },
      }).then(res => {
        if (res) {
          this.setState({
            dataSource: res.content,
            pagination: createPagination(res),
            selectedRows: [],
            selectedRowKeys: [],
          });
        }
      });
    }
  }

  @Bind()
  handleSearchBtnClick() {
    this.handleSearch();
  }

  @Bind()
  handleResetBtnClick() {
    const { form } = this.props;
    form.resetFields();
  }

  // Table
  getColumns() {
    const { typeModeCode } = this.props;
    if (typeModeCode === 'USER_GROUP') {
      return [
        {
          title: intl.get('hmsg.receiverType.model.receiverType.userGroupCode').d('用户组编码'),
          dataIndex: 'groupCode',
          width: 200,
        },
        {
          title: intl.get('hmsg.receiverType.model.receiverType.userGroupName').d('用户组名称'),
          dataIndex: 'groupName',
        },
      ];
    } else if (typeModeCode === 'UNIT') {
      return [
        {
          title: intl.get('hmsg.receiverType.model.receiverType.unitCode').d('组织编码'),
          dataIndex: 'unitCode',
          width: 200,
        },
        {
          title: intl.get('hmsg.receiverType.model.receiverType.unitName').d('组织名称'),
          dataIndex: 'unitName',
        },
      ];
    }
    return [];
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows = []) {
    const { typeModeCode } = this.props;
    if (typeModeCode === 'USER_GROUP') {
      this.setState({
        selectedRows,
        selectedRowKeys: selectedRows.map(record => record.userGroupId),
      });
    } else if (typeModeCode === 'UNIT') {
      this.setState({
        selectedRows,
        selectedRowKeys: selectedRows.map(record => record.unitId),
      });
    } else {
      this.setState({
        selectedRows: [],
        selectedRowKeys: [],
      });
    }
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleSearch({ page, sort });
  }

  // Modal
  @Bind()
  handleModalOk() {
    const { selectedRows = [] } = this.state;
    const { typeModeCode } = this.props;
    if (!(typeModeCode === 'USER_GROUP' || typeModeCode === 'UNIT')) {
      // 永远不会走到这个分支， 走到这个分支是 哪里出错了
      return;
    }
    if (selectedRows.length === 0) {
      this.handleModalCancel();
    } else {
      const { onOk, tenantId, receiverTypeId } = this.props;
      onOk({
        records: selectedRows.map(record => ({
          receiveTargetCode: typeModeCode, // USER_GROUP/UNIT
          receiveTargetId:
            typeModeCode === 'USER_GROUP'
              ? record.userGroupId
              : typeModeCode === 'UNIT'
              ? record.unitId
              : '', // 用户组ID或者组织部门ID
          receiveTargetTenantId: tenantId, // 用户组或者组织部门对应的租户ID
          receiverTypeId, // 接收组ID
        })),
        id: receiverTypeId,
      });
    }
  }

  @Bind()
  handleModalCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const {
      visible = false,
      assignListLoading = false,
      queryNoAssignUnitListLoading = false,
      queryNoAssignUserGroupListLoading = false,
      typeModeCode,
      form,
    } = this.props;
    const {
      dataSource = [],
      pagination = {},
      selectedRows = [],
      selectedRowKeys = [],
    } = this.state;
    const columns = this.getColumns();
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectionChange,
    };
    const isUserGroup = typeModeCode === 'USER_GROUP';
    const isUnit = typeModeCode === 'UNIT';
    const title = isUserGroup
      ? intl.get('hmsg.receiverType.view.title.userGroupAdd').d('新增用户组')
      : isUnit
      ? intl.get('hmsg.receiverType.view.title.unitAdd').d('新增组织')
      : '';
    return (
      <Modal
        destroyOnClose
        title={title}
        width={720}
        visible={visible}
        confirmLoading={assignListLoading}
        onOk={this.handleModalOk}
        onCancel={this.handleModalCancel}
        okButtonProps={{
          loading: assignListLoading,
          disabled:
            selectedRows.length === 0 ||
            queryNoAssignUserGroupListLoading ||
            queryNoAssignUnitListLoading,
        }}
      >
        <Form className={SEARCH_FORM_CLASSNAME}>
          <Row {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                key={isUserGroup ? 'groupCode' : isUnit ? 'unitCode' : 'unKnowCode'}
                label={
                  isUserGroup
                    ? intl.get('hmsg.receiverType.model.receiverType.userGroupCode').d('用户组编码')
                    : isUnit
                    ? intl.get('hmsg.receiverType.model.receiverType.unitCode').d('组织编码')
                    : ''
                }
              >
                {form.getFieldDecorator(
                  isUserGroup ? 'groupCode' : isUnit ? 'unitCode' : 'unKnowCode'
                )(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                key={isUserGroup ? 'groupName' : isUnit ? 'unitName' : 'unKnowName'}
                label={
                  isUserGroup
                    ? intl.get('hmsg.receiverType.model.receiverType.userGroupName').d('用户组名称')
                    : isUnit
                    ? intl.get('hmsg.receiverType.model.receiverType.unitName').d('组织名称')
                    : ''
                }
              >
                {form.getFieldDecorator(
                  isUserGroup ? 'groupName' : isUnit ? 'unitName' : 'unKnowName'
                )(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_3_LAYOUT} className={SEARCH_COL_CLASSNAME}>
              <Form.Item>
                <Button
                  htmlType="button"
                  onClick={this.handleResetBtnClick}
                  disabled={assignListLoading}
                >
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button
                  type="primary"
                  htmlType="button"
                  onClick={this.handleSearchBtnClick}
                  disabled={assignListLoading}
                  loading={queryNoAssignUserGroupListLoading || queryNoAssignUnitListLoading}
                >
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
        <EditTable
          bordered
          rowKey={isUserGroup ? 'userGroupId' : isUnit ? 'unitId' : 'id'}
          loading={
            assignListLoading || queryNoAssignUnitListLoading || queryNoAssignUserGroupListLoading
          }
          dataSource={dataSource}
          pagination={pagination}
          rowSelection={rowSelection}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={this.handleTableChange}
        />
      </Modal>
    );
  }
}
