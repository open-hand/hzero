/**
 * Detail - 服务计费配置
 * @date: 2019/8/28
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Button, Card, Tabs, Modal } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { isTenantRoleLevel } from 'utils/utils';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { Header, Content } from 'components/Page';

import DetailForm from './DetailForm';
import DetailTable from './DetailTable';

const { TabPane } = Tabs;
const isTenant = isTenantRoleLevel();

/**
 * 服务计费配置
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} serviceCharge - 数据源
 * @reactProps {boolean} fetchHeadLoading - 表单数据加载是否完成
 * @reactProps {boolean} preListLoading - 预付费表格数据加载是否完成
 * @reactProps {boolean} postListLoading - 后付费表格数据加载是否完成
 * @reactProps {boolean} saveHeadLoading - 保存计费组加载是否完成
 * @reactProps {boolean} createChargeRuleLoading - 创建计费规则行加载标志
 * @reactProps {boolean} updateChargeRuleLoading - 更新计费规则行加载标志
 * @reactProps {boolean} deleteChargeRuleLoading - 删除计费规则行加载标志
 * @reactProps {boolean} fetchRuleDetailLoading -查询计费规则详情加载标志
 * @reactProps {boolean} chargeScopeLoading - 服务范围加载标志
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ serviceCharge, loading }) => ({
  serviceCharge,
  fetchHeadLoading: loading.effects['serviceCharge/queryGroupDetail'],
  preListLoading:
    loading.effects['serviceCharge/queryPreRuleList'] ||
    loading.effects['serviceCharge/deleteChargeRule'],
  postListLoading:
    loading.effects['serviceCharge/queryPostRuleList'] ||
    loading.effects['serviceCharge/deleteChargeRule'],
  saveHeadLoading:
    loading.effects['serviceCharge/createGroup'] || loading.effects['serviceCharge/updateGroup'],
  fetchRuleDetailLoading: loading.effects['serviceCharge/queryRuleDetail'],
  deleteChargeRuleLoading: loading.effects['serviceCharge/deleteChargeRule'],
  createChargeRuleLoading: loading.effects['serviceCharge/createChargeRule'],
  updateChargeRuleLoading: loading.effects['serviceCharge/updateChargeRule'],
  chargeScopeLoading:
    loading.effects['serviceCharge/queryChargeScope'] ||
    loading.effects['serviceCharge/createChargeScope'] ||
    loading.effects['serviceCharge/deleteChargeScope'],
}))
@formatterCollections({
  code: ['hchg.serviceCharge'],
})
export default class Detail extends Component {
  state = {
    selectedRowKeys: [],
    selectedRows: [],
  };

  componentDidMount() {
    const { match } = this.props;
    const { id } = match.params;
    if (id) {
      this.fetchHeadInfo();
      this.fetchPreList();
    }
    this.handleQueryIdpValue();
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceCharge/updateState',
      payload: {
        groupDetail: {},
        preList: {
          dataSource: [], // 预付费列表
          pagination: {}, // 预付费分页
        },
        postList: {
          dataSource: [], // 后付费列表
          pagination: {}, // 后付费分页
        },
      },
    });
  }

  // 查询计费组详情
  @Bind()
  fetchHeadInfo() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    dispatch({
      type: 'serviceCharge/queryGroupDetail',
      payload: { chargeGroupId: id },
    });
  }

  /**
   * 查询值集
   */
  @Bind()
  handleQueryIdpValue() {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceCharge/queryIdpValue',
    });
  }

  /**
   * 查询预付费规则
   */
  @Bind()
  fetchPreList(fields = {}) {
    const { dispatch, match, preListLoading } = this.props;
    if (preListLoading) return;
    const { id } = match.params;
    dispatch({
      type: 'serviceCharge/queryPreRuleList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        chargeGroupId: id,
        paymentMethod: 'PRE',
      },
    });
  }

  /**
   * 查询后付费规则
   */
  @Bind()
  fetchPostList(fields = {}) {
    const { dispatch, match, postListLoading } = this.props;
    if (postListLoading) return;
    const { id } = match.params;
    dispatch({
      type: 'serviceCharge/queryPostRuleList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        chargeGroupId: id,
        paymentMethod: 'POST',
      },
    });
  }

  /**
   * 新建或更新计费组头
   */
  @Bind()
  handleSaveGroupHead() {
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
   * 保存计费组头
   * @param {Object} values - 表单数据
   */
  @Bind()
  save(values) {
    const {
      serviceCharge: { groupDetail = {} },
      match = {},
    } = this.props;
    const { id } = match.params;
    let nextValues = { ...values };
    if (id) {
      const { chargeGroupId, objectVersionNumber, _token, status } = groupDetail;
      nextValues = {
        ...nextValues,
        chargeGroupId,
        objectVersionNumber,
        status,
        _token,
      };
      this.handleUpdateGroup(nextValues);
    } else {
      this.handleCreateGroup(nextValues);
    }
  }

  /**
   * 更新数据组
   * @param {object} payload - 表单数据
   */
  @Bind()
  handleUpdateGroup(payload) {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceCharge/updateGroup',
      payload,
    }).then(response => {
      if (response) {
        notification.success();
        this.fetchHeadInfo();
      }
    });
  }

  /**
   * 创建数据组
   * @param {object} payload - 表单数据
   */
  @Bind()
  handleCreateGroup(payload) {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceCharge/createGroup',
      payload: {
        ...payload,
        status: 'NEW',
      },
    }).then(response => {
      if (response) {
        notification.success();
        this.redirectToEdit(response.chargeGroupId);
      }
    });
  }

  /**
   * 跳转至编辑页面
   *@param {number} chargeGroupId - 计费组ID
   */
  @Bind()
  redirectToEdit(chargeGroupId) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hchg/service-charge/detail/${chargeGroupId}`,
      })
    );
  }

  /**
   * 切换面板
   * @param {string} activeKey - 当前激活面板
   */
  @Bind()
  handleChangeTab(activeKey) {
    const { match = {} } = this.props;
    const { id } = match.params;
    if (!id) return;
    this.setState(
      {
        selectedRowKeys: [],
        selectedRows: [],
      },
      () => {
        if (activeKey === 'pre') {
          this.fetchPreList();
        } else {
          this.fetchPostList();
        }
      }
    );
  }

  /**
   * 选择行
   * @param {array} selectedRowKeys 选中行的key
   * @param {array} selectedRows 选中行
   */
  @Bind()
  handleRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRowKeys,
      selectedRows,
    });
  }

  /**
   * 删除计费规则
   */
  @Bind()
  deleteChargeRule(type) {
    const { dispatch } = this.props;
    const { selectedRows } = this.state;
    const that = this;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk() {
        dispatch({
          type: 'serviceCharge/deleteChargeRule', // 删除
          payload: selectedRows,
        }).then(res => {
          if (res) {
            notification.success({});
            that.setState(
              {
                selectedRowKeys: [],
                selectedRows: {},
              },
              () => {
                if (type === 'PRE') {
                  that.fetchPreList();
                } else {
                  that.fetchPostList();
                }
              }
            );
          }
        });
      },
    });
  }

  /**
   * 查询计费规则详情
   * @param {number} chargeRuleId - 计费规则ID
   */
  @Bind()
  fetchRuleDetail(chargeRuleId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceCharge/queryRuleDetail',
      chargeRuleId,
    });
  }

  /**
   * 创建计费规则
   * @param {object} payload - 计费规则表单数据
   */
  @Bind()
  createChargeRule(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'serviceCharge/createChargeRule',
      payload,
    });
  }

  /**
   * 更新计费规则
   * @param {object} payload - 计费规则表单数据
   */
  @Bind()
  updateChargeRule(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'serviceCharge/updateChargeRule',
      payload,
    });
  }

  /**
   * 查询服务计费范围
   */
  @Bind()
  queryChargeScope(fields = {}, chargeRuleId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceCharge/queryChargeScope',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        chargeRuleId,
      },
    });
  }

  /**
   * 创建服务计费范围
   * @param {object} payload - 选择的行
   */
  @Bind()
  createChargeScope(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'serviceCharge/createChargeScope',
      payload,
    });
  }

  /**
   * 删除服务计费范围
   * @param {object} payload - 选择的行
   */
  @Bind()
  deleteChargeScope(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'serviceCharge/deleteChargeScope',
      payload,
    });
  }

  /**
   * 清空规则行数据
   */
  @Bind()
  cleanData() {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceCharge/updateState',
      payload: {
        ruleDetail: {},
        chargeScopeList: {
          dataSource: [],
          pagination: {},
        },
      },
    });
  }

  /**
   * 更新阶梯行列表
   * @param {object} payload - 选择的行
   */
  @Bind()
  updateChargeRuleLineList(payload) {
    const { dispatch } = this.props;
    dispatch({
      type: 'serviceCharge/updateState',
      payload,
    });
  }

  render() {
    const {
      match,
      serviceCharge = {},
      fetchHeadLoading = false,
      preListLoading,
      postListLoading,
      saveHeadLoading,
      deleteChargeRuleLoading,
      fetchRuleDetailLoading = false,
      createChargeRuleLoading,
      updateChargeRuleLoading,
      chargeScopeLoading,
    } = this.props;
    const { selectedRowKeys } = this.state;
    const {
      enumMap = {},
      groupDetail = {},
      preList = {},
      postList = {},
      ruleDetail = {},
      chargeScopeList = {},
    } = serviceCharge;
    const { id } = match.params;
    const isCreate = !id;
    const isPublished = groupDetail.status === 'PUBLISHED';
    const {
      cycleTypes = [],
      chargeMethodTypes = [],
      chargeTypes = [],
      measureBasisTypes = [],
      chargeBasisTypes = [],
      intervalCycleTypes = [],
      intervalMeasureTypes = [],
    } = enumMap;
    const formProps = {
      dataSource: groupDetail,
      isCreate,
      isPublished,
      isTenant,
      cycleTypes,
      wrappedComponentRef: node => {
        this.form = node;
      },
    };
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const commonTableProps = {
      isCreate,
      groupStatus: groupDetail.status,
      chargeGroupId: groupDetail.chargeGroupId,
      rowSelection,
      ruleDetail,
      chargeScopeList,
      lovCode: groupDetail.chargeService,
      deleteChargeRuleLoading,
      fetchRuleDetailLoading,
      createChargeRuleLoading,
      updateChargeRuleLoading,
      chargeScopeLoading,
      chargeMethodTypes,
      chargeTypes,
      measureBasisTypes,
      chargeBasisTypes,
      intervalCycleTypes,
      intervalMeasureTypes,
      fetchRuleDetail: this.fetchRuleDetail,
      fetchPreList: this.fetchPreList,
      fetchPostList: this.fetchPostList,
      createChargeRule: this.createChargeRule,
      updateChargeRule: this.updateChargeRule,
      deleteChargeRule: this.deleteChargeRule,
      createChargeScope: this.createChargeScope,
      deleteChargeScope: this.deleteChargeScope,
      queryChargeScope: this.queryChargeScope,
      cleanData: this.cleanData,
      updateChargeRuleLineList: this.updateChargeRuleLineList,
    };
    const preProps = {
      ...commonTableProps,
      type: 'PRE',
      loading: preListLoading,
      dataSource: preList.dataSource || [],
      pagination: preList.pagination || {},
      ref: node => {
        this.preTable = node;
      },
    };
    const postProps = {
      ...commonTableProps,
      type: 'POST',
      loading: postListLoading,
      dataSource: postList.dataSource || [],
      pagination: postList.pagination || {},
      ref: node => {
        this.postTable = node;
      },
    };
    return (
      <>
        <Header
          title={intl
            .get('hchg.serviceCharge.view.title.serviceCharge.detail')
            .d('服务计费配置详情')}
          backPath="/hchg/service-charge/list"
        >
          {(isCreate || groupDetail.status === 'NEW' || groupDetail.status === 'CANCELLED') && (
            <Button
              icon="save"
              type="primary"
              loading={saveHeadLoading}
              onClick={this.handleSaveGroupHead}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          )}
        </Header>
        <Content>
          <Card
            key="data-group-head"
            bordered={false}
            title={<h3>{intl.get('hchg.serviceCharge.view.title.configInfo').d('配置信息')}</h3>}
            className={DETAIL_CARD_CLASSNAME}
            loading={fetchHeadLoading}
          >
            <DetailForm {...formProps} />
          </Card>
          <Tabs animated={false} forceRender onChange={this.handleChangeTab}>
            <TabPane
              tab={intl.get('hchg.serviceCharge.view.title.pay.before').d('预付费')}
              key="pre"
            >
              <DetailTable {...preProps} />
            </TabPane>
            <TabPane
              tab={intl.get('hchg.serviceCharge.view.title.pay.after').d('后付费')}
              key="post"
            >
              <DetailTable {...postProps} />
            </TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}
