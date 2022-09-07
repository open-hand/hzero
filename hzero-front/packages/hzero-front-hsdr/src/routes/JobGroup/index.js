/**
 * 任务执行器
 * @date: 2018-9-3
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Form, Popconfirm, Table, Tag } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { addressTypeRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import {
  filterNullValueObject,
  getEditTableData,
  isTenantRoleLevel,
  tableScrollWidth,
} from 'utils/utils';

import JobGroupForm from './JobGroupForm';
import ExecutorDrawer from './ExecutorDrawer.js';
import SearchForm from './SearchForm';

/**
 * 任务执行器
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} jobGroup - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ jobGroup, loading }) => ({
  jobGroup,
  tenantRoleLevel: isTenantRoleLevel(),
  saving: loading.effects['jobGroup/updateGroups'],
  creating: loading.effects['jobGroup/createGroups'],
  fetchList: loading.effects['jobGroup/fetchGroupsList'],
  fetchExecutorLoading: loading.effects['jobGroup/fetchExecutorList'],
  deleteExecutorLoading: loading.effects['jobGroup/deleteExecutor'],
  saveExecutorLoading: loading.effects['jobGroup/updateExecutor'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hsdr.jobGroup', 'entity.tenant'] })
export default class JobGroup extends Component {
  constructor(props) {
    super(props);
    this.state = {
      groupsData: {},
      selectExecutorRecord: {},
      executorModalVisible: false,
    };
  }

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'jobGroup/init',
    });
    this.fetchGroupsList();
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  fetchGroupsList(fields = {}) {
    const { dispatch } = this.props;
    const filterValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'jobGroup/fetchGroupsList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...filterValues,
      },
    });
  }

  /**
   * @function showModal - 新增显示模态框
   */
  @Bind()
  showModal() {
    this.setState({ groupsData: {} }, () => {
      this.handleModalVisible(true);
    });
  }

  /**
   * @function handleModalVisible - 控制modal显示与隐藏
   * @param {boolean} flag 是否显示modal
   */
  @Bind()
  handleModalVisible(flag) {
    const { dispatch } = this.props;
    dispatch({
      type: 'jobGroup/updateState',
      payload: {
        modalVisible: !!flag,
      },
    });
  }

  @Bind()
  handleModalSave(fieldsValue) {
    const {
      dispatch,
      jobGroup: { pagination },
    } = this.props;
    const { groupsData = {} } = this.state;
    // if (groupsData.executorType !== 0) {
    dispatch({
      type: `jobGroup/${groupsData.executorId ? 'updateGroups' : 'createGroups'}`,
      payload: {
        executorType: 1,
        ...groupsData,
        ...fieldsValue,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleModalVisible(false);
        this.fetchGroupsList(pagination);
      }
    });
    // } else {
    //   this.handleModalVisible(false);
    // }
  }

  /**
   * @function handleUpdateEmail - 编辑
   * @param {object} record - 头数据
   */
  @Bind()
  handleUpdateGroups(record) {
    this.setState({ groupsData: record });
    this.handleModalVisible(true);
  }

  @Bind()
  showExecutorModal(flag) {
    this.setState({ executorModalVisible: flag });
  }

  @Bind()
  handleCancelExecutor() {
    this.showExecutorModal(false);
  }

  @Bind()
  handleSaveExecutor() {
    const { dispatch, jobGroup = {} } = this.props;
    const { executorList = [] } = jobGroup;
    const params = getEditTableData(executorList, ['executorId']);
    if (Array.isArray(params) && params.length > 0) {
      dispatch({
        type: 'jobGroup/updateExecutor',
        payload: params,
      }).then((res) => {
        if (res) {
          notification.success();
          this.fetchExecutorList();
        }
      });
    }
  }

  /**
   * 删除执行器配置
   */
  @Bind()
  handleDeleteExecutor(data = [], keys = []) {
    const {
      dispatch,
      jobGroup: { executorList = [] },
    } = this.props;
    const filterData = data.filter((item) => item._status !== 'create');
    // 删除未保存的数据
    const createList = data.filter((item) => item._status === 'create');
    if (createList.length > 0) {
      const deleteList = executorList.filter((item) => !keys.includes(item.executorId));
      dispatch({
        type: 'jobGroup/updateState',
        payload: { executorList: deleteList },
      });
      notification.success();
    }
    if (filterData.length > 0) {
      dispatch({
        type: 'jobGroup/deleteExecutor',
        payload: filterData,
      }).then((res) => {
        if (res) {
          notification.success();
          this.fetchExecutorList();
        }
      });
    }
  }

  @Bind()
  handleEditExecutor(record, flag) {
    const {
      jobGroup: { executorList = [] },
      dispatch,
    } = this.props;
    const newList = executorList.map((item) => {
      if (record.uuid === item.uuid) {
        return { ...item, _status: flag ? 'update' : '' };
      }
      return item;
    });
    dispatch({
      type: 'jobGroup/updateState',
      payload: { executorList: newList },
    });
  }

  /**
   * 执行器配置
   */
  @Bind()
  handleExecutorConfig(record) {
    this.showExecutorModal(true);
    this.setState({ selectExecutorRecord: record }, () => {
      this.fetchExecutorList();
    });
  }

  /**
   * 获取执行器配置列表
   * @param {object} params - 查询参数
   */
  @Bind()
  fetchExecutorList(params) {
    const { dispatch } = this.props;
    const { selectExecutorRecord = {} } = this.state;
    const { executorId } = selectExecutorRecord;
    // 清空缓存
    dispatch({
      type: 'jobGroup/updateState',
      payload: { executorList: [] },
    });
    dispatch({
      type: 'jobGroup/fetchExecutorList',
      payload: { ...params, executorId },
    });
  }

  /**
   * 数据列表，头删除
   * @function handleDeleteGroups
   * @param {obejct} record - 操作对象
   */
  @Bind()
  handleDeleteGroups(record) {
    const {
      dispatch,
      jobGroup: { pagination },
    } = this.props;
    dispatch({
      type: 'jobGroup/deleteGroups',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchGroupsList(pagination);
      }
    });
  }

  /**
   * 传递表单对象
   * @param {object} ref - FilterForm对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 更改上下线状态
   * @param {object} record
   * @param {boolean} isOffLine
   * @memberof JobGroup
   */
  @Bind()
  handleChangeStatus(record, isOffLine) {
    const {
      dispatch,
      jobGroup: { pagination },
    } = this.props;
    dispatch({
      type: 'jobGroup/updateGroups',
      payload: {
        executorType: 1,
        ...record,
        status: isOffLine ? 'ONLINE' : 'OFFLINE',
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchGroupsList(pagination);
      }
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      fetchList,
      saving,
      creating,
      tenantRoleLevel,
      match: { path },
      saveExecutorLoading = false,
      deleteExecutorLoading = false,
      fetchExecutorLoading = false,
      jobGroup: { groupsList, modalVisible, pagination, statusList, executorList = [] },
    } = this.props;
    const {
      groupsData: { executorId },
      executorModalVisible,
      selectExecutorRecord,
    } = this.state;
    const jobGroupFormProps = {
      saving,
      creating,
      modalVisible,
      statusList,
      tenantRoleLevel,
      modalTitle: executorId
        ? intl.get('hsdr.jobGroup.view.message.edit').d('编辑执行器')
        : intl.get('hsdr.jobGroup.view.message.create').d('新增执行器'),
      initData: this.state.groupsData,
      onCancel: () => this.handleModalVisible(false),
      onOk: this.handleModalSave,
    };
    const filterProps = {
      onSearch: this.fetchGroupsList,
      onRef: this.handleBindRef,
    };
    const executorProps = {
      selectExecutorRecord,
      path,
      modalVisible: executorModalVisible,
      saveLoading: saveExecutorLoading,
      deleteLoading: deleteExecutorLoading,
      initLoading: fetchExecutorLoading,
      dataSource: executorList,
      onEdit: this.handleEditExecutor,
      onDelete: this.handleDeleteExecutor,
      onOk: this.handleSaveExecutor,
      onCancel: this.handleCancelExecutor,
    };
    const columns = [
      {
        title: intl.get('hsdr.jobGroup.model.jobGroup.orderSeq').d('排序'),
        dataIndex: 'orderSeq',
        width: 80,
      },
      {
        title: intl.get('hsdr.jobGroup.model.jobGroup.executorCode').d('执行器编码'),
        dataIndex: 'executorCode',
        width: 200,
      },
      {
        title: intl.get('hsdr.jobGroup.model.jobGroup.executorName').d('执行器名称'),
        dataIndex: 'executorName',
        width: 150,
      },
      {
        title: intl.get('hsdr.jobGroup.model.jobGroup.addressType').d('注册方式'),
        dataIndex: 'executorType',
        width: 120,
        render: addressTypeRender,
      },
      {
        title: intl.get('hsdr.jobGroup.view.message.serverName').d('服务'),
        dataIndex: 'serverName',
        width: 170,
      },
      {
        title: intl.get('hsdr.jobGroup.model.jobGroup.status').d('状态'),
        dataIndex: 'statusMeaning',
        width: 120,
        render: (text, record) => {
          let color;
          switch (record.status) {
            case 'ONLINE':
              color = 'green';
              break;
            case 'OFFLINE':
              color = 'orange';
              break;
            case 'A-OFFLINE':
              color = 'red';
              break;
            default:
              return;
          }
          return <Tag color={color}>{text}</Tag>;
        },
      },
      {
        title: intl.get('hsdr.jobGroup.model.jobGroup.addressList').d('OnLine 机器地址'),
        dataIndex: 'addressList',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        width: 200,
        fixed: 'right',
        render: (val, record) => {
          const { status } = record;
          const isOffLine = status === 'OFFLINE';
          const operators = [];
          operators.push(
            {
              key: 'executor',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.executor`,
                      type: 'button',
                      meaning: '执行器管理-配置',
                    },
                  ]}
                  onClick={() => this.handleExecutorConfig(record)}
                >
                  {intl.get('hsdr.jobGroup.button.executor').d('配置')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hsdr.jobGroup.button.executor').d('配置'),
            },
            {
              key: 'changeStatus',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.changeStatus`,
                      type: 'button',
                      meaning: '执行器管理-更改状态',
                    },
                  ]}
                  onClick={() => this.handleChangeStatus(record, isOffLine)}
                >
                  {isOffLine
                    ? intl.get('hsdr.jobGroup.button.onLine').d('上线')
                    : intl.get('hsdr.jobGroup.button.offLine').d('下线')}
                </ButtonPermission>
              ),
              len: 2,
              title: isOffLine
                ? intl.get('hsdr.jobGroup.button.onLine').d('上线')
                : intl.get('hsdr.jobGroup.button.offLine').d('下线'),
            },
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '执行器管理-编辑',
                    },
                  ]}
                  onClick={() => this.handleUpdateGroups(record)}
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
                  placement="topRight"
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => this.handleDeleteGroups(record)}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.delete`,
                        type: 'button',
                        meaning: '执行器管理-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators, record, { limit: 4 });
        },
      },
    ].filter((col) => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    return (
      <>
        <Header title={intl.get('hsdr.jobGroup.view.message.group').d('执行器管理')}>
          <ButtonPermission
            type="primary"
            icon="plus"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '执行器管理-新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <SearchForm {...filterProps} />
          <Table
            bordered
            rowKey="executorId"
            loading={fetchList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            dataSource={groupsList}
            pagination={pagination}
            onChange={this.fetchGroupsList}
          />
          {modalVisible && <JobGroupForm {...jobGroupFormProps} />}
          {executorModalVisible && <ExecutorDrawer {...executorProps} />}
        </Content>
      </>
    );
  }
}
