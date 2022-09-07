/**
 * ListTable - 表结构变更-列表页
 * @date: 2019-7-18
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table, Icon, Modal, Button, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { TagRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

const { TextArea } = Input;
const prompt = 'hdtt.producerConfig.model.producerConfig';
const promptCode = 'hdtt.exception';
/**
 * 跳转条件数据列表
 * @extends {Component} - React.Component
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} isHandling - 是否在处理中
 * @reactProps {Function} onView - 查看配置
 * @reactProps {Function} onHandle - 处理
 * @return React.element
 */
export default class ListTable extends Component {
  state = {
    isShowChange: false, // 是否显示变更内容
    isShowMessage: false, // 是否显示错误消息或处理消息
    message: null,
    changeText: null,
    currentLogId: null,
  };

  /**
   * 编辑
   * @param {number} producerConfigId - 生产配置Id
   */
  @Bind()
  handleView(producerConfigId) {
    this.props.onView(producerConfigId);
  }

  /**
   * 显示变更内容模态框
   */
  @Bind()
  handleOpenChangeModal(text) {
    this.setState({
      isShowChange: true,
      changeText: text,
    });
  }

  /**
   * 关闭变更内容模态框
   */
  @Bind()
  handleCloseChangeModal() {
    this.setState({
      isShowChange: false,
    });
  }

  /**
   * 显示处理消息模态框
   * @param {string} text - 错误信息
   */
  @Bind()
  handleOpenMessageModal(text) {
    this.setState({
      message: text,
      isShowMessage: true,
    });
  }

  /**
   * 关闭处理消息模态框
   */
  @Bind()
  handleCloseMessageModal() {
    this.setState({
      isShowMessage: false,
    });
  }

  /**
   * 处理错误
   * @param {object} record - 表格行数据
   */
  @Bind()
  handleError(record) {
    this.setState(
      {
        currentLogId: record.dbMigrationLogId,
      },
      () => {
        this.props.onHandle(record);
      }
    );
  }

  /**
   * 将JSON字符串转换为格式化JSON
   * @param {string} - JSON字符串
   */
  @Bind()
  handleTransObj(str) {
    let result = null;
    try {
      result = JSON.parse(str);
    } catch (err) {
      return null;
    }
    return result;
  }

  /**
   * 渲染操作按钮
   * @param {object} record - 表格行数据
   */
  @Bind()
  renderActions(record) {
    const { isHandling } = this.props;
    const { currentLogId } = this.state;
    if (record.processStatus === 'P' || record.processStatus === 'E') {
      const actionBtns =
        currentLogId === record.dbMigrationLogId && isHandling ? (
          <a>
            <Icon type="loading" />
          </a>
        ) : (
          <a onClick={() => this.handleError(record)}>
            {intl.get(`${promptCode}.model.exception.handle`).d('处理')}
          </a>
        );
      return actionBtns;
    } else {
      return (
        <a style={{ pointerEvents: 'none', color: 'rgba(0, 0, 0, 0.25)' }}>
          {intl.get(`${promptCode}.model.exception.handle`).d('处理')}
        </a>
      );
    }
  }

