import React, { Component } from 'react';
import { connect } from 'dva';
import { Tabs, Button } from 'hzero-ui';
import classnames from 'classnames';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';
import 'codemirror/mode/clike/clike'; // java 样式
import 'codemirror/mode/javascript/javascript'; // javascript/json 样式

import CodeMirror from 'components/CodeMirror';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import sagaImg from './SagaImg.less';

const { TabPane } = Tabs;

@connect(({ loading, sagaInstance }) => ({
  sagaInstance,
  unLockLoading: loading.effects['sagaInstance/instanceUnlock'],
  retryLoading: loading.effects['sagaInstance/instanceRetry'],
}))
@formatterCollections({ code: ['hagd.sagaInstance', 'hagd.saga', 'hagd.sagaImage'] })
export default class SagaImg extends Component {
  constructor(props) {
    super(props);
    this.state = this.getInitState();
  }

  @Bind()
  getInitState() {
    const { instance, data } = this.props;
    return {
      showDetail: false,
      task: {},
      json: '',
      lineData: {},
      activeCode: '',
      activeTab: instance ? 'run' : '',
      jsonTitle: false, // 是否展示input output
      data,
      intervals: [],
    };
  }

  // eslint-disable-next-line camelcase
  UNSAFE_componentWillMount() {
    const {
      data: { tasks },
    } = this.state;
    this.getLineData(tasks);
  }

  componentWillUnmount() {
    const { intervals } = this.state;
    clearInterval(intervals);
  }

  // eslint-disable-next-line
  UNSAFE_componentWillReceiveProps(nextProps) {
    const {
      data: { tasks },
    } = nextProps;
    // const { data } = this.props;
    this.setState(this.getInitState(), () => {
      this.setState({ data: nextProps.data });
      this.getLineData(tasks);
    });
  }

  /**
   * 获取节点数据
   */
  @Bind()
  getLineData(tasks = []) {
    const lineData = {};
    const {
      task: { code, taskCode },
    } = this.state;
    tasks.forEach(items =>
      items.forEach(task => {
        lineData[task.code || task.taskCode] = task;
      })
    );
    const task = { ...lineData[code || taskCode] };
    this.setState({
      lineData,
      task,
    });
  }

  /**
   * 绘制输入输出节点
   * @param {string} code - 节点类型
   */
  @Bind()
  circleWrapper(code) {
    const { activeCode } = this.state;
    const { instance } = this.props;
    const clsNames = classnames(sagaImg['c7n-saga-img-circle'], {
      [sagaImg['c7n-saga-task-active']]: code.toLowerCase() === activeCode,
      output: !instance && code === 'Output',
    });
    return (
      <div
        className={clsNames}
        onClick={this.showDetail.bind(this, code.toLowerCase())}
        key={uuid()}
      >
        {code}
      </div>
    );
  }

  /**
   * 绘制流程节点
   * @param {string} node - 节点
   * @param {string} status - 状态
   */
  @Bind()
  squareWrapper(node, status = '') {
    if (typeof node === 'string') {
      const { instance } = this.props;
      const { activeCode } = this.state;
      const clsNames = classnames(sagaImg['c7n-saga-img-square'], {
        [sagaImg['c7n-saga-task-active']]: node === activeCode,
        [sagaImg[status.toLowerCase()]]: !!instance,
      });
      return (
        <div
          className={clsNames}
          onClick={this.showDetail.bind(this, node)}
          key={`${node}-${uuid()}`}
        >
          <span>{node}</span>
        </div>
      );
    }
    return (
      <div className={sagaImg['c7n-saga-img-squares']} key={`${node}-${uuid()}`}>
        {node}
      </div>
    );
  }

  /**
   * 显示流程节点详情
   * @param {string} code - 节点类型
   */
  @Bind()
  showDetail(code) {
    const { instance } = this.props;
    // 不渲染非实例的输出节点
    if (!instance && code === 'output') {
      return;
    }
    // 渲染输入输出类型的节点详情
    if (code === 'input' || code === 'output') {
      const { data } = this.state;
      this.setState({
        showDetail: false,
        jsonTitle:
          code === 'input'
            ? intl.get('hagd.sagaImage.view.title.input').d('输入数据')
            : intl.get('hagd.sagaImage.view.title.output').d('输出数据'),
        json: data[code],
        activeCode: code,
      });
      return;
    }
    const { lineData } = this.state;
    const task = { ...lineData[code] };
    this.setState({
      showDetail: true,
      jsonTitle: false,
      task,
      activeCode: code,
    });
  }

