import G6 from '@antv/g6';
import '@antv/g6/build/plugin.tool.tooltip';
import { isEmpty } from 'lodash';
import TASK_INSTANCE_LANG from '@/langs/taskInstanceLang';
import { INS_STATUS_GANTT } from '@/constants/constants';

const titleNames = [
  {
    field: 'taskName',
    name: TASK_INSTANCE_LANG.TASK_NAME,
  },
  {
    field: 'taskTypeMeaning',
    name: TASK_INSTANCE_LANG.TASK_TYPE,
  },
  {
    field: 'statusMeaning',
    name: TASK_INSTANCE_LANG.STATUS,
  },
  {
    field: 'submittedTime',
    name: TASK_INSTANCE_LANG.SUBMITTED_TIME,
  },
  {
    field: 'startTime',
    name: TASK_INSTANCE_LANG.START_TIME,
  },
  {
    field: 'endTime',
    name: TASK_INSTANCE_LANG.END_TIME,
  },
  {
    field: 'timeConsumptionDesc',
    name: TASK_INSTANCE_LANG.TIME_CONSUMPTION_DESC,
  },
  {
    field: 'failureStrategyMeaning',
    name: TASK_INSTANCE_LANG.FAILURE_STRATEGY,
  },
  {
    field: 'threadMechanismMeaning',
    name: TASK_INSTANCE_LANG.THREAD_MECHANISM,
  },
  {
    field: 'host',
    name: TASK_INSTANCE_LANG.HOST,
  },
  {
    field: 'workgroup',
    name: TASK_INSTANCE_LANG.WORK_GROUP,
  },
];

// eslint-disable-next-line new-cap
const tooltip = new G6.Plugins['tool.tooltip']({
  dx: 10,
  dy: 10,
  getTooltip({ item }) {
    if (item && item.type === 'node') {
      const model = item.getModel();
      const { updatedMessage } = model;
      if (isEmpty(updatedMessage)) {
        return '';
      }
      let lis = '';
      titleNames.forEach((data) => {
        const { field, name } = data;
        const color = INS_STATUS_GANTT[updatedMessage.statusCode] || '#b4bac2';
        if (updatedMessage[field]) {
          lis += `<li style="font-size: 12px;list-style-type: none;"><span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:${color};"></span><span>${name}</span>: ${updatedMessage[field]}</li>`;
        }
      });

      return `
              <div class="g6-tooltip" style="
                position: absolute;
                white-space: nowrap;
                zIndex: 8;
                box-shadow: 0px 0px 10px #aeaeae;
                line-height: 20px;
                transition: left 0.4s cubic-bezier(0.23, 1, 0.32, 1) 0s, top 0.4s cubic-bezier(0.23, 1, 0.32, 1) 0s;
                background-color: rgba(50, 50, 50, 0.7);
                border-width: 0px;
                border-color: rgb(51, 51, 51);
                border-radius: 4px;
                color: rgb(255, 255, 255);
                font: 14px / 21px sans-serif;
                padding: 5px;
                pointer-events: none;
              ">
                <ul class="g6-tooltip-list" style="
                  padding: 0;
                  margin: 0;
                  margin-top: 4px;
                ">
                  ${lis}
                </ul>
              </div>
            `;
    }
  },
});

export { tooltip };
