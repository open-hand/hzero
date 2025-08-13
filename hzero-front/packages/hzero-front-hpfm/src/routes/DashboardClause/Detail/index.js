/**
 * DashboardClauseDetail - 条目配置新建/编辑
 * @date: 2019-03-07
 * @author: YB <bo.yang02@hand-chinacom>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */

import React, { Component } from 'react';
import { Card, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import queryString from 'querystring';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { delItemsToPagination } from 'utils/utils';
import { DETAIL_CARD_CLASSNAME, DETAIL_CARD_TABLE_CLASSNAME } from 'utils/constants';

import DetailForm from './DetailForm';
import DetailTable from './DetailTable';

const promptCode = 'hpfm.dashboardClause';

/**
 * 条目配置新建/编辑
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} dashboard - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ dashboardClause, loading }) => ({
  dashboardClause,
  loading:
    loading.effects['dashboardClause/fetchHead'] || loading.effects['dashboardClause/fetchTable'],
  saving: loading.effects['dashboardClause/saveClause'],
  deleting: loading.effects['dashboardClause/deleteCard'],
}))
export default class DashboardClauseDetail extends Component {
  constructor(props) {
    super(props);
    const {
      match: {
        params: { clauseId },
      },
    } = props;
    this.state = {
      clauseId,
      isEdit: !!clauseId,
    };
  }

  componentDidMount() {
    const {
      dashboardClause: { clauseDetailPagination = {} },
    } = this.props;
    const { clauseId } = this.state;
    this.queryValueCode();
    if (clauseId !== undefined) {
      this.handleSearchHead();
      this.handleSearchTable(clauseDetailPagination);
    }
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'dashboardClause/updateState',
      payload: {
        clauseDetailHead: {},
        clauseDetailTableList: [],
        clauseDetailPagination: {},
      },
    });
  }

  /**
   * 查询值集
   */
  @Bind()
  queryValueCode() {
    const { dispatch } = this.props;
    dispatch({
      type: 'dashboardClause/init',
    });
  }

  /**
   * 查询卡片头
   */
  @Bind()
  handleSearchHead() {
    const { dispatch } = this.props;
    const { clauseId } = this.state;
    dispatch({
      type: 'dashboardClause/fetchHead',
      payload: clauseId,
    });
  }

  /**
   * 查询卡片头
   */
  @Bind()
  handleSearchTable(page = {}) {
    const { dispatch } = this.props;
    const { clauseId } = this.state;
    dispatch({
      type: 'dashboardClause/fetchTable',
      payload: {
        page,
        clauseId,
      },
    });
  }

  /**
   * 保存条目配置
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      history,
      dashboardClause: {
        clauseDetailHead = {},
        clauseDetailTableList = [],
        clauseDetailPagination = {},
      },
      match: { path },
      location: { search },
    } = this.props;
    const { isEdit } = this.state;
    const form = this.form || {};
    form.validateFields((err, filterValues) => {
      if (!err) {
        const createRows = clauseDetailTableList
          .filter(o => o.isLocal)
          .map(item => {
            const { id, isLocal, ...other } = item;
            return other;
          });
        dispatch({
          type: 'dashboardClause/saveClause',
          payload: {
            ...clauseDetailHead,
            ...filterValues,
            isEdit,
            dashboardCardClauseList: createRows,
          },
        }).then(res => {
          if (res) {
            notification.success();
            if (isEdit) {
              this.handleSearchHead();
              this.handleSearchTable(clauseDetailPagination);
            } else {
              const router = `/hpfm/dashboard-clause/detail/${res.clauseId}`;
              const { access_token: accessToken } = queryString.parse(search.substring(1));
              history.push({
                pathname: path.indexOf('/private') === 0 ? `/private${router}` : `${router}`,
                search: path.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
              });
            }
          }
        });
      }
    });
  }

  /**
   * 删除卡片
   * @param {Array} selectedRows 选中行
   */
  @Bind()
  handleDelete(selectedRows) {
    const {
      dispatch,
      dashboardClause: { clauseDetailTableList = [], clauseDetailPagination = {} },
    } = this.props;
    const newSelectedRows = selectedRows.map(o => o.id);
    const newDataList = clauseDetailTableList.filter(o => !newSelectedRows.includes(o.id));
    dispatch({
      type: 'dashboardClause/updateState',
      payload: {
        clauseDetailTableList: newDataList,
        clauseDetailPagination: delItemsToPagination(
          selectedRows.length,
          clauseDetailTableList.length,
          clauseDetailPagination
        ),
      },
    });

    const idList = selectedRows.filter(o => !o.isLocal);
    if (!isEmpty(idList)) {
      dispatch({
        type: 'dashboardClause/deleteCard',
        payload: idList,
      }).then(res => {
        if (res) {
          notification.success();
          if (isEmpty(newDataList)) this.handleSearchTable();
        }
      });
    }
  }

  /**
   * @param {object} ref - Form子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = ref.props.form;
  }

  render() {
    const {
      loading = false,
      saving = false,
      deleting = false,
      match,
      dashboardClause: {
        flags = [],
        clauseDetailHead = {},
        clauseDetailTableList = [],
        clauseDetailPagination = {},
      },
      location: { search },
    } = this.props;
    const { isEdit, clauseId } = this.state;
    const formProps = {
      isEdit,
      flags,
      headInfo: clauseDetailHead,
      onRef: this.handleBindRef,
    };
    const tableProps = {
      match,
      clauseId,
      deleting,
      headInfo: clauseDetailHead,
      dataSource: clauseDetailTableList,
      pagination: clauseDetailPagination,
      onDelete: this.handleDelete,
      onTableChange: this.handleSearchTable,
    };
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    return (
      <>
        <Header
          backPath={
            match.path.indexOf('/private') === 0
              ? `/private/hpfm/dashboard-clause/list?access_token=${accessToken}`
              : '/hpfm/dashboard-clause/list'
          }
          title={
            isEdit
              ? intl.get(`${promptCode}.view.message.title.clauseEdit`).d('条目配置编辑')
              : intl.get(`${promptCode}.view.message.title.clauseAdd`).d('条目配置创建')
          }
        >
          <ButtonPermission
            type="primary"
            icon="save"
            loading={saving}
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '条目配置明细-保存',
              },
            ]}
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Spin spinning={isEdit ? loading : false}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>
                  {intl.get(`${promptCode}.view.message.title.dashboardClause`).d('条目配置')}
                </h3>
              }
              loading={false}
            >
              <DetailForm {...formProps} />
            </Card>
            {isEdit && (
              // 只有编辑状态下才能分配卡片
              <Card
                bordered={false}
                className={DETAIL_CARD_TABLE_CLASSNAME}
                title={
                  <h3>{intl.get(`${promptCode}.view.message.title.dashboardCard`).d('卡片')}</h3>
                }
              >
                <DetailTable {...tableProps} />
              </Card>
            )}
          </Spin>
        </Content>
      </>
    );
  }
}
