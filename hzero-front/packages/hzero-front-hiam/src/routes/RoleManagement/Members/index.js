import React, { PureComponent } from 'react';
import { Button, Table } from 'hzero-ui';
import { find, isEmpty, isInteger, isNil, pullAllBy, uniqBy } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';
import { getCurrentOrganizationId, tableScrollWidth } from 'utils/utils';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { VERSION_IS_OP } from 'utils/config';
import Drawer from '../Drawer';
// import OrganizationModal from './Organization';
import QueryForm from './QueryForm';
import EditableRow from './EditableRow';
import EditableCell from './EditableCell';
import styles from './index.less';

const EditableContext = React.createContext();
const currentTenantId = getCurrentOrganizationId();

// const EditableRow = ({ form, index, ...props }) => {
//   return (
//     <EditableContext.Provider value={form}>
//       <tr {...props} />
//     </EditableContext.Provider>
//   );
// };

// const EditableFormRow = Form.create({ fieldNameProp: null })(EditableRow);

function getCodeTag(value, code = []) {
  let result;
  if (value && !isEmpty(code)) {
    const codeList = code.filter((n) => n.value === value);
    if (!isEmpty(codeList)) {
      result = codeList[0].tag;
    }
  }
  return result;
}

function assignResourceLevel(
  options = [],
  roleDatasource,
  recordOrganizationId,
  parentRoleAssignLevel
) {
  let newOptions = options;
  const parentRoleAssignLevelTag = Number(getCodeTag(parentRoleAssignLevel, options));
  // if (roleDatasource.tenantId !== 0) {
  newOptions = options.filter((o) => o.value !== 'site');
  // } else {
  //   newOptions = options.filter(o => o.value === 'site');
  // }

  return newOptions.filter((o) => Number(o.tag) >= parentRoleAssignLevelTag);
  // roleDatasource.tenantId !== recordOrganizationId
  // ? newOptions.filter(o => Number(o.tag) >= parentRoleAssignLevelTag)
  // newOptions;
}

export default class Members extends PureComponent {
  constructor(props) {
    super(props);
    this.onDrawerClose = this.onDrawerClose.bind(this);
    this.fetchDataSource = this.fetchDataSource.bind(this);
  }

