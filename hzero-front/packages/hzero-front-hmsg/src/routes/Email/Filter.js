import React from 'react';
import { Form, Input, Modal, Tooltip, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { getEditTableData } from 'utils/utils';
import { operatorRender } from 'utils/renderer';

@Form.create({ fieldNameProp: null })
export default class AppSourceForm extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      selectedRows: [],
      selectedRowKeys: [],
    };
  }

  @Bind()
  handleOk() {
    const { onOk = (e) => e, dataSource } = this.props;
    const params = getEditTableData(dataSource, ['emailFilterId']);
    if (Array.isArray(params) && params.length > 0) {
      onOk(params);
    }
  }

  @Bind()
  handleSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRows,
      selectedRowKeys,
    });
  }

  @Bind()
  handleDelete() {
    const { onDelete = (e) => e } = this.props;
    const { selectedRows, selectedRowKeys } = this.state;
    onDelete(selectedRows, selectedRowKeys);
  }

  @Bind()
  handleCreate() {
    const { onCreate = (e) => e, currentFilter } = this.props;
    onCreate({
      _status: 'create',
      emailFilterId: uuid(),
      serverId: currentFilter.serverId,
      address: '',
    });
  }

  @Bind()
  handlePagination(pagination) {
    const { onSearch = (e) => e } = this.props;
    onSearch({
      page: pagination,
    });
  }

  render() {
    const {
      visible = false,
      loading = false,
      deleteLoading = false,
      fetchLoading = false,
      onCancel = (e) => e,
      onEdit = (e) => e,
      dataSource = [],
      pagination = {},
      path,
    } = this.props;
    const { selectedRows = [] } = this.state;
    const columns = [
      {
        title: (
          <>
            {intl.get('hmsg.email.model.email.name').d('邮箱')}
            <Tooltip
              title={intl
                .get('hmsg.email.model.email.userName.filterType')
                .d('邮箱账户或邮箱服务器')}
            >
              <Icon type="question-circle-o" style={{ marginLeft: 5 }} />
            </Tooltip>
          </>
        ),
        dataIndex: 'address',
        render: (val, record) => {
          if (record._status === 'create' || record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('address', {
                  initialValue: val,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hmsg.email.model.email.name').d('邮箱'),
                      }),
                    },
                  ],
                })(<Input trim />)}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        render: (val, record) => {
          const operators = [];
          if (record._status === 'update') {
            operators.push({
              key: 'cancel',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.cancelEdit`,
                      type: 'button',
                      meaning: '邮箱账户-取消编辑',
                    },
                  ]}
                  onClick={() => onEdit(record, false)}
                >
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          } else if (record._status !== 'create') {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.editFilter`,
                      type: 'button',
                      meaning: '邮箱账户-编辑黑白名单',
                    },
                  ]}
                  onClick={() => onEdit(record, true)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          return operatorRender(operators);
        },
      },
    ];
    const rowSelection = {
      onChange: this.handleSelectChange,
    };
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={intl.get('hmsg.email.view.message.title.filter').d('编辑黑白名单')}
        width="620px"
        visible={visible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <div
          style={{ display: 'flex', justifyContent: 'flex-end' }}
          className="table-list-operator"
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.createFilter`,
                type: 'button',
                meaning: '邮箱账户-新建黑白名单',
              },
            ]}
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.deleteFilter`,
                type: 'button',
                meaning: '邮箱账户-删除黑白名单',
              },
            ]}
            onClick={this.handleDelete}
            disabled={selectedRows.length === 0}
            loading={deleteLoading}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </div>
        <EditTable
          bordered
          loading={fetchLoading}
          rowSelection={rowSelection}
          rowKey="emailFilterId"
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={this.handlePagination}
        />
      </Modal>
    );
  }
}
