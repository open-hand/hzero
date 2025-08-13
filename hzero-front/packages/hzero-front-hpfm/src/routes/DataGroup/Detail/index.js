/**
 * DataGroup- 数据组管理-详情
 * @date: 2019/7/15
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Card, Modal } from 'hzero-ui';
import { connect } from 'dva';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME, DETAIL_CARD_TABLE_CLASSNAME } from 'utils/constants';

import DetailForm from './DetailForm';
import DetailTable from './DetailTable';
import AddDataModal from './AddDataModal';

/**
 * 数据组管理-详情
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} dataGroup - 数据源
 * @reactProps {boolean} loading - 数据组行加载标志
 * @reactProps {boolean} fetchHeadLoading - 数据组头加载标志
 * @reactProps {boolean} saveHeadLoading - 数据组头更新加载标志
 * @reactProps {boolean} fetchModalLoading - 选择维度代码弹窗列表加载标志
 * @reactProps {boolean} createLineLoading - 创建数据组行加载标志
 * @reactProps {boolean} fetchValueModalLoading - 选择维度值弹窗列表加载标志
 * @reactProps {boolean} fetchAssignedValueLoading - 分配值列表加载标志
 * @reactProps {boolean} createAssignedValueLoading - 创建分配值加载标志
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@connect(({ dataGroup, loading }) => ({
  dataGroup,
  loading:
    loading.effects['dataGroup/fetchDataGroupLineList'] ||
    loading.effects['dataGroup/deleteDataGroupLine'],
  fetchHeadLoading: loading.effects['dataGroup/fetchDataGroupHead'],
  saveHeadLoading: loading.effects['dataGroup/updateDataGroupHead'],
  fetchModalLoading: loading.effects['dataGroup/fetchModalData'],
  createLineLoading: loading.effects['dataGroup/createDataGroupLine'],
  fetchValueModalLoading: loading.effects['dataGroup/fetchValueModalData'],
  fetchAssignedValueLoading:
    loading.effects['dataGroup/fetchAssignedValueList'] ||
    loading.effects['dataGroup/deleteAssignedValue'],
  createAssignedValueLoading: loading.effects['dataGroup/createDataGroupValue'],
}))
@formatterCollections({ code: ['hpfm.dataGroup', 'entity.tenant'] })
export default class Detail extends Component {
  state = {
    selectedRowKeys: [], // 选中行key集合
    selectedRows: {}, // 选中行集合
    addModalVisible: false,
  };

  /**
   * 初始查询列表数据
   */
  componentDidMount() {
    const page = {};
    this.fetchDataGroupHead();
    this.handleSearch(page);
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataGroup/updateState',
      payload: {
        lineList: [], // 数据组行列表
        linePagination: {}, // 数据组行列表分页
        dataGroupHeadInfo: {}, // 数据组头信息
      },
    });
  }

  /**
   * 查询数据组头信息
   */
  @Bind()
  fetchDataGroupHead() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'dataGroup/fetchDataGroupHead',
        payload: id.indexOf('?') === -1 ? id : id.substring(0, id.indexOf('?')),
      });
    }
  }

  /**
   * 保存数据组头信息
   */
  @Bind()
  handleSaveDataGroupHead() {
    const { fetchHeadLoading, saveHeadLoading } = this.props;
    const { validateFields = e => e } = this.form.props.form; // 上面接口定义DOM节点
    if (fetchHeadLoading || saveHeadLoading) {
      return;
    }
    validateFields((err, values) => {
      if (isEmpty(err)) {
        this.save(values);
      }
    });
  }

  /**
   * 保存数据组头
   * @param {Object} values - 表单数据
   */
  @Bind()
  save(values) {
    const {
      dispatch,
      dataGroup: {
        dataGroupHeadInfo: { objectVersionNumber, _token, tenantName, groupId },
      },
    } = this.props;
    dispatch({
      type: 'dataGroup/updateDataGroupHead',
      payload: {
        ...values,
        groupId,
        tenantName,
        objectVersionNumber,
        _token,
      },
    }).then(response => {
      if (response) {
        notification.success();
        this.fetchDataGroupHead();
      }
    });
  }

  /**
   * 查询数据组行列表
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'dataGroup/fetchDataGroupLineList',
        payload: {
          page: isEmpty(fields) ? {} : fields,
          groupId: id.indexOf('?') === -1 ? id : id.substring(0, id.indexOf('?')),
        },
      });
    }
  }

  /**
   * 获取选中行
   * @param {array} selectedRowKeys 选中行的key值集合
   * @param {object} selectedRows 选中行集合
   */
  @Bind()
  handleRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRowKeys,
      selectedRows,
    });
  }

  /**
   * 打开新建数据组弹窗
   */
  @Bind()
  handleOpenModal() {
    this.fetchModalData();
    this.setState({
      addModalVisible: true,
    });
  }

  /**
   * 隐藏新建数据组弹窗
   */
  @Bind()
  handleCloseModal() {
    this.dataGroupRef.state.addRows = [];
    this.setState({
      addModalVisible: false,
    });
  }

  /**
   * 查询弹窗数据
   * @param {Object} queryData - 查询数据
   */
  @Bind()
  fetchModalData(queryData = {}) {
    const {
      dispatch,
      dataGroup: { dataGroupHeadInfo = {} },
    } = this.props;
    dispatch({
      type: 'dataGroup/fetchModalData',
      payload: {
        lovTypeCode: 'IDP',
        tenantId: dataGroupHeadInfo.tenantId,
        ...queryData,
        enabledFlag: 1,
      },
    });
  }

  @Bind()
  handleBindRef(ref = {}) {
    this.dataGroupRef = ref;
  }

  /**
   * 创建数据组行
   * @param {Aarray} addRows 选择的数据
   */
  @Bind()
  handleCreateLine(addRows) {
    const {
      dispatch,
      match,
      dataGroup: { dataGroupHeadInfo = {} },
    } = this.props;
    const { id } = match.params;
    const nextAddRows = [];
    addRows.forEach(item => {
      nextAddRows.push({
        lineValueId: item.lovId,
        tenantId: dataGroupHeadInfo.tenantId,
      });
    });
    dispatch({
      type: 'dataGroup/createDataGroupLine',
      payload: {
        dataGroupId: id.indexOf('?') === -1 ? id : id.substring(0, id.indexOf('?')),
        dataGroupLine: nextAddRows,
      },
    }).then(response => {
      if (response) {
        this.handleCloseModal();
        notification.success();
        this.dataGroupRef.state.addRows = [];
        this.handleSearch();
      }
    });
  }

  /**
   * 删除数据组行
   */
  @Bind()
  handleDeleteLine() {
    const { selectedRows } = this.state;
    const { dispatch, match } = this.props;
    const { id } = match.params;
    const that = this;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk() {
        dispatch({
          type: 'dataGroup/deleteDataGroupLine',
          payload: {
            dataGroupId: id.indexOf('?') === -1 ? id : id.substring(0, id.indexOf('?')),
            dataGroupLine: selectedRows,
          },
        }).then(response => {
          if (response) {
            notification.success();
            that.setState(
              {
                selectedRowKeys: [],
                selectedRows: {},
              },
              () => {
                that.handleSearch();
              }
            );
          }
        });
      },
    });
  }

  /**
   * 删除分配值
   * @param {object} payload - 请求数据
   */
  @Bind()
  deleteAssignedValue(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'dataGroup/deleteAssignedValue',
      payload: { ...payload },
    });
  }

  /**
   * 查询分配值列表
   * @param {number} groupLineId - 数据组行ID
   * @param {object} params - 分页参数
   */
  @Bind()
  fetchAssignedValueList(groupLineId, fields = {}) {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataGroup/fetchAssignedValueList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        groupLineId,
      },
    });
  }

  /**
   * 查询选择维度值的弹窗数据
   */
  @Bind()
  fetchValueModalData(queryData = {}) {
    const {
      dispatch,
      dataGroup: { dataGroupHeadInfo = {} },
    } = this.props;
    dispatch({
      type: 'dataGroup/fetchValueModalData',
      payload: {
        tenantId: dataGroupHeadInfo.tenantId,
        ...queryData,
      },
    });
  }

  /**
   * 新建分配值
   * @param {Object} payload - 请求数据
   */
  @Bind()
  createAssignedValue(payload) {
    const { dispatch } = this.props;
    const { groupLineId, nextAddRows } = payload;
    return dispatch({
      type: 'dataGroup/createAssignedValue',
      payload: {
        groupLineId,
        dataGroupDtlList: nextAddRows,
      },
    });
  }

  /**
   * 清空分配值相关数据
   */
  @Bind()
  resetAssignedValue() {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataGroup/updateState',
      payload: {
        assignedValueList: [],
        assignedValuePagination: {},
        valueDataSource: [], // 新建数据组值的弹窗列表数据
        valuePagination: {}, // 新建数据组值的弹窗列表分页
      },
    });
  }

  render() {
    const { selectedRowKeys } = this.state;
    const {
      dataGroup: {
        dataGroupHeadInfo,
        lineList,
        linePagination,
        createDataSource = [],
        createPagination = {},
        valueDataSource = [],
        valuePagination = {},
        assignedValueList = [],
        assignedValuePagination = {},
      },
      location: { search, pathname },
      match,
      loading,
      fetchModalLoading,
      createLineLoading,
      fetchHeadLoading = false,
      saveHeadLoading = false,
      fetchValueModalLoading = false,
      fetchAssignedValueLoading = false,
      createAssignedValueLoading = false,
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const { addModalVisible } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const detailTableProps = {
      loading,
      match,
      dataSource: lineList,
      pagination: linePagination,
      rowSelection,
      dataGroupHeadInfo,
      onChange: this.handleSearch,
      onCreate: this.handleOpenModal,
      onClean: this.resetAssignedValue,
      fetchAssignedValueList: this.fetchAssignedValueList,
      createAssignedValue: this.createAssignedValue,
      fetchValueModalData: this.fetchValueModalData,
      deleteAssignedValue: this.deleteAssignedValue,
      fetchValueModalLoading,
      fetchAssignedValueLoading,
      createAssignedValueLoading,
      assignedValueList,
      assignedValuePagination,
      valueDataSource,
      valuePagination,
    };
    const formProps = {
      dataGroupHeadInfo,
      onRef: node => {
        this.form = node;
      },
    };

    const columns = [
      {
        title: intl.get('hpfm.dataGroup.model.dataGroup.dimensionCode').d('维度代码'),
        dataIndex: 'lovCode',
      },
      {
        title: intl.get('hpfm.dataGroup.model.dataGroup.dimensionName').d('维度名称'),
        dataIndex: 'lovName',
        width: 300,
      },
    ];

    const addModalOptions = {
      columns,
      confirmLoading: createLineLoading,
      loading: fetchModalLoading,
      title: intl.get('hpfm.dataGroup.view.message.modal.code.choose').d('选择维度代码'),
      rowKey: 'lovId',
      queryName: 'lovName',
      queryCode: 'lovCode',
      queryNameDesc: intl.get('hpfm.dataGroup.model.dataGroup.dimensionName').d('维度名称'),
      queryCodeDesc: intl.get('hpfm.dataGroup.model.dataGroup.dimensionCode').d('维度代码'),
      dataSource: createDataSource,
      pagination: createPagination,
      modalVisible: addModalVisible,
      addData: this.handleCreateLine,
      onHideAddModal: this.handleCloseModal,
      fetchModalData: this.fetchModalData,
      onRef: this.handleBindRef,
    };

    return (
      <>
        <Header
          title={intl.get('hpfm.dataGroup.view.message.detail').d('数据组详情')}
          backPath={
            pathname.indexOf('/private') === 0
              ? `/private/hpfm/data-group/list?access_token=${accessToken}`
              : '/hpfm/data-group/list'
          }
        >
          <ButtonPermission
            icon="save"
            type="primary"
            loading={saveHeadLoading}
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '数据组详情-保存',
              },
            ]}
            onClick={this.handleSaveDataGroupHead}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            key="data-group-head"
            bordered={false}
            title={<h3>{intl.get('hpfm.dataGroup.view.message.dataGroup.head').d('数据组头')}</h3>}
            className={DETAIL_CARD_CLASSNAME}
            loading={fetchHeadLoading}
          >
            <DetailForm {...formProps} />
          </Card>
          <Card
            key="data-group-line"
            bordered={false}
            title={<h3>{intl.get('hpfm.dataGroup.view.message.dataGroup.line').d('数据组行')}</h3>}
            className={DETAIL_CARD_TABLE_CLASSNAME}
          >
            <div className="table-list-operator">
              <ButtonPermission
                type="primary"
                permissionList={[
                  {
                    code: `${match.path}.button.create`,
                    type: 'button',
                    meaning: '数据组详情-新建',
                  },
                ]}
                onClick={() => this.handleOpenModal()}
              >
                {intl.get('hzero.common.button.create').d('新建')}
              </ButtonPermission>
              <ButtonPermission
                disabled={isEmpty(rowSelection.selectedRowKeys)}
                permissionList={[
                  {
                    code: `${match.path}.button.delete`,
                    type: 'button',
                    meaning: '数据组详情-删除',
                  },
                ]}
                onClick={this.handleDeleteLine}
              >
                {intl.get('hzero.common.button.delete').d('删除')}
              </ButtonPermission>
            </div>
            <AddDataModal {...addModalOptions} />
            <DetailTable {...detailTableProps} />
          </Card>
        </Content>
      </>
    );
  }
}
