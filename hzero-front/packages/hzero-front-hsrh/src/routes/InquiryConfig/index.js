/**
 * @since 2019-10-17
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Badge } from 'choerodon-ui';
import { DataSet, Table, Button } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import formatterCollections from 'utils/intl/formatterCollections';
import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

import { tableDS } from '@/stores/inquiryConfigGroupDS';

@formatterCollections({
  code: ['hsrh.inquiryConfig', 'hsrh.incSyncConfig'],
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
    const indexCodeSet = this.tableDS.queryDataSet.current.get('indexCodeSet');
    if (!isEmpty(indexCodeSet)) {
      const { indexVersion } = indexCodeSet;
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
    // 跳转到当前行详情页
    const { history } = this.props;
    const { type = '', configId = 'configId' } = data;
    history.push(`/hsrh/inquiry-config/${type}/${configId}`);
  }

  // /**
  //  * 复制
  //  */
  // @Bind()
  // handleCopy(indexId) {
  //   this.openDetailPage({ type: 'copy', configId: indexId });
  // }

  get columns() {
    return [
      {
        name: 'configCode',
        align: 'left',
      },
      {
        name: 'indexCode',
        align: 'left',
      },
      // {
      //   name: 'indexVersion',
      //   width: 80,
      //   align: 'left',
      // },
      {
        name: 'enabledFlag',
        width: 80,
        align: 'left',
        renderer: ({ value }) => {
          const statusMap = ['error', 'success'];
          return (
            <Badge
              status={statusMap[value]}
              text={
                value === '1'
                  ? intl.get('hzero.common.status.valid').d('有效')
                  : intl.get('hzero.common.status.Invalid').d('失效')
              }
            />
          );
        },
      },
      {
        name: 'remark',
        align: 'left',
      },
      {
        name: 'activeEndTime',
      },
      {
        name: 'activeStartTime',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 160,
        renderer: ({ record }) => {
          const actions = [];
          const { configId } = record.toJSONData();
          actions.push(
            {
              ele: (
                <a
                  onClick={() => {
                    this.handleEdit(configId);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              key: 'edit',
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              ele: (
                <a
                  onClick={() => {
                    this.handleDelete(record);
                  }}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </a>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
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
  handleEdit(configId) {
    this.openDetailPage({ type: 'edit', configId });
  }

  /**
   *  回滚
   */
  @Bind()
  async rollBack(record, enabledFlag) {
    const tempEnabledFlag = enabledFlag === '1' ? '0' : '1';
    record.set('enabledFlag', tempEnabledFlag);
    await this.tableDS.submit();
    await this.tableDS.query();
  }

  /**
   * 删除行
   */
  @Bind()
  async handleDelete(record) {
    await this.tableDS.delete(record);
  }

  render() {
    return (
      <React.Fragment>
        <Header title={intl.get('hsrh.inquiryConfig.view.message.inquiryConfig').d('查询配置')}>
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
