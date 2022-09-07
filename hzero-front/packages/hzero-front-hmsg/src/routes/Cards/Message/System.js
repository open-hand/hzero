import React from 'react';
import { Bind } from 'lodash-decorators';
import { Col, Row, Timeline } from 'hzero-ui';
import styles from './System.less';
import temporarily from '../../../assets/cards/temporarily-no-data.svg';

export default class System extends React.Component {
  componentDidMount() {
    const { handleUserMessage = e => e } = this.props;
    handleUserMessage('MSG');
  }

  /**
   * 点击跳转详情页
   * @param {number} number
   * @memberof System
   */
  @Bind()
  handleSkip(number) {
    const { handleRead = e => e, handleDetails = e => e } = this.props;
    handleRead(number, 'message');
    handleDetails(number, 'message');
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
        <Row className={styles['message-row']}>
          <Col
            span={16}
            className={styles['message-title']}
            onClick={() => this.handleSkip(item.userMessageId)}
          >
            {item.subject}
          </Col>
          <Col
            span={7}
            className={styles['message-time']}
            onClick={() => this.handleSkip(item.userMessageId)}
          >
            {item.creationDate}
          </Col>
        </Row>
      </Timeline.Item>
    );
  }

  render() {
    const { systemMessageList } = this.props;
    if (systemMessageList.length) {
      return (
        <Timeline className={styles['message-overflow']}>
          {systemMessageList.map((item, index) => this.renderColor(item, index))}
        </Timeline>
      );
    } else {
      return (
        <div className={styles['message-img-wrap']}>
          <img src={temporarily} alt="" className={styles['message-img']} />
        </div>
      );
    }
  }
}
