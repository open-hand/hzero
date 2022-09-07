import { Input } from 'hzero-ui';
import { proxyComponentMethod } from '@/utils/utils';

export default proxyComponentMethod({
  config: [
    {
      method: 'onChange',
      args: ['record'],
    },
  ],
  omitProps: ['onChange', 'record'],
})(Input);
