import React, { useContext, useEffect, useState } from 'react';
import { observer } from 'mobx-react-lite';
import { DataSet, Table, Stores } from 'choerodon-ui/pro';
import { map } from 'lodash';
import moment from 'moment';
import styles from './style/index.less';
import Store from './stores';
import { getQueryFields } from './utils';

const { ToolBar, AdvancedQueryBar } = Table;

const QueryBar = observer(({ searchId }) => {
  const { dataSet, listDataSet, fields, queryBar = 'normal', queryFieldsLimit = 0 } = useContext(
    Store
  );
  const [props, setProps] = useState(null);
  const resetArr = [];
  const syncDataLength = listDataSet.filter(record => record.status === 'sync').length;

  function setQuery(name, value, comparator, type) {
    let vl = value;
    if (type === 'date') vl = moment(value).format('YYYY-MM-DD HH:mm:ss');
    dataSet.setQueryParameter(
      `search.condition.${name}`,
      `${name.split('-')[0]},${comparator},${vl}`
    );
    resetArr.push(`search.condition.${name}`);
  }

  async function findLovParam(name, value) {
    const field = dataSet.getField(`${name.split('-')[0]}`);
    if (field) {
      const lovCode = field.get('lovCode');
      const valueField = field.get('valueField');
      if (lovCode) {
        if (!valueField) {
          const config = await Stores.LovCodeStore.fetchConfig(lovCode);
          return {
            name: config.valueField,
            value: value[config.valueField],
          };
        } else {
          return {
            name: valueField,
            value: value[valueField],
          };
        }
      }
      return {
        name,
        value,
      };
    }
  }

  useEffect(() => {
    if (listDataSet.length) {
      const dsFields = fields || map(Object.values(dataSet.fields.toJSON()), field => field.props);
      const queryFields = listDataSet.find(record => record.get('searchId') === searchId).toData()
        .queryList;
      const barFields = [];
      map(queryFields, (qField, index) => {
        map(dsFields, field => {
          if (qField.fieldName === field.name) {
            barFields.push({
              ...field,
              name: `${field.name}-${index}`,
              type: field.type === 'intl' ? 'string' : field.type,
              defaultValue: qField.comparator,
            });
          }
        });
      });
      dataSet.props.queryFields = [];
      dataSet.props.queryDataSet = new DataSet({
        fields: barFields,
        events: {
          update: async ({ name, value }) => {
            const param = await findLovParam(name, value);
            if (param.name.indexOf('search.condition') !== 0) {
              const { defaultValue, type } = barFields.filter(t => t.name === name)[0];
              setQuery(param.name, param.value, defaultValue, type);
            }
          },
          reset: () => {
            map(resetArr, name => {
              dataSet.setQueryParameter(name, '');
            });
          },
        },
      });

      setProps({
        dataSet,
        queryDataSet: dataSet.queryDataSet || dataSet.props.queryDataSet,
        buttons: [],
        queryFields: getQueryFields(dataSet),
        queryFieldsLimit,
      });
    }
  }, [syncDataLength, searchId]);

  function renderBar() {
    if (typeof queryBar === 'function') {
      return queryBar(props);
    }
    switch (queryBar) {
      case 'normal':
        return <ToolBar key="toolbar" {...props} />;
      case 'advancedBar':
        return <AdvancedQueryBar key="advancedBar" {...props} />;
      default:
        return null;
    }
  }

  return <div className={styles['sl-query']}>{props && renderBar()}</div>;
});

export default QueryBar;
