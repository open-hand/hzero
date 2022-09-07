/**
 * InventoryOrg -库存组织页面
 * @date: 2018-7-5
 * @author dengtingmin <tingmin.deng@hand-china.com>
 * @version: 0.0.3
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Form } from 'hzero-ui';
import { connect } from 'dva';
import { isUndefined, isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  getCurrentOrganizationId,
  getEditTableData,
  delItemToPagination,
  addItemToPagination,
  filterNullValueObject,
} from 'utils/utils';

import FilterForm from './FilterForm';
import ListTable from './ListTable';

export const EditableContext = React.createContext();

@connect(({ inventoryOrg, loading }) => ({
  inventoryOrg,
  fetchInventoryDataLoading: loading.effects['inventoryOrg/fetchInventoryData'],
  updateLoading:
    loading.effects['inventoryOrg/updateAllInventoryData'] ||
    loading.effects['inventoryOrg/fetchInventoryData'],
}))
@formatterCollections({
  code: 'hpfm.inventoryOrg',
})
@Form.create({ fieldNameProp: null })
export default class InventoryOrg extends Component {
  constructor(props) {
    super(props);
    this.state = {
      getOrganizationId: getCurrentOrganizationId(),
    };
  }

  /**
   * componentDidMount 生命周期函数
   * render()执行后获取页面数据
   */
  componentDidMount() {
    this.queryInventory();
  }

  /**
   * 子组件
   * @param {Object} ref
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.setState({ form: (ref.props || {}).form });
  }

  /**
   * 查询库存组织
   * @param {object} params --查询参数
   * @param {?number} params.page --页码
   * @param {?number} params.size --条数
   * @param {?string} params.getOrganizationId --租户ID
   */
  @Bind()
  queryInventory(params = {}) {
    const { dispatch } = this.props;
    const { form, getOrganizationId } = this.state;
    const fieldValues = isUndefined(form) ? {} : filterNullValueObject(form.getFieldsValue());
    dispatch({
      type: 'inventoryOrg/fetchInventoryData',
      payload: {
        body: {
          page: isEmpty(params) ? {} : params,
          ...fieldValues,
        },
        organizationId: getOrganizationId,
      },
    });
  }

  /**
   * 表单为编辑状态
   * @param {?object} record
   * @param {?boolean} flag 编辑/取消编辑
   */
  @Bind()
  handleOrgEdit(record, flag) {
    const {
      dispatch,
      inventoryOrg: {
        fetchInventoryData: { content = {} },
      },
    } = this.props;
    const index = content.findIndex(item => item.organizationId === record.organizationId);
    const updateFlag = flag ? 'update' : '';
    dispatch({
      type: 'inventoryOrg/editData',
      payload: {
        content: [
          ...content.slice(0, index),
          {
            ...record,
            _status: updateFlag,
          },
          ...content.slice(index + 1),
        ],
      },
    });
  }

  // 新建，添加一列子表单
  @Bind()
  handleCreateOrg() {
    const {
      dispatch,
      commonSourceCode,
      commonExternalSystemCode,
      inventoryOrg: { fetchInventoryData = {}, pagination = {} },
    } = this.props;
    const newContent = fetchInventoryData.content;
    dispatch({
      type: 'inventoryOrg/updateStateReducer',
      payload: {
        fetchInventoryData: {
          content: [
            {
              organizationCode: '',
              organizationName: '',
              ouName: '',
              organizationId: uuidv4(),
              sourceCode: commonSourceCode,
              externalSystemCode: commonExternalSystemCode,
              enabledFlag: 1,
              _status: 'create',
            },
            ...newContent,
          ],
        },
        pagination: addItemToPagination(fetchInventoryData.content.length, pagination),
      },
    });
  }

  @Bind()
  handleCleanLine(record = {}) {
    const {
      dispatch,
      inventoryOrg: {
        fetchInventoryData: { content = [] },
        pagination = {},
      },
    } = this.props;
    const newList = content.filter(item => item.organizationId !== record.organizationId);
    dispatch({
      type: 'inventoryOrg/updateStateReducer',
      payload: {
        fetchInventoryData: {
          content: [...newList],
        },
        pagination: delItemToPagination(content.length, pagination),
      },
    });
  }

  /**
   * 新建数据/更新数据
   */
  @Bind()
  handleUpdateOrg() {
    const {
      dispatch,
      inventoryOrg: {
        fetchInventoryData: { content = {} },
        pagination = {},
      },
    } = this.props;
    const { getOrganizationId } = this.state;
    const editData = content.filter(item => item._status);
    const params = getEditTableData(editData, ['organizationId']);
    const paramsList = params.map(item => {
      const copyList = { ...item };
      copyList.tenantId = getOrganizationId;
      if (copyList.ouName === '') {
        copyList.organizationId = '';
      }
      return copyList;
    });
    if (Array.isArray(paramsList) && paramsList.length === 0) {
      return;
    }
    dispatch({
      type: 'inventoryOrg/updateAllInventoryData',
      payload: {
        organizationId: getOrganizationId,
        body: paramsList,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.queryInventory(pagination);
      }
    });
  }

  render() {
    const {
      inventoryOrg: { fetchInventoryData = {}, pagination = {} },
      match,
      fetchInventoryDataLoading,
      updateLoading,
      commonSourceCode,
    } = this.props;
    const { getOrganizationId } = this.state;
    const filterForm = {
      getOrganizationId,
      onHandleBindRef: this.handleBindRef,
      onFetchOrg: this.queryInventory,
    };
    const listTable = {
      commonSourceCode,
      match,
      pagination,
      getOrganizationId,
      fetchInventoryData,
      fetchInventoryDataLoading,
      onFetchInventory: this.queryInventory,
      onHandleOrgEdit: this.handleOrgEdit,
      onHandleCancelOrg: this.handleCleanLine,
      onHandleUpdateOrg: this.handleUpdateOrg,
    };
    const editList = fetchInventoryData.content.filter(item => item._status);
    return (
      <React.Fragment>
        <Header
          title={intl.get('hpfm.inventoryOrg.view.inventoryOrg.headerTitle').d('库存组织定义')}
        >
          <ButtonPermission
            type="primary"
            icon="save"
            onClick={this.handleUpdateOrg}
            loading={updateLoading && !isEmpty(editList)}
            disabled={isEmpty(editList)}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '库存组织-新建',
              },
            ]}
            onClick={this.handleCreateOrg}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content noCard>
          <div className="table-list-search">
            <FilterForm {...filterForm} />
          </div>
          <ListTable {...listTable} />
        </Content>
      </React.Fragment>
    );
  }
}
