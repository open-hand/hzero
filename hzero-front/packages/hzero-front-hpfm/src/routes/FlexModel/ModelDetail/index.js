/* eslint-disable eqeqeq */
import React, { Component, Fragment } from 'react';
import { Tabs, Button, Icon, Dropdown, Menu } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';

import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { Header, Content } from 'components/Page';
import { queryMapIdpValue } from 'services/api';

import ModelInfoForm from './ModelInfoForm';
import Field from './Field';
import Relation from './Relation';
import FieldModal from './FieldModal';
import RelationModal from './RelationModal';

@formatterCollections({ code: ['hpfm.flexModelDetail'] })
@connect(({ loading = {}, flexModel: { dataSource = [] } }) => ({
  dataSource,
  queryFieldsListLoading: loading.effects['flexModel/queryFieldsList'],
  refreshFieldsLoading: loading.effects['flexModel/initFields'],
  addFieldLoading: loading.effects['flexModel/addField'],
  removeFieldLoading: loading.effects['flexModel/removeField'],
}))
export default class ModelDetail extends Component {
  table;

  state = {
    modelInfo: {},
    fieldList: [],
    activeTabKey: 'fields',
    addFieldModleVisible: false,
    relationModelVisible: false,
    relationModelData: {},
    relation: '', // 关系
    fieldTypeOptions: [],
    fieldCategoryOptions: [],
    fieldComponentOptions: [],
    modelRelationOptions: [],
    dateFormatOptions: [],
  };

  componentDidMount() {
    const {
      match: {
        params: { modelId },
      },
    } = this.props;
    this.fetchModel({ modelId });
    this.fetchLovData(); // 查询lov值集
    this.fetchList({ modelId });
    this.fetchRelationList({ modelId });
  }

  @Bind
  fetchLovData() {
    queryMapIdpValue({
      fieldType: 'HPFM.CUST.FIELD_TYPE',
      fieldCategory: 'HPFM.CUST.FIELD_CATEGORY',
      fieldComponent: 'HPFM.CUST.FIELD_COMPONENT',
      modelRelation: 'HPFM.CUST.MODEL_RELATION',
      dateFormat: 'HPFM.CUST.DATE_FORMAT',
    }).then((res) => {
      if (res) {
        this.setState({
          fieldTypeOptions: res.fieldType || [],
          fieldCategoryOptions: res.fieldCategory || [],
          fieldComponentOptions: res.fieldComponent || [],
          modelRelationOptions: res.modelRelation || [],
          dateFormatOptions: res.dateFormat || [],
        });
      }
    });
  }

  @Bind()
  fetchModel(params = {}) {
    this.props
      .dispatch({
        type: 'flexModel/queryModelDetail',
        params,
      })
      .then((res) => {
        if (res) {
          this.setState({
            modelInfo: res || {},
          });
        }
      });
  }

  @Bind()
  fetchList(params = {}) {
    this.props
      .dispatch({
        type: 'flexModel/queryFieldsList',
        params,
      })
      .then((res) => {
        if (res) {
          this.setState({
            fieldList: res.dataSource,
          });
        }
      });
  }

  @Bind()
  fetchRelationList(params = {}) {
    this.props
      .dispatch({
        type: 'flexModel/queryModelRelationList',
        params,
      })
      .then((res) => {
        if (res) {
          this.setState({
            relationList: res.dataSource,
          });
        }
      });
  }

  @Bind()
  changeActiveTabKey(activeTabKey) {
    this.setState({ activeTabKey });
  }

  @Bind()
  handleSaveModel(params = {}) {
    return this.props.dispatch({
      type: 'flexModel/updateModel',
      params,
    });
  }

  @Bind()
  addField() {
    this.setState({ addFieldModleVisible: true });
  }

  @Bind()
  removeField(fieldId = '') {
    const {
      dispatch,
      match: {
        params: { modelId },
      },
    } = this.props;
    dispatch({
      type: 'flexModel/removeField',
      params: { fieldId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchList({ modelId });
      }
    });
  }

  @Bind()
  removeRelation(relationId = '') {
    const {
      dispatch,
      match: {
        params: { modelId },
      },
    } = this.props;
    dispatch({
      type: 'flexModel/deleteRelation',
      params: { relationId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchRelationList({ modelId });
      }
    });
  }

  @Bind()
  handleAddField(params) {
    const {
      dispatch,
      match: {
        params: { modelId },
      },
    } = this.props;
    dispatch({
      type: 'flexModel/addField',
      params: {
        ...params,
        modelId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchList({ modelId });
        this.setState({ addFieldModleVisible: false });
      }
    });
  }

