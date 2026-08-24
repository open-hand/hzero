import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { map, filter } from 'lodash';
import { DataSet, Button, Select, Table } from 'choerodon-ui/pro';
import { OPERATOR_TYPE } from '../constant';
import Store from './stores';

const QueryTable = observer(() => {
  const { queryDataSet, fieldArr } = useContext(Store);

  function handleFieldChange() {
    const value = queryDataSet.current.get('fieldName');
    const comparator = queryDataSet.current.getField('comparator');
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
              data: filter(
                OPERATOR_TYPE[field.type.toUpperCase()],
                type => type.value.indexOf('NULL') === -1
              ),
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
  ];

  const addBtn = (
    <Button
      funcType="flat"
      icon="add"
      key="query-add-btn"
      onClick={() => queryDataSet.create({}, 0)}
    >
      新增
    </Button>
  );

  return (
    <Table header="查询" buttons={[addBtn, 'delete']} dataSet={queryDataSet} columns={columns} />
  );
});

export default QueryTable;
