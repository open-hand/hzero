import { kebabCase } from 'lodash';

/**
 * build the className by paths
 * 1. prefix hzero-side-layout-menu in paths,
 * 2. let paths join with -
 * 3. call lodash.kebabCase
 * 4. return className
 * @param {...string} [paths] - the strings of className
 */
function defaultGetClassName(...paths) {
  return kebabCase(['hzero-common-layout-header-search', ...paths].join('-'));
}

function defaultGetHistoryClassName(...paths) {
  return defaultGetClassName('history', ...paths);
}

export { defaultGetClassName, defaultGetHistoryClassName };
