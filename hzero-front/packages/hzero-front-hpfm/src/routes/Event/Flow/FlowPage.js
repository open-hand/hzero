/**
 * 事件规则 - 流程图
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @Date: 2020-08-13
 * @Copyright: Copyright (c) 2020, Hand
 */

import { connect } from 'dva';
import uuid from 'uuid/v4';
import { isEmpty } from 'lodash';
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import GGEditor, { Flow } from 'gg-editor';
import {
  DataSet,
  Row,
  Col,
  Spin,
  Form,
  TextField,
  Select,
  NumberField,
  Switch,
  Modal,
} from 'choerodon-ui/pro';

import { Header } from 'components/Page';
import EditorMinimap from '@/components/gg-editor/EditorMinimap';
import { FlowContextMenu } from '@/components/gg-editor/EditorContextMenu';
import { FlowToolbar } from '@/components/gg-editor/EditorToolbar';
import { FlowItemPanel } from '@/components/gg-editor/EditorItemPanel';
import { FlowDetailPanel } from '@/components/gg-editor/EditorDetailPanel';
import { CustomNode, CustomEndNode, CustomStartNode } from '@/components/gg-editor/customNode';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';

import { nodeDs } from '@/stores/FlowDS';

import SaveButton from './SaveButton';
import styles from './index.less';

/**
 * 流程图
 * @extends {Component} - Component
 * @reactProps {Object} [history={}]
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 *
 */

@connect()
@formatterCollections({ code: ['hpfm.event'] })
export default class FlowPage extends Component {
  constructor(props) {
    super(props);
    const { match } = props;
    const { id } = match.params;
    this.nodeDS = new DataSet(nodeDs(id));
    this.saveButton = React.createRef();
    this.state = {
      loading: true,
      originData: { nodes: [], edges: [] },
    };
  }

  conditionList = [];

  position = {};

  componentDidMount() {
    this.handleSearch();
  }

