/**
 * EditModal.js
 * 当编辑自己的帐号时, 角色时不可以新增和删除的
 * @date 2018-12-16
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import {
  Checkbox,
  Col,
  DatePicker,
  Form,
  Input,
  Modal,
  Row,
  Select,
  Spin,
  Switch,
  Tooltip,
  Icon,
} from 'hzero-ui';
import { differenceWith, find, forEach, isEmpty, isUndefined, join, map } from 'lodash';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import PropTypes from 'prop-types';

import Lov from 'components/Lov';
import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import {
  addItemsToPagination,
  createPagination,
  delItemsToPagination,
  getDateFormat,
  getEditTableData,
  tableScrollWidth,
} from 'utils/utils';
import { EMAIL, NOT_CHINA_PHONE, PHONE, CODE } from 'utils/regExp';
import {
  DEFAULT_DATE_FORMAT,
  EDIT_FORM_ITEM_LAYOUT_COL_3,
  FORM_COL_2_LAYOUT,
  MODAL_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import { VERSION_IS_OP } from 'utils/config';
import notification from 'utils/notification';
import { validatePasswordRule } from '@/utils/validator';

import styles from '../../index.less';
import RoleModal from './RoleModal';

@Form.create({ fieldNameProp: null })
export default class EditModal extends React.Component {
  state = {
    selectedRowKeys: [],
    roleModalProps: {}, // 新建角色框
    // 角色表格的信息
    dataSource: [],
    pagination: false,
    roleTableFetchLoading: false, // 角色加载数据 和 翻页改变
  };

  rolePaginationCache; // 角色 Table 分页信息的缓存

  // todo 最后 页面的 propTypes 推荐 全部删掉
  static propTypes = {
    fetchRoles: PropTypes.func.isRequired,
    onOk: PropTypes.func.isRequired,
  };

  static getDerivedStateFromProps(nextProps, prevState) {
    const { isCreate = true } = nextProps;
    const nextState = {};
    if (isCreate && prevState.pagination !== false) {
      nextState.pagination = false;
    }
    if (isEmpty(nextState)) {
      return null;
    }
    return nextState;
  }

  componentDidMount() {
    const { isCreate = true, fetchDetailData, detailRecord } = this.props;
    if (!isCreate) {
      fetchDetailData(detailRecord).then((editRecord) => {
        this.fetchRolesFirst(editRecord);
      });
    }
  }

  @Bind()
  changeCountryId() {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({ regionId: undefined });
  }

  fetchRolesFirst(editRecord = {}) {
    const { fetchCurrentUserRoles, isCreate = true, isAdmin = true } = this.props;
    if (!isCreate) {
      this.showRoleTableLoading();
      fetchCurrentUserRoles({ userId: editRecord.id })
        .then((roleContent) => {
          // 在前面中已经 getResponse 了
          if (roleContent) {
            this.setState({
              dataSource: isAdmin
                ? roleContent.content.map((r) => ({ ...r, _status: 'update', _isRemote: true }))
                : map(roleContent.content, (r) => ({ ...r, _status: 'update', _isRemote: true })),
              pagination: createPagination(roleContent),
            });
            // 翻页清空 已取消默认角色的租户
          }
        })
        .finally(() => {
          this.hiddenRoleTableLoading();
        });
    }
  }

  /**
   * 角色 table 分页改变
   * 如果是新增用户 分页是
   * @param {object} page
   * @param {object} filter
   * @param {object} sort
   */
  @Bind()
  handleRoleTableChange(page, filter, sort) {
    const { fetchCurrentUserRoles, isCreate = true, editRecord = {} } = this.props;
    if (!isCreate) {
      this.showRoleTableLoading();
      fetchCurrentUserRoles({ page, sort, userId: editRecord.id })
        .then((roleContent) => {
          // 在前面中已经 getResponse 了
          if (roleContent) {
            this.setState({
              dataSource: map(roleContent.content, (r) => ({
                ...r,
                _status: 'update',
                _isRemote: true,
              })),
              pagination: createPagination(roleContent),
            });
            // 翻页清空 已取消默认角色的租户
          }
        })
        .finally(() => {
          this.hiddenRoleTableLoading();
        });
    }
  }

  @Bind()
  showRoleTableLoading() {
    this.setState({ roleTableFetchLoading: true });
  }

  @Bind()
  hiddenRoleTableLoading() {
    this.setState({ roleTableFetchLoading: false });
  }

  // @Bind()
  // isRoleCanUpdate(role = {}, account = {}) {
  //   if (role._status === 'create') {
  //     return true;
  //   }
  //   // 只检查了 层级 为 平台 和 租户的情况
  //   if (role.assignLevel === 'site' || role.assignLevel === 'organization') {
  //     if (role.assignLevelValue !== (account.organizationId || account.tenantId)) {
  //       return false;
  //     }
  //   }
  //   return true;
  // }

  getRoleColumns() {
    const { editRecord, isCreate } = this.props;
    return [
      {
        title: intl.get('hiam.subAccount.model.role.name').d('角色名称'),
        dataIndex: 'name',
        render: (v, record) => {
          if (record.tipMessage) {
            return (
              <>
                <span>{v}</span>
                <Tooltip title={record.tipMessage}>
                  &nbsp;
                  <Icon type="exclamation-circle-o" />
                </Tooltip>
              </>
            );
          } else {
            return v;
          }
        },
      },
      !VERSION_IS_OP && {
        title: intl.get('entity.tenant.name').d('租户名称'),
        dataIndex: 'tenantName',
        width: 250,
      },
      {
        title: intl.get('hiam.subAccount.model.role.startDateActive').d('起始时间'),
        key: 'startDateActive',
        width: 140,
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
                  disabled={record.removableFlag === 0 || record.manageableFlag === 0}
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
        width: 140,
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
                  disabled={record.removableFlag === 0 || record.manageableFlag === 0}
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
        key: 'defaultRoleId',
        width: 80,
        render: (_, record) => {
          const { defaultRole, level } = record;
          if (level === 'organization' || level === 'org' || level === 'site') {
            return (
              <Checkbox
                checked={defaultRole}
                disabled={isCreate ? false : editRecord.userType !== 'P'}
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
  }

  @Bind()
  handleRowSelectChange(_, selectedRows = []) {
    this.setState({
      selectedRowKeys: map(selectedRows, (r) => r.id),
      selectedRows,
    });
  }

  @Bind()
  handleRoleDefaultChange(record) {
    const { dataSource = [] } = this.state;
    const defaultChangeTo = !record.defaultRole;
    const newDataSource = dataSource.map((item) => {
      if (item.id === record.id) {
        return {
          ...item,
          defaultRole: defaultChangeTo,
          defaultRoleIdUpdate: true,
        };
      } else if (defaultChangeTo && item.tenantId === record.tenantId) {
        // item 取消同租户的 defaultRole
        return {
          ...item,
          defaultRole: false,
          defaultRoleIdUpdate: item.defaultRole,
        };
      } else {
        return item;
      }
    });
    this.setState({
      dataSource: newDataSource,
    });
  }

  @Bind()
  handleRoleRemove() {
    Modal.confirm({
      title: intl.get(`hzero.common.message.confirm.title`).d('提示'),
      content: intl.get(`hiam.subAccount.view.message.title.content`).d('确定删除吗？'),
      onOk: () => {
        const { selectedRows = [] } = this.state;
        const { editRecord = {}, onRoleRemove } = this.props;
        const remoteRemoveDataSource = [];
        forEach(selectedRows, (r) => {
          if (r._isRemote) {
            remoteRemoveDataSource.push({
              roleId: r.id,
              memberId: editRecord.id,
            });
          }
        });
        if (remoteRemoveDataSource.length > 0) {
          onRoleRemove(remoteRemoveDataSource).then((res) => {
            if (res) {
              this.removeLocaleRoles();
              notification.success();
            }
          });
        } else {
          this.removeLocaleRoles();
          notification.success();
        }
      },
    });
  }

  @Bind()
  removeLocaleRoles() {
    const nextState = {};
    const { dataSource = [], selectedRowKeys = [], pagination = {} } = this.state;
    const { isCreate = true } = this.props;
    nextState.dataSource = differenceWith(dataSource, selectedRowKeys, (r1, r2) => r1.id === r2);
    nextState.pagination = isCreate
      ? false
      : delItemsToPagination(selectedRowKeys.length, dataSource.length, pagination);
    nextState.selectedRowKeys = [];
    nextState.selectedRows = [];
    this.setState(nextState);
  }

  @Bind()
  handleRoleCreate() {
    const { editRecord = {}, isCreate = true } = this.props;
    const { dataSource = [] } = this.state;
    const excludeUserIds = []; // 当前编辑帐号的帐号id( 需要的排除帐号对应的角色 )
    const excludeRoleIds = [];
    if (!isCreate) {
      excludeUserIds.push(editRecord.id);
    }
    dataSource.forEach((role) => {
      if (role._status === 'create') {
        excludeRoleIds.push(role.id);
      }
    });
    this.setState({
      roleModalProps: {
        visible: true,
        excludeUserIds,
        excludeRoleIds,
      },
    });
  }

  @Bind()
  handleRoleCreateSave(roles) {
    if (roles && roles.length > 0) {
      const { dataSource = [], pagination = {} } = this.state;
      const { isCreate = true } = this.props;
      this.setState({
        dataSource: [
          ...dataSource,
          ...roles.map((role) => {
            // 保存时的数据结构
            // assignLevel: "organization" // 层级
            // assignLevelValue: 180149 // 层级值
            // memberType: "user" // 恒定为 user
            // roleId: 150191 // 角色id
            // sourceId: 180149 // 当前用户所属租户
            // sourceType: "organization" // 当前租户层级

            // todo 角色层级值 的取值 到底需要怎么做
            // warn: 如果 角色 的 level 为 site 那么 这个角色的 租户 为 0租户
            const assignLevel = 'organization';
            const assignLevelValue = role.tenantId;
            const assignLevelValueMeaning = role.tenantName;
            return {
              level: role.level, // 需要将 role.level 传递下来, 下面的判断需要
              // 角色所属的租户
              tenantId: role.tenantId,
              // 角色所属的租户名称
              tenantName: role.tenantName,
              // 角色名称 展示使用
              name: role.name,
              // 用来限制 层级值
              parentRoleAssignLevel: role.parentRoleAssignLevel,
              // 角色id
              id: role.id,
              // sourceType 是 选中角色的 level
              sourceType: role.level,
              // memberType 是固定的 user
              memberType: 'user',
              // assignLevel 和 assignLevelValue 是需要自己填写的
              assignLevel,
              assignLevelValue,
              assignLevelValueMeaning,
              _status: 'create',
              // 新加进来的 角色 都是可以管理的
              manageableFlag: 1,
            };
          }),
        ],
        pagination: isCreate
          ? false
          : addItemsToPagination(roles.length, dataSource.length, pagination),
      });
    }
    this.handleRoleCreateCancel();
  }

  @Bind()
  handleRoleCreateCancel() {
    this.setState({
      roleModalProps: {
        visible: false,
      },
    });
  }

  @Bind()
  handleEditModalOk() {
    const { form, isCreate } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const { dataSource = [] } = this.state;
        const memberRoleList = [];
        // defaultRoleIds 不使用了, 使用 [ { roleId, tenantId, defaultFlag } ]
        const defaultRoles = [];

        const validatingDataSource = dataSource.filter(
          (r) => r._status === 'create' || r._status === 'update'
        );
        const validateDataSource = getEditTableData(validatingDataSource);
        dataSource.forEach((oldR) => {
          // // 如果 assignLevelValue 有值, 则使用他的值, 否则通过 层级 来确定层级值
          // const assignLevelValue = isUndefined(r.assignLevelValue)
          //   ? r.assignLevel === 'site' || r.assignLevel === 'organization'
          //     ? fieldsValue.organizationId
          //     : undefined
          //   : r.assignLevelValue;
          // 去掉前端添加的字段
          const r = find(validateDataSource, (or) => or.id === oldR.id);
          if (r) {
            // 是可以修改的数据
            const { defaultRoleIdUpdate, id, startDateActive, endDateActive, ...oriRecord } = r;
            const newRole = {
              ...oriRecord, // 需要得到之前角色的 id 等信息
              assignLevel: r.assignLevel, // 层级
              // 当 层级 为 组织 和 租户 时, 层级值为 租户id
              assignLevelValue: r.assignLevelValue, // 层级值
              memberType: 'user', // 恒定为 user
              roleId: id, // 角色id
              sourceId: fieldsValue.organizationId, // 当前用户所属租户
              sourceType: r.sourceType, // 当前租户层级
              startDateActive: startDateActive && startDateActive.format(DEFAULT_DATE_FORMAT),
              endDateActive: endDateActive && endDateActive.format(DEFAULT_DATE_FORMAT),
            };
            if (defaultRoleIdUpdate) {
              defaultRoles.push({
                roleId: newRole.roleId,
                tenantId: newRole.tenantId,
                defaultFlag: newRole.defaultRole === false ? 0 : 1,
              });
            }
            if (
              // 数据校验
              !isUndefined(newRole.roleId) &&
              !isUndefined(newRole.tenantId) &&
              // 重复校验
              (oldR._status === 'create' ||
                (oldR._status === 'update' && oldR.startDateActive !== newRole.startDateActive) ||
                (oldR._status === 'update' && oldR.endDateActive !== newRole.endDateActive))
              // 如果是正常流程 这两个是一定相同的
              // || oldR.sourceId !== newRole.sourceId
              // || oldR.sourceType !== newRole.sourceType
            ) {
              memberRoleList.push(newRole);
            }
          } else {
            if (oldR.defaultRoleIdUpdate && oldR.defaultRole === false) {
              // 该角色是 租户/组织 层 默认角色更新过 且 取消了
              defaultRoles.push({
                roleId: oldR.id,
                tenantId: oldR.tenantId,
                defaultFlag: 0,
              });
            }
            if (oldR.defaultRoleIdUpdate && oldR.defaultRole !== false) {
              defaultRoles.push({
                roleId: oldR.id,
                tenantId: oldR.tenantId,
                defaultFlag: 1,
              });
            }
          }
        });
        if (dataSource.length !== 0 && validatingDataSource.length !== validateDataSource.length) {
          // 必须要有角色, 且校验通过
          return;
        }
        const { editRecord = {}, onOk } = this.props;
        const saveData = {
          ...editRecord,
          ...fieldsValue,
          startDateActive: fieldsValue.startDateActive.format(DEFAULT_DATE_FORMAT),
          endDateActive: fieldsValue.endDateActive
            ? fieldsValue.endDateActive.format(DEFAULT_DATE_FORMAT)
            : undefined,
          birthday: fieldsValue.birthday
            ? fieldsValue.birthday.format(DEFAULT_DATE_FORMAT)
            : undefined,
          defaultRoles,
          memberRoleList,
        };
        const newSaveData = isCreate
          ? {
              userType: 'P',
              ...saveData,
            }
          : saveData;
        onOk(newSaveData);
      }
    });
  }

  /**
   * @date 2019-06-13
   * 区号改变 需要 重置手机号的校验状态
   */
  @Bind()
  reValidationPhone(value) {
    const { form } = this.props;
    const prevInternationalTelCode = form.getFieldValue('internationalTelCode');
    if (value === '+86' || prevInternationalTelCode === '+86') {
      // 只要 +86 出现在 中间态 就需要重新手动校验 phone
      const curPhone = form.getFieldValue('phone');
      let errors = null;
      if (curPhone) {
        const testReg = value === '+86' ? PHONE : NOT_CHINA_PHONE;
        if (!testReg.test(curPhone)) {
          errors = [new Error(intl.get('hzero.common.validation.phone').d('手机格式不正确'))];
        }
      } else {
        errors = [
          new Error(
            intl.get('hzero.common.validation.notNull', {
              name: intl.get('hiam.subAccount.model.user.phone').d('手机号码'),
            })
          ),
        ];
      }
      form.setFields({
        phone: {
          value: curPhone,
          errors,
        },
      });
    }
  }

  // #region 验证密码修改后 是否和 确认密码一致

  /**
   * 检查 确认密码是否与密码一致
   */
  @Bind()
  validatePasswordRepeatForPassword(e) {
    const { form } = this.props;

    const anotherPassword = form.getFieldValue('anotherPassword');
    const anotherPasswordField = {
      value: anotherPassword,
    };
    if (e.target.value) {
      if (e.target.value === anotherPassword) {
        anotherPasswordField.errors = null;
      } else {
        anotherPasswordField.errors = [
          new Error(
            intl.get('hiam.subAccount.view.validation.passwordSame').d('确认密码必须与密码一致')
          ),
        ];
      }
    } else {
      anotherPasswordField.errors = null;
    }
    form.setFields({
      anotherPassword: anotherPasswordField,
    });
  }

  @Bind()
  tenantChange(value, record) {
    const { getPasswordRule } = this.props;
    getPasswordRule(record.tenantId);
  }

  renderForm() {
    const {
      path,
      form,
      editRecord = {},
      isCreate = true,
      isAdmin = true,
      roleRemoveLoading,
      idd = [],
      gender = [],
      currentUser: { currentRoleCode = '' },
      passwordTipMsg = {},
    } = this.props;
    const {
      selectedRowKeys = [],
      dataSource = [],
      pagination = false,
      roleTableFetchLoading,
    } = this.state;
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
      getCheckboxProps: (record) => ({
        disabled: record.removableFlag === 0,
      }),
    };
    const isSiteFlag = currentRoleCode === 'role/site/default/administrator';
    const emailError = form.getFieldError('email');
    const sameEmail = editRecord.email === form.getFieldValue('email');
    const phoneError = form.getFieldError('phone');
    const samePhone = editRecord.phone === form.getFieldValue('phone');
    const dateFormat = getDateFormat();
    const roleColumns = this.getRoleColumns(isSiteFlag);
    // eslint-disable-next-line no-nested-ternary
    const roleNode = isSiteFlag ? (
      <React.Fragment key="role-no-same-user-btn">
        <Row style={{ textAlign: 'right', marginBottom: 16 }}>
          <Col span={23}>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.deleteUser`,
                  type: 'button',
                  meaning: '子账户管理-删除用户',
                },
              ]}
              style={
                editRecord.userType === 'P' || isCreate ? { marginRight: 8 } : { display: 'none' }
              }
              onClick={this.handleRoleRemove}
              disabled={selectedRowKeys.length === 0}
              loading={roleRemoveLoading}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
            <ButtonPermission
              style={
                editRecord.userType !== 'P' && !isCreate
                  ? { display: 'none' }
                  : { display: 'inline' }
              }
              permissionList={[
                {
                  code: `${path}.button.createUser`,
                  type: 'button',
                  meaning: '子账户管理-新建用户',
                },
              ]}
              type="primary"
              onClick={this.handleRoleCreate}
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
          </Col>
          <Col span={1} />
        </Row>
        <Row type="flex">
          <Col span={3} />
          <Col span={20} className={styles['rule-table']}>
            <EditTable
              bordered
              rowKey="id"
              dataSource={dataSource}
              pagination={pagination}
              loading={roleTableFetchLoading}
              columns={roleColumns}
              scroll={{ x: tableScrollWidth(roleColumns) }}
              onChange={this.handleRoleTableChange}
              rowSelection={rowSelection}
            />
          </Col>
        </Row>
      </React.Fragment>
    ) : isAdmin ? (
      <Col key="role-same-user">
        <Form.Item
          label={intl.get('hiam.subAccount.view.message.title.role').d('角色')}
          {...EDIT_FORM_ITEM_LAYOUT_COL_3}
        >
          <EditTable
            bordered
            pagination={false}
            rowKey="id"
            columns={roleColumns}
            scroll={{ x: tableScrollWidth(roleColumns) }}
            rowSelection={rowSelection}
            dataSource={dataSource}
          />
        </Form.Item>
      </Col>
    ) : (
      <React.Fragment key="role-no-same-user-btn">
        <Row style={{ textAlign: 'right', marginBottom: 16 }}>
          <Col span={23}>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.deleteDiffUser`,
                  type: 'button',
                  meaning: '子账户管理-删除用户',
                },
              ]}
              style={
                editRecord.userType === 'P' || isCreate ? { marginRight: 8 } : { display: 'none' }
              }
              onClick={this.handleRoleRemove}
              disabled={selectedRowKeys.length === 0}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.createDiffUser`,
                  type: 'button',
                  meaning: '子账户管理-新建用户',
                },
              ]}
              type="primary"
              onClick={this.handleRoleCreate}
              style={
                editRecord.userType !== 'P' && !isCreate
                  ? { display: 'none' }
                  : { display: 'inline' }
              }
            >
              {intl.get('hzero.common.button.create').d('新建')}
            </ButtonPermission>
          </Col>
          <Col span={1} />
        </Row>
        <Row type="flex">
          <Col span={3} />
          <Col span={20} className={styles['rule-table']}>
            <EditTable
              bordered
              rowKey="id"
              dataSource={dataSource}
              pagination={pagination}
              loading={roleTableFetchLoading}
              columns={roleColumns}
              scroll={{ x: tableScrollWidth(roleColumns) }}
              onChange={this.handleRoleTableChange}
              rowSelection={isAdmin ? null : rowSelection}
            />
          </Col>
        </Row>
      </React.Fragment>
    );

    return (
      <Form>
        <Row type="flex">
          <Col key="loginName" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={
                <span>
                  {intl.get('hiam.subAccount.model.user.loginName').d('账号')}&nbsp;
                  <Tooltip
                    title={intl
                      .get('hiam.subAccount.view.message.loginName.tooltip')
                      .d('不输入账户则自动生成')}
                  >
                    <Icon type="question-circle-o" />
                  </Tooltip>
                </span>
              }
            >
              {form.getFieldDecorator('loginName', {
                initialValue: editRecord.loginName,
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                  isCreate && {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ].filter(Boolean),
              })(<Input disabled={!isCreate} />)}
            </Form.Item>
          </Col>
          <Col key="realName" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.realName').d('名称')}
            >
              {form.getFieldDecorator('realName', {
                initialValue: editRecord.realName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.subAccount.model.user.realName').d('名称'),
                    }),
                  },
                  {
                    max: 40,
                    message: intl.get('hzero.common.validation.max', {
                      max: 40,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col key="birthday" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              key="birthday"
              label={intl.get('hiam.subAccount.model.user.birthday').d('出生日期')}
            >
              {form.getFieldDecorator('birthday', {
                initialValue: editRecord.birthday
                  ? moment(editRecord.birthday, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(<DatePicker format={dateFormat} style={{ width: '100%' }} placeholder="" />)}
            </Form.Item>
          </Col>
          <Col key="nickname" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.nickname').d('昵称')}
            >
              {form.getFieldDecorator('nickname', {
                initialValue: editRecord.nickname,
                rules: [
                  {
                    max: 10,
                    message: intl.get('hzero.common.validation.max', {
                      max: 10,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col key="gender" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.gender').d('性别')}
            >
              {form.getFieldDecorator('gender', {
                initialValue: isUndefined(editRecord.gender) ? '' : `${editRecord.gender}`,
              })(
                <Select allowClear>
                  {map(gender, (item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col key="country" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.countryId').d('国家')}
            >
              {form.getFieldDecorator('countryId', {
                initialValue: editRecord.countryId,
              })(
                <Lov
                  code="HPFM.COUNTRY"
                  onChange={this.changeCountryId}
                  textValue={editRecord.countryName}
                  queryParams={{ enabledFlag: 1 }}
                  // textField="tenantName"
                  // disabled={!isCreate}
                />
              )}
            </Form.Item>
          </Col>
          <Col key="regionId" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.regionId').d('地区')}
            >
              {form.getFieldDecorator('regionId', {
                initialValue: editRecord.regionId,
              })(
                <Lov
                  code="HPFM.REGION"
                  queryParams={{
                    countryId: form.getFieldValue('countryId'),
                  }}
                  textValue={editRecord.regionName}
                  // textField="tenantName"
                  // disabled={!isCreate}
                />
              )}
            </Form.Item>
          </Col>
          <Col key="addressDetail" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.addressDetail').d('详细地址')}
            >
              {form.getFieldDecorator('addressDetail', {
                initialValue: editRecord.addressDetail,
                rules: [
                  {
                    max: 50,
                    message: intl.get('hzero.common.validation.max', {
                      max: 50,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          {isCreate && (
            <Col key="tenant" {...FORM_COL_2_LAYOUT}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.user.tenant').d('所属租户')}
              >
                {form.getFieldDecorator('organizationId', {
                  initialValue: editRecord.organizationId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.subAccount.model.user.tenant').d('所属租户'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HPFM.TENANT"
                    textValue={editRecord.tenantName}
                    textField="tenantName"
                    disabled={!isCreate}
                    onChange={this.tenantChange}
                  />
                )}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_2_LAYOUT} key="email">
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.email').d('邮箱')}
              {...(isCreate
                ? {}
                : {
                    hasFeedback: true,
                    // eslint-disable-next-line no-nested-ternary
                    help: emailError
                      ? join(emailError)
                      : (sameEmail && editRecord.email && editRecord.emailCheckFlag) ||
                        !form.getFieldValue('email')
                      ? ''
                      : intl.get('hiam.subAccount.view.validation.emailNotCheck').d('邮箱未验证'),
                    // eslint-disable-next-line no-nested-ternary
                    validateStatus: emailError
                      ? 'error'
                      : // eslint-disable-next-line no-nested-ternary
                      sameEmail && editRecord.email && editRecord.emailCheckFlag
                      ? 'success'
                      : form.getFieldValue('email')
                      ? 'warning'
                      : undefined,
                  })}
            >
              {form.getFieldDecorator('email', {
                initialValue: editRecord.email,
                rules: [
                  {
                    pattern: EMAIL,
                    message: intl.get('hzero.common.validation.email').d('邮箱格式不正确'),
                  },
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', {
                      max: 128,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col key="phone" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.phone').d('手机号码')}
              {...(isCreate
                ? {}
                : {
                    hasFeedback: true,
                    // eslint-disable-next-line no-nested-ternary
                    help: phoneError
                      ? join(phoneError)
                      : editRecord.phoneCheckFlag
                      ? ''
                      : intl
                          .get('hiam.subAccount.view.validation.phoneNotCheck')
                          .d('手机号码未验证'),
                    // eslint-disable-next-line no-nested-ternary
                    validateStatus: phoneError
                      ? 'error'
                      : samePhone && editRecord.phoneCheckFlag
                      ? 'success'
                      : 'warning',
                  })}
            >
              {form.getFieldDecorator('phone', {
                initialValue: editRecord.phone,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hiam.subAccount.model.user.phone').d('手机号码'),
                    }),
                  },
                  {
                    pattern:
                      form.getFieldValue('internationalTelCode') === '+86'
                        ? PHONE
                        : NOT_CHINA_PHONE,
                    message: intl.get('hzero.common.validation.phone').d('手机格式不正确'),
                  },
                ],
              })(
                <Input
                  addonBefore={form.getFieldDecorator('internationalTelCode', {
                    initialValue:
                      editRecord.internationalTelCode || (idd[0] && idd[0].value) || '+86',
                  })(
                    <Select onChange={this.reValidationPhone}>
                      {map(idd, (r) => (
                        <Select.Option key={r.value} value={r.value}>
                          {r.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                />
              )}
            </Form.Item>
          </Col>
          {isCreate && (
            <Col key="password" {...FORM_COL_2_LAYOUT}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={
                  <span>
                    {intl.get('hiam.subAccount.model.user.password').d('密码')}&nbsp;
                    <Tooltip
                      title={intl
                        .get('hiam.subAccount.view.message.password.tooltip')
                        .d('不输入密码则使用默认密码')}
                    >
                      <Icon type="question-circle-o" />
                    </Tooltip>
                  </span>
                }
              >
                {form.getFieldDecorator('password', {
                  rules: [
                    {
                      validator: (_, value, callback) => {
                        validatePasswordRule(value, callback, {
                          ...passwordTipMsg,
                          loginName: form.getFieldValue('loginName'),
                        });
                      },
                    },
                    {
                      max: 110,
                      message: intl.get('hzero.common.validation.max', {
                        max: 110,
                      }),
                    },
                  ],
                })(
                  <Input
                    type="password"
                    autocomplete="new-password"
                    onChange={this.validatePasswordRepeatForPassword}
                  />
                )}
              </Form.Item>
            </Col>
          )}
          <Col key="startDateActive" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.active.from').d('有效日期从')}
            >
              {form.getFieldDecorator('startDateActive', {
                initialValue: editRecord.startDateActive
                  ? moment(editRecord.startDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hzero.common.date.active.from').d('有效日期从'),
                    }),
                  },
                ],
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder={null}
                  disabledDate={(currentDate) =>
                    form.getFieldValue('endDateActive') &&
                    moment(form.getFieldValue('endDateActive')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
          <Col key="endDateActive" {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...MODAL_FORM_ITEM_LAYOUT}
              key="endDateActive"
              label={intl.get('hzero.common.date.active.to').d('有效日期至')}
            >
              {form.getFieldDecorator('endDateActive', {
                initialValue: editRecord.endDateActive
                  ? moment(editRecord.endDateActive, DEFAULT_DATE_FORMAT)
                  : undefined,
              })(
                <DatePicker
                  format={dateFormat}
                  style={{ width: '100%' }}
                  placeholder=""
                  disabledDate={(currentDate) =>
                    form.getFieldValue('startDateActive') &&
                    moment(form.getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
          {isCreate || (
            <Col key="tenant" {...FORM_COL_2_LAYOUT}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.user.tenant').d('所属租户')}
              >
                {form.getFieldDecorator('organizationId', {
                  initialValue: editRecord.organizationId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.subAccount.model.user.tenant').d('所属租户'),
                      }),
                    },
                  ],
                })(
                  <Lov code="HPFM.TENANT" textValue={editRecord.tenantName} disabled={!isCreate} />
                )}
              </Form.Item>
            </Col>
          )}
          {isCreate || (
            <Col key="enabled" {...FORM_COL_2_LAYOUT}>
              <Form.Item
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.subAccount.model.user.enabled').d('冻结')}
              >
                {form.getFieldDecorator('enabled', {
                  initialValue: isUndefined(editRecord.enabled) ? true : editRecord.enabled,
                })(<Switch checkedValue={false} unCheckedValue />)}
              </Form.Item>
            </Col>
          )}
        </Row>
        <Row>{roleNode}</Row>
      </Form>
    );
  }

  render() {
    const {
      form,
      editRecord = {},
      isCreate,
      fetchRoles,
      queryDetailLoading = false,
      labelList,
      ...modalProps
    } = this.props;
    // todo 租户id 明天问下 明伟哥。
    const tenantId = 0;
    const { roleModalProps = {} } = this.state;
    return (
      <Modal
        title={
          isCreate
            ? intl.get('hiam.subAccount.view.message.title.userCreate').d('账号新建')
            : intl.get('hiam.subAccount.view.message.title.userEdit').d('账号编辑')
        }
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        width={1000}
        {...modalProps}
        onOk={this.handleEditModalOk}
      >
        <Spin spinning={queryDetailLoading}>
          {this.renderForm()}
          {roleModalProps.visible && (
            <RoleModal
              {...roleModalProps}
              fetchRoles={fetchRoles}
              onSave={this.handleRoleCreateSave}
              onCancel={this.handleRoleCreateCancel}
              tenantId={tenantId}
              id={editRecord.id}
              labelList={labelList}
            />
          )}
        </Spin>
      </Modal>
    );
  }
}
