/**
 * @date 2018-12-17
 * @author WY <yang.wang06@hand-china.com>
 */

import { isEmpty, isUndefined } from 'lodash';
import { createPagination, getResponse, setSession } from 'utils/utils';
import { queryMapIdpValue, queryUnifyIdpValue, getPublicKey } from 'hzero-front/lib/services/api'; // 相对路径
import notification from 'utils/notification';
import {
  addUserGroup,
  apiFieldApiQuery,
  apiFieldFieldPermissionCreate,
  apiFieldFieldPermissionQuery,
  apiFieldFieldPermissionRemove,
  apiFieldFieldPermissionUpdate,
  deleteRoles,
  deleteUserGroup,
  querySubAccountOrgList,
  queryUnitsTree,
  subAccountOrgCreateOne,
  subAccountOrgGroupCurrent,
  subAccountOrgGroupQueryAll,
  subAccountOrgQuery,
  queryLabelList,
  subAccountOrgRoleCurrent,
  subAccountOrgRoleQueryAll,
  subAccountOrgUpdateOne,
  subAccountOrgUpdatePassword,
  subAccountSiteUserUnlock,
  queryAssignableSecGrp,
  queryFieldPermission,
  queryDataDimension,
  querySecurityGroup,
  queryFieldConfig,
  assignSecGrp,
  deleteSecGrp,
  queryTabList,
  queryDimension,
  queryEmployee,
  postCaptcha,
  resetPassword,
} from '../services/subAccountOrgService';
import { getPasswordRule } from '../services/commonService';
// import uuid from 'uuid/v4';

