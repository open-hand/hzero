import React from 'react';
import { Form, InputNumber, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import { operatorRender, enableRender } from 'utils/renderer';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';

const FormItem = Form.Item;

export default class UserModal extends React.Component {
  state = {
    selectedRowKeys: [],
    selectRows: [],
  };

  @Bind()
  handleSelectTable(keys, rows) {
    this.setState({ selectedRowKeys: keys, selectRows: rows });
  }

  @Bind()
  handleDelete() {
    const { onDelete = e => e } = this.props;
    const { selectRows, selectedRowKeys } = this.state;
    onDelete(
      selectRows.filter(item => {
        const temp = item;
        delete temp._status;
        return temp;
      }),
      selectedRowKeys
    );
    this.setState({ selectedRowKeys: [] });
  }

  /**
   * 编辑当前行
   * @param {Object} record - 当前行数据
   */
  @Bind()
  editRow(record, flag) {
    const { onEdit = e => e } = this.props;
    onEdit(record, flag);
  }

  @Bind()
  handleCancel() {
    const { onCancel = e => e } = this.props;
    this.setState({ selectRows: [], selectedRowKeys: [] });
    onCancel();
  }

  render() {
    const {
      path,
      initLoading = false,
      saveLoading = false,
      deleteLoading = false,
      dataSource = [],
      modalVisible = false,
      onCancel = e => e,
      onOk = e => e,
    } = this.props;
    const { selectedRowKeys } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleSelectTable,
    };
    const updateList = dataSource.filter(item => item._status === 'update');
    const columns = [
      {
        title: intl.get('hsdr.jobGroup.model.address').d('执行器地址'),
        width: 160,
        dataIndex: 'address',
      },
      {
        title: intl.get('hsdr.jobGroup.model.weight').d('权重'),
        width: 120,
        dataIndex: 'weight',
        render: (value, record) => {
          if (record._status === 'create' || record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('weight', {
                  initialValue: value,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hsdr.jobGroup.model.weight').d('权重'),
                      }),
                    },
                  ],
                })(<InputNumber min={0} />)}
              </FormItem>
            );
          } else {
            return value;
          }
        },
      },
      {
        title: intl.get('hsdr.jobGroup.model.maxConcurrent').d('最大并发量'),
        width: 120,
        dataIndex: 'maxConcurrent',
        render: (value, record) => {
          if (record._status === 'create' || record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('maxConcurrent', {
                  initialValue: value,
                })(<InputNumber min={0} />)}
              </FormItem>
            );
          } else {
            return value;
          }
        },
      },
      {
        title: intl.get('hsdr.jobGroup.model.enabledFlag').d('状态'),
        width: 80,
        dataIndex: 'enabledFlag',
        render: (value, record) => {
          if (record._status === 'create' || record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('enabledFlag', {
                  initialValue: value,
                })(<Switch />)}
              </FormItem>
            );
          } else {
            return enableRender(value);
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
                <a onClick={() => this.editRow(record, false)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          }
          if (record._status !== 'create' && record._status !== 'update') {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.configEdit`,
                      type: 'button',
                      meaning: '执行器管理-配置编辑',
                    },
                  ]}
                  onClick={() => this.editRow(record, true)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          if (record._status === 'create') {
            operators.push({
              key: 'delete',
              ele: (
                <ButtonPermission onClick={() => this.deleteRow(record)}>
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          }
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ];
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        width={720}
        title={intl.get('hsdr.jobGroup.view.message.executor').d('配置')}
        visible={modalVisible}
        confirmLoading={saveLoading || deleteLoading}
        onCancel={this.handleCancel}
        footer={[
          <ButtonPermission key="cancel" onClick={onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </ButtonPermission>,
          <ButtonPermission
            loading={saveLoading || deleteLoading}
            type="primary"
            key="save"
            disabled={updateList.length === 0}
            onClick={onOk}
          >
            {intl.get('hzero.common.button.ok').d('确定')}
          </ButtonPermission>,
        ]}
      >
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.reset`,
                type: 'button',
                meaning: '执行器管理-配置重置',
              },
            ]}
            onClick={this.handleDelete}
            loading={deleteLoading}
            disabled={selectedRowKeys.length === 0 || saveLoading}
          >
            {intl.get('hzero.common.button.reset').d('重置')}
          </ButtonPermission>
        </div>
        <EditTable
          bordered
          rowSelection={rowSelection}
          loading={initLoading}
          rowKey="uuid"
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
        />
      </Modal>
    );
  }
}
