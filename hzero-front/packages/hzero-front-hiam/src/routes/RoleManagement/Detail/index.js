/* eslint-disable no-nested-ternary */
import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row, Spin, Select } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import pathParse from 'path-parse';

import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';

import { getCurrentOrganizationId, isTenantRoleLevel, getResponse } from 'utils/utils';

import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';
import { VERSION_IS_OP } from 'utils/config';
import { CODE_LOWER } from 'utils/regExp';

import Drawer from '../Drawer';
import styles from './index.less';

const FormItem = Form.Item;

const tenantRoleLevel = isTenantRoleLevel();

@Form.create({ fieldNameProp: null })
export default class Detail extends PureComponent {
  state = {
    // dataSource: {},
    // roleTenantIsSame: false, // 选择所属租户后 租户id 和 角色的 租户一样
    roleLabels: [],
  };

  // getSnapshotBeforeUpdate(prevProps) {
  //   const { visible, actionType, roleId, copyFormId, inheritFormId } = this.props;
  //   return (
  //     visible &&
  //     ((actionType === 'create' && isNumber(roleId) && roleId !== prevProps.roleId) ||
  //       (actionType === 'edit' && isNumber(roleId) && roleId !== prevProps.roleId) ||
  //       (actionType === 'copy' && isNumber(copyFormId) && copyFormId !== prevProps.copyFormId) ||
  //       (actionType === 'inherit' &&
  //         isNumber(inheritFormId) &&
  //         inheritFormId !== prevProps.inheritFormId))
  //   );
  // }
  //
  // componentDidUpdate(prevProps, prevState, snapshot) {

  // if (snapshot) {
  //   const { actionType } = this.props;
  //   // 只要是重新打开模态框 都需要查询一次 父级角色
  //   this.fetchAdminRole();
  //   if (actionType === 'edit') {
  //     this.handleFetchDataSource();
  //   }
  // }
  // }
  //
  // fetchAdminRole() {
  //   const { roleId, fetchAdminRole } = this.props;
  //   this.setState({
  //     parentRole: {},
  //   });
  //   fetchAdminRole(roleId).then(res => {
  //     if (res) {
  //       this.setState({
  //         parentRole: res,
  //       });
  //     }
  //   });
  // }
  //
  // handleFetchDataSource() {
  //   const { fetchDataSource = e => e, roleId, roleSourceCode } = this.props;
  //   fetchDataSource(roleId).then(res => {
  //     if (res) {
  //       this.setState({
  //         dataSource: {
  //           ...res,
  //           roleSourceMeaning: getCodeMeaning(res.roleSource || 'custom', roleSourceCode),
  //         },
  //       });
  //     }
  //   });
  // }

  // getCodeDescription(value, code = []) {
  //   let result;
  //   if (value && !isEmpty(code)) {
  //     const codeList = code.filter(n => n.value === value);
  //     if (!isEmpty(codeList)) {
  //       result = codeList[0].description;
  //     }
  //   }
  //   return result;
  // }

  componentDidMount() {
    const {
      currentRowData: { id },
      actionType,
    } = this.props;
    if (!isUndefined(id) && actionType === 'edit') {
      this.props.queryLabel(id).then((res) => {
        if (res && getResponse(res) && !isEmpty(getResponse(res))) {
          this.setState({ roleLabels: res });
        }
      });
    }
  }

  handleClose() {
    const { close = (e) => e } = this.props;
    close();
  }

  /**
   * 编辑的保存
   */
  handleSave() {
    const {
      save,
      form: { validateFields = (e) => e },
      roleId,
      currentRowData = {},
      labelList = [],
    } = this.props;
    validateFields((error, values) => {
      if (!isEmpty(error)) {
        return;
      }
      const { description, name, tenantId, _tls, roleLabels } = values;
      const data = {
        ...currentRowData,
        description,
        name,
        _tls,
        roleLabels: labelList
          .filter((item) => roleLabels.includes(item.name))
          .map((item) => ({ ...item, labelId: item.id })),
        tenantId:
          // eslint-disable-next-line no-nested-ternary
          VERSION_IS_OP && String(getCurrentOrganizationId()) !== '0'
            ? getCurrentOrganizationId()
            : !isUndefined(tenantId)
            ? tenantId
            : currentRowData.tenantId,
      };
      save(roleId, data, this.handleClose.bind(this));
    });
  }

