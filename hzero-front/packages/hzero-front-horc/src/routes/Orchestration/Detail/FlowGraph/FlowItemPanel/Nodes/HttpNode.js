/**
 * HttpNode
 * @author baitao.huang@hand-china.com
 * @date 2020/4/9
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { RegisterNode } from 'gg-editor';
import { drawNode } from './drawUtils';

const HttpNode = () => {
  const nodeConfig = {
    draw: (item) =>
      drawNode({
        item,
        logoText: 'http',
        primaryColor: '#59b9ff',
        fillColor: '#e6f7ff',
      }),
    afterDraw() {},
    anchor: [
      [0, 0.5], // 左边中点
      [1, 0.5], // 右边中点
    ],
  };

  return <RegisterNode name="HTTP-NODE" config={nodeConfig} extend="rect" />;
};

export default HttpNode;
