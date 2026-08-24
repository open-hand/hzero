import intl from 'react-intl-universal';

// TODO: 因为 react-intl-universal 切换语言时候会加载外网的一个js，此文件为了将来改造方便而创建.
export default intl;

window.intl = intl;
