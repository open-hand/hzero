import React, { useState } from 'react';
import styles from './index.less';

const Key = 'categoryId';
const InlineIndent = 12;

export default function CategoryList({ category, categoryChange }) {
  const [activeKey, setActiveKey] = useState('');
  if (!Array.isArray(category)) return null;

  const titleClass = (level, key) => {
    let res = styles['tree-item-leaf'];
    if (level === 0) {
      res = `${res} ${styles['tree-item-leaf-0']}`;
    }
    if (activeKey === key) {
      res = `${res} ${styles['tree-item-leaf-active']}`;
    }
    return res;
  };

  const handleClickCategory = (key, item) => {
    if (key === activeKey) {
      // 取消
      setActiveKey('');
      if (categoryChange) {
        categoryChange('', {});
      }
    } else {
      // 选中
      setActiveKey(key);
      if (categoryChange) {
        categoryChange(key, item);
      }
    }
  };

  const loop = (data) => {
    return data.map((item) => {
      if (item.children) {
        return (
          <ul className={styles['tree-wrapper']} key={item[Key]}>
            <li
              onClick={() => handleClickCategory(item[Key], item)}
              className={titleClass(item.level, item[Key])}
              key={item[Key]}
              style={{ paddingLeft: `${(item.level + 1) * InlineIndent}px` }}
            >
              {item.categoryName}
            </li>
            {item.children.length ? (
              <li className={styles['tree-item-node']}>{loop(item.children)}</li>
            ) : null}
          </ul>
        );
      }
      return (
        <li
          onClick={() => handleClickCategory(item[Key], item)}
          className={
            item[Key] === activeKey
              ? `${styles['tree-item-leaf']} ${styles['tree-item-leaf-active']}`
              : styles['tree-item-leaf']
          }
          style={{ paddingLeft: `${item.level * InlineIndent}px` }}
          key={item[Key]}
        >
          {item.categoryName}
        </li>
      );
    });
  };

  return <div className={styles['category-wrapper']}>{loop(category)}</div>;
}
