import { Button } from 'hzero-ui';
import { proxyComponentMethod } from '@/utils/utils';

export default proxyComponentMethod({
  config: [
    {
      method: 'onClick',
      args: ['record'],
    },
  ],
  omitProps: ['onClick', 'record'],
})(Button);
