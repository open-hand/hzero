import React from 'react';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import styles from './index.less';

const SHOW_KEYS = {
  changedLibs: intl.get('hadm.marketclient.view.component.change').d('变更组件'),
  missingLibs: intl.get('hadm.marketclient.view.component.deprecate').d('弃用组件'),
  newLibs: intl.get('hadm.marketclient.view.component.deprecate').d('新增组件'),
};

function ComponentsCompare({ compareResult = {} }) {
  const compareItemContent = (value) => {
    const { artifactId, groupId, version } = value;
    const valueItem = (tag, val) => {
      const content = <span>{val}</span>;
      return (
        <p>
          <span>{`<${tag}>`}</span>
          <span>{content}</span>
          <span>{`</${tag}>`}</span>
        </p>
      );
    };

    return (
      <div className={styles['compare-item']}>
        {valueItem('groupId', groupId)}
        {valueItem('artifactId', artifactId)}
        {valueItem('version', version)}
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
    <div>
      {Object.keys(compareResult).map((key) => {
        if (!SHOW_KEYS[key]) return;
        return renderItemGroup(key, SHOW_KEYS[key], compareResult[key]);
      })}
    </div>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient'],
})(ComponentsCompare);
