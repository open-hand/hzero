import React, { Component } from 'react';
import { Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isObject } from 'lodash';
import { connect } from 'dva';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';

import Search from './Search';
import List from './List';
import DelegateModal from './DelegateModal';

@formatterCollections({
  code: [
    'hwfp.processDelegate',
    'hwfp.task',
    'hwfp.automaticProcess',
    'hwfp.common',
    'hzero.common',
  ],
})
@connect(({ processDelegate, loading }) => ({
  processDelegate,
  querying: loading.effects['processDelegate/fetchProcess'],
  saving: loading.effects['processDelegate/delegatePorcess'],
}))
export default class processDelegate extends Component {
  form;

  state = {
    modalVisible: false,
    selectedRows: [],
    rowsData: [],
    rowsDataPagination: null,
  };

  componentDidMount() {
    this.fetchProcess();
    this.props.dispatch({ type: 'processDelegate/queryProcessStatus' });
  }

  @Bind()
  fetchProcess(params = {}) {
    const queryParams = this.form.getFieldsValue();
    this.props
      .dispatch({
        type: 'processDelegate/fetchProcess',
        params: {
          ...queryParams,
          ...params,
        },
      })
      .then((res) => {
        const { dataSource, pagination } = res;
        this.setState({
          selectedRows: [],
          rowsData: dataSource,
          rowsDataPagination: pagination,
        });
      });
  }

  @Bind()
  onRef(ref) {
    this.form = (ref.props || {}).form;
  }

  @Bind()
  toogleModal() {
    const { modalVisible } = this.state;
    this.setState({ modalVisible: !modalVisible });
  }

  @Bind()
  handleSelectRows(selectedRows) {
    this.setState({ selectedRows });
  }

  @Bind()
  delegate(targetEmployee) {
    const { selectedRows } = this.state;
    this.props
      .dispatch({
        type: 'processDelegate/delegatePorcess',
        params: {
          taskIdList: selectedRows.map((item) => item.id),
          sourceEmployeeList: selectedRows.map((item) => item.assignee),
          targetEmployee,
        },
      })
      .then((res) => {
        if (isObject(res) && isEmpty(res)) {
          notification.success();
          this.toogleModal();
          this.fetchProcess();
        }
      });
  }

  render() {
    const { selectedRows = [], modalVisible, rowsData, rowsDataPagination } = this.state;
    const {
      querying = false,
      processDelegate: { processStatus = [] },
    } = this.props;

    return (
      <>
        <Header title={intl.get('hwfp.processDelegate.view.message.title').d('流程转交')}>
          <Button type="primary" onClick={this.toogleModal} disabled={!selectedRows.length}>
            {intl.get('hwfp.task.view.option.delegate', { name: '转交' }).d('转交')}
          </Button>
        </Header>
        <Content>
          <Search handleRef={this.onRef} onSearch={this.fetchProcess} />
          <List
            dataSource={rowsData}
            loading={querying}
            processStatus={processStatus}
            handleChange={this.fetchProcess}
            pagination={rowsDataPagination}
            selectedRows={selectedRows}
            onSelectRows={this.handleSelectRows}
          />
          <DelegateModal
            visible={modalVisible}
            onSubmit={this.delegate}
            selectedRows={selectedRows}
            handleClose={this.toogleModal}
          />
        </Content>
      </>
    );
  }
}
