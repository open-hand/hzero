/**
 * 个性化组件
 * @date: 2019-12-15
 * @version: 0.0.1
 * @author: zhaotong <tong.zhao@hand-china.com>
 * @copyright Copyright (c) 2019, Hands
 */

import React, { useEffect, useState } from 'react';
import { isString, isNil, isArray } from 'lodash';
import { Icon, Select, DatePicker, Checkbox, Switch, Radio } from 'hzero-ui';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';
import Lov from 'components/Lov';
import { getCurrentOrganizationId, getUserOrganizationId } from 'utils/utils';
import { queryMapIdpValue } from 'services/api';

// Option组件初始化
const { Option } = Select;

export function FlexSelect({
  lovCode,
  params = {},
  form,
  multipleFlag,
  fieldCode,
  onChange: oldOnChange,
  ...contentProps
}) {
  const cache = (window as any).CUSTOMIZECACHE || {};
  const cacheOptions = lovCode && cache[lovCode];
  const [selectOptionsDataSource, setSelectOptionsDataSource] = useState(cacheOptions || []);
  const [loading, setLoading] = useState(false);
  const updateTriggers = [lovCode].concat(Object.keys(params), Object.values(params)).join(',');
  useEffect(() => {
    if (!cacheOptions) {
      setLoading(true);
      if (!(window as any).CUSTOMIZECACHE) {
        (window as any).CUSTOMIZECACHE = {};
      }
      (window as any).CUSTOMIZECACHE[lovCode] = 'loading';
      queryMapIdpValue({ lovCode, ...params })
        .then((res = []) => {
          const options = (res && !res.failed && res.lovCode) || [];
          setSelectOptionsDataSource(options);
          (window as any).CUSTOMIZECACHE[lovCode] = options;
        })
        .catch(() => {
          (window as any).CUSTOMIZECACHE[lovCode] = false;
        })
        .finally(() => {
          setLoading(false);
        });
    } else if (cacheOptions !== 'loading') {
      setSelectOptionsDataSource(cacheOptions);
    }
  }, [updateTriggers, cacheOptions]);
  const onChange = (v) => {
    let value = v;
    if (multipleFlag && isArray(v)) {
      value = v.length > 0 ? v.join(',') : undefined;
    }
    // eslint-disable-next-line no-unused-expressions
    typeof oldOnChange === 'function' && oldOnChange(value);
  };
  const { value, defaultValue } = contentProps;
  const multipleConfig =
    multipleFlag === 1
      ? {
          mode: 'multiple',
          defaultValue: isNil(defaultValue) || defaultValue === '' ? [] : defaultValue.split(','),
          value: typeof value === 'string' && value !== '' ? value.split(',') : undefined,
        }
      : undefined;
  return (
    <Select
      allowClear
      {...contentProps}
      {...multipleConfig}
      style={{ width: '100%' }}
      onChange={onChange}
    >
      {loading || isString(selectOptionsDataSource) ? (
        <Option key="loading">
          <Icon type="loading" />
        </Option>
      ) : (
        selectOptionsDataSource.map((n: any) => (
          <Option value={String(n.value)}>{n.meaning}</Option>
        ))
      )}
    </Select>
  );
}
export function FlexRadioGroup({ lovCode, lovMappings = [], form, fieldCode, ...contentProps }) {
  const [radioOptionsDataSource, setRadioOptionsDataSource] = useState([]);
  useEffect(() => {
    queryMapIdpValue({ lovCode }).then((res = []) => {
      setRadioOptionsDataSource((res && !res.failed && res.lovCode) || []);
    });
  }, [1]);
  const onChange = (v) => {
    const record = radioOptionsDataSource.find((i: any) => i.value === v.target.value) || {};
    form.setFieldsValue({
      ...lovMappings.reduce(
        (pre, cur: any) => ({ ...pre, [cur.targetCode]: record[cur.sourceCode] }),
        {}
      ),
      [fieldCode]: v,
    });
  };
  return (
    <Radio.Group {...contentProps} style={{ width: '100%' }} onChange={onChange}>
      {radioOptionsDataSource.map((n: any) => (
        <Radio value={n.value}>{n.meaning}</Radio>
      ))}
    </Radio.Group>
  );
}
export function FlexLov({
  form,
  fieldName,
  textValue,
  textField,
  lovMappings = [],
  onChange,
  ...rest
}) {
  const innerOnChange = (_, record, ...args) => {
    // eslint-disable-next-line no-unused-expressions
    typeof onChange === 'function' && onChange(_, record, ...args);
    if (lovMappings.length > 0) {
      form.setFieldsValue({
        ...lovMappings.reduce(
          (pre, cur: any) => ({ ...pre, [cur.targetCode]: record[cur.sourceCode] }),
          {}
        ),
      });
    }
  };
  return <Lov onChange={innerOnChange} code={rest.lovCode} textValue={textValue} {...rest} />;
}
export function FlexDatePicker(options) {
  return <DatePicker format={DEFAULT_DATE_FORMAT} {...options} />;
}
export function FlexCheckbox(options) {
  return <Checkbox checkedValue={1} unCheckedValue={0} {...options} />;
}
export function FlexSwitch(options) {
  return <Switch checkedValue={1} unCheckedValue={0} {...options} />;
}
export function FlexLink(options) {
  const { linkTitle, linkHref, linkNewWindow, form, dataSource = {} } = options;
  let newHref = linkHref || '';
  let newTitle = linkTitle || '';
  const mappings = newHref.match(/{([^{}]*)}/g);
  const titleMappings = newTitle.match(/{([^{}]*)}/g);
  let values = {};
  if (mappings || titleMappings) {
    values = { ...dataSource, ...(form && form.getFieldsValue()) };
  }
  if (mappings) {
    newHref = replace(mappings, values, newHref);
  }
  if (titleMappings) {
    newTitle = replace(titleMappings, values, newTitle);
  }
  return (
    <a href={newHref} target={linkNewWindow ? '_blank' : '_self'} rel="noopener noreferrer">
      {newTitle}
    </a>
  );
}

function replace(mappings, values, targetString) {
  let newString = targetString;
  for (let i = 0; i < mappings.length; i++) {
    if (mappings[i] === '{organizationId}' || mappings[i] === '{tenantId}') {
      // eslint-disable-next-line no-continue
      continue;
    }
    const key = mappings[i].match(/{([^{}]*)}/)[1];
    const value = isNil(values[key]) ? '' : values[key];
    newString = newString.replace(`{${key}}`, value);
  }
  newString = newString.replace(/{organizationId}/, getCurrentOrganizationId());
  newString = newString.replace(/{tenantId}/, getUserOrganizationId());
  return newString;
}
