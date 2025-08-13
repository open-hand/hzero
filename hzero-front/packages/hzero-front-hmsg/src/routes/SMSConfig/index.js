/**
 * SMSConfig - 短信配置
 * @date: 2018-8-1
 * @author: CJ <juan.chen@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { withRouter } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import {
  filterNullValueObject,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  encryptPwd,
  addItemToPagination,
} from 'utils/utils';

import QueryForm from './QueryForm';
import ListTable from './ListTable';
import DetailModal from './DetailModal';
import Filter from './Filter';

/**
 * 短信账户数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} formValues - 查询表单值
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {Boolean} modalVisible - 模态框是否可见
 * @return React.element
 */
@connect(({ smsConfig, loading }) => ({
  smsConfig,
  tenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
  querySMSListLoading: loading.effects['smsConfig/fetchSMSList'],
  saving: loading.effects['smsConfig/createSMS'] || loading.effects['smsConfig/editSMS'],
  deleteFilterLoading: loading.effects['smsConfig/deleteFilter'],
}))
@withRouter
@formatterCollections({ code: ['hmsg.smsConfig', 'hmsg.common'] })
export default class SMSConfig extends PureComponent {
  form;

  state = {
    modalVisible: false,
    isCreate: true,
    tableRecord: {},
    isCopy: false,
    currentFilter: {},
    filterVisible: false,
  };

  componentDidMount() {
    this.fetchEnums();
    this.fetchTableList();
    this.fetchServerTypeCode();
    this.fetchPublicKey();
  }

  // 获取值集
  @Bind()
  fetchEnums() {
    const { dispatch } = this.props;
    dispatch({
      type: 'smsConfig/fetchEnums',
    });
  }

