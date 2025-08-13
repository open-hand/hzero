/**
 * seataMonitor - Seata监控
 * @date: 2020-03-25
 * @author: HQ <qi.he@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { withRouter } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import { tableDS } from '@/stores/seataMonitorDS';

@withRouter
export default class SeataMonitor extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS());
  }

  // 删除
  @Bind()
  async deleteApi(record) {
    await this.tableDS.delete(record);
    this.tableDS.query();
  }

  get columns() {
    return [
      {
        name: 'namespace',
      },
      {
        name: 'module',
      },
      {
        name: 'labelMap',
      },
      {
        name: 'timestamp',
      },
      {
        name: 'value',
      },
    ];
  }

  render() {
    return (
      <>
        <Header title={intl.get('hadm.seataMonitor.view.title').d('Seata监控')} />
        <Content>
          <Table dataSet={this.tableDS} columns={this.columns} queryFieldsLimit={3} />
        </Content>
      </>
    );
  }
}
