/**
 * AssignMember
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-14
 * @copyright 2019 © HAND
 */
import React from 'react';
import uuid from 'uuid/v4';
import { Modal } from 'hzero-ui';
import { isNil } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';

import notification from 'utils/notification';
import {
  parseParameters,
  delItemsToPagination,
  addItemsToPagination,
  getResponse,
  getEditTableData,
} from 'utils/utils';

import SearchForm from './SearchForm';
import EditDataTable from './EditDataTable';

/**
 * TODO: 每个角色 的唯一key 都使用 uuid 生成, 如果之后做了校验 可以使用 id
 * 由于 用户 id 作为 主键, 防止冲突, 使用使用mockId 作为主键
 * @type {string}
 */
const rowKey = '_mockId';

export default class AssignMember extends React.Component {
  searchFormRef = React.createRef();

  state = {
    dataSource: [],
    pagination: {},
  };

  componentDidMount() {
    const { roleDatasource = {} } = this.props;
    if (roleDatasource && !isNil(roleDatasource.id)) {
      this.handleSearch();
    }
  }

  componentDidUpdate(prevProps) {
    const { roleDatasource: prevRoleDatasource } = prevProps;
    const { roleDatasource, visible } = this.props;
    if (visible && roleDatasource && !isNil(roleDatasource.id)) {
      if ((prevRoleDatasource && prevRoleDatasource.id) !== roleDatasource.id) {
        this.handleSearch(undefined, !prevProps.visible && this.props.visible);
      }
    }
  }

  async reload() {
    const { cachePagination } = this.state;
    this.handleSearch(cachePagination);
  }

  async handleSearch(pagination, reOpen) {
    if (reOpen) {
      this.setState({
        dataSource: [],
        pagination: {},
        cachePagination: {},
      });
    } else {
      this.setState({
        cachePagination: pagination,
      });
    }
    const { handleFetchData, roleDatasource = {} } = this.props;
    const params = this.searchFormRef.current
      ? this.searchFormRef.current.props.form.getFieldsValue()
      : {};
    const res = await handleFetchData(
      parseParameters({
        ...params,
        ...pagination,
      })
    );
    if (res) {
      this.setState({
        ...res,
        dataSource: res.dataSource.map((item) => ({
          ...item,
          roleId: roleDatasource.id,
          [rowKey]: uuid(),
        })),
      });
    }
  }

  // SearchForm
  @Bind()
  async handleSearchFormAction() {
    await this.handleSearch();
  }

  // EditDataTable
  @Bind()
  async handleTableChange(page, filter, sorter) {
    await this.handleSearch({ page, sort: sorter });
  }

  @Bind()
  handleTableAdd() {
    const { resourceLevel = [], roleDatasource = {} } = this.props;
    const { dataSource = [], pagination } = this.state;
    const resourceLevelMap = {};
    resourceLevel.forEach((item) => {
      resourceLevelMap[item.value] = item.meaning;
    });
    const newUser = {
      [rowKey]: uuid(),
      isCreate: true,
      _status: 'create',
      roleId: roleDatasource.id,
    };
    // if (roleDatasource.level === 'site') {
    //   newUser.assignLevel = 'organization';
    //   newUser.assignLevelMeaning = resourceLevelMap.orginization;
    //   newUser.assignLevelValue = roleDatasource.tenantId;
    //   newUser.assignLevelValueMeaning = roleDatasource.tenantName;
    // }
    // 分配 用户 默认分配 租户 层级
    newUser.assignLevel = 'organization';
    newUser.assignLevelMeaning = resourceLevelMap.orginization;
    newUser.assignLevelValue = roleDatasource.tenantId;
    newUser.assignLevelValueMeaning = roleDatasource.tenantName;
    this.setState({
      dataSource: [newUser, ...dataSource],
      pagination: addItemsToPagination(1, dataSource.length, pagination),
    });
  }

  @Bind()
  async handleTableDelete(deleteRows) {
    const backDeleteRows = [];
    deleteRows.forEach((record) => {
      if (!record.isCreate) {
        const { _status: _, ...deleteRecord } = record;
        backDeleteRows.push(deleteRecord);
      }
    });
    if (backDeleteRows.length !== 0) {
      const { onDeleteMember } = this.props;
      const res = await onDeleteMember(
        backDeleteRows.map((memberRole) => ({
          memberType: 'user',
          memberId: memberRole.id,
          roleId: memberRole.roleId,
        }))
      );
      const responseRes = getResponse(res);
      if (responseRes) {
        notification.success();
      } else {
        // 调用删除接口失败, 取消删除行为
        return;
      }
    }
    const { dataSource = [] } = this.state;
    const noDeleteEditData = dataSource.filter(
      (item) => item._status && !deleteRows.some((record) => record[rowKey] === item[rowKey])
    );
    if (noDeleteEditData.length !== 0) {
      Modal.confirm({
        title: intl
          .get('hiam.roleManagement.view,message.title.isRefresh')
          .d('有未保存的数据, 是否刷新'),
        onOk: () => {
          this.reload();
        },
        onCancel: () => {
          const { pagination } = this.props;
          this.setState({
            dataSource: dataSource.filter(
              (item) => !deleteRows.some((record) => record[rowKey] === item[rowKey])
            ),
            pagination: delItemsToPagination(deleteRows.length, dataSource.length, pagination),
          });
        },
      });
    } else {
      this.reload();
    }
  }

  // Modal
  @Bind()
  handleOk() {
    const { dataSource } = this.state;
    const editDataSource = dataSource.filter((item) => item._status);
    const validateDataSource = getEditTableData(editDataSource);
    // 由于控制了 确认按钮, 所以 editDataSource 一定优质
    if (validateDataSource.length === editDataSource.length) {
      // 数据校验成功
      const { handleSave } = this.props;
      handleSave(
        validateDataSource.map((item) => {
          const { [rowKey]: _, isCreate, _status, id, ...newItem } = item;
          newItem.memberId = id;
          return newItem;
        }),
        false,
        () => {
          notification.success();
          // 成功后关闭 模态框
          this.handleClose();
        }
      );
    }
  }

  @Bind()
  async handleClose() {
    const { close } = this.props;
    this.searchFormRef.current.props.form.resetFields();
    close();
  }

  render() {
    const {
      visible = false,
      title,
      processing = {},
      roleDatasource = {},
      tenantRoleLevel,
      path,
    } = this.props;
    const { dataSource = [], pagination } = this.state;
    return (
      <Modal
        width={700}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        title={title}
        onCancel={this.handleClose}
        onOk={this.handleOk}
        confirmLoading={processing.save}
        cancelButtonProps={{ disabled: processing.save || processing.delete }}
        okButtonProps={{
          disabled:
            dataSource.filter((record) => record._status).length === 0 ||
            processing.delete ||
            processing.query,
        }}
      >
        <SearchForm
          wrappedComponentRef={this.searchFormRef}
          onSearch={this.handleSearchFormAction}
        />
        <EditDataTable
          path={path}
          tenantRoleLevel={tenantRoleLevel}
          role={roleDatasource}
          dataSource={dataSource}
          pagination={pagination}
          onChange={this.handleTableChange}
          onTableAdd={this.handleTableAdd}
          onTableDelete={this.handleTableDelete}
          deleteLoading={processing.delete}
          queryLoading={processing.query}
          saveLoading={processing.save}
        />
      </Modal>
    );
  }
}
