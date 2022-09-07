/**
 * 结算规则 - 流程图
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-17
 * @LastEditTime: 2019-10-23 23:58
 * @Copyright: Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Header } from 'components/Page';
import { Row, Col, Spin, Button } from 'choerodon-ui/pro';
import GGEditor, { Flow } from 'gg-editor';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { openTab } from 'utils/menuTab';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';
import querystring from 'querystring';
import { testFlow } from '@/services/ruleTestService';
import flowDS from '../stores/FlowDS';
import SaveButton from './SaveButton';
import EditorMinimap from '../../../components/EditorMinimap';
import { FlowContextMenu } from '../../../components/EditorContextMenu';
import { FlowToolbar } from '../../../components/EditorToolbar';
import { FlowItemPanel } from '../../../components/EditorItemPanel';
import { FlowDetailPanel } from '../../../components/EditorDetailPanel';
import { CustomNode, CustomEndNode, CustomStartNode } from '../../../components/customNode';
import styles from './index.less';
import nodeDS from '../stores/NodeDS';

/**
 * 流程图
 * @extends {Component} - Component
 * @reactProps {Object} [history={}]
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 *
 */

/**
 * 跳转组件详情路由变量名
 * @type {object}
 */
const cptTypes = {
  FORMULA_EXECUTOR: 'formula',
  SQL_EXECUTOR: 'sql',
  GROUP_EXECUTOR: 'grouping',
  MAPPING_EXECUTOR: 'mapping',
  RULE_EXECUTOR: 'rule-component',
};

@connect()
@formatterCollections({ code: ['hres.flow', 'hres.common'] })
export default class FlowPage extends Component {
  saveButton = React.createRef();

  state = {
    loading: true,
  };

  async componentDidMount() {
    const {
      location: { search },
      match,
    } = this.props;
    const { code } = match.params;
    const { refresh } = querystring.parse(search.substring(1));
    flowDS.setQueryParameter('ruleCode', code);
    flowDS.setQueryParameter('tenantId', getCurrentOrganizationId());
    nodeDS.setQueryParameter('ruleCode', code);
    nodeDS.setQueryParameter('tenantId', getCurrentOrganizationId());
    // 新建、更新组件后保留节点是否更改状态，保证移动位置后判断调用保存API
    const dirty = nodeDS.isModified();
    const nodeRes = await nodeDS.query();
    if (!isEmpty(nodeRes) && nodeDS.current) {
      nodeDS.current.set('isChange', dirty);
    }
    // 流程图查询，初始节点、边信息赋值
    if ((!isEmpty(code) && isEmpty(flowDS.current)) || refresh) {
      try {
        const res = await flowDS.query();
        if (!res.status && !isEmpty(res)) {
          flowDS.current.set('nodes', res.nodes);
          flowDS.current.set('edges', res.edges);
          flowDS.current.set('ruleCode', code);
          flowDS.current.set('tenantId', getCurrentOrganizationId());
          flowDS.current.set('flowData', res);
        } else {
          flowDS.create({
            ruleCode: code,
            tenantId: getCurrentOrganizationId(),
          });
        }
      } catch (e) {
        flowDS.create({
          ruleCode: code,
          tenantId: getCurrentOrganizationId(),
        });
      }
    }
    this.setState({
      loading: false,
    });
  }

  /**
   * 双击节点跳转
   * @param e
   */
  @Bind()
  nodeDetail(e) {
    if (
      e._type === 'dblclick' &&
      e.item &&
      e.item.type === 'node' &&
      !['START', 'END'].includes(e.item.model.componentType)
    ) {
      const flowData = this.saveButton.current.props.propsAPI.save();
      flowDS.current.set('nodes', flowData.nodes);
      flowDS.current.set('edges', flowData.edges);
      flowDS.current.set('flowData', flowData);
      const { componentType, ruleCode, id, componentName } = e.item.model;
      this.gotoDetail(componentType, componentName, ruleCode, id);
    }
  }

