/**
 * @since 2019-10-16
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import formatterCollections from 'utils/intl/formatterCollections';
import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import { /* enableRender, */ operatorRender } from 'utils/renderer';

import { tableDS } from '@/stores/syncLogGroupDS';

@formatterCollections({
  code: ['hsrh.syncLog'],
})
export default class SearchConfig extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS());
  }

  componentDidMount() {
    this.tableDS.queryDataSet.addEventListener('update', this.changeData);
  }

  componentWillMount() {
    this.tableDS.queryDataSet.removeEventListener('update', this.changeData);
  }

  @Bind()
  changeData() {
    const indexCodeLov = this.tableDS.queryDataSet.current.get('indexCodeLov');
    if (!isEmpty(indexCodeLov)) {
      const { indexVersion } = indexCodeLov;
      this.tableDS.queryDataSet.current.set('indexVersion', indexVersion);
    } else {
      this.tableDS.queryDataSet.current.set('indexVersion', null);
    }
  }

  /**
   * 历史记录详情页
   * @param {*} record
   */
  @Bind()
  openDetailPage(data) {
    const { history } = this.props;
    const { syncLogId = '' } = data;
    history.push(`/hsrh/sync-log/detail/${syncLogId}`);
  }

  get columns() {
    return [
      {
        name: 'syncConfCode',
      },
      {
        name: 'syncStatusMeaning',
      },
      {
        name: 'indexCode',
      },
      {
        name: 'syncStartTime',
      },
      {
        name: 'syncEndTime',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 100,
        renderer: ({ record }) => {
          const actions = [];
          const { syncLogId } = record.toJSONData();
          actions.push(
            {
              ele: (
                <a
                  onClick={() => {
                    this.handleEdit(syncLogId);
                  }}
                >
                  {intl.get('hzero.common.button.detail').d('详情')}
                </a>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.detail').d('详情'),
            }
            // {
            //   ele: <a>{intl.get('hzero.common.button.info').d('消息内容')}</a>,
            //   key: 'edit',
            //   len: 5,
            //   title: intl.get('hzero.common.button.info').d('消息内容'),
            // }
          );
          return operatorRender(actions);
        },
        lock: 'right',
        align: 'center',
      },
    ];
  }

  /**
   * 编辑当前行数据
   */
  @Bind()
  handleEdit(syncLogId) {
    this.openDetailPage({ syncLogId });
  }

  render() {
    return (
      <React.Fragment>
        <Header title={intl.get('hsrh.syncLog.view.title.syncLog').d('同步日志')} />
        <Content>
          <Table dataSet={this.tableDS} columns={this.columns} />
        </Content>
      </React.Fragment>
    );
  }
}
