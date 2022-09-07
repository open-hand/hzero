import React from 'react';
import { Form, Input, Modal, Tooltip, Icon, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { getEditTableData } from 'utils/utils';
import { FORM_FIELD_CLASSNAME } from 'utils/constants';

import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

@Form.create({ fieldNameProp: null })
export default class Filter extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedRows: [],
      selectedRowKeys: [],
    };
  }

  @Bind()
  handleCreate() {
    const { onCreate = (e) => e, currentFilter } = this.props;
    onCreate({
      _status: 'create',
      smsFilterId: uuid(),
      serverId: currentFilter.serverId,
      idd: '',
      phone: '',
    });
  }

  @Bind()
  handleDelete() {
    const { onDelete = (e) => e } = this.props;
    const { selectedRows, selectedRowKeys } = this.state;
    onDelete(selectedRows, selectedRowKeys);
  }

  @Bind()
  handlePagination(pagination) {
    const { onSearch = (e) => e } = this.props;
    onSearch({
      page: pagination,
    });
  }

  @Bind()
  handleOk() {
    const { onOk = (e) => e, dataSource } = this.props;
    const params = getEditTableData(dataSource, ['smsFilterId']);
    if (Array.isArray(params) && params.length > 0) {
      onOk(params);
    }
  }

  @Bind()
  handleSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRowKeys,
      selectedRows,
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
      iddList,
    } = this.props;
    const { selectedRows = [] } = this.state;

    const columns = [
      {
        width: '200px',
        title: intl.get('hmsg.smsConfig.filter.idd').d('国际冠码'),
        dataIndex: 'idd',
        render: (val, record) => {
          return ['create', 'update'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('idd', {
                initialValue: val,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hmsg.smsConfig.filter.idd').d('国际冠码'),
                    }),
                  },
                ],
              })(
                <Select className={FORM_FIELD_CLASSNAME}>
                  {iddList.map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          ) : (
            val
          );
        },
      },
      {
        title: (
          <>
            {intl.get('hmsg.smsConfig.view.title.mobileNumber').d('手机号')}
            <Tooltip
              title={intl
                .get('hmsg.smsConfig.view.title.userName.filterType')
                .d('手机账户或短信签名')}
            >
              <Icon type="question-circle-o" style={{ marginLeft: 5 }} />
            </Tooltip>
          </>
        ),
        dataIndex: 'phone',
        render: (val, record) => {
          return ['create', 'update'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('phone', {
                initialValue: val,
              })(<Input trim />)}
            </Form.Item>
          ) : (
            val
          );
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
                      meaning: '手机账户-取消编辑',
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
                      meaning: '手机账户-编辑黑白名单',
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
        title={intl.get('hmsg.smsConfig.view.title.filter').d('设置黑白名单')}
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
                meaning: '手机账户-新建黑白名单',
              },
            ]}
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          {/* ------------------------------------------- */}
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.deleteFilter`,
                type: 'button',
                meaning: '手机账户-删除黑白名单',
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
          rowKey="smsFilterId"
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={this.handlePagination}
        />
      </Modal>
    );
  }
}
