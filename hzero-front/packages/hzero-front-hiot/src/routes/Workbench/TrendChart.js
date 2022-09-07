/**
 * @date 2019-11-26
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * 趋势图
 * chartType: gateway: 网关趋势图 device：设备趋势图 warnTrend：告警趋势图
 */
import React from 'react';
import { Select } from 'choerodon-ui/pro';
import echarts from 'echarts';

import styles from './index.less';
import { curveOption, lineOption } from './ChartOptions';

const { Option } = Select;

export default class TrendChart extends React.Component {
  componentDidMount() {
    const { chartType } = this.props;
    // 初始化图表
    this.chart = echarts.init(this.chartDiv);
    this.chart.setOption(chartType === 'warnEvent' ? lineOption : curveOption);
  }

  render() {
    const { chartType } = this.props;
    return (
      <div style={{ paddingLeft: 10 }}>
        {chartType === 'gateway' ? (
          ''
        ) : (
          <Select>
            <Option value="jack">Jack</Option>
            <Option value="lucy">Lucy</Option>
            <Option value="wu">Wu</Option>
          </Select>
        )}
        <div
          className={styles['workbench-trend-chart-container']}
          ref={child => {
            this.chartDiv = child;
          }}
        />
      </div>
    );
  }
}
