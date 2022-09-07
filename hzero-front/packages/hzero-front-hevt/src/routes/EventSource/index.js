/*
 * @Descripttion:
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-05-18 21:02:51
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { Component } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { withRouter } from 'react-router';
import intl from 'utils/intl';
import { enableRender } from 'utils/renderer';
import { Header, Content } from 'components/Page';
import { getResponse } from 'utils/utils';
import { getPublicKey } from 'services/api';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import { eventSourceDS } from '@/stores/EventSourceDS';
import EditModalRender from './EditModal';

@withRouter
@formatterCollections({ code: ['hevt.common', 'hevt.eventSource'] })
export default class ListPage extends Component {
  constructor(props) {
    super(props);
    this.eventSourceDS = new DataSet(eventSourceDS(true));
    this.publicKey = '';
  }

  async componentDidMount() {
    this.publicKey = getResponse(await getPublicKey());
  }

  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      { name: 'eventSourceCode', width: 200 },
      {
        name: 'eventSourceName',
        width: 200,
      },
      {
        name: 'eventSourceType',
        width: 100,
      },
      {
        name: 'serviceAddress',
      },
      {
        name: 'enabledFlag',
        width: 80,
        renderer: ({ value }) => enableRender(Number(value)),
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        renderer: ({ record }) => {
          return (
            <span className="action-link">
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.create`,
                    type: 'button',
                    meaning: '编辑',
                  },
                ]}
                onClick={() => EditModalRender(false, this.eventSourceDS, record, this.publicKey)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            </span>
          );
        },
        lock: 'right',
      },
    ];
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hevt.eventSource.view.title.eventSource').d('事件源定义')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.cunsumeConfig`,
                type: 'button',
                meaning: '新建',
              },
            ]}
            icon="add"
            onClick={() => EditModalRender(true, this.eventSourceDS, null, this.publicKey)}
            color="primary"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.eventSourceDS} queryFieldsLimit={3} columns={this.columns} />
        </Content>
      </>
    );
  }
}
