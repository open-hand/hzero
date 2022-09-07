/**
 * sagaInstance - 事务实例
 * @date: 2018-12-25
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Col, Row, Tabs } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import SagaFilter from './Saga/SagaFilter';
import SagaTable from './Saga/SagaTable';
import TaskFilter from './Task/TaskFilter';
import TaskTable from './Task/TaskTable';
import Drawer from './Drawer';
import styles from './index.less';

function InstanceStatus({ value, text, status }) {
  return (
    <div className={styles['status-line']}>
      <div className={styles[`status-line-${status}`]}>
        <span>{value}</span>/<span>{text}</span>
      </div>
    </div>
  );
}

@connect(({ loading, sagaInstance }) => ({
  sagaInstance,
  fetchLoading: loading.effects['sagaInstance/fetchSagaInstanceList'],
  fetchTaskLoading: loading.effects['sagaInstance/fetchSagaInstanceTaskList'],
  detailLoading: loading.effects['sagaInstance/querySagaInstanceDetail'],
  runDetailLoading: loading.effects['sagaInstance/queryInstanceRun'],
  unLockLoading: loading.effects['sagaInstance/instanceUnlock'],
  retryLoading: loading.effects['sagaInstance/instanceRetry'],
}))
@formatterCollections({ code: ['hagd.sagaInstance'] })
export default class sagaInstance extends React.Component {
  state = {
    modalVisible: false,
    activeTab: 'saga',
  };

  sagaForm;

  taskForm;

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'sagaInstance/init',
    });
    this.queryInstanceStatistic();
    this.fetchSagaInstanceList();
    this.fetchSagaInstanceTaskList();
  }

  queryInstanceStatistic() {
    const { dispatch } = this.props;
    dispatch({
      type: 'sagaInstance/queryInstanceStatistic',
    });
  }

  fetchSagaInstanceList(params = {}) {
    const {
      dispatch,
      sagaInstance: { pagination = {} },
    } = this.props;
    const fieldsValue = this.sagaForm.getFieldsValue();
    dispatch({
      type: 'sagaInstance/fetchSagaInstanceList',
      payload: { page: pagination, ...fieldsValue, ...params },
    });
  }

  fetchSagaInstanceTaskList(params = {}) {
    const {
      dispatch,
      sagaInstance: { taskPagination = {} },
    } = this.props;
    const fieldsValue = this.taskForm ? this.taskForm.getFieldsValue() : {};
    dispatch({
      type: 'sagaInstance/fetchSagaInstanceTaskList',
      payload: { page: taskPagination, ...fieldsValue, ...params },
    });
  }

  @Bind()
  handleBindSagaRef(ref) {
    this.sagaForm = (ref.props || {}).form;
  }

  @Bind()
  handleBindTaskRef(ref) {
    this.taskForm = (ref.props || {}).form;
  }

  @Bind()
  handleTabChange(key) {
    this.setState({ activeTab: key });
  }

  /**
   * 查询事务表单
   */
  @Bind()
  handleSearchSaga() {
    this.fetchSagaInstanceList({ page: {} });
  }

  /**
   * 查询任务表单
   */
  @Bind()
  handleSearchTask() {
    this.fetchSagaInstanceTaskList({ page: {} });
  }

  @Bind()
  handleShowDetail(record = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'sagaInstance/queryInstanceRun',
      payload: { id: record.id },
    }).then((res) => {
      if (res) {
        this.setState({ modalVisible: true });
      }
    });
  }

  @Bind()
  handleModalVisible() {
    this.setState({ modalVisible: false });
  }

  /**
   * 事务分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handleSagaPagination(pagination) {
    this.fetchSagaInstanceList({ page: pagination });
  }

  /**
   * 任务分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handleTaskPagination(pagination) {
    this.fetchSagaInstanceTaskList({ page: pagination });
  }

  @Bind()
  handleRefresh() {
    this.fetchSagaInstanceList();
  }

  /**
   * 实例解锁
   * @param {object} data 实例对象
   */
  @Bind()
  handleUnLock(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'sagaInstance/instanceUnlock',
      payload: { id: data.id },
    }).then((res) => {
      if (res) {
        notification.success();
        // 解锁成功后重新获取运行情况
        // this.handleShowDetail(data);
      }
    });
  }

  /**
   * 实例失败重试
   * @param {object} data 实例对象
   */
  @Bind()
  handleRetry(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'sagaInstance/instanceRetry',
      payload: { id: data.id },
    }).then((res) => {
      if (res) {
        notification.success();
        // 重试成功后重新获取运行情况
        // this.retryCallBack(data);
      }
    });
  }

  // @Bind()
  // retryCallBack(data) {
  //   this.handleShowDetail(data).then((res) => {
  //     if (res) {
  //       const { status } = res;
  //       if (status !== 'RUNNING') {
  //         this.retryCallBack(res);
  //       }
  //     }
  //   });
  // }

  render() {
    const {
      fetchLoading = false,
      refreshLoading = false,
      runDetailLoading = false,
      fetchTaskLoading = false,
      unLockLoading = false,
      retryLoading = false,
      sagaInstance: {
        sagaInstanceList = [],
        sagaInstanceRunDetail = {},
        instanceStatistic = {},
        pagination = {},
        sagaInstanceTaskList = [],
        taskPagination = {},
        statusList = [],
      },
    } = this.props;
    const { COMPLETED = 0, FAILED = 0, RUNNING = 0 } = instanceStatistic;
    const { modalVisible, activeTab } = this.state;
    return (
      <>
        <Header title={intl.get('hagd.sagaInstance.view.title.header').d('事务实例')}>
          {activeTab === 'saga' && (
            <Button type="primary" icon="sync" onClick={this.handleRefresh}>
              {intl.get('hzero.common.button.refresh').d('刷新')}
            </Button>
          )}
        </Header>
        <Content>
          <Row style={{ marginBottom: 10 }}>
            <Row type="flex" justify="end">
              <Col>
                <h3 style={{ width: 360, textAlign: 'left' }}>
                  {intl.get('hagd.sagaInstance.view.title.contentHeader').d('事务实例状态总览')}
                </h3>
              </Col>
            </Row>
            <Row type="flex" justify="end">
              <Col>
                <InstanceStatus
                  value={COMPLETED}
                  text={intl.get('hagd.sagaInstance.model.sagaInstance.status.completed').d('完成')}
                  status="completed"
                />
              </Col>
              <Col>
                <InstanceStatus
                  value={RUNNING}
                  text={intl.get('hagd.sagaInstance.model.sagaInstance.status.running').d('运行中')}
                  status="running"
                />
              </Col>
              <Col>
                <InstanceStatus
                  value={FAILED}
                  text={intl.get('hagd.sagaInstance.model.sagaInstance.status.failed').d('失败')}
                  status="failed"
                />
              </Col>
            </Row>
          </Row>
          <Tabs defaultActiveKey="saga" animated={false} onChange={this.handleTabChange}>
            <Tabs.TabPane
              tab={intl.get('hagd.sagaInstance.view.title.contentHeader.saga').d('事务')}
              key="saga"
            >
              <SagaFilter
                statusList={statusList}
                onSearch={this.handleSearchSaga}
                onRef={this.handleBindSagaRef}
              />
              <SagaTable
                loading={fetchLoading || refreshLoading || runDetailLoading}
                dataSource={sagaInstanceList}
                showDetail={this.handleShowDetail}
                onChange={this.handleSagaPagination}
                pagination={pagination}
              />
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={intl.get('hagd.sagaInstance.view.title.contentHeader.task').d('任务')}
              key="task"
            >
              <div className="table-list-search">
                <TaskFilter
                  statusList={statusList}
                  onSearch={this.handleSearchTask}
                  onRef={this.handleBindTaskRef}
                />
              </div>
              <TaskTable
                loading={fetchTaskLoading || refreshLoading || runDetailLoading}
                showDetail={this.handleShowDetail}
                dataSource={sagaInstanceTaskList}
                onChange={this.handleTaskPagination}
                pagination={taskPagination}
              />
            </Tabs.TabPane>
          </Tabs>
          <Drawer
            title={intl
              .get('hagd.sagaInstance.view.title.modal', { id: sagaInstanceRunDetail.id })
              .d(`${sagaInstanceRunDetail.id}-事务实例详情`)}
            initLoading={runDetailLoading}
            unLockLoading={unLockLoading}
            retryLoading={retryLoading}
            initData={sagaInstanceRunDetail}
            modalVisible={modalVisible}
            onCancel={this.handleModalVisible}
            onUnLock={this.handleUnLock}
            onRetry={this.handleRetry}
          />
        </Content>
      </>
    );
  }
}
