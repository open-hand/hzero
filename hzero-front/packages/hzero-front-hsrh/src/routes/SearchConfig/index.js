/**
 * @since 2019-10-16
 * @author WT <tao13.wang@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Table, Button } from 'choerodon-ui/pro';
import { Badge } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import formatterCollections from 'utils/intl/formatterCollections';
import { Header, Content } from 'components/Page';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

import { tableDS } from '@/stores/searchConfigGroupDS';

@formatterCollections({
  code: ['hsrh.searchConfig', 'hsrh.inquiryConfig'],
})
export default class SearchConfig extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS());
  }

  /**
   * 历史记录详情页
   * @param {*} record
   */
  @Bind()
  openDetailPage(data) {
    const { history } = this.props;
    const { type = '', indexId = 'indexId', enabledFlag = '' } = data;
    if (type === 'edit') {
      history.push(`/hsrh/search-config/${type}/${indexId}?enabledFlag=${enabledFlag}`);
    } else {
      history.push(`/hsrh/search-config/${type}/${indexId}`);
    }
  }

  get columns() {
    return [
      {
        name: 'indexCode',
        align: 'left',
      },
      {
        name: 'enabledFlag',
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
        name: 'createUser',
        align: 'left',
      },
      {
        name: 'creationDate',
        align: 'left',
      },
      {
        name: 'updateUser',
        align: 'left',
      },
      {
        name: 'lastUpdateDate',
        align: 'left',
      },
      {
        header: intl.get('hzero.common.table.column.option').d('操作'),
        width: 192,
        renderer: ({ record }) => {
          const actions = [];
          const { indexId, enabledFlag } = record.toJSONData();
          actions.push(
            {
              ele: (
                <a
                  onClick={() => {
                    this.handleEdit(indexId, enabledFlag);
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
                    this.handleCopy(indexId);
                  }}
                >
                  {intl.get('hzero.common.button.copy').d('复制')}
                </a>
              ),
              key: 'delete',
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            },
            {
              ele: (
                <a
                  onClick={() => {
                    this.rollBack(record, enabledFlag);
                  }}
                >
                  {enabledFlag === '1'
                    ? intl.get('hzero.common.status.Invalid').d('失效')
                    : intl.get('hzero.common.status.valid').d('有效')}
                </a>
              ),
              key: 'disable-enable',
              len: 2,
              title:
                enabledFlag === '1'
                  ? intl.get('hzero.common.status.Invalid').d('失效')
                  : intl.get('hzero.common.status.valid').d('有效'),
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
  handleEdit(indexId, enabledFlag) {
    this.openDetailPage({ type: 'edit', indexId, enabledFlag });
  }

  /**
   * 复制
   */
  @Bind()
  handleCopy(indexId) {
    this.openDetailPage({ type: 'copy', indexId });
  }

  /**
   *  回滚
   */
  @Bind()
  async rollBack(record, enabledFlag) {
    const tempEnabledFlag = enabledFlag === '1' ? '0' : '1';
    record.set('enabledFlag', tempEnabledFlag);
    await this.tableDS.submit().catch((err) => {
      if (err && err.message) {
        this.tableDS.query();
      }
    });
    await this.tableDS.query();
  }

  render() {
    return (
      <React.Fragment>
        <Header title={intl.get('hsrh.searchConfig.view.message.title.searchConfig').d('索引配置')}>
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
