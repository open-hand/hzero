/**
 * ChargeRule - 计费规则
 * @date: 2020-2-18
 * @author: fengwanjun <wanjun.feng@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { DataSet, Table } from 'choerodon-ui/pro';
import React, { Component } from 'react';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { CHARGE_GROUP_STATUS_FIELDS } from '@/constants/CodeConstants';
import ChargeGroupRuleDS from '../../../stores/ChargeGroup/ChargeGroupRuleDS';

/**
 * 计费规则
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hitf.chargeGroup'] })
export default class ChargeRule extends Component {
  constructor(props) {
    super(props);

    let disabled = true;
    // 组件控制
    if (this.props.tableDS) {
      this.tableDS = this.props.tableDS;
      // 状态=新建 操作列按钮可点击
      disabled = CHARGE_GROUP_STATUS_FIELDS.NEW !== this.props.statusCode;
    } else {
      // 页面/Modal控制
      this.tableDS = new DataSet({
        ...ChargeGroupRuleDS(),
        autoQuery: false,
      });
    }

    this.state = {
      disabled, // 操作列按钮是否可点击
    };
  }

  componentDidMount() {
    const { tableDS, match } = this.props;
    if (!tableDS) {
      // 页面控制
      if (match && match.params) {
        // 设置查询参数
        this.tableDS.queryParameter.groupHeaderId = this.props.match.params.groupHeaderId;
      } else {
        // Modal控制
        // 设置查询参数
        this.tableDS.queryParameter.groupHeaderId = this.props.groupHeaderId;
      }
      this.tableDS.query();
    }
  }

  //  eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    this.setState({
      disabled: CHARGE_GROUP_STATUS_FIELDS.NEW !== nextProps.statusCode,
    });
  }

  render() {
    const { disabled } = this.state;
    const { match } = this.props;
    return (
      <>
        {match && match.params && (
          <Header
            title={intl
              .get('hitf.chargeGroup.model.chargeGroupHeader.chargeGroupRule')
              .d('计费规则')}
            backPath="/hitf/charge-group/list"
          />
        )}
        <Content>
          <Table
            dataSet={this.tableDS}
            disabled={disabled}
            columns={[
              {
                header: intl.get('hitf.chargeGroup.model.chargeGroupLine.sort').d('序号'),
                lock: 'left',
                width: 70,
                align: 'center',
                renderer: ({ record }) =>
                  (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
              },
              {
                name: 'chargeRuleObject',
                width: 240,
                editor: true,
              },
              {
                name: 'settlementPeriod',
                editor: true,
              },
              {
                name: 'paymentModel',
              },
              {
                name: 'startDate',
                width: 130,
              },
              {
                name: 'endDate',
                width: 130,
              },
              {
                name: 'remark',
                editor: true,
              },
              {
                header: intl.get('hzero.common.button.action').d('操作'),
                lock: 'right',
                align: 'center',
                width: 200,
                renderer: ({ record }) => (
                  <span className="action-link">
                    <a onClick={() => this.tableDS.delete(record)}>
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </a>
                  </span>
                ),
                hidden: disabled,
              },
            ]}
            queryBar="none"
            editMode={disabled ? 'inline' : 'cell'}
          />
        </Content>
      </>
    );
  }
}
