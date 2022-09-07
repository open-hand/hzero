/**
 * Detail - 数据消费生产消费配置详情页-表格
 * @date: 2019/4/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Table, Icon, Modal, Button, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

const { TextArea } = Input;
const promptCode = 'hdtt.producerConfig.model.producerConfig';

/**
 * 列信息数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} rowSelection - 行选择配置
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */

export default class ConsumerTable extends Component {
  state = {
    currentConsDbConfigId: '',
    dbInitMsg: '',
    isShowDbMsg: false,
  };

  /**
   * 编辑
   * @param {Object} record - 消费配置行数据
   */
  @Bind()
  handleEdit(record) {
    this.props.onEdit('edit', record);
  }

  /**
   * 消费者初始化 - db维度
   * @param {Object} record - 消费配置行数据
   */
  @Bind()
  handleInit(record) {
    this.setState({
      currentConsDbConfigId: record.consDbConfigId,
    });
    this.props.onInit(record);
  }

  /**
   * 渲染初始化按钮
   *  @param {Object} record - 消费配置行数据
   */
  @Bind()
  renderInitBtn(record) {
    const { isDbIniting, tenantFlag } = this.props;
    const { currentConsDbConfigId } = this.state;
    let btn = null;
    if (isDbIniting && currentConsDbConfigId === record.consDbConfigId) {
      btn = (
        <a>
          <Icon type="loading" />
        </a>
      );
    } else if (!tenantFlag && record.processStatus !== 'R') {
      btn = (
        <a onClick={() => this.handleInit(record)}>{intl.get(`${promptCode}.init`).d('初始化')}</a>
      );
    } else if (!tenantFlag && record.processStatus === 'R') {
      btn = (
        <a style={{ pointerEvents: 'none', color: 'rgba(0, 0, 0, 0.25)' }}>
          {intl.get(`${promptCode}.init`).d('初始化')}
        </a>
      );
    }
    return btn;
  }

  /**
   * 显示初始化消息弹窗
   * @param {string} text - 初始化消息
   */
  @Bind()
  handleOpenMsgModal(text) {
    this.setState({
      isShowDbMsg: true,
      dbInitMsg: text,
    });
  }

  /**
   * 关闭初始化消息弹窗
   */
  @Bind()
  handleCloseMsgModal() {
    this.setState({
      isShowDbMsg: false,
      dbInitMsg: '',
    });
  }

  render() {
    const {
      loading = false,
      dataSource = [],
      pagination = false,
      rowSelection,
      onChange,
      tenantFlag,
    } = this.props;
    const { isShowDbMsg, dbInitMsg } = this.state;
    const columns = [
      {
        title: intl.get(`${promptCode}.consServiceName`).d('消费服务'),
        dataIndex: 'consumerService',
        width: 150,
      },
      {
        title: intl.get(`${promptCode}.consDB`).d('消费DB'),
        dataIndex: 'consumerDb',
        width: 150,
      },
      {
        title: intl.get(`${promptCode}.consTableName`).d('消费表名'),
        dataIndex: 'consumerTable',
        width: 150,
      },
      {
        title: intl.get(`${promptCode}.consDataSource`).d('消费数据源'),
        dataIndex: 'consumerDs',
        width: 150,
      },
      {
        title: intl.get(`${promptCode}.consumerOffset`).d('初始偏移'),
        dataIndex: 'consumerOffset',
        width: 100,
      },
      {
        title: intl.get(`${promptCode}.enable`).d('是否启用'),
        align: 'center',
        width: 120,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get(`${promptCode}.initStatus`).d('初始化状态'),
        align: 'center',
        dataIndex: 'processStatusMeaning',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.initTime`).d('初始化时间'),
        dataIndex: 'processTime',
        width: 120,
      },
      {
        title: intl.get(`${promptCode}.initMsg`).d('初始化消息'),
        dataIndex: 'processMsg',
        width: 150,
        render: text => <a onClick={() => this.handleOpenMsgModal(text)}>{text}</a>,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: tenantFlag ? 120 : 150,
        fixed: 'right',
        key: 'edit',
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.handleEdit(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'init',
              ele: this.renderInitBtn(record),
              len: 3,
              title: intl.get(`${promptCode}.init`).d('初始化'),
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
          rowKey="consDbConfigId"
          rowSelection={rowSelection}
          loading={loading}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          pagination={pagination}
          onChange={page => onChange(page)}
        />
        <Modal
          visible={isShowDbMsg}
          destroyOnClose
          maskClosable
          title={intl.get(`${promptCode}.initMsg`).d('初始化消息')}
          onCancel={this.handleCloseMsgModal}
          footer={[
            <Button key="cancel" onClick={this.handleCloseMsgModal}>
              {intl.get('hzero.common.button.close').d('关闭')}
            </Button>,
          ]}
        >
          <TextArea row={4} style={{ height: '260px' }} readOnly>
            {dbInitMsg}
          </TextArea>
        </Modal>
      </>
    );
  }
}
