import React, { Component } from 'react';
import moment from 'moment';
import { Bind } from 'lodash-decorators';
import ReactEcharts from 'echarts-for-react';
import { Button as C7nButton } from 'choerodon-ui/pro';
import { Button, Select, Spin, DatePicker } from 'choerodon-ui';
import { uniqBy, isEmpty, isArray, orderBy } from 'lodash';

import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';

import styles from './index.less';
import {
  fetchApiCount,
  fetchMicroservice,
  fetchApiInvokeCount,
  fetchSingleApiInvokeCount,
} from '../../services/apiOverviewService';

const { Option } = Select;
const currentTime = moment();
const numberList = [7, 15, 30];
const { RangePicker } = DatePicker;
const backgroundStyle = { backgroundColor: 'rgba(0, 0, 0, 0.08)' }; // 被选中的按钮颜色
const colorArr = ['#FDB34E', '#5266D4', '#FD717C', '#53B9FC', '#F44336', '#6B83FC', '#00BFA5']; // 默认取色

const textList = [
  intl.get('hadm.apiOverview.view.button.7days').d('近7天'),
  intl.get('hadm.apiOverview.view.button.15days').d('近15天'),
  intl.get('hadm.apiOverview.view.button.30days').d('近30天'),
];

@formatterCollections({ code: ['hadm.apiOverview'] })
export default class ApiOverview extends Component {
  constructor(props) {
    super(props);
    this.state = {
      apiCountLoading: false,
      apiInvokeLoading: false,
      singleApiInvokeLoading: false,
      apiCountData: {}, // api总数
      apiInvokeData: {}, // api调用总数
      singleApiInvokeData: {}, // 单个api调用总数
      microserviceList: [], // 微服务
      currentService: {}, // 当前微服务
      apiInvokeEndTime: currentTime, // api调用总数的结束时间
      singleApiInvokeEndTime: currentTime, // 单个api调用总数的结束时间
      apiInvokeStartTime: moment().subtract(6, 'days'), // api调用总数的起始时间
      singleApiInvokeStartTime: moment().subtract(6, 'days'), // 单个api调用总数的起始时间
      apiCountClickedBtn: 7, // 被点击的api调用总数图表按钮样式
      singleApiClickedBtn: 7, // 被点击的单个api调用总数图表按钮
      showData: [], // 时间框的显示值
    };
  }

  componentDidMount() {
    const {
      apiInvokeEndTime,
      apiInvokeStartTime,
      singleApiInvokeEndTime,
      singleApiInvokeStartTime,
    } = this.state;
    this.queryApiCount();
    this.queryApiInvokeCount(apiInvokeStartTime, apiInvokeEndTime);
    this.queryMicroservice(singleApiInvokeStartTime, singleApiInvokeEndTime);
  }

  /**
   * 刷新
   */
  @Bind()
  handleRefresh() {
    const apiInvokeEndTime = currentTime;
    const apiInvokeStartTime = moment().subtract(6, 'days');
    const singleApiInvokeEndTime = currentTime;
    const singleApiInvokeStartTime = moment().subtract(6, 'days');
    this.setState({
      apiInvokeEndTime,
      apiInvokeStartTime,
      singleApiInvokeEndTime,
      singleApiInvokeStartTime,
      apiCountClickedBtn: 7,
      singleApiClickedBtn: 7,
      showData: [],
    });
    this.queryApiCount();
    this.queryApiInvokeCount(apiInvokeStartTime, apiInvokeEndTime);
    this.queryMicroservice(singleApiInvokeStartTime, singleApiInvokeEndTime);
  }

  /**
   * 查询微服务
   */
  @Bind()
  queryMicroservice(startTime, endTime) {
    fetchMicroservice().then((data) => {
      if (data && !data.failed) {
        if (!isEmpty(data)) {
          const handledData = uniqBy(
            data.map((item) => ({ name: item.name.split(':')[1] })),
            'name'
          );
          this.setState({
            microserviceList: handledData,
            currentService: handledData[0],
          });
          this.querySingleApiInvokeCount(startTime, endTime, handledData[0]);
        }
      }
    });
  }

