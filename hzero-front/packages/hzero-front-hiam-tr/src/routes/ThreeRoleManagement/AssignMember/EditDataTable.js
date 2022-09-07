/**
 * EditDataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-14
 * @copyright 2019 © HAND
 */
import React from 'react';
import { Form } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';
import EditTable from 'components/EditTable';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';

const rowKey = '_mockId';

export default class EditDataTable extends React.Component {
  state = {};

  @Bind()
  handleAddBtnClick() {
    const { onTableAdd } = this.props;
    onTableAdd();
  }

  @Bind()
  async handleDeleteBtnClick() {
    const { onTableDelete } = this.props;
    const { selectedRows = [] } = this.state;
    await onTableDelete(selectedRows);
  }

  // Table
  getColumns() {
    const { role = {}, tenantRoleLevel } = this.props;
    const { id: roleId } = role;
    return [
      {
        dataIndex: 'realName',
        title: intl.get('hiam.roleManagement.model.roleManagement.userLoginName').d('用户名'),
        render: (realName, record) => {
          const { _status } = record;
          if (_status === 'create') {
            const { $form: form } = record;
            return (
              <Form.Item>
                {form.getFieldDecorator('id', {
                  initialValue: record.id,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hiam.roleManagement.model.roleManagement.userLoginName')
                          .d('用户名'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    allowClear={false}
                    queryParams={{ roleId }}
                    style={{ width: '100%' }}
                    code={tenantRoleLevel ? 'HIAM.USER.ORG' : 'HIAM.USER'}
                    textValue={realName}
                    onChange={(userId, userRecord) => {
                      this.handleUserChange(userId, userRecord, record);
                    }}
                  />
                )}
              </Form.Item>
            );
          } else {
            return realName;
          }
        },
      },
      !VERSION_IS_OP && {
        title: intl.get('hiam.roleManagement.model.roleManagement.tenant').d('所属租户'),
        dataIndex: 'tenantName',
        render: (tenantName, record) => {
          const { _status } = record;
          if (_status === 'create' || _status === 'update') {
            const { $form: form } = record;
            return form.getFieldDecorator('tenantName', {
              initialValue: tenantName,
            })(<>{form.getFieldValue('tenantName')}</>);
          } else {
            return tenantName;
          }
        },
      },
    ].filter(Boolean);
  }

  @Bind()
  handleUserChange(userId, userRecord, record) {
    const { $form: form } = record;
    form.setFieldsValue({
      // assignLevel: undefined,
      // assignLevelMeaning: undefined,
      // assignLevelValue: undefined,
      // assignLevelValueMeaning: undefined,
      tenantName: record.assignLevelValueMeaning,
    });
  }

  @Bind()
  handleRowSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRows,
      selectedRowKeys: selectedRows.map((record) => record[rowKey]),
    });
  }

  @Bind()
  getCheckboxProps(record) {
    if (record.admin) {
      return {
        disabled: true,
      };
    } else {
      return {};
    }
  }

  render() {
    const {
      dataSource = [],
      pagination,
      onChange,
      path,
      queryLoading = false,
      saveLoading = false,
      deleteLoading = false,
    } = this.props;
    const { selectedRowKeys = [] } = this.state;
    const columns = this.getColumns();
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectionChange,
      getCheckboxProps: this.getCheckboxProps,
    };
    return (
      <>
        <div className="table-operator">
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.addAssign`,
                type: 'button',
                meaning: '角色管理-新增',
              },
            ]}
            type="primary"
            onClick={this.handleAddBtnClick}
          >
            {intl.get('hzero.common.button.add').d('新增')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.deleteAssign`,
                type: 'button',
                meaning: '角色管理-删除',
              },
            ]}
            onClick={this.handleDeleteBtnClick}
            disabled={selectedRowKeys.length === 0 || saveLoading || queryLoading}
            loading={deleteLoading}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </div>
        <EditTable
          bordered
          rowKey={rowKey}
          dataSource={dataSource}
          pagination={pagination}
          loading={queryLoading}
          rowSelection={rowSelection}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={onChange}
        />
      </>
    );
  }
}
