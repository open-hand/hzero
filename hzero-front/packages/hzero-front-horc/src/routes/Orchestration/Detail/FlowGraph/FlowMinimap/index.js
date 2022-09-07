/**
 * FlowMinimap
 * 任务流画布-缩略图
 * @author baitao.huang@hand-china.com
 * @date 2020/1/2
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Card } from 'hzero-ui';
import { Minimap } from 'gg-editor';

import ORCHESTRATION_LANG from '@/langs/orchestrationLang';

const FlowMinimap = () => {
  return (
    <Card type="inner" size="small" title={ORCHESTRATION_LANG.MINI_MAP} bordered={false}>
      <Minimap height={400} width={400} />
    </Card>
  );
};

export default FlowMinimap;