  /**
   * 查询api总数
   */
  @Bind()
  queryApiCount() {
    this.setState({ apiCountLoading: true });
    fetchApiCount().then((res) => {
      let obj = {};
      if (res && !res.failed) {
        obj = res;
      }
      this.setState({
        apiCountData: obj,
        apiCountLoading: false,
      });
    });
    this.getApiCountChart();
  }

  /**
   * 查询api调用总数
   */
  @Bind()
  queryApiInvokeCount(startTime, endTime) {
    const beginDate = startTime.format().split('T')[0];
    const endDate = endTime.format().split('T')[0];
    this.setState({ apiInvokeLoading: true });
    const params = {
      beginDate,
      endDate,
    };
    fetchApiInvokeCount(params).then((res) => {
      let obj = {};
      if (res && !res.failed) {
        const { details = [], entities = [] } = res;
        if (details.length && entities.length) {
          const handleDetails = details.map((item) => ({
            ...item,
            sortIndex: entities.indexOf(item.service),
          }));
          const finalDetails = orderBy(handleDetails, ['sortIndex'], ['asc']);
          res.details = finalDetails;
        }
        obj = res;
      }
      this.setState({
        apiInvokeData: obj,
        apiInvokeLoading: false,
      });
    });
    this.getApiInvokeChart();
  }

  /**
   * 查询单个api调用总数
   */
  @Bind()
  querySingleApiInvokeCount(startTime, endTime, serviceName) {
    const { currentService } = this.state;
    const service = serviceName || currentService;
    const beginDate = startTime.format().split('T')[0];
    const endDate = endTime.format().split('T')[0];
    const params = {
      beginDate,
      endDate,
      service: service && service.name,
    };
    this.setState({ singleApiInvokeLoading: true });
    fetchSingleApiInvokeCount(params).then((res) => {
      let obj = {};
      if (res && !res.failed) {
        const { entities = [] } = res;
        if (entities.length) {
          const arr = entities.map((item) => `${item.split(':')[1]}: ${item.split(':')[0]}`);
          res.entities = arr;
        }
        obj = res;
      }
      this.setState({
        singleApiInvokeData: obj,
        singleApiInvokeLoading: false,
      });
      this.getSingleApiInvokeChart();
    });
  }

  getInitState() {
    return {
      dateType: 'seven',
      thirdDateType: 'seven',
    };
  }

  // 获取api总数chart
  getApiCountChart = () => {
    const { apiCountLoading } = this.state;
    return (
      <div className={styles['hadm-api-overview-top-container-first-container']}>
        <Spin spinning={apiCountLoading}>
          <ReactEcharts
            style={{ width: '100%', height: 380 }}
            option={this.getApiCountChartOption()}
          />
        </Spin>
      </div>
    );
  };

  // 获取api调用总数图表
  getApiInvokeChart = () => {
    const { apiCountClickedBtn, apiInvokeLoading } = this.state;
    return (
      <div className={styles['hadm-api-overview-top-container-sec-container']}>
        <Spin spinning={apiInvokeLoading}>
          <div className={styles['hadm-api-overview-top-container-sec-container-timewrapper']}>
            <div>
              {numberList.map((item, index) => {
                return (
                  <Button
                    key={item}
                    style={apiCountClickedBtn === item ? backgroundStyle : {}}
                    onClick={() => this.onClickApiInvokeTimeBtn(item, index)}
                  >
                    {textList[index]}
                  </Button>
                );
              })}
            </div>
          </div>
          <ReactEcharts
            style={{ width: '100%', height: 380 }}
            option={this.getApiInvokeChartOption()}
            notMerge
          />
        </Spin>
      </div>
    );
  };

