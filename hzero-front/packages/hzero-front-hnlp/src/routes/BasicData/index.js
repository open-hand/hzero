/**
 * 基础数据管理
 * BasicData
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-28
 * @copyright 2019-05-28 © HAND
 */

import React, { Component } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { Button, Popconfirm, Tooltip } from 'hzero-ui';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { openTab } from 'utils/menuTab';
import { filterNullValueObject, getAccessToken, getCurrentOrganizationId } from 'utils/utils';
import { API_HOST, HZERO_NLP } from 'utils/config';
import { DEBOUNCE_TIME } from 'utils/constants';

import { getFieldsValueByRef } from '@/utils/utils';

import SearchForm from './SearchForm';
import DataTable from './DataTable';
import ActualValueEditModal from './ActualValueEditModal';

import styles from './styles.less';

const id = 'id';

@connect(mapStateToProps, mapDispatchToProps)
@formatterCollections({ code: ['hnlp.basicData'] })
export default class BasicData extends Component {
  // #endregion
  constructor(props) {
    super(props);
    this.searchFormRef = React.createRef();
    this.state = {
      selectedRows: [],
      selectedRowKeys: [],
      editActualValueModalVisible: false,
      // pagination: {}, // 存储分页信息
    };
    // this.fileExportLoadingTimer = undefined; // 设置 导出 按钮的 loading
  }

