const braftEditorLoader = () => import('braft-editor');

export const braftEditorViewLoaders = [
  braftEditorLoader,
  () => import('braft-editor/dist/output.css'),
];

export const braftEditorEditLoaders = [
  braftEditorLoader,
  () => import('braft-editor/dist/index.css'),
];
