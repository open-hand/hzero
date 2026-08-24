/**
 * 分配安全组 - 侧滑
 * @date: 2019-11-15
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Modal, Table, Form, Row, Col, Button, Tabs } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { SEARCH_FORM_ROW_LAYOUT } from 'utils/constants';

import SearchForm from './SearchForm';
import AddDataModal from './AddSecGrpModal';
import FieldPermissionTab from './Permissions/FieldPermissionTab';
import DimensionTab from './Permissions/DimensionTab';
import DataPermissionTab from './Permissions/DataPermissionTab';
import styles from './index.less';

const { TabPane } = Tabs;

function getRefFieldsValue(ref) {
  if (ref.current) {
    return ref.current.props.form.getFieldsValue();
  }
  return {};
}
@Form.create({ fieldNameProp: null })
export default class AssignSecGrp extends React.Component {
  constructor(props) {
    super(props);
    this.oneSearchFormRef = React.createRef();
    this.state = this.getInitialState();
  }

  getInitialState() {
    return {
      selectedRowKeys: [], // 选中行key集合
      selectedRows: {}, // 选中行集合
      addModalVisible: false,
      currentSecGrp: {}, // 点击的安全组
      activeKey: 'field', // 选中的tab
      visitPermissionList: [],
      defaultExpandedRowKeys: [],
    };
  }

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onFormSearch } = this.props;
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    onFormSearch(fieldsValue);
  }

  /**
   * 安全组分页切换
   * @param {*} pagination
   */
  @Bind()
  onTableChange(pagination = {}) {
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    const { onSecGrpPageChange } = this.props;
    onSecGrpPageChange(pagination, fieldsValue);
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.addModalRef = ref;
  }

  /**
   * 显示新增弹窗
   */
  @Bind()
  handleShowAddModal() {
    this.fetchModalData();
    this.setState({
      addModalVisible: true,
    });
  }

  /**
   * 隐藏新建弹窗
   */
  @Bind()
  handleCloseModal() {
    this.addModalRef.state.addRows = [];
    this.setState({
      addModalVisible: false,
    });
  }

  /**
   * 查询弹窗数据
   * @param {Object} queryData - 查询数据
   */
  @Bind()
  fetchModalData(queryData = {}) {
    const { fetchAssignableSecGrp = () => {} } = this.props;
    fetchAssignableSecGrp(queryData);
  }

  /**
   * 角色分配安全组
   * @param {array} addData - 选中的数据
   */
  @Bind()
  handleAssignSecGrp(addData) {
    const { onAssignSecGrp = () => {} } = this.props;
    onAssignSecGrp(addData, this.handleCloseModal);
  }

  /**
   * 获取选中行
   * @param {array} selectedRowKeys 选中行的key值集合
   * @param {object} selectedRows 选中行集合
   */
  @Bind()
  handleRowSelectChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRowKeys,
      selectedRows,
    });
  }

  /**
   * 删除安全组
   */
  @Bind()
  handleDeleteLine() {
    const { onDelete = () => {}, deleteLoading = false, secGrpAddModalPagination } = this.props;
    if (deleteLoading) return;
    const { selectedRows } = this.state;
    const that = this;
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk() {
        onDelete(selectedRows).then(response => {
          if (response) {
            notification.success();
            that.setState(
              {
                selectedRowKeys: [],
                selectedRows: {},
                currentSecGrp: {},
              },
              () => {
                that.onTableChange(secGrpAddModalPagination);
              }
            );
          }
        });
      },
    });
  }

  /**
   * 点击行
   * @param {object} record - 当前行数据
   */
  @Bind()
  onRow(record) {
    const { onResetPermissions = () => {} } = this.props;
    const { currentSecGrp = {} } = this.state;
    // 重复点击同一行 不触发操作
    if (!isEmpty(currentSecGrp) && currentSecGrp.secGrpId === record.secGrpId) return;
    onResetPermissions();
    this.setState({ ...this.getInitialState(), currentSecGrp: record }, () => {
      this.handleChangePermissionType('field');
    });
  }

  /**
   * 切换tab
   * @param {string} activeKey - 选中的tab
   */
  @Bind()
  handleChangePermissionType(activeKey) {
    this.setState({ activeKey });
    switch (activeKey) {
      case 'field':
        this.fetchFieldPermission();
        break;
      case 'dimension':
        this.fetchDataDimension();
        break;
      case 'data':
        this.fetchDataPermission();
        break;
      default:
        break;
    }
  }

  /**
   * 查询字段权限
   */
  @Bind()
  fetchFieldPermission() {
    const {
      currentSecGrp: { secGrpId },
    } = this.state;
    const { onFetchFieldPermission = e => e } = this.props;
    onFetchFieldPermission(secGrpId);
  }

  /**
   * 查询字段权限配置
   * @param {number} permissionId - 接口id
   */
  @Bind()
  fetchFieldConfigList(permissionId) {
    const { onFetchFieldConfigList = e => e } = this.props;
    const {
      currentSecGrp: { secGrpId },
    } = this.state;
    return onFetchFieldConfigList({ permissionId, secGrpId });
  }

  /**
   * 查询数据权限维度
   */
  @Bind()
  fetchDataDimension() {
    const {
      currentSecGrp: { secGrpId },
    } = this.state;
    const { onFetchDataDimension = e => e } = this.props;
    onFetchDataDimension(secGrpId);
  }

  /**
   * 查询数据权限左侧tab
   */
  @Bind()
  fetchDataPermission() {
    const {
      currentSecGrp: { secGrpId },
    } = this.state;
    const { onFetchDataPermission = e => e } = this.props;
    onFetchDataPermission(secGrpId);
  }

  /**
   * 关闭侧滑
   */
  @Bind()
  handleClose() {
    const { onCancel = e => e } = this.props;
    onCancel(this.resetData);
  }

  /**
   * 清空数据
   */
  @Bind()
  resetData() {
    const initailState = this.getInitialState();
    this.setState({ ...initailState });
  }

  /**
   * 高亮显示选中行
   * @param {object} record - 行数据
   */
  @Bind()
  addHighlight(record) {
    const { currentSecGrp } = this.state;
    return record.secGrpId === currentSecGrp.secGrpId ? styles['auth-row-hover'] : '';
  }

  render() {
    const {
      visible = false,
      roleId,
      path,
      tenantId,
      queryLoading,
      queryModalLoading,
      saveModalLoading,
      deleteLoading,
      secGrpList,
      secGrpPagination,
      secGrpAddModalList,
      secGrpAddModalPagination,
      queryFieldPermissionLoading,
      secGrpFieldPermissionList,
      secGrpFieldPermissionPagination,
      queryFieldConfigLoading,
      secGrpDimensionList,
      secGrpDimensionPagination,
      secGrpDataPermissionTabList = [], // 安全组数据权限tab页
      queryDataDimensionLoading,
      queryDataPermissionTabLoading,
    } = this.props;
    const { addModalVisible, selectedRowKeys, currentSecGrp, activeKey } = this.state;
    const searchFormProps = {
      onSearch: this.handleSearch,
      wrappedComponentRef: this.oneSearchFormRef,
    };

    const commonColumns = [
      {
        title: intl.get('hiam.subAccount.model.subAccount.secGrpName').d('安全组名称'),
        dataIndex: 'secGrpName',
      },
      {
        title: intl.get('hiam.subAccount.model.subAccount.secGrpCode').d('安全组代码'),
        dataIndex: 'secGrpCode',
        width: '30%',
      },
    ];
    const addModalOptions = {
      columns: [
        ...commonColumns,
        {
          title: intl.get('hiam.subAccount.model.subAccount.secGrpSource').d('安全组来源'),
          dataIndex: 'createRoleName',
          width: '30%',
        },
      ],
      confirmLoading: saveModalLoading,
      loading: queryModalLoading,
      title: intl.get('hiam.subAccount.view.title.secGrp.choose').d('选择安全组'),
      rowKey: 'secGrpId',
      queryName: 'secGrpName',
      queryCode: 'secGrpCode',
      queryNameDesc: intl.get('hiam.subAccount.model.subAccount.secGrpName').d('安全组名称'),
      queryCodeDesc: intl.get('hiam.subAccount.model.subAccount.secGrpCode').d('安全组代码'),
      dataSource: secGrpAddModalList,
      pagination: secGrpAddModalPagination,
      modalVisible: addModalVisible,
      addData: this.handleAssignSecGrp,
      onHideAddModal: this.handleCloseModal,
      fetchModalData: this.fetchModalData,
      onRef: this.handleBindRef,
    };
    const rowSelection = {
      selectedRowKeys,
      onChange: this.handleRowSelectChange,
    };
    const tableProps = {
      loading: queryLoading,
      dataSource: secGrpList,
      columns: [
        ...commonColumns,
        {
          title: intl.get('hiam.subAccount.model.subAccount.secGrpSource').d('安全组来源'),
          dataIndex: 'createRoleName',
          width: '30%',
        },
      ],
      rowKey: 'secGrpId',
      bordered: true,
      pagination: secGrpPagination,
      rowSelection,
      onChange: this.onTableChange,
      rowClassName: this.addHighlight,
      onRow: record => ({
        onClick: () => this.onRow(record),
      }),
    };

    const fieldPermissionProps = {
      dataSource: secGrpFieldPermissionList,
      pagination: secGrpFieldPermissionPagination,
      loading: queryFieldPermissionLoading,
      onFetchFieldConfigList: this.fetchFieldConfigList,
      queryFieldConfigLoading,
    };

    const dimensionProps = {
      loading: queryDataDimensionLoading,
      dataSource: secGrpDimensionList,
      pagination: secGrpDimensionPagination,
    };
    const dataPermissionProps = {
      allTabList: secGrpDataPermissionTabList,
      roleId,
      tenantId,
      path,
      secGrpId: currentSecGrp.secGrpId,
      queryDataPermissionTabLoading,
    };
    return (
      <Modal
        width={1200}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        destroyOnClose
        visible={visible}
        title={intl.get('hiam.subAccount.view.button.secGrp').d('分配安全组')}
        onCancel={this.handleClose}
        footer={
          <Button type="primary" onClick={this.handleClose}>
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        }
      >
        <Row>
          <Col span={8}>
            <SearchForm {...searchFormProps} />
            <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ marginTop: '14px', marginBottom: '8px' }}>
              <Col span={12} offset={14}>
                <Button
                  onClick={this.handleDeleteLine}
                  style={{ marginRight: 8 }}
                  loading={deleteLoading}
                  disabled={isEmpty(rowSelection.selectedRowKeys)}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </Button>
                <Button onClick={this.handleShowAddModal}>
                  {intl.get('hzero.common.button.add').d('新增')}
                </Button>
              </Col>
            </Row>
            <Table {...tableProps} />
          </Col>
          <Col span={16} style={{ marginTop: '87px', paddingLeft: '24px' }}>
            <div className={styles['secGrp-detail-container']}>
              <div className={styles['secGrp-detail-container-title']}>
                {intl.get('hiam.subAccount.view.title.secGrp.detail').d('安全组权限详情')}
              </div>
              {!isEmpty(currentSecGrp) && (
                <Tabs
                  tabBarGutter={10}
                  defaultActiveKey="field"
                  animated={false}
                  onChange={this.handleChangePermissionType}
                  activeKey={activeKey}
                >
                  <TabPane
                    tab={intl
                      .get('hiam.securityGroup.view.title.tab.field.permission')
                      .d('字段权限')}
                    key="field"
                  >
                    <FieldPermissionTab {...fieldPermissionProps} />
                  </TabPane>
                  <TabPane
                    tab={intl.get('hiam.securityGroup.view.title.tab.dimension').d('数据权限维度')}
                    key="dimension"
                  >
                    <DimensionTab {...dimensionProps} />
                  </TabPane>
                  <TabPane
                    tab={intl
                      .get('hiam.securityGroup.view.title.tab.data.permission')
                      .d('数据权限')}
                    key="data"
                  >
                    <DataPermissionTab {...dataPermissionProps} />
                  </TabPane>
                </Tabs>
              )}
              {isEmpty(currentSecGrp) && (
                <h3 style={{ color: 'gray', margin: '5% 0', textAlign: 'center' }}>
                  {intl.get('hiam.securityGroup.view.title.secGrp.empty').d('暂无数据')}
                </h3>
              )}
            </div>
          </Col>
        </Row>
        <AddDataModal {...addModalOptions} />
      </Modal>
    );
  }
}
