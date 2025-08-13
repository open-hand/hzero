import React from 'react';

export const TRACE_LOG = {
  CN: (
    <div>
      trace日志作用：在进行trace收集期间，记录用户在系统的操作的日志。收集完成后收集的日志信息会生成附件。
      <br />
      步骤：
      <br />
      1、点击【开始收集】按钮收集trace日志
      <br />
      2、在系统里操作需要反馈的步骤
      <br />
      3、步骤操作完成后，点击【结束收集】按钮，完成trace日志收集。
      <br />
      注：trace日志收集的前提是环境需要已安装并启用 hzero-starter-tracer 组件，具体可参考文档
      <a
        target="_blank"
        rel="noopener noreferrer"
        href="https://open.hand-china.com/document-center/doc/component/157/12609?doc_id=11077&_back=%2Fdocument-center%3Fs%3Dtrace"
      >
        请求链路追踪
      </a>
    </div>
  ),
  EN: (
    <div>
      {`The role of trace log：During the trace collection, the log of the user's operation in the system is recorded.`}
      <br />
      Step：
      <br />
      1、Click on [start collection] button to collect trace logs.
      <br />
      2、Run the steps that need feedback in the system.
      <br />
      3、After the steps are completed, click the [stop collection] button to complete the trace log
      collection.
      <br />
      Note: the premise of trace log collection is that the environment needs to have the hzero
      starter tracer component installed and enabled. Please refer to the document for&nbsp;
      <a
        target="_blank"
        rel="noopener noreferrer"
        href="https://open.hand-china.com/document-center/doc/component/157/12609?doc_id=11077&_back=%2Fdocument-center%3Fs%3Dtrace"
      >
        link tracking
      </a>
    </div>
  ),
};
