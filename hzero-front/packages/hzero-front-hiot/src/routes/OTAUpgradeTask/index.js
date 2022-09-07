/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/27
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: OTA升级任务列表页
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { Button as ButtonPermission } from 'components/Permission';
import { otaUpgradeTaskListDS } from '@/stores/otaUpgradeTaskDS';

const prefix = 'hiot.ota';

@formatterCollections({
  code: [prefix, 'hiot.common'],
})
export default class OTAUpgradeTask extends Component {
  constructor(props) {
    super(props);
    this.otaUpgradeTaskListDS = new DataSet(otaUpgradeTaskListDS());
  }

  handleNew() {
    const { history } = this.props;
    history.push(`/hiot/ota-upgrade/task/create`);
  }

  /**
   * 跳转详情页
   */
  @Bind()
  handleDetail(id) {
    const { history } = this.props;
    history.push(`/hiot/ota-upgrade/task/detail/${id}`);
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const columns = [
      ...otaUpgradeTaskListDS().fields.map(({ name }) => ({ name })),
      {
        name: 'action',
        width: 80,
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        renderer: ({ record }) => {
          const { taskId: id } = record.toData();
          const detailView = intl.get('hzero.common.button.detail').d('详情');
          const operators = [
            {
              key: 'detail',
              len: 2,
              title: detailView,
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.detail`,
                      type: 'button',
                      meaning: 'OTA升级任务-详情',
                    },
                  ]}
                  onClick={() => this.handleDetail(id)}
                >
                  {detailView}
                </ButtonPermission>
              ),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    return (
      <>
        <Header title={intl.get(`${prefix}.view.upgrade.task`).d('OTA升级任务')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: 'OTA升级任务-新建',
              },
            ]}
            color="primary"
            icon="add"
            onClick={() => this.handleNew()}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table
            selectionMode="click"
            dataSet={this.otaUpgradeTaskListDS}
            queryFieldsLimit={3}
            columns={columns}
          />
        </Content>
      </>
    );
  }
}
