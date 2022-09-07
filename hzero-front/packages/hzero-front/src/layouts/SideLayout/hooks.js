// import React from 'react';
// import { debounce } from 'lodash';
//
// import { DEBOUNCE_TIME } from 'utils/constants';
//
// /**
//  * 使用 lodash debounce 方法 的 useState
//  * @param initial - 初始值
//  * @param {number} debounceTime - 时间 ms
//  * @param {ReadonlyArray<any>} inputs - 是否更新 debounce 方法
//  * @param debounceOptions - lodash.debounce 的配置
//  * @returns {[unknown, function(...[*]): void]}
//  */
// export const useDebounceState = (
//   initial,
//   debounceTime = DEBOUNCE_TIME,
//   inputs = [],
//   { debounceOptions = {} } = {}
// ) => {
//   const [value, setValue] = React.useState(initial);
//   const [debounceSetValue, setDebounceSetValue] = React.useState(() => setValue);
//   React.useEffect(() => {
//     const debounceCallback = debounce(setValue, debounceTime, debounceOptions);
//     setDebounceSetValue(() => debounceCallback);
//     return () => {
//       debounceCallback.cancel();
//     };
//   }, [...inputs, setValue]);
//   return [value, debounceSetValue];
// };
