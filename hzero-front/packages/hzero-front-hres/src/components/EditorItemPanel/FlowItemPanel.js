import React from 'react';
import intl from 'utils/intl';
import { Card } from 'choerodon-ui';
import { ItemPanel, Item } from 'gg-editor';
import styles from './index.less';
import sql from '../NodeSvg/sql.svg';
import group from '../NodeSvg/group.svg';
import mapping from '../NodeSvg/mapping.svg';
import endCircle from '../NodeSvg/endCircle.svg';
import startCircle from '../NodeSvg/startCircle.svg';
import rule from '../NodeSvg/rule.svg';
import formula from '../NodeSvg/formula.svg';

const FlowItemPanel = () => {
  return (
    <ItemPanel className={styles.itemPanel}>
      <Card bordered={false} style={{ height: '100%' }}>
        <Item
          type="node"
          size="72*72"
          shape="custom-start"
          model={{
            color: '#FA8C16',
            label: 'Start',
            labelOffsetY: 0,
            componentType: 'START',
            componentTypeDesc: intl.get('hres.flow.model.flow.start').d('开始'),
          }}
          src={startCircle}
        />
        <Item
          type="node"
          size="80*48"
          shape="custom-node"
          model={{
            color: '#1890FF',
            labelOffsetY: 40,
            label: intl.get('hres.flow.model.flow.formulaCmp').d('公式组件'),
            componentType: 'FORMULA_EXECUTOR',
            componentTypeDesc: intl.get('hres.flow.model.flow.formulaCmp').d('公式组件'),
            icon:
              'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB2aWV3Qm94PScwIDAgMTAyNCAxMDI0JyB2ZXJzaW9uPScxLjEnIHhtbG5zPSdodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2Zycgd2lkdGg9JzIwMCcgaGVpZ2h0PScyMDAnPjxwYXRoIGQ9J001MTIgOTI4SDEyOGEzMiAzMiAwIDAgMS0yNi44OC00OS45MkwzNDUuNiA1MTIgMTAxLjEyIDE0NS45MkEzMiAzMiAwIDAgMSAxMjggOTZoMzg0YTMyIDMyIDAgMCAxIDAgNjRIMTg3LjUybDIyMy4zNiAzMzQuMDhhMzMuMjggMzMuMjggMCAwIDEgMCAzNS44NEwxODcuNTIgODY0SDUxMmEzMiAzMiAwIDAgMSAwIDY0ek02NDAgOTI4YTM2LjQ4IDM2LjQ4IDAgMCAxLTE3LjkyLTUuMTIgMzIuNjQgMzIuNjQgMCAwIDEtOC45Ni00NC44bDI1Ni0zODRhMzIgMzIgMCAwIDEgNTMuNzYgMzUuODRsLTI1NiAzODRhMzMuMjggMzMuMjggMCAwIDEtMjYuODggMTQuMDh6JyBmaWxsPScjNEQ0RDREJz48L3BhdGg+PHBhdGggZD0nTTg5NiA5MjhhMzMuMjggMzMuMjggMCAwIDEtMjYuODgtMTQuMDhsLTI1Ni0zODRhMzIgMzIgMCAxIDEgNTMuNzYtMzUuODRsMjU2IDM4NGEzMi42NCAzMi42NCAwIDAgMS04Ljk2IDQ0LjggMzYuNDggMzYuNDggMCAwIDEtMTcuOTIgNS4xMnpNOTkyIDIyNGgtMzIwYTMyIDMyIDAgMCAxIDAtNjRoMzIwYTMyIDMyIDAgMCAxIDAgNjR6JyBmaWxsPScjNEQ0RDREJz48L3BhdGg+PHBhdGggZD0nTTgzMiAzODRhMzIgMzIgMCAwIDEtMzItMzJ2LTMyMGEzMiAzMiAwIDAgMSA2NCAwdjMyMGEzMiAzMiAwIDAgMS0zMiAzMnonIGZpbGw9JyM0RDRENEQnPjwvcGF0aD48L3N2Zz4=',
          }}
          src={formula}
        />
        <Item
          type="node"
          size="80*48"
          shape="custom-node"
          model={{
            color: '#1890FF',
            labelOffsetY: 40,
            label: intl.get('hres.flow.model.flow.sqlCmp').d('Sql组件'),
            componentType: 'SQL_EXECUTOR',
            componentTypeDesc: intl.get('hres.flow.model.flow.sqlCmp').d('Sql组件'),
            icon:
              'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB2aWV3Qm94PScwIDAgMTAyNCAxMDI0JyB2ZXJzaW9uPScxLjEnIHhtbG5zPSdodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2Zycgd2lkdGg9JzMyJyBoZWlnaHQ9JzMyJz48cGF0aCBkPSdNOTMwLjgxNiAxMDEyLjczNkgyNDguMzJjLTQ2LjA4IDAtODIuOTQ0LTM3LjM3Ni04Mi45NDQtODIuOTQ0VjI0Ny4yOTZjMC00Ni4wOCAzNy4zNzYtODIuOTQ0IDgyLjk0NC04Mi45NDRoNjgxLjk4NGM0Ni4wOCAwIDgyLjk0NCAzNy4zNzYgODIuOTQ0IDgyLjk0NHY2ODEuOTg0YzAuNTEyIDQ2LjA4LTM2Ljg2NCA4My40NTYtODIuNDMyIDgzLjQ1NnpNMjQ4LjMyIDIyNy44NGMtMTAuNzUyIDAtMTkuNDU2IDguNzA0LTE5LjQ1NiAxOS40NTZ2NjgxLjk4NGMwIDEwLjc1MiA4LjcwNCAxOS40NTYgMTkuNDU2IDE5LjQ1Nmg2ODEuOTg0YzEwLjc1MiAwIDE5LjQ1Ni04LjcwNCAxOS40NTYtMTkuNDU2VjI0Ny4yOTZjMC0xMC43NTItOC43MDQtMTkuNDU2LTE5LjQ1Ni0xOS40NTZIMjQ4LjMyeicgZmlsbD0nIzUxNTE1MSc+PC9wYXRoPjxwYXRoIGQ9J00xNjguOTYgODU2LjU3NmMtODcuNTUyIDAtMTU4LjcyLTcxLjE2OC0xNTguNzItMTU4LjcyVjkxLjY0OGMwLTQ2LjA4IDM3LjM3Ni04Mi45NDQgODIuOTQ0LTgyLjk0NGg2MDYuNzJjODcuNTUyIDAgMTU4LjcyIDcxLjE2OCAxNTguNzIgMTU4LjcyaC02NGMwLTUyLjIyNC00Mi40OTYtOTQuNzItOTQuNzItOTQuNzJIOTMuMTg0Yy0xMC43NTIgMC0xOS40NTYgOC43MDQtMTkuNDU2IDE5LjQ1NnY2MDYuNzJjMCA1Mi4yMjQgNDIuNDk2IDk0LjcyIDk0LjcyIDk0LjcydjYyLjk3NnonIGZpbGw9JyM1MTUxNTEnPjwvcGF0aD48cGF0aCBkPSdNNDE1Ljc0NCA1NzAuODhjLTM3LjM3Ni0yNC41NzYtNTQuNzg0LTM2LjM1Mi01NC43ODQtNTEuMiAwLTE1LjM2IDE0LjMzNi0xNS44NzIgMjIuNTI4LTE1LjM2IDguNzA0IDAuNTEyIDcxLjE2OCAwLjUxMiA3MS4xNjggMC41MTJWNDU1LjY4SDM1OS40MjRjLTE1Ljg3MiAwLTQ3LjEwNCAxOC40MzItNTUuODA4IDQ4LjEyOC01LjYzMiAyMC40OCAyLjU2IDQwLjQ0OCA0LjA5NiA0NC41NDQgMi41NiA2LjE0NCA3LjE2OCAxNS4zNiAxOS45NjggMjguMTYgMzQuODE2IDM1Ljg0IDcyLjcwNCA0MS40NzIgNzcuMzEyIDY0IDAuNTEyIDIuMDQ4IDEuNTM2IDcuNjgtMS41MzYgMTIuOC00LjA5NiA2LjY1Ni0xMy4zMTIgOS4yMTYtMTkuNDU2IDkuMjE2SDMwMi41OTJ2NTEuMnM1OS4zOTIgMC41MTIgOTQuNzIgMCA2NC41MTItMjkuNjk2IDY0LjUxMi02NC41MTItOS4yMTYtNTMuNzYtNDYuMDgtNzguMzM2ek02MDYuNzIgNDUxLjA3MmMtNzEuNjggMC0xMzAuMDQ4IDU5LjkwNC0xMzAuMDQ4IDEzMy42MzIgMCA1NC43ODQgMzEuNzQ0IDEwMy40MjQgODEuNDA4IDEyMy45MDRsMTcuOTItNDMuNTJjLTMxLjc0NC0xMy4zMTItNTIuNzM2LTQ1LjA1Ni01Mi43MzYtODAuODk2IDAtNDcuNjE2IDM3LjM3Ni04Ni41MjggODMuNDU2LTg2LjUyOHM4My40NTYgMzguOTEyIDgzLjQ1NiA4Ni41MjhjMCAzNS44NC0yMC45OTIgNjcuMDcyLTUxLjcxMiA4MC4zODR2LTQ2LjU5MmgtNDYuNTkydjE1Mi4wNjRoNDYuNTkydi01Ni4zMmM1OC4zNjgtMTQuODQ4IDk4LjgxNi02Ny41ODQgOTguODE2LTEyOS41MzYtMC41MTItNzMuMjE2LTU4Ljg4LTEzMy4xMi0xMzAuNTYtMTMzLjEyek04MjkuNDQgNjY1LjA4OGMtMTQuMzM2IDAtMjYuMTEyLTExLjc3Ni0yNi4xMTItMjYuMTEyVjQ1NS42OGgtNDYuNTkydjE4My4yOTZjMCAzOS45MzYgMzIuNzY4IDcyLjcwNCA3Mi43MDQgNzIuNzA0aDQ3LjEwNHYtNDYuNTkySDgyOS40NHonIGZpbGw9JyM1MTUxNTEnPjwvcGF0aD48L3N2Zz4=',
          }}
          src={sql}
        />
        <Item
          type="node"
          size="80*48"
          shape="custom-node"
          model={{
            color: '#1890FF',
            labelOffsetY: 40,
            label: intl.get('hres.flow.model.flow.groupComponent').d('分组组件'),
            componentType: 'GROUP_EXECUTOR',
            componentTypeDesc: intl.get('hres.flow.model.flow.groupComponent').d('分组组件'),
            icon:
              'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB2aWV3Qm94PScwIDAgMTAyNCAxMDI0JyB2ZXJzaW9uPScxLjEnIHhtbG5zPSdodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2Zycgd2lkdGg9JzMyJyBoZWlnaHQ9JzMyJz48cGF0aCBkPSdNMjU2IDUxMiAyNTYgNDQ4bDM4NCAwIDAgNjRMMzMyLjggNTEyIDI1NiA1MTJ6TTMyMCA4MzJsNjQgMCAwIDY0TDMyMCA4OTYgMjgxLjYgODk2IDI1NiA4OTYgMzkuNDg4IDg5NkMxNC43MiA4OTYgMCA4OTkuMDA4IDAgODc0LjI0TDAgNDIzLjY4IDAgMzg0IDAgMzIwIDAgMjU5Ljg0IDAgMTM0LjRDMCAxMDguMjg4IDE1Ljg3MiA2NCA0Mi4wNDggNjRsMzUzLjI4IDBDNDIxLjQ0IDY0IDQ0OCAxMDguMjg4IDQ0OCAxMzQuNEw0NDggMTkybDQ2MS44ODggMEM5MzQuNTkyIDE5MiA5NjAgMjM1LjEzNiA5NjAgMjU5Ljg0TDk2MCAzODRsLTY0IDAtMTI4IDBMNjQgMzg0bDAgMzkuNjhMNjQgODMybDE5MiAwTDMyMCA4MzJ6TTY0IDMyMGw4MzIgMEw4OTYgMjU2IDQ0Mi42ODggMjU2IDM4NCAyNTYgMzg0IDIxNS4wNCAzODQgMTI4IDY0IDEyOGwwIDEzMS44NEw2NCAzMjB6TTgzMiA2NDAgODMyIDU3NmwtNjQgMCAwIDMyMCA2NCAwIDAtNjQgMTkyIDAgMCAxOTItMTkyIDAgMC02NC0xMjggMCAwLTY0IDAtMTI4LTY0IDAgMCAxMjhMNDQ4IDg5NiA0NDggNTc2bDE5MiAwIDAgMTI4IDY0IDBMNzA0IDU3NiA3MDQgNTEybDY0IDAgNjQgMEw4MzIgNDQ4bDE5MiAwIDAgMTkyTDgzMiA2NDB6TTg5NiA1NzZsNjQgMEw5NjAgNTEybC02NCAwTDg5NiA1NzZ6TTU3NiA2NDAgNTEyIDY0MGwwIDE5MiA2NCAwTDU3NiA2NDB6TTg5NiA5NjBsNjQgMCAwLTY0LTY0IDBMODk2IDk2MHonIGZpbGw9JyM1MTUxNTEnPjwvcGF0aD48L3N2Zz4=',
          }}
          src={group}
        />
        <Item
          type="node"
          size="80*48"
          shape="custom-node"
          model={{
            color: '#1890FF',
            labelOffsetY: 40,
            label: intl.get('hres.mapping.model.mapping.mappingCmp').d('映射组件'),
            componentType: 'MAPPING_EXECUTOR',
            componentTypeDesc: intl.get('hres.flow.model.flow.mappingCmp').d('映射组件'),
            icon:
              'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB2aWV3Qm94PScwIDAgMTAyNCAxMDI0JyB2ZXJzaW9uPScxLjEnIHhtbG5zPSdodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2Zycgd2lkdGg9JzMyJyBoZWlnaHQ9JzMyJz48cGF0aCBkPSdNMzAyLjY0NTY3NSA0NjcuMzU2MzIzYzE2LjUwOTg3IDAgMzAuMDIxMTUzLTEzLjUxMTI4MyAzMC4wMjExNTItMjkuMjQ1MDQ3IDAtMTYuNTA5ODctMTMuNTExMjgzLTI5LjI4MDMyNS0zMC4wMjExNTItMjkuMjgwMzI2cy0zMC4wMjExNTMgMTMuNTExMjgzLTMwLjAyMTE1MyAyOS4yODAzMjZjMCAxNS43MzM3NjUgMTMuNTExMjgzIDI5LjI0NTA0OCAzMC4wMjExNTMgMjkuMjQ1MDQ3ek0yNTUuNDA5MTAyIDc2OC45MDgzOTZoNDgyLjM0OTI2MWM3OS44NjgyNjEgMCAxNDUuMTY2OTE0LTY1LjI5ODY1MyAxNDUuMTY2OTEzLTE0NS4xNjY5MTRWMTcxLjIwMTcwOWMwLTU5LjAxOTI1OC00OC4yOTQ4OTgtMTA3LjE3MzA0Ni0xMDcuMTczMDQ1LTEwNy4xNzMwNDZIMjQ4LjI0Nzc2OWMtNTguOTgzOTggMC0xMDcuMTczMDQ2IDQ4LjE4OTA2NS0xMDcuMTczMDQ1IDEwNy4xNzMwNDZ2NjgxLjYzMTg2YzAgNTkuMDE5MjU4IDQ4LjE4OTA2NSAxMDcuMTczMDQ2IDEwNy4xNzMwNDUgMTA3LjE3MzA0Nkg3NzUuNzUyMjMxYzQyLjE1NjYxMyAwIDc4LjczOTM4MS0yNC42NTg5NzMgOTYuMjM3MDItNjAuMTQ4MTM4SDI1NS40MDkxMDJjLTIyLjAxMzE2IDAtNDAuMTQ1Nzk1LTE3Ljk5MTUyNS00MC4xNDU3OTUtNDAuMTQ1Nzk2di01MC42OTM3NjhjMC4wMzUyNzgtMjIuMDEzMTYgMTcuOTkxNTI1LTQwLjExMDUxOCA0MC4xNDU3OTUtNDAuMTEwNTE3eiBtNDk4Ljk2NDk2NC0zODcuMTAwMDExYzMwLjc2MTk4IDAgNTYuMjY3NjEzIDI0Ljc2NDgwNSA1Ni4yNjc2MTIgNTUuNTI2Nzg2cy0yNS41MDU2MzMgNTUuNTI2Nzg1LTU2LjI2NzYxMiA1NS41MjY3ODVjLTMxLjUwMjgwOCAwLTU2LjI2NzYxMy0yNC43NjQ4MDUtNTYuMjY3NjEzLTU1LjUyNjc4NXMyNS41MDU2MzMtNTUuNTI2Nzg1IDU2LjI2NzYxMy01NS41MjY3ODZ6TTIxMy4zMjMwNDQgNDM3LjMzNTE3MWMwLTQ4Ljc4ODc4MyAzOS43NTc3NDMtODcuODA1Njk4IDg5LjI4NzM1My04Ny44MDU2OTggNDAuODg2NjIzIDAgNzQuMjU5MTM4IDI3LjI2OTUwOCA4NC43NzE4MzMgNjMuNzgxNzJoMTg1LjM4MzI2NGwtNTYuMjY3NjEzLTU0Ljc4NTk1OGMtOC45OTU3NjMtOC4yNTQ5MzUtOS43NzE4NjgtMjMuMjQ3ODczLTEuNTE2OTMyLTMyLjI3ODkxM2wxLjUxNjkzMi0xLjUxNjkzMmM4Ljk5NTc2My05Ljc3MTg2OCAyNC4wMjM5NzgtOS43NzE4NjggMzMuNzYwNTY4LTAuNzQwODI4bDAuNzQwODI4IDAuNzQwODI4IDExNC44MjgyNjMgMTEyLjU3MDUwMy0xMTQuODI4MjYzIDExMi41NzA1MDRjLTguOTk1NzYzIDkuNzcxODY4LTI0LjAyMzk3OCA5Ljc3MTg2OC0zMy43NjA1NjggMC43NDA4MjdsLTAuNzQwODI4LTAuNzQwODI3Yy04Ljk5NTc2My04LjI1NDkzNS05Ljc3MTg2OC0yMy4yNDc4NzMtMS41MTY5MzItMzIuMjc4OTEzbDEuNTE2OTMyLTEuNTE2OTMzIDU2LjI2NzYxMy01NC43ODU5NThoLTE4NS4zNDc5ODZjLTEwLjU4MzI1IDM2Ljg2NDk4OC00NC40MTQzNzMgNjMuNzgxNzItODQuNzcxODMzIDYzLjc4MTcyMS00OC43ODg3ODMgMC4wNzA1NTUtODkuMzIyNjMxLTM4Ljk4MTYzOC04OS4zMjI2MzEtODcuNzM1MTQzeicgZmlsbD0nIzUxNTE1MSc+PC9wYXRoPjwvc3ZnPg==',
          }}
          src={mapping}
        />
        <Item
          type="node"
          size="80*48"
          shape="custom-node"
          model={{
            color: '#1890FF',
            labelOffsetY: 40,
            label: intl.get('hres.flow.model.flow.ruleCmp').d('规则组件'),
            componentType: 'RULE_EXECUTOR',
            componentTypeDesc: intl.get('hres.flow.model.flow.ruleCmp').d('规则组件'),
            icon:
              'data:image/svg+xml;charset=utf-8;base64,PHN2ZyB2aWV3Qm94PScwIDAgMTAyNCAxMDI0JyB2ZXJzaW9uPScxLjEnIHhtbG5zPSdodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2Zycgd2lkdGg9JzMyJyBoZWlnaHQ9JzMyJz48cGF0aCBkPSdNMTQ3IDExNGg2ODB2MzE1LjZoNTBWNjRIOTd2ODk2aDMwNy42di01MEgxNDd6JyBmaWxsPScjNTE1MTUxJz48L3BhdGg+PHBhdGggZD0nTTIzNyA2MDEuMmgxMDcuNnY1MEgyMzd6TTIzNyAyODFoNTAwdjUwSDIzN3pNOTI3IDcyNS45di01MGgtODEuOGMtNC4yLTI5LjYtMTUuOS01Ni45LTMzLjEtNzkuOGw1Ny44LTU3LjgtMzUuNC0zNS40LTU3LjggNTcuOGMtMjIuOS0xNy4yLTUwLjItMjguOC03OS44LTMzLjF2LTgxLjhoLTUwdjgxLjhjLTI5LjYgNC4yLTU2LjkgMTUuOS03OS44IDMzLjFsLTU3LjgtNTcuOC0zNS4zIDM1LjMgNTcuOCA1Ny44Yy0xNy4yIDIyLjktMjguOCA1MC4yLTMzLjEgNzkuOEg0MTd2NTBoODEuOGM0LjIgMjkuNiAxNS45IDU2LjkgMzMuMSA3OS44TDQ3NCA4NjMuNWwzNS40IDM1LjQgNTcuOC01Ny44YzIyLjkgMTcuMiA1MC4yIDI4LjggNzkuOCAzMy4xVjk1Nmg1MHYtODEuOGMyOS42LTQuMiA1Ni45LTE1LjkgNzkuOC0zMy4xbDU3LjggNTcuOCAzNS40LTM1LjQtNTcuOC01Ny44YzE3LjItMjIuOSAyOC44LTUwLjIgMzMuMS03OS44SDkyN3ogbS0xMzEuMy03LjFjLTIuOSAyMC42LTExLjEgNDAuMi0yMy42IDU2LjlsLTEwLjggMTQuNS0xNC41IDEwLjhjLTE2LjcgMTIuNS0zNi4zIDIwLjYtNTYuOSAyMy42bC0xNy45IDIuNi0xNy45LTIuNmMtMjAuNi0yLjktNDAuMi0xMS4xLTU2LjktMjMuNmwtMTQuNS0xMC44LTEwLjgtMTQuNWMtMTIuNS0xNi43LTIwLjYtMzYuMy0yMy42LTU2LjlsLTIuNi0xNy45IDIuNi0xNy45YzIuOS0yMC42IDExLjEtNDAuMiAyMy42LTU2LjlsMTAuOC0xNC41IDE0LjUtMTAuOGMxNi43LTEyLjUgMzYuMy0yMC42IDU2LjktMjMuNmwxNy45LTIuNiAxNy45IDIuNmMyMC42IDIuOSA0MC4yIDExLjEgNTYuOSAyMy42bDE0LjUgMTAuOCAxMC44IDE0LjVjMTIuNSAxNi43IDIwLjYgMzYuMyAyMy42IDU2LjlsMi42IDE3LjktMi42IDE3Ljl6TTIzNyA0NDEuMWgxNjcuNnY1MEgyMzd6JyBmaWxsPScjNTE1MTUxJz48L3BhdGg+PC9zdmc+',
          }}
          src={rule}
        />
        <Item
          type="node"
          size="72*72"
          shape="custom-end"
          model={{
            color: '#722ED1',
            label: 'End',
            labelOffsetY: 0,
            componentType: 'END',
            componentTypeDesc: intl.get('hres.flow.model.flow.end').d('结束'),
          }}
          src={endCircle}
        />
      </Card>
    </ItemPanel>
  );
};

export default FlowItemPanel;
