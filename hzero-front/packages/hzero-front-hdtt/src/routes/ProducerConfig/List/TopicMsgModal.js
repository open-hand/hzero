/**
 * TopicMsgModal - Topic消息模态框
 * @date: 2019/5/22
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Modal, Row, Col, Input, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';

const { TextArea } = Input;

/**
 * @extends {Component} - React.Component
 * @reactProps {Boolean} visible - 是否显示Topic消息模态框
 * @reactProps {string} topicGeneratedMsg - Topic消息
 * @reactProps {string} topicGeneratedTime - Topic创建时间
 * @return React.element
 */
export default class TopicMsgModal extends Component {
  /**
   * 关闭topic消息模态框
   */
  @Bind
  handleCloseMessageModal() {
    this.props.onCancel();
  }

  render() {
    const { visible, topicGeneratedMsg, topicGeneratedTime } = this.props;
    return (
      <Modal
        visible={visible}
        destroyOnClose
        maskClosable
        title={intl.get(`hdtt.producerConfig.view.message.topicFailedInfo`).d('Topic创建失败信息')}
        onCancel={this.handleCloseMessageModal}
        footer={[
          <Button key="cancel" onClick={this.handleCloseMessageModal}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>,
        ]}
      >
        <Row>
          <Col span={6}>
            {intl.get(`hdtt.producerConfig.view.message.topicTime`).d('Topic创建时间')}:
          </Col>
          <Col span={18}>{topicGeneratedTime}</Col>
        </Row>
        <Row style={{ marginTop: '10px' }}>
          <Col span={6}>
            {intl.get(`hdtt.producerConfig.view.message.topicMsg`).d('Topic创建消息')}:
          </Col>
          <Col span={18}>
            <TextArea readOnly style={{ height: '184px' }}>
              {topicGeneratedMsg}
            </TextArea>
          </Col>
        </Row>
      </Modal>
    );
  }
}
