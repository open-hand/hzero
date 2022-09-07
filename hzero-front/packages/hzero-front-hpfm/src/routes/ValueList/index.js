/**
 * index.js - 值集定义
 * @date: 2018-10-26
 * @author: geekrainy <chao.zheng02@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import React from 'react';
import { connect } from 'dva';
import { Tag, Badge, Popconfirm } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';

import { Content, Header } from 'components/Page';
import CustomTable from 'components/CustomTable';
import { Button as ButtonPermission } from 'components/Permission';
import { openTab } from 'utils/menuTab';

import {
  filterNullValueObject,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  tableScrollWidth,
} from 'utils/utils';
import { enableRender, operatorRender, yesOrNoRender } from 'utils/renderer';
import intl from 'utils/intl';
import { VERSION_IS_OP, HZERO_PLATFORM } from 'utils/config';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import CreateForm from './ListForm';

import SearchForm from './SearchForm';
import CopyValue from './CopyValue';

@connect(({ valueList, loading }) => ({
  valueList,
  list: valueList.list,
  loading: loading.effects['valueList/queryLovHeadersList'],
  saving: loading.effects['valueList/saveLovHeaders'],
  copyLoading: loading.effects['valueList/copyLov'],
  tenantId: getCurrentOrganizationId(),
  isSiteFlag: !isTenantRoleLevel(),
}))
@formatterCollections({
  code: ['hpfm.valueList', 'hpfm.common'],
})
export default class ValueList extends React.Component {
  createForm;

  // 侧边栏内部引用
  constructor(props) {
    super(props);
    this.pageConfig = this.pageConfig();
    this.filterForm = {};
    this.state = {
      selectedRows: [],
      copyValueVisible: false,
      copyValueData: {}, // 复制选择的值集
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
    const {
      pageConfig: {
        searchDispatch,
        searchCallback = (e) => e,
        paramsFilter = (e) => e,
        otherParams = {},
      } = {},
    } = this;
    const { dispatch = (e) => e } = this.props;
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
      },
    }).then((res) => {
      searchCallback(res);
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

  @Bind()
  handleRowSelectChange(_, selectedRows) {
    this.setState({ selectedRows });
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
  handleLovCopy(record) {
    const { dispatch, valueList: { pagination = {} } = {}, isSiteFlag, tenantId } = this.props;
    if (isSiteFlag) {
      this.setState({ copyValueVisible: true, copyValueData: record });
      return;
    }
    dispatch({
      type: 'valueList/copyLov',
      payload: { lovCode: record.lovCode, lovId: record.lovId, tenantId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  @Bind()
  handleCopyValue(data) {
    const { dispatch, valueList: { pagination = {} } = {} } = this.props;
    const {
      copyValueData: { lovCode, lovId },
    } = this.state;
    dispatch({
      type: 'valueList/copyLov',
      payload: { ...data, lovCode, lovId },
    }).then((res) => {
      if (res) {
        this.setState({ copyValueVisible: false, copyValueData: {} });
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  @Bind()
  hideCopyValue() {
    this.setState({ copyValueVisible: false });
  }

  render() {
    const { selectedRows = [], copyValueVisible = false } = this.state;
    const {
      copyLoading = false,
      valueList: { lovType = [], list = {}, pagination = {} } = {},
    } = this.props; // 根据 modelName 获取 Model 数据
    const customTableProps = this.tableProps();
    const columns = [];
    const rowSelection = {
      selectedRowKeys: selectedRows.map((n) => n.lovId),
      onChange: this.handleRowSelectChange,
    };
    const filterProps = {
      lovType,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const tableProps = {
      bordered: true,
      rowKey: 'lovId',
      dataSource: list.content,
      pagination,
      rowSelection,
      columns,
      onChange: this.handleSearch,
      ...customTableProps,
    };

    const copyValueProps = {
      visible: copyValueVisible,
      loading: copyLoading,
      onOk: this.handleCopyValue,
      onCancel: this.hideCopyValue,
    };

    return (
      <React.Fragment>
        {this.renderHeader()}
        <Content {...this.contentProps()}>
          <div className="table-list-search">
            <SearchForm {...filterProps} />
          </div>
          <CustomTable {...tableProps} />
          {copyValueVisible && <CopyValue {...copyValueProps} />}
        </Content>
        {this.renderOther()}
      </React.Fragment>
    );
  }

  @Bind()
  pageConfig() {
    const { tenantId } = this.props;
    return {
      modelName: 'valueList',
      customSearch: true,
      cacheKey: '/hpfm/value-list/list',
      searchDispatch: 'valueList/queryLovHeadersList',
      searchCallback: this.searchCallback,
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
   * 搜索回调
   */
  @Bind()
  searchCallback() {
    this.setState({
      selectedRows: [],
    });
  }

  /**
   * 新建值集
   */
  @Bind()
  handleAdd(fieldsValue) {
    const { history, dispatch, tenantId } = this.props;
    const { parentTenantId } = this.state;
    // TODO: 校验表单
    dispatch({
      type: 'valueList/saveLovHeaders',
      payload: {
        tenantId,
        ...fieldsValue,
        enabledFlag: 1,
        mustPageFlag: 1,
        parentTenantId,
      },
    }).then((response) => {
      if (response) {
        this.hideModal();
        notification.success();
        history.push(`/hpfm/value-list/detail/${response.lovId}`);
      }
    });
  }

  /**
   * 删除值集
   */
  @Bind()
  handleDelete(record) {
    const {
      dispatch,
      valueList: { pagination },
    } = this.props;
    dispatch({
      type: 'valueList/deleteLovHeaders',
      payload: {
        ...record,
      },
    }).then((response) => {
      if (response) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  @Bind()
  handleParentLovChange(record) {
    this.setState({
      parentTenantId: record.tenantId,
    });
  }

  /**
   * 导入返回消息
   */
  @Bind()
  handleImport() {
    openTab({
      key: `/hpfm/value-list/import-data/HPFM.LOV`,
      title: 'hzero.common.button.import',
      search: queryString.stringify({
        action: 'hzero.common.button.import',
        prefixPatch: HZERO_PLATFORM,
      }),
    });
  }

  /**
   * 导入返回消息
   */
  @Bind()
  handleValueImport() {
    openTab({
      key: `/hpfm/value-list/import-data/HPFM.LOV_VALUE`,
      title: 'hzero.common.button.import',
      search: queryString.stringify({
        action: 'hzero.common.button.import',
        prefixPatch: HZERO_PLATFORM,
      }),
    });
  }

  /**
   * 设置单元格属性
   */
  @Bind()
  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 300,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: (e) => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  renderOther() {
    const {
      saving,
      valueList: { lovType, lovTypeFilter, requestMethods = [] },
    } = this.props;
    return (
      <CreateForm
        title={intl.get('hpfm.valueList.view.title.create').d('新增值集')}
        onRef={(ref) => {
          this.createForm = ref;
        }}
        requestMethods={requestMethods}
        handleAdd={this.handleAdd}
        confirmLoading={saving}
        modalVisible={this.state.modalVisible}
        hideModal={this.hideModal}
        width={520}
        lovType={isTenantRoleLevel() && !VERSION_IS_OP ? lovTypeFilter : lovType}
        onParentLovChange={this.handleParentLovChange}
      />
    );
  }

  renderHeader() {
    const { selectedRows = [] } = this.state;
    const { match, isSiteFlag } = this.props;
    return (
      <Header title={intl.get('hpfm.valueList.view.message.title.valueList').d('值集配置')}>
        <ButtonPermission
          icon="plus"
          type="primary"
          permissionList={[
            {
              code: `${match.path}.button.create`,
              type: 'button',
              meaning: '值集定义-新建',
            },
          ]}
          onClick={this.showModal}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
        {!isEmpty(selectedRows) && (
          <ButtonPermission
            icon="minus"
            permissionList={[
              {
                code: `${match.path}.button.delete`,
                type: 'button',
                meaning: '值集定义-删除',
              },
            ]}
            onClick={this.handleDeleteLov}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        )}
        {!isSiteFlag && (
          <ButtonPermission
            permissionList={[
              {
                code: `${match.path}.button.import`,
                type: 'button',
                meaning: '值集视图-值集头导入',
              },
            ]}
            icon="to-top"
            onClick={this.handleImport}
          >
            {intl.get('hpfm.valueList.view.message.title.import').d('值集导入')}
          </ButtonPermission>
        )}
      </Header>
    );
  }

  tableProps() {
    const { history, loading, tenantId, isSiteFlag, match } = this.props;
    const columns = [
      !isTenantRoleLevel() && {
        title: intl.get('hpfm.valueList.model.header.tenantName').d('所属租户'),
        width: 200,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hpfm.valueList.model.header.lovCode').d('值集编码'),
        width: 200,
        dataIndex: 'lovCode',
      },
      {
        title: intl.get('hpfm.valueList.model.header.lovName').d('值集名称'),
        width: 200,
        dataIndex: 'lovName',
      },
      {
        title: intl.get('hpfm.valueList.model.header.lovTypeCode').d('值集类型'),
        width: 100,
        dataIndex: 'lovTypeMeaning',
      },
      {
        title: intl.get('hpfm.valueList.model.header.routeName').d('目标路由名'),
        width: 120,
        dataIndex: 'routeName',
      },
      {
        title: intl.get('hpfm.valueList.model.header.description').d('描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hpfm.valueList.model.header.publicFlag').d('是否公开'),
        width: 100,
        dataIndex: 'publicFlag',
        render: (val, record) => {
          return record.lovTypeCode === 'IDP' ? (
            <Badge status="success" text={intl.get('hzero.common.status.yes').d('是')} />
          ) : (
            yesOrNoRender(val)
          );
        },
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      isTenantRoleLevel() && {
        title: intl.get('hzero.common.source').d('来源'),
        width: 100,
        render: (_, record) => {
          return tenantId === record.tenantId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          );
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 180,
        fixed: 'right',
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
                      meaning: '值集定义-编辑',
                    },
                  ]}
                  onClick={() => {
                    history.push(`/hpfm/value-list/detail/${record.lovId}`);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'copy',
              ele: isSiteFlag
                ? record.tenantId === tenantId && (
                    // eslint-disable-next-line react/jsx-indent
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.copy`,
                          type: 'button',
                          meaning: '值集定义-复制',
                        },
                      ]}
                      onClick={() => this.handleLovCopy(record)}
                    >
                      {intl.get('hzero.common.button.copy').d('复制')}
                    </ButtonPermission>
                  )
                : record.tenantId !== tenantId && (
                    // eslint-disable-next-line react/jsx-indent
                    <ButtonPermission
                      type="text"
                      permissionList={[
                        {
                          code: `${match.path}.button.copy`,
                          type: 'button',
                          meaning: '值集定义-复制',
                        },
                      ]}
                      onClick={() => this.handleLovCopy(record)}
                    >
                      {intl.get('hzero.common.button.copy').d('复制')}
                    </ButtonPermission>
                  ),
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            },
            isSiteFlag ||
              (!isSiteFlag &&
                record.tenantId === tenantId && {
                  key: 'delete',
                  ele: (
                    <Popconfirm
                      title={intl
                        .get('hzero.common.message.confirm.delete')
                        .d('是否删除此条记录？')}
                      onConfirm={() => this.handleDelete(record)}
                    >
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${match.path}.button.delete`,
                            type: 'button',
                            meaning: '值集定义-删除',
                          },
                        ]}
                      >
                        {intl.get('hzero.common.button.delete').d('删除')}
                      </ButtonPermission>
                    </Popconfirm>
                  ),
                  len: 2,
                  title: intl.get('hzero.common.button.delete').d('删除'),
                }),
          ];
          return operatorRender(operators);
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
}