  // 获取单个api调用总数图表
  getSingleApiInvokeChart = () => {
    const { showData, currentService, singleApiClickedBtn, singleApiInvokeLoading } = this.state;
    return (
      <div className={styles['hadm-api-overview-third-container']}>
        <Spin spinning={singleApiInvokeLoading}>
          <div className={styles['hadm-api-overview-third-container-timewrapper']}>
            <Select
              style={{ width: '175px', marginRight: '34px' }}
              value={currentService.name}
              // getPopupContainer={() => document.getElementsByClassName('page-content')[0]}
              onChange={this.handleServiceChange}
              label={
                <span>
                  {intl.get('hadm.apiOverview.view.message.currentService').d('所属微服务')}
                </span>
              }
            >
              {this.getOptionList()}
            </Select>
            <div style={{ marginRight: 20 }}>
              {numberList.map((item, index) => {
                return (
                  <Button
                    key={item}
                    style={singleApiClickedBtn === item ? backgroundStyle : {}}
                    onClick={() => this.onClickSingleApiTimeBtn(item, index)}
                  >
                    {textList[index]}
                  </Button>
                );
              })}
            </div>
            <RangePicker
              size="small"
              allowClear={false}
              disabledDate={(current) => {
                // Can not select days after today and today
                return current && current > moment().endOf('day');
              }}
              onChange={this.onSingleApiInvokeTimeChange}
              value={showData}
            />
          </div>
          <ReactEcharts
            className={styles['hadm-api-overview-third-chart']}
            style={{ width: '100%', height: 400 }}
            option={this.getSingleApiInvokeChartOption()}
            notMerge
          />
        </Spin>
      </div>
    );
  };

  /* 微服务下拉框 */
  getOptionList() {
    const { microserviceList } = this.state;
    return microserviceList && microserviceList.length ? (
      microserviceList.map(({ name }) => (
        <Option key={name} value={name}>
          {name}
        </Option>
      ))
    ) : (
      <Option value="empty" key="empty" disabled>
        {intl.get('hzero.common.components.noticeIcon.null').d('暂无数据')}
      </Option>
    );
  }

  /**
   * 微服务下拉框改变事件
   * @param serviceName 服务名称
   */
  @Bind()
  handleServiceChange(serviceName) {
    const { microserviceList = [], singleApiInvokeEndTime, singleApiInvokeStartTime } = this.state;
    const currentService = microserviceList.find((service) => service.name === serviceName);
    this.setState({
      currentService,
    });
    this.querySingleApiInvokeCount(
      singleApiInvokeStartTime,
      singleApiInvokeEndTime,
      currentService
    );
  }

  // 获取api总数图表的配置参数
  @Bind()
  getApiCountChartOption() {
    const { apiCountData = {} } = this.state;
    const { services, apiCounts = [] } = apiCountData;
    let handledApiCountData;
    if (isArray(services)) {
      handledApiCountData = services.map((item, index) => ({
        name: item,
        value: apiCounts[index],
      }));
    }
    return {
      title: {
        text: intl.get('hadm.apiOverview.view.title.apiCount').d('各服务API总数'),
        textStyle: {
          color: 'rgba(0,0,0,0.87)',
          fontWeight: '400',
        },
        top: 20,
        left: 16,
      },
      tooltip: {
        trigger: 'item',
        confine: true,
        formatter: '{b} <br/>百分比: {d}% <br/>总数: {c}',
        backgroundColor: '#FFFFFF',
        borderWidth: 1,
        borderColor: '#DDDDDD',
        extraCssText: 'box-shadow: 0 2px 4px 0 rgba(0,0,0,0.20)',
        textStyle: {
          fontSize: 13,
          color: '#000000',
        },
      },
      legend: {
        right: 15,
        itemHeight: 11,
        top: 60,
        height: '70%',
        y: 'center',
        type: 'scroll',
        data: services || [],
        orient: 'vertical', // 图例纵向排列
        icon: 'circle',
      },
      // calculable: true,
      series: [
        {
          type: 'pie',
          radius: [20, 110],
          center: ['31%', '50%'],
          roseType: 'radius',
          minAngle: 30,
          label: {
            normal: {
              show: false,
            },
            emphasis: {
              show: false,
            },
          },
          data: handledApiCountData || {},
        },
      ],
      color: colorArr,
    };
  }

