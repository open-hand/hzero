import React, { Component } from 'react';
import { connect } from 'dva';
import { Button, Spin, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isEmpty from 'lodash/isEmpty';

import { Content, Header } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import DisplayForm from './DisplayForm';
import ProcessNodeMenu from './ProcessNodeMenu';
import List from './List';
import NodeLineModal from './NodeLineModal';
import ApprovalDetail from '../ApprovalDetail';
import styles from '../style/index.less';

@formatterCollections({ code: ['hwfp.processDefine', 'hwfp.common'] })
@connect(({ loading }) => ({
  fetchProcessDetailLoading: loading.effects['processDefine/fetchProcessModelDetail'],
  fetchProcessNodeLoading: loading.effects['processDefine/fetchProcessModelNodes'],
  fetchNodesLineLoading: loading.effects['processDefine/fetchNodesLine'],
  deleteNodesLineLoading: loading.effects['processDefine/deleteNodesLine'],
  saveNodesLineLoading: loading.effects['processDefine/saveNodesLine'],
}))
export default class ProcessDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      processInfo: {}, // 审批链详情信息
      processNodes: [], // 审批链节点
      defaultSelectedNode: [], // 默认选中的审批链节点
      processNodeLines: [], // 单条审批链的行数据
      approveChainLine: {}, // 编辑的审批链行数据
      approveChainLineId: null, // 编辑的审批链行数据标识
      selectedNodelinesKeys: [], // 勾选的审批节点行数据的keys
      selectedNodelines: [], // 勾选的审批节点行数据
      NodeLineModalVisible: false, // 审批节点行数据modal显示标识
      NodeLineModalData: {}, // 审批节点行数据modal编辑数据
      showApprovaDetail: false, // 显示隐藏审批链详情标识
      approvalLines: [], // 审批行数据
    };
  }

  componentDidMount() {
    this.fetchProcessInfo();
    this.fetchProcessNodes();
  }

  @Bind()
  fetchProcessInfo() {
    const {
      dispatch,
      match: {
        params: { processId },
      },
    } = this.props;
    dispatch({
      type: 'processDefine/fetchProcessModelDetail',
      params: processId,
    }).then((res) => {
      if (res) {
        this.setState({
          processInfo: res || {},
        });
      }
    });
  }

  @Bind()
  fetchProcessNodes() {
    const {
      dispatch,
      match: {
        params: { processId },
      },
    } = this.props;
    dispatch({
      type: 'processDefine/fetchProcessModelNodes',
      params: {
        modelId: processId,
      },
    }).then((res) => {
      if (res) {
        const processNodes = res || [];
        let defaultSelectedNode = '';
        if (processNodes.length > 0) {
          defaultSelectedNode = (processNodes[0] || {}).nodeId;
          this.fetchNodesLine(defaultSelectedNode);
        }
        this.setState({
          processNodes,
          defaultSelectedNode,
        });
      }
    });
  }

  @Bind()
  selectNodes(nodeId) {
    const { defaultSelectedNode } = this.state;
    if (nodeId !== defaultSelectedNode) {
      this.setState({
        defaultSelectedNode: nodeId,
        processNodeLines: [],
        selectedNodelines: [],
        selectedNodelinesKeys: [],
      });
      this.fetchNodesLine(nodeId);
      this.closeNodeLineDetail();
    }
  }

  @Bind()
  fetchNodesLine(nodeId) {
    this.props
      .dispatch({
        type: 'processDefine/fetchNodesLine',
        params: {
          userTaskId: nodeId,
        },
      })
      .then((res) => {
        if (res) {
          const { dataSource = [] } = res || {};
          this.setState({
            processNodeLines: dataSource, // 单条审批链的行数据
          });
        }
      });
  }

  @Bind()
  selectNodeLines(nodeLinesKeys = [], nodeLines = []) {
    this.setState({ selectedNodelines: nodeLines, selectedNodelinesKeys: nodeLinesKeys });
  }

  @Bind()
  deleteNodeLines() {
    const { selectedNodelines, defaultSelectedNode, processNodes = [] } = this.state;
    const targetNode = processNodes.find((item) => item.nodeId === defaultSelectedNode) || {};
    this.props
      .dispatch({
        type: 'processDefine/deleteNodesLine',
        params: {
          userTaskId: defaultSelectedNode,
          approveChainId: targetNode.approveChainId,
          approveChainLineList: selectedNodelines,
        },
      })
      .then((res) => {
        if (isEmpty(res)) {
          notification.success();
          this.setState({ selectedNodelines: [] });
          this.fetchNodesLine(defaultSelectedNode);
        }
      });
  }

  @Bind()
  toggleNodeLineModal() {
    const { NodeLineModalVisible } = this.state;
    this.setState({ NodeLineModalVisible: !NodeLineModalVisible });
  }

  // 新增审批链节点行
  @Bind()
  createNodeLine(params) {
    const { processInfo = {}, defaultSelectedNode, processNodes = [] } = this.state;
    const targetNode = processNodes.find((item) => item.nodeId === defaultSelectedNode) || {};
    const { processKey } = processInfo;
    this.props
      .dispatch({
        type: 'processDefine/saveNodesLine',
        params: {
          approveChainLineList: [params],
          processKey,
          userTaskId: defaultSelectedNode,
          approveChainId: targetNode.approveChainId,
        },
      })
      .then((res) => {
        if (!isEmpty(res) && !res.failed) {
          notification.success();
          this.toggleNodeLineModal();
          this.fetchNodesLine(defaultSelectedNode);
          if (!targetNode.approveChainId) {
            targetNode.approveChainId = res.approveChainId;
            // eslint-disable-next-line array-callback-return
            processNodes.find((item) => {
              if (item.nodeId === defaultSelectedNode) {
                // eslint-disable-next-line no-param-reassign
                item = targetNode;
              }
            });
            this.setState({ processNodes });
          }
        }
      });
  }

  @Bind()
  editNodeLine(nodeLine) {
    this.setState({
      NodeLineModalData: nodeLine,
    });
    this.toggleNodeLineModal();
  }

  @Bind()
  openNodeLineModal() {
    this.setState({
      NodeLineModalData: {},
    });
    this.toggleNodeLineModal();
  }

  @Bind()
  showNodeLineDetail(approveChainLine = {}) {
    this.setState({
      approveChainLine,
      showApprovaDetail: true,
      approveChainLineId: approveChainLine.approveChainLineId,
    });
    this.fetchApprovalLineDetail({ approveChainLineId: approveChainLine.approveChainLineId });
  }

  @Bind()
  fetchApprovalLineDetail(params = {}) {
    this.props
      .dispatch({
        type: 'processDefine/queryApproveChainLineDetail',
        params,
      })
      .then((res) => {
        if (res) {
          const { dataSource } = res;
          this.setState({
            approvalLines: dataSource, // 审批链行数据
          });
        }
      });
  }

  @Bind()
  closeNodeLineDetail() {
    const { defaultSelectedNode } = this.state;
    this.setState({
      showApprovaDetail: false,
      approvalLines: [],
      approveChainLineId: null,
    });
    this.fetchNodesLine(defaultSelectedNode);
  }

  render() {
    const {
      processInfo,
      processNodes = [],
      defaultSelectedNode = '',
      processNodeLines = [],
      selectedNodelines = [],
      selectedNodelinesKeys = [],
      NodeLineModalVisible,
      NodeLineModalData = {},
      showApprovaDetail = false,
      approvalLines = [],
      approveChainLine = {},
      approveChainLineId = null,
    } = this.state;
    const {
      fetchProcessDetailLoading,
      fetchProcessNodeLoading,
      fetchNodesLineLoading,
      deleteNodesLineLoading,
      saveNodesLineLoading,
    } = this.props;

    return (
      <>
        <Header
          title={intl.get('hwfp.processDefine.view.message.title.maintainProcess').d('维护审批链')}
          backPath="/hwfp/process-define/list"
        />
        <Content className={styles.content}>
          <div className={styles['display-container']}>
            {/* <div className={styles['display-container-title']}>
              {intl.get('hwfp.processDefine.view.message.title.process').d('审批流程')}
            </div> */}
            <Spin spinning={fetchProcessDetailLoading}>
              <DisplayForm processInfo={processInfo} />
            </Spin>
          </div>
          <div className={styles['wrap-container']}>
            <div className={styles['left-container']}>
              <div className={styles['node-title']}>
                {intl.get('hwfp.processDefine.view.message.title.processNode').d('审批链节点')}
              </div>
              <ProcessNodeMenu
                processNodes={processNodes}
                defaultSelectedNode={defaultSelectedNode}
                handleSelectNodes={this.selectNodes}
                fetchProcessNodeLoading={fetchProcessNodeLoading}
              />
            </div>
            {!showApprovaDetail ? (
              <div className={styles['right-container-index']}>
                <div>
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
                    placement="top"
                    onConfirm={this.deleteNodeLines}
                  >
                    <Button icon="delete" disabled={!selectedNodelines.length > 0}>
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </Button>
                  </Popconfirm>
                  <Button
                    icon="plus"
                    type="primary"
                    style={{ marginLeft: 8 }}
                    disabled={!processNodes.length > 0}
                    onClick={this.openNodeLineModal}
                  >
                    {intl.get('hzero.common.button.create').d('新建')}
                  </Button>
                </div>
                <List
                  selectedRowKeys={selectedNodelinesKeys}
                  dataSource={processNodeLines}
                  loading={fetchNodesLineLoading || deleteNodesLineLoading || saveNodesLineLoading}
                  handleSelectRows={this.selectNodeLines}
                  handleEdit={this.editNodeLine}
                  handleEditDetail={this.showNodeLineDetail}
                />
              </div>
            ) : (
              <div className={styles['right-container-detail']}>
                <ApprovalDetail
                  processInfo={approveChainLine}
                  approvalLines={approvalLines}
                  approveChainLineId={approveChainLineId}
                  backToIndex={this.closeNodeLineDetail}
                  fetchApprovalLineDetail={this.fetchApprovalLineDetail}
                />
              </div>
            )}
          </div>
        </Content>
        <NodeLineModal
          visible={NodeLineModalVisible}
          editData={NodeLineModalData}
          processNodeLines={processNodeLines}
          saveLoading={saveNodesLineLoading}
          handleClose={this.toggleNodeLineModal}
          handleCreate={this.createNodeLine}
        />
      </>
    );
  }
}
