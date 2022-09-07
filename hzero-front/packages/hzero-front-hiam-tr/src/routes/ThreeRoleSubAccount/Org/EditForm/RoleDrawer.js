/**
 * EditForm - 用户管理 - 账号编辑表单
 * @date 2018/11/13
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import PropTypes from 'prop-types';
import { cloneDeep, forEach, isEmpty, map, omit, find } from 'lodash';
import { Checkbox, Col, DatePicker, Form, Modal, Row } from 'hzero-ui';
import moment from 'moment';
import { Bind } from 'lodash-decorators';

import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import {
  addItemsToPagination,
  createPagination,
  delItemsToPagination,
  getDateFormat,
  getEditTableData,
  tableScrollWidth,
} from 'utils/utils';

import RoleModal from './RoleModal';
import SearchForm from './SearchForm';

import styles from '../../index.less';

/**
 * EditForm-编辑子账户信息
 * @reactProps {Function} fetchUserRoles 获取当前编辑用户已分配的角色
 * @reactProps {Object[]} dataSource 编辑用户已分配的角色
 * @reactProps {Object[]} LEVEL 资源层级的值集
 */
@Form.create({ fieldNameProp: null })
export default class RoleDrawer extends React.Component {
  constructor(props) {
    super(props);
    const { onRef } = props;
    onRef(this);
    this.cancelDefaultParams = new Set();
  }

  searchFormRef = React.createRef();

  static propTypes = {
    fetchUserRoles: PropTypes.func.isRequired,
    dataSource: PropTypes.array,
    level: PropTypes.array,
  };

  static defaultProps = {
    dataSource: [],
    level: [],
  };

  state = {
    dataSource: [],
    // TODO: 什么时候重构
    oldDataSource: [], // 存储查询出来的数据 用来比较 以 排出没有更改的数据
    pagination: false,
    roleTableFetchLoading: false,
    level: [],
    selectedRowKeys: [],
    visible: false,
    // 选择组织的框是否显示
  };

  /**
   * @param {Object} nextProps 下一个属性
   * @param {Object} prevState 上一个状态
   */
  static getDerivedStateFromProps(nextProps, prevState) {
    const nextState = {};
    const { level, isCreate } = nextProps;
    if (isCreate && prevState.pagination !== false) {
      // 新建分页没有 分页
      nextState.pagination = false;
    }
    if (level !== prevState.level) {
      nextState.level = level;
      nextState.levelMap = {};
      forEach(level, (l) => {
        nextState.levelMap[l.value] = l;
      });
    }
    return nextState;
  }

  /**
   * 将 hook 方法传递出去
   */
  componentDidMount() {
    this.init();
  }

  /**
   * 初始化数据
   * 编辑 + 加载用户角色
   * 重置form表单
   */
  init() {
    const { form } = this.props;
    form.resetFields();
    this.cancelDefaultParams.clear();
    this.handleRoleTableChange();
  }

  /**
   * 获取编辑完成的数据
   */
  @Bind()
  getEditFormData() {
    const { form, organizationId, userInfo } = this.props;
    const { dataSource, oldDataSource } = this.state;
    const memberRoleList = [];
    form.validateFields((err) => {
      if (!err) {
        const excludeArray = [];
        const validatingDataSource = dataSource.filter(
          (r) => r._status === 'create' || r._status === 'update'
        );
        const validateDataSource = getEditTableData(validatingDataSource);
        forEach(dataSource, (record) => {
          const r = find(validateDataSource, (or) => or.id === record.id);
          if (r) {
            // 是可以修改的数据
            const { startDateActive, endDateActive } = r;
            const omitRecord = omit(record, [
              'id',
              '_assignLevelValue',
              '_assignLevelValueMeaning',
              'defaultRoleIdUpdate',
            ]);
            const newRecord = {
              ...omitRecord, // 需要 _token 等字段
              roleId: record.id,
              assignLevel: record.assignLevel,
              assignLevelValue: record.assignLevelValue,
              memberType: record.memberType,
              memberId: userInfo.id,
              sourceId: organizationId,
              sourceType: record.sourceType,
              startDateActive: startDateActive && startDateActive.format(DEFAULT_DATE_FORMAT),
              endDateActive: endDateActive && endDateActive.format(DEFAULT_DATE_FORMAT),
            };
            // 由于这种写法 判断不了 哪些数据没有更新 所以全部保存
            if (!isEmpty(newRecord.assignLevel) && !isEmpty(newRecord.assignLevelValue)) {
              // 只有 当 旧数据修改后 才传给后端
              if (
                !oldDataSource.some(
                  (oldR) =>
                    oldR.id === record.id &&
                    record.assignLevel === oldR.assignLevel &&
                    record.assignLevelValue === oldR.assignLevelValue
                ) ||
                (r._status === 'update' && record.startDateActive !== newRecord.startDateActive) ||
                record.endDateActive !== newRecord.endDateActive
              ) {
                memberRoleList.push(newRecord);
              }
            }
            excludeArray.push(`assignLevel#${record.id}`, `assignLevelValue#${record.id}`);
          }
        });
      }
    });
    return memberRoleList;
  }

