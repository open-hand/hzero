/* eslint-disable react/no-deprecated */
/**
 * @date 2019-11-26
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * 导览工作台——项目层
 */
import React from 'react';
import { Tabs } from 'choerodon-ui';
import { DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty, isArray } from 'lodash';
import uuid from 'uuid/v4';
import intl from 'utils/intl';
import notification from 'utils/notification';

import { deviceTrendDS, warnEventDS } from '@/stores/workbenchDS';
import { fetchDeviceMap } from '@/services/WorkbenchService';
import ProjectInfo from './ProjectInfo';
import ProjectChart from './ProjectChart';
import DeviceCards from './DeviceCards';
import MapView from './MapView';

const gatewayTitles = [
  intl.get('hiot.workbench.gateway.online.rate').d('网关在线率'),
  intl.get('hiot.workbench.real-time.gateway.total.number').d('实时网关总数'),
  intl.get('hiot.workbench.real-time.gateway.online.number').d('实时网关在线数'),
];

const deviceTitles = [
  intl.get('hiot.workbench.device.online.rate').d('设备在线率'),
  intl.get('hiot.workbench.real-time.device.total.number').d('实时设备总数'),
  intl.get('hiot.workbench.real-time.device.online.number').d('实时设备在线数'),
];

export default class ProjectWorkbench extends React.Component {
  constructor(props) {
    super(props);
    const { tabKey } = props;
    this.state = {
      deviceInfo: [],
      deviceData: {},
      gatewayInfo: [],
      gatewayData: {},
      warnInfo: [],
      warnData: {},
      mapDataL: [],
      tabKey: tabKey || '1',
      uuidKey: '',
    };
    this.deviceTrendDS = new DataSet(deviceTrendDS());
    this.warnEventDS = new DataSet(warnEventDS());
  }

  componentDidMount() {
    const { projectInfo = {} } = this.props;
    const { thingGroupId, levelPath } = projectInfo;
    if (thingGroupId) {
      this.loadData(thingGroupId, levelPath);
    }
  }

  componentWillReceiveProps(nextProps) {
    const {
      projectInfo: { thingGroupId, levelPath },
    } = nextProps;
    if (thingGroupId !== this.props.projectInfo.thingGroupId) {
      this.loadData(thingGroupId, levelPath);
    }
  }

  @Bind()
  loadData(thingGroupId, levelPath) {
    // 清空筛选条件
    if (this.deviceChart) {
      this.deviceChart.selectDS.reset();
    }
    if (this.warnEventChart) {
      this.warnEventChart.selectDS.reset();
    }
    // 加载网关在线趋势
    this.loadGatewayTrend(thingGroupId, 1);
    // 加载设备在线趋势
    this.loadGatewayTrend(thingGroupId, 0);
    // 加载告警事件趋势
    this.loadWarnEvent(thingGroupId);
    // 加载设备地图
    this.loadDeviceMap(levelPath);
  }

  /**
   * 加载网关在线趋势
   */
  @Bind()
  loadGatewayTrend(thingGroupId, isGateway, category) {
    this.deviceTrendDS.queryParameter = { thingGroupId, isGateway, category };
    this.deviceTrendDS
      .query()
      .then((resp) => {
        const { dataX = [], total = [], rate = [], online = [] } = resp;
        const deviceInfo = this.setInfo(
          isGateway ? gatewayTitles : deviceTitles,
          rate,
          total,
          online
        );
        const deviceData = { dataX, total, rate, online };
        if (isGateway) {
          this.setState({ gatewayInfo: deviceInfo, gatewayData: deviceData });
        } else {
          this.setState({ deviceInfo, deviceData });
        }
      })
      .catch(() => {
        notification.error({
          message: intl.get('hiot.workbench.project.fail.get.trend.chart').d('查询趋势图失败！'),
        });
      });
  }