  @Bind()
  init() {
    const { modelInfo = {} } = this.state;
    const { serviceName, modelTable, modelId } = modelInfo;
    this.props
      .dispatch({
        type: 'flexModel/initFields',
        params: {
          serviceName,
          modelId,
          tableName: modelTable,
        },
      })
      .then((res) => {
        if (res) {
          this.setState({ fieldList: res.dataSource });
          notification.success();
        }
      });
  }

  @Bind()
  hideModal() {
    this.setState({ addFieldModleVisible: false });
  }

  @Bind()
  handleEditField() {
    const {
      match: {
        params: { modelId },
      },
    } = this.props;
    this.fetchList({ modelId });
  }

  @Bind()
  handleMenuClick({ key }) {
    this.setState({ relationModelVisible: true, relation: key, relationModelData: {} });
  }

  @Bind()
  openRelationModal() {
    this.setState({ relationModelVisible: true, relationModelData: {} });
  }

  @Bind()
  closeRelationModal() {
    this.setState({ relationModelVisible: false });
  }

  @Bind()
  handleEditRelation(relationId = '') {
    const { relationList = [] } = this.state;
    const data = relationList.find((item) => item.id == relationId) || {};
    this.setState({ relationModelVisible: true, relationModelData: data });
  }

  render() {
    const {
      modelInfo = {},
      activeTabKey,
      addFieldModleVisible,
      relationModelVisible,
      fieldList,
      relation,
      relationList,
      relationModelData,
      fieldTypeOptions = [],
      fieldCategoryOptions = [],
      fieldComponentOptions = [],
      modelRelationOptions = [],
      dateFormatOptions = [],
    } = this.state;
    const {
      queryFieldsListLoading,
      refreshFieldsLoading,
      addFieldLoading,
      removeFieldLoading,
      match: {
        params: { modelId },
      },
    } = this.props;
    const menu = (
      <Menu onClick={this.handleMenuClick}>
        {modelRelationOptions.map((item) => (
          <Menu.Item key={item.value}>
            <Icon type="plus" />
            {item.meaning}
          </Menu.Item>
        ))}
      </Menu>
    );
    return (
      <Fragment>
        <Header
          title={intl
            .get('hpfm.flexModelDetail.view.message.title.flexModelDetail')
            .d('数据模型详情')}
          backPath="/hpfm/ui-customize/model/list"
        >
          {activeTabKey === 'fields' ? (
            <>
              <Button type="primary" onClick={this.addField} disabled>
                {intl.get('hpfm.flexModelDetail.view.message.button.addField').d('新建字段')}
              </Button>
              <Button onClick={this.init}>
                {intl.get('hpfm.flexModelDetail.view.message.button.syncField').d('同步表字段')}
              </Button>
            </>
          ) : (
            <>
              <Dropdown overlay={menu}>
                <Button>
                  <Icon type="plus" />
                  {intl
                    .get('hpfm.flexModelDetail.view.message.button.createRelation')
                    .d('新建关系')}
                  <Icon type="down" />
                </Button>
              </Dropdown>
            </>
          )}
        </Header>
        <Content>
          <ModelInfoForm
            modelInfo={modelInfo}
            onSaveModalName={this.handleSaveModel}
            fetchModel={this.fetchModel}
          />
          <Tabs defaultActiveKey={activeTabKey} animated={false} onChange={this.changeActiveTabKey}>
            <Tabs.TabPane
              tab={intl.get('hpfm.flexModelDetail.view.message.tab.fields').d('字段')}
              key="fields"
            >
              <Field
                primaryKey={modelInfo.primaryKey}
                handleEdit={this.handleEditField}
                dataSource={fieldList}
                handleRemoveField={this.removeField}
                queryFieldsListLoading={queryFieldsListLoading}
                refreshFieldsLoading={refreshFieldsLoading}
                removeFieldLoading={removeFieldLoading}
                fieldTypeOptions={fieldTypeOptions}
                fieldCategoryOptions={fieldCategoryOptions}
                fieldComponentOptions={fieldComponentOptions}
                dateFormatOptions={dateFormatOptions}
              />
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={intl.get('hpfm.flexModelDetail.view.message.tab.relatedModel').d('关联模型')}
              key="relation"
            >
              <Relation
                dataSource={relationList}
                modelId={modelId}
                handleRemoveRelation={this.removeRelation}
                handleEdit={this.handleEditRelation}
              />
            </Tabs.TabPane>
          </Tabs>
        </Content>
        <FieldModal
          visible={addFieldModleVisible}
          handleAddField={this.handleAddField}
          hideModal={this.hideModal}
          addFieldLoading={addFieldLoading}
        />
        <RelationModal
          modelInfo={modelInfo}
          fieldList={fieldList}
          modelRelationOptions={modelRelationOptions}
          relation={relation}
          data={relationModelData}
          visible={relationModelVisible}
          handleClose={this.closeRelationModal}
          fetchRelationList={this.fetchRelationList}
        />
      </Fragment>
    );
  }
}
