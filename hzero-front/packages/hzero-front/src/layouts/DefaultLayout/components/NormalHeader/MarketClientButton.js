import React, { useState, useEffect } from 'react';
import fetch from 'dva/fetch';
import { connect } from 'dva';
import { Tooltip } from 'hzero-ui';
import { getAccessToken, getRequestId } from 'utils/utils';
import { API_HOST, HZERO_ADM } from 'utils/config';
import styled from 'styled-components';
import { withRouter } from 'dva/router';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import MarketIcon from '@/assets/market/market-enter-icon.png';

const StyledMarketClientButton = styled.a`
  display: flex;
  line-height: 28px;
  font-size: 14px;
  color: #fff;
  white-space: nowrap;
  align-items: center;

  :hover {
    opacity: 0.8;
    color: #fff;
  }

  img {
    width: 24px;
    height: 24px;
    margin-right: 12px;
  }
`;

function MarketClientButton(props) {
  const [isShowButton, setIsShowButton] = useState(false);
  useEffect(() => {
    queryShowButtonResult();
  }, []);

  const queryShowButtonResult = () => {
    // 这个请求很特殊，只有有结果的时候才会显示按钮，否则静默失败
    const checkStatus = (response) => {
      if (response.status >= 200 && response.status < 300) {
        return response.json();
      }
    };
    fetch(`${API_HOST}${HZERO_ADM}/v1/market/config/show-flag`, {
      headers: {
        Authorization: `bearer ${getAccessToken()}`,
        'H-Request-Id': `${getRequestId()}`,
      },
    })
      .then(checkStatus)
      .then((res = {}) => {
        if (res) {
          try {
            setIsShowButton(!!res.iconFlag);
            props.dispatch({
              type: 'global/updateMarketConfig',
              payload: {
                feedbackFlag: !!res.feedbackFlag,
              },
            });
          } catch (e) {
            console.error(e);
          }
        }
      });
  };
  if (!isShowButton) return null;
  return (
    <StyledMarketClientButton onClick={() => props.history.push('/market-client')}>
      <Tooltip title={intl.get('hadm.marketclient.view.market.client').d('应用市场')}>
        <img src={MarketIcon} alt="market-icon" />
      </Tooltip>
    </StyledMarketClientButton>
  );
}

export default formatterCollections({
  code: ['hadm.marketclient'],
})(connect()(withRouter(MarketClientButton)));
