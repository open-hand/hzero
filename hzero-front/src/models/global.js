import { getGlobalModalConfig } from 'hzero-front/lib/models/global';
// import { hzeroFrontHlcdModelPlugin } from 'hzero-front-hlcd/lib/plugins';
import { getWrapperRouterData } from '@/utils/router';

export default getGlobalModalConfig({
  getWrapperRouterData,
  app: window.dvaApp,
  // plugins: { hzeroFrontHlcdModelPlugin },
});
