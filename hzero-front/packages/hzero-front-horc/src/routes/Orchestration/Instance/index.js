/**
 * @author HBT <baitao.huang@hand-china.com>
 * @creationDate 2020/6/10
 * @copyright HAND ® 2020
 */
import React from 'react';
import { connect } from 'dva';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import { Table, DataSet, Modal } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { operatorRender, yesOrNoRender, TagRender, numberRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { routerRedux } from 'dva/router';
import { isUndefined } from 'lodash';
import { getResponse } from 'utils/utils';
import INSTANCE_LANG from '@/langs/instanceLang';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import { tableDS } from '@/stores/Orchestration/instanceDS';
import {
  TAG_ORCH_INS_STATUS,
  TAG_ORCH_STATEMENT_TYPE,
  TAG_ORCH_FAILURE_STRATEGY,
  INSTANCE_BUTTONS,
  ORCH_INS_STATUS,
} from '@/constants/constants';
import GanttModal from './GanttModal';

@connect(({ instance }) => ({
  instance,
}))
@formatterCollections({ code: ['horc.instance'] })
export default class List extends React.Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(tableDS());
  }

  componentDidMount() {
    this.props.dispatch({ type: 'instance/init' });
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
        pathname: `/horc/orchestration-instance${path}`,
      })
    );
  }

  /**
   * 上线/下线
   */
  @Bind()
  async handleToggle(record, requestType) {
    record.set('_requestType', requestType);
    const confirm = await Modal.confirm({
      children: (
        <p>
          {requestType === 'online' ? INSTANCE_LANG.ONLINE_CONFIRM : INSTANCE_LANG.OFFLINE_CONFIRM}
        </p>
      ),
    });
    if (confirm === 'ok') {
      const res = await this.tableDS.submit();
      if (getResponse(res)) {
        await this.tableDS.query();
      }
    }
  }

  /**
   * 按钮状态切换判定
   * @param status 状态
   * @param buttonType 按钮类型
   * @returns {boolean}
   */
  @Bind
  buttonStatusToggle(status, buttonType) {
    let disabled = false;
    switch (buttonType) {
      case INSTANCE_BUTTONS.PAUSE:
        // 正在运行 时，亮起
        disabled = status !== ORCH_INS_STATUS.RUNNING;
        break;
      case INSTANCE_BUTTONS.RESUME:
        // 已失败 时，灰掉
        disabled = status !== ORCH_INS_STATUS.PAUSED;
        break;
      case INSTANCE_BUTTONS.STOP:
        // 正在运行 时，亮起
        disabled = status !== ORCH_INS_STATUS.RUNNING;
        break;
      case INSTANCE_BUTTONS.DELETE:
      case INSTANCE_BUTTONS.RERUN:
      case INSTANCE_BUTTONS.EDIT:
        // 非（已成功 或 已暂停 或 已失败 或已停止） 时，灰掉
        disabled =
          status !== ORCH_INS_STATUS.SUCCESSFUL &&
          status !== ORCH_INS_STATUS.PAUSED &&
          status !== ORCH_INS_STATUS.FAILED &&
          status !== ORCH_INS_STATUS.STOPPED;
        break;
      default:
        return !disabled;
    }
    return disabled;
  }

  /**
   * 处理
   */
  @Bind()
  async handleExecute(record, buttonType) {
    record.set('_buttonType', buttonType);
    const res = await this.tableDS.submit();
    if (getResponse(res)) {
      await this.tableDS.query();
    }
  }

  /**
   * 处理
   */
  @Bind()
  handleGantt(record) {
    const modalProps = {
      ganttRecord: record,
    };
    this.executeModal = Modal.open({
      title: ORCHESTRATION_LANG.GANTT_TITLE,
      drawer: false,
      fullScreen: true,
      closable: true,
      destroyOnClose: true,
      okCancel: false,
      okText: INSTANCE_LANG.CLOSE,
      style: { width: '75%' },
      children: <GanttModal {...modalProps} />,
      // footer: (_okBtn, cancelBtn) => <div style={{ textAlign: 'right' }}>{cancelBtn}</div>,
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
        name: 'instanceName',
        width: 380,
        renderer: ({ value, record }) => (
          <a onClick={() => this.handleGotoDetail(record.get('instanceId'))}>{value}</a>
        ),
      },
      {
        name: 'statusCode',
        width: 140,
        align: 'center',
        renderer: ({ value }) => TagRender(value, TAG_ORCH_INS_STATUS),
      },
      {
        name: 'statementType',
        width: 160,
        align: 'center',
        renderer: ({ value }) => TagRender(value, TAG_ORCH_STATEMENT_TYPE),
      },
      {
        name: 'statementStartTime',
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
        width: 220,
      },
      {
        name: 'failoverFlag',
        width: 120,
        renderer: ({ value }) => yesOrNoRender(value ? 1 : 0),
      },
      {
        name: 'failureStrategy',
        width: 120,
        align: 'center',
        renderer: ({ value }) => TagRender(value, TAG_ORCH_FAILURE_STRATEGY),
      },
      {
        name: 'host',
        width: 200,
      },
      {
        name: 'workerGroup',
        width: 100,
      },
      {
        header: INSTANCE_LANG.OPERATOR,
        width: 360,
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
                      code: `${path}.button.rerun`,
                      type: 'button',
                      meaning: '编排实例-重跑',
                    },
                  ]}
                  disabled={this.buttonStatusToggle(
                    record.get('statusCode'),
                    INSTANCE_BUTTONS.RERUN
                  )}
                  onClick={() => this.handleExecute(record, INSTANCE_BUTTONS.RERUN)}
                >
                  {INSTANCE_LANG.RERUN}
                </ButtonPermission>
              ),
              key: 'rerun',
              len: 2,
              title: INSTANCE_LANG.RERUN,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.pause`,
                      type: 'button',
                      meaning: '编排实例-暂停',
                    },
                  ]}
                  disabled={this.buttonStatusToggle(
                    record.get('statusCode'),
                    INSTANCE_BUTTONS.PAUSE
                  )}
                  onClick={() => this.handleExecute(record, INSTANCE_BUTTONS.PAUSE)}
                >
                  {INSTANCE_LANG.PAUSE}
                </ButtonPermission>
              ),
              key: 'pause',
              len: 2,
              title: INSTANCE_LANG.PAUSE,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.stop`,
                      type: 'button',
                      meaning: '编排实例-停止',
                    },
                  ]}
                  disabled={this.buttonStatusToggle(
                    record.get('statusCode'),
                    INSTANCE_BUTTONS.STOP
                  )}
                  onClick={() => this.handleExecute(record, INSTANCE_BUTTONS.STOP)}
                >
                  {INSTANCE_LANG.STOP}
                </ButtonPermission>
              ),
              key: 'stop',
              len: 2,
              title: INSTANCE_LANG.STOP,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.resume`,
                      type: 'button',
                      meaning: '编排实例-恢复',
                    },
                  ]}
                  disabled={this.buttonStatusToggle(
                    record.get('statusCode'),
                    INSTANCE_BUTTONS.RESUME
                  )}
                  onClick={() => this.handleExecute(record, INSTANCE_BUTTONS.RESUME)}
                >
                  {INSTANCE_LANG.RESUME}
                </ButtonPermission>
              ),
              key: 'resume',
              len: 2,
              title: INSTANCE_LANG.RESUME,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.gantt`,
                      type: 'button',
                      meaning: '编排实例-甘特图',
                    },
                  ]}
                  onClick={() => this.handleGantt(record)}
                >
                  {INSTANCE_LANG.GANTT}
                </ButtonPermission>
              ),
              key: 'gantt',
              len: 4,
              title: INSTANCE_LANG.GANTT,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '编排定义-编辑',
                    },
                  ]}
                  disabled={this.buttonStatusToggle(
                    record.get('statusCode'),
                    INSTANCE_BUTTONS.EDIT
                  )}
                  onClick={() => this.handleGotoDetail(record.get('instanceId'))}
                >
                  {INSTANCE_LANG.EDIT}
                </ButtonPermission>
              ),
              key: 'edit',
              len: 2,
              title: INSTANCE_LANG.EDIT,
            },
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '编排定义-删除',
                    },
                  ]}
                  disabled={this.buttonStatusToggle(
                    record.get('statusCode'),
                    INSTANCE_BUTTONS.DELETE
                  )}
                  onClick={() => this.tableDS.delete(record)}
                >
                  {INSTANCE_LANG.DELETE}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: INSTANCE_LANG.DELETE,
            },
          ];
          return operatorRender(actions, record, { limit: 8 });
        },
      },
    ];
  }

  render() {
    return (
      <>
        <Header title={INSTANCE_LANG.HEADER} />
        <Content>
          <Table dataSet={this.tableDS} columns={this.serviceChoreographyColumns} />
        </Content>
      </>
    );
  }
}
