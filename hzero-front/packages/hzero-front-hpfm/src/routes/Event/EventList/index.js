/**
 * Event - 事件规则
 * @date: 2018-6-20
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Modal, Tag } from 'hzero-ui';
import { isUndefined } from 'lodash';

import { Content, Header } from 'components/Page';
import ExcelExport from 'components/ExcelExport';
import CustomTable from 'components/CustomTable';
import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, operatorRender } from 'utils/renderer';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { HZERO_PLATFORM } from 'utils/config';
import { filterNullValueObject, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

import EventCreateForm from './EventCreateForm';
import EventSearchForm from './EventSearchForm';

@connect(({ event, loading }) => ({
  event,
  isSiteFlag: !isTenantRoleLevel(),
  organizationId: getCurrentOrganizationId(),
  list: event.list,
  loading: loading.effects['event/query'],
  saving: loading.effects['event/action'],
}))
@formatterCollections({
  code: 'hpfm.event',
})
export default class EventList extends React.Component {
  constructor(props) {
    super(props);
    this.filterForm = React.createRef();
    this.createForm = React.createRef();
    this.state = {
      selectedRows: [],
    };
  }

  componentDidMount() {
    const {
      event: searchPageData = {},
      location: { state: { _back } = {} },
    } = this.props;
    const { pagination } = searchPageData;
    this.handleSearch(isUndefined(_back) ? {} : pagination);
  }

  /**
   * 列表查询
   * @param {Object} pagination 查询参数
   */
  @Bind()
  handleSearch(pagination = {}) {
    const { dispatch = (e) => e } = this.props;
    const filterForm = this.getFilterFormRefFallbackEmptyObject(this.filterForm);
    const { form } = filterForm.props;
    const params = isUndefined(form) ? {} : form.getFieldsValue();
    const filterValues = filterNullValueObject({
      ...params,
    });
    dispatch({
      type: 'event/query',
      payload: {
        page: pagination,
        ...filterValues,
      },
    });
  }

  @Bind()
  handleRowSelectChange(_, selectedRows) {
    this.setState({ selectedRows });
  }

  @Bind()
  showModal() {
    this.handleModalVisible(true);
  }

  @Bind()
  hideModal() {
    const { saving = false } = this.props;
    if (!saving) {
      this.handleModalVisible(false);
    }
  }

  handleModalVisible(flag) {
    const { dispatch } = this.props;
    if (flag === false && this.getFilterFormRefFallbackEmptyObject(this.createForm)) {
      this.getFilterFormRefFallbackEmptyObject(this.createForm).resetForm();
    }
    dispatch({
      type: 'event/updateState',
      payload: {
        modalVisible: !!flag,
      },
    });
  }

  @Bind()
  handleAdd(fieldsValue) {
    const { history, dispatch, organizationId } = this.props;
    dispatch({
      type: 'event/action',
      method: 'addEvent',
      payload: { tenantId: organizationId, ...fieldsValue },
    }).then((response) => {
      if (response) {
        this.hideModal();
        history.push(`/hpfm/event/detail/${response.eventId}`);
      }
    });
  }

  @Bind()
  deleteEvent() {
    const { selectedRows } = this.state;
    const {
      dispatch,
      organizationId,
      event: { pagination = {} },
    } = this.props;
    const onOk = () => {
      dispatch({
        type: 'event/action',
        method: 'remove',
        payload: { selectedRows, tenantId: organizationId },
      }).then((res) => {
        if (res) {
          this.setState({
            selectedRows: [],
          });
          this.handleSearch(pagination);
          notification.success();
        }
      });
    };

    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
    });
  }

  @Bind()
  getFilterFormRefFallbackEmptyObject(ref) {
    return ref && ref.current;
  }

  @Bind()
  getSearchFormData() {
    const filterForm = this.getFilterFormRefFallbackEmptyObject(this.filterForm);
    const { selectedRows } = this.state;
    const eventIds = selectedRows.map((item) => {
      return item.eventId;
    });
    if (filterForm.props) {
      return { ...filterForm.props.form.getFieldsValue(), eventIds };
    }
    return { ...eventIds };
  }

  render() {
    const {
      isSiteFlag,
      saving,
      loading,
      organizationId,
      match,
      event: { modalVisible, list = {}, pagination = {} },
    } = this.props;
    const { selectedRows = [] } = this.state;

    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.eventId),
      onChange: this.handleRowSelectChange,
    };
    const filterProps = {
      isSiteFlag,
      onSearch: this.handleSearch,
      wrappedComponentRef: this.filterForm,
    };

    const columns = [
      isSiteFlag && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        width: 200,
        dataIndex: 'tenantName',
        filterField: true,
      },
      {
        title: intl.get('hpfm.event.model.event.code').d('事件编码'),
        width: 200,
        dataIndex: 'eventCode',
        filterField: true,
      },
      {
        title: intl.get('hpfm.event.model.event.description').d('事件描述'),
        minWidth: 200,
        dataIndex: 'eventDescription',
        filterField: true,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
        filterField: true,
      },
      !isSiteFlag && {
        title: intl.get('hzero.common.source').d('来源'),
        width: 100,
        filterField: true,
        render: (_, record) => {
          return organizationId === record.tenantId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          );
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        filterBar: true,
        width: 200,
        fixed: 'right',
        render: (_, record) => {
          const operators = [];
          const { history } = this.props;
          operators.push(
            {
              key: 'processMaintain',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.processMaintain`,
                      type: 'button',
                      meaning: '事件规则-事件维护',
                    },
                  ]}
                  onClick={() => {
                    history.push(`/hpfm/event/detail/${record.eventId}`);
                  }}
                >
                  {intl.get('hpfm.event.model.event.processMaintain').d('事件维护')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hpfm.event.model.event.processMaintain').d('事件维护'),
            },
            {
              key: 'imgMaintain',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.imgMaintain`,
                      type: 'button',
                      meaning: '事件规则-图形编辑界面',
                    },
                  ]}
                  onClick={() => {
                    history.push(`/hpfm/event/graph/${record.eventId}`);
                  }}
                >
                  {intl.get('hpfm.event.model.event.imgMaintain').d('图形编辑界面')}
                </ButtonPermission>
              ),
              len: 6,
              title: intl.get('hpfm.event.model.event.imgMaintain').d('图形编辑界面'),
            }
          );
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);

    const tableProps = {
      columns,
      loading,
      pagination,
      rowSelection,
      bordered: true,
      rowKey: 'eventId',
      // TODO: 后续完全去掉表格动态列功能
      // customCode: 'EVENT_TABLE',
      dataSource: list.content,
      onChange: this.handleSearch,
    };

    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.event.view.message.title.event').d('事件规则')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '事件规则-新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            icon="delete"
            loading={saving}
            disabled={selectedRows.length === 0}
            permissionList={[
              {
                code: `${match.path}.button.delete`,
                type: 'button',
                meaning: '事件规则-删除',
              },
            ]}
            onClick={this.deleteEvent}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
          <ExcelExport
            exportAsync
            requestUrl={`${HZERO_PLATFORM}/v1/${
              isTenantRoleLevel() ? `${organizationId}/events/export` : 'events/export'
            }`}
            queryParams={this.getSearchFormData}
          />
        </Header>
        <Content>
          <EventSearchForm {...filterProps} />
          <CustomTable {...tableProps} />
        </Content>
        <EventCreateForm
          isSiteFlag={isSiteFlag}
          title={intl.get('hpfm.event.view.createForm.title').d('创建事件')}
          wrappedComponentRef={this.createForm}
          handleAdd={this.handleAdd}
          confirmLoading={saving}
          modalVisible={modalVisible}
          hideModal={this.hideModal}
        />
      </React.Fragment>
    );
  }
}
