const delay = require('mocker-api/utils/delay');


const mock = {
};

// 是否禁用代理
const noProxy = process.env.NO_PROXY === 'true';

let allMock = {
    // 'GET /api/hpfm/v1/:id/ui-customize': {
    //     "HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.EDIT": {
    //         "unitType": "FORM",
    //         "maxCol": 1,
    //         "readOnly": false,
    //         "fields": [{
    //             "fieldCode": "positionId",
    //             "fieldType": "SELECT",
    //             "lovCode": "LOV_POSITION",
    //             "required": -1,
    //             "editable": -1,
    //             "formRow": 7,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": true,
    //             "isStandardField": true,
    //             "visible": -1,
    //         }, {
    //             "fieldCode": "email",
    //             "required": -1,
    //             "editable": -1,
    //             "formRow": 5,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": true,
    //             "isStandardField": true,
    //             "visible": -1,
    //         }, {
    //             "fieldName": "attribute9",
    //             "fieldCode": "attribute9",
    //             "fieldType": "INPUT",
    //             "required": 0,
    //             "editable": 1,
    //             "formRow": 10,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": false,
    //             "isStandardField": false,
    //             "visible": 1,
    //             defaultValue: 'test',
    //         }, {
    //             "fieldName": "attribute1",
    //             "fieldCode": "attribute1",
    //             "fieldType": "SELECT",
    //             "lovCode": "HIOT.PROPERTY_CATEGORY",
    //             "defaultValue": "PROPERTY",
    //             "defaultValueMeaning": "数据点",
    //             "required": 0,
    //             "editable": 1,
    //             "formRow": 10,
    //             "formCol": 2,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": false,
    //             "isStandardField": false,
    //             "visible": 1,
    //         }, {
    //             "fieldCode": "gender",
    //             "required": -1,
    //             "editable": -1,
    //             "formRow": 3,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": true,
    //             "isStandardField": true,
    //             "visible": -1,
    //         }, {
    //             "fieldCode": "mobile",
    //             "required": -1,
    //             "editable": -1,
    //             "formRow": 4,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": true,
    //             "isStandardField": true,
    //             "visible": -1,
    //             "selfValidators": {
    //                 conditionList: [
    //                     {
    //                         conCode: 1,
    //                         conExpression: '!=',
    //                         sourceFieldCode: 'attribute9',
    //                         sourceUnitCode: 'HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.EDIT',
    //                         targetType: 'fixed',
    //                         targetValue: '12',
    //                     },
    //                 ],
    //                 validatorList: [
    //                     {
    //                         conExpression: '1',
    //                         errMessage: 'ceshi',
    //                     },
    //                 ],
    //             },
    //         }, {
    //             "fieldCode": "status",
    //             "required": -1,
    //             "editable": -1,
    //             "formRow": 11,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": true,
    //             "isStandardField": true,
    //             "visible": -1,
    //         }, {
    //             "fieldCode": "name",
    //             "required": -1,
    //             "editable": -1,
    //             "formRow": 2,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": true,
    //             "isStandardField": true,
    //             "visible": -1,
    //         }, {
    //             "fieldCode": "employeeCode",
    //             "required": -1,
    //             "editable": -1,
    //             "formRow": 1,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": true,
    //             "isStandardField": true,
    //             "visible": -1,
    //         }, {
    //             "fieldCode": "enabledFlag",
    //             "required": -1,
    //             "editable": -1,
    //             "formRow": 12,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": true,
    //             "isStandardField": true,
    //             "visible": -1,
    //         }, {
    //             "fieldCode": "departmentLov",
    //             "fieldType": "LOV",
    //             "lovCode": "HPFM.PLUS.ALL.DEPARTMENT",
    //             "required": -1,
    //             "editable": -1,
    //             "formRow": 6,
    //             "formCol": 1,
    //             "renderOptions": "WIDGET",
    //             "conditionHeaderDTOs": [],
    //             "standardField": true,
    //             "isStandardField": true,
    //             "visible": -1,
    //         }],
    //         "unitAlias": [{
    //             "unitCode": "HPFM.ORG_LIST.DEPARTMENT.EMPLOYEE.EDIT",
    //             "alias": "u1",
    //         }],
    //     },
    // },
};
if (!noProxy) {
    Object.keys(mock).forEach((mockKey) => {
        allMock = Object.assign(allMock, mock[mockKey]);
    });
}
module.exports = noProxy ? allMock : delay(allMock, 1000);
