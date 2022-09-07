/* eslint-disable react/no-deprecated */
/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-11 10:05:31
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 设备详情-设备监控-测量点监控组件
 */
import React from 'react';
import echarts from 'echarts';
import { Modal } from 'choerodon-ui';
import { DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import { historyMonitorDS } from '@/stores/deviceManageDS';
import HistoryMonitor from './HistoryMonitor';
import styles from '../index.less';

export default class MeasurePoint extends React.Component {
  constructor(props) {
    super(props);
    this.historyMonitorDS = props.dataSet || new DataSet(historyMonitorDS());
    this.state = {
      show: false,
      hisXData: [],
      hisYData: [],
    };
    this.initOption = {
      grid: {
        top: 25,
        right: 0,
        bottom: 25,
        left: 30,
      },
      tooltip: {
        trigger: 'axis',
      },
      axisTick: {
        show: false,
      },
      xAxis: {
        boundaryGap: false,
        data: [],
        axisLine: {
          lineStyle: {
            color: '#EFEFEF',
          },
        },
      },
      yAxis: {
        type: 'value',
        show: false,
        splitLine: {
          show: false,
        },
        min: (value) => Math.floor(value.min),
        max: (value) => Math.ceil(value.max),
      },
      series: [
        {
          data: [],
          type: 'line',
          smooth: true,
          symbol: 'none',
          lineStyle: {
            color: '#c2e9fb',
          },
          areaStyle: {
            normal: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgb(49, 150, 250, 0.4)',
                },
                {
                  offset: 1,
                  color: 'rgba(49, 150, 250, 0.1)',
                },
              ]),
            },
          },
        },
      ],
    };
  }

  componentDidMount() {
    const { yAxis } = this.props;
    // 初始化echarts图表
    this.lineChart = echarts.init(this.charts);
    this.setChart(yAxis);
  }

  componentWillReceiveProps(nextProps) {
    this.setChart(nextProps.yAxis);
  }

  /**
   * 设置图表
   * @param ydata y轴数据
   */
  @Bind()
  setChart(ydata) {
    if (ydata) {
      this.initOption.series[0].data = ydata;
      this.lineChart.setOption(this.initOption);
    }
  }

  /**
   * 点击测量点打开历史监控图表
   * 获取到的数据从点击的那一刻往前30天的数据
   * @param propertyId 数据的唯一性id
   */
  @Bind()
  handleShowHistory(propertyId) {
    this.setState({
      show: true,
    });
    const { thingId } = this.props;
    this.historyMonitorDS.setQueryParameter('propertyId', propertyId);
    this.historyMonitorDS.setQueryParameter('thingId', thingId);
    this.historyMonitorDS.query().then((resp) => {
      const { dataX, dataY } = resp;
      this.setState({
        hisXData: dataX,
        hisYData: dataY,
      });
    });
  }

  // 关闭Modal框
  @Bind()
  handleCancel(e) {
    e.stopPropagation();
    this.setState({ show: false });
  }

  render() {
    const { show, hisXData, hisYData } = this.state;
    const { propertyName, val, unitCode, title, propertyId } = this.props;
    return (
      <div role="none" style={{ width: '33%' }} onClick={() => this.handleShowHistory(propertyId)}>
        <p className={styles['chart-title']}>{propertyName}</p>
        <p className={styles['chart-value']}>
          {val} {unitCode}
        </p>
        <div
          style={{ height: 150, width: '100%' }}
          ref={(child) => {
            this.charts = child;
          }}
        />
        <Modal
          title={title || intl.get('hiot.deviceManage.view.title.historyChart').d('测量点历史记录')}
          footer={null}
          visible={show}
          width={1000}
          onCancel={this.handleCancel}
        >
          <HistoryMonitor title={propertyName} hisXData={hisXData} hisYData={hisYData} />
        </Modal>
      </div>
    );
  }
}
