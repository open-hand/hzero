import { setTraceLog } from 'hzero-front/lib/customize/traceLog';

import { dynamicWrapper } from '../utils/router/utils';

setTraceLog({
  code: 'TraceLog',
  component: async () =>
    dynamicWrapper(window.dvaApp, ['global'], () => import('../routes/TraceLog')),
});