export default {
  namespace: 'subAccountOrg',
  state: {
    enumMap: {},
    // 可分配角色
    createSubRoles: [],
    dataSource: [],
    pagination: {}, // 分页信息
    editFormProps: {},
    editModalProps: {},
    passwordProps: {},
    phoneProps: {},
    // 组织树
    unitsTree: [],

    requestMethod: [], // 请求方式
    fieldType: [], // 字段类型
    apiDataSource: [], // 接口数据源
    apiPagination: {}, // 接口分页
    fieldPermissionDataSource: [], // 字段权限数据源
    fieldPermissionPagination: {}, // 字段权限分页
    passwordTipMsg: {},
    publicKey: '', // 公钥
    secGrpList: [], // 安全组列表
    secGrpPagination: {}, // 安全组分页
    secGrpAddModalList: [], // 可分配安全组列表
    secGrpAddModalPagination: {}, // 可分配安全组分页
    secGrpFieldPermissionList: [], // 安全组字段权限列表
    secGrpFieldPermissionPagination: {}, // 安全组字段权限分页
    secGrpDimensionList: [], // 安全组数据权限维度列表
    secGrpDimensionPagination: {}, // 安全组数据权限维度分页
    secGrpDataPermissionTabList: [], // 安全组数据权限tab页
    dimensionList: [],
    employeePagination: {},
    employeeList: [],
    labelList: [],
  },
  effects: {
    *fetchList({ payload }, { call, put }) {
      const res = getResponse(yield call(querySubAccountOrgList, payload));
      if (!isEmpty(res)) {
        yield put({
          type: 'updateState',
          payload: {
            dataSource: res.content,
            pagination: createPagination(res),
          },
        });
      }
    },

    *getPublicKey(_, { call, put }) {
      const res = yield call(getPublicKey);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            publicKey: res.publicKey,
          },
        });
      }
      return res;
    },

    // 获取密码校验规则
    *getPasswordRule({ payload }, { call, put }) {
      const res = getResponse(yield call(getPasswordRule, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            passwordTipMsg: isUndefined(payload.forceCodeVerify)
              ? res
              : { ...res, forceCodeVerify: payload.forceCodeVerify },
          },
        });
      }
      return res;
    },

    *fetchDetail({ payload }, { call }) {
      const subAccountDetail = yield call(subAccountOrgQuery, payload);
      return getResponse(subAccountDetail);
    },

    // 获取授权类型信息
    *queryLabelList({ payload }, { call, put }) {
      const res = yield call(queryLabelList, payload);
      if (res && !res.failed) {
        yield put({
          type: 'updateState',
          payload: {
            labelList: res,
          },
        });
      }
    },
    // 更新账号信息
    *updateOne({ payload }, { call }) {
      const res = getResponse(yield call(subAccountOrgUpdateOne, payload));
      return res;
    },
    // 创建新的账号
    *createOne({ payload }, { call }) {
      const res = getResponse(yield call(subAccountOrgCreateOne, payload));
      return res;
    },
    // 当前登录用户所拥有的id
    *roleQueryAll({ payload }, { call, put }) {
      const res = yield call(subAccountOrgRoleQueryAll, payload);
      const createSubRoles = getResponse(res);
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            createSubRoles,
          },
        });
      }
      return createSubRoles;
    },
    // 查询当前用户所拥有的角色
    *roleCurrent({ payload }, { call }) {
      const res = yield call(subAccountOrgRoleCurrent, payload);
      return getResponse(res);
    },
    // 获取快码
    *fetchEnum(_, { call, put }) {
      const res = getResponse(
        yield call(queryMapIdpValue, {
          level: 'HIAM.RESOURCE_LEVEL',
          authorityType: 'HIAM.AUTHORITY_TYPE_CODE',
          idd: 'HPFM.IDD', // 国际化手机号前缀
          gender: 'HPFM.GENDER', // 性别
          userType: 'HIAM.USER_TYPE',
        })
      );
      if (!isEmpty(res)) {
        yield put({
          type: 'updateState',
          payload: {
            enumMap: res,
          },
        });
      }
    },
    // 查询 组织
    *queryUnitsTree({ payload: { params } }, { call, put }) {
      const res = yield call(queryUnitsTree, params);
      const response = getResponse(res);
      yield put({
        type: 'updateState',
        payload: {
          unitsTree: !isEmpty(response) ? response : [],
        },
      });
    },
    // 打开修改密码
    *openPassword({ payload }, { put, select }) {
      const { validCodeLimitTimeEnd } = yield select((state) => state.subAccountOrg.passwordProps);
      const { userInfo } = payload;
      yield put({
        type: 'updateState',
        payload: {
          passwordProps: {
            visible: true,
            userInfo,
            validCodeLimitTimeEnd,
            validCodeSendLimitFlag: !!validCodeLimitTimeEnd,
          },
        },
      });
    },
    // 打开修改密码
    *openPhone({ payload }, { put, select }) {
      const { validCodeLimitTimeEnd } = yield select((state) => state.subAccountOrg.phoneProps);
      const { userInfo } = payload;
      yield put({
        type: 'updateState',
        payload: {
          phoneProps: {
            visible: true,
            userInfo,
            validCodeLimitTimeEnd,
            validCodeSendLimitFlag: !!validCodeLimitTimeEnd,
          },
        },
      });
    },
    // 关闭修改密码
    *closePassword(_, { put, select }) {
      const { validCodeLimitTimeEnd, validCodeSendLimitFlag } = yield select(
        (state) => state.subAccountOrg.passwordProps
      );
      yield put({
        type: 'updateState',
        payload: {
          passwordProps: {
            visible: false,
            userInfo: {},
            validCodeLimitTimeEnd,
            validCodeSendLimitFlag,
          },
        },
      });
    },
    // 关闭修改密码
    *closePhone(_, { put, select }) {
      const { validCodeLimitTimeEnd, validCodeSendLimitFlag } = yield select(
        (state) => state.subAccountOrg.phoneProps
      );
      yield put({
        type: 'updateState',
        payload: {
          phoneProps: {
            visible: false,
            userInfo: {},
            validCodeLimitTimeEnd,
            validCodeSendLimitFlag,
          },
        },
      });
    },
    // 更新密码
    *updatePassword({ payload }, { call }) {
      const { id, userOrganizationId, antherPassword, ...params } = payload;
      const res = getResponse(
        yield call(subAccountOrgUpdatePassword, id, userOrganizationId, {
          ...params,
          organizationId: userOrganizationId,
        })
      );
      return res;
    },
    // 解锁用户
    *unlockUser({ payload }, { call }) {
      const { userId, organizationId } = payload;
      const res = getResponse(yield call(subAccountSiteUserUnlock, userId, organizationId));
      return res;
    },
    // 删除角色
    *deleteRoles({ payload }, { call }) {
      const { memberRoleList } = payload;
      const res = getResponse(yield call(deleteRoles, memberRoleList));
      return res;
    },

    // 查询 当前用户 已分配的用户组
    *getCurrentUserGroups({ payload }, { call }) {
      const res = yield call(subAccountOrgGroupCurrent, payload);
      return getResponse(res);
      // // 同时查询 层级的 值集
    },
    // 查询当前租户可分配的用户组
    *fetchGroups({ payload }, { call }) {
      const rolesRes = yield call(subAccountOrgGroupQueryAll, payload);
      return getResponse(rolesRes);
    },
    // 添加用户组
    *addUserGroup({ payload }, { call }) {
      const res = yield call(addUserGroup, payload);
      return getResponse(res);
    },
    // 删除用户组
    *deleteUserGroup({ payload }, { call }) {
      const res = yield call(deleteUserGroup, payload);
      return getResponse(res);
    },
    /* 接口字段权限维护 */
    // 初始化 接口查询 值集 等 初始化 数据
    *apiInit(_, { call, put }) {
      const res = yield call(queryUnifyIdpValue, 'HIAM.REQUEST_METHOD');
      const requestMethod = getResponse(res);
      if (requestMethod) {
        yield put({
          type: 'updateState',
          payload: {
            requestMethod,
          },
        });
      }
    },
    // 查询接口
    *queryApis({ payload }, { call, put }) {
      const { params } = payload;
      const res = yield call(apiFieldApiQuery, params);
      const apis = getResponse(res);
      if (apis) {
        yield put({
          type: 'updateState',
          payload: {
            apiDataSource: apis.content,
            apiPagination: createPagination(apis),
          },
        });
      }
    },
    // 初始化 接口查询 值集 等 初始化 数据
    *fieldPermissionInit(_, { call, put }) {
      const res = yield call(queryMapIdpValue, {
        fieldType: 'HIAM.FIELD.TYPE',
        permissionRule: 'HIAM.FIELD.PERMISSION_TYPE',
      });
      const enums = getResponse(res);
      if (enums) {
        yield put({
          type: 'updateState',
          payload: enums,
        });
      }
    },
    // 查询接口对应字段权限
    *queryFieldPermissions({ payload }, { call, put }) {
      const { userId, permissionId, params } = payload;
      const res = yield call(apiFieldFieldPermissionQuery, userId, permissionId, params);
      const fieldPermissions = getResponse(res);
      if (fieldPermissions) {
        yield put({
          type: 'updateState',
          payload: {
            fieldPermissionDataSource: fieldPermissions.content,
            fieldPermissionPagination: createPagination(fieldPermissions),
          },
        });
      }
    },
    // 更新接口对应字段权限
    *updateFieldPermission({ payload }, { call }) {
      const { userId, permissionId, record } = payload;
      const res = yield call(apiFieldFieldPermissionUpdate, userId, permissionId, record);
      return getResponse(res);
    },
    // 新增接口对应字段权限
    *createFieldPermission({ payload }, { call }) {
      const { userId, permissionId, record } = payload;
      const res = yield call(apiFieldFieldPermissionCreate, userId, permissionId, record);
      return getResponse(res);
    },
    // 删除接口对应字段权限
    *removeFieldPermission({ payload }, { call }) {
      const { permissionId, record } = payload;
      const res = yield call(apiFieldFieldPermissionRemove, permissionId, record);
      return getResponse(res);
    },

    // 查询角色已分配的安全组
    *querySecurityGroup({ payload }, { call, put }) {
      const res = yield call(querySecurityGroup, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            secGrpList: list.content,
            secGrpPagination: createPagination(list),
          },
        });
      }
    },

    // 角色分配安全组
    *assignSecGrp({ secGrpList, roleId }, { call }) {
      const res = yield call(assignSecGrp, secGrpList, roleId);
      return getResponse(res);
    },

    // 批量删除安全组
    *deleteSecGrp({ payload }, { call }) {
      const res = yield call(deleteSecGrp, payload);
      return getResponse(res);
    },

    // 查询角色已分配的指定安全组的字段权限
    *queryFieldPermission({ payload }, { call, put }) {
      const res = yield call(queryFieldPermission, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            secGrpFieldPermissionList: list.content,
            secGrpFieldPermissionPagination: createPagination(list),
          },
        });
      }
    },

    // 查询角色已分配的指定安全组的字段权限
    *queryFieldConfig({ payload }, { call }) {
      const res = yield call(queryFieldConfig, payload);
      const list = getResponse(res);
      return {
        dataSource: list ? list.content : [],
        pagination: list ? createPagination(list) : {},
      };
    },

    // 查询角色已分配的指定安全组的数据权限维度
    *queryDataDimension({ payload }, { call, put }) {
      const res = yield call(queryDataDimension, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            secGrpDimensionList: list.content,
            secGrpDimensionPagination: createPagination(list),
          },
        });
      }
    },

    // 查询安全组数据权限tab页
    *queryDataPermissionTab({ secGrpId }, { call, put }) {
      const res = yield call(queryTabList, secGrpId);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            secGrpDataPermissionTabList: list,
          },
        });
      }
    },

    // 查询可分配的安全组
    *fetchAssignableSecGrp({ payload }, { call, put }) {
      const res = yield call(queryAssignableSecGrp, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            secGrpAddModalList: list.content,
            secGrpAddModalPagination: createPagination(list),
          },
        });
      }
    },
    *queryDimension({ payload }, { call, put }) {
      const res = getResponse(yield call(queryDimension, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            dimensionList: res,
          },
        });
      }
      return res;
    },
    *queryEmployee({ payload }, { call, put }) {
      const res = yield call(queryEmployee, payload);
      const employeeList = getResponse(res);
      if (employeeList) {
        yield put({
          type: 'updateState',
          payload: {
            employeeList: employeeList.content,
            employeePagination: createPagination(employeeList),
          },
        });
      }
    },
    // 发送验证码
    *postCaptcha({ payload }, { call, put, select }) {
      const passwordProps = yield select((state) => state.subAccountOrg.passwordProps);
      const phoneProps = yield select((state) => state.subAccountOrg.phoneProps);
      const captchaField = 'captchaKey';
      const res = getResponse(yield call(postCaptcha, payload));
      const validCodeLimitTimeStart = new Date().getTime();
      // 60秒限制
      const validCodeLimitTimeEnd = validCodeLimitTimeStart + 60000;
      if (res) {
        notification.success({ message: res.message });
        if (captchaField) {
          setSession(`sub-account-org-phone`, res[captchaField] || 0);
        }
      }
      yield put({
        type: 'updateState',
        payload: {
          passwordProps: {
            ...passwordProps,
            validCodeLimitTimeEnd: res ? validCodeLimitTimeEnd : 0,
            validCodeSendLimitFlag: !!res,
          },
          phoneProps: {
            ...phoneProps,
            validCodeLimitTimeEnd: res ? validCodeLimitTimeEnd : 0,
            validCodeSendLimitFlag: !!res,
          },
        },
      });
      return res;
    },

    // 重置密码
    *resetPassword({ payload }, { call }) {
      const { id, userOrganizationId, ...params } = payload;
      const res = getResponse(
        yield call(resetPassword, id, userOrganizationId, {
          ...params,
          organizationId: userOrganizationId,
        })
      );
      return res;
    },
  },
  reducers: {
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
