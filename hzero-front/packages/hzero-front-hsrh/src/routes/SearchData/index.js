/**
 * @since 2020-1-5
 * @author MLF <linfeng.miao@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */
import React from 'react';
import { Form, TextField, DataSet, Select, Button, Lov, CodeArea } from 'choerodon-ui/pro';
// import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import JSONFormatter from 'choerodon-ui/pro/lib/code-area/formatters/JSONFormatter';
import { Header, Content } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { fetchSearchDataSend } from '@/services/searchDataService';

import { detailFormDS } from '@/stores/searchDataDS';

@formatterCollections({
  code: ['hsrh.searchData', 'hsrh.inquiryConfig'],
})
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    this.detailFormDS = new DataSet(detailFormDS());
  }

  // componentDidMount() {
  //   // this.refresh();
  // }

  // 发送请求
  @Bind()
  async handleSend() {
    const param = this.detailFormDS.toData();
    const httpRequestDTO = {
      indexCode: param[0].indexCode,
      methodType: param[0].methodType,
      requestBody: param[0].requestBody,
      requestUrl: param[0].requestUrl,
    };
    const respondParam = await fetchSearchDataSend(httpRequestDTO);
    if (respondParam.error) {
      this.detailFormDS.get(0).set('respondParam', JSON.stringify(respondParam.error));
    } else {
      this.detailFormDS.get(0).set('respondParam', JSON.stringify(respondParam));
    }
  }

  render() {
    return (
      <React.Fragment>
        <Header title={intl.get('hsrh.searchData.view.title.searchData').d('搜索数据')}>
          <Button color="primary" icon="save" onClick={this.handleSend}>
            {intl.get(`hsrh.searchData.model.searchData.send`).d('发送')}
          </Button>
        </Header>
        <Content>
          <Form dataSet={this.detailFormDS} columns={10}>
            <Select name="methodType" colSpan={2} />
            <Lov name="indexCodeSet" colSpan={3} />
            <CodeArea
              formatter={JSONFormatter}
              name="respondParam"
              rowSpan={11}
              colSpan={6}
              style={{ height: 355 }}
            />
            <TextField colSpan={5} name="requestUrl" />
            <CodeArea
              formatter={JSONFormatter}
              name="requestBody"
              rowSpan={5}
              colSpan={5}
              style={{ height: 256 }}
            />
          </Form>
        </Content>
      </React.Fragment>
    );
  }
}
