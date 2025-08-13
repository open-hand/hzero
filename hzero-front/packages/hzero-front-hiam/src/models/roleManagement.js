/**
 * roleManagement - 角色管理model
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import { isEmpty } from 'lodash';
import { queryMapIdpValue, queryUnifyIdpValue } from 'hzero-front/lib/services/api';
import {
  getResponse,
  createPagination,
  getCurrentOrganizationId,
  // getCurrentRole,
  isTenantRoleLevel,
} from 'utils/utils';
import {
  queryCode,
  queryRole,
  queryOrganizationRole,
  enableRole,
  queryRoleLabel,
  queryLabels,
  queryLabelList,
  createRole,
  createOrganizationRole,
  editRole,
  editOrganizationRole,
  copyRole,
  copyOrganizationRole,
  inheritRole,
  inheritOrganizationRole,
  queryPermissionSets,
  queryLevelPermissions,
  queryOrganizationLevelPermissions,
  queryHrunitsTree,
  queryMemberRolesUsers,
  queryOrganizationMemberRolesUsers,
  queryMemberRolesClients,
  saveMembers,
  saveOrganizationMembers,
  deleteMember,
  deleteOrganizationMember,
  queryRoleAuth,
  saveRoleAuth,
  deleteRoleAuth,
  queryPermissionMenus,
  queryCurrentRole,
  batchAssignPermissionSets,
  batchAssignOrganizationPermissionSets,
  batchUnassignPermissionSets,
  batchUnassignOrganizationPermissionSets,
  queryCreatedSubroles,
  // 角色分配卡片相关
  roleCardsQuery,
  roleCardsDelete,
  roleCardsInsertOrUpdate,
  // 角色分配卡片相关 租户级
  orgRoleCardsQuery,
  orgRoleCardsDelete,
  orgRoleCardsInsertOrUpdate,
  // 查询当前用户的所有租户
  queryCurrentTenants,
  // 查询父级角色
  queryAdminRole,
  apiFieldApiQuery,
  apiFieldFieldPermissionCreate,
  apiFieldFieldPermissionQuery,
  apiFieldFieldPermissionRemove,
  apiFieldFieldPermissionUpdate,
  deleteClient,
  saveClients,
  queryRoleTree,
  queryRoleTreeAll,
  queryRoleCard,
  querySecurityGroup,
  queryAssignableSecGrp,
  assignSecGrp,
  deleteSecGrp,
  querySecGrpPermissionMenus,
  queryFieldPermission,
  queryFieldConfig,
  queryCardPermission,
  queryDataDimension,
  queryTabList,
  shieldSecGrpPermission,
  cancelShieldSecGrpPermission,
  querySearchLabels, // 查询搜索字段标签数据
  fetchPasswordPolicyList,
  fetchUserList, // 查询用户信息
  fetchClientList, // 查询客户端信息
  // 修改角色管理树形页面接口 Modify by Nemo @2020119
  fetchTreeRole,
} from '../services/roleManagementService';

/**
 * tableState - table默认属性配置
 */
const tableState = {
  dataSource: [],
  pagination: {
    pageSize: 10,
    total: 0,
    current: 1,
  },
};

function dealDataState(data) {
  // 处理行 处理字段为update
  let config = [];
  if (Array.isArray(data) && data.length > 0) {
    config = data.map((item) => {
      return {
        ...item,
        _status: 'update',
      };
    });
  }
  return config;
}

// /**
//  * 对象property属性定义方法
//  * @function defineProperty
//  * @param {!object} obj - 目标对象
//  * @param {!string} property - 对象属性名称
//  * @param {any} value - 属性值
//  * @returns
//  */
// function defineProperty(obj, property, value) {
//   Object.defineProperty(obj, property, {
//     value,
//     writable: true,
//     enumerable: false,
//     configurable: true,
//   });
// }

