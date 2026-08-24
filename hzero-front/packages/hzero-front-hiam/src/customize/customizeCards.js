import { setCard } from 'hzero-front/lib/customize/cards';

import { dynamicWrapper } from '../utils/router/utils';

setCard({
  code: 'HZERO_CommonlyUsed',
  component: async () =>
    dynamicWrapper(window.dvaApp, ['cards/commonlyUsed'], () =>
      import('../routes/Cards/commonlyUsed')
    ),
});
