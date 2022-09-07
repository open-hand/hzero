/**
 * SaveFlowCommand
 * @author baitao.huang@hand-china.com
 * @date 2020/2/23
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { isEmpty } from 'lodash';
import { RegisterCommand, withPropsAPI } from 'gg-editor';
import { Popover } from 'choerodon-ui';
import FlowMinimap from '../../FlowMinimap';

class SaveFlowCommand extends React.Component {
  render() {
    const config = {
      queue: true,
      enable: () => {
        const { propsAPI } = this.props;
        return !isEmpty(propsAPI.save());
      },
      execute: () => {
        const { propsAPI, handleOpenHeaderModal } = this.props;
        const data = propsAPI.save();
        handleOpenHeaderModal(data);
      },
    };

    return [
      <Popover content={<FlowMinimap />} trigger="click">
        <RegisterCommand name="saveFlow" config={config} />
      </Popover>,
    ];
  }
}

export default withPropsAPI(SaveFlowCommand);
