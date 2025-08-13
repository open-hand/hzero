/**
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/5
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { Modal } from 'choerodon-ui/pro';

import intl from 'utils/intl';

import { labelTemplateLabelInsertConfig } from '@/stores/labelTemplateDS';

import LabelInsertForm from './LabelInsertForm';

import { transformDataToElement, transformElementToData } from '../../utils';

const modalKey = Modal.key();

/**
 * LabelTemplate 编辑器入口
 * @param CKEDITOR - 编辑器
 */
export default function ckEditorPluginEntry(CKEDITOR) {
  const prevPlugin = CKEDITOR.plugins.get('label-template');
  // 不存在/占位初始化
  if (!prevPlugin || !prevPlugin.labelTemplateIsInit) {
    // eslint-disable-next-line no-param-reassign
    delete CKEDITOR.plugins.registered['label-template'];
    CKEDITOR.plugins.add('label-template', {
      init: editor => {
        if (editor.ui.addButton) {
          editor.ui.addButton('label-template', {
            // cke -> ckeditor, p -> plugin
            icon: 'Link',
            label: intl.get('hrpt.labelTemplate.view.title.cke.p.label').d('标签'),
            command: 'hzeroLabelTemplateInsertLabel',
            toolbar: 'label-template, 20',
          });
          editor.addCommand('hzeroLabelTemplateInsertLabel', {
            exec: () => {
              const ref = React.createRef();
              Modal.open({
                key: modalKey,
                children: <LabelInsertForm interactedRef={ref} />,
                title: intl.get('hrpt.labelTemplate.view.title.cke.p.labelInsert').d('标签插入'),
                onOk: () => {
                  // only data ok, to close Modal
                  if (ref.current) {
                    return ref.current.getValidateData().then(
                      data => {
                        if (data) {
                          const { constants } = labelTemplateLabelInsertConfig();
                          const element = editor.document.createElement(
                            [constants.type.img, constants.type.bar, constants.type.qr].includes(
                              data.type
                            )
                              ? 'span'
                              : 'span'
                          );
                          transformDataToElement(data, element);
                          editor.insertElement(element);
                          return true;
                        }
                        return false;
                      },
                      () => false
                    );
                  }
                  return Promise.resolve(false);

                  // do something with editor
                },
              });
            },
          });
        }

        editor.addCommand('hzeroLabelTemplateEditLabel', {
          exec: editor1 => {
            const selection = editor1.getSelection();
            const selectedElement = selection.getSelectedElement();
            const initialData = {};
            if (selectedElement && selectedElement.data('shape')) {
              Object.assign(initialData, transformElementToData(selectedElement) || {});
            } else {
              // 选择编辑的标签
              return;
            }
            const ref = React.createRef();
            Modal.open({
              key: modalKey,
              children: (
                <LabelInsertForm interactedRef={ref} initialData={initialData} isCreate={false} />
              ),
              title: intl.get('hrpt.labelTemplate.view.title.cke.p.labelEdit').d('标签编辑'),
              onOk: () => {
                // only data ok, to close Modal
                if (ref.current) {
                  return ref.current.getValidateData().then(
                    data => {
                      if (data) {
                        transformDataToElement(data, selectedElement);
                        editor1.fire('change');
                        return true;
                      }
                      return false;
                    },
                    () => false
                  );
                }
                return Promise.resolve(false);

                // do something with editor
              },
            });
          },
        });

        editor.addCommand('hzeroLabelTemplateDeleteLabel', {
          exec: editor1 => {
            const path = editor1.elementPath();

            let node = path.contains(['span', 'img'], 1);

            // 不是 span/img 和 label 就什么都不做
            if (!node || !node.data('shape')) return;

            Modal.confirm({
              children: intl.get('hrpt.labelTemplate.view.confirm.delete').d('是否确认删除'),
              onOk() {
                // copy from ckeditor4
                const parent = node.getParent();

                const editable = editor1.editable();

                if (
                  parent.getChildCount() === 1 &&
                  !parent.is('td', 'th') &&
                  !parent.equals(editable)
                ) {
                  node = parent;
                }

                const range = editor1.createRange();
                // eslint-disable-next-line no-undef
                range.moveToPosition(node, CKEDITOR.POSITION_BEFORE_START);
                node.remove();
                range.select();
              },
            });
          },
        });

        // If the "menu" plugin is loaded, register the menu items.
        // If the "contextmenu" plugin is loaded, register the listeners.
        if (editor.contextMenu) {
          editor.addMenuGroup('hzeroLabelTemplate');
          editor.addMenuItems({
            hzeroLabelTemplateEditLabel: {
              label: intl.get('hzero.common.button.edit').d('编辑'),
              command: 'hzeroLabelTemplateEditLabel',
              group: 'hzeroLabelTemplate',
              order: 1,
            },
            hzeroLabelTemplateDeleteLabel: {
              label: intl.get('hzero.common.button.delete').d('删除'),
              command: 'hzeroLabelTemplateDeleteLabel',
              group: 'hzeroLabelTemplate',
              order: 2,
            },
          });
          editor.contextMenu.addListener(element => {
            const node = element.getAscendant({ span: 1, img: 1 }, true);
            if (node && node.data('shape')) {
              // menu item state is resolved on commands.
              return {
                // eslint-disable-next-line no-undef
                hzeroLabelTemplateDeleteLabel: CKEDITOR.TRISTATE_OFF,
                // eslint-disable-next-line no-undef
                hzeroLabelTemplateEditLabel: CKEDITOR.TRISTATE_OFF,
              };
            }
          });
        }
      },
      labelTemplateIsInit: true,
    });
  }
}
