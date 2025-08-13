/**
 * 监听 favicon 的 修改, 监听到后, 刷新 header/meta 信息
 * TODO: 是否修改为 公共的更新修改 header/meta 信息 组件
 */
import React from 'react';
import { connect } from 'dva';

let iconElement;

/**
 * 更新 favicon
 */
function updateFavicon(favicon) {
  if (favicon) {
    if (!iconElement) {
      iconElement = document.querySelector('head > link[rel="shortcut icon"]');
    }

    if (!iconElement) {
      // 1
      const linkTag = document.createElement('link');
      linkTag.href = `${(process.env.PUBLIC_URL || '').replace(/\/$/, '')}/favicon.ico`;
      linkTag.setAttribute('rel', 'shortcut icon');
      document.getElementsByTagName('head')[0].append(linkTag);

      // 2
      let headHTML = document.getElementsByTagName('head')[0].innerHTML;
      headHTML += `<link rel="shortcut icon" href="${(process.env.PUBLIC_URL || '').replace(
        /\/$/,
        ''
      )}/favicon.ico" />`;
      document.getElementsByTagName('head')[0].innerHTML = headHTML;
    }

    if (iconElement && iconElement.href !== favicon) {
      const img = new Image();
      img.onload = () => {
        iconElement.href = favicon;
      };
      img.src = favicon;
    }
  }
}

class DefaultListenFavicon extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {};
  }

  static getDerivedStateFromProps(nextProps) {
    updateFavicon(nextProps.favicon);
    return null;
  }

  render() {
    return null;
  }
}

export default connect(({ user = {} }) => ({
  favicon: (user.currentUser || {}).favicon,
}))(DefaultListenFavicon);
