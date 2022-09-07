/**
 * HttpNode
 * @author changwen.yu@hand-china.com
 * @date 2020/9/21
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { RegisterNode } from 'gg-editor';
import { drawNode } from './drawUtils';

const ConditionNode = () => {
  const nodeConfig = {
    draw: (item) =>
      drawNode({
        item,
        logoText: 'GC',
        primaryColor: '#b175ca',
        fillColor: '#FCF0FF',
      }),
    afterDraw() {},
    anchor: [
      [0, 0.5], // 左边中点
      [1, 0.5], // 右边中点
    ],
  };

  return <RegisterNode name="CONDITIONS-NODE" config={nodeConfig} extend="rect" />;
};

export default ConditionNode;
