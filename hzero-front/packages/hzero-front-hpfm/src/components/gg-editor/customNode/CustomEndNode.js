/**
 * 流程图 - 自定义结束节点
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-11-05
 * @LastEditTime: 2019-11-05 14:43
 * @Copyright: Copyright (c) 2018, Hand
 */
import React from 'react';
import { RegisterNode } from 'gg-editor';

class CustomEndNode extends React.Component {
  render() {
    const config = {
      draw(item) {
        const keyShape = this.drawKeyShape(item);

        // 绘制标签
        this.drawLabel(item);

        return keyShape;
      },

      anchor: [
        [0, 0.5], // 左边中点
      ],
    };

    return <RegisterNode name="custom-end" config={config} extend="flow-circle" />;
  }
}

export default CustomEndNode;
