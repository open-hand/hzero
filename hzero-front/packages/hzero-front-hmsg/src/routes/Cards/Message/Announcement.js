import React from 'react';
import { Col, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import styles from './Announcement.less';

import newsImg from '../../../assets/cards/news.svg';
import otherImg from '../../../assets/cards/other.svg';
import winningImg from '../../../assets/cards/winning-notice.svg';
import industryImg from '../../../assets/cards/industry-news.svg';
import companyImg from '../../../assets/cards/company-notice.svg';
import businessImg from '../../../assets/cards/business-friend.svg';
import platformImg from '../../../assets/cards/platform-bulletin.svg';
import temporarily from '../../../assets/cards/temporarily-no-data.svg';

export default class Announcement extends React.Component {
  componentDidMount() {
    const { handleAnnouncementList = e => e } = this.props;
    handleAnnouncementList();
  }

  /**
   * 点击跳转详情页
   * @param {number} number
   * @memberof System
   */
  @Bind()
  handleSkip(number) {
    const { handleDetails = e => e } = this.props;
    handleDetails(number, 'announce');
  }

  render() {
    const { announcementList } = this.props;
    const typeObj = {
      PTGG: platformImg,
      GSXW: newsImg,
      HYZX: industryImg,
      QTGG: otherImg,
      XPZJ: businessImg,
      SYQ: businessImg,
      GSWD: companyImg,
      GSTZ: companyImg,
      GSZD: companyImg,
      ZBYG: winningImg,
      ZBGG: winningImg,
      ZBCQ: winningImg,
      XJTZ: winningImg,
      BJTZ: winningImg,
    };
    if (announcementList.length) {
      return (
        <div className={styles['notice-list-wrap']}>
          {announcementList.map(item => (
            <Row
              key={item.noticeId}
              type="flex"
              justify="space-around"
              align="middle"
              className={styles['notice-row']}
              onClick={() => this.handleSkip(item.noticeId)}
            >
              <Col span={2} className={styles['notice-type']}>
                <img alt="" src={typeObj[item.noticeTypeCode]} className={styles['notice-img']} />
              </Col>
              <Col span={22} className={styles['notice-content']}>
                <Row style={{ marginBottom: '4px' }}>
                  <Col span={16} className={styles['notice-title']}>
                    {item.title}
                  </Col>
                  <Col span={8} className={styles['notice-time']}>
                    {item.publishedDateTimeStr}
                  </Col>
                </Row>
                {item.noticeContent && item.noticeContent.noticeBody && (
                  <Col
                    className={styles['notice-list']}
                    dangerouslySetInnerHTML={{ __html: item.noticeContent.noticeBody }}
                  />
                )}
              </Col>
            </Row>
          ))}
        </div>
      );
    } else {
      return (
        <div className={styles['notice-img-wrap']}>
          <img src={temporarily} alt="" className={styles['notice-no-img']} />
        </div>
      );
    }
  }
}
