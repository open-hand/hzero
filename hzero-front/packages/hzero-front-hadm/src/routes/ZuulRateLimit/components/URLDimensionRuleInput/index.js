/**
 * url 维度 规则编辑组件
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/25
 * @copyright 2019 ® HAND
 */

import React from 'react';
import uuid from 'uuid/v4';
import { Button, Input } from 'hzero-ui';

import EditTable from 'components/EditTable';

import intl from 'utils/intl';
import { getEditTableData } from 'utils/utils';
import { TABLE_OPERATOR_CLASSNAME } from 'utils/constants';

import {
  renderVariableWithString,
  transformArgsToString,
  transformStringToArgs,
  transformStringToVariable,
} from '../utils';

/**
 * url = /path[?search][#hash]
 * 没有就给 {1}
 * 输入值后 显示对应值
 */

/**
 * value = [arg1][;arg2][...;argN]
 * 没有就给 ''
 */

/**
 * [文本]
 * [输入表格]
 */

const URLArgTable = ({ dataSource = [], onChange }) => {
  const columns = React.useMemo(
    () => [
      {
        title: intl.get('hadm.zuulRateLimit.view.cmp.url.argValue').d('Value'),
        dataIndex: 'value',
        render(_, record) {
          const { _status, $form: form } = record;
          if (_status) {
            return form.getFieldDecorator('value', {
              initialValue: record.value,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hadm.zuulRateLimit.view.cmp.url.argValue').d('Value'),
                  }),
                },
              ],
            })(<Input style={{ width: '100%' }} />);
          } else {
            return record.value;
          }
        },
      },
    ],
    []
  );
  const handleSave = React.useCallback(() => {
    const validateDataSource = getEditTableData(dataSource);
    if (dataSource.length === validateDataSource.length) {
      onChange(validateDataSource);
    }
  }, [dataSource, onChange]);
  return (
    <>
      <div className={TABLE_OPERATOR_CLASSNAME}>
        <Button onClick={handleSave}>{intl.get('hzero.common.button.save').d('保存')}</Button>
      </div>
      <EditTable bordered rowKey="id" dataSource={dataSource} columns={columns} />
    </>
  );
};

const URLDimensionRuleInput = ({ value = '', onChange, url = '{1}' }) => {
  const dataSource = React.useMemo(() => {
    const data = transformStringToArgs(value);
    return transformStringToVariable(url)
      .filter(item => item.type === 'v')
      .map(() => ({
        id: uuid(),
        value: '',
        _status: 'update',
      }))
      .map((record, index) => ({
        ...record,
        value: data[index] || '',
      }));
  }, [url, value]);
  React.useEffect(() => {}, [value, url]);
  // TODO
  const displayValue = React.useMemo(
    () =>
      renderVariableWithString(url, transformArgsToString(dataSource.map(record => record.value))),
    [dataSource, url]
  );
  const handleArgChange = React.useCallback(
    newDataSource => {
      // setEditDataSource(newDataSource);
      onChange(transformArgsToString(newDataSource.map(rd => rd.value)));
    },
    [onChange]
  );

  return (
    <div>
      <div>{displayValue}</div>
      <URLArgTable dataSource={dataSource} onChange={handleArgChange} />
    </div>
  );
};

export default URLDimensionRuleInput;
