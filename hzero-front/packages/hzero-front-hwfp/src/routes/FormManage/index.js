/**
 * FormManage - 流程设置/表单管理
 * @date: 2018-8-15
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';

import { Header, Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { getCurrentOrganizationId, filterNullValueObject } from 'utils/utils';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import Drawer from './Drawer';

/**
 * 表单管理组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} formManage - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hwfp.formManage', 'hwfp.common'] })
@connect(({ formManage, loading }) => ({
  formManage,
  loading: loading.effects['formManage/fetchFormList'],
  saving: loading.effects['formManage/creatOne'] || loading.effects['formManage/editOne'],
  tenantId: getCurrentOrganizationId(),
}))
export default class FormManage extends Component {
  form;

  /**
   * state初始化
   */
  state = {
    formValues: {},
    tableRecord: {},
    visible: false,
    isCreate: false,
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    this.handleSearch();
    this.fetchCategory();
  }

  /**
   * 获取流程分类
   * @memberof FormManage
   */
  @Bind()
  fetchCategory() {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'formManage/queryCategory',
      payload: { tenantId },
    });
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch, tenantId } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'formManage/fetchFormList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
        tenantId,
      },
    });
  }

  /**
   * 获取表格中的记录
   *
   * @param {*} record
   * @memberof FormManage
   */
  @Bind()
  getRecordData(record) {
    this.setState({
      tableRecord: { ...record },
    });
    this.showEditModal();
  }

  /**
   * 保存表单中的值
   *
   * @param {*} values
   * @memberof FormManage
   */
  @Bind()
  storeFormValues(values) {
    this.setState({
      formValues: { ...values },
    });
  }

  /**
   * 关闭模态框
   *
   * @memberof FormManage
   */
  @Bind()
  handleCancel() {
    this.setState({
      visible: false,
      isCreate: false,
      tableRecord: {},
    });
  }

  /**
   * 打开新增模态框
   *
   * @memberof FormManage
   */
  @Bind()
  showModal() {
    this.setState({
      visible: true,
      isCreate: true,
    });
  }

  /**
   * 打开编辑模态框
   *
   * @memberof FormManage
   */
  @Bind()
  showEditModal() {
    this.setState({
      visible: true,
      isCreate: false,
    });
  }

  /**
   *  新建表单
   *
   * @param {*} values
   * @memberof FormManage
   */
  @Bind()
  handleAdd(values) {
    const {
      dispatch,
      tenantId,
      formManage: { pagination },
    } = this.props;
    dispatch({
      type: 'formManage/creatOne',
      payload: { ...values, tenantId },
    }).then(response => {
      if (response) {
        this.handleCancel();
        this.handleSearch(pagination);
        notification.success();
      }
    });
  }

  /**
   * 编辑表单
   *
   * @param {*} values
   * @memberof FormManage
   */
  @Bind()
  handleEdit(values) {
    const {
      dispatch,
      tenantId,
      formManage: { pagination },
    } = this.props;
    dispatch({
      type: 'formManage/editOne',
      payload: { ...values, tenantId },
    }).then(response => {
      if (response) {
        this.handleCancel();
        this.handleSearch(pagination);
        notification.success();
      }
    });
  }

  /**
   *  删除表单
   *
   * @param {*} values
   * @memberof FormManage
   */
  @Bind()
  handleDelete(values) {
    const {
      dispatch,
      tenantId,
      formManage: { pagination },
    } = this.props;
    dispatch({
      type: 'formManage/deleteOne',
      payload: { ...values, tenantId },
    }).then(response => {
      if (response) {
        this.handleSearch(pagination);
        notification.success();
      }
    });
  }

  /**
   * 条件编码唯一性校验
   * @param {!object} rule - 规则
   * @param {!string} value - 表单值
   * @param {!Function} callback
   */
  @Bind()
  checkUnique(rule, value, callback, codeValue) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'formManage/checkUnique',
      payload: {
        tenantId,
        code: value,
      },
    }).then(res => {
      if (res && res.failed && codeValue) {
        if (res.code === 'error.process.form.definition.code.exist') {
          callback(
            intl.get('hwfp.common.view.validation.code.exist').d('编码已存在，请输入其他编码')
          );
        } else {
          callback(intl.get('hwfp.formManage.view.validation.codeNotValid').d('编码不能为纯数字'));
        }
      }
      callback();
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
      loading,
      saving,
      tenantId,
      formManage: { formManageList = {}, category = [], pagination },
    } = this.props;
    const { formValues = {}, tableRecord = {}, isCreate, visible } = this.state;
    const filterProps = {
      category,
      onSearch: this.handleSearch,
      onStore: this.storeFormValues,
      onRef: this.handleBindRef,
    };
    const listProps = {
      formValues,
      formManageList,
      loading,
      category,
      pagination,
      onChange: this.handleSearch,
      onGetRecord: this.getRecordData,
      onDelete: this.handleDelete,
    };
    const drawerProps = {
      tableRecord,
      visible,
      saving,
      isCreate,
      tenantId,
      category,
      anchor: 'right',
      onCancel: this.handleCancel,
      onAdd: this.handleAdd,
      onEdit: this.handleEdit,
      onCheck: this.checkUnique,
    };
    return (
      <>
        <Header title={intl.get('hwfp.formManage.view.message.title').d('表单管理')}>
          <Button
            type="primary"
            icon="plus"
            onClick={() => {
              this.showModal();
            }}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <ListTable {...listProps} />
          <Drawer {...drawerProps} />
        </Content>
      </>
    );
  }
}
