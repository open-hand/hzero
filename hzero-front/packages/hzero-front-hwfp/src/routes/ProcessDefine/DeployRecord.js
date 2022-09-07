/*
 * DeployRecord - 发布记录
 * @date: 2019-05-08
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Modal, Timeline, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
// import intl from 'utils/intl';

/**
 * 发布记录
 * @extends {Component} - React.Component
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onHandleOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 操作对象
 * @return React.element
 */
export default class DeployRecord extends Component {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: [],
    };
  }

  getSnapshotBeforeUpdate(preProps) {
    const { visible: preVisible } = preProps;
    const { visible } = this.props;
    if (!preVisible && visible) {
      return visible;
    }
    return null;
  }

  componentDidUpdate(preProps, preState, snapshot) {
    if (snapshot) {
      const { onFetchRecord, record } = this.props;
      if (record.key) {
        onFetchRecord(record.key).then(res => {
          if (res) {
            this.setState({
              dataSource: res,
            });
          }
        });
      }
    }
  }

  @Bind()
  handleClick() {
    const { onCancel } = this.props;
    this.setState({ dataSource: [] });
    onCancel();
  }

  render() {
    const { dataSource = [] } = this.state;
    const { anchor = 'right', visible, title = 'test', loading = false } = this.props;
    return (
      <Modal
        title={title}
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.handleClick}
        onCancel={this.handleClick}
        destroyOnClose
      >
        <Spin spinning={loading}>
          {dataSource.length > 0 ? (
            <Timeline>
              {dataSource.map(n => (
                <Timeline.Item>
                  {`${intl.get(`hwfp.common.view.message.version`).d('版本')}：${n.version}`} {`${intl.get(`hzero.common.date.releaseTime`).d('发布时间')}：${n.deploymentTime}`}
                </Timeline.Item>
              ))}
            </Timeline>
          ) : (
            intl.get('hwfp.common.view.message.deployRecord.noContent').d('暂无记录')
          )}
        </Spin>
      </Modal>
    );
  }
}
