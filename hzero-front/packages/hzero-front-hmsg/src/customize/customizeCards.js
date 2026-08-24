import { setCard } from 'hzero-front/lib/customize/cards';

import { dynamicWrapper } from '../utils/router/utils';

setCard({
  code: 'HzeroMessage',
  component: async () =>
    dynamicWrapper(window.dvaApp, ['cards/message'], () => import('../routes/Cards/Message')),
});
