import React, { Component } from 'react';
import { connect } from 'dva';
import { Row, Col, Divider, Popover } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import classnames from 'classnames';

import intl from 'utils/intl';

import styles from './Saga.less';

@connect(({ loading, sagaInstance }) => ({
  sagaInstance,
  detailLoading: loading.effects['sagaInstance/querySagaInstanceDetail'],
}))
export default class InstanceExpandRow extends Component {
  state = {
    detail: null,
  };

  componentDidMount() {
    this.queryInstanceDetail();
  }

  @Bind()
  queryInstanceDetail() {
    const {
      dispatch,
      record: { id },
    } = this.props;
    dispatch({
      type: 'sagaInstance/querySagaInstanceDetail',
      payload: { id },
    }).then(res => {
      if (res) {
        this.setState({ detail: res });
      }
    });
  }

  @Bind()
  getCircle() {
    const { record = {} } = this.props;
    const { detail } = this.state;
    const { status } = record;
    let statusText = '';
    if (status === 'FAILED') {
      statusText = intl.get('hagd.sagaInstance.model.sagaInstance.status.failed').d('失败');
    } else if (status === 'COMPLETED') {
      statusText = intl.get('hagd.sagaInstance.model.sagaInstance.status.completed').d('完成');
    } else if (status === 'RUNNING') {
      statusText = intl.get('hagd.sagaInstance.model.sagaInstance.status.running').d('运行中');
    }
    const { completed, failed, running, queue } = detail;
    const sum = completed + failed + running + queue;
    const completedCorrect =
      sum > 0 ? ((completed + running + queue) / sum) * (Math.PI * 2 * 30) : 0;
    const runningCorrect = sum > 0 ? ((running + queue) / sum) * (Math.PI * 2 * 30) : 0;
    const queueCorrect = sum > 0 ? (queue / sum) * (Math.PI * 2 * 30) : 0;
    return (
      <svg width="80" height="80">
        <Popover
          placement="left"
          content={
            <div>
              <div className={styles['c7n-saga-spot c7n-saga-spot-error']} />
              {`${intl
                .get('hagd.sagaInstance.view.title.saga.failedTitle')
                .d('失败任务')}：${failed}`}
            </div>
          }
        >
          <circle
            cx="40"
            cy="40"
            r="30"
            strokeWidth={5}
            stroke={failed > 0 ? '#f44336' : '#f3f3f3'}
            className={styles['c7n-saga-circle-error']}
          />
        </Popover>
        <Popover
          placement="left"
          content={
            <div>
              <div className={styles['c7n-saga-spot c7n-saga-spot-completed']} />
              {`${intl
                .get('hagd.sagaInstance.view.title.saga.completedTitle')
                .d('完成任务')}：${completed}`}
            </div>
          }
        >
          <circle
            cx="40"
            cy="40"
            r="30"
            stroke={completed > 0 ? '#00bfa5' : '#f3f3f3'}
            className={styles['c7n-saga-circle']}
            strokeDasharray={`${completedCorrect}, 10000`}
          />
        </Popover>
        <Popover
          placement="left"
          content={
            <div>
              <div className={styles['c7n-saga-spot c7n-saga-spot-running']} />
              {`${intl
                .get('hagd.sagaInstance.view.title.saga.runningTitle')
                .d('进行中任务')}：${running}`}
            </div>
          }
        >
          <circle
            cx="40"
            cy="40"
            r="30"
            stroke={running > 0 ? '#4d90fe' : '#f3f3f3'}
            className={styles['c7n-saga-circle-running']}
            strokeDasharray={`${runningCorrect}, 10000`}
          />
        </Popover>
        <Popover
          placement="left"
          content={
            <div>
              <div className={styles['c7n-saga-spot c7n-saga-spot-queue']} />
              {`${intl
                .get('hagd.sagaInstance.view.title.saga.queueTitle')
                .d('等待中任务')}：${queue}`}
            </div>
          }
        >
          <circle
            cx="40"
            cy="40"
            r="30"
            stroke={queue > 0 ? '#ffb100' : '#f3f3f3'}
            className={styles['c7n-saga-circle-queue']}
            strokeDasharray={`${queueCorrect}, 10000`}
          />
        </Popover>
        <text x="50%" y="39.5" className={styles['c7n-saga-circle-num']}>
          {`${sum - failed}/${sum}`}
        </text>
        <text x="50%" y="54" fontSize="12" className={styles['c7n-saga-circle-text']}>
          {statusText}
        </text>
      </svg>
    );
  }

