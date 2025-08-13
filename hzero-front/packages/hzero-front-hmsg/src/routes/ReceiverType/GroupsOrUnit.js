/**
 * Groups
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-14
 * @copyright 2019-06-14 © HAND
 */

import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Table, Form, Input, Select } from 'hzero-ui';
import uuidv4 from 'uuid/v4';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { createPagination, tableScrollWidth, isTenantRoleLevel } from 'utils/utils';
import EditTable from 'components/EditTable';
import { EMAIL, PHONE } from 'utils/regExp';
import Lov from 'components/Lov';

import GroupsOrUnitModal from './GroupsOrUnitModal';

@Form.create({ fieldNameProp: null })
export default class GroupsOrUnit extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  componentDidMount() {
    this.handleSearch();
  }

  // 查询已经分配的用户
  handleSearch(pagination = {}) {
    const { queryAssignedList, receiverTypeId, updateState } = this.props;
    queryAssignedList({
      id: receiverTypeId,
      query: pagination,
    }).then((res) => {
      if (res) {
        this.setState({
          selectedRowKeys: [],
          selectedRows: [],
          dataSource: res.content,
          pagination: createPagination(res),
          cachePagination: pagination,
        });
        // 处理数据为EditTable需要的数据，并且带上两个_token
        const data = res.content.map((item) => {
          return {
            ...item.receiverDetail,
            receiverTypeLineId: item.receiverTypeLineId,
            _status: 'update',
            innerToken: item.receiverDetail && item.receiverDetail._token,
            outToken: item._token,
            receiveTargetId: item.receiveTargetId,
          };
        });
        updateState(data);
      }
    });
  }

  // 删除 已经分配的用户
  @Bind()
  delSelectedList() {
    const { removeReceiverTypeList, receiverTypeId } = this.props;
    const { selectedRows } = this.state;
    removeReceiverTypeList({
      id: receiverTypeId,
      records: selectedRows,
    }).then((res) => {
      if (res) {
        notification.success();
        const { cachePagination } = this.state;
        this.handleSearch(cachePagination);
      }
    });
  }

  // 删除 已经分配的外部用户
  @Bind()
  handleDeleteExt() {
    const {
      removeReceiverTypeList,
      receiverTypeId,
      updateState,
      dataSource = [],
      typeModeCode,
    } = this.props;
    const { selectedRows } = this.state;
    // 过滤删除数据中新建的数据
    const deleteData = selectedRows.filter(
      (item) =>
        !(item.receiverTypeLineId && item.receiverTypeLineId.toString().startsWith('create-'))
    );
    // 过滤后如果还存在数据，则调用删除接口
    if (deleteData && deleteData.length !== 0) {
      removeReceiverTypeList({
        id: receiverTypeId,
        records: deleteData.map((item) => {
          return {
            _token: item.outToken,
            receiverDetail: {
              accountNum: item.accountNum,
              accountTypeCode: item.accountTypeCode,
              description: item.description,
              receiverTypeId,
              _token: item.innerToken,
            },
            receiverTypeId,
            receiverTargetCode: typeModeCode,
            receiverTypeLineId: item.receiverTypeLineId,
            receiveTargetId: item.receiveTargetId,
          };
        }),
      }).then((res) => {
        if (res) {
          notification.success();
          const { cachePagination } = this.state;
          this.handleSearch(cachePagination);
        }
      });
    } else {
      selectedRows.forEach((item) => {
        // 过滤新建的数据
        this.filterData = dataSource.filter(
          (i) => i.receiverTypeLineId !== item.receiverTypeLineId
        );
      });
      // 删除后更新数据
      updateState(this.filterData);
    }
  }

  // 分配用户给用户组
  @Bind()
  assignList(payload) {
    const { assignListToReceiverType } = this.props;
    assignListToReceiverType(payload).then((res) => {
      if (res) {
        notification.success();
        const { cachePagination } = this.state;
        this.handleSearch(cachePagination);
        this.setState({
          visible: false,
        });
      }
    });
  }

  // Table
  getColumns() {
    return [
      {
        title: intl.get('hmsg.receiverType.model.receiverType.receiver').d('接收者'),
        dataIndex: 'receiveTargetName',
      },
    ];
  }

  getUserColumns() {
    return [
      {
        title: intl.get('hmsg.receiverType.model.receiverType.userName').d('用户名'),
        dataIndex: 'receiveTargetName',
      },
    ];
  }

  // 获取外部用户的columns
  getExtColumns() {
    const { extTypeList = [] } = this.props;
    return [
      {
        title: intl.get('hmsg.receiverType.model.receiverType.accountTypeMeaning').d('账户类型'),
        width: 120,
        dataIndex: 'accountTypeMeaning',
        render: (_, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`accountTypeCode`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hmsg.receiverType.model.receiverType.accountTypeMeaning')
                        .d('账户类型'),
                    }),
                  },
                ],
                initialValue: record.accountTypeCode,
              })(
                <Select allowClear>
                  {extTypeList.length &&
                    extTypeList.map(({ value, meaning }) => (
                      <Select.Option key={value} value={value}>
                        {meaning}
                      </Select.Option>
                    ))}
                </Select>
              )}
            </Form.Item>
          ) : (
            <span>{record.accountTypeMeaning}</span>
          ),
      },
      {
        title: intl.get('hmsg.receiverType.model.receiverType.accountNum').d('账户'),
        width: 220,
        dataIndex: 'accountNum',
        render: (_, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`accountNum`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmsg.receiverType.model.receiverType.accountNum').d('账户'),
                    }),
                  },
                  {
                    pattern:
                      record.$form.getFieldValue('accountTypeCode') === 'PHONE' ? PHONE : EMAIL,
                    message:
                      record.$form.getFieldValue('accountTypeCode') === 'PHONE'
                        ? intl.get('hzero.common.validation.phone').d('手机格式不正确')
                        : intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                  },
                ],
                initialValue: record.accountNum,
              })(<Input />)}
            </Form.Item>
          ) : (
            <span>{record.accountNum}</span>
          ),
      },
      {
        title: intl.get('hmsg.receiverType.model.receiverType.description').d('描述'),
        dataIndex: 'description',
        render: (_, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`description`, {
                initialValue: record.description,
              })(<Input />)}
            </Form.Item>
          ) : (
            <span>{record.description}</span>
          ),
      },
    ];
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    this.setState({
      selectedRows,
      selectedRowKeys: selectedRows.map((r) => r.receiverTypeLineId),
    });
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleSearch({ page, sort });
  }

  // Button
  @Bind()
  handleAddBtnClick() {
    this.setState({
      visible: true,
    });
  }

  @Bind()
  handleDelBtnClick() {
    this.delSelectedList();
  }

  // Modal
  @Bind()
  closeModal() {
    this.setState({
      visible: false,
    });
  }

  /**
   * 添加一行外部用户
   */
  @Bind()
  handleAddExt() {
    const { dataSource = [], updateState } = this.props;
    const item = {
      receiverDetail: {
        _status: 'create',
      },
      receiverTypeLineId: `create-${uuidv4()}`,
      _status: 'create',
    };
    updateState([item].concat(dataSource));
  }

  @Bind()
  handleLovOk(record) {
    const { receiverTypeId } = this.props;
    this.assignList({
      id: receiverTypeId,
      records: [
        {
          receiverTargetCode: 'USER',
          receiveTargetId: record.id,
          receiveTargetTenantId: record.organizationId,
          receiverTypeId,
        },
      ],
    });
  }

  render() {
    const {
      queryAssignedListLoading = false,
      assignListToReceiverTypeLoading = false,
      removeReceiverTypeListLoading = false,
      typeModeCode,
      receiverTypeId,
      tenantId,
      queryNoAssignUnitListLoading,
      queryNoAssignUnitList,
      queryNoAssignUserGroupListLoading,
      queryNoAssignUserGroupList,
      path,
      receiverType: { assignDataSource = [], assignPagination = {} },
    } = this.props;
    const { dataSource = [], selectedRowKeys = [], pagination = false, visible } = this.state;
    // eslint-disable-next-line no-nested-ternary
    const columns =
      // eslint-disable-next-line no-nested-ternary
      typeModeCode === 'EXT_USER'
        ? this.getExtColumns()
        : typeModeCode === 'USER'
        ? this.getUserColumns()
        : this.getColumns();
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectionChange,
    };
    return (
      <>
        <div className="table-operator">
          {typeModeCode === 'USER' && (
            <Lov
              htmlType="button"
              type="primary"
              isButton
              code={isTenantRoleLevel() ? 'HIAM.USER.ORG' : 'HIAM.USER'}
              onOk={this.handleLovOk}
            >
              {intl.get('hzero.common.button.add').d('新增')}
            </Lov>
          )}
          {typeModeCode !== 'USER' && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.add`,
                  type: 'button',
                  meaning: '接收组维护-新增用户组',
                },
              ]}
              htmlType="button"
              type="primary"
              onClick={typeModeCode === 'EXT_USER' ? this.handleAddExt : this.handleAddBtnClick}
              loading={queryAssignedListLoading}
              disabled={
                removeReceiverTypeListLoading ||
                queryAssignedListLoading ||
                assignListToReceiverTypeLoading
              }
            >
              {intl.get('hzero.common.button.add').d('新增')}
            </ButtonPermission>
          )}
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '接收组维护-删除用户组',
              },
            ]}
            htmlType="button"
            onClick={typeModeCode === 'EXT_USER' ? this.handleDeleteExt : this.handleDelBtnClick}
            loading={removeReceiverTypeListLoading}
            disabled={
              selectedRowKeys.length === 0 ||
              removeReceiverTypeListLoading ||
              queryAssignedListLoading ||
              assignListToReceiverTypeLoading
            }
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </div>
        {typeModeCode !== 'EXT_USER' && (
          <Table
            bordered
            rowKey="receiverTypeLineId"
            dataSource={dataSource}
            rowSelection={rowSelection}
            pagination={pagination}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            onChange={this.handleTableChange}
            loading={queryAssignedListLoading}
          />
        )}
        {typeModeCode === 'EXT_USER' && (
          <EditTable
            bordered
            rowKey="receiverTypeLineId"
            dataSource={assignDataSource}
            rowSelection={rowSelection}
            pagination={assignPagination}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            onChange={this.handleTableChange}
            loading={queryAssignedListLoading}
          />
        )}
        {visible && (
          <GroupsOrUnitModal
            visible={visible}
            onOk={this.assignList}
            onCancel={this.closeModal}
            assignListLoading={assignListToReceiverTypeLoading}
            typeModeCode={typeModeCode}
            receiverTypeId={receiverTypeId}
            tenantId={tenantId}
            queryNoAssignUnitListLoading={queryNoAssignUnitListLoading}
            queryNoAssignUnitList={queryNoAssignUnitList}
            queryNoAssignUserGroupListLoading={queryNoAssignUserGroupListLoading}
            queryNoAssignUserGroupList={queryNoAssignUserGroupList}
          />
        )}
      </>
    );
  }
}
