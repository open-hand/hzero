/*
 * @description: 文件描述
 * @Author: suyu.zeng@hand-china.com
 * @Date: 2019-09-23 11:11:37
 * @LastEditors: suyu.zeng@hand-china.com
 * @LastEditTime: 2019-10-14 20:26:12
 */
module.exports = [
  {
    path: '/hexl/calc/sheet', // 路由 webExcel管理
    component: () => import('../routes/Calc'), // 页面组件
    models: [
      // model数据模型
      // "calc",
    ],
  },
  {
    path: '/hexl/calc/runtime', // Excel试算
    component: () => import('../routes/ExcelRuntime'),
    models: [
      // "calc/calc",
    ],
  },
];
