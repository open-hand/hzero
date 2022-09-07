/**
 * Logo
 * @date 2019-02-28
 * @author WY yang.wang06@hand-china.com
 * @copyright ® HAND 2019
 */

import React from 'react';
import { Icon } from 'hzero-ui';
import { Link } from 'dva/router';

import defaultLogo from '../../../assets/hzero-default-favicon.png';

/**
 * @param {string} logo - logo
 * @param {boolean} collapsed - 是否需要缩放
 * @param {string} - title 标题
 * @param {object} styles - 传进来的样式 需要包含 logo, logo title, logo icon-img, logo icon-icon
 */
class DefaultHeaderLogo extends React.Component {
  render() {
    const { logo = defaultLogo, collapsed, title, styles = {} } = this.props;
    const logoClassNames = [styles.logo];
    if (collapsed) {
      logoClassNames.push(styles.collapsed);
    }
    return (
      <div className={logoClassNames.join(' ')}>
        <Link to="/">
          {this.renderIcon(logo)}
          {!collapsed && (
            <h1 className={styles.title} title={title}>
              {title}
            </h1>
          )}
        </Link>
      </div>
    );
  }

  renderIcon(icon) {
    const { styles = {} } = this.props;
    if (typeof icon === 'string' && (icon.startsWith('http') || icon.startsWith('data:'))) {
      return <img src={icon} alt="" className={styles['icon-img']} />;
    }
    if (typeof icon === 'string') {
      return <Icon type={icon} className={styles['icon-icon']} />;
    }
    return icon;
  }
}

export default DefaultHeaderLogo;
