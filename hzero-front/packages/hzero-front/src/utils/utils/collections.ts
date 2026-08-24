/**
 * 对集合操作的方法
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/30
 * @copyright HAND ® 2019
 */

interface TravelTreeOption<T> {
  childrenName?: string;
  parent?: T;
  childrenNullable: boolean;
}
/**
 * 遍历并转化树
 */
export function mapTree<T = any, P = T>(
  tree: T[],
  iter: (item: T, parent?: P) => P,
  options: TravelTreeOption<P>
): P[] {
  return (tree || []).map(item => {
    const { childrenName = 'children', parent, childrenNullable = false } = options || {};
    const tran = iter(item, parent);
    if (tran[childrenName] || !childrenNullable) {
      tran[childrenName] = mapTree(tran[childrenName] || [], iter, {
        ...options,
        parent: tran,
      });
    }
    return tran;
  });
}
