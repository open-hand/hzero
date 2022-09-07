/**
 * 服务注册-多语言
 * @author baitao.huang@hand-china.com
 * @date 2020-03-12
 * @version: 0.0.1
 * @copyright: Copyright (c) 2020, Hand
 */

import intl from 'utils/intl';

const getLang = (key) => {
  const PREFIX = 'hitf.services';
  const SERVICE_PREFIX = `${PREFIX}.model.services`;

  const LANGS = {
    PREFIX,
    SAVE: intl.get('hzero.common.button.save').d('保存'),
    OPERATOR: intl.get('hzero.common.button.action').d('操作'),
    MODEL_EDIT: intl.get('hzero.common.edit').d('编辑'),
    RETRY: intl.get('hzero.common.retry').d('重试'),
    FILE_PROTOCOL_PASSWORD: intl.get('hzero.common.model.common.password').d('密码'),
    SURE: intl.get('hzero.common.button.sure').d('确定'),
    CODE: intl
      .get('hzero.common.validation.code')
      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
    CODE_UPPER: intl
      .get('hzero.common.validation.codeUpper')
      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),

    FIELD_NAME: intl.get(`${SERVICE_PREFIX}.fieldName`).d('属性名'),
    FIELD_TYPE: intl.get(`${SERVICE_PREFIX}.fieldType`).d('字段类型'),
    FIELD_DESC: intl.get(`${SERVICE_PREFIX}.fieldDesc`).d('字段描述'),
    FIELD_EXPR: intl.get(`${SERVICE_PREFIX}.fieldExpr`).d('表达式'),
    FIELD_LEVEL: intl.get(`${SERVICE_PREFIX}.fieldLevel`).d('隐私级别'),
    SEQ_NUM: intl.get(`${SERVICE_PREFIX}.sqeNum`).d('排序号'),
    PARAM_NAME: intl.get(`${SERVICE_PREFIX}.paramName`).d('参数名称'),
    PARAM_TYPE: intl.get(`${SERVICE_PREFIX}.paramType`).d('参数类型'),
    PARAM_DESC: intl.get(`${SERVICE_PREFIX}.paramDesc`).d('描述'),
    BIND_FIELD_NAME: intl.get(`${SERVICE_PREFIX}.bindAttr`).d('绑定属性'),
    PARAM_LOCATION: intl.get(`${SERVICE_PREFIX}.paramLocation`).d('参数位置'),
    OPERATOR_CODE: intl.get(`${SERVICE_PREFIX}.operatorCode`).d('操作符'),
    REQUIRED_FLAG: intl.get(`${SERVICE_PREFIX}.requiredFlag`).d('是否必填'),
    DEFAULT_VALUE: intl.get(`${SERVICE_PREFIX}.defaultValue`).d('默认值'),
    REQUEST_PROTOCOL: intl.get(`${SERVICE_PREFIX}.requestProtocol`).d('请求协议'),
    RESPONSE_TYPE: intl.get(`${SERVICE_PREFIX}.responseType`).d('响应类型'),
    EXPRESSION_TYPE: intl.get(`${SERVICE_PREFIX}.expressionType`).d('表达式类型'),
    EXPRESSION_CONTENT: intl.get(`${SERVICE_PREFIX}.expressionContent`).d('表达式内容'),
    DETAIL_TITLE: intl.get(`${PREFIX}.view.message.title.detail`).d('服务注册详情'),
    DETAIL_TITLE_TIP: intl
      .get(`${PREFIX}.view.message.title.detail.tip`)
      .d(
        '服务注册详情。一个网站可以看做一个服务，网站下的各个接口即对应接口平台的接口。针对服务，接口平台提供统一的认证配置及其他配套配置。'
      ),

    INTERFACE_CODE: intl.get(`${SERVICE_PREFIX}.interfaceCode`).d('接口编码'),
    INTERFACE_CODE_TIP: intl
      .get(`${SERVICE_PREFIX}.interfaceCode.tip`)
      .d(
        '接口编码。服务维度下确保唯一，一经填写就不可修改。特别地，前置机适配器自动注册时为适配器编码，等。'
      ),
    INTERFACE_NAME: intl.get(`${SERVICE_PREFIX}.interfaceName`).d('接口名称'),
    INTERFACE_NAME_TIP: intl
      .get(`${SERVICE_PREFIX}.interfaceName.tip`)
      .d('接口名称。特别地，前置机适配器自动注册时为适配器编码，等'),
    INTERFACE_URL: intl.get(`${SERVICE_PREFIX}.interfaceUrl`).d('接口地址'),
    INTERFACE_URL_TIP: intl
      .get(`${SERVICE_PREFIX}.interfaceUrl.tip`)
      .d('接口地址。对方接口的路由地址'),
    SOAP_VERSION: intl.get(`${SERVICE_PREFIX}.soapVersion`).d('SOAP版本'),
    REQUEST_METHOD: intl.get(`${SERVICE_PREFIX}.requestMethod`).d('请求方法'),
    REQUEST_METHOD_TIP: intl
      .get(`${SERVICE_PREFIX}.requestMethod.tip`)
      .d('对方接口的请求方法。支持GET/POST/PUT/DELETE等'),
    REQUEST_HEADER: intl.get(`${SERVICE_PREFIX}.requestHeader`).d('接口ContentType'),
    REQUEST_HEADER_TIP: intl
      .get(`${SERVICE_PREFIX}.requestHeader.tip`)
      .d(
        '接口ContentType。对方接口的内容类型。特别地，如果为文件上传的接口，请务必设置为multipart/form-data。'
      ),
    PUBLISH_TYPE: intl.get(`${SERVICE_PREFIX}.publishType`).d('发布类型'),
    PUBLISH_TYPE_TIP: intl
      .get(`${SERVICE_PREFIX}.publishType.tip`)
      .d(
        '发布类型。此配置为接口平台的配置项，RESTful与SOAP相互转换的配置即为此项，配置该类型后，将把对方接口的相应内容转换成当前配置项内容。'
      ),
    SOAP_ACTION: intl.get(`${SERVICE_PREFIX}.soapAction`).d('soapAction'),
    SOAP_ACTION_TIP: intl
      .get(`${SERVICE_PREFIX}.soapAction.tip`)
      .d('soapAction。此参数一般为wsdl描述文件中的soapAction属性的值。'),
    BODY_NAMESPACE_FLAG: intl.get(`${SERVICE_PREFIX}.bodyNamespaceFlag`).d('body命名空间'),
    BODY_NAMESPACE_FLAG_TIP: intl
      .get(`${SERVICE_PREFIX}.bodyNamespaceFlag.tip`)
      .d(
        'body命名空间。此参数用来开关是否需要配置请求报文中soap-body(不同协议body标签有差异，根据实际情况判定)中的命名空间。'
      ),
    SERVICE_CODE: intl.get(`${SERVICE_PREFIX}.code`).d('服务代码'),
    SERVICE_CODE_TIP: intl
      .get(`${SERVICE_PREFIX}.code.tip`)
      .d('服务代码。租户维度下确保唯一，一经填写就不可修改。'),
    NAMESPACE: intl.get(`${SERVICE_PREFIX}.namespace`).d('服务命名空间'),
    DOMAIN: intl.get(`${SERVICE_PREFIX}.domain`).d('服务领域'),
    DOMAIN_TIP: intl
      .get(`${SERVICE_PREFIX}.domain.tip`)
      .d('服务领域。用于服务领域划分，用于服务资产化，属于领域资产归类。'),
    INVOKE_VERIFY_SIGN_FLAG: intl.get(`${SERVICE_PREFIX}.invokeVerifySignFlag`).d('校验签名'),
    INVOKE_VERIFY_SIGN_FLAG_TIP: intl
      .get(`${SERVICE_PREFIX}.invokeVerifySignFlag.tip`)
      .d('校验签名。此功能主要作为接口验签使用。需结合验签功能一起使用，为特定场景使用功能。'),
    SOAP_DATA_NODE: intl.get(`${SERVICE_PREFIX}.soapDataNode`).d('Soap响应体标签'),
    SOAP_DATA_NODE_TIP: intl
      .get(`${SERVICE_PREFIX}.soapDataNode.tip`)
      .d(
        'Soap响应体标签。此参数为Soap报文响应体的标签，默认值为soap-env:Body。此参数主要用来指定提取Soap响应体的始点，亦即作为提取响应的表达式的主要内容。如果您的报文响应体标签为<soap:Body>[省略千万字...]</soap:Body>"，请将当前参数设置为soap:Body'
      ),
    REQUEST_CONTENT_TYPE: intl.get(`${SERVICE_PREFIX}.requestContentType`).d('请求内容类型'),
    REQUEST_CONTENT_TYPE_TIP: intl
      .get(`${SERVICE_PREFIX}.requestContentType.tip`)
      .d(
        '请求内容类型(Content Type)。此参数用来指定"对方接口"的请求内容类型。请注意与媒体类型MediaType(如application/json)区分，正确的内容类型一般应该在媒体类型基础上拼接字符集，如"application/json;charset=utf-8"、"multipart/form-data; boundary=xxx"(form-data此处仅用来示例，非参数使用场景)'
      ),
    RESPONSE_CONTENT_TYPE: intl.get(`${SERVICE_PREFIX}.responseContentType`).d('响应内容类型'),
    RESPONSE_CONTENT_TYPE_TIP: intl
      .get(`${SERVICE_PREFIX}.responseContentType.tip`)
      .d(
        '响应内容类型(Content Type)。此参数用来指定接口平台收到对方接口的响应时以什么样的内容类型(特别如字符集)处理最终通过接口平台返回至接口平台调用方。请注意与媒体类型MediaType(如application/json)区分，正确的内容类型一般应该在媒体类型基础上拼接字符集，如"application/json;charset=utf-8"、"multipart/form-data; boundary=xxx"(form-data此处仅用来示例，非参数使用场景)'
      ),
    SOAP_WSS_PASSWORD_TYPE: intl.get(`${SERVICE_PREFIX}.soapWssPasswordType`).d('SOAP加密类型'),
    SOAP_WSS_PASSWORD_TYPE_TIP: intl
      .get(`${SERVICE_PREFIX}.soapWssPasswordType.tip`)
      .d(
        'SOAP加密类型。此加密类型用于兼容1.5(不含1.5)之前认证。1.5及以后，此功能过时，使用服务认证配置的"SOAP认证"替代。'
      ),
    SOAP_ELEMENT_PREFIX: intl.get(`${SERVICE_PREFIX}.soapElementPrefix`).d('SOAP参数前缀'),
    SOAP_ELEMENT_PREFIX_TIP: intl
      .get(`${SERVICE_PREFIX}.soapElementPrefix.tip`)
      .d(
        'SOAP参数前缀。此参数用来给SOAP接口[请求报文]做参数调整。一般用来给"接口配置"的"接口地址"(后文提及的接口地址也为此处说明的接口地址)以及请求报文的命名空间追加前缀。如集成时SAP系统的该参数项一般配置为urn。示例： `<soap:Body><XX接口地址 xmlns="urn:sap-com:document:sap:rfc:functions"><ET_ORGLIST><item></item></ET_ORGLIST></XX接口地址></soap:Body>` =>  `<soap:Body><urn:XX接口地址 xmlns:urn="urn:sap-com:document:sap:rfc:functions"><ET_ORGLIST><item></item></ET_ORGLIST></urn:XX接口地址></soap:Body>`'
      ),
    SOAP_NAMESPACE: intl.get(`${SERVICE_PREFIX}.soapNamespace`).d('SOAP命名空间'),
    SOAP_NAMESPACE_TIP: intl
      .get(`${SERVICE_PREFIX}.soapNamespace.tip`)
      .d('SOAP命名空间。一般为wsdl描述文件中"targetNamespace"或"xmlns:tns"等属性的值'),
    ADDRESS: intl.get(`${SERVICE_PREFIX}.address`).d('服务地址'),
    ADDRESS_TIP: intl
      .get(`${SERVICE_PREFIX}.address.tip`)
      .d(
        '服务地址。内部接口地址为HZero内服务，外部接口为实际对方接口地址。支持http/https，选择https时需选择平台证书，亦即需提前至平台证书处维护证书。'
      ),
    PUBLIC_FLAG: intl.get(`${SERVICE_PREFIX}.publicFlag`).d('公开服务'),
    PUBLIC_FLAG_TIP: intl
      .get(`${SERVICE_PREFIX}.publicFlag.tip`)
      .d(
        '公开服务。公开服务仅可在注册时维护，一经填写就不可修改。设置为公开服务后结合调用路由(/hitf/public，普通路由为/hitf/v1)可实现无网关授权、接口平台授权访问。特别地，此操作仅为特别需求设置，一旦设置为公开服务请知悉将带来的安全隐患。注意，设置为公开的接口不可通过常规调用方式(/hitf/v*)调用'
      ),
    SERVICE_CATEGORY: intl.get(`${SERVICE_PREFIX}.category`).d('服务类别'),
    SERVICE_CATEGORY_TIP: intl
      .get(`${SERVICE_PREFIX}.category.tip`)
      .d(
        '服务类别。此服务类别特指服务的来源，现支持透传(包装、代理)及基础数据开发。目前支持内部接口(相对于HZERO，特指HZERO内部接口)、外部接口(相对于HZERO，特指非HZERO系统的接口，如公网接口等)、数据源(直接将SQL或者表发布成接口)等'
      ),
    SERVICE_TYPE: intl.get(`${SERVICE_PREFIX}.type`).d('服务类型'),
    SERVICE_TYPE_TIP: intl
      .get(`${SERVICE_PREFIX}.type.tip`)
      .d('服务类型。此处的类型特指集成协议，目前主要支持RESTful、Soap等协议。'),
    NAMESPACE_TIP: intl
      .get(`${SERVICE_PREFIX}.namespace.tip`)
      .d(
        '服务命名空间。默认为租户编码，选择租户或由当前上下文带出，不可更改。前置机自动注册服务时为前置机指定内容。'
      ),
    ASYNC_FLAG: intl.get(`${SERVICE_PREFIX}.asyncFlag`).d('异步调用'),
    ASYNC_FLAG_TIP: intl
      .get(`${SERVICE_PREFIX}.asyncFlag.tip`)
      .d(
        '异步调用。接口平台配置项。开启后透传将使用异步线程方式处理请求。如果您需要同步获取对方接口内容，切勿设置此项。'
      ),
    MAPPING_CLASS: intl.get(`${SERVICE_PREFIX}.mappingClass`).d('映射类'),
    MAPPING_CLASS_TIP: intl
      .get(`${SERVICE_PREFIX}.mappingClass.tip`)
      .d(
        '映射类。此功能为回调处理功能，一般用来处理请求参数或者响应内容。请求内容的处理将发生在透传处理最开始处，响应内容的处理将发生在sdk即将返回结果前(数据映射、数据转换发生在此功能之前)。'
      ),
    STATUS: intl.get(`${SERVICE_PREFIX}.status`).d('状态'),
    HTTP_CONFIG: intl.get(`${SERVICE_PREFIX}.httpConfig`).d('http配置'),
    HTTP_CONFIG_TIP: intl
      .get(`${SERVICE_PREFIX}.httpConfig.tip`)
      .d('http配置。可设置连接超时以及读超时时间，优先级高于服务级别的HTTP参数配置'),
    REQUEST_TRANSFORM: intl.get(`${SERVICE_PREFIX}.requestTransform`).d('请求映射'),
    REQUEST_TRANSFORM_TIP: intl
      .get(`${SERVICE_PREFIX}.requestTransform.tip`)
      .d(
        '请求字段(key)映射。针对接口平台接收的请求参数做字段映射，请提供源结构及目标结构(您想要的数据结构)。此功能主要通过图形化拖拉拽方式处理字段(key)转换，亦可做轻量及的数据(value)加工'
      ),
    RESPONSE_TRANSFORM: intl.get(`${SERVICE_PREFIX}.responseTransform`).d('响应映射'),
    RESPONSE_TRANSFORM_TIP: intl
      .get(`${SERVICE_PREFIX}.responseTransform.tip`)
      .d(
        '响应字段(key)映射。针对透传调用对方接收的响应内容(接收到对方响应之时，还未返回至调用方)做字段映射，请提供源结构(对方接口响应的数据结构)及目标结构(您想要的数据结构)。此功能主要通过图形化拖拉拽方式处理字段(key)转换，亦可做轻量及的数据(value)加工'
      ),
    REQUEST_CAST: intl.get(`${SERVICE_PREFIX}.requestCast`).d('请求转化'),
    REQUEST_CAST_TIP: intl
      .get(`${SERVICE_PREFIX}.requestCast.tip`)
      .d(
        '请求数据转(value)换。针对接口平台接收的请求参数做数据(value)映射。特别注意，当前操作发生在请求字段(key)映射之"后"。提供字段值加工功能，支持数组(执行类似lambda的map操作)及对象处理，若字段存在即根据规则加工，不存在则根据规则创造该字段。目前支持SQL、公式、值映射、值集等'
      ),
    RESPONSE_CAST: intl.get(`${SERVICE_PREFIX}.responseCast`).d('响应转化'),
    RESPONSE_CAST_TIP: intl
      .get(`${SERVICE_PREFIX}.responseCast.tip`)
      .d(
        '响应数据(value)转换。针对透传调用对方接收的响应内容(接收到对方响应之时，还未返回至调用方)做数据(value)映射。特别注意，当前操作发生在响应字段(key)映射之"后"。提供字段值加工功能，支持数组(执行类似lambda的map操作)及对象处理，若字段存在即根据规则加工，不存在则根据规则创造该字段。目前支持SQL、公式、值映射、值集等'
      ),
    PUBLISH_URL: intl.get(`${SERVICE_PREFIX}.publishUrl`).d('发布地址'),

    DATASOURCE_LOV: intl.get(`${SERVICE_PREFIX}.dataSourceLov`).d('数据源名称'),
    DS_TYPE: intl.get(`${SERVICE_PREFIX}.dsType`).d('数据源类型'),
    EXPR_TYPE: intl.get(`${SERVICE_PREFIX}.exprType`).d('表达式类型'),
    REMARK: intl.get(`${SERVICE_PREFIX}.useRemark`).d('用途说明'),

    CREATE_MODEL: intl.get(`${PREFIX}.view.title.create`).d('新建服务模型'),
    ATTR_LIST: intl.get(`${PREFIX}.view.title.attrList`).d('属性列表'),
    PARAM_LIST: intl.get(`${PREFIX}.view.title.paramList`).d('参数列表'),
    VIEW: intl.get(`${SERVICE_PREFIX}.view`).d('表或视图'),
    BUTTON_ADD: intl.get(`${PREFIX}.view.button.add`).d('新增'),
    BUTTON_BATCH_DELETE: intl.get(`${PREFIX}.view.button.batchDelete`).d('批量删除'),
    BUTTON_RELEASE: intl.get(`${PREFIX}.view.button.release`).d('发布'),
    BUTTON_OFFLINE: intl.get(`${PREFIX}.view.button.offline`).d('下线'),
    BUTTON_CREATE: intl.get(`${PREFIX}.view.button.create`).d('注册'),
    BASIC_ATTR: intl.get(`${PREFIX}.view.title.basicAttribute`).d('基本属性'),
    VIEW_MAPPING_CLASS: intl.get(`${PREFIX}.view.title.viewMappingClass`).d('查看映射类详情'),
    VIEW_HTTP_CONFIG: intl.get(`${PREFIX}.view.title.viewHttpConfig`).d('查看http配置'),
    MAIN_CONFIG: intl.get(`${PREFIX}.view.title.mainConfig`).d('详细配置'),
    SQL: intl.get(`${SERVICE_PREFIX}.sql`).d('SQL表达式'),
    PARAM_CANCEL_CHECK: intl
      .get(`${PREFIX}.view.title.paramCancelCheck`)
      .d('取消勾选将会删除关联的参数行'),
    SAVE_VALIDATE: intl.get(`${PREFIX}.view.validate.save`).d('请完善必输信息'),
    ANALYSIS: intl
      .get(`${PREFIX}.view.title.analysis`)
      .d('解析SQL将会删除属性列表和参数列表数据，是否解析'),
    SQL_ERROR: intl.get(`${PREFIX}.view.title.sqlError`).d('请先填写SQL表达式'),
    CHANGE_VIEW: intl
      .get(`${PREFIX}.view.title.changeView`)
      .d('切换表或视图将会删除属性列表和参数列表数据，是否切换'),

    BUTTON_ANALYSIS: intl.get(`${PREFIX}.view.button.analysis`).d('解析'),
    MODEL_FIELD_LIST: intl.get(`${PREFIX}.view.title.modelFieldList`).d('属性列表'),
    SQL_EXTRA: intl.get(`${PREFIX}.view.message.sql`).d('在sql最外层请不要写SELECT *'),
    ATTR_SAVE_ERROR: intl
      .get(`${PREFIX}.view.message.attrSaveError`)
      .d('新建的接口只能通过右下角保存按钮进行保存'),
    FIELD_DELETE_CONFIRM: intl
      .get(`${PREFIX}.view.message.fieldDeleteConfirm`)
      .d('删除该属性字段的同时，会删除被其绑定的参数，是否确认删除？'),
    FIELDS_SELECT_NOTIFICATION: intl
      .get(`${PREFIX}.view.message.fieldsSelectedAll`)
      .d('在线状态的模型不支持选择属性字段作为参数，请先下线模型后再进行选择！'),
    SQL_PARSE_WARNING: intl
      .get(`${PREFIX}.view.message.sqlParseWarning`)
      .d('未解析出属性字段或者请求参数，请确认sql是否正确！'),
    NOTIFICATION_ERROR_MODEL: intl
      .get(`${PREFIX}.notification.error.model`)
      .d('表单验证失败，请检查！'),
    BUTTON_CLEAN: intl.get(`${PREFIX}.view.button.clean`).d('清除'),
    BUTTON_DELETE: intl.get(`${PREFIX}.view.button.delete`).d('删除'),
    SOAP_VERSION_TOOLTIP: intl
      .get(`${SERVICE_PREFIX}.soapVersionTooltip`)
      .d(
        'SOAP版本可以通过配置值集和消息模板来扩展。对应模板值集值=对应模板消息模板的CODE即可。(注意 \u0024 {soapSecurity}、\u0024 {soapHeader}、\u0024 {soapBody}需要放到模板对应的位置)'
      ),

    ANALYSIS_SQL: intl.get(`${PREFIX}.view.button.analysisSQL`).d('解析SQL'),
    UNCHANGE: intl.get(`${PREFIX}.view.placeholder.unchange`).d('未更改'),
    CHANGE_TABLE: intl.get(`${PREFIX}.view.button.changeTable`).d('切换表或视图'),

    SUBJECT: intl.get(`${SERVICE_PREFIX}.subject`).d('对象'),
    CONDITION: intl.get(`${SERVICE_PREFIX}.condition`).d('条件'),
    FIELD: intl.get(`${SERVICE_PREFIX}.field`).d('字段'),
    KEY: intl.get(`${SERVICE_PREFIX}.key`).d('键'),
    VALUE: intl.get(`${SERVICE_PREFIX}.value`).d('值'),
    TYPE: intl.get(`${SERVICE_PREFIX}.type`).d('类型'),
    EXPECTATION: intl.get(`${SERVICE_PREFIX}.expectation`).d('期望值'),
    JSON_BODY: intl.get(`${PREFIX}.model.http.assertion.jsonBody`).d('JSON Path'),
    JSON_BODY_TIP: intl
      .get(`${PREFIX}.model.http.assertion.jsonBody.tip`)
      .d('JSON响应体需要通过JSON Path语法匹配对应值,可参考：'),
    XML_BODY: intl.get(`${PREFIX}.model.http.assertion.xmlBody`).d('XPath 1.0'),
    XML_BODY_TIP: intl
      .get(`${PREFIX}.model.http.assertion.xmlBody.tip`)
      .d('XML响应体需要通过XPath 1.0语法匹配对应值,可参考：'),

    TIMES: intl.get(`${SERVICE_PREFIX}.times`).d('次'),
    SECONDS: intl.get(`${SERVICE_PREFIX}.seconds`).d('秒'),
    RETRY_TIMES: intl.get(`${SERVICE_PREFIX}.retryTimes`).d('失败重试次数'),
    RETRY_INTERVAL: intl.get(`${SERVICE_PREFIX}.retryInterval`).d('失败重试间隔'),

    HTTP_CONN_UOM: intl.get(`${SERVICE_PREFIX}.ms`).d('毫秒'),
    HTTP_CONN_CONFIG: intl.get(`${PREFIX}.view.button.httpConnectConfig`).d('HTTP连接配置'),
    HTTP_CONN_CONFIG_TIP: intl
      .get(`${PREFIX}.view.button.httpConnectConfig.tip`)
      .d(
        'HTTP连接配置。单位ms(毫秒)。' +
          '此连接配置为服务级别配置，接口若未配置连接配置则使用此连接配置。接口配置的HTTP连接配置优先于服务的HTTP连接配置。' +
          '可配置参数有："ConnectionRequestTimeout"（从连接池获取连接超时时间，默认15s(15,000ms)）、' +
          '"ConnectTimeout"（客户端和服务器建立连接的超时时间，默认30s(30,000ms)）、"SocketTimeout"（客户端从服务器读取数据的超时时间，默认值-1，为系统底层默认，一般情况下将使用ConnectTimeout超时时间）。' +
          '详细JavaDoc文档请参考org.apache.http.client.config.RequestConfig的getConnectionRequestTimeout、getConnectTimeout、getSocketTimeout。' +
          'ConnectionRequestTimeout、ConnectTimeout参数的默认值（兜底值）可通过接口平台后端配置指定，配置分别为hzero.interface.http.connectionRequestTimeout、hzero.interface.http.connectTimeout，' +
          '配置类为InterfaceHttpConnectionProperties（请注意，此配置参数需要在使用到sdk(boot-interface)的服务中配置）。' +
          '请注意，这些参数值一般不宜超过接口平台调用方的Hystrix特别是Ribbon的超时时间（透传内部接口时为接口平台的Hystrix及Ribbon的配置）。' +
          '特殊值。0：不超时。-1：系统底层默认，由系统底层策略决定超时时间。2147483647：上限值'
      ),

    ASSERTION: intl.get(`${SERVICE_PREFIX}.assertion`).d('重试断言'),
    ASSERTION_TIP: intl
      .get(`${SERVICE_PREFIX}.assertion.tip`)
      .d(
        '断言，即通过下列选项设定判定条件，设定多个条件时为"与"的逻辑关系。满足条件则重试请求第三方接口。'
      ),

    BUSINESS_ASSERTION: intl.get(`${SERVICE_PREFIX}.business.assertion`).d('业务状态断言'),
    BUSINESS_ASSERTION_TIP: intl
      .get(`${SERVICE_PREFIX}.business.assertion.tip`)
      .d(
        '断言，即通过下列选项设定判定条件，设定多个条件时为"与"的逻辑关系。在日志监控页面业务状态列体现。'
      ),

    ADD_ASSERTION: intl.get(`${PREFIX}.view.button.addAssertion`).d('添加断言'),
    CLEAR_ASSERTION: intl.get(`${PREFIX}.view.button.clearAssertion`).d('清空断言'),

    MAINTAIN_REQUEST_MAPPING: intl.get(`${SERVICE_PREFIX}.clearAssertion`).d('请求映射维护'),
    MAINTAIN_RESPONSE_MAPPING: intl.get(`${SERVICE_PREFIX}.clearAssertion`).d('响应映射维护'),
    CURRENT_VERSION: intl.get(`${PREFIX}.view.message.current.version`).d('当前版本'),
    HISTORY_VERSION: intl.get(`${PREFIX}.view.message.history.version`).d('历史版本'),
    MAINTAIN_REQUEST_DATA_MAPPING: intl.get(`${SERVICE_PREFIX}.clearAssertion`).d('请求转化维护'),
    MAINTAIN_RESPONSE_DATA_MAPPING: intl.get(`${SERVICE_PREFIX}.clearAssertion`).d('响应转化维护'),

    MAPPING_MESSAGE_CONFIRM: intl
      .get(`${PREFIX}.view.message.mappingMessageConfirm`)
      .d('新增的映射/转化信息尚未保存，请先保存再退出当前滑窗。'),

    FILE_PROTOCOL: intl.get(`${SERVICE_PREFIX}.file.protocol`).d('文件协议'),
    FILE_PATH: intl.get(`${SERVICE_PREFIX}.file.path`).d('文件路径'),
    FILE_FILE_NAME: intl.get(`${SERVICE_PREFIX}.file.fileName`).d('文件名'),
    FILE_ENABLE_SUB_PATH: intl.get(`${SERVICE_PREFIX}.file.enableSubPath`).d('读取子路径'),
    FILE_ENABLE_ARCHIVE: intl.get(`${SERVICE_PREFIX}.file.enableArchive`).d('归档'),
    FILE_ENABLE_STORE_FILE: intl.get(`${SERVICE_PREFIX}.file.enableStoreFile`).d('持久化到文件'),
    FILE_PROTOCOL_CONFIG: intl.get(`${SERVICE_PREFIX}.file.protocol.config`).d('文件协议配置'),

    FILE_PROTOCOL_AUTH: intl.get(`${SERVICE_PREFIX}.file.protocol.auth.password`).d('认证模式'),
    FILE_PROTOCOL_AUTH_PASSWORD: intl
      .get(`${SERVICE_PREFIX}.file.protocol.auth.password`)
      .d('密码认证'),
    FILE_PROTOCOL_AUTH_PRIVATE_KEY: intl
      .get(`${SERVICE_PREFIX}.file.protocol.auth.privateKey`)
      .d('秘钥认证'),
    FILE_PROTOCOL_HOSTNAME: intl.get(`${SERVICE_PREFIX}.file.protocol.hostname`).d('主机'),
    FILE_PROTOCOL_PORT: intl.get(`${SERVICE_PREFIX}.file.protocol.port`).d('端口'),
    FILE_PROTOCOL_USERNAME: intl.get(`${SERVICE_PREFIX}.file.protocol.username`).d('用户名'),
    FILE_PROTOCOL_PRIVATE_KEY: intl.get(`${SERVICE_PREFIX}.file.protocol.privateKey`).d('秘钥'),

    MAINTAIN_FIELD_MAPPING: intl.get(`${PREFIX}.modal.fieldMapping`).d('字段映射'),
    MAINTAIN_DATA_MAPPING: intl.get(`${PREFIX}.modal.dataMapping`).d('数据映射'),

    IMPORT_SERVICE: intl.get(`${SERVICE_PREFIX}.importService`).d('根据WSDL注册SOAP'),
    TENANT_NAME: intl.get(`${SERVICE_PREFIX}.tenantName`).d('所属租户'),
    IMPORT_WARNING: intl
      .get(`${SERVICE_PREFIX}.importWarning`)
      .d('您未勾选任何待注册接口记录，将仅为您创建服务不生成接口，确认请继续'),
    CONTINUE: intl.get(`${SERVICE_PREFIX}.continue`).d('继续'),
    BACK: intl.get(`${SERVICE_PREFIX}.back`).d('返回'),
    BASIC_CONFIG: intl.get(`${SERVICE_PREFIX}.wsdl.basicConfig`).d('基础设置'),
    WSDL_PLACEHOLDER: intl
      .get(`${SERVICE_PREFIX}.wsdlPlaceholder`)
      .d('WSDL地址或本地WSDL内容的文本文件'),
    PIC_FILES: intl.get(`${SERVICE_PREFIX}.picFiles`).d('选择文件'),
    LOAD_WSDL: intl.get(`${SERVICE_PREFIX}.loadWsdl`).d('加载WSDL'),
    TEXT_PLACEHOLDER: intl
      .get(`${SERVICE_PREFIX}.textPlaceholder`)
      .d("此参数可由'加载WSDL'触发后自动填充，若填写则以此为准"),
    ADVANCE_CONFIG: intl.get(`${SERVICE_PREFIX}.wsdl.advanceConfig`).d('高级设置'),
    INTERFACE: intl.get(`${SERVICE_PREFIX}.interface`).d('待注册接口'),
    IMPORT_URL: intl.get(`${SERVICE_PREFIX}.importUrl`).d('WSDL地址'),
    SERVER_NAME: intl.get(`${SERVICE_PREFIX}.serverName`).d('服务名称'),
    SERVER_CODE: intl.get(`${SERVICE_PREFIX}.serverCode`).d('服务代码'),
    USERNAME: intl.get(`${SERVICE_PREFIX}.wsdl.username`).d('认证账号'),
    AuthPASSWORD: intl.get(`${SERVICE_PREFIX}.wsdl.authPassword`).d('认证密码'),
    SWAGGER: intl.get(`${SERVICE_PREFIX}.swagger`).d('RESTful地址'),
    SWAGGER_FLAG: intl.get(`${SERVICE_PREFIX}.swaggerFlag`).d('注册方式'),
    REGISTER: intl.get(`${SERVICE_PREFIX}.wsdl.register`).d('手动注册'),
    RESTFUL: intl.get(`${SERVICE_PREFIX}.wsdl.Restful`).d('一键注册RESTful'),
    SOAP: intl.get(`${SERVICE_PREFIX}.wsdl.Soap`).d('一键注册SOAP'),
    REQUEST: intl.get(`${SERVICE_PREFIX}.restful.Soap`).d('请求方法'),
    IMPORT_PLACEHOLDER: intl.get(`${SERVICE_PREFIX}.wsdl.importPlaceholder`).d('默认为否'),
    REQUEST_PLACEHOLDER: intl.get(`${SERVICE_PREFIX}.wsdl.requestPlaceholder`).d('默认为POST'),
  };
  return LANGS[key];
};

export default getLang;
