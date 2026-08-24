/*
  文档参考：https://www.npmjs.com/package/@smock/mock
*/
module.exports = {
  name: 'user js',
  desc: 'js apis',
  apis: [
    {
      name: 'hello one',
      desc: 'example 1',
      method: 'GET',
      url: '/test-js-1',
      handle: (req, res) => {
        res.status(200);
        res.send({
          code: 0,
          message: "hello smock."
        });
      }
    },
    {
      name: 'hello two',
      desc: 'example 2',
      method: 'POST',
      url: '/test-js-2',
      handle: (req, res) => {
        return {
          status: 200,
          data: {
            message: 'hello smock js.',
          }
        }
      }
    }
  ],
}