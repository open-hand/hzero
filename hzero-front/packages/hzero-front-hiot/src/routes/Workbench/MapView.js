import React from 'react';
import echarts from 'echarts';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';
import 'echarts/extension/bmap/bmap';
import { openTab } from 'utils/menuTab';
import intl from 'utils/intl';
import styles from './index.less';

export default class MapView extends React.Component {
  constructor(props) {
    super(props);
    this.ref = React.createRef();
  }

  componentDidMount() {
    // 初始化图表
    if (this.ref.current) {
      this.chart = echarts.init(this.ref.current);
      this.renderMap();
      this.chart.on('click', 'series', this.onClick);
    }
  }

  @Bind()
  formatData(mapData = []) {
    const onLineData = [];
    const offLineData = [];
    const abnormalData = [];
    mapData.forEach((item) => {
      const { thingName, longitude, latitude, connected, status, statusMeaning } = item;
      const data = {
        name: thingName,
        coord: [longitude, latitude], // 标记点位置：[经度,纬度]
        deviceStatus:
          // eslint-disable-next-line no-nested-ternary
          connected === 1
            ? status === 'REGISTERED'
              ? intl.get('hiot.workbench.view.device.onLine').d('在线')
              : statusMeaning
            : intl.get('hiot.workbench.view.device.offLine').d('离线'),
        ...item,
      };
      if (connected === 1) {
        if (status === 'ALERT') {
          abnormalData.push(data);
        } else {
          onLineData.push(data);
        }
      } else {
        offLineData.push(data);
      }
    });
    return {
      onLineData,
      offLineData,
      abnormalData,
    };
  }

  @Bind()
  renderMap() {
    const { mapData } = this.props;
    const deviceData = this.formatData(mapData) || {};
    const { onLineData = [], offLineData = [], abnormalData = [] } = deviceData;
    const options = {
      tooltip: {
        trigger: 'item',
        formatter(params) {
          const { data = {} } = params;
          const {
            name = '',
            thingCode = '',
            categoryMeaning = '',
            thingModelName = '',
            deviceStatus = '',
          } = data;
          let stringData = `${intl
            .get('hiot.common.device.name')
            .d('设备名称')}：${name}</br>${intl
            .get('hiot.common.device.code')
            .d('设备编码')}：${thingCode}`;
          if (categoryMeaning) {
            stringData += `</br>${intl
              .get('hiot.common.device.type')
              .d('设备类别')}：${categoryMeaning}`;
          }
          if (thingModelName) {
            stringData += `</br>${intl
              .get('hiot.common.model.device.deviceModel')
              .d('设备模型')}：${thingModelName}`;
          }
          stringData += `</br>${intl
            .get('hiot.workbench.view.device.deviceStatus')
            .d('设备状态')}：${deviceStatus}`;
          return stringData;
        },
      },
      series: [
        {
          name: 'abnormal',
          type: 'effectScatter',
          coordinateSystem: 'bmap',
          data: [],
          markPoint: {
            label: {
              normal: {
                formatter(params) {
                  return params.name;
                },
                show: true,
              },
              emphasis: {
                show: true,
              },
            },
            itemStyle: {
              normal: {
                color: '#ed2d2e',
              },
            },
            data: abnormalData || [],
          },
        },
        {
          name: 'online',
          type: 'effectScatter',
          coordinateSystem: 'bmap',
          data: [],
          markPoint: {
            label: {
              normal: {
                formatter(params) {
                  return params.name;
                },
                show: true,
              },
              emphasis: {
                show: true,
              },
            },
            itemStyle: {
              normal: {
                color: 'green',
              },
            },
            data: onLineData || [],
          },
        },
        {
          name: 'offline',
          type: 'effectScatter',
          coordinateSystem: 'bmap',
          data: [],
          markPoint: {
            label: {
              normal: {
                formatter(params) {
                  return params.name;
                },
                show: true,
              },
              emphasis: {
                show: true,
              },
            },
            itemStyle: {
              normal: {
                color: 'grey',
              },
            },
            data: offLineData || [],
          },
        },
      ],
      bmap: {
        center: [116.307698, 40.056975], // 中心位置坐标
        zoom: 1, // 地图当前的缩放比例
        roam: true, // 开启鼠标缩放和平移漫游
      },
    };
    this.chart.setOption(options);
  }

  // 点击标记时触发
  @Bind()
  onClick(target) {
    const { data = {} } = target;
    const { guid, thingId } = data;
    openTab({
      key: `/hiot/device/manage/detail/${thingId}`,
      path: `/hiot/device/manage/detail/${thingId}`,
      title: intl.get('hzero.common.view.title.deviceDetailTitle').d('设备详情'),
      search: queryString.stringify({
        guid,
        bMap: true,
      }),
      closable: true,
    });
  }

  render() {
    return (
      <div
        style={{ width: '100%', height: '75vh' }}
        className={styles['workbench-map-view']}
        ref={this.ref}
      />
    );
  }
}
