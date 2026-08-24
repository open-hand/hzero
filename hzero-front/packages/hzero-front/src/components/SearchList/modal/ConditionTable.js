import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Button, Select, Table, DataSet } from 'choerodon-ui/pro';
import { map } from 'lodash';
import Store from './stores';
import { getEditorByField } from '../utils';
import { OPERATOR_TYPE, OPERATOR } from '../constant';

const ConditionTable = observer(() => {
  const { conditionDataSet, tableDataSet, fieldArr } = useContext(Store);

  function handleFieldChange() {
    const value = conditionDataSet.current.get('fieldName');
    const comparator = conditionDataSet.current.getField('comparator');
    if (
      comparator.get('options') &&
      comparator.get('options').toData().length === OPERATOR_TYPE.ALL.length
    ) {
      map(fieldArr, field => {
        if (value === field.value) {
          comparator.set(
            'options',
            new DataSet({
              selection: 'single',
              data: OPERATOR_TYPE[field.type.toUpperCase()],
            })
          );
        }
      });
    }
  }

  const columns = [
    {
      name: 'fieldName',
      editor: <Select searchable />,
    },
    {
      name: 'comparator',
      editor: <Select onFocus={() => handleFieldChange()} />,
    },
    {
      name: 'value',
      editor: record => {
        if (
          record.get('fieldName') &&
          record.get('comparator') &&
          ![OPERATOR.IS_NULL.value, OPERATOR.IS_NOT_NULL.value].includes(record.get('comparator'))
        ) {
          const tableField = tableDataSet.getField(record.get('fieldName'));
          return tableField ? getEditorByField(tableField) : false;
        }
        return false;
      },
    },
  ];

  const addBtn = (
    <Button
      funcType="flat"
      icon="add"
      key="condition-add-btn"
      onClick={() => conditionDataSet.create({}, 0)}
    >
      新增
    </Button>
  );

  return (
    <Table
      header="条件"
      buttons={[addBtn, 'delete']}
      dataSet={conditionDataSet}
      columns={columns}
    />
  );
});

export default ConditionTable;
