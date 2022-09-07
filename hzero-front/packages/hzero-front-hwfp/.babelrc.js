module.exports = {
  extends: '../../.babelrc',
  plugins: [
    ['module-resolver', {
      "alias": {
        '@': './src',
      }
    }],
  ]
};
// 更改提交自动部署测试