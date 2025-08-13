/**
 * 事件规则 - 流程图保存按钮
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @Date: 2020-08-13
 * @Copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { withPropsAPI } from 'gg-editor';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';

class SaveButton extends React.Component {
  /**
   * 点击保存函数及保存校验
   * @returns {Promise<void>}
   */
  handleClick = async () => {
    const { propsAPI, nodeDS, onSearch } = this.props;
    if (!nodeDS.isModified()) {
      notification.error({
        message: intl.get('hpfm.event.view.message.validate.unChange').d('没有数据变更!'),
      });
      return false;
    }
    const data = await propsAPI.save();
    const flag = await nodeDS.validate();
    if (!flag) {
      notification.error({
        message: intl
          .get('hpfm.event.view.message.validate.data')
          .d('流程图中有校验不通过的数据，请检查数据!'),
      });
      return flag;
    }

    const nodesLen = data.nodes ? data.nodes.length : 0;
    const edgesLen = data.edges ? data.edges.length : 0;
    const startNodes = data.nodes ? data.nodes.filter((item) => item.componentType === 'START') : 0;
    if (startNodes.length > 1) {
      notification.error({
        message: intl.get('hpfm.event.view.message.validate.start').d('存在多个开始节点!'),
      });
      return false;
    }
    if (data.nodes && nodesLen === 1 && !data.nodes[0].componentName) {
      notification.error({
        message: intl
          .get('hpfm.event.view.message.validate.nodeCreated')
          .d('流程图至少存在一个已创建的组件节点!'),
      });
      return false;
    } else if (edgesLen !== (3 * (nodesLen - 2)) / 2) {
      notification.error({
        message: intl
          .get('hpfm.event.view.message.validate.edge')
          .d('流程图中有组件缺失，请检查连线!!'),
      });
      return false;
    } else if (!data.nodes) {
      notification.error({
        message: intl.get('hpfm.event.view.message.validate.node').d('流程图至少存在一个节点!'),
      });

      return false;
    }
    await nodeDS.submit();
    onSearch();
  };

  render() {
    const { path, disabled } = this.props;
    return (
      <div style={{ ...this.props.style, padding: 8 }}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}.save`,
              type: 'button',
              meaning: '事件规则流程图-保存',
            },
          ]}
          icon="save"
          onClick={this.handleClick}
          disabled={disabled}
        >
          {intl.get('hzero.common.button.save').d('保存')}
        </ButtonPermission>
      </div>
    );
  }
}

export default withPropsAPI(SaveButton);
