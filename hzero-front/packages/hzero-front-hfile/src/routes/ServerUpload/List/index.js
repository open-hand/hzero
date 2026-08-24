/**
 * serverUpload 服务器上传配置
 * @date: 2019-7-4
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Form, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, tableScrollWidth } from 'utils/utils';

import FilterForm from './FilterForm';

@connect(({ serverUpload, loading }) => ({
  serverUpload,
  isSiteFlag: !isTenantRoleLevel(),
  fetchConfigListLoading: loading.effects['serverUpload/fetchConfigList'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hfile.serverUpload'],
})
export default class serverUpload extends React.Component {
  constructor(props) {
    super(props);
    this.filterFormRef = React.createRef();
  }

  componentDidMount() {
    this.handleSearch();
    const { dispatch } = this.props;
    const lovCodes = { typeList: 'HFLE.SERVER.SOURCE_TYPE' };
    dispatch({
      type: 'serverUpload/init',
      payload: {
        lovCodes,
      },
    });
  }

  /**
   * @function handleSearch - 获取服务器上传配置列表头数据
   * @param {object} params - 查询参数
   */
  @Bind()
  handleSearch(params = {}) {
    const { dispatch } = this.props;
    const fieldsValue = this.filterFormRef.current.getFieldsValue();
    dispatch({
      type: 'serverUpload/fetchConfigList',
      payload: { ...fieldsValue, ...params },
    });
  }

  /**
   * @function handleResetSearch - 重置查询表单
   * @param {object} params - 查询参数
   */
  @Bind()
  handleResetSearch() {
    this.filterFormRef.resetFields();
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handlePagination(pagination = {}) {
    this.handleSearch({
      page: pagination,
    });
  }

  /**
   * 编辑配置
   */
  @Bind()
  handleEditConfig(record) {
    const { history } = this.props;
    const { configId } = record;
    history.push(`/hfile/server-upload/detail/${configId}`);
  }

  /**
   * 新建配置
   */
  @Bind()
  handleCreate() {
    const { history } = this.props;
    history.push(`/hfile/server-upload/detail/create`);
  }

  render() {
    const {
      fetchConfigListLoading = false,
      isSiteFlag,
      match: { path },
      serverUpload: { pagination = {}, serverUploadList = [] },
    } = this.props;
    const columns = [
      isSiteFlag && {
        title: intl.get('hzero.common.model.tenantName').d('租户'),
        width: 180,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hfile.serverUpload.model.serverUpload.configCode').d('配置编码'),
        dataIndex: 'configCode',
        Width: 160,
      },
      {
        title: intl.get('hfile.serverUpload.model.serverUpload.description').d('描述'),
        dataIndex: 'description',
      },
      {
        title: intl.get('hfile.serverUpload.model.serverUpload.sourceType').d('上传类型'),
        dataIndex: 'sourceTypeMeaning',
        width: 100,
      },
      {
        title: intl.get('hfile.serverUpload.model.serverUpload.rootDir').d('根目录'),
        width: 180,
        dataIndex: 'rootDir',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        width: 80,
        dataIndex: 'enabledFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        render: (text, record) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <a
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '服务器上传配置-编辑',
                  },
                ]}
                onClick={() => {
                  this.handleEditConfig(record);
                }}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter(Boolean);
    return (
      <>
        <Header
          title={intl.get('hfile.serverUpload.view.message.title.serverUpload').d('服务器上传配置')}
        >
          <ButtonPermission
            icon="plus"
            type="primary"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '服务器上传配置-新建',
              },
            ]}
            onClick={() => this.handleCreate()}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <FilterForm
            onSearch={this.handleSearch}
            isSiteFlag={isSiteFlag}
            ref={this.filterFormRef}
          />
          <Table
            bordered
            rowKey="configId"
            loading={fetchConfigListLoading}
            dataSource={serverUploadList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
        </Content>
      </>
    );
  }
}