  /**
   * 路由跳转组件详情
   * @param {string} cptType - 组件类型
   * @param {string} componentName - 组件名称
   * @param {string} ruleCode - 查询code
   * @param {string} id - 组件Id
   */
  @Bind()
  gotoDetail(cptType, componentName, ruleCode, id) {
    const {
      dispatch,
      location: { search },
      match,
    } = this.props;
    const { pageType, ruleName } = querystring.parse(search.substring(1));
    const { code } = match.params;
    const pathname = `/hres/rules/flow/${cptTypes[cptType]}/detail/${ruleCode || code}/${id}`;
    dispatch(
      routerRedux.push({
        pathname,
        search: querystring.stringify({ componentName, pageType, ruleName }),
      })
    );
  }

  /**
   * flow 命令监听
   * @param command
   * @returns {Promise<void>}
   */
  @Bind()
  async onAfterCommandExecute({ command }) {
    const {
      location: { search },
      match,
    } = this.props;
    const { code } = match.params;
    const { pageType } = querystring.parse(search.substring(1));
    const frozenFlag = pageType === 'view';
    const { propsAPI } = this.saveButton.current.props;
    const item = propsAPI.getSelected()[0];
    // 冻结状态不可操作（因为需保留左侧预览节点，readonly属性无效、使用undo处理）
    if (frozenFlag) {
      propsAPI.executeCommand('undo');
      return;
    }
    // 添加节点、开始结束节点直接提交，其他节点临时缓存、propsAPI.update更新流程图节点信息
    // 边信息缓存，点击保存再提交
    if (command.name === 'add') {
      if (command.type === 'node') {
        nodeDS.create(
          {
            ...command.addModel,
            ruleCode: code,
            tenantId: getCurrentOrganizationId(),
          },
          0
        );
        if (['START', 'END'].includes(command.addModel.componentType)) {
          const nodesArr = nodeDS.toData();
          const startNodes = nodesArr.filter(start => start.componentType === 'START');
          const endNodes = nodesArr.filter(end => end.componentType === 'END');
          if (startNodes.length > 1 || endNodes.length > 1) {
            propsAPI.executeCommand('undo');
            nodeDS.remove(nodeDS.current);
            notification.error({
              message: intl.get('hres.flow.view.message.validate.overs').d('已存在开始或结束节点'),
            });
          } else {
            const resNode = await nodeDS.submit();
            if (flowDS.current && resNode && resNode.success) {
              propsAPI.update(item, resNode.content[0]);
            }
          }
        }
      } else if (command.type === 'edge') {
        const nodesArr = nodeDS.toData();
        const endNode = nodesArr.filter(ende => ende.componentType === 'END')[0];
        const startNode = nodesArr.filter(starte => starte.componentType === 'START')[0];
        const isSelf = command.addModel.target === command.addModel.source;
        const isEnd = command.addModel.source === (endNode ? endNode.id : null);
        const isStart = command.addModel.target === (startNode ? startNode.id : null);
        if (isSelf) {
          notification.error({
            message: intl.get('hres.flow.view.message.validate.connectSelf').d('不支持节点自连接!'),
          });
          propsAPI.executeCommand('undo');
        }
        if (isEnd || isStart) {
          propsAPI.executeCommand('undo');
        }
      }
    }
    // 流程图执行命令（节点或边有做修改），nodeDS变化设置为true
    if (command.name === 'common') {
      nodeDS.current.set('isChange', true);
    }
    // 删除校验
    if (command.name === 'delete' && command.selectedItems.length === 1) {
      const edgesId = command.snapShot.edges ? command.snapShot.edges.map(edge => edge.id) : [];
      const selectedItem = command.selectedItems[0];
      if (edgesId.includes(selectedItem)) return;
      const selectNode = command.snapShot.nodes.filter(node => node.id === selectedItem)[0];
      const recordNode = nodeDS.records.filter(record => record.get('id') === selectedItem);
      if (selectNode.componentName || ['START', 'END'].includes(selectNode.componentType)) {
        try {
          const res = await nodeDS.delete(
            recordNode,
            intl.get('hres.flow.view.message.validate.delete').d('是否删除节点？')
          );
          if (!isEmpty(res) && res.failed) {
            propsAPI.executeCommand('undo');
          } else if (res === undefined) {
            propsAPI.executeCommand('undo');
          }
        } catch (e) {
          propsAPI.executeCommand('undo');
        }
      }
    } else if (command.name === 'delete' && command.selectedItems.length > 1) {
      notification.error({
        message: intl
          .get('hres.flow.view.message.validate.deleteBatch')
          .d('不支持批量删除组件节点!'),
      });
      propsAPI.executeCommand('undo');
    }
  }

