/**
 * EditCommand
 * @author baitao.huang@hand-china.com
 * @date 2020/8/11
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { isEmpty } from 'lodash';
import { RegisterCommand, withPropsAPI } from 'gg-editor';
import { Popover } from 'choerodon-ui';
import FlowMinimap from '../../FlowMinimap';

class EditCommand extends React.Component {
  render() {
    const config = {
      queue: true,
      enable: () => {
        const { propsAPI } = this.props;
        return !isEmpty(propsAPI.save());
      },
      execute: () => {
        const { propsAPI, onOpenDrawerByShape } = this.props;
        const itemModel = propsAPI.getSelected()[0] || {};
        const { shape } = itemModel.model;
        onOpenDrawerByShape(shape);
      },
    };

    return [
      <Popover content={<FlowMinimap />} trigger="click">
        <RegisterCommand name="editFlow" config={config} />
      </Popover>,
    ];
  }
}

export default withPropsAPI(EditCommand);
