import React from 'react';
import { Drawer, Form, Icon, Input, InputNumber, Select, Button, Tag } from 'hzero-ui';
import { uniqBy, isEmpty, toSafeInteger } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';

import TLEditor from 'components/TLEditor';
import EditTable from 'components/EditTable';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import { CODE_LOWER } from 'utils/regExp';

import Permissions from '../Permissions';
import Lovs from '../Lovs';
import QueryForm from './QueryForm';
import styles from './index.less';

@Form.create({ fieldNameProp: null })
export default class PermissionSet extends React.Component {
  state = {
    tableDataSource: [],
    currentRowData: {},
    permissionsDrawerVisible: false,
    editingRows: [],
    processingRowKeys: [],
    lovsDrawerVisible: false,
  };

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, menuDataSource } = this.props;
    return (
      visible &&
      menuDataSource.id !== undefined &&
      menuDataSource.id !== prevProps.menuDataSource.id
    );
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.fetchDataSource();
    }
  }

  @Bind()
  fetchDataSource(params) {
    const { handleQueryList = (e) => e, menuDataSource = {} } = this.props;
    handleQueryList(menuDataSource.id, params).then((res) => {
      const { dataSource } = res;
      this.setState({
        tableDataSource: dataSource,
      });
    });
  }

  @Bind()
  onDrawerClose() {
    const { close = (e) => e } = this.props;
    this.setState({
      tableDataSource: [],
      currentRowData: {},
      editingRows: [],
    });
    close();
  }

  @Bind()
  save(record, form) {
    const { handleSave = (e) => e, handleCreate = (e) => e, menuDataSource = {} } = this.props;
    const { processingRowKeys = [] } = this.state;
    const { validateFields = (e) => e } = form;
    const defaultCode = menuDataSource.code;
    const codePrefix = `${defaultCode}.ps.`;
    validateFields((err, values) => {
      if (isEmpty(err)) {
        const newRecord = {
          ...record,
          ...values,
          icon: 'link',
        };
        this.setState({
          processingRowKeys: uniqBy(processingRowKeys.concat(record.key)),
        });
        if (record._status === 'create') {
          newRecord.code = `${codePrefix}${values.code}`;
          handleCreate(newRecord, () => {
            this.fetchDataSource();
            // this.cancel(record);
            this.setState({
              processingRowKeys: processingRowKeys.filter((o) => o !== record.key),
            });
          });
        } else {
          handleSave(newRecord, () => {
            this.fetchDataSource();
            this.cancel(record);
            this.setState({
              processingRowKeys: processingRowKeys.filter((o) => o !== record.key),
            });
          });
        }
      } else {
        setTimeout(() => {
          this.forceUpdate();
        }, 23);
      }
    });
  }

  @Bind()
  operationRender(text, record) {
    const { handleEnable = (e) => e, processing = {}, path } = this.props;
    const { processingRowKeys = [] } = this.state;
    let operators = [];
    if (record._status === 'create' || record._status === 'update') {
      if (
        (processing.save || processing.create) &&
        !isEmpty(processingRowKeys.filter((o) => o === record.key))
      ) {
        operators = [
          {
            key: 'loading',
            ele: <Icon type="loading" />,
            len: 2,
          },
        ];
      } else {
        operators = [
          {
            key: 'save',
            ele: (
              <a onClick={() => this.save(record, record.$form)}>
                {intl.get('hzero.common.button.save').d('保存')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.save').d('保存'),
          },
          {
            key: 'cancel',
            ele: (
              <a onClick={() => this.cancel(record)}>
                {intl.get('hzero.common.button.cancel').d('取消')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.cancel').d('取消'),
          },
        ];
      }
    } else {
      operators = [
        {
          key: 'edit',
          ele: (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.editPermissionSet`,
                  type: 'button',
                  meaning: '菜单配置-权限集编辑',
                },
              ]}
              onClick={() => this.edit(record)}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </ButtonPermission>
          ),
          len: 2,
          title: intl.get('hzero.common.button.edit').d('编辑'),
        },
        {
          key: 'able',
          ele:
            record.enabledFlag === 1 ? (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.disablePermissionSet`,
                    type: 'button',
                    meaning: '菜单配置-权限集禁用',
                  },
                ]}
                onClick={() => handleEnable(record, 'disable', this.fetchDataSource)}
              >
                {intl.get('hzero.common.button.disable').d('禁用')}
              </ButtonPermission>
            ) : (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.enablePermissionSet`,
                    type: 'button',
                    meaning: '菜单配置-权限集启用',
                  },
                ]}
                onClick={() => handleEnable(record, 'enable', this.fetchDataSource)}
              >
                {intl.get('hzero.common.button.enable').d('启用')}
              </ButtonPermission>
            ),
          len: 2,
          title:
            record.enabledFlag === 1
              ? intl.get('hzero.common.button.disable').d('禁用')
              : intl.get('hzero.common.button.enable').d('启用'),
        },
        {
          key: 'permissions',
          ele: (
            <a onClick={() => this.openPermissionsDrawer(record, record.editDetailFlag === 1)}>
              {intl.get('hiam.menuConfig.view.message.title.permissions').d('权限')}
            </a>
          ),
          len: 2,
          title: intl.get('hiam.menuConfig.view.message.title.permissions').d('权限'),
        },
        {
          key: 'lov',
          ele: (
            <a onClick={() => this.openLovsDrawer(record, record.editDetailFlag === 1)}>
              {intl.get('hiam.menuConfig.view.message.title.lovs').d('Lov')}
            </a>
          ),
          len: 3,
          title: intl.get('hiam.menuConfig.view.message.title.lovs').d('Lov'),
        },
      ];
    }
    return operatorRender(operators, record, { limit: 5 });
  }

  @Bind()
  openPermissionsDrawer(currentRowData) {
    this.setState({
      currentRowData,
      permissionsDrawerVisible: true,
    });
  }

  @Bind()
  closePermissionsDrawer() {
    this.setState({
      currentRowData: {},
      permissionsDrawerVisible: false,
    });
  }

  @Bind()
  openLovsDrawer(currentRowData) {
    this.setState({
      currentRowData,
      lovsDrawerVisible: true,
    });
  }

  @Bind()
  closeLovsDrawer() {
    this.setState({
      currentRowData: {},
      lovsDrawerVisible: false,
    });
  }

  @Bind()
  add() {
    const { menuDataSource = {} } = this.props;
    const { tableDataSource = [], editingRows } = this.state;
    const item = {
      _status: 'create',
      key: uuidv4(),
      type: 'ps',
      level: menuDataSource.level,
      enabledFlag: 1,
      newSubnodeFlag: 1,
      editDetailFlag: 1,
      parentId: menuDataSource.id,
    };
    this.setState({
      editingRows: uniqBy(editingRows.concat(item), 'key'),
      tableDataSource: [item].concat(tableDataSource),
    });
  }

  @Bind()
  edit(record) {
    const { tableDataSource = [] } = this.state;
    this.setState({
      tableDataSource: tableDataSource.map((rd) => {
        if (rd.key === record.key) {
          return { ...rd, _status: 'update' };
        } else {
          return rd;
        }
      }),
    });
  }

  @Bind()
  cancel(record) {
    const { tableDataSource, editingRows } = this.state;
    this.setState({
      tableDataSource:
        record._status !== 'create'
          ? tableDataSource.map((rd) => {
              if (rd.key === record.key) {
                return { ...rd, _status: '' };
              } else {
                return rd;
              }
            })
          : tableDataSource.filter((rd) => rd.key !== record.key),
      editingRows: editingRows.filter((o) => o.key !== record.key),
    });
  }

  @Bind()
  getColumns() {
    const { controllerType = [] } = this.props;
    const defaultColumns = [
      {
        title: intl.get('hiam.menuConfig.model.menuConfig.permissionSetCode').d('权限集编码'),
        dataIndex: 'code',
        width: 300,
        render: (val, record) => {
          if (record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('code', {
                  initialValue: record.code,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hiam.menuConfig.model.menuConfig.permissionSetCode')
                          .d('权限集编码'),
                      }),
                    },
                    {
                      pattern: CODE_LOWER,
                      message: intl
                        .get('hzero.common.validation.codeLower')
                        .d('全小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                    },
                    {
                      max: 128,
                      message: intl.get('hzero.common.validation.max', {
                        max: 128,
                      }),
                    },
                  ],
                })(
                  <Input
                    trim
                    style={{ width: 200 }}
                    typeCase="lower"
                    inputChinese={false}
                    disabled={record._status !== 'create'}
                  />
                )}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hiam.menuConfig.model.menuConfig.permissionSetName').d('权限集名称'),
        dataIndex: 'name',
        width: 160,
        render: (val, record) => {
          const { _token } = record;
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('name', {
                  initialValue: record.name,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hiam.menuConfig.model.menuConfig.permissionSetName')
                          .d('权限集名称'),
                      }),
                    },
                    {
                      max: 128,
                      message: intl.get('hzero.common.validation.max', {
                        max: 128,
                      }),
                    },
                  ],
                })(<TLEditor field="name" token={_token} />)}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hiam.menuConfig.model.menuConfig.sort').d('序号'),
        dataIndex: 'sort',
        width: 80,
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('sort', {
                  initialValue: record.sort,
                })(<InputNumber style={{ width: 70 }} min={0} step={1} parser={toSafeInteger} />)}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        dataIndex: 'permissionType',
        title: intl.get('hiam.menuConfig.model.menuConfig.permissionType').d('权限类型'),
        width: 150,
        render: (value = '') => {
          const texts = {
            api: intl.get('hiam.roleManagement.view.message.api').d('API'),
            button: intl.get('hiam.roleManagement.view.message.button').d('按钮'),
            table: intl.get('hiam.roleManagement.view.message.table').d('表格列'),
            formItem: intl.get('hiam.roleManagement.view.message.formItem').d('表单项'),
            formField: intl.get('hiam.roleManagement.view.message.formField').d('表单域'),
          };
          const valueList = value.split(',') || [];
          const text = valueList.map((item) => (texts[item] ? texts[item] : '')) || [];
          return value === undefined || value === '' ? null : (
            <Tag color={value === 'api' ? 'green' : 'orange'}>{text.join()}</Tag>
          );
        },
      },
      {
        title: intl
          .get('hiam.menuConfig.model.menuConfig.controllerTypeMeaning')
          .d('权限集控制类型'),
        width: 120,
        dataIndex: 'controllerTypeMeaning',
        render: (val, record) => {
          if (
            (record._status === 'update' || record._status === 'create') &&
            record.permissionType !== 'api'
          ) {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('controllerType', {
                  initialValue: record.controllerType,
                })(
                  <Select style={{ width: '100%' }} allowClear>
                    {controllerType.map((item) => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hiam.menuConfig.model.menuConfig.description').d('描述'),
        dataIndex: 'description',
        render: (val, record) => {
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('description', {
                  initialValue: record.description,
                })(<Input />)}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 80,
      },
      {
        title: intl.get('hzero.common.table.column.option').d('操作'),
        width: 260,
        fixed: 'right',
        render: this.operationRender,
      },
    ];
    return defaultColumns;
  }

  onPermissionsDrawerOk(key, permissions, keyPath) {
    const { tableDataSource } = this.state;
    const getTableDataSource = (collections = []) =>
      collections.map((n) => {
        const m = n;
        m.permissions = m.key === key ? permissions : m.permissions;
        return m;
      });
    this.setState({
      tableDataSource: getTableDataSource(tableDataSource, keyPath),
    });
  }

  savePermissions(key, permissions, keyPath = [], cb = (e) => e) {
    const { handleSave = (e) => e } = this.props;
    const { tableDataSource } = this.state;
    let newRecord = {};
    const findDeep = (collections = []) =>
      collections.forEach((n) => {
        const m = n;
        if (m.key === key) {
          newRecord = m;
        }
      });
    findDeep(tableDataSource, keyPath);
    newRecord.permissions = permissions;

    handleSave(newRecord, () => {
      this.fetchDataSource();
      cb();
    });
  }

  render() {
    const {
      currentRoleCode,
      visible,
      path,
      processing = {},
      menuDataSource = {},
      handleQueryPermissions = (e) => e,
      handleQueryPermissionsBySet = (e) => e,
      handleQueryLovsBySet = (e) => e,
      handleQueryLovs = (e) => e,
      onAssignPermissions = (e) => e,
      onDeletePermissions = (e) => e,
    } = this.props;
    const {
      tableDataSource,
      permissionsDrawerVisible,
      currentRowData,

      lovsDrawerVisible,
    } = this.state;
    const drawerProps = {
      title: intl
        .get('hiam.menuConfig.view.message.title.permissionName', {
          name: menuDataSource.name,
        })
        .d(`"${menuDataSource.name}"的权限集`),
      visible,
      mask: true,
      maskStyle: { backgroundColor: 'rgba(0,0,0,.85)' },
      placement: 'right',
      destroyOnClose: true,
      onClose: this.onDrawerClose,
      width: 980,
      wrapClassName: styles['hiam-menu-config-permission-set'],
    };
    const columns = this.getColumns();
    const tableProps = {
      rowKey: 'key',
      dataSource: tableDataSource,
      columns,
      scroll: { x: tableScrollWidth(columns) },
      pagination: false,
      bordered: true,
      loading: processing.query || processing.setPermissionLoading,
    };

    const permissionsDrawerProps = {
      path,
      currentRoleCode,
      handleQueryPermissions, // 查询可分配的权限
      handleQueryPermissionsBySet, // 查询已分配的权限
      onAssignPermissions,
      onDeletePermissions,
      processing,
      visible: permissionsDrawerVisible,
      permissionSetDataSource: currentRowData,
      close: this.closePermissionsDrawer,
      menuId: menuDataSource.id,
      title: intl
        .get('hiam.menuConfig.view.message.title.viewPermissions', { name: currentRowData.name })
        .d(`“${currentRowData.name}”的权限`),
      editable: currentRowData.editDetailFlag === 1,
    };
    const lovsDrawerProps = {
      path,
      currentRoleCode,
      handleQueryLovs, // 查询可分配的Lov
      handleQueryLovsBySet, // 查询已分配的Lov
      onAssignPermissions,
      onDeletePermissions,
      processing,
      visible: lovsDrawerVisible,
      permissionSetDataSource: currentRowData,
      close: this.closeLovsDrawer,
      menuId: menuDataSource.id,
      title: intl
        .get('hiam.menuConfig.view.message.title.viewLovs', { name: currentRowData.name })
        .d(`“${currentRowData.name}”的Lov`),
      editable: currentRowData.editDetailFlag === 1,
    };
    return (
      <Drawer {...drawerProps}>
        <QueryForm handleQueryList={this.fetchDataSource} />
        <br />
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <Button style={{ marginRight: 8 }} onClick={this.collapseAll}>
            {intl.get('hzero.common.button.collapseAll').d('全部收起')}
          </Button>
          <Button onClick={this.expandAll}>
            {intl.get('hzero.common.button.expandAll').d('全部展开')}
          </Button>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.createPermissionSet`,
                type: 'button',
                meaning: '菜单配置-新建权限集',
              },
            ]}
            style={{ marginLeft: 8 }}
            onClick={this.add}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </div>
        <br />
        <EditTable {...tableProps} />
        <br />
        <br />
        <div className="footer">
          <Button onClick={this.onDrawerClose} type="primary">
            {intl.get('hzero.common.button.close').d('关闭')}
          </Button>
        </div>
        {permissionsDrawerVisible && <Permissions {...permissionsDrawerProps} />}
        {lovsDrawerVisible && <Lovs {...lovsDrawerProps} />}
      </Drawer>
    );
  }
}
