import intl from 'utils/intl';

// 额外的编码

// 公用但是没有使用的国际化
// intl.get('hzero.common.button.revoke.delete').d('撤销删除');
// intl.get('hzero.common.button.continue').d('继续');
// intl.get('hzero.common.button.sureAndRelease').d('确认并发布');
// intl.get('hzero.common.button.addCard').d('添加卡片');
// intl.get('hzero.common.button.send').d('发送');
// intl.get('hzero.common.status.startFlag').d('正常');
// intl.get('hzero.common.status.stopFlag').d('未在监控');
// intl.get('hzero.common.status.noFlag').d('故障');
// intl.get('hzero.common.status.sync').d('同步');
// intl.get('hzero.common.status.async').d('异步');
// intl.get('hzero.common.gender.male').d('男');
// intl.get('hzero.common.gender.female').d('女');
// intl.get('hzero.common.validation.requireInput').d('请输入{name}');
// intl.get('hzero.common.validation.requireSelect').d('请选择{name}');
// intl.get('hzero.common.table.column.remark').d('备注');
// intl.get('hzero.common.validation.trim').d('前后不能为空');
// intl.get('hzero.common.validation.atLeastOneRecord').d('请至少选择一条数据');
// intl.get('hzero.common.currency.none').d('无');
// intl.get('hzero.common.tagSelect.all').d('全部');
// intl.get('hzero.common.scope.global').d('全局');
// intl.get('hzero.common.scope.org').d('当前组织');
// intl.get('hzero.common.link.about').d('了解详情');
// intl.get('hzero.common.processStatus.finished').d('已结束');
// intl.get('hzero.common.processStatus.suspended').d('挂起中');
// intl.get('hzero.common.processStatus.running').d('运行中');
// intl.get('hzero.common.basicLayout.viewMore').d('查看更多');
// intl.get('hzero.common.components.operationAudit.history').d('操作历史');
// intl.get('hzero.common.components.operationAudit.viewHistory').d('查看操作历史');
// intl.get('hzero.common.components.operationAudit.operationCode').d('操作代码');
// intl.get('hzero.common.components.operationAudit.operationName').d('操作名称');
// intl.get('hzero.common.components.operationAudit.operationBy').d('操作人');
// intl.get('hzero.common.components.operationAudit.operatedTime').d('操作时间');
// intl.get('hzero.common.components.operationAudit.operatedRemark').d('操作内容');
// intl.get('hzero.common.components.operationAudit.operatedBy').d('操作人');
// intl.get('hzero.common.notification.typeError').d('网络请求异常');
// intl.get('hzero.common.message.confirm.pay').d('是否确定支付？');
// intl.get('hzero.common.globalExcetion.button.backWorkplace').d('返回首页');
// intl.get('hzero.common.status.enableFlag').d('启用');
// intl.get('hzero.common.status.ssoServerUrl').d('单点登录服务器地址');
// intl.get('hzero.common.status.ssoLoginUrl').d('单点登录地址');
// intl.get('hzero.common.status.clientHostUrl').d('客户端地址');
// intl.get('hzero.common.button.clear').d('清除');
// intl.get('hzero.common.title.individuation.formInputPropsAllowThousandth').d('是否启用千分位');
// intl.get('hzero.common.title.uploadImage').d('上传图片');
// intl.get('hzero.common.title.selectFile').d('选择文件');
// intl.get('hzero.common.startUpload').d('开始上传');
// intl.get('hzero.common.uploading').d('正在上传');
// intl.get('hzero.common.title.individuation.formInputDisabled').d('是否不可编辑');
// intl.get('hzero.common.title.individuation.formInputAllowThousandth').d('是否启用千分位');
// intl.get('hzero.common.title.individuation.formInputTrimAll').d('是否删除所有空格');
// intl.get('hzero.common.title.individuation.formInputTrim').d('是否删除前后空格');
// intl.get('hzero.common.title.individuation.formInputDbc2sbc').d('是否转换全角到半角');
// intl.get('hzero.common.title.individuation.formInputTypeCase').d('组件的大小写输入限制');
// intl.get('hzero.common.title.individuation.formInputSize').d('控件大小');
// intl.get('hzero.common.button.closeOther').d('关闭其他');
// intl.get('hzero.common.button.closeAll').d('关闭全部');
// intl.get('hzero.common.title.exception501').d('服务不可用');
// intl.get('hzero.common.button.result').d('结果');
// intl.get('hzero.common.components.excelExport.export').d('导出');
// intl.get('hzero.common.components.excelExport.setCondition').d('设置导出条件');
// intl.get('hzero.common.components.excelExport.selectColumns').d('选择要导出的列');
// intl.get('hzero.common.message.agree').d('同意');
// intl.get('hzero.common.message.reject').d('拒绝');
// intl.get('hzero.common.message.addSign').d('加签');
// intl.get('hzero.common.message.delegate').d('转交');
// intl.get('hzero.common.message.jump').d('跳转');
// intl.get('hzero.common.message.recall').d('撤回');
// intl.get('hzero.common.message.autoDelegate').d('自动转交');
// intl.get('hzero.common.message.carbonCopy').d('抄送');
// intl.get('hzero.common.priority.height').d('高');
// intl.get('hzero.common.message.priority.day').d('天');
// intl.get('hzero.common.message.priority.hours').d('小时');
// intl.get('hzero.common.message.priority.minutes').d('分钟');
// intl.get('hzero.common.basicLayout.menuSelect').d('菜单搜索');
// intl.get('hzero.common.validation.date.before').d('{startDate}不能早于{endDate}');
// intl.get('hzero.common.validation.date.after').d('{startDate}不能晚于{endDate}');
// intl.get('hzero.common.date.active').d('有效日期');
// intl.get('hzero.common.date.release').d('发布日期');

