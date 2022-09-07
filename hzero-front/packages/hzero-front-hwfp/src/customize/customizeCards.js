import { setCard } from 'hzero-front/lib/customize/cards';
import { dynamicWrapper } from '../utils/router/utils';

setCard({
  code: 'Workflow',
  component: async () =>
    dynamicWrapper(window.dvaApp, ['cards/workflow'], () => import('../routes/Cards/Workflow')),
});