  /**
   * 双击节点跳转
   * @param e
   */
  @Bind()
  async handleSearch() {
    this.setState({
      loading: true,
    });
    await this.nodeDS.query();
    const data = this.nodeDS.toData();
    const nodes = [
      {
        color: '#FA8C16',
        componentType: 'START',
        componentTypeDesc: '开始',
        id: 'ea1184e8',
        label: 'Start',
        labelOffsetY: 0,
        shape: 'custom-start',
        size: '72*72',
        type: 'node',
        x: 100,
        y: 300,
      },
      {
        color: '#722ED1',
        componentType: 'END',
        componentTypeDesc: '结束',
        id: '481fbb1a',
        label: 'End',
        labelOffsetY: 0,
        shape: 'custom-end',
        size: '72*72',
        type: 'node',
        x: 800,
        y: 300,
      },
    ];
    const edges = [];
    data.forEach((item, index) => {
      const key = uuid();
      nodes.push({
        color: '#1890FF',
        componentType: 'CONDITION_EXECUTOR',
        componentTypeDesc: '条件组件',
        label: { text: '条件组件' },
        labelOffsetY: 0,
        id: item.eventRuleId,
        shape: 'custom-node',
        size: '80*48',
        type: 'node',
        x: 300,
        y: (index + 1) * 100,
      });
      nodes.push({
        color: '#1890FF',
        componentType:
          // eslint-disable-next-line no-nested-ternary
          item.callType === 'M'
            ? 'METHOD_EXECUTOR'
            : item.callType === 'A'
            ? 'API_EXECUTOR'
            : 'WEBHOOK_EXECUTOR',
        componentTypeDesc:
          // eslint-disable-next-line no-nested-ternary
          item.callType === 'M'
            ? intl.get('hpfm.event.model.event.method').d('方法组件')
            : item.callType === 'A'
            ? intl.get('hpfm.event.model.event.API').d('API组件')
            : intl.get('hpfm.event.model.event.webHook').d('WebHook组件'),
        label: {
          text: `${
            // eslint-disable-next-line no-nested-ternary
            item.callType === 'M'
              ? intl.get('hpfm.event.model.event.method').d('方法组件')
              : item.callType === 'A'
              ? intl.get('hpfm.event.model.event.API').d('API组件')
              : intl.get('hpfm.event.model.event.webHook').d('WebHook组件')
          }
      

${item.orderSeq || ''}`,
        },
        // // eslint-disable-next-line no-nested-ternary
        // item.callType === 'M'
        //   ? intl.get('hpfm.event.model.event.method').d('方法组件')
        //   : item.callType === 'A'
        //   ? intl.get('hpfm.event.model.event.API').d('API组件')
        //   : intl.get('hpfm.event.model.event.webHook').d('WebHook组件'),
        id: key,
        labelOffsetY: 20,
        shape: 'custom-node',
        size: '80*48',
        type: 'node',
        parentId: item.eventRuleId,
        // icon: formula,
        x: 600,
        y: (index + 1) * 100,
      });
      edges.push({
        source: 'ea1184e8',
        target: item.eventRuleId,
      });
      edges.push({
        source: key,
        target: '481fbb1a',
      });
      edges.push({
        source: item.eventRuleId,
        target: key,
      });
      this.position[index] = index + 1;
    });
    this.setState({
      loading: false,
      originData: { nodes, edges },
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
      const { componentType, id, parentId } = e.item.model;
      if (componentType === 'CONDITION_EXECUTOR') {
        const record = this.nodeDS.find((item) =>
          item.get('id') ? item.get('id') === id : item.get('eventRuleId') === id
        );
        record.set('current', '0');
        Modal.open({
          closable: false,
          key: 'useConfig',
          title: intl.get('hpfm.event.view.message.condition').d('编辑条件组件'),
          drawer: false,
          children: (
            <Form record={record}>
              <TextField name="matchingRule" maxLength={500} />
            </Form>
          ),
          onOk: async () => {
            const flag = await record.validate();
            return flag;
          },
          onCancel: () => {},
          onClose: () => {
            record.set('current', null);
          },
        });
      } else {
        const record = this.nodeDS.find((item) =>
          item.get('id') ? item.get('id') === parentId : item.get('eventRuleId') === parentId
        );
        record.set('current', '1');
        Modal.open({
          closable: false,
          key: 'useConfig',
          title: intl.get('hpfm.event.view.message.application').d('编辑应用组件'),
          drawer: false,
          children: (
            <Form record={record}>
              {/* eslint-disable-next-line no-nested-ternary */}
              {componentType === 'METHOD_EXECUTOR' ? (
                <TextField name="beanName" maxLength={240} />
              ) : componentType === 'API_EXECUTOR' ? (
                <TextField name="apiUrl" maxLength={480} />
              ) : (
                <TextField name="serverCode" />
              )}
              {/* eslint-disable-next-line no-nested-ternary */}
              {componentType === 'METHOD_EXECUTOR' ? (
                <TextField name="methodName" maxLength={240} />
              ) : componentType === 'API_EXECUTOR' ? (
                <Select name="apiMethod" />
              ) : (
                <TextField name="messageCode" />
              )}
              <NumberField name="orderSeq" min={1} max={100} step={1} />
              <TextField name="ruleDescription" maxLength={255} />
              <Switch name="syncFlag" />
              <Switch name="resultFlag" />
              <Switch name="enabledFlag" />
            </Form>
          ),
          onOk: async () => {
            const flag = await record.validate();
            if (flag) {
              const { propsAPI } = this.saveButton.current.props;
              const temp = propsAPI.find(e.item.id);
              propsAPI.update(temp, {
                ...temp.model,
                label: {
                  text: `${
                    // eslint-disable-next-line no-nested-ternary
                    record.get('callType') === 'M'
                      ? intl.get('hpfm.event.model.event.method').d('方法组件')
                      : record.get('callType') === 'A'
                      ? intl.get('hpfm.event.model.event.API').d('API组件')
                      : intl.get('hpfm.event.model.event.webHook').d('WebHook组件')
                  }


${record.get('orderSeq') || ''}`,
                },
                labelOffsetY: 20,
              });
            }
            return flag;
          },
          onCancel: () => {},
          onClose: () => {
            record.set('current', null);
          },
        });
      }
    }
  }

  /**
   * flow 命令监听
   * @param command
   * @returns {Promise<void>}
   */
  @Bind()
  async onAfterCommandExecute({ command }) {
    const { propsAPI } = this.saveButton.current.props;
    // 添加节点、开始结束节点直接提交，其他节点临时缓存、propsAPI.update更新流程图节点信息
    // 边信息缓存，点击保存再提交
    if (command.name === 'add') {
      if (command.type === 'node') {
        if (command.addModel.componentType === 'CONDITION_EXECUTOR') {
          let i = 0;
          let j = 0;
          while (i < 999) {
            if (this.position[i]) {
              i++;
            } else {
              j = i;
              i = 1000;
              this.position[j] = 'n';
            }
          }

          this.conditionList.push(command.addModel.id);
          const temp = propsAPI.find(command.addId);
          propsAPI.update(temp, {
            ...command.addModel,
            x: command.addModel.componentType === 'CONDITION_EXECUTOR' ? 300 : 600,
            y: (j + 1) * 100,
          });
          propsAPI.add('edge', {
            source: 'ea1184e8',
            target: command.addModel.id,
          });
          this.nodeDS.create({ id: command.addModel.id });
        } else if (isEmpty(this.conditionList)) {
          propsAPI.executeCommand('undo');
          notification.error({
            message: intl
              .get('hpfm.event.view.message.validate.condition')
              .d('没有匹配的条件组件!'),
          });
        } else {
          let i = 0;
          let j = 0;
          while (i < 999) {
            if (this.position[i] && this.position[i] !== 'n') {
              i++;
            } else if (this.position[i] === 'n') {
              j = i;
              i = 1000;
              this.position[j] = j + 1;
            }
          }

          const temp = propsAPI.find(command.addId);
          propsAPI.update(temp, {
            ...command.addModel,
            x: command.addModel.componentType === 'CONDITION_EXECUTOR' ? 300 : 600,
            y: (j + 1) * 100,
            parentId: this.conditionList[0],
          });
          propsAPI.add('edge', {
            source: command.addModel.id,
            target: '481fbb1a',
          });
          propsAPI.add('edge', {
            source: this.conditionList[0],
            target: command.addModel.id,
          });
          const record = this.nodeDS.find((item) =>
            item.get('id')
              ? item.get('id') === this.conditionList[0]
              : item.get('eventRuleId') === this.conditionList[0]
          );
          record.set('callType', command.addModel.componentType[0]);
          this.conditionList.shift();
        }
      } else if (command.type === 'edge') {
        propsAPI.executeCommand('undo');
      }
    }
    // 流程图执行命令（节点或边有做修改），nodeDS变化设置为true
    if (command.name === 'common') {
      propsAPI.executeCommand('undo');
    }
    // 删除校验
    if (command.name === 'delete' && command.selectedItems.length === 1) {
      const selectedItem = command.selectedItems[0];
      const selectChildNode =
        command.snapShot.nodes.filter((node) => node.parentId === selectedItem)[0] || {};
      const selectNode = command.snapShot.nodes.filter((node) => node.id === selectedItem)[0] || {};
      const selectParentNode =
        command.snapShot.nodes.filter((node) => node.id === selectNode.parentId)[0] || {};
      const recordNode = this.nodeDS.records.filter((record) =>
        record.get('id')
          ? record.get('id') === selectedItem
          : record.get('eventRuleId') === selectedItem
      )[0];
      if (selectNode.componentType === 'CONDITION_EXECUTOR') {
        const temp = propsAPI.find(selectChildNode.id);
        propsAPI.remove(temp);
        if (recordNode.get('eventRuleId')) {
          recordNode.set('status', 'delete');
        } else {
          this.nodeDS.remove(recordNode);
        }
        this.position[selectNode.y / 100 - 1] = null;
      } else if (!['START', 'END'].includes(selectNode.componentType)) {
        this.conditionList.push(selectParentNode.id);
        this.position[selectNode.y / 100 - 1] = 'n';
      } else {
        propsAPI.executeCommand('undo');
      }
    } else if (command.name === 'delete' && command.selectedItems.length > 1) {
      notification.error({
        message: intl
          .get('hpfm.event.view.message.validate.deleteBatch')
          .d('不支持批量删除组件节点!'),
      });
      propsAPI.executeCommand('undo');
    }
  }

  render() {
    const { match } = this.props;
    const { path } = match;
    const { originData } = this.state;
    const currentData =
      this.nodeDS.length > 0
        ? originData
        : {
            nodes: [
              {
                color: '#FA8C16',
                componentType: 'START',
                componentTypeDesc: '开始',
                id: 'ea1184e8',
                label: 'Start',
                labelOffsetY: 0,
                shape: 'custom-start',
                size: '72*72',
                type: 'node',
                x: 100,
                y: 300,
              },
              {
                color: '#722ED1',
                componentType: 'END',
                componentTypeDesc: '结束',
                id: '481fbb1a',
                label: 'End',
                labelOffsetY: 0,
                shape: 'custom-end',
                size: '72*72',
                type: 'node',
                x: 800,
                y: 300,
              },
            ],
            edges: [],
          };

    return (
      <>
        <GGEditor onAfterCommandExecute={this.onAfterCommandExecute} className={styles.editor}>
          <Header
            title={intl.get('hpfm.event.view.title.edit').d('流程维护')}
            backPath="/hpfm/event/list"
          >
            <SaveButton
              // onLoading={(loading) => {
              //   // this.setState({ loading });
              // }}
              disabled={this.state.loading}
              onSearch={() => {
                this.handleSearch();
              }}
              ref={this.saveButton}
              key="submit"
              nodeDS={this.nodeDS}
              path={path}
            />
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
              <Spin dataSet={this.nodeDS} />
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
                }}
                noEndEdge={false}
                onDoubleClick={this.nodeDetail}
                className={styles.flow}
                data={currentData}
              />
              <CustomNode />
              <CustomEndNode />
              <CustomStartNode />
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
