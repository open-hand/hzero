/**
 * Orgination - 员工定义
 * @date: 2018-6-27
 * @author: WH <heng.wei@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component, Fragment } from 'react';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import uuidv4 from 'uuid/v4';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';
import queryString from 'querystring';

import { CustBox as CustButton, WithCustomize } from 'components/Customize';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  addItemToPagination,
  delItemToPagination,
  filterNullValueObject,
  getCurrentOrganizationId,
  getEditTableData,
} from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import { openTab } from 'utils/menuTab';
import { HZERO_PLATFORM } from 'utils/config';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

/**
 * 业务组件 - 员工定义
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} employee - 数据源
 * @reactProps {!boolean} loading - 数据加载是否完成
 * @reactProps {!String} tenantId - 租户ID
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */
// @withFlexFields(flexModelCode)
@WithCustomize({
  unitCode: ['HPFM.EMPLOYEE_DEFINITION.HEADER_FILTER', 'HPFM.EMPLOYEE_DEFINITION.LINE.GRID'],
})
@connect(({ employee, loading }) => ({
  employee,
  loading: loading.effects['employee/fetchEmployeeData'],
  saveLoading: loading.effects['employee/saveEmployee'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hpfm.employee', 'entity.employee'] })
export default class List extends Component {
  /**
   * state初始化
   * @param {object} props - 组件Props
   */
  constructor(props) {
    super(props);
    this.state = {
      // flexFieldsConfig: [],
    };
  }

  /**
   * componentDidMount 生命周期函数
   * render后请求页面数据
   */
  componentDidMount() {
    const {
      location: { state: { _back } = {} },
      employee: { pagination = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = _back === -1 ? pagination : {};
    this.fetchEnum();
    this.handleSearchEmployee(page);
    // getFlexFieldsConfig(ruleCode).then(flexFieldsConfig => {
    //   this.setState({
    //     flexFieldsConfig,
    //   });
    // });
  }

  @Bind()
  fetchEnum() {
    const { dispatch } = this.props;
    dispatch({
      type: 'employee/fetchEnum',
    });
  }

  /**
   * 查询
   * @param {Object} fields 查询参数
   */
  @Bind()
  handleSearchEmployee(fields = {}) {
    const { dispatch, tenantId } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());

    dispatch({
      type: 'employee/fetchEmployeeData',
      payload: {
        tenantId,
        page: isEmpty(fields) ? {} : fields,
        customizeUnitCode: 'HPFM.EMPLOYEE_DEFINITION.LINE.GRID',
        ...fieldValues, // 表单查询值
      },
    });
  }

  /**
   * 新增员工信息
   */
  @Bind()
  handleAddEmployee() {
    const {
      dispatch,
      tenantId,
      employee: { list = [], pagination = {} },
    } = this.props;
    const newItem = {
      tenantId,
      cid: '',
      employeeNum: '',
      name: '',
      email: '',
      mobile: '',
      employeeId: uuidv4(),
      gender: 0,
      enabledFlag: 1, // 启用标记
      _status: 'create', // 新建员工标记位
    };
    dispatch({
      type: 'employee/updateState',
      payload: {
        list: [newItem, ...list],
        pagination: addItemToPagination(list.length, pagination),
      },
    });
  }

  /**
   * 员工信息批量保存
   * 保存对象: 新增数据
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      tenantId,
      employee: { list = [], pagination = {} },
    } = this.props;
    const params = getEditTableData(list, ['employeeId']);
    if (Array.isArray(params) && params.length !== 0) {
      const arr = params.map((item) => {
        const temp = item;
        temp.entryDate = temp.entryDate && temp.entryDate.format(DEFAULT_DATE_FORMAT);
        return temp;
      });

      dispatch({
        type: 'employee/saveEmployee',
        payload: {
          tenantId,
          customizeUnitCode: 'HPFM.EMPLOYEE_DEFINITION.LINE.GRID',
          saveData: [...arr],
        },
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleSearchEmployee(pagination);
        }
      });
    }
  }

  /**
   * 批量导入
   */
  @Bind()
  handleBatchImport() {
    openTab({
      key: '/hpfm/hr/staff/data-import/HPFM.EMPLOYEE',
      search: queryString.stringify({
        key: '/hpfm/hr/staff/data-import/HPFM.EMPLOYEE',
        title: 'hzero.common.title.batchImport',
        action: 'hzero.common.title.batchImport',
        auto: true,
        prefixPatch: HZERO_PLATFORM,
      }),
    });
  }

  /**
   * 批量导入
   */
  @Bind()
  handleUserImport() {
    openTab({
      key: '/hpfm/hr/staff/data-import/HPFM.EMPLOYEE_USER',
      search: queryString.stringify({
        key: '/hpfm/hr/staff/data-import/HPFM.EMPLOYEE_USER',
        title: 'hzero.common.title.userImport',
        action: 'hzero.common.title.userImport',
        prefixPatch: HZERO_PLATFORM,
      }),
    });
  }

  /**
   * 批量导入
   */
  @Bind()
  handleStationImport() {
    openTab({
      key: '/hpfm/hr/staff/data-import/HPFM.EMPLOYEE_ASSIGN',
      search: queryString.stringify({
        key: '/hpfm/hr/staff/data-import/HPFM.EMPLOYEE_ASSIGN',
        title: 'hzero.common.title.stationImport',
        action: 'hzero.common.title.stationImport',
        prefixPatch: HZERO_PLATFORM,
      }),
    });
  }

  /**
   * 清除新增员工信息
   * @param {Object} record 员工信息
   */
  @Bind()
  handleCleanLine(record) {
    const {
      dispatch,
      employee: { list = [], pagination = {} },
    } = this.props;
    const newList = list.filter((item) => item.employeeId !== record.employeeId);
    dispatch({
      type: 'employee/updateState',
      payload: {
        list: [...newList],
        pagination: delItemToPagination(list.length, pagination),
      },
    });
  }

  /**
   * 获取员工明细，跳转明细页面
   * @param {number} employeeId - 员工Id
   * @param {number} employeeNum - 员工编码
   */
  @Bind()
  handleEditEmployee(employeeId, employeeNum) {
    const { dispatch } = this.props;
    // 清除明细缓存
    dispatch({
      type: 'employee/updateState',
      payload: {
        positionList: [],
        userList: [],
      },
    });
    dispatch(
      routerRedux.push({
        pathname: `/hpfm/hr/staff/detail/${employeeId}/${employeeNum}`,
      })
    );
  }

  /**
   * 设置form对象
   * @param {object} ref - FilterForm子组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  getSaveBtnDisabled() {
    const {
      employee: { list = [] },
    } = this.props;
    return !list.some((record) => record._status === 'create');
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      saveLoading,
      match,
      employee: {
        list = [],
        pagination = {},
        lov: { employeeStatus = [] },
      },
      customizeFilterForm,
      customizeTable,
      // flexFieldsMiddleware = {},
    } = this.props;
    // const { flexFieldsConfig } = this.state;
    // const { FlexFieldsButton } = flexFieldsMiddleware;

    const filterProps = {
      customizeFilterForm,
      onSearch: this.handleSearchEmployee,
      onRef: this.handleBindRef,
      // flexFieldsMiddleware,
    };
    const listProps = {
      pagination,
      loading,
      match,
      dataSource: list,
      onClean: this.handleCleanLine,
      onEdit: this.handleEditEmployee,
      onSearch: this.handleSearchEmployee,
      employeeStatus,
      customizeTable,
      // flexFieldsTriggers: flexFieldsMiddleware.flexFieldsTriggers || [],
      onRef: (node) => {
        this.list = node;
      },
    };

    return (
      <Fragment>
        <Header title={intl.get('hpfm.employee.view.message.title.define').d('员工定义')}>
          <ButtonPermission
            icon="save"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.save`,
                type: 'button',
                meaning: '员工定义-保存',
              },
            ]}
            onClick={this.handleSave}
            loading={saveLoading}
            disabled={loading || this.getSaveBtnDisabled()}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '员工定义-新建',
              },
            ]}
            onClick={this.handleAddEmployee}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            icon="upload"
            permissionList={[
              {
                code: `${match.path}.button.batchImport`,
                type: 'button',
                meaning: '员工定义-批量导入',
              },
            ]}
            onClick={this.handleBatchImport}
          >
            {intl.get('hzero.common.title.batchImport').d('批量导入')}
          </ButtonPermission>
          <ButtonPermission
            icon="share-alt"
            permissionList={[
              {
                code: `${match.path}.button.userImport`,
                type: 'button',
                meaning: '员工定义-用户分配',
              },
            ]}
            onClick={this.handleUserImport}
          >
            {intl.get('hzero.common.title.userImport').d('用户分配')}
          </ButtonPermission>
          <ButtonPermission
            icon="fork"
            permissionList={[
              {
                code: `${match.path}.button.stationImport`,
                type: 'button',
                meaning: '员工定义-岗位分配',
              },
            ]}
            onClick={this.handleStationImport}
          >
            {intl.get('hzero.common.title.stationImport').d('岗位分配')}
          </ButtonPermission>
          <CustButton
            unit={[
              {
                code: 'HPFM.EMPLOYEE_DEFINITION.LINE.GRID',
              },
            ]}
          />
          {/* <FlexFieldsButton /> */}
        </Header>
        <Content>
          <div>
            <FilterForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
        </Content>
      </Fragment>
    );
  }
}
