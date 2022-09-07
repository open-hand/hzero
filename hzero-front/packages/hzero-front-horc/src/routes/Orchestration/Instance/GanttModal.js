/**
 * 头弹窗
 * @author aaron.yi
 * @date 2020/08/11
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { DataSet, Spin } from 'choerodon-ui/pro';
import { instanceGanttDS } from '@/stores/Orchestration/instanceGanttDS';
import echarts from 'echarts';
import 'echarts/lib/chart/bar';
import 'echarts/lib/component/tooltip';
import { isUndefined } from 'lodash';
import 'echarts/lib/component/title';
import ReactEcharts from 'echarts-for-react';
import Bind from 'lodash-decorators';
import { dateTimeRender, timeRender } from 'utils/renderer';
import TASK_INSTANCE_LANG from '@/langs/taskInstanceLang';
import moment from 'moment';
import { INS_STATUS_GANTT } from '@/constants/constants';

class GanttModal extends React.Component {
  constructor(props) {
    super(props);
    this.instanceGanttDS = new DataSet(instanceGanttDS());
    this.state = {};
  }

  componentDidMount() {
    const { ganttRecord } = this.props;
    this.instanceGanttDS.setQueryParameter('instanceId', ganttRecord.get('instanceId'));
    this.instanceGanttDS.query().then((res) => {
      if (res) {
        this.setState({ instance: res });
      }
    });
  }

  /**
   * getOption
   */
  @Bind
  getOption() {
    const { instance = {} } = this.state;
    const { taskInstances = [] } = instance;
    const data = [];
    const { startTime = '' } = instance;
    const tasks = taskInstances.map((task) => task.taskName);
    const types = [
      {
        name: TASK_INSTANCE_LANG.TASK_NAME,
      },
      {
        name: TASK_INSTANCE_LANG.TASK_TYPE,
      },
      {
        name: TASK_INSTANCE_LANG.STATUS,
      },
      {
        name: TASK_INSTANCE_LANG.SUBMITTED_TIME,
      },
      {
        name: TASK_INSTANCE_LANG.START_TIME,
      },
      {
        name: TASK_INSTANCE_LANG.END_TIME,
      },
      {
        name: TASK_INSTANCE_LANG.TIME_CONSUMPTION_DESC,
      },
      {
        name: TASK_INSTANCE_LANG.HOST,
      },
      {
        name: TASK_INSTANCE_LANG.WORK_GROUP,
      },
      {
        name: TASK_INSTANCE_LANG.FAILED_STRATEGY,
      },
      {
        name: TASK_INSTANCE_LANG.THREAD_MECHANISM,
      },
    ];

    taskInstances.forEach((task, index) => {
      data.push({
        name: types.map((type) => type.name),
        value: [
          index,
          new Date(task.startTime).getTime(),
          new Date(task.endTime).getTime(),
          task.timeConsumption || '',
          task.taskName || '',
          task.taskTypeMeaning || '',
          task.statusMeaning || '',
          task.submittedTime || '',
          task.startTime || '',
          task.endTime || '',
          task.timeConsumptionDesc || '',
          task.host || '',
          task.workerGroup || '',
          task.failureStrategyMeaning || '',
          task.threadMechanismMeaning || '',
        ],
        itemStyle: {
          normal: {
            color: INS_STATUS_GANTT[task.statusCode],
          },
        },
      });
    });

    function renderItem(params, api) {
      const categoryIndex = api.value(0);
      const start = api.coord([api.value(1), categoryIndex]);
      const end = api.coord([api.value(2), categoryIndex]);
      const height = api.size([0, 1])[1] * 0.6;

      const rectShape = echarts.graphic.clipRectByRect(
        {
          x: start[0],
          y: start[1] - height / 2,
          width: end[0] - start[0],
          height,
        },
        {
          x: params.coordSys.x,
          y: params.coordSys.y,
          width: params.coordSys.width,
          height: params.coordSys.height,
        }
      );

      return (
        rectShape && {
          type: 'rect',
          shape: rectShape,
          style: api.style(),
        }
      );
    }

    const { instanceName = '' } = instance;
    const instanceStartTime = new Date(startTime).getTime();
    return {
      title: {
        text: instanceName,
        left: 'center',
      },
      tooltip: {
        formatter(params) {
          let tooltipHtml = '';
          if (!isUndefined(params.name)) {
            const tooltipData = params.name.map((n, index) => {
              return {
                name: n,
                value: params.value[index + 4],
              };
            });
            tooltipData.forEach((d) => {
              tooltipHtml += `${params.marker + d.name}: ${d.value} <br>`;
            });
          }
          return tooltipHtml;
        },
      },
      dataZoom: [
        {
          type: 'slider',
          filterMode: 'weakFilter',
          height: 20,
          showDataShadow: false,
          borderColor: 'transparent',
          handleIcon:
            'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
          handleSize: '90%',
          handleStyle: {
            color: '#fff',
            shadowBlur: 3,
            shadowColor: 'rgba(0, 0, 0, 0.6)',
            shadowOffsetX: 2,
            shadowOffsetY: 2,
          },
          labelFormatter: '',
        },
        {
          type: 'inside',
          filterMode: 'weakFilter',
        },
      ],
      grid: {
        height: document.body.clientHeight - 288,
      },
      xAxis: {
        min: instanceStartTime,
        scale: true,
        axisLabel: {
          formatter(val, index) {
            if (index === 0) {
              return dateTimeRender(moment(val));
            }
            return timeRender(moment(val));
          },
        },
      },
      yAxis: {
        data: tasks,
      },
      series: [
        {
          type: 'custom',
          renderItem,
          itemStyle: {
            opacity: 0.8,
          },
          encode: {
            x: [1, 2],
            y: 0,
          },
          data,
        },
      ],
    };
  }

  render() {
    return (
      <Spin dataSet={this.instanceGanttDS}>
        <ReactEcharts
          option={this.getOption()}
          style={{ height: document.body.clientHeight - 158 }}
          opts={{ renderer: 'svg' }}
        />
      </Spin>
    );
  }
}

export default GanttModal;
