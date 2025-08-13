import React, { Component } from 'react';
import { Button, Form, Input, InputNumber, Modal } from 'hzero-ui';
import { DatePicker } from 'choerodon-ui';
import classNames from 'classnames';
import moment from 'moment';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';
import Checkbox from 'components/Checkbox';
import EditTable from 'components/EditTable';
import { Content, Header } from 'components/Page';

import { getEditTableData } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';

import styles from './index.less';

/**
 * 期间维护Modal组件
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAddPeriod - 添加期间操作
 * @reactProps {Function} onCleanLine - 清除新增行操作
 * @reactProps {Function} onChangeFlag - 变更行编辑状态
 * @reactProps {Function} onSave - 保存操作
 * @reactProps {Function} onCancel - modal关闭
 * @reactProps {Array} dataSource - 表格数据源
 * @reactProps {Object} periodItem - 维护期间的会计期间对象
 * @return React.element
 */
export default class PeriodCreate extends Component {
  /**
   * 保存
   */
  @Bind()
  handleSavePeriod() {
    const { dataSource, onSave } = this.props;
    const params = getEditTableData(dataSource, ['periodId']);
    if (Array.isArray(params) && params.length !== 0) {
      const data = params.map((item) => {
        const { dateRange, ...others } = item;
        return {
          ...others,
          startDate: dateRange && dateRange[0].format(DEFAULT_DATE_FORMAT),
          endDate: dateRange && dateRange[1].format(DEFAULT_DATE_FORMAT),
        };
      });
      onSave(data);
    }
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      match,
      dataSource,
      // dateFormat,
      savePeriodLoading,
      fetchPeriodLoading,
      visible,
      onCancel,
      onChangeFlag,
      onCleanLine,
      onAddPeriod,
    } = this.props;
    const columns = [
      {
        title: intl.get('hpfm.period.model.period.periodYear').d('年'),
        dataIndex: 'periodYear',
        align: 'center',
        width: 100,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`periodYear`, {
                initialValue: val,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.period.model.period.periodYear').d('年'),
                    }),
                  },
                ],
              })(<InputNumber min={1} precision={0} max={9999} />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.period.model.period.periodName').d('期间'),
        dataIndex: 'periodName',
        width: 150,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`periodName`, {
                initialValue: val,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.period.model.period.periodName').d('期间'),
                    }),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input trim typeCase="upper" inputChinese={false} />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.period.view.message.range').d('期间范围'),
        dataIndex: 'dateRange',
        width: 250,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`dateRange`, {
                initialValue:
                  record._status === 'create'
                    ? []
                    : [moment(record.startDate), moment(record.endDate)],
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.period.view.message.range').d('期间范围'),
                    }),
                  },
                ],
              })(<DatePicker.RangePicker />)}
            </Form.Item>
          ) : (
            <DatePicker.RangePicker
              defaultValue={[moment(record.startDate || ''), moment(record.endDate || '')]}
              disabled
            />
          ),
      },
      {
        title: intl.get('hpfm.period.model.period.periodQuarter').d('季度'),
        dataIndex: 'periodQuarter',
        width: 100,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`periodQuarter`, {
                initialValue: val,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.period.model.period.periodQuarter').d('季度'),
                    }),
                  },
                ],
              })(<InputNumber min={1} max={4} />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hpfm.period.model.period.adjustPeriodFlag').d('是否调整期'),
        align: 'center',
        width: 100,
        dataIndex: 'adjustPeriodFlag',
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`adjustPeriodFlag`, {
                initialValue: val ? 1 : 0,
                valuePropName: 'checked',
              })(<Checkbox />)}
            </Form.Item>
          ) : (
            enableRender(val ? 1 : 0)
          ),
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        align: 'center',
        width: 100,
        dataIndex: 'enabledFlag',
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator(`enabledFlag`, {
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
        width: 100,
        align: 'center',
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
                      code: `${match.path}.button.maintainEdit`,
                      type: 'button',
                      meaning: '期间定义维护-编辑',
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
      <Modal
        maskClosable={false}
        width={1100}
        onCancel={onCancel}
        visible={visible}
        footer={
          <Button type="primary" onClick={onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        }
      >
        <Header title={intl.get('hpfm.period.view.message.maintain').d('期间维护')} />
        <Content>
          <div className="table-operator">
            <Button type="primary" onClick={this.handleSavePeriod} loading={savePeriodLoading}>
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
            <ButtonPermission
              permissionList={[
                {
                  code: `${match.path}.button.maintainCreate`,
                  type: 'button',
                  meaning: '期间定义维护-新建',
                },
              ]}
              onClick={onAddPeriod}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
          </div>
          <EditTable
            bordered
            rowKey="periodId"
            loading={fetchPeriodLoading}
            className={classNames(styles['hpfm-period-list'])}
            dataSource={dataSource}
            columns={columns}
            pagination={false}
            scroll={{ y: 400 }}
          />
        </Content>
      </Modal>
    );
  }
}
