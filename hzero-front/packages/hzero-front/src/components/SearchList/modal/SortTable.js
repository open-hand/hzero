import React, { useContext } from 'react';
import { observer } from 'mobx-react-lite';
import { Button, Select, Table } from 'choerodon-ui/pro';
import Store from './stores';

const SortTable = observer(() => {
  const { sortDataSet } = useContext(Store);

  const columns = [
    {
      name: 'fieldName',
      editor: <Select searchable />,
    },
    {
      name: 'direction',
      editor: true,
    },
  ];

  const sortAddBtn = (
    <Button funcType="flat" icon="add" key="sort-add-btn" onClick={() => sortDataSet.create({}, 0)}>
      新增
    </Button>
  );

  return (
    <Table header="排序" buttons={[sortAddBtn, 'delete']} dataSet={sortDataSet} columns={columns} />
  );
});

export default SortTable;
