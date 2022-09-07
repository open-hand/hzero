/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/20
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 云账户配置列表页
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Content, Header } from 'components/Page';
import { operatorRender, enableRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { Button as ButtonPermission } from 'components/Permission';
import { cloudAccountListDS } from '@/stores/cloudAccountConfDS';

const prefix = 'hiot.cloudAccount';

@formatterCollections({ code: ['hiot.cloudAccount', 'hiot.common'] })
export default class CloudAccountConf extends Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet({
      data: {},
      ...cloudAccountListDS(),
    });
  }

  @Bind
  handleOperation(record, operation) {
    const { history } = this.props;
    const path =
      operation === 'new'
        ? '/hiot/cloud-account/config/action/new'
        : `/hiot/cloud-account/config/action/${operation}/${record.toData().configId}`;
    history.push(path);
  }

  @Bind()
  toConsumerPage(record) {
    const { history } = this.props;
    history.push(`/hiot/cloud-account/config/consumer-group/${record.get('configId')}`);
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const columns = [
      { name: 'configCode' },
      { name: 'configName' },
      { name: 'platform' },
      { name: 'endpoint' },
      { name: 'enabledFlag', align: 'center', renderer: ({ value }) => enableRender(value) },
      {
        name: 'operation',
        header: <span>{intl.get('hzero.common.button.action').d('操作')}</span>,
        align: 'center',
        lock: 'right',
        width: 140,
        renderer: ({ record }) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '云账号配置-编辑',
                    },
                  ]}
                  onClick={() => this.handleOperation(record, 'edit')}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (record.get('platform') === 'ALI') {
            operators.push({
              key: 'consumerGroups',
              ele: (
                <a onClick={() => this.toConsumerPage(record)}>
                  {intl.get(`${prefix}.view.new.consumerGroups`).d('消费者组')}
                </a>
              ),
              len: 4,
              title: intl.get(`${prefix}.view.new.consumerGroups`).d('消费者组'),
            });
          }
          return operatorRender(operators);
        },
      },
    ];

    return (
      <>
        <Header title={intl.get(`${prefix}.view.title.config`).d('云账号配置')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '云账号配置-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleOperation({}, 'new')}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.tableDS} queryFieldsLimit={3} columns={columns} />
        </Content>
      </>
    );
  }
}
