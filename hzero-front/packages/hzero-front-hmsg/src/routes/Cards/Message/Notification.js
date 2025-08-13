import React from 'react';
import { Bind } from 'lodash-decorators';
import { Col, Row, Timeline } from 'hzero-ui';
import styles from './Notification.less';
import temporarily from '../../../assets/cards/temporarily-no-data.svg';

export default class Notification extends React.Component {
  componentDidMount() {
    const { handleUserMessage = e => e } = this.props;
    handleUserMessage('NOTICE');
  }

  /**
   * 点击跳转详情页
   * @param {number} number
   * @memberof System
   */
  @Bind()
  handleSkip(number) {
    const { handleRead = e => e, handleDetails = e => e } = this.props;
    handleRead(number, 'notice');
    handleDetails(number, 'notice');
  }

  /**
   * 设置消息数据展示
   * @param {object} item
   * @param {number} key
   * @memberof System
   */
  @Bind()
  renderColor(item, key) {
    const colorObject = {
      0: '#0687ff',
      1: '#cb38ad',
      2: '#ffbc00',
      3: '#f02b2b',
    };
    return (
      <Timeline.Item key={item.messageId} color={colorObject[key % 4]}>
        <Row className={styles['notification-row']}>
          <Col
            span={16}
            className={styles['notification-title']}
            onClick={() => this.handleSkip(item.userMessageId)}
          >
            {item.subject}
          </Col>
          <Col
            span={7}
            className={styles['notification-time']}
            onClick={() => this.handleSkip(item.userMessageId)}
          >
            {item.creationDate}
          </Col>
        </Row>
      </Timeline.Item>
    );
  }

  render() {
    const { notificationList } = this.props;
    if (notificationList.length) {
      return (
        <Timeline className={styles['notification-overflow']}>
          {notificationList.map((item, index) => this.renderColor(item, index))}
        </Timeline>
      );
    } else {
      return (
        <div className={styles['notification-img-wrap']}>
          <img src={temporarily} alt="" className={styles['notification-img']} />
        </div>
      );
    }
  }
}
