import { kebabCase } from 'lodash';

/**
 * build the className by paths
 * 1. prefix side-layout in paths,
 * 2. let paths join with -
 * 3. call lodash.kebabCase
 * 4. return className
 * @param {...string} [paths] - the strings of className
 */
function defaultGetClassName(...paths) {
  return kebabCase(['hzero-side-line-layout', ...paths].join('-'));
}

/**
 * 遍历并转化树
 * @param tree
 * @param iter
 * @param options
 */
function travelMapTree(tree, iter, options = {}) {
  return (tree || []).map(item => {
    const { childrenName = 'children', parent, childrenNullable = false } = options || {};
    const tran = iter(item, parent);
    if (tran[childrenName] || !childrenNullable) {
      tran[childrenName] = travelMapTree(tran[childrenName] || [], iter, {
        ...options,
        parent: tran,
      });
    }
    return tran;
  });
}

// /**
//  * 遍历树
//  * @param tree
//  * @param iter
//  * @param options
//  */
// function travelTreeFind(tree, iter, options = {}) {
//   return (tree || []).find(item => {
//     const { childrenName = 'children', parent, childrenNullable = false } = options || {};
//     const find = iter(item, parent);
//     if (!find && (item[childrenName] || !childrenNullable)) {
//       return travelMapTree(item[childrenName] || [], iter, { ...options, parent: item });
//     }
//     return find;
//   });
// }

export { defaultGetClassName, travelMapTree };
