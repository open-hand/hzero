/**
 * 模板管理
 * Template
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-24
 * @copyright 2019-05-24 © HAND
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';

import { getFieldsValueByRef } from '@/utils/utils';

import SearchForm from './SearchForm';
import DataTable from './DataTable';
import EditFormModal from './EditFormModal';

const id = 'templateId';

@connect(mapStateToProps, mapDispatchToProps)
@formatterCollections({ code: ['hnlp.template'] })
export default class Template extends Component {
  // #endregion
  constructor(props) {
    super(props);
    this.searchFormRef = React.createRef();
    this.state = {
      // selectedRows: [],
      // selectedRowKeys: [],
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
  getLanguageMessage() {
    return {
      model: {
        template: {
          templateCode: intl.get('hnlp.template.model.template.templateCode').d('模板编码'),
          templateName: intl.get('hnlp.template.model.template.templateName').d('模板名称'),
          description: intl.get('hnlp.template.model.template.description').d('模板描述'),
          replaceChar: intl.get('hnlp.template.model.template.replaceChar').d('替换字符串'),
          maxGram: intl.get('hnlp.template.model.template.maxGram').d('模型准确度'),
          tenantName: intl.get('hnlp.templateWord.model.templateWord.tenantName').d('租户'),
        },
      },
      view: {
        title: {
          create: intl.get('hnlp.template.view.title.create').d('创建模板'),
          edit: intl.get('hnlp.template.view.title.edit').d('编辑模板'),
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
          codeUpper: intl
            .get('hzero.common.validation.codeUpper')
            .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
        },
        btn: {
          create: intl.get('hzero.common.button.create').d('新建'),
          del: intl.get('hzero.common.button.delete').d('删除'),
          search: intl.get('hzero.common.button.search').d('查询'),
          reset: intl.get('hzero.common.button.reset').d('重置'),
          edit: intl.get('hzero.common.button.edit').d('编辑'),
          action: intl.get('hzero.common.button.action').d('操作'),
          viewMore: intl.get('hzero.common.button.viewMore').d('更多查询'),
          collected: intl.get('hzero.common.button.collected').d('收起查询'),
        },
        _status: intl.get('hzero.common.status').d('状态'),
        status: {
          enable: intl.get('hzero.common.status.enable').d('启用'),
          disable: intl.get('hzero.common.status.disable').d('禁用'),
        },
        message: {
          confirm: {
            del: intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？'),
          },
        },
      },
    };
  }

  reload() {
    const { pagination } = this.state;
    this.handleSearch(pagination);
  }

  // #region data inv
  handleSearch(pagination = {}) {
    const { query } = this.props;
    const queryData = getFieldsValueByRef(this.searchFormRef);
    this.setState({ pagination });
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

  // @Bind()
  // async handleDelBtnClick() {
  //   const {selectedRows = []} = this.state;
  //   const {removeBatch} = this.props;
  //   removeBatch({records: selectedRows})
  //     .then(res => {
  //       if (res) {
  //         notification.success()
  //         this.reload();
  //       }
  //     })
  // }
  //
  // delBtnDisabled() {
  //   const {selectedRows = []} = this.state;
  //   return selectedRows.length === 0;
  // }

  // #endregion

  // #region DataTable
  // @Bind()
  // handleRowSelectionChange({selectedRows = [], selectedRowKeys = []}) {
  //   this.setState({
  //     selectedRowKeys,
  //     selectedRows,
  //   });
  // }

  @Bind()
  handleRecordEdit(record) {
    const { queryDetail } = this.props;
    this.setState({
      editFormModalVisible: true,
      editFormModalIsCreate: false,
      editRecord: {},
    });
    queryDetail({
      id: record[id],
    }).then((res) => {
      if (res) {
        this.setState({
          editRecord: res,
        });
      }
    });
  }

  @Bind()
  handleRecordRemove(record) {
    const { remove } = this.props;
    remove({
      record,
      id: record[id],
    }).then((res) => {
      if (res) {
        notification.success();
        this.reload();
      }
    });
  }

  @Bind()
  handleTableChange(page, filter, sort) {
    this.handleSearch({ page, sort });
  }

  // #endregion

  // #region EditFormModal
  @Bind()
  handleRecordEditOk(record) {
    const { editFormModalIsCreate = true } = this.state;
    let updateOrCreatePromise;
    if (editFormModalIsCreate) {
      const { create } = this.props;
      updateOrCreatePromise = create({ record });
    } else {
      const { update } = this.props;
      const { editRecord } = this.state;
      updateOrCreatePromise = update({ record: { ...editRecord, ...record }, id: editRecord[id] });
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
  handleRecordEditCancel() {
    this.closeEditFormModal();
  }

  closeEditFormModal() {
    this.setState({
      editFormModalIsCreate: true,
      editFormModalVisible: false,
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
      removeLoading,
      updateLoading,
      queryLoading,
      queryDetailLoading,
      enums: { modelAccuracy },
      isTenant,
      organizationId,
    } = this.props;
    const { editFormModalVisible = false, editFormModalIsCreate = true, editRecord } = this.state;
    const languageMessage = this.getLanguageMessage();
    return (
      <>
        <Header title={intl.get('hnlp.template.view.message.title.template').d('NLP模板管理')}>
          <Button icon="plus" type="primary" onClick={this.handleCreateBtnClick}>
            {languageMessage.common.btn.create}
          </Button>
        </Header>
        <Content>
          <SearchForm
            organizationId={organizationId}
            languageMessage={languageMessage}
            wrappedComponentRef={this.searchFormRef}
            onSearch={this.handleSearchFormSubmit}
            modelAccuracy={modelAccuracy}
            isTenantRoleLevel={isTenant}
          />
          <DataTable
            organizationId={organizationId}
            onRecordEdit={this.handleRecordEdit}
            onRecordRemove={this.handleRecordRemove}
            onChange={this.handleTableChange}
            dataSource={dataSource}
            pagination={pagination}
            languageMessage={languageMessage}
            queryLoading={queryLoading}
            removeLoading={removeLoading}
            queryDetailLoading={queryDetailLoading}
            isTenantRoleLevel={isTenant}
          />
          <EditFormModal
            organizationId={organizationId}
            languageMessage={languageMessage}
            visible={editFormModalVisible}
            isCreate={editFormModalIsCreate}
            record={editRecord}
            onOk={this.handleRecordEditOk}
            onCancel={this.handleRecordEditCancel}
            queryDetailLoading={queryDetailLoading}
            updateLoading={updateLoading}
            createLoading={createLoading}
            modelAccuracy={modelAccuracy}
            isTenantRoleLevel={isTenant}
          />
        </Content>
      </>
    );
  }
}

function mapStateToProps({ nlpTemplate = {}, loading = {} }) {
  const { dataSource, pagination, enums } = nlpTemplate;
  return {
    dataSource,
    pagination,
    enums,
    isTenant: isTenantRoleLevel(),
    organizationId: getCurrentOrganizationId(),
    initLoading: loading.effects['nlpTemplate/init'],
    createLoading: loading.effects['nlpTemplate/create'],
    removeLoading: loading.effects['nlpTemplate/remove'],
    updateLoading: loading.effects['nlpTemplate/update'],
    queryLoading: loading.effects['nlpTemplate/query'],
    queryDetailLoading: loading.effects['nlpTemplate/queryDetail'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    init: (payload) =>
      dispatch({
        type: 'nlpTemplate/init',
        payload,
      }),
    create: (payload) =>
      dispatch({
        type: 'nlpTemplate/create',
        payload,
      }),
    remove: (payload) =>
      dispatch({
        type: 'nlpTemplate/remove',
        payload,
      }),
    update: (payload) =>
      dispatch({
        type: 'nlpTemplate/update',
        payload,
      }),
    query: (payload) =>
      dispatch({
        type: 'nlpTemplate/query',
        payload,
      }),
    queryDetail: (payload) =>
      dispatch({
        type: 'nlpTemplate/queryDetail',
        payload,
      }),
  };
}
