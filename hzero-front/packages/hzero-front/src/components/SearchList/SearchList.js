import React, { useContext, useState, useEffect } from 'react';
import { observer } from 'mobx-react-lite';
import { Button, Select, Modal } from 'choerodon-ui/pro';
import { isEmpty, map } from 'lodash';
import classNames from 'classnames';
import styles from './style/index.less';
import SearchModal from './modal';
import Store from './stores';
import QueryBar from './QueryBar';
import { setCondition, setOrder } from './utils';

const modalKey = Modal.key();
const { Option } = Select;

const SearchList = observer(() => {
  const {
    className = '',
    dataSet,
    fields,
    ignoreFields,
    border = false,
    noCopy = false,
    noSort = false,
    noQuery = false,
    noCondition = false,
    listDataSet,
    searchCode,
    modalProps = {},
  } = useContext(Store);

  const [listValue, setListValue] = useState(undefined);
  const [type, setType] = useState(null);
  const syncDataLength = listDataSet.filter(record => record.status === 'sync').length;

  useEffect(() => {
    const fetchData = async () => {
      const result = await listDataSet.query();
      if (result.length) {
        const { conditionList, orderList, searchId } = listDataSet.current.toData();
        setListValue(searchId);
        dataSet.setQueryParameter('search', searchCode);
        await setCondition(dataSet, conditionList);
        await setOrder(dataSet, orderList);
        dataSet.query();
      } else {
        setListValue(undefined);
        dataSet.query();
      }
    };
    fetchData();
  }, []);

  useEffect(() => {
    if (listDataSet.length) {
      const { searchId } = listDataSet.current.toData();
      setListValue(searchId);
    } else {
      setListValue(null);
    }
  }, [syncDataLength]);

  /**
   * 搜索配置弹窗
   * @param modalType
   */
  function handleOpenModal(modalType) {
    setType(modalType);
    // 新建配置，重置查询参数
    if (modalType === 'create') {
      listDataSet.create({ searchCode }, 0);
    }
    Modal.open({
      okText: '保存',
      key: modalKey,
      title: '搜索配置',
      className: 'search-modal',
      closable: true,
      style: {
        width: 800,
      },
      ...modalProps,
      children: (
        <SearchModal
          tableDataSet={dataSet}
          searchCode={searchCode}
          searchId={listValue}
          fields={fields}
          ignoreFields={ignoreFields}
          listDataSet={listDataSet}
          noSort={noSort}
          noQuery={noQuery}
          noCondition={noCondition}
          type={modalType}
        />
      ),
      onClose: cancelClose,
    });
  }

  /**
   * 关闭弹窗
   * 重置查询条件并查询table
   */
  function cancelClose() {
    listDataSet.remove(listDataSet.filter(record => record.status === 'add'));
    setType(undefined);
  }

  function resetParams() {
    const queryDataSet = dataSet.queryDataSet || dataSet.props.queryDataSet;
    if (queryDataSet) queryDataSet.reset();
    const { conditionList, orderList } = listDataSet.current.toData();
    setCondition(dataSet, conditionList, true);
    setOrder(dataSet, orderList, true);
  }

  async function handleChange(value) {
    setListValue(value);

    // 重置查询参数
    await resetParams();

    if (!value) {
      dataSet.query();
    } else {
      listDataSet.map(async record => {
        if (record.get('searchId') === value) {
          listDataSet.locate(record.index);
          const { conditionList, orderList } = record.toData();
          await setCondition(dataSet, conditionList);
          await setOrder(dataSet, orderList);
          dataSet.query();
        }
        return null;
      });
    }
  }

  async function handleDelete() {
    await resetParams();
    const res = await listDataSet.delete(listDataSet.current, '是否删除本条搜索模板？');
    if (!isEmpty(res)) {
      await listDataSet.query();
      if (listDataSet.length) {
        const { conditionList, orderList } = listDataSet.current.toData();
        dataSet.setQueryParameter('search', searchCode);
        await setCondition(dataSet, conditionList);
        await setOrder(dataSet, orderList);
      }
      dataSet.query();
    }
  }

  const classString = classNames(styles['search-list-wrap'], {
    [styles['search-list-wrap-border']]: border,
  });

  return (
    <div className={`${className} ${classString}`}>
      <span className={styles['sl-tip']}>查看列表</span>
      <Select searchable value={listValue} onChange={handleChange}>
        {map(listDataSet.toData(), data => (
          <Option value={data.searchId}>{data.searchName}</Option>
        ))}
      </Select>

      <Button funcType="flat" icon="add_task" onClick={() => handleOpenModal('create')}>
        新建
      </Button>
      <Button
        funcType="flat"
        icon="mode_edit"
        disabled={!listValue}
        onClick={() => handleOpenModal('edit')}
      >
        编辑
      </Button>
      {!noCopy && (
        <Button
          funcType="flat"
          icon="baseline-file_copy"
          disabled={!listValue}
          onClick={() => handleOpenModal('copy')}
        >
          复制
        </Button>
      )}
      <Button
        color="red"
        funcType="flat"
        icon="delete"
        disabled={!listValue}
        onClick={() => handleDelete()}
      >
        删除
      </Button>
      {!noQuery && listValue && <QueryBar type={type} searchId={listValue} />}
    </div>
  );
});

export default SearchList;
