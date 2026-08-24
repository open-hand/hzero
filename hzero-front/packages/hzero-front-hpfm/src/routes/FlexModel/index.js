import React, { Component, Fragment } from 'react';
import { Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';

import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { filterNullValueObject } from 'utils/utils';

import FilterForm from './FilterForm';
import List from './List';
import ModelEditor from './ModelEditor';

const defaultListRowKey = 'modelId';

@formatterCollections({ code: ['hpfm.flexModel'] })
@connect(({ loading = {}, flexModel = {} }) => ({
  flexModel,
  queryListLoading: loading.effects['flexModel/queryList'],
}))
export default class FlexModel extends Component {
  form;

  state = {
    dataSource: [],
    pagination: [],
    modelEditorVisible: false,
    modelEditorData: {},
  };

  componentDidMount() {
    this.fetchList();
  }

  @Bind()
  fetchList(params = {}) {
    let newParams = params;
    if (params.page) {
      const filterValue = filterNullValueObject(this.form.getFieldsValue());
      newParams = Object.assign(newParams, filterValue);
    }
    this.props
      .dispatch({
        type: 'flexModel/queryList',
        params: newParams,
      })
      .then((res) => {
        if (res) {
          const { dataSource, pagination } = res || {};
          this.setState({
            dataSource,
            pagination,
          });
        }
      });
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  @Bind()
  createModel() {
    this.setState({ modelEditorVisible: true, modelEditorData: {} });
  }

  @Bind()
  afterCreateModel(modelId = '') {
    // 新建完直接跳转到详情
    this.editModel(modelId);
  }

  @Bind()
  editModel(id = '') {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/hpfm/ui-customize/model/detail/${id}`,
      })
    );
  }

  @Bind()
  closeModelEditor() {
    this.setState({ modelEditorVisible: false });
  }

  @Bind()
  deleteModel(modelId = '') {
    this.props
      .dispatch({
        type: 'flexModel/deleteModel',
        params: { modelId },
      })
      .then((res) => {
        if (res) {
          const { getFieldsValue = () => {} } = this.form;
          this.fetchList(getFieldsValue());
          notification.success();
        }
      });
  }

  render() {
    const { dataSource, pagination, modelEditorData, modelEditorVisible } = this.state;
    const { queryListLoading } = this.props;

    return (
      <Fragment>
        <Header title={intl.get('hpfm.flexModel.view.message.title.flexModel').d('模型配置')}>
          <Button type="primary" icon="plus" onClick={this.createModel}>
            {intl.get('hpfm.flexModel.view.message.button.importModel').d('导入模型')}
          </Button>
        </Header>
        <Content>
          <FilterForm onRef={this.handleBindRef} handleSearch={this.fetchList} />
          <List
            dataSource={dataSource}
            rowKey={defaultListRowKey}
            pagination={pagination}
            queryListLoading={queryListLoading}
            handleEditModel={this.editModel}
            handleFetchList={this.fetchList}
          />
        </Content>
        <ModelEditor
          visible={modelEditorVisible}
          editorData={modelEditorData}
          handleCreate={this.afterCreateModel}
          handleClose={this.closeModelEditor}
        />
      </Fragment>
    );
  }
}
