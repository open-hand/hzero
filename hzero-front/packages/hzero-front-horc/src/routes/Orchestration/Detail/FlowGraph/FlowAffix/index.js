/**
 * FlowAffix
 * @author baitao.huang@hand-china.com
 * @date 2020/3/6
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { isEmpty } from 'lodash';
import { Affix, Icon, Tabs } from 'choerodon-ui';
import { Tooltip } from 'hzero-ui';
import { withPropsAPI } from 'gg-editor';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
// eslint-disable-next-line import/no-cycle
import { FlowMinimap } from '../../FlowGraph';

const { TabPane } = Tabs;

class FlowAffix extends React.Component {
  state = {
    currentKey: '',
  };

  handleTabClick = (key) => {
    const { currentKey } = this.state;
    if (key === currentKey) {
      this.setState({ currentKey: '' });
    } else {
      this.setState({ currentKey: key });
    }
  };

  get getDisabled() {
    try {
      const { propsAPI } = this.props;
      return isEmpty(propsAPI.save());
    } catch (e) {
      return false;
    }
  }

  render() {
    const { currentKey } = this.state;
    const disabled = this.getDisabled;
    return (
      <Affix style={{ position: 'absolute', top: 120, right: 0 }}>
        <Tabs
          tabPosition="right"
          size="small"
          activeKey={currentKey}
          onTabClick={this.handleTabClick}
        >
          <TabPane
            forceRender
            disabled={disabled}
            key="1"
            tab={
              <Tooltip title={ORCHESTRATION_LANG.MINI_MAP} placement="left">
                <Icon disabled={disabled} type="photo_library" />
              </Tooltip>
            }
          >
            <FlowMinimap />
          </TabPane>
        </Tabs>
      </Affix>
    );
  }
}

export default withPropsAPI(FlowAffix);
