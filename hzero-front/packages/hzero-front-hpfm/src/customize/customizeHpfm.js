import { setComponent } from 'hzero-front/lib/customize/hpfm';
import { dynamicWrapper } from '../utils/router/utils';

setComponent('PersonalLoginRecordRoute', () =>
  // TODO: there is use global vari dvaApp
  dynamicWrapper(window.dvaApp, ['personalLoginRecord'], () =>
    import('../routes/PersonalLoginRecord')
  )
);
