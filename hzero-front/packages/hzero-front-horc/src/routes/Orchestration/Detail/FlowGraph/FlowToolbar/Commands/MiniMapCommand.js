/**
 * FlowMinimapCommand
 * @author baitao.huang@hand-china.com
 * @date 2020/2/23
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { isEmpty } from 'lodash';
import { RegisterCommand, withPropsAPI } from 'gg-editor';

class MiniMapCommand extends React.Component {
  render() {
    const config = {
      queue: true,
      enable: () => {
        const { propsAPI } = this.props;
        return !isEmpty(propsAPI.save());
      },
      execute: () => {
        const { miniMapVisible, toggleMiniMapVisible } = this.props;
        toggleMiniMapVisible(!miniMapVisible);
      },
    };

    return [<RegisterCommand name="miniMap" config={config} />];
  }
}

export default withPropsAPI(MiniMapCommand);