  /**
   * 新增角色模态框确认按钮点击
   */
  @Bind()
  handleRoleAddSaveBtnClick(roles) {
    const { dataSource = [], pagination = {} } = this.state;
    this.setState({
      dataSource: [
        ...map(roles, (r) => ({
          ...omit(r, ['assignLevel', 'assignLevelValue']), // FIXME: 将角色之前的 层级信息 去掉
          memberType: 'user',
          // sourceType: r.level,
          // 租户级 可以分配的 肯定是 租户级的
          assignLevel: 'organization',
          assignLevelValue: r.tenantId,
          assignLevelValueMeaning: r.tenantName,
          _assignLevelValue: r.tenantId,
          _assignLevelValueMeaning: r.tenantName,
          isNew: true,
          // 新加进来的 角色 都是可以管理的
          manageableFlag: 1,
          _status: 'create',
        })),
        ...dataSource,
      ],
      pagination: addItemsToPagination(roles.length, dataSource.length, pagination),
      visible: false,
    });
    return Promise.resolve();
  }

  /**
   * 新增角色模态框取消按钮点击
   */
  @Bind()
  handleRoleAddCancelBtnClick() {
    this.setState({
      visible: false,
    });
  }

  /**
   * 打开新增角色 选择模态框
   */
  @Bind()
  handleRoleAddBtnClick() {
    // if (isEmpty(noAllocRoles)) {
    //   Modal.warn({
    //     content: intl
    //       .get('hiam.subAccount.view.message.noEnoughRole')
    //       .d('可分配的角色已全部分配完毕'),
    //   });
    //   return;
    // }
    const { isCreate, initialValue = {} } = this.props;
    const { dataSource = [] } = this.state;
    const roleModalProps = {
      excludeRoleIds: [],
      excludeUserIds: [],
    };
    if (!isCreate) {
      roleModalProps.excludeUserIds.push(initialValue.id);
    }
    dataSource.forEach((r) => {
      if (r.isNew) {
        roleModalProps.excludeRoleIds.push(r.id);
      }
    });
    this.setState({
      visible: true,
      roleModalProps,
    });
  }

  @Bind()
  fetchRoles(fields) {
    const { fetchAllRoles, currentUser } = this.props;
    const params = {
      ...fields,
      userId: currentUser.id,
    };
    return fetchAllRoles(params);
  }

  /**
   * @param {String[]} selectedRowKeys 选中的rowKey
   */
  @Bind()
  handleRoleSelectionChange(selectedRowKeys) {
    this.setState({ selectedRowKeys });
  }

