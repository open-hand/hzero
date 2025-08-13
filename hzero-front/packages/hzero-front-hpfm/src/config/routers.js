module.exports = [
  {
    path: '/hpfm/api-customize',
    components: [
      {
        path: '/hpfm/api-customize/list',
        component: () => import('../routes/ApiIndividuation'),
      },
      {
        path: '/hpfm/api-customize/:type/:id',
        component: () => import('../routes/ApiIndividuation/Detail'),
      },
    ],
  },
  {
    path: '/hpfm/card-manage',
    component: () => import('../routes/CardManage'),
    models: [() => import('../models/cardManage')],
  },
  {
    path: '/private/hpfm/card-manage',
    authorized: true,
    component: () => import('../routes/CardManage'),
    models: [() => import('../models/cardManage')],
  },
  {
    path: '/hpfm/excel-async-export',
    component: () => import('../routes/ExcelAsyncExport'),
    models: [() => import('../models/excelAsyncExport')],
  },
  {
    path: '/hpfm/cost-center',
    component: () => import('../routes/CostCenter'),
    models: [() => import('../models/costCenter')],
  },
  {
    path: '/private/hpfm/excel-async-export',
    key: '/private/hpfm/excel-async-export',
    authorized: true,
    component: () => import('../routes/ExcelAsyncExport'),
    models: [() => import('../models/excelAsyncExport')],
  },
  {
    path: '/hpfm/code-rule',
    models: [() => import('../models/codeRule')],
    components: [
      {
        path: '/hpfm/code-rule/list',
        component: () => import('../routes/CodeRule/CodeRuleList'),
        models: [() => import('../models/codeRule')],
      },
      {
        path: '/hpfm/code-rule/dist/:id',
        component: () => import('../routes/CodeRule/CodeRuleDist'),
        models: [() => import('../models/codeRule')],
      },
    ],
  },
  {
    path: '/private/hpfm/code-rule',
    authorized: true,
    models: [() => import('../models/codeRule')],
    components: [
      {
        path: '/private/hpfm/code-rule/list',
        authorized: true,
        component: () => import('../routes/CodeRule/CodeRuleList'),
        models: [() => import('../models/codeRule')],
      },
      {
        path: '/private/hpfm/code-rule/dist/:id',
        authorized: true,
        component: () => import('../routes/CodeRule/CodeRuleDist'),
        models: [() => import('../models/codeRule')],
      },
    ],
  },
  {
    path: '/hpfm/code-rule-org',
    models: [() => import('../models/codeRuleOrg')],
    components: [
      {
        path: '/hpfm/code-rule-org/list',
        component: () => import('../routes/CodeRuleOrg/CodeRuleList'),
        models: [() => import('../models/codeRuleOrg')],
      },
      {
        path: '/hpfm/code-rule-org/dist/:id',
        component: () => import('../routes/CodeRuleOrg/CodeRuleDist'),
        models: [() => import('../models/codeRuleOrg')],
      },
    ],
  },
  {
    path: '/private/hpfm/code-rule-org',
    authorized: true,
    models: [() => import('../models/codeRuleOrg')],
    components: [
      {
        path: '/private/hpfm/code-rule-org/list',
        authorized: true,
        component: () => import('../routes/CodeRuleOrg/CodeRuleList'),
        models: [() => import('../models/codeRuleOrg')],
      },
      {
        path: '/private/hpfm/code-rule-org/dist/:id',
        authorized: true,
        component: () => import('../routes/CodeRuleOrg/CodeRuleDist'),
        models: [() => import('../models/codeRuleOrg')],
      },
    ],
  },
  {
    path: '/pub/hpfm/code-rule-org/dist/:id',
    component: () => import('../routes/CodeRuleOrg/CodeRuleDist'),
    authorized: true,
    key: '/pub/hpfm/code-rule-org/dist/:id',
    models: [() => import('../models/codeRuleOrg')],
  },
  {
    path: '/hpfm/config',
    component: () => import('../routes/Config'),
    models: [() => import('../models/config')],
  },
  {
    path: '/private/hpfm/config',
    authorized: true,
    component: () => import('../routes/Config'),
    models: [() => import('../models/config')],
  },
  {
    // 数据源驱动
    path: '/hpfm/data-source-driver',
    component: () => import('../routes/DatasourceDriver'),
    models: [() => import('../models/datasourceDriver')],
  },
  // 系统管理--模板维护
  {
    path: '/hpfm/templates',
    component: () => import('../routes/Templates'),
    models: [() => import('../models/hpfmTemplate')],
  },
  // 系统管理--模板管理
  // {
  //   path: '/hpfm/template-configs',
  //   component: () => import('../routes/TemplateConfigs'),
  //   models: [() => import('../models/templateConfigs')],
  //   key: '/hpfm/template-configs',
  //   authorized: true,
  //   title: '系统管理--模板管理',
  // },
  // 表单动态配置
  {
    path: '/hpfm/dynamic-form',
    models: [],
    components: [
      {
        path: '/hpfm/dynamic-form/list',
        models: [() => import('../models/dynamicForm/header')],
        component: () => import('../routes/DynamicForm/Header/index'),
      },
      {
        path: '/hpfm/dynamic-form/detail/:formHeaderId',
        models: [
          () => import('../models/dynamicForm/header'),
          () => import('../models/dynamicForm/line'),
        ],
        component: () => import('../routes/DynamicForm/Line/index'),
      },
    ],
  },
  // 系统管理-服务器集群管理
  {
    path: '/hpfm/server-cluster',
    component: () => import('../routes/ServerCluster'),
    models: [() => import('../models/serverCluster')],
  },
  {
    path: '/private/hpfm/server-cluster',
    key: '/private/hpfm/server-cluster',
    authorized: true,
    component: () => import('../routes/ServerCluster'),
    models: [() => import('../models/serverCluster')],
  },
  {
    path: '/hpfm/server-define',
    component: () => import('../routes/ServerDefine'),
    models: [() => import('../models/serverDefine')],
  },
  {
    path: '/private/hpfm/server-define',
    key: '/private/hpfm/server-define',
    authorized: true,
    component: () => import('../routes/ServerDefine'),
    models: [() => import('../models/serverDefine')],
  },
  {
    path: '/hpfm/dashboard-clause',
    models: [() => import('../models/dashboardClause')],
    components: [
      {
        path: '/hpfm/dashboard-clause/list',
        component: () => import('../routes/DashboardClause'),
        models: [() => import('../models/dashboardClause')],
      },
      {
        path: '/hpfm/dashboard-clause/create',
        component: () => import('../routes/DashboardClause/Detail'),
        models: [() => import('../models/dashboardClause')],
      },
      {
        path: '/hpfm/dashboard-clause/detail/:clauseId',
        component: () => import('../routes/DashboardClause/Detail'),
        models: [() => import('../models/dashboardClause')],
      },
    ],
  },
  {
    path: '/private/hpfm/dashboard-clause',
    authorized: true,
    models: [() => import('../models/dashboardClause')],
    components: [
      {
        path: '/private/hpfm/dashboard-clause/list',
        authorized: true,
        component: () => import('../routes/DashboardClause'),
        models: [() => import('../models/dashboardClause')],
      },
      {
        path: '/private/hpfm/dashboard-clause/create',
        authorized: true,
        component: () => import('../routes/DashboardClause/Detail'),
        models: [() => import('../models/dashboardClause')],
      },
      {
        path: '/private/hpfm/dashboard-clause/detail/:clauseId',
        authorized: true,
        component: () => import('../routes/DashboardClause/Detail'),
        models: [() => import('../models/dashboardClause')],
      },
    ],
  },
  {
    path: '/hpfm/data-source',
    models: [() => import('../models/dataSource')],
    components: [
      {
        path: '/hpfm/data-source/list',
        component: () => import('../routes/DataSource'),
        models: [() => import('../models/dataSource')],
      },
      {
        path: '/hpfm/data-source/detail/:datasourceId',
        component: () => import('../routes/DataSource/Detail'),
        models: [() => import('../models/dataSource')],
      },
    ],
  },
  {
    path: '/hpfm/data-hierarchies',
    component: () => import('../routes/DataHierarchies'),
    models: [() => import('../models/dataHierarchies')],
  },
  {
    path: '/private/hpfm/data-hierarchies',
    authorized: true,
    component: () => import('../routes/DataHierarchies'),
    models: [() => import('../models/dataHierarchies')],
  },
  {
    path: '/hpfm/event',
    models: [],
    components: [
      {
        path: '/hpfm/event/list',
        component: () => import('../routes/Event/EventList'),
        models: [() => import('../models/event')],
      },
      {
        path: '/hpfm/event/detail/:id',
        component: () => import('../routes/Event/EventDetail'),
        models: [() => import('../models/event')],
      },
      {
        path: '/hpfm/event/graph/:id',
        component: () => import('../routes/Event/Flow/FlowPage'),
        models: [() => import('../models/event')],
      },
      // {
      //   path: '/hpfm/event/message/:id',
      //   component: () => import('../routes/Event/EventMessage'),
      //   models: [() => import('../models/event')],
      // },
    ],
  },
  {
    path: '/hpfm/financial-code',
    models: [],
    components: [
      {
        path: '/hpfm/financial-code/list',
        component: () => import('../routes/FinancialCode'),
        models: [() => import('../models/financialCode')],
      },
    ],
  },
  {
    path: '/hpfm/hr/org',
    models: [() => import('../models/organization')],
    components: [
      {
        path: '/hpfm/hr/org/company',
        component: () => import('../routes/Organization'),
        models: [() => import('../models/organization')],
      },
      {
        path: '/hpfm/hr/org/department/:companyId',
        component: () => import('../routes/Department'),
        models: [() => import('../models/department')],
      },
      {
        path: '/hpfm/hr/org/post/:unitId',
        component: () => import('../routes/Post'),
        models: [() => import('../models/post')],
      },
      {
        path: '/hpfm/hr/org/staff/:positionId',
        component: () => import('../routes/Staff'),
        models: [() => import('../models/staff')],
      },
    ],
  },
  {
    path: '/hpfm/org',
    models: [],
    components: [
      {
        path: '/hpfm/org/list',
        component: () => import('../routes/NewOrganization'),
        models: [],
      },
    ],
  },
  {
    path: '/hpfm/hr/staff',
    models: [],
    components: [
      {
        path: '/hpfm/hr/staff/list',
        component: () => import('../routes/Employee/List'),
        models: [() => import('../models/employee')],
      },
      {
        path: '/hpfm/hr/staff/detail/:employeeId/:employeeNum',
        component: () => import('../routes/Employee/Detail'),
        models: [() => import('../models/employee')],
      },
      {
        authorized: true,
        path: '/hpfm/hr/staff/data-import/:code',
        component: () => import('../routes/himp/CommentImport'),
        models: [],
      },
    ],
  },
  {
    path: '/hpfm/languages',
    component: () => import('../routes/Languages'),
    models: [() => import('../models/languages')],
  },
  {
    path: '/hpfm/lov-view',
    models: [() => import('../models/lovSetting')],
    components: [
      {
        path: '/hpfm/lov-view/lov-view-list',
        component: () => import('../routes/Lov/LovSetting'),
        models: [() => import('../models/lovSetting')],
      },
      {
        path: '/hpfm/lov-view/detail/:id',
        component: () => import('../routes/Lov/Detail'),
        models: [() => import('../models/lovSetting')],
      },
    ],
  },
  {
    path: '/hpfm/mdm/bank',
    component: () => import('../routes/Bank'),
    models: [() => import('../models/bank')],
  },
  {
    path: '/hpfm/mdm/calendar',
    models: [],
    components: [
      {
        path: '/hpfm/mdm/calendar/list',
        component: () => import('../routes/Calendar/List'),
        models: [() => import('../models/calendar')],
      },
      {
        path: '/hpfm/mdm/calendar/detail/:calendarId',
        component: () => import('../routes/Calendar/Detail'),
        models: [() => import('../models/calendar')],
      },
    ],
  },
  {
    path: '/hpfm/mdm/country',
    models: [],
    components: [
      {
        path: '/hpfm/mdm/country/list',
        component: () => import('../routes/Country'),
        models: [() => import('../models/country')],
      },
      {
        path: '/hpfm/mdm/country/region/:id/:code/:name',
        component: () => import('../routes/Region'),
        models: [() => import('../models/region')],
      },
    ],
  },
  {
    path: '/private/hpfm/mdm/country/region/:id/:code/:name',
    component: () => import('../routes/Region'),
    authorized: true,
    key: '/private/hpfm/mdm/country/region/:id/:code/:name',
    models: [() => import('../models/region')],
  },
  {
    path: '/private/hpfm/mdm/country/list',
    component: () => import('../routes/Country'),
    authorized: true,
    key: '/private/hpfm/mdm/country/list',
    models: [() => import('../models/country')],
  },
  {
    path: '/hpfm/mdm/currency',
    component: () => import('../routes/Currency'),
    models: [() => import('../models/currency')],
  },
  {
    path: '/hpfm/mdm/industry-category',
    component: () => import('../routes/IndustryCategory'),
    models: [() => import('../models/industryCategory')],
  },
  {
    path: '/hpfm/mdm/period',
    component: () => import('../routes/Period'),
    models: [() => import('../models/period')],
  },
  {
    path: '/hpfm/mdm/rate',
    component: () => import('../routes/Rate'),
    models: [() => import('../models/rate')],
  },
  {
    path: '/hpfm/mdm/rate-type',
    component: () => import('../routes/RateType'),
    models: [() => import('../models/rateType')],
  },
  {
    path: '/hpfm/mdm/tax-rate',
    component: () => import('../routes/TaxRate'),
    models: [() => import('../models/taxRate')],
  },
  {
    path: '/hpfm/mdm/uom',
    component: () => import('../routes/Uom'),
    models: [() => import('../models/uom')],
  },
  {
    path: '/hpfm/mdm/uom-type',
    component: () => import('../routes/UomType'),
    models: [() => import('../models/uomType')],
  },
  {
    path: '/hpfm/message',
    component: () => import('../routes/Message'),
    models: [() => import('../models/message')],
  },
  {
    path: '/hpfm/online',
    component: () => import('../routes/Online'),
    models: [() => import('../models/online')],
  },
  {
    path: '/private/hpfm/online',
    key: '/private/hpfm/online',
    authorized: true,
    component: () => import('../routes/Online'),
    models: [() => import('../models/online')],
  },
  {
    path: '/hpfm/org-info',
    components: [
      {
        path: '/hpfm/org-info',
        component: () => import('../routes/OrgInfo'),
        models: [() => import('../models/group')],
      },
      {
        path: '/hpfm/org-info/company',
        component: () => import('../routes/OrgInfo/Company'),
        models: [() => import('../models/company')],
      },
      {
        path: '/hpfm/org-info/group',
        component: () => import('../routes/OrgInfo/Group'),
        models: [() => import('../models/group')],
      },
      {
        path: '/hpfm/org-info/inventory-org',
        component: () => import('../routes/OrgInfo/InventoryOrg'),
        models: [() => import('../models/inventoryOrg')],
      },
      {
        path: '/hpfm/org-info/library-position',
        component: () => import('../routes/OrgInfo/LibraryPosition'),
        models: [() => import('../models/libraryPosition')],
      },
      {
        path: '/hpfm/org-info/operation-unit',
        component: () => import('../routes/OrgInfo/OperationUnit'),
        models: [() => import('../models/operationUnit')],
      },
      {
        path: '/hpfm/org-info/purchase-agent',
        component: () => import('../routes/OrgInfo/PurchaseAgent'),
        models: [() => import('../models/purchaseAgent')],
      },
      {
        path: '/hpfm/org-info/purchase-org',
        component: () => import('../routes/OrgInfo/PurchaseOrg'),
        models: [() => import('../models/purchaseOrg')],
      },
      {
        path: '/hpfm/org-info/store-room',
        component: () => import('../routes/OrgInfo/StoreRoom'),
        models: [() => import('../models/storeRoom')],
      },
    ],
  },
  {
    path: '/hpfm/permission',
    component: () => import('../routes/Permission'),
    models: [() => import('../models/permission')],
  },
  {
    path: '/private/hpfm/permission',
    authorized: true,
    component: () => import('../routes/Permission'),
    models: [() => import('../models/permission')],
  },
  {
    path: '/hpfm/platform-log',
    component: () => import('../routes/PlatformManager'),
    models: [() => import('../models/platformManager')],
  },
  {
    path: '/hpfm/profile',
    component: () => import('../routes/Profile/Site'),
    models: [() => import('../models/profile')],
  },
  {
    path: '/private/hpfm/profile',
    authorized: true,
    component: () => import('../routes/Profile/Site'),
    models: [() => import('../models/profile')],
  },
  {
    path: '/hpfm/profile-org',
    component: () => import('../routes/Profile/Org'),
    models: [() => import('../models/profileOrg')],
  },
  {
    path: '/private/hpfm/profile-org',
    authorized: true,
    component: () => import('../routes/Profile/Org'),
    models: [() => import('../models/profileOrg')],
  },
  {
    path: '/hpfm/prompt',
    component: () => import('../routes/Prompt'),
    models: [() => import('../models/prompt')],
  },
  {
    path: '/hpfm/prompt/import-data/:code',
    component: () => import('../routes/himp/CommentImport'),
    models: [],
  },

  {
    path: '/hpfm/rule-engine',
    models: [() => import('../models/ruleEngine')],
    components: [
      {
        path: '/hpfm/rule-engine/list',
        component: () => import('../routes/RuleEngine/List'),
        models: [() => import('../models/ruleEngine')],
      },
      {
        path: '/hpfm/rule-engine/create',
        component: () => import('../routes/RuleEngine/Detail'),
        models: [() => import('../models/ruleEngine')],
      },
      {
        path: '/hpfm/rule-engine/detail/:id',
        component: () => import('../routes/RuleEngine/Detail'),
        models: [() => import('../models/ruleEngine')],
      },
    ],
  },
  {
    path: '/hpfm/sql-execute',
    component: () => import('../routes/SqlExecute'),
    models: [() => import('../models/sqlExecute')],
  },
  {
    path: '/hpfm/static-text',
    models: [() => import('../models/staticText')],
    components: [
      {
        path: '/hpfm/static-text/list',
        component: () => import('../routes/StaticText/Site'),
        models: [() => import('../models/staticText')],
      },
      {
        path: '/hpfm/static-text/detail/:action',
        component: () => import('../routes/StaticText/Site/Detail'),
        models: [() => import('../models/staticText')],
      },
    ],
  },
  {
    path: '/hpfm/static-text-org',
    models: [() => import('../models/staticTextOrg')],
    components: [
      {
        path: '/hpfm/static-text-org/list',
        component: () => import('../routes/StaticText/Org'),
        models: [() => import('../models/staticTextOrg')],
      },
      {
        path: '/hpfm/static-text-org/detail/:action',
        component: () => import('../routes/StaticText/Org/Detail'),
        models: [() => import('../models/staticTextOrg')],
      },
    ],
  },
  {
    path: '/hpfm/tenant-log',
    component: () => import('../routes/TenantManager'),
    models: [() => import('../models/tenantManager')],
  },
  {
    path: '/private/hpfm/tenant-log',
    key: '/private/hpfm/tenant-log',
    authorized: true,
    component: () => import('../routes/TenantManager'),
    models: [() => import('../models/tenantManager')],
  },
  {
    path: '/hpfm/ui',
    models: [],
    components: [
      {
        path: '/hpfm/ui/page',
        models: [],
        components: [
          {
            path: '/hpfm/ui/page/list',
            component: () => import('../routes/UI/Site/PageList'),
            models: [() => import('../models/uiPage')],
          },
          {
            path: '/hpfm/ui/page/detail/:pageCode',
            component: () => import('../routes/UI/Site/PageDetail'),
            models: [() => import('../models/uiPage')],
          },
        ],
      },
      {
        path: '/hpfm/ui/page-org',
        models: [],
        components: [
          {
            path: '/hpfm/ui/page-org/list',
            component: () => import('../routes/UI/Org/PageList'),
            models: [() => import('../models/uiPageOrg')],
          },
          {
            path: '/hpfm/ui/page-org/detail/:pageCode',
            component: () => import('../routes/UI/Org/PageDetail'),
            models: [() => import('../models/uiPageOrg')],
          },
        ],
      },
      {
        title: 'hzero.common.title.uiPagePreview',
        authorized: true,
        icon: 'search',
        path: '/hpfm/ui/page/common/:pageCode',
        component: () => import('../routes/UI/Common'),
        models: [],
      },
      {
        title: 'hzero.common.title.uiPagePreview',
        authorized: true,
        icon: 'search',
        path: '/hpfm/ui/page/preview/:pageCode',
        component: () => import('../routes/UI/Site/PagePreview'),
        models: [],
      },
    ],
  },
  {
    path: '/hpfm/value-list',
    models: [() => import('../models/valueList')],
    components: [
      {
        path: '/hpfm/value-list/list',
        component: () => import('../routes/ValueList'),
        models: [() => import('../models/valueList')],
      },
      {
        path: '/hpfm/value-list/detail/:lovId',
        component: () => import('../routes/ValueList/ValueDetail'),
        models: [() => import('../models/valueList')],
      },
      {
        path: '/hpfm/value-list/import-data/:code',
        component: () => import('../routes/himp/CommentImport'),
        models: [],
      },
    ],
  },
  {
    path: '/hpfm/customize',
    models: [() => import('../models/customize')],
    components: [
      {
        path: '/hpfm/customize/list',
        models: [() => import('../models/customize')],
        component: () => import('../routes/Customize'),
      },
      {
        path: '/hpfm/customize/detail/:type/:id',
        models: [() => import('../models/customize')],
        component: () => import('../routes/Customize'),
      },
    ],
  },
  {
    path: '/hpfm/data-group',
    models: [],
    components: [
      {
        path: '/hpfm/data-group/list',
        component: () => import('../routes/DataGroup/List'),
        models: [() => import('../models/dataGroup')],
      },
      {
        path: '/hpfm/data-group/detail/:id',
        component: () => import('../routes/DataGroup/Detail'),
        models: [() => import('../models/dataGroup')],
      },
    ],
  },
  {
    path: '/private/hpfm/data-group',
    key: '/private/hpfm/data-group',
    authorized: true,
    components: [
      {
        path: '/private/hpfm/data-group/list',
        key: '/private/hpfm/data-group/list',
        authorized: true,
        component: () => import('../routes/DataGroup/List'),
        models: [() => import('../models/dataGroup')],
      },
      {
        path: '/private/hpfm/data-group/detail/:id',
        key: '/private/hpfm/data-group/detail/:id',
        authorized: true,
        component: () => import('../routes/DataGroup/Detail'),
        models: [() => import('../models/dataGroup')],
      },
    ],
  },
  {
    path: '/hpfm/data-dimension-config',
    models: [],
    components: [
      {
        path: '/hpfm/data-dimension-config/list',
        component: () => import('../routes/DimensionConfig/List'),
        models: [() => import('../models/dimensionConfig')],
      },
      {
        path: '/hpfm/data-dimension-config/detail/:lovId',
        component: () => import('../routes/DimensionConfig/ValueDetail'),
        models: [() => import('../models/dimensionConfig')],
      },
    ],
  },
  {
    path: '/private/hpfm/data-dimension-config',
    key: '/private/hpfm/data-dimension-config',
    authorized: true,
    components: [
      {
        path: '/private/hpfm/data-dimension-config/list',
        key: '/private/hpfm/data-dimension-config/list',
        authorized: true,
        component: () => import('../routes/DimensionConfig/List'),
        models: [() => import('../models/dimensionConfig')],
      },
      {
        path: '/private/hpfm/data-dimension-config/detail/:lovId',
        key: '/private/hpfm/data-dimension-config/detail/:lovId',
        authorized: true,
        component: () => import('../routes/DimensionConfig/ValueDetail'),
        models: [() => import('../models/dimensionConfig')],
      },
    ],
  },
  {
    path: '/hpfm/ca-management',
    component: () => import('../routes/CaManagement'),
    models: [() => import('../models/caManagement')],
  },
  {
    path: '/hpfm/sync-to-outer-system',
    components: [
      {
        path: '/hpfm/sync-to-outer-system/list',
        component: () => import('../routes/SyncOuterSystem'),
      },
      {
        path: '/hpfm/sync-to-outer-system/detail/:syncId',
        component: () => import('../routes/SyncOuterSystem/LogList'),
      },
    ],
  },
  {
    path: '/hpfm/sys-tools',
    component: () => import('../routes/SysTools'),
  },
  // 个性化
  {
    path: '/hpfm/ui-customize',
    models: [
      () => import('../models/configCustomize'),
      () => import('../models/flexModel'),
      () => import('../models/individuationUnit'),
    ],
    components: [
      {
        path: '/hpfm/ui-customize/cust-config',
        models: [() => import('../models/configCustomize')],
        component: () => import('../routes/ConfigCustomize'),
      },
      {
        path: '/hpfm/ui-customize/model',
        components: [
          {
            path: '/hpfm/ui-customize/model/list',
            models: [() => import('../models/flexModel')],
            component: () => import('../routes/FlexModel'),
          },
          {
            path: '/hpfm/ui-customize/model/detail/:modelId',
            models: [() => import('../models/flexModel')],
            component: () => import('../routes/FlexModel/ModelDetail'),
          },
        ],
      },
      {
        path: '/hpfm/ui-customize/cust-unit',
        models: [() => import('../models/individuationUnit')],
        component: () => import('../routes/IndividuationUnit'),
      },
    ],
  },
  {
    path: '/hpfm/ds-routes',
    components: [
      {
        key: '/hpfm/ds-routes',
        path: '/hpfm/ds-routes/list',
        exact: true,
        component: () => import('../routes/DsRoutes'),
      },
      {
        key: '/hpfm/ds-routes',
        path: '/hpfm/ds-routes/detail/:dsRouteId',
        component: () => import('../routes/DsRoutes/Detail'),
      },
    ],
  },
  // 通用模板管理
  {
    path: '/hpfm/general-template',
    components: [
      {
        path: '/hpfm/general-template/list',
        component: () => import('../routes/GeneralTemplate'),
      },
      {
        path: '/hpfm/general-template/detail/:action/:id',
        component: () => import('../routes/GeneralTemplate/Detail'),
      },
    ],
  },
];
