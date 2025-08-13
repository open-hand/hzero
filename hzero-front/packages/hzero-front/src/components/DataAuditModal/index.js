/**
 * DataAuditModal - 数据审计弹窗
 * @date: 2019/5/9
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import { getResponse, createPagination } from 'utils/utils';
import intl from 'utils/intl';
import { queryAuditVersion } from '../../services/api';
import VersionList from './VersionList';
import DetailList from './DetailList';

/**
 * 数据审计弹窗
 * @extends {Component} - Component
 * @reactProps {!string} entityCode - 审计编码，由后端在列表查询接口中返回
 * @reactProps {!number} entityId - 实体ID，对应列表查询接口中的主键id的值
 * @reactProps {array} columns 页面列配置
 * @return React.element
 */
export default class DataAuditModal extends Component {
  state = this.getInitState();

  /**
   * 初始化state
   */
  @Bind()
  getInitState() {
    return {
      visible: false,
      versionList: [],
      versionPage: {},
      detailList: [],
      versionLoading: false,
      detailLoading: false,
    };
  }

  /**
   * 查询审计版本数据
   */
  @Bind()
  fetchData(fields = {}) {
    this.setState({ versionLoading: true });
    const { entityCode, entityId } = this.props;
    const params = {
      page: isEmpty(fields) ? {} : fields,
      entityCode,
      entityId,
    };
    queryAuditVersion(params).then(res => {
      if (getResponse(res)) {
        this.setState({
          versionList: res.content || [],
          versionPage: createPagination(res),
          versionLoading: false,
        });
      } else {
        this.setState({
          versionLoading: false,
        });
      }
    });
  }

  /**
   * 查询审计版本详情数据
   * @param {number} auditDataId 审计版本数据中的主键
   */
  @Bind()
  fetchDetailData(auditDataId) {
    const { versionList } = this.state;
    this.setState({ detailLoading: true, detailList: [] });
    const targetVersion = versionList.find(item => item.auditDataId === auditDataId);
    const { auditDataLineList } = targetVersion;
    this.setState({
      detailList: auditDataLineList,
      detailLoading: false,
    });
  }

  /**
   * 显示模态框
   */
  @Bind()
  handleOpenModal() {
    this.setState({
      visible: true,
    });
    this.fetchData();
  }

  /**
   * 关闭模态框
   */
  @Bind()
  handleCloseModal() {
    this.setState(this.getInitState());
  }

  render() {
    const { columns = [] } = this.props;
    const {
      visible,
      versionList,
      versionPage,
      detailList,
      versionLoading,
      detailLoading,
    } = this.state;
    const versionListProps = {
      dataSource: versionList,
      pagination: versionPage,
      loading: versionLoading,
      onFetch: this.fetchDetailData,
      onChange: this.fetchData,
    };
    const detailListProps = {
      dataSource: detailList,
      comparedData: columns,
      loading: detailLoading,
    };
    return (
      <>
        <a onClick={() => this.handleOpenModal()}>
          {intl.get('hzero.common.components.dataAudit.viewHistory').d('查看数据历史')}
        </a>
        {visible && (
          <Modal
            width="50%"
            visible={visible}
            destroyOnClose
            title={intl.get('hzero.common.components.dataAudit.changeHistory').d('数据变更历史')}
            onCancel={this.handleCloseModal}
            footer={[
              <Button key="cancel" onClick={this.handleCloseModal}>
                {intl.get('hzero.common.button.close').d('关闭')}
              </Button>,
            ]}
          >
            <div style={{ maxHeight: '60vh', overflowY: 'scroll' }}>
              <VersionList {...versionListProps} />
              <DetailList {...detailListProps} />
            </div>
          </Modal>
        )}
      </>
    );
  }
}
