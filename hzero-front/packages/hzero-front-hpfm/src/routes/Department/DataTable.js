import React, { PureComponent } from 'react';
import { Form, Input, InputNumber, Checkbox } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import EditTable from 'components/EditTable';
import TLEditor from 'components/TLEditor';
import { Button as ButtonPermission } from 'components/Permission';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { operatorRender, yesOrNoRender } from 'utils/renderer';
import { tableScrollWidth, getCurrentOrganizationId } from 'utils/utils';

import styles from './index.less';

/**
 * 部门维护-数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} addLine - 新增部门
 * @reactProps {Function} clearLine - 清除新增部门
 * @reactProps {Function} forbidLine - 禁用部门
 * @reactProps {Function} enabledLine - 启用部门
 * @reactProps {Function} showSubLine - 下级部门展示
 * @reactProps {Function} gotoSubGrade - 部门分配岗位
 * @reactProps {Function} activeLine - 编辑框激活
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Array} expandedRowKeys - 展开行标识
 * @return React.element
 */
export default class DataTable extends PureComponent {
  /**
   * 点击'+',获取当前节点的下级节点
   * @param {Boolean} isExpand 展开标记
   * @param {Object} record  当前行
   */
  @Bind()
  handleExpandRow(isExpand, record) {
    this.props.onShowSubLine(isExpand, record);
  }

  /**
   * 分配岗位
   * @param {Object} record 操作对象
   */
  @Bind()
  gotoSubGrade(record) {
    this.props.gotoSubGrade(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      dataSource,
      loading,
      expandedRowKeys,
      onClearLine,
      onAddLine,
      onEnabledLine,
      onForbidLine,
      onEdit,
      match,
    } = this.props;
    const columns = [
      {
        title: intl.get('entity.department.code').d('部门编码'),
        dataIndex: 'unitCode',
        width: 300,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('unitCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.department.code').d('部门编码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', { max: 30 }),
                  },
                ],
              })(<Input trim typeCase="upper" inputChinese={false} />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.department.model.unit.enableBudgetFlag').d('是否启用预算'),
        dataIndex: 'enableBudgetFlag',
        width: 300,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('enableBudgetFlag')(
                <Checkbox unCheckedValue={0} checkedValue={1} />
              )}
            </Form.Item>
          ) : (
            yesOrNoRender(val)
          ),
      },
      {
        title: intl.get('hpfm.department.model.unit.costName').d('所属成本中心'),
        dataIndex: 'costName',
        width: 300,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('costCode')(
                <Lov
                  code="HPFM.COST_CENTER"
                  // lovOptions={{
                  //   displayField: 'costName',
                  //   valueField: 'costId',
                  // }}
                  textField="costName"
                  queryParams={{
                    tenantId: getCurrentOrganizationId(),
                  }}
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('entity.department.name').d('部门名称'),
        dataIndex: 'unitName',
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('unitName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.department.name').d('部门名称'),
                    }),
                  },
                  {
                    max: 40,
                    message: intl.get('hzero.common.validation.max', { max: 40 }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get('entity.department.name').d('部门名称')}
                  field="unitName"
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.department.model.department.quickIndex').d('快速索引'),
        dataIndex: 'quickIndex',
        width: 200,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('quickIndex', {
                initialValue: val,
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get('hpfm.department.model.department.quickIndex').d('快速索引')}
                  field="quickIndex"
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.department.model.department.phoneticize').d('拼音'),
        dataIndex: 'phoneticize',
        width: 120,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('phoneticize', {
                initialValue: val,
                rules: [
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(<Input inputChinese={false} />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.common.model.common.orderSeq').d('排序号'),
        dataIndex: 'orderSeq',
        width: 150,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('orderSeq', {
                initialValue: 1,
              })(<InputNumber min={1} precision={0} />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 200,
        render: (val, record) => {
          const actions = [];
          if (record._status === 'create') {
            actions.push({
              key: 'clean',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.clean`,
                      type: 'button',
                      meaning: '公司分配部门-清除',
                    },
                  ]}
                  onClick={() => onClearLine(record)}
                >
                  {intl.get('hzero.common.button.clean').d('清除')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.clean').d('清除'),
            });
          } else if (record.enabledFlag) {
            actions.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '公司分配部门-编辑',
                    },
                  ]}
                  onClick={() => onEdit(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
            actions.push({
              key: 'add-children',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.addChildren`,
                      type: 'button',
                      meaning: '公司分配部门-新增下级',
                    },
                  ]}
                  onClick={() => onAddLine(record)}
                >
                  {intl.get('hzero.common.button.addChildren').d('新增下级')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hzero.common.button.addChildren').d('新增下级'),
            });
            actions.push({
              key: 'disable',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.disable`,
                      type: 'button',
                      meaning: '公司分配部门-禁用',
                    },
                  ]}
                  onClick={() => onForbidLine(record)}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            });
            actions.push({
              key: 'assign-grade',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.assign`,
                      type: 'button',
                      meaning: '公司分配部门-分配岗位',
                    },
                  ]}
                  onClick={() => this.gotoSubGrade(record)}
                >
                  {intl.get('hpfm.department.view.option.assign').d('分配岗位')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hpfm.department.view.option.assign').d('分配岗位'),
            });
          } else {
            actions.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '公司分配部门-编辑',
                    },
                  ]}
                  onClick={() => onEdit(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
            actions.push({
              key: 'disable',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.disable`,
                      type: 'button',
                      meaning: '公司分配部门-禁用',
                    },
                  ]}
                  style={{ color: '#F04134' }}
                >
                  {intl.get('hzero.common.button.disable').d('禁用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.disable').d('禁用'),
            });
            actions.push({
              key: 'enable',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.enable`,
                      type: 'button',
                      meaning: '公司分配部门-启用',
                    },
                  ]}
                  onClick={() => onEnabledLine(record)}
                >
                  {intl.get('hzero.common.status.enable').d('启用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.status.enable').d('启用'),
            });
            actions.push({
              key: 'assign-grade',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.assign`,
                      type: 'button',
                      meaning: '公司分配部门-分配岗位',
                    },
                  ]}
                  onClick={() => this.gotoSubGrade(record)}
                >
                  {intl.get('hpfm.department.view.option.assign').d('分配岗位')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hpfm.department.view.option.assign').d('分配岗位'),
            });
          }
          return operatorRender(actions, record);
        },
      },
    ];
    return (
      <EditTable
        bordered
        rowKey="unitId"
        className={styles['hpfm-hr-show']}
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        onExpand={this.handleExpandRow}
        expandedRowKeys={expandedRowKeys}
        indentSize={24}
        pagination={false}
      />
    );
  }
}