  // 获取短信列表信息
  @Bind()
  fetchTableList(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'smsConfig/fetchSMSList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  // 获取服务类型
  fetchServerTypeCode() {
    const { dispatch } = this.props;
    dispatch({
      type: 'smsConfig/fetchServerType',
    });
  }

  // 打开新增模态框
  @Bind()
  showModal() {
    this.setState({
      modalVisible: true,
      isCreate: true,
      isCopy: false,
    });
  }

  // 打开编辑模态框
  @Bind()
  showEditModal() {
    this.setState({
      modalVisible: true,
      isCreate: false,
      isCopy: false,
    });
  }

  // 关闭模态框
  @Bind()
  handleCancel() {
    this.setState({
      modalVisible: false,
      isCreate: true,
      tableRecord: {},
      isCopy: false,
    });
  }

  /**
   * 请求公钥
   */
  @Bind()
  fetchPublicKey() {
    const { dispatch = () => {} } = this.props;
    dispatch({
      type: 'smsConfig/getPublicKey',
    });
  }

  // 新建短信账户
  @Bind()
  handleAdd(values) {
    const {
      dispatch,
      smsConfig: { pagination = {}, publicKey },
      tenantId,
      tenantRoleLevel,
    } = this.props;
    const newValues = { ...values };
    if (values.accessKeySecret) {
      newValues.accessKeySecret = encryptPwd(values.accessKeySecret, publicKey);
    }
    const params = {
      ...newValues,
      tenantId: tenantRoleLevel ? tenantId : values.tenantId,
    };
    dispatch({
      type: 'smsConfig/createSMS',
      payload: params,
    }).then((response) => {
      if (response) {
        this.handleCancel();
        this.fetchTableList(pagination);
        notification.success();
      }
    });
  }

  // 编辑短信账户
  @Bind()
  handleEdit(values) {
    const {
      dispatch,
      smsConfig: { pagination = {}, publicKey },
      tenantId,
      tenantRoleLevel,
    } = this.props;
    const newValues = { ...values };
    if (values.accessKeySecret) {
      newValues.accessKeySecret = encryptPwd(values.accessKeySecret, publicKey);
    }
    const editParams = {
      ...newValues,
      tenantId: tenantRoleLevel ? tenantId : values.tenantId,
    };
    dispatch({
      type: 'smsConfig/editSMS',
      payload: editParams,
    }).then((response) => {
      if (response) {
        this.handleCancel();
        this.fetchTableList(pagination);
        notification.success();
      }
    });
  }

  // 获取编辑数据记录
  @Bind()
  getRecordData(record) {
    this.setState({
      tableRecord: { ...record },
    });
    this.showEditModal();
  }

  @Bind()
  handleCopy(record) {
    this.setState({
      tableRecord: record,
      isCopy: true,
      modalVisible: true,
    });
  }

  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'smsConfig/deleteSMS',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchTableList();
      }
    });
  }

  @Bind()
  handleFilterSearch(params = {}) {
    const {
      dispatch,
      smsConfig: { filterPagination = {} },
    } = this.props;
    const { currentFilter: { serverId } = {} } = this.state;
    dispatch({
      type: 'smsConfig/fetchFilterList',
      payload: { page: filterPagination, serverId, ...params },
    });
  }

  @Bind()
  handleShowFilter(record) {
    this.setState({ filterVisible: true, currentFilter: record });
    this.handleFilterSearch({
      serverId: record.serverId,
    });
  }

  @Bind()
  handleFilterOk(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'smsConfig/updateFilter',
      payload: data,
    }).then((res) => {
      if (res) {
        notification.success();
        // this.handleFilterCancel(false);
        this.handleFilterSearch();
      }
    });
  }

  @Bind()
  handleFilterCancel() {
    this.setState({ filterVisible: false });
  }

  @Bind()
  handleFilterDelete(data = [], keys = []) {
    const { dispatch, smsConfig: { filterList = [] } = {} } = this.props;
    const filterData = data.filter((item) => item._status !== 'create');
    // 删除未保存的数据
    const createList = data.filter((item) => item._status === 'create');
    if (createList.length > 0) {
      const deleteList = filterList.filter((item) => !keys.includes(item.smsFilterId));
      dispatch({
        type: 'smsConfig/updateState',
        payload: { filterList: deleteList },
      });
      notification.success();
    }
    if (filterData.length > 0) {
      dispatch({
        type: 'smsConfig/deleteFilter',
        payload: filterData,
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleFilterSearch();
        }
      });
    }
  }

  @Bind()
  handleFilterCreate(data) {
    const {
      dispatch,
      smsConfig: { filterList = [], filterPagination = {} },
    } = this.props;
    dispatch({
      type: 'smsConfig/updateState',
      payload: {
        filterList: [data, ...filterList],
        filterPagination: addItemToPagination(filterList.length, filterPagination),
      },
    });
  }

  @Bind()
  handleFilterEdit(record, flag) {
    const { dispatch, smsConfig: { filterList = [] } = {} } = this.props;
    const newList = filterList.map((item) => {
      if (record.smsFilterId === item.smsFilterId) {
        return { ...item, _status: flag ? 'update' : '' };
      }
      return item;
    });
    dispatch({
      type: 'smsConfig/updateState',
      payload: { filterList: newList },
    });
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  render() {
    const {
      smsConfig: {
        smsData = {},
        serverTypeList = [],
        pagination = {},
        filterList = [],
        enums: { iddList = [], filterStrategyList = [] },
        filterPagination = {},
      },
      querySMSListLoading,
      saving,
      tenantId,
      tenantRoleLevel,
      match: { path },
      fetchFilterLoading = false,
      deleteFilterLoading = false,
      filterLoading = false,
    } = this.props;
    const {
      filterVisible,
      modalVisible,
      tableRecord = {},
      isCreate,
      currentFilter,
      isCopy,
    } = this.state;
    const formProps = {
      serverTypeList,
      tenantRoleLevel,
      onSearch: this.fetchTableList,
      onRef: this.handleBindRef,
    };
    const tableProps = {
      path,
      pagination,
      smsData,
      tenantId,
      tenantRoleLevel,
      loading: querySMSListLoading,
      onGetRecord: this.getRecordData,
      onCopy: this.handleCopy,
      onChange: this.fetchTableList,
      onDelete: this.handleDelete,
      handleShowFilter: this.handleShowFilter,
    };
    const detailProps = {
      isCopy,
      title: isCreate
        ? intl.get('hmsg.smsConfig.view.message.create').d('新建短信账户')
        : intl.get('hmsg.smsConfig.view.message.edit').d('编辑短信账户'),
      modalVisible,
      serverTypeList,
      tableRecord,
      isCreate,
      saving,
      tenantRoleLevel,
      filterStrategyList, // 安全策略组值集
      onCancel: this.handleCancel,
      anchor: 'right',
      onAdd: this.handleAdd,
      onEdit: this.handleEdit,
    };

    const filterProps = {
      currentFilter,
      fetchLoading: fetchFilterLoading,
      deleteLoading: deleteFilterLoading,
      loading: filterLoading,
      dataSource: filterList,
      visible: filterVisible,
      pagination: filterPagination,
      onOk: this.handleFilterOk,
      onCancel: this.handleFilterCancel,
      onDelete: this.handleFilterDelete,
      onCreate: this.handleFilterCreate,
      onSearch: this.handleFilterSearch,
      onEdit: this.handleFilterEdit,
      path,
      iddList,
    };
    return (
      <>
        <Header title={intl.get('hmsg.smsConfig.view.message.title.smsConfig').d('短信配置')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '短信配置-新建',
              },
            ]}
            type="primary"
            onClick={this.showModal}
            icon="plus"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <QueryForm {...formProps} />
          </div>
          <ListTable {...tableProps} />
        </Content>
        <DetailModal {...detailProps} />
        {filterVisible && <Filter {...filterProps} />}
      </>
    );
  }
}
