/**
 * DataHierarchies 数据层级配置
 * @date: 2019-8-14
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';
import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import {
  tableScrollWidth,
  isTenantRoleLevel,
  getCurrentOrganizationId,
  getCurrentTenant,
} from 'utils/utils';

import FilterForm from './FilterForm';
import Drawer from './Drawer';

@connect(({ dataHierarchies, loading }) => ({
  dataHierarchies,
  tenantId: getCurrentOrganizationId(),
  tenantName: getCurrentTenant().tenantName,
  isSiteFlag: !isTenantRoleLevel(),
  fetchListLoading: loading.effects['dataHierarchies/fetchConfigList'],
  getConfigDetailLoading: loading.effects['dataHierarchies/getConfigDetail'],
  createConfigLoading: loading.effects['dataHierarchies/createConfig'],
  editConfigLoading: loading.effects['dataHierarchies/editConfig'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hpfm.dataHierarchies'],
})
export default class DataHierarchies extends React.Component {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
    this.state = {
      modalVisible: false,
      editFlag: false,
      addFlag: false,
      trueTenantId: props.isSiteFlag ? 0 : undefined,
    };
  }

  componentDidMount() {
    this.props.dispatch({
      type: 'dataHierarchies/init',
    });
    this.handleSearch();
  }

  /**
   * @function handleSearch - 获取单点登陆配置列表数据
   * @param {object} params - 查询参数
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch, form } = this.props;
    const fieldsValue = this.filterFormRef.current.getFieldsValue();
    const fieldValue = form.getFieldsValue();
    dispatch({
      type: 'dataHierarchies/fetchConfigList',
      payload: { ...fieldsValue, ...fieldValue, ...params },
    });
  }

  /**
   * @function handleChange - 改变租户值
   */
  @Bind()
  handleOk() {
    const { form } = this.props;
    const fieldsValue = form.getFieldsValue();
    this.setState({ trueTenantId: fieldsValue.tenantId });
    this.handleSearch();
  }

  /**
   * 模态窗确认
   */
  @Bind()
  handleConfirm(fieldValues) {
    const { dispatch } = this.props;
    const { editFlag, addFlag } = this.state;
    if (editFlag && !addFlag) {
      dispatch({
        type: 'dataHierarchies/editConfig',
        payload: fieldValues,
      }).then((res) => {
        if (res) {
          this.setState({ editFlag: false });
          notification.success();
          this.hideModal();
          this.handleSearch();
        }
      });
    } else {
      dispatch({
        type: 'dataHierarchies/createConfig',
        payload: fieldValues,
      }).then((res) => {
        if (res) {
          this.setState({ editFlag: false });
          notification.success();
          this.hideModal();
          this.handleSearch();
        }
      });
    }
  }

  /**
   * 点击'+',获取当前节点的下级节点
   * @param {Boolean} isExpand 展开标记
   * @param {Object} record  当前行
   */
  @Bind()
  handleExpandRow(isExpand, record) {
    const {
      dataHierarchies: { expandedRowKeys },
      dispatch,
    } = this.props;
    const rowKeys = isExpand
      ? [...expandedRowKeys, record.dataHierarchyId]
      : expandedRowKeys.filter((item) => item !== record.dataHierarchyId);
    dispatch({
      type: 'dataHierarchies/updateState',
      payload: {
        expandedRowKeys: [...rowKeys],
      },
    });
  }

  /**
   * 展开全部
   * 将页面展示的数据进行展开
   */
  @Bind()
  handleExpand() {
    const {
      dispatch,
      dataHierarchies: { pathMap = {} },
    } = this.props;
    dispatch({
      type: 'dataHierarchies/updateState',
      payload: {
        expandedRowKeys: Object.keys(pathMap).map((item) => item),
      },
    });
  }

  /**
   * 收起全部
   * 页面顶部收起全部按钮，将内容树收起
   */
  @Bind()
  handleShrink() {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataHierarchies/updateState',
      payload: { expandedRowKeys: [] },
    });
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}, editFlag, addFlag) {
    const { dispatch } = this.props;
    const { dataHierarchyId, dataHierarchyName, tenantId, enabledFlag, displayStyle } = record;
    this.setState({ editFlag, addFlag });
    if (editFlag && !addFlag) {
      dispatch({
        type: 'dataHierarchies/getConfigDetail',
        payload: { ...record },
      });
    } else if (addFlag) {
      dispatch({
        type: 'dataHierarchies/updateState',
        payload: {
          dataHierarchiesDetail: {
            parentId: dataHierarchyId,
            parentName: dataHierarchyName,
            tenantId,
            enabledFlag,
            displayStyle,
          },
        },
      });
    }
    this.handleModalVisible(true);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataHierarchies/updateState',
      payload: { dataHierarchiesDetail: {} },
    });
    this.handleModalVisible(false);
  }

  render() {
    const {
      fetchListLoading = false,
      getConfigDetailLoading = false,
      createConfigLoading = false,
      editConfigLoading = false,
      isSiteFlag,
      tenantName,
      tenantId,
      match,
      form,
      dataHierarchies: {
        renderTree = [],
        dataHierarchiesDetail = {},
        expandedRowKeys,
        displayList = [],
      },
    } = this.props;
    const { modalVisible, editFlag, addFlag, trueTenantId } = this.state;
    const columns = [
      {
        title: intl.get('hpfm.dataHierarchies.model.dataHierarchies.dataHierarchyCode').d('编码'),
        width: 240,
        dataIndex: 'dataHierarchyCode',
      },
      {
        title: intl.get('hpfm.dataHierarchies.model.dataHierarchies.dataHierarchyName').d('名称'),
        dataIndex: 'dataHierarchyName',
      },
      isSiteFlag && {
        title: intl.get('hpfm.dataHierarchies.model.dataHierarchies.tenantName').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hpfm.dataHierarchies.model.dataHierarchies.displayStyle').d('显示样式'),
        dataIndex: 'displayStyleMeaning',
        width: 200,
      },
      {
        title: intl.get('hpfm.dataHierarchies.model.dataHierarchies.valueSourceId').d('值来源'),
        dataIndex: 'valueSourceName',
        width: 200,
      },
      {
        title: intl.get('hpfm.dataHierarchies.model.dataHierarchies.orderSeq').d('排序'),
        dataIndex: 'orderSeq',
        width: 60,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 90,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 130,
        fixed: 'right',
        render: (_, record) => {
          const operators = [
            {
              key: 'addChildren',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.addChildren`,
                      type: 'button',
                      meaning: '数据层级配置-新增下级',
                    },
                  ]}
                  onClick={() => this.showModal(record, true, true)}
                >
                  {intl.get('hzero.common.button.addChildren').d('新增下级')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hzero.common.button.addChildren').d('新增下级'),
            },
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '数据层级配置-编辑',
                    },
                  ]}
                  onClick={() => this.showModal(record, true, false)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ].filter(Boolean);
    return (
      <React.Fragment>
        <Header title={intl.get('hpfm.dataHierarchies.view.message.title.list').d('数据层级配置')}>
          <ButtonPermission
            type="primary"
            icon="plus"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '数据层级配置-新建',
              },
            ]}
            onClick={() => this.showModal({}, false, false)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <Button icon="down" onClick={this.handleExpand}>
            {intl.get('hzero.common.button.expandAll').d('全部展开')}
          </Button>
          <Button icon="up" onClick={this.handleShrink}>
            {intl.get('hzero.common.button.collapseAll').d('全部收起')}
          </Button>
          {isSiteFlag && (
            <Form.Item style={{ marginBottom: 0 }}>
              {form.getFieldDecorator('tenantId', {
                initialValue: 0,
              })(
                <Lov
                  code="HPFM.TENANT"
                  textValue={tenantName}
                  onOk={this.handleOk}
                  allowClear={false}
                />
              )}
            </Form.Item>
          )}
        </Header>
        <Content>
          <FilterForm
            isSiteFlag={isSiteFlag}
            ref={this.filterFormRef}
            onSearch={this.handleSearch}
          />
          <Table
            bordered
            rowKey="dataHierarchyId"
            indentSize={24}
            columns={columns}
            pagination={false}
            dataSource={renderTree}
            loading={fetchListLoading}
            expandedRowKeys={expandedRowKeys}
            scroll={{ x: tableScrollWidth(columns) }}
            onExpand={this.handleExpandRow}
          />
          <Drawer
            title={
              // eslint-disable-next-line no-nested-ternary
              editFlag
                ? addFlag
                  ? intl
                      .get('hpfm.dataHierarchies.model.dataHierarchies.down')
                      .d('新增数据层级配置下级')
                  : intl
                      .get('hpfm.dataHierarchies.model.dataHierarchies.edit')
                      .d('编辑数据层级配置')
                : intl
                    .get('hpfm.dataHierarchies.model.dataHierarchies.create')
                    .d('新建数据层级配置')
            }
            displayList={displayList}
            loading={createConfigLoading || editConfigLoading}
            initLoading={getConfigDetailLoading}
            modalVisible={modalVisible}
            organizationId={trueTenantId !== undefined ? trueTenantId : tenantId}
            isSiteFlag={isSiteFlag}
            addFlag={addFlag}
            editFlag={editFlag}
            initData={dataHierarchiesDetail}
            onCancel={this.hideModal}
            onOk={this.handleConfirm}
          />
        </Content>
      </React.Fragment>
    );
  }
}
