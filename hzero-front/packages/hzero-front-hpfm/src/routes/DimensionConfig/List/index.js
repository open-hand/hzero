/**
 * DimensionConfig - 数据维度配置
 * @date: 2019-7-16
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { connect } from 'dva';
import { isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';
import CustomTable from 'components/CustomTable';
import { Button as ButtonPermission } from 'components/Permission';

import {
  filterNullValueObject,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  tableScrollWidth,
} from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import CreateForm from './Drawer';
import SearchForm from './SearchForm';

@connect(({ dimensionConfig, loading }) => ({
  dimensionConfig,
  list: dimensionConfig.list,
  loading: loading.effects['dimensionConfig/queryLovHeadersList'],
  saving: loading.effects['dimensionConfig/saveLovHeaders'],
  copyLoading: loading.effects['dimensionConfig/copyLov'],
  tenantId: getCurrentOrganizationId(),
  isSiteFlag: !isTenantRoleLevel(),
}))
@formatterCollections({
  code: ['hpfm.valueList', 'hpfm.common', 'hpfm.dimensionConfig', 'hpfm.dataSourceDriver'],
})
export default class DimensionConfig extends React.Component {
  createForm;

  // 侧边栏内部引用
  constructor(props) {
    super(props);
    this.pageConfig = this.pageConfig();
    this.filterForm = {};
    this.state = {
      modalVisible: false,
    };
    this.customConstructor(props); // 初始化自定义数据
  }

  componentDidMount() {
    const {
      pageConfig: { modelName },
    } = this;

    const {
      [modelName]: searchPageData = {},
      location: { state: { _back } = {} },
    } = this.props;
    const { pagination } = searchPageData;
    this.handleSearch(isUndefined(_back) ? {} : pagination);
  }

  /**
   * 列表查询
   * @param {Object} pagination 查询参数
   */
  @Bind()
  handleSearch(pagination = {}) {
    const { pageConfig: { searchDispatch, paramsFilter = e => e, otherParams = {} } = {} } = this;
    const { dispatch = e => e } = this.props;
    const form = this.filterForm.props && this.filterForm.props.form;
    const params = isUndefined(form) ? {} : form.getFieldsValue();
    const filterValues = filterNullValueObject({
      ...params,
      ...paramsFilter(params),
    });
    dispatch({
      type: searchDispatch,
      payload: {
        ...otherParams,
        page: pagination,
        ...filterValues,
        lovTypeCode: 'IDP',
      },
    });
  }

  /**
   *
   * @param {object} ref - FilterForm子组件对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.filterForm = ref;
  }

  /**
   * 自定义初始化数据
   */
  // eslint-disable-next-line
  customConstructor(props) {
    return {};
  }

  /**
   * Conent 属性, 子类覆盖
   */
  contentProps() {
    return null;
  }

  @Bind()
  pageConfig() {
    const { tenantId } = this.props;
    return {
      modelName: 'dimensionConfig',
      customSearch: true,
      cacheKey: '/hpfm/value-list/list',
      searchDispatch: 'dimensionConfig/queryLovHeadersList',
      otherParams: { tenantId: isTenantRoleLevel() ? tenantId : '' },
    };
  }

  /**
   * 显示侧边栏
   */
  @Bind()
  showModal() {
    this.handleModalVisible(true);
  }

  /**
   * 隐藏侧边栏
   */
  @Bind()
  hideModal() {
    const { saving = false } = this.props;
    if (!saving) {
      this.handleModalVisible(false);
    }
  }

  /**
   * 侧边栏显示控制函数
   * @param {Boolean} flag - 显示隐藏参数
   */
  handleModalVisible(flag) {
    if (flag === false && this.createForm) {
      this.createForm.resetForm();
    }
    this.setState({
      modalVisible: !!flag,
    });
  }

  /**
   * 新建数据维度
   * @param {object} fieldsValue - 表单值
   */
  @Bind()
  handleAdd(fieldsValue) {
    const {
      history,
      dispatch,
      tenantId,
      match,
      location: { search },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const { parentTenantId } = this.state;
    // TODO: 校验表单
    dispatch({
      type: 'dimensionConfig/saveLovHeaders',
      payload: {
        tenantId,
        ...fieldsValue,
        enabledFlag: 1,
        mustPageFlag: 1,
        parentTenantId,
      },
    }).then(response => {
      if (response) {
        this.hideModal();
        notification.success();
        history.push({
          pathname:
            match.path.indexOf('/private') === 0
              ? `/private/hpfm/data-dimension-config/detail/${response.lovId}`
              : `/hpfm/data-dimension-config/detail/${response.lovId}`,
          search: match.path.indexOf('/private') === 0 ? `?access_token=${accessToken}` : '',
        });
      }
    });
  }

  /**
   * 父级维度切换
   * @param {*} record - lov选择行数据
   */
  @Bind()
  handleParentLovChange(record) {
    this.setState({
      parentTenantId: record.tenantId,
    });
  }

  tableProps() {
    const {
      history,
      loading,
      match,
      location: { search },
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const columns = [
      !isTenantRoleLevel() && {
        title: intl.get('hpfm.valueList.model.header.tenantName').d('所属租户'),
        width: 200,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hpfm.dimensionConfig.model.header.dimensionCode').d('维度代码'),
        width: 200,
        dataIndex: 'lovCode',
      },
      {
        title: intl.get('hpfm.dimensionConfig.model.header.dimensionName').d('维度名称'),
        width: 200,
        dataIndex: 'lovName',
      },
      {
        title: intl.get('hpfm.valueList.model.header.description').d('描述'),
        dataIndex: 'description',
        width: 200,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 100,
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '数据维度配置-编辑',
                    },
                  ]}
                  onClick={() => {
                    history.push(
                      match.path.indexOf('/private') === 0
                        ? `/private/hpfm/data-dimension-config/detail/${record.lovId}?access_token=${accessToken}`
                        : `/hpfm/data-dimension-config/detail/${record.lovId}`
                    );
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);
    return {
      rowKey: 'lovId',
      columns,
      rowSelection: null,
      loading,
      scroll: { x: tableScrollWidth(columns) },
    };
  }

  render() {
    const { dimensionConfig: { list = {}, pagination = {} } = {}, saving, match } = this.props; // 根据 modelName 获取 Model 数据
    const customTableProps = this.tableProps();
    const columns = [];
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const tableProps = {
      bordered: true,
      rowKey: 'lovId',
      dataSource: list.content,
      pagination,
      columns,
      onChange: this.handleSearch,
      ...customTableProps,
    };

    return (
      <>
        <Header title={intl.get('hpfm.dimensionConfig.view.title.header').d('数据维度配置')}>
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '数据维度配置-新建',
              },
            ]}
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content {...this.contentProps()}>
          <div className="table-list-search">
            <SearchForm {...filterProps} />
          </div>
          <CustomTable {...tableProps} />
        </Content>
        <CreateForm
          confirmLoading={saving}
          modalVisible={this.state.modalVisible}
          onSave={this.handleAdd}
          onCancel={this.hideModal}
          onParentLovChange={this.handleParentLovChange}
          onRef={ref => {
            this.createForm = ref;
          }}
        />
      </>
    );
  }
}
