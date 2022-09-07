import React, { useState, useMemo } from 'react';
import { connect } from 'dva';
import { Spin } from 'hzero-ui';

import { getEnvConfig } from 'utils/iocUtils';
import { getAccessToken } from 'utils/utils';

import styles from './index.less';

function Link(props) {
  const { match: { params: { link = '' } = {} } = {}, global: { menuLeafNode = [] } = {} } = props;
  const [initLoading, setInitLoading] = useState(true);
  const [accessToken] = useState(getAccessToken());
  const API_HOST = useMemo(() => getEnvConfig().API_HOST, []);

  const menu = menuLeafNode.find((item) => item.id.toString() === link) || {};
  const type = menu.type || '';
  let url = '';
  if (type === 'link') {
    url = menu.path.startsWith('http') ? menu.path : `http://${menu.path}`;
  } else if (type === 'inner-link') {
    if (menu.path.startsWith('http')) {
      url = menu.path;
    } else {
      url = `${API_HOST}${menu.path}`;
    }
  }

  if (type === 'inner-link') {
    url = `${url}${url.includes('?') ? '&' : '?'}access_token=${accessToken}&from=hzero`;
  }

  return (
    (type === 'link' || type === 'inner-link') && (
      <Spin spinning={initLoading} wrapperClassName={styles['iframe-loading']}>
        <iframe
          onLoad={() => setInitLoading(false)}
          title={link}
          src={url}
          style={{ width: '100%', height: '100%', verticalAlign: 'top' }}
          frameBorder="0"
        />
      </Spin>
    )
  );
}

export default connect(({ global }) => ({ global }))(Link);
