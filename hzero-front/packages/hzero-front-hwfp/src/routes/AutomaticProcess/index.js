import React, { Component } from 'react';
import { connect } from 'dva';
import { Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isEmpty from 'lodash/isEmpty';

import { Content, Header } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { queryMapIdpValue } from 'services/api';

import FilterForm from './FilterForm';
import List from './List';
import Modal from './Modal';

@formatterCollections({ code: ['hwfp.automaticProcess', 'hwfp.common'] })
@connect(({ loading }) => ({
  fetchProcessListLoading: loading.effects['automaticProcess/fetchProcessList'],
  removeProcessLoading: loading.effects['automaticProcess/removeProcess'],
  modifyProcessLoading: loading.effects['automaticProcess/modifyProcess'],
  addProcessLoading: loading.effects['automaticProcess/addProcess'],
}))
export default class AutomaticProcess extends Component {
  form;

  constructor(props) {
    super(props);
    this.state = {
      selectedLines: [], // 选择的行数据主键
      selectedLinesData: [], // 选择菜单行数据
      processList: [], // 流程数据
      processListPagination: {}, // 流程数据分页对象
      modalVisible: false, // 侧滑编辑框显示隐藏标识
      isBatchEdit: false, // 是否批量编辑标识
      processConditionOptions: [], // 侧滑编辑框审批条件值集数据
      processRuleOptions: [], // 侧滑编辑框审批规则值集数据
      processtimeOutOptions: [], // 侧滑编辑框超时单位值集数据
    };
  }

  componentDidMount() {
    this.fetchProcessList();
    this.fetchLovData();
  }

  @Bind()
  fetchLovData() {
    queryMapIdpValue({
      processConditionOptions: 'HWFP.PROCESS_CONDITION',
      processRuleOptions: 'HWFP.PROCESS_RULE',
      processtimeOutOptions: 'HWFP.TIMEOUT_UNIT',
    }).then((res) => {
      if (res) {
        this.setState({
          processConditionOptions: res.processConditionOptions || [],
          processRuleOptions: res.processRuleOptions || [],
          processtimeOutOptions: res.processtimeOutOptions || [],
        });
      }
    });
  }

  @Bind()
  fetchProcessList(params = {}) {
    this.props
      .dispatch({
        type: 'automaticProcess/fetchProcessList',
        params,
      })
      .then((res) => {
        if (res) {
          const { dataSource = [], pagination = {} } = res || {};
          this.setState({
            processList: dataSource,
            processListPagination: pagination,
          });
        }
      });
  }

  @Bind()
  deleteSelectedLines() {
    const { selectedLinesData = [], processListPagination = {} } = this.state;
    this.props
      .dispatch({
        type: 'automaticProcess/removeProcess',
        params: selectedLinesData,
      })
      .then((res) => {
        if (isEmpty(res)) {
          notification.success();
          const { getFieldsValue = () => {} } = this.form;
          this.fetchProcessList({
            ...getFieldsValue(),
            page: processListPagination,
          });
          this.setState({
            selectedLines: [],
            selectedLinesData: [],
          });
        }
      });
  }

  @Bind()
  openBatchEditModal() {
    this.setState({
      modalVisible: true,
      isBatchEdit: true,
      modelEditData: [],
    });
  }

  // 设置form
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  @Bind()
  openEditModal(processInfo = {}) {
    this.setState({
      modalVisible: true,
      isBatchEdit: false,
      modelEditData: processInfo,
    });
  }

  @Bind()
  selectProcessLines(processLines = [], processLinesData = []) {
    this.setState({
      selectedLines: processLines,
      selectedLinesData: processLinesData,
    });
  }

  @Bind()
  closeModal() {
    this.setState({
      modalVisible: false,
      modelEditData: [],
    });
  }

  @Bind()
  saveProcess(params) {
    const { isBatchEdit = false, modelEditData = {}, selectedLines = [] } = this.state;
    const { processKey, automaticId } = modelEditData;
    // 非批量编辑
    if (!isBatchEdit) {
      // 已存在automaticId 则是更新， 不存在则是新建
      if (automaticId) {
        const newParams = {
          ...modelEditData,
          ...params,
        };
        this.fetchModifyProcess(newParams);
      } else {
        const newParams = {
          ...params,
          processKeyList: [processKey],
        };
        this.fetchAddProcess(newParams);
      }
    } else {
      const newParams = {
        ...params,
        processKeyList: selectedLines,
      };
      this.fetchAddProcess(newParams);
    }
  }

  @Bind()
  fetchModifyProcess(params) {
    this.props
      .dispatch({
        type: 'automaticProcess/modifyProcess',
        params,
      })
      .then((res) => {
        if (!isEmpty(res)) {
          notification.success();
          const { processListPagination = {} } = this.state;
          const { getFieldsValue = () => {} } = this.form;
          this.fetchProcessList({
            ...getFieldsValue(),
            page: processListPagination,
          });
          this.closeModal();
        }
      });
  }

  @Bind()
  fetchAddProcess(params) {
    this.props
      .dispatch({
        type: 'automaticProcess/addProcess',
        params,
      })
      .then((res) => {
        if (!isEmpty(res)) {
          notification.success();
          const { getFieldsValue = () => {} } = this.form;
          this.fetchProcessList({
            ...getFieldsValue(),
          });
          this.closeModal();
        }
      });
  }

  @Bind()
  handleChangePagination(pagination) {
    const { getFieldsValue = () => {} } = this.form;
    this.setState({ processListPagination: pagination });
    this.fetchProcessList({
      ...getFieldsValue(),
      ...pagination,
    });
  }

  render() {
    const {
      selectedLines = [],
      processList = [],
      processListPagination = {},
      modalVisible,
      isBatchEdit,
      modelEditData = [],
      processConditionOptions = [],
      processRuleOptions = [],
      processtimeOutOptions = [],
    } = this.state;
    const {
      fetchProcessListLoading,
      removeProcessLoading,
      modifyProcessLoading,
      addProcessLoading,
    } = this.props;

    return (
      <>
        <Header
          title={intl
            .get('hwfp.automaticProcess.view.message.title.automaticProcess')
            .d('自动处理规则')}
        >
          <Button
            icon="edit"
            type="primary"
            style={{ marginLeft: 8 }}
            disabled={!selectedLines.length > 0}
            onClick={this.openBatchEditModal}
          >
            {intl.get('hwfp.automaticProcess.view.button.batchEdit').d('批量编辑')}
          </Button>
          <Button
            disabled={!selectedLines.length > 0}
            onClick={this.deleteSelectedLines}
            icon="delete"
          >
            {intl.get('hzero.common.button.clear').d('清空')}
          </Button>
        </Header>
        <Content>
          <FilterForm
            bindRef={this.handleBindRef}
            handleSearch={this.fetchProcessList}
            handleSelectProcessLines={this.selectProcessLines}
          />
          <List
            loading={fetchProcessListLoading || removeProcessLoading || false}
            dataSource={processList}
            selectedRowKeys={selectedLines}
            pagination={processListPagination}
            handleSelectRows={this.selectProcessLines}
            handleEdit={this.openEditModal}
            handleChangePagination={this.handleChangePagination}
          />
        </Content>
        <Modal
          visible={modalVisible}
          isBatch={isBatchEdit}
          editData={modelEditData}
          handleCreate={this.saveProcess}
          handleClose={this.closeModal}
          loading={modifyProcessLoading || addProcessLoading || false}
          processConditionOptions={processConditionOptions}
          processRuleOptions={processRuleOptions}
          processtimeOutOptions={processtimeOutOptions}
        />
      </>
    );
  }
}
