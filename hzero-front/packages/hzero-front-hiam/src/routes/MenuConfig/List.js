/**
 * Table - 菜单配置 - 列表页面表格
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Icon, Table, Popconfirm } from 'hzero-ui';

import Icons from 'components/Icons';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { enableRender, TagRender, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, tableScrollWidth } from 'utils/utils';

const viewButtonPrompt = 'hiam.menuConfig.view.button';
const modelPrompt = 'hiam.menuConfig.model.menuConfig';
const commonPrompt = 'hzero.common';

const tenantRoleLevel = isTenantRoleLevel();

const menuIconStyle = {
  width: 14,
  height: 14,
  lineHeight: '14px',
};

export default class List extends PureComponent {
  defaultTableRowKey = 'id';

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

  operationRender(text, record) {
    const {
      path,
      processingEnableRow,
      processing = {},
      handleEdit = (e) => e,
      handleEnable = (e) => e,
      handleEditPermissionSet = (e) => e,
      onCreate = (e) => e,
      onCopy = (e) => e,
      handleDelete = (e) => e,
    } = this.props;
    // 租户级应该能看到他的菜单挂载到了哪个目录下了，但是，需要限制，租户层功能只能看到标准菜单，但是不能做任何更改（没有任何操作按钮）
    // changeLog:
    // FIXME: 租户级可以在平台菜单下进行新建客制化菜单
    if (tenantRoleLevel && record.customFlag === 0) {
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

      return operatorRender(operators, record, { limit: 3 });
    }
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
          onClick={() => handleEdit(record)}
        >
          {intl.get(`${commonPrompt}.button.edit`).d('编辑')}
        </ButtonPermission>
      ),
      len: 2,
      title: intl.get(`${commonPrompt}.button.edit`).d('编辑'),
    });
    if (processing.setEnable && record.id === processingEnableRow) {
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
    } else if (record.enabledFlag === 1) {
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
            onClick={() => handleEnable(record, 'disable')}
          >
            {intl.get(`${commonPrompt}.status.disable`).d('禁用')}
          </ButtonPermission>
        ),
        len: 2,
        title: intl.get(`${commonPrompt}.status.disable`).d('禁用'),
      });
    } else {
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
            onClick={() => handleEnable(record, 'enable')}
          >
            {intl.get(`${commonPrompt}.status.enable`).d('启用')}
          </ButtonPermission>
        ),
        len: 2,
        title: intl.get(`${commonPrompt}.status.enable`).d('启用'),
      });
    }
    if (['menu', 'inner-link', 'link', 'window'].includes(record.type)) {
      operators.push({
        key: 'permission',
        ele: (
          <ButtonPermission
            type="text"
            permissionList={[
              {
                code: `${path}.button.permissionSet`,
                type: 'button',
                meaning: '菜单配置-列表权限集',
              },
            ]}
            onClick={() => handleEditPermissionSet(record)}
          >
            {intl.get(`${viewButtonPrompt}.button.permissionSet`).d('权限集')}
          </ButtonPermission>
        ),
        len: 3,
        title: intl.get(`${viewButtonPrompt}.button.permissionSet`).d('权限集'),
      });
    }
    operators.push({
      key: 'permission',
      ele: (
        <ButtonPermission
          type="text"
          permissionList={[
            {
              code: `${path}.button.copy`,
              type: 'button',
              meaning: '菜单配置-复制菜单',
            },
          ]}
          onClick={() => onCopy(record)}
        >
          {intl.get(`${commonPrompt}.button.copy`).d('复制')}
        </ButtonPermission>
      ),
    });
    if (!record.enabledFlag && ((tenantRoleLevel && record.customFlag === 1) || !tenantRoleLevel)) {
      operators.push({
        key: 'delete',
        ele: (
          <Popconfirm
            title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
            onConfirm={() => handleDelete(record)}
          >
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.delete`,
                  type: 'button',
                  meaning: '菜单配置-删除',
                },
              ]}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
          </Popconfirm>
        ),
        len: 2,
        title: intl.get('hzero.common.button.delete').d('删除'),
      });
    }

    return operatorRender(operators, record, { limit: 3 });
  }

  render() {
    const {
      dataSource = [],
      // handleEdit = e => e,
      // handleDelete = e => e,
      // handleEditPermissionSet = e => e,
      // handleEnable = e => e,
      processing = {},
      processingDeleteRow,
      processingEnableRow,
      levelCode = [],
      menuTypeList = [],
      expandClick = (e) => e,
      ...others
    } = this.props;
    const filteredMenuTypeList = menuTypeList.filter((item) => item.value !== 'ps');
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
        render: (code) => <Icons type={code} size={14} style={menuIconStyle} />,
      },
      {
        title: intl.get(`${modelPrompt}.code`).d('编码'),
        dataIndex: 'code',
        onCell: this.onCell.bind(this),
      },
      !tenantRoleLevel && {
        title: intl.get(`${modelPrompt}.level`).d('层级'),
        dataIndex: 'level',
        width: 80,
        render: (text) => {
          let levelMeaning = '';
          levelCode
            .filter((o) => o.value !== 'org')
            .forEach((element) => {
              if (text === element.value) {
                levelMeaning = element.meaning;
              }
            });
          return levelMeaning;
        },
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
        width: 200,
        fixed: 'right',
        render: this.operationRender.bind(this),
      },
    ].filter(Boolean);
    const tableProps = {
      columns,
      dataSource,
      pagination: false,
      bordered: true,
      rowKey: this.defaultTableRowKey,
      loading: processing.query,
      childrenColumnName: 'subMenus',
      scroll: { x: tableScrollWidth(columns) },
      onExpand: expandClick,
      ...others,
    };
    return <Table {...tableProps} />;
  }
}