  getLanguageMessage() {
    return {
      model: {
        basicData: {
          id: intl.get('hnlp.basicData.model.basicData.id').d('数据ID'),
          dataKey: intl.get('hnlp.basicData.model.basicData.dataKey').d('数据编码'),
          dataType: intl.get('hnlp.basicData.model.basicData.dataType').d('数据类型'),
          value: intl.get('hnlp.basicData.model.basicData.value').d('数据值'),
          context: intl.get('hnlp.basicData.model.basicData.context').d('上下文'),
          actualValue: intl.get('hnlp.basicData.model.basicData.mappedValue').d('映射值'),
          empty: intl.get('hnlp.basicData.model.basicData.empty').d('存在映射值'),
        },
      },
      view: {
        title: {
          actualValueEdit: intl.get('hnlp.basicData.view.title.actualValueEdit').d('实际类型编辑'),
        },
        help: {
          fileExport: intl
            .get('hnlp.basicData.view.title.help.fileExport')
            .d('上下文多于3列，请修改导入模板，模板编码HNLP.BASIC_DATA'),
        },
      },
      common: {
        btn: {
          add: intl.get('hzero.common.button.add').d('新增'),
          del: intl.get('hzero.common.button.delete').d('删除'),
          search: intl.get('hzero.common.button.search').d('查询'),
          reset: intl.get('hzero.common.button.reset').d('重置'),
          edit: intl.get('hzero.common.button.edit').d('编辑'),
          submit: intl.get('hzero.common.button.submit').d('提交'),
          fileExport: intl.get('hzero.common.button.export').d('导出'),
          batchImport: intl.get('hzero.common.title.batchImport').d('批量导入'),
        },
        status: {
          yes: intl.get('hzero.common.status.yes').d('是'),
          no: intl.get('hzero.common.status.no').d('否'),
        },
        message: {
          noSaveDataSubmit: intl
            .get('hnlp.common.message.noSaveDataSubmit')
            .d('有修改的数据未提交，是否继续?'),
          confirm: {
            remove: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
          },
        },
      },
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

  /**
   * 批量导入
   */
  @Bind()
  handleBatchImport() {
    openTab({
      key: '/hnlp/data-import/HNLP.BASIC_DATA',
      search: queryString.stringify({
        key: '/hnlp/data-import/HNLP.BASIC_DATA',
        title: 'hzero.common.title.batchImport',
        action: 'hzero.common.title.batchImport',
        prefixPatch: HZERO_NLP,
      }),
    });
  }

  /**
   * 导出按钮点击后给1秒钟的 loading 效果
   */
  @Bind()
  handleFileExportClick() {
    clearTimeout(this.fileExportLoadingTimer);
    this.setState({
      fileExportLoading: true,
    });
    this.fileExportLoadingTimer = setTimeout(() => {
      this.setState({
        fileExportLoading: false,
      });
    }, DEBOUNCE_TIME * 5);
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
      editActualValueModalVisible: true,
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
  handleRecordActualValueEditOk(record) {
    const { update } = this.props;
    const { editRecord } = this.state;
    update({ record: { ...editRecord, ...record }, id: editRecord[id] }).then((res) => {
      if (res) {
        notification.success();
        this.closeEditFormModal();
        this.reload();
      }
    });
  }

  @Bind()
  handleRecordActualValueEditCancel() {
    this.closeEditFormModal();
  }

  closeEditFormModal() {
    this.setState({
      editActualValueModalVisible: false,
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

  // 导出组件 获取查询表单数据方法
  @Bind()
  handleGetFormValue() {
    return getFieldsValueByRef(this.searchFormRef);
  }

  render() {
    const {
      organizationId,
      dataSource,
      pagination,
      removeBatchLoading,
      updateLoading,
      queryLoading,
    } = this.props;
    const {
      selectedRowKeys,
      editActualValueModalVisible = false,
      editRecord,
      fileExportLoading = false,
    } = this.state;
    const languageMessage = this.getLanguageMessage();
    return (
      <>
        <Header title={intl.get('hnlp.basicData.view.message.title.basicData').d('基础数据')}>
          <Popconfirm
            title={languageMessage.common.message.confirm.remove}
            onConfirm={this.handleDelBtnClick}
          >
            <Button
              disabled={this.delBtnDisabled()}
              loading={removeBatchLoading}
              icon="delete"
              type="primary"
            >
              {languageMessage.common.btn.del}
            </Button>
          </Popconfirm>
          <Tooltip placement="bottom" title={languageMessage.view.help.fileExport}>
            <Button icon="to-top" onClick={this.handleBatchImport}>
              {languageMessage.common.btn.batchImport}
            </Button>
          </Tooltip>
          <Button
            icon="export"
            loading={fileExportLoading}
            onClick={this.handleFileExportClick}
            href={`${API_HOST}${HZERO_NLP}/v1/${organizationId}/basic-datas/export?${queryString.stringify(
              filterNullValueObject({
                ...this.handleGetFormValue(),
                access_token: getAccessToken(),
              })
            )}`}
          >
            {languageMessage.common.btn.fileExport}
          </Button>
        </Header>
        <Content className={styles['hnlp-basic-data']}>
          <SearchForm
            languageMessage={languageMessage}
            wrappedComponentRef={this.searchFormRef}
            onSearch={this.handleSearchFormSubmit}
            removeBatchLoading={removeBatchLoading}
          />
          <DataTable
            onRowSelectionChange={this.handleRowSelectionChange}
            onRecordActualValueEdit={this.handleRecordEdit}
            onChange={this.handleTableChange}
            dataSource={dataSource}
            pagination={pagination}
            languageMessage={languageMessage}
            selectedRowKeys={selectedRowKeys}
            queryLoading={queryLoading}
            removeBatchLoading={removeBatchLoading}
          />
          <ActualValueEditModal
            languageMessage={languageMessage}
            visible={editActualValueModalVisible}
            record={editRecord}
            onOk={this.handleRecordActualValueEditOk}
            onCancel={this.handleRecordActualValueEditCancel}
            updateLoading={updateLoading}
          />
        </Content>
      </>
    );
  }
}

function mapStateToProps({ nlpBasicData = {}, loading = {} }) {
  const { dataSource, pagination, enums } = nlpBasicData;
  return {
    organizationId: getCurrentOrganizationId(),
    dataSource,
    pagination,
    enums,
    initLoading: loading.effects['nlpBasicData/init'],
    removeBatchLoading: loading.effects['nlpBasicData/removeBatch'],
    updateLoading: loading.effects['nlpBasicData/update'],
    queryLoading: loading.effects['nlpBasicData/query'],
  };
}

function mapDispatchToProps(dispatch) {
  return {
    init: (payload) =>
      dispatch({
        type: 'nlpBasicData/init',
        payload,
      }),
    removeBatch: (payload) =>
      dispatch({
        type: 'nlpBasicData/removeBatch',
        payload,
      }),
    update: (payload) =>
      dispatch({
        type: 'nlpBasicData/update',
        payload,
      }),
    query: (payload) =>
      dispatch({
        type: 'nlpBasicData/query',
        payload,
      }),
  };
}
