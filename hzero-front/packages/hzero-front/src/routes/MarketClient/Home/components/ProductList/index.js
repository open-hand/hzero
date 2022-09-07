import React from 'react';
import { Icon } from 'hzero-ui';
import { CLIENT_JUMP_URL } from 'utils/market-client';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import ImgTrail from '../../../../../assets/market/free.svg';
import styles from './index.less';

const ProductList = ({ productInfo }) => {
  const handleToDetail = () => {
    window.open(`${CLIENT_JUMP_URL}/market-home/detail/${productInfo.productId}`);
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.logo}>
        <img src={productInfo.logoUrl} alt="logo" />
      </div>
      <div className={styles.content}>
        <div className={styles.main}>
          <div className={styles.info}>
            <div className={styles.name}>
              {productInfo.productName}
              <span className={styles.trail}>
                <img src={ImgTrail} alt="trail" />
                {intl.get('hadm.marketclient.view.home.trial.free').d('免费试用')}
              </span>
            </div>
            <span className={styles.category}>{productInfo.productCategories.join('-')}</span>
          </div>
          <div className={styles.detail} onClick={handleToDetail}>
            {intl.get('hzero.common.status.detail').d('查看详情')}
            <Icon type="caret-right" theme="filled" />
          </div>
        </div>
        <div className={styles.introduction}>{productInfo.introduction}</div>
      </div>
    </div>
  );
};

export default formatterCollections({
  code: ['hadm.marketclient'],
})(ProductList);
