/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */
import classNames from 'classnames';
import { Link } from 'dva/router';
import { Icon } from 'hzero-ui';
import { isEqual } from 'lodash';
import React from 'react';

import { getEnvConfig } from 'utils/iocUtils';
import { getClassName as getCommonLayoutClassName } from '../../../../utils';

// @ts-ignore
import trialInfo from '../../../../../../assets/trial-info.png';

interface LogoProps {
  components: {
    Icon: React.FC;
    Title: React.FC;
  };
  getClassName: (cls: string) => string;
  logo: string;
  title: string;
  config: any;
}

interface IconProps {
  icon: string;
  getClassName?: (cls: string) => string;
}

interface TitleProps {
  title: string;
  getClassName?: (cls: string) => string;
}

const DefaultIcon: React.FC<IconProps> = ({ icon, getClassName = getCommonLayoutClassName }) => {
  if (typeof icon === 'string') {
    if (icon.startsWith('http') || icon.startsWith('data:')) {
      return (
        <img
          src={icon}
          alt=""
          className={classNames(
            getClassName('header-logo-icon'),
            getClassName('header-logo-icon-img')
          )}
        />
      );
    }
    return (
      <Icon
        type={icon}
        className={classNames(
          getClassName('header-logo-icon'),
          getClassName('header-logo-icon-icon')
        )}
      />
    );
  }
  return icon || null;
};

const DefaultTitle: React.FC<TitleProps> = ({ title, getClassName = getCommonLayoutClassName }) => {
  return (
    <h1 className={getClassName('header-logo-title')} title={title}>
      {title || null}
    </h1>
  );
};

const Logo: React.FC<LogoProps> = ({
  components = { Icon: DefaultIcon, Title: DefaultTitle },
  getClassName = getCommonLayoutClassName,
  title,
  logo,
  config = getEnvConfig(),
}) => {
  const { Icon: LogoIcon, Title } = components;
  const { ENV_SIGN } = config;
  return (
    <div className={getClassName('header-left')}>
      {!isEqual(ENV_SIGN, 'undefined') && (
        <div className={getClassName('header-sign')}>
          <img src={trialInfo} alt="trial-info" className={getClassName('header-trail-img-icon')} />
          <span className={getClassName('header-sign-title')}>{ENV_SIGN}</span>
        </div>
      )}
      <div
        className={
          !isEqual(ENV_SIGN, 'undefined')
            ? getClassName('header-logo-sign')
            : getClassName('header-logo')
        }
      >
        <Link to="/">
          <LogoIcon icon={logo} />
          <Title title={title} />
        </Link>
      </div>
    </div>
  );
};

export default Logo;
