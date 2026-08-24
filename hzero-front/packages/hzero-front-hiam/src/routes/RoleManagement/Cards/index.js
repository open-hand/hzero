/**
 * 角色分配卡片
 * RoleAssignCardsEditModal
 * 使用 didUpdate 判断需不需要从新加载卡片
 * 重新加载卡片的条件是 visible 为 true, 且有角色信息
 * @date 2019-01-25
 * @author WY yang.wang06@hand-china.com
 * @copyright © HAND 2019
 */

import React from 'react';
import { Bind } from 'lodash-decorators';
import { isBoolean } from 'lodash';
import { Modal, InputNumber, Form, Table, Row, Col } from 'hzero-ui';
// import uuid from 'uuid/v4';

import { operatorRender } from 'utils/renderer';

import EditTable from 'components/EditTable';
import Checkbox from 'components/Checkbox';
import { Button as ButtonPermission } from 'components/Permission';
import {
  createPagination,
  delItemsToPagination,
  delItemToPagination,
  getEditTableData,
  tableScrollWidth,
} from 'utils/utils';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';
import notification from 'utils/notification';

import ModalFilter from './ModalFilter';

const CARD_MAX_HEIGHT = 200;

const patchInputStyle = {
  width: '100%',
};

function getRefFieldsValue(ref) {
  if (ref.current) {
    return ref.current.props.form.getFieldsValue();
  }
  return {};
}

/**
 * @ReactProps {!Function} onFetchRoleCards - 查询已经分配的卡片
 */
export default class RoleAssignCardsEditModal extends React.Component {
  constructor(props) {
    super(props);
    this.oneSearchFormRef = React.createRef();
    this.state = {
      rowSelection: {
        selectedRowKeys: [],
        selectedRows: [],
        onChange: this.handleRowSelectionChange,
      },
      dataSource: [],
      pagination: false,
      addModalVisible: false,
      selectedAddRows: [],
    };
  }

  componentDidUpdate(prevProps) {
    const { role } = prevProps;
    const { visible, role: nextRole } = this.props;
    if (visible) {
      if (nextRole && role !== nextRole) {
        // 两个角色不相同 且有下一个角色
        this.fetchRoleCards();
      }
    }
  }

  @Bind()
  handleSelectAddRows(_, selectedAddRows) {
    this.setState({
      selectedAddRows,
    });
  }

  @Bind()
  handleAddCancel() {
    this.setState({
      selectedAddRows: [],
      addModalVisible: false,
    });
  }

  @Bind()
  handleAddOk() {
    const { selectedAddRows } = this.state;
    const { role, onAdd } = this.props;
    onAdd(
      selectedAddRows.map((item) => ({
        ...item,
        roleId: role.id,
        x: 0,
        y: 0,
        defaultDisplayFlag: 0,
      }))
    ).then(() => {
      this.setState({
        addModalVisible: false,
        selectedAddRows: [],
      });
      this.fetchRoleCards();
    });
  }

  @Bind()
  cardPageChange(pagination = {}) {
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    const { cardTableChange } = this.props;
    cardTableChange(pagination, fieldsValue);
  }

  @Bind()
  handleOneSearchFormSearch() {
    const {
      onFormSearch,
      role: { id },
    } = this.props;
    const fieldsValue = getRefFieldsValue(this.oneSearchFormRef);
    onFormSearch({ ...fieldsValue, roleId: id });
  }

