/**
 * 服务编排
 * @author baitao.huang@hand-china.com
 * @date 2020/4/9
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */
import React from 'react';
import { connect } from 'dva';
import GGEditor, { Flow } from 'gg-editor';
import { uniqBy, isUndefined, isArray, isEmpty, words } from 'lodash';
import { Bind } from 'lodash-decorators';
import { Col, Row } from 'choerodon-ui';
import { DataSet, Modal, ModalProvider } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { Header, Content } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { getResponse, getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import { routerRedux } from 'dva/router';
import { queryMapIdpValue } from 'services/api';
import webSocketManager from 'utils/webSoket';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import INSTANCE_LANG from '@/langs/instanceLang';
import TASK_INSTANCE_LANG from '@/langs/taskInstanceLang';
import { flowGraphDS } from '@/stores/Orchestration/orchestrationDS';
import {
  ORCH_DEF_STATUS_ONLINE,
  ORCH_DEF_STATUS_OFFLINE,
  WBS_KEY_ORCH,
} from '@/constants/constants';
import { ORCH_TASK_TYPE } from '@/constants/CodeConstants';
import { joinField, ORCH_TYPE_ENUM } from '@/utils/utils';
import { tooltip } from './plugins';
import {
  FlowAffix,
  FlowContextMenu,
  FlowItemPanel,
  FlowToolbar,
  FlowTopButtonBar,
  FlowEdge,
} from './FlowGraph';
import { HeaderModal, ParamModal } from './Modals';
import { renderChildren, nodeDrawerProps } from './utils';
import styles from './index.less';

const Context = React.createContext('');
const { useModal } = ModalProvider;
const ModalBinder = (props) => {
  const modal = useModal();
  props.onRef(modal);
  return <div />;
};

@connect(({ orchestration }) => ({
  orchestration,
}))
@formatterCollections({
  code: [ORCHESTRATION_LANG.PREFIX],
})
export default class Detail extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      detailLoading: false,
      saveLoading: false,
      currentCommand: {}, // 当前画布command
      disabledFlag: false,
      graphName: '',
      orchTaskType: [], // 左侧菜单数据
      graphStatus: {
        statusCode: ORCH_DEF_STATUS_OFFLINE,
        statusMeaning: ORCHESTRATION_LANG.STATUS_OFFLINE,
      },
      // 编排类型：definition、instance、task
      orchType: this.getOrchType(),
      organizationId: getCurrentOrganizationId(),
    };
    this.flowGraphDS = new DataSet({
      ...flowGraphDS({ ref: this }),
    });
  }

  componentDidMount() {
    const {
      dispatch,
      match: { params },
    } = this.props;
    const { orchType } = this.state;
    const { id } = params;
    if (!isUndefined(id)) {
      this.handleSearchDetail(id);
    }
    dispatch({ type: 'orchestration/init' });
    this.fetchLeftMenu();
    if (orchType !== ORCH_TYPE_ENUM.definition) {
      webSocketManager.initWebSocket();
      webSocketManager.addListener(WBS_KEY_ORCH, this.updateGraphStatus);
    }
  }

  /**
   * 根据websocket消息更新图状态
   */
  @Bind()
  updateGraphStatus({ message }) {
    const { graphName, organizationId } = this.state;
    const messageJson = isEmpty(message) ? undefined : JSON.parse(message);
    const { body = {} } = messageJson;
    const { instanceName, tenantId } = body;
    // 根据instanceName、tenantId来判断websocket信息唯一
    if (!isEmpty(messageJson) && instanceName === graphName && tenantId === organizationId) {
      const { statusCode, statusMeaning } = body;
      this.setState({ webSocketMessage: messageJson }, () => {
        this.setState({ graphStatus: { statusCode, statusMeaning } });
        this.propsAPI.executeCommand('webSocketExec');
      });
    }
  }

  componentWillUnmount() {
    const { orchType } = this.state;
    if (orchType !== ORCH_TYPE_ENUM.definition) {
      webSocketManager.destroyWebSocket();
    }
  }

  /**
   * 根据路径截取orchType
   */
  @Bind()
  getOrchType() {
    const {
      match: { path },
    } = this.props;
    const type = words(path, /[^/]+/g)[1];
    if (type === 'orchestration-definition') {
      return ORCH_TYPE_ENUM.definition;
    }
    if (type === 'orchestration-instance') {
      return ORCH_TYPE_ENUM.instance;
    }
    if (type === 'orchestration-task-instance') {
      return ORCH_TYPE_ENUM.task;
    }
  }

  /**
   * 改变 loading
   * @param {String} loadingKey - loading key
   * @param {Boolean} boolean - true | false
   */
  @Bind()
  changeLoading(loadingKey, boolean) {
    this.setState({ [loadingKey]: boolean });
  }

  /**
   * 查询图
   * @param {Number} id definitionId | instanceId | taskId
   * @param {Boolean} autoZoomFlag
   * @returns {Promise<void>}
   */
  @Bind()
  async handleSearchDetail(id, autoZoomFlag = true) {
    // 查询
    this.changeLoading('detailLoading', true);
    this.flowGraphDS.setQueryParameter(joinField(this, 'id'), id);
    const workflow = await this.flowGraphDS.query();
    if (getResponse(workflow)) {
      const {
        statusCode,
        statusMeaning,
        orchTaskInstanceList,
        failureStrategy,
        graphLayout = '[]',
      } = workflow;
      const name = workflow[joinField(this, 'name')];
      const json = workflow[joinField(this, 'json')];
      const { tasks = [] } = JSON.parse(json);
      const edges = JSON.parse(graphLayout);
      // 渲染图形
      const workflowGraphData = {
        edges: isArray(edges)
          ? this.setStatusForEdges(edges, tasks, orchTaskInstanceList, failureStrategy)
          : [],
        nodes: isArray(tasks) ? this.setStatusForNodes(tasks, orchTaskInstanceList) : [],
      };
      this.propsAPI.read(workflowGraphData);
      if (autoZoomFlag) {
        // 画布自动排版
        this.propsAPI.executeCommand('autoZoom');
        this.propsAPI.executeCommand('resetZoom');
      }
      this.setState({
        graphName: name,
        disabledFlag: this.setDisabledFlag(statusCode),
        graphStatus: { statusCode, statusMeaning },
      });
    }
    this.changeLoading('detailLoading', false);
  }

  /**
   * 结合全局和局部的失败策略来决定是否渲染边的颜色
   * @param {string} globalStrategy 全局的失败策略
   * @param {string} localStrategy 局部的失败策略
   */
  setEdgeColorFlag(globalStrategy, localStrategy, threadMechanism) {
    if (threadMechanism === 'ASYNC') {
      return true;
    }
    if (localStrategy === 'CONTINUE') {
      return true;
    }
    if (localStrategy !== 'FINISH' && globalStrategy === 'CONTINUE') {
      return true;
    }
    return false;
  }

  /**
   * 根据orchTaskInstanceList中statusCode,设置nodes里面的statusCod
   */
  setStatusForNodes(nodes, orchTaskInstanceList) {
    if (isUndefined(orchTaskInstanceList)) {
      return nodes;
    }
    const temps = nodes.map((node) => {
      const updatedMessage = orchTaskInstanceList.find((item) => item.taskName === node.name) || {};
      const { statusCode } = updatedMessage;
      return { ...node, statusCode, updatedMessage };
    });
    return temps;
  }

  /**
   * 根据orchTaskInstanceList中statusCode,设置edges里面的statusCod
   */
  setStatusForEdges(edges, nodes, orchTaskInstanceList, globalFailureStrategy) {
    if (isUndefined(orchTaskInstanceList)) {
      return edges;
    }
    const temps = edges.map((edge) => {
      const { name, threadMechanism, timeOut = {} } =
        nodes.find((node) => node.id === edge.source) || {};
      const { alertFlag, strategy } = timeOut;
      const failureStrategy = alertFlag ? strategy : undefined;
      const updatedMessage = orchTaskInstanceList.find((item) => item.taskName === name) || {};
      const { statusCode } = updatedMessage;
      return {
        ...edge,
        statusCode,
        renderColorFlag: this.setEdgeColorFlag(
          globalFailureStrategy,
          failureStrategy,
          threadMechanism
        ),
      };
    });
    return temps;
  }

  /**
   * 设置明细页编辑状态
   */
  setDisabledFlag(statusCode) {
    const { orchType } = this.state;
    // 目前只有编排定义才允许编辑
    if (orchType === ORCH_TYPE_ENUM.definition) {
      return ORCH_DEF_STATUS_ONLINE === statusCode;
    }
    // TODO: 编排实例、任务实例目前不可以编辑
    return true;
  }

  /**
   * 获取左侧菜单数据
   */
  @Bind()
  fetchLeftMenu() {
    queryMapIdpValue({
      orchTaskType: ORCH_TASK_TYPE,
    }).then((res) => {
      if (res) {
        const { orchTaskType = [] } = res;
        this.setState({ orchTaskType });
      }
    });
  }

  /**
   * 打开头弹窗
   */
  @Bind()
  handleOpenHeaderModal(workflowGraph) {
    const {
      match: { path },
    } = this.props;
    const { saveLoading, orchType } = this.state;
    const currentRecord = this.flowGraphDS.current;
    const { nodes = [], edges = [] } = workflowGraph;
    // 任务校验(检查是否有同名label，节点是否为空)
    const nodeLength = nodes.length;
    const uiqLabelLength = uniqBy(nodes, 'label').length;
    if (nodeLength === 0) {
      Modal.warning(ORCHESTRATION_LANG.GRAPH_NOT_EMPTY);
      return;
    }
    if (uiqLabelLength < nodeLength) {
      Modal.warning(ORCHESTRATION_LANG.GRAPH_NODE_REPEAT);
      return;
    }
    const tasks = nodes.map((task) => ({
      ...task,
      preTasks: this.getPreTask(task.id, nodes, edges),
    }));
    const json = {
      tasks,
      globalParams: [],
    };
    currentRecord.set(joinField(this, 'json'), JSON.stringify(json));
    currentRecord.set('graphLayout', JSON.stringify(edges));

    const headerModalProps = {
      orchType,
      graph: currentRecord,
      onRef: (ref = {}) => {
        this.headerFormDS = ref.headerFormDS;
      },
    };
    this.headerModal = Modal.open({
      title: ORCHESTRATION_LANG.FIELD_MAPPING,
      closable: true,
      destroyOnClose: true,
      style: { width: 600 },
      children: <HeaderModal {...headerModalProps} />,
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            loading={saveLoading}
            onClick={this.handleSaveGraph}
          >
            {ORCHESTRATION_LANG.SAVE}
          </ButtonPermission>
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 获取前置节点
   */
  getPreTask(taskId, nodes, edges) {
    const { nodes: originNodes = [], edges: originEdges = [] } = this.propsAPI.save();
    const preTaskId = (edges || originEdges)
      .filter((edge) => edge.target === taskId)
      .map((edge) => edge.source);
    const preTasks = (nodes || originNodes)
      .filter((node) => preTaskId.includes(node.id))
      .map((node) => node.name);
    return preTasks;
  }

  /**
   * 保存任务流画布
   * @param workflowGraph
   */
  @Bind()
  async handleSaveGraph() {
    const { match } = this.props;
    const { id } = match.params;
    const isCreate = isUndefined(id);
    const headerValidate = this.headerFormDS.validate();
    if (!headerValidate) {
      return;
    }
    const headerData = this.headerFormDS.current.toData();
    this.insertHeaderData(headerData, isCreate);
    this.changeLoading('saveLoading', true);
    try {
      const response = await this.flowGraphDS.submit();
      if (getResponse(response)) {
        this.headerModal.close();
        if (isCreate) {
          this.handleGotoDetail(response.content[0][joinField(this, 'id')]);
        } else {
          // 重新查询
          await this.handleSearchDetail(id, false);
        }
      }
    } finally {
      this.changeLoading('saveLoading', false);
    }
  }

  /**
   * 跳转到明细页面
   * @param {*} id
   */
  @Bind()
  handleGotoDetail(id) {
    const { dispatch = () => {} } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/horc/${this.transFormMiddlePath()}/detail/${id}`,
        state: { orchType: ORCH_TYPE_ENUM.definition },
      })
    );
  }

  /**
   * 根据orchType转换中间路径
   */
  transFormMiddlePath() {
    const { orchType } = this.state;
    const middlePath =
      orchType !== ORCH_TYPE_ENUM.task
        ? `orchestration-${orchType}`
        : 'orchestration-task-instance';
    return middlePath;
  }

  /**
   * flowGraphDS.current插入头数据
   */
  insertHeaderData(data, isCreate) {
    const { remark, timeoutFlag, timeout, alertCode } = data;
    const name = data[joinField(this, 'name')];
    const record = this.flowGraphDS.current;
    record.set('_status', isCreate ? 'create' : 'update');
    record.set(joinField(this, 'name'), name);
    record.set('remark', remark);
    if (timeoutFlag) {
      record.set('timeout', timeout);
      record.set('alertCode', alertCode);
    }
  }

  /**
   * GGEditor - 画布拖拽校验
   * @param {Object} event - 操作事件数据
   * @param {String} event.command.name - 操作事件名称
   */
  @Bind()
  handleAfterCommandExecute(event) {
    const { command } = event;
    const { name, type, addModel = {}, pasteData = [] } = command;
    const { shape } = addModel;
    this.setState({ currentCommand: command });
    if (type === 'node' && name === 'add') {
      this.openDrawerByShape(shape, addModel);
    }
    if (type === 'edge' || name === 'update') {
      if (!this.validateGraph()) {
        command.back();
      }
    }
    if (name === 'paste' || name === 'pasteHere') {
      this.setLabelAfterPaste(pasteData);
    }
  }

  /**
   * GGEditor - 命令执行之前
   * @param {Object} event - 操作事件数据
   * @param {String} event.command.name - 操作事件名称
   */
  @Bind()
  handleBeforeCommandExecute(event) {
    const { command } = event;
    const { name } = command;
    // 启动参数，全局、局部参数command的逻辑
    if (name === 'startParamInfo' || name === 'varParamInfo') {
      this.openParamModal(name);
    }
  }

  /**
   * 粘贴后重新设置名称
   */
  setLabelAfterPaste(pasteData) {
    const { executeCommand, find, update } = this.propsAPI;
    const model = pasteData[0].model || {};
    const nodeItem = find(model.id);
    const name = `${model.name}-${model.id}`;
    const updateData = {
      ...model,
      name,
      label: name,
      size: `${name.length * 8 + 100}*48`,
      params: {
        ...model.params,
        name,
      },
    };
    executeCommand(() => {
      update(nodeItem, updateData);
    });
  }

  /**
   * 校验图是否合法
   * TODO: 目前只有串行的流程图，以下判断逻辑都是基于此
   */
  validateGraph() {
    const {
      // nodes = [],
      edges = [],
    } = this.propsAPI.save();
    // 边的个数小于节点的个数
    // if (edges.length >= nodes.length) {
    //   return false;
    // }
    for (const key in edges) {
      // eslint-disable-next-line no-prototype-builtins
      if (edges.hasOwnProperty(key)) {
        const edge = edges[key];
        const {
          source,
          target,
          // sourceAnchor,
        } = edge;
        // 禁止自身连接
        if (source === target) {
          return false;
        }
        // 禁止多个相同起点
        // if (edges.filter((item) => item.source === source).length > 1) {
        //   return false;
        // }
        // 禁止多个相同终点
        // if (edges.filter((item) => item.target === target).length > 1) {
        //   return false;
        // }
        // 禁止同一个锚点多个出入线
        // const linkEdge = edges.filter((item) => item.target === source)[0] || {};
        // if (linkEdge.targetAnchor === sourceAnchor) {
        //   return false;
        // }
      }
    }
    return true;
  }

  /**
   * Flow-节点Node双击打开编辑
   * @param {Object} data - 画布双击节点数据
   * @returns {Promise<void>}
   */
  @Bind()
  async handleNodeDoubleClick(data) {
    const { item } = data;
    const {
      model: { shape },
    } = item;
    this.openDrawerByShape(shape);
  }

  /**
   * 根据节点的类型打开不同的弹窗
   * @param {string} shape 节点类型
   */
  @Bind()
  openDrawerByShape(shape, addNode = {}) {
    const extraProps = {
      addNode,
      shape,
    };
    this.nodeDrawer = this.drawerProvider.open(this.getDrawerProps(extraProps));
  }

  /**
   * drawer弹窗的属性
   * @param {Object} extraProps 额外附带的一些信息
   */
  getDrawerProps(extraProps) {
    const {
      orchestration,
      match: { path },
    } = this.props;
    const { operatorList, assertionSubjects } = orchestration;
    const { disabledFlag } = this.state;
    const { addNode, shape } = extraProps;
    const drawerContentProps = {
      path,
      disabledFlag,
      operatorList,
      assertionSubjects,
      onRef: (ref = {}) => {
        this.nodeDrawerRef = ref;
      },
    };
    const props = {
      ...nodeDrawerProps[shape],
      drawer: true,
      closable: true,
      destroyOnClose: true,
      children: renderChildren(shape, this.getCommonCardProps(addNode), drawerContentProps),
      afterClose: this.handleAfterDrawerClose,
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          {!disabledFlag && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.node.sure`,
                  type: 'button',
                  meaning: '确定',
                },
              ]}
              type="c7n-pro"
              color="primary"
              onClick={this.handleNodeDrawerOk}
            >
              {ORCHESTRATION_LANG.ENSURE_ADD}
            </ButtonPermission>
          )}
          {cancelBtn}
        </div>
      ),
    };
    return props;
  }

  /**
   * 获取CommonCard的通用属性
   */
  getCommonCardProps(addNode = {}) {
    const { disabledFlag } = this.state;
    const commonCardProps = {
      disabledFlag,
      addNode,
      onGetPreTask: this.getPreTask,
      onRef: (ref = {}) => {
        this.commonNodeFormDS = ref.commonNodeFormDS;
      },
    };
    return commonCardProps;
  }

  /**
   * 节点信息滑窗信息保存
   */
  @Bind()
  async handleNodeDrawerOk() {
    const commonData = await this.formatCommonCardData();
    const nodeData = await this.nodeDrawerRef.handleOk();
    if (!commonData || !nodeData) {
      return notification.error({
        message: ORCHESTRATION_LANG.SAVE_VALIDATE,
      });
    }
    const { getSelected, executeCommand, update } = this.propsAPI;
    setTimeout(() => {
      const item = getSelected()[0];
      if (!item) {
        return;
      }
      const itemModel = item.model;
      const updateData = {
        ...itemModel,
        ...commonData,
        ...nodeData,
      };
      executeCommand(() => {
        update(item, updateData);
      });
    }, 0);
    this.nodeDrawer.close();
  }

  /**
   * 格式化CommonCard里的通用数据
   */
  async formatCommonCardData() {
    const validate = await this.commonNodeFormDS.validate();
    if (!validate) {
      return undefined;
    }
    const data = this.commonNodeFormDS.current.toData();
    const { name, interval, alertFlag, alertCode, strategy, alertCodeLov, ...other } = data;
    return {
      ...other,
      name,
      label: name,
      size: `${name.length * 8 + 100}*48`,
      timeout: {
        interval,
        alertFlag,
        alertCode,
        strategy: strategy.toString(),
      },
    };
  }

  @Bind()
  handleAfterDrawerClose() {
    const { currentCommand } = this.state;
    const { name } = currentCommand;
    // 新打开的滑窗，如果没有填写任何内容就关闭滑窗，那么就画布上该节点删掉
    if (name === 'add') {
      currentCommand.back();
    }
  }

  /**
   * 打开参数弹窗，启动参数/ 全局参数、局部参数
   */
  @Bind()
  openParamModal(commandName) {
    const {
      graphLayout,
      instanceJson,
      orchTaskInstanceList,
      ...other
    } = this.flowGraphDS.current.toData();
    const paramModalProps = {
      record: other,
      type: commandName, // 取值varParamInfo、startParamInfo
    };
    Modal.open({
      title:
        commandName === 'startParamInfo'
          ? ORCHESTRATION_LANG.START_PARAM
          : ORCHESTRATION_LANG.VIEW_VAR,
      closable: true,
      destroyOnClose: true,
      style: { width: 750 },
      children: <ParamModal {...paramModalProps} />,
      // afterClose: this.handleAfterDrawerClose,
      footer: null,
    });
  }

  /**
   * 头标题渲染
   */
  @Bind()
  transformHeaderTitle() {
    const { orchType, graphName } = this.state;
    let prefix = '';
    switch (orchType) {
      case 'definition':
        prefix = ORCHESTRATION_LANG.HEADER;
        break;
      case 'task':
        prefix = TASK_INSTANCE_LANG.HEADER;
        break;
      case 'instance':
        prefix = INSTANCE_LANG.HEADER;
        break;
      default:
    }
    const headerTitle = isEmpty(graphName) ? prefix : `${prefix} - ${graphName}`;
    return headerTitle;
  }

  @Bind()
  handleBindRef(ref = {}) {
    this.drawerProvider = ref;
  }

  render() {
    const {
      disabledFlag,
      graphStatus,
      orchType,
      orchTaskType,
      webSocketMessage,
      detailLoading = false,
      saveLoading = false,
    } = this.state;
    const flowToolbarProps = {
      disabledFlag,
      graphStatus,
      orchType,
      webSocketMessage,
      loading: detailLoading || saveLoading,
      handleOpenHeaderModal: this.handleOpenHeaderModal,
      onOpenDrawerByShape: this.openDrawerByShape,
    };
    const topButtonBarProps = {
      onRef: (ref) => {
        this.propsAPI = (ref.props || {}).propsAPI;
      },
    };
    const flowItemPanelProps = {
      disabledFlag,
      orchTaskType,
    };
    return (
      <>
        <Header
          title={this.transformHeaderTitle()}
          backPath={`/horc/${this.transFormMiddlePath()}`}
        />
        <Content className={styles['horc-flow-content']}>
          <GGEditor
            className={styles['horc-flow-editor']}
            onAfterCommandExecute={this.handleAfterCommandExecute}
            onBeforeCommandExecute={this.handleBeforeCommandExecute}
          >
            <div className={styles['horc-editor-bd']}>
              <div className={styles['horc-editor-sidebar']}>
                <FlowTopButtonBar {...topButtonBarProps} />
                <FlowItemPanel {...flowItemPanelProps} />
              </div>
              <div span={22} className={styles['horc-editor-right']}>
                <Row className={styles['horc-editor-hd']}>
                  <Col span={24}>
                    <FlowToolbar {...flowToolbarProps} />
                  </Col>
                </Row>
                <Row className={styles['horc-flow-editor-bd']}>
                  <Col span={24} className={styles['horc-flow-col']}>
                    <Flow
                      graph={{
                        plugins: [tooltip],
                        edgeDefaultShape: 'flow-edge',
                      }}
                      shortcut={{ delete: !disabledFlag }}
                      className={styles['horc-flow']}
                      noEndEdge={false}
                      onNodeDoubleClick={this.handleNodeDoubleClick}
                    />
                  </Col>
                </Row>
              </div>
              <FlowAffix />
            </div>
            <Context.Provider value="provider">
              <ModalProvider>
                <ModalBinder onRef={this.handleBindRef} />
              </ModalProvider>
            </Context.Provider>
            {!disabledFlag && <FlowContextMenu />}
            <FlowEdge />
          </GGEditor>
        </Content>
      </>
    );
  }
}
