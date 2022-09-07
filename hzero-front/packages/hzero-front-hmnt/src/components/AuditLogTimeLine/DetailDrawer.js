import React from 'react';

const DetailDrawer = (props) => {
  const { record } = props;

  return (
    <>
      {record.map((item) => {
        return <p>{item}</p>;
      })}
    </>
  );
};

export default DetailDrawer;