  render() {
    const {
      visible,
      role,
      loading,
      path,
      cardList = [],
      cardListPagination = {},
      queryRoleCardLoading,
    } = this.props;
    const {
      dataSource,
      pagination,
      rowSelection,
      addModalVisible,
      selectedAddRows = [],
    } = this.state;
    const rowAddSelection = {
      selectedRowKeys: selectedAddRows.map((n) => n.cardId),
      onChange: this.handleSelectAddRows,
    };
    const addColumns = [
      {
        title: intl.get('hiam.roleManagement.model.roleManagement.cardCode').d('卡片代码'),
        dataIndex: 'code',
      },
      {
        title: intl.get('hiam.roleManagement.model.roleManagement.cardName').d('卡片名称'),
        dataIndex: 'name',
      },
      {
        title: intl.get('hiam.roleManagement.model.roleManagement.catalogMeaning').d('卡片类型'),
        dataIndex: 'catalogMeaning',
      },
    ];
    return (
      <Modal
        title={intl.get('hiam.roleManagement.view.title.assignCards').d('工作台配置')}
        width="1000px"
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        onOk={this.handleModalOkBtnClick}
        onCancel={this.handleModalCancelBtnClick}
        confirmLoading={loading.saveRoleCards}
        cancelText={intl.get('hzero.common.button.close').d('关闭')}
      >
        <div className="table-list-operator">
          <Form>
            <Row type="flex" gutter={24} align="bottom">
              <Col span={8}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hiam.roleManagement.model.roleManagement.name').d('角色名称')}
                >
                  {role.name}
                </Form.Item>
              </Col>
              <Col span={8}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hiam.roleManagement.model.roleManagement.code').d('角色编码')}
                >
                  {role.code}
                </Form.Item>
              </Col>
            </Row>
          </Form>
        </div>
        <div className="table-list-operator" style={{ textAlign: 'right' }}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.deleteCardAssign`,
                type: 'button',
                meaning: '角色管理-删除卡片分配',
              },
            ]}
            key="remove"
            disabled={rowSelection.selectedRowKeys.length === 0}
            onClick={this.handleRemoveBtnClick}
            loading={loading.removeRoleCards}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.addCardAssign`,
                type: 'button',
                meaning: '角色管理-新建角色分配',
              },
            ]}
            type="primary"
            key="add"
            onClick={this.handleAddBtnClick}
          >
            {intl.get('hzero.common.button.add').d('新增')}
          </ButtonPermission>
        </div>
        <EditTable
          bordered
          rowKey="id"
          columns={this.columns}
          scroll={{ x: tableScrollWidth(this.columns) }}
          onChange={this.handleTableChange}
          pagination={pagination}
          dataSource={dataSource}
          rowSelection={rowSelection}
          loading={loading.fetchRoleCards}
        />
        <Modal
          destroyOnClose
          visible={addModalVisible}
          title={intl.get('hiam.roleManagement.view.title.choseCards').d('选择卡片')}
          onCancel={this.handleAddCancel}
          onOk={this.handleAddOk}
          width={800}
        >
          <ModalFilter
            wrappedComponentRef={this.oneSearchFormRef}
            onSearch={this.handleOneSearchFormSearch}
          />
          <Table
            rowKey="cardId"
            bordered
            loading={queryRoleCardLoading}
            dataSource={cardList}
            rowSelection={rowAddSelection}
            pagination={cardListPagination}
            columns={addColumns}
            onChange={this.cardPageChange}
          />
        </Modal>
      </Modal>
    );
  }

  /**
   * 查询角色拥有的卡片
   */
  fetchRoleCards(pagination = {}) {
    const { onFetchRoleCards, role } = this.props;
    const payload = { roleId: role.id, ...pagination };
    if (role.id !== undefined) {
      // 有角色 才能查询
      onFetchRoleCards(payload).then((res) => {
        if (res) {
          this.setState({
            dataSource: res.content,
            pagination: createPagination(res),
          });
        }
      });
    }
  }

  // Modal 相关
  @Bind()
  handleModalOkBtnClick() {
    const { onCancel, onOk, role } = this.props;
    const { dataSource } = this.state;
    const updateOrCreateData = dataSource.filter((r) => r._status);
    if (updateOrCreateData.length > 0) {
      // 有需要更新的数据
      const validationData = getEditTableData(updateOrCreateData, ['_status', 'id']);
      if (validationData.length === updateOrCreateData.length) {
        onOk(
          validationData.map((r) => {
            const { _status, ...restR } = r;
            return { ...restR, roleId: role.id };
          })
        );
      }
    } else {
      onCancel();
    }
  }

  @Bind()
  handleModalCancelBtnClick() {
    const { onCancel } = this.props;
    onCancel();
  }

  // 头按钮相关
  @Bind()
  handleAddBtnClick() {
    const {
      onAddCard,
      role: { id },
    } = this.props;
    // const { dataSource, pagination } = this.state;
    onAddCard({
      roleId: id,
    });
    this.setState({
      // 新增卡片 x,y 默认为0
      addModalVisible: true,
      // dataSource: [{ id: uuid(), _status: 'create', x: 0, y: 0 }, ...dataSource],
      // pagination: addItemToPagination(dataSource.length, pagination),
    });
  }

  @Bind()
  handleRemoveBtnClick() {
    const {
      rowSelection: { selectedRows },
      dataSource,
      pagination,
    } = this.state;
    const addRows = [];
    const oriRows = [];
    selectedRows.forEach((record) => {
      if (record._status === 'create') {
        addRows.push(record);
      } else {
        oriRows.push(record);
      }
    });
    if (oriRows.length > 0) {
      const { onRemoveRoleCards } = this.props;
      onRemoveRoleCards(oriRows).then((res) => {
        if (res) {
          notification.success();
          // 删除成功
          this.setState({
            dataSource: dataSource.filter((r) => !selectedRows.some((addR) => addR.id === r.id)),
            pagination: delItemsToPagination(selectedRows.length, dataSource.length, pagination),
            rowSelection: {
              selectedRowKeys: [],
              selectedRows: [],
              onChange: this.handleRowSelectionChange,
            },
          });
        }
      });
    } else {
      notification.success();
      this.setState({
        dataSource: dataSource.filter((r) => !addRows.some((addR) => addR.id === r.id)),
        pagination: delItemsToPagination(addRows.length, dataSource.length, pagination),
        rowSelection: {
          selectedRowKeys: [],
          selectedRows: [],
          onChange: this.handleRowSelectionChange,
        },
      });
    }
  }

  // Table 相关

  @Bind()
  handleTableChange(page, filter, sort) {
    this.fetchRoleCards({ page, sort });
  }

  @Bind()
  handleRowSelectionChange(_, selectedRows) {
    this.setState({
      rowSelection: {
        selectedRows,
        onChange: this.handleRowSelectionChange,
        selectedRowKeys: selectedRows.map((r) => r.id),
      },
    });
  }

  @Bind()
  handleLineEdit(record) {
    const { dataSource } = this.state;
    this.setState({
      dataSource: dataSource.map((r) => {
        if (r.id === record.id) {
          return {
            ...r,
            _status: 'update',
          };
        } else {
          return r;
        }
      }),
    });
  }

  @Bind()
  handleLineEditCancel(record) {
    const { dataSource } = this.state;
    this.setState({
      dataSource: dataSource.map((r) => {
        if (r.id === record.id) {
          const { _status, ...oriRecord } = r;
          return oriRecord;
        } else {
          return r;
        }
      }),
    });
  }

  @Bind()
  handleLineClean(record) {
    const { dataSource, rowSelection, pagination } = this.state;
    const nextSelectedRows = rowSelection.selectedRows.filter((r) => r.id !== record.id);
    this.setState({
      dataSource: dataSource.filter((r) => r.id !== record.id),
      pagination: delItemToPagination(dataSource.length, pagination),
      rowSelection: {
        ...rowSelection,
        selectedRows: nextSelectedRows,
        selectedRowKeys: nextSelectedRows.map((r) => r.id),
      },
    });
  }

  columns = [
    {
      title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardCode').d('卡片代码'),
      dataIndex: 'code',
      width: 180,
    },
    {
      title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardName').d('卡片名称'),
      dataIndex: 'name',
    },
    {
      title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardType').d('卡片类别'),
      dataIndex: 'catalogMeaning',
      width: 100,
    },
    {
      title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardH').d('高度'),
      dataIndex: 'h',
      width: 80,
      align: 'right',
    },
    {
      title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardW').d('长度'),
      dataIndex: 'w',
      width: 80,
    },
    {
      title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardX').d('位置X'),
      dataIndex: 'x',
      width: 120,
      render: (x, record) => {
        if (record._status) {
          const form = record.$form;
          const formOptions = {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.roleManagement.model.tenantAssignCards.cardX').d('位置X'),
                }),
              },
            ],
          };
          formOptions.initialValue = record._status === 'update' ? x : 0;
          return (
            <Form.Item>
              {form.getFieldDecorator(
                'x',
                formOptions
              )(
                <InputNumber
                  min={0}
                  max={CARD_MAX_HEIGHT}
                  precision={0}
                  step={1}
                  style={patchInputStyle}
                />
              )}
            </Form.Item>
          );
        } else {
          return x;
        }
      },
    },
    {
      title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardY').d('位置Y'),
      dataIndex: 'y',
      width: 120,
      align: 'right',
      render: (y, record) => {
        if (record._status) {
          const form = record.$form;
          const formOptions = {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hiam.roleManagement.model.tenantAssignCards.cardY').d('位置Y'),
                }),
              },
            ],
          };
          formOptions.initialValue = record._status === 'update' ? y : 0;
          return (
            <Form.Item>
              {form.getFieldDecorator(
                'y',
                formOptions
              )(
                <InputNumber
                  min={0}
                  max={CARD_MAX_HEIGHT}
                  precision={0}
                  step={1}
                  style={patchInputStyle}
                />
              )}
            </Form.Item>
          );
        } else {
          return y;
        }
      },
    },
    {
      title: intl.get('hiam.roleManagement.model.tenantAssignCards.cardInit').d('初始化'),
      dataIndex: 'defaultDisplayFlag',
      width: 80,
      render: (defaultDisplayFlag, record) => {
        if (record._status) {
          const form = record.$form;
          const formOptions = {};
          if (record._status === 'update') {
            formOptions.initialValue = defaultDisplayFlag;
          } else {
            formOptions.initialValue = 0;
          }
          return form.getFieldDecorator('defaultDisplayFlag', formOptions)(<Checkbox />);
        } else {
          return <Checkbox checked={defaultDisplayFlag === 1} disabled />;
        }
      },
    },
    {
      title: intl.get('hzero.common.button.action').d('操作'),
      width: 85,
      fixed: 'right',
      render: (_, record) => {
        const { path } = this.props;
        const operators = [];
        if (record._status === 'create') {
          operators.push({
            key: 'clean',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.cleanCardCreate`,
                    type: 'button',
                    meaning: '角色管理-清除卡片分配新增',
                  },
                ]}
                key="action-create"
                onClick={this.handleLineClean.bind(undefined, record)}
              >
                {intl.get('hzero.common.button.clean').d('清除')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.clean').d('清除'),
          });
        } else if (record._status === 'update') {
          operators.push({
            key: 'cancel',
            ele: (
              <a key="action-cancel" onClick={this.handleLineEditCancel.bind(undefined, record)}>
                {intl.get('hzero.common.button.cancel').d('取消')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.cancel').d('取消'),
          });
        } else {
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.editCardAssign`,
                    type: 'button',
                    meaning: '角色管理-编辑卡片分配',
                  },
                ]}
                key="action-edit"
                onClick={this.handleLineEdit.bind(undefined, record)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
        }
        return operatorRender(operators);
      },
    },
  ].filter((column) => !isBoolean(column));
}
