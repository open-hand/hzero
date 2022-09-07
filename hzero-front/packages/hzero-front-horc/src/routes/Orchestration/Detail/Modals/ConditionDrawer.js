/**
 *节点详情
 * @author changwen.yu@hand-china.com
 * @date 2020/9/18
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { Card } from 'choerodon-ui';
import { DataSet, Form, Select } from 'choerodon-ui/pro';
import { withPropsAPI } from 'gg-editor';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import QuestionPopover from '@/components/QuestionPopover';
import LogicOperation from '@/components/LogicOperation';

import { conditionDS } from '@/stores/Orchestration/orchestrationDS';

class ConditionDrawer extends React.Component {
  constructor(props) {
    super(props);
    this.conditionDS = new DataSet(conditionDS());
    props.onRef(this);
    this.state = {
      dependency: {},
      nextTasks: [],
      preTasksExcludeAsyncNode: [],
      successNode: [],
      failedNode: [],
    };
  }

  componentDidMount() {
    if (!isEmpty(this.nodeItem.model)) {
      this.init();
    } else {
      this.conditionDS.create();
    }
  }

  /**
   * 数据加载
   */
  init() {
    const { conditionResult, dependency, id } = this.nodeItem.model;
    const { successNode, failedNode } = conditionResult;
    const formData = {
      successNode,
      failedNode,
      dependency,
    };
    this.setState({
      dependency: isUndefined(dependency) ? { and: [] } : JSON.parse(dependency),
      nextTasks: this.getNextTasks(id),
      preTasksExcludeAsyncNode: this.getPreTasksExcludeAsyncNode(id),
      successNode,
      failedNode,
    });
    this.conditionDS.create(formData);
  }

  /**
   * 获取当前节点
   */
  get nodeItem() {
    const { propsAPI } = this.props;
    return propsAPI.getSelected()[0] || {};
  }

  /**
   * 获取前置节点-排除掉异步节点
   */
  getPreTasksExcludeAsyncNode(taskId) {
    const { propsAPI } = this.props;
    const { nodes: originNodes = [], edges: originEdges = [] } = propsAPI.save();
    const preTaskId = originEdges
      .filter((edge) => edge.target === taskId)
      .map((edge) => edge.source);
    const preTasks = originNodes
      .filter((node) => preTaskId.includes(node.id) && node.threadMechanism === 'SYNC')
      .map((node) => node.name);
    return preTasks;
  }

  /**
   * 获取后置节点
   */
  getNextTasks(taskId) {
    const { propsAPI } = this.props;
    const { nodes: originNodes = [], edges: originEdges = [] } = propsAPI.save();
    const nextTaskId = originEdges
      .filter((edge) => edge.source === taskId)
      .map((edge) => edge.target);
    const nextTasks = originNodes
      .filter((node) => nextTaskId.includes(node.id))
      .map((node) => node.name);
    return nextTasks;
  }

  /**
   * 节点信息滑窗信息保存
   */
  @Bind()
  async handleOk() {
    if (!(await this.conditionDS.validate())) {
      return undefined;
    }
    return this.formatNodeData(this.conditionDS.current.toData());
  }

  /**
   * 格式化节点数据格式
   */
  formatNodeData(nodeData) {
    const { dependency, successNode, failedNode, ...other } = nodeData;
    const conditionResult = {
      successNode,
      failedNode,
    };
    return {
      ...other,
      dependency,
      conditionResult,
    };
  }

  @Bind()
  getFormat(format) {
    const { jsonLogicFormat: dependency } = format;
    const { current } = this.conditionDS;
    current.set('dependency', dependency);
  }

  render() {
    const { dependency, preTasksExcludeAsyncNode } = this.state;
    const { disabledFlag } = this.props;
    const logicOperationProps = {
      value: dependency,
      operators: [
        {
          value: 'is_empty',
          meaning: ORCHESTRATION_LANG.CONDITION_SUCCESS_NODE,
        },
        {
          value: 'is_not_empty',
          meaning: ORCHESTRATION_LANG.CONDITION_FAILED_NODE,
        },
      ],
      showSearch: false,
      fieldOptions: preTasksExcludeAsyncNode,
      onGetFormat: this.getFormat,
    };
    return (
      <>
        <Card
          style={{ marginTop: '3px' }}
          title={
            <h3>
              {
                <QuestionPopover
                  text={ORCHESTRATION_LANG.GATEWAY}
                  message={ORCHESTRATION_LANG.GATEWAY_TIP}
                />
              }
            </h3>
          }
        >
          <Form dataSet={this.conditionDS} labelWidth={120}>
            <LogicOperation
              name="dependency"
              readOnly={disabledFlag}
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.DEPENDENCY}
                  message={ORCHESTRATION_LANG.DEPENDENCY_TIP}
                />
              }
              {...logicOperationProps}
            />
          </Form>
        </Card>
        <Card
          style={{ marginTop: '3px' }}
          title={
            <h3>
              {
                <QuestionPopover
                  text={ORCHESTRATION_LANG.CONDITION_RESULT}
                  message={ORCHESTRATION_LANG.CONDITION_RESULT_TIP}
                />
              }
            </h3>
          }
        >
          <Form dataSet={this.conditionDS} labelWidth={120}>
            <Select
              name="successNode"
              placeholder={ORCHESTRATION_LANG.CONDITION_SUCCESS_NODE}
              searchable
              disabled={disabledFlag}
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.CONDITION_SUCCESS_NODE}
                  message={ORCHESTRATION_LANG.CONDITION_SUCCESS_NODE_TIP}
                />
              }
              onChange={(value) => this.setState({ successNode: value === null ? [] : value })}
            >
              {this.state.nextTasks
                .filter((nextTask) => !this.state.failedNode.includes(nextTask))
                .map((nextTask) => (
                  <Select.Option value={nextTask}>{nextTask}</Select.Option>
                ))}
            </Select>
            <Select
              name="failedNode"
              placeholder={ORCHESTRATION_LANG.CONDITION_FAILED_NODE}
              searchable
              disabled={disabledFlag}
              label={
                <QuestionPopover
                  text={ORCHESTRATION_LANG.CONDITION_FAILED_NODE}
                  message={ORCHESTRATION_LANG.CONDITION_FAILED_NODE_TIP}
                />
              }
              onChange={(value) => this.setState({ failedNode: value === null ? [] : value })}
            >
              {this.state.nextTasks
                .filter((nextTask) => !this.state.successNode.includes(nextTask))
                .map((nextTask) => (
                  <Select.Option value={nextTask}>{nextTask}</Select.Option>
                ))}
            </Select>
          </Form>
        </Card>
      </>
    );
  }
}
export default withPropsAPI(ConditionDrawer);