  /**
   * 创建角色/复制 继承的 保存
   */
  handleCreate() {
    const {
      actionType,
      create,
      form: { validateFields = (e) => e },
      inheritFormId,
      copyFormId,
      inherit,
      copy,
      currentRowData = {},
      labelList = [],
    } = this.props;
    // const parentRoleId =
    //   currentRowData.parentRoleAdminFlag === 1 ? currentRowData.id : currentRowData.parentRoleId;
    validateFields((error, values) => {
      if (!isEmpty(error)) {
        return;
      }
      const { code, description, name, tenantId, _tls, roleLabels } = values;
      const data = {
        code,
        description,
        name,
        tenantId:
          VERSION_IS_OP && String(getCurrentOrganizationId()) !== '0'
            ? getCurrentOrganizationId()
            : tenantId,
        parentRoleId: currentRowData.parentRoleId,
        _tls,
        roleLabels: labelList
          .filter((item) => roleLabels.includes(item.name))
          .map((item) => ({ ...item, labelId: item.id })),
        roleSource: 'custom',
        rolePermissionSets: [],
      };
      if (actionType === 'inherit') {
        data.inheritRoleId = inheritFormId;
        // data.tenantId = currentRowData.tenantId;
        inherit(data, this.handleClose.bind(this));
      }
      if (actionType === 'copy') {
        data.copyFromRoleId = copyFormId;
        copy(data, this.handleClose.bind(this));
      }
      if (actionType === 'create') {
        create(data, this.handleClose.bind(this));
      }
    });
  }

  // handleDeselect(value) {
  //   // const { roleLabels = [] } = this.state;
  //   // if (!isEmpty(roleLabels)) {
  //   //   const temp = roleLabels.filter((item) => item.label.name === value);
  //   //   if (temp[0] && temp[0].assignType === 'A') {
  //   //     const roleLabel = this.props.form.getFieldValue('roleLabels');
  //   //     setTimeout(() => {
  //   //       this.props.form.setFieldsValue({
  //   //         roleLabels: roleLabel,
  //   //       });
  //   //     }, 0);
  //   //   }
  //   // }
  // }

