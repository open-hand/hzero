/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/26
 * @copyright HAND ® 2019
 */

import React from 'react';

import { SharedComponent } from '../../customize/hfle';

import { FileChunkUploaderProps } from './type';

const FileChunkUploader: React.FC<FileChunkUploaderProps> = props => {
  return <SharedComponent componentProps={props} componentCode="FileChunkUploader" />;
};

export default FileChunkUploader;
