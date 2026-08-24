import styles from './iconfont.css';

export function getIconClassName(iconStr) {
  return [styles.iconfont, styles[`hpfm-ui-icon-${iconStr}`]].join(' ');
}
