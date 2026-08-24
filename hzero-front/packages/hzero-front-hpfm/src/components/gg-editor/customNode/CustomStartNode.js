/**
 * 流程图 - 自定义开始节点
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-11-05
 * @LastEditTime: 2019-11-05 14:43
 * @Copyright: Copyright (c) 2018, Hand
 */
import React from 'react';
import { RegisterNode } from 'gg-editor';

class CustomStartNode extends React.Component {
  render() {
    const config = {
      draw(item) {
        const keyShape = this.drawKeyShape(item);

        // 绘制标签
        this.drawLabel(item);

        return keyShape;
      },

      anchor: [
        [1, 0.5], // 右边中点
      ],
    };

    return <RegisterNode name="custom-start" config={config} extend="flow-circle" />;
  }
}

export default CustomStartNode;
