/*
 * @Description: 事件查询
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-06-09 13:49:47
 * @Copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Table, Modal, Button } from 'hzero-ui';
import { Modal as proModal, Table as ProTable, DataSet } from 'choerodon-ui/pro';

import { Button as ButtonPermission } from 'components/Permission';
import { Header, Content } from 'components/Page';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { dateTimeRender } from 'utils/renderer';
import { consumeDS } from '@/stores/EventMessageDS';
import { queryMapIdpValue } from 'services/api';

import QueryForm from './QueryForm';

const modalKey = proModal.key();
const modalKey2 = proModal.key();
@connect(({ eventMessage, loading }) => ({
  eventMessage,
  tenantId: getCurrentOrganizationId(),
  fetchLoading: loading.effects['eventMessage/queryMessageList'],
}))
@formatterCollections({ code: ['hevt.eventMessage', 'hevt.common'] })
export default class EventMessage extends React.Component {
  constructor(props) {
    super(props);
    this.consumeDS = new DataSet(consumeDS());
    this.state = {
      visible: false,
      viewData: '',
      mapValues: {
        processTypes: null,
        processStatus: null,
      },
    };
  }

  QueryForm;

  async componentDidMount() {
    this.handleQueryMessage();
    const mapValues = getResponse(
      await queryMapIdpValue({
        processTypes: 'HEVT.MESSAGE_PROCESS_TYPE',
        processStatus: 'HEVT.MESSAGE_PROCESS_STATUS',
      })
    );
    if (mapValues) {
      this.setState({
        mapValues,
      });
    }
  }

  /**
   * 获取查询表单组件this对象
   * @param {Object} ref - 查询表单组件this
   */
  @Bind()
  handleBindRef(ref) {
    this.QueryForm = (ref.props || {}).form;
  }

  /**
   * 重置表单查询条件
   */
  @Bind()
  handleResetSearch() {
    this.QueryForm.resetFields();
  }

  /**
   * 获取事件消息信息
   * @param {Object} params 传递的参数
   */
  @Bind()
  handleQueryMessage(params = {}) {
    const {
      dispatch,
      tenantId,
      eventMessage: { pagination = {} },
    } = this.props;
    const filterValue = this.QueryForm === undefined ? {} : this.QueryForm.getFieldsValue();
    dispatch({
      type: 'eventMessage/queryMessageList',
      payload: { tenantId, ...filterValue, page: pagination, ...params },
    });
  }

  /**
   * 重试
   */
  @Bind()
  handleResendMessage(record) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'eventMessage/resendMessage',
      payload: { tenantId, ...record },
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  /**
   * handlePagination - 分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handlePagination(pagination) {
    this.handleQueryMessage({ page: pagination });
  }

  @Bind()
  handleShowMore(value) {
    if (value) {
      this.setState({
        visible: true,
        viewData: value,
      });
    } else {
      this.setState({
        visible: false,
      });
    }
  }

  get consumeColumns() {
    return [
      { name: 'processHost', width: 120 },
      { name: 'eventName', width: 120 },
      { name: 'categoryName', width: 120 },
      { name: 'processStatus' },
      { name: 'processTime', width: 150 },
      {
        name: 'remark',
        width: 200,
        renderer: ({ value }) => {
          return (
            <span className="action-link">
              <a onClick={() => this.handleShowMore(value)}>{value}</a>
            </span>
          );
        },
      },
    ];
  }

  /**
   *显示更多 --同步详情显示更多
   *
   */
  handleShowMore = (value) => {
    const modal = proModal.open({
      closable: true,
      style: {
        width: 700,
        top: 10,
      },
      key: modalKey2,
      children: value,
      footer: (
        <Button color="primary" onClick={() => modal.close()}>
          {intl.get('hzero.common.button.close').d('关闭')}
        </Button>
      ),
    });
  };

  handleConsumeDetail = (record) => {
    const { eventMessageId } = record;
    this.consumeDS.queryParameter = {
      processType: 'CONSUME',
      sourceMessageId: eventMessageId,
    };
    this.consumeDS.query();
    const modal = proModal.open({
      title: intl.get('hevt.eventMessage.view.button.consume').d('消费详情'),
      drawer: true,
      closable: true,
      style: {
        width: 800,
      },
      key: modalKey,
      children: <ProTable dataSet={this.consumeDS} queryBar="none" columns={this.consumeColumns} />,
      destroyOnClose: true,
      onCancel: () => this.consumeDS.reset(),
      onClose: () => this.consumeDS.reset(),
      footer: (
        <Button type="primary" onClick={() => modal.close()}>
          {intl.get('hzero.common.button.close').d('关闭')}
        </Button>
      ),
    });
  };

  render() {
    const {
      eventMessage: { messageData = [], pagination = {} },
      fetchLoading,
      match: { path },
    } = this.props;
    const columns = [
      {
        title: intl.get('hevt.eventMessage.model.eventMessage.categoryName').d('事件类型'),
        dataIndex: 'categoryName',
      },
      {
        title: intl.get('hevt.eventMessage.model.eventMessage.eventName').d('事件'),
        dataIndex: 'eventName',
        width: 200,
      },
      {
        title: intl.get('hevt.eventMessage.model.eventMessage.action').d('功能'),
        dataIndex: 'action',
        width: 80,
        render: (value) => {
          switch (value?.toLocaleLowerCase()) {
            case 'create':
              return intl.get('hevt.eventMessage.view.action.create').d('创建');
            case 'update':
              return intl.get('hevt.eventMessage.view.action.update').d('更新');
            case 'delete':
              return intl.get('hevt.eventMessage.view.action.delete').d('删除');
            default:
              return value;
          }
        },
      },
      {
        title: intl.get('hevt.eventMessage.model.eventMessage.data').d('事件数据'),
        dataIndex: 'data',
        width: 250,
        render: (value) => {
          return (
            <span className="action-link">
              <a onClick={() => this.handleShowMore(value)}>{value}</a>
            </span>
          );
        },
      },
      {
        title: intl.get('hevt.eventMessage.model.eventMessage.processStatus').d('状态'),
        dataIndex: 'processStatus',
        width: 80,
        render: (value) => {
          const obj = this.state.mapValues.processStatus?.filter((e) => e.value === value);
          return obj && obj[0]?.meaning;
        },
      },
      messageData.length > 0 &&
        messageData[0].processType === 'PRODUCE' && {
          title: intl.get('hevt.eventMessage.model.eventMessage.consumeSuccess').d('消费状态'),
          dataIndex: 'consumeStatus',
          render: (value) => {
            switch (value) {
              case 'success':
                return intl.get('hevt.common.view.message.success').d('成功');
              case 'failed':
                return intl.get('hevt.common.view.message.failed').d('失败');
              default:
                return intl.get('hevt.common.view.message.pending').d('待消费');
            }
          },
        },
      {
        title: intl.get('hevt.eventMessage.model.eventMessage.sendTime').d('发送时间'),
        dataIndex: 'processTime',
        width: 180,
        render: dateTimeRender,
      },
      messageData.length > 0 &&
        messageData[0].processType === 'PRODUCE' && {
          title: intl.get('hzero.common.button.action').d('操作'),
          width: 150,
          fixed: 'right',
          render: (_, record) => (
            <span className="action-link">
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.resend`,
                    type: 'button',
                    meaning: '重试',
                  },
                ]}
                onClick={() => {
                  this.handleResendMessage(record);
                }}
              >
                {intl.get('hevt.eventMessage.view.button.resend').d('重试')}
              </ButtonPermission>
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.consume`,
                    type: 'button',
                    meaning: '消费详情',
                  },
                ]}
                onClick={() => {
                  this.handleConsumeDetail(record);
                }}
              >
                {intl.get('hevt.eventMessage.view.button.consume').d('消费详情')}
              </ButtonPermission>
            </span>
          ),
        },
    ].filter(Boolean);

    return (
      <>
        <Header title={intl.get('hevt.eventMessage.view.eventMessage.title').d('事件查询')} />
        <Content>
          <div className="table-list-search">
            <QueryForm
              onSearch={this.handleQueryMessage}
              onReset={this.handleResetSearch}
              onRef={this.handleBindRef}
              mapValues={this.state.mapValues}
            />
          </div>
          <Table
            bordered
            columns={columns}
            rowKey="eventMessageId"
            dataSource={messageData || []}
            loading={fetchLoading}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          <Modal
            visible={this.state.visible}
            closable={false}
            footer={[
              <Button type="primary" onClick={() => this.handleShowMore()}>
                {intl.get('hzero.common.button.close').d('关闭')}
              </Button>,
            ]}
          >
            {this.state.viewData}
          </Modal>
        </Content>
      </>
    );
  }
}
