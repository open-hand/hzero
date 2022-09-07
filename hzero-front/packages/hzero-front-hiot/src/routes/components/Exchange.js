/**
 * moveUp function 将因子移动到公式
 * moveDown function 删除公式中的因子
 * mode 显示样式 vertical: 垂直 horizontal，水平 horizontal 默认水平
 */
import React, { PureComponent } from 'react';
import { Col, Row } from 'choerodon-ui/pro';
import { Button } from 'choerodon-ui';

export default class Exchange extends PureComponent {
  render() {
    const { moveDown, moveUp, mode } = this.props;
    const upButton = (
      <Button
        funcType="flat"
        shape="circle"
        icon={mode === 'horizontal' ? 'arrow_forward' : 'arrow_downward'}
        color="primary"
        onClick={moveUp}
      />
    );
    const downButton = (
      <Button
        funcType="flat"
        shape="circle"
        icon={mode === 'horizontal' ? 'arrow_back' : 'arrow_upward'}
        color="primary"
        onClick={moveDown}
      />
    );
    return mode === 'horizontal' ? (
      <Row
        type="flex"
        justify="center"
        align="middle"
        style={{ height: '100%', flexDirection: 'column' }}
      >
        <Col span={12}>{upButton}</Col>
        <Col span={12}>{downButton}</Col>
      </Row>
    ) : (
      <Row>
        <Col style={{ textAlign: 'center' }}>
          {upButton}
          {downButton}
        </Col>
      </Row>
    );
  }
}
