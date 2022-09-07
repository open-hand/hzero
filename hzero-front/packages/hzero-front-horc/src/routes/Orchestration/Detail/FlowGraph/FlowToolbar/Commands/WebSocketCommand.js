/**
 * WebSocketCommand
 * @author baitao.huang@hand-china.com
 * @date 2020/8/18
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { isEmpty } from 'lodash';
import { RegisterCommand, withPropsAPI } from 'gg-editor';

class WebSocketCommand extends React.Component {
  render() {
    const config = {
      queue: false,
      execute: () => {
        const { webSocketMessage = {}, propsAPI } = this.props;
        const { body = {} } = webSocketMessage;
        const { taskInstances = [] } = body;
        if (!isEmpty(body)) {
          const { nodes = [], edges = [] } = propsAPI.save();
          const updatedNodes = nodes.map((node) => {
            const updatedTask = taskInstances.find((task) => task.taskName === node.name) || {};
            const { taskName, statusCode } = updatedTask;
            return node.name === taskName
              ? {
                  ...node,
                  statusCode,
                  updatedMessage: updatedTask,
                }
              : node;
          });
          const updatedEdges = edges.map((edge) => {
            const matchNode = nodes.find((node) => node.id === edge.source) || {};
            const { statusCode, taskName } =
              taskInstances.find((task) => task.taskName === matchNode.name) || {};
            if (matchNode.name === taskName) {
              return { ...edge, statusCode };
            } else {
              return edge;
            }
          });
          const workflowGraphData = { edges: updatedEdges, nodes: updatedNodes };
          propsAPI.read(workflowGraphData);
        }
      },
    };

    return [<RegisterCommand name="webSocketExec" config={config} />];
  }
}

export default withPropsAPI(WebSocketCommand);