  /**
   * 测试流程图是否合法
   */
  @Bind()
  async testFlow() {
    const { match } = this.props;
    const { code } = match.params;
    if (!nodeDS.isModified()) {
      const res = await testFlow({ ruleCode: code, tenantId: getCurrentOrganizationId() });
      const response = getResponse(res);
      if (response && response.status) {
        if (response.status === 'E') {
          notification.error({
            message: response.errorMsg,
          });
        } else if (response.status === 'S') {
          notification.success({
            message: intl.get('hres.common.notification.validate').d('校验通过'),
          });
        }
      }
    } else {
      notification.error({
        message: intl.get('hres.flow.view.message.validateSave').d('请保存流程图后再校验'),
      });
    }
  }

  /**
   * 跳转测试界面
   */
  @Bind()
  goToTestPage() {
    const {
      location: { search },
      match,
    } = this.props;
    const { code } = match.params;
    const { pageType, ruleName } = querystring.parse(search.substring(1));
    const pathname = `/hres/rules/test/${code}`;
    openTab({
      key: pathname,
      title: `${intl.get('hzero.common.button.test').d('测试')}_${ruleName}`,
      search: querystring.stringify({
        isFlow: true,
        pageType,
        ruleName,
      }),
    });
  }

  render() {
    const {
      location: { search },
      match,
    } = this.props;
    const { code } = match.params;
    const { pageType, ruleName } = querystring.parse(search.substring(1));
    const currentData = flowDS.current ? flowDS.current.toData().flowData : {};
    const frozenFlag = pageType === 'view';

    return (
      <>
        <GGEditor onAfterCommandExecute={this.onAfterCommandExecute} className={styles.editor}>
          <Header
            title={`${intl.get('hres.flow.view.title.edit').d('编辑流程')}_${ruleName}`}
            backPath="/hres/rules/list"
          >
            <SaveButton
              disabled={frozenFlag}
              ruleCode={code}
              ref={this.saveButton}
              key="submit"
              dataSet={flowDS}
            />
            <Button icon="test_execute" onClick={this.testFlow}>
              {intl.get('hres.flow.view.button.validate').d('校验')}
            </Button>
            <Button icon="test_execute" onClick={this.goToTestPage}>
              {intl.get('hzero.common.button.test').d('测试')}
            </Button>
          </Header>
          <Row type="flex" className={styles.editorHd}>
            <Col span={24}>
              <FlowToolbar />
            </Col>
          </Row>
          <Row type="flex" className={styles.editorBd}>
            <Col span={4} className={styles.editorSidebar}>
              <FlowItemPanel />
            </Col>
            <Col span={16} className={styles.editorContent}>
              {this.state.loading ? (
                <Spin />
              ) : (
                <>
                  <Flow
                    shortcut={{
                      copy: false,
                      paste: false,
                      selectAll: false,
                      multiSelect: false,
                      undo: false,
                      redo: false,
                    }}
                    graph={{
                      edgeDefaultShape: 'flow-polyline',
                      mode: frozenFlag && 'readOnly',
                    }}
                    noEndEdge={false}
                    onDoubleClick={this.nodeDetail}
                    className={styles.flow}
                    data={currentData}
                  />
                  <CustomNode />
                  <CustomEndNode />
                  <CustomStartNode />
                </>
              )}
            </Col>
            <Col span={4} className={styles.editorSidebar}>
              <EditorMinimap />
              <FlowDetailPanel />
            </Col>
          </Row>
          <FlowContextMenu />
        </GGEditor>
      </>
    );
  }
}
