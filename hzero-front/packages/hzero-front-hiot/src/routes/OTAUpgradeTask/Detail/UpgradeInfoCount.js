import React, { Component } from 'react';
import { Form, Col, Row, Output } from 'choerodon-ui/pro';
import { Card, Icon } from 'choerodon-ui';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import styles from './index.less';
import { fields } from '@/stores/otaUpgradeTaskDS';

const prefix = 'hiot.ota';

export default class UpgradeInfoCount extends Component {
  render() {
    const { dataSet } = this.props;
    return (
      <Form dataSet={dataSet}>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={intl.get(`${prefix}.view.upgrade.info.count`).d('升级信息统计')}
        >
          <Row gutter={64}>
            <Col span={6}>
              <Card className={styles['info-card']}>
                <Row className={styles['top-style']}>{fields.currentUsedTime().label}</Row>
                <Row
                  type="flex"
                  justify="space-around"
                  align="middle"
                  className={styles['bottom-style']}
                >
                  <Col span={18} className={styles['bottom-text-style']}>
                    <Output name={fields.currentUsedTime().name} />
                  </Col>
                  <Col span={6}>
                    <Icon type="av_timer" className={styles['bottom-icon-style']} />
                  </Col>
                </Row>
              </Card>
            </Col>
            <Col span={6}>
              <Card className={styles['info-card']}>
                <Row className={styles['top-style']}>{fields.upgradeDeviceNum().label}</Row>
                <Row
                  type="flex"
                  justify="space-around"
                  align="middle"
                  className={styles['bottom-style']}
                >
                  <Col span={18} className={styles['bottom-text-style']}>
                    <Output name={fields.upgradeDeviceNum().name} />
                  </Col>
                  <Col span={6}>
                    <Icon type="file_upload" className={styles['bottom-icon-style']} />
                  </Col>
                </Row>
              </Card>
            </Col>
            <Col span={6}>
              <Card className={styles['info-card']}>
                <Row className={styles['top-style']}>{fields.upgradeSucceedNumber().label}</Row>
                <Row
                  type="flex"
                  justify="space-around"
                  align="middle"
                  className={styles['bottom-style']}
                >
                  <Col span={18} className={styles['bottom-text-style']}>
                    <Output name={fields.upgradeSucceedNumber().name} />
                  </Col>
                  <Col span={6}>
                    <Icon type="finished" className={styles['bottom-icon-style']} />
                  </Col>
                </Row>
              </Card>
            </Col>
            <Col span={6}>
              <Card className={styles['info-card']}>
                <Row className={styles['top-style']}>{fields.upgradeFailedNumber().label}</Row>
                <Row
                  type="flex"
                  justify="space-around"
                  align="middle"
                  className={styles['bottom-style']}
                >
                  <Col span={18} className={styles['bottom-text-style']}>
                    <Output name={fields.upgradeFailedNumber().name} />
                  </Col>
                  <Col span={6}>
                    <Icon type="close" className={styles['bottom-icon-style']} />
                  </Col>
                </Row>
              </Card>
            </Col>
          </Row>
        </Card>
      </Form>
    );
  }
}
