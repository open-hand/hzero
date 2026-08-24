import React, { PureComponent } from 'react';
import { Form, Input, InputNumber } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import TLEditor from 'components/TLEditor';
import EditTable from 'components/EditTable';
import Checkbox from 'components/Checkbox';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { yesOrNoRender, operatorRender } from 'utils/renderer';
import { CODE_UPPER } from 'utils/regExp';

import styles from './index.less';

/**
 * 岗位维护-数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAddLine - 新增岗位
 * @reactProps {Function} onClearLine - 清除新增岗位
 * @reactProps {Function} onForbidLine - 禁用岗位
 * @reactProps {Function} onEnabledLine - 启用岗位
 * @reactProps {Function} activeLine - 编辑框激活
 * @reactProps {Function} showSubLine - 下级岗位展示
 * @reactProps {Function} gotoSubGrade - 岗位分配员工
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Array} expandedRowKeys - 展开行标识
 * @return React.element
 */
export default class DataTable extends PureComponent {
  /**
   * 分配员工
   * @param {Object} record  操作对象
   */
  gotoSubGrade(record) {
    this.props.gotoSubGrade(record);
  }

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
   * render
   * @returns React.element
   */
  render() {
    const {
      dataSource,
      expandedRowKeys,
      loading,
      onAddLine,
      onClearLine,
      onForbidLine,
      onEnabledLine,
      onEdit,
      match,
    } = this.props;
    const columns = [
      {
        title: intl.get('entity.position.code').d('岗位编码'),
        dataIndex: 'positionCode',
        width: 300,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`positionCode`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.position.code').d('岗位编码'),
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
        title: intl.get('entity.position.name').d('岗位名称'),
        dataIndex: 'positionName',
        width: 300,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`positionName`, {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.position.name').d('岗位名称'),
                    }),
                  },
                  {
                    max: 40,
                    message: intl.get('hzero.common.validation.max', { max: 40 }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get('entity.position.name').d('岗位名称')}
                  field="positionName"
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.common.model.common.orderSeq').d('排序号'),
        dataIndex: 'orderSeq',
        width: 130,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`orderSeq`, {
                initialValue: 1,
              })(<InputNumber min={1} precision={0} />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.position.model.position.supervisorFlag').d('主管岗位'),
        dataIndex: 'supervisorFlag',
        width: 100,
        render: (val, record) =>
          record._status === 'create' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('supervisorFlag', {
                initialValue: val,
                valuePropName: 'checked',
              })(<Checkbox />)}
            </Form.Item>
          ) : (
            yesOrNoRender(val)
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 300,
        render: (val, record) => {
          const operators = [];
          if (record._status === 'create') {
            operators.push({
              key: 'clean',
              ele: (
                <a onClick={() => onClearLine(record)}>
                  {intl.get('hzero.common.button.clean').d('清除')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.clean').d('清除'),
            });
          } else if (record.enabledFlag) {
            operators.push(
              {
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '部门分配岗位-编辑',
                      },
                    ]}
                    onClick={() => onEdit(record)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'addChildren',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.addChildren`,
                        type: 'button',
                        meaning: '部门分配岗位-新增下级',
                      },
                    ]}
                    onClick={() => onAddLine(record)}
                  >
                    {intl.get('hzero.common.button.addChildren').d('新增下级')}
                  </ButtonPermission>
                ),
                len: 4,
                title: intl.get('hzero.common.button.addChildren').d('新增下级'),
              },
              {
                key: 'disable',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.disable`,
                        type: 'button',
                        meaning: '部门分配岗位-禁用',
                      },
                    ]}
                    onClick={() => onForbidLine(record)}
                  >
                    {intl.get('hzero.common.button.disable').d('禁用')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.disable').d('禁用'),
              },
              {
                key: 'assign',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.assign`,
                        type: 'button',
                        meaning: '部门分配岗位-分配员工',
                      },
                    ]}
                    onClick={() => this.gotoSubGrade(record)}
                  >
                    {intl.get('hpfm.position.view.option.assign').d('分配员工')}
                  </ButtonPermission>
                ),
                len: 4,
                title: intl.get('hpfm.position.view.option.assign').d('分配员工'),
              }
            );
          } else {
            operators.push(
              {
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.edit`,
                        type: 'button',
                        meaning: '部门分配岗位-编辑',
                      },
                    ]}
                    onClick={() => onEdit(record)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'disable',
                ele: (
                  <a style={{ color: '#F04134' }}>
                    {intl.get('hzero.common.button.disable').d('禁用')}
                  </a>
                ),
                len: 2,
                title: intl.get('hzero.common.button.disable').d('禁用'),
              },
              {
                key: 'enable',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.enable`,
                        type: 'button',
                        meaning: '部门分配岗位-启用',
                      },
                    ]}
                    onClick={() => onEnabledLine(record)}
                  >
                    {intl.get('hzero.common.status.enable').d('启用')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.status.enable').d('启用'),
              },
              {
                key: 'assign',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.assign`,
                        type: 'button',
                        meaning: '部门分配岗位-分配员工',
                      },
                    ]}
                    onClick={() => this.gotoSubGrade(record)}
                  >
                    {intl.get('hpfm.position.view.option.assign').d('分配员工')}
                  </ButtonPermission>
                ),
                len: 4,
                title: intl.get('hpfm.position.view.option.assign').d('分配员工'),
              }
            );
          }
          return operatorRender(operators);
        },
      },
    ];
    return (
      <EditTable
        bordered
        className={styles['hpfm-hr-show']}
        rowKey="positionId"
        indentSize={24}
        loading={loading}
        columns={columns}
        dataSource={dataSource}
        pagination={false}
        onExpand={this.handleExpandRow}
        expandedRowKeys={expandedRowKeys}
      />
    );
  }
}
