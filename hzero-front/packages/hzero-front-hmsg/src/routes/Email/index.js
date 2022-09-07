/**
 * Email 邮箱
 * @date: 2018-7-25
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Form, Table, Tag, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import {
  getCurrentOrganizationId,
  isTenantRoleLevel,
  tableScrollWidth,
  encryptPwd,
  addItemToPagination,
} from 'utils/utils';
import { enableRender, operatorRender } from 'utils/renderer';
import { VERSION_IS_OP } from 'utils/config';

import EmailForm from './EmailForm';
import SearchForm from './SearchForm';
import Filter from './Filter';

@formatterCollections({ code: ['hmsg.email', 'hmsg.form', 'entity.tenant', 'hmsg.common'] })
@Form.create({ fieldNameProp: null })
@connect(({ loading, email }) => ({
  email,
  tenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
  fetchEmailLoading: loading.effects['email/fetchEmail'],
  updateEmailLoading: loading.effects['email/updateEmail'],
  createEmailLoading: loading.effects['email/createEmail'],
  detailLoading: loading.effects['email/queryEmailServers'],
  filterLoading: loading.effects['email/updateFilter'],
  deleteFilterLoading: loading.effects['email/deleteFilter'],
  fetchFilterLoading: loading.effects['email/fetchFilterList'],
}))
export default class Email extends PureComponent {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
    this.state = {
      emailFormData: {},
      currentFilter: {},
      modalVisible: false,
      filterVisible: false,
      isCopy: false,
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'email/fetchEnums',
    });
    this.fetchEmail();
    this.fetchPublicKey();
  }

  /**
   * @function fetchEmail - 获取邮箱账户列表数据
   * @param {object} params - 查询参数
   */
  fetchEmail(params = {}, query) {
    const {
      dispatch,
      email: { pagination = {} },
    } = this.props;
    dispatch({
      type: 'email/fetchEmail',
      payload: { page: pagination, ...params, ...query },
    });
  }

  /**
   * 请求公钥
   */
  @Bind()
  fetchPublicKey() {
    const { dispatch = () => {} } = this.props;
    dispatch({
      type: 'email/getPublicKey',
    });
  }

  /**
   * @function showModal - 新增显示模态框
   */
  @Bind()
  showModal() {
    const { dispatch } = this.props;
    this.setState({ emailFormData: {}, isCopy: false });
    dispatch({
      type: 'email/updateState',
      payload: { emailProperties: [] },
    });
    this.handleModalVisible(true);
  }

  /**
   * @function handleModalVisible - 控制modal显示与隐藏
   * @param {boolean} flag 是否显示modal
   */
  @Bind()
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  /**
   * @function handleSearch - 搜索邮箱账户
   */
  @Bind()
  handleSearch(form) {
    this.fetchEmail({ page: {} }, form.getFieldsValue());
  }

  /**
   * @function handleAddItem - 新增一条邮箱服务器配置项
   * @param {object} params - 参数
   * @param {string} params.propertyCode - 服务器配置项 - 属性名称
   * @param {string} params.propertyName - 服务器配置项 - 属性值
   */
  @Bind()
  handleAddItem(itemValue, initData, more) {
    const { dispatch, email: { emailProperties = [] } = {} } = this.props;
    if (!more) {
      if (
        emailProperties.find(
          (item) =>
            item.propertyCode === itemValue.propertyCode &&
            itemValue.isCreate &&
            item.propertyId !== itemValue.propertyId
        )
      ) {
        return notification.warning({
          message: intl
            .get('hmsg.email.view.message.title.exist')
            .d('该配置项已存在，请忽重复添加'),
        });
      }
      if (initData.propertyId) {
        const updateList = emailProperties.map((item) => {
          if (initData.propertyId === item.propertyId) {
            return { ...initData, ...itemValue };
          }
          return item;
        });
        dispatch({
          type: 'email/updateState',
          payload: {
            emailProperties: updateList,
          },
        });
      } else {
        dispatch({
          type: 'email/updateState',
          payload: {
            emailProperties: [
              ...emailProperties,
              {
                ...itemValue,
              },
            ],
          },
        });
      }
    } else {
      dispatch({
        type: 'email/updateState',
        payload: {
          emailProperties: [...itemValue],
        },
      });
    }
  }

  /**
   * @function handleDeleteItem - 删除一条邮箱服务器配置项
   * @param {object} record - 要删除的服务器配置项
   */
  @Bind()
  handleDeleteItem(record) {
    const {
      dispatch,
      email: { emailProperties },
    } = this.props;
    const newList = emailProperties.filter((item) => item.propertyId !== record.propertyId);
    dispatch({
      type: 'email/updateState',
      payload: {
        emailProperties: newList,
      },
    });
  }

  /**
   * @function handleAdd - 新增一条邮箱服务器配置项
   * @param {object} params - 新增参数
   */
  @Bind()
  handleSaveEmail(fieldsValue) {
    const {
      dispatch,
      email: { emailProperties, publicKey },
    } = this.props;
    const { isCopy, emailFormData } = this.state;
    const dataSource = !emailFormData.serverId || isCopy ? {} : emailFormData;
    const newValue = { ...fieldsValue };
    if (fieldsValue.passwordEncrypted) {
      newValue.passwordEncrypted = encryptPwd(fieldsValue.passwordEncrypted, publicKey);
    }
    dispatch({
      type: `email/${!emailFormData.serverId || isCopy ? 'createEmail' : 'updateEmail'}`,
      payload: {
        ...dataSource,
        ...newValue,
        enabledFlag: fieldsValue.enabledFlag ? 1 : 0,
        emailProperties: emailProperties.map((item, index, arr) => {
          if (item.isCreate) {
            // eslint-disable-next-line
            delete arr[index].propertyId;
          }
          return { ...item, tenantId: fieldsValue.tenantId };
        }),
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleModalVisible(false);
        this.handleSearch(this.filterFormRef.current.props.form);
      }
    });
  }

  /**
   * @function handleUpdateEmail - 编辑邮箱账户行数据
   * @param {object} record - 行数据
   */
  @Bind()
  handleUpdateEmail(record) {
    const { dispatch } = this.props;
    this.setState({ emailFormData: record, isCopy: false });
    this.handleModalVisible(true);
    dispatch({
      type: 'email/queryEmailServers',
      payload: { serverId: record.serverId },
    });
  }

  @Bind()
  handleCopy(record) {
    this.setState({ emailFormData: record, isCopy: true });
    this.handleModalVisible(true);
  }

  /**
   * @function handlePagination - 分页操作
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchEmail({
      page: pagination,
    });
  }

  @Bind()
  handleShowFilter(record) {
    this.setState({ filterVisible: true, currentFilter: record });
    this.handleFilterSearch({
      serverId: record.serverId,
      address: record.address,
    });
  }

  @Bind()
  handleFilterCancel() {
    this.setState({ filterVisible: false });
  }

  @Bind()
  handleFilterSearch(params = {}) {
    const {
      dispatch,
      email: { filterPagination = {} },
    } = this.props;
    const { currentFilter: { serverId } = {} } = this.state;
    dispatch({
      type: 'email/fetchFilterList',
      payload: { page: filterPagination, serverId, ...params },
    });
  }

  @Bind()
  handleFilterEdit(record, flag) {
    const { dispatch, email: { filterList = [] } = {} } = this.props;
    const newList = filterList.map((item) => {
      if (record.emailFilterId === item.emailFilterId) {
        return { ...item, _status: flag ? 'update' : '' };
      }
      return item;
    });
    dispatch({
      type: 'email/updateState',
      payload: { filterList: newList },
    });
  }

  @Bind()
  handleFilterDelete(data = [], keys = []) {
    const { dispatch, email: { filterList = [] } = {} } = this.props;
    const filterData = data.filter((item) => item._status !== 'create');
    // 删除未保存的数据
    const createList = data.filter((item) => item._status === 'create');
    if (createList.length > 0) {
      const deleteList = filterList.filter((item) => !keys.includes(item.emailFilterId));
      dispatch({
        type: 'email/updateState',
        payload: { filterList: deleteList },
      });
      notification.success();
    }
    if (filterData.length > 0) {
      dispatch({
        type: 'email/deleteFilter',
        payload: filterData,
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleFilterSearch();
        }
      });
    }
  }

  @Bind()
  handleFilterCreate(data) {
    const {
      dispatch,
      email: { filterList = [], filterPagination = {} },
    } = this.props;
    dispatch({
      type: 'email/updateState',
      payload: {
        filterList: [data, ...filterList],
        filterPagination: addItemToPagination(filterList.length, filterPagination),
      },
    });
  }

  @Bind()
  handleFilterOk(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'email/updateFilter',
      payload: data,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleFilterCancel(false);
      }
    });
  }

  @Bind()
  handleDelete(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'email/deleteEmail',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(this.filterFormRef.current.props.form);
      }
    });
  }

  render() {
    const {
      fetchEmailLoading,
      updateEmailLoading,
      createEmailLoading,
      tenantRoleLevel,
      match: { path },
      detailLoading = false,
      fetchFilterLoading = false,
      filterLoading = false,
      deleteFilterLoading = false,
      email: {
        emailList = [],
        emailProperties,
        pagination,
        enums,
        filterStrategyList = [],
        filterList = [],
        filterPagination = {},
      },
    } = this.props;
    const { modalVisible, filterVisible, currentFilter, isCopy } = this.state;
    const columns = [
      !isTenantRoleLevel() && {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hmsg.common.view.accountCode').d('账户代码'),
        dataIndex: 'serverCode',
      },
      {
        title: intl.get('hmsg.common.view.accountName').d('账户名称'),
        dataIndex: 'serverName',
      },
      {
        title: intl.get('hmsg.email.model.email.host').d('邮件服务器'),
        width: 200,
        dataIndex: 'host',
      },
      {
        title: intl.get('hmsg.email.model.email.port').d('端口'),
        width: 100,
        dataIndex: 'port',
      },
      {
        title: intl.get('hmsg.email.model.email.filterStrategy').d('安全策略'),
        width: 100,
        dataIndex: 'filterStrategyMeaning',
      },
      isTenantRoleLevel() &&
        !VERSION_IS_OP && {
          title: intl.get('hmsg.common.view.source').d('来源'),
          width: 120,
          dataIndex: 'tenantId',
          render: (_, record) => {
            const { tenantId } = this.props;
            return tenantId.toString() === record.tenantId.toString() ? (
              <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
            ) : (
              <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
            );
          },
        },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 100,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 220,
        fixed: 'right',
        render: (text, record) => {
          const operators = [];
          const { tenantId } = this.props;
          if (tenantId.toString() === record.tenantId.toString() || !isTenantRoleLevel()) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '邮箱账户-编辑',
                    },
                  ]}
                  onClick={() => this.handleUpdateEmail(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          if (
            tenantId.toString() !== record.tenantId.toString() &&
            isTenantRoleLevel() &&
            !VERSION_IS_OP
          ) {
            operators.push({
              key: 'copy',
              ele: (
                <a onClick={() => this.handleCopy(record)}>
                  {intl.get('hzero.common.button.copy').d('复制')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            });
          }
          if (tenantId.toString() === record.tenantId.toString() || !isTenantRoleLevel()) {
            operators.push({
              key: 'filter',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.filter`,
                      type: 'button',
                      meaning: '邮箱账户-设置黑白名单',
                    },
                  ]}
                  onClick={() => this.handleShowFilter(record)}
                >
                  {intl.get('hmsg.email.button.filter').d('设置黑白名单')}
                </ButtonPermission>
              ),
              len: 6,
              title: intl.get('hmsg.email.button.filter').d('设置黑白名单'),
            });
          }
          if (
            tenantId.toString() === record.tenantId.toString() &&
            isTenantRoleLevel() &&
            !VERSION_IS_OP
          ) {
            operators.push({
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl
                    .get(`hmsg.email.view.message.title.confirmDelete`)
                    .d('确定删除该数据吗？')}
                  onConfirm={() => this.handleDelete(record)}
                  style={{ textAlign: 'center' }}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          }
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);

    const filterProps = {
      currentFilter,
      fetchLoading: fetchFilterLoading,
      deleteLoading: deleteFilterLoading,
      loading: filterLoading,
      dataSource: filterList,
      visible: filterVisible,
      pagination: filterPagination,
      onOk: this.handleFilterOk,
      onCancel: this.handleFilterCancel,
      onDelete: this.handleFilterDelete,
      onCreate: this.handleFilterCreate,
      onSearch: this.handleFilterSearch,
      onEdit: this.handleFilterEdit,
      path,
    };

    return (
      <>
        <Header title={intl.get('hmsg.email.view.message.title').d('邮箱账户')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '邮箱账户-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.showModal}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">
            <SearchForm onSearch={this.handleSearch} wrappedComponentRef={this.filterFormRef} />
          </div>
          <Table
            bordered
            rowKey="serverId"
            loading={fetchEmailLoading}
            dataSource={emailList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          <EmailForm
            title={`${
              this.state.emailFormData.serverId
                ? intl.get('hmsg.email.view.message.title.modal.edit').d('编辑邮箱账户')
                : intl.get('hmsg.email.view.message.title.modal.create').d('新建邮箱账户')
            }`}
            loading={this.state.emailFormData.serverId ? updateEmailLoading : createEmailLoading}
            detailLoading={detailLoading}
            modalVisible={modalVisible}
            onCancel={this.hideModal}
            onOk={this.handleSaveEmail}
            initData={this.state.emailFormData}
            itemList={emailProperties}
            addItem={this.handleAddItem}
            deleteItem={this.handleDeleteItem}
            tenantRoleLevel={tenantRoleLevel}
            enums={enums}
            filterStrategyList={filterStrategyList}
            path={path}
            isCopy={isCopy}
          />
          {filterVisible && <Filter {...filterProps} />}
        </Content>
      </>
    );
  }
}
