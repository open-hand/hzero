/**
 * Detail - 我参与的流程/我发起的流程/我抄送的流程 明细
 * @date: 2018-4-18
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Modal, Button, Tabs, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import classNames from 'classnames';

import { Header, Content } from 'components/Page';

import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { closeTab } from 'utils/menuTab';

import ApproveHistory from '../../components/ApproveHistory';
import ApproveForm from '../../components/ApproveForm';
import FlowChart from '../../components/FlowChart';
import ApproveItem from '../../components/ApproveItem';
import ApproveHistoryRecord from '../../components/ApproveHistoryRecord';

import styles from './index.less';

@Form.create({ fieldNameProp: null })
// TODO: 调整多语言
@formatterCollections({
  code: ['hwfp.carbonCopyTask', 'hwfp.common', 'entity.position', 'entity.department'],
})
@connect(({ carbonCopyTask, loading }) => ({
  carbonCopyTask,
  fetchDetailLoading: loading.effects['carbonCopyTask/fetchDetail'],
  forecastLoading: loading.effects['carbonCopyTask/fetchForecast'],
  recallLoading: loading.effects['carbonCopyTask/taskRecall'],
  fetchHistoryApprovalLoading: loading.effects['carbonCopyTask/fetchHistoryApproval'],
  tenantId: getCurrentOrganizationId(),
}))
export default class CarbonCopyTaskDetail extends Component {
  state = {
    detailData: {},
    forecastData: {},
  };

  approveFormChildren;

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'carbonCopyTask/updateState',
      payload: { detail: {}, forecast: [], uselessParam: 'init' },
    });
    this.handleSearch();
  }

  @Bind()
  handleSearch() {
    const { dispatch, match, tenantId } = this.props;
    // 获取详情
    dispatch({
      type: 'carbonCopyTask/fetchDetail',
      payload: {
        tenantId,
        id: match.params.id,
        type: 'carbonCopy',
      },
    }).then((res) => {
      if (res) {
        this.setState({ detailData: res });
        this.fetchHistoryRecord(res);
      }
    });
    // 获取流程图
    dispatch({
      type: 'carbonCopyTask/fetchForecast',
      payload: {
        tenantId,
        id: match.params.id,
      },
    }).then((data) => {
      if (data) {
        this.setState({
          forecastData: data,
        });
      }
    });
  }

  // 查询审批历史
  @Bind()
  fetchHistoryRecord(currentDetailData) {
    const { businessKey } = currentDetailData;
    if (businessKey) {
      this.props
        .dispatch({
          type: 'carbonCopyTask/fetchHistoryApproval',
          params: {
            businessKey,
          },
        })
        .then((res) => {
          if (res) {
            this.setState({
              historyApprovalRecords: res || [],
            });
          }
        });
    }
  }

  @Bind()
  executeTaskAction() {
    const { tenantId, match, dispatch } = this.props;
    Modal.confirm({
      title: intl.get('hwfp.common.view.message.confirm').d('确认'),
      content: intl.get('hwfp.carbonCopyTask.view.message.title.confirmBack').d(`确认撤回吗?`),
      onOk: () => {
        const params = {
          type: 'carbonCopyTask/taskRecall',
          payload: {
            tenantId,
            processInstanceId: match.params.id,
          },
        };
        dispatch(params).then((res) => {
          if (res) {
            notification.success();
            this.props.history.push(`/hwfp/involved-task`);
            closeTab(`/hwfp/involved-task/detail/${match.params.id}`);
          }
        });
      },
    });
  }

  renderApproveBtn() {
    const { recallLoading } = this.props;
    const { detailData } = this.state;
    if (detailData.recall && detailData.recall === true) {
      return (
        <Button
          style={{ marginTop: 12 }}
          type="primary"
          loading={recallLoading}
          onClick={() => this.executeTaskAction('recall')}
        >
          {intl.get('hwfp.common.view.message.recall').d('撤回')}
        </Button>
      );
    }
  }

  render() {
    const {
      dispatch,
      fetchDetailLoading,
      tenantId,
      match,
      forecastLoading = false,
      fetchHistoryApprovalLoading = false,
      carbonCopyTask: { detail = {}, forecast = [], uselessParam },
    } = this.props;
    const { forecastData = {}, historyApprovalRecords = [], detailData = {} } = this.state;
    const { mergeHistory } = detailData;
    const { formKey = null } = detail;
    const historyProps = {
      mergeHistory,
      detail,
      historyApprovalRecords,
      loading: fetchDetailLoading || fetchHistoryApprovalLoading,
    };

    const flowProps = {
      dispatch,
      match,
      tenantId,
      forecast,
      detail,
      uselessParam,
      forecastData,
      loading: forecastLoading,
    };

    const formProps = {
      disabled: true,
      detail,
      ref: (ref) => {
        this.approveFormChildren = ref;
      },
      onAction: this.taskAction,
    };

    const historyRecordProps = {
      loading: fetchHistoryApprovalLoading,
      records: historyApprovalRecords,
    };

    return (
      <>
        <Header title={intl.get('hwfp.common.model.process.detail').d('流程明细')} />
        <Content>
          <Spin spinning={fetchDetailLoading}>
            <div className={classNames(styles['label-col'])}>
              {intl.get('hwfp.common.model.approval.item').d('审批事项')}
            </div>
            <ApproveItem detail={detail} />
            {formKey && (
              <>
                <div className={classNames(styles['label-col'])}>
                  {intl.get('hwfp.common.model.approval.form').d('审批表单')}
                </div>
                <ApproveForm {...formProps} />
              </>
            )}
            <Tabs defaultActiveKey="1" animated={false}>
              <Tabs.TabPane
                tab={intl.get('hwfp.common.model.approval.record').d('审批记录')}
                key="1"
              >
                <ApproveHistory {...historyProps} />
              </Tabs.TabPane>
              <Tabs.TabPane tab={intl.get('hwfp.common.model.process.graph').d('流程图')} key="2">
                <FlowChart {...flowProps} />
              </Tabs.TabPane>
              {!mergeHistory && historyApprovalRecords.length > 0 && (
                <Tabs.TabPane
                  tab={intl.get('hwfp.common.model.approval.history').d('审批历史')}
                  key="3"
                >
                  <ApproveHistoryRecord {...historyRecordProps} />
                </Tabs.TabPane>
              )}
            </Tabs>
            {this.renderApproveBtn()}
          </Spin>
        </Content>
      </>
    );
  }
}
