/**
 * RuleTable - 阶梯规则表格
 * @date: 2019/9/4
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Button, InputNumber, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';

import EditTable from 'components/EditTable';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';

const promptCode = 'hchg.serviceCharge.model.serviceCharge';
const FormItem = Form.Item;
const tenantId = getCurrentOrganizationId();

/**
 * 阶梯规则表格
 * @extends {Component} - React.Component
 * @reactProps {boolean} isPublished - 计费组是否已发布
 * @reactProps {object} ruleDetail - 规则行详情
 * @reactProps {number} chargeGroupId - 计费组id
 * @reactProps {Object} currentChargeRule - 当前选中的计费组
 * @reactProps {boolean} isCreate - 是否是创建规则
 * @reactProps {Function} onUpdateList - 更新规则行
 * @return React.element
 */

export default class RuleTable extends Component {
  /**
   * 新建阶梯行
   */
  @Bind()
  handleCreateInterval() {
    const {
      ruleDetail = {},
      chargeGroupId,
      isCreate,
      currentChargeRule,
      onUpdateList = () => {},
    } = this.props;
    const { chargeRuleLineList = [], ...rest } = ruleDetail;
    const payload = {
      ruleDetail: {
        ...rest,
        chargeRuleLineList: [
          {
            chargeLineId: uuidv4(),
            rangeFrom: null,
            rangeTo: null,
            chargeGroupId,
            _status: 'create',
            chargeRuleId: isCreate ? null : currentChargeRule.chargeRuleId,
            tenantId,
          },
          ...chargeRuleLineList,
        ],
      },
    };
    onUpdateList(payload);
  }

  /**
   * 清除新增行数据
   * @param {Object} record - 待清除的数据对象
   */
  @Bind()
  handleCleanLine(record) {
    const { onUpdateList = () => {}, ruleDetail = {} } = this.props;
    const { chargeRuleLineList = [], ...rest } = ruleDetail;
    const newList = chargeRuleLineList.filter((item) => item.chargeLineId !== record.chargeLineId);
    const payload = {
      ruleDetail: {
        ...rest,
        chargeRuleLineList: newList,
      },
    };
    onUpdateList(payload);
  }

  /**
   * 删除行
   * @param {Object} record - 删除的行
   */
  @Bind()
  handleDeleteLine(record) {
    const { onUpdateList = () => {}, ruleDetail = {} } = this.props;
    const { chargeRuleLineList = [], ...rest } = ruleDetail;
    const newList = chargeRuleLineList.map((item) => {
      let nextItem = { ...item };
      if (item.chargeLineId === record.chargeLineId) {
        nextItem = {
          ...nextItem,
          _status: 'delete',
        };
      }
      return nextItem;
    });
    const payload = {
      ruleDetail: {
        ...rest,
        chargeRuleLineList: newList,
      },
    };
    onUpdateList(payload);
  }

  /**
   * 编辑行
   * @param {Object} record - 备选值行数据
   * @param {Boolean} flag - 编辑/取消标记
   */
  @Bind()
  handleEditLine(record, flag) {
    const { onUpdateList = () => {}, ruleDetail = {} } = this.props;
    const { chargeRuleLineList = [], ...rest } = ruleDetail;
    const newList = chargeRuleLineList.map((item) =>
      item.chargeLineId === record.chargeLineId ? { ...item, _status: flag ? 'update' : '' } : item
    );
    const payload = {
      ruleDetail: {
        ...rest,
        chargeRuleLineList: newList,
      },
    };
    onUpdateList(payload);
  }

  @Bind()
  renderCharge() {
    const { currentChargeMethod } = this.props;
    let otherProps = {};
    if (currentChargeMethod === 'DISCOUNT') {
      otherProps = {
        max: 1,
        min: 0,
        step: 0.01,
      };
    }
    return <InputNumber style={{ width: '100%' }} {...otherProps} />;
  }

  render() {
    const { loading = false, isPublished, ruleDetail = {} } = this.props;
    const { chargeRuleLineList = [] } = ruleDetail;
    const nextDataSource = chargeRuleLineList.filter((item) => item._status !== 'delete');
    const columns = [
      {
        title: intl.get(`${promptCode}.rangeFrom`).d('阶梯从'),
        dataIndex: 'rangeFrom',
        width: 150,
        render: (val, record) =>
          ['create', 'update'].includes(record._status) ? (
            <FormItem>
              {record.$form.getFieldDecorator('rangeFrom', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.rangeFrom`).d('阶梯从'),
                    }),
                  },
                ],
                initialValue: record.rangeFrom,
              })(<InputNumber style={{ width: '100%' }} min={0} />)}
            </FormItem>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${promptCode}.rangeTo`).d('阶梯至'),
        dataIndex: 'rangeTo',
        width: 150,
        render: (val, record) =>
          ['create', 'update'].includes(record._status) ? (
            <FormItem>
              {record.$form.getFieldDecorator('rangeTo', {
                // rules: [
                //   {
                //     required: true,
                //     message: intl.get('hzero.common.validation.notNull', {
                //       name: intl.get(`${promptCode}.rangeTo`).d('阶梯至'),
                //     }),
                //   },
                // ],
                initialValue: record.rangeTo,
              })(<InputNumber style={{ width: '100%' }} min={0} />)}
            </FormItem>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${promptCode}.charge`).d('计费'),
        dataIndex: 'charge',
        width: 150,
        render: (val, record) =>
          ['create', 'update'].includes(record._status) ? (
            <FormItem>
              {record.$form.getFieldDecorator('charge', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.charge`).d('计费'),
                    }),
                  },
                ],
                initialValue: record.charge,
              })(this.renderCharge())}
            </FormItem>
          ) : (
            val
          ),
      },
      !isPublished && {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 90,
        render: (val, record) => (
          <span className="action-link">
            {/* eslint-disable-next-line no-nested-ternary */}
            {record._status === 'create' ? (
              <a onClick={() => this.handleCleanLine(record)}>
                {intl.get('hzero.common.button.clean').d('清除')}
              </a>
            ) : record._status === 'update' ? (
              <a onClick={() => this.handleEditLine(record, false)}>
                {intl.get('hzero.common.button.cancel').d('取消')}
              </a>
            ) : (
              <>
                <a onClick={() => this.handleEditLine(record, true)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  placement="topRight"
                  onConfirm={() => this.handleDeleteLine(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              </>
            )}
          </span>
        ),
      },
    ].filter(Boolean);
    const editTableProps = {
      loading,
      columns,
      dataSource: nextDataSource,
      bordered: true,
      rowKey: 'chargeLineId',
    };
    return (
      <>
        <div
          className="table-operator"
          style={{ textAlign: 'right', display: isPublished ? 'none' : 'block' }}
        >
          <Button type="primary" onClick={this.handleCreateInterval}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </div>
        <EditTable {...editTableProps} />
      </>
    );
  }
}
