import React, { Component, Fragment } from 'react';
import { Form, Input } from 'hzero-ui';
import classNames from 'classnames';

import EditTable from 'components/EditTable';
import TLEditor from 'components/TLEditor';
import Checkbox from 'components/Checkbox';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import { CODE_UPPER } from 'utils/regExp';

import styles from './index.less';

/**
 * 期间定义数据展示组件
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChangeFlag - 行编辑
 * @reactProps {Function} onCleanLine - 行清除操作
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Array} dataSource - table数据源
 * @reactProps {object} pagination - 分页器
 * @reactProps {object} [pagination.current] - 当前页码
 * @reactProps {object} [pagination.pageSize] - 分页大小
 * @reactProps {object} [pagination.total] - 数据总量
 * @reactProps {Function} createRule - 期间维护
 * @return React.element
 */
export default class ListTable extends Component {
  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      match,
      loading,
      dataSource,
      pagination,
      onChange,
      onCleanLine,
      onChangeFlag,
      onCreateRule,
    } = this.props;
    const columns = [
      {
        title: intl.get('hpfm.period.model.period.periodSetCode').d('会计期编码'),
        dataIndex: 'periodSetCode',
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('periodSetCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.period.model.period.periodSetCode').d('会计期编码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
                initialValue: val,
              })(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  disabled={!(record._status === 'create')}
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.period.model.period.periodSetName').d('会计期名称'),
        dataIndex: 'periodSetName',
        width: 250,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item style={{ display: 'inline-block', marginBottom: 0 }}>
              {record.$form.getFieldDecorator('periodSetName', {
                initialValue: val,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.period.model.period.periodSetName').d('会计期名称'),
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get('hpfm.period.model.period.periodSetName').d('会计期名称')}
                  field="periodSetName"
                  token={record._token}
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.period.model.period.periodTotalCount').d('期间总数'),
        dataIndex: 'periodTotalCount',
        width: 100,
      },
      {
        title: intl.get('hpfm.period.view.option.create').d('创建规则'),
        dataIndex: 'createRule',
        width: 110,
        render: (val, record) =>
          record._status !== 'create' && (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${match.path}.button.maintain`,
                  type: 'button',
                  meaning: '期间定义-期间维护',
                },
              ]}
              onClick={() => onCreateRule(record)}
            >
              {intl.get('hpfm.period.view.message.maintain').d('期间维护')}
            </ButtonPermission>
          ),
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('enabledFlag', {
                initialValue: val,
                valuePropName: 'checked',
              })(<Checkbox />)}
            </Form.Item>
          ) : (
            enableRender(val)
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        fixed: 'right',
        width: 60,
        render: (val, record) => {
          const operators = [];
          if (record._status === 'create') {
            operators.push({
              key: 'clean',
              ele: (
                <a style={{ cursor: 'pointer' }} onClick={() => onCleanLine(record)}>
                  {intl.get('hzero.common.button.clean').d('清除')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.clean').d('清除'),
            });
          } else if (record._status === 'update') {
            operators.push({
              key: 'cancel',
              ele: (
                <a onClick={() => onChangeFlag(record, false)} style={{ cursor: 'pointer' }}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          } else {
            operators.push({
              key: 'cancel',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '期间定义-编辑',
                    },
                  ]}
                  onClick={() => onChangeFlag(record, true)}
                  style={{ cursor: 'pointer' }}
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
    return (
      <Fragment>
        <EditTable
          bordered
          rowKey="periodSetId"
          loading={loading}
          className={classNames(styles['hpfm-period-list'])}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          pagination={pagination}
          onChange={page => onChange(page)}
        />
      </Fragment>
    );
  }
}