  render() {
    const { detail } = this.state;
    if (!detail) {
      return null;
    }
    const {
      id,
      sagaCode,
      description,
      service,
      level,
      startTime,
      endTime,
      refType,
      refId,
    } = detail;
    return (
      <>
        <Row className={styles['saga-instance-detail']}>
          <Col span={14}>
            <Row className={styles['saga-instance-detail-row']}>
              <Col span={4}>ID:</Col>
              <Col span={14} className={styles['saga-instance-detail-value']}>
                {id}
              </Col>
            </Row>
            <Row className={styles['saga-instance-detail-row']}>
              <Col span={4}>
                {intl.get('hagd.sagaInstance.model.sagaInstance.saga.sagaCode').d('所属事务定义')}:
              </Col>
              <Col span={14} className={styles['saga-instance-detail-value']}>
                {sagaCode}
              </Col>
            </Row>
            <Row className={styles['saga-instance-detail-row']}>
              <Col span={4}>{intl.get('hagd.saga.model.saga.description').d('描述')}:</Col>
              <Col span={14} className={styles['saga-instance-detail-value']}>
                {description}
              </Col>
            </Row>
            <Row className={styles['saga-instance-detail-row']}>
              <Col span={4}>
                {intl.get('hagd.sagaInstance.model.sagaInstance.saga.service').d('所属微服务')}:
              </Col>
              <Col span={14} className={styles['saga-instance-detail-value']}>
                {service}
              </Col>
            </Row>
            <Row className={styles['saga-instance-detail-row']}>
              <Col span={4}>
                {intl.get('hagd.sagaInstance.model.sagaInstance.saga.level').d('触发层级')}:
              </Col>
              <Col span={14} className={styles['saga-instance-detail-value']}>
                {level}
              </Col>
            </Row>
            <Row className={styles['saga-instance-detail-row']}>
              <Col span={4}>
                {intl.get('hagd.sagaInstance.model.sagaInstance.startTime').d('开始时间')}:
              </Col>
              <Col span={4} className={styles['saga-instance-detail-value']}>
                {startTime}
              </Col>
              <Col span={4}>
                {intl.get('hagd.sagaInstance.model.sagaInstance.saga.endTime').d('结束时间')}:
              </Col>
              <Col span={4} className={styles['saga-instance-detail-value']}>
                {endTime}
              </Col>
            </Row>
          </Col>
          <Col span={10}>{this.getCircle()}</Col>
        </Row>
        <Divider orientation="left">
          <span style={{ fontSize: 12 }}>
            {intl.get('hagd.sagaInstance.view.title.ref').d('关联业务')}
          </span>
        </Divider>
        <Row>
          <Col span={14}>
            <Row className={styles['saga-instance-detail-row']}>
              <Col span={4}>
                {intl.get('hagd.sagaInstance.model.sagaInstance.refType').d('关联事务类型')}:
              </Col>
              <Col span={14} className={styles['saga-instance-detail-value']}>
                {refType}
              </Col>
            </Row>
            <Row className={styles['saga-instance-detail-row']}>
              <Col span={4}>
                {intl.get('hagd.sagaInstance.model.sagaInstance.refId').d('关联业务ID')}:
              </Col>
              <Col
                span={14}
                className={classnames(
                  styles['saga-instance-detail-value'],
                  styles['c7n-saga-expand-row']
                )}
              >
                {refId}
              </Col>
            </Row>
          </Col>
        </Row>
      </>
    );
  }
}
