/**
 * SecGrpDetailPage - 安全组维护详情页
 * // TODO c7npro的table树结构性能差，改用hzero的table，注释代码暂不删除。
 * @date: 2019-10-25
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button } from 'choerodon-ui/pro';
import { Tag, Checkbox } from 'choerodon-ui';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import { getResponse } from 'utils/utils';
import notification from 'utils/notification';

//  import VisitPermissionDS from '../Stores/VisitPermissionDS';
import {
  batchAssignPermissionSets,
  batchUnassignPermissionSets,
  queryPermissionMenus,
  queryOthersPermissionMenus,
} from '@/services/securityGroupService';

export default class VisitPermission extends Component {
  constructor(props) {
    super(props);
    // this.visitPermissionDS = new DataSet(VisitPermissionDS(secGrpId));
    this.defaultExpandedRowKeys = [];
    this.state = {
      dataSource: [],
      expandedRowKeys: [],
    };
  }

  componentDidMount() {
    this.fetchVisitPermission();
  }

  @Bind()
  async fetchVisitPermission() {
    const { isSelf = false, roleId, secGrpSource } = this.props;
    this.setState({ loading: true });
    const { secGrpId } = this.props;
    let response;
    if (isSelf) {
      response = await queryPermissionMenus({ secGrpId });
    } else {
      response = await queryOthersPermissionMenus({ secGrpId, roleId, secGrpSource });
    }
    const dataSource = this.assignListData(getResponse(response));
    this.setState({ dataSource, loading: false });
  }

  /**
   * 组装新dataSource
   * @function assignListData
   * @param {!Array} [collections = []] - 树节点集合
   * @returns {Array} - 新的dataSourcee
   */
  @Bind()
  assignListData(collections = []) {
    return collections.map((n) => {
      const m = n;
      m.key = n.id;
      if (isEmpty(m.subMenus)) {
        m.subMenus = null;
      } else {
        m.subMenus = this.assignListData(m.subMenus);
        this.defaultExpandedRowKeys.push(m.id);
        const checkedCount = m.subMenus.filter((o) => o.checkedFlag === 'Y').length;
        const indeterminateCount = m.subMenus.filter((o) => o.checkedFlag === 'P').length;
        m.checkedFlag =
          // eslint-disable-next-line no-nested-ternary
          checkedCount === m.subMenus.length
            ? 'Y'
            : // eslint-disable-next-line no-nested-ternary
            checkedCount === 0
            ? indeterminateCount === 0
              ? null
              : 'P'
            : 'P';
      }
      return m;
    });
  }

  /**
   * 勾选改变时
   * @param {object} record - 表格行数据
   */
  @Bind()
  onCheckboxChange(record) {
    const setIdList = [];
    const getSubSetIdList = (collections = []) => {
      collections.forEach((n) => {
        if (n.type === 'ps') {
          setIdList.push(n.id);
        }
        if (!isEmpty(n.subMenus)) {
          getSubSetIdList(n.subMenus);
        }
      });
    };

    if (record.type === 'ps') {
      setIdList.push(record.id);
    }

    if (!isEmpty(record.subMenus)) {
      getSubSetIdList(record.subMenus);
    }

    const flag = record.checkedFlag !== 'Y';
    this.handlePermissionSets(setIdList, flag);
  }

  /**
   * 批量添加或取消勾选
   * @param {array} setIdList - 数据改变数组
   * @param {true} flag - 是否勾选
   */
  @Bind()
  async handlePermissionSets(setIdList, flag) {
    const { secGrpId } = this.props;
    const res = flag
      ? await batchAssignPermissionSets(secGrpId, setIdList)
      : await batchUnassignPermissionSets(secGrpId, setIdList);
    if (res && res.failed) {
      notification.error({ description: res.message });
    } else {
      notification.success();
      this.fetchVisitPermission();
    }
  }

  /**
   * 渲染操作列
   * @param {object} record - 表格行数据
   */
  @Bind()
  operationRender(_, record) {
    const { isSelf } = this.props;
    const checkboxProps = {
      indeterminate: record.checkedFlag === 'P',
      checked: record.checkedFlag === 'Y',
      onChange: this.onCheckboxChange.bind(this, record),
    };
    return <Checkbox {...checkboxProps} disabled={!isSelf} />;
  }

  /**
   * expandAll - 全部展开
   */
  @Bind()
  expandAll() {
    this.setState({
      expandedRowKeys: this.defaultExpandedRowKeys,
    });
  }

  /**
   * 全部收起
   */
  @Bind()
  collapseAll() {
    this.setState({
      expandedRowKeys: [],
    });
  }

  /**
   * 展开树
   * @param {boolean} expanded - 是否展开
   * @param {record} record - 当前行数据
   */
  @Bind()
  onExpand(expanded, record) {
    const { expandedRowKeys = [] } = this.state;
    this.setState({
      expandedRowKeys: expanded
        ? expandedRowKeys.concat(record.key)
        : expandedRowKeys.filter((o) => o !== record.key),
    });
  }

  get columns() {
    const { isSelf } = this.props;
    return [
      {
        title: intl.get('hiam.roleManagement.model.roleManagement.permissionName').d('权限名称'),
        dataIndex: 'name',
      },
      {
        title: intl.get('hiam.roleManagement.model.roleManagement.permission.Type').d('权限类型'),
        dataIndex: 'permissionType',
        width: 150,
        render: (value = '', record) => {
          const texts = {
            api: intl.get('hiam.roleManagement.view.message.api').d('API'),
            button: intl.get('hiam.roleManagement.view.message.button').d('按钮'),
            table: intl.get('hiam.roleManagement.view.message.table').d('表格列'),
            formItem: intl.get('hiam.roleManagement.view.message.formItem').d('表单项'),
            formField: intl.get('hiam.roleManagement.view.message.formField').d('表单域'),
          };
          const valueList = value.split(',') || [];
          const text = valueList.map((item) => (texts[item] ? texts[item] : '')) || [];
          return (
            record.type === 'ps' && (
              <Tag color={value === 'api' ? 'green' : 'orange'}>{text.join()}</Tag>
            )
          );
        },
      },
      isSelf && {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 85,
        render: this.operationRender,
      },
    ].filter(Boolean);
  }

  render() {
    const { dataSource, expandedRowKeys, loading } = this.state;
    const tableProps = {
      columns: this.columns,
      bordered: true,
      dataSource,
      loading,
      childrenColumnName: 'subMenus',
      pagination: false,
      expandedRowKeys,
      onExpand: this.onExpand,
    };

    return (
      <>
        <div className="action" style={{ textAlign: 'right', marginBottom: '10px' }}>
          <Button onClick={() => this.collapseAll()} style={{ marginRight: 8 }}>
            {intl.get(`hzero.common.button.collapseAll`).d('全部收起')}
          </Button>
          <Button onClick={this.expandAll}>
            {intl.get(`hzero.common.button.expandAll`).d('全部展开')}
          </Button>
        </div>
        <Table {...tableProps} />
        {/* <Table
           dataSet={this.visitPermissionDS}
           columns={this.columns}
           buttons={['expandAll', 'collapseAll', 'save']}
           mode='tree'
         /> */}
      </>
    );
  }
}
