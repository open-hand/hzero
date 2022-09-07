/* eslint-disable no-nested-ternary */
/**
 * EditableCell - 角色管理分配用户明细页面 - 行内编辑可编辑单元格组件
 * @date: 2018-10-26
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Select } from 'hzero-ui';
import { isFunction, isNumber, isNil } from 'lodash';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { getCurrentOrganizationId, isTenantRoleLevel, getCodeMeaning } from 'utils/utils';
import Lov from 'components/Lov';
import { VERSION_IS_OP } from 'utils/config';

// FormItem组件初始化
const FormItem = Form.Item;
const { Option } = Select;

// const organizationId = getCurrentOrganizationId();

/**
 * EditableCell - 送货单创建明细页面 - 行内编辑可编辑单元格组件
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {object} contextProvider - Context.Provider
 * @return React.element
 */
export default class EditableCell extends PureComponent {
  /**
   *
   *
   * @param {*} description
   * @param {*} getFieldDecorator
   * @memberof EditableCell
   */
  @Bind()
  onModelFieldIdChange(description, getFieldDecorator) {
    getFieldDecorator('fieldDescription', { initialValue: description });
  }

  @Bind()
  onAssignLevelChange(value) {
    const {
      record = {},
      assignRowData = () => {},
      options = [],
      dataIndex,
      roleTenantId,
      roleTenantName,
    } = this.props;
    const newRecord = record;
    newRecord.assignLevel = value;
    newRecord[`${dataIndex}Meaning`] = getCodeMeaning(value, options);
    Object.assign(
      newRecord,
      value === 'org'
        ? {
            assignLevelValue: null,
            assignLevelValueMeaning: null,
            sourceType: null,
          }
        : {
            assignLevelValue: roleTenantId, // record.organizationId,
            assignLevelValueMeaning: roleTenantName, // record.tenantName,
          }
    );
    assignRowData(newRecord);
  }

  @Bind()
  onIdChange(selectedRow, form) {
    const { assignRowData = () => {}, record = {}, roleDatasource = {} } = this.props;
    const { setFields = () => {} } = form;
    const assignLevelDisabled =
      !isNumber(record.organizationId) ||
      roleDatasource.level === 'site' ||
      !isNil(roleDatasource.parentRoleAssignUnitId);
    const { id, loginName, realName, tenantName } = selectedRow;
    let newRecord = record;
    newRecord = {
      ...newRecord,
      id,
      loginName,
      realName,
      tenantName,
      organizationId:
        VERSION_IS_OP && getCurrentOrganizationId() !== 0
          ? getCurrentOrganizationId()
          : selectedRow.organizationId,
      assignLevelMeaning: null,
      assignLevelValueMeaning: assignLevelDisabled ? newRecord.assignLevelValueMeaning : null,
      // eslint-disable-next-line no-nested-ternary
      assignLevelValue: assignLevelDisabled
        ? newRecord.assignLevelValue
        : VERSION_IS_OP && getCurrentOrganizationId() !== 0
        ? selectedRow.organizationId
        : null,
    };

    if (!assignLevelDisabled) {
      setFields({
        assignLevel: {
          value: undefined,
        },
      });
    }

    assignRowData(newRecord);
  }

  @Bind()
  onAssignLevelValueChange(selectedRow) {
    const { assignRowData = () => {}, record = {} } = this.props;
    const newRecord = record;
    newRecord.assignLevelValue = selectedRow.unitId;
    newRecord.assignLevelValueMeaning = selectedRow.unitName;
    assignRowData(newRecord);
  }

