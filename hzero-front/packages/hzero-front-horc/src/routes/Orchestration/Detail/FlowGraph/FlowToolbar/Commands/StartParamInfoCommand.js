/**
 * StartParamCommand
 * @author baitao.huang@hand-china.com
 * @date 2020/8/24
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { RegisterCommand, withPropsAPI } from 'gg-editor';
import { Popover } from 'choerodon-ui';
import FlowMinimap from '../../FlowMinimap';

class StartParamCommand extends React.Component {
  render() {
    const config = {
      queue: false,
      execute: () => {},
    };

    return [
      <Popover content={<FlowMinimap />} trigger="click">
        <RegisterCommand name="startParamInfo" config={config} />
      </Popover>,
    ];
  }
}

export default withPropsAPI(StartParamCommand);
