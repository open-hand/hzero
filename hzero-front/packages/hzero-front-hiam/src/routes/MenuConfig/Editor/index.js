/* eslint-disable no-nested-ternary */
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

const viewMessagePrompt = 'hiam.menuConfig.view.message';
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
      relatedRowKeys: [],
      menuLabels: [],
    };
  }

  componentDidMount() {
    //   const { resetFields = e => e } = this.editForm;
    //   resetFields();
    const {
      dataSource: { id },
      copyFlag,
      queryLabel = (e) => e,
    } = this.props;
    if (copyFlag) {
      this.fetchMenuList();
    }
    if (!copyFlag && id !== undefined) {
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
    const { visible, dataSource, copyFlag } = this.props;
    const { code } = dataSource;
    return visible && copyFlag && code !== (prevProps.dataSource || {}).code;
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
      isSiteFlag,
      dataSource: { currentId, level },
      fetchCopyMenuList = (e) => e,
    } = this.props;
    const params = {
      rootMenuId: currentId,
      // eslint-disable-next-line no-nested-ternary
      level: isSiteFlag ? (level ? level.toUpperCase() : 'SITE') : 'ORGANIZATION',
    };
    // fetchCopyMenuList(params).then(
    //   ({ copyMenuList = [], copyMenuListDefaultRowKeys = [], relatedRowKeys = [] }) => {
    //     this.setState({
    //       copyMenuList,
    //       menuSelectedRowKeys: copyMenuListDefaultRowKeys,
    //       relatedRowKeys,
    //     });
    //   }
    // );
    fetchCopyMenuList(params).then((res) => {
      if (res) {
        this.setState({
          copyMenuList: res.copyMenuList,
          menuSelectedRowKeys: res.copyMenuListDefaultRowKeys,
          relatedRowKeys: res.relatedRowKeys,
        });
      }
    });
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
    });
    onCancel();
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
    validateFields(['type', 'level', 'parentId', 'name', 'route', 'icon', 'code'], (err) => {
      if (isEmpty(err)) {
        const values = getFieldsValue();
        handleSave(
          {
            ...dataSource,
            ...newDataSource,
            ...values,
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
    const { handleCreate = (e) => e, dataSource, siteLabelList, tenantLabelList } = this.props;
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
            labels: (dataSource.level === 'site' ? siteLabelList : tenantLabelList)
              .filter((item) => values.labels.includes(item.name))
              .map((item) => ({ ...item, labelId: item.id })),
            parentId: values.parentId,
            code: `${values.codePrefix}.${values.code}`.toLowerCase(),
            virtualFlag: values.virtualFlag ? 1 : 0,
            codePrefix: values.codePrefix.slice(0, values.codePrefix.indexOf('.')),
          },
          this.cancel.bind(this)
        );
      }
    });
  }

  // 复制并创建菜单
  copyCreateMenu() {
    const { handleCopyCreate = (e) => e, dataSource, siteLabelList, tenantLabelList } = this.props;
    const newDataSource = this.state.dataSource;
    const { validateFields = (e) => e, getFieldsValue } = this.editForm;
    validateFields(['type', 'level', 'parentId', 'name', 'route', 'icon', 'code'], (err) => {
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
              labels: (dataSource.level === 'site' ? siteLabelList : tenantLabelList)
                .filter((item) => values.labels.includes(item.name))
                .map((item) => ({ ...item, labelId: item.id })),
              parentId: values.parentId,
              code: `${values.codePrefix}.${values.code}`.toLowerCase(),
              virtualFlag: values.virtualFlag ? 1 : 0,
              codePrefix: values.codePrefix.slice(0, values.codePrefix.indexOf('.')),
              tenantId: organizationId,
            },
          },
          this.cancel.bind(this)
        );
      }
    });
  }

  addPermissions() {
    const { handleQueryPermissions = (e) => e } = this.props;
    const { getFieldsValue = (e) => e } = this.editForm;
    const { level } = getFieldsValue();
    if (!isEmpty(level)) {
      this.setState({
        permissionsModalVisible: true,
      });
      handleQueryPermissions({ level, page: 0, size: 10 }).then((res) => {
        if (res) {
          const { dataSource, pagination } = res;
          const { permissionsModalDataSource = [] } = this.state;
          this.setState({
            permissionsModalDataSource: uniqBy(dataSource.concat(permissionsModalDataSource), 'id'),
            permissionsModalPagination: pagination,
          });
        }
      });
    }
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

  deletePermissions() {
    const { permissionsSelectedRows = [], dataSource } = this.state;
    this.setState({
      dataSource: {
        ...dataSource,
        permissions: pullAllBy([...dataSource.permissions], permissionsSelectedRows, 'id'),
      },
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
    const { menuSelectedRowKeys, relatedRowKeys } = this.state;
    const setIdList = [];
    if (record.id === dataSource.currentId) return; // 复制的节点必须勾选,不可取消

    // 子节点被勾选时，其父节点若未被勾选，则自动被勾选
    // 节点的被动勾选，不会触发勾选其所有子节点的逻辑，反之，则会勾选所有子节点
    const addParentIdList = (parentId) => {
      const isIncludeParent = menuSelectedRowKeys.includes(parentId);
      if (!isIncludeParent) {
        setIdList.push(parentId);
        const targetParentRow = relatedRowKeys.find((item) => item.id === parentId);
        if (!isUndefined(targetParentRow)) {
          addParentIdList(targetParentRow.parentId);
        }
      }
    };

    const getSubSetIdList = (collections = []) => {
      collections.forEach((n) => {
        setIdList.push(n.id);
        if (!isEmpty(n.subMenus)) {
          getSubSetIdList(n.subMenus);
        }
      });
    };

    if (!isEmpty(record.subMenus)) {
      getSubSetIdList(record.subMenus);
    }

    if (selected) {
      addParentIdList(record.parentId);
    }
    setIdList.push(record.id);
    const filteredRowKeys = filter(menuSelectedRowKeys, (item) => !setIdList.includes(item));
    this.setState({
      menuSelectedRowKeys: selected ? menuSelectedRowKeys.concat(setIdList) : filteredRowKeys,
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
      recordCreateFlag = false,
      copyFlag = false,
      dataSource: propsDataSource,
      siteLabelList,
      tenantLabelList,
    } = this.props;
    const typePrompt = {
      dir: intl.get(`${viewMessagePrompt}.menu.dir`).d('目录'),
      menu: intl.get(`${viewMessagePrompt}.menu.menu`).d('菜单'),
      root: intl.get(`${viewMessagePrompt}.menu.root`).d('预置目录'),
      link: intl.get(`${viewMessagePrompt}.menu.link`).d('链接'),
    };
    const {
      dataSource: stateDataSource,
      permissionsModalDataSource,
      permissionsModalPagination,
      permissionsModalVisible,
      // permissionsSelectedRows,
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
    // eslint-disable-next-line no-nested-ternary
    const title = copyFlag
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
      recordCreateFlag,
      copyFlag,
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
    };

    // const tableProps = {
    //   dataSource: dataSource.permissions,
    //   pagination: {
    //     showSizeChanger: true,
    //     pageSizeOptions: ['10', '20', '50', '100'],
    //     pageSize: 10,
    //     total: (dataSource.permissions || []).length,
    //     showTotal: totalRender,
    //   },
    //   rowSelection: {
    //     selectedRowKeys: permissionsSelectedRows.map(n => n.id),
    //     onSelect: this.onPermissionsSelect.bind(this),
    //     onSelectAll: this.onPermissionsSelectAll.bind(this),
    //   },
    // };

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
          {/* {dataSource.type === 'menu' && (
            <h3>{intl.get(`${viewMessagePrompt}.title.basicInfo`).d('基本信息')}</h3>
          )} */}

          <EditorForm {...formProps} />
          {copyFlag && (
            <>
              <Divider orientation="left">
                {intl.get(`${viewMessagePrompt}.title.copyMenu`).d('选择复制内容')}
              </Divider>
              <CopyMenuTable {...tableProps} />
            </>
          )}
          <br />
          {/* {dataSource.type === 'menu' && (
            <Fragment>
              <h3>{intl.get(`${viewMessagePrompt}.title.menuPermission`).d('菜单权限')}</h3>
              <div className="action">
                <Button
                  type="primary"
                  icon="plus"
                  style={{ marginRight: 8 }}
                  onClick={this.addPermissions.bind(this)}
                >
                  {intl.get(`${commonPrompt}.button.create`).d('新建')}
                </Button>
                <Button icon="delete" onClick={this.deletePermissions.bind(this)}>
                  {intl.get(`${commonPrompt}.button.delete`).d('删除')}
                </Button>
              </div>
              <br />
              <Table {...tableProps} />
            </Fragment>
          )} */}
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
          <Button
            onClick={this.cancel.bind(this)}
            disabled={processing.save || processing.create}
            style={{ marginRight: 8 }}
          >
            {intl.get(`${commonPrompt}.button.cancel`).d('取消')}
          </Button>
          {copyFlag ? (
            <Button
              type="primary"
              loading={processing.copyCreate}
              onClick={this.copyCreateMenu.bind(this)}
            >
              {intl.get(`${commonPrompt}.button.ok`).d('确定')}
            </Button>
          ) : dataSource.id !== undefined ? (
            <Button type="primary" loading={processing.save} onClick={this.save.bind(this)}>
              {intl.get(`${commonPrompt}.button.save`).d('保存')}
            </Button>
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
