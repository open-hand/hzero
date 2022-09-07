create table ACT_HI_PROCINST (
    id_ varchar(64) not null,
    proc_inst_id_ varchar(64) not null,
    business_key_ varchar(255),
    proc_def_id_ varchar(64) not null,
    start_time_ datetime(3) not null,
    end_time_ datetime(3),
    duration_ bigint,
    start_user_id_ varchar(255),
    start_act_id_ varchar(255),
    end_act_id_ varchar(255),
    super_process_instance_id_ varchar(64),
    delete_reason_ varchar(4000),
    tenant_id_ varchar(255) default '',
    name_ varchar(255),
    primary key (id_),
    unique (proc_inst_id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table ACT_HI_ACTINST (
    id_ varchar(64) not null,
    proc_def_id_ varchar(64) not null,
    proc_inst_id_ varchar(64) not null,
    execution_id_ varchar(64) not null,
    act_id_ varchar(255) not null,
    task_id_ varchar(64),
    call_proc_inst_id_ varchar(64),
    act_name_ varchar(255),
    act_type_ varchar(255) not null,
    assignee_ varchar(255),
    start_time_ datetime(3) not null,
    end_time_ datetime(3),
    duration_ bigint,
    delete_reason_ varchar(4000),
    tenant_id_ varchar(255) default '',
    primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table ACT_HI_TASKINST (
    id_ varchar(64) not null,
    proc_def_id_ varchar(64),
    task_def_key_ varchar(255),
    proc_inst_id_ varchar(64),
    execution_id_ varchar(64),
    name_ varchar(255),
    parent_task_id_ varchar(64),
    description_ varchar(4000),
    owner_ varchar(255),
    assignee_ varchar(255),
    start_time_ datetime(3) not null,
    claim_time_ datetime(3),
    end_time_ datetime(3),
    duration_ bigint,
    delete_reason_ varchar(4000),
    priority_ integer,
    due_date_ datetime(3),
    form_key_ varchar(255),
    category_ varchar(255),
    tenant_id_ varchar(255) default '',
    primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table ACT_HI_VARINST (
    id_ varchar(64) not null,
    proc_inst_id_ varchar(64),
    execution_id_ varchar(64),
    task_id_ varchar(64),
    name_ varchar(255) not null,
    var_type_ varchar(100),
    rev_ integer,
    bytearray_id_ varchar(64),
    double_ double,
    long_ bigint,
    text_ varchar(4000),
    text2_ varchar(4000),
    create_time_ datetime(3),
    last_updated_time_ datetime(3),
    primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table ACT_HI_DETAIL (
    id_ varchar(64) not null,
    type_ varchar(255) not null,
    proc_inst_id_ varchar(64),
    execution_id_ varchar(64),
    task_id_ varchar(64),
    act_inst_id_ varchar(64),
    name_ varchar(255) not null,
    var_type_ varchar(255),
    rev_ integer,
    time_ datetime(3) not null,
    bytearray_id_ varchar(64),
    double_ double,
    long_ bigint,
    text_ varchar(4000),
    text2_ varchar(4000),
    primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table ACT_HI_COMMENT (
    id_ varchar(64) not null,
    type_ varchar(255),
    time_ datetime(3) not null,
    user_id_ varchar(255),
    task_id_ varchar(64),
    proc_inst_id_ varchar(64),
    action_ varchar(255),
    message_ varchar(4000),
    full_msg_ longblob,
    primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table ACT_HI_ATTACHMENT (
    id_ varchar(64) not null,
    rev_ integer,
    user_id_ varchar(255),
    name_ varchar(255),
    description_ varchar(4000),
    type_ varchar(255),
    task_id_ varchar(64),
    proc_inst_id_ varchar(64),
    url_ varchar(4000),
    content_id_ varchar(64),
    time_ datetime(3),
    primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;

create table ACT_HI_IDENTITYLINK (
    id_ varchar(64),
    group_id_ varchar(255),
    type_ varchar(255),
    user_id_ varchar(255),
    task_id_ varchar(64),
    proc_inst_id_ varchar(64),
    primary key (id_)
) engine=innodb default charset=utf8 collate utf8_bin;


create index act_idx_hi_pro_inst_end on ACT_HI_PROCINST(end_time_);
create index act_idx_hi_pro_i_buskey on ACT_HI_PROCINST(business_key_);
create index act_idx_hi_act_inst_start on ACT_HI_ACTINST(start_time_);
create index act_idx_hi_act_inst_end on ACT_HI_ACTINST(end_time_);
create index act_idx_hi_detail_proc_inst on ACT_HI_DETAIL(proc_inst_id_);
create index act_idx_hi_detail_act_inst on ACT_HI_DETAIL(act_inst_id_);
create index act_idx_hi_detail_time on ACT_HI_DETAIL(time_);
create index act_idx_hi_detail_name on ACT_HI_DETAIL(name_);
create index act_idx_hi_detail_task_id on ACT_HI_DETAIL(task_id_);
create index act_idx_hi_procvar_proc_inst on ACT_HI_VARINST(proc_inst_id_);
create index act_idx_hi_procvar_name_type on ACT_HI_VARINST(name_, var_type_);
create index act_idx_hi_procvar_task_id on ACT_HI_VARINST(task_id_);
create index act_idx_hi_act_inst_procinst on ACT_HI_ACTINST(proc_inst_id_, act_id_);
create index act_idx_hi_act_inst_exec on ACT_HI_ACTINST(execution_id_, act_id_);
create index act_idx_hi_ident_lnk_user on ACT_HI_IDENTITYLINK(user_id_);
create index act_idx_hi_ident_lnk_task on ACT_HI_IDENTITYLINK(task_id_);
create index act_idx_hi_ident_lnk_procinst on ACT_HI_IDENTITYLINK(proc_inst_id_);
create index act_idx_hi_task_inst_procinst on ACT_HI_TASKINST(proc_inst_id_);
