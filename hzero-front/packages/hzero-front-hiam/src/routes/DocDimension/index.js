/**
 * docDimension-单据维度
 * @date: 2019-09-19
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Button, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { enableRender } from 'utils/renderer';
import { tableScrollWidth, isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';

import FilterForm from './FilterForm';
import CreateDrawer from './CreateDrawer';

function getFieldsValueByWrappedComponentRef(ref) {
  if (ref.current) {
    const { form } = ref.current.props;
    return form.getFieldsValue();
  }
  return {};
}

@connect(({ loading, docDimension }) => ({
  docDimension,
  isSiteFlag: !isTenantRoleLevel(),
  tenantId: getCurrentOrganizationId(),
  fetchLoading: loading.effects['docDimension/query'],
  saveLoading: loading.effects['docDimension/create'] || loading.effects['docDimension/update'],
  detailLoading: loading.effects['docDimension/queryDetail'],
}))
@formatterCollections({ code: ['hiam.docDimension'] })
export default class docDimension extends PureComponent {
  constructor(props) {
    super(props);
    this.searchFormRef = React.createRef();

    this.state = {
      modalVisible: false, // 控制模态框显示
      createFlag: false,
    };
  }

  /**
   * render()调用后请求数据
   */
  componentDidMount() {
    const { dispatch } = this.props;
    const lovCodes = {
      dimensionTypeList: 'HIAM.AUTHORITY_SCOPE_CODE',
      valueSourceTypeList: 'HIAM.DOC_DIMENSION.SOURCE_TYPE',
    };
    dispatch({
      type: 'docDimension/init',
      payload: {
        lovCodes,
      },
    });
    this.handleSearch();
  }

  /**
   * 查询数据
   * @param {Object} params - 查询参数
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    const fieldValues = getFieldsValueByWrappedComponentRef(this.searchFormRef);
    dispatch({
      type: 'docDimension/query',
      payload: { ...fieldValues, page: isEmpty(params) ? {} : params },
    });
  }

  /**
   * 新建时，显示Drawer
   */
  @Bind()
  handleCreate() {
    this.setState({
      modalVisible: true,
      createFlag: true,
    });
  }

  /**
   * 编辑时，显示Drawer
   */
  @Bind()
  handleEdit(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'docDimension/queryDetail',
      payload: { ...record },
    });
    this.setState({
      modalVisible: true,
      createFlag: false,
    });
  }

  /**
   * 隐藏Drawer
   */
  @Bind()
  hideDrawer() {
    const { dispatch } = this.props;
    this.setState({
      modalVisible: false,
    });
    dispatch({
      type: 'docDimension/updateState',
      payload: { dimensionDetail: {} },
    });
  }

  /**
   * 保存
   * @param {Object} fieldsValue - 操作数据
   */
  @Bind()
  handleSave(fieldsValue) {
    const {
      dispatch,
      docDimension: { pagination = {} },
    } = this.props;
    const { createFlag } = this.state;
    dispatch({
      type: createFlag ? 'docDimension/create' : 'docDimension/update',
      payload: fieldsValue,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
        this.hideDrawer();
      }
    });
  }

  /**
   * 渲染操作按钮
   * @param text - 文字描述
   * @param record - 当前记录
   * @returns {*}
   */
  @Bind()
  optionsRender(_, record) {
    return (
      <span className="action-link">
        <a onClick={() => this.handleEdit(record)}>
          {intl.get('hzero.common.button.edit').d('编辑')}
        </a>
      </span>
    );
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      fetchLoading = false,
      saveLoading = false,
      detailLoading = false,
      isSiteFlag,
      tenantId,
      docDimension: {
        dimensionList = [],
        dimensionDetail = {},
        valueSourceTypeList,
        dimensionTypeList,
        pagination,
      } = {},
    } = this.props;
    const { createFlag = {}, modalVisible = false } = this.state;
    const filterProps = {
      isSiteFlag,
      valueSourceTypeList,
      wrappedComponentRef: this.searchFormRef,
      onSearch: this.handleSearch,
    };
    const columns = [
      {
        title: intl.get('hiam.docDimension.model.docDimension.dimensionCode').d('维度编码'),
        dataIndex: 'dimensionCode',
        width: 150,
      },
      {
        title: intl.get('hiam.docDimension.model.docDimension.dimensionName').d('维度名称'),
        dataIndex: 'dimensionName',
      },
      {
        title: intl.get('hiam.docDimension.model.docDimension.dimensionType').d('维度类型'),
        dataIndex: 'dimensionTypeMeaning',
        width: 100,
      },
      {
        title: intl.get('hiam.docDimension.model.docDimension.orderSeq').d('排序号'),
        dataIndex: 'orderSeq',
        width: 150,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 100,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 60,
        fixed: 'right',
        render: this.optionsRender,
      },
    ].filter(Boolean);
    const tableProps = {
      columns,
      pagination,
      bordered: true,
      rowKey: 'dimensionId',
      loading: fetchLoading,
      dataSource: dimensionList,
      scroll: { x: tableScrollWidth(columns) },
      onChange: (page) => this.handleSearch(page),
    };
    const drawerProps = {
      title: createFlag
        ? intl.get('hiam.docDimension.model.docDimension.create').d('新建单据维度')
        : intl.get('hiam.docDimension.model.docDimension.edit').d('编辑单据维度'),
      tenantId,
      modalVisible,
      dimensionTypeList,
      valueSourceTypeList,
      initData: dimensionDetail,
      initLoading: detailLoading,
      confirmLoading: saveLoading,
      onOk: this.handleSave,
      onCancel: this.hideDrawer,
    };
    return (
      <>
        <Header title={intl.get('hiam.docDimension.view.message.title.docDimension').d('单据维度')}>
          <Button icon="plus" type="primary" onClick={() => this.handleCreate()}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <Table {...tableProps} />
          <CreateDrawer {...drawerProps} />
        </Content>
      </>
    );
  }
}