// 在代码中不需要默认值的国际化
intl.get('hzero.common.serverCode200').d('服务器成功返回请求的数据。');
intl.get('hzero.common.serverCode201').d('新建或修改数据成功。');
intl.get('hzero.common.serverCode202').d('一个请求已经进入后台排队（异步任务）。');
intl.get('hzero.common.serverCode204').d('删除数据成功。');
intl.get('hzero.common.serverCode400').d('发出的请求有错误，服务器没有进行新建或修改数据的操作。');
intl.get('hzero.common.serverCode401').d('用户没有权限（令牌、用户名、密码错误）。');
intl.get('hzero.common.serverCode403').d('用户得到授权，但是访问是被禁止的。');
intl.get('hzero.common.serverCode404').d('发出的请求针对的是不存在的记录，服务器没有进行操作。');
intl.get('hzero.common.serverCode406').d('请求的格式不可得。');
intl.get('hzero.common.serverCode410').d('请求的资源被永久删除，且不会再得到的。');
intl.get('hzero.common.serverCode422').d('当创建一个对象时，发生一个验证错误。');
intl.get('hzero.common.serverCode500').d('服务器发生错误，请检查服务器。');
intl.get('hzero.common.serverCode502').d('网关错误。');
intl.get('hzero.common.serverCode503').d('服务不可用，服务器暂时过载或维护。');
intl.get('hzero.common.serverCode504').d('网关超时。');

// authorized 路由的标题
intl.get('hzero.common.title.wordEdit').d('文档编辑');
intl.get('hzero.common.title.globalException').d('全局错误历史记录');
intl.get('hzero.common.title.uiPagePreview').d('页面预览');
intl.get('hzero.common.title.roleImport').d('角色导入');
intl.get('hzero.common.title.permissionImport').d('权限导入');
intl.get('hzero.common.title.batchImport').d('批量导入');
intl.get('hzero.common.title.disposeInvite').d('合作邀约处理');
intl.get('hzero.common.title.workspace').d('工作台');

// 通用导入页面 标题
intl.get('hzero.common.title.roleImport').d('角色导入');
intl.get('hzero.common.title.permissionImport').d('权限导入');
intl.get('hzero.common.title.authorityCodeImport').d('授权码导入');
intl.get('hzero.common.title.importErp').d('导入ERP');
intl.get('hzero.common.title.historyDetail').d('历史详情');
intl.get('hzero.common.title.modelTypeImport').d('模型类型导入');

// 已经确认移除的国际化
// intl.get('hzero.common.btn.action').d('操作');
// intl.get('hzero.common.btn.delete').d('删除');
// intl.get('hzero.common.btn.release').d('发布');
// intl.get('hzero.common.btn.add').d('新增');
// intl.get('hzero.common.btn.save').d('保存');
// intl.get('hzero.common.requestError').d('请求错误');
