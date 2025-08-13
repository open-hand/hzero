/**
 * 系统管理 - 模板维护
 * @date: 2019-6-26
 * @author: XL <liang.xiong@hand-china.com>
 * @version: 0.11.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { filterNullValueObject, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

import QueryForm from './QueryForm';
import ListTable from './ListTable';
import Drawer from './Drawer';

/**
 * 门户模板定义数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} formValues - 查询表单值
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {Boolean} modalVisible - 模态框是否可见
 * @return React.element
 */
@connect(({ hpfmTemplate, loading }) => ({
  hpfmTemplate,
  loading: loading.effects['hpfmTemplate/fetchTemplates'],
  saving:
    loading.effects['hpfmTemplate/createTemplate'] || loading.effects['hpfmTemplate/editTemplate'],
  organizationId: getCurrentOrganizationId(),
  isTenant: isTenantRoleLevel(),
}))
@formatterCollections({ code: ['hpfm.hpfmTemplate', 'entity.tenant'] })
export default class Templates extends Component {
  form;

  state = {
    formValues: {},
    tableRecord: {},
    fileList: [], // 编辑时模板缩略图
    modalVisible: false,
    isCreate: true,
  };

  // 初始化
  componentDidMount() {
    this.fetchTableList();
    this.fetchEnum();
  }

  /**
   * 获取值集
   */
  fetchEnum() {
    const { dispatch } = this.props;
    dispatch({
      type: 'hpfmTemplate/fetchEnum',
    });
  }

  // 获取表格数据
  @Bind()
  fetchTableList(fields = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'hpfmTemplate/fetchTemplates',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  // 保存表单中的值
  @Bind()
  storeFormValues(values) {
    this.setState({
      formValues: { ...values },
    });
  }

  // 获取表格中的记录
  @Bind()
  getRecordData(record = {}) {
    this.setState({
      tableRecord: { ...record },
      fileList: [
        {
          uid: '-1',
          name: record.imageName,
          status: 'done',
          url: record.templateAvatar,
        },
      ],
    });
    this.showEditModal();
  }

  // 关闭模态框
  @Bind()
  handleCancel() {
    this.setState({
      modalVisible: false,
      isCreate: true,
      tableRecord: {},
      fileList: [],
    });
  }

  // 打开新增模态框
  @Bind()
  showModal() {
    this.setState({
      modalVisible: true,
      isCreate: true,
    });
  }

  // 打开编辑模态框
  @Bind()
  showEditModal() {
    this.setState({
      modalVisible: true,
      isCreate: false,
    });
  }

  // 新建模板维护
  @Bind()
  handleAdd(values) {
    const {
      dispatch,
      hpfmTemplate: { pagination },
    } = this.props;
    dispatch({
      type: 'hpfmTemplate/createTemplate',
      payload: values,
    }).then((response) => {
      if (response) {
        this.handleCancel();
        this.fetchTableList(pagination);
        notification.success();
      }
    });
  }

  // 编辑模板维护
  @Bind()
  handleEdit(values) {
    const {
      dispatch,
      hpfmTemplate: { pagination },
    } = this.props;
    dispatch({
      type: 'hpfmTemplate/editTemplate',
      payload: values,
    }).then((response) => {
      if (response) {
        this.handleCancel();
        this.fetchTableList(pagination);
        notification.success();
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

  render() {
    const { modalVisible, formValues, tableRecord, isCreate, fileList = [] } = this.state;
    const {
      saving,
      loading,
      organizationId,
      isTenant,
      match,
      hpfmTemplate: {
        templateData,
        pagination,
        dataTenantLevel, // 租户数据层级 值集
      },
    } = this.props;
    const formProps = {
      onSearch: this.fetchTableList,
      onStore: this.storeFormValues,
      onRef: this.handleBindRef,
      isTenantRoleLevel: isTenant,
      dataTenantLevel,
    };
    const tableProps = {
      match,
      templateData,
      formValues,
      loading,
      pagination,
      organizationId,
      isTenantRoleLevel: isTenant,
      onChange: this.fetchTableList,
      onGetRecord: this.getRecordData,
    };
    const drawerProps = {
      dataTenantLevel,
      tableRecord,
      modalVisible,
      saving,
      isCreate,
      fileList,
      anchor: 'right',
      isTenantRoleLevel: isTenant,
      onCancel: this.handleCancel,
      onAdd: this.handleAdd,
      onEdit: this.handleEdit,
    };
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.hpfmTemplate.view.message.title.template').d('内容模板')}>
          <ButtonPermission
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '模板维护-新建',
              },
            ]}
            onClick={this.showModal}
            icon="plus"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <QueryForm {...formProps} />
          </div>
          <ListTable {...tableProps} />
        </Content>
        <Drawer {...drawerProps} />
      </React.Fragment>
    );
  }
}
