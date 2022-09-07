/**
 * 流程图 - 流程图保存按钮
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-21
 * @LastEditTime: 2019-10-23 23:48
 * @Copyright: Copyright (c) 2018, Hand
 */
import React from 'react';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import { isEmpty } from 'lodash';
import { Button } from 'choerodon-ui/pro';
import { withPropsAPI } from 'gg-editor';
import nodeDS from '../stores/NodeDS';

class SaveButton extends React.Component {
  /**
   * 点击保存函数及保存校验
   * @returns {Promise<void>}
   */
  handleClick = async () => {
    const { propsAPI, dataSet, ruleCode } = this.props;
    const data = await propsAPI.save();
    const nodesLen = data.nodes ? data.nodes.length : 0;
    const edgesLen = data.edges ? data.edges.length : 0;
    const startNodes = data.nodes ? data.nodes.filter(item => item.componentType === 'START') : 0;
    let res = true;
    if (startNodes.length > 1) {
      notification.error({
        message: intl.get('hres.flow.view.message.validate.start').d('存在多个开始节点!'),
      });
      res = false;
    }
    if (data.nodes && nodesLen > 1) {
      if (!data.edges || edgesLen !== nodesLen - 1) {
        notification.error({
          message: intl
            .get('hres.flow.view.message.validate.edge')
            .d('流程图中有循环或孤立组件，请检查连线!'),
        });
        res = false;
      }
      let isAlone = false;
      data.nodes.map(node => {
        if (!node.componentName && !['START', 'END'].includes(node.componentType)) {
          isAlone = true;
          res = false;
        }
        return res;
      });
      if (isAlone) {
        notification.error({
          message: intl
            .get('hres.flow.view.message.validate.tempNode')
            .d('流程图存在临时的组件节点!'),
        });
      }
    } else if (data.nodes && nodesLen === 1 && !data.nodes[0].componentName) {
      notification.error({
        message: intl
          .get('hres.flow.view.message.validate.nodeCreated')
          .d('流程图至少存在一个已创建的组件节点!'),
      });
      res = false;
    } else if (!data.nodes) {
      notification.error({
        message: intl.get('hres.flow.view.message.validate.node').d('流程图至少存在一个节点!'),
      });
      res = false;
    }
    // 更新流程图DS提交信息
    let newNodes = [];
    const newEdges =
      data.edges &&
      data.edges.map(edge => ({ ...edge, tenantId: getCurrentOrganizationId(), ruleCode }));
    if (nodeDS.isModified()) {
      newNodes =
        data.nodes &&
        data.nodes.map(node => {
          if (['START', 'END'].includes(node.componentType)) {
            return { ...node, tenantId: getCurrentOrganizationId(), ruleCode, _status: 'update' };
          } else {
            return { ...node, _status: 'update' };
          }
        });
    }
    const currentRecord = dataSet.current;
    currentRecord.set('edges', newEdges);
    currentRecord.set('nodes', newNodes);
    currentRecord.set('ruleCode', ruleCode);
    currentRecord.set('tenantId', getCurrentOrganizationId());
    if (res) {
      const submitRes = await dataSet.submit();
      if (!isEmpty(submitRes) && submitRes.success && submitRes.content[0]) {
        submitRes.content[0].nodes.map(node => {
          propsAPI.update(propsAPI.find(node.id), node);
          return node;
        });
        currentRecord.set('flowData', data);
        nodeDS.query();
      }
    }
  };

  render() {
    return (
      <div style={{ ...this.props.style, padding: 8 }}>
        <Button
          icon="save"
          color="primary"
          onClick={this.handleClick}
          disabled={this.props.disabled}
        >
          {intl.get('hzero.common.button.save').d('保存')}
        </Button>
      </div>
    );
  }
}

export default withPropsAPI(SaveButton);
