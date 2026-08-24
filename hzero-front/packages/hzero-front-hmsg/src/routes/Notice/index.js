/**
 * notice - 公告管理
 * @date: 2018-9-20
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Link } from 'dva/router';
import {
  Button,
  Col,
  DatePicker,
  Form,
  Icon,
  Input,
  Popconfirm,
  Row,
  Select,
  Table,
  Tabs,
} from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import { Content, Header } from 'components/Page';
import Checkbox from 'components/Checkbox';
import cacheComponent from 'components/CacheComponent';
import { Button as ButtonPermission } from 'components/Permission';

import { dateTimeRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import {
  getCurrentOrganizationId,
  getDateTimeFormat,
  isTenantRoleLevel,
  tableScrollWidth,
} from 'utils/utils';
import {
  DEFAULT_DATETIME_FORMAT,
  // DEFAULT_DATE_FORMAT,
  DEFAULT_TIME_FORMAT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

import SystemNoticePublishDrawer from './SystemNoticePublishDrawer';

const FormItem = Form.Item;
const { TabPane } = Tabs;
const { Option } = Select;

@connect(({ loading, hmsgNotice }) => ({
  hmsgNotice,
  organizationId: getCurrentOrganizationId(),
  isTenantRole: isTenantRoleLevel(),
  publicLoading: loading.effects['hmsgNotice/publicNotice'],
  fetchNoticeLoading: loading.effects['hmsgNotice/fetchNotice'],
  removeDraftReceiveLoading: loading.effects['hmsgNotice/removeDraftReceive'],
  querySystemHistoryLoading: loading.effects['hmsgNotice/querySystemHistory'],
  queryReceiverLoading: loading.effects['hmsgNotice/queryReceiver'],
  createReceiverLoading: loading.effects['hmsgNotice/createReceiver'],
  publishSystemNoticeLoading: loading.effects['hmsgNotice/publishSystemNotice'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hmsg.notice', 'entity.lang', 'hmsg.common'] })
@cacheComponent({ cacheKey: '/hmsg/notices/list' })
export default class Notice extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      isExpendSearch: false,
      actionNoticeId: '',
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'hmsgNotice/init',
    });
    this.fetchNotice({
      containsDeletedDataFlag: 1,
    });
  }

  /**
   * @function fetchEmail - 获取公告列表数据
   * @param {object} params - 查询参数
   * @param {number} params.page - 页码
   * @param {number} params.size - 页数
   */
  fetchNotice(params = {}) {
    const {
      dispatch,
      form,
      organizationId,
      hmsgNotice: { pagination = {} },
    } = this.props;
    // 格式化时间
    const { fromDate, toDate, publishedDateFrom, publishedDateTo } = form.getFieldsValue();
    const dateParams = {
      fromDate: fromDate && moment(fromDate).format(DEFAULT_DATETIME_FORMAT),
      toDate: toDate && moment(toDate).format(DEFAULT_DATETIME_FORMAT),
      publishedDateFrom:
        publishedDateFrom && moment(publishedDateFrom).format(DEFAULT_DATETIME_FORMAT),
      publishedDateTo: publishedDateTo && moment(publishedDateTo).format(DEFAULT_DATETIME_FORMAT),
    };
    dispatch({
      type: 'hmsgNotice/fetchNotice',
      payload: {
        organizationId,
        page: pagination,
        ...form.getFieldsValue(),
        ...dateParams,
        ...params,
      },
    });
  }

  /**
   * @function handleCreate - 新建
   */
  @Bind()
  handleCreate() {
    const { history } = this.props;
    history.push('/hmsg/notices/detail/create');
  }

  /**
   * @function handlePagination - 分页操作
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchNotice({
      page: pagination,
    });
  }

  /**
   * @function handleExpendSearch - 显示高级查询条件
   * @param {boolean} flag - 显示高级查询标识
   */
  @Bind()
  handleExpendSearch() {
    const { isExpendSearch } = this.state;
    this.setState({ isExpendSearch: !isExpendSearch });
  }

  /**
   * 重置查询表单
   */
  @Bind()
  handleResetSearch() {
    const { form } = this.props;
    const { receiverTypeCode = '' } = form.getFieldsValue() || {};
    form.resetFields();
    form.setFieldsValue({ receiverTypeCode });
  }

  /**
   * @function handleSearch - 搜索公告
   */
  @Bind()
  handleSearch() {
    this.fetchNotice({ page: {} });
  }

  /**
   * @function handleNoticeTypeChange - 切换类别
   * @param {*} e - 事件对象
   */
  @Bind()
  handleNoticeTypeChange(key) {
    this.fetchNotice({ receiverTypeCode: key });
  }

  /**
   * @function handlePublicNotice - 发布公告信息
   * @param {string} organizationId - 租户ID
   * @param {object} record - 公告信息行数据
   * @param {string} record.noticeId - 公告信息ID
   */
  @Bind()
  handlePublicNotice(record) {
    const { dispatch, organizationId } = this.props;
    this.setState({ actionNoticeId: record.noticeId });
    if (record.receiverTypeCode === 'NOTIFY' || record.receiverTypeCode === 'ANNOUNCE') {
      dispatch({
        type: 'hmsgNotice/updateState',
        payload: {
          systemNoticePublishModalVisible: true,
          systemNoticePublishRecord: record,
        },
      });
    } else {
      dispatch({
        type: 'hmsgNotice/publicNotice',
        payload: { organizationId, noticeId: record.noticeId },
      }).then((res) => {
        if (res) {
          notification.success();
          this.fetchNotice();
        }
      });
    }
  }

  /**
   * @function handleRevokeNotice - 撤销删除公告信息
   * @param {string} organizationId - 租户ID
   * @param {object} record - 公告信息行数据
   * @param {string} record.noticeId - 公告信息ID
   */
  @Bind()
  handleRevokeNotice(record) {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'hmsgNotice/revokeNotice',
      payload: { organizationId, noticeId: record.noticeId, record },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchNotice({
          containsDeletedDataFlag: 1,
        });
      }
    });
  }

  /**
   * @function handleDeleteNotice - 删除公告信息
   * @param {string} organizationId - 租户ID
   * @param {object} record - 公告信息行数据
   * @param {string} record.noticeId - 公告信息ID
   */
  @Bind()
  handleDeleteNotice(record) {
    const { dispatch, organizationId } = this.props;
    dispatch({
      type: 'hmsgNotice/deleteNotice',
      payload: { organizationId, ...record },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchNotice();
      }
    });
  }

  /**
   * @function renderFilterForm - 渲染筛选查询表单
   */
  @Bind()
  renderFilterForm() {
    const {
      form,
      hmsgNotice: { noticeReceiverType = [], noticeCategory = [], noticeStatus = [] },
    } = this.props;
    const { getFieldDecorator } = form;
    const { receiverTypeCode = '' } = form.getFieldsValue() || {};
    const { isExpendSearch } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row>
          <Col>
            <FormItem>
              {getFieldDecorator('receiverTypeCode', {
                initialValue: '',
              })(
                <Tabs
                  onChange={this.handleNoticeTypeChange}
                  defaultActiveKey=""
                  activeKey={receiverTypeCode}
                  animated={false}
                >
                  <TabPane
                    key=""
                    tab={intl.get('hmsg.notice.model.notice.receiverTypeCode.All').d('全部公告')}
                  />
                  {noticeReceiverType.map((item) => (
                    <TabPane tab={item.meaning} key={item.value} />
                  ))}
                </Tabs>
              )}
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col>
            <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={12} align="bottom">
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  label={intl.get('hmsg.notice.model.notice.title').d('公告标题')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('title')(<Input style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  label={intl.get('hmsg.notice.model.notice.noticeCategoryCode').d('公告类别')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('noticeCategoryCode')(
                    <Select allowClear style={{ width: '100%' }}>
                      {noticeCategory.map((item) => (
                        <Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  label={intl.get('hmsg.notice.model.notice.statusCode').d('公告状态')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('statusCode')(
                    <Select allowClear style={{ width: '100%' }}>
                      {noticeStatus.map((item) => (
                        <Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
                <FormItem>
                  <Button onClick={this.handleExpendSearch}>
                    {isExpendSearch
                      ? intl.get('hzero.common.button.collected').d('收起查询')
                      : intl.get('hzero.common.button.viewMore').d('更多查询')}
                  </Button>
                  <Button onClick={this.handleResetSearch}>
                    {intl.get('hzero.common.button.reset').d('重置')}
                  </Button>
                  <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                    {intl.get('hzero.common.button.search').d('查询')}
                  </Button>
                </FormItem>
              </Col>
            </Row>
            <Row
              {...SEARCH_FORM_ROW_LAYOUT}
              style={{ display: isExpendSearch ? '' : 'none' }}
              type="flex"
              gutter={12}
              align="bottom"
            >
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  label={intl.get('hmsg.notice.model.notice.noticeContent').d('公告内容')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('noticeBody')(<Input style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  label={intl.get('hmsg.notice.view.notice.creationFrom').d('创建时间从')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('fromDate')(
                    <DatePicker
                      style={{ width: '100%' }}
                      placeholder=""
                      format={getDateTimeFormat()}
                      showTime={{ format: DEFAULT_TIME_FORMAT }}
                      disabledDate={(currentDate) =>
                        form.getFieldValue('toDate') &&
                        moment(form.getFieldValue('toDate')).isBefore(currentDate, 'day')
                      }
                    />
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  label={intl.get('hmsg.notice.view.notice.creationTo').d('创建时间至')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('toDate')(
                    <DatePicker
                      style={{ width: '100%' }}
                      placeholder=""
                      format={getDateTimeFormat()}
                      showTime={{ format: DEFAULT_TIME_FORMAT }}
                      disabledDate={(currentDate) =>
                        form.getFieldValue('fromDate') &&
                        moment(form.getFieldValue('fromDate')).isAfter(currentDate, 'day')
                      }
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
          </Col>
        </Row>
        <Row
          {...SEARCH_FORM_ROW_LAYOUT}
          style={{ display: isExpendSearch ? '' : 'none' }}
          type="flex"
          gutter={12}
          align="bottom"
        >
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hzero.common.date.release.from').d('发布日期从')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('publishedDateFrom')(
                <DatePicker
                  style={{ width: '100%' }}
                  placeholder=""
                  format={getDateTimeFormat()}
                  disabledDate={(currentDate) =>
                    form.getFieldValue('publishedDateTo') &&
                    moment(form.getFieldValue('publishedDateTo')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hzero.common.date.release.to').d('发布日期至')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('publishedDateTo')(
                <DatePicker
                  style={{ width: '100%' }}
                  placeholder=""
                  format={getDateTimeFormat()}
                  disabledDate={(currentDate) =>
                    form.getFieldValue('publishedDateFrom') &&
                    moment(form.getFieldValue('publishedDateFrom')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col offset={20} {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hmsg.notice.model.notice.containsDeletedDataFlag').d('显示已删除')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('containsDeletedDataFlag', {
                initialValue: 1,
              })(<Checkbox />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  // SystemNoticePublicModal
  /**
   *
   */
  @Bind()
  handleSystemNoticeModalOk() {
    this.closeSystemNoticeModal();
  }

  /**
   *
   */
  @Bind()
  handleSystemNoticeModalCancel() {
    this.closeSystemNoticeModal();
  }

  /**
   * 关闭 System 发布 模态框, 会重置 所有信息
   */
  closeSystemNoticeModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'hmsgNotice/updateState',
      payload: {
        systemNoticePublishModalVisible: false, // 发送系统消息公告
        systemNoticePublishRecord: {}, // 发送当前系统消息公告的记录
        systemNoticeHistoryDataSource: [], // 历史信息 数据
        systemNoticeHistoryPagination: false, // 历史信息 分页
        systemNoticeHistorySelectedRows: [], // 历史信息 选中数据
        systemNoticeHistorySelectedRowKeys: [], // 历史信息 选中数据key
        systemNoticeReceiveDataSource: [], // 接收信息 数据
        systemNoticeReceivePagination: false, // 接收信息 分页
      },
    });
  }

  // SystemNoticePublishModal 相关的 model 调用
  @Bind()
  removeDraftReceive(payload) {
    const { dispatch } = this.props;
    return dispatch({ type: 'hmsgNotice/removeDraftReceive', payload });
  }

  @Bind()
  querySystemHistory(payload) {
    const { dispatch } = this.props;
    return dispatch({ type: 'hmsgNotice/querySystemHistory', payload });
  }

  @Bind()
  queryReceiver(payload) {
    const {
      dispatch,
      hmsgNotice: { systemNoticeHistorySelectedRowKeys = [] },
    } = this.props;
    return dispatch({
      type: 'hmsgNotice/queryReceiver',
      payload: {
        ...payload,
        prevSystemNoticeHistorySelectedRowKeys: systemNoticeHistorySelectedRowKeys,
      },
    });
  }

  @Bind()
  createReceiver(payload) {
    const { dispatch } = this.props;
    return dispatch({ type: 'hmsgNotice/createReceiver', payload });
  }

  @Bind()
  publishSystemNotice(payload) {
    const { dispatch } = this.props;
    return dispatch({ type: 'hmsgNotice/publishSystemNotice', payload });
  }

  render() {
    const {
      fetchNoticeLoading,
      publicLoading,
      match: { path },
      hmsgNotice: {
        noticeList = [],
        pagination = {},
        systemNoticePublishModalVisible = false,
        systemNoticePublishRecord,
        systemNoticeHistoryDataSource,
        systemNoticeHistoryPagination,
        systemNoticeHistorySelectedRows,
        systemNoticeHistorySelectedRowKeys,
        systemNoticeReceiveDataSource,
        systemNoticeReceivePagination,
        receiverRecordType = [],
      },
      querySystemHistoryLoading,
      queryReceiverLoading,
      createReceiverLoading,
      publishSystemNoticeLoading,
      removeDraftReceiveLoading,
      isTenantRole,
      organizationId,
    } = this.props;
    const { actionNoticeId } = this.state;
    const columns = [
      {
        title: intl.get('hmsg.notice.model.notice.receiverType').d('发布类别'),
        width: 100,
        dataIndex: 'receiverTypeMeaning',
      },
      {
        title: intl.get('hmsg.notice.model.notice.title').d('公告标题'),
        dataIndex: 'title',
      },
      {
        title: intl.get('hmsg.notice.model.notice.publishedByUser').d('发布人'),
        width: 150,
        dataIndex: 'publishedByUser',
      },
      {
        title: intl.get('hmsg.notice.model.notice.statusCode').d('公告状态'),
        width: 100,
        dataIndex: 'statusMeaning',
      },
      {
        title: intl.get('hmsg.notice.model.notice.publishedDate').d('发布时间'),
        width: 200,
        dataIndex: 'publishedDate',
        render: dateTimeRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        fixed: 'right',
        render: (text, record) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <Link to={`/hmsg/notices/detail/${record.noticeId}`}>
                {intl.get('hzero.common.button.edit').d('编辑')}
              </Link>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          if (record.statusCode === 'DELETED') {
            operators.push({
              key: 'revoke',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.revoke2`,
                      type: 'button',
                      meaning: '公告管理-撤销',
                    },
                  ]}
                  onClick={() => this.handleRevokeNotice(record)}
                >
                  {intl.get('hzero.common.status.revoke').d('撤销')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.status.revoke').d('撤销'),
            });
          } else if (publicLoading && record.noticeId === actionNoticeId) {
            operators.push({
              key: 'loading',
              ele: <Icon type="loading" style={{ marginLeft: 20 }} />,
              len: 2,
            });
          } else {
            operators.push({
              key: 'release',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.release2`,
                      type: 'button',
                      meaning: '公告管理-发布',
                    },
                  ]}
                  onClick={() => this.handlePublicNotice(record)}
                >
                  {intl.get('hzero.common.button.release').d('发布')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.release').d('发布'),
            });
          }
          operators.push({
            key: 'delete',
            ele: (
              <Popconfirm
                title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                onConfirm={() => {
                  this.handleDeleteNotice(record);
                }}
              >
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '公告管理-删除',
                    },
                  ]}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              </Popconfirm>
            ),
            len: 2,
            title: intl.get('hzero.common.button.delete').d('删除'),
          });
          return operatorRender(operators);
        },
      },
    ];
    return (
      <>
        <Header title={intl.get('hmsg.notice.view.message.title.list').d('公告管理')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '公告管理-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">{this.renderFilterForm()}</div>
          <Table
            bordered
            rowKey="noticeId"
            loading={fetchNoticeLoading || publicLoading}
            dataSource={noticeList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          {systemNoticePublishModalVisible && (
            <SystemNoticePublishDrawer
              visible={systemNoticePublishModalVisible}
              record={systemNoticePublishRecord}
              receiverRecordType={receiverRecordType}
              onOk={this.handleSystemNoticeModalOk}
              onCancel={this.handleSystemNoticeModalCancel}
              querySystemHistory={this.querySystemHistory}
              queryReceiver={this.queryReceiver}
              createReceiver={this.createReceiver}
              publishSystemNotice={this.publishSystemNotice}
              removeDraftReceive={this.removeDraftReceive}
              querySystemHistoryLoading={querySystemHistoryLoading}
              queryReceiverLoading={queryReceiverLoading}
              createReceiverLoading={createReceiverLoading}
              publishSystemNoticeLoading={publishSystemNoticeLoading}
              removeDraftReceiveLoading={removeDraftReceiveLoading}
              systemNoticeHistoryDataSource={systemNoticeHistoryDataSource}
              systemNoticeHistoryPagination={systemNoticeHistoryPagination}
              systemNoticeHistorySelectedRows={systemNoticeHistorySelectedRows}
              systemNoticeHistorySelectedRowKeys={systemNoticeHistorySelectedRowKeys}
              systemNoticeReceiveDataSource={systemNoticeReceiveDataSource}
              systemNoticeReceivePagination={systemNoticeReceivePagination}
              organizationId={organizationId}
              isTenantRole={isTenantRole}
            />
          )}
        </Content>
      </>
    );
  }
}
