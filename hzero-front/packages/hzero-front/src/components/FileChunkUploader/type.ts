import { ModalProps as C7NModalProps } from 'choerodon-ui/pro/es/modal/Modal';
import { ModalProps as HzeroModalProps } from 'hzero-ui/es/modal/Modal';
import { ComponentType, CSSProperties, ReactComponentElement } from 'react';

export interface FileChunkUploaderBucketProps {
  type: 'bucket';
  bucketName?: string; // 默认是 BKT_PUBLIC
  directory?: string;
  storageCode?: string;
}

export interface FileChunkUploaderServerProps {
  type: 'server';
  configCode: string;
  path?: string;
}

export interface HzeroProps {
  componentType: 'hzero';
  modalProps: HzeroModalProps;
}
export interface C7NProps {
  componentType: 'c7n';
  modalProps: C7NModalProps;
}

export type FileChunkUploaderProps<T = any> = (
  | FileChunkUploaderServerProps
  | FileChunkUploaderBucketProps) &
  (HzeroProps & C7NProps) & {
    type: 'bucket' | 'server';
    componentType: 'c7n' | 'hzero'; // 模态框类型
    children: ReactComponentElement<ComponentType<T>>; // 如果有children 就直接显示 children, 但是会在外面包一个 div(以触发弹出文件上传)
    title: string;
    disabled?: boolean;
    style?: CSSProperties;
    organizationId?: number;
  };
