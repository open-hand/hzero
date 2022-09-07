import React from 'react';
import { Col, Row } from 'hzero-ui';

import intl from 'utils/intl';

const ApproveItem = props => {
  const { detail = {} } = props;
  const name = `${detail.startUserName ? `${detail.startUserName}(${detail.startUserId})` : ''}`;

  return (
    <>
      <Row
        style={{ borderBottom: '1px dashed #dcdcdc', paddingBottom: 4, marginBottom: 20 }}
        type="flex"
        justify="space-between"
        align="bottom"
      >
        <Col md={8}>
          <Row>
            <Col md={6} style={{ color: '#999' }}>
              {intl.get('hwfp.common.model.process.name').d('流程名称')}:
            </Col>
            <Col md={16}> {detail.processName}</Col>
          </Row>
        </Col>
        <Col md={8}>
          <Row>
            <Col md={6} style={{ color: '#999' }}>
              {intl.get('hwfp.common.model.process.ID').d('流程标识')}:
            </Col>
            <Col md={16}> {detail.id}</Col>
          </Row>
        </Col>
        <Col md={8}>
          <Row>
            <Col md={6} style={{ color: '#999' }}>
              {intl.get('hwfp.common.model.apply.owner').d('申请人')}:
            </Col>
            <Col md={16}> {name}</Col>
          </Row>
        </Col>
      </Row>
      <Row
        style={{ borderBottom: '1px dashed #dcdcdc', paddingBottom: 4, marginBottom: 40 }}
        type="flex"
        justify="flex-start"
        align="bottom"
      >
        <Col md={8}>
          <Row>
            <Col md={6} style={{ color: '#999' }}>
              {intl.get('hwfp.common.model.apply.time').d('申请时间')}:
            </Col>
            <Col md={16}> {detail.startTime}</Col>
          </Row>
        </Col>
        <Col md={8}>
          <Row>
            <Col md={6} style={{ color: '#999' }}>
              {intl.get('hwfp.common.model.process.description').d('流程描述')}:
            </Col>
            <Col md={16}> {detail.description}</Col>
          </Row>
        </Col>
      </Row>
    </>
  );
};

export default ApproveItem;