  render() {
    const { loading, dataSource, pagination, onChange, onView } = this.props;
    const { isShowChange, message, isShowMessage, changeText } = this.state;
    const columns = [
      {
        title: intl.get(`${prompt}.producerService`).d('生产服务'),
        dataIndex: 'sourceService',
        width: 180,
      },
      {
        title: intl.get(`hdtt.tableChangeLog.model.tableChangeLog.sourceDs`).d('生产数据源'),
        width: 180,
        dataIndex: 'sourceDs',
      },
      {
        title: intl.get(`${prompt}.sourceDb`).d('生产DB'),
        width: 180,
        dataIndex: 'sourceDb',
      },
      {
        title: intl.get(`${prompt}.tableName`).d('生产表名'),
        width: 180,
        dataIndex: 'sourceTable',
      },
      {
        title: intl.get(`${prompt}.consServiceName`).d('消费服务'),
        dataIndex: 'targetService',
        width: 180,
      },
      {
        title: intl.get(`hdtt.tableChangeLog.model.tableChangeLog.targetDs`).d('消费数据源'),
        dataIndex: 'targetDs',
        width: 180,
      },
      {
        title: intl.get(`${prompt}.consDB`).d('消费DB'),
        dataIndex: 'targetDb',
      },
      {
        title: intl.get(`${prompt}.consTableName`).d('消费表名'),
        dataIndex: 'targetTable',
        width: 180,
      },
      {
        title: intl.get(`hdtt.tableChangeLog.model.tableChangeLog.changeLog`).d('变更内容'),
        dataIndex: 'messages',
        width: 150,
        render: text => <a onClick={() => this.handleOpenChangeModal(text)}>{text}</a>,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'processStatus',
        width: 150,
        render: text => {
          const statusList = [
            {
              status: 'R',
              color: 'blue',
              text: intl.get(`${promptCode}.model.exception.status.handling`).d('处理中'),
            },
            {
              status: 'S',
              color: 'green',
              text: intl.get(`${promptCode}.model.exception.status.success`).d('完成'),
            },
            {
              status: 'E',
              color: 'red',
              text: intl.get(`${promptCode}.model.exception.status.failed`).d('失败'),
            },
            {
              status: 'P',
              color: 'orange',
              text: intl.get(`${promptCode}.model.exception.status.waiting`).d('待处理'),
            },
            {
              status: 'N',
              color: 'gold',
              text: intl.get(`${promptCode}.model.exception.status.right`).d('无需处理'),
            },
            {
              status: 'ER',
              color: 'lime',
              text: intl.get(`${promptCode}.model.exception.status.post`).d('错误已上报'),
            },
            {
              status: 'ERE',
              color: 'volcano',
              text: intl.get(`${promptCode}.model.exception.status.post.failed`).d('错误上报失败'),
            },
          ];
          return TagRender(text, statusList);
        },
      },
      {
        title: intl.get(`hdtt.tableChangeLog.model.tableChangeLog.processTime`).d('处理时间'),
        dataIndex: 'processTime',
        width: 160,
      },
      {
        title: intl.get(`hdtt.tableChangeLog.model.tableChangeLog.processMsg`).d('处理信息'),
        dataIndex: 'processMsg',
        width: 150,
        render: text => <a onClick={() => this.handleOpenMessageModal(text)}>{text}</a>,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        fixed: 'right',
        key: 'edit',
        render: (_, record) => {
          const operators = [
            {
              key: 'view',
              ele: (
                <a onClick={() => onView(record.producerConfigId)}>
                  {intl.get(`${promptCode}.model.exception.view.config`).d('查看配置')}
                </a>
              ),
              len: 4,
              title: intl.get(`${promptCode}.model.exception.view.config`).d('查看配置'),
            },
            {
              key: 'deal',
              ele: this.renderActions(record),
              len: 2,
              title: intl.get(`${promptCode}.model.exception.handle`).d('处理'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    return (
      <>
        <Table
          bordered
          rowKey="dbMigrationLogId"
          scroll={{ x: tableScrollWidth(columns) }}
          loading={loading}
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={page => onChange(page)}
        />
        <Modal
          visible={isShowChange}
          destroyOnClose
          maskClosable
          title={intl.get(`hdtt.tableChangeLog.model.tableChangeLog.changeLog`).d('变更内容')}
          onCancel={this.handleCloseChangeModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseChangeModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <TextArea row={4} style={{ height: '260px' }} readOnly>
            {changeText}
          </TextArea>
        </Modal>
        <Modal
          visible={isShowMessage}
          destroyOnClose
          maskClosable
          title={intl.get(`hdtt.tableChangeLog.model.tableChangeLog.processMsg`).d('处理信息')}
          onCancel={this.handleCloseMessageModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseMessageModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <TextArea row={4} style={{ height: '260px' }} readOnly>
            {message}
          </TextArea>
        </Modal>
      </>
    );
  }
}
