import React from 'react';
import { Table } from 'hzero-ui';
import request from 'utils/request';
import { getCurrentOrganizationId, getResponse } from 'utils/utils';
import { getEnvConfig } from 'utils/iocUtils';
import intl from 'utils/intl';

export default class CustomTable extends React.Component {
  constructor(props) {
    super(props);
    const { customCode, columns } = props;
    this.state = {
      init: customCode === undefined,
      customColumns: [],
      tableColumns: columns,
      saveCustomColumnsLoading: false, // 保存 customColumns 时的loading
    };

    this.showLoading = this.showLoading.bind(this);
    this.hiddenLoading = this.hiddenLoading.bind(this);
    this.config = getEnvConfig();
  }

  componentDidMount() {
    const { customCode } = this.props;
    if (customCode) {
      this.loadHiddenColumns(customCode);
    }
  }

  render() {
    const {
      init,
      customColumns = [],
      tableColumns = [],
      saveCustomColumnsLoading = false,
    } = this.state;
    const { columns, ...restProps } = this.props;

    // 用户个性化 Table 菜单的国际化
    const filterBarLocale = {
      display: intl.get('hzero.common.userUI.view.filterBar.display').d('显示'),
      field: intl.get('hzero.common.userUI.view.filterBar.field').d('字段'),
      fixedLeft: intl.get('hzero.common.userUI.view.filterBar.lock').d('lock'),
      orderSeq: intl.get('hzero.common.userUI.view.filterBar.orderSeq').d('排序'),
    };
    return init ? (
      <Table
        columns={tableColumns}
        loading={saveCustomColumnsLoading}
        {...restProps}
        filterBarLocale={filterBarLocale}
        customColumns={customColumns}
        onCustomColumnFilter={this.onColumnFilter}
      />
    ) : null;
  }

  showLoading(loadingProp) {
    this.setState({
      [loadingProp]: true,
    });
  }

  hiddenLoading(loadingProp) {
    this.setState({
      [loadingProp]: false,
    });
  }

  async loadHiddenColumns(code) {
    const tenantId = getCurrentOrganizationId();
    try {
      const customColumns = await request(
        `${this.config.HZERO_PLATFORM}/v1/${tenantId}/custom/table/${code}`
      );
      if (customColumns && customColumns instanceof Array) {
        const tenantCustomColumns = customColumns.filter((col) => col.dimensionType === 'TENANT');
        const userCustomColumns = customColumns.filter((col) => col.dimensionType === 'USER');
        const { customColumns: calculateCustomColumns, tableColumns } = this.calculateCustomColumns(
          tenantCustomColumns,
          userCustomColumns
        );
        this.setState({
          customColumns: calculateCustomColumns,
          tableColumns,
        });
      }
    } finally {
      this.setState({
        init: true,
      });
    }
  }

  /**
   * 保存修改的 customColumns
   * 如果保存成功 更新 Table 中的 customColumns
   */
  onColumnFilter = (customColumns = []) => {
    const { customCode } = this.props;
    const tenantId = getCurrentOrganizationId();
    if (customColumns && customColumns.length > 0) {
      this.showLoading('saveCustomColumnsLoading');
      return request(`${this.config.HZERO_PLATFORM}/v1/${tenantId}/custom/table/${customCode}`, {
        method: 'POST',
        body: customColumns,
      })
        .then((res) => {
          if (getResponse(res)) {
            this.setState({
              customColumns,
            });
          }
        })
        .finally(() => {
          this.hiddenLoading('saveCustomColumnsLoading');
        });
    }
  };

  /**
   * 通过比较 租户级 和 用户级 的区别 来 计算出 合并过后的 customColumns
   * 以及 过滤后的 columns
   */
  calculateCustomColumns(tenantCustomColumns = [], userCustomColumns = []) {
    const hiddenByTenantFields = []; // 存储在 租户级隐藏的 field
    const customColumns = []; // 存储 合并的 customColumn
    const { columns = [] } = this.props; // 拿到原始的 columns
    tenantCustomColumns.forEach((customColumn) => {
      if (customColumn.hidden === 1) {
        hiddenByTenantFields.push(customColumn.fieldKey);
      } else {
        customColumns.push(customColumn);
      }
    });
    userCustomColumns.forEach((customColumn) => {
      const hasHiddenByTenant = hiddenByTenantFields.some(
        (hiddenFieldKey) => hiddenFieldKey === customColumn.fieldKey
      );
      if (hasHiddenByTenant) {
        return;
      }
      const tenantCustomColumnIndex = customColumns.findIndex(
        (c) => c.fieldKey === customColumn.fieldKey
      );
      if (tenantCustomColumnIndex !== -1) {
        customColumns[tenantCustomColumnIndex] = customColumn;
      } else {
        customColumns.push(customColumn);
      }
    });
    // 计算 经过租户级 过滤的 columns
    const tableColumns = columns.filter(
      (column, index) =>
        // 只要 hiddenByTenantFields 中有 就 不出现在新的 TableColumns 中
        !hiddenByTenantFields.some(
          (hiddenFieldKey) => (column.key || column.dataIndex || index || '') === hiddenFieldKey
        )
    );
    return {
      tableColumns,
      customColumns,
    };
  }
}
