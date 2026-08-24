import React, { PureComponent } from 'react';
import { Form, Input } from 'hzero-ui';
import classNames from 'classnames';

import Checkbox from 'components/Checkbox';
import TLEditor from 'components/TLEditor';
import EditTable from 'components/EditTable';
import Lov from 'components/Lov';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { getCurrentOrganizationId, tableScrollWidth } from 'utils/utils';
import { CODE } from 'utils/regExp';

import styles from './index.less';

const prefix = 'hpfm.uom.model.uom';

/**
 * 计量单位数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onCleanLine - 清除行
 * @reactProps {Function} onEditLine - 编辑行
 * @reactProps {Function} onSearch - 分页查询
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class ListTable extends PureComponent {
  state = {
    tenantId: getCurrentOrganizationId(),
  };

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      dataSource,
      pagination,
      onSearch,
      onCleanLine,
      onEditLine,
      match,
    } = this.props;
    const { tenantId } = this.state;
    const columns = [
      {
        title: intl.get(`${prefix}.uomCode`).d('计量单位编码'),
        dataIndex: 'uomCode',
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('uomCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${prefix}.uomCode`).d('计量单位编码'),
                    }),
                  },
                  {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
                initialValue: val,
              })(<Input disabled={record._status !== 'create'} inputChinese={false} />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${prefix}.uomName`).d('计量单位名称'),
        dataIndex: 'uomName',
        width: 250,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('uomName', {
                initialValue: val,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${prefix}.uomName`).d('计量单位名称'),
                    }),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get(`${prefix}.uomName`).d('计量单位名称')}
                  field="uomName"
                  token={record._token}
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${prefix}.uomTypeName`).d('单位类型名称'),
        dataIndex: 'uomTypeCode',
        width: 120,
        render: (val, record) => {
          const { uomTypeName } = record;
          return ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('uomTypeCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${prefix}.uomTypeCode`).d('单位类型名称'),
                    }),
                  },
                ],
                initialValue: record.uomTypeCode,
              })(
                <Lov
                  code="HPFM.UOM_TYPE"
                  queryParams={{ tenantId }}
                  textValue={uomTypeName}
                  textField="uomTypeName"
                />
              )}
            </Form.Item>
          ) : (
            uomTypeName
          );
        },
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('enabledFlag', {
                initialValue: record.enabledFlag,
                valuePropName: 'checked',
              })(<Checkbox />)}
            </Form.Item>
          ) : (
            <span>{enableRender(val)}</span>
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 60,
        fixed: 'right',
        render: (val, record) => {
          const operators = [
            {
              key: 'clean',
              ele:
                // eslint-disable-next-line no-nested-ternary
                record._status === 'create' ? (
                  <a onClick={() => onCleanLine(record)}>
                    {intl.get('hzero.common.button.clean').d('清除')}
                  </a>
                ) : record._status === 'update' ? (
                  <a onClick={() => onEditLine(record, false)}>
                    {intl.get('hzero.common.button.cancel').d('取消')}
                  </a>
                ) : (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '计量单位定义-编辑',
                      },
                    ]}
                    onClick={() => onEditLine(record, true)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
              len: 2,
              title:
                // eslint-disable-next-line no-nested-ternary
                record._status === 'create'
                  ? intl.get('hzero.common.button.clean').d('清除')
                  : record._status === 'update'
                  ? intl.get('hzero.common.button.cancel').d('取消')
                  : intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    return (
      <EditTable
        bordered
        rowKey="uomId"
        loading={loading}
        className={classNames(styles['hpfm-uom-list'])}
        scroll={{ x: tableScrollWidth(columns) }}
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        onChange={(page) => onSearch(page)}
      />
    );
  }
}
