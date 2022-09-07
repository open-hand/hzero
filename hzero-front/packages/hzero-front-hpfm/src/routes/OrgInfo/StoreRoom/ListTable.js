/*
 * ListTable - 库房表格信息
 * @date: 2018/08/07 14:49:19
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Form, Input } from 'hzero-ui';
import classnames from 'classnames';
import { Bind } from 'lodash-decorators';

import Checkbox from 'components/Checkbox';
import Lov from 'components/Lov';
import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { getCurrentOrganizationId, tableScrollWidth } from 'utils/utils';
import { enableRender } from 'utils/renderer';
import { CODE_UPPER } from 'utils/regExp';

import styles from '../index.less';

// function getNewParams(lovRecord, record, fields) {
//   const newRecord = { ...record };
//   fields.forEach(field=>{
//     newRecord[field] = lovRecord[field] === undefined ? record[field] : lovRecord[field];
//   });
//   return newRecord;
// }
/**
 * 库房数据表单显示
 * @extends {Component} - React.Component
 * @reactProps {Function} editRow 修改行
 * @reactProps {Function} deleteRow 删除行
 * @reactProps {Function} 取消行handleLovOnChange Lov发生改变的回调
 * @reactProps {Object} form 表单
 * @return React.element
 */
const FormItem = Form.Item;
const modelPrompt = 'hpfm.storeRoom.model.storeRoom';
export default class ListTable extends Component {
  /**
   * 修改行数据
   * @param {obj} record 当前行数据
   */
  @Bind()
  editRow(record, flag) {
    this.props.onEditLine(record, flag);
  }

  /**
   * 删除行数据
   * @param {Object} record
   */
  @Bind()
  deleteRow(record) {
    this.props.onDelete(record);
  }

  /**
   * @param {*} value 当前值
   * @param {*} lovRecord lov选中的行
   * @param {*} record 行的值
   */
  @Bind()
  handleLovOnChange(value, lovRecord, record) {
    record.$form.getFieldDecorator('ouId');
    record.$form.getFieldDecorator('tenantId');
    record.$form.setFieldsValue({
      ouId: lovRecord.ouId,
      ouName: lovRecord.ouName,
      tenantId: lovRecord.tenantId,
    });
  }

  render() {
    const { loading, dataSource, onSearch, pagination, commonSourceCode, match } = this.props;
    const columns = [
      {
        title: intl.get(`${modelPrompt}.inventoryCode`).d('库房编码'),
        width: 120,
        dataIndex: 'inventoryCode',
        render: (val, record) =>
          ['create', 'update'].includes(record._status) ? (
            <FormItem>
              {record.$form.getFieldDecorator(`inventoryCode`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${modelPrompt}.inventoryCode`).d('库房编码'),
                    }),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
                initialValue: record.inventoryCode,
              })(
                <Input
                  disabled={record.sourceCode !== commonSourceCode || record._status === 'update'}
                  typeCase="upper"
                  inputChinese={false}
                />
              )}
            </FormItem>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${modelPrompt}.inventoryName`).d('库房名称'),
        dataIndex: 'inventoryName',
        render: (val, record) =>
          ['create', 'update'].includes(record._status) ? (
            <FormItem>
              {record.$form.getFieldDecorator(`inventoryName`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${modelPrompt}.inventoryName`).d('库房名称'),
                    }),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
                initialValue: record.inventoryName,
              })(<Input disabled={record.sourceCode !== commonSourceCode} />)}
            </FormItem>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${modelPrompt}.invOrganizationName`).d('库存组织'),
        dataIndex: 'invOrganizationName',
        width: 200,
        render: (val, record) =>
          ['create', 'update'].includes(record._status) ? (
            <FormItem>
              {record.$form.getFieldDecorator(`organizationId`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${modelPrompt}.invOrganizationName`).d('库存组织'),
                    }),
                  },
                ],
                initialValue: record.organizationId,
              })(
                <Lov
                  code="HPFM.INV_ORG"
                  onChange={(value, lovRecord) => this.handleLovOnChange(value, lovRecord, record)}
                  textValue={record.invOrganizationName}
                  queryParams={{ organizationId: getCurrentOrganizationId(), enabledFlag: 1 }}
                  disabled={record.sourceCode !== commonSourceCode}
                />
              )}
            </FormItem>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${modelPrompt}.ouName`).d('业务实体'),
        dataIndex: 'ouName',
        width: 120,
        render: (val, record) =>
          ['create', 'update'].includes(record._status) ? (
            <FormItem>
              {record.$form.getFieldDecorator(`ouName`, {
                initialValue: record.ouName,
              })(<Input disabled />)}
            </FormItem>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${modelPrompt}.sourceCode`).d('数据来源'),
        dataIndex: 'sourceCode',
        align: 'center',
        width: 100,
      },
      {
        title: intl.get(`${modelPrompt}.externalSystemCode`).d('来源系统'),
        dataIndex: 'externalSystemCode',
        align: 'center',
        width: 100,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        align: 'center',
        width: 80,
        render: (val, record) =>
          ['create', 'update'].includes(record._status) ? (
            <FormItem>
              {record.$form.getFieldDecorator(`enabledFlag`, {
                initialValue: record.enabledFlag === 0 ? 0 : 1,
              })(<Checkbox />)}
            </FormItem>
          ) : (
            enableRender(val)
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 70,
        fixed: 'right',
        render: (val, record) => (
          <span className="action-link">
            {record._status === 'update' && (
              <a onClick={() => this.editRow(record, false)}>
                {intl.get('hzero.common.button.cancel').d('取消')}
              </a>
            )}
            {!(record._status === 'create') && !(record._status === 'update') && (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${match.path}.button.edit`,
                    type: 'button',
                    meaning: '库房-编辑',
                  },
                ]}
                onClick={() => this.editRow(record, true)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            )}
            {record._status === 'create' && (
              <a onClick={() => this.deleteRow(record)}>
                {intl.get('hzero.common.button.clean').d('清除')}
              </a>
            )}
          </span>
        ),
      },
    ];
    const editTableProps = {
      loading,
      columns,
      dataSource,
      pagination,
      bordered: true,
      rowKey: 'inventoryId',
      scroll: { x: tableScrollWidth(columns) },
      onChange: page => onSearch(page),
      className: classnames(styles['db-list']),
    };
    return <EditTable {...editTableProps} />;
  }
}
