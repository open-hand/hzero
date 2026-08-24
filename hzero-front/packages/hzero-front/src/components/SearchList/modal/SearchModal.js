import React, { useContext, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { map, isEmpty, cloneDeep } from 'lodash';
import { Form, TextField, CheckBox } from 'choerodon-ui/pro';
import notification from 'utils/notification';
import SortTable from './SortTable';
import QueryTable from './QueryTable';
import ConditionTable from './ConditionTable';
import Store from './stores';
import { setCondition, setOrder, omitData } from '../utils';

const SearchModal = observer(() => {
  const {
    searchId,
    searchCode,
    tableDataSet,
    searchDataSet,
    listDataSet,
    type,
    modal,
    noCondition,
    noSort,
    noQuery,
  } = useContext(Store);

  function clearData(data) {
    const cloneData = cloneDeep(data);
    cloneData.queryList = map(cloneData.queryList, q => omitData(q));
    cloneData.conditionList = map(cloneData.conditionList, c => omitData(c));
    cloneData.orderList = map(cloneData.orderList, s => omitData(s));
    return omitData(cloneData);
  }

  useEffect(() => {
    const record = listDataSet.find(r => r.get('searchId') === searchId);
    if (['copy', 'edit'].includes(type)) {
      const searchName = record.get('searchName');

      if (type === 'edit') searchDataSet.loadData([record.toData()]);
      if (type === 'copy') {
        searchDataSet.create(clearData(record.toData()), 0);
        searchDataSet.current.set('searchName', `${searchName}_copy`);
      }
    } else {
      searchDataSet.create({ searchCode }, 0);
    }
  }, []);

  modal.handleOk(async () => {
    try {
      const queryModified = searchDataSet.children.queryList.dirty;
      const preRecord = listDataSet.find(record => record.get('searchId') === searchId);
      const res = await searchDataSet.submit();
      if (!isEmpty(res) && res.success) {
        // 重置查询参数
        const queryDataSet = tableDataSet.queryDataSet || tableDataSet.props.queryDataSet;
        if (queryDataSet) queryDataSet.reset();
        if (preRecord) {
          const { conditionList, orderList } = preRecord.toData();
          await setCondition(tableDataSet, conditionList, true);
          await setOrder(tableDataSet, orderList, true);
        }

        const currentId = res.content[0].searchId;

        await listDataSet.query();

        await listDataSet.map(async record => {
          if (record.get('searchId') === currentId) {
            listDataSet.locate(record.index);
            record.set('queryModified', queryModified);
            const { conditionList, orderList } = record.toData();
            tableDataSet.setQueryParameter('search', searchCode);
            await setCondition(tableDataSet, conditionList);
            await setOrder(tableDataSet, orderList);
          }
          return null;
        });
        tableDataSet.query();
        return true;
      } else if (res === undefined) {
        notification.warning({
          message: '表单未做修改',
        });
        return false;
      }
      if (!isEmpty(res) && res.failed && res.message) {
        notification.error({
          message: res.message,
        });
      }
      return false;
    } catch (e) {
      return false;
    }
  });

  return (
    <>
      <Form labelLayout="horizontal" columns={2} dataSet={searchDataSet}>
        <TextField name="searchName" />
        <CheckBox name="defaultFlag" />
        <TextField name="remark" colSpan={2} />
      </Form>
      {!noSort && <SortTable />}
      {!noQuery && <QueryTable />}
      {!noCondition && <ConditionTable />}
    </>
  );
});

export default SearchModal;
