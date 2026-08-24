import React from 'react';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import styles from './index.less';

const nameMap = {
  newConfigYml: intl.get('hadm.marketclient.view.config.new').d('新增配置'),
  missingConfigYml: intl.get('hadm.marketclient.view.config.deprecate').d('弃用配置'),
};

const ConfigCompare = ({ compareResult = {} }) => {
  const renderItemGroup = (configType, content) => {
    return (
      <div
        className={`${styles['compare-group']} ${
          ['missingConfigYml', 'invalidConfigYml'].includes(configType) ? styles.delete : null
        }`}
      >
        <h3>{nameMap[configType]}</h3>
        {content && (
          <div className={styles.content} dangerouslySetInnerHTML={{ __html: content }} />
        )}
      </div>
    );
  };

  return (
    <div className={styles.wrapper}>
      {Object.entries(compareResult).map(([configType, content]) =>
        renderItemGroup(configType, content)
      )}
    </div>
  );
};

export default formatterCollections({
  code: ['hadm.marketclient'],
})(ConfigCompare);