  state = {
    selectedRows: [],
    dataSource: [],
    pagination: {},
    editingRows: [],
    // organizationModalVisible: false,
    // organizationModalDataSource: [],
    // currentEditingRow: {},
  };

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, roleDatasource } = this.props;
    return (
      visible && isInteger(roleDatasource.id) && roleDatasource.id !== prevProps.roleDatasource.id
    );
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    if (snapshot) {
      this.fetchDataSource();
    }
  }

  onDrawerClose() {
    const { close = (e) => e } = this.props;
    this.setState({
      dataSource: [],
      selectedRows: [],
      pagination: {},
      editingRows: [],
    });
    close();
  }

  @Bind()
  onTableSelectedRowChange(selectedRowKeys, selectedRows) {
    this.setState({
      selectedRows,
    });
  }

  @Bind()
  onTableChange(pagination) {
    const { getFieldsValue = (e) => e } = this.queryForm;
    const { current = 1, pageSize = 10 } = pagination;
    this.fetchDataSource({ ...getFieldsValue(), page: current - 1, size: pageSize });
  }

  fetchDataSource(params = {}) {
    const { handleFetchData = (e) => e } = this.props;
    handleFetchData({ page: 0, size: 10, ...params }).then((res) => {
      if (res) {
        const { dataSource, pagination } = res;
        this.setState({
          dataSource,
          pagination,
          editingRows: [],
        });
      }
    });
  }

  add() {
    const { editingRows, dataSource } = this.state;
    const { roleDatasource = {} /* , resourceLevel = [] */ } = this.props;
    const item = { key: uuidv4() };
    // if (roleDatasource.tenantId === 0) {
    //   item.organizationId = roleDatasource.tenantId;
    //   item.tenantName = roleDatasource.tenantName;
    //   // item.assignLevel = 'site';
    //   // item.assignLevelMeaning = getCodeMeaning('site', resourceLevel);
    //   // item.assignLevelValue = 0;
    //   // item.assignLevelValueMeaning = roleDatasource.tenantName;
    // }

    if (roleDatasource.level === 'site') {
      item.assignLevel = 'organization';
      item.assignLevelValue = roleDatasource.tenantId;
      item.assignLevelValueMeaning = roleDatasource.tenantName;
      // 之前的逻辑
      // item.organizationId = roleDatasource.tenantId;
      // item.tenantName = roleDatasource.tenantName;
      // item.assignLevel = 'organization';
      // // item.assignLevelMeaning = getCodeMeaning('site', resourceLevel);
      // item.assignLevelValue = 0;
      // // item.assignLevelValueMeaning = roleDatasource.tenantName;
    }
    if (!isNil(roleDatasource.parentRoleAssignUnitId)) {
      item.assignLevel = 'org';
    }

    if (String(currentTenantId) !== '0' && VERSION_IS_OP) {
      item.organizationId = roleDatasource.tenantId;
      item.tenantName = roleDatasource.tenantName;
      item.assignLevel = 'organization';
      // item.assignLevelMeaning = getCodeMeaning('site', resourceLevel);
      item.assignLevelValue = 0;
      // item.assignLevelValueMeaning = roleDatasource.tenantName;
    }

    this.setState({
      dataSource: [item].concat(dataSource),
      editingRows: uniqBy(editingRows.concat(item), 'key'),
    });
  }

  deleteRow() {
    const { selectedRows, dataSource, pagination } = this.state;
    const { handleDelete = (e) => e, roleDatasource = {} } = this.props;
    const { current, pageSize } = pagination;
    const { getFieldsValue = (e) => e } = this.queryForm;
    const data = selectedRows.filter((o) => isInteger(Number(o.key)));
    this.setState({
      dataSource: pullAllBy(
        [...dataSource],
        selectedRows.filter((o) => !isInteger(Number(o.key)), 'key')
      ),
    });
    if (!isEmpty(data)) {
      handleDelete(
        data.map((n) => ({
          memberId: n.id,
          roleId: roleDatasource.id,
        })),
        () => {
          notification.success();
          this.fetchDataSource({
            roleId: roleDatasource.id,
            page: current - 1,
            size: pageSize,
            ...getFieldsValue(),
          });
        }
      );
    }
  }

  @Bind()
  save() {
    const { handleSave = (e) => e, roleDatasource } = this.props;
    const { dataSource = [], editingRows = [] } = this.state;
    const tableRowForms = this.tableRowForms
      .map((o) => {
        const item = editingRows.find((n) => o.key === n.key);
        return !isEmpty(item) ? { ...o, rowData: item } : false;
      })
      .filter(Boolean);
    Promise.all(
      tableRowForms.map(
        (o) =>
          new Promise((resolve, rejcet) => {
            const { validateFields = () => {} } = o.row || {};
            validateFields((error, values) => {
              if (!isEmpty(error)) {
                rejcet(error);
              } else {
                resolve({ ...o.rowData, ...values });
              }
            });
          })
      )
    )
      .then((result = []) => {
        const data = dataSource.map((n) => {
          const newItem = result.find((o) => o.key === n.key);
          const item = !isEmpty(newItem) ? newItem : n;
          const { id, assignLevel, assignLevelValue } = item;
          return {
            memberId: id,
            assignLevel,
            assignLevelValue,
            roleId: roleDatasource.id,
          };
        });

        if (!isEmpty(data)) {
          handleSave(data, false, () => {
            notification.success({
              message: intl.get(`hzero.common.notification.success.save`).d('保存成功'),
            });
            this.onDrawerClose();
          });
        }
      })
      .catch((e) => {
        window.console.warn(e);
      });
    // if (!isEmpty(dataSource.filter(o => !isEmpty(o.error)))) {
    //   return;
    // }
  }

  search(params) {
    this.fetchDataSource({ page: 0, size: 10, ...params });
  }

  getColumns(defaultColumns) {
    const { roleDatasource, resourceLevel, handleFetchHrunitsTree = (e) => e } = this.props;
    const { editingRows, dataSource = [] } = this.state;
    const saveBtnTarget = this.saveBtn;
    const setRecord = (newRecored) => {
      this.setState({
        dataSource: dataSource.map((o) => (o.key === newRecored.key ? newRecored : o)),
      });
    };
    const getColumn = (defaultColumn) => {
      const { dataIndex, title } = defaultColumn;
      return dataIndex !== 'realName' && dataIndex !== 'tenantName'
        ? {
            ...defaultColumn,
            render: (text, record) => {
              const isUpdate = isInteger(Number(record.key));
              const editing = !isEmpty(editingRows.filter((o) => o.key === record.key));
              const editable =
                defaultColumn.className === 'editable-cell' ||
                defaultColumn.className === 'editable-cell-operation';
              const isColumnEdited = {
                id: editing && !isUpdate,
                assignLevel: editing, // editing && record.organizationId !== 0,
                assignLevelValue: editing && record.assignLevel === 'org',
                // editing && record.organizationId !== 0 && record.assignLevel === 'org',
              };
              const editableCellProps = {
                title,
                text,
                dataIndex,
                record,
                saveBtnTarget,
                editable,
                editing: isColumnEdited[dataIndex],
                currentEditingRow: find(editingRows, (o) => o.key === record.key),
                roleTenantId: roleDatasource.tenantId,
                roleTenantName: roleDatasource.tenantName,
                setRecord,
                // siteMeaning: getCodeMeaning('site', resourceLevel),
                // 角色数据 需要传给 EditCell 来做判断
                roleDatasource,
              };
              if (dataIndex === 'assignLevel') {
                editableCellProps.options = assignResourceLevel(
                  resourceLevel,
                  roleDatasource,
                  record.organizationId,
                  roleDatasource.parentRoleAssignLevel
                );
              }
              if (dataIndex === 'assignLevelValue') {
                editableCellProps.handleFetchOrganizationData = handleFetchHrunitsTree;
                editableCellProps.roleId = roleDatasource.id;
              }
              if (dataIndex === 'id') {
                editableCellProps.roleId = roleDatasource.id;
              }

              return (
                <EditableContext.Consumer>
                  {(form) =>
                    // eslint-disable-next-line no-nested-ternary
                    defaultColumn.className === 'editable-cell' ? (
                      <EditableCell form={form} {...editableCellProps} />
                    ) : !isEmpty(editingRows.filter((o) => o.key === record.key)) ? (
                      <a onClick={this.cancel.bind(this, record, form)}>
                        {isInteger(Number(record.key))
                          ? intl.get(`hzero.common.button.cancel`).d('取消')
                          : intl.get(`hzero.common.button.clean`).d('清除')}
                      </a>
                    ) : (
                      <a onClick={this.edit.bind(this, record, form)}>
                        {intl.get(`hzero.common.button.edit`).d('编辑')}
                      </a>
                    )
                  }
                </EditableContext.Consumer>
              );
            },
          }
        : defaultColumn;
    };
    return defaultColumns.map((n) => getColumn(n));
  }

  @Bind()
  assignRowData(newRecored) {
    const { dataSource = [] } = this.state;
    const newDataSource = dataSource.map((o) =>
      o.key === newRecored.key ? { ...o, ...newRecored } : o
    );
    this.setState({
      dataSource: newDataSource,
    });
  }

  @Bind()
  getColumns2() {
    const {
      code = {},
      roleDatasource,
      resourceLevel,
      handleFetchHrunitsTree = () => {},
    } = this.props;
    const { editingRows } = this.state;
    const defaultColumns = [
      {
        title: intl.get(`hiam.roleManagement.model.roleManagement.userLoginName`).d('用户名'),
        dataIndex: 'id',
        render: (text, record) => record.realName,
      },
      !VERSION_IS_OP &&
        String(currentTenantId) === '0' && {
          title: intl.get(`hiam.roleManagement.model.roleManagement.tenant`).d('所属租户'),
          dataIndex: 'tenantName',
        },
      {
        title: intl.get(`hiam.roleManagement.model.roleManagement.assignLevel`).d('分配层级'),
        dataIndex: 'assignLevel',
        width: 150,
        render: (text, record) => record.assignLevelMeaning,
      },
      {
        title: intl
          .get(`hiam.roleManagement.model.roleManagement.assignLevelValue`)
          .d('分配层级值'),
        dataIndex: 'assignLevelValue',
        width: 180,
        render: (text, record) => record.assignLevelValueMeaning,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'action',
        width: 90,
        fixed: 'right',
        render: this.actionRender,
      },
    ].filter(Boolean);

    return defaultColumns.map((n) => ({
      ...n,
      onCell: (record) => {
        const isUpdate = isInteger(Number(record.key));
        const editing = !isEmpty(editingRows.filter((o) => o.key === record.key));
        const isColumnEdited = {
          id: editing && !isUpdate,
          assignLevel: editing, // editing && record.organizationId !== 0,
          assignLevelValue: editing && record.assignLevel === 'org',
          // editing && record.organizationId !== 0 && record.assignLevel === 'org',
        };
        const editableCellProps = {
          record,
          dataIndex: n.dataIndex,
          title: n.title,
          style: {
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            whiteSpace: 'nowrap',
          },
          assignRowData: this.assignRowData,
          onClick: (e) => {
            const { target } = e;
            if (target.style.whiteSpace === 'normal') {
              target.style.whiteSpace = 'nowrap';
            } else {
              target.style.whiteSpace = 'normal';
            }
          },
          editing: isColumnEdited[n.dataIndex],
          contextConsumer: EditableContext.Consumer,
          render: n.render,
          code,
          roleDatasource,
          roleTenantId: roleDatasource.tenantId,
          roleTenantName: roleDatasource.tenantName,
        };

        if (n.dataIndex === 'assignLevel') {
          editableCellProps.options = assignResourceLevel(
            resourceLevel,
            roleDatasource,
            record.organizationId,
            roleDatasource.parentRoleAssignLevel
          );
        }
        if (n.dataIndex === 'assignLevelValue') {
          editableCellProps.handleFetchOrganizationData = handleFetchHrunitsTree;
          editableCellProps.roleId = roleDatasource.id;
        }
        if (n.dataIndex === 'id') {
          editableCellProps.roleId = roleDatasource.id;
        }

        return editableCellProps;
      },
    }));
  }

  @Bind()
  actionRender(text, record) {
    const { editingRows } = this.state;
    const operators = [
      {
        key: 'cancel',
        ele: !isEmpty(editingRows.filter((o) => o.key === record.key)) ? (
          <a onClick={() => this.cancel(record)}>
            {isInteger(Number(record.key))
              ? intl.get(`hzero.common.button.cancel`).d('取消')
              : intl.get(`hzero.common.button.clean`).d('清除')}
          </a>
        ) : (
          <a onClick={() => this.edit(record)}>{intl.get(`hzero.common.button.edit`).d('编辑')}</a>
        ),
        len: 2,
        // eslint-disable-next-line no-nested-ternary
        title: !isEmpty(editingRows.filter((o) => o.key === record.key))
          ? isInteger(Number(record.key))
            ? intl.get(`hzero.common.button.cancel`).d('取消')
            : intl.get(`hzero.common.button.clean`).d('清除')
          : intl.get(`hzero.common.button.edit`).d('编辑'),
      },
    ];
    return operatorRender(operators);
  }

  @Bind()
  edit(record) {
    const { editingRows } = this.state;
    this.setState({
      editingRows: uniqBy(editingRows.concat({ ...record }), 'key'),
    });
  }

  @Bind()
  cancel(record) {
    const { dataSource, editingRows } = this.state;
    const defaultItem = editingRows.find((o) => o.key === record.key);
    this.setState({
      dataSource: isInteger(record.key)
        ? dataSource.map((n) => (n.key === defaultItem.key ? defaultItem : n))
        : dataSource.filter((o) => o.key !== record.key),
      editingRows: editingRows.filter((o) => o.key !== record.key),
    });
  }

  @Bind()
  onTableRow(record = {}) {
    const { editableRowKey = [] } = this.props;
    return {
      onRef: (node) => {
        this.setTableRowForms(node, record);
      },
      contextProvider: EditableContext.Provider,
      ...(editableRowKey === record.key
        ? {
            style: {
              height: 70,
            },
          }
        : {}),
    };
  }

  /**
   * setTableRowForms - 设置行缓存
   * @param {!object} node - 表格行this对象
   * @param {object} record - 行数据
   */
  @Bind()
  setTableRowForms(node, record) {
    if (isEmpty(this.tableRowForms)) {
      this.tableRowForms = []; // new Map();
    }
    // this.tableRowForms = this.tableRowForms.set(record.key, node);
    this.tableRowForms = uniqBy(
      this.tableRowForms.concat({ key: record.key, row: node.props.form }),
      'key'
    );
  }

  render() {
    const { visible, title, processing = {}, prompt = {} } = this.props;
    const { selectedRows, dataSource, pagination, editingRows } = this.state;
    const columns = [
      {
        title: intl.get(`hiam.roleManagement.model.roleManagement.userLoginName`).d('用户名'),
        className: 'editable-cell',
        dataIndex: 'id',
      },
      !VERSION_IS_OP &&
        String(currentTenantId) === '0' && {
          title: intl.get(`hiam.roleManagement.model.roleManagement.tenant`).d('所属租户'),
          dataIndex: 'tenantName',
          width: 200,
        },
      {
        title: intl.get(`hiam.roleManagement.model.roleManagement.assignLevel`).d('分配层级'),
        dataIndex: 'assignLevel',
        className: 'editable-cell',
      },
      {
        title: intl
          .get(`hiam.roleManagement.model.roleManagement.assignLevelValue`)
          .d('分配层级值'),
        className: 'editable-cell',
        dataIndex: 'assignLevelValue',
        width: 180,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operation',
        className: 'editable-cell-operation',
        width: 85,
      },
    ].filter(Boolean);

    const drawerProps = {
      title,
      visible,
      onCancel: this.onDrawerClose.bind(this),
      width: 1000,
      anchor: 'right',
      wrapClassName: styles['hiam-role-members'],
      footer: (
        <>
          <Button onClick={this.onDrawerClose.bind(this)} disabled={processing.save}>
            {intl.get(`hzero.common.button.cancel`).d('取消')}
          </Button>
          {!isEmpty(editingRows) && (
            <Button
              ref={(node) => {
                this.saveBtn = node;
              }}
              id="saveBtn"
              type="primary"
              loading={processing.save}
              onClick={this.save}
            >
              {intl.get(`hzero.common.button.save`).d('保存')}
            </Button>
          )}
        </>
      ),
    };

    const queryFormProps = {
      ref: (node) => {
        this.queryForm = node;
      },
      prompt,
      handleFetchData: this.search.bind(this),
      disabled: processing.save,
      loading: processing.query,
    };

    const components = {
      body: {
        row: EditableRow, // EditableFormRow
        cell: EditableCell,
      },
    };

    const tableColumns = this.getColumns.bind(this)(columns);

    const tableProps = {
      components,
      pagination,
      dataSource,
      bordered: true,
      className: 'editable-table',
      columns: this.getColumns2(),
      scroll: { x: tableScrollWidth(tableColumns) },
      loading: processing.query || processing.save,
      rowSelection: {
        selectedRowKeys: selectedRows.map((n) => n.key),
        onChange: this.onTableSelectedRowChange,
      },
      onChange: this.onTableChange,
      onRow: this.onTableRow,
    };
    return (
      <Drawer {...drawerProps}>
        <QueryForm {...queryFormProps} />
        <br />
        <div className="action" style={{ textAlign: 'right' }}>
          <Button
            onClick={this.deleteRow.bind(this)}
            disabled={
              isEmpty(selectedRows) || processing.delete || processing.save || processing.query
            }
            style={{ marginRight: 8 }}
          >
            {intl.get(`hzero.common.button.delete`).d('删除')}
          </Button>
          <Button
            type="primary"
            onClick={this.add.bind(this)}
            disabled={processing.delete || processing.save || processing.query}
          >
            {intl.get(`hzero.common.button.add`).d('新增')}
          </Button>
        </div>
        <br />
        <Table {...tableProps} />
      </Drawer>
    );
  }
}
