/*
 * @Description: Event - 事件定义
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-03-26 15:04:34
 * @Copyright: Copyright (c) 2020, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { Modal as ProModal } from 'choerodon-ui/pro';

import { Button as ButtonPermission } from 'components/Permission';
import { Content, Header } from 'components/Page';
import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import { enableRender } from 'utils/renderer';

import EditModal from './EditModal';
import FilterForm from './FilterForm';
import './style.module.less';

@connect(({ loading, event }) => ({
  event,
  tenantId: getCurrentOrganizationId(),
  fetchDataLoading: loading.effects['event/fetchEventData'],
  createLoading: loading.effects['event/createEvent'],
}))
@formatterCollections({ code: ['hevt.event', 'hevt.common'] })
export default class event extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      eventFormData: {},
    };
  }

  filterForm;

  /**
   * 组件挂载后执行方法
   */
  componentDidMount() {
    this.fetchEventData();
  }

  /**
   * 获取事件定义信息
   * @param {object} params 传递的参数
   */
  fetchEventData(params = {}) {
    const {
      dispatch,
      event: { pagination = {} },
      tenantId,
    } = this.props;
    const filterValue = this.filterForm === undefined ? {} : this.filterForm.getFieldsValue();
    dispatch({
      type: 'event/fetchEventData',
      payload: { tenantId, ...filterValue, page: pagination, ...params },
    });
  }

  /**
   * 获取查询表单组件this对象
   * @param {object} ref - 查询表单组件this
   */
  @Bind
  handleBindRef(ref) {
    this.filterForm = (ref.props || {}).form;
  }

  @Bind()
  handleSearch() {
    const { dispatch } = this.props;
    const queryData = this.filterForm === undefined ? {} : this.filterForm.getFieldsValue();
    dispatch({
      type: 'event/updateState',
      payload: { queryData },
    });
    this.fetchEventData({ page: {} });
  }

  /**
   * 重置表单查询条件
   */
  @Bind()
  handleResetSearch() {
    const { dispatch } = this.props;
    dispatch({
      type: 'event/updateState',
      payload: { queryData: {} },
    });
    this.filterForm.resetFields();
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    const { dispatch } = this.props;
    dispatch({
      type: 'event/updateState',
      payload: {
        modalVisible: !!flag,
      },
    });
  }

  /**
   * 展示新建窗口
   */
  @Bind()
  showModal() {
    this.setState({
      eventFormData: {},
    });
    this.handleModalVisible(true);
  }

  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  /**
   * 保存事件信息
   * @param {object} fieldsValue 传递的fieldsValue
   */
  @Bind()
  handleSaveEvent(fieldsValue) {
    const { dispatch, tenantId } = this.props;
    const { eventFormData } = this.state;
    dispatch({
      type: 'event/createEvent',
      payload: { tenantId, ...eventFormData, ...fieldsValue },
    }).then((res) => {
      if (res) {
        notification.success();
        this.hideModal();
        this.fetchEventData();
      }
    });
  }

  /**
   * handlePagination - 分页设置
   * @param {object} pagination - 分页对象
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchEventData({ page: pagination });
  }

  /**
   * 编辑数据
   */
  @Bind()
  handleUpdate(record = {}) {
    this.setState({
      eventFormData: record,
    });
    this.handleModalVisible(true);
  }

  /**
   * 启用/禁用
   * @param {*} type 类型
   * @param {*} record 当前行数据
   */
  @Bind()
  async handleEnableDisable(type, record) {
    const { dispatch, tenantId } = this.props;
    ProModal.confirm({
      title:
        type === 'open'
          ? intl.get('hevt.event.view.title.enabledEvent').d('启用事件?')
          : intl.get('hevt.event.view.title.disabledEvent').d('停用事件?'),
      onOk: async () => {
        if (type === 'open') {
          dispatch({
            type: 'event/createEvent',
            payload: { tenantId, ...record, enabledFlag: 1 },
          }).then((res) => {
            if (res) {
              notification.success();
              this.hideModal();
              this.fetchEventData();
            }
          });
        } else {
          dispatch({
            type: 'event/createEvent',
            payload: { tenantId, ...record, enabledFlag: 0 },
          }).then((res) => {
            if (res) {
              notification.success();
              this.hideModal();
              this.fetchEventData();
            }
          });
        }
      },
    });
  }

  render() {
    const {
      fetchDataLoading,
      createLoading,
      history,
      dispatch,
      match: { path },
      event: { eventData = [], modalVisible, pagination = {}, queryData, categoryName },
    } = this.props;
    const { eventFormData = {} } = this.state;
    const columns = [
      {
        title: intl.get('hevt.common.model.categoryCode').d('事件编码'),
        dataIndex: 'eventCode',
      },
      {
        title: intl.get('hevt.common.model.eventName').d('事件名称'),
        dataIndex: 'eventName',
      },
      {
        title: intl.get('hevt.event.model.event.categoryName').d('事件类型'),
        dataIndex: 'categoryName',
      },
      {
        title: intl.get('hevt.event.model.event.eventSourceName').d('事件源'),
        dataIndex: 'eventSourceName',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        render: (_, record) => (
          <span className="action-link">
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.edit`,
                  type: 'button',
                  meaning: '编辑',
                },
              ]}
              onClick={() => {
                this.handleUpdate(record);
              }}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </ButtonPermission>
            {record.enabledFlag !== 1 ? (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.enabled`,
                    type: 'button',
                    meaning: '启用',
                  },
                ]}
                onClick={() => this.handleEnableDisable('open', record)}
              >
                {intl.get('hzero.common.button.enable').d('启用')}
              </ButtonPermission>
            ) : (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.disabled`,
                    type: 'button',
                    meaning: '禁用',
                  },
                ]}
                onClick={() => this.handleEnableDisable('close', record)}
              >
                {intl.get('hzero.common.button.disable').d('禁用')}
              </ButtonPermission>
            )}
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.handleEvent`,
                  type: 'button',
                  meaning: '事件处理',
                },
              ]}
              onClick={() => {
                history.push(`handle?eventId=${record.eventId}`);
              }}
            >
              {intl.get('hevt.event.view.message.handleEvent').d('事件处理')}
            </ButtonPermission>
          </span>
        ),
      },
    ];
    return (
      <>
        <Header title={intl.get('hevt.event.view.message.title').d('事件定义')}>
          <ButtonPermission
            type="c7n-pro"
            icon="add"
            color="primary"
            permissionList={[
              {
                code: `${path}.button.cunsumeConfig`,
                type: 'button',
                meaning: '新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm
              dispatch={dispatch}
              onSearch={this.handleSearch}
              onReset={this.handleResetSearch}
              onRef={this.handleBindRef}
              queryData={queryData}
              categoryName={categoryName}
            />
          </div>
          <Table
            bordered
            rowKey="eventId"
            loading={fetchDataLoading}
            dataSource={eventData}
            columns={columns}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          <EditModal
            title={
              eventFormData.eventId === undefined
                ? intl.get('hevt.event.view.button.event.create').d('新建事件')
                : intl.get('hevt.event.view.button.event.edit').d('编辑事件')
            }
            loading={createLoading}
            modalVisible={modalVisible}
            initData={eventFormData}
            onCancel={this.hideModal}
            onOk={this.handleSaveEvent}
            style={{
              zIndex: 0,
            }}
          />
        </Content>
      </>
    );
  }
}
