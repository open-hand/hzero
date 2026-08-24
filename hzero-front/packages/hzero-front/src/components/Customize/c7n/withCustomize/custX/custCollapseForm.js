/* eslint-disable no-param-reassign */
import React from 'react';
import { isEmpty, isArray, omit, isNil } from 'lodash';
import { Output } from 'choerodon-ui/pro';
import moment from 'moment';
import template from 'utils/template';
import {
  getComponent,
  coverConfig,
  parseProps,
  transformCompProps,
  getFieldValueObject,
  selfValidator,
  defaultValueFx,
} from '../customizeTool';
import { getFieldConfig } from './common';

export default function custCollapseForm(options = {}, form) {
  const proxyForm = form;
  const { custConfig = {}, loading = false, cacheType } = this.state;
  const { code = '', readOnly: readOnly1 } = options;
  const { dataSet = { data: [{}] } } = proxyForm.props;
  if (loading) {
    proxyForm.props.children = [];
  }
  if (!code || isEmpty(custConfig[code])) return form;
  const fieldMap = new Map();
  const formChildren = isArray(proxyForm.props.children)
    ? proxyForm.props.children
    : [proxyForm.props.children];
  formChildren.forEach((item, seq) => {
    if (item?.props?.name) {
      fieldMap.set(item.props.name, { item, seq, empty: item.props.empty });
    }
  });
  const tools = { ...this.getToolFuns(), code };
  // TODO: c7n不支持字段宽度配置
  const { maxCol = 3, fields = [], unitAlias = [], readOnly: readOnly2 } = custConfig[code];
  const readOnly = readOnly1 || readOnly2;
  const current = dataSet.current || { toData: () => ({}) };
  this.setDataMap(code, current.toData());
  const unitData = getFieldValueObject(unitAlias, this.getToolFuns(), code);
  if (!cacheType[code]) {
    cacheType[code] = 'collapse-form';
    dataSet.addEventListener(
      'update',
      ({ name, record, value }) => {
        const ds = dataSet.get(0) || { toData: () => ({}) };
        const data = ds.toData();
        this.setDataMap(code, data);
        fields.forEach((item) => {
          const { conditionHeaderDTOs = [], fieldCode, lovMappings = [], conValidDTO = {} } = item;
          const newFieldConfig = getFieldConfig({
            required: item.required,
            editable: item.editable,
            ...coverConfig(conditionHeaderDTOs, tools, ['visible']),
          });
          const validators = selfValidator(conValidDTO, tools);
          const { defaultValue } = defaultValueFx({ ...tools, code }, item);
          const oldFieldConfig = (dataSet.getField(fieldCode) || {}).pristineProps || {};
          if (defaultValue !== undefined) {
            oldFieldConfig.defaultValue = defaultValue;
          }
          dataSet.addField(fieldCode, {
            ...parseProps(
              omit(item, [
                'width',
                'fieldName',
                'fieldCode',
                'fixed',
                'renderOptions',
                'conditionHeaderDTOs',
              ]),
              tools,
              oldFieldConfig
            ),
            ...newFieldConfig,
            ...validators,
          });
          if (lovMappings.length > 0 && name === fieldCode && typeof value === 'object') {
            lovMappings.forEach((i) => {
              record.set(i.targetCode, value[i.sourceCode]);
            });
          }
        });
        this.setState({ lastUpdateUnit: `${code}${name}` });
      },
      false
    );
    dataSet.addEventListener(
      'load',
      () => {
        fields.forEach((item) => {
          if (item.fieldType === 'EMPTY') return;
          const { conditionHeaderDTOs = [], fieldCode, conValidDTO = {} } = item;
          const data = (dataSet.current && dataSet.current.toData()) || {};
          this.setDataMap(code, data);
          const newFieldConfig = getFieldConfig({
            required: item.required,
            editable: item.editable,
            ...coverConfig(conditionHeaderDTOs, tools, ['visible']),
          });
          const validators = selfValidator(conValidDTO, tools);
          const { defaultValue } = defaultValueFx({ ...tools, code }, item);
          const oldFieldConfig = (dataSet.getField(fieldCode) || {}).pristineProps || {};
          if (defaultValue !== undefined) {
            oldFieldConfig.defaultValue = defaultValue;
          }
          dataSet.addField(fieldCode, {
            ...parseProps(
              omit(item, [
                'width',
                'fieldName',
                'fieldCode',
                'fixed',
                'renderOptions',
                'conditionHeaderDTOs',
              ]),
              this.getToolFuns(),
              oldFieldConfig
            ),
            ...newFieldConfig,
            ...validators,
          });
        });
        this.setState({ lastUpdateUnit: `load${code}` });
      },
      false
    );
  }
  const proxyFields = [];
  const tempFields = fields.filter((i) => {
    const originSeq = fieldMap[i.fieldName] && fieldMap[i.fieldName].seq;
    if ((i.formRow === undefined || i.formCol === undefined) && originSeq === undefined) {
      return true;
    }
    const seq = i.formRow * maxCol + i.formCol;
    proxyFields.push({ ...i, seq: typeof seq === 'number' ? seq : originSeq });
    return false;
  });
  proxyFields.sort((p, n) => p.seq - n.seq);
  let newChildren = [];
  proxyFields.concat(tempFields).forEach((item) => {
    const {
      fieldCode,
      fieldName,
      renderOptions,
      conditionHeaderDTOs,
      renderRule,
      colSpan,
      conValidDTO = {},
      ...otherProps
    } = item;
    const oldChild = fieldMap.get(fieldCode);
    const {
      visible = item.visible,
      required = item.required,
      editable = item.editable,
    } = coverConfig(conditionHeaderDTOs, tools);
    const validators = selfValidator(conValidDTO, tools);
    const { defaultValue } = defaultValueFx({ ...tools, code }, item);
    const newFieldConfig = getFieldConfig({
      visible,
      required,
      editable,
    });
    if (visible === 0) {
      dataSet.addField(fieldCode, { required: false });
      fieldMap.delete(fieldCode);
      return;
    }
    if (item.fieldType !== 'EMPTY') {
      if (item.fieldType === 'CHECKBOX' || item.fieldType === 'SWITCH') {
        newFieldConfig.trueValue = 1;
        newFieldConfig.falseValue = 0;
      }
      if (fieldName !== undefined) {
        newFieldConfig.label = fieldName;
      }
      const oldFieldConfig = (dataSet.getField(fieldCode) || {}).pristineProps || {};
      if (defaultValue !== undefined) {
        oldFieldConfig.defaultValue = defaultValue;
      }
      dataSet.addField(fieldCode, {
        ...parseProps(otherProps, tools, oldFieldConfig),
        ...newFieldConfig,
        ...validators,
      });
    }
    if (visible !== undefined) {
      // 做新增扩展字段处理
      if (!oldChild && visible !== -1) {
        if (readOnly || renderOptions === 'TEXT') {
          const outputProps = {
            name: fieldCode,
            label: fieldName,
            colSpan,
          };
          if (item.fieldType === 'DATE_PICKER') {
            outputProps.renderer = ({ value }) => value && moment(value).format(item.dateFormat);
          }
          if (!isNil(renderRule)) {
            const renderer = () => (
              // eslint-disable-next-line react/no-danger
              <div dangerouslySetInnerHTML={{ __html: template.render(renderRule, unitData) }} />
            );
            newChildren.push(<Output {...outputProps} renderer={renderer} />);
          } else {
            newChildren.push(<Output {...outputProps} />);
          }
        } else {
          newChildren.push(
            getComponent(item.fieldType, { currentData: dataSet.toData() })({
              name: fieldCode,
              label: fieldName,
              ...transformCompProps(otherProps),
            })
          );
        }
      } else if (oldChild) {
        if (item.editable !== -1) {
          oldChild.item.props.disabled = !item.editable;
        }
        if (colSpan) {
          oldChild.item.props.colSpan = colSpan;
        }
        if (item.placeholder !== undefined) {
          oldChild.item.props.placeholder = item.placeholder;
        }
        newChildren.push(oldChild.item);
      }
    }
    fieldMap.delete(fieldCode);
  });
  if (dataSet.all.length === 0) {
    dataSet.create();
  }
  newChildren = newChildren.concat(Array.from(fieldMap.values()).map((i) => i.item));
  proxyForm.props.children = newChildren;
  proxyForm.props.columns = maxCol;
  return form;
}
