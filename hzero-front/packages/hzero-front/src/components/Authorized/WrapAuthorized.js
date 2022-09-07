import RenderAuthorized from './index';

let Authorized = RenderAuthorized(); // eslint-disable-line

// Reload the rights component
const reloadAuthorized = () => {
  Authorized = RenderAuthorized();
};

export { reloadAuthorized };
export default Authorized;
