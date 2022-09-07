/* eslint-disable react/no-deprecated */
/**
 * @date 2019-11-26
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * 项目层级的图表组件
 * title: 标题
 * displayInfo： 组件左侧展示信息 [{infoTitle: 展示信息的标题, unit: 单位, fieldName: 对应后端中的哪个字段}]
 */
import React, { Fragment } from 'react';
import { Card, Row, Col } from 'choerodon-ui';
import { Select, DataSet } from 'choerodon-ui/pro';
import echarts from 'echarts';
import { Bind } from 'lodash-decorators';

import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import intl from 'utils/intl';
import { curveOption, lineOption } from '@/routes/Workbench/ChartOptions';
import styles from './index.less';

const gatewayLegendArr = [
  intl.get('hiot.workbench.total.gateway').d('网关总数'),
  intl.get('hiot.workbench.gateway.oline').d('网关在线数'),
];

const deviceLegendArr = [
  intl.get('hiot.workbench.total.device').d('设备总数'),
  intl.get('hiot.workbench.device.online').d('设备在线数'),
];

const warnLegendArr = [intl.get('hiot.workbench.not.recovered.total').d('实时未恢复总数')];

export default class ProjectInfo extends React.PureComponent {
  constructor(props) {
    super(props);
    const { lookupCode } = props;
    this.state = {};
    this.selectDS = new DataSet({
      fields: [
        {
          name: 'selectValue',
          type: 'string',
          lookupCode,
        },
      ],
    });
  }

  componentDidMount() {
    const { type, onRef } = this.props;
    if (onRef) {
      onRef(this);
    }
    // 初始化图表
    this.chart = echarts.init(this.chartDiv);
    this.chart.setOption(type === 'warnEvent' ? lineOption : curveOption);
  }

  componentWillReceiveProps(nextProps) {
    const { type, value } = nextProps;
    if (type === 'gateway') {
      this.initGateway(value, 'gateway');
    } else if (type === 'device') {
      this.initGateway(value, 'device');
    } else {
      this.initWarnEvent(value);
    }
  }

  // 初始化网关、设备趋势图表数据
  @Bind()
  initGateway(value, type) {
    const { dataX = [], total = [], online = [] } = value;
    curveOption.xAxis.data = dataX;
    const [series0, series1] = curveOption.series;
    series0.data = total;
    series1.data = online;
    if (type === 'gateway') {
      curveOption.legend.data = gatewayLegendArr;
      [series0.name, series1.name] = gatewayLegendArr;
    } else {
      curveOption.legend.data = deviceLegendArr;
      [series0.name, series1.name] = deviceLegendArr;
    }
    this.chart.setOption(curveOption);
  }

  // 初始化告警事件图表数据
  @Bind()
  initWarnEvent(value) {
    const { dataX = [], recovered = [] } = value;
    lineOption.series = [recovered].map((data, index) => ({
      name: warnLegendArr[index],
      type: 'line',
      data,
    }));
    lineOption.xAxis.data = dataX;
    lineOption.legend.data = warnLegendArr;
    this.chart.setOption(lineOption);
  }

  @Bind()
  handleSelectChange(value) {
    const { type, onSelect, thingGroupId } = this.props;
    if (type === 'device') {
      onSelect(thingGroupId, 0, value);
    } else {
      onSelect(thingGroupId, value);
    }
  }

  render() {
    const { title, displayInfo = [], type } = this.props;
    return (
      <Card className={DETAIL_CARD_CLASSNAME} bordered={false} title={title}>
        <Row className={styles['workbench-chart-container-border-row']}>
          <Col span={4}>
            {displayInfo.map((item) => {
              const { infoTitle, value, unit, key } = item;
              return (
                <Fragment key={key}>
                  <p>{infoTitle}</p>
                  <div className={styles['workbench-project-chart-info-container']}>
                    <p className={styles['workbench-project-chart-info-container-value']}>
                      {value}
                    </p>
                    <p className={styles['workbench-project-chart-info-container-unit']}>{unit}</p>
                  </div>
                </Fragment>
              );
            })}
          </Col>
          <Col span={20} className={styles['workbench-chart-container-border-col']}>
            <div style={{ paddingLeft: 10 }}>
              {type === 'gateway' ? (
                ''
              ) : (
                <Select
                  dataSet={this.selectDS}
                  name="selectValue"
                  onChange={this.handleSelectChange}
                />
              )}
              <div
                className={styles['workbench-trend-chart-container']}
                ref={(child) => {
                  this.chartDiv = child;
                }}
              />
            </div>
          </Col>
        </Row>
      </Card>
    );
  }
}
