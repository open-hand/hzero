/**
 * @date 2019-01-23
 * @author WY yang.wang06@hand-china.com
 * @copyright © HAND 2019
 */

import React from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';
import uuid from 'uuid/v4'; // 用于生成每次打开分配模态框的key

import { Button as ButtonPermission } from 'components/Permission';
import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import { isTenantRoleLevel, tableScrollWidth } from 'utils/utils';

import SearchForm from './SearchForm';
import CardEditModal from './CardEditModal';
import CardTenantEditModal from './CardTenantEditModal';

@connect(({ loading, cardManage }) => {
  return {
    cardManage,
    initLoading: loading.effects['cardManage/init'],
    detailLoading: loading.effects['cardManage/cardDetails'],
    cardFetchLoading: loading.effects['cardManage/cardFetch'],
    cardCreateLoading: loading.effects['cardManage/cardCreate'],
    cardUpdateLoading: loading.effects['cardManage/cardUpdate'],
    cardTenantFetchLoading: loading.effects['cardManage/cardTenantFetch'],
    cardTenantAddLoading: loading.effects['cardManage/cardTenantAdd'],
    cardTenantRemoveLoading: loading.effects['cardManage/cardTenantRemove'],
  };
})
@formatterCollections({ code: ['hpfm.card'] })
export default class CardManage extends React.Component {
  constructor(props) {
    super(props);

    const {
      cardManage: { dataSource },
    } = props;
    this.refSearchForm = null; // SearchForm 的 this
    this.state = {
      dataSource, // 数据
      prevDataSource: dataSource,
      cardEditModalProps: {
        isEdit: false, // 新增/编辑
        editRecord: {}, // 编辑的数据
        modalProps: { visible: false }, // Modal 的属性
      }, // 卡片编辑模态框的属性
      cardTenantEditModalProps: {
        modalProps: { visible: false },
        // cardId: undefined, // 卡片的id
        // disabled: true, // 卡片默认是禁用的
      },
    };
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    const { prevDataSource } = prevState;
    const {
      cardManage: { dataSource },
    } = nextProps;
    if (dataSource && dataSource !== prevDataSource) {
      return {
        dataSource,
        prevDataSource: dataSource,
      };
    }
    return null;
  }

  componentDidMount() {
    this.init();
    this.fetchCards();
  }