  /**
   * 删除选中的角色
   * 由于 租户级这边 删除后 是重新查询, 所以 不需要保存之前的 分页信息
   */
  @Bind()
  handleRoleRemoveBtnClick() {
    const { deleteRoles, userInfo, isCreate = true } = this.props;
    const { dataSource, selectedRowKeys, pagination } = this.state;
    const that = this;
    if (selectedRowKeys.length === 0) {
      Modal.error({
        content: intl.get('hiam.subAccount.view.message.chooseRoleFirst').d('请先选择要删除的角色'),
      });
      return;
    }
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.title').d('提示'),
      content: intl.get(`hiam.subAccount.view.message.title.content`).d('确定删除吗？'),
      onOk() {
        const ids = [];
        const newDataSource = [];
        dataSource.forEach((item) => {
          if (!item.isNew && selectedRowKeys.indexOf(item.id) >= 0) {
            ids.push({
              roleId: item.id,
              memberId: userInfo.id,
            });
          }
          if (!(item.isNew && selectedRowKeys.indexOf(item.id) >= 0)) {
            newDataSource.push(item);
          }
        });
        if (ids.length > 0) {
          deleteRoles(ids).then((res) => {
            if (res) {
              that.setState({
                dataSource: newDataSource,
                selectedRowKeys: [],
                // pagination: isCreate ? false: delItemsToPagination(selectedRowKeys.length, dataSource.length, pagination),
              });
              that.handleRoleTableChange();
              notification.success();
            }
          });
        } else {
          that.setState({
            dataSource: newDataSource,
            selectedRowKeys: [],
            pagination: isCreate
              ? false
              : delItemsToPagination(selectedRowKeys.length, dataSource.length, pagination),
          });
          notification.success();
        }
      },
    });
  }

  /**
   * 默认角色改变
   * @param {object} record 默认角色 改变
   */
  handleRoleDefaultChange(record) {
    if (record.defaultRole) {
      // 已经是 默认角色了 什么都不做 则取消默认角色并将该租户加入 删除角色租户中
      this.cancelDefaultParams.add(record.tenantId);
    }
    const defaultChangeTo = !record.defaultRole;
    const { dataSource = [] } = this.state;
    this.setState({
      dataSource: dataSource.map((item) => {
        if (item.id === record.id) {
          return {
            ...item,
            defaultRole: defaultChangeTo,
            defaultRoleIdUpdate: true,
          };
        }
        if (defaultChangeTo && item.tenantId === record.tenantId) {
          // item 取消同租户的 defaultRoleId
          if (item.defaultRole) {
            this.cancelDefaultParams.add(item.tenantId);
          }
          return {
            ...item,
            defaultRole: false,
            defaultRoleIdUpdate: item.defaultRole,
          };
        }
        return item;
      }),
    });
  }

  /**
   * 角色 table 分页改变
   * 如果是新增用户 分页是
   * @param {?object} page
   * @param {?object} filter
   * @param {?object} sort
   */
  @Bind()
  handleRoleTableChange(page, sort) {
    const { fetchUserRoles, userInfo = {} } = this.props;
    this.showRoleTableLoading();
    fetchUserRoles({ page, sort, userId: userInfo.id })
      .then((roleContent) => {
        // 在前面中已经 getResponse 了
        if (roleContent) {
          const dataSource = roleContent.content.map((r) => ({ ...r, _status: 'update' })) || [];
          this.setState({
            oldDataSource: cloneDeep(dataSource),
            dataSource,
            pagination: createPagination(roleContent),
          });
          // 翻页清空 已取消默认角色的租户
          this.cancelDefaultParams.clear();
        }
      })
      .finally(() => {
        this.hiddenRoleTableLoading();
      });
  }

  @Bind()
  showRoleTableLoading() {
    this.setState({ roleTableFetchLoading: true });
  }

  @Bind()
  hiddenRoleTableLoading() {
    this.setState({ roleTableFetchLoading: false });
  }

  @Bind()
  handleSearchFormAction() {
    const params = this.searchFormRef.current
      ? this.searchFormRef.current.props.form.getFieldsValue()
      : {};
    const { fetchUserRoles, userInfo = {} } = this.props;
    this.showRoleTableLoading();
    fetchUserRoles({ userId: userInfo.id, ...params })
      .then((roleContent) => {
        // 在前面中已经 getResponse 了
        if (roleContent) {
          const dataSource = roleContent.content.map((r) => ({ ...r, _status: 'update' })) || [];
          this.setState({
            oldDataSource: cloneDeep(dataSource),
            dataSource,
            pagination: createPagination(roleContent),
          });
          // 翻页清空 已取消默认角色的租户
          this.cancelDefaultParams.clear();
        }
      })
      .finally(() => {
        this.hiddenRoleTableLoading();
      });
  }

  /**
   * 渲染 分配角色Table
   */
  @Bind()
  renderRoleTable() {
    const {
      dataSource = [],
      selectedRowKeys = [],
      roleTableFetchLoading,
      pagination = false,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRoleSelectionChange,
    };
    const columns = [
      {
        title: intl.get('hiam.subAccount.model.role.name').d('角色名称'),
        dataIndex: 'name',
        width: 100,
      },
      {
        title: intl.get('entity.tenant.name').d('租户名称'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hiam.subAccount.model.role.startDateActive').d('起始时间'),
        key: 'startDateActive',
        width: 160,
        render: (_, record) => {
          const { $form } = record;
          const { getFieldDecorator } = $form;
          const dateFormat = getDateFormat();
          return (
            <Form.Item>
              {getFieldDecorator('startDateActive', {
                initialValue: record.startDateActive
                  ? moment(record.startDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabledDate={(currentDate) => {
                    return (
                      $form.getFieldValue('endDateActive') &&
                      moment($form.getFieldValue('endDateActive')).isBefore(currentDate, 'day')
                    );
                  }}
                />
              )}
            </Form.Item>
          );
        },
      },
      {
        title: intl.get('hiam.subAccount.model.role.endDateActive').d('失效时间'),
        key: 'endDateActive',
        width: 160,
        render: (_, record) => {
          const { $form } = record;
          const { getFieldDecorator } = $form;
          const dateFormat = getDateFormat();
          return (
            <Form.Item>
              {getFieldDecorator('endDateActive', {
                initialValue: record.endDateActive
                  ? moment(record.endDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabledDate={(currentDate) =>
                    $form.getFieldValue('startDateActive') &&
                    moment($form.getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          );
        },
      },
      {
        title: intl.get('hiam.subAccount.model.role.defaultRoleId').d('默认'),
        key: 'defaultRole',
        width: 60,
        render: (_, record) => {
          const { defaultRole, assignLevel } = record;
          if (assignLevel === 'organization' || assignLevel === 'org') {
            return (
              <Checkbox
                checked={defaultRole}
                onClick={() => {
                  this.handleRoleDefaultChange(record);
                }}
              />
            );
          }
          return null;
        },
      },
    ].filter(Boolean);
    return (
      <EditTable
        bordered
        rowKey="id"
        onChange={this.handleRoleTableChange}
        loading={roleTableFetchLoading}
        rowSelection={rowSelection}
        dataSource={dataSource}
        pagination={pagination}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
      />
    );
  }

  render() {
    const { selectedRowKeys, visible, roleModalProps = {} } = this.state;
    const {
      path,
      // isAdmin,
      // isCreate,
      // initialValue = {},
      loadingDistributeUsers,
      deleteRolesLoading = false,
      // currentUser: { currentRoleCode = '' },
    } = this.props;
    return (
      <>
        <SearchForm
          wrappedComponentRef={this.searchFormRef}
          onSearch={this.handleSearchFormAction}
        />
        <Form>
          <Row style={{ textAlign: 'right' }}>
            <Col span={23}>
              <Form.Item>
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.deleteRole`,
                      type: 'button',
                      meaning: '用户管理-删除角色',
                    },
                  ]}
                  style={{ marginRight: 8 }}
                  onClick={this.handleRoleRemoveBtnClick}
                  disabled={selectedRowKeys.length === 0}
                  loading={deleteRolesLoading}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.createRole`,
                      type: 'button',
                      meaning: '用户管理-新增角色',
                    },
                  ]}
                  style={{ marginRight: 10 }}
                  type="primary"
                  onClick={() => this.handleRoleAddBtnClick()}
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </ButtonPermission>
              </Form.Item>
            </Col>
            <Col span={1} />
          </Row>
          <Row type="flex">
            <Col span={23} className={styles['rule-table']}>
              {this.renderRoleTable()}
            </Col>
          </Row>
        </Form>
        {!!visible && (
          <RoleModal
            {...roleModalProps}
            visible={visible}
            loading={loadingDistributeUsers}
            fetchRoles={this.fetchRoles}
            onSave={this.handleRoleAddSaveBtnClick}
            onCancel={this.handleRoleAddCancelBtnClick}
          />
        )}
      </>
    );
  }
}
