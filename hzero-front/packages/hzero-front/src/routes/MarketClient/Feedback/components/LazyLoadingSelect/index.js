import React, { useEffect, useState } from 'react';
import { Select } from 'hzero-ui';

import Loading from '../../../components/Loading';

import styles from './index.less';

/**
 * 懒加载下拉框，滚动底部加载下一页数据
 */
const DEFAULT_PAGE_SIZE = 10;
const { Option } = Select;

export default ({
  category,
  value,
  onChange,
  queryApi,
  subCategories,
  setSubCategories,
  onSubCategoryChange,
  setFieldsValue,
  setPageLoading,
}) => {
  const [optionsLoading, setOptionsLoading] = useState(false);
  const [totalPages, setTotalPages] = useState(1);
  const [current, setCurrent] = useState(1);

  // 初始化被选中节点
  useEffect(() => {
    query(1);
  }, [category]);

  // 滚动到底部，加载下一页数据
  function onPopupScroll(e) {
    e.persist();
    const { target } = e;
    if (Math.round(target.scrollTop + target.offsetHeight) >= Math.round(target.scrollHeight)) {
      query(current + 1); // 调用api方法
    }
  }

  async function query(_current = 1) {
    if (_current <= totalPages && !optionsLoading) {
      setOptionsLoading(true);
      if (_current === 1) {
        setPageLoading(true);
      }
      const res = await queryApi({
        page: _current,
        size: DEFAULT_PAGE_SIZE,
        category,
      });
      setPageLoading(false);
      setOptionsLoading(false);
      if ((res && res.failed) || !res) {
        return;
      }
      setCurrent(_current);
      // eslint-disable-next-line no-unused-expressions
      res.totalPages && setTotalPages(res.totalPages + 1); // 修复重置搜索结果的totalPages导致不能全局搜索；由于服务器 totalPages 从 0 开始统计，故自行计算 totalPages

      let newList;
      if (_current === 1) {
        newList = [...res.content];
        onSubCategoryChange(newList[0]?.sourceHeaderId, newList);
        setFieldsValue({ sourceHeaderId: newList[0]?.sourceHeaderId });
      } else {
        newList = [...subCategories, ...res.content];
      }
      setSubCategories(newList);
    }
  }

  return (
    <Select
      value={value}
      allowClear
      showSearch
      onPopupScroll={onPopupScroll}
      onChange={onChange}
      dropdownClassName={styles['lazy-loading-select']}
      filterOption={false}
      getPopupContainer={(triggerNode) => triggerNode.parentElement}
    >
      {(subCategories || []).map(({ sourceHeaderId, subcategory }) => {
        return (
          <Option value={sourceHeaderId} className={current === 0 ? styles.hidden : ''}>
            {subcategory}
          </Option>
        );
      })}
      {optionsLoading && (
        <Option key="loading" disabled>
          <Loading
            wrapperStyle={{
              minHeight: 32,
              marginTop: -8,
            }}
          />
        </Option>
      )}
    </Select>
  );
};
