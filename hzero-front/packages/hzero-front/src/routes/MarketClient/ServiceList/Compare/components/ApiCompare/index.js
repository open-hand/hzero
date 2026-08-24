import React from 'react';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import styles from './index.less';

const SHOW_KEYS = {
  changedEndpoints: intl.get('hadm.marketclient.view.interface.change').d('变更接口'),
  missingEndpoints: intl.get('hadm.marketclient.view.interface.deprecate').d('弃用接口'),
  newEndpoints: intl.get('hadm.marketclient.view.interface.new').d('新增接口'),
};
const KEY_ADD = 'add';
const KEY_DELETE = 'delete';
const KEY_MODIFY = 'modify';

function ApiCompare({ compareResult = {} }) {
  const handleModifyValue = (data) => {
    const { leftParameter = {}, rightParameter = {} } = data;
    let resText = '';

    if (leftParameter.required !== rightParameter.required) {
      resText += `${intl.get('hadm.marketclient.view.interface.required').d('是否必输')}：${
        leftParameter.required
      } -> ${rightParameter.required} `;
    } else if (leftParameter.format !== rightParameter.format) {
      resText += `${intl.get('hzero.common.model.common.type').d('类型')}：${
        leftParameter.format
      } -> ${rightParameter.format} `;
    } else if (leftParameter.in !== rightParameter.in) {
      resText += `${intl.get('hadm.marketclient.view.interface.params').d('参数位置')}：${
        leftParameter.in
      } -> ${rightParameter.in} `;
    } else if (leftParameter.description !== rightParameter.description) {
      resText += `${intl.get('hadm.marketclient.view.interface.notes').d('注释')}：${
        leftParameter.description
      } -> ${rightParameter.description} `;
    }

    return resText;
  };
  const renderDiffResultItemOne = (data) => {
    const { name, el, description, action, leftParameter = {} } = data;
    let icon;
    const nameText = name || el; // params props 返回值结构不同
    let newDescription = description;
    switch (action) {
      case KEY_ADD:
        icon = `${intl.get('hadm.marketclient.view.field.new').d('新增字段')}：`;
        break;
      case KEY_DELETE:
        icon = `${intl.get('hadm.marketclient.view.field.delete').d('删除字段')}：`;
        break;
      case KEY_MODIFY:
        icon = `${leftParameter.name || ''} ${intl
          .get('hadm.marketclient.view.field.change')
          .d('字段修改')}：`;
        newDescription = handleModifyValue(data);
        break;
      default:
    }

    if (!newDescription && !nameText) return null;

    return (
      <div className={`${styles['diff-result-item']} ${styles[action]}`}>
        <span>{icon}</span>
        <span>{nameText}</span>
        <span>{newDescription}</span>
      </div>
    );
  };
  const renderDiffResultItem = (dataList, action) => {
    if (!Array.isArray(dataList) || !dataList.length) return null;
    return dataList.map((o) => renderDiffResultItemOne({ ...o, action }));
  };
  const renderDiffContent = (changedEndpointsItem) => {
    const {
      diffParam, // 是否有 diff 的结果
      diffProp,
      addParameters = [], // 增加的属性
      addProps = [],
      changedParameter = [], // 改变的属性
      changedProps = [],
      missingParameters = [], // 删除的属性
      missingProps = [],
    } = changedEndpointsItem;
    return (
      <div className={styles['diff-result']}>
        {diffParam ? (
          <div className={styles['result-sub-title']}>
            {intl.get('hadm.marketclient.view.model.params').d('参数')}：
          </div>
        ) : null}
        {renderDiffResultItem(addParameters, KEY_ADD)}
        {renderDiffResultItem(changedParameter, KEY_MODIFY)}
        {renderDiffResultItem(missingParameters, KEY_DELETE)}
        {diffProp ? (
          <div className={styles['result-sub-title']}>
            {intl.get('hadm.marketclient.view.model.return').d('返回值')}：
          </div>
        ) : null}
        {renderDiffResultItem(addProps, KEY_ADD)}
        {renderDiffResultItem(changedProps, KEY_MODIFY)}
        {renderDiffResultItem(missingProps, KEY_DELETE)}
      </div>
    );
  };
  const renderComparePathHeader = ({ method, pathUrl, summary }) => {
    return (
      <h3 className={styles['compare-path-header']}>
        <div className={styles[String(method).toLowerCase()]}>{method}</div>
        <div>{pathUrl}</div>
        <div>{summary}</div>
      </h3>
    );
  };
  const compareItemContent = (item) => {
    const { diff } = item;

    return (
      <div className={styles['compare-item']}>
        {renderComparePathHeader(item)}
        {diff ? renderDiffContent(item) : null}
      </div>
    );
  };
  const renderItemGroup = (key, title, listData = []) => {
    return (
      <div className={`${styles['compare-group']} ${styles[key]}`}>
        <h3>{title}</h3>
        {listData.map((o) => compareItemContent(o))}
      </div>
    );
  };

  return (
    <div className={styles['api-diff-result-wrap']}>
      {Object.keys(compareResult).map((key) => {
        if (!SHOW_KEYS[key]) return null;
        return renderItemGroup(key, SHOW_KEYS[key], compareResult[key]);
      })}
    </div>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient'],
})(ApiCompare);
