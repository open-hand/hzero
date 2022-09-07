/**
 * 词语映射
 * TenantWord
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-29
 * @copyright 2019-05-29 © HAND
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Button, Popconfirm } from 'hzero-ui';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import { isTenantRoleLevel } from 'utils/utils';
import { getFieldsValueByRef } from '@/utils/utils';

import SearchForm from './SearchForm';
import EditFormModal from './EditFormModal';
import DataTable from './DataTable';

const id = 'tenantWordId';

@connect(mapStateToProps, mapDispatchToProps)
@formatterCollections({ code: ['hnlp.tenantWord'] })
export default class BasicData extends Component {
  getLanguageMessage() {
    return {
      model: {
        tenantWord: {
          word: intl.get('hnlp.tenantWord.model.tenantWord.value').d('转化后词语'),
          actualWord: intl.get('hnlp.tenantWord.model.tenantWord.actualValue').d('实际词语'),
          tenant: intl.get('hzero.common.model.tenantName').d('租户'),
        },
      },
      view: {
        title: {
          create: intl.get('hnlp.tenantWord.view.title.create').d('创建词语映射'),
          edit: intl.get('hnlp.tenantWord.view.title.edit').d('编辑词语映射'),
        },
      },
      common: {
        validation: {
          notNull(name) {
            return intl.get('hzero.common.validation.notNull', { name });
          },
          max(max) {
            return intl.get('hzero.common.validation.max', { max });
          },
        },
        btn: {
          action: intl.get('hzero.common.button.action').d('操作'),
          create: intl.get('hzero.common.button.create').d('新建'),
          del: intl.get('hzero.common.button.delete').d('删除'),
          search: intl.get('hzero.common.button.search').d('查询'),
          reset: intl.get('hzero.common.button.reset').d('重置'),
          edit: intl.get('hzero.common.button.edit').d('编辑'),
        },
        status: {
          yes: intl.get('hzero.common.status.yes').d('是'),
          no: intl.get('hzero.common.status.no').d('否'),
        },
        message: {
          confirm: {
            remove: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
          },
        },
      },
    };
  }

  // #endregion
  constructor(props) {
    super(props);
    this.searchFormRef = React.createRef();
    this.state = {
      selectedRows: [],
      selectedRowKeys: [],
      editFormModalVisible: false,
      editFormModalIsCreate: true,
      // pagination: {}, // 存储分页信息
    };
  }

  componentDidMount() {
    const { init } = this.props;
    init();
    this.handleSearch();
  }

  // #region gen functions

  reload() {
    const { pagination } = this.state;
    this.handleSearch(pagination);
  }

  // #region data inv
  handleSearch(pagination = {}) {
    const { query } = this.props;
    const queryData = getFieldsValueByRef(this.searchFormRef);
    this.setState({
      pagination,
      selectedRows: [],
      selectedRowKeys: [],
    });
    return query({
      query: { ...queryData, ...pagination },
    });
  }

  // #end

  // #region Header Btn Functions

  @Bind()
  handleCreateBtnClick() {
    this.setState({
      editFormModalVisible: true,
      editFormModalIsCreate: true,
      editRecord: {},
    });
  }

  @Bind()
  async handleDelBtnClick() {
    const { selectedRows = [] } = this.state;
    const { removeBatch } = this.props;
    removeBatch({ records: selectedRows }).then((res) => {
      if (res) {
        notification.success();
        this.reload();
      }
    });
  }

  delBtnDisabled() {
    const { selectedRows = [] } = this.state;
    return selectedRows.length === 0;
  }

  // #endregion

  // #region DataTable
  @Bind()
  handleRowSelectionChange({ selectedRows = [], selectedRowKeys = [] }) {
    this.setState({
      selectedRowKeys,
      selectedRows,
    });
  }

  @Bind()
  handleRecordEdit(record) {
    this.setState({
      editFormModalVisible: true,
      editFormModalIsCreate: false,
      editRecord: record,
    });
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleSearch({ page, sort });
  }

  // #endregion

  // #region EditFormModal
  @Bind()
  handleRecordEditFormEditOk(record) {
    const { editFormModalIsCreate = true } = this.state;
    let updateOrCreatePromise;
    if (editFormModalIsCreate) {
      const { create } = this.props;
      updateOrCreatePromise = create({ record });
    } else {
      const { update } = this.props;
      const { editRecord } = this.state;
      updateOrCreatePromise = update({
        record: {
          ...editRecord,
          ...record,
          tenantName: intl.get('hnlp.tenantWord.model.tenantWord.hzeroPlatform').d('HZERO平台'),
        },
        id: editRecord[id],
      });
    }
    updateOrCreatePromise.then((res) => {
      if (res) {
        notification.success();
        this.closeEditFormModal();
        this.handleSearch();
      }
    });
  }

  @Bind()
  handleRecordEditFormEditCancel() {
    this.closeEditFormModal();
  }

  closeEditFormModal() {
    this.setState({
      editFormModalVisible: false,
      editFormModalIsCreate: true,
      editRecord: {},
    });
  }

  // #endregion

  // #region SearchForm
  @Bind()
  handleSearchFormSubmit() {
    this.handleSearch();
  }

  // #endregion

  render() {
    const {
      dataSource,
      pagination,
      createLoading,
      removeBatchLoading,
      updateLoading,
      queryLoading,
      isSiteFlag,
    } = this.props;
    const {
      selectedRowKeys,
      editFormModalVisible = false,
      editFormModalIsCreate = true,
      editRecord,
    } = this.state;
    const languageMessage = this.getLanguageMessage();
    return (
      <>
        <Header title={intl.get('hnlp.tenantWord.view.message.title.tenantWord').d('租户词语映射')}>
          <Button icon="plus" type="primary" onClick={this.handleCreateBtnClick}>
            {languageMessage.common.btn.create}
          </Button>
          <Popconfirm
            title={languageMessage.common.message.confirm.remove}
            onConfirm={this.handleDelBtnClick}
          >
            <Button icon="delete" disabled={this.delBtnDisabled()} loading={removeBatchLoading}>
              {languageMessage.common.btn.del}
            </Button>
          </Popconfirm>
        </Header>
        <Content>
          <SearchForm
            languageMessage={languageMessage}
            wrappedComponentRef={this.searchFormRef}
            onSearch={this.handleSearchFormSubmit}
            removeBatchLoading={removeBatchLoading}
            isSiteFlag={isSiteFlag}
          />
          <DataTable
            onRowSelectionChange={this.handleRowSelectionChange}
            onRecordEdit={this.handleRecordEdit}
            onChange={this.handleTableChange}
            dataSource={dataSource}
            pagination={pagination}
            languageMessage={languageMessage}
            selectedRowKeys={selectedRowKeys}
            queryLoading={queryLoading}
            removeBatchLoading={removeBatchLoading}
            isSiteFlag={isSiteFlag}
          />
          <EditFormModal
            languageMessage={languageMessage}
            isCreate={editFormModalIsCreate}
            visible={editFormModalVisible}
            record={editRecord}
            onOk={this.handleRecordEditFormEditOk}
            onCancel={this.handleRecordEditFormEditCancel}
            createLoading={createLoading}
            updateLoading={updateLoading}
            isSiteFlag={isSiteFlag}
          />
        </Content>
      </>
    );
  }
}

function mapStateToProps({ nlpTenantWord = {}, loading = {} }) {
  const { dataSource, pagination } = nlpTenantWord;
  return {
    dataSource,
    pagination,
    isSiteFlag: !isTenantRoleLevel(),
    initLoading: loading.effects['nlpTenantWord/init'],
    createLoading: loading.effects['nlpTenantWord/create'],
    removeBatchLoading: loading.effects['nlpTenantWord/removeBatch'],
    updateLoading: loading.effects['nlpTenantWord/update'],
    queryLoading: loading.effects['nlpTenantWord/query'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    init: (payload) =>
      dispatch({
        type: 'nlpTenantWord/init',
        payload,
      }),
    create: (payload) =>
      dispatch({
        type: 'nlpTenantWord/create',
        payload,
      }),
    removeBatch: (payload) =>
      dispatch({
        type: 'nlpTenantWord/removeBatch',
        payload,
      }),
    update: (payload) =>
      dispatch({
        type: 'nlpTenantWord/update',
        payload,
      }),
    query: (payload) =>
      dispatch({
        type: 'nlpTenantWord/query',
        payload,
      }),
  };
}
