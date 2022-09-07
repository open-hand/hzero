/**
 * FieldMappingNode
 * @author baitao.huang@hand-china.com
 * @date 2020/4/9
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { RegisterNode } from 'gg-editor';
import { drawNode } from './drawUtils';

const FieldMappingNode = () => {
  const nodeConfig = {
    draw: (item) =>
      drawNode({
        item,
        logoText: 'fm',
        primaryColor: '#fa541c',
        fillColor: '#fff2e8',
      }),
    afterDraw() {},
    anchor: [
      [0, 0.5], // 左边中点
      [1, 0.5], // 右边中点
    ],
  };

  return <RegisterNode name="TRANSFORM-NODE" config={nodeConfig} extend="rect" />;
};

export default FieldMappingNode;
