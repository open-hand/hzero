/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/28
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: OTA升级详情页
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { DataSet } from 'choerodon-ui/pro';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import UpgradeInfoCount from '@/routes/OTAUpgradeTask/Detail/UpgradeInfoCount';
import {
  fields,
  otaUpgradeTaskDetailInfoDS,
  otaUpgradeTaskDetailListDS,
} from '@/stores/otaUpgradeTaskDS';
import BasicInfo from '@/routes/OTAUpgradeTask/Detail/BasicInfo';
import DeviceInfo from '@/routes/OTAUpgradeTask/Detail/DeviceInfo';
import Spin from '@/routes/components/loading/Spin';

const prefix = 'hiot.ota';

@formatterCollections({
  code: [prefix, 'hiot.common'],
})
export default class Detail extends Component {
  constructor(props) {
    super(props);
    this.otaUpgradeTaskDetailInfoDS = new DataSet(otaUpgradeTaskDetailInfoDS());
    this.otaUpgradeTaskDetailListDS = new DataSet(otaUpgradeTaskDetailListDS());
    this.state = {};
  }

  componentDidMount() {
    this.handleQuery();
  }

  @Bind()
  handleQuery() {
    const { id } = this.props.match.params;
    this.otaUpgradeTaskDetailInfoDS.setQueryParameter('taskId', id);
    this.otaUpgradeTaskDetailListDS.setQueryParameter('taskId', id);
    this.otaUpgradeTaskDetailInfoDS.query().then((res) => {
      const recordDS = this.otaUpgradeTaskDetailInfoDS.get(0);
      const { consumeTime } = res;
      const hours = parseInt(consumeTime / 3600, 10);
      const hoursMod = consumeTime % 3600;
      const minutes = parseInt(hoursMod / 60, 10);
      const minutesMod = hoursMod % 60;
      const second = minutesMod % 60;
      recordDS.set(fields.currentUsedTime().name, `${hours}h:${minutes}min:${second}s`);
    });
    this.otaUpgradeTaskDetailListDS.query();
  }

  render() {
    const { backPath } = this.state;
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header
          backPath={backPath || '/hiot/ota-upgrade/task/list'}
          title={intl.get(`${prefix}.view.upgrade.task.detail`).d('升级任务详情')}
        />
        <Content>
          <Spin dataSet={this.otaUpgradeTaskDetailInfoDS}>
            <UpgradeInfoCount dataSet={this.otaUpgradeTaskDetailInfoDS} />
            <BasicInfo dataSet={this.otaUpgradeTaskDetailInfoDS} />
          </Spin>
          <DeviceInfo dataSet={this.otaUpgradeTaskDetailListDS} path={path} />
        </Content>
      </>
    );
  }
}
