import React, { PureComponent } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Button as ButtonPermission } from 'components/Permission';
import { operatorRender } from 'utils/renderer';
import { historyTableDS } from '@/stores/FieldMapping/FieldMappingDS';
import FIELD_MAPPING_LANG from '@/langs/fieldMappingLang';

export default class HistoryModal extends PureComponent {
  constructor(props) {
    super(props);
    this.historyTableDS = new DataSet(historyTableDS());
  }

  async componentDidMount() {
    const { transformId } = this.props;
    this.historyTableDS.setQueryParameter('transformId', transformId);
    await this.historyTableDS.query();
  }

  get historyColumns() {
    const { path, onGotoDetail } = this.props;
    return [
      {
        name: 'transformCode',
      },
      {
        name: 'transformName',
      },
      {
        name: 'transformType',
        width: 130,
      },
      {
        name: 'versionDesc',
        width: 80,
      },
      {
        header: FIELD_MAPPING_LANG.OPERATOR,
        width: 80,
        align: 'center',
        renderer: ({ record }) => {
          const actions = [
            {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.history.view`,
                      type: 'button',
                      meaning: '历史版本-查看',
                    },
                  ]}
                  onClick={() => onGotoDetail(record.get('transformId'), record.get('version'))}
                >
                  {FIELD_MAPPING_LANG.VIEW}
                </ButtonPermission>
              ),
              key: 'view',
              len: 2,
              title: FIELD_MAPPING_LANG.VIEW,
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
