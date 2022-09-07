/**
 * 保存流程图节点 - 回传流程图节点信息函数
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-21
 * @LastEditTime: 2019-11-11 22:42
 * @Copyright: Copyright (c) 2018, Hand
 */
import uuid from 'uuid/v4';
import { isEmpty } from 'lodash';
import nodeConfig from './componentNodeConfig';
import nodeDS from '../routes/rules/stores/NodeDS';
import flowDS from '../routes/rules/stores/FlowDS';

/**
 * 保存回传节点信息
 * @param value 节点信息
 * @param nodeId 节点ID
 */
async function saveNode(value, nodeId) {
  if (value) {
    const { id, _status, ...nodeInfo } = value;
    if (_status === 'create') {
      // 创建组件成功后 重新查询节点 保证后续直接删除
      const res = await nodeDS.query();
      // 保证流程图保存更新节点位置
      if (nodeDS.current) {
        nodeDS.current.set('isChange', true);
      }
      // 更新流程图节点信息
      if (!isEmpty(res) && flowDS.current) {
        const record = flowDS.current.toData();
        const nodes = res.map(item => {
          if (item.id === id) {
            return { id, ...nodeInfo };
          } else {
            return item;
          }
        });
        flowDS.current.set('nodes', nodes);
        flowDS.current.set('flowData', { ...record.flowData, nodes });
      }
    }
  } else if (!isEmpty(nodeDS.toData())) {
    const currentNode = nodeDS.find(record => {
      return record.get('id') === nodeId.toString();
    });
    currentNode.set('isChange', true);
  }
}

/**
 * 获取节点信息
 * @param data 组件信息
 * @param nodeType 节点类型
 * @param name 组件名称字段
 */
function getNodeConfig(data, nodeType, name) {
  const { tenantId, ruleCode, _status, id } = data;
  let xPositon = 60;
  let yPositon = 170;
  if (nodeDS) {
    const nodeArr = nodeDS.toData();
    nodeArr.map(item => {
      if (item.id === id) {
        xPositon = item.x;
        yPositon = item.y;
      }
      return item;
    });
  }
  if (_status === 'create') {
    return {
      ...nodeConfig()[nodeType],
      id: id || uuid().substr(0, 8),
      label: data[name],
      componentName: data[name],
      x: xPositon,
      y: yPositon,
      tenantId,
      ruleCode,
      _status,
    };
  }
}

export { saveNode, getNodeConfig };
