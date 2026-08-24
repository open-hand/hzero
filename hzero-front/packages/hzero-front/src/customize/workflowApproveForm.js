import { isFunction } from 'lodash';

import { mapCustomize } from 'utils/customize';

/**
 * 如果 审批表单存在 则加载表单
 * 否则 返回 null
 * @param formCode
 * @return {Promise<null|*>}
 */
export async function loadWorkflowApproveFormAsync(formCode) {
  if (
    mapCustomize.has({ module: 'hzero-front-hwfp', feature: 'workflowApproveForm', key: formCode })
  ) {
    const layout = mapCustomize.get({
      module: 'hzero-front-hwfp',
      feature: 'workflowApproveForm',
      key: formCode,
    });
    if (isFunction(layout && layout.component)) {
      const approveForm = await layout.component();
      return approveForm;
    }
  }
  return null;
}

export function setWorkflowApproveForm(formConfig) {
  // TODO: 判断是否需要检查 重复设置的问题
  mapCustomize.set({
    module: 'hzero-front-hwfp',
    feature: 'workflowApproveForm',
    key: formConfig.code,
    data: { component: formConfig.component },
  });
}
