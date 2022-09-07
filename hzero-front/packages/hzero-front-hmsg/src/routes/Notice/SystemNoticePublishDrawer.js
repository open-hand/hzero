/**
 * TODO: 可优化， 详情和列表都使用到了该组件 可以自己直接 connect 连接 redux
 * SystemNoticePublishModal
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-06-12
 * @copyright 2019-06-12 © HAND
 */

import React, { Component } from 'react';
import { Button, Card, Col, Drawer, Popconfirm, Row, Spin, Table } from 'hzero-ui';
import { Bind, Debounce } from 'lodash-decorators';
import { isNil } from 'lodash';

import notification from 'utils/notification';
import intl from 'utils/intl';
import {
  DEBOUNCE_TIME,
  DETAIL_CARD_TABLE_THIRD_CLASSNAME,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
} from 'utils/constants';
import { dateTimeRender, operatorRender } from 'utils/renderer';

import SystemNoticePublishAddDrawer from './SystemNoticePublishAddDrawer';

function historyRowKeyFunc(record, index) {
  return `${index}-${record}`;
}

export default class SystemNoticePublishDrawer extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addDrawerVisible: false,
      cacheHistoryPagination: {},
      cacheReceivePagination: {},
    };
  }

  componentDidMount() {
    this.handleHistorySearch();
  }

  componentWillUnmount() {
    this.handleHistoryTableRowChange.cancel();
  }

  /**
   * // 会重置 选中/分页 右侧的状态
   * 查询历史记录
   * @param pagination
   */
  handleHistorySearch(pagination = {}) {
    const { querySystemHistory, record } = this.props;
    this.setState({
      cacheHistoryPagination: pagination,
    });
    return querySystemHistory({
      noticeId: record.noticeId,
      query: pagination,
    });
  }

  /**
   * 查询接受信息
   * @param {object} query 分页信息 或者 选中的历史信息
   * @param {string} type - page history reload
   * page - 传递 {page, sort}
   * history - 传递 {systemNoticeHistorySelectedRows, systemNoticeHistorySelectedRowKeys}
   * reload - 传递 state 中的 cacheReceivePagination
   */
  handleReceiveSearch(query = {}, type) {
    const { queryReceiver, record } = this.props;
    switch (type) {
      case 'page':
        this.setState({
          cacheReceivePagination: query,
        });
        break;
      case 'history':
        break;
      case 'reload':
        break;
      default:
        break;
    }
    return queryReceiver({
      noticeId: record.noticeId,
      query,
    });
  }

  // Drawer
  /**
   * 关闭 Drawer
   */
  @Bind()
  handleClose() {
    const { onCancel } = this.props;
    onCancel();
  }

  // Left Table
  @Bind()
  handleHistoryTableChange(page, filter, sort) {
    this.handleHistorySearch({ page, sort });
  }

  @Debounce(DEBOUNCE_TIME)
  @Bind()
  handleHistoryTableRowChange(_, selectedRows) {
    const selectedRowKeys = selectedRows.map((r) => r.publishedId);
    this.handleReceiveSearch(
      {
        systemNoticeHistorySelectedRows: selectedRows,
        systemNoticeHistorySelectedRowKeys: selectedRowKeys,
      },
      'history'
    );
  }

  getHistoryColumns() {
    return [
      {
        dataIndex: 'publishedStatusMeaning',
        title: intl.get('hmsg.notice.model.publish.publishedStatus').d('发布状态'),
        width: 100,
      },
      {
        dataIndex: 'publishedDate',
        title: intl.get('hmsg.notice.model.publish.publishedDate').d('发布时间'),
        render: dateTimeRender,
        width: 160,
      },
      {
        dataIndex: 'publisherName',
        title: intl.get('hmsg.notice.model.publish.publisherName').d('发布人'),
      },
    ];
  }

  // Right Table
  @Bind()
  handleReceiveTableChange(page, filter, sort) {
    this.handleReceiveSearch({ page, sort }, 'page');
  }

  @Bind()
  handleRemoveDraftReceive(record) {
    const { removeDraftReceive } = this.props;
    removeDraftReceive({
      record,
    }).then((res) => {
      if (res) {
        const { cacheReceivePagination = {} } = this.state;
        this.handleReceiveSearch(cacheReceivePagination, 'reload');
      }
    });
  }

  getReceiveColumns() {
    return [
      {
        dataIndex: 'receiverTypeMeaning',
        title: intl.get('hmsg.common.view.type').d('类型'),
        width: 100,
      },
      {
        dataIndex: 'receiverSourceName',
        title: intl.get('hmsg.notice.model.receive.receiverSource').d('接收方名称'),
      },
      {
        key: 'action',
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        render: (_, record) => {
          const operators = [];
          if (!isNil(record.receiverId)) {
            operators.push({
              key: 'delete',
              ele: (
                <Popconfirm
                  onConfirm={() => {
                    this.handleRemoveDraftReceive(record);
                  }}
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          }
          return operatorRender(operators);
        },
      },
    ];
  }

  // Right Btn
  @Bind()
  handleAddBtnClick() {
    // 打开 新增接收配置的 模态框
    this.setState({
      addDrawerVisible: true,
    });
  }

  @Bind()
  handlePublishBtnClick() {
    const { record, publishSystemNotice, systemNoticeHistorySelectedRows = [] } = this.props;
    publishSystemNotice({
      noticeId: record.noticeId,
      records: systemNoticeHistorySelectedRows.map((r) => r.publishedId),
    }).then((res) => {
      if (res) {
        const { cacheHistoryPagination = {} } = this.state;
        this.handleHistorySearch(cacheHistoryPagination);
      }
    });
  }

  // SystemNoticePublishAddDrawer
  @Bind()
  handleAddDrawerClose() {
    this.setState({
      addDrawerVisible: false,
    });
  }

  @Bind()
  handleAddReceiver(payload) {
    const { createReceiver } = this.props;
    createReceiver(payload).then((res) => {
      if (res) {
        notification.success();
        const { cacheHistoryPagination = {} } = this.state;
        this.handleHistorySearch(cacheHistoryPagination);
        this.handleAddDrawerClose();
      }
    });
  }

  render() {
    const {
      visible,
      systemNoticeHistoryDataSource = [],
      systemNoticeHistoryPagination = false,
      systemNoticeHistorySelectedRows = [],
      systemNoticeHistorySelectedRowKeys = [],
      systemNoticeReceiveDataSource = [],
      systemNoticeReceivePagination = false,
      receiverRecordType,
      record,
      organizationId,
      isTenantRole,
      querySystemHistoryLoading = false,
      queryReceiverLoading = false,
      createReceiverLoading,
      publishSystemNoticeLoading = false,
      removeDraftReceiveLoading = false,
    } = this.props;
    const { addDrawerVisible = false } = this.state;
    const historyRowSelection = {
      selectedRowKeys: systemNoticeHistorySelectedRowKeys,
      onChange: this.handleHistoryTableRowChange,
    };
    const historyColumns = this.getHistoryColumns();
    const receiveColumns = this.getReceiveColumns();

    return (
      <Drawer
        title={intl
          .get('hmsg.notice.view.message.title.publish', {
            type:
              record.receiverTypeCode === 'NOTIFY'
                ? intl.get('hmsg.notice.model.notice.receiverTypeCode.notify').d('系统通知')
                : intl.get('hmsg.notice.model.notice.receiverTypeCode.ANNOUNCE').d('系统公告'),
          })
          .d(
            `发布${
              record.receiverTypeCode === 'NOTIFY'
                ? intl.get('hmsg.notice.model.notice.receiverTypeCode.notify').d('系统通知')
                : intl.get('hmsg.notice.model.notice.receiverTypeCode.ANNOUNCE').d('系统公告')
            }`
          )}
        visible={visible}
        onClose={this.handleClose}
        width={1000}
      >
        <Spin
          spinning={querySystemHistoryLoading || publishSystemNoticeLoading || queryReceiverLoading}
        >
          <Row {...EDIT_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_2_LAYOUT}>
              <Card
                bordered={false}
                title={
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span>{intl.get('hmsg.notice.view.title.publishedHistory').d('发布记录')}</span>
                  </div>
                }
                className={DETAIL_CARD_TABLE_THIRD_CLASSNAME}
              >
                <Table
                  bordered
                  rowKey="publishedId"
                  pagination={systemNoticeHistoryPagination}
                  dataSource={systemNoticeHistoryDataSource}
                  rowSelection={historyRowSelection}
                  onChange={this.handleHistoryTableChange}
                  columns={historyColumns}
                  style={{ marginTop: '4px' }}
                />
              </Card>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <Card
                bordered={false}
                title={
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span>{intl.get('hmsg.notice.view.title.receiveHistory').d('接收记录')}</span>
                    <div>
                      <Button
                        type="primary"
                        onClick={this.handlePublishBtnClick}
                        disabled={
                          removeDraftReceiveLoading || systemNoticeHistorySelectedRows.length === 0
                        }
                        loading={publishSystemNoticeLoading}
                        style={{ marginRight: '8px' }}
                      >
                        {intl.get('hzero.common.button.republish').d('重新发布')}
                      </Button>
                      <Button onClick={this.handleAddBtnClick} disabled={removeDraftReceiveLoading}>
                        {intl.get('hzero.common.button.add').d('新增')}
                      </Button>
                    </div>
                  </div>
                }
                className={DETAIL_CARD_TABLE_THIRD_CLASSNAME}
              >
                <Table
                  bordered
                  rowKey={historyRowKeyFunc}
                  pagination={systemNoticeReceivePagination}
                  dataSource={systemNoticeReceiveDataSource}
                  onChange={this.handleReceiveTableChange}
                  columns={receiveColumns}
                />
              </Card>
            </Col>
          </Row>
        </Spin>
        {addDrawerVisible && (
          <SystemNoticePublishAddDrawer
            receiverRecordType={receiverRecordType}
            visible={addDrawerVisible}
            onClose={this.handleAddDrawerClose}
            onAddReceiver={this.handleAddReceiver}
            record={record}
            organizationId={organizationId}
            isTenantRole={isTenantRole}
            createReceiverLoading={createReceiverLoading}
          />
        )}
      </Drawer>
    );
  }
}
