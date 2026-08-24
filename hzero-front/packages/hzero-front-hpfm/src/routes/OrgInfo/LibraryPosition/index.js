/**
 * libraryPosition - 库位-组织信息
 * @date: 2018-8-4
 * @author: YB <bo.yang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import uuidv4 from 'uuid/v4';
import { isEmpty, isUndefined } from 'lodash';
import { Bind, Debounce } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { DEBOUNCE_TIME } from 'utils/constants';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import {
  getCurrentOrganizationId,
  getEditTableData,
  addItemToPagination,
  delItemToPagination,
  filterNullValueObject,
} from 'utils/utils';

import FilterForm from './FilterForm';
import DataList from './DataList';

const viewPrompt = 'hpfm.libraryPosition.view.message';
/**
 * 库位--组织信息
 * @extends {Component} - PureComponent
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} libraryPosition - 数据源
 * @reactProps {boolean} loading - 数据加载是否完成
 * @reactProps {boolean} saving - 保存操作是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {String} organizationId - 租户Id
 * @reactProps {Function} [dispatch= e => e] - redux dispatch方法
 * @return React.element
 */

@connect(({ libraryPosition, loading }) => ({
  libraryPosition,
  loading: loading.effects['libraryPosition/fetchLibraryPosition'],
  saving: loading.effects['libraryPosition/saveLibraryPosition'],
  organizationId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: 'hpfm.libraryPosition' })
export default class LibraryPosition extends PureComponent {
  /**
   * state初始化
   * @param {object} props - 组件Props
   */
  constructor(props) {
    super(props);
    this.state = {};
  }

  /**
   * 生命周期函数
   * 获取render渲染的数据
   */
  componentDidMount() {
    this.handleSearchLibrary();
  }

  /**
   *按条件查询数据
   * @prop {object} payload - 请求参数
   * @memberof LibraryPosition
   */
  @Bind()
  handleSearchLibrary(payload = {}) {
    const { dispatch, organizationId } = this.props;
    const { form } = this.state;
    const filterValues = isUndefined(form) ? {} : filterNullValueObject(form.getFieldsValue());
    dispatch({
      type: 'libraryPosition/fetchLibraryPosition',
      payload: {
        organizationId,
        page: isEmpty(payload) ? {} : payload,
        ...filterValues,
      },
    });
  }

  /**
   *
   * @param {object} ref - FilterForm子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.setState({ form: ref.props.form });
  }

  /**
   *保存编辑或者新建的数据
   *
   * @memberof LibraryPosition
   */
  @Bind()
  handleSaveOption() {
    const {
      dispatch,
      organizationId,
      libraryPosition: { libraryList = [], pagination = {} },
    } = this.props;
    const payloadData = getEditTableData(libraryList, ['locationId']);
    if (isEmpty(payloadData)) return;

    dispatch({
      type: 'libraryPosition/saveLibraryPosition',
      payload: { payloadData, organizationId },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleSearchLibrary(pagination);
      }
    });
  }

  /**
   *新建行
   * @memberof LibraryPosition
   */
  @Bind()
  @Debounce(DEBOUNCE_TIME)
  handleCreateOption() {
    const {
      dispatch,
      organizationId,
      commonSourceCode,
      commonExternalSystemCode,
      libraryPosition: { libraryList = [], pagination = {} },
    } = this.props;
    dispatch({
      type: 'libraryPosition/updateState',
      payload: {
        libraryList: [
          {
            locationId: uuidv4(),
            locationCode: '',
            locationName: '',
            invOrganizationName: '',
            ouName: '',
            sourceCode: commonSourceCode,
            externalSystemCode: commonExternalSystemCode,
            enabledFlag: 1,
            tenantId: organizationId,
            _status: 'create', // 新建标记位
          },
          ...libraryList,
        ],
        pagination: addItemToPagination(libraryList.length, pagination),
      },
    });
  }

  /**
   *批量编辑行
   * @param {object} record 每行数据
   * @memberof LibraryPosition
   */
  @Bind()
  handleEditRow(record) {
    const {
      libraryPosition: { libraryList },
      dispatch,
    } = this.props;
    const newLibraryList = libraryList.map(item => {
      if (record.locationId === item.locationId) {
        if (item.sourceCode === 'ERP') {
          return { ...item, isErp: true, _status: 'update' };
        }
        return { ...item, _status: 'update' };
      }
      return item;
    });
    dispatch({
      type: 'libraryPosition/updateState',
      payload: { libraryList: newLibraryList },
    });
  }

  /**
   *取消编辑行
   * @param {object} record 行数据
   * @memberof LibraryPosition
   */
  @Bind()
  handleCancelRow(record) {
    const {
      dispatch,
      libraryPosition: { libraryList },
    } = this.props;
    const newLibraryList = libraryList.map(item => {
      if (item.locationId === record.locationId) {
        const { _status, ...other } = item;
        return other;
      }
      return item;
    });
    dispatch({
      type: 'libraryPosition/updateState',
      payload: { libraryList: newLibraryList },
    });
  }

  /**
   *删除新建的行
   * @param {object} record
   * @memberof LibraryPosition
   */
  @Bind()
  handleDeleteRow(record) {
    const {
      dispatch,
      libraryPosition: { libraryList = [], pagination = {} },
    } = this.props;
    const newLibraryList = libraryList.filter(item => item.locationId !== record.locationId);
    dispatch({
      type: 'libraryPosition/updateState',
      payload: {
        libraryList: newLibraryList,
        pagination: delItemToPagination(libraryList.length, pagination),
      },
    });
  }

  render() {
    const {
      loading,
      saving,
      organizationId,
      commonSourceCode,
      match,
      libraryPosition: { libraryList = [], pagination = {} },
    } = this.props;
    const isSaveList = libraryList.filter(
      item => item._status === 'create' || item._status === 'update'
    );
    const filterProps = {
      organizationId,
      onSearch: this.handleSearchLibrary,
      onRef: this.handleBindRef,
    };
    const listProps = {
      commonSourceCode,
      loading,
      pagination,
      organizationId,
      match,
      dataSource: libraryList,
      onSearch: this.handleSearchLibrary,
      onEditRow: this.handleEditRow,
      onDelete: this.handleDeleteRow,
      onCancel: this.handleCancelRow,
    };
    return (
      <React.Fragment>
        <Header title={intl.get(`${viewPrompt}.title`).d('库位')}>
          <ButtonPermission
            type="primary"
            icon="save"
            disabled={isEmpty(isSaveList)}
            loading={(saving || loading) && !isEmpty(isSaveList)}
            onClick={this.handleSaveOption}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          <ButtonPermission
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '库位-新建',
              },
            ]}
            onClick={this.handleCreateOption}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content noCard>
          <div className="table-list-search">
            <FilterForm {...filterProps} />
          </div>
          <DataList {...listProps} />
        </Content>
      </React.Fragment>
    );
  }
}