  render() {
    const {
      cardManage: {
        fdLevel, // 层级的值集
        catalogType, // 分类的值集
        pagination, // 分页
      },
      match: { path },
      initLoading,
      detailLoading,
      cardFetchLoading,
      cardCreateLoading,
      cardUpdateLoading,
      cardTenantFetchLoading,
      cardTenantAddLoading,
      cardTenantRemoveLoading,
    } = this.props;
    const {
      dataSource, // 数据
      cardEditModalProps, // 传递给 卡片编辑模态框的属性
      cardTenantEditModalProps, // 传递给 卡片分配租户模态框的属性
    } = this.state;
    const tableLoading = initLoading || cardFetchLoading;
    const cardEditModalLoading = cardCreateLoading || cardUpdateLoading;
    const cardTenantEditModalTableLoading = cardTenantFetchLoading;
    const cardTenantEditModalLoading = cardTenantAddLoading || cardTenantRemoveLoading;
    const columns = [
      {
        title: intl.get('hpfm.card.model.card.code').d('卡片代码'),
        dataIndex: 'code',
        width: 100,
      },
      {
        title: intl.get('hpfm.card.model.card.cardParams').d('卡片参数'),
        dataIndex: 'cardParams',
        width: 120,
      },
      {
        title: intl.get('hpfm.card.model.card.name').d('卡片名称'),
        dataIndex: 'name',
        width: 200,
      },
      {
        title: intl.get('hpfm.card.model.card.description').d('卡片描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hpfm.card.model.card.catalogType').d('卡片类型'),
        dataIndex: 'catalogMeaning',
        width: 100,
      },
      !isTenantRoleLevel() && {
        title: intl.get('hpfm.card.model.card.fdLevel').d('层级'),
        dataIndex: 'levelMeaning',
        width: 100,
      },
      {
        title: intl.get('hpfm.card.model.card.w').d('宽度'),
        dataIndex: 'w',
        width: 80,
      },
      {
        title: intl.get('hpfm.card.model.card.h').d('高度'),
        dataIndex: 'h',
        width: 80,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'action',
        fixed: 'right',
        width: 130,
        render: (_, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  key="edit"
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '卡片管理-编辑',
                    },
                  ]}
                  onClick={this.handleLineEditBtnClick.bind(undefined, record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          if (record.level === 'TENANT' && !isTenantRoleLevel()) {
            operators.push({
              key: 'assignCard',
              ele: (
                <ButtonPermission
                  key="assignCard"
                  onClick={this.handleLineAssignCardBtnClick.bind(undefined, record)}
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.assignCard`,
                      type: 'button',
                      meaning: '卡片管理-分配卡片',
                    },
                  ]}
                >
                  {intl.get('hpfm.card.view.button.assignCard').d('分配卡片')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hpfm.card.view.button.assignCard').d('分配卡片'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);

    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.card.view.title.list').d('卡片管理')}>
          <ButtonPermission
            type="primary"
            icon="plus"
            onClick={this.handleCreateBtnClick}
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '卡片管理-新建',
              },
            ]}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <SearchForm
              onRef={this.handleRefSearchForm}
              onSearch={this.fetchCards}
              fdLevel={fdLevel}
            />
          </div>
          <Table
            bordered
            rowKey="id"
            scroll={{ x: tableScrollWidth }}
            columns={columns}
            onChange={this.handleTableChange}
            loading={tableLoading}
            pagination={pagination}
            dataSource={dataSource}
          />
        </Content>
        {cardEditModalProps.modalProps.visible && (
          <CardEditModal
            {...cardEditModalProps}
            confirmLoading={cardEditModalLoading}
            detailLoading={detailLoading}
            catalogType={catalogType}
            fdLevel={fdLevel}
            onOk={this.handleCardEditModalOk}
            onCancel={this.handleCardEditModalCancel}
          />
        )}
        {cardTenantEditModalProps.modalProps.visible && (
          <CardTenantEditModal
            {...cardTenantEditModalProps}
            path={path}
            confirmLoading={cardTenantEditModalLoading}
            onOk={this.handleCardTenantEditModalOk}
            onCancel={this.handleCardTenantEditModalCancel}
            onFetchCardTenants={this.handleFetchCardTends}
            onRemoveCardTenants={this.handleRemoveCardTenants}
            fetchCardTenantsLoading={cardTenantEditModalTableLoading}
          />
        )}
      </React.Fragment>
    );
  }

  /**
   * 执行 init 方法, 加载页面只需要加载一次 且必须的数据
   */
  init() {
    const { dispatch } = this.props;
    dispatch({
      type: 'cardManage/init',
    });
  }

  // 查询相关的内容

  @Bind()
  handleRefSearchForm(refSearchForm) {
    this.refSearchForm = refSearchForm;
  }

  @Bind()
  handleSearchBtnClick() {
    this.fetchCards();
  }

  @Bind()
  fetchCards(pagination = {}) {
    const { dispatch } = this.props;
    const params = this.refSearchForm.props.form.getFieldsValue();
    dispatch({
      type: 'cardManage/cardFetch',
      payload: {
        ...params,
        ...pagination,
      },
    }).then((res) => {
      if (res) {
        this.setState({
          dataSource: res.content,
        });
      }
    });
  }

  @Bind()
  reloadList() {
    const {
      cardManage: { queryPagination },
    } = this.props;
    this.fetchCards(queryPagination);
  }

  // Table 相关的内容

  @Bind()
  handleTableChange(page, filter, sort) {
    this.fetchCards({ page, sort });
  }

  // CardEditModal 相关的内容

  @Bind()
  hiddenCardEditModal() {
    this.setState({
      cardEditModalProps: {
        isEdit: false, // 新增/编辑
        editRecord: {}, // 编辑的数据
        modalProps: { visible: false }, // Modal 的属性
      }, // 编辑模态框的属性
    });
  }

  /**
   * 重新刷新列表 并且关闭CardEditModal
   */
  @Bind()
  reloadAndHiddenCardEditModal() {
    this.hiddenCardEditModal();
    this.reloadList();
  }

  /**
   * 新建新的卡片
   */
  @Bind()
  handleCreateBtnClick() {
    this.setState({
      cardEditModalProps: {
        isEdit: false, // 新增/编辑
        editRecord: {}, // 编辑的数据
        modalProps: { visible: true }, // Modal 的属性
      }, // 编辑模态框的属性
    });
  }

  /**
   * 编辑
   * a 标签的事件 阻止默认事件
   */
  @Bind()
  handleLineEditBtnClick(record, event) {
    event.preventDefault();
    const { dispatch } = this.props;
    this.setState({
      cardEditModalProps: {
        isEdit: true, // 新增/编辑
        modalProps: { visible: true }, // Modal 的属性
      }, // 编辑模态框的属性
    });
    dispatch({
      type: 'cardManage/cardDetails',
      payload: {
        dashboardCardId: record.id,
      },
    }).then((res) => {
      this.setState({
        cardEditModalProps: {
          editRecord: res || {}, // 编辑的数据
          isEdit: true, // 新增/编辑
          modalProps: { visible: true }, // Modal 的属性
        }, // 编辑模态框的属性
      });
    });
  }

  /**
   * 取消编辑 卡片
   */
  @Bind()
  handleCardEditModalCancel() {
    this.hiddenCardEditModal();
  }

  /**
   * 保存编辑的卡片
   */
  @Bind()
  handleCardEditModalOk(formFields) {
    const { dispatch } = this.props;
    const { cardEditModalProps } = this.state;
    if (cardEditModalProps.isEdit) {
      dispatch({
        type: 'cardManage/cardUpdate', // 卡片更新
        payload: {
          ...cardEditModalProps.editRecord,
          ...formFields,
        },
      }).then((res) => {
        if (res) {
          notification.success();
          // 成功
          this.reloadAndHiddenCardEditModal();
        }
      });
    } else {
      dispatch({
        type: 'cardManage/cardCreate', // 卡片新建
        payload: formFields,
      }).then((res) => {
        if (res) {
          notification.success();
          // 成功
          this.reloadAndHiddenCardEditModal();
        }
      });
    }
  }

  // CardTenantEditModal 相关的内容

  /**
   * 分配卡片
   * a 标签的事件 阻止默认事件
   */
  @Bind()
  handleLineAssignCardBtnClick(record, event) {
    event.preventDefault();
    this.setState({
      cardTenantEditModalProps: {
        disabled: record.enabledFlag === 0,
        key: uuid(),
        modalProps: { visible: true },
        cardId: record.id,
      },
    });
  }

  @Bind()
  hiddenCardTenantEditModalAndReload() {
    this.hiddenCardTenantEditModal();
    this.reloadList();
  }

  @Bind()
  hiddenCardTenantEditModal() {
    const { cardTenantEditModalProps } = this.state;
    this.setState({
      cardTenantEditModalProps: {
        ...cardTenantEditModalProps,
        cardId: undefined, // 去掉 cardId
        disabled: true, // disabled 默认是真
        modalProps: { visible: false }, // 模态框隐藏
      },
    });
  }

  @Bind()
  handleFetchCardTends(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'cardManage/cardTenantFetch',
      payload,
    });
  }

  @Bind()
  handleRemoveCardTenants(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'cardManage/cardTenantRemove',
      payload,
    });
  }

  @Bind()
  handleCardTenantEditModalOk(payload) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'cardManage/cardTenantAdd',
      payload,
    }).then((res) => {
      if (res) {
        notification.success();
        // 成功 关闭模态框 不需要刷新页面
        this.hiddenCardTenantEditModal();
      }
    });
  }

  @Bind()
  handleCardTenantEditModalCancel() {
    this.hiddenCardTenantEditModal();
  }
}
