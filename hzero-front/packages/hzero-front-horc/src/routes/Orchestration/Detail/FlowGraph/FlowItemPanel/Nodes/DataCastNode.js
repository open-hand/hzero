/**
 * DataCastNode
 * @author baitao.huang@hand-china.com
 * @date 2020/7/15
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { RegisterNode } from 'gg-editor';
import { drawNode } from './drawUtils';

const DataCastNode = () => {
  const nodeConfig = {
    draw: (item) =>
      drawNode({
        item,
        logoText: 'dc',
        primaryColor: '#faad14',
        fillColor: '#fffbe6',
      }),
    afterDraw() {},
    anchor: [
      [0, 0.5], // 左边中点
      [1, 0.5], // 右边中点
    ],
  };

  return <RegisterNode name="CAST-NODE" config={nodeConfig} extend="rect" />;
};

export default DataCastNode;