  // 获取Api调用总数图表的配置参数
  @Bind()
  getApiInvokeChartOption() {
    const { apiInvokeData = {} } = this.state;
    const { dates, details, entities } = apiInvokeData;
    let handleSeriesData = [];
    if (isArray(details)) {
      handleSeriesData = details.map((item) => ({
        type: 'line',
        name: item.service,
        data: item.data,
        smooth: 0.5,
        smoothMonotone: 'x',
        symbol: 'circle',
        areaStyle: {
          opacity: '0.5',
        },
      }));
    }
    return {
      title: {
        text: intl.get('hadm.apiOverview.view.title.apiInvoke').d('各服务API调用总数'),
        textStyle: {
          color: 'rgba(0,0,0,0.87)',
          fontWeight: '400',
        },
        top: 20,
        left: 16,
      },

      tooltip: {
        trigger: 'axis',
        confine: true,
        borderWidth: 1,
        backgroundColor: '#fff',
        borderColor: '#DDDDDD',
        extraCssText: 'box-shadow: 0 2px 4px 0 rgba(0,0,0,0.20)',
        textStyle: {
          fontSize: 13,
          color: '#000000',
        },
      },
      legend: {
        top: 60,
        right: 0,
        itemHeight: 11,
        orient: 'vertical', // 图例纵向排列
        icon: 'circle',
        type: 'scroll',
        data: entities || [],
      },
      grid: {
        left: '2%',
        top: 110,
        width: '63%',
        height: '55%',
        containLabel: true,
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          axisTick: { show: false },
          axisLine: {
            lineStyle: {
              color: '#eee',
              type: 'solid',
              width: 2,
            },
            onZero: true,
          },
          axisLabel: {
            margin: 7, // X轴文字和坐标线之间的间距
            textStyle: {
              color: 'rgba(0, 0, 0, 0.65)',
              fontSize: 12,
            },
            formatter(value) {
              const month = value.split('-')[1];
              const day = value.split('-')[2];
              return `${month}/${day}`;
            },
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#eee'],
              width: 1,
              type: 'solid',
            },
          },
          data: dates || [],
        },
      ],
      yAxis: [
        {
          type: 'value',
          minInterval: 1,
          name: intl.get('hadm.apiOverview.view.title.timeText').d('次数'),
          nameLocation: 'end',
          nameTextStyle: {
            color: '#000',
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: '#eee',
            },
          },
          axisTick: {
            show: false,
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#eee'],
            },
          },
          axisLabel: {
            color: 'rgba(0,0,0,0.65)',
          },
        },
      ],
      series: handleSeriesData,
      color: colorArr,
    };
  }

  // 获取单个api调用总数图表的配置参数
  getSingleApiInvokeChartOption() {
    const { singleApiInvokeData } = this.state;
    const { entities, dates, details } = singleApiInvokeData;
    const copyThirdChartData = JSON.parse(JSON.stringify(singleApiInvokeData));
    let handledData = [];
    let handledApis = {};
    if (isArray(details)) {
      handledData = details.map((item) => ({
        type: 'line',
        name: `${item.api.split(':')[1]}: ${item.api.split(':')[0]}`,
        data: item.data,
        smooth: 0.2,
      }));

      if (copyThirdChartData.entities.length) {
        copyThirdChartData.entities.forEach((item) => {
          handledApis[item] = false;
        });
        const selectedApis = copyThirdChartData.entities.splice(0, 10);
        for (const item of selectedApis) {
          handledApis[item] = true;
        }
      } else {
        handledApis = {};
      }
    }

    return {
      title: {
        text: intl.get('hadm.apiOverview.view.title.singleApiInvoke').d('单个API调用总数'),
        textStyle: {
          color: 'rgba(0,0,0,0.87)',
          fontWeight: '400',
        },
        top: 20,
        left: 16,
      },
      tooltip: {
        trigger: 'item',
        confine: true,
        borderWidth: 1,
        backgroundColor: '#fff',
        borderColor: '#DDDDDD',
        extraCssText: 'box-shadow: 0 2px 4px 0 rgba(0,0,0,0.20)',
        textStyle: {
          fontSize: 13,
          color: '#000000',
        },

        formatter(params) {
          return `<div>
              <div>${params.name}</div>
              <div><span class="hadm-apioverview-charts-tooltip" style="background-color:${
                params.color
              };"></span>${params.seriesName}</div>
              <div>${intl.get('hadm.apiOverview.view.title.timeText').d('次数')}: ${
            params.value
          }</div>
            <div>`;
        },
      },
      legend: {
        show: true,
        type: 'scroll',
        orient: 'vertical', // 图例纵向排列
        itemHeight: 11,
        top: 80,
        left: '72%',
        // right: 5,
        icon: 'circle',
        height: '70%',
        data: entities || [],
        selected: handledApis,
        formatter(name) {
          const showLength = 44; // 截取长度
          let newName = name;
          if (name.length > showLength) {
            newName = `${name.substring(0, showLength)}...`;
          }
          return newName;
        },
        tooltip: {
          show: true,
        },
      },
      grid: {
        left: '3%',
        top: 110,
        containLabel: true,
        width: '66%',
        height: '62.5%',
      },
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          axisTick: { show: false },
          axisLine: {
            lineStyle: {
              color: '#eee',
              type: 'solid',
              width: 2,
            },
            onZero: true,
          },
          axisLabel: {
            margin: 7, // X轴文字和坐标线之间的间距
            textStyle: {
              color: 'rgba(0, 0, 0, 0.65)',
              fontSize: 12,
            },
            formatter(value) {
              const month = value.split('-')[1];
              const day = value.split('-')[2];
              return `${month}/${day}`;
            },
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#eee'],
              width: 1,
              type: 'solid',
            },
          },
          data: dates || [],
        },
      ],
      yAxis: [
        {
          type: 'value',
          minInterval: 1,
          name: intl.get('hadm.apiOverview.view.title.timeText').d('次数'),
          nameLocation: 'end',
          nameTextStyle: {
            color: '#000',
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: '#eee',
            },
          },
          axisTick: {
            show: false,
          },
          splitLine: {
            show: true,
            lineStyle: {
              color: ['#eee'],
            },
          },
          axisLabel: {
            color: 'rgba(0,0,0,0.65)',
          },
        },
      ],
      series: handledData,
      color: colorArr,
    };
  }

  /**
   * 点击api调用总数图表按钮
   * @param {number} number
   */
  @Bind()
  onClickApiInvokeTimeBtn(number, index) {
    const endTime = moment();
    const startTime = moment().subtract(number - 1, 'days');
    this.setState({
      apiInvokeStartTime: startTime,
      apiInvokeEndTime: endTime,
    });
    this.queryApiInvokeCount(startTime, endTime);
    this.setState({
      apiCountClickedBtn: numberList[index],
    });
  }

  /**
   * 单个api调用时间框改变事件
   * @param {array} [value=[]]
   */
  @Bind()
  onSingleApiInvokeTimeChange(value = []) {
    if (!isEmpty(value)) {
      const [startTime, endTime] = value;
      this.setState({
        singleApiInvokeStartTime: startTime,
        singleApiInvokeEndTime: endTime,
      });
      this.querySingleApiInvokeCount(startTime, endTime);
      this.setState({ showData: value, singleApiClickedBtn: 0 });
    }
  }

  /**
   * 点击单个api调用图表按钮
   * @param {number} number
   * @param {number} index
   * @memberof ApiOverview
   */
  @Bind()
  onClickSingleApiTimeBtn(number, index) {
    const endTime = moment();
    const startTime = moment().subtract(number - 1, 'days');
    this.setState({
      singleApiInvokeStartTime: startTime,
      singleApiInvokeEndTime: endTime,
    });
    this.querySingleApiInvokeCount(startTime, endTime);
    this.setState({
      showData: [],
      singleApiClickedBtn: numberList[index],
    });
  }

  render() {
    return (
      <>
        <Header title={intl.get('hadm.apiOverview.view.title.apiOverview').d('API统计')}>
          <C7nButton onClick={this.handleRefresh} icon="refresh">
            {intl.get('hzero.common.button.refresh').d('刷新')}
          </C7nButton>
        </Header>
        <Content>
          <div className={styles['hadm-api-overview-top-container']}>
            {this.getApiCountChart()}
            {this.getApiInvokeChart()}
          </div>
          {this.getSingleApiInvokeChart()}
        </Content>
      </>
    );
  }
}
