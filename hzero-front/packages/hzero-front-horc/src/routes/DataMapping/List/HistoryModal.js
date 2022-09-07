import React, { PureComponent } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { operatorRender } from 'utils/renderer';
import { historyTableDS } from '@/stores/DataMapping/DataMappingDS';
import DATA_MAPPING_LANG from '@/langs/dataMappingLang';

export default class HistoryModal extends PureComponent {
  constructor(props) {
    super(props);
    this.historyTableDS = new DataSet(historyTableDS());
  }

  async componentDidMount() {
    const { castHeaderId } = this.props;
    this.historyTableDS.setQueryParameter('castHeaderId', castHeaderId);
    await this.historyTableDS.query();
  }

  get historyColumns() {
    const { onGotoDetail } = this.props;
    return [
      {
        name: 'castCode',
      },
      {
        name: 'castName',
      },
      {
        name: 'dataType',
        width: 130,
      },
      {
        name: 'versionDesc',
        width: 80,
      },
      {
        header: DATA_MAPPING_LANG.OPERATOR,
        width: 80,
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <a onClick={() => onGotoDetail(record.get('castHeaderId'), record.get('version'))}>
                  {DATA_MAPPING_LANG.VIEW}
                </a>
              ),
              key: 'view',
              len: 2,
              title: DATA_MAPPING_LANG.VIEW,
            },
          ];
          return operatorRender(actions, record);
        },
      },
    ];
  }

  render() {
    return (
      <Table dataSet={this.historyTableDS} columns={this.historyColumns} style={{ width: 850 }} />
    );
  }
}
