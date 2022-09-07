/*
 * module - 执行记录
 * @author: NJQ <jiangqi.nan@hand-china.com>
 * @date: 2019-10-23
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { getCurrentOrganizationId } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { routerRedux } from 'dva/router';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import ExecuteDS from '../stores/ExecuteDS';

@connect()
@formatterCollections({ code: ['hres.record', 'hres.common'] })
export default class ListPage extends Component {
  constructor(props) {
    super(props);
    this.executeDS = new DataSet({
      ...ExecuteDS(),
      queryParameter: {
        tenantId: getCurrentOrganizationId(),
      },
    });
  }

  /**
   * 表格行
   * @returns {*[]}
   */
  get columns() {
    return [
      { name: 'uuid', tooltip: 'overflow', width: 290 },
      { name: 'ruleCode', width: 180 },
      { name: 'ruleName', width: 120 },
      { name: 'inParameter', width: 150 },
      { name: 'outParameter', width: 150 }, // 暂时隐藏掉
      /* { name: 'variable' }, */ { name: 'status' },
      { name: 'message', width: 160 },
      { name: 'startDatetime', tooltip: 'overflow', width: 160 },
      { name: 'endDatetime', tooltip: 'overflow', width: 160 },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        command: ({ record }) => this.commands(record),
        width: 150,
        lock: 'right',
        align: 'center',
      },
    ];
  }

  /**
   * 行内操作按钮组
   */
  @Bind()
  commands(record) {
    return [
      <span className="action-link">
        <a onClick={() => this.gotoFlow(record)}>
          {intl.get('hres.record.model.record.process').d('流程')}
        </a>
        <a onClick={() => this.gotoDetail(record)}>
          {intl.get('hzero.common.button.detail').d('详情')}
        </a>
      </span>,
    ];
  }

  /**
   * 流程跳转
   */
  @Bind()
  gotoFlow(record) {
    const { dispatch } = this.props;
    const { uuid, ruleCode } = record.toData();
    const pathname = `/hres/execute/flow/${ruleCode}/${uuid}`;
    dispatch(
      routerRedux.push({
        pathname,
      })
    );
  }

  /**
   * 路由跳转
   */
  @Bind()
  gotoDetail(record) {
    const { dispatch } = this.props;
    const { uuid = undefined } = record.toData();
    const pathname = `/hres/execute/detail/${uuid}/code`;
    dispatch(
      routerRedux.push({
        pathname,
      })
    );
  }

  render() {
    return (
      <React.Fragment>
        <Header
          title={intl.get('hres.record.view.title.execution.execution.record').d('执行记录')}
        />
        <Content>
          <Table dataSet={this.executeDS} columns={this.columns} queryFieldsLimit={2} />
        </Content>
      </React.Fragment>
    );
  }
}
