/**
 * Event - 事件消息界面
 * @date: 2018-6-20
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Table, Modal, Card, Col, Row } from 'hzero-ui';
import { connect } from 'dva';
import { filter } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { enableRender, operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import {
  DETAIL_CARD_CLASSNAME,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  DETAIL_CARD_TABLE_CLASSNAME,
} from 'utils/constants';

import EventMessageForm from './EventMessageForm';

import styles from './styles.less';

@connect(({ event, loading }) => ({
  event,
  isSiteFlag: !isTenantRoleLevel(),
  tenantId: getCurrentOrganizationId(),
  getMessageListLoading: loading.effects['event/getMessageList'],
  getEventLoading: loading.effects['event/getEvent'],
  saving: loading.effects['event/createMessage'] || loading.effects['event/updateMessage'],
}))
@formatterCollections({
  code: ['hpfm.event'],
})
@Form.create({ fieldNameProp: null })
export default class EditForm extends React.Component {
  state = {
    selectedRowKeys: [],
    messageVisible: false,
    itemData: {},
    event: {},
    currentDetailTenantId: '',
  };

  componentDidMount() {
    this.fetchMessageTypeCode();
    this.loadEvent();
  }

  /**
   *获取消息类型
   */
  @Bind()
  fetchMessageTypeCode() {
    const { dispatch } = this.props;
    dispatch({
      type: 'event/fetchMessageTypeCode',
    });
  }

  /**
   * 查询消息列表
   *
   * @memberof EditForm
   */
  @Bind()
  queryMessageList(params = {}) {
    const { dispatch } = this.props;
    const { event } = this.state;
    dispatch({
      type: 'event/getMessageList',
      payload: { eventCode: event.eventCode, tenantId: event.tenantId, ...params },
    });
  }

  /**
   * 查询事件
   *
   * @memberof EditForm
   */
  @Bind()
  loadEvent() {
    const { form, dispatch, match, tenantId } = this.props;
    dispatch({
      type: 'event/getEvent',
      payload: { id: match.params.id, tenantId },
    }).then((res) => {
      if (res) {
        this.setState({
          event: res,
          currentDetailTenantId: res.tenantId,
        });
        const formValues = {
          eventCode: res.eventCode,
          eventDescription: res.eventDescription,
          enabledFlag: res.enabledFlag,
        };
        form.setFieldsValue(formValues);
        this.queryMessageList();
      }
    });
  }

  /**
   * 新增消息模态框
   *
   * @memberof EditForm
   */
  @Bind()
  showCreateModal() {
    this.setState({
      messageVisible: true,
    });
  }

  /**
   * 打开编辑模态框
   * @param {*} record
   * @memberof EditForm
   */
  @Bind()
  showEditModal(record) {
    this.setState({
      itemData: { ...record },
      messageVisible: true,
    });
  }

  /**
   * 关闭模态框
   *
   * @memberof EditForm
   */
  @Bind()
  hideModal() {
    this.setState({
      messageVisible: false,
      itemData: {},
    });
  }

  /**
   * 新增消息
   *
   * @param {*} fieldsValue
   * @memberof EditForm
   */
  @Bind()
  handleAdd(fieldsValue) {
    const { dispatch, match } = this.props;
    const { event } = this.state;
    const data = {
      tenantId: event.tenantId,
      eventId: match.params.id,
      eventCode: event.eventCode,
      ...fieldsValue,
    };
    dispatch({
      type: 'event/createMessage',
      payload: data,
    }).then((res) => {
      if (res) {
        this.hideModal();
        notification.success();
        this.queryMessageList();
      }
    });
  }

  /**
   * 更新消息
   *
   * @param {*} fieldsValue
   * @memberof EditForm
   */
  @Bind()
  handleEdit(fieldsValue) {
    const { dispatch } = this.props;
    const { event } = this.state;
    const { objectVersionNumber, eventId } = this.state.itemData;
    const data = {
      tenantId: event.tenantId,
      eventId,
      objectVersionNumber,
      ...fieldsValue,
    };
    dispatch({
      type: 'event/updateMessage',
      payload: data,
    }).then((res) => {
      if (res) {
        this.hideModal();
        notification.success();
        this.queryMessageList();
      }
    });
  }

  /**
   * 批量删除消息
   *
   * @memberof EditForm
   */
  @Bind()
  deleteEventMessage() {
    const {
      dispatch,
      tenantId,
      event: { messageList = {} },
    } = this.props;
    const { content } = messageList;
    const { selectedRowKeys } = this.state;
    const newMessageList = filter(content, (item) => {
      return selectedRowKeys.indexOf(item.messageEventId) >= 0;
    });
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk: () => {
        dispatch({
          type: 'event/deleteMessages',
          payload: {
            tenantId,
            messageEvents: newMessageList,
          },
        }).then((res) => {
          if (res) {
            notification.success();
            this.queryMessageList();
            this.setState({
              selectedRowKeys: [],
            });
          }
        });
      },
    });
  }

  /**
   * 获取删除选中行
   *
   * @param {*} selectedRowKeys
   * @memberof EditForm
   */
  @Bind()
  handleRowSelectChange(selectedRowKeys) {
    this.setState({ selectedRowKeys });
  }

  render() {
    const {
      isSiteFlag,
      form,
      match,
      saving,
      getEventLoading,
      getMessageListLoading,
      tenantId: currentTenantId,
      event: { messageList = {}, messageTypeCode = [] },
    } = this.props;
    const {
      selectedRowKeys,
      messageVisible,
      currentDetailTenantId,
      itemData = {},
      event: { tenantName } = {},
    } = this.state;

    const basePath = match.path.substring(0, match.path.indexOf('/message'));

    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const messageProps = {
      currentDetailTenantId,
      messageVisible,
      itemData,
      messageTypeCode,
      saving,
      onOk: this.handleAdd,
      onEditOk: this.handleEdit,
      onCancel: this.hideModal,
    };
    // 是否预定义按钮控制
    const isPredefined = currentTenantId !== currentDetailTenantId;

    const columns = [
      {
        title: intl.get('hpfm.event.model.eventMessage.receiverTypeId').d('接收组'),
        dataIndex: 'receiverTypeName',
        width: 200,
      },
      {
        title: intl.get('hpfm.event.model.eventMessage.messageName').d('消息发送配置'),
        dataIndex: 'messageName',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        render: (_, record) => {
          const operators = [];
          if (isSiteFlag || !isPredefined) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '事件消息界面-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.showEditModal(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
            return operatorRender(operators);
          }
        },
      },
    ];

    return (
      <React.Fragment>
        <Header
          title={intl.get('hpfm.event.view.message.title').d('事件消息')}
          backPath={`${basePath}/list`}
        />
        <Content>
          <Card
            key="event-header"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hpfm.event.view.title.event').d('事件规则')}</h3>}
            loading={getEventLoading}
          >
            <Form className={styles['event-message-form']} layout="inline">
              <Row {...SEARCH_FORM_ROW_LAYOUT}>
                {isSiteFlag && (
                  <Col {...FORM_COL_4_LAYOUT}>
                    <Form.Item
                      className={styles['event-message-form-field-view']}
                      label={intl.get('hzero.common.model.common.tenantId').d('租户')}
                    >
                      {tenantName}
                    </Form.Item>
                  </Col>
                )}
                <Col {...FORM_COL_4_LAYOUT}>
                  <Form.Item
                    className={styles['event-message-form-field-view']}
                    label={intl.get('hpfm.event.model.event.code').d('事件编码')}
                  >
                    {form.getFieldValue('eventCode')}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_4_LAYOUT}>
                  <Form.Item
                    className={styles['event-message-form-field-view']}
                    label={intl.get('hzero.common.status').d('状态')}
                  >
                    {enableRender(form.getFieldValue('enabledFlag'))}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_4_LAYOUT} span={12}>
                  <Form.Item
                    className={styles['event-message-form-field-view']}
                    label={intl.get('hpfm.event.model.event.description').d('事件描述')}
                  >
                    {form.getFieldValue('eventDescription')}
                  </Form.Item>
                </Col>
              </Row>
            </Form>
          </Card>
          <Card
            key="event-message"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={<h3>{intl.get('hpfm.event.view.title.eventMessage').d('事件消息')}</h3>}
          >
            {(isSiteFlag || !isPredefined) && (
              <div className="table-list-operator">
                <ButtonPermission
                  type="primary"
                  permissionList={[
                    {
                      code: `${match.path}.button.create`,
                      type: 'button',
                      meaning: '事件消息界面-新增消息',
                    },
                  ]}
                  onClick={this.showCreateModal}
                >
                  {intl.get('hpfm.event.view.message.button.create').d('新增消息')}
                </ButtonPermission>
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${match.path}.button.remove`,
                      type: 'button',
                      meaning: '事件消息界面-删除消息',
                    },
                  ]}
                  onClick={this.deleteEventMessage}
                  disabled={selectedRowKeys.length === 0}
                >
                  {intl.get('hpfm.event.view.message.button.remove').d('删除消息')}
                </ButtonPermission>
              </div>
            )}
            <Table
              bordered
              pagination={false}
              rowKey="messageEventId"
              rowSelection={rowSelection}
              loading={getMessageListLoading}
              dataSource={messageList.content}
              columns={columns}
            />
          </Card>
        </Content>
        <EventMessageForm {...messageProps} />
      </React.Fragment>
    );
  }
}
