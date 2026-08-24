import React, { PureComponent, Fragment } from 'react';
import { Form, Input } from 'hzero-ui';
import classNames from 'classnames';
import { isNumber, sum } from 'lodash';

import Lov from 'components/Lov';
import Checkbox from 'components/Checkbox';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

import EditTable from 'components/EditTable';
import styles from './index.less';

const modelPrompt = 'hpfm.libraryPosition.model.lp';

/**
 * 库位信息展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 分页查询
 * @reactProps {Function} onDelete -  新增行删除
 * @reactProps {Function} onEditRow - 编辑行
 * @reactProps {Function} onCancel - 编辑行取消
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */
export default class ListTable extends PureComponent {
  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      dataSource,
      pagination,
      organizationId,
      onSearch,
      onEditRow,
      onDelete,
      onCancel,
      match,
    } = this.props;
    const columns = [
      {
        title: intl.get(`${modelPrompt}.locationCode`).d('库位编码'),
        width: 120,
        dataIndex: 'locationCode',
        render: (text, record) => {
          if (['update', 'create'].includes(record._status)) {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('locationCode', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get(`${modelPrompt}.locationCode`).d('库位编码'),
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
                  initialValue: record.locationCode,
                })(
                  <Input
                    typeCase="upper"
                    inputChinese={false}
                    disabled={!!record.isErp || record._status === 'update'}
                  />
                )}
              </Form.Item>
            );
          }
          return text;
        },
      },
      {
        title: intl.get(`${modelPrompt}.locationName`).d('库位名称'),
        width: 140,
        dataIndex: 'locationName',
        render: (text, record) => {
          if (['update', 'create'].includes(record._status)) {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('locationName', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get(`${modelPrompt}.locationName`).d('库位名称'),
                      }),
                    },
                    {
                      max: 60,
                      message: intl.get('hzero.common.validation.max', {
                        max: 60,
                      }),
                    },
                  ],
                  initialValue: record.locationName,
                })(<Input disabled={!!record.isErp} />)}
              </Form.Item>
            );
          }
          return text;
        },
      },
      {
        title: intl.get(`${modelPrompt}.inventoryName`).d('库房'),
        dataIndex: 'inventoryName',
        width: 140,
        render: (text, record) => {
          if (['update', 'create'].includes(record._status)) {
            const { getFieldDecorator, setFieldsValue } = record.$form;
            getFieldDecorator('inventoryId', {
              initialValue: record.inventoryId,
            });
            getFieldDecorator('invOrganizationName', {
              initialValue: record.invOrganizationName,
            });
            getFieldDecorator('ouName', { initialValue: record.ouName });
            getFieldDecorator('ouId', { initialValue: record.ouId });
            getFieldDecorator('organizationId', {
              initialValue: record.organizationId,
            });
            return (
              <Form.Item>
                {getFieldDecorator('inventoryName', {
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get(`${modelPrompt}.inventoryName`).d('库房'),
                      }),
                    },
                  ],
                  initialValue: record.inventoryName,
                })(
                  <Lov
                    code="HPFM.LOCATION.INVENTORY"
                    queryParams={{ tenantId: organizationId, enabledFlag: 1 }}
                    textValue={record.inventoryName || ''}
                    disabled={!!record.isErp}
                    onChange={(value, lovRecord) => {
                      const setFormData = {
                        invOrganizationName: lovRecord.invOrganizationName,
                        inventoryId: lovRecord.inventoryId,
                        ouName: lovRecord.ouName,
                        ouId: lovRecord.ouId,
                        organizationId: lovRecord.organizationId,
                      };
                      setFieldsValue(setFormData);
                    }}
                  />
                )}
              </Form.Item>
            );
          }
          return text;
        },
      },
      {
        title: intl.get(`${modelPrompt}.invOrganizationName`).d('库存组织'),
        dataIndex: 'invOrganizationName',
        width: 120,
        render: (text, record) => {
          if (['update', 'create'].includes(record._status)) {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('invOrganizationName', {
                  initialValue: record.invOrganizationName,
                })(<Input disabled />)}
              </Form.Item>
            );
          }
          return text;
        },
      },
      {
        title: intl.get(`${modelPrompt}.ouName`).d('业务实体'),
        dataIndex: 'ouName',
        width: 80,
        render: (text, record) => {
          if (['update', 'create'].includes(record._status)) {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('ouName', {
                  initialValue: record.ouName,
                })(<Input disabled />)}
              </Form.Item>
            );
          }
          return text;
        },
      },
      {
        title: intl.get(`${modelPrompt}.sourceCode`).d('数据来源'),
        width: 75,
        align: 'center',
        dataIndex: 'sourceCode',
      },
      {
        title: intl.get(`${modelPrompt}.externalSystemCode`).d('来源系统'),
        width: 75,
        align: 'center',
        dataIndex: 'externalSystemCode',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 75,
        align: 'center',
        dataIndex: 'enabledFlag',
        render: (text, record) => {
          if (['update', 'create'].includes(record._status)) {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('enabledFlag', {
                  initialValue: record.enabledFlag ? 1 : 0,
                })(<Checkbox />)}
              </Form.Item>
            );
          }
          return enableRender(text);
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        dataIndex: 'edit',
        width: 75,
        render: (text, record) => {
          const operators = [];
          if (record._status === 'create') {
            operators.push({
              key: 'delete',
              ele: (
                <a onClick={() => onDelete(record)}>
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
                <a onClick={() => onCancel(record)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
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
                      meaning: '库位-编辑',
                    },
                  ]}
                  onClick={() => onEditRow(record)}
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
    const scrollX = sum(columns.map(n => (isNumber(n.width) ? n.width : 0))) + 200;
    return (
      <Fragment>
        <EditTable
          bordered
          rowKey="locationId"
          scroll={{ x: scrollX }}
          className={classNames(styles['data-list'])}
          loading={loading}
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={onSearch}
        />
      </Fragment>
    );
  }
}
