/**
 * @since 2019-10-16
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Table, Button } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import formatterCollections from 'utils/intl/formatterCollections';
import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

import { tableDS } from '@/stores/incrementSyncConfigGroupDS';

@formatterCollections({
  code: ['hsrh.incrementSyncConfig', 'hsrh.incrementSync', 'hsrh.syncConfig', 'hsrh.incSyncConfig'],
})
export default class SearchConfig extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS());
  }

  componentDidMount() {
    this.tableDS.queryDataSet.addEventListener('update', this.changeData);
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillMount() {
    this.tableDS.queryDataSet.removeEventListener('update', this.changeData);
  }

  @Bind()
  changeData() {
    const indexCodeSet = this.tableDS.queryDataSet.current.get('indexCodeSet');
    if (!isEmpty(indexCodeSet)) {
      const { indexVersion } = indexCodeSet;
      this.tableDS.queryDataSet.current.set('indexVersion', indexVersion);
    } else {
      this.tableDS.queryDataSet.current.set('indexVersion', null);
    }
  }

  /**
   * 详情页
   * @param {*} data
   */
  @Bind()
  openDetailPage(data) {
    // 跳转到当前行详情页
    const { history } = this.props;
    const { type = '', syncConfId = 'syncConfId' } = data;
    history.push(`/hsrh/increment-sync/${type}/${syncConfId}`);
  }

  get columns() {
    return [
      {
        name: 'syncConfCode',
      },
      {
        name: 'sourceFromTypeMeaning',
      },
      {
        name: 'dataSources',
      },
      {
        name: 'sourceFromDetailCode',
      },
      {
        name: 'remark',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 160,
        renderer: ({ record }) => {
          const actions = [];
          const { syncConfId } = record.toJSONData();
          actions.push(
            {
              ele: (
                <a
                  onClick={() => {
                    this.handleEdit(syncConfId);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              key: 'edit',
              len: 3,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            }
            // {
            //   ele: (
            //     <a
            //       onClick={() => {
            //         this.handleDelete(record);
            //       }}
            //     >
            //       {intl.get('hzero.common.button.delete').d('删除')}
            //     </a>
            //   ),
            //   key: 'delete',
            //   len: 2,
            //   title: intl.get('hzero.common.button.delete').d('删除'),
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
  handleEdit(syncConfId) {
    this.openDetailPage({ type: 'edit', syncConfId });
  }

  // /**
  //  * 删除行
  //  */
  // @Bind()
  // async handleDelete(record) {
  //   await this.tableDS.delete(record);
  // }

  render() {
    return (
      <React.Fragment>
        <Header
          title={intl.get('hsrh.incrementSyncConfig.view.title.incrementSyncCfg').d('索引同步配置')}
        >
          <Button
            color="primary"
            icon="add"
            onClick={() => this.openDetailPage({ type: 'create' })}
          >
            {intl.get(`hzero.common.button.create`).d('新建')}
          </Button>
        </Header>
        <Content>
          <Table dataSet={this.tableDS} columns={this.columns} />
        </Content>
      </React.Fragment>
    );
  }
}
