/* eslint-disable no-nested-ternary */
/* eslint-disable react/jsx-wrap-multilines */
import React, { PureComponent } from 'react';
import { Button, Drawer, Divider } from 'hzero-ui';
import { isEmpty, pullAllBy, uniqBy, filter, isUndefined } from 'lodash';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';
// import { totalRender } from 'utils/renderer';
import intl from 'utils/intl';
import EditorForm from './Form';
import Permissions from './PermissionsLov';
import IconsModal from './Icons';
import CopyMenuTable from './CopyMenuTable';
import styles from './index.less';

const viewMessagePrompt = 'hiam.tenantMenu.view.message';
const commonPrompt = 'hzero.common';

export default class Editor extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      dataSource: {},
      permissionsSelectedRows: [],
      permissionsModalVisible: false,
      permissionsModalDataSource: [],
      permissionsModalPagination: {},
      iconsModalVisible: false,
      copyMenuList: [],
      menuSelectedRowKeys: [],
      menuSelectedRows: [],
      menuLabels: [],
    };
  }

  componentDidMount() {
    // const { resetFields = e => e } = this.editForm;
    // resetFields();
    const {
      dataSource: { id },
      isCopy,
      queryLabel = (e) => e,
    } = this.props;
    if (isCopy) {
      this.fetchMenuList();
    }
    if (!isCopy && id !== undefined) {
      if (!isUndefined(id)) {
        queryLabel(id).then((res) => {
          if (res && getResponse(res) && !isEmpty(getResponse(res))) {
            this.setState({ menuLabels: res });
          }
        });
      }
    }
  }

  static getDerivedStateFromProps(nextProps, prevState) {
    return nextProps.dataSource.id !== prevState.dataSource.id
      ? { dataSource: nextProps.dataSource }
      : null;
  }

  getSnapshotBeforeUpdate(prevProps) {
    const { visible, dataSource, isCopy } = this.props;
    const { code } = dataSource;
    return visible && isCopy && code !== (prevProps.dataSource || {}).code;
  }

  componentDidUpdate(prevProps, prevState, snapshot) {
    // If we have a snapshot value, we've just added new items.
    // Adjust scroll so these new items don't push the old ones out of view.
    // (snapshot here is the value returned from getSnapshotBeforeUpdate)
    if (snapshot) {
      this.fetchMenuList();
    }
  }

  fetchMenuList() {
    const {
      dataSource: { currentId },
      fetchCopyMenuList = (e) => e,
      isCopy,
    } = this.props;
    const params = {
      rootMenuId: currentId,
      level: 'ORGANIZATION',
    };
    if (isCopy) {
      fetchCopyMenuList(params).then((res) => {
        if (res) {
          this.setState({
            copyMenuList: res.copyMenuList,
            menuSelectedRowKeys: res.copyMenuListDefaultRowKeys,
            menuSelectedRows: res.copyMenuListDefaultRows,
          });
        }
      });
    }
  }

  cancel() {
    const { onCancel = (e) => e } = this.props;
    const { dataSource = {} } = this.state;
    const { resetFields = (e) => e } = this.editForm;
    resetFields();
    this.setState({
      dataSource: { ...dataSource, permissions: [] },
      permissionsSelectedRows: [],
      copyMenuList: [],
      menuSelectedRowKeys: [],
      menuSelectedRows: [],
    });
    onCancel();
  }

  // 复制并创建菜单
  copyCreateMenu() {
    const { handleCopyCreate = (e) => e, dataSource } = this.props;
    const newDataSource = this.state.dataSource;
    const { validateFields = (e) => e, getFieldsValue } = this.editForm;
    validateFields(
      ['type', 'level', 'parentId', 'name', 'route', 'icon', 'code', 'selectedTenantId'],
      (err) => {
        if (!err) {
          const values = getFieldsValue();
          const organizationId = getCurrentOrganizationId();
          handleCopyCreate(
            {
              copyMenuIds: this.state.menuSelectedRowKeys,
              rootMenu: {
                ...dataSource,
                ...values,
                ...newDataSource,
                parentId: values.parentId,
                code: `${values.codePrefix}.${values.code}`.toLowerCase(),
                virtualFlag: values.virtualFlag ? 1 : 0,
                codePrefix: values.codePrefix.slice(0, values.codePrefix.indexOf('.')),
                tenantId: organizationId,
              },
              targetTenantId: values.selectedTenantId,
              sourceTenantId: dataSource.tenantId,
            },
            this.cancel.bind(this),
            dataSource.tenantId
          );
        }
      }
    );
  }

  save() {
    const {
      handleSave = (e) => e,
      onCancel = (e) => e,
      dataSource,
      siteLabelList,
      tenantLabelList,
    } = this.props;
    const newDataSource = this.state.dataSource;
    const { validateFields = (e) => e, getFieldsValue } = this.editForm;
    validateFields(['type', 'parentId', 'name', 'route', 'icon', 'code'], (err) => {
      if (isEmpty(err)) {
        const values = getFieldsValue();
        handleSave(
          {
            ...dataSource,
            ...newDataSource,
            ...values,
            level: values?.level || newDataSource?.level,
            labels: (dataSource.level === 'site' ? siteLabelList : tenantLabelList)
              .filter((item) => values.labels.includes(item.name))
              .map((item) => ({ ...item, labelId: item.id })),
            virtualFlag: values.virtualFlag ? 1 : 0,
            codePrefix: values.codePrefix.slice(0, values.codePrefix.indexOf('.')),
          },
          onCancel
        );
      }
    });
  }

  create() {
    const { handleCreate = (e) => e, dataSource } = this.props;
    const newDataSource = this.state.dataSource;
    const { validateFields = (e) => e, getFieldsValue } = this.editForm;
    validateFields(['type', 'level', 'parentId', 'name', 'route', 'icon', 'code'], (err) => {
      if (!err) {
        const values = getFieldsValue();
        handleCreate(
          {
            ...dataSource,
            ...values,
            ...newDataSource,
            // labels: (dataSource.level === 'site' ? siteLabelList : tenantLabelList)
            //   .filter((item) => values.labels.includes(item.name))
            //   .map((item) => ({ ...item, labelId: item.id })),
            parentId: values.parentId,
            code: `${values.codePrefix}.${values.code}`.toLowerCase(),
            virtualFlag: values.virtualFlag ? 1 : 0,
            codePrefix:
              values.codePrefix && values.codePrefix.slice(0, values.codePrefix.indexOf('.')),
          },
          this.cancel.bind(this)
        );
      }
    });
  }

  closePermissionsModal() {
    this.setState({
      permissionsModalVisible: false,
      permissionsModalDataSource: [],
      permissionsModalPagination: {},
    });
  }

  onPermissionsModalOk(selectedRows) {
    // const { dispatch } = this.props;
    const { dataSource } = this.state;
    this.setState({
      dataSource: {
        ...dataSource,
        permissions: uniqBy(dataSource.permissions.concat(selectedRows), 'id'),
      },
    });
    this.setState({
      permissionsModalVisible: false,
      permissionsModalDataSource: [],
      permissionsModalPagination: {},
    });
  }

  onPermissionsSelect(record, selected) {
    const { permissionsSelectedRows = [] } = this.state;
    this.setState({
      permissionsSelectedRows: selected
        ? permissionsSelectedRows.concat(record)
        : permissionsSelectedRows.filter((n) => n.id !== record.id),
    });
  }

  onPermissionsSelectAll(selected, newSelectedRows, changeRows) {
    const { permissionsSelectedRows = [] } = this.state;
    this.setState({
      permissionsSelectedRows: selected
        ? permissionsSelectedRows.concat(changeRows)
        : pullAllBy([...permissionsSelectedRows], changeRows, 'id'),
    });
  }

  openIconModal() {
    this.setState({
      iconsModalVisible: true,
    });
  }

  onIconSelect(icon) {
    const { setFieldsValue = (e) => e } = this.editForm;
    setFieldsValue({ icon });
    this.setState({
      iconsModalVisible: false,
    });
  }

  closeIconsModal() {
    this.setState({
      iconsModalVisible: false,
    });
  }

  handleTypeChange(type) {
    const { setFields = (e) => e } = this.editForm;
    const { dataSource } = this.state;
    setFields({
      dir: { error: undefined },
      code: { value: undefined, error: undefined },
      name: { value: undefined, error: undefined },
      parentId: { value: undefined, error: undefined },
      route: { value: undefined, error: undefined },
    });
    this.setState({
      dataSource: { ...dataSource, type, permissions: [] },
    });
  }

  setDataSource(newDataSource = {}) {
    const { dataSource } = this.state;
    this.setState({
      dataSource: { ...dataSource, ...newDataSource },
    });
  }

  handlePermissionsTableChange(params) {
    const { handleQueryPermissions = (e) => e } = this.props;
    const { getFieldsValue = (e) => e } = this.editForm;
    const { level } = getFieldsValue();
    handleQueryPermissions({ level, page: 0, size: 10, ...params }).then((res) => {
      if (res) {
        const { dataSource, pagination } = res;
        this.setState({
          permissionsModalDataSource: dataSource,
          permissionsModalPagination: pagination,
        });
      }
    });
  }

  /**
   * 选中或取消复制的内容的菜单
   * @param {object} record - 当前行数据
   * @param {boolean} selected - 是否选中
   */
  handleMenuRowSelect(record, selected) {
    const { dataSource } = this.props;
    const { menuSelectedRowKeys, menuSelectedRows } = this.state;
    if (record.id === dataSource.currentId) return; // 复制的节点必须勾选
    if (selected) {
      // 子节点被勾选时，其父节点必须已经被勾选，否则会造成断层
      const targetParent = menuSelectedRowKeys.includes(record.parentId);
      if (!targetParent) return;
    }
    const setIdList = [];
    const setRowList = [];
    let nextRows = [];
    const getSubSetIdList = (collections = []) => {
      collections.forEach((n) => {
        setIdList.push(n.id);
        if (selected) {
          setRowList.push(n);
        }
        if (!isEmpty(n.subMenus)) {
          getSubSetIdList(n.subMenus);
        }
      });
    };

    if (!isEmpty(record.subMenus)) {
      getSubSetIdList(record.subMenus);
    }
    setIdList.push(record.id);
    const filteredRowKeys = filter(menuSelectedRowKeys, (item) => !setIdList.includes(item));
    if (selected) {
      setRowList.push(record);
      nextRows = uniqBy(menuSelectedRows.concat(setRowList), 'id');
    } else {
      nextRows = filter(menuSelectedRows, (item) => filteredRowKeys.includes(item.id));
    }
    // menuSelectedRows中存储的是打平的树形数据 不含subMenus
    const nextExportSelectedRows = nextRows.map((row) => {
      const nextRow = { ...row };
      const { subMenus, ...rest } = nextRow;
      const newValue = { ...rest };
      return newValue;
    });
    this.setState({
      menuSelectedRowKeys: selected ? menuSelectedRowKeys.concat(setIdList) : filteredRowKeys,
      menuSelectedRows: nextExportSelectedRows,
    });
  }

  render() {
    const {
      path,
      visible,
      processing = {},
      handleCheckMenuDirExists = (e) => e,
      levelCode = [],
      handleQueryDir = (e) => e,
      menuPrefixList = [],
      menuTypeList,
      dataSource: propsDataSource,
      isCopy,
      recordCreateFlag,
      id,
      isEdit,
      siteLabelList,
      tenantLabelList,
    } = this.props;
    const {
      dataSource: stateDataSource,
      permissionsModalDataSource,
      permissionsModalPagination,
      permissionsModalVisible,
      iconsModalVisible,
      menuSelectedRowKeys,
      copyMenuList,
      menuLabels,
    } = this.state;
    const filteredMenuTypeList = menuTypeList.filter((item) => item.value !== 'ps');
    const dataSource = {
      type: recordCreateFlag ? 'dir' : 'root',
      ...stateDataSource,
      ...propsDataSource,
    };
    const typePrompt = {
      dir: intl.get(`${viewMessagePrompt}.menu.dir`).d('目录'),
      menu: intl.get(`${viewMessagePrompt}.menu.menu`).d('菜单'),
      root: intl.get(`${viewMessagePrompt}.menu.root`).d('预置目录'),
      link: intl.get(`${viewMessagePrompt}.menu.link`).d('链接'),
    };
    // eslint-disable-next-line no-nested-ternary
    const title = isCopy
      ? intl.get(`${viewMessagePrompt}.title.copyCreate`).d('复制并创建')
      : dataSource.id !== undefined
      ? intl
          .get(`${viewMessagePrompt}.title.editWithParam`, { name: typePrompt[dataSource.type] })
          .d(`编辑${typePrompt[dataSource.type]}`)
      : intl
          .get(`${viewMessagePrompt}.title.createWithParam`, {
            name: typePrompt[dataSource.type || 'dir'],
          })
          .d(`创建${typePrompt[dataSource.type || 'dir']}`);
    const organizationId = getCurrentOrganizationId();
    // const userInfo = getCurrentUser();
    const drawerProps = {
      title,
      visible,
      mask: true,
      maskStyle: { backgroundColor: 'rgba(0,0,0,.85)' },
      placement: 'right',
      destroyOnClose: true,
      onClose: this.cancel.bind(this),
      width: 620,
    };
    const formProps = {
      path,
      menuPrefixList,
      menuTypeList: filteredMenuTypeList,
      menuLabels,
      siteLabelList,
      tenantLabelList,
      ref: (ref) => {
        this.editForm = ref;
      },
      organizationId,
      handleCheckMenuDirExists,
      dataSource: { level: 'site', ...dataSource },
      levelCode: levelCode.filter((o) => o.value !== 'org'),
      editable: dataSource.id !== undefined,
      dirModalLoading: processing.queryDir,
      handleQueryDir,
      handleTypeChange: this.handleTypeChange.bind(this),
      handleOpenIconModal: this.openIconModal.bind(this),
      handleSetDataSource: this.setDataSource.bind(this),
      isCopy,
      recordCreateFlag,
      id,
      isEdit,
    };

    const permissionsModalProps = {
      visible: permissionsModalVisible,
      dataSource: permissionsModalDataSource,
      pagination: permissionsModalPagination,
      onCancel: this.closePermissionsModal.bind(this),
      onOk: this.onPermissionsModalOk.bind(this),
      handleFetchData: this.handlePermissionsTableChange.bind(this),
      loading: processing.queryPermissions || false,
      selectedRows: dataSource.permissions,
    };

    const iconsModalProps = {
      visible: iconsModalVisible,
      onSelect: this.onIconSelect.bind(this),
      onCancel: this.closeIconsModal.bind(this),
    };
    const rowSelection = {
      selectedRowKeys: menuSelectedRowKeys,
      onSelect: this.handleMenuRowSelect.bind(this),
    };
    const tableProps = {
      menuTypeList,
      dataSource: copyMenuList,
      loading: processing.queryCopyMenu,
      rowSelection,
      defaultExpandedRowKeys: menuSelectedRowKeys,
    };

    return (
      <Drawer {...drawerProps}>
        <div className={styles['hiam-menu-config-editor']}>
          <EditorForm {...formProps} />
          {isCopy && (
            <>
              <Divider orientation="left">
                {intl.get(`${viewMessagePrompt}.title.copyMenu`).d('选择复制内容')}
              </Divider>
              <CopyMenuTable {...tableProps} />
            </>
          )}
        </div>
        <div
          style={{
            position: 'absolute',
            bottom: 0,
            width: '100%',
            borderTop: '1px solid #e8e8e8',
            padding: '10px 16px',
            textAlign: 'right',
            left: 0,
            background: '#fff',
            borderRadius: '0 0 4px 4px',
            zIndex: 100,
          }}
        >
          <Button onClick={this.cancel.bind(this)} style={{ marginRight: 8 }}>
            {intl.get(`${commonPrompt}.button.cancel`).d('取消')}
          </Button>
          {isCopy ? (
            <Button
              type="primary"
              loading={processing.copyCreate}
              onClick={this.copyCreateMenu.bind(this)}
            >
              {intl.get(`${commonPrompt}.button.ok`).d('确定')}
            </Button>
          ) : dataSource.id !== undefined ? (
            isEdit && (
              <Button type="primary" loading={processing.save} onClick={this.save.bind(this)}>
                {intl.get(`${commonPrompt}.button.save`).d('保存')}
              </Button>
            )
          ) : (
            <Button type="primary" loading={processing.create} onClick={this.create.bind(this)}>
              {intl.get(`${commonPrompt}.button.ok`).d('确定')}
            </Button>
          )}
        </div>
        <Permissions {...permissionsModalProps} />
        <IconsModal {...iconsModalProps} />
      </Drawer>
    );
  }
}
