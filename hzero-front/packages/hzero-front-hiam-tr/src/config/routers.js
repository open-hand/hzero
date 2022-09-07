module.exports = [
  // 角色管理
  {
    path: '/hiam/tr-role',
    components: [
      {
        path: '/hiam/tr-role/list',
        component: () => import('../routes/ThreeRoleManagement'),
        models: [
          () => import('../models/trAuthorityDimension'),
          () => import('../models/trRoleManagement'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityManagement'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityCompany'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityPurorg'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityPuragent'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityValueList'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityLovView'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityDataSource'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityDataGroup'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityCompany'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityDataGroup'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityDataSource'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityLovView'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityPuragent'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityPurorg'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityValueList'),
        ],
      },
      {
        path: '/hiam/tr-role/api/:roleId',
        component: () => import('../routes/ThreeRoleManagement/ApiField'),
        models: [() => import('../models/trRoleManagement')],
      },
      {
        path: '/hiam/tr-role/field/:roleId/:permissionId',
        component: () => import('../routes/ThreeRoleManagement/ApiField/FieldPermission'),
        models: [() => import('../models/trRoleManagement')],
      },
    ],
  },
  {
    path: '/private/hiam/tr-role',
    components: [
      {
        path: '/private/hiam/tr-role/list',
        component: () => import('../routes/ThreeRoleManagement'),
        models: [
          () => import('../models/trAuthorityDimension'),
          () => import('../models/trRoleManagement'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityManagement'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityCompany'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityPurorg'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityPuragent'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityValueList'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityLovView'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityDataSource'),
          () => import('../models/trRoleDataAuthority/trRoleDataAuthorityDataGroup'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityCompany'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityDataGroup'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityDataSource'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityLovView'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityPuragent'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityPurorg'),
          () => import('../models/trSecurityGroupAuthority/trSecGrpAuthorityValueList'),
        ],
      },
      {
        path: '/private/hiam/tr-role/api/:roleId',
        component: () => import('../routes/ThreeRoleManagement/ApiField'),
        models: [() => import('../models/trRoleManagement')],
      },
      {
        path: '/private/hiam/tr-role/field/:roleId/:permissionId',
        component: () => import('../routes/ThreeRoleManagement/ApiField/FieldPermission'),
        models: [() => import('../models/trRoleManagement')],
      },
    ],
  },
  // 用户管理
  {
    path: '/hiam/tr-account/org',
    models: [],
    components: [
      {
        path: '/hiam/tr-account/org/users',
        component: () => import('../routes/ThreeRoleSubAccount/Org'),
        models: [
          () => import('../models/trSubAccountOrg'),
          () => import('../models/userInfo'),
          () => import('../models/trAuthorityDimension'),
          () => import('../models/trAccountSecurityGroupAuthority/trAccSecGrpAuthorityCompany'),
          () => import('../models/trAccountSecurityGroupAuthority/trAccSecGrpAuthorityDataGroup'),
          () => import('../models/trAccountSecurityGroupAuthority/trAccSecGrpAuthorityDataSource'),
          () => import('../models/trAccountSecurityGroupAuthority/trAccSecGrpAuthorityLovView'),
          () => import('../models/trAccountSecurityGroupAuthority/trAccSecGrpAuthorityPuragent'),
          () => import('../models/trAccountSecurityGroupAuthority/trAccSecGrpAuthorityPurorg'),
          () => import('../models/trAccountSecurityGroupAuthority/trAccSecGrpAuthorityValueList'),
        ],
      },
      {
        path: '/hiam/tr-account/org/authority-management',
        component: () => import('../routes/ThreeRoleSubAccount/Org/AuthorityManagement'),
        models: [
          () => import('../models/trAuthorityDimension'),
          () => import('../models/trAuthorityManagement/trAuthorityManagement'),
          () => import('../models/trAuthorityManagement/trAuthorityCompany'),
          () => import('../models/trAuthorityManagement/trAuthorityCustomer'),
          () => import('../models/trAuthorityManagement/trAuthoritySupplier'),
          () => import('../models/trAuthorityManagement/trAuthorityPurorg'),
          () => import('../models/trAuthorityManagement/trAuthorityPuragent'),
          () => import('../models/trAuthorityManagement/trAuthorityPurcat'),
          () => import('../models/trAuthorityManagement/trAuthorityLovView'),
          () => import('../models/trAuthorityManagement/trAuthorityValueList'),
          () => import('../models/trAuthorityManagement/trAuthorityDataSource'),
          () => import('../models/trAuthorityManagement/trAuthorityDataGroup'),
        ],
      },
      {
        path: '/hiam/tr-account/org/api-field',
        components: [
          {
            path: '/hiam/tr-account/org/api/:userId',
            component: () => import('../routes/ThreeRoleSubAccount/Org/ApiField'),
            models: [() => import('../models/trSubAccountOrg')],
          },
          {
            path: '/hiam/tr-account/org/field/:userId/:permissionId',
            component: () => import('../routes/ThreeRoleSubAccount/Org/ApiField/FieldPermission'),
            models: [() => import('../models/trSubAccountOrg')],
          },
        ],
      },
      {
        path: '/hiam/tr-account/org/data-import/:code',
        authorized: true,
        component: () => import('../routes/himp/CommentImport'),
        models: [],
      },
    ],
  },
  {
    path: '/hiam/tr-account/site',
    component: () => import('../routes/ThreeRoleSubAccount/Site'),
    models: [() => import('../models/trSubAccount')],
  },
  // 客户端管理
  {
    path: '/hiam/tr-client',
    component: () => import('../routes/ThreeRoleClient'),
    models: [() => import('../models/trClient')],
  },
  // 三员操作审计
  {
    path: '/hmnt/three-audit-query',
    component: () => import('../routes/ThreeAuditQuery'),
  },
  // 三元数据审计查询
  {
    path: '/hmnt/three-data-audit-log',
    component: () => import('../routes/ThreeDataAuditLog'),
  },
];
