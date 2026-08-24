/* eslint-disable no-nested-ternary */
import React, { Fragment, PureComponent } from 'react';
import { Form, Input, Modal, Popconfirm, Table } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import { isTenantRoleLevel } from 'utils/utils';
import intl from 'utils/intl';
import { CODE } from 'utils/regExp';
import { operatorRender } from 'utils/renderer';

const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

/**
 * 新建或编辑模态框数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAdd - 添加确定的回调函数
 * @reactProps {Function} onEdit - 编辑确定的回调函数
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {String} anchor - 模态框弹出方向
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  @Bind()
  onOk() {
    const { form, onAdd, isCreate, tableRecord, onEdit } = this.props;
    const { databaseId, objectVersionNumber, _token } = tableRecord;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        if (isCreate) {
          onAdd(values);
        } else {
          onEdit({ databaseId, objectVersionNumber, _token, ...values });
        }
      }
    });
  }

  /**
   * 选择租户
   *
   * @param {*} value
   * @memberof Drawer
   */
  @Bind()
  onSelectTenantOk(value) {
    const { onTenantOk } = this.props;
    onTenantOk(value);
  }

  /**
   * 删除租户
   *
   * @param {*} record
   * @memberof Drawer
   */
  @Bind()
  deleteTenantOk(record) {
    const { onDeleteTenant } = this.props;
    onDeleteTenant(record);
  }

  /**
   * Lov值改变时触发
   * @param {*} _
   * @param {object} data
   */
  @Bind()
  codeChange(_, data) {
    const { form } = this.props;
    form.setFieldsValue({
      description: data.description,
    });
  }

  render() {
    const {
      match,
      visible,
      onCancel,
      saving,
      anchor,
      tableRecord,
      isCreate,
      databaseId,
      loading,
      datasourceId,
      tenantData = {},
      tenantPagination,
      onChangeTenant,
    } = this.props;
    const { getFieldDecorator } = this.props.form;
    const columns = [
      {
        title: intl.get('hpfm.database.model.database.tenantNum').d('租户编码'),
        dataIndex: 'tenantNum',
        width: 150,
      },
      {
        title: intl.get('hpfm.database.model.database.tenantName').d('租户名称'),
        dataIndex: 'tenantName',
        // width: 200,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 100,
        align: 'center',
        render: (val, record) => {
          const operators = [
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => {
                    this.deleteTenantOk(record);
                  }}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.tenantDelete`,
                        type: 'button',
                        meaning: '数据库设置租户-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <Modal
        destroyOnClose
        width={620}
        title={
          isCreate
            ? intl.get('hpfm.database.view.message.create').d('新建数据库')
            : intl.get('hpfm.database.view.message.edit').d('编辑数据库')
        }
        visible={visible}
        onCancel={onCancel}
        onOk={this.onOk}
        confirmLoading={saving}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        okText={intl.get('hzero.common.button.save').d('保存')}
      >
        <Form>
          <FormItem
            label={intl.get('hpfm.database.model.database.databaseCode').d('数据库代码')}
            {...formLayout}
          >
            {getFieldDecorator('databaseCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.database.model.database.databaseCode').d('数据库代码'),
                  }),
                },
                {
                  pattern: CODE,
                  message: intl
                    .get('hzero.common.validation.code')
                    .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
                {
                  max: 150,
                  message: intl.get('hzero.common.validation.max', {
                    max: 150,
                  }),
                },
              ],
              initialValue: tableRecord ? tableRecord.databaseCode : '',
            })(<Input trim inputChinese={false} disabled={!isCreate} />)}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.database.model.database.databaseName').d('数据库名称')}
            {...formLayout}
          >
            {getFieldDecorator('databaseName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.database.model.database.dataSourceName').d('数据库描述'),
                  }),
                },
                {
                  max: 150,
                  message: intl.get('hzero.common.validation.max', {
                    max: 150,
                  }),
                },
              ],
              initialValue: tableRecord ? tableRecord.databaseName : '',
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.database.model.database.datasourceId').d('数据源代码')}
            {...formLayout}
          >
            {getFieldDecorator('datasourceId', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.database.model.database.datasourceId').d('数据源代码'),
                  }),
                },
              ],
              initialValue: tableRecord ? tableRecord.datasourceId : '',
            })(
              <Lov
                code="HPFM.DATABASE.DATASOURCE"
                textValue={tableRecord.datasourceCode}
                lovOptions={{ displayField: 'datasourceCode' }}
                onChange={this.codeChange}
                queryParams={{ enabledFlag: 1, dsPurposeCode: 'DT' }}
                disabled={!isCreate}
              />
            )}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.database.model.database.description').d('数据源描述')}
            {...formLayout}
          >
            {getFieldDecorator('description', {
              rules: [
                {
                  required: false,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.database.model.database.description').d('数据源描述'),
                  }),
                },
              ],
              initialValue: tableRecord ? tableRecord.description : '',
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.database.model.database.tablePrefix').d('表前缀')}
            {...formLayout}
          >
            {getFieldDecorator('tablePrefix', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.database.model.database.tablePrefix').d('表前缀'),
                  }),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
              initialValue: tableRecord ? tableRecord.tablePrefix : '',
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.database.model.database.publicFlag').d('公共库标识')}
            {...formLayout}
          >
            {getFieldDecorator('publicFlag', {
              initialValue: isUndefined(tableRecord.publicFlag) ? 1 : tableRecord.publicFlag,
            })(<Switch />)}
          </FormItem>
          <FormItem label={intl.get('hzero.common.status.enable').d('启用')} {...formLayout}>
            {getFieldDecorator('enabledFlag', {
              initialValue: isUndefined(tableRecord.enabledFlag) ? 1 : tableRecord.enabledFlag,
            })(<Switch />)}
          </FormItem>
        </Form>
        {isTenantRoleLevel() ? null : isUndefined(databaseId) ? null : (
          <Fragment>
            <div className="table-operator">
              <Lov
                isButton
                type="primary"
                icon="plus"
                permissionList={[
                  {
                    code: `${match.path}.button.tenantAdd`,
                    type: 'button',
                    meaning: '数据库设置租户-添加租户',
                  },
                ]}
                onOk={this.onSelectTenantOk}
                style={{ marginBottom: 18 }}
                code="HPFM.DATABASE.TENANT"
                queryParams={{ datasourceId, enabledFlag: 1 }}
              >
                {intl.get('hpfm.database.view.button.add').d('添加租户')}
              </Lov>
            </div>
            <Table
              bordered
              columns={columns}
              dataSource={tenantData.content}
              rowKey="databaseTenantId"
              pagination={tenantPagination}
              onChange={(page) => onChangeTenant(page)}
              loading={loading}
            />
          </Fragment>
        )}
      </Modal>
    );
  }
}
