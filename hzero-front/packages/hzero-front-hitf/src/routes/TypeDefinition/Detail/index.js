/**
 * TypeApplication - 应用类型定义-详情
 * @date: 2019/8/22
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Card } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { isEmpty, isUndefined, isNull } from 'lodash';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';
import notification from 'utils/notification';
import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import { Button as ButtonPermission } from 'components/Permission';
import { getEditTableData, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME, DETAIL_CARD_TABLE_CLASSNAME } from 'utils/constants';
import DetailForm from './Form';
import DetailTable from './List';

const isTenant = isTenantRoleLevel();
const organizationId = getCurrentOrganizationId();

/**
 *  应用类型定义-详情
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} typeDefinition - 数据源
 * @reactProps {boolean} fetchHeadLoading - 详情头加载标志
 * @reactProps {boolean} fetchListLoading - 详情行加载标志
 * @reactProps {boolean} saveHeadLoading - 实例配置头更新加载标志
 * @reactProps {boolean} saveInstanceLoading - 新建/更新实例加载标志
 * @reactProps {boolean} deleteInstanceLoading - 删除实例加载标志
 * @reactProps {boolean} fetchInstanceDetailLoading - 实例详情加载标志
 * @reactProps {boolean} refreshInstanceLoading - 刷新实例加载标志
 * @reactProps {boolean} fetchMappingClassLoading - 查询映射类加载标志
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@connect(({ typeDefinition, loading }) => ({
  typeDefinition,
  fetchHeadLoading: loading.effects['typeDefinition/queryDefinition'],
  fetchListLoading: loading.effects['typeDefinition/queryInstanceLineList'],
  saveHeadLoading: loading.effects['typeDefinition/saveDefinition'],
  saveInstanceLoading: loading.effects['typeDefinition/saveInstance'],
  deleteInstanceLoading: loading.effects['typeDefinition/deleteInstanceLineList'],
  fetchInstanceDetailLoading:
    loading.effects['typeDefinition/queryInstanceDetail'] ||
    loading.effects['typeDefinition/refreshInstance'],
  refreshInstanceLoading: loading.effects['typeDefinition/refreshInstance'],
  fetchMappingClassLoading: loading.effects['typeDefinition/queryMappingClass'],
  testMappingClassLoading: loading.effects['typeDefinition/testMappingClass'],
}))
@formatterCollections({ code: ['hitf.typeDefinition', 'hitf.document', 'hitf.common'] })
export default class Detail extends Component {
  /**
   * 初始查询列表数据
   */
  componentDidMount() {
    const { match = {} } = this.props;
    const { id } = match.params;
    this.queryIdpValue();
    if (id) {
      const page = {};
      this.fetchInstanceHead();
      this.handleSearch(page);
    }
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'typeDefinition/updateState',
      payload: {
        instanceList: {
          dataSource: [],
          pagination: {},
        },
        instanceHeadInfo: {}, // 数据组头信息
      },
    });
  }

  /**
   * 查询直接
   */
  @Bind()
  queryIdpValue() {
    const { dispatch } = this.props;
    dispatch({
      type: 'typeDefinition/queryIdpValue',
    });
  }

  /**
   * 查询应用类型详情
   */
  @Bind()
  fetchInstanceHead() {
    const { dispatch, match } = this.props;
    const params = match.params.id;
    const id = params.indexOf('?') === -1 ? params : params.substring(0, params.indexOf('?'));
    if (!isUndefined(id)) {
      dispatch({
        type: 'typeDefinition/queryDefinition',
        applicationId: id,
      }).then((res) => {
        if (res) {
          this.fetchMinorCategory({ parentValue: res.majorCategory });
        }
      });
    }
  }

  /**
   * 创建/修改应用类型
   */
  @Bind()
  handleSaveDefinitionHead() {
    const { fetchHeadLoading, saveHeadLoading } = this.props;
    const { validateFields = (e) => e } = this.form.props.form; // 上面接口定义DOM节点
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
   * 保存应用类型
   * @param {Object} values - 表单数据
   */
  @Bind()
  save(values) {
    const {
      dispatch,
      match = {},
      typeDefinition: { instanceHeadInfo = {} },
    } = this.props;
    const params = match.params.id;
    const id =
      isUndefined(params) || params.indexOf('?') === -1
        ? params
        : params.substring(0, params.indexOf('?'));
    let nextValues = { ...values };
    if (id) {
      const { applicationId, objectVersionNumber, _token } = instanceHeadInfo;
      nextValues = {
        ...nextValues,
        applicationId,
        objectVersionNumber,
        _token,
      };
    }
    if (isTenant) {
      nextValues.tenantId = organizationId;
    }
    dispatch({
      type: 'typeDefinition/saveDefinition',
      payload: {
        ...nextValues,
      },
    }).then((response) => {
      if (response) {
        notification.success();
        if (id) {
          this.fetchInstanceHead();
          this.handleSearch();
        } else {
          this.redirectToEdit(response.applicationId);
        }
      }
    });
  }

  /**
   * 跳转至编辑页面
   *@param {number} applicationId - 应用ID
   */
  @Bind()
  redirectToEdit(applicationId) {
    const {
      dispatch,
      location: { search, pathname },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    dispatch(
      routerRedux.push({
        pathname:
          pathname.indexOf('/private') === 0
            ? `/private/hitf/application-type-definition/detail/${applicationId}?access_token=${accessToken}`
            : `/hitf/application-type-definition/detail/${applicationId}`,
      })
    );
  }

  /**
   * 查询应用实例列表
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, match } = this.props;
    const params = match.params.id;
    const id = params.indexOf('?') === -1 ? params : params.substring(0, params.indexOf('?'));
    if (!isUndefined(id)) {
      dispatch({
        type: 'typeDefinition/queryInstanceLineList',
        payload: {
          page: isEmpty(fields) ? {} : fields,
          applicationId: id,
        },
      });
    }
  }

  /**
   * 删除实例配置行
   */
  @Bind()
  deleteLine(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'typeDefinition/deleteInstanceLineList',
      payload,
    });
  }

  /**
   * 查询映射类
   */
  @Bind()
  fetchMappingClass(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'typeDefinition/queryMappingClass',
      payload: params,
    });
  }

  /**
   * 测试映射类
   * @param {number} applicationInstId - 实例id
   * @param {string} template - 映射类代码
   */
  @Bind()
  testMappingClass(applicationInstId, template) {
    const { dispatch } = this.props;
    const payload = { template };
    if (!isNull(applicationInstId)) {
      payload.applicationInstId = applicationInstId;
    }
    return dispatch({
      type: 'typeDefinition/testMappingClass',
      payload,
    });
  }

  /**
   * 创建实例配置
   */
  @Bind()
  saveInstance(payload) {
    const {
      dispatch,
      match,
      typeDefinition: { instanceDetail, instanceHeadInfo = {} },
    } = this.props;
    const params = match.params.id;
    const id = params.indexOf('?') === -1 ? params : params.substring(0, params.indexOf('?'));
    let nextPayload = {};
    // 创建
    if (isEmpty(instanceDetail)) {
      nextPayload = {
        ...payload,
        applicationId: id,
        tenantId: instanceHeadInfo.tenantId,
      };
      // 更新
    } else {
      // eslint-disable-next-line no-unused-vars
      const { applicationInstMapList = [], ...rest } = instanceDetail;
      let nextList = getEditTableData(instanceDetail.applicationInstMapList, ['_status']);
      if (Array.isArray(nextList) && nextList.length !== 0) {
        nextList = nextList.map((item) => {
          const nextItem = { ...item };
          const { targetParamRemark, targetParamNameType, targetParamName, ...others } = nextItem;
          return {
            ...others,
            targetParamId: targetParamName,
          };
        });
      } else {
        nextList = instanceDetail.applicationInstMapList;
      }
      nextPayload = {
        ...rest,
        ...payload,
        applicationInstMapList: nextList,
      };
    }
    return dispatch({
      type: 'typeDefinition/saveInstance',
      payload: nextPayload,
    });
  }

  @Bind()
  cleanInstanceData() {
    const { dispatch } = this.props;
    dispatch({
      type: 'typeDefinition/updateState',
      payload: {
        instanceDetail: {},
      },
    });
  }

  /**
   * 查询实例详情
   * @param {number} applicationInstId - 实例ID
   */
  @Bind()
  fetchInstanceDetail(applicationInstId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'typeDefinition/queryInstanceDetail',
      payload: applicationInstId,
    });
  }

  // 刷新实例
  @Bind()
  refreshInstance(applicationInstId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'typeDefinition/refreshInstance',
      payload: applicationInstId,
    });
  }

  /**
   * 编辑参数
   * @param {Object} record - 行数据
   * @param {Boolean} flag - 编辑/取消标记
   */
  @Bind()
  handleEditLine(record, flag) {
    const {
      dispatch,
      typeDefinition: { instanceDetail = {} },
    } = this.props;
    const { applicationInstMapList, ...rest } = instanceDetail;
    const newList = applicationInstMapList.map((item) =>
      item.applicationInstMapId === record.applicationInstMapId
        ? { ...item, _status: flag ? 'update' : '' }
        : item
    );
    dispatch({
      type: 'typeDefinition/updateState',
      payload: {
        instanceDetail: {
          applicationInstMapList: newList,
          ...rest,
        },
      },
    });
  }

  /**
   * 查询应用小类
   * @param {object} payload - 查询参数
   */
  @Bind()
  fetchMinorCategory(payload) {
    const { dispatch } = this.props;
    dispatch({
      type: 'typeDefinition/fetchMinorCategory',
      payload: {
        lovCode: 'HITF.APP_MINOR_CATEGORY',
        ...payload,
      },
    });
  }

  render() {
    const {
      match = {},
      location: { search },
      typeDefinition = {},
      fetchHeadLoading,
      saveHeadLoading,
      fetchListLoading,
      saveInstanceLoading,
      deleteInstanceLoading,
      refreshInstanceLoading = false,
      fetchInstanceDetailLoading = false,
      fetchMappingClassLoading = false,
      testMappingClassLoading = false,
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const { id } = match.params;
    const { path } = match;
    const isCreate = isUndefined(id);
    const {
      instanceHeadInfo = {},
      instanceList = {},
      enumMap,
      instanceDetail = {},
      minorCategoryList = [],
    } = typeDefinition;
    const { composePolicyTypes = [] } = enumMap;
    const formProps = {
      instanceHeadInfo,
      composePolicyTypes,
      minorCategoryList,
      fetchMinorCategory: this.fetchMinorCategory,
      isCreate,
      isTenant,
      wrappedComponentRef: (node) => {
        this.form = node;
      },
    };
    const { interfaceId, composePolicy } = instanceHeadInfo;
    const detailTableProps = {
      path,
      loading: fetchListLoading,
      isCreate,
      interfaceId,
      composePolicy,
      dataSource: instanceList.dataSource || [],
      pagination: instanceList.pagination || {},
      instanceDetail,
      onChange: this.handleSearch,
      onDelete: this.deleteLine,
      onSave: this.saveInstance,
      onClean: this.cleanInstanceData,
      onEditLine: this.handleEditLine,
      fetchInstanceDetail: this.fetchInstanceDetail,
      refreshInstance: this.refreshInstance,
      fetchMappingClass: this.fetchMappingClass,
      testMappingClass: this.testMappingClass,
      saveInstanceLoading,
      deleteInstanceLoading,
      refreshInstanceLoading,
      fetchInstanceDetailLoading,
      fetchMappingClassLoading,
      testMappingClassLoading,
    };
    return (
      <>
        <Header
          title={intl.get('hitf.typeDefinition.view.message.title.instance.config').d('实例配置')}
          backPath={
            match.path.indexOf('/private') === 0
              ? `/private/hitf/application-type-definition/list?access_token=${accessToken}`
              : '/hitf/application-type-definition/list'
          }
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '应用类型定义-新建',
              },
            ]}
            icon="save"
            type="c7n-pro"
            color="primary"
            loading={saveHeadLoading}
            onClick={this.handleSaveDefinitionHead}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            key="instance-head"
            bordered={false}
            title={
              <h3>
                {intl.get('hitf.typeDefinition.view.message.title.application.type').d('应用类型')}
              </h3>
            }
            className={DETAIL_CARD_CLASSNAME}
            loading={fetchHeadLoading}
          >
            <DetailForm {...formProps} />
          </Card>
          <Card
            key="instance-line"
            bordered={false}
            title={
              <h3>
                {intl.get('hitf.typeDefinition.view.message.title.instance.config').d('实例配置')}
              </h3>
            }
            className={DETAIL_CARD_TABLE_CLASSNAME}
          >
            <DetailTable {...detailTableProps} />
          </Card>
        </Content>
      </>
    );
  }
}
