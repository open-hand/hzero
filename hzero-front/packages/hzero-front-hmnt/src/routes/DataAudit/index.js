/**
 * DataAudit - 数据变更审计
 * @date: 2019-7-9
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import queryString from 'query-string';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import { routerRedux } from 'dva/router';

import { Content, Header } from 'components/Page';
import ExcelExport from 'components/ExcelExport';

import intl from 'utils/intl';
import { HZERO_MNT } from 'utils/config';
import { filterNullValueObject, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

const isTenant = isTenantRoleLevel();

/**
 * 数据审计
 * @extends {Component} - Component
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} dataAudit - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ dataAudit, loading }) => ({
  dataAudit,
  organizationId: getCurrentOrganizationId(),
  loading: loading.effects['dataAudit/fetchAuditList'],
}))
@formatterCollections({
  code: ['hmnt.dataAudit'],
})
export default class DataAudit extends Component {
  form;

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      dataAudit: {
        auditList: { pagination = {} },
      },
      location: { state: { _back } = {}, search },
    } = this.props;
    const page = isUndefined(_back) ? {} : pagination;
    const { auditBatchNumber, auditDataId = '' } = queryString.parse(search);
    if (auditBatchNumber && this.form) {
      this.form.setFieldsValue({ auditBatchNumber });
    }
    this.handleSearch(page, auditDataId);
  }

  componentDidUpdate(prevProps) {
    const {
      location: { search },
    } = this.props;
    const {
      location: { search: prevSearch },
    } = prevProps;
    if (search !== prevProps) {
      const { auditBatchNumber, auditDataId } = queryString.parse(search);
      const {
        auditBatchNumber: prevAuditBatchNumber,
        auditDataId: AreauditDataId,
      } = queryString.parse(prevSearch);
      if (auditBatchNumber !== prevAuditBatchNumber) {
        if (this.form) {
          this.form.setFieldsValue({
            auditBatchNumber,
          });
          this.handleSearch();
        }
      }
      if (auditDataId !== AreauditDataId) {
        this.handleSearch({}, auditDataId);
      }
    }
  }

  /**
   * 查询
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}, auditDataId = '') {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    fieldValues.startProcessTime =
      fieldValues.startProcessTime && fieldValues.startProcessTime.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.endProcessTime =
      fieldValues.endProcessTime && fieldValues.endProcessTime.format(DEFAULT_DATETIME_FORMAT);
    const data = String(auditDataId) ? { auditDataId } : {};
    dispatch({
      type: 'dataAudit/fetchAuditList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
        ...data,
      },
    });
  }

  /**
   * 查看详情
   * @param {number} entityId - 实体ID
   */
  @Bind()
  goDetail(entityId) {
    const { dispatch } = this.props;
    dispatch(
      routerRedux.push({
        pathname: `/hmnt/data-audit/detail/${entityId}`,
      })
    );
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  @Bind()
  getSearchFormData() {
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    fieldValues.startProcessTime =
      fieldValues.startProcessTime && fieldValues.startProcessTime.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.endProcessTime =
      fieldValues.endProcessTime && fieldValues.endProcessTime.format(DEFAULT_DATETIME_FORMAT);
    return fieldValues;
  }

  render() {
    const {
      dataAudit: {
        auditList: { pagination = {}, list = [] },
      },
      loading,
      organizationId,
    } = this.props;
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
      isTenant,
    };
    const listProps = {
      auditList: {
        dataSource: list,
        pagination,
      },
      loading,
      onChange: this.handleSearch,
      onView: this.goDetail,
      isTenant,
    };
    return (
      <>
        <Header title={intl.get('hmnt.dataAudit.view.message.title').d('数据审计查询')}>
          <ExcelExport
            requestUrl={`${HZERO_MNT}/v1/${
              isTenantRoleLevel() ? `${organizationId}/audit-datas/export` : '/audit-datas/export'
            }`}
            queryParams={this.getSearchFormData}
          />
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
        </Content>
      </>
    );
  }
}
