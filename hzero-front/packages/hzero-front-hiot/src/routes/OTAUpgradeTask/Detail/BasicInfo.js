import React, { Component } from 'react';
import { Form, Col, Row, Output } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { fields } from '@/stores/otaUpgradeTaskDS';

export default class BasicInfo extends Component {
  render() {
    const { dataSet } = this.props;
    return (
      <Card
        className={DETAIL_CARD_CLASSNAME}
        bordered={false}
        title={intl.get('hiot.common.view.baseInfo').d('基本信息')}
      >
        <Row>
          <Col span={16}>
            <Form dataSet={dataSet} columns={2}>
              <Output label={fields.cloudPlatform().label} name={fields.cloudPlatform().name} />
              <Output label={fields.cloudAccount().label} name={fields.cloudAccount().name} />
              <Output label={fields.taskName().label} name={fields.taskName().name} />
              <Output label={fields.taskCode().label} name={fields.taskCode().name} />
              <Output label={fields.upgradeCategory().label} name={fields.upgradeCategory().name} />
              <Output label={fields.templateName().label} name={fields.templateName().name} />
              <Output label={fields.templateCode().label} name={fields.templateCode().name} />
              <Output label={fields.versionNum().label} name={fields.versionNum().name} />
              <Output label={fields.upgradeTime().label} name={fields.upgradeTime().name} />
              <Output label={fields.description().label} name={fields.description().name} />
            </Form>
          </Col>
        </Row>
      </Card>
    );
  }
}
