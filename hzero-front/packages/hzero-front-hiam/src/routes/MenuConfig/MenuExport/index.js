import React, { PureComponent } from 'react';
import { Modal, Table, Checkbox } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { enableRender, TagRender } from 'utils/renderer';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';
import Icons from 'components/Icons';
import styles from '../index.less';

const modelPrompt = 'hiam.menuConfig.model.menuConfig';
const commonPrompt = 'hzero.common';
const menuIconStyle = {
  width: 14,
  height: 14,
  lineHeight: '14px',
};

export default class MenuExport extends PureComponent {
  state = {
    setIdList: [],
  };

  /**
   * 取消
   */
  @Bind()
  handleCancel() {
    const { onCancel = e => e } = this.props;
    onCancel();
  }

  /**
   * 导出
   */
  @Bind()
  handleOk() {
    const { rowSelection = {}, onExport = e => e } = this.props;
    if (rowSelection.selectedRowKeys.length) {
      onExport();
    } else {
      notification.warning({
        message: intl.get('hiam.menuConfig.view.message.export.warning').d('请至少选择一行数据'),
      });
    }
  }

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

  @Bind()
  operationRender(text, record) {
    const { setIdList = [] } = this.state;
    const checkboxProps = {
      indeterminate: record.checkedFlag === 'P',
      checked: setIdList.find(item => item === record.id),
      onChange: () => this.handleCheckboxChange(record),
    };
    return <Checkbox {...checkboxProps} />;
  }

  render() {
    const {
      visible,
      loading,
      menuTypeList = [],
      rowSelection,
      dataSource,
      confirmLoading,
    } = this.props;
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
        render: code => <Icons type={code} size={14} style={menuIconStyle} />,
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
        render: enableRender,
      },
    ];

    const tableProps = {
      loading,
      dataSource,
      columns,
      rowKey: 'id',
      pagination: false,
      bordered: true,
      childrenColumnName: 'subMenus',
      scroll: { x: tableScrollWidth(columns) },
      rowSelection,
      className: styles['hiam-menu-config-export'],
    };
    return (
      <Modal
        width={900}
        title={intl.get('hiam.menuConfig.view.message.title.exportMenu').d('导出客户化菜单')}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        visible={visible}
        onOk={this.handleOk}
        onCancel={this.handleCancel}
        confirmLoading={confirmLoading}
        destroyOnClose
      >
        <Table {...tableProps} />
      </Modal>
    );
  }
}
