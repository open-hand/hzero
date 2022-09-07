import React, { PureComponent } from 'react';
import { Form, Input, Popconfirm } from 'hzero-ui';
import classnames from 'classnames';

import Checkbox from 'components/Checkbox';
import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';

import styles from '../index.less';

const FormItem = Form.Item;

export default class ListTable extends PureComponent {
  /**
   * 保存当前行
   * @param {Object} record - 当前行数据
   */
  @Bind()
  handleSave(record) {
    this.props.onSave(record);
  }

  /**
   * 编辑当前行
   * @param {Object} record - 当前行数据
   */
  @Bind()
  editRow(record) {
    this.props.onEdit(record);
  }

  /**
   * 删除当前行
   * @param {Object} record - 当前行数据
   */
  @Bind()
  deleteRow(record) {
    this.props.onDelete(record);
  }

  /**
   * 取消编辑当前行
   * @param {Object} record - 当前行数据
   */
  @Bind()
  cancelRow(record) {
    this.props.onCancel(record);
  }

  @Bind()
  onSelectUser(record) {
    this.props.onSelectUser(record);
  }

  render() {
    const {
      rowKey,
      dataSource,
      loading,
      pagination,
      onSearch,
      commonSourceCode,
      match,
    } = this.props;

    const columns = [
      {
        title: intl.get('hpfm.purchaseAgent.model.purchaseAgent.purchaseAgentCode').d('采购员编码'),
        width: 120,
        dataIndex: 'purchaseAgentCode',
        render: (value, record) => {
          if (record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('purchaseAgentCode', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.purchaseAgent.model.purchaseAgent.purchaseAgentCode')
                          .d('采购员编码'),
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
                  initialValue: record.purchaseAgentCode,
                })(<Input trim typeCase="upper" inputChinese={false} />)}
              </FormItem>
            );
          } else {
            return value;
          }
        },
      },
      {
        title: intl.get('hpfm.purchaseAgent.model.purchaseAgent.purchaseAgentName').d('采购员名称'),
        dataIndex: 'purchaseAgentName',
        render: (value, record) => {
          if (
            record.sourceCode === commonSourceCode &&
            (record._status === 'create' || record._status === 'update')
          ) {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('purchaseAgentName', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.purchaseAgent.model.purchaseAgent.purchaseAgentName')
                          .d('采购员名称'),
                      }),
                    },
                    {
                      max: 60,
                      message: intl.get('hzero.common.validation.max', {
                        max: 60,
                      }),
                    },
                  ],
                  initialValue: record.purchaseAgentName,
                })(<Input />)}
              </FormItem>
            );
          } else {
            return value;
          }
        },
      },
      {
        title: intl.get('hpfm.purchaseAgent.model.purchaseAgent.contactInfo').d('联系方式'),
        width: 120,
        dataIndex: 'contactInfo',
        render: (value, record) => {
          if (
            record.sourceCode === commonSourceCode &&
            (record._status === 'create' || record._status === 'update')
          ) {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('contactInfo', {
                  initialValue: record.contactInfo,
                })(<Input />)}
              </FormItem>
            );
          } else {
            return value;
          }
        },
      },
      {
        title: intl.get('hpfm.purchaseAgent.model.purchaseAgent.sourceCode').d('数据来源'),
        width: 100,
        dataIndex: 'sourceCode',
        render: (value, record) => (record._status === 'create' ? commonSourceCode : value),
      },
      {
        title: intl.get('hpfm.purchaseAgent.model.purchaseAgent.externalSystemCode').d('来源系统'),
        width: 100,
        dataIndex: 'externalSystemCode',
        render: (value, record) => (record._status === 'create' ? commonSourceCode : value),
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 80,
        dataIndex: 'enabledFlag',
        render: (value, record) => {
          if (record._status === 'create' || record._status === 'update') {
            const { getFieldDecorator } = record.$form;
            return (
              <FormItem>
                {getFieldDecorator('enabledFlag', {
                  initialValue: record.enabledFlag ? 1 : 0,
                })(<Checkbox />)}
              </FormItem>
            );
          } else {
            return enableRender(value);
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        fixed: 'right',
        render: (val, record) => {
          const operators = [
            record._status === 'update' && {
              key: 'cancel',
              ele: (
                <a onClick={() => this.cancelRow(record)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            },
            record._status !== 'create' &&
              record._status !== 'update' && {
                key: 'cancel',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.userId`,
                        type: 'button',
                        meaning: '采购员-指定用户',
                      },
                    ]}
                    onClick={() => this.onSelectUser(record)}
                  >
                    {intl.get('hpfm.purchaseAgent.model.purchaseAgent.userId').d('指定用户')}
                  </ButtonPermission>
                ),
                len: 4,
                title: intl.get('hpfm.purchaseAgent.model.purchaseAgent.userId').d('指定用户'),
              },
            record._status !== 'create' &&
              record._status !== 'update' && {
                key: 'cancel',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '采购员-编辑',
                      },
                    ]}
                    onClick={() => this.editRow(record)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
            record._status === 'create' && {
              key: 'cancel',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
                  onConfirm={() => this.deleteRow(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators.filter(Boolean));
        },
      },
    ];

    return (
      <EditTable
        bordered
        className={classnames(styles['db-list'])}
        loading={loading}
        rowKey={rowKey}
        scroll={{ x: tableScrollWidth(columns) }}
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        onChange={onSearch}
      />
    );
  }
}
