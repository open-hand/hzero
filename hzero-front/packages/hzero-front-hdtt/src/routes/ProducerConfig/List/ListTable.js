/**
 * ListTable - 数据消息生产消费配置列表页
 * @date: 2019-4-15
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { enableRender, yesOrNoRender, operatorRender } from 'utils/renderer';
import TopicMsgModal from './TopicMsgModal';

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Function} onEdit - 跳转详情页
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @return React.element
 */
export default class ListTable extends Component {
  state = {
    topicMsg: '',
    topicTime: '',
    isShowMessage: false,
  };

  /**
   * 编辑
   * @param {number} producerConfigId - 生产配置Id
   */
  @Bind()
  handleEdit(producerConfigId) {
    this.props.onEdit(producerConfigId);
  }

  /**
   * 显示Topic消息内容模态框
   */
  @Bind()
  handleOpenMessageModal(topicGeneratedMsg, topicGeneratedTime) {
    this.setState({
      isShowMessage: true,
      topicMsg: topicGeneratedMsg,
      topicTime: topicGeneratedTime,
    });
  }

  /**
   * 关闭Topic消息内容模态框
   */
  @Bind()
  handleCloseMessageModal() {
    this.setState({
      isShowMessage: false,
    });
  }

  render() {
    const { loading, dataSource, pagination, onChange, onDelete, isTenant } = this.props;
    const { isShowMessage, topicMsg, topicTime } = this.state;
    const topicModalProps = {
      topicGeneratedMsg: topicMsg,
      topicGeneratedTime: topicTime,
      visible: isShowMessage,
      onCancel: this.handleCloseMessageModal,
    };
    const columns = [
      {
        title: intl.get(`hdtt.producerConfig.model.producerConfig.producerService`).d('生产服务'),
        width: 150,
        dataIndex: 'serviceName',
      },
      {
        title: intl.get(`hdtt.producerConfig.model.producerConfig.tableName`).d('生产表名'),
        dataIndex: 'tableName',
        width: 150,
      },
      {
        title: intl.get(`hdtt.producerConfig.model.producerConfig.description`).d('说明'),
        dataIndex: 'description',
        width: 300,
      },
      !isTenant && {
        title: intl
          .get(`hdtt.producerConfig.model.producerConfig.tenantFlag`)
          .d('是否按照租户分发'),
        width: 130,
        dataIndex: 'tenantFlag',
        render: yesOrNoRender,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 120,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get(`hdtt.producerConfig.model.producerConfig.topicStatus`).d('Topic创建状态'),
        dataIndex: 'topicGeneratedStatusMeaning',
        render: (text, { topicGeneratedStatus, topicGeneratedMsg, topicGeneratedTime }) => {
          if (topicGeneratedStatus === 'E') {
            return (
              <a onClick={() => this.handleOpenMessageModal(topicGeneratedMsg, topicGeneratedTime)}>
                {text}
              </a>
            );
          } else {
            return text;
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 130,
        key: 'edit',
        fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.handleEdit(record.producerConfigId)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  placement="topRight"
                  onConfirm={() => onDelete(record)}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
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
          rowKey="producerConfigId"
          loading={loading}
          columns={columns}
          dataSource={dataSource}
          pagination={pagination}
          onChange={page => onChange(page)}
        />
        <TopicMsgModal {...topicModalProps} />
      </>
    );
  }
}
