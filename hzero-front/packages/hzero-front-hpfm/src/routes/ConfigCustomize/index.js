/* eslint-disable eqeqeq */
/* eslint-disable react/jsx-props-no-spreading */
/* eslint-disable react/state-in-constructor */
import React, { Component } from 'react';
import {
  Button,
  Tree,
  Row,
  Col,
  Form,
  Spin,
  InputNumber,
  Table,
  Icon,
  Popconfirm,
  Dropdown,
  Menu,
  Tooltip,
  Badge,
} from 'hzero-ui';
import { isEmpty, isNil } from 'lodash';
import { Bind } from 'lodash-decorators';
import { Header } from 'components/Page';
import intl from 'utils/intl';
// import { yesOrNoRender } from 'utils/renderer';
import { connect } from 'dva';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  getFieldNameAlias,
  getFieldConfigAlias,
  getDefaultActiveAlias,
} from '@/utils/constConfig.js';
import { yesOrNoRender } from 'utils/renderer';
import ConfigModal from './ConfigModal';
import CopyFieldModal from './CopyFieldModal';
import styles from './index.less';

const FormItem = Form.Item;
const { TreeNode } = Tree;
// const { Search } = Input;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};
function getStatus(status) {
  if (status === -1 || status === undefined) return <span>fx</span>;
  if (status === 1) return <Icon type="check" />;
  return null;
}
@connect(({ configCustomize, loading }) => {
  const {
    treeData,
    unitGroup,
    lineData,
    moduleList,
    codes,
    currentUnit,
    unitAlias,
  } = configCustomize;
  return {
    currentUnit,
    treeData,
    unitGroup,
    lineData,
    moduleList,
    codes,
    unitAlias,
    loadTree: loading.effects['configCustomize/queryTree'],
    loadingGroup: loading.effects['configCustomize/queryGroup'],
    loadingCurrentUnit: loading.effects['configCustomize/queryUnitDetails'],
  };
})
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hpfm.individual', 'hpfm.customize', 'hpfm.individuationUnit'] })
export default class configCustomize extends Component {
  state = {
    classifyCode: '',
    currentGroup: {},
    currentRecord: { field: {}, widget: {} },
    visible: false,
    copyFieldModalVisible: false,
    expandedKeys: [],
    unitList: [],
    fieldList: {},
    selectedRows: [],
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'configCustomize/queryTree',
    })
      .then(this.parseTreeData)
      .then((originTreeData) => {
        this.setState({ originTreeData });
      });
    dispatch({
      type: 'configCustomize/queryCodes',
      payload: {
        whereOptions: 'HPFM.CUST.FIELD_QUERY_REALTION',
        renderOptions: 'HPFM.CUST.RENDER_OPTIONS',
        dateFormat: 'HPFM.CUST.DATE_FORMAT',
        fieldWidget: 'HPFM.CUST.FIELD_COMPONENT',
        custType: 'HPFM.CUST.FIELD_CUST_TYPE',
        fieldType: 'HPFM.CUST.FIELD_TYPE',
        fixed: 'HPFM.CUST.GIRD.FIXED',
        unitType: 'HPFM.CUST.UNIT_TYPE',
        relationShip: 'HPFM.CUST.FIELD_COND_REALTION',
        condOptions: 'HPFM.CUST.UNIT_COND_OPTIONS',
      },
    });
  }

  @Bind()
  parseTreeData(data) {
    let result = [];
    data.forEach((item) => {
      result.push(item);
      if (item.subMenus) {
        result = [...result, ...this.parseTreeData(item.subMenus)];
      }
    });
    return result;
  }

  queryRelatedUnits(id) {
    const { dispatch } = this.props;
    dispatch({
      type: 'configCustomize/queryRelatedUnits',
      payload: { unitId: id },
    }).then((res) => {
      if (!isEmpty(res)) {
        const unitList = res || [];
        const fieldList = {};
        unitList.forEach((i) => {
          fieldList[i.unitId] = i.unitFields || [];
        });
        this.setState({ unitList, fieldList });
      } else {
        this.setState({ unitList: [], fieldList: {} });
      }
    });
  }

  renderTreeNodes = (data) =>
    data.map((item) => {
      const { menuCode, menuName, subMenus, ...rest } = item;
      if (item.subMenus) {
        return (
          <TreeNode title={menuName} key={menuCode} dataRef={item}>
            {this.renderTreeNodes(subMenus)}
          </TreeNode>
        );
      }
      return <TreeNode {...rest} title={menuName} key={menuCode} dataRef={item} />;
    });

  @Bind()
  toggleConfigModal(record, type) {
    const { visible } = this.state;
    this.setState({
      modalType: type,
      visible: !visible,
      currentRecord: record,
    });
  }

  @Bind()
  renderColumns() {
    const {
      currentUnit: { unitType },
      codes: { fixedObj, fieldWidgetObj, custTypeObj },
    } = this.props;
    const pureVirtual = unitType === 'TABPANE' || unitType === 'COLLAPSE';
    const isFormType = unitType === 'FORM' || unitType === 'QUERYFORM';
    return [
      {
        title: getFieldNameAlias(unitType),
        dataIndex: 'fieldName',
        render: (val, record) => (
          <div className={styles['table-extra']}>
            <Dropdown
              overlay={
                <Menu>
                  <Menu.Item key="0" onClick={() => this.toggleConfigModal(record)}>
                    <Icon type="edit" />
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </Menu.Item>
                  {!isNil(record.configFieldId) && record.custType === 'STD' && (
                    <Menu.Item key="2">
                      <Popconfirm
                        title={intl.get('hzero.common.button.reset').d('重置')}
                        onConfirm={() => this.deleteFieldIndividual(record)}
                        okText={intl.get('hzero.common.status.yes').d('是')}
                        cancelText={intl.get('hzero.common.status.no').d('否')}
                      >
                        <Icon type="reload" style={{ marginRight: '8px' }} />
                        {intl.get('hzero.common.button.reset').d('重置')}
                      </Popconfirm>
                    </Menu.Item>
                  )}
                  {!isNil(record.configFieldId) && record.custType !== 'STD' && (
                    <Menu.Item key="2">
                      <Popconfirm
                        title={intl
                          .get('hzero.common.message.confirm.delete')
                          .d('是否删除此条记录')}
                        onConfirm={() => this.deleteFieldIndividual(record)}
                        okText={intl.get('hzero.common.status.yes').d('是')}
                        cancelText={intl.get('hzero.common.status.no').d('否')}
                      >
                        <Icon type="delete" style={{ marginRight: '8px' }} />
                        {intl.get('hzero.common.button.delete').d('删除')}
                      </Popconfirm>
                    </Menu.Item>
                  )}
                </Menu>
              }
              trigger={['click']}
            >
              <div className="operator">.&nbsp;.&nbsp;.</div>
            </Dropdown>
            <div
              style={{ color: '#666', fontWeight: '600', display: 'flex', alignItems: 'center' }}
            >
              {val}
              <Badge style={{ marginLeft: '8px' }} dot={!isNil(record.configFieldId)} />
            </div>
            <div style={{ color: '#a5a5a5' }}>{record.fieldCodeAlias}</div>
          </div>
        ),
      },
      pureVirtual && {
        title: getDefaultActiveAlias(unitType),
        dataIndex: 'defaultActive',
        width: 100,
        render: yesOrNoRender,
      },
      !pureVirtual && {
        title: intl.get('hpfm.individual.model.config.custType').d('类型'),
        dataIndex: 'custType',
        width: 100,
        render: (val) => custTypeObj[val],
      },
      !pureVirtual && {
        title: intl.get('hpfm.individual.model.config.modelCategory').d('所属模型'),
        dataIndex: 'field.modelName',
        width: 150,
      },
      !pureVirtual && {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.bindField').d('字段绑定'),
        dataIndex: 'bindField',
      },
      {
        title: intl.get('hpfm.individual.model.config.visible').d('显示'),
        dataIndex: 'visible',
        width: 60,
        render: getStatus,
      },
      !pureVirtual && {
        title: intl.get('hzero.common.button.editable').d('编辑'),
        dataIndex: 'fieldEditable',
        width: 60,
        render: getStatus,
      },
      !pureVirtual && {
        title: intl.get('hzero.common.title.individuation.required1').d('必输'),
        dataIndex: 'fieldRequired',
        width: 60,
        render: getStatus,
      },
      isFormType && {
        title: intl.get('hpfm.individual.model.config.row').d('行'),
        width: 60,
        dataIndex: 'formRow',
      },
      isFormType && {
        title: intl.get('hpfm.individual.model.config.col').d('列'),
        width: 60,
        dataIndex: 'formCol',
      },
      !isFormType && {
        title: intl.get('hpfm.individual.model.config.position').d('位置'),
        width: 60,
        dataIndex: 'gridSeq',
      },
      unitType === 'GRID' && {
        title: intl.get('hpfm.individual.model.config.gridWidth').d('宽度'),
        width: 90,
        dataIndex: 'gridWidth',
      },
      unitType === 'GRID' && {
        title: intl.get('hpfm.individual.model.config.gridFixed').d('冻结'),
        width: 90,
        dataIndex: 'gridFixed',
        render: (val) => fixedObj[val],
      },
      !pureVirtual && {
        title: intl.get('hpfm.individual.model.config.componentType').d('组件类型'),
        dataIndex: 'widget.fieldWidget',
        width: 150,
        render: (val) => fieldWidgetObj[val],
      },
    ].filter(Boolean);
  }

  @Bind()
  onSelect(key, { node }) {
    if (node.isLeaf()) {
      this.setState({
        selectedRows: [], // 切换菜单后重置已勾选字段
        classifyCode: key,
      });
      const { dispatch } = this.props;
      dispatch({
        type: 'configCustomize/queryGroup',
        payload: { menuCode: node.props.eventKey },
      }).then((res) => {
        if (!isEmpty(res)) {
          const currentGroup = res[0] || { units: [] };
          this.setState({ currentGroup });
          if (!currentGroup.units) return;
          const { id } = currentGroup.units[0] || {};
          this.queryUnitDetails(id);
        }
      });
    } else {
      let { expandedKeys } = this.state;
      if (key.length === 0) {
        expandedKeys = expandedKeys.filter((i) => !i.startsWith(node.props.eventKey));
      } else {
        expandedKeys = expandedKeys.concat(key);
      }
      this.setState({ expandedKeys });
    }
  }

  @Bind()
  onExpand(keys) {
    this.setState({ expandedKeys: keys });
  }

  @Bind()
  clickUnit(e) {
    if (e.target && e.target.id) {
      // 切换单元时重置table seleted
      if (this.props.currentUnit && this.props.currentUnit.id !== e.target.id) {
        this.setState({ selectedRows: [] });
      }
      this.queryUnitDetails(e.target.id);
    }
  }

  @Bind()
  clickGroup(e) {
    const { unitGroup } = this.props;
    if (e.target && e.target.id) {
      // 切换单元组时重置table seleted
      if (this.state.currentGroup && this.state.currentGroup.unitGroupId !== e.target.id) {
        this.setState({ selectedRows: [] });
      }
      // eslint-disable-next-line eqeqeq
      const currentGroup = unitGroup.find((i) => i.unitGroupId == e.target.id) || { units: [] };
      this.setState({ currentGroup });
      if (!currentGroup.units) return;
      this.queryUnitDetails((currentGroup.units[0] || {}).id);
    }
  }

  @Bind()
  queryUnitDetails(unitId) {
    const { dispatch } = this.props;
    dispatch({
      type: 'configCustomize/queryUnitDetails',
      payload: { unitId },
    });
    this.queryRelatedUnits(unitId);
  }

  @Bind()
  deleteFieldIndividual(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'configCustomize/deleteFieldIndividual',
      payload: { configFieldId: record.configFieldId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.queryUnitDetails(this.props.currentUnit.id);
      }
    });
  }

  @Bind()
  handleSelectRows(_, selectedRows) {
    this.setState({ selectedRows });
  }

  @Bind()
  handleToggleCopyFieldModal() {
    this.setState({ copyFieldModalVisible: !this.state.copyFieldModalVisible });
  }

  @Bind()
  handleSaveUnitConfig() {
    const { form, dispatch, currentUnit = {} } = this.props;
    const { id, config = {} } = currentUnit;
    const showLines = form.getFieldValue('showLines');
    dispatch({
      type: 'configCustomize/saveUnitConfigHeader',
      payload: {
        unitId: id,
        id: config.id,
        showLines,
      },
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  renderContent() {
    const { classifyCode, currentGroup, selectedRows } = this.state;
    const {
      form,
      lineData,
      unitGroup = [],
      loadingGroup,
      loadingCurrentUnit,
      codes: { unitTypeObj },
    } = this.props;
    const noUnits = isEmpty(currentGroup.units);
    const currentUnit = noUnits ? {} : this.props.currentUnit;
    const pureVirtual = currentUnit.unitType === 'TABPANE' || currentUnit.unitType === 'COLLAPSE';
    const isFormType = currentUnit.unitType === 'FORM' || currentUnit.unitType === 'QUERYFORM';
    if (!classifyCode) {
      return (
        <div className={styles['blank-area']}>
          <div className="blank-pic" />
          <div className="blank-desc">
            {intl
              .get('hpfm.individual.view.message.title.tips1')
              .d('请从左侧个性化目录中选择分类!')}
          </div>
          <div className="blank-desc-supply">
            {intl
              .get('hpfm.individual.view.message.title.tips2')
              .d('个性化目录与系统菜单相对应，可根据需要配置对应菜单下的个性化单元')}
          </div>
        </div>
      );
    }
    return (
      <div className="right-container">
        <div className="right-box">
          <div style={{ display: 'flex', justifyContent: 'space-between', margin: '10px 0' }}>
            <div className={styles.title}>
              {intl.get('hpfm.individual.model.config.unitList').d('单元列表')}
            </div>
          </div>
          <Spin spinning={loadingGroup}>
            <div className={styles['units-list']}>
              <FormItem label={intl.get('hpfm.individual.model.config.unitGroup').d('单元分组')}>
                <div className="units-container" onClick={this.clickGroup}>
                  {unitGroup.map((i) => (
                    <div
                      id={i.unitGroupId}
                      className={`unit-card ${
                        currentGroup.unitGroupId == i.unitGroupId ? 'active' : ''
                      }`}
                    >
                      <div id={i.unitGroupId} className="icon-unit" />
                      <div id={i.unitGroupId} className="content-unit">
                        {i.groupName}
                      </div>
                    </div>
                  ))}
                </div>
              </FormItem>
              <FormItem
                label={intl.get('hpfm.individual.model.config.customizeUnit').d('个性化单元')}
              >
                <div className="units-container" onClick={this.clickUnit}>
                  {!noUnits &&
                    currentGroup.units.map((i) =>
                      i.enableFlag === 0 ? (
                        <Tooltip
                          title={intl.get('hpfm.individual.model.config.lockUnit').d('单元已禁用')}
                        >
                          <div
                            id={i.id}
                            className={`unit-card ${
                              currentUnit.id == i.id ? 'active' : ''
                            } disabled`}
                          >
                            <div id={i.id} className="icon-unit" />
                            <div id={i.id} className="content-unit">
                              {i.unitName}
                            </div>
                          </div>
                        </Tooltip>
                      ) : (
                        <div
                          id={i.id}
                          className={`unit-card ${currentUnit.id == i.id ? 'active' : ''}`}
                        >
                          <div id={i.id} className="icon-unit" />
                          <div id={i.id} className="content-unit">
                            {i.unitName}
                          </div>
                        </div>
                      )
                    )}
                </div>
              </FormItem>
            </div>
          </Spin>
        </div>
        {!loadingCurrentUnit && !loadingGroup && currentUnit.enableFlag !== 0 ? (
          <Spin spinning={loadingGroup}>
            <div className="right-box">
              <div style={{ display: 'flex', justifyContent: 'space-between', margin: '10px 0' }}>
                <div className={styles.title}>
                  {intl.get('hpfm.individual.view.message.title.unitConfig').d('单元配置')}
                </div>
                {currentUnit.unitType === 'FORM' && (
                  <Button type="primary" onClick={this.handleSaveUnitConfig}>
                    {intl.get('hzero.common.button.save').d('保存')}
                  </Button>
                )}
              </div>
              {/* <section className="table-container">
                  <div className="table-row">
                    <div className="table-column-group">
                      <div className="table-cell-label">
                        ceshi
                      </div>
                      <div className="table-cell-value">
                        ceshi
                      </div>
                    </div>
                  </div>
                </section> */}
              <Row gutter={48} className="read-row">
                <Col span={8}>
                  <FormItem
                    label={intl.get('hpfm.individual.model.config.unitType').d('单元类型')}
                    {...formLayout}
                  >
                    {unitTypeObj[currentUnit.unitType]}
                  </FormItem>
                </Col>
                <Col span={8}>
                  <FormItem
                    label={intl.get('hpfm.individual.model.config.individualCode').d('个性化编码')}
                    {...formLayout}
                  >
                    {currentUnit.unitCode}
                  </FormItem>
                </Col>
                {/* <Col span={8}>
                  <FormItem
                    label={intl.get('hpfm.individual.model.config.readOnly').d('只读')}
                    {...formLayout}
                  >
                    {yesOrNoRender(currentUnit.readOnly)}
                  </FormItem>
                </Col> */}
              </Row>
              {isFormType && (
                <>
                  <Row gutter={48} className="read-row">
                    <Col span={8}>
                      <FormItem
                        label={intl.get('hpfm.individual.model.config.labelCol').d('标签比例')}
                        {...formLayout}
                      >
                        {currentUnit.labelCol}
                      </FormItem>
                    </Col>
                    <Col span={8}>
                      <FormItem
                        label={intl.get('hpfm.individual.model.config.wrapperCol').d('组件比例')}
                        {...formLayout}
                      >
                        {currentUnit.wrapperCol}
                      </FormItem>
                    </Col>
                  </Row>
                  <Row gutter={48} className="read-row">
                    <Col span={8}>
                      <FormItem
                        label={intl.get('hpfm.individual.model.config.formColumns').d('表单列数')}
                        {...formLayout}
                      >
                        {currentUnit.formMaxCol}
                        {/* {form.getFieldDecorator('maxCol', {
                          initialValue: (currentUnit.config || {}).maxCol,
                        })(<InputNumber precision={0} min={1} style={{ width: '100%' }} />)} */}
                      </FormItem>
                    </Col>
                    {currentUnit.unitType === 'FORM' && (
                      <Col span={8}>
                        <FormItem
                          className={styles['input-number-item']}
                          label={
                            <>
                              {intl.get('hpfm.individual.model.config.showLines').d('显示行数')}
                              <Tooltip
                                title={intl
                                  .get('hpfm.individual.view.message.onlyUseForCollapseForm')
                                  .d('仅适用于折叠表单')}
                              >
                                <Icon
                                  type="info-circle"
                                  style={{ verticalAlign: 'middle', margin: '0 4px' }}
                                />
                              </Tooltip>
                            </>
                          }
                          {...formLayout}
                        >
                          {form.getFieldDecorator('showLines', {
                            initialValue: (currentUnit.config || {}).showLines,
                          })(<InputNumber precision={0} min={1} style={{ width: '100%' }} />)}
                        </FormItem>
                      </Col>
                    )}
                  </Row>
                </>
              )}
            </div>
            <div className="right-box">
              <div style={{ display: 'flex', justifyContent: 'space-between', margin: '10px 0' }}>
                <div className={styles.title}>{getFieldConfigAlias(currentUnit.unitType)}</div>
                <div>
                  <Button
                    onClick={this.handleToggleCopyFieldModal}
                    disabled={selectedRows.length < 1}
                    style={{ display: pureVirtual ? 'none' : 'inline-block', marginRight: '8px' }}
                  >
                    {intl.get('hpfm.individual.model.config.copyField').d('拷贝字段')}
                  </Button>
                  <Button
                    icon="plus"
                    type="primary"
                    onClick={() => this.toggleConfigModal({}, 'new')}
                    disabled={!currentUnit.id}
                    style={{ display: pureVirtual ? 'none' : 'inline-block' }}
                  >
                    {intl.get('hpfm.individual.model.config.addExtraField').d('添加扩展字段')}
                  </Button>
                </div>
              </div>
              <Table
                rowKey={'configFieldId'}
                bordered
                rowSelection={{
                  selectedRowKeys: selectedRows.map((n) => n.configFieldId),
                  onChange: this.handleSelectRows,
                  getCheckboxProps: (record) => ({
                    disabled: isNil(record.configFieldId),
                  }),
                }}
                pagination={false}
                columns={this.renderColumns()}
                dataSource={noUnits ? [] : lineData}
              />
            </div>
          </Spin>
        ) : (
          <div
            className="right-box"
            style={{
              display: 'flex',
              height: '120px',
              justifyContent: 'center',
              alignItems: 'center',
            }}
          >
            {loadingCurrentUnit || loadingGroup ? null : (
              <span style={{ fontSize: '18px' }}>
                <Icon type="lock" />
                {intl.get('hpfm.individual.model.config.lockUnit').d('单元已禁用')}
              </span>
            )}
          </div>
        )}
      </div>
    );
  }

  render() {
    const {
      visible,
      copyFieldModalVisible,
      currentRecord,
      modalType,
      expandedKeys,
      unitList,
      fieldList,
      selectedRows,
    } = this.state;
    const { loadTree = false, treeData, currentUnit } = this.props;
    return (
      <>
        <Header title={intl.get(`hpfm.individual.view.message.title.config`).d('个性化配置')} />
        <div className={styles['main-container']}>
          <div className={styles['wrap-container']}>
            <div className={styles.left}>
              <header
                style={{
                  textAlign: 'left',
                  fontWeight: '600',
                  paddingLeft: '12px',
                  background: '#fff',
                  lineHeight: '42px',
                  borderBottom: '1px solid #f5f5f5',
                }}
              >
                {intl.get(`hpfm.individual.view.message.title.category`).d('个性化目录')}
              </header>
              <Spin spinning={loadTree}>
                <div className="left-container">
                  <Tree
                    onSelect={this.onSelect}
                    expandedKeys={expandedKeys}
                    onExpand={this.onExpand}
                  >
                    {this.renderTreeNodes(treeData)}
                  </Tree>
                </div>
              </Spin>
            </div>
            <div className={styles.right}>{this.renderContent()}</div>
          </div>
        </div>
        <ConfigModal
          type={modalType}
          visible={visible}
          unitList={unitList}
          fieldList={fieldList}
          id={currentUnit.id}
          modelId={currentUnit.modelId}
          refreshLineData={this.queryUnitDetails}
          unitType={currentUnit.unitType}
          unitCode={currentUnit.unitCode}
          unitAlias={currentUnit.unitAlias}
          record={currentRecord}
          onClose={() => this.toggleConfigModal({})}
        />
        {copyFieldModalVisible && (
          <CopyFieldModal
            visible={copyFieldModalVisible}
            currentUnit={currentUnit}
            copyFields={selectedRows}
            handleClose={this.handleToggleCopyFieldModal}
          />
        )}
      </>
    );
  }
}
