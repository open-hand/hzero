const path = require('path');

module.exports = {

  // mock 配置
  mockExcludes: [],
  mockDirs: [`./_smock/**`],
  mockCwd: __dirname,
  // mockDirs: [],

  /* 拦截工具cwd */
  cwd: __dirname,

  /* 代理目标设置（真实的接口地址）*/
  target: "http://hzeronb.saas.hand-china.com",

  /* 资源缓存路径 */
  workDir: path.resolve(__dirname, '.smock'),

  /* 拦截服务运行的端口 */
  workPort: 10011,

  /* 正则匹配到的路径将会被缓存 */
  matchRegexp: /.*/,

  /* 是否缓存静态文件 */
  cacheStatic: true,

  /* 加入pathIgnore的路径，仍然会被缓存，但缓存的时候将不会以query或body作为区分
   * 这样做的原因是有些缓存的时候是根据method参数、query参数和body参数为基础计算出来的哈希值
   * 如果请求数据中含有随机数，那么每次请求都会记录一个新值，造成开启拦截后找不到数据的情况
   * 此时可以将这些路径加入忽略路径中。
  */
  pathIgnore: {
    query: [
      '/oauth/logout'
    ],
    body: [
      '/oauth/login'
    ]
  },
  plugins: []
}