  @Bind()
  setInfo(titleArr, rate, total, online) {
    const lastRate = isEmpty(rate) ? 0 : rate[rate.length - 1];
    const lastTotal = isEmpty(total) ? 0 : total[total.length - 1];
    const lastOnline = isEmpty(online) ? 0 : online[online.length - 1];
    const numberArr = [lastRate, lastTotal, lastOnline];
    const unitArr = [
      '%',
      intl.get('hzero.common.unit.set').d('台'),
      intl.get('hzero.common.unit.set').d('台'),
    ];
    return numberArr.map((number, index) => {
      const num = index === 0 ? 100 : 1;
      return {
        infoTitle: titleArr[index],
        value: number === 'null' ? 0 : number * num,
        unit: unitArr[index],
        key: index,
      };
    });
  }

  @Bind()
  loadWarnEvent(thingGroupId, alertLevel) {
    this.warnEventDS.queryParameter = { thingGroupId, alertLevel };
    this.warnEventDS
      .query()
      .then((resp) => {
        const { dataX = [], recovered = [] } = resp;
        let recoveredTotal = 0;
        if (!isEmpty(recovered)) {
          recoveredTotal = recovered[recovered.length - 1];
        }
        const warnInfo = [
          {
            infoTitle: intl.get('hiot.workbench.no.recovered.alarms').d('实时未恢复全部告警数'),
            value: recoveredTotal === 'null' ? 0 : recoveredTotal,
            unit: intl.get('hzero.common.unit.individual').d('个'),
            key: '1',
          },
        ];
        const warnData = { dataX, recovered };
        this.setState({ warnData, warnInfo });
      })
      .catch(() => {
        notification.error({
          message: intl.get('hiot.workbench.project.fail.get.trend.chart').d('查询趋势图失败！'),
        });
      });
  }

  @Bind()
  tabsCallback(key) {
    this.setState({ tabKey: key });
  }

  // 加载设备地图数据
  @Bind()
  loadDeviceMap(levelPath) {
    fetchDeviceMap({ levelPath }).then((res) => {
      if (res && !res.failed) {
        this.setState({ mapData: isArray(res) ? res : [], uuidKey: uuid() });
      }
      if (res && res.failed) {
        notification.error({
          message: res.message,
        });
      }
    });
  }

  render() {
    const { projectInfo, onLinkToDevice, infoDS, path, firstRender } = this.props;
    const { thingGroupId } = projectInfo;
    const {
      deviceInfo,
      deviceData,
      gatewayInfo,
      gatewayData,
      warnInfo,
      warnData,
      uuidKey,
      tabKey,
      mapData = [],
    } = this.state;

    return (
      <div>
        <Tabs activeKey={tabKey} onChange={this.tabsCallback}>
          <Tabs.TabPane
            tab={intl.get('hiot.workbench.view.title.deviceGroupInfo').d('设备分组信息')}
            key="1"
          >
            <ProjectInfo projectInfo={projectInfo} />
            <ProjectChart
              type="gateway"
              title={intl.get('hiot.workbench.gateway.online.trend').d('网关在线趋势')}
              displayInfo={gatewayInfo}
              value={gatewayData}
            />
            <ProjectChart
              type="device"
              thingGroupId={thingGroupId}
              title={intl.get('hiot.workbench.device.online.trend').d('设备在线趋势')}
              displayInfo={deviceInfo}
              value={deviceData}
              lookupCode="HIOT.THING_CATEGORY"
              onSelect={this.loadGatewayTrend}
              onRef={(child) => {
                this.deviceChart = child;
              }}
            />
            <ProjectChart
              type="warnEvent"
              thingGroupId={thingGroupId}
              title={intl.get('hiot.workbench.warning.event.trend').d('告警事件趋势')}
              displayInfo={warnInfo}
              value={warnData}
              lookupCode="HIOT.ALERT_LEVEL"
              onSelect={this.loadWarnEvent}
              onRef={(child) => {
                this.warnEventChart = child;
              }}
            />
          </Tabs.TabPane>
          <Tabs.TabPane tab={intl.get('hiot.workbench.device.cards').d('设备卡片')} key="2">
            <DeviceCards
              infoDS={infoDS}
              thingGroupId={thingGroupId}
              onLinkToDevice={onLinkToDevice}
              path={path}
            />
          </Tabs.TabPane>
          <Tabs.TabPane tab={intl.get('hiot.workbench.device.map').d('设备地图')} key="map">
            {firstRender && <MapView key={uuidKey} mapData={mapData} />}
          </Tabs.TabPane>
        </Tabs>
      </div>
    );
  }
}