  render() {
    const {
      actionType,
      form: { getFieldDecorator },
      processing = {},
      visible,
      currentRowData = {},
      labelList = [],
      isHaveParams,
    } = this.props;
    const {
      name,
      code,
      description,
      tenantId,
      tenantName,
      // roleLabels = [],
      _token,
      levelPath,
      _levelPath,
    } = currentRowData;
    const { roleLabels = [] } = this.state;
    const drawerTitle = {
      edit: intl
        .get('hiam.roleManagement.view.title.content.editRole', { name })
        .d(`修改“${name}”`),
      copy: intl.get(`hiam.roleManagement.view.title.createRole`).d('创建角色'),
      inherit: intl.get(`hiam.roleManagement.view.title.createRole`).d('创建角色'),
      create: intl.get(`hiam.roleManagement.view.title.createRole`).d('创建角色'),
    };
    const parentRoleName = `${currentRowData.parentRoleName}(${currentRowData.parentRoleTenantName})`;
    // actionType === 'create' || actionType === 'copy' || actionType === 'inherit' ? `${currentRowData.parentName}(${currentRowData.parentTenantName})` :  `${currentRowData.parentRoleName}(${currentRowData.parentRoleTenantName})`;
    // eslint-disable-next-line no-nested-ternary
    // actionType === 'edit'
    //   ? `${currentRowData.parentRoleName}(${currentRowData.parentRoleTenantName})`
    //   : `${currentRowData.name}(${currentRowData.tenantName})`;
    // 在 新建/继承/复制 情况下的禁用
    // eslint-disable-next-line no-nested-ternary
    const tenantDisabled = !(actionType === 'inherit'
      ? currentRowData.inheritRoleLevel === 'organization' &&
        String(currentRowData.inheritedRoleTenantId) === '0'
      : currentRowData.parentRoleAdminFlag === 1
      ? currentRowData.level === 'organization' && String(currentRowData.tenantId) === '0'
      : currentRowData.adminRoleLevel === 'organization' &&
        String(currentRowData.parentRoleTenantId) === 0);

    const drawerProps = {
      title: drawerTitle[actionType],
      visible,
      onCancel: this.handleClose.bind(this),
      width: 1000,
      anchor: 'right',
      wrapClassName: styles['hiam-role-detail'],
      footer: (
        <>
          <Button
            onClick={this.handleClose.bind(this)}
            disabled={processing.save || processing.create || false}
          >
            {intl.get(`hzero.common.button.cancel`).d('取消')}
          </Button>
          {(actionType === 'create' || actionType === 'copy' || actionType === 'inherit') && (
            <Button
              type="primary"
              loading={processing.create || processing.copy || processing.inherit}
              onClick={this.handleCreate.bind(this)}
            >
              {intl.get(`hzero.common.button.ok`).d('确定')}
            </Button>
          )}
          {actionType === 'edit' && (
            <Button type="primary" loading={processing.save} onClick={this.handleSave.bind(this)}>
              {intl.get(`hzero.common.button.ok`).d('确定')}
            </Button>
          )}
        </>
      ),
    };
    return (
      <Drawer {...drawerProps}>
        <Spin spinning={processing.query || processing.queryParentRole || false}>
          <Form className={styles['hiam-role-detail-form']}>
            <Row>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl
                    .get('hiam.roleManagement.model.roleManagement.adminRole')
                    .d('父级管理角色')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('parentRoleName', {
                    initialValue: parentRoleName,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl
                    .get(`hiam.roleManagement.model.roleManagement.inheritedRole`)
                    .d('继承自')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('inheritedRoleName', {
                    initialValue:
                      // eslint-disable-next-line no-nested-ternary
                      actionType === 'inherit'
                        ? `${currentRowData.inheritedRoleName}(${currentRowData.inheritedRoleTenantName})`
                        : actionType === 'edit'
                        ? currentRowData.inheritedRoleName
                          ? `${currentRowData.inheritedRoleName}(${currentRowData.inheritedRoleTenantName})`
                          : undefined
                        : undefined,
                    // eslint-disable-next-line no-nested-ternary
                    // actionType === 'inherit'
                    //   ? `${currentRowData.name}(${currentRowData.tenantName})`
                    //   : actionType === 'edit'
                    //   ? currentRowData.inheritedRoleName
                    //     ? `${currentRowData.inheritedRoleName}(${currentRowData.inheritedRoleTenantName})`
                    //     : undefined
                    //   : undefined,
                  })(<Input disabled />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get('hiam.roleManagement.model.roleManagement.code').d('角色编码')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('code', {
                    initialValue: actionType === 'edit' ? pathParse(code || '').base : undefined,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hiam.roleManagement.model.roleManagement.code')
                            .d('角色编码'),
                        }),
                      },
                      {
                        max: 64,
                        message: intl.get(`hzero.common.validation.max`, { max: 64 }),
                      },
                      {
                        pattern: CODE_LOWER,
                        message: intl
                          .get('hzero.common.validation.codeLower')
                          .d('全小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                      },
                    ],
                  })(
                    <Input
                      trim
                      typeCase="lower"
                      inputChinese={false}
                      disabled={actionType === 'edit'}
                    />
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  label={intl.get(`hiam.roleManagement.model.roleManagement.name`).d('角色名称')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('name', {
                    initialValue: actionType === 'edit' ? name : undefined,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get(`hiam.roleManagement.model.roleManagement.name`)
                            .d('角色名称'),
                        }),
                      },
                      {
                        max: 64,
                        message: intl.get(`hzero.common.validation.max`, { max: 64 }),
                      },
                    ],
                  })(
                    <TLEditor
                      label={intl.get('hiam.roleManagement.view.title.name').d('角色名称')}
                      field="name"
                      inputSize={{ zh: 64, en: 64 }}
                      // eslint-disable-next-line
                      token={actionType === 'create' || actionType === 'copy' ? undefined : _token}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row>
              {!VERSION_IS_OP && (
                <Col span={12}>
                  <FormItem
                    label={intl
                      .get('hiam.roleManagement.model.roleManagement.tenant')
                      .d('所属租户')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('tenantId', {
                      initialValue:
                        actionType === 'edit'
                          ? tenantId
                          : tenantDisabled
                          ? actionType === 'inherit'
                            ? currentRowData.tenantId
                            : // 新建/复制
                            (actionType === 'create' || actionType === 'copy') &&
                              currentRowData.parentRoleAdminFlag === 1
                            ? currentRowData.tenantId
                            : currentRowData.parentRoleTenantId
                          : undefined,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hiam.roleManagement.model.roleManagement.tenant')
                              .d('所属租户'),
                          }),
                        },
                      ],
                    })(
                      <Lov
                        disabled={actionType === 'edit' || tenantDisabled}
                        textValue={
                          actionType === 'edit'
                            ? tenantName
                            : tenantDisabled
                            ? actionType === 'inherit'
                              ? currentRowData.tenantName
                              : // 新建/复制
                              (actionType === 'create' || actionType === 'copy') &&
                                currentRowData.parentRoleAdminFlag === 1
                              ? currentRowData.tenantName
                              : currentRowData.adminRoleTenantName
                            : undefined
                        }
                        code={tenantRoleLevel ? 'HPFM.TENANT.ORG' : 'HPFM.TENANT'}
                      />
                    )}
                  </FormItem>
                </Col>
              )}
              {(actionType === 'create' || VERSION_IS_OP) && (
                <Col span={12}>
                  <FormItem
                    label={intl
                      .get('hiam.roleManagement.model.roleManagement.description')
                      .d('角色描述')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('description', {
                      initialValue: actionType === 'edit' ? description : undefined,
                      rules: [
                        {
                          max: 240,
                          message: intl.get('hzero.common.validation.max', {
                            max: 240,
                          }),
                        },
                      ],
                    })(<Input />)}
                  </FormItem>
                </Col>
              )}
              {actionType === 'create' && VERSION_IS_OP && (
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hiam.roleManagement.model.roleManagement.roleLabels')
                      .d('角色标签')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('roleLabels', {
                      initialValue:
                        roleLabels
                          .filter((i) => i.label)
                          .map((item) => item.label && item.label.name) || [],
                    })(
                      <Select mode="multiple">
                        {labelList.map((n) => (
                          <Select.Option
                            key={n.name}
                            value={n.name}
                            disabled={roleLabels
                              .filter((item) => item.assignType === 'A')
                              .map((item) => item.label && item.label.name)
                              .includes(n.name)}
                          >
                            {n.name}
                          </Select.Option>
                        ))}
                      </Select>
                    )}
                  </FormItem>
                </Col>
              )}
              {actionType !== 'create' && !VERSION_IS_OP && (
                <Col span={12}>
                  <FormItem
                    label={intl
                      .get('hiam.roleManagement.model.roleManagement.description')
                      .d('角色描述')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('description', {
                      initialValue: actionType === 'edit' ? description : undefined,
                      rules: [
                        {
                          max: 240,
                          message: intl.get('hzero.common.validation.max', {
                            max: 240,
                          }),
                        },
                      ],
                    })(<Input />)}
                  </FormItem>
                </Col>
              )}
            </Row>
            <Row>
              {(actionType !== 'create' ? true : !VERSION_IS_OP) && (
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hiam.roleManagement.model.roleManagement.roleLabels')
                      .d('角色标签')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('roleLabels', {
                      initialValue:
                        roleLabels
                          .filter((i) => i.label)
                          .map((item) => item.label && item.label.name) || [],
                    })(
                      <Select mode="multiple">
                        {labelList.map((n) => (
                          <Select.Option
                            key={n.name}
                            value={n.name}
                            disabled={roleLabels
                              .filter((item) => item.assignType === 'A')
                              .map((item) => item.label && item.label.name)
                              .includes(n.name)}
                          >
                            {n.name}
                          </Select.Option>
                        ))}
                      </Select>
                    )}
                  </FormItem>
                </Col>
              )}
              {/* {actionType === 'edit' && (
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl
                      .get('hiam.roleManagement.model.roleManagement.assignedFlag')
                      .d('分配标志')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {assignedFlag
                      ? getFieldDecorator('assignedFlag', {
                          initialValue: assignedFlag,
                        })(<Tag color="green"> {intl.get('hzero.common.status.yes').d('是')} </Tag>)
                      : getFieldDecorator('assignedFlag', {
                          initialValue: assignedFlag,
                        })(
                          <Tag color="orange"> {intl.get('hzero.common.status.no').d('否')} </Tag>
                        )}
                  </FormItem>
                </Col>
              )} */}
            </Row>
            {/*
            {actionType === 'edit' && (
              <Row>
                <Col {...FORM_COL_2_LAYOUT}>
                  <FormItem
                    label={intl
                      .get(`hiam.roleManagement.model.roleManagement.adminFlag`)
                      .d('管理标志')}
                    {...MODAL_FORM_ITEM_LAYOUT}
                  >
                    {adminFlag
                      ? getFieldDecorator('adminFlag', {
                          initialValue: adminFlag,
                        })(<Tag color="green"> {intl.get('hzero.common.status.yes').d('是')} </Tag>)
                      : getFieldDecorator('adminFlag', {
                          initialValue: adminFlag,
                        })(
                          <Tag color="orange"> {intl.get('hzero.common.status.no').d('否')} </Tag>
                        )}
                  </FormItem>
                </Col>
              </Row>
            )} */}
            <Row>
              {actionType !== 'create' && (
                <Col span={24}>
                  <FormItem
                    label={intl
                      .get('hiam.roleManagement.model.roleManagement.levelPath')
                      .d('角色路径')}
                    labelCol={{ span: 3 }}
                    wrapperCol={{ span: 21 }}
                  >
                    {getFieldDecorator('levelPath', {
                      initialValue:
                        actionType === 'edit' ? (isHaveParams ? _levelPath : levelPath) : undefined,
                    })(<Input disabled />)}
                  </FormItem>
                </Col>
              )}
            </Row>
          </Form>
        </Spin>
      </Drawer>
    );
  }
}
