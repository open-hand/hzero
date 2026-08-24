import Authorized from './Authorized';
import AuthorizedRoute from './AuthorizedRoute';
import PubAuthorizedRoute from './PubAuthorizedRoute';
import DefaultAuthorizedRoute from './DefaultAuthorizedRoute';
import Secured from './Secured';
import check from './CheckPermissions';

// /* eslint-disable import/no-mutable-exports */
// const CURRENT = 'NULL';

Authorized.Secured = Secured;
Authorized.AuthorizedRoute = AuthorizedRoute;
Authorized.PubAuthorizedRoute = PubAuthorizedRoute;
Authorized.DefaultAuthorizedRoute = DefaultAuthorizedRoute;
Authorized.check = check;

/**
 * use  authority or getAuthority
 * @param {string|()=>String} currentAuthority
 */
const renderAuthorize = (/* currentAuthority */) =>
  // if (currentAuthority) {
  //   if (currentAuthority.constructor.name === 'Function') {
  //     CURRENT = currentAuthority();
  //   }
  //   if (currentAuthority.constructor.name === 'String') {
  //     CURRENT = currentAuthority;
  //   }
  // } else {
  //   CURRENT = 'NULL';
  // }
  Authorized;
// export { CURRENT };
export default renderAuthorize;
