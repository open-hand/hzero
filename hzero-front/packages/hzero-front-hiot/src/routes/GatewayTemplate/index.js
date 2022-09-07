import React, { Component } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import notification from 'utils/notification';
import { yesOrNoRender, operatorRender } from 'utils/renderer';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { gatewayTemplateListDS, detailDS } from '@/stores/gatewayTemplateDS';

@formatterCollections({ code: ['hiot.common', 'hiot.gatewayTemplate'] })
export default class GatewayTemplate extends Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(gatewayTemplateListDS());
    this.detailDS = new DataSet(detailDS());
  }

  /**
   * 处理页面跳转
   * @param path 跳转路径
   */
  @Bind()
  pageJump(path) {
    const { history } = this.props;
    history.push(path);
  }

  @Bind()
  handleOperation(record, operation) {
    let path = '/hiot/gateway-temp/new/-1';
    if (operation !== 'new') {
      const {
        data: { thingModelId },
      } = record;
      path = `/hiot/gateway-temp/${operation}/${thingModelId}`;
    }
    this.pageJump(path);
  }

  // 删除
  @Bind()
  async handleDelete() {
    try {
      const { selected } = this.tableDS;
      if (isEmpty(selected)) {
        notification.warning({
          message: intl.get('hzero.common.validation.atLeastOneRecord').d('请至少选择一条数据'),
        });
        return false;
      }
      const res = await this.tableDS.delete(selected);
      if (res) {
        this.tableDS.query();
      }
    } catch (error) {
      //
    }
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const columns = [
      { name: 'thingModelCode', width: 150 },
      { name: 'thingModelName', width: 200 },
      { name: 'platformMeaning', width: 100 },
      { name: 'configName', width: 150 },
      { name: 'enabled', width: 80 },
      { name: 'description' },
      { name: 'consumerName' },
      {
        name: 'isReferred',
        width: 100,
        align: 'center',
        renderer: ({ value }) => yesOrNoRender(value),
      },
      {
        name: 'operation',
        width: 80,
        header: <span>{intl.get(`hzero.common.button.action`).d('操作')}</span>,
        align: 'center',
        lock: 'right',
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
                      meaning: '网关模版-编辑',
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
          return operatorRender(operators);
        },
      },
    ];

    return (
      <>
        <Header title={intl.get('hiot.gatewayTemplate.view.title.gatewayModel').d('网关模型')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '网关模版-新建',
              },
            ]}
            icon="add"
            color="primary"
            onClick={() => this.handleOperation({}, 'new')}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '网关模版-删除',
              },
            ]}
            icon="delete"
            onClick={this.handleDelete}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table queryFieldsLimit={3} columns={columns} dataSet={this.tableDS} />
        </Content>
      </>
    );
  }
}