export default {
  namespace: 'roleManagement',
  state: {
    code: {}, // 值集集合
    searchLabels: [], // 搜索字段标签
    list: {
      // 列表页面数据集合
      expandedRowKeys: [], // 可展开的行数据key集合
      dataSource: [], // 表格数据
      rowKeys: [], // 用于控制是否全部展开/收起的行数据key集合
    },
    detail: {
      // 角色管理明细编辑页面数据逻辑集合
      form: {}, // 表单数据集合
      permissions: {
        ...tableState,
      },
      permissionSets: [],
    },
    roleAuth: {},
    roleAuthScopeCode: [],
    roleAuthScopeTypeCode: [],

    requestMethod: [], // 请求方式
    fieldType: [], // 字段类型
    apiDataSource: [], // 接口数据源
    apiPagination: {}, // 接口分页
    fieldPermissionDataSource: [], // 字段权限数据源
    fieldPermissionPagination: {}, // 字段权限分页
    levelList: [], // 分配层级列表
    clientList: [], // 已分配客户端列表
    clientPagination: {}, // 客户端分配分页
    cardList: [],
    cardListPagination: {},
    addData: {}, // 新增数据
    secGrpList: [], // 安全组列表
    secGrpPagination: {}, // 安全组分页
    secGrpAddModalList: [], // 可分配安全组列表
    secGrpAddModalPagination: {}, // 可分配安全组分页
    secGrpFieldPermissionList: [], // 安全组字段权限列表
    secGrpFieldPermissionPagination: {}, // 安全组字段权限分页
    secGrpCardList: [], // 安全组工作台权限列表
    secGrpCardPagination: {}, // 安全组工作台权限分页
    secGrpDimensionList: [], // 安全组数据权限维度列表
    secGrpDimensionPagination: {}, // 安全组数据权限维度分页
    secGrpDataPermissionTabList: [], // 安全组数据权限tab页
    labelList: [], // 标签列表
    treeList: {
      list: [], // 树形结构列表数据
      pagination: {}, // 树形结构列表分页
      defaultTreeExpandedRowKeys: [], // 默认展开的节点
    },
    passwordPolicyList: {},
    // 分配用户选择用户弹窗
    memberModalList: [],
    memberModalPagination: {},
    // 分配客户端选择客户端弹窗
    clientModalList: [],
    clientModalPagination: {},
  },
  effects: {
    // 获取密码策略数据
    *fetchPasswordPolicyList({ payload }, { call, put }) {
      const list = yield call(fetchPasswordPolicyList, payload);
      const res = getResponse(list);
      if (res) {
        yield put({
          type: 'updateState',
          payload: { passwordPolicyList: res },
        });
      }
    },

    // 查询角色列表数据
    *queryList({ params }, { put, call }) {
      const res = yield call(queryCreatedSubroles, params);
      const response = getResponse(res);
      const rowKeys = [];

      // const { id } = getCurrentRole();

      /**
       * 组装新dataSource
       * @function assignListData
       * @param {!Array} [collections = []] - 树节点集合
       * @returns {Array} - 新的dataSourcee
       */
      function assignListData(collections = []) {
        return collections.map((n) => {
          const m = n;
          // TODO: 新的逻辑 不需要限制 当前角色
          // if (id === m.id) {
          //   defineProperty(m, 'disadbleCurrentEnabled', true);
          //   defineProperty(m, 'disadbleEdit', true);
          // } else {
          //   defineProperty(m, 'disadbleView', true);
          // }
          if (isEmpty(m.childRoles)) {
            m.childRoles = null;
          } else {
            m.childRoles = assignListData(m.childRoles);
            rowKeys.push(m.id);
          }
          return m;
        });
      }

      if (response) {
        const dataSource = assignListData(isEmpty(response) ? [] : response.content);
        yield put({
          type: 'updateRoleListReducer',
          payload: {
            dataSource,
            pagination: createPagination(response),
            rowKeys,
          },
        });
      }
    },

    // 查询树形根节点
    *queryRoleRoot({ payload }, { call, put }) {
      let result = yield call(queryRoleTree, payload);
      result = getResponse(result);
      if (result) {
        let nextContent = [];
        if (result.content.length) {
          nextContent = result.content.map((item) => ({
            ...item,
            levelPath: item.id,
            // TODO: 由于 levelPath 挪作他用, 所以这里使用 _levelPath 存储原来的 levelPath 值
            _levelPath: item.levelPath,
            rootElement: 1,
          }));
        }
        yield put({
          type: 'updateState',
          payload: {
            treeList: {
              list: nextContent,
              pagination: createPagination(result),
            },
          },
        });
      }
    },

    // 查询树形子节点
    *queryRoleChildren({ payload }, { call, put, select }) {
      const treeData = yield select((state) => state.roleManagement.treeList.list);
      const pagination = yield select((state) => state.roleManagement.treeList.pagination);
      let result = yield call(queryRoleTree, payload);
      result = getResponse(result);

      function findParentNode(pathArr, nextContent) {
        const nextTreeList = [...treeData];
        let target = nextTreeList.find((item) => String(item.id) === pathArr[0]);
        if (pathArr.length > 2) {
          pathArr.shift();
          target = findMoreLevelTarget(target, pathArr);
        }
        target.children = target.children.concat(nextContent);
        return treeData;
      }

      function findMoreLevelTarget(target, pathArr) {
        const targetParent = target.children.find((item) => String(item.id) === pathArr[0]);
        if (pathArr.length === 2) {
          return targetParent;
        } else {
          pathArr.shift();
          return findMoreLevelTarget(targetParent, pathArr);
        }
      }

      if (result) {
        let nextContent = [];
        if (payload.parentRoleId && result.content.length) {
          nextContent = result.content.map((item) => ({
            ...item,
            levelPath: `${payload.levelPath}@${item.id}`,
            // TODO: 由于 levelPath 挪作他用, 所以这里使用 _levelPath 存储原来的 levelPath 值
            _levelPath: item.levelPath,
          }));
          const pathArr = nextContent[0].levelPath.split('@'); // 将由id组成的path分割
          const nextTreeList = findParentNode(pathArr, nextContent);
          yield put({
            type: 'updateState',
            payload: {
              treeList: {
                list: nextTreeList,
                pagination,
              },
            },
          });
        }
      }
    },

    // 树形的条件查询
    *queryRoleAll({ payload }, { call, put }) {
      let result = yield call(queryRoleTreeAll, payload);
      result = getResponse(result);
      const defaultTreeExpandedRowKeys = [];

      function traverseTree(node) {
        if (!node) {
          return;
        }
        defaultTreeExpandedRowKeys.push(node.id);
        if (node.children && node.children.length > 0) {
          for (let i = 0; i < node.children.length; i++) {
            traverseTree(node.children[i]);
          }
        }
      }
      if (result) {
        result.map((item) => ({ ...item, rootElement: 1 })).forEach((item) => traverseTree(item));
        yield put({
          type: 'updateState',
          payload: {
            treeList: {
              list: result,
              pagination: {},
              defaultTreeExpandedRowKeys,
            },
          },
        });
      }
    },

    // 获取授权类型信息
    *init(_, { call, put }) {
      const { levelList, flagList } = yield call(queryMapIdpValue, {
        levelList: 'HIAM.RESOURCE_LEVEL',
        flagList: 'HPFM.ENABLED_FLAG',
      });
      yield put({
        type: 'updateState',
        payload: {
          levelList: levelList.filter((item) => item.value !== 'site'),
        },
      });
      yield put({
        type: 'setCodeReducer',
        payload: {
          'HIAM.RESOURCE_LEVEL': levelList,
          'HPFM.ENABLED_FLAG': flagList,
        },
      });
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

    // 获取标签
    *queryRoleLabel({ payload }, { call }) {
      const res = yield call(queryRoleLabel, payload);
      // if (res && !res.failed) {
      //   yield put({
      //     type: 'updateState',
      //     payload: {
      //       // labelList: res,
      //     },
      //   });
      // }
      return res;
    },

    // // 获取授权类型信息
    // *queryUserTypeList(_, { call, put }) {
    //   const res = yield call(queryIdpValue, 'HIAM.USER_TYPE');
    //   const userTypeList = getResponse(res);
    //   yield put({
    //     type: 'updateState',
    //     payload: {
    //       userTypeList,
    //     },
    //   });
    // },

    // 查询角色管理明细页面表单
    *queryDetailForm({ roleId }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(queryOrganizationRole, roleId, organizationId);
      } else {
        res = yield call(queryRole, roleId);
      }
      const response = getResponse(res);
      // if (response) {
      //   yield put({
      //     type: 'updateRoleDetailReducer',
      //     payload: {
      //       form: response,
      //     },
      //   });
      // }
      return response || {};
    },
    // 查询简单的角色信息
    *queryAdminRole({ payload }, { call }) {
      const { roleId } = payload;
      const res = yield call(queryAdminRole, roleId);
      return getResponse(res);
    },
    // 设置角色是否启用
    *setRoleEnabled({ payload }, { call }) {
      const response = yield call(enableRole, payload);
      return response;
    },
    // 查询值集
    *queryCode({ payload }, { put, call }) {
      const response = yield call(queryCode, payload);
      if (response && !response.failed) {
        yield put({
          type: 'setCodeReducer',
          payload: {
            [payload.lovCode]: response,
          },
        });
      }
    },
    *querySearchLabels({ payload }, { put, call }) {
      const response = yield call(querySearchLabels, payload);
      if (response && !response.failed) {
        yield put({
          type: 'updateState',
          payload: {
            searchLabels: response,
          },
        });
      }
    },
    *queryRole({ payload }, { put, call }) {
      const response = yield call(queryRole, payload);
      if (response && !response.failed) {
        yield put({
          type: 'updateRoleDetailReducer',
          payload: {
            form: response,
          },
        });
      }
    },
    *queryLabels({ payload }, { call }) {
      const response = yield call(queryLabels, payload);
      return response && !response.failed ? response : [];
    },
    // 查询可分配的所有权限层级
    *queryAvailablePermissionSets({ roleId, params }, { call }) {
      const res = yield call(queryPermissionSets, roleId, params);
      return getResponse(res);
    },
    *queryLevelPermissions({ id, params = {} }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(queryOrganizationLevelPermissions, id, params, organizationId);
      } else {
        res = yield call(queryLevelPermissions, id, params);
      }
      return getResponse(res);
    },

    // 创建角色
    *createRole({ data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (isTenantRoleLevel()) {
        response = yield call(createOrganizationRole, data, organizationId);
      } else {
        response = yield call(createRole, data);
      }
      return getResponse(response);
    },

    // 修改角色
    *saveRole({ data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (isTenantRoleLevel()) {
        response = yield call(editOrganizationRole, data, organizationId);
      } else {
        response = yield call(editRole, data);
      }
      return getResponse(response);
    },

    // 复制并创建角色
    *copyRole({ data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (isTenantRoleLevel()) {
        response = yield call(copyOrganizationRole, data, organizationId);
      } else {
        response = yield call(copyRole, data);
      }
      return getResponse(response);
    },

    // 继承并创建角色
    *inheritRole({ data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let response;
      if (isTenantRoleLevel()) {
        response = yield call(inheritOrganizationRole, data, organizationId);
      } else {
        response = yield call(inheritRole, data);
      }
      return getResponse(response);
    },

    // 查询组织架构,用于分配成员时的弹出框选择
    *queryHrunitsTree({ organizationId, payload }, { call }) {
      const res = yield call(queryHrunitsTree, payload, organizationId);

      function assignListData(collections = []) {
        return collections.map((n) => {
          const m = n;
          if (isEmpty(m.childHrUnits)) {
            m.childHrUnits = null;
          } else {
            m.childHrUnits = assignListData(m.childHrUnits);
          }
          return m;
        });
      }

      const response = getResponse(res);
      return assignListData(isEmpty(response) ? [] : response);
    },

    // 查询成员角色
    *queryMemberRolesUsers({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(queryOrganizationMemberRolesUsers, payload, organizationId);
      } else {
        res = yield call(queryMemberRolesUsers, payload);
      }
      const response = getResponse(res);
      return response
        ? {
            dataSource: (response.content || []).map((n) => ({ key: n.id, ...n })),
            pagination: createPagination(res),
          }
        : null;
    },

    // 查询角色已分配的客户端
    *queryMemberRolesClients({ payload }, { call, put }) {
      const res = yield call(queryMemberRolesClients, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            clientList: dealDataState(list.content),
            clientPagination: createPagination(list),
          },
        });
      }
      return list;
    },

    *queryRoleCard({ payload }, { call, put }) {
      const res = yield call(queryRoleCard, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            cardList: list.content,
            cardListPagination: createPagination(list),
          },
        });
      }
      return list;
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

    // 查询角色已分配的指定安全组的访问权限
    *querySecGrpPermissionMenus({ roleId, secGrpId }, { call }) {
      const res = yield call(querySecGrpPermissionMenus, roleId, secGrpId);
      const response = getResponse(res);
      const defaultExpandedRowKeys = [];

      /**
       * 组装新dataSource
       * @function assignListData
       * @param {!Array} [collections = []] - 树节点集合
       * @returns {Array} - 新的dataSourcee
       */
      function assignListData(collections = []) {
        return collections.map((n) => {
          const m = n;
          m.key = n.id;
          if (isEmpty(m.subMenus)) {
            m.subMenus = null;
          } else {
            m.subMenus = assignListData(m.subMenus);
            defaultExpandedRowKeys.push(m.id);
            const checkedCount = m.subMenus.filter((o) => o.checkedFlag === 'Y').length;
            const indeterminateCount = m.subMenus.filter((o) => o.checkedFlag === 'P').length;
            m.checkedFlag =
              // eslint-disable-next-line no-nested-ternary
              checkedCount === m.subMenus.length
                ? 'Y'
                : // eslint-disable-next-line no-nested-ternary
                checkedCount === 0
                ? indeterminateCount === 0
                  ? null
                  : 'P'
                : 'P';
          }
          return m;
        });
      }

      return {
        dataSource: assignListData(response || []),
        defaultExpandedRowKeys,
      };
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

    // 查询角色已分配的指定安全组的工作台权限
    *queryCardPermission({ payload }, { call, put }) {
      const res = yield call(queryCardPermission, payload);
      const list = getResponse(res);
      if (list) {
        yield put({
          type: 'updateState',
          payload: {
            secGrpCardList: list.content,
            secGrpCardPagination: createPagination(list),
          },
        });
      }
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

    // 屏蔽安全组权限
    *shieldSecGrpPermission({ payload }, { call }) {
      const res = yield call(shieldSecGrpPermission, payload);
      return getResponse(res);
    },

    // 取消屏蔽安全组权限
    *cancelShieldSecGrpPermission({ payload }, { call }) {
      const res = yield call(cancelShieldSecGrpPermission, payload);
      return getResponse(res);
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
    // 批量删除客户端
    *deleteClient({ payload }, { call }) {
      const res = yield call(deleteClient, payload);
      return getResponse(res);
    },

    // 保存客户端
    *saveClients({ data, isEdit }, { call }) {
      const res = yield call(saveClients, data, isEdit);
      return getResponse(res);
    },

    // 保存成员
    *saveMembers({ data, isEdit }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(saveOrganizationMembers, data, isEdit, organizationId);
      } else {
        res = yield call(saveMembers, data, isEdit);
      }
      return getResponse(res);
    },

    // 删除成员
    *deleteMembers({ data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(deleteOrganizationMember, data, organizationId);
      } else {
        res = yield call(deleteMember, data);
      }
      return res;
    },
    *queryPermissionMenus({ roleId, params }, { call }) {
      const res = yield call(queryPermissionMenus, roleId, params);
      const response = getResponse(res);
      const defaultExpandedRowKeys = [];

      /**
       * 组装新dataSource
       * @function assignListData
       * @param {!Array} [collections = []] - 树节点集合
       * @returns {Array} - 新的dataSourcee
       */
      function assignListData(collections = []) {
        return collections.map((n) => {
          const m = n;
          m.key = n.id;
          if (isEmpty(m.subMenus)) {
            m.subMenus = null;
          } else {
            m.subMenus = assignListData(m.subMenus);
            defaultExpandedRowKeys.push(m.id);
            const checkedCount = m.subMenus.filter((o) => o.checkedFlag === 'Y').length;
            const indeterminateCount = m.subMenus.filter((o) => o.checkedFlag === 'P').length;
            m.checkedFlag =
              // eslint-disable-next-line no-nested-ternary
              checkedCount === m.subMenus.length
                ? 'Y'
                : // eslint-disable-next-line no-nested-ternary
                checkedCount === 0
                ? indeterminateCount === 0
                  ? null
                  : 'P'
                : 'P';
          }
          return m;
        });
      }

      return {
        dataSource: assignListData(response || []),
        defaultExpandedRowKeys,
      };
    },

    *batchAssignPermissionSets({ roleId, data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(batchAssignOrganizationPermissionSets, roleId, data, organizationId);
      } else {
        res = yield call(batchAssignPermissionSets, roleId, data);
      }
      return res;
    },

    *batchUnassignPermissionSets({ roleId, data }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(batchUnassignOrganizationPermissionSets, roleId, data, organizationId);
      } else {
        res = yield call(batchUnassignPermissionSets, roleId, data);
      }
      return res;
    },

    // 查询权限维度类型及所有权限维度
    *queryRoleAuthType(_, { call, put }) {
      const { roleAuthScopeCode, roleAuthScopeTypeCode } = yield call(queryMapIdpValue, {
        roleAuthScopeCode: 'HIAM.AUTHORITY_SCOPE_CODE',
        roleAuthScopeTypeCode: 'HIAM.AUTHORITY_TYPE_CODE',
      });

      yield put({
        type: 'updateStateReducer',
        payload: {
          roleAuthScopeCode: getResponse(roleAuthScopeCode),
          roleAuthScopeTypeCode: getResponse(roleAuthScopeTypeCode),
        },
      });
    },
    // 查询角色单据权限
    *queryRoleAuth({ payload }, { call, put }) {
      const res = yield call(queryRoleAuth, payload);
      const roleAuth = getResponse(res);
      yield put({
        type: 'updateStateReducer',
        payload: { roleAuth },
      });
      return roleAuth;
    },
    // 保存当前角色权限
    *saveRoleAuth({ payload }, { call }) {
      const res = yield call(saveRoleAuth, payload);
      return getResponse(res);
    },
    // 删除角色全新啊
    *deleteRoleAuth({ payload }, { call }) {
      const res = yield call(deleteRoleAuth, payload);
      return getResponse(res);
    },
    *queryCurrentRole(action, { call }) {
      const res = yield call(queryCurrentRole);
      return getResponse(res);
    },
    // 查询角色已经分配的卡片
    *fetchRoleCards({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(orgRoleCardsQuery, organizationId, payload);
      } else {
        res = yield call(roleCardsQuery, payload);
      }
      return getResponse(res);
    },
    // 删除角色已经分配的卡片
    *removeRoleCards({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(orgRoleCardsDelete, organizationId, payload);
      } else {
        res = yield call(roleCardsDelete, payload);
      }
      return getResponse(res);
    },
    // 新增或更新角色已经分配的卡片
    *saveRoleCards({ payload }, { call }) {
      const organizationId = getCurrentOrganizationId();
      let res;
      if (isTenantRoleLevel()) {
        res = yield call(orgRoleCardsInsertOrUpdate, organizationId, payload);
      } else {
        res = yield call(roleCardsInsertOrUpdate, payload);
      }
      return getResponse(res);
    },
    // 查询当前用户的所有租户
    *fetchCurrentTenants(_, { call }) {
      const res = yield call(queryCurrentTenants);
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
      const { roleId, permissionId, params } = payload;
      const res = yield call(apiFieldFieldPermissionQuery, roleId, permissionId, params);
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
      const { roleId, permissionId, record } = payload;
      const res = yield call(apiFieldFieldPermissionUpdate, roleId, permissionId, record);
      return getResponse(res);
    },
    // 新增接口对应字段权限
    *createFieldPermission({ payload }, { call }) {
      const { roleId, permissionId, record } = payload;
      const res = yield call(apiFieldFieldPermissionCreate, roleId, permissionId, record);
      return getResponse(res);
    },
    // 删除接口对应字段权限
    *removeFieldPermission({ payload }, { call }) {
      const { permissionId, record } = payload;
      const res = yield call(apiFieldFieldPermissionRemove, permissionId, record);
      return getResponse(res);
    },

    *fetchUserList({ payload }, { call, put }) {
      const res = getResponse(yield call(fetchUserList, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            memberModalList: res.content,
            memberModalPagination: createPagination(res),
          },
        });
      }
    },
    *fetchClientList({ payload }, { call, put }) {
      const res = getResponse(yield call(fetchClientList, payload));
      if (res) {
        yield put({
          type: 'updateState',
          payload: {
            clientModalList: res.content,
            clientModalPagination: createPagination(res),
          },
        });
      }
    },

    // 修改角色管理树形页面接口 Modify by Nemo @2020119
    *fetchTreeRoleRoot({ payload }, { call, put }) {
      const result = getResponse(yield call(fetchTreeRole, payload));
      if (result) {
        let nextContent = [];
        if (result.content.length) {
          nextContent = result.content.map((item) => ({
            ...item,
            levelPath: item.id,
            // TODO: 由于 levelPath 挪作他用, 所以这里使用 _levelPath 存储原来的 levelPath 值
            _levelPath: item.levelPath,
            children: item.childrenNum > 0 ? item.children : undefined,
            rootElement: 1,
          }));
        }
        yield put({
          type: 'updateState',
          payload: {
            treeList: {
              list: nextContent,
              pagination: createPagination(result),
            },
          },
        });
      }
    },

    *fetchTreeRoleChildren({ payload }, { call, put, select }) {
      const treeData = yield select((state) => state.roleManagement.treeList.list);
      const pagination = yield select((state) => state.roleManagement.treeList.pagination);
      const result = getResponse(yield call(fetchTreeRole, payload));

      function findParentNode(pathArr, nextContent) {
        const nextTreeList = [...treeData];
        let target = nextTreeList.find((item) => String(item.id) === pathArr[0]);
        const newNextContent = nextContent.map((item) => ({
          ...item,
          // 前端新增的数据，为第一级角色的数据
          rootParentInfo: target,
        }));
        if (pathArr.length > 2) {
          pathArr.shift();
          target = findMoreLevelTarget(target, pathArr);
        }
        target.children = target.children.concat(newNextContent);
        target.childrenTotalElements = result.totalElements;
        return treeData;
      }

      function findMoreLevelTarget(target, pathArr) {
        const targetParent = target.children.find((item) => String(item.id) === pathArr[0]);
        if (pathArr.length === 2) {
          return targetParent;
        } else {
          pathArr.shift();
          return findMoreLevelTarget(targetParent, pathArr);
        }
      }

      if (result) {
        let nextContent = [];
        if (payload.parentRoleId && result.content.length) {
          nextContent = result.content.map((item) => ({
            ...item,
            levelPath: `${payload.levelPath}@${item.id}`,
            // TODO: 由于 levelPath 挪作他用, 所以这里使用 _levelPath 存储原来的 levelPath 值
            _levelPath: item.levelPath,
            children: item.childrenNum > 0 ? item.children : undefined,
          }));
          const pathArr = nextContent[0]?.levelPath.split('@'); // 将由id组成的path分割
          const nextTreeList = findParentNode(pathArr, nextContent);
          yield put({
            type: 'updateState',
            payload: {
              treeList: {
                list: nextTreeList,
                pagination,
              },
            },
          });
        }
      }
    },
  },
  reducers: {
    updateStateReducer(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
    setCodeReducer(state, { payload }) {
      return {
        ...state,
        code: Object.assign(state.code, payload),
      };
    },
    updateRoleDetailReducer(state, { payload }) {
      return {
        ...state,
        detail: {
          ...state.detail,
          ...payload,
        },
      };
    },
    updateRoleListReducer(state, { payload }) {
      return {
        ...state,
        list: {
          ...state.list,
          ...payload,
        },
      };
    },
    initRoleDetailReducer(state) {
      return {
        ...state,
        detail: Object.assign(state.detail, {
          form: {},
          permissions: {
            ...tableState,
          },
          permissionSets: [],
        }),
      };
    },
    updateState(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
