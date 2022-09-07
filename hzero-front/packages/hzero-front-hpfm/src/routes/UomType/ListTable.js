/*
 * ListTable - 计量单位类型定义数据表格显示
 * @date: 2018/08/07 14:41:37
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Table } from 'hzero-ui';
import classNames from 'classnames';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';

import styles from './index.less';

const promptCode = 'hpfm.uomType';
/**
 * 计量单位数据表单显示
 * @extends {Component} - React.Component
 * @reactProps {Function} showEditModal // 显示编辑模态框
 * @reactProps {Object} form
 * @return React.element
 */

export default class ListTable extends Component {
  /**
   * 显示编辑模态框
   * @param {obj} record 当前行数据
   */
  @Bind()
  showEditModal(record) {
    this.props.editLine(record);
  }

  render() {
    const { loading, dataSource, onSearch, pagination, match } = this.props;
    const columns = [
      {
        title: intl.get(`${promptCode}.model.uomType.uomTypeCode`).d('单位类型代码'),
        dataIndex: 'uomTypeCode',
      },
      {
        title: intl.get(`${promptCode}.model.uomType.uomTypeName`).d('单位类型名称'),
        dataIndex: 'uomTypeName',
        width: 150,
      },
      {
        title: intl.get(`${promptCode}.model.uomType.baseUomCode`).d('基本单位代码'),
        dataIndex: 'baseUomCode',
        width: 150,
      },
      {
        title: intl.get(`${promptCode}.model.uomType.baseUomName`).d('基本单位名称'),
        dataIndex: 'baseUomName',
        width: 150,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        key: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 60,
        fixed: 'right',
        render: (val, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '计量单位类型定义-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.showEditModal(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    const editTableProps = {
      loading,
      columns,
      scroll: { x: tableScrollWidth(columns) },
      dataSource,
      pagination,
      bordered: true,
      rowKey: 'uomTypeId',
      onChange: page => onSearch(page),
      className: classNames(styles['uom-list']),
    };
    return <Table {...editTableProps} />;
  }
}
