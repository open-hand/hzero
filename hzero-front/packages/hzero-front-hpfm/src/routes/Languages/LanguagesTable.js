import React from 'react';
import { Form, Icon, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { getEditTableData, tableScrollWidth } from 'utils/utils';
import { operatorRender } from 'utils/renderer';
import classNames from 'classnames';
import styles from './index.less';

export default class LanguagesTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  columns = [
    {
      title: intl.get('entity.lang.code').d('语言编码'),
      width: 200,
      dataIndex: 'code',
      render: (text, record) => {
        if (record._status) {
          const { $form: form } = record;
          return (
            <Form.Item>
              {form.getFieldDecorator(`code`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.lang.code').d('语言编码'),
                    }),
                  },
                ],
                initialValue: record.code,
              })(<Input disabled />)}
            </Form.Item>
          );
        }
        return text;
      },
    },
    {
      title: intl.get('entity.lang.name').d('语言名称'),
      dataIndex: 'name',
      render: (text, record) => {
        if (record._status) {
          const { $form: form } = record;
          return (
            <Form.Item>
              {form.getFieldDecorator(`name`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.lang.name').d('语言名称'),
                    }),
                  },
                  {
                    max: 10,
                    message: intl.get('hzero.common.validation.max', {
                      max: 10,
                    }),
                  },
                ],
                initialValue: record.name,
              })(<Input />)}
            </Form.Item>
          );
        }
        return text;
      },
    },
    {
      title: intl.get('entity.lang.description').d('语言描述'),
      dataIndex: 'description',
      render: (text, record) => {
        if (record._status) {
          const { $form: form } = record;
          return (
            <Form.Item>
              {form.getFieldDecorator(`description`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.lang.description').d('语言描述'),
                    }),
                  },
                  {
                    max: 42,
                    message: intl.get('hzero.common.validation.max', {
                      max: 42,
                    }),
                  },
                ],
                initialValue: record.description,
              })(<Input />)}
            </Form.Item>
          );
        }
        return text;
      },
    },
    {
      title: intl.get('hzero.common.button.action').d('操作'),
      key: 'operator',
      width: 110,
      render: (text, record) => {
        const { saving, onEdit, onCancel, match } = this.props;
        const operators = [];
        if (record._status) {
          if (saving) {
            operators.push({
              key: 'loading',
              ele: <Icon type="loading" />,
              len: 4,
            });
          } else {
            operators.push({
              key: 'save',
              ele: (
                <a key="save" onClick={() => this.handleSave(record)}>
                  {intl.get('hzero.common.button.save').d('保存')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.save').d('保存'),
            });
            operators.push({
              key: 'record',
              ele: (
                <a key="cancel" onClick={() => onCancel(record)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          }
        } else {
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${match.path}.button.edit`,
                    type: 'button',
                    meaning: '语言维护-编辑',
                  },
                ]}
                key="edit"
                onClick={() => onEdit(record)}
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

  @Bind()
  handleSave(record) {
    const validateDataSource = getEditTableData([record]);
    if (validateDataSource.length !== 0) {
      const { onSave } = this.props;
      onSave(validateDataSource[0]);
    }
  }

  render() {
    const { loading, saving, onChange, dataSource, pagination } = this.props;
    return (
      <EditTable
        bordered
        className={classNames(styles['db-list'])}
        columns={this.columns}
        scroll={{ x: tableScrollWidth(this.columns) }}
        pagination={pagination}
        dataSource={dataSource}
        onChange={onChange}
        loading={loading || saving}
      />
    );
  }
}
