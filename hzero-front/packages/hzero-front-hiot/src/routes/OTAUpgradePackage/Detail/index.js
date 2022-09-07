/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/26
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: OTA升级包详情页
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { DataSet, Table, Form, Output } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { Button as ButtonPermission } from 'components/Permission';
import {
  fields,
  otaUpgradePackageDetailBasicDS,
  otaUpgradePackageCreatedTaskDS,
} from '@/stores/otaUpgradePackageDS';

const prefix = 'hiot.otaPackage';

@formatterCollections({
  code: [prefix, 'hiot.common'],
})
export default class Detail extends Component {
  constructor(props) {
    super(props);
    this.otaUpgradePackageDetailBasicDS = new DataSet(otaUpgradePackageDetailBasicDS());
    this.otaUpgradePackageCreatedTaskDS = new DataSet(otaUpgradePackageCreatedTaskDS());
  }

  componentDidMount() {
    const { id } = this.props.match.params;
    this.otaUpgradePackageDetailBasicDS.setQueryParameter('packageId', id);
    this.otaUpgradePackageCreatedTaskDS.setQueryParameter('packageId', id);
    this.otaUpgradePackageDetailBasicDS.query();
    this.otaUpgradePackageCreatedTaskDS.query();
  }

  @Bind()
  handleTaskDetail(taskId) {
    const { history } = this.props;
    history.push(`/hiot/ota-upgrade/task/detail/${taskId}`);
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const columns = [
      ...otaUpgradePackageCreatedTaskDS().fields.map(({ name }) => ({ name })),
      {
        name: 'action',
        width: 150,
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
                      meaning: 'OTA升级包详情-详情',
                    },
                  ]}
                  onClick={() => this.handleTaskDetail(id)}
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
        <Header
          backPath="/hiot/ota-upgrade/package/list"
          title={intl.get(`${prefix}.view.upgrade.package.detail`).d('OTA升级包详情')}
        />
        <Content>
          <Card
            className={DETAIL_CARD_CLASSNAME}
            bordered={false}
            title={intl.get('hiot.common.view.baseInfo').d('基本信息')}
          >
            <Form columns={2} dataSet={this.otaUpgradePackageDetailBasicDS}>
              <Output name={fields.upgradeCategory().name} />
              <Output name={fields.templateName().name} />
              <Output name={fields.templateCode().name} />
              <Output name={fields.versionNum().name} />
              <Output name={fields.upgradePackage().name} />
              <Output
                renderer={({ value }) => `${value}M`}
                name={fields.upgradePackageSize({ name: 'fileSizeMB' }).name}
              />
              <Output name={fields.signatureAlgorithm().name} />
              <Output name={fields.signatureValue().name} />
              <Output name={fields.status().name} />
              <Output name={fields.description().name} />
            </Form>
          </Card>
          <Card
            className={DETAIL_CARD_CLASSNAME}
            bordered={false}
            title={intl.get(`${prefix}.view.created.info`).d('已创建任务')}
          >
            <Table
              selectionMode="click"
              dataSet={this.otaUpgradePackageCreatedTaskDS}
              columns={columns}
            />
          </Card>
        </Content>
      </>
    );
  }
}