  /**
   * 切换tab
   * @param {string} activeTab - 当前显示tab code
   */
  @Bind()
  handleTabChange(activeTab) {
    const { instance } = this.props;
    this.setState({
      activeTab: instance ? activeTab : '',
    });
  }

  @Bind()
  reload() {
    const {
      data: { id },
      intervals,
    } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'sagaInstance/queryInstanceRun',
      payload: { id },
    }).then(res => {
      if (res) {
        const { tasks, status } = res;
        this.setState({ data: res });
        this.getLineData(tasks);
        if (status !== 'RUNNING') {
          clearInterval(intervals);
        }
      }
    });
  }

  @Bind()
  handleUnLock() {
    const { dispatch } = this.props;
    const { task } = this.state;
    // onUnLock();
    dispatch({
      type: 'sagaInstance/instanceUnlock',
      payload: { id: task.id },
    }).then(res => {
      if (res) {
        notification.success();
        this.reload();
      }
    });
    // 操作之后获取节点数据
    // this.reload();
    // this.getLineData(tasks);
    // todo 事务实例解锁操作
  }

  @Bind()
  handleRetry() {
    const { onRetry = e => e } = this.props;
    const {
      data: { tasks = [] },
    } = this.state;
    onRetry();
    // 操作之后获取节点数据
    this.getLineData(tasks);
  }

  // // todo 复制异常信息
  // @Bind()
  // handleCopy() {
  //   const {
  //     task: { exceptionMessage },
  //   } = this.state;
  //   const failed = document.getElementById('failed');
  //   failed.value = exceptionMessage;
  //   failed.select();
  //   document.execCommand('copy');
  //   notification.success({ message: '已复制到剪贴板' });
  // }

  @Bind()
  handleTransObj(str) {
    let obj = null;
    if (!str) {
      return obj;
    }
    obj = JSON.parse(str);
    if (typeof obj === 'string') {
      /* eslint-disable-next-line */
      obj = JSON.parse(obj);
    }
    return obj;
  }

  @Bind()
  line() {
    return <div className={sagaImg['c7n-saga-img-line']} key={uuid()} />;
  }

  @Bind()
  renderContent() {
    const {
      data: { tasks = [] },
    } = this.state;
    const content = [];
    if (tasks.length) {
      content.push(this.line());
      tasks.forEach(items => {
        const node = items.map(({ code, taskCode, status }) =>
          this.squareWrapper(code || taskCode, status)
        );
        if (node.length === 1) {
          content.push(node);
        } else {
          content.push(this.squareWrapper(node));
        }
        content.push(this.line());
      });
      return content;
    }
    return this.line();
  }

  @Bind()
  renderStatus(status) {
    let obj = {};
    switch (status) {
      case 'RUNNING':
        obj = {
          key: 'running',
          value: intl.get('hagd.sagaInstance.model.sagaInstance.status.running').d('运行中'),
        };
        break;
      case 'FAILED':
        obj = {
          key: 'failed',
          value: intl.get('hagd.sagaInstance.model.sagaInstance.status.failed').d('失败'),
        };
        break;
      case 'QUEUE':
        obj = {
          key: 'queue',
          value: intl.get('hagd.sagaInstance.model.sagaInstance.status.queue').d('队列中'),
        };
        break;
      case 'COMPLETED':
        obj = {
          key: 'completed',
          value: intl.get('hagd.sagaInstance.model.sagaInstance.status.completed').d('完成'),
        };
        break;
      default:
        break;
    }
    return (
      <span className={classnames(sagaImg['c7n-saga-status'], sagaImg[`${obj.key}`])}>
        {obj.value}
      </span>
    );
  }

  @Bind()
  renderTaskRunDetail() {
    const { task = {}, unLockLoading, retryLoading } = this.state;
    const {
      code,
      taskCode,
      status,
      seq,
      maxRetryCount,
      retriedCount,
      instanceLock,
      exceptionMessage,
      output,
      plannedStartTime,
      actualStartTime,
      actualEndTime,
    } = task;
    const list = [
      {
        key: intl.get('hagd.sagaInstance.model.sagaInstance.task.taskCode').d('任务编码'),
        value: code || taskCode,
      },
      {
        key: intl.get('hagd.sagaInstance.model.sagaInstance.status').d('状态'),
        value: this.renderStatus(status),
      },
      {
        key: intl.get('hagd.sagaImage.model.image.seq').d('序列'),
        value: seq,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.instanceLock').d('运行的微服务实例'),
        value: instanceLock,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.maxRetryCount').d('最大重试次数'),
        value: maxRetryCount,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.retriedCount').d('已重试次数'),
        value: retriedCount,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.plannedStartTime').d('计划执行时间'),
        value: plannedStartTime,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.actualStartTime').d('实际开始时间'),
        value: actualStartTime,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.actualEndTime').d('实际完成时间'),
        value: actualEndTime,
      },
    ];
    // 异常状态下的数据
    const failed = {
      key: intl.get('hagd.sagaImage.view.title.failed').d('异常信息'),
      value: exceptionMessage,
    };
    // 完成状态下的数据
    const completed = {
      key: intl.get('hagd.sagaImage.view.title.completed').d('运行结果'),
      value: output ? JSON.stringify(output, null, 4).replace(/\\/g, '') : '',
    };
    return (
      <div className={sagaImg['c7n-saga-task-run']}>
        <div className={sagaImg['c7n-saga-task-btns']}>
          {instanceLock && (status === 'RUNNING' || status === 'FAILED') && (
            <Button
              type="primary"
              icon="unlock"
              loading={unLockLoading}
              onClick={this.handleUnLock}
            >
              {intl.get('hagd.sagaImage.view.title.btnUnLock').d('解锁')}
            </Button>
          )}
          {status === 'FAILED' && (
            <Button
              type="primary"
              icon="sync"
              style={{ marginBottom: 10 }}
              loading={retryLoading}
              onClick={this.handleRetry}
            >
              {intl.get('hagd.sagaImage.view.title.btnRetry').d('重试')}
            </Button>
          )}
        </div>
        <div className={sagaImg['c7n-saga-task-detail']}>
          <div className={sagaImg['c7n-saga-task-detail-content']}>
            {list.map(({ key, value }) => (
              <div key={`task-run-${key}-${uuid()}`}>
                {key}: {value}
              </div>
            ))}
            {status === 'FAILED' && (
              <div>
                <div style={{ marginBottom: 10 }}>{failed.key}:</div>
                <div className={sagaImg['c7n-saga-detail-json']}>
                  <CodeMirror
                    codeMirrorProps={{
                      value: failed.value.trim(),
                      options: {
                        mode: 'text/x-java',
                        autoFocus: false,
                        readOnly: 'nocursor',
                      },
                    }}
                  />
                </div>
              </div>
            )}
            {status === 'COMPLETED' && (
              <div>
                <div style={{ marginBottom: 10 }}>{completed.key}:</div>
                <div className={sagaImg['c7n-saga-detail-json']}>
                  <CodeMirror
                    codeMirrorProps={{
                      value: JSON.stringify(this.handleTransObj(completed.value), null, 4).replace(
                        /\\/g,
                        ''
                      ),
                      options: {
                        mode: 'application/json',
                        autoFocus: false,
                        readOnly: 'nocursor',
                      },
                    }}
                  />
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    );
  }

  @Bind()
  renderTaskDetail() {
    const { instance } = this.props;
    const {
      task: {
        code,
        taskCode,
        description,
        seq,
        maxRetryCount,
        timeoutSeconds,
        timeoutPolicy,
        service,
        concurrentLimitPolicy,
        concurrentLimitNum,
        inputSchema,
      },
    } = this.state;
    const list = [
      {
        key: intl.get('hagd.sagaInstance.model.sagaInstance.task.taskInstanceCode').d('任务编码'),
        value: code || taskCode,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.task.description').d('任务描述'),
        value: description,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.seq').d('序列'),
        value: seq,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.concurrentLimitPolicy').d('并发限制模式'),
        value: concurrentLimitPolicy,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.concurrentLimitNum').d('最大并发数'),
        value: concurrentLimitNum,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.maxRetryCount').d('最大重试次数'),
        value: maxRetryCount,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.timeoutSeconds').d('超时时间'),
        value: timeoutSeconds,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.timeoutPolicy').d('超时策略'),
        value: timeoutPolicy,
      },
      {
        key: intl.get('hagd.sagaImage.model.image.service').d('所属微服务'),
        value: service,
      },
    ];
    return (
      <div className={sagaImg['c7n-saga-task-detail']}>
        <div className={sagaImg['c7n-saga-task-detail-content']}>
          {list.map(({ key, value }) => (
            <div key={`task-run-${key}-${uuid()}`}>
              {key}：{value}
            </div>
          ))}
          {!instance && (
            <div>
              <div style={{ marginBottom: 10 }}>
                {intl.get('hagd.sagaImage.view.title.input').d('输入数据')}：
              </div>
              <div className={sagaImg['c7n-saga-detail-json']}>
                <CodeMirror
                  codeMirrorProps={{
                    value: JSON.stringify(this.handleTransObj(inputSchema), null, 4).replace(
                      /\\/g,
                      ''
                    ),
                    options: {
                      mode: 'application/json',
                      autoFocus: false,
                      readOnly: 'nocursor',
                    },
                  }}
                />
              </div>
            </div>
          )}
        </div>
      </div>
    );
  }

  @Bind()
  renderJson() {
    const { jsonTitle, json } = this.state;
    return (
      <div className={sagaImg['c7n-saga-task-detail']}>
        <div className={sagaImg['c7n-saga-task-detail-title']}>{jsonTitle}</div>
        <div className={sagaImg['c7n-saga-task-detail-content']}>
          <div className={sagaImg['c7n-saga-detail-json']}>
            <CodeMirror
              codeMirrorProps={{
                value: JSON.stringify(this.handleTransObj(json), null, 4).replace(/\\/g, ''),
                options: {
                  mode: 'application/json',
                  autoFocus: false,
                  readOnly: 'nocursor',
                },
              }}
            />
          </div>
        </div>
      </div>
    );
  }

  @Bind()
  renderWithoutInstance() {
    return (
      <div className={sagaImg['c7n-saga-task-detail']} style={{ width: 600 }}>
        <div className={sagaImg['c7n-saga-task-detail-title']}>
          {intl.get('hagd.sagaImage.view.title.taskDetail').d('任务详情')}
        </div>
        {this.renderTaskDetail()}
      </div>
    );
  }

  render() {
    const { instance } = this.props;
    const { showDetail, jsonTitle, activeTab } = this.state;
    const clsNames = classnames(sagaImg['c7n-saga-img-detail-wrapper'], {
      [sagaImg['c7n-saga-instance']]: !!instance,
    });
    return (
      <div className={clsNames} style={{ display: 'flex', justifyContent: 'flex-start' }}>
        <div className={sagaImg['c7n-saga-img']}>
          {this.circleWrapper('Input')}
          {this.renderContent()}
          {this.circleWrapper('Output')}
        </div>
        {showDetail && (
          <div className={sagaImg['c7n-saga-img-detail']} style={{ width: 600 }}>
            {instance && (
              <Tabs activeKey={activeTab} animated={false} onChange={this.handleTabChange}>
                <TabPane
                  tab={intl.get('hagd.sagaImage.view.title.taskRunDetail').d('任务运行情况')}
                  key="run"
                />
                <TabPane
                  tab={intl.get('hagd.sagaImage.view.title.taskDetail').d('任务详情')}
                  key="detail"
                />
              </Tabs>
            )}
            {instance && activeTab === 'run' ? this.renderTaskRunDetail() : ''}
            {instance && activeTab !== 'run' ? this.renderTaskDetail() : ''}
            {instance ? '' : this.renderWithoutInstance()}
          </div>
        )}
        {jsonTitle && (
          <div className={sagaImg['c7n-saga-img-detail']} style={{ width: 600 }}>
            {this.renderJson()}
          </div>
        )}
      </div>
    );
  }
}
