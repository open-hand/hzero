/**
 * 执行记录 - 执行记录流程
 * @Author: wangke <ke.wang05@hand-china.com>
 * @Date: 2019-10-25
 * @LastEditTime: 2019-10-25 09:48
 * @Copyright: Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Header } from 'components/Page';
import { Row, Col, Spin } from 'choerodon-ui/pro';
import GGEditor, { Flow } from 'gg-editor';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import axios from 'axios';

import { connect } from 'dva';
import { routerRedux } from 'dva/router';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { HZERO_HRES } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';

// import { detailExecuteStatus } from '@/services/ruleTestService';
import EditorMinimap from '../../../components/EditorMinimap';
import { FlowContextMenu } from '../../../components/EditorContextMenu';
import { FlowToolbar } from '../../../components/EditorToolbar';
import { FlowItemPanel } from '../../../components/EditorItemPanel';
import { CustomNode, CustomEndNode, CustomStartNode } from '../../../components/customNode';
import styles from './index.less';
import SaveButton from '../../rules/flow/SaveButton';

const COLOR = {
  E: '#ff0000',
  S: '#00ff00',
  undefined: '#1890FF',
};

/**
 * 执行记录
 * @extends {Component} - Component
 * @reactProps {Object} [history={}]
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 *
 */
@formatterCollections({ code: ['hres.record'] })
@connect()
export default class FlowPage extends Component {
  propsButton = React.createRef();

  state = {
    loading: true,
    flowData: {},
  };

  async componentDidMount() {
    const { match } = this.props;
    const { code, id } = match.params;
    // const resStatus = await detailExecuteStatus({ ruleCode: code, historyUuid: id });
    axios({
      url: `${HZERO_HRES}/v1/${getCurrentOrganizationId()}/history-detail/execute-status`,
      method: 'GET',
      params: {
        ruleCode: code,
        historyUuid: id,
        tenantId: getCurrentOrganizationId(),
      },
    })
      .then((resStatus) => {
        if (isEmpty(resStatus)) {
          this.setState({
            loading: false,
          });
        } else if (!isEmpty(resStatus)) {
          const nodesMap = resStatus[0].nodeMap;
          const edgesMap = resStatus[0].edgeMap;
          const nodes = resStatus[0].process.nodes.map((node) => ({
            ...node,
            color: !isEmpty(nodesMap) && nodesMap[node.id] ? COLOR[nodesMap[node.id]] : node.color,
          }));
          const edges = resStatus[0].process.edges.map((edge) => ({
            ...edge,
            color: !isEmpty(edgesMap) && edgesMap[edge.id] ? COLOR[edgesMap[edge.id]] : edge.color,
          }));
          this.setState({
            loading: false,
            flowData: { nodes, edges },
          });
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  }

  /**
   * 双击节点跳转
   * @param e
   */
  @Bind()
  goToList(e) {
    const { dispatch } = this.props;
    if (
      e._type === 'dblclick' &&
      e.item &&
      e.item.type === 'node' &&
      !['START', 'END'].includes(e.item.model.componentType)
    ) {
      const { match } = this.props;
      const { code, id } = match.params;
      const pathname = `/hres/execute/detail/${id}/${code}`;
      dispatch(
        routerRedux.push({
          pathname,
        })
      );
    }
  }

  /**
   * flow 命令监听
   * @returns {Promise<void>}
   */
  @Bind()
  async onBeforecommandexecute() {
    const { propsAPI } = this.propsButton.current.props;
    propsAPI.executeCommand('undo');
  }

  render() {
    const { flowData, loading } = this.state;

    return (
      <React.Fragment>
        <Header
          title={intl.get('hres.record.view.title.execution.process.list').d('流程执行记录')}
          backPath="/hres/execute/list"
        />
        <GGEditor onAfterCommandExecute={this.onBeforecommandexecute} className={styles.editor}>
          <Row type="flex" className={styles.editorHd}>
            <Col span={24}>
              <FlowToolbar />
            </Col>
          </Row>
          <Row type="flex" className={styles.editorBd}>
            <Col span={2} className={styles.editorSidebar}>
              <FlowItemPanel />
            </Col>
            <Col span={18} className={styles.editorContent}>
              {loading ? (
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
                      mode: 'readOnly',
                    }}
                    noEndEdge={false}
                    onDoubleClick={this.goToList}
                    className={styles.flow}
                    data={flowData}
                  />
                  <CustomNode />
                  <CustomEndNode />
                  <CustomStartNode />
                </>
              )}
            </Col>
            <Col span={4} className={styles.editorSidebar}>
              <SaveButton ref={this.propsButton} style={{ display: 'none' }} />
              <EditorMinimap />
            </Col>
          </Row>
          <FlowContextMenu />
        </GGEditor>
      </React.Fragment>
    );
  }
}
