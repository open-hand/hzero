const mockjs = require('mockjs');

module.exports = {
    'GET /hpfm/v1/users/self': (req, res) => {
        const d = mockjs.mock({
            'list|1-200': [
                {
                    name: mockjs.mock('@name'),
                    password: mockjs.mock('@password'),
                },
            ],
        }).list[0];
        res.json(d);
    },
    'GET /hpfm/v1/users': (req, res) => {
        const d = mockjs.mock({
            'list|1-200': [
                {
                    name: mockjs.mock('@name'),
                    password: mockjs.mock('@password'),
                },
            ],
        }).list;
        res.json(d);
    },
};