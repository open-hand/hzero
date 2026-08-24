/**
 * RuleEngine - 规则引擎
 * @date: 2018-9-28
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { filterNullValueObject } from 'utils/utils';

import FilterForm from './FilterForm';
import TableList from './TableList';

/**
 * 规则引擎组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} ruleEngine - 数据源
 * @reactProps {!Object} queryTableListLoading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */
@connect(({ ruleEngine, loading }) => ({
  ruleEngine,
  queryTableListLoading: loading.effects['ruleEngine/queryTableList'],
}))
@formatterCollections({ code: ['hpfm.ruleEngine'] })
export default class List extends PureComponent {
  form;

  state = {
    formValues: {}, // 表单中的值
  };

  /**
   * componentDidMount 生命周期函数
   * render()执行后获取页面数据
   */
  componentDidMount() {
    const {
      ruleEngine: { pagination = {} },
      dispatch,
    } = this.props;
    this.queryTableList(pagination);
    this.queryScriptTypeCode();
    dispatch({
      type: 'ruleEngine/queryCategoryList',
    });
  }

  /**
   * 查询规则引擎数据
   *
   * @param {*} fields
   * @memberof RuleEngine
   */
  @Bind()
  queryTableList(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'ruleEngine/queryTableList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 获取脚本类型
   */
  @Bind()
  queryScriptTypeCode() {
    const { dispatch } = this.props;
    dispatch({
      type: 'ruleEngine/queryScriptTypeCode',
    });
  }

  /**
   * 保存表单中的值
   *
   * @param {*} values
   * @memberof RuleEngine
   */
  @Bind()
  storeFormValues(values) {
    this.setState({
      formValues: { ...values },
    });
  }

  /**
   * 新增模板，跳转到明细页面
   */
  @Bind()
  handleAddTemplate() {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hpfm/rule-engine/create`,
      })
    );
  }

  /**
   * 修改规则引擎信息，跳转到明细页面
   * @param {object} record - 规则引擎对象
   */
  @Bind()
  handleEditContent(record) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hpfm/rule-engine/detail/${record.ruleScriptId}`,
      })
    );
  }

  /**
   * 删除引擎规则
   *
   * @param {*} record
   * @memberof RuleEngine
   */
  @Bind()
  handleDelete(record) {
    const {
      dispatch,
      ruleEngine: { pagination },
    } = this.props;
    dispatch({
      type: 'ruleEngine/deleteRuleEngine',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.queryTableList(pagination);
      }
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

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      queryTableListLoading,
      match,
      ruleEngine: { ruleEngineData = {}, scriptTypeCode = [], pagination = {}, categoryList = [] },
    } = this.props;
    const { formValues = {} } = this.state;
    const filterProps = {
      scriptTypeCode,
      categoryList,
      onSearch: this.queryTableList,
      onStoreValues: this.storeFormValues,
      onRef: this.handleBindRef,
    };
    const listProps = {
      formValues,
      ruleEngineData,
      pagination,
      match,
      loading: queryTableListLoading,
      onChange: this.queryTableList,
      onEdit: this.handleEditContent,
      onDelete: this.handleDelete,
    };
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.ruleEngine.view.message.title.ruleEngine').d('规则脚本')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '规则引擎-新建',
              },
            ]}
            onClick={this.handleAddTemplate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <TableList {...listProps} />
        </Content>
      </React.Fragment>
    );
  }
}
