/**
 * @date 2019-11-26
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import echarts from 'echarts';

import intl from 'utils/intl';

const optionLegendColor = '#999999';

const curveOption = {
  tooltip: {
    trigger: 'axis',
  },
  legend: {
    data: [],
    left: 'left',
    textStyle: {
      color: optionLegendColor,
    },
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '5%',
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    axisLine: {
      lineStyle: {
        color: optionLegendColor,
      },
    },
    splitLine: {
      show: false,
    },
    axisTick: {
      interval: 23,
    },
    axisLabel: {
      interval: 23,
      formatter(params) {
        const weekArr = [
          intl.get('hzero.common.week.sunday').d('周日'),
          intl.get('hzero.common.week.monday').d('周一'),
          intl.get('hzero.common.week.tuesday').d('周二'),
          intl.get('hzero.common.week.wednesday').d('周三'),
          intl.get('hzero.common.week.thursday').d('周四'),
          intl.get('hzero.common.week.friday').d('周五'),
          intl.get('hzero.common.week.saturday').d('周六'),
        ];
        const date = new Date(params);
        return `${date.getMonth() + 1}-${date.getDate()}${weekArr[date.getDay()]}`;
      },
    },
    data: [],
  },
  yAxis: {
    type: 'value',
    axisLine: {
      lineStyle: {
        color: optionLegendColor,
      },
    },
    splitLine: {
      show: false,
    },
  },
  series: [
    {
      name: '',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      itemStyle: {
        normal: {
          color: '#FFDB7C',
        },
      },
      areaStyle: {
        normal: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: '#FFF7E2',
            },
            {
              offset: 1,
              color: '#FFF7E2',
            },
          ]),
        },
      },
      data: [],
    },
    {
      name: '',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      itemStyle: {
        normal: {
          color: '#8EC9FF',
        },
      },
      areaStyle: {
        normal: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            {
              offset: 0,
              color: '#8EC9FF',
            },
            {
              offset: 1,
              color: '#8EC9EB',
            },
          ]),
        },
      },
      data: [],
    },
  ],
};

const lineOption = {
  tooltip: {
    trigger: 'axis',
  },
  legend: {
    left: 'left',
    textStyle: {
      color: optionLegendColor,
    },
    data: [],
  },
  color: [
    '#3a88ff',
    '#3af097',
    '#30aeff',
    '#6be2eb',
    '#888ff3',
    '#d09eff',
    '#ff8ad2',
    '#82ff60',
    '#ffaabb',
    '#ffaaee',
  ],
  grid: {
    left: '3%',
    right: '4%',
    bottom: '5%',
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    axisLine: {
      lineStyle: {
        color: optionLegendColor,
      },
    },
    splitLine: {
      show: false,
    },
    axisLabel: {
      formatter(params) {
        const weekArr = [
          intl.get('hzero.common.week.sunday').d('周日'),
          intl.get('hzero.common.week.monday').d('周一'),
          intl.get('hzero.common.week.tuesday').d('周二'),
          intl.get('hzero.common.week.wednesday').d('周三'),
          intl.get('hzero.common.week.thursday').d('周四'),
          intl.get('hzero.common.week.friday').d('周五'),
          intl.get('hzero.common.week.saturday').d('周六'),
        ];
        const date = new Date(params);
        return `${date.getMonth() + 1}-${date.getDate()}${weekArr[date.getDay()]}`;
      },
    },
  },
  yAxis: {
    type: 'value',
    axisLine: {
      lineStyle: {
        color: optionLegendColor,
      },
    },
    splitLine: {
      show: false,
    },
  },
  series: [],
};

export { curveOption, lineOption };
