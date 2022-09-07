/**
 * ListTable - 生产消费异常监控列表页
 * @date: 2019-5-6
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

import jsonFormat from '../../../components/JsonFormat';

const { TextArea } = Input;
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
 */
export default class ListTable extends Component {
  state = {
    isShowMessage: false, // 是否显示消息内容
    isShowError: false, // 是否显示错误消息或处理消息
    messageType: 'error', // 消息类型 错误消息或处理消息
    message: null,
    errorText: null,
    currentErrorEventId: null,
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
   * 显示消息内容模态框
   */
  @Bind()
  handleOpenMessageModal(text) {
    const obj = this.handleTransObj(text);
    const jsonText = jsonFormat(obj);
    this.setState({
      isShowMessage: true,
      message: jsonText,
    });
  }

  /**
   * 关闭消息内容模态框
   */
  @Bind()
  handleCloseMessageModal() {
    this.setState({
      isShowMessage: false,
    });
  }

  /**
   * 显示错误消息或处理消息模态框
   * @param {string} text - 错误信息
   * @param {string} messageType - 消息类型
   */
  @Bind()
  handleOpenErrorModal(text, messageType) {
    this.setState({
      errorText: text,
      isShowError: true,
      messageType,
    });
  }

  /**
   * 关闭错误消息或处理消息模态框
   */
  @Bind()
  handleCloseErrorModal() {
    this.setState({
      isShowError: false,
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
        currentErrorEventId: record.errorEventId,
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
    const { currentErrorEventId } = this.state;
    if (record.processStatus === 'R') {
      return (
        <a style={{ pointerEvents: 'none', color: 'rgba(0, 0, 0, 0.25)' }}>
          {intl.get(`${promptCode}.model.exception.handle`).d('处理')}
        </a>
      );
    } else if (record.processStatus !== 'S') {
      const actionBtns =
        currentErrorEventId === record.errorEventId && isHandling ? (
          <a>
            <Icon type="loading" />
          </a>
        ) : (
          <a onClick={() => this.handleError(record)}>
            {intl.get(`${promptCode}.model.exception.handle`).d('处理')}
          </a>
        );
      return actionBtns;
    }
  }

  render() {
    const { loading, dataSource, pagination, onChange, onView, isTenant } = this.props;
    const { isShowMessage, message, isShowError, errorText, messageType } = this.state;
    const columns = [
      {
        title: intl.get(`${promptCode}.model.exception.eventId`).d('事件ID'),
        dataIndex: 'eventId',
        width: 90,
      },
      {
        title: intl.get(`${promptCode}.model.exception.eventType`).d('事件类型'),
        width: 90,
        dataIndex: 'eventTypeMeaning',
      },
      {
        title: intl.get(`${promptCode}.model.exception.sourceService`).d('生产服务'),
        width: 150,
        dataIndex: 'sourceService',
      },
      {
        title: intl.get(`${promptCode}.model.exception.tableName`).d('生产表名'),
        width: 150,
        dataIndex: 'sourceTable',
      },
      {
        title: intl.get(`${promptCode}.model.exception.sourceKeyId`).d('数据ID'),
        dataIndex: 'sourceKeyId',
        width: 90,
      },
      !isTenant && {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'sourceTenantName',
        width: 140,
      },
      {
        title: intl.get(`${promptCode}.model.exception.targetService`).d('消费服务'),
        dataIndex: 'targetService',
      },
      {
        title: intl.get(`${promptCode}.model.exception.targetDb`).d('消费DB'),
        dataIndex: 'targetDb',
      },
      {
        title: intl.get(`${promptCode}.model.exception.targetTable`).d('消费表'),
        dataIndex: 'targetTable',
      },
      {
        title: intl.get(`${promptCode}.model.exception.messages`).d('消息内容'),
        dataIndex: 'messages',
        width: 100,
        render: text => <a onClick={() => this.handleOpenMessageModal(text)}>{text}</a>,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'processStatus',
        width: 130,
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
          ];
          return TagRender(text, statusList);
        },
      },
      {
        title: intl.get(`${promptCode}.model.exception.errorType`).d('错误类型'),
        dataIndex: 'errorTypeMeaning',
        width: 100,
      },
      {
        title: intl.get(`${promptCode}.model.exception.errorTime`).d('出错时间'),
        dataIndex: 'errorTime',
        width: 150,
      },
      {
        title: intl.get(`${promptCode}.model.exception.errorText`).d('错误消息'),
        dataIndex: 'errorText',
        width: 100,
        render: text => <a onClick={() => this.handleOpenErrorModal(text, 'error')}>{text}</a>,
      },
      {
        title: intl.get(`${promptCode}.model.exception.retryTimes`).d('已重试次数'),
        dataIndex: 'retryTimes',
        width: 100,
      },
      {
        title: intl.get(`${promptCode}.model.exception.processMsg`).d('处理信息'),
        dataIndex: 'processMsg',
        width: 100,
        render: text => <a onClick={() => this.handleOpenErrorModal(text, 'process')}>{text}</a>,
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
    ].filter(Boolean);
    return (
      <>
        <Table
          bordered
          rowKey="errorEventId"
          scroll={{ x: tableScrollWidth(columns) }}
          loading={loading}
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={page => onChange(page)}
        />
        <Modal
          visible={isShowMessage}
          destroyOnClose
          maskClosable
          title={intl.get(`${promptCode}.model.exception.messages`).d('消息内容')}
          onCancel={this.handleCloseMessageModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseMessageModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <pre style={{ maxHeight: '350px' }}>
            <code>{message}</code>
          </pre>
        </Modal>
        <Modal
          visible={isShowError}
          destroyOnClose
          maskClosable
          title={
            messageType === 'error'
              ? intl.get(`${promptCode}.model.exception.errorText`).d('错误消息')
              : intl.get(`${promptCode}.model.exception.processMsg`).d('处理信息')
          }
          onCancel={this.handleCloseErrorModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseErrorModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <TextArea row={4} style={{ height: '260px' }} readOnly>
            {errorText}
          </TextArea>
        </Modal>
      </>
    );
  }
}
