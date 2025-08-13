/**
 * FieldPermissionDrawer - 字段权限配置 - 侧滑
 * @date: 2019-10-30
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { DataSet, Table, Lov, Button, Modal, Select, TextField } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import { observer } from 'mobx-react';

import { HZERO_IAM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';
import intl from 'utils/intl';

import { fieldPermissionDrawerDS } from '@/stores/SecurityGroupDS';

const FieldModalKey = Modal.key();
const organizationId = getCurrentOrganizationId();
const levelUrl = isTenantRoleLevel() ? `/${organizationId}` : '';
let modal;

@observer
class FieldPermissionDrawer extends Component {
  configDS = new DataSet({
    ...fieldPermissionDrawerDS(),
    transport: {
      read: ({ data, params }) => {
        const { record = {}, isSelf = false, roleId, secGrpId, secGrpSource } = this.props;
        const permissionId = record.get('id');
        let newParams = { ...data, ...params };
        if (!isSelf) {
          newParams = {
            ...newParams,
            roleId,
            secGrpSource,
          };
        }
        return {
          url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-acl-fields/${secGrpId}/${permissionId}/fields`,
          params: newParams,
          method: 'get',
        };
      },
      submit: ({ data }) => {
        const { record = {} } = this.props;
        const permissionId = record.get('id');
        let nextData = [...data];
        nextData = nextData.map((val) => {
          const nextVal = { ...val };
          const { __id, _status, ...rest } = nextVal;
          return { ...rest, _status: _status === 'add' ? 'create' : _status, permissionId };
        });
        const { secGrpId } = this.props;
        return {
          url: `${HZERO_IAM}/v1${levelUrl}/sec-grp-acl-fields/${secGrpId}/operate`,
          data: nextData,
        };
      },
    },
  });

  constructor(props) {
    super(props);
    this.props.callback(this.configDS);
  }

  async componentDidMount() {
    const { record = {} } = this.props;
    const permissionId = record.get('id');
    const fieldLov = this.configDS.getField('fieldLov');
    fieldLov.setLovPara('permissionDimension', 'ROLE');
    fieldLov.setLovPara('permissionId', permissionId);
    fieldLov.setLovPara('organizationId', organizationId);
  }

  get columns() {
    const { isSelf } = this.props;
    return [
      {
        name: 'fieldLov',
        editor: (record) => {
          if (record.status === 'add') return <Lov noCache onChange={this.handleChangeField} />;
        },
      },
      { name: 'fieldTypeMeaning' },
      {
        name: 'permissionType',
        editor: (record) => {
          if (record.status === 'add' || record.status === 'update') {
            return <Select onChange={(value) => this.selectChange(value, record)} />;
          }
        },
      },
      {
        name: 'permissionRule',
        editor: (record) => {
          if (record.status === 'add' || record.get('editing') === true) {
            if (record.get('permissionType') === 'DESENSITIZE') {
              return <TextField />;
            }
            return null;
          }
        },
      },
      {
        name: 'remark',
        editor: (record) => {
          if (record.status === 'add' || record.get('editing') === true) return <TextField />;
        },
      },
      isSelf && {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 200,
        command: ({ record }) => this.commands(record),
      },
    ].filter(Boolean);
  }

  /**
   * 行内操作按钮组
   */
  @Bind()
  commands(record) {
    return [
      <span className="action-link">
        {
          // eslint-disable-next-line no-nested-ternary
          record.status === 'add' ? (
            <a onClick={() => this.handleClear(record)}>
              {intl.get('hzero.common.button.clean').d('清除')}
            </a>
          ) : // eslint-disable-next-line no-nested-ternary
          record.get('editing') === true ? (
            <a onClick={() => this.handleCancel(record)}>
              {intl.get('hzero.common.button.cancel').d('取消')}
            </a>
          ) : record.get('deleteEnableFlag') === 1 ? (
            <>
              <a onClick={() => this.handleEdit(record)} disabled={record.status === 'delete'}>
                {intl.get('hzero.common.button.edit').d('编辑')}
              </a>
              <a onClick={() => this.handleDelete(record)} disabled={record.status === 'delete'}>
                {intl.get('hzero.common.button.delete').d('删除')}
              </a>
            </>
          ) : null
        }
      </span>,
    ];
  }

  /**
   * 编辑
   * @param {object} record - 行数据
   */
  @Bind()
  handleEdit(record) {
    record.set('editing', true);
  }

  /**
   * 取消
   * @param {object} record - 行数据
   */
  @Bind()
  handleCancel(record) {
    record.reset();
  }

  /**
   * 清除
   * @param {object} record - 行数据
   */
  @Bind()
  handleClear(record) {
    this.configDS.remove(record);
  }

  /**
   * 删除
   * @param {object} record - 行数据
   */
  @Bind()
  handleDelete(record) {
    this.configDS.remove(record);
  }

  /**
   * 新建接口字段权限配置
   */
  @Bind()
  async handleCreateConfig() {
    const { roleId } = this.props;
    const res = await this.configDS.validate();
    if (res) {
      await this.configDS.create({});
      this.configDS.current.getField('fieldLov').setLovPara('roleId', roleId);
    } else {
      return false;
    }
  }

  /**
   * 选择框值改变时触发
   * @param {string} value
   * @param {object} record
   */
  @Bind()
  selectChange(value, record) {
    if (value !== 'DESENSITIZE') {
      record.set('permissionRule', '');
    }
  }

  render() {
    const { isSelf } = this.props;
    return (
      <Table
        dataSet={this.configDS}
        columns={this.columns}
        queryFieldsLimit={2}
        buttons={
          isSelf
            ? [
                // eslint-disable-next-line react/jsx-indent
                <Button
                  key="create"
                  icon="playlist_add"
                  color="primary"
                  funcType="flat"
                  onClick={this.handleCreateConfig}
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </Button>,
              ]
            : []
        }
      />
    );
  }
}

let fieldConfigDS;
function callback(configDS) {
  fieldConfigDS = configDS;
}

// 更新或新建后提交
async function handleSubmit() {
  const res = await fieldConfigDS.submit();
  if (!isEmpty(res) && res.failed && res.message) {
    notification.error({
      message: res.message,
    });
  } else if (!isEmpty(res) && res.success) {
    fieldConfigDS.query();
  } else if (res === undefined) {
    notification.warning({
      message: intl.get('hiam.securityGroup.view.message.form.noChange').d('表单未做修改'),
    });
  }
  return false;
}

export default function openConfigDrawer(record, secGrpId, isSelf, roleId, secGrpSource) {
  const modalContent = {
    title: intl.get('hiam.securityGroup.view.title.field.permission.config').d('字段权限配置'),
    drawer: true,
    closable: true,
    style: { width: 800 },
    key: FieldModalKey,
    children: (
      <FieldPermissionDrawer
        record={record}
        callback={callback}
        secGrpId={secGrpId}
        isSelf={isSelf}
        roleId={roleId}
        secGrpSource={secGrpSource}
      />
    ),
    destroyOnClose: true,
    onOk: handleSubmit,
  };
  if (!isSelf) {
    modalContent.footer = (
      <Button
        onClick={() => {
          modal.close();
        }}
      >
        {intl.get('hzero.common.button.close').d('关闭')}
      </Button>
    );
  }
  modal = Modal.open(modalContent);
}
