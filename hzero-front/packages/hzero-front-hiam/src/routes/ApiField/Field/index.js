/**
 * 接口字段维护-详情/接口id
 * /hiam/api-field/:permissionId
 * Field
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-10
 * @copyright 2019-07-10 © HAND
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';

import SearchForm from './SearchForm';
import DataTable from './DataTable';
import EditFormModal from './EditFormModal';

function getFieldsValueByWrappedComponentRef(ref) {
  if (ref.current) {
    const { form } = ref.current.props;
    return form.getFieldsValue();
  }
  return {};
}

@connect(
  mapStateToProps,
  mapDispatchToProps
)
export default class Field extends Component {
  constructor(props) {
    super(props);
    this.state = {
      cachePagination: {}, // 编辑后 需要之前的分页信息
      editFormModalVisible: false, // 编辑模态框显示
      isCreate: true, // 编辑表单是否是新建
      editRecord: {}, // 编辑的记录
    };
    this.searchFormRef = React.createRef();
  }

  componentDidMount() {
    const { fieldInit } = this.props;
    fieldInit();
    this.reload();
  }

  componentWillUnmount() {
    const { updateState } = this.props;
    updateState({
      fieldDataSource: [],
      fieldPagination: {},
    });
  }

  reload() {
    const { cachePagination } = this.state;
    this.handleSearch(cachePagination);
  }

  handleSearch(pagination = {}) {
    const {
      queryFields,
      match: {
        params: { permissionId },
      },
    } = this.props;
    this.setState({
      cachePagination: pagination,
    });
    const params = getFieldsValueByWrappedComponentRef(this.searchFormRef);
    queryFields({
      permissionId,
      params: {
        ...params,
        ...pagination,
      },
    });
  }

  // Header Button
  @Bind()
  handleCreateBtnClick() {
    this.setState({
      editFormModalVisible: true,
      isCreate: true,
    });
  }

  // SearchForm
  @Bind()
  handleSearchFormSearch() {
    this.handleSearch();
  }

  // DataTable
  @Bind()
  handleDataTableChange(page, filter, sort) {
    this.handleSearch({ page, sort });
  }

  @Bind()
  handleRecordRemove(record) {
    const {
      removeField,
      match: {
        params: { permissionId },
      },
    } = this.props;
    removeField({
      permissionId,
      record,
    }).then(res => {
      if (res) {
        notification.success();
        this.reload();
      }
    });
  }

  @Bind()
  handleRecordEdit(record) {
    this.setState({
      editFormModalVisible: true,
      isCreate: false,
      editRecord: record,
    });
  }

  // EditFormModal

  closeEditFormModal(callback) {
    this.setState(
      {
        editFormModalVisible: false,
        isCreate: true,
        editRecord: {},
      },
      callback
    );
  }

  @Bind()
  handleEditFormModalCancel() {
    this.closeEditFormModal();
  }

  @Bind()
  handleEditFormModalOk(data) {
    const { isCreate } = this.state;
    if (isCreate) {
      const {
        createField,
        match: {
          params: { permissionId },
        },
      } = this.props;
      createField({
        permissionId,
        record: data,
      }).then(res => {
        if (res) {
          notification.success();
          this.closeEditFormModal(() => {
            this.reload();
          });
        }
      });
    } else {
      const {
        updateField,
        match: {
          params: { permissionId },
        },
      } = this.props;
      const { editRecord = {} } = this.state;
      updateField({
        permissionId,
        record: {
          ...editRecord,
          ...data,
        },
      }).then(res => {
        if (res) {
          notification.success();
          this.closeEditFormModal(() => {
            this.reload();
          });
        }
      });
    }
  }

  render() {
    const {
      dataSource,
      pagination,
      fieldType,
      loading,
      saveLoading,
      removeLoading,
      match: { path },
    } = this.props;
    const { isCreate, editFormModalVisible, editRecord } = this.state;
    return (
      <>
        <Header
          title={intl.get('hiam.apiField.view.title').d('接口字段配置')}
          backPath="/hiam/api-field"
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '接口字段维护详情-新建',
              },
            ]}
            type="primary"
            onClick={this.handleCreateBtnClick}
            icon="plus"
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <SearchForm
            onSearch={this.handleSearchFormSearch}
            fieldType={fieldType}
            wrappedComponentRef={this.searchFormRef}
          />
          <DataTable
            dataSource={dataSource}
            pagination={pagination}
            onChange={this.handleDataTableChange}
            onRecordDelete={this.handleRecordRemove}
            onRecordEdit={this.handleRecordEdit}
            loading={loading}
            removeLoading={removeLoading}
            path={path}
          />
          <EditFormModal
            visible={editFormModalVisible}
            isCreate={isCreate}
            fieldType={fieldType}
            onCancel={this.handleEditFormModalCancel}
            onOk={this.handleEditFormModalOk}
            record={editRecord}
            loading={saveLoading}
          />
        </Content>
      </>
    );
  }
}

function mapStateToProps({ hiamApiField, loading }) {
  const { fieldDataSource, fieldPagination, fieldType } = hiamApiField;
  return {
    fieldType,
    dataSource: fieldDataSource,
    pagination: fieldPagination,
    loading:
      loading.effects['hiamApiField/fieldInit'] || loading.effects['hiamApiField/queryFields'],
    saveLoading:
      loading.effects['hiamApiField/updateField'] || loading.effects['hiamApiField/createField'],
    removeLoading: loading.effects['hiamApiField/removeField'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    fieldInit(payload) {
      return dispatch({
        type: 'hiamApiField/fieldInit',
        payload,
      });
    },
    queryFields(payload) {
      return dispatch({
        type: 'hiamApiField/queryFields',
        payload,
      });
    },
    updateField(payload) {
      return dispatch({
        type: 'hiamApiField/updateField',
        payload,
      });
    },
    createField(payload) {
      return dispatch({
        type: 'hiamApiField/createField',
        payload,
      });
    },
    removeField(payload) {
      return dispatch({
        type: 'hiamApiField/removeField',
        payload,
      });
    },
    updateState(payload) {
      return dispatch({
        type: 'hiamApiField/updateState',
        payload,
      });
    },
  };
}
