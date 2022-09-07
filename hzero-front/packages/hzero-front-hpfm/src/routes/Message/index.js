/**
 * Message API返回消息管理
 * @date: 2019-1-9
 * @author: guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { openTab } from 'utils/menuTab';
import { operatorRender } from 'utils/renderer';
import { HZERO_PLATFORM } from 'utils/config';

import MessageDrawer from './MessageDrawer';
import FilterForm from './FilterForm';

@connect(({ loading, message, user }) => ({
  message,
  user,
  fetchMessageLoading: loading.effects['message/fetchMessageList'],
  saving: loading.effects['message/createMessage'],
  getMessageLoading: loading.effects['message/getMessageDetail'],
  updateMessageLoading: loading.effects['message/updateMessage'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hpfm.message'] })
export default class Message extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      fieldsValue: {},
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'message/init' });
    this.fetchMessageList();
  }

  /**
   * 获取消息列表
   * @param {object} params
   * @memberof Message
   */
  @Bind()
  fetchMessageList(params = {}) {
    const {
      dispatch,
      message: { pagination = {} },
    } = this.props;
    const { fieldsValue } = this.state;
    dispatch({
      type: 'message/fetchMessageList',
      payload: { ...fieldsValue, page: pagination, ...params },
    });
  }

  /**
   * 编辑打开模态框
   */
  @Bind()
  handleUpdateMessage(record) {
    const { dispatch } = this.props;
    this.handleModalVisible(true);
    dispatch({
      type: 'message/updateState',
      payload: {
        messageDetail: {},
      },
    });
    dispatch({
      type: 'message/getMessageDetail',
      payload: { messageId: record.messageId },
    });
  }

  /**
   * 保存消息
   */
  @Bind()
  handleSaveMessage(fieldsValue) {
    const {
      dispatch,
      message: { messageDetail = {} },
    } = this.props;
    dispatch({
      type: `message/${messageDetail.messageId !== undefined ? 'updateMessage' : 'createMessage'}`,
      payload: { ...messageDetail, ...fieldsValue },
    }).then((res) => {
      if (res) {
        notification.success();
        this.hideModal();
        this.fetchMessageList();
      }
    });
  }

  /**
   * 删除
   */
  @Bind()
  handleDeleteMessage(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'message/deleteMessage',
      payload: { ...record, messageId: record.messageId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchMessageList();
      }
    });
  }

  /**
   * 分页
   */
  @Bind()
  handleTableChange(pagination) {
    this.fetchMessageList({
      page: pagination,
    });
  }

  /**
   * 查询消息
   */
  @Bind()
  handleSearch(form) {
    const fieldsValue = form.getFieldsValue();
    this.setState({ fieldsValue });
    this.fetchMessageList({ ...fieldsValue, page: {} });
  }

  /**
   * 重置表单
   */
  @Bind()
  handleResetSearch(form) {
    this.setState({ fieldsValue: {} });
    form.resetFields();
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}) {
    const { dispatch } = this.props;
    if (record.messageId !== undefined) {
      dispatch({
        type: 'message/getMessageDetail',
        payload: { messageId: record.messageId },
      });
    }
    dispatch({
      type: 'message/updateState',
      payload: { messageDetail: {} },
    });
    this.handleModalVisible(true);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  /**
   * 导入返回消息
   */
  @Bind()
  handleImport() {
    openTab({
      key: `/hpfm/prompt/import-data/HPFM.MESSAGE`,
      title: 'hzero.common.button.import',
      search: queryString.stringify({
        action: 'hzero.common.button.import',
        prefixPatch: HZERO_PLATFORM,
      }),
    });
  }

  render() {
    const {
      updateMessageLoading = false,
      fetchMessageLoading = false,
      getMessageLoading = false,
      saving = false,
      match,
      message: {
        messageList = [],
        pagination = {},
        languageList = [],
        messageDetail = {},
        messageType = [],
      },
    } = this.props;
    const { modalVisible, detailFirstLoadLoading } = this.state;
    const messageColumns = [
      {
        title: intl.get('hpfm.message.model.message.code').d('消息编码'),
        width: 200,
        dataIndex: 'code',
      },
      {
        title: intl.get('hpfm.message.model.message.type').d('消息类型'),
        width: 100,
        dataIndex: 'type',
      },
      {
        title: intl.get('hpfm.message.model.message.lang').d('语言'),
        width: 100,
        dataIndex: 'langMeaning',
      },
      {
        title: intl.get('hpfm.message.model.message.description').d('消息描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        fixed: 'right',
        dataIndex: 'edit',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '返回消息管理-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleUpdateMessage(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => {
                    this.handleDeleteMessage(record);
                  }}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.delete`,
                        type: 'button',
                        meaning: '返回消息管理-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    const scroll = {
      x: 549,
    };
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.message.view.message.title').d('返回消息管理')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '返回消息管理-新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.Import`,
                type: 'button',
                meaning: '返回消息管理-导入',
              },
            ]}
            icon="to-top"
            onClick={this.handleImport.bind(this)}
          >
            {intl.get('hzero.common.button.import').d('导入')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm
            messageType={messageType}
            languageList={languageList}
            search={this.handleSearch}
            reset={this.handleResetSearch}
          />
          <Table
            bordered
            rowKey="messageId"
            loading={fetchMessageLoading}
            dataSource={messageList}
            columns={messageColumns}
            pagination={pagination}
            onChange={this.handleTableChange}
            scroll={scroll}
          />
          <MessageDrawer
            title={
              messageDetail.messageId
                ? intl.get('hpfm.message.view.message.edit').d('编辑消息')
                : intl.get('hpfm.message.view.message.create').d('新建消息')
            }
            initLoading={getMessageLoading}
            loading={messageDetail.messageId !== undefined ? updateMessageLoading : saving}
            modalVisible={modalVisible}
            initData={messageDetail}
            languageList={languageList}
            messageType={messageType}
            onCancel={this.hideModal}
            onOk={this.handleSaveMessage}
            detailFirstLoadLoading={detailFirstLoadLoading}
          />
        </Content>
      </React.Fragment>
    );
  }
}
