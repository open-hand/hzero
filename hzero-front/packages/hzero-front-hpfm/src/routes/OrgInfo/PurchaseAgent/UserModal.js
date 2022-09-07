import React from 'react';
import { Button, Input, Form, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import EditTable from 'components/EditTable';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';

const FormItem = Form.Item;

export default class UserModal extends React.Component {
  state = {
    selectedRowKeys: [],
    selectRows: [],
    organizationId: getCurrentOrganizationId(),
  };

  @Bind()
  handleSelectTable(keys, rows) {
    this.setState({ selectedRowKeys: keys, selectRows: rows });
  }

  @Bind()
  handleCreate() {
    const { onCreate = e => e, selectUserRecord = {} } = this.props;
    const { purchaseAgentId } = selectUserRecord;
    onCreate({
      _status: 'create',
      realName: '',
      loginName: '',
      purchaseAgentId,
      userKey: uuid(),
      userId: '',
    });
  }

  @Bind()
  handleDelete() {
    const { onDelete = e => e } = this.props;
    const { selectRows, selectedRowKeys } = this.state;
    onDelete(selectRows, selectedRowKeys);
    this.setState({ selectedRowKeys: [] });
  }

  @Bind()
  handlePagination(pagination) {
    const { onChange = e => e } = this.props;
    onChange({ page: pagination });
  }

  @Bind()
  handleCancel() {
    const { onCancel = e => e } = this.props;
    this.setState({ selectRows: [], selectedRowKeys: [] });
    onCancel();
  }

  render() {
    const {
      userPagination = {},
      initLoading = false,
      saveLoading = false,
      dataSource = [],
      modalVisible = false,
      onCancel = e => e,
      onOk = e => e,
      match,
    } = this.props;
    const { selectedRowKeys, organizationId } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleSelectTable,
    };
    const createList = dataSource.filter(item => item._status === 'create');
    const columns = [
      {
        title: intl.get('hpfm.purchaseAgent.model.purchaseAgent.account').d('账户'),
        width: 200,
        dataIndex: 'loginName',
        render: (value, record) => {
          if (record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('loginName', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: '账户',
                      }),
                    },
                  ],
                  initialValue: record.loginName,
                })(
                  <Lov
                    code="HPFM.PURCHASE_AGENT.USER"
                    queryParams={{ organizationId }}
                    textValue={record.loginName}
                    onChange={(text, item) => {
                      record.$form.setFieldsValue({ realName: item.realName });
                      record.$form.getFieldDecorator('userId', { initialValue: item.id });
                      record.$form.getFieldDecorator('userKey', { initialValue: item.id });
                    }}
                  />
                )}
              </FormItem>
            );
          }
          return value;
        },
      },
      {
        title: intl.get('hpfm.purchaseAgent.model.purchaseAgent.describe').d('用户描述'),
        dataIndex: 'realName',
        render: (value, record) => {
          if (record._status === 'create' || record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('realName', {
                  initialValue: record.realName,
                })(<Input disabled />)}
              </FormItem>
            );
          }
          return value;
        },
      },
    ];
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        width={620}
        title={intl.get('hpfm.purchaseAgent.model.purchaseAgent.userId').d('指定用户')}
        visible={modalVisible}
        confirmLoading={saveLoading}
        onCancel={this.handleCancel}
        footer={[
          <Button key="cancel" onClick={onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
          <Button
            loading={saveLoading}
            type="primary"
            key="save"
            disabled={createList.length === 0}
            onClick={onOk}
          >
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
      >
        <div style={{ marginBottom: 12 }}>
          <ButtonPermission
            icon="plus"
            style={{ marginRight: 10 }}
            permissionList={[
              {
                code: `${match.path}.button.userCreate`,
                type: 'button',
                meaning: '采购员指定用户-新建',
              },
            ]}
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.userDelete`,
                type: 'button',
                meaning: '采购员指定用户-删除',
              },
            ]}
            onClick={this.handleDelete}
            icon="delete"
            disabled={selectedRowKeys.length === 0}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </div>
        <EditTable
          bordered
          rowSelection={rowSelection}
          loading={initLoading}
          rowKey="userId"
          columns={columns}
          dataSource={dataSource}
          pagination={userPagination}
          onChange={this.handlePagination}
        />
      </Modal>
    );
  }
}
