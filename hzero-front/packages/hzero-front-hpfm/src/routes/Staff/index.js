/**
 * Staff - 岗位分配员工
 * @date: 2018-6-19
 * @author: WH <heng.wei@hand-china.com>
 * @version: 0.0.1
 *  @copyright Copyright (c) 2018, Hand
 */
import React, { Component, Fragment } from 'react';
import { connect } from 'dva';
import { Col, Icon, Row, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import classNames from 'classnames';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';

import List from './List';
import styles from './index.less';

const btnIconStyle = {
  marginLeft: 0,
};

const listColSpan = {
  xxl: 11,
  xl: 10,
  xs: 24,
};
const listBtnColSpan = {
  xxl: 2,
  xl: 4,
  xs: 24,
};

/**
 * 员工维护组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} staff - 数据源
 * @reactProps {!boolean} addibleLoading - 可添加员工数据加载是否完成
 * @reactProps {!boolean} addedLoading - 已添加员工数据加载是否完成
 * @reactProps {!String} tenantId - 租户ID
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */

@connect(({ loading, staff }) => ({
  staff,
  addedLoading: loading.effects['staff/fetchAddedStaff'],
  addibleLoading: loading.effects['staff/fetchAddibleStaff'],
  addStaffLoading: loading.effects['staff/addStaff'],
  deleteStaffLoading: loading.effects['staff/deleteStaff'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({
  code: ['hpfm.employee', 'entity.position', 'entity.employee', 'hpfm.staff'],
})
export default class Staff extends Component {
  constructor(props) {
    super(props);
    this.state = {
      addibleRowKeys: [], // 可添加员工RowKey
      addedRowKeys: [], // 已添加员工RowKey
      fistLoadedLoading: true, // 第一次进来时 loading
    };
  }

  /**
   * componentDidMount 生命周期函数
   * render调用后获取页面数据
   */
  componentDidMount() {
    const { dispatch, tenantId, match } = this.props;
    dispatch({
      type: 'staff/fetchPositionInfo',
      payload: {
        tenantId,
        positionId: match.params.positionId,
      },
    })
      .then(() => {
        return Promise.all([this.handleAddibleStaff(), this.handleAddedStaff()]);
      })
      .finally(() => {
        this.setState({
          fistLoadedLoading: false, // 第一次数据加载完后取消 loading
        });
      });
  }

  /**
   * 查询岗位可添加的员工信息
   * @param {Object} fields 查询条件
   */
  @Bind()
  handleAddibleStaff(fields = {}) {
    const { dispatch, tenantId, match, staff } = this.props;
    const {
      addibleStaff: { pagination },
      unitId,
    } = staff;
    const { left } = this.state;
    const fieldValues = isUndefined(left) ? {} : left.getFieldValue('option');
    return dispatch({
      type: 'staff/fetchAddibleStaff',
      payload: {
        tenantId,
        unitId,
        positionId: match.params.positionId,
        page: pagination,
        ...fieldValues,
        ...fields,
      },
    });
  }

  /**
   * 查询岗位已关联的员工信息
   * @param {Object} fields 查询条件
   */
  @Bind()
  handleAddedStaff(fields = {}) {
    const { dispatch, tenantId, match, staff } = this.props;
    const {
      addedStaff: { pagination },
      unitId,
    } = staff;
    const { right } = this.state;
    const fieldValues = isUndefined(right) ? {} : right.getFieldValue('option');
    return dispatch({
      type: 'staff/fetchAddedStaff',
      payload: {
        tenantId,
        unitId,
        page: pagination,
        positionId: match.params.positionId,
        ...fieldValues,
        ...fields,
      },
    });
  }

  /**
   * 未添加员工列表，数据操作
   * @param {Array} newSelectedRowKeys
   */
  @Bind()
  handleLeftRowSelectChange(newSelectedRowKeys) {
    this.setState({ addibleRowKeys: newSelectedRowKeys });
  }

  /**
   * 已添加员工列表，数据操作
   * @param {Array} newSelectedRowKeys
   */
  @Bind()
  handleRightRowSelectChange(newSelectedRowKeys) {
    this.setState({ addedRowKeys: newSelectedRowKeys });
  }

  /**
   * 添加员工
   * @param {Array} employeeList 待添加的员工列表
   */
  @Bind()
  handleAddStaff(employeeList) {
    const { dispatch, match, staff, tenantId } = this.props;
    dispatch({
      type: 'staff/addStaff',
      payload: {
        tenantId,
        employeeList,
        unitId: staff.unitId,
        positionId: match.params.positionId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleAddibleStaff();
        this.handleAddedStaff();
        this.setState({ addedRowKeys: [], addibleRowKeys: [] });
      }
    });
  }

  /**
   * 移除员工
   * @param {Array} employeeList 待移除的员工列表
   */
  @Bind()
  handleDeleteStaff(employeeList) {
    const { dispatch, match, staff, tenantId } = this.props;
    dispatch({
      type: 'staff/deleteStaff',
      payload: {
        tenantId,
        employeeList,
        unitId: staff.unitId,
        positionId: match.params.positionId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleAddibleStaff();
        this.handleAddedStaff();
        this.setState({ addedRowKeys: [], addibleRowKeys: [] });
      }
    });
  }

  /**
   * @param {object} ref - FilterForm子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.setState({ [ref.props.locate]: (ref.props || {}).form });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      addedLoading,
      addibleLoading,
      staff,
      match,
      addStaffLoading = false,
      deleteStaffLoading = false,
      location: { search },
    } = this.props;
    const { fromSource } = queryString.parse(search.substring(1));
    const { addibleStaff, addedStaff, positionName } = staff;
    const { addedRowKeys, addibleRowKeys, fistLoadedLoading = true } = this.state;
    const leftProps = {
      locate: 'left',
      loading: addibleLoading,
      selectedRowKeys: addibleRowKeys,
      dataSource: addibleStaff.list,
      pagination: addibleStaff.pagination,
      onSearch: this.handleAddibleStaff,
      rowSelectChange: this.handleLeftRowSelectChange,
      onChange: this.handleAddibleStaff,
      onRef: this.handleBindRef,
    };
    const rightProps = {
      locate: 'right',
      loading: addedLoading,
      selectedRowKeys: addedRowKeys,
      dataSource: addedStaff.list,
      pagination: addedStaff.pagination,
      onSearch: this.handleAddedStaff,
      rowSelectChange: this.handleRightRowSelectChange,
      onChange: this.handleAddedStaff,
      onRef: this.handleBindRef,
    };
    const backPath =
      fromSource === 'company'
        ? `/hpfm/hr/org/post/${staff.unitId}?fromSource=company`
        : `/hpfm/hr/org/post/${staff.unitId}`;
    return (
      <Fragment>
        <Header
          title={intl.get('hpfm.employee.view.message.title.assign').d('岗位分配员工')}
          backPath={backPath}
        />
        <Content className={classNames(styles['hpfm-hr-staff'])}>
          {fistLoadedLoading ? (
            <Spin spining />
          ) : (
            <React.Fragment>
              <p className={classNames(styles['hpfm-hr-title'])}>
                <span />
                {intl
                  .get('hpfm.staff.view.message.tips', {
                    name: positionName,
                  })
                  .d(`当前正在为「${positionName}」岗位，分配员工`)}
              </p>
              <Row gutter={16}>
                <Col {...listColSpan}>
                  <List {...leftProps} />
                </Col>
                <Col {...listBtnColSpan}>
                  <ButtonPermission
                    type="primary"
                    className={classNames(styles['btn-common'], styles['btn-add'])}
                    disabled={addibleRowKeys.length === 0}
                    loading={addStaffLoading}
                    permissionList={[
                      {
                        code: `${match.path}.button.add`,
                        type: 'button',
                        meaning: '岗位分配员工-添加岗位',
                      },
                    ]}
                    onClick={() => {
                      this.handleAddStaff(addibleRowKeys);
                    }}
                  >
                    {intl.get('hpfm.employee.view.option.add').d('添加岗位')}
                    <Icon type="right" style={btnIconStyle} />
                  </ButtonPermission>
                  <ButtonPermission
                    type="primary"
                    className={classNames(styles['btn-common'], styles['btn-delete'])}
                    disabled={addedRowKeys.length === 0}
                    loading={deleteStaffLoading}
                    permissionList={[
                      {
                        code: `${match.path}.button.remove`,
                        type: 'button',
                        meaning: '岗位分配员工-删除岗位',
                      },
                    ]}
                    onClick={() => {
                      this.handleDeleteStaff(addedRowKeys);
                    }}
                  >
                    <Icon type="left" style={btnIconStyle} />
                    {intl.get('hpfm.employee.view.option.remove').d('删除岗位')}
                  </ButtonPermission>
                </Col>
                <Col {...listColSpan}>
                  <List {...rightProps} />
                </Col>
              </Row>
            </React.Fragment>
          )}
        </Content>
      </Fragment>
    );
  }
}
