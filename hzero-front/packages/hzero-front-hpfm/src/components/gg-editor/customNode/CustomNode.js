/**
 * 流程图 - 自定义节点
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-29
 * @LastEditTime: 2019-10-29 18:04
 * @Copyright: Copyright (c) 2018, Hand
 */
import React from 'react';
import { RegisterNode } from 'gg-editor';

class CustomNode extends React.Component {
  render() {
    const config = {
      draw(item) {
        const keyShape = this.drawKeyShape(item);

        // 绘制图标
        const group = item.getGraphicGroup();
        const model = item.getModel();

        group.addShape('image', {
          attrs: {
            x: -15,
            y: -15,
            width: 30,
            height: 30,
            img: model.icon,
          },
        });

        // 绘制标签
        this.drawLabel(item);

        return keyShape;
      },
      anchor: [
        [0, 0.5], // 左边中点
        [1, 0.5], // 右边中点
      ],
    };

    return <RegisterNode name="custom-node" config={config} extend="flow-rect" />;
  }
}

export default CustomNode;
