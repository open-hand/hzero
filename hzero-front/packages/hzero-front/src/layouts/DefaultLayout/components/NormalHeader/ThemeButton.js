import React from 'react';
import styled from 'styled-components';
import { Tooltip } from 'hzero-ui';
import { withRouter } from 'dva/router';
import themeIcon from '@/assets/theme-configure.svg';
import intl from 'utils/intl';

const StyledThemeButton = styled.a`
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

function ThemeButton(props) {
  return (
    <StyledThemeButton onClick={() => props.history.push('/theme-config')}>
      <Tooltip title={intl.get('hzero.common.view.title.UED').d('UED配置')}>
        <img src={themeIcon} alt="theme-icon" />
      </Tooltip>
    </StyledThemeButton>
  );
}

export default withRouter(ThemeButton);
