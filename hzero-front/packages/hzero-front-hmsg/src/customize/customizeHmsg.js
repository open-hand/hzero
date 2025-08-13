import { setComponent } from 'hzero-front/lib/customize/hmsg';
import { dynamicWrapper } from '../utils/router/utils';

setComponent('UserReceiveConfigRoute', async () =>
  // TODO: there is use global vari dvaApp
  dynamicWrapper(window.dvaApp, ['userReceiveConfig'], () => import('../routes/UserReceiveConfig'))
);
