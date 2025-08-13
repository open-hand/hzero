/**
 * dataSourceDriver-数据源驱动
 * @date: 2019-08-22
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React, { PureComponent, Fragment } from 'react';
import { connect } from 'dva';
import { Table, Tag, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isUndefined } from 'lodash';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { HZERO_FILE, BKT_PLATFORM } from 'utils/config';
import notification from 'utils/notification';
import { enableRender, operatorRender } from 'utils/renderer';
import {
  filterNullValueObject,
  tableScrollWidth,
  isTenantRoleLevel,
  getCurrentOrganizationId,
} from 'utils/utils';

import { downloadFile } from 'services/api';
import FilterForm from './FilterForm';
import CreateDrawer from './CreateDrawer';

@connect(({ loading, dataSourceDriver }) => ({
  dataSourceDriver,
  isSiteFlag: !isTenantRoleLevel(),
  fetchLoading: loading.effects['dataSourceDriver/fetchDriversList'],
  saveLoading:
    loading.effects['dataSourceDriver/createDriver'] ||
    loading.effects['dataSourceDriver/updateDriver'],
  detailLoading: loading.effects['dataSourceDriver/fetchDriverById'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: ['hpfm.dataSourceDriver'] })
export default class dataSourceDriver extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      formData: {}, // 存储详情明细
      fileList: [], // 文件上传列表
      modalVisible: false, // 控制模态框显示
    };
  }

  /**
   * render()调用后请求数据
   */
  componentDidMount() {
    const { dispatch } = this.props;
    const lovCodes = { typeList: 'HPFM.DATABASE_TYPE' };
    dispatch({
      type: 'dataSourceDriver/init',
      payload: {
        lovCodes,
      },
    });
    this.handleSearch();
  }

  /**
   * 查询数据
   * @param {Object} params - 查询参数
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'dataSourceDriver/fetchDriversList',
      payload: { ...fieldValues, page: isEmpty(params) ? {} : params },
    });
  }

  /**
   * 设置form对象
   * @param {object} ref - FilterForm子组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 下载
   */
  @Bind()
  handleDownload(record) {
    const { isSiteFlag } = this.props;
    const organizationId = getCurrentOrganizationId();
    const api = `${HZERO_FILE}/v1/${isSiteFlag ? '' : `${organizationId}/`}files/download`;
    const queryParams = [{ name: 'url', value: encodeURIComponent(record.driverPath) }];
    queryParams.push({ name: 'bucketName', value: BKT_PLATFORM });
    queryParams.push({ name: 'directory', value: 'hpfm02' });
    downloadFile({
      requestUrl: api,
      queryParams,
    });
  }

  /**
   * 新建时，显示Drawer
   */
  @Bind()
  handleCreate() {
    this.setState({
      modalVisible: true,
    });
  }

  /**
   * 新建时，显示Drawer
   */
  @Bind()
  handleEdit(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataSourceDriver/fetchDriverById',
      payload: { ...record },
    }).then((res) => {
      if (res) {
        this.setState({
          formData: res,
          fileList: [
            {
              uid: '-1',
              name: res.fileName,
              status: 'done',
              url: res.driverPath,
            },
          ],
        });
      }
    });
    this.setState({
      modalVisible: true,
    });
  }

  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataSourceDriver/removeDriver',
      payload: { ...record },
    }).then((res) => {
      if (res) {
        notification.success({
          message: intl.get(`hzero.common.notification.success.delete`).d('删除成功'),
        });
        this.handleSearch();
      }
    });
  }

  /**
   * 隐藏Drawer
   */
  @Bind()
  hideDrawer() {
    this.setState({
      formData: {},
      modalVisible: false,
      fileList: [],
    });
  }

  /**
   * 保存
   * @param {Object} fieldsValue - 操作数据
   */
  @Bind()
  handleSave(fieldsValue) {
    const {
      dispatch,
      dataSourceDriver: { pagination = {} },
    } = this.props;
    const { formData } = this.state;
    dispatch({
      type: isUndefined(formData.driverId)
        ? 'dataSourceDriver/createDriver'
        : 'dataSourceDriver/updateDriver',
      payload: fieldsValue,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
        this.hideDrawer();
      }
    });
  }

  /**
   * 渲染操作按钮
   * @param text - 文字描述
   * @param record - 当前记录
   * @returns {*}
   */
  @Bind()
  optionsRender(_, record) {
    const { match, tenantId } = this.props;
    const operators = [
      {
        key: 'download',
        ele: (
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${match.path}.button.download`,
                type: 'button',
                meaning: '数据源驱动-下载',
              },
            ]}
            onClick={() => this.handleDownload(record)}
          >
            {intl.get('hzero.common.button.download').d('下载')}
          </ButtonPermission>
        ),
        len: 2,
        title: intl.get('hzero.common.button.download').d('下载'),
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
                meaning: '数据源驱动-编辑',
              },
            ]}
            onClick={() => this.handleEdit(record)}
          >
            {intl.get('hzero.common.button.edit').d('编辑')}
          </ButtonPermission>
        ),
        len: 2,
        title: intl.get('hzero.common.button.edit').d('编辑'),
      },
      tenantId === record.tenantId && {
        key: 'delete',
        ele: (
          <Popconfirm
            title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
            placement="topRight"
            onConfirm={() => this.handleDelete(record)}
          >
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${match.path}.button.delete`,
                  type: 'button',
                  meaning: '数据源驱动-删除',
                },
              ]}
              // onClick={() => this.handleDelete(record)}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
          </Popconfirm>
        ),
        len: 3,
        title: intl.get('hzero.common.button.delete').d('删除'),
      },
    ];
    return operatorRender(operators, record);
  }

  @Bind()
  databaseTypeRender(value, record) {
    const {
      dataSourceDriver: { dataSourceTypeList },
    } = this.props;
    const color = dataSourceTypeList.find((i) => i.value === record.databaseType);
    return value ? <Tag color={color ? color.tag : null}>{value}</Tag> : null;
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      fetchLoading = false,
      saveLoading = false,
      detailLoading = false,
      isSiteFlag,
      match,
      dataSourceDriver: { driverList = [], dataSourceTypeList, pagination },
      tenantId,
    } = this.props;
    const { formData = {}, modalVisible = false, fileList = [] } = this.state;
    const filterProps = {
      isSiteFlag,
      dataSourceTypeList,
      onRef: this.handleBindRef,
      onSearch: this.handleSearch,
    };
    const columns = [
      {
        title: intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.tenantName').d('租户名称'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.driverName').d('驱动名称'),
        dataIndex: 'driverName',
        width: 150,
      },
      {
        title: intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.databaseType').d('驱动类型'),
        dataIndex: 'databaseTypeMeaning',
        width: 150,
        render: (value, record) => this.databaseTypeRender(value, record),
      },
      {
        title: intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.driverVersion').d('驱动版本'),
        dataIndex: 'driverVersion',
        width: 150,
      },
      {
        title: intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.mainClass').d('主类入口'),
        dataIndex: 'mainClass',
        width: 200,
      },
      {
        title: intl.get('hzero.common.source').d('来源'),
        dataIndex: 'source',
        width: 100,
        render: (_, record) => {
          return tenantId === record.tenantId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          );
        },
      },
      {
        title: intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.description').d('描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 100,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 180,
        fixed: 'right',
        render: this.optionsRender,
      },
    ].filter((col) => {
      return isTenantRoleLevel() ? col.dataIndex !== 'tenantName' : col.dataIndex !== 'source';
    });
    // Table
    const tableProps = {
      columns,
      pagination,
      bordered: true,
      rowKey: 'driverId',
      loading: fetchLoading,
      dataSource: driverList,
      scroll: { x: tableScrollWidth(columns) },
      onChange: (page) => this.handleSearch(page),
    };
    const drawerProps = {
      dataSourceTypeList,
      title: formData.driverId
        ? intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.edit').d('编辑驱动')
        : intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.create').d('创建驱动'),
      fileList,
      isSiteFlag,
      initData: formData,
      initLoading: detailLoading,
      confirmLoading: saveLoading,
      modalVisible,
      onOk: this.handleSave,
      onCancel: this.hideDrawer,
    };
    return (
      <Fragment>
        <Header
          title={intl.get('hpfm.dataSourceDriver.model.dataSourceDriver.title').d('数据源驱动')}
        >
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${match.path}.button.create`,
                type: 'button',
                meaning: '数据源驱动-新建',
              },
            ]}
            onClick={() => this.handleCreate()}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <Table {...tableProps} />
          <CreateDrawer {...drawerProps} />
        </Content>
      </Fragment>
    );
  }
}
