import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { enableRender, TagRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import Icons from 'components/Icons';

const modelPrompt = 'hiam.tenantMenu.model.tenantMenu';
const commonPrompt = 'hzero.common';
const menuIconStyle = {
  width: 14,
  height: 14,
  lineHeight: '14px',
};

export default class CopyMenuTable extends PureComponent {
  @Bind()
  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 220,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: e => {
        const { target } = e;
        if (target.style.whiteSpace === 'normal') {
          target.style.whiteSpace = 'nowrap';
        } else {
          target.style.whiteSpace = 'normal';
        }
      },
    };
  }

  render() {
    const { loading, menuTypeList = [], dataSource, rowSelection } = this.props;
    const columns = [
      {
        title: intl.get(`${modelPrompt}.name`).d('目录/菜单'),
        dataIndex: 'name',
        fixed: 'left',
        width: 200,
      },
      {
        title: intl.get(`${modelPrompt}.parentName`).d('上级目录'),
        width: 120,
        dataIndex: 'parentName',
      },
      {
        title: intl.get(`${modelPrompt}.quickIndex`).d('快速索引'),
        width: 120,
        dataIndex: 'quickIndex',
      },
      {
        title: intl.get(`${modelPrompt}.icon`).d('图标'),
        width: 60,
        dataIndex: 'icon',
        render: value => <Icons type={value} size={14} style={menuIconStyle} />,
      },
      {
        title: intl.get(`${modelPrompt}.code`).d('编码'),
        dataIndex: 'code',
        onCell: this.onCell.bind(this),
      },
      {
        title: intl.get(`${modelPrompt}.menuType`).d('类型'),
        dataIndex: 'type',
        width: 120,
        render: value => {
          const statusList = menuTypeList.map(item => ({
            status: item.value,
            color:
              item.value === 'root'
                ? 'blue'
                : item.value === 'dir'
                ? 'green'
                : item.value === 'menu'
                ? 'orange'
                : 'purple',
            text: item.meaning,
          }));
          return TagRender(value, statusList);
        },
      },
      {
        title: intl.get(`${modelPrompt}.sort`).d('序号'),
        dataIndex: 'sort',
        width: 60,
      },
      {
        title: intl.get(`${modelPrompt}.description`).d('描述'),
        dataIndex: 'description',
        width: 200,
        onCell: this.onCell.bind(this),
      },
      {
        title: intl.get(`${commonPrompt}.status`).d('状态'),
        dataIndex: 'enabledFlag',
        width: 120,
        fixed: 'right',
        render: enableRender,
      },
    ].filter(Boolean);

    const tableProps = {
      loading,
      columns,
      dataSource,
      pagination: false,
      bordered: true,
      rowKey: 'id',
      childrenColumnName: 'subMenus',
      rowSelection,
      scroll: { x: tableScrollWidth(columns) },
    };
    return <Table {...tableProps} />;
  }
}
