/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-10 19:40:16
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 网关管理-列表页面
 */
import React from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import { EDIT_ACTION, NEW_ACTION } from '@/utils/constants';
import { gatewayManageDS } from '@/stores/gatewayManageDS';

@formatterCollections({ code: ['hiot.dataPointTemplate', 'hiot.gatewayManage', 'hiot.common'] })
export default class GatewayManage extends React.Component {
  constructor(props) {
    super(props);
    this.gatewayManageDS = new DataSet(gatewayManageDS());
  }

  componentDidMount() {
    this.gatewayManageDS.query();
  }

  /**
   * 跳转到对应网关新建/详情/编辑页面
   * @param gatewayId 模板主键id
   * @param type edit: 编辑 detail: 详情 create: 新建
   */
  @Bind()
  handleDetailOrCreate(type, gatewayId = '', hubType, configId) {
    if (type === NEW_ACTION) {
      this.props.history.push(`/hiot/gateway/manage/${NEW_ACTION}`);
    } else {
      this.props.history.push({
        pathname: `/hiot/gateway/manage/${type}/${gatewayId}`,
        state: { hubType, configId },
      });
    }
  }

  /**
   * 删除网关
   */
  @Bind()
  async handleGatewayManageDelete() {
    try {
      const { selected } = this.gatewayManageDS;
      if (selected.length === 0) {
        notification.warning({
          message: intl.get('hiot.common.view.message.pleaseSelectItem').d('请先选择数据'),
        });
        return;
      }
      const res = await this.gatewayManageDS.delete(selected);
      if (!res) {
        return false;
      }
      await this.gatewayManageDS.query();
    } catch (err) {
      //
    }
  }

  // 数据点模板table列
  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      { name: 'gatewayCode', width: 200 },
      { name: 'gatewayName', width: 200 },
      { name: 'gatewayIp', width: 150 },
      { name: 'platformMeaning', width: 100 },
      { name: 'configName', width: 150 },
      {
        name: 'connected',
        width: 100,
        align: 'center',
        renderer: ({ value }) => (
          <span style={{ color: value === 1 ? 'green' : 'red' }}>
            {value === 1
              ? intl.get('hiot.common.view.title.online').d('在线')
              : intl.get('hiot.common.view.title.offline').d('离线')}
          </span>
        ),
      },
      { name: 'statusMeaning' },
      { name: 'name' },
      { name: 'thingModelName', width: 200 },
      { name: 'subNumber' },
      {
        name: 'operation',
        width: 80,
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
                      meaning: '网关管理-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleDetailOrCreate(
                      EDIT_ACTION,
                      record.get('gatewayId'),
                      record.get('platform'),
                      record.get('configId')
                    );
                  }}
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
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hiot.gatewayManage.view.title.header').d('网关维护')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '网关管理-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleDetailOrCreate(NEW_ACTION)}
            style={{ marginLeft: 8 }}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '网关管理-删除',
              },
            ]}
            icon="delete"
            onClick={this.handleGatewayManageDelete}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.gatewayManageDS} columns={this.columns} queryFieldsLimit={3} />
        </Content>
      </>
    );
  }
}
