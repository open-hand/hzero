/**
 * MessageQuery - 消息查询列表
 * @date: 2018-7-29
 * @author: CJ <juan.chen@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { isTenantRoleLevel } from 'utils/utils';

import ContentView from './ContentView';
import RecipientView from './RecipientView';
import ListTable from './ListTable';
import QueryForm from './QueryForm';

/**
 * 消息查询数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} formValues - 查询表单值
 * @reactProps {Object} recordData - 表格中信息的一条记录
 * @return React.element
 */

function getRefFieldsValue(ref) {
  if (ref.current) {
    return ref.current.props.form.getFieldsValue();
  }
  return {};
}

@connect(({ messageQuery, loading }) => ({
  messageQuery,
  tenantRoleLevel: isTenantRoleLevel(),
  queryMessageLoading: loading.effects['messageQuery/queryMessageList'],
  queryRecipientLoading: loading.effects['messageQuery/queryRecipient'],
  queryContentLoading: loading.effects['messageQuery/queryContent'],
  queryErrorLoading: loading.effects['messageQuery/queryError'],
  deleteLoading: loading.effects['messageQuery/deleteMessage'],
  resendLoading: loading.effects['messageQuery/resendMessage'],
}))
@formatterCollections({ code: ['hmsg.messageQuery', 'entity.tenant', 'hmsg.common'] })
export default class MessageQuery extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      formValues: {},
      contentVisible: false, // 内容和错误模态框是否可见
      recipientVisible: false, // 收件人模态框是否可见
      isContent: true, // 是否为内容
      recordData: {},
      selectedRows: [], // 选择的行数据
    };
    this.multiSearchFormRef = React.createRef();
  }

  /**
   * 初始化数据
   *
   * @memberof MessageQuery
   */
  componentDidMount() {
    this.handleQueryMessage();
    this.props.dispatch({
      type: 'messageQuery/init',
    });
  }

  /**
   * 获取消息列表
   *
   * @param {*} [params={}]
   * @memberof MessageQuery
   */
  @Bind()
  handleQueryMessage(params = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'messageQuery/queryMessageList',
      payload: params,
    });
  }

  /**
   * 点击内容查看模态框
   *
   * @param {*} record
   * @memberof MessageQuery
   */
  @Bind()
  handleOpenContentModal(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'messageQuery/queryContent',
      payload: record.messageId,
    });
    this.setState({
      isContent: true,
      contentVisible: true,
    });
  }

  /**
   * 确认内容和错误模态框
   *
   * @memberof MessageQuery
   */
  @Bind()
  handleOk() {
    const { isContent } = this.state;
    this.setState({
      contentVisible: false,
      isContent: !isContent,
    });
  }

  /**
   * 收件人查看数据
   *
   * @param {*} record
   * @memberof MessageQuery
   */
  @Bind()
  handleOpenRecipientModal(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'messageQuery/queryRecipient',
      payload: record,
    });
    this.setState({
      recipientVisible: true,
      recordData: record,
    });
  }

  /**
   * 确认收件人模态框
   *
   * @memberof MessageQuery
   */
  @Bind()
  handleRecipientOk() {
    this.setState({
      recipientVisible: false,
    });
  }

  /**
   * 点击错误查看模态框
   *
   * @param {*} record
   * @memberof MessageQuery
   */
  @Bind()
  handleOpenErrorModal(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'messageQuery/queryError',
      payload: record.transactionId,
    });
    this.setState({
      isContent: false,
      contentVisible: true,
    });
  }

  /**
   * 重试
   *
   * @param {*} record
   * @memberof MessageQuery
   */
  @Bind()
  handleResendMessage(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'messageQuery/resendMessage',
      payload: record.transactionId,
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  /**
   * 获取表单的值
   *
   * @param {*} values
   * @memberof MessageQuery
   */
  @Bind()
  storeFormValues(values) {
    this.setState({
      formValues: { ...values },
    });
  }

  /**
   * 表单查询
   */
  @Bind()
  handleMultiSearchFormSearch() {
    const fieldsValue = getRefFieldsValue(this.multiSearchFormRef);
    let values = fieldsValue;
    values = {
      startDate: fieldsValue.startDate
        ? fieldsValue.startDate.format(DEFAULT_DATETIME_FORMAT)
        : undefined,
      endDate: fieldsValue.endDate
        ? fieldsValue.endDate.format(DEFAULT_DATETIME_FORMAT)
        : undefined,
    };
    this.handleQueryMessage({ ...fieldsValue, ...values });
  }

  @Bind()
  handleSelectChange(rows) {
    this.setState({ selectedRows: rows });
  }

  @Bind()
  handleDelete() {
    const { dispatch } = this.props;
    const { selectedRows = [] } = this.state;
    dispatch({
      type: 'messageQuery/deleteMessage',
      payload: selectedRows,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleMultiSearchFormSearch();
      }
    });
  }

  @Bind()
  handlePageChange(pagination = {}, fieldsValue) {
    this.handleQueryMessage({
      page: pagination,
      ...fieldsValue,
    });
  }

  render() {
    const {
      queryMessageLoading,
      queryRecipientLoading,
      queryContentLoading,
      queryErrorLoading,
      resendLoading,
      deleteLoading = false,
      match: { path },
      messageQuery: {
        messageData = {},
        content = {},
        recipientData = {},
        error = {},
        statusList = [],
        messageTypeList = [],
      },
      tenantRoleLevel,
    } = this.props;
    const {
      contentVisible,
      recipientVisible,
      isContent,
      selectedRows = [],
      recordData = {},
      formValues = {},
    } = this.state;
    const formProps = {
      messageTypeList,
      statusList,
      tenantRoleLevel,
      onQueryMessage: this.handlePageChange,
      onStoreFormValues: this.storeFormValues,
      wrappedComponentRef: this.multiSearchFormRef,
      onSearch: this.handleMultiSearchFormSearch,
    };
    const tableProps = {
      messageData,
      formValues,
      tenantRoleLevel,
      loading: queryMessageLoading,
      onSelectChange: this.handleSelectChange,
      onOpenRecipientModal: this.handleOpenRecipientModal,
      onOpenContentModal: this.handleOpenContentModal,
      onOpenErrorModal: this.handleOpenErrorModal,
      onQueryMessage: this.handleQueryMessage,
      onResendMessage: this.handleResendMessage,
      path,
      resendLoading,
    };
    const contentViewProps = {
      isContent,
      contentVisible,
      content,
      error,
      loading: queryContentLoading || queryErrorLoading || false,
      onOk: this.handleOk,
      path,
    };
    const recipientViewProps = {
      recipientVisible,
      recipientData,
      recordData,
      loading: queryRecipientLoading,
      onOk: this.handleRecipientOk,
      onOpenRecipientModal: this.handleOpenRecipientModal,
      path,
    };
    return (
      <>
        <Header title={intl.get('hmsg.messageQuery.view.message.title').d('消息查询')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '消息查询-删除',
              },
            ]}
            type="primary"
            icon="delete"
            onClick={this.handleDelete}
            disabled={selectedRows.length === 0}
            loading={deleteLoading}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <QueryForm {...formProps} />
          <ListTable {...tableProps} />
        </Content>
        <ContentView {...contentViewProps} />
        <RecipientView {...recipientViewProps} />
      </>
    );
  }
}
