const mockjs = require('mockjs');

module.exports = {
'GET /hpfm/v1/menus': (req, res) => {
        const d = mockjs.mock({
            'list|1-30': [
                {
                    id: '@id',
                    name: '@name',
                    path: '@url',
                    title: '@title',
                },
            ],
        });
        res.json(d);
    },
};