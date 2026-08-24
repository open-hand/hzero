/**
 * index.js - 业务实体定义
 * @date: 2018-10-26
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import React, { PureComponent, Fragment } from 'react';
import { connect } from 'dva';
import uuidv4 from 'uuid/v4';

import { Button as ButtonPermission } from 'components/Permission';
import { Header, Content } from 'components/Page';

import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  getCurrentOrganizationId,
  getEditTableData,
  addItemToPagination,
  delItemToPagination,
} from 'utils/utils';

import ListTable from './ListTable';
import FilterForm from './FilterForm';

@connect(({ loading, operationUnit }) => ({
  operationUnit,
  loading: loading.effects['operationUnit/queryOperationUnit'],
  saving: loading.effects['operationUnit/saveOperationUnit'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({
  code: ['hpfm.operationUnit', 'entity.company'],
})
export default class OperationUnit extends PureComponent {
  constructor(props) {
    super(props);
    this.filterForm = {}; // 获取查询表单对象
    this.rowKey = 'ouId';
    this.queryPageSize = 10;
  }

  componentDidMount() {
    const {
      operationUnit: { pagination },
    } = this.props;
    this.handleSearch(pagination);
  }

  componentWillUnmount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'operationUnit/updateState',
      payload: {
        pagination: {},
        list: {},
      },
    });
  }

  /**
   * 查询业务实体列表
   * @param {Object} params - 查询条件及分页参数对象
   */
  @Bind()
  handleSearch(page = {}) {
    const { dispatch, tenantId } = this.props;
    const values = this.filterForm.props && this.filterForm.props.form.getFieldsValue();
    dispatch({
      type: 'operationUnit/queryOperationUnit',
      payload: {
        tenantId,
        page,
        ...values,
      },
    });
  }

  /**
   * 编辑行
   * @param {Obj} record
   */
  @Bind()
  handleEdit(record) {
    const {
      operationUnit: { list = {} },
      dispatch,
    } = this.props;
    const index = list.content.findIndex(item => item[this.rowKey] === record[this.rowKey]);
    const newList = {
      ...list,
      content: [
        ...list.content.slice(0, index),
        {
          ...record,
          _status: 'update',
        },
        ...list.content.slice(index + 1),
      ],
    };

    dispatch({
      type: 'operationUnit/updateState',
      payload: {
        list: newList,
      },
    });
  }

  /**
   * 新建行
   */
  @Bind()
  handleCreate() {
    const {
      dispatch,
      commonSourceCode,
      commonExternalSystemCode,
      operationUnit: { list = {}, pagination = {} },
    } = this.props;
    const newLine = {
      isCreate: true,
      enabledFlag: 1,
      ouCode: '',
      ouName: '',
      ouId: uuidv4(),
      sourceCode: commonSourceCode,
      externalSystemCode: commonExternalSystemCode,
      _status: 'create',
    };
    dispatch({
      type: 'operationUnit/updateState',
      payload: {
        list: {
          ...list,
          content: [newLine, ...list.content],
        },
        pagination: addItemToPagination(list.content.length, pagination),
      },
    });
  }

  /**
   * 保存，校验成功保存新增行和修改行
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      operationUnit: { list = {}, pagination = {} },
      tenantId,
    } = this.props;
    const { content } = list;
    const params = getEditTableData(content, [this.rowKey]);
    if (Array.isArray(params) && params.length === 0) {
      return;
    }
    dispatch({
      type: 'operationUnit/saveOperationUnit',
      payload: {
        tenantId,
        list: params,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 取消编辑行
   * @param {Obj} record
   * @memberof StoreRoom
   */
  @Bind()
  handleCancel(record) {
    const {
      operationUnit: { list = {} },
      dispatch,
    } = this.props;
    const index = list.content.findIndex(item => item[this.rowKey] === record[this.rowKey]);
    const { _status, ...other } = record;
    const newList = {
      ...list,
      content: [...list.content.slice(0, index), other, ...list.content.slice(index + 1)],
    };

    dispatch({
      type: 'operationUnit/updateState',
      payload: {
        list: newList,
      },
    });
  }

  /**
   * 删除新建行
   * @param {*} record
   */
  @Bind()
  handleDelete(record) {
    const {
      operationUnit: { list = {}, pagination = {} },
      dispatch,
    } = this.props;
    dispatch({
      type: 'operationUnit/updateState',
      payload: {
        list: {
          ...list,
          content: list.content.filter(item => item[this.rowKey] !== record[this.rowKey]),
        },
        pagination: delItemToPagination(list.content.length, pagination),
      },
    });
  }

  render() {
    const {
      form,
      loading,
      saving,
      tenantId,
      commonSourceCode,
      match,
      operationUnit: {
        list: { content = [] },
        pagination = {},
      },
    } = this.props;
    const hasEdit = content.findIndex(item => !!item._status) !== -1;
    const listProps = {
      commonSourceCode,
      loading,
      pagination,
      form,
      tenantId,
      match,
      rowKey: this.rowKey,
      dataSource: content,
      onEdit: this.handleEdit,
      onDelete: this.handleDelete,
      onCancel: this.handleCancel,
      onSearch: this.handleSearch,
    };

    return (
      <Fragment>
        <Header title={intl.get('hpfm.operationUnit.view.title.operationUnit').d('业务实体')}>
          <ButtonPermission
            type="primary"
            icon="save"
            onClick={this.handleSave}
            loading={(saving || loading) && hasEdit}
            disabled={!hasEdit}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '业务实体-新建',
              },
            ]}
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content noCard>
          <div className="table-list-search">
            <FilterForm
              onRef={ref => {
                this.filterForm = ref;
              }}
              onSearch={this.handleSearch}
            />
          </div>
          <ListTable {...listProps} />
        </Content>
      </Fragment>
    );
  }
}
