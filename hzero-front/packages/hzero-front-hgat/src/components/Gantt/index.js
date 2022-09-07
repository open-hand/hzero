/**
 * index - 甘特图组件
 * @date: 2020-3-26
 * @author: jmy <mingyang.jin@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { gantt } from 'dhtmlx-gantt';
import axios from 'axios';
import 'dhtmlx-gantt/codebase/ext/dhtmlxgantt_tooltip.js';
import 'dhtmlx-gantt/codebase/ext/dhtmlxgantt_drag_timeline.js';
import 'dhtmlx-gantt/codebase/ext/dhtmlxgantt_multiselect.js';
import 'dhtmlx-gantt/codebase/ext/dhtmlxgantt_marker.js';
import 'dhtmlx-gantt/codebase/dhtmlxgantt.css';
// import 'dhtmlx-gantt/codebase/skins/dhtmlxgantt_skyblue.css';
// import 'dhtmlx-gantt/codebase/skins/dhtmlxgantt_meadow.css';
// import 'dhtmlx-gantt/codebase/skins/dhtmlxgantt_material.css';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';
import { HZERO_PLATFORM } from 'utils/config';
// import { queryUnifyIdpValue } from 'services/api';
import {
  deleteGanttTaskData,
  deleteGanttLinkData,
  getGanttLinkData,
  getGanttTaskData,
} from 'services/api';

import './index.less';

@formatterCollections({ code: ['hgat.dhtmlxGantt', 'entity.tenant'] })
export default class Gantt extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(gantt);
  }

  // instance of gantt.dataProcessor
  dataProcessor = null;

  componentWillUnmount() {
    if (this.dataProcessor) {
      this.dataProcessor.destructor();
      this.dataProcessor = null;
    }
  }

  componentDidMount() {
    const { locale = {}, config, templates, code, onAfterConfig = (e) => e } = this.props;
    let configObj = {};
    gantt.ext.zoom.init({
      levels: [
        {
          name: 'Days',
          scale_height: 60,
          min_column_width: 70,
          scales: [
            {
              unit: 'day',
              step: 1,
              format: `%M %d${intl.get('hgat.dhtmlxGantt.view.message.day').d('日')}`,
            },
          ],
        },
        {
          name: 'Months',
          scale_height: 60,
          min_column_width: 70,
          scales: [
            { unit: 'year', step: 1, format: '%Y' },
            { unit: 'month', step: 1, format: '%F' },
          ],
        },
      ],
    });

    // queryUnifyIdpValue('HPFM.GANTT.TASK_PRIORITY').then(resp => {
    // // 设置存储优先级数组
    // if (resp) {
    //   gantt.serverList('priority', resp.map(item => ({ label: item.meaning, key: item.value })));
    // }
    gantt.config.lightbox.sections = [
      { name: 'description', height: 38, map_to: 'text', type: 'textarea', focus: true },
      // {
      //   name: 'priority',
      //   height: 22,
      //   map_to: 'priority',
      //   type: 'select',
      //   options: gantt.serverList('priority'),
      // },
      {
        name: 'split',
        type: 'checkbox',
        map_to: 'render',
        options: [
          {
            key: 'split',
            label: intl.get('hgat.dhtmlxGantt.view.message.splitTask').d('任务分割'),
          },
        ],
      },
      { name: 'period', height: 72, map_to: 'auto', type: 'time' },
    ];

    gantt.config.date_format = '%Y-%m-%d %H:%i';
    gantt.config = { ...gantt.config, ...config };
    gantt.templates = { ...gantt.templates, ...templates };

    gantt.templates.progress_text = function renderProgressText(start, end, task) {
      return `${(task.progress * 100).toFixed(2)}%`;
    };

    gantt.locale = {
      date: {
        month_full: [
          intl.get('hgat.dhtmlxGantt.view.message.january').d('一月'),
          intl.get('hgat.dhtmlxGantt.view.message.february').d('二月'),
          intl.get('hgat.dhtmlxGantt.view.message.march').d('三月'),
          intl.get('hgat.dhtmlxGantt.view.message.april').d('四月'),
          intl.get('hgat.dhtmlxGantt.view.message.mayday').d('五月'),
          intl.get('hgat.dhtmlxGantt.view.message.june').d('六月'),
          intl.get('hgat.dhtmlxGantt.view.message.july').d('七月'),
          intl.get('hgat.dhtmlxGantt.view.message.august').d('八月'),
          intl.get('hgat.dhtmlxGantt.view.message.september').d('九月'),
          intl.get('hgat.dhtmlxGantt.view.message.october').d('十月'),
          intl.get('hgat.dhtmlxGantt.view.message.november').d('十一月'),
          intl.get('hgat.dhtmlxGantt.view.message.december').d('十二月'),
        ],
        month_short: [
          intl.get('hgat.dhtmlxGantt.view.message.jan').d('1月'),
          intl.get('hgat.dhtmlxGantt.view.message.feb').d('2月'),
          intl.get('hgat.dhtmlxGantt.view.message.mar').d('3月'),
          intl.get('hgat.view.message.apr').d('4月'),
          intl.get('hgat.dhtmlxGantt.view.message.may').d('5月'),
          intl.get('hgat.dhtmlxGantt.view.message.jun').d('6月'),
          intl.get('hgat.dhtmlxGantt.view.message.jul').d('7月'),
          intl.get('hgat.dhtmlxGantt.view.message.aug').d('8月'),
          intl.get('hgat.dhtmlxGantt.view.message.sep').d('9月'),
          intl.get('hgat.dhtmlxGantt.view.message.oct').d('10月'),
          intl.get('hgat.dhtmlxGantt.view.message.nov').d('11月'),
          intl.get('hgat.dhtmlxGantt.view.message.dec').d('12月'),
        ],
        day_full: [
          intl.get('hgat.dhtmlxGantt.view.message.sunday').d('星期日'),
          intl.get('hgat.dhtmlxGantt.view.message.monday').d('星期一'),
          intl.get('hgat.dhtmlxGantt.view.message.tuesday').d('星期二'),
          intl.get('hgat.dhtmlxGantt.view.message.wednesday').d('星期三'),
          intl.get('hgat.dhtmlxGantt.view.message.thursday').d('星期四'),
          intl.get('hgat.dhtmlxGantt.view.message.friday').d('星期五'),
          intl.get('hgat.dhtmlxGantt.view.message.saturday').d('星期六'),
        ],
        day_short: [
          intl.get('hgat.dhtmlxGantt.view.message.sun').d('日'),
          intl.get('hgat.dhtmlxGantt.view.message.mon').d('一'),
          intl.get('hgat.dhtmlxGantt.view.message.tue').d('二'),
          intl.get('hgat.dhtmlxGantt.view.message.wed').d('三'),
          intl.get('hgat.dhtmlxGantt.view.message.thu').d('四'),
          intl.get('hgat.dhtmlxGantt.view.message.fri').d('五'),
          intl.get('hgat.dhtmlxGantt.view.message.sat').d('六'),
        ],
        ...(locale.date || {}),
      },
      labels: {
        dhx_cal_today_button: intl.get('hgat.dhtmlxGantt.view.message.today').d('今天'),
        day_tab: intl.get('hgat.dhtmlxGantt.view.message.day').d('日'),
        week_tab: intl.get('hgat.dhtmlxGantt.view.message.week').d('周'),
        month_tab: intl.get('hgat.dhtmlxGantt.view.message.month').d('月'),
        new_task: intl.get('hgat.dhtmlxGantt.view.message.newTask').d('新任务'),
        new_event: intl.get('hgat.dhtmlxGantt.view.message.create').d('新建日程'),
        icon_save: intl.get('hzero.common.button.save').d('保存'),
        icon_cancel: intl.get('hzero.common.button.close').d('关闭'),
        icon_details: intl.get('hgat.dhtmlxGantt.view.message.detail').d('详细'),
        icon_edit: intl.get('hzero.common.button.edit').d('编辑'),
        icon_delete: intl.get('hzero.common.button.delete').d('删除'),
        confirm_closing: intl.get('hgat.dhtmlxGantt.view.confirm.cancel').d('是否撤销修改!'), // Your changes will be lost, are your sure?
        confirm_deleting: intl.get('hgat.dhtmlxGantt.view.confirm.delete').d('是否删除日程?'),
        section_priority: intl.get('hgat.dhtmlxGantt.view.message.priority').d('优先级'),
        section_description: intl.get('hgat.dhtmlxGantt.view.message.description').d('描述'),
        section_split: intl.get('hgat.dhtmlxGantt.view.message.split').d('展示'),
        section_time: intl.get('hgat.dhtmlxGantt.view.message.dateRange').d('时间范围'),
        section_type: intl.get('hgat.dhtmlxGantt.view.message.type').d('类型'),
        section_constraint: intl.get('hgat.dhtmlxGantt.view.message.constraint').d('约束'),
        section_period: intl.get('hgat.dhtmlxGantt.view.message.dateRange').d('时间范围'),
        constraint_type: intl.get('hgat.dhtmlxGantt.view.message.constraintType').d('约束类型'),
        constraint_date: intl.get('hgat.dhtmlxGantt.view.message.constraintDate').d('约束日期'),
        message_ok: intl.get('hzero.common.button.ok').d('确定'),
        message_cancel: intl.get('hzero.common.button.cancel').d('取消'),
        gantt_save_btn: intl.get('hzero.common.button.save').d('保存'),
        gantt_cancel_btn: intl.get('hzero.common.button.cancel').d('取消'),
        gantt_delete_btn: intl.get('hzero.common.button.delete').d('删除'),
        /* grid columns */
        column_wbs: intl.get('hgat.dhtmlxGantt.view.message.WBS').d('工作分解结构'),
        column_text: intl.get('hgat.dhtmlxGantt.view.message.taskName').d('任务名'),
        column_start_date: intl.get('hgat.dhtmlxGantt.view.message.startDate').d('开始时间'),
        column_duration: intl.get('hgat.dhtmlxGantt.view.message.duration').d('持续时间'),
        column_add: '',

        resources_filter_placeholder: intl.get('hgat.dhtmlxGantt.view.message.ttf').d('过滤类型'),
        resources_filter_label: intl.get('hgat.dhtmlxGantt.view.message.he').d('隐藏空'),
        /* link confirmation */

        link: intl.get('hgat.dhtmlxGantt.view.message.link').d('关联'),
        confirm_link_deleting: intl.get('hgat.dhtmlxGantt.view.confirm.deleteLink').d('是否删除'),
        link_from: intl.get('hgat.dhtmlxGantt.view.message.from').d('来自'),
        link_to: intl.get('hgat.dhtmlxGantt.view.message.to').d('到'),
        link_start: intl.get('hgat.dhtmlxGantt.view.message.start').d(' (开始)'),
        link_end: intl.get('hgat.dhtmlxGantt.view.message.end').d(' (结束)'),

        type_task: intl.get('hgat.dhtmlxGantt.view.message.task').d('任务'),
        type_project: intl.get('hgat.dhtmlxGantt.view.message.project').d('项目'),
        type_milestone: intl.get('hgat.dhtmlxGantt.view.message.milestone').d('里程碑'),

        minutes: intl.get('hgat.dhtmlxGantt.view.message.minutes').d('分钟'),
        hours: intl.get('hgat.dhtmlxGantt.view.message.hours').d('小时'),
        days: intl.get('hgat.dhtmlxGantt.view.message.days').d('天'),
        weeks: intl.get('hgat.dhtmlxGantt.view.message.weeks').d('周'),
        months: intl.get('hgat.dhtmlxGantt.view.message.months').d('月'),
        years: intl.get('hgat.dhtmlxGantt.view.message.years').d('年'),
        ...(locale.labels || {}),
      },
    };

    if (code) {
      axios({
        url: isTenantRoleLevel()
          ? `${HZERO_PLATFORM}/v1/${getCurrentOrganizationId()}/gantt-configs/by-code`
          : `${HZERO_PLATFORM}/v1/gantt-configs/by-code`,
        method: 'GET',
        params: { ganttCode: code },
      })
        .then((res) => {
          if (res) {
            gantt.serverList('ganttId', [res.ganttId]);
            configObj = res.ganttConfigList.map((item) => ({
              key: item.configCode,
              value: item.configValue,
            }));
            // if (configObj.filter(item => item.key === 'SKIN_STYLE').length === 0) {
            //   require('dhtmlx-gantt/codebase/dhtmlxgantt.css');
            // }
            configObj.map((item) => {
              switch (item.key) {
                case 'DOUBLE_MODE':
                  if (item.value !== 'true') {
                    gantt.config.details_on_dblclick = false;
                    const colContent = function colContent(task) {
                      return (
                        `${
                          '<i class="anticon anticon-edit gantt_button_grid gantt_grid fa fa-pencil"' +
                          'onclick="gantt.showLightbox('
                        }'${task.id}')"></i>` +
                        `<i class="anticon anticon-plus gantt_button_grid gantt_grid fa fa-plus ${
                          task.objectVersionNumber ? '' : 'fa-hidden'
                        }"` +
                        `onclick="gantt.createTask(null,'${task.id}')"></i>` +
                        `<i class="anticon anticon-minus gantt_button_grid gantt_grid fa fa-times"` +
                        `onclick="gantt.confirm({title: gantt.locale.labels.confirm_deleting_title,text: gantt.locale.labels.confirm_deleting,callback: function (res) {if (res)gantt.deleteTask('${task.id}');}})"></i>`
                      );
                    };

                    const colHeader =
                      '<div class="gantt_grid_head_cell gantt_grid_head_add" onclick="gantt.createTask()"></div>';
                    gantt.config.columns = [
                      { name: 'wbs', label: 'WBS', width: '100', template: gantt.getWBSCode },
                      { name: 'text', tree: true, width: '110' },
                      { name: 'start_date', align: 'center', width: 100 },
                      { name: 'duration', align: 'center', width: 100 },
                      { name: 'buttons', label: colHeader, width: 100, template: colContent },
                    ];
                  } else {
                    const addContent = function addContent(task) {
                      return `<i class="anticon anticon-plus gantt_button_grid gantt_grid fa fa-plus ${
                        task.objectVersionNumber ? '' : 'fa-hidden'
                      }"onclick="gantt.createTask(null,'${task.id}')"></i>`;
                    };
                    const colHeader =
                      '<div class="gantt_grid_head_cell gantt_grid_head_add" onclick="gantt.createTask()"></div>';
                    gantt.config.details_on_dblclick = true;
                    gantt.config.columns = [
                      { name: 'wbs', label: 'WBS', width: '100', template: gantt.getWBSCode },
                      { name: 'text', tree: true, width: '110' },
                      { name: 'start_date', align: 'center', width: 100 },
                      { name: 'duration', align: 'center', width: 100 },
                      { name: 'buttons', width: 100, label: colHeader, template: addContent },
                    ];
                  }
                  break;
                case 'TIME_DISPLAY_MODE':
                  if (item.value === 'month') {
                    gantt.ext.zoom.setLevel('Months');
                  }
                  break;
                case 'GANTT_LAYOUT':
                  if (item.value === 'top_and_down') {
                    gantt.config.layout = {
                      css: 'gantt_container',
                      rows: [
                        {
                          cols: [
                            { resizer: true, width: 1 },
                            {
                              view: 'timeline',
                              id: 'timeline',
                              scrollX: 'scrollHor',
                              scrollY: 'scrollVer',
                            },
                            { view: 'scrollbar', scroll: 'y', id: 'scrollVer' },
                          ],
                        },
                        { view: 'grid', id: 'grid', scrollX: 'scrollHor', scrollY: 'scrollVer' },
                        { view: 'scrollbar', scroll: 'x', id: 'scrollHor', height: 20 },
                      ],
                    };
                  } else if (item.value === 'down_and_top') {
                    gantt.config.layout = {
                      css: 'gantt_container',
                      rows: [
                        { view: 'grid', id: 'grid', scrollX: 'scrollHor', scrollY: 'scrollVer' },
                        {
                          cols: [
                            { resizer: true, width: 1 },
                            {
                              view: 'timeline',
                              id: 'timeline',
                              scrollX: 'scrollHor',
                              scrollY: 'scrollVer',
                            },
                            { view: 'scrollbar', scroll: 'y', id: 'scrollVer' },
                          ],
                        },
                        { view: 'scrollbar', scroll: 'x', id: 'scrollHor', height: 20 },
                      ],
                    };
                  } else if (item.value === 'left_and_rignt') {
                    gantt.config.layout = {
                      css: 'gantt_container',
                      rows: [
                        {
                          cols: [
                            { resizer: true, width: 1 },
                            { view: 'scrollbar', scroll: 'y', id: 'scrollVer' },
                            {
                              view: 'timeline',
                              id: 'timeline',
                              scrollX: 'scrollHor',
                              scrollY: 'scrollVer',
                            },
                            {
                              view: 'grid',
                              id: 'grid',
                              scrollX: 'scrollHor',
                              scrollY: 'scrollVer',
                            },
                          ],
                        },
                        { view: 'scrollbar', scroll: 'x', id: 'scrollHor', height: 20 },
                      ],
                    };
                  } else {
                    gantt.config.layout = {
                      css: 'gantt_container',
                      rows: [
                        {
                          cols: [
                            {
                              view: 'grid',
                              id: 'grid',
                              scrollX: 'scrollHor',
                              scrollY: 'scrollVer',
                            },
                            { resizer: true, width: 1 },
                            {
                              view: 'timeline',
                              id: 'timeline',
                              scrollX: 'scrollHor',
                              scrollY: 'scrollVer',
                            },
                            { view: 'scrollbar', scroll: 'y', id: 'scrollVer' },
                          ],
                        },
                        { view: 'scrollbar', scroll: 'x', id: 'scrollHor', height: 20 },
                      ],
                    };
                  }
                  break;
                case 'READONLY':
                  if (item.value !== 'false') {
                    gantt.config.readonly = true;
                    gantt.config.columns = [
                      { name: 'wbs', label: 'WBS', width: '100', template: gantt.getWBSCode },
                      { name: 'text', tree: true, width: '110' },
                      { name: 'start_date', align: 'center', width: 100 },
                      { name: 'duration', align: 'center', width: 100 },
                    ];
                  } else {
                    gantt.config.readonly = false;
                  }
                  break;
                case 'SKIN_STYLE':
                  // if (item.value === 'teracce') {
                  //   require('dhtmlx-gantt/codebase/dhtmlxgantt.css');
                  // } else {
                  //   try {
                  //     // eslint-disable-next-line
                  //     require(`dhtmlx-gantt/codebase/skins/dhtmlxgantt_${item.value}.css`);
                  //   } catch {
                  //     require('dhtmlx-gantt/codebase/dhtmlxgantt.css');
                  //   }
                  // }
                  break;
                case 'EXPEND_TREE':
                  if (item.value !== 'false') {
                    gantt.config.open_tree_initially = true;
                  } else {
                    gantt.config.open_tree_initially = false;
                  }
                  break;
                // case 'CREATE_TASK_START_DATE':
                //   gantt.config.start_date = new Date(item.value);
                //   break;
                // case 'CREATE_TASK_END_DATE':
                //   gantt.config.end_date = new Date(item.value);
                //   break;
                case 'FIT_TASKS':
                  if (item.value !== 'false') {
                    gantt.config.fit_tasks = true;
                  } else {
                    gantt.config.fit_tasks = false;
                  }
                  break;
                case 'ENABLE_AUTO_SCROLL':
                  if (item.value !== 'true') {
                    gantt.config.autoscroll = false;
                  } else {
                    gantt.config.autoscroll = true;
                  }
                  break;
                case 'ROUND_DND_DATES':
                  if (item.value !== 'true') {
                    gantt.config.round_dnd_dates = false;
                  } else {
                    gantt.config.round_dnd_dates = true;
                  }
                  break;
                case 'SHOW_TASKS_OUTSIDE_TIMESCALE':
                  if (item.value !== 'true') {
                    gantt.config.show_tasks_outside_timescale = false;
                  } else {
                    gantt.config.show_tasks_outside_timescale = true;
                  }
                  break;
                default:
                  break;
              }
              return true;
            });

            gantt.init(this.ganttContainer);
            gantt.resetLayout();
            gantt.render();
            onAfterConfig();
            // 添加onchange事件
            this.initGanttDataProcessor();
          } else {
            // require('dhtmlx-gantt/codebase/dhtmlxgantt.css');
          }
        })
        .catch((err) => {
          // require('dhtmlx-gantt/codebase/dhtmlxgantt.css');
          notification.error({
            message: err.message,
          });
        });
    }
    // }
    // );

    // gantt.templates.task_class = function renderTaskClass(start, end, task) {
    //     if (task.priority === '1') {
    //         return styles['gantt_task_low']
    //     } else if (task.priority === '2') {
    //         return styles['gantt_task_normal']
    //     } else if (task.priority === '3') {
    //         return styles['gantt_task_high']
    //     } else {
    //         return ''
    //     }
    // };
  }

  @Bind()
  initGanttDataProcessor() {
    /**
     * type: "task"|"link"
     * action: "create"|"update"|"delete"
     * item: data object object
     */
    this.dataProcessor = gantt.createDataProcessor(
      (type, action, item, id) =>
        new Promise((resolve) => {
          if (!this.props.onChange) {
            // 监听onchange事件
            this.props.onChange(type, action, item, id);
          } else if (action === 'delete') {
            if (item.objectVersionNumber) {
              if (type === 'task') {
                deleteGanttTaskData(item).then((res) => {
                  if (res) {
                    notification.success();
                    getGanttTaskData(gantt).then((res1) => {
                      getGanttLinkData(gantt).then((resp) => {
                        gantt.clearAll();
                        gantt.parse({ data: res1, links: resp });
                      });
                    });
                  }
                });
              } else {
                deleteGanttLinkData(item).then((res) => {
                  if (res) {
                    notification.success();
                    getGanttTaskData(gantt).then((res1) => {
                      getGanttLinkData(gantt).then((resp) => {
                        gantt.clearAll();
                        gantt.parse({ data: res1, links: resp });
                      });
                    });
                  }
                });
              }
            }
          }
          return resolve();
        })
    );
  }

  render() {
    return (
      <>
        <div
          ref={(input) => {
            this.ganttContainer = input;
          }}
          style={{ width: '100%', height: '500px' }}
        />
      </>
    );
  }
}
