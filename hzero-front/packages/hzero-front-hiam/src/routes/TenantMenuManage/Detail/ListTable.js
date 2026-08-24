import React from 'react';
import { Table, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { enableRender, TagRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import Icons from 'components/Icons';

import { Button as ButtonPermission } from 'components/Permission';

const modelPrompt = 'hiam.tenantMenu.model.tenantMenu';
const commonPrompt = 'hzero.common';
const menuIconStyle = {
  width: 14,
  height: 14,
  lineHeight: '14px',
};
export default class ListTable extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      loading: false,
    };
  }

  defaultTableRowKey = 'id';

  @Bind()
  onCell() {
    return {
      style: {
        overflow: 'hidden',
        maxWidth: 220,
        textOverflow: 'ellipsis',
        whiteSpace: 'nowrap',
      },
      onClick: (e) => {
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
  handleEnable(record, type) {
    const { handleEnable } = this.props;
    this.setState({
      loading: true,
    });
    handleEnable(record, type);
    this.setState({
      loading: false,
    });
  }

  render() {
    const {
      menuDataSource = [],
      path,
      queryTreeListLoading,
      onCopy = (e) => e,
      onCreate = (e) => e,
      handleEdit = (e) => e,
      // handleEnable = (e) => e,
      menuTypeList = [],
      handleEditPermissionSet = (e) => e,
      processing,
      processingEnableRow,
      id,
      processing: { enableLoading, disableLoading },
      expandedRowKeys = [],
    } = this.props;
    const { loading } = this.state;
    const filteredMenuTypeList = menuTypeList.filter((item) => item.value !== 'ps');
    const menuColumns = [
      {
        title: intl.get(`${modelPrompt}.name`).d('目录/菜单'),
        dataIndex: 'name',
        fixed: 'left',
        width: 300,
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
        render: (code) => <Icons type={code} size={14} style={menuIconStyle} />,
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
        render: (value) => {
          const statusList = filteredMenuTypeList.map((item) => ({
            status: item.value,
            // eslint-disable-next-line no-nested-ternary
            color: item.value === 'root' ? 'blue' : item.value === 'dir' ? 'green' : 'orange',
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
      {
        title: intl.get(`${commonPrompt}.table.column.option`).d('操作'),
        width: 220,
        fixed: 'right',
        render: (text, record) => {
          const operators = [];
          if (record.type === 'root' || record.type === 'dir') {
            operators.push({
              key: 'create',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.createList`,
                      type: 'button',
                      meaning: '菜单配置-列表新建',
                    },
                  ]}
                  onClick={() => onCreate(record, true)}
                >
                  {intl.get(`${commonPrompt}.button.create`).d('新建')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get(`${commonPrompt}.button.create`).d('新建'),
            });
          }
          if (record.customFlag === 1 && record.tenantId.toString() === id) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.editList`,
                      type: 'button',
                      meaning: '菜单配置-列表编辑',
                    },
                  ]}
                  onClick={() => handleEdit(record, true)}
                >
                  {intl.get(`${commonPrompt}.button.edit`).d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get(`${commonPrompt}.button.edit`).d('编辑'),
            });
          } else {
            operators.push({
              key: 'view',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.view`,
                      type: 'button',
                      meaning: '菜单配置-查看',
                    },
                  ]}
                  onClick={() => handleEdit(record, false)}
                >
                  {intl.get(`${commonPrompt}.button.view`).d('查看')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get(`${commonPrompt}.button.view`).d('查看'),
            });
          }
          if (
            (processing.enableLoading || processing.disableLoading) &&
            record.id === processingEnableRow
          ) {
            operators.push({
              key: 'enable-disabled',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.loading`,
                      type: 'button',
                      meaning: '菜单配置-loading',
                    },
                  ]}
                  key="loading"
                >
                  <Icon type="loading" />
                </ButtonPermission>
              ),
              len: 4,
            });
          } else if (
            record.enabledFlag === 1 &&
            record.customFlag === 1 &&
            record.tenantId.toString() === id
          ) {
            operators.push({
              key: 'enable-disabled',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.disableList`,
                      type: 'button',
                      meaning: '菜单配置-列表禁用',
                    },
                  ]}
                  onClick={() => this.handleEnable(record, 'disable')}
                >
                  {intl.get(`${commonPrompt}.status.disable`).d('禁用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get(`${commonPrompt}.status.disable`).d('禁用'),
            });
          } else if (
            record.enabledFlag === 0 &&
            record.customFlag === 1 &&
            record.tenantId.toString() === id
          ) {
            operators.push({
              key: 'enable-disabled',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enableList`,
                      type: 'button',
                      meaning: '菜单配置-列表启用',
                    },
                  ]}
                  onClick={() => this.handleEnable(record, 'enable')}
                >
                  {intl.get(`${commonPrompt}.status.enable`).d('启用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get(`${commonPrompt}.status.enable`).d('启用'),
            });
          }
          if (
            ['menu', 'inner-link', 'link'].includes(record.type) &&
            record.customFlag === 1 &&
            record.tenantId.toString() === id
          ) {
            operators.push({
              key: 'permissionSet',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.permissionSet`,
                      type: 'button',
                      meaning: '租户菜单管理-列表权限集',
                    },
                  ]}
                  onClick={() => handleEditPermissionSet(record)}
                >
                  {intl.get('hzero.common.button.permissionSet').d('权限集')}
                </ButtonPermission>
              ),
              len: 3,
              title: intl.get('hzero.common.button.permissionSet').d('权限集'),
            });
          }
          operators.push(
            record.customFlag &&
              record.tenantId.toString() === id && {
                key: 'copy',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.copy`,
                        type: 'button',
                        meaning: '租户菜单管理-复制菜单',
                      },
                    ]}
                    onClick={() => onCopy(record)}
                  >
                    {intl.get('hzero.common.button.copy').d('复制')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.copy').d('复制'),
              }
          );
          return operatorRender(operators);
        },
      },
    ];
    const tableProps = {
      bordered: true,
      pagination: false,
      uncontrolled: true,
      columns: menuColumns,
      loading: queryTreeListLoading || enableLoading || disableLoading || loading,
      dataSource: menuDataSource,
      childrenColumnName: 'subMenus',
      rowKey: this.defaultTableRowKey,
      scroll: { x: tableScrollWidth(menuColumns) },
      expandedRowKeys,
    };
    return <Table {...tableProps} />;
  }
}
