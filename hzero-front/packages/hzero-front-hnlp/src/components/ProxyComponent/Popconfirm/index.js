import { Popconfirm } from 'hzero-ui';
import { proxyComponentMethod } from '@/utils/utils';

/**
 * 代理 Popconfirm 的 onConfirm 方法 和 record 属性
 */
export default proxyComponentMethod({
  config: [
    {
      method: 'onConfirm',
      args: ['record'],
    },
  ],
  omitProps: ['onConfirm', 'record'],
})(Popconfirm);
