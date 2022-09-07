/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-08 13:50:54
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 可选项组件
 */
import React from 'react';
import { Button } from 'choerodon-ui/pro';
import { SketchPicker } from 'react-color';
import { Popover } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

export default class ColorPopover extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      visible: false,
      color: '#fff',
    };
  }

  /**
   * 确认选择的颜色
   */
  @Bind()
  handleSubmit() {
    this.props.handleSubmitColor(this.state.color);
    this.setState({
      visible: false,
    });
  }

  @Bind()
  handleVisibleChange(visible) {
    if (visible) {
      this.setState({
        color: this.props.color,
        visible,
      });
    }
  }

  render() {
    const { children } = this.props;
    return (
      <Popover
        content={
          <div className="color-popover-content">
            <SketchPicker
              className="color-picker-sketch"
              color={this.state.color}
              onChange={(value) => {
                this.setState({ color: value.hex });
              }}
            />
            <Button type="primary" onClick={this.handleSubmit}>
              {intl.get('hzero.common.button.ok').d('确定')}
            </Button>
          </div>
        }
        trigger="click"
        visible={this.state.visible}
        onVisibleChange={this.handleVisibleChange}
      >
        {children}
      </Popover>
    );
  }
}
