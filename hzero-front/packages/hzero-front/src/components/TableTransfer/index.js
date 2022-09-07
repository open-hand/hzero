import React, { PureComponent } from 'react';
import { Row, Col, Button } from 'hzero-ui';

export default class TableTransfer extends PureComponent {
  render() {
    return (
      <Row gutter={24}>
        <Col span={10} />
        <Col span={4}>
          <div style={{ width: '100%' }}>
            <Button>add</Button>
          </div>
          <div style={{ width: '100%' }}>
            <Button>remove</Button>
          </div>
        </Col>
        <Col span={10} />
      </Row>
    );
  }
}
