import React, { Component } from 'react';
import { connect } from 'dva';
import { Button, Popconfirm, Tooltip, Icon, Divider } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isEmpty from 'lodash/isEmpty';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import DisplayForm from './DisplayForm';
import List from './List';
import Modal from './Model';
import styles from '../style/index.less';

@formatterCollections({ code: ['hwfp.processDefine', 'hwfp.common'] })
@connect(({ loading }) => ({
  fetchApprovalLineDetailLoading: loading.effects['processDefine/queryApproveChainLineDetail'],
  deleteApprovalLineDetailLoading: loading.effects['processDefine/deleteApprovalLineDetail'],
  saveApproveChainLineDetailLoading: loading.effects['processDefine/saveApproveChainLineDetail'],
}))
export default class ApprovalDetail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      selectedApprovallines: [], // 已选择的审批链行数据
      modalVisible: false, // 弹窗显示标识
      modalData: {}, // 弹窗显示数据
    };
  }

  @Bind()
  deleteApprovalLines() {
    const { selectedApprovallines = [] } = this.state;
    const { approveChainLineId, fetchApprovalLineDetail = () => {} } = this.props;
    this.props
      .dispatch({
        type: 'processDefine/deleteApprovalLineDetail',
        params: selectedApprovallines,
      })
      .then((res) => {
        if (isEmpty(res)) {
          notification.success();
          this.setState({ selectedApprovallines: [] });
          fetchApprovalLineDetail({ approveChainLineId });
        }
      });
  }

  @Bind()
  closeModal() {
    this.setState({ modalVisible: false });
  }

  @Bind()
  openCreateModal() {
    this.setState({ modalVisible: true, modalData: {} });
  }

  @Bind
  openEditModal(record = {}) {
    this.setState({ modalVisible: true, modalData: record });
  }

  @Bind()
  selectApprovalLines(selectedRows = []) {
    this.setState({ selectedApprovallines: selectedRows });
  }

  @Bind()
  saveApproveChainLineDetail(params = {}) {
    const { approveChainLineId, fetchApprovalLineDetail = () => {} } = this.props;
    this.props
      .dispatch({
        type: 'processDefine/saveApproveChainLineDetail',
        params: [
          {
            ...params,
            approveChainLineId,
          },
        ],
      })
      .then((res) => {
        this.closeModal();
        if (!isEmpty(res) && !res.failed) {
          notification.success();
          fetchApprovalLineDetail({ approveChainLineId });
        }
      });
  }

  render() {
    const { selectedApprovallines = [], modalVisible, modalData } = this.state;
    const {
      fetchApprovalLineDetailLoading,
      deleteApprovalLineDetailLoading,
      saveApproveChainLineDetailLoading,
      processInfo = {},
      backToIndex = () => {},
      approvalLines = [],
    } = this.props;
    return (
      <>
        <div className={styles.header}>
          <Tooltip title={intl.get('hzero.common.status.back').d('返回')} placement="bottom">
            <Icon type="arrow-left" className={styles['back-icon']} onClick={backToIndex} />
          </Tooltip>
          <span className={styles['header-title']}>
            {intl.get('hwfp.processDefine.view.message.title.approvalRuleDetail').d('审批规则详情')}
          </span>
        </div>
        <div className={styles['right-content-detail-list']}>
          <DisplayForm processInfo={processInfo} />
          <Divider className={styles['right-content-detail-divider']} />
          <div className={styles['right-content-detail-table-operator']}>
            <Popconfirm
              title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
              placement="top"
              onConfirm={this.deleteApprovalLines}
            >
              <Button icon="delete" disabled={!selectedApprovallines.length > 0}>
                {intl.get('hzero.common.button.delete').d('删除')}
              </Button>
            </Popconfirm>
            <Button
              icon="plus"
              type="primary"
              style={{ marginLeft: 8 }}
              onClick={this.openCreateModal}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </Button>
          </div>
          <List
            dataSource={approvalLines}
            loading={fetchApprovalLineDetailLoading || deleteApprovalLineDetailLoading}
            handleSelectRows={this.selectApprovalLines}
            handleEdit={this.openEditModal}
          />
        </div>
        <Modal
          visible={modalVisible}
          editData={modalData}
          saveLoading={saveApproveChainLineDetailLoading}
          handleClose={this.closeModal}
          handleSave={this.saveApproveChainLineDetail}
        />
      </>
    );
  }
}
