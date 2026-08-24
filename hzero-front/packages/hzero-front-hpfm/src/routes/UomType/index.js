/*
 * index - 计量单位类型定义
 * @date: 2018/08/07 14:30:36
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { isEmpty, isUndefined } from 'lodash';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { filterNullValueObject } from 'utils/utils';
import { Content, Header } from 'components/Page';

import FilterForm from './FilterForm';
import UomForm from './UomForm';
import ListTable from './ListTable';

const promptCode = 'hpfm.uomType';

/**
 * 计量单位类型定义
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} uomType - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} formDom - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@connect(({ loading, uomType }) => ({
  loading: loading.effects['uomType/fetchUomList'],
  saving: loading.effects['uomType/addUomTypes'],
  uomType,
}))
@formatterCollections({ code: ['hpfm.uomType'] })
export default class UomType extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      uom: {},
      addVisible: false,
    };
  }

  static propTypes = {
    dispatch: PropTypes.func,
  };

  static defaultProps = {
    dispatch: e => e,
  };

  componentDidMount() {
    this.handleSearch();
  }

  /**
   * 查询平台计量单位类型列表
   * @param {obj} page 查询字段
   */
  @Bind()
  handleSearch(page = {}) {
    const { dispatch } = this.props;
    const filterValues = isUndefined(this.formDom)
      ? {}
      : filterNullValueObject(this.formDom.getFieldsValue());
    dispatch({
      type: 'uomType/fetchUomList',
      payload: {
        page,
        ...filterValues,
      },
    });
  }

  /**
   * 新建计量单位类型定义
   */
  @Bind()
  handleCreateUom() {
    this.setState({ uom: { enabledFlag: 1 } }, this.handleModalVisible(true));
  }

  /**
   * 隐藏模态框
   */
  @Bind()
  hideModal() {
    const { saving = false } = this.props;
    this.setState({ uom: {} });
    if (!saving) {
      this.handleModalVisible(false);
    }
  }

  /**
   * 修改当前行信息
   * @param {obj} uom
   */
  @Bind()
  editLine(uom) {
    this.setState({ uom }, this.handleModalVisible(true));
  }

  /**
   * 改变当前模态框显示状态
   * @param {boolean} flag
   */
  @Bind()
  handleModalVisible(flag) {
    this.setState({ addVisible: flag });
  }

  /**
   * 新增单位类型定义
   * @param {Object} fields
   */
  @Bind()
  handleAdd(fields) {
    const {
      dispatch,
      uomType: { pagination },
    } = this.props;
    const { uom } = this.state;
    const { baseUomName, baseUomName1, ...otherFields } = fields;
    const item = { ...uom, ...otherFields, baseUomName: baseUomName1 || baseUomName };
    // this.setState({ uom: item });
    dispatch({
      type: 'uomType/addUomTypes',
      payload: item,
    }).then(res => {
      if (!isEmpty(res)) {
        this.hideModal();
        this.handleSearch(pagination);
        notification.success();
      }
    });
  }

  render() {
    const {
      uomType: { uomList, pagination },
      loading,
      saving,
      match,
    } = this.props;
    const { uom, addVisible } = this.state;
    const filterProps = {
      onRef: node => {
        this.formDom = node.props.form;
      },
      onFilterChange: this.handleSearch,
    };
    const listProps = {
      pagination,
      loading,
      match,
      dataSource: uomList,
      editLine: this.editLine,
      onSearch: this.handleSearch,
    };
    return (
      <React.Fragment>
        <Header title={intl.get(`${promptCode}.view.message.title`).d('计量单位类型定义')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '计量单位类型定义-新建',
              },
            ]}
            onClick={this.handleCreateUom}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
        </Content>
        <UomForm
          anchor="right"
          title={
            uom.uomTypeId
              ? intl.get(`${promptCode}.view.message.title.modal.edit`).d('编辑单位类型')
              : intl.get(`${promptCode}.view.message.title.modal.create`).d('新建单位类型')
          }
          onRef={node => {
            this.uomForm = node;
          }}
          data={uom}
          onHandleAdd={this.handleAdd}
          confirmLoading={saving}
          visible={addVisible}
          onCancel={this.hideModal}
        />
      </React.Fragment>
    );
  }
}