  /**
   *
   *
   * @returns
   * @memberof EditableCell
   */
  @Bind()
  getFormItem() {
    const {
      dataIndex,
      title,
      record = {},
      contextConsumer,
      children,
      editable,
      status,
      render,
      roleId,
      editing,
      options = [],
      // code = {},
      roleDatasource,
      ...restProps
    } = this.props;
    const WrapperContextConsumer = contextConsumer;
    const tenantRoleLevel = isTenantRoleLevel();
    const defaultCellRender = isFunction(render)
      ? render(record[dataIndex], record)
      : record[dataIndex];

    const defaultFormItems = {
      id: () => (
        <WrapperContextConsumer>
          {(form = {}) => {
            const { getFieldDecorator = (e) => e } = form || {};
            return (
              <FormItem style={{ margin: 0 }}>
                {getFieldDecorator(dataIndex, {
                  initialValue: record[dataIndex],
                  rules: [
                    {
                      required: true,
                      message: intl
                        .get(`hzero.common.validation.notNull`, { name: title })
                        .d(`${title}不能为空`),
                    },
                  ],
                })(
                  <Lov
                    queryParams={{ roleId }}
                    style={{ width: 150 }}
                    code={tenantRoleLevel ? 'HIAM.USER.ORG' : 'HIAM.USER'}
                    textValue={record.realName}
                    onChange={(value, selectedRow) => this.onIdChange(selectedRow, form)}
                  />
                )}
              </FormItem>
            );
          }}
        </WrapperContextConsumer>
      ),
      assignLevel: () => (
        <WrapperContextConsumer>
          {(form = {}) => {
            const { getFieldDecorator = (e) => e } = form || {};
            return editing ? (
              <FormItem style={{ margin: 0 }}>
                {getFieldDecorator(dataIndex, {
                  initialValue: record[dataIndex],
                  rules: [
                    {
                      required: true,
                      message: intl
                        .get(`hzero.common.validation.notNull`, { name: title })
                        .d(`${title}不能为空`),
                    },
                  ],
                })(
                  <Select
                    // ref={node => {
                    //   this.input = node;
                    // }}
                    style={{ width: 120 }}
                    onChange={this.onAssignLevelChange.bind(this)}
                    disabled={
                      !isNumber(record.organizationId) ||
                      roleDatasource.level === 'site' ||
                      !isNil(roleDatasource.parentRoleAssignUnitId)
                    }
                  >
                    {options.map((o) => (
                      <Option key={o.value} value={o.value}>
                        {o.meaning}
                      </Option>
                    ))}
                  </Select>
                )}
              </FormItem>
            ) : (
              record.assignLevelMeaning
            );
          }}
        </WrapperContextConsumer>
      ),
      assignLevelValue: () => (
        <WrapperContextConsumer>
          {(form = {}) => {
            const { getFieldDecorator = (e) => e } = form || {};
            return (
              editing &&
              record.assignLevel === 'org' && (
                <FormItem style={{ margin: 0 }}>
                  {getFieldDecorator(dataIndex, {
                    initialValue: record[dataIndex],
                    rules: [
                      {
                        required: true,
                        message: intl
                          .get(`hzero.common.validation.notNull`, { name: title })
                          .d(`${title}不能为空`),
                      },
                    ],
                  })(
                    <Lov
                      style={{ width: 150 }}
                      textValue={record.assignLevelValueMeaning}
                      code="HIAM.ASSIGN_LEVEL_VALUE_ORG"
                      onChange={(value, selectedRow) =>
                        this.onAssignLevelValueChange(selectedRow, form)
                      }
                      queryParams={{
                        roleId,
                      }}
                    />
                  )}
                </FormItem>
              )
            );
          }}
        </WrapperContextConsumer>
      ),
      action: () => (
        <WrapperContextConsumer>
          {(form = {}) => render(record[dataIndex], record, form)}
        </WrapperContextConsumer>
      ),
    };
    return (
      <td {...restProps}>
        {dataIndex && defaultFormItems[dataIndex] && contextConsumer
          ? dataIndex === 'action'
            ? defaultFormItems[dataIndex]()
            : editing
            ? defaultFormItems[dataIndex]()
            : defaultCellRender
          : children || null}
      </td>
    );
  }

  render() {
    return this.getFormItem();
  }
}
