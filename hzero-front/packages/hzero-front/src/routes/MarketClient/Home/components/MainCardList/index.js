import React from 'react';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import styles from './index.less';

const MainCardList = ({ icon, title, toAll, children }) => {
  return (
    <div className={styles.wrapper}>
      <div className={styles.head}>
        <div className={styles.title}>
          <img src={icon} alt="" />
          <span className={styles.name}>{title}</span>
        </div>
        <a className={styles.to} onClick={toAll}>
          {intl.get('hadm.marketclient.view.home.viewAll').d('查看全部')}
        </a>
      </div>
      <div className={styles.list}>{children}</div>
    </div>
  );
};

export default formatterCollections({
  code: ['hadm.marketclient'],
})(MainCardList);
