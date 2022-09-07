import { setIM } from 'hzero-front/lib/customize/IM';

import { dynamicWrapper } from '../utils/router/utils';

setIM({
  code: 'HzeroIM',
  component: async () => dynamicWrapper(window.dvaApp, ['global'], () => import('../routes/IM')),
});
