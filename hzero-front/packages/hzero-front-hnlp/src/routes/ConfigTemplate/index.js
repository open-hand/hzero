/**
 * 模板配置
 * ConfigTemplate
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-24
 * @copyright 2019-05-24 © HAND
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

import { getFieldsValueByRef } from '@/utils/utils';

import SearchForm from './SearchForm';
import DataTable from './DataTable';
import EditFormModal from './EditFormModal';

const id = 'dataId';

@connect(mapStateToProps, mapDispatchToProps)
@formatterCollections({ code: ['hnlp.template', 'hnlp.configTemplate'] })
export default class ConfigTemplate extends Component {
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

  // #region gen functions
  getLanguageMessage() {
    return {
      model: {
        configTemplate: {
          actualType: intl.get('hnlp.configTemplate.model.configTemplate.actualType').d('识别要素'),
          type: intl.get('hnlp.configTemplate.model.configTemplate.type').d('映射数据类型'),
          tenant: intl.get('hzero.common.model.tenantName').d('租户'),
          typeNum: intl.get('hnlp.configTemplate.model.configTemplate.typeNum').d('识别要素数量'),
          isCustom: intl.get('hnlp.configTemplate.model.configTemplate.isCustom').d('是否映射'),
          template: intl.get('hnlp.configTemplate.model.configTemplate.template').d('模板'),
          templateCode: intl
            .get('hnlp.configTemplate.model.configTemplate.templateCode')
            .d('模板编码'),
          templateName: intl
            .get('hnlp.configTemplate.model.configTemplate.templateName')
            .d('模板名称'),
          actualTypeMsg: intl
            .get('hnlp.configTemplate.model.configTemplate.baseActualTypeMsg')
            .d('基础数据中存在的实际类型'),
          typeMsg: intl
            .get('hnlp.configTemplate.model.configTemplate.resultActualTypeMsg')
            .d('识别结果中的数据类型映射'),
          typeNumMsg: intl
            .get('hnlp.configTemplate.model.configTemplate.typeNumMsg')
            .d('同一数据类型，能够被识别的词语数量'),
        },
      },
      view: {
        title: {
          create: intl.get('hnlp.configTemplate.view.title.create').d('创建模板配置'),
          edit: intl.get('hnlp.configTemplate.view.title.edit').d('编辑模板配置'),
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
          code: intl
            .get('hzero.common.validation.code')
            .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
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
        status: {
          yes: intl.get('hzero.common.status.yes').d('是'),
          no: intl.get('hzero.common.status.no').d('否'),
        },
        message: {
          confirm: {
            del: intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？'),
          },
        },
        predefined: intl.get('hzero.common.predefined').d('预定义'),
        custom: intl.get('hzero.common.custom').d('自定义'),
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

  componentDidMount() {
    const { init, isSiteFlag, organizationId } = this.props;
    if (!isSiteFlag) {
      init(organizationId);
    }
    this.handleSearch();
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

  // #endregion

  // #region DataTable
  @Bind()
  handleRecordEdit(record) {
    const { queryDetail } = this.props;
    const { tenantName } = record;
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
          editRecord: { ...res, tenantName },
        });
      }
    });
  }

  @Bind()
  handleRecordRemove(record) {
    const { remove } = this.props;
    remove({
      id: record[id],
      record,
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
        this.reload();
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
      organizationId,
      isSiteFlag,
      dataSource,
      pagination,
      createLoading,
      removeLoading,
      updateLoading,
      queryLoading,
      queryDetailLoading,
      enums: { basicDataType } = {},
      init,
    } = this.props;
    const { editFormModalVisible = false, editFormModalIsCreate = true, editRecord } = this.state;
    const languageMessage = this.getLanguageMessage();
    return (
      <>
        <Header
          title={intl.get('hnlp.configTemplate.view.message.title.configTemplate').d('NLP模板配置')}
        >
          <Button icon="plus" htmlType="button" type="primary" onClick={this.handleCreateBtnClick}>
            {languageMessage.common.btn.create}
          </Button>
        </Header>
        <Content>
          <SearchForm
            languageMessage={languageMessage}
            wrappedComponentRef={this.searchFormRef}
            onSearch={this.handleSearchFormSubmit}
            basicDataType={basicDataType}
            organizationId={organizationId}
            isSiteFlag={isSiteFlag}
          />
          <DataTable
            onRecordEdit={this.handleRecordEdit}
            onRecordRemove={this.handleRecordRemove}
            onChange={this.handleTableChange}
            dataSource={dataSource}
            pagination={pagination}
            languageMessage={languageMessage}
            queryLoading={queryLoading}
            removeLoading={removeLoading}
            queryDetailLoading={queryDetailLoading}
            isSiteFlag={isSiteFlag}
          />
          <EditFormModal
            languageMessage={languageMessage}
            visible={editFormModalVisible}
            isCreate={editFormModalIsCreate}
            record={editRecord}
            onOk={this.handleRecordEditOk}
            onCancel={this.handleRecordEditCancel}
            updateLoading={updateLoading}
            createLoading={createLoading}
            queryDetailLoading={queryDetailLoading}
            organizationId={organizationId}
            basicDataType={basicDataType}
            isSiteFlag={isSiteFlag}
            init={init}
          />
        </Content>
      </>
    );
  }
}

function mapStateToProps({ nlpConfigTemplate = {}, loading = {} }) {
  const { dataSource, pagination, enums } = nlpConfigTemplate;
  return {
    dataSource,
    pagination,
    enums,
    isSiteFlag: !isTenantRoleLevel(),
    organizationId: getCurrentOrganizationId(),
    initLoading: loading.effects['nlpConfigTemplate/init'],
    createLoading: loading.effects['nlpConfigTemplate/create'],
    removeLoading: loading.effects['nplTemplate/remove'],
    updateLoading: loading.effects['nlpConfigTemplate/update'],
    queryLoading: loading.effects['nlpConfigTemplate/query'],
    queryDetailLoading: loading.effects['nlpConfigTemplate/queryDetail'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    init: (payload) =>
      dispatch({
        type: 'nlpConfigTemplate/init',
        payload,
      }),
    create: (payload) =>
      dispatch({
        type: 'nlpConfigTemplate/create',
        payload,
      }),
    remove: (payload) =>
      dispatch({
        type: 'nlpConfigTemplate/remove',
        payload,
      }),
    update: (payload) =>
      dispatch({
        type: 'nlpConfigTemplate/update',
        payload,
      }),
    query: (payload) =>
      dispatch({
        type: 'nlpConfigTemplate/query',
        payload,
      }),
    queryDetail: (payload) =>
      dispatch({
        type: 'nlpConfigTemplate/queryDetail',
        payload,
      }),
  };
}
