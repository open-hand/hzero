/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-11 13:51:20
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 历史监控图标
 */
import React from 'react';
import echarts from 'echarts';
import { Bind } from 'lodash-decorators';

export default class HistoryMonitor extends React.Component {
  constructor(props) {
    super(props);
    this.historyOption = {
      title: {
        text: this.props.title,
        textStyle: {
          color: '#999999',
        },
      },
      tooltip: {
        trigger: 'axis',
        showDelay: 0, // 显示延迟，添加显示延迟可以避免频繁切换，单位ms
      },
      dataZoom: [
        {
          show: false,
        },
        {
          type: 'inside',
        },
      ],
      xAxis: {
        data: [],
        splitLine: {
          show: false,
        },
        label: {
          show: true,
        },
        axisLine: {
          lineStyle: {
            color: 'white',
          },
        },
        axisTick: {
          alignWithLabel: true,
          show: true,
        },
        axisLabel: {
          show: true,
          interval: 0,
          rotate: 40,
          textStyle: {
            color: '#333',
          },
          // interval: (index, value) => {
          //   return isNaN(new Date(value));
          // },
        },
      },
      yAxis: {
        type: 'value',
        name: '',
        splitLine: {
          show: false,
        },
        axisLine: {
          lineStyle: {
            color: '#999999',
          },
        },
      },
      series: [
        {
          type: 'line',
          data: [],
          smooth: true,
          lineStyle: {
            color: '#c2e9fb',
          },
          itemStyle: {
            normal: {
              color: '#c2e9fb',
            },
          },
          areaStyle: {
            normal: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(194, 233, 251, 0.5)',
                },
                {
                  offset: 1,
                  color: 'rgba(161, 196, 253, 0.5)',
                },
              ]),
            },
          },
        },
      ],
    };
  }

  componentDidMount() {
    const { hisXData, hisYData } = this.props;
    this.chart = echarts.init(this.historyChart);
    this.setChart(hisXData, hisYData);
  }

  // eslint-disable-next-line react/no-deprecated
  componentWillReceiveProps(nextProps) {
    this.setChart(nextProps.hisXData, nextProps.hisYData);
  }

  /**
   * 设置图表数据
   * @param xdata x轴数据
   * @param ydata y轴数据
   */
  @Bind()
  setChart(xdata, ydata) {
    if (xdata && ydata) {
      this.historyOption.xAxis.data = xdata;
      this.historyOption.series[0].data = ydata;
      this.chart.setOption(this.historyOption);
    }
  }

  render() {
    return (
      <div
        style={{ height: 360, width: 1000 }}
        ref={child => {
          this.historyChart = child;
        }}
      />
    );
  }
}
