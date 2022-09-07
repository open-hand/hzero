/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/6/22
 * @copyright HAND ® 2020
 */
import React from 'react';
import { connect } from 'dva';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet, Modal, Button } from 'choerodon-ui/pro';
import { Spin } from 'choerodon-ui';
import notification from 'utils/notification';
import { Bind } from 'lodash-decorators';
import { operatorRender, yesOrNoRender, TagRender, numberRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { routerRedux } from 'dva/router';
import { isUndefined } from 'lodash';
import TASK_INSTANCE_LANG from '@/langs/taskInstanceLang';
import { tableDS, resultTableDS } from '@/stores/Orchestration/taskInstanceDS';
import {
  TAG_ORCH_TASK_TYPE,
  TAG_ORCH_PRIORITY_NUM,
  TAG_ORCH_INS_STATUS,
  TAG_ORCH_FAILURE_STRATEGY,
  TAG_ORCH_THREAD_MECHANISM,
} from '@/constants/constants';
import { queryLog } from '@/services/taskInstanceService';
import ResultModal from './ResultModal';

let resultModal;
@connect(({ taskInstance }) => ({
  taskInstance,
}))
@formatterCollections({ code: ['horc.taskInstance'] })
export default class List extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS());
    this.resultTableDS = new DataSet(resultTableDS());
  }

  componentDidMount() {
    this.props.dispatch({ type: 'taskInstance/init' });
  }

  /**
   * 调整到新建/明细页面
   * @param {*} id
   */
  @Bind()
  handleGotoDetail(id) {
    const { dispatch = () => {} } = this.props;
    const path = isUndefined(id) ? '/create' : `/detail/${id}`;
    dispatch(
      routerRedux.push({
        pathname: `/horc/orchestration-task-instance${path}`,
      })
    );
  }

  /**
   * 跳转到日志详情页面
   * @param {*} res
   */
  handleGoToLogDetail(res, taskName) {
    const w = window.open('', '_blank');
    w.focus();
    w.document.write(`<pre>${res.replace(/<br\/?>/gi, '\r\n')}</pre>`);
    w.document.title = taskName + TASK_INSTANCE_LANG.LOG_DETAIL;
    w.document.close();
  }

  isJsonString(str) {
    try {
      if (typeof JSON.parse(str) === 'object') {
        return true;
      }
    } catch (e) {
      return false;
    }
    return false;
  }

  /**
   * 日志详情模态框
   * @param Record
   */
  @Bind()
  openExecuteModal(record) {
    const { taskId } = record.data;
    const { taskName } = record.data;
    const modal = Modal.open({
      key: Modal.key(),
      title: TASK_INSTANCE_LANG.LOG_DETAIL,
      style: { width: '60%' },
      fullScreen: true,
      children: (
        <Spin>
          <div />
        </Spin>
      ),
      okCancel: false,
    });
    queryLog(taskId).then((res) => {
      if (this.isJsonString(res) && res.failed) {
        notification.error({
          message: res.message,
          placement: 'bottomRight',
        });
      } else {
        modal.update({
          children: (
            <pre style={{ whiteSpace: 'pre-wrap', wordWrap: 'break-word' }}>
              {res.replace(/<br\/?>/gi, '\r\n')}
            </pre>
          ),
          footer: (okBtn) => {
            return (
              <div>
                <Button
                  onClick={() => {
                    modal.close();
                    this.handleGoToLogDetail(res, taskName);
                  }}
                >
                  {TASK_INSTANCE_LANG.NEW_WINDOW_SHOW}
                </Button>
                {okBtn}
              </div>
            );
          },
        });
      }
    });
  }

  /**
   * 任务结果点击事件
   * @param record
   */
  @Bind()
  handleResultClick(record) {
    const { taskId, taskName } = record.data;
    this.openResultModal(taskId, taskName);
  }

  /**
   * 打开任务结果侧边栏
   * @param taskId
   */
  @Bind()
  openResultModal(taskId, taskName) {
    if (resultModal) {
      resultModal.close();
    }
    resultModal = Modal.open({
      key: Modal.key(),
      title: TASK_INSTANCE_LANG.TASK_RESULT,
      style: {
        width: 600,
      },
      drawer: true,
      children: (
        <Spin>
          <div />
        </Spin>
      ),
      okCancel: false,
    });
    this.resultTableDS.setQueryParameter('taskId', taskId);
    this.resultTableDS.query().then((res) => {
      if (res) {
        resultModal.update({
          children: (
            <ResultModal
              resultTableDS={this.resultTableDS}
              openResultModal={this.openResultModal}
              taskName={taskName}
            />
          ),
        });
      } else {
        resultModal.update({
          children: <div>no task result</div>,
        });
      }
    });
  }

  get serviceChoreographyColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      {
        name: 'seqNumber',
        width: 80,
        align: 'center',
        renderer: ({ record }) =>
          (this.tableDS.currentPage - 1) * this.tableDS.pageSize + record.index + 1,
      },
      {
        name: 'taskName',
        width: 200,
      },
      {
        name: 'instanceName',
        width: 380,
        renderer: ({ value, record }) => (
          // 任务实例和编排实例共用同一个明细接口，所以id传入与编排实例一样的instanceId
          <a onClick={() => this.handleGotoDetail(record.get('instanceId'))}>{value}</a>
        ),
      },
      {
        name: 'taskType',
        align: 'center',
        width: 140,
        renderer: ({ value, record }) =>
          TagRender(value, TAG_ORCH_TASK_TYPE, record.get('taskTypeMeaning')),
      },
      {
        name: 'statusCode',
        align: 'center',
        width: 140,
        renderer: ({ value }) => TagRender(value, TAG_ORCH_INS_STATUS),
      },
      {
        name: 'submittedTime',
        align: 'center',
        width: 180,
      },
      {
        name: 'startTime',
        align: 'center',
        width: 180,
      },
      {
        name: 'endTime',
        align: 'center',
        width: 180,
      },
      {
        name: 'timeConsumption',
        align: 'right',
        width: 140,
        renderer: ({ value }) => numberRender(value),
      },
      {
        name: 'timeConsumptionDesc',
        width: 200,
      },
      {
        name: 'failureStrategy',
        width: 120,
        align: 'center',
        renderer: ({ value }) => TagRender(value, TAG_ORCH_FAILURE_STRATEGY),
      },
      {
        name: 'threadMechanism',
        width: 120,
        align: 'center',
        renderer: ({ value }) => TagRender(value, TAG_ORCH_THREAD_MECHANISM),
      },
      {
        name: 'host',
        width: 200,
      },
      {
        name: 'alertFlag',
        width: 120,
        renderer: ({ value }) => yesOrNoRender(value ? 1 : 0),
      },
      {
        name: 'retryTimes',
        align: 'right',
        width: 100,
        renderer: ({ value }) => numberRender(value),
      },
      {
        name: 'instancePriority',
        align: 'center',
        width: 120,
        renderer: ({ value }) => TagRender(value, TAG_ORCH_PRIORITY_NUM),
      },
      {
        name: 'workerGroup',
        width: 120,
      },
      {
        name: 'remark',
        width: 250,
      },
      {
        header: TASK_INSTANCE_LANG.OPERATOR,
        width: 150,
        lock: 'right',
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.log`,
                      type: 'button',
                      meaning: '任务实例-日志',
                    },
                  ]}
                  onClick={() => this.openExecuteModal(record)}
                >
                  {TASK_INSTANCE_LANG.LOG}
                </ButtonPermission>
              ),
              key: 'execute',
              len: 2,
              title: TASK_INSTANCE_LANG.LOG,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.result`,
                      type: 'button',
                      meaning: '任务实例-任务结果',
                    },
                  ]}
                  onClick={() => this.handleResultClick(record)}
                >
                  {TASK_INSTANCE_LANG.TASK_RESULT}
                </ButtonPermission>
              ),
              key: 'result',
              len: 4,
              title: TASK_INSTANCE_LANG.TASK_RESULT,
            },
          ];
          return operatorRender(actions, record, { limit: 2 });
        },
      },
    ];
  }

  render() {
    return (
      <>
        <Header title={TASK_INSTANCE_LANG.HEADER} />
        <Content>
          <Table dataSet={this.tableDS} columns={this.serviceChoreographyColumns} />
        </Content>
      </>
    );
  }
}
