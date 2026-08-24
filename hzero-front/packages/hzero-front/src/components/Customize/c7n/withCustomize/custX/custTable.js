/* eslint-disable no-param-reassign */
import React from 'react';
import { isEmpty, omit, isNil } from 'lodash';
import { DataSet, Table, Output } from 'choerodon-ui/pro';
import moment from 'moment';
import { Spin } from 'choerodon-ui';
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
import { getFieldConfig, getColumnsConfig } from './common';

export default function custTable(options = {}, table) {
  const { custConfig = {}, loading = false, cacheType, cache } = this.state;
  const { code = '', filterCode = '', readOnly: readOnly1 } = options;
  const { dataSet } = table.props;
  let { columns = [] } = table.props;
  const fieldMap = new Map(); // 记录已配置的字段
  columns.forEach((item) => {
    fieldMap.set(item.name, item);
  });
  if (!code || isEmpty(custConfig[code])) {
    return table;
  }
  if (loading) {
    return (
      <Spin spinning={this.state.loading}>
        <Table dataSet={new DataSet()} columns={[]} />
      </Spin>
    );
  }
  const unitConfig = custConfig[code] || {};
  const { unitAlias = [], pageSize, fields = [], readOnly: readOnly2 } = unitConfig;
  const readOnly = readOnly1 | readOnly2;
  const proxyTableProps = table.props || {};
  let queryFieldsLimit;
  const tools = this.getToolFuns();
  const unitData = getFieldValueObject(unitAlias, tools);
  if (filterCode && !isEmpty(custConfig[filterCode]) && !cacheType[filterCode]) {
    cacheType[filterCode] = 'form';
    cache[filterCode] = initTableFilter({
      dataSet,
      filterCode,
      unitConfig: custConfig[filterCode],
      tablePropsQueryFields: table.props.queryFields,
      parentThis: this,
      proxyTableProps,
    });
    queryFieldsLimit = cache[filterCode].maxCol;
  }
  if (queryFieldsLimit !== undefined) {
    proxyTableProps.queryFieldsLimit = queryFieldsLimit;
  }
  if (cache[filterCode] && cache[filterCode].queryFields) {
    proxyTableProps.queryFields = cache[filterCode].queryFields;
  }
  if (!cacheType[code]) {
    cacheType[code] = 'table';
    const newData = dataSet.toData() || [];
    newData.forEach((item, index) => {
      this.setArrayDataMap(code, item, index);
    });
    if (pageSize) {
      dataSet.pageSize = pageSize;
    }
    dataSet.addEventListener(
      'update',
      ({ record, value, name }) => {
        this.setArrayDataMap(code, record.toData(), record.index);
        fields.forEach((item) => {
          const { conditionHeaderDTOs = [], fieldCode, lovMappings = [], conValidDTO = {} } = item;
          const {
            required = item.required,
            editable = item.editable,
          } = coverConfig(conditionHeaderDTOs, { ...tools, index: record.index, code }, [
            'visible',
          ]);
          const newFieldConfig = getFieldConfig({
            required,
            editable,
          });
          const validators = selfValidator(conValidDTO, {
            ...this.getToolFuns(),
            index: record.index,
            code,
          });
          const { defaultValue } = defaultValueFx({ ...tools, code }, item);
          const oldFieldConfig = (record.getField(fieldCode) || {}).pristineProps || {};
          if (defaultValue !== undefined) {
            oldFieldConfig.defaultValue = defaultValue;
          }
          record.addField(fieldCode, {
            ...parseProps(
              omit(item, [
                'width',
                'fieldName',
                'fieldCode',
                'fixed',
                'renderOptions',
                'conditionHeaderDTOs',
              ]),
              { ...tools, index: record.index, code },
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
      ({ dataSet: ds }) => {
        (ds.records || []).forEach((item, index) => {
          this.setArrayDataMap(code, item.toData(), item.index);
          fields.forEach((i) => {
            const { conditionHeaderDTOs = [], fieldCode, conValidDTO = {} } = i;
            const {
              required = i.required,
              editable = i.editable,
              visible = i.visible,
            } = coverConfig(conditionHeaderDTOs, { ...tools, index, code });
            if (visible === 0) return;
            const newFieldConfig = getFieldConfig({
              required,
              editable,
            });
            const validators = selfValidator(conValidDTO, {
              ...tools,
              index,
              code,
            });
            const { defaultValue } = defaultValueFx({ ...tools, code }, item);
            const oldFieldConfig = (item.getField(fieldCode) || {}).pristineProps || {};
            if (defaultValue !== undefined) {
              oldFieldConfig.defaultValue = defaultValue;
            }
            item.addField(fieldCode, {
              ...parseProps(
                omit(i, [
                  'width',
                  'fieldName',
                  'fieldCode',
                  'fixed',
                  'renderOptions',
                  'conditionHeaderDTOs',
                ]),
                { ...tools, index, code },
                oldFieldConfig
              ),
              ...newFieldConfig,
              ...validators,
            });
          });
        });
        this.setState({ lastUpdateUnit: `load${code}` });
      },
      false
    );
  }
  if (fields && fields.length > 0) {
    // 根据列顺序属性排序
    fields.sort((before, after) => before.seq - after.seq);
    // 左固定前置， 右固定后置
    const leftFixedColumns = fields.filter((item) => item.fixed === 'L');
    const rightFixedColumns = fields.filter((item) => item.fixed === 'R');
    const centerFixedColumns = fields.filter((item) => item.fixed !== 'L' && item.fixed !== 'R');
    const newFields = leftFixedColumns.concat(centerFixedColumns).concat(rightFixedColumns);
    const newColumns = [];
    newFields.forEach((item) => {
      const {
        width,
        fieldName,
        fieldCode,
        fixed,
        renderOptions,
        conditionHeaderDTOs,
        renderRule,
        conValidDTO = {},
        fieldType,
      } = item;
      const oldCol = fieldMap.get(fieldCode);
      if (!oldCol && item.visible === -1) return;
      const {
        visible = item.visible,
        required = item.required,
        editable = item.editable,
      } = coverConfig(conditionHeaderDTOs, { ...tools, code }, ['editable', 'required']);
      const newFieldConfig = getFieldConfig({
        visible,
        required,
        editable,
      }); // ds配置覆盖
      if (visible === 0) {
        dataSet.addField(fieldCode, { ignore: true, custIgnore: true });
        fieldMap.delete(fieldCode);
        return;
      }
      if (fieldType === 'CHECKBOX' || fieldType === 'SWITCH') {
        newFieldConfig.trueValue = 1;
        newFieldConfig.falseValue = 0;
      }
      const validators = selfValidator(conValidDTO, { ...tools, code });
      const { defaultValue } = defaultValueFx({ ...tools, code }, item);
      const newColumnsConfig = {
        name: fieldCode,
        ...getColumnsConfig({
          fixed,
          width,
          visible,
        }),
      };
      // 原表格columns配置覆盖
      if (fieldName !== undefined) {
        newFieldConfig.label = fieldName;
        newColumnsConfig.header = fieldName;
      }
      if (oldCol && oldCol.header) {
        if (typeof oldCol.header === 'function') {
          newColumnsConfig.header = (records, name) => oldCol.header(records, fieldName, name);
        } else if (typeof oldCol.header === 'object') {
          newColumnsConfig.header = oldCol.header;
        }
      }
      const oldFieldConfig = (dataSet.getField(fieldCode) || {}).pristineProps || {};
      if (oldFieldConfig.custIgnore) {
        newFieldConfig.ignore = false;
        newFieldConfig.custConfig = false;
      }
      if (defaultValue !== undefined) {
        oldFieldConfig.defaultValue = defaultValue;
      }
      dataSet.addField(fieldCode, {
        ...parseProps(item, tools, oldFieldConfig),
        ...newFieldConfig,
        ...validators,
      });
      if (!oldCol) {
        const formFieldGen = (record) =>
          getComponent(item.fieldType, { currentData: record.toData() })(transformCompProps(item));
        newColumnsConfig.editor = false;
        if (readOnly || renderOptions === 'TEXT') {
          if (!isNil(renderRule)) {
            newColumnsConfig.renderer = (line) => (
              // eslint-disable-next-line react/no-danger
              <div
                // eslint-disable-next-line react/no-danger
                dangerouslySetInnerHTML={{
                  __html: template.render(renderRule, {
                    ...unitData,
                    self: line.record.toData(),
                  }),
                }}
              />
            );
          } else if (fieldType === 'DATE_PICKER') {
            newColumnsConfig.renderer = ({ value }) =>
              value && moment(value).format(item.dateFormat);
          }
        } else if (fieldType === 'LINK') {
          newColumnsConfig.renderer = (line) => formFieldGen(line.record);
        } else {
          newColumnsConfig.editor = formFieldGen;
        }
      }
      fieldMap.delete(fieldCode);
      newColumns.push({
        ...oldCol,
        ...newColumnsConfig,
      });
    });
    // 代码中而配置中没有的字段
    columns = newColumns.concat(Array.from(fieldMap.values()));
  }
  proxyTableProps.columns = columns;
  return table;
}
function initTableFilter({
  dataSet,
  filterCode,
  unitConfig,
  tools,
  tablePropsQueryFields,
  parentThis,
}) {
  const { fields: filterFields = [], unitAlias: unitFilterAlias = [], maxCol = 3 } = unitConfig;
  const cache = {};
  const queryFields = {};
  filterFields.sort((before, after) => before.seq - after.seq);
  const { queryDataSet = {} } = dataSet.props;
  let reCreateDs = isEmpty(queryDataSet);
  if (!reCreateDs && !queryDataSet.reCreateDs) {
    queryDataSet.addEventListener(
      'load',
      ({ record, value, name }) => {
        this.setDataMap(filterCode, record.toData());
        filterFields.forEach((item) => {
          const { fieldCode, lovMappings = [] } = item;
          if (lovMappings.length > 0 && name === fieldCode && typeof value === 'object') {
            lovMappings.forEach((i) => {
              record.set(i.targetCode, value[i.sourceCode]);
            });
          }
        });
        this.setState({ lastUpdateUnit: `${filterCode}${name}` });
      },
      false
    );
  }
  const searchFields = []; // 用于重新创建ds
  const dsQueryFieldsMap = {};
  const dsQueryFields = queryDataSet.fields;
  if (!isEmpty(dsQueryFields)) {
    const fieldObj = dsQueryFields.toJSON();
    Object.keys(fieldObj).forEach((i) => {
      dsQueryFieldsMap[i] = fieldObj[i].pristineProps;
    });
  }
  filterFields.forEach((item) => {
    const { fieldCode, fieldName, renderOptions, visible, renderRule, ...others } = item;
    const oldConfig = dsQueryFieldsMap[fieldCode];
    const config = { name: fieldCode };
    Object.assign(config, parseProps(item, tools, oldConfig), getFieldConfig(item));
    if (fieldName !== undefined) {
      config.label = fieldName;
    }
    const noOldElement = !dsQueryFieldsMap[fieldCode] && visible === 1;
    // 排除代码中不存在且显示属性为-1的情况
    const updateConfig = (dsQueryFieldsMap[fieldCode] && visible !== 0) || noOldElement;
    if (updateConfig) {
      searchFields.push(config);
    }
    if (!reCreateDs && updateConfig) {
      queryDataSet.addField(fieldCode, config);
    }
    if (noOldElement) {
      if (renderOptions === 'TEXT') {
        if (!isNil(renderRule)) {
          const renderer = () => {
            const unitFilterData = getFieldValueObject(unitFilterAlias, tools, filterCode);
            // eslint-disable-next-line react/no-danger
            return (
              <div
                dangerouslySetInnerHTML={{
                  __html: template.render(renderRule, unitFilterData),
                }}
              />
            );
          };
          queryFields[fieldCode] = (
            <Output name={fieldCode} label={fieldName} renderer={renderer} />
          );
        } else {
          queryFields[fieldCode] = <Output name={fieldCode} label={fieldName} />;
        }
      } else {
        queryFields[fieldCode] = getComponent(item.fieldType, {
          currentData: reCreateDs ? {} : queryDataSet.toData(),
        })(transformCompProps(others));
      }
      // eslint-disable-next-line no-param-reassign
      cache.queryFields = { ...tablePropsQueryFields, ...queryFields };
    }
    if (dsQueryFieldsMap[fieldCode] && visible === 0) {
      reCreateDs = true;
    }
    delete dsQueryFieldsMap[fieldCode];
  });
  cache.queryFieldsLimit = maxCol;
  const events = {
    update: ({ record, value, name }) => {
      this.setDataMap(filterCode, record.toData());
      filterFields.forEach((item) => {
        const { fieldCode, lovMappings = [] } = item;
        if (lovMappings.length > 0 && name === fieldCode && typeof value === 'object') {
          lovMappings.forEach((i) => {
            record.set(i.targetCode, value[i.sourceCode]);
          });
        }
      });
      parentThis.setState({ lastUpdateUnit: `${filterCode}${name}` });
    },
  };
  if (reCreateDs) {
    dataSet.queryDataSet = new DataSet({
      fields: searchFields.concat(Object.values(dsQueryFieldsMap)),
      events,
    });
    dataSet.queryDataSet.reCreateDs = true;
  }
  if (dataSet.queryDataSet.all.length === 0) {
    dataSet.queryDataSet.create();
  }
  if (!dataSet.queryDataSet.customize) {
    dataSet.queryDataSet.reset();
    dataSet.queryDataSet.create();
  }
  dataSet.queryDataSet.customize = true;
  return cache;
}
